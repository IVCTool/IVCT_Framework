package nato.ivct.gui.server.sut;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.eclipse.scout.rt.platform.BEANS;
import org.eclipse.scout.rt.platform.exception.VetoException;
import org.eclipse.scout.rt.platform.job.IFuture;
import org.eclipse.scout.rt.platform.text.TEXTS;
import org.eclipse.scout.rt.shared.services.common.security.ACCESS;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nato.ivct.commander.BadgeDescription;
import nato.ivct.commander.Factory;
import nato.ivct.commander.InteroperabilityRequirement;
import nato.ivct.gui.server.ServerSession;
import nato.ivct.gui.server.ServerSession.SutTcResultDescription;
import nato.ivct.gui.server.cb.CbService;
import nato.ivct.gui.shared.cb.ReadCbPermission;
import nato.ivct.gui.shared.sut.ISuTTcService;
import nato.ivct.gui.shared.sut.SuTTcExecutionFormData;
import nato.ivct.gui.shared.sut.SuTTcRequirementFormData;
import nato.ivct.gui.shared.sut.SuTTcRequirementFormData.TcExecutionHistoryTable;
import nato.ivct.gui.shared.sut.SuTTcRequirementFormData.TcExecutionHistoryTable.TcExecutionHistoryTableRowData;

public class SuTTcService implements ISuTTcService {
	private static final Logger LOG = LoggerFactory.getLogger(ServerSession.class);
	private SutTcResultDescription sutTcResults = null;

	@Override
	public SuTTcRequirementFormData prepareCreate(SuTTcRequirementFormData formData) {
		return formData;
	}

	@Override
	public SuTTcRequirementFormData create(SuTTcRequirementFormData formData) {
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
			// get the requirements for this badge
			Optional<Entry<String, InteroperabilityRequirement>> first =bd.requirements.entrySet().stream()
					.filter(requirement -> formData.getRequirementId().equals(requirement.getValue().ID)).findFirst();
			first.ifPresent(requirement -> {
				formData.getReqDescr().setValue(requirement.getValue().description);
//				formData.getTestCaseName().setValue(requirement.TC);
				
				// get log files for this test case
//				if (bd.ID != null && requirement.TC != null)
//					// work-around if the requirement has no test case associated
//					loadLogFiles(formData, bd.ID, requirement.TC);
			});
		}

		return formData;
	}

	@Override
	public SuTTcRequirementFormData store(SuTTcRequirementFormData formData) {
		return formData;
	}

	public SuTTcRequirementFormData loadLogFileContent(SuTTcRequirementFormData formData, String fileName) {
		// get requirement description and test case
		CbService cbService = (CbService) BEANS.get(CbService.class);
		BadgeDescription bd = cbService.getBadgeDescription(formData.getBadgeId());
		if (bd != null) {
			// get content of the requested log file
			Path tcLogFile = Paths.get(Factory.getSutPathsFiles().getSutLogPathName(formData.getSutId(), bd.ID),
					                   fileName);
			try {
				formData.getTcExecutionLog()
						.setValue(java.nio.file.Files.lines(tcLogFile).collect(Collectors.joining("\n")));
			} catch (NoSuchFileException e) {
				LOG.info("log files not found: {}", tcLogFile.toString());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return formData;
	}

	@Override
	public SuTTcRequirementFormData updateLogFileTable(SuTTcRequirementFormData formData) {
		TcExecutionHistoryTable tbl = formData.getTcExecutionHistoryTable();
		// TODO make this smarter instead brute force
		ServerSession.get().updateSutResultMap(formData.getSutIdProperty().getValue(), formData.getBadgeIdProperty().getValue(), formData.getTestCaseIdProperty().getValue());

		tbl.clearRows();
		loadLogFiles(formData, formData.getBadgeId(), formData.getTestCaseId());
		return formData;
	}

	@Override
	public SuTTcExecutionFormData loadLogFileContent(SuTTcExecutionFormData formData, String tcFullName) {
		// get requirement description and test case
		CbService cbService = (CbService) BEANS.get(CbService.class);
		BadgeDescription bd = cbService.getBadgeDescription(formData.getBadgeId());

		if (bd != null) {
			// load content of the newest log file for this test case
			try {
				final Path folder = Paths.get(Factory.getSutPathsFiles().getSutLogPathName(formData.getSutId(), bd.ID));
				final String tcName = tcFullName.substring(tcFullName.lastIndexOf('.') + 1);

				Optional<Path> optionalTcLogFile = getLogFilesOrderedByCreationDate(tcName, folder).findFirst();

				if (optionalTcLogFile.isPresent())
					formData.getTcExecutionLog()
							.setValue(Files.lines(optionalTcLogFile.get()).collect(Collectors.joining("\n")));
				else
					LOG.info("log files not found: {}*.log", folder + "\\" + tcName);
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
			Optional<Entry<String, InteroperabilityRequirement>> first =bd.requirements.entrySet().stream()
					.filter(requirement -> formData.getRequirementId().equals(requirement.getValue().ID)).findFirst();
			first.ifPresent(requirement -> {
				formData.getReqDescr().setValue(requirement.getValue().description);
//				formData.getTestCaseName().setValue(requirement.TC);
			});
		}

		return formData;
	}

	private SuTTcRequirementFormData loadLogFiles(SuTTcRequirementFormData fd, String bdId, String tcFullName) {
		final Path folder = Paths.get(Factory.getSutPathsFiles().getSutLogPathName(fd.getSutId(), bdId));
		final String tcName = tcFullName.substring(tcFullName.lastIndexOf('.') + 1);
		
		// load the (logfile,verdict) pairs
//		if (sutTcResults == null) {
			final IFuture<SutTcResultDescription> future1 = ServerSession.get().getLoadTcResultsJob();
			sutTcResults = future1.awaitDoneAndGet();
//		}

		try {
			getLogFilesOrderedByCreationDate(tcName, folder).forEach(path -> {
				final String logFileName = path.getFileName().toString();
				LOG.info("Log file found: {}" ,logFileName);
				final TcExecutionHistoryTableRowData row = fd.getTcExecutionHistoryTable().addRow();
				row.setFileName(logFileName);
				final String verdict = sutTcResults.sutResultMap.get(fd.getSutId()).getOrDefault(fd.getBadgeId(), new HashMap<>()).getOrDefault(logFileName, "");
				row.setTcVerdict(verdict);
			});
		} catch (NoSuchFileException exc) {
            LOG.info("log files not found: {}", folder+"\\"+tcName);
		} catch (IOException exc) {
			exc.printStackTrace();
		}

		return fd;
	}

	private Stream<Path> getLogFilesOrderedByCreationDate(final String fileName, final Path folder) throws IOException {
		final String fileNamePattern = fileName.substring(fileName.lastIndexOf('.') + 1);

		try {
			return Files.find(folder, 1, (path, fileAttributes) -> {
				String filenameToCheck = path.getFileName().toString();
				return fileAttributes.isRegularFile() && filenameToCheck.contains(fileNamePattern)
						&& filenameToCheck.endsWith(".log");
			}).sorted(new FileCreationTimeComparator().thenComparing(new FileModificationTimeComparator()).reversed());
		} catch (IllegalStateException exc) {
			throw new IOException(exc);
		}
	}

	private static final class FileCreationTimeComparator implements Comparator<Path> {

		@Override
		public int compare(Path path1, Path path2) {
			try {
//				System.out.println(path1.toString() + " " + Files.readAttributes(path1, BasicFileAttributes.class).creationTime().toMillis());
//				System.out.println(path2.toString() + " " + Files.readAttributes(path2, BasicFileAttributes.class).creationTime().toMillis());
//				System.out.println("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX");
				return Long.compare(Files.readAttributes(path1, BasicFileAttributes.class).creationTime().to(TimeUnit.SECONDS),
						Files.readAttributes(path2, BasicFileAttributes.class).creationTime().to(TimeUnit.SECONDS));
			} catch (IOException exc) {
				throw new IllegalStateException(exc);
			}
		}
	}
	
	private static final class FileModificationTimeComparator implements Comparator<Path> {

		@Override
		public int compare(Path path1, Path path2) {
			try {
//				System.out.println(path1.toString() + " " + Files.readAttributes(path1, BasicFileAttributes.class).lastModifiedTime().toMillis());
//				System.out.println(path2.toString() + " " + Files.readAttributes(path2, BasicFileAttributes.class).lastModifiedTime().toMillis());
//				System.out.println("YYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYY");
				return Files.readAttributes(path1, BasicFileAttributes.class).lastModifiedTime()
						.compareTo(Files.readAttributes(path2, BasicFileAttributes.class).lastModifiedTime());
			} catch (IOException exc) {
				throw new IllegalStateException(exc);
			}
		}
		
	}
}
