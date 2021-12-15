package dis_lib;

import de.fraunhofer.iosb.tc_lib_if.*;

public class IVCT_BaseModel implements IVCT_BaseModelIf {

    private String sutName = "DIS Simulator";

    @Override
    public void startup() {

    }

    @Override
    public void shutdown() {

    }

    @Override
    public void setSettingsDesignator(String settingsDesignator) {
    }

    @Override
    public void setFederationName(String federationName) {
    }

    @Override
	public String getSutFederateName() {
        return this.sutName;
    }

    @Override
	public void setSutFederateName(String sutFederateName) {
    }
    
}
