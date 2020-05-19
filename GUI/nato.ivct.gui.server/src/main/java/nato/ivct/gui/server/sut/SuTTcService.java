package nato.ivct.gui.server.sut;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

import org.eclipse.scout.rt.platform.BEANS;
import org.eclipse.scout.rt.platform.exception.VetoException;
import org.eclipse.scout.rt.platform.job.IFuture;
import org.eclipse.scout.rt.platform.text.TEXTS;
import org.eclipse.scout.rt.shared.services.common.security.ACCESS;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;

import nato.ivct.commander.CmdSendLogMsg;
import nato.ivct.commander.Factory;
import nato.ivct.commander.SutDescription;
import nato.ivct.gui.server.ServerSession;
import nato.ivct.gui.server.ServerSession.SutTcResultDescription;
import nato.ivct.gui.server.ts.TsService;
import nato.ivct.gui.shared.cb.ReadCbPermission;
import nato.ivct.gui.shared.sut.ISuTTcService;
import nato.ivct.gui.shared.sut.SuTTcExecutionFormData;
import nato.ivct.gui.shared.sut.SuTTcExecutionFormData.TcExecutionHistoryTable;
import nato.ivct.gui.shared.sut.SuTTcExecutionFormData.TcExecutionHistoryTable.TcExecutionHistoryTableRowData;
import nato.ivct.gui.shared.sut.SuTTcExecutionFormData.TcLog.TcLogRowData;
import nato.ivct.gui.shared.sut.SuTTcRequirementFormData;
import nato.ivct.gui.shared.sut.TcLogMsgNotification;


public class SuTTcService implements ISuTTcService {
    private static final Logger    LOG          = LoggerFactory.getLogger(ServerSession.class);
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
    public SuTTcExecutionFormData loadJSONLogFileContent(String sutId, String testsuiteId, String fileName, SuTTcExecutionFormData formData) {
        final Path logFilePath = Paths.get(Factory.getSutPathsFiles().getSutLogPathName(sutId, testsuiteId), fileName);
        try {
            java.nio.file.Files.lines(logFilePath).map(line -> {
                TcLogMsgNotification logMsgNotification = null;
                try {
                    final JsonObject jObj = new Gson().fromJson(line, JsonObject.class);
                    logMsgNotification = new TcLogMsgNotification();
                    logMsgNotification.setLogLevel(jObj.get(CmdSendLogMsg.LOG_MSG_LEVEL).getAsString());
                    logMsgNotification.setTimeStamp(jObj.get(CmdSendLogMsg.LOG_MSG_TIME).getAsLong());
                    logMsgNotification.setLogMsg(jObj.get(CmdSendLogMsg.LOG_MSG_EVENT).getAsString());
                }
                catch (JsonSyntaxException exc) {
                    LOG.info("incorrect log format " + logFilePath, exc);
                }
                return Optional.ofNullable(logMsgNotification);
            }).filter(Optional::isPresent).forEach(optionalLogMsgNotification -> {
                final TcLogRowData row = formData.getTcLog().addRow();
                final TcLogMsgNotification logMsgNotification = optionalLogMsgNotification.get();
                row.setLogLevel(logMsgNotification.getLogLevel());
                row.setTimeStamp(logMsgNotification.getTimeStamp());
                row.setLogMsg(logMsgNotification.getLogMsg());
            });
        }
        catch (final NoSuchFileException exc) {
            LOG.info("log files not found: " + logFilePath, exc);
        }
        catch (final IOException exc) {
            LOG.error("", exc);
        }
        return formData;
    }


    @Override
    public SuTTcExecutionFormData updateLogFileTable(SuTTcExecutionFormData formData) {
        final TcExecutionHistoryTable tbl = formData.getTcExecutionHistoryTable();

        ServerSession.get().updateSutResultMap(formData.getSutIdProperty().getValue(), formData.getTestsuiteIdProperty().getValue(), formData.getTestCaseIdProperty().getValue());

        tbl.clearRows();
        loadLogFiles(formData);
        return formData;
    }


    @Override
    public SuTTcExecutionFormData load(SuTTcExecutionFormData formData) {
        LOG.info("load tc execution form");
        if (!ACCESS.check(new ReadCbPermission())) {
            throw new VetoException(TEXTS.get("AuthorizationFailed"));
        }

        formData.getTcDescr().setValue(BEANS.get(TsService.class).getTsDescription(formData.getTestsuiteId()).testcases.get(formData.getTestCaseId()).description);

        // get log files for this test case
        loadLogFiles(formData);

        return formData;
    }


    private SuTTcExecutionFormData loadLogFiles(SuTTcExecutionFormData fd) {
        final Path folder = Paths.get(Factory.getSutPathsFiles().getSutLogPathName(fd.getSutId(), fd.getTestsuiteId()));
        final String tcName = fd.getTestCaseId().substring(fd.getTestCaseId().lastIndexOf('.') + 1);

        // load the (logfile,verdict) pairs
        final IFuture<SutTcResultDescription> future1 = ServerSession.get().getLoadTcResultsJob();
        sutTcResults = future1.awaitDoneAndGet();

        try {
            getLogFilesOrderedByCreationDate(tcName, folder).forEach(path -> {
                final String logFileName = path.getFileName().toString();
                LOG.info("Log file found: {}", logFileName);
                final TcExecutionHistoryTableRowData row = fd.getTcExecutionHistoryTable().addRow();
                row.setFileName(logFileName);
                final String verdict = sutTcResults.sutResultMap.getOrDefault(fd.getSutId(), new HashMap<>()).getOrDefault(fd.getTestsuiteId(), new HashMap<>()).getOrDefault(logFileName, "UNKNOWN");
                row.setTcVerdict(verdict);
            });
        }
        catch (final NoSuchFileException exc) {
            LOG.info("log files not found: {}", folder + "\\" + tcName);
        }
        catch (final IOException exc) {
            LOG.error(" ", exc);
        }

        return fd;
    }


    private Stream<Path> getLogFilesOrderedByCreationDate(final String fileName, final Path folder) throws IOException {
        final String fileNamePattern = fileName.substring(fileName.lastIndexOf('.') + 1);

        try {
            return Files.find(folder, 1, (path, fileAttributes) -> {
                final String filenameToCheck = path.getFileName().toString();
                return fileAttributes.isRegularFile() && filenameToCheck.contains(fileNamePattern) && filenameToCheck.endsWith(".log");
            }).sorted(new FileCreationTimeComparator().thenComparing(new FileModificationTimeComparator()).reversed());
        }
        catch (final IllegalStateException exc) {
            throw new IOException(exc);
        }
    }

    private static final class FileCreationTimeComparator implements Comparator<Path> {

        @Override
        public int compare(Path path1, Path path2) {
            try {
                return Long.compare(Files.readAttributes(path1, BasicFileAttributes.class).creationTime().to(TimeUnit.SECONDS), Files.readAttributes(path2, BasicFileAttributes.class).creationTime().to(TimeUnit.SECONDS));
            }
            catch (final IOException exc) {
                throw new IllegalStateException(exc);
            }
        }
    }

    private static final class FileModificationTimeComparator implements Comparator<Path> {

        @Override
        public int compare(Path path1, Path path2) {
            try {
                return Files.readAttributes(path1, BasicFileAttributes.class).lastModifiedTime().compareTo(Files.readAttributes(path2, BasicFileAttributes.class).lastModifiedTime());
            }
            catch (final IOException exc) {
                throw new IllegalStateException(exc);
            }
        }
    }

    @Override
    public void executeTestCase(String sutId, String tc, String tsId) {
        // execute the CmdStartTc commands
        final SutDescription sut = BEANS.get(SuTService.class).getSutDescription(sutId);
        ServerSession.get().execStartTc(sutId, tc, tsId, sut.settingsDesignator, sut.federation, sut.sutFederateName);
    }


    @Override
    public String getTcLastVerdict(String sutId, String testsuiteId, String tcId) {

        final Path folder = Paths.get(Factory.getSutPathsFiles().getSutLogPathName(sutId, testsuiteId));
        final String tcName = tcId.substring(tcId.lastIndexOf('.') + 1);

        // load the (logfile,verdict) pairs
        final IFuture<SutTcResultDescription> future1 = ServerSession.get().getLoadTcResultsJob();
        sutTcResults = future1.awaitDoneAndGet();

        String verdict = "";
        try {
            final Optional<Path> optionalLogFile = getLogFilesOrderedByCreationDate(tcName, folder).findFirst();
            final String logFileName = optionalLogFile.isPresent() ? optionalLogFile.get().getFileName().toString() : "";
            LOG.info("Log file found: {}", logFileName);
            verdict = sutTcResults.sutResultMap.getOrDefault(sutId, new HashMap<>()).getOrDefault(testsuiteId, new HashMap<>()).getOrDefault(logFileName, "");
        }
        catch (final NoSuchFileException exc) {
            LOG.info("log files not found: {}", exc.getMessage());
            LOG.trace("", exc);
        }
        catch (final IOException exc) {
            LOG.error(" ", exc);
        }

        return verdict;
    }

}
