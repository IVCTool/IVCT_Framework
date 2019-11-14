package nato.ivct.gui.server.sut;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import org.eclipse.scout.rt.platform.resource.BinaryResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nato.ivct.commander.Factory;
import nato.ivct.gui.server.ServerSession;
import nato.ivct.gui.shared.sut.ISuTCbService;
import nato.ivct.gui.shared.sut.SuTCbNodePageData;


public class SuTCbService implements ISuTCbService {
    private static final Logger                        LOG    = LoggerFactory.getLogger(ServerSession.class);
    private static HashMap<String, SuTCbNodePageData> cap_hm = new HashMap<>();

    @Override
    public BinaryResource getFileContent(final String sutId, final String tsId, final String fileName) {
        BinaryResource fileContent = null;

        try {
            fileContent = new BinaryResource(fileName, Files.readAllBytes(Paths.get(Factory.getSutPathsFiles().getTcParamPath(sutId, tsId)).resolve(fileName)));
        }
        catch (IOException | InvalidPathException exc) {
            LOG.error("error to access fileName %", fileName);
            exc.printStackTrace();
            fileContent = new BinaryResource(fileName, null);
        }

        return fileContent;
    }


    @Override
    public boolean copyUploadedTcExtraParameterFile(final String sutId, final String tsId, final BinaryResource file) {
        try {
            Files.copy(new ByteArrayInputStream(file.getContent()), Paths.get(Factory.getSutPathsFiles().getTcParamPath(sutId, tsId)).resolve(file.getFilename()), StandardCopyOption.REPLACE_EXISTING);
        }
        catch (final IOException exc) {
            LOG.error("error when copying file %", file.getFilename());
            exc.printStackTrace();
            return false;
        }

        return true;
    }
    
    @Override
    public boolean deleteUploadedTcExtraParameterFile(final String sutId, final String tsId, final BinaryResource file) {
        try {
            return Files.deleteIfExists(Paths.get(Factory.getSutPathsFiles().getTcParamPath(sutId, tsId)).resolve(file.getFilename()));
        }
        catch (final IOException exc) {
            LOG.error("error when deleting file %", file.getFilename());
            exc.printStackTrace();
            return false;
        }
    }


    @Override
    public ArrayList<String> loadTcExtraParameterFiles(final String sutId, final String tsId) {
        final ArrayList<String> extraParamFileNames = new ArrayList<>();
        final Path folder = Paths.get(Factory.getSutPathsFiles().getTcParamPath(sutId, tsId));

        try {
            getExtraTcParamFilesOrderedByName(folder).sorted().forEachOrdered(path -> {
                extraParamFileNames.add(path.getFileName().toString());
                LOG.info("Extra parameter file found: {}", path.getFileName().toString());
            });
        }
        catch (final NoSuchFileException exc) {
            LOG.info("no extra TC parameter files found in folder: {}", folder);
        }
        catch (final IOException exc) {
            exc.printStackTrace();
        }
        finally {
            return extraParamFileNames;
        }
    }


    private Stream<Path> getExtraTcParamFilesOrderedByName(final Path folder) throws IOException {
        try {
            return Files.find(folder, 1, (path, fileAttributes) -> {
                final String filenameToCheck = path.getFileName().toString();
                // ignore the regular parameter file and all potential report files
                return fileAttributes.isRegularFile() && !filenameToCheck.equalsIgnoreCase("TcParam.json") && !Pattern.compile("report", Pattern.CASE_INSENSITIVE).matcher(filenameToCheck).find();
            }).sorted(new Comparator<Path>() {
                @Override
                public int compare(Path p1, Path p2) {
                    return p1.getFileName().toString().compareToIgnoreCase(p2.getFileName().toString());
                }
            });
        }
        catch (final IllegalStateException exc) {
            throw new IOException(exc);
        }
    }


    @Override
    public String loadTcParams(String sutId, String tsId) {
        Path paramFile = null;
        try {
            paramFile = getParamFile(sutId, tsId);
            if (paramFile == null) {
                LOG.info("TC parameter file for SuT" + sutId + " and testsuite " + tsId + " does not exist");
                return null;
            }
            LOG.debug("load TC parameters from file " + paramFile.toString());
            return new String(Files.readAllBytes(paramFile));
        }
        catch (final InvalidPathException e) {
            LOG.error("invalid path for TC parameter file for SuT" + sutId + " and testsuite " + tsId);
            return null;
        }
        catch (final Exception e) {
            LOG.error("could not read TC parameters from file " + paramFile.toString());
            e.printStackTrace();
            return null;
        }
    }


    @Override
    public boolean storeTcParams(String sutId, String tsId, String parameters) {
        Path paramFile = null;
        try {
            paramFile = getParamFile(sutId, tsId);
            if (paramFile == null) {
                LOG.info("TC parameter file for SuT" + sutId + " and testsuite " + tsId + " does not exist");
                return false;
            }
            LOG.debug("store TC parameters to file " + paramFile.toString());
            Files.write(paramFile, parameters.getBytes(), StandardOpenOption.TRUNCATE_EXISTING);
            return true;
        }
        catch (final InvalidPathException e) {
            LOG.error("invalid path for TC parameter file for SuT" + sutId + " and testsuite " + tsId);
            return false;
        }
        catch (final IOException e) {
            LOG.error("could not write badge parameters to file " + paramFile.toString());
            e.printStackTrace();
            return false;
        }
    }


    private Path getParamFile(String sutId, String tsId) throws InvalidPathException {
        final List<String> tcParamFiles = Factory.getSutPathsFiles().getTcParamFileNames(sutId, tsId, true);
        if (!tcParamFiles.isEmpty())
            return Paths.get(tcParamFiles.get(0));
        else
            return null;
    }
}
