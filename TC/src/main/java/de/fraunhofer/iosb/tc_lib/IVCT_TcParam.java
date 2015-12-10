package de.fraunhofer.iosb.tc_lib;

import java.net.URL;


public interface IVCT_TcParam {

    /**
     * @return the federate name
     */
    public String getFederationName();


    /**
     * @return the settings designator
     */
    public String getSettingsDesignator();


    /**
     * @return urls
     */
    public URL[] getUrls();

}
