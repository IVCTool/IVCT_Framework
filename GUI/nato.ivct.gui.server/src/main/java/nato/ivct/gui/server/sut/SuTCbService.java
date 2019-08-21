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
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import org.eclipse.scout.rt.platform.BEANS;
import org.eclipse.scout.rt.platform.exception.VetoException;
import org.eclipse.scout.rt.platform.resource.BinaryResource;
import org.eclipse.scout.rt.platform.text.TEXTS;
import org.eclipse.scout.rt.shared.services.common.jdbc.SearchFilter;
import org.eclipse.scout.rt.shared.services.common.security.ACCESS;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nato.ivct.commander.BadgeDescription;
import nato.ivct.commander.Factory;
import nato.ivct.commander.SutDescription;
import nato.ivct.gui.server.ServerSession;
import nato.ivct.gui.server.cb.CbService;
import nato.ivct.gui.shared.cb.CreateCbPermission;
import nato.ivct.gui.shared.cb.ReadCbPermission;
import nato.ivct.gui.shared.cb.UpdateCbPermission;
import nato.ivct.gui.shared.sut.ISuTCbService;
import nato.ivct.gui.shared.sut.SuTCbFormData;
import nato.ivct.gui.shared.sut.SuTCbFormData.CbRequirementsTable.CbRequirementsTableRowData;
import nato.ivct.gui.shared.sut.SuTCbFormData.SutTcExtraParameterTable.SutTcExtraParameterTableRowData;
import nato.ivct.gui.shared.sut.SuTCbTablePageData;
import nato.ivct.gui.shared.sut.SuTCbTablePageData.SuTCbTableRowData;

public class SuTCbService implements ISuTCbService {
	private static final Logger LOG = LoggerFactory.getLogger(ServerSession.class);
	private static HashMap<String, SuTCbTablePageData> cap_hm = new HashMap<String, SuTCbTablePageData>();

	/*
	 * get CapapbilityTablePageData for a specific SuT id. Create new one or select existing
	 * 
	 * @see nato.ivct.gui.shared.sut.ICapabilityService#getCapabilityTableData(org.eclipse.scout.rt.shared.services.common.jdbc.SearchFilter)
	 */
	@Override
	public SuTCbTablePageData getSuTCbTableData(SearchFilter filter) {
		String[] searchText = filter.getDisplayTexts();
		SuTCbTablePageData pageData = cap_hm.get (searchText);
		if (pageData == null) {
			pageData = new SuTCbTablePageData();
		}

		LOG.info("getCapabilityTableData");
		CbService cbService = BEANS.get(CbService.class);
		BadgeDescription badge = cbService.getBadgeDescription(searchText[0]);
		
		for (nato.ivct.commander.InteroperabilityRequirement requirement : badge.requirements.values()) {
			SuTCbTableRowData row = pageData.addRow();
			row.setRequirementId(requirement.ID);
			row.setRequirementDesc(requirement.description);
//			row.setAbstractTC(badge.requirements[j].TC);
//			row.setTCstatus("no result");
		};
		
		cap_hm.put(badge.ID, pageData);
		return pageData;
	}

	public void executeTestCase(String sutId, String tc, String badgeId) {
		// execute the CmdStartTc commands
		SutDescription sut =BEANS.get(SuTService.class).getSutDescription(sutId);
		ServerSession.get().execStartTc(sutId, tc, badgeId, sut.settingsDesignator, sut.federation);
		// mark test cases as being started
		SuTCbTablePageData capPage = cap_hm.get(badgeId);
		if (capPage == null) {
			LOG.error("no capability map found for badge: " + badgeId);
		} else {
			for (int i = 0; i < capPage.getRowCount(); i++) {
				SuTCbTableRowData row = capPage.rowAt(i);
				if (row.getAbstractTC().equals(tc)) {
					row.setTCstatus("starting");
				}
			}
		}
	}

	@Override
	public SuTCbFormData load(SuTCbFormData formData) {
		LOG.info("load");
		if (!ACCESS.check(new ReadCbPermission())) {
			throw new VetoException(TEXTS.get("AuthorizationFailed"));
		}

		CbService cbService = BEANS.get(CbService.class);
		BadgeDescription badgeDescription = cbService.getBadgeDescription(formData.getCbId());
		formData.getCbName().setValue(badgeDescription.name);
		formData.getCbDescription().setValue(badgeDescription.description);
		
		// fill requirement table of this form
		importRequirements(formData, badgeDescription);
		
		// load extra TC parameter files
		loadTcExtraParameterFiles(formData);

		return formData;
	}


	@Override
	public BinaryResource getFileContent(final String sutId, final String cbId, final String fileName) {
		BinaryResource fileContent = null;
		
		try {
			fileContent = new BinaryResource(fileName, Files.readAllBytes(Paths.get(Factory.getSutPathsFiles().getTcParamPath(sutId, cbId)).resolve(fileName)));
		} catch (IOException | InvalidPathException exc) {
			LOG.error("error to access fileName %", fileName);
			// TODO Auto-generated catch block
			exc.printStackTrace();
			fileContent = new BinaryResource(fileName, null);
		}  
		
		return fileContent;
	}

	@Override
	public boolean copyUploadedTcExtraParameterFile(final String sutId, final String cbId, final BinaryResource file) {
		try {
			Files.copy(new ByteArrayInputStream(file.getContent()), Paths.get(Factory.getSutPathsFiles().getTcParamPath(sutId, cbId)).resolve(file.getFilename()), StandardCopyOption.REPLACE_EXISTING);
		} catch (IOException exc) {
			LOG.error("error when copying file %", file.getFilename());
			exc.printStackTrace();
			return false;
		}
		
		return true;
	}

	private SuTCbFormData loadTcExtraParameterFiles(SuTCbFormData cbFd) {
		final Path folder = Paths.get(Factory.getSutPathsFiles().getTcParamPath(cbFd.getSutIdProperty().getValue(), cbFd.getCbIdProperty().getValue()));
		SutTcExtraParameterTableRowData[] oldRows = cbFd.getSutTcExtraParameterTable().getRows();	
		try {
			cbFd.getSutTcExtraParameterTable().clearRows();
			getExtraTcParamFilesOrderedByName(folder).forEach(path -> {
				String logFileName = path.getFileName().toString();
				LOG.info("Log file found: {}" ,logFileName);
				// add files to table
				cbFd.getSutTcExtraParameterTable().addRow().setFileName(logFileName);
			});
		} catch (NoSuchFileException exc) {
            LOG.info("no extra TC parameter files found in folder: {}", folder);
		} catch (IOException exc) {
			exc.printStackTrace();
			// fallback to original state
			cbFd.getSutTcExtraParameterTable().clearRows();
			cbFd.getSutTcExtraParameterTable().setRows(oldRows);
		}

		return cbFd;
	}


	private Stream<Path> getExtraTcParamFilesOrderedByName(final Path folder) throws IOException {
		try {
			return Files.find(folder, 1, (path, fileAttributes) -> {
				String filenameToCheck = path.getFileName().toString();
				// ignore the regular parameter file and all potential report files
				return fileAttributes.isRegularFile() && !filenameToCheck.equalsIgnoreCase("TcParam.json") &&  !Pattern.compile("report", Pattern.CASE_INSENSITIVE).matcher(filenameToCheck).find();
			}).sorted(new Comparator<Path>() {
				@Override
				public int compare(Path p1, Path p2) {return p1.getFileName().toString().compareToIgnoreCase(p2.getFileName().toString());}
			});
		} catch (IllegalStateException exc) {
			throw new IOException(exc);
		}
	}

	@Override
	public String loadTcParams(String sutId, String badgeId) {
		Path paramFile = null;
		try {
			paramFile = getParamFile(sutId, badgeId);
			if (paramFile == null) {
				LOG.info("badge parameter file for SuT" + sutId + " and badge " + badgeId + " does not exist");
				return null;
			}
			LOG.debug("load TC parameters from file " + paramFile.toString());
			return new String(Files.readAllBytes(paramFile));
		} catch (InvalidPathException e) {
			LOG.error("invalid path for TC parameter file for SuT" + sutId + " and badge " + badgeId);
			return null;
		} catch (Exception e) {
			LOG.error("could not read TC parameters from file " + paramFile.toString());
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public boolean storeTcParams(String sutId, String badgeId, String parameters) {
        Path paramFile = null;
        try {
            paramFile = getParamFile(sutId, badgeId);
			if (paramFile == null) {
				LOG.info("badge parameter file for SuT" + sutId + " and badge " + badgeId + " does not exist");
				return false;
			}
            LOG.debug("store TC parameters to file " + paramFile.toString());
            Files.write(paramFile, parameters.getBytes(), StandardOpenOption.TRUNCATE_EXISTING);
            return true;
        } catch (InvalidPathException e) {
            LOG.error("invalid path for TC parameter file for SuT" + sutId + " and badge " + badgeId);
            return false;
        } catch (IOException e) {
            LOG.error("could not write badge parameters to file " + paramFile.toString());
            e.printStackTrace();
            return false;
        }
	}
	
	private Path getParamFile (String sutId, String badgeId) throws InvalidPathException {
	    List<String> tcParamFiles = Factory.getSutPathsFiles().getTcParamFileNames(sutId, badgeId, true);
	    if (!tcParamFiles.isEmpty())
	        return Paths.get(tcParamFiles.get(0));
	    else
	        return null;
	}

	private SuTCbFormData importRequirements(final SuTCbFormData fd, final BadgeDescription bd) {
		bd.requirements.forEach((key, requirement) -> {
			CbRequirementsTableRowData row = fd.getCbRequirementsTable().addRow();
			row.setRequirementId(requirement.ID);
			row.setRequirementDesc(requirement.description);
//			row.setAbstractTC(requirement.TC);
		});
		
		return fd;
	}

	@Override
	public SuTCbFormData store(SuTCbFormData formData) {
		LOG.info("store");
		if (!ACCESS.check(new UpdateCbPermission())) {
			throw new VetoException(TEXTS.get("AuthorizationFailed"));
		}
		
		return formData;
	}

	@Override
	public SuTCbFormData prepareCreate(SuTCbFormData formData) {
		LOG.info("prepareCreate");
		if (!ACCESS.check(new CreateCbPermission())) {
			throw new VetoException(TEXTS.get("AuthorizationFailed"));
		}
		return formData;
	}

	@Override
	public SuTCbFormData create(SuTCbFormData formData) {
		LOG.info("create");
		if (!ACCESS.check(new CreateCbPermission())) {
			throw new VetoException(TEXTS.get("AuthorizationFailed"));
		}
		
		return formData;
	}
}
