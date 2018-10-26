package nato.ivct.gui.server.sut;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.eclipse.scout.rt.platform.BEANS;
import org.eclipse.scout.rt.platform.exception.VetoException;
import org.eclipse.scout.rt.platform.text.TEXTS;
import org.eclipse.scout.rt.shared.services.common.security.ACCESS;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.api.client.util.Objects;

import nato.ivct.commander.BadgeDescription;
import nato.ivct.commander.BadgeDescription.InteroperabilityRequirement;
import nato.ivct.commander.Factory;
import nato.ivct.gui.server.ServerSession;
import nato.ivct.gui.server.cb.CbService;
import nato.ivct.gui.shared.cb.ReadCbPermission;
import nato.ivct.gui.shared.sut.ISuTTcService;
import nato.ivct.gui.shared.sut.SuTTcExecutionFormData;
import nato.ivct.gui.shared.sut.SuTTcRequirementFormData;
import nato.ivct.gui.shared.sut.SuTTcRequirementFormData.TcExecutionHistoryTable;

public class SuTTcService implements ISuTTcService {
	private static final Logger LOG = LoggerFactory.getLogger(ServerSession.class);

	@Override
	public SuTTcRequirementFormData prepareCreate(SuTTcRequirementFormData formData) {
		// TODO Auto-generated method stub
		return formData;
	}

	@Override
	public SuTTcRequirementFormData create(SuTTcRequirementFormData formData) {
		// TODO Auto-generated method stub
		return formData;
	}

	@Override
	public SuTTcRequirementFormData load(SuTTcRequirementFormData formData) {
		LOG.info("load requirement form");
		if (!ACCESS.check(new ReadCbPermission())) {
			throw new VetoException(TEXTS.get("AuthorizationFailed"));
		}

		CbService cbService = (CbService) BEANS.get(CbService.class);
		
		// get requirement description and test case
		BadgeDescription bd = cbService.getBadgeDescription(formData.getBadgeId());
		if (bd != null) {
			Optional<InteroperabilityRequirement> first = Arrays.stream(bd.requirements).filter(requirement -> formData.getRequirementId().equals(requirement.ID)).findFirst();
			first.ifPresent(requirement -> {
				formData.getReqDescr().setValue(requirement.description);
				formData.getTestCaseName().setValue(requirement.TC);
				// get log files for this test case
				loadLogFiles(formData, bd.ID, requirement.TC);
			});
		}

		return formData;
	}

	@Override
	public SuTTcRequirementFormData store(SuTTcRequirementFormData formData) {
		// TODO Auto-generated method stub
		return formData;
	}

	public SuTTcRequirementFormData loadLogFileContent(SuTTcRequirementFormData formData, String fileName) {
		// get requirement description and test case
		CbService cbService = (CbService) BEANS.get(CbService.class);
		BadgeDescription bd = cbService.getBadgeDescription(formData.getBadgeId());
		if (bd != null) {
			// get content of the requested log file
			Path tcLogFile = Paths.get(Paths.get(Factory.props.getProperty(Factory.IVCT_SUT_HOME_ID), formData.getSutId(), bd.ID).toString(), fileName);
			try {
				formData.getTcExecutionLog().setValue(java.nio.file.Files.lines(tcLogFile).collect(Collectors.joining("\n")));
			} catch (NoSuchFileException e) {
				LOG.info("log files not found: " + tcLogFile);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		return formData;
	}

	@Override
	public SuTTcRequirementFormData updateLogFileTable(SuTTcRequirementFormData formData) {
		TcExecutionHistoryTable tbl = formData.getTcExecutionHistoryTable();
		tbl.clearRows();
		loadLogFiles(formData, formData.getBadgeId(), formData.getTestCaseId());
		return formData;
	}

	@Override
	public SuTTcExecutionFormData loadLogFileContent(SuTTcExecutionFormData formData, String fileName) {
		// get requirement description and test case
		CbService cbService = (CbService) BEANS.get(CbService.class);
		BadgeDescription bd = cbService.getBadgeDescription(formData.getBadgeId());
		if (bd != null) {
			// load content of the newest log file for this test case
			final String fileNamePattern = fileName.substring(fileName.lastIndexOf('.') + 1);
			
			try {
				Optional<Path> optionalTcLogFile = Files.find(Paths.get(Factory.props.getProperty(Factory.IVCT_SUT_HOME_ID), formData.getSutId(), bd.ID), 1, (path, fileAttributes) -> {
					String filenameToCheck = path.getFileName().toString();
					return fileAttributes.isRegularFile() && filenameToCheck.startsWith(fileNamePattern) && filenameToCheck.endsWith(".log");
				}).sorted(new FileCreationTimeComparator()
					.reversed())
					.findFirst();
				
				if (optionalTcLogFile.isPresent())
					formData.getTcExecutionLog().setValue(Files.lines(optionalTcLogFile.get()).collect(Collectors.joining("\n")));
				else
					LOG.info("log files not found: " + fileNamePattern + "*.log");
			} catch (IOException | IllegalStateException exc) {
				LOG.error("", exc);
			}
		}
		
		return formData;
	}

	@Override
	public SuTTcExecutionFormData load(SuTTcExecutionFormData formData) {
		LOG.info("load tc execution form");
		if (!ACCESS.check(new ReadCbPermission())) {
			throw new VetoException(TEXTS.get("AuthorizationFailed"));
		}

		CbService cbService = (CbService) BEANS.get(CbService.class);
		
		// get requirement description and test case
		BadgeDescription bd = cbService.getBadgeDescription(formData.getBadgeId());
		if (bd != null) {
			Optional<InteroperabilityRequirement> first = Arrays.stream(bd.requirements).filter(requirement -> formData.getRequirementId().equals(requirement.ID)).findFirst();
			first.ifPresent(requirement -> {
				formData.getReqDescr().setValue(requirement.description);
				formData.getTestCaseName().setValue(requirement.TC);
			});
		}
		
		return formData;
	}
	
	private SuTTcRequirementFormData loadLogFiles(SuTTcRequirementFormData fd, String bdId, String tcFullName) {
		Path tcLogFolder = Paths.get(Factory.props.getProperty(Factory.IVCT_SUT_HOME_ID), fd.getSutId(), bdId);
		String tcName = Stream.of(tcFullName.split(Pattern.quote("."))).reduce((a,b) -> b).get();
		try (DirectoryStream<Path> files =
				Files.newDirectoryStream(tcLogFolder, tcName+"*.log"))
		{
		  for (Path file : files) {
			  String logFileName = file.getFileName().toString();
			  LOG.info("Log file found: " +logFileName);
			  fd.getTcExecutionHistoryTable().addRow().setFileName(logFileName);
		  }
		} catch (NoSuchFileException e) {
			LOG.info("No log files found for test case: " + tcFullName);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return fd;
	}
	
	private static final class FileCreationTimeComparator implements Comparator<Path> {

		@Override
		public int compare(Path path1, Path path2) {
			try {
				return Files.readAttributes(path1, BasicFileAttributes.class).creationTime().compareTo(Files.readAttributes(path2, BasicFileAttributes.class).creationTime());
			} catch (IOException exc) {
				throw new IllegalStateException(exc);
			}
		}
		
	}
}
