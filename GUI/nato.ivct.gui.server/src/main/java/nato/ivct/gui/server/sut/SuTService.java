package nato.ivct.gui.server.sut;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

import org.eclipse.scout.rt.platform.exception.VetoException;
import org.eclipse.scout.rt.platform.job.IFuture;
import org.eclipse.scout.rt.platform.text.TEXTS;
import org.eclipse.scout.rt.platform.util.CollectionUtility;
import org.eclipse.scout.rt.shared.services.common.security.ACCESS;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nato.ivct.commander.BadgeTcParam;
import nato.ivct.commander.CmdListSuT;
import nato.ivct.commander.CmdListSuT.SutDescription;
import nato.ivct.commander.CmdUpdateSUT;
import nato.ivct.commander.Command;
import nato.ivct.gui.server.ServerSession;
import nato.ivct.gui.shared.sut.CreateSuTPermission;
import nato.ivct.gui.shared.sut.ISuTService;
import nato.ivct.gui.shared.sut.ReadSuTPermission;
import nato.ivct.gui.shared.sut.SuTEditFormData;
import nato.ivct.gui.shared.sut.SuTFormData;
import nato.ivct.gui.shared.sut.UpdateSuTPermission;

public class SuTService implements ISuTService {
	private static final Logger LOG = LoggerFactory.getLogger(ServerSession.class);
	private HashMap<String, SutDescription> sutMap = null;
	
	// TODO check whether or not this attribute is ever used
	HashMap<String, SutDescription> sut_hm = new HashMap<String, SutDescription>();
	
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
		// fill the form data: GeneralBox
		formData.getSutVendor().setValue(sut.vendor);
		formData.getDescr().setValue(sut.description);
		
		// TODO: get these data out from the data model
		formData.getVersion().setValue("VERSION");
		formData.setSutId(sut.ID);
		formData.getName().setValue(sut.ID); // TODO set the correct SUT name 

		// fill the form data: SuTCapabilities table with conformance status

		// fill the form data: SuTReports table
		
		return formData;
	}

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
		// fill the form data: GeneralBox
		formData.getSutVendor().setValue(sut.vendor);
		formData.getDescr().setValue(sut.description);
		
		// TODO: get these data out from the data model
		formData.getVersion().setValue("VERSION");
		formData.setSutId(sut.ID);
		formData.getName().setValue(sut.ID); // TODO set the correct SUT name 
        
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

        // get the selected capabilities
        HashSet<BadgeTcParam> badgeTcParams = CollectionUtility.emptyHashSet();
        Set<String> cb = formData.getSuTCapabilityBox().getValue();
        if (cb != null) {
	        cb.forEach(bd->badgeTcParams.add(new BadgeTcParam().setId(bd)));
        }
        
        // save SuT
        try {
        	LOG.info ("SuT description stored for: " + formData.getName().getValue());
			new CmdUpdateSUT(formData.getName().getValue(), formData.getDescr().getValue(), formData.getSutVendor().getValue(), badgeTcParams).execute();
		} catch (Exception e) {
			LOG.error("Error when storing SuT description for: " + formData.getName().getValue());
			e.printStackTrace();
		}
        
        // Update SuT map 
        updateSutMap();
        
        return formData;
    }


	@Override
	public SuTEditFormData store(SuTEditFormData formData) {
		if (!ACCESS.check(new UpdateSuTPermission())) {
			throw new VetoException(TEXTS.get("AuthorizationFailed"));
		}

        // get the selected capabilities
        HashSet<BadgeTcParam> badgeTcParams = CollectionUtility.emptyHashSet();
        Set<String> cb = formData.getSuTCapabilityBox().getValue();
        if (cb != null) {
	        cb.forEach(bd->badgeTcParams.add(new BadgeTcParam().setId(bd)));
        }
        
        // save SuT
        try {
        	LOG.info ("SuT description stored for: " + formData.getName().getValue());
			new CmdUpdateSUT(formData.getSutId(), formData.getDescr().getValue(), formData.getSutVendor().getValue(), badgeTcParams).execute(); //TODO Add SUT name as well
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
