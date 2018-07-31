package nato.ivct.gui.server.sut;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
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

import nato.ivct.commander.BadgeDescription;
import nato.ivct.commander.BadgeDescription.InteroperabilityRequirement;
import nato.ivct.commander.Factory;
import nato.ivct.gui.server.ServerSession;
import nato.ivct.gui.server.cb.CbService;
import nato.ivct.gui.shared.cb.ReadCbPermission;
import nato.ivct.gui.shared.sut.ISuTTcService;
import nato.ivct.gui.shared.sut.SuTTcExecutionFormData;

public class SuTTcService implements ISuTTcService {
	private static final Logger LOG = LoggerFactory.getLogger(ServerSession.class);

	@Override
	public SuTTcExecutionFormData prepareCreate(SuTTcExecutionFormData formData) {
		// TODO Auto-generated method stub
		return formData;
	}

	@Override
	public SuTTcExecutionFormData create(SuTTcExecutionFormData formData) {
		// TODO Auto-generated method stub
		return formData;
	}

	@Override
	public SuTTcExecutionFormData load(SuTTcExecutionFormData formData) {
		LOG.info("load");
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
				Path tcLogFolder = Paths.get(Factory.props.getProperty(Factory.IVCT_SUT_HOME_ID), formData.getSutId(), bd.ID);
				// get the log files from this folder
				String tcFullName = requirement.TC;
				String tcName = Stream.of(tcFullName.split(Pattern.quote("."))).reduce((a,b) -> b).get();
				try (DirectoryStream<Path> files =
						Files.newDirectoryStream(tcLogFolder, tcName+"*.log"))
				{
				  for (Path file : files) {
					  String logFileName = file.getFileName().toString();
					  LOG.info("Log file found: " +logFileName);
					  formData.getTcExecutionHistoryTable().addRow().setFileName(logFileName);
				  }
				} catch (NoSuchFileException e) {
					LOG.info("No log files found for test case: " + requirement.TC);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			});
		}
		
		
		return formData;
	}

	@Override
	public SuTTcExecutionFormData store(SuTTcExecutionFormData formData) {
		// TODO Auto-generated method stub
		return formData;
	}

	public SuTTcExecutionFormData loadLogFile(SuTTcExecutionFormData formData, String fileName) {
		// get requirement description and test case
		CbService cbService = (CbService) BEANS.get(CbService.class);
		BadgeDescription bd = cbService.getBadgeDescription(formData.getBadgeId());
		if (bd != null) {
			// get log files for this test case
			Path tcLogFile = Paths.get(Paths.get(Factory.props.getProperty(Factory.IVCT_SUT_HOME_ID), formData.getSutId(), bd.ID).toString(), fileName);
			try {
				formData.getTcExecutionLog().setValue(java.nio.file.Files.lines(tcLogFile).collect(Collectors.joining("\n")));
//				formData.getTcExecutionLog().setValue(java.nio.file.Files.lines(tcLogFile).reduce((a,b) -> a+"\n"+b).get());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		return formData;
	}

}
