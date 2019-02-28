package nato.ivct.gui.server.sut;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.eclipse.scout.rt.platform.exception.VetoException;
import org.eclipse.scout.rt.platform.job.IFuture;
import org.eclipse.scout.rt.platform.text.TEXTS;
import org.eclipse.scout.rt.shared.services.common.security.ACCESS;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nato.ivct.commander.CmdListSuT;
import nato.ivct.commander.CmdUpdateSUT;
import nato.ivct.commander.Command;
import nato.ivct.commander.Factory;
import nato.ivct.commander.SutDescription;
import nato.ivct.gui.server.ServerSession;
import nato.ivct.gui.shared.sut.CreateSuTPermission;
import nato.ivct.gui.shared.sut.ISuTService;
import nato.ivct.gui.shared.sut.ReadSuTPermission;
import nato.ivct.gui.shared.sut.SuTEditFormData;
import nato.ivct.gui.shared.sut.SuTFormData;
import nato.ivct.gui.shared.sut.TestReportFormData;
import nato.ivct.gui.shared.sut.UpdateSuTPermission;

public class SuTService implements ISuTService {
	private static final Logger LOG = LoggerFactory.getLogger(ServerSession.class);
	private HashMap<String, SutDescription> sutMap = null;
	
	@Override
	public Set<String> loadSuts() {
		if (sutMap == null)
			// load SuT descriptions
			waitForSutLoading(); 

		return new TreeSet<>(sutMap.keySet());
	}
	
	public SutDescription getSutDescription(String sutId) {
		return sutMap.get(sutId);
	}
	
	private void waitForSutLoading() {
		IFuture<CmdListSuT> future1 = ServerSession.get().getCmdJobs();
		ServerSession.get().getLoadBadgesJob().awaitDone();
		Command resultSuT = future1.awaitDoneAndGet();
		// copy sut descriptions into table rows
		CmdListSuT sutCmd = (CmdListSuT) resultSuT;
		sutMap = sutCmd.sutMap;
	}

	/*
	 * functions for SuTFormData
	 */
	
	@Override
	public SuTFormData load(SuTFormData formData) {
		LOG.info (getClass().toString()+".load");
		if (!ACCESS.check(new ReadSuTPermission())) {
			throw new VetoException(TEXTS.get("AuthorizationFailed"));
		}
		// find the SuT description by selected SuTid.
		SutDescription sut = sutMap.get(formData.getSutId());
		formData.setSutId(sut.ID);

		// fill the form data: GeneralBox
		formData.getName().setValue(sut.name == null ? sut.ID : sut.name);
		formData.getVersion().setValue(sut.version);
		formData.getSutVendor().setValue(sut.vendor);
		formData.getDescr().setValue(sut.description);

		// fill the form data: SuTCapabilities table with conformance status

		// fill the form data: SuTReports table
		loadReportFiles(formData, formData.getSutId());
		
		return formData;
	}

//>>>>>
	
	/*
	 * functions for TestReport
	 */
	
	@Override
	public TestReportFormData load(TestReportFormData formData) {
		String testReportFileName = formData.getReportFileName();

		// get content of the requested report file
		List<String> testReportFiles = Factory.getSutPathsFiles().getSutReportFileNames(formData.getSutIdProperty().getValue(), true);
		testReportFiles.stream().filter(value -> value.contains(testReportFileName))
		.map(Paths::get).findAny()
		.ifPresent(path -> {	
			try {
				formData.getTestReport().setValue(java.nio.file.Files.lines(path).collect(Collectors.joining("\n")));
			} catch (IOException exc) {
				exc.printStackTrace();
			}
		});
		
		return formData;
	}
	
	private SuTFormData loadReportFiles(SuTFormData fd, String sutId) {
		final Path folder = Paths.get(Factory.getSutPathsFiles().getReportPath(sutId));

		try {
			getReportFilesOrderedByCreationDate(folder).forEach(path -> {
				String reportFileName = path.getFileName().toString();
				LOG.info("Log file found: {}" ,reportFileName);
				fd.getTestReportTable().addRow().setFileName(reportFileName);
			});
		} catch (NoSuchFileException exc) {
            LOG.info("report files not found in folder: {}", folder);
		} catch (IOException exc) {
			exc.printStackTrace();
		}

		return fd;
	}

	private Stream<Path> getReportFilesOrderedByCreationDate(final Path folder) throws IOException {
		try {
			return Files.find(folder, 1, (path, fileAttributes) -> {
				String filenameToCheck = path.getFileName().toString();
				return fileAttributes.isRegularFile() && filenameToCheck.endsWith(".txt");
			}).sorted(new FileCreationTimeComparator().reversed());
		} catch (IllegalStateException exc) {
			throw new IOException(exc);
		}
	}

	private static final class FileCreationTimeComparator implements Comparator<Path> {

		@Override
		public int compare(Path path1, Path path2) {
			try {
				return Files.readAttributes(path1, BasicFileAttributes.class).creationTime()
						.compareTo(Files.readAttributes(path2, BasicFileAttributes.class).creationTime());
			} catch (IOException exc) {
				throw new IllegalStateException(exc);
			}
		}
	}
	
	
//<<<<<<<<<<<<<<<<<<<>	
	
	/*
	 *  functions for SuTEditFormData
	 */
	
    @Override
    public SuTEditFormData load(SuTEditFormData formData) {
        LOG.info (getClass().toString()+".load");
        if (!ACCESS.check(new ReadSuTPermission())) {
            throw new VetoException(TEXTS.get("AuthorizationFailed"));
        }
        // find the SuT description by selected SuTid.
        SutDescription sut = sutMap.get(formData.getSutId());
		formData.setSutId(sut.ID);
		
        // fill the form data: GeneralBox
		formData.getName().setValue(sut.name == null ? "" : sut.name); 
		formData.getVersion().setValue(sut.version);
		formData.getSutVendor().setValue(sut.vendor);
		formData.getDescr().setValue(sut.description);
        
        return formData;
    }

    @Override
    public SuTEditFormData prepareCreate(SuTEditFormData formData) {
        LOG.info (getClass().toString()+".prepareCreate");
        if (!ACCESS.check(new CreateSuTPermission())) {
            throw new VetoException(TEXTS.get("AuthorizationFailed"));
        }

        return formData;
    }

    @Override
    public SuTEditFormData create(SuTEditFormData formData) {
        LOG.info (getClass().toString()+".create");
        if (!ACCESS.check(new CreateSuTPermission())) {
            throw new VetoException(TEXTS.get("AuthorizationFailed"));
        }
        
        // fill the SUT description with the from values
        SutDescription sut = new SutDescription();
        sut.ID = formData.getName().getValue().replaceAll("\\W", "_");
        sut.name = formData.getName().getValue();
        sut.version = formData.getVersion().getValue();
        sut.description = formData.getDescr().getValue();
        sut.vendor = formData.getSutVendor().getValue();
        
        // get the selected capabilities
        Set<String> cb = formData.getSuTCapabilityBox().getValue();
        if (cb != null)
        	sut.conformanceStatement = cb.toArray(new String[cb.size()]);
        
        // save SuT
        try {
        	LOG.info ("SuT description stored for: " + formData.getName().getValue());
			new CmdUpdateSUT(sut).execute();
		} catch (Exception e) {
			LOG.error("Error when storing SuT description for: " + formData.getName().getValue());
			e.printStackTrace();
		}
        
        // set the SUT ID in the form
        formData.setSutId(sut.ID);
        
        // Update SuT map 
        updateSutMap();
        
        return formData;
    }


	@Override
	public SuTEditFormData store(SuTEditFormData formData) {
		if (!ACCESS.check(new UpdateSuTPermission())) {
			throw new VetoException(TEXTS.get("AuthorizationFailed"));
		}
		
        // fill the SUT description with the from values
        SutDescription sut = new SutDescription();
        sut.ID = formData.getSutId();
        sut.name = formData.getName().getValue();
        sut.version = formData.getVersion().getValue();
        sut.description = formData.getDescr().getValue();
        sut.vendor = formData.getSutVendor().getValue();

        // get the selected capabilities
        Set<String> cb = formData.getSuTCapabilityBox().getValue();
        if (cb != null)
        	sut.conformanceStatement = cb.toArray(new String[cb.size()]);
        
        // save SuT
        try {
        	LOG.info ("SuT description stored for: " + formData.getName().getValue());
			new CmdUpdateSUT(sut).execute();
		} catch (Exception e) {
			LOG.error("Error when storing SuT description for: " + formData.getName().getValue());
			e.printStackTrace();
		}
        
        //update SuT map
        updateSutMap();

		return formData;
	}
	
	private void updateSutMap ( ) {
		LOG.info ("update sutMap attribute");
		// TODO must be better re-written!
        CmdListSuT sutCmd = new CmdListSuT();
        sutCmd.execute();
        sutMap = sutCmd.sutMap;
	}
}
