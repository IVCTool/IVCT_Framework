package dis_lib;

import de.fraunhofer.iosb.tc_lib_if.*;

public class IVCT_BaseModel {

    private String sutName = "DIS Simulator";

    public void startup() {

    }

    public void shutdown() {

    }

    public void setSettingsDesignator(String settingsDesignator) {
    }

    public void setFederationName(String federationName) {
    }

	public String getSutFederateName() {
        return this.sutName;
    }

	public void setSutFederateName(String sutFederateName) {
    }
    
}
