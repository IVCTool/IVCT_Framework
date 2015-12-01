package de.fraunhofer.iosb.tc_lib;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;


/**
 * Store test case parameters
 *
 * @author Johannes Mulder (Fraunhofer IOSB)
 */
public class TcParamTmr implements IVCT_TcParam {
    // Get test case parameters
    //      use some constants for this example till we get params from a file
    //    private final String federation_name    = "NETN2";
    private final String federation_name    = "HelloWorld";
    private final String rtiHost            = "localhost";
    private final String settingsDesignator = "crcAddress=" + this.rtiHost;
    private final int    fileNum            = 10;
    private File[]       fddFiles           = new File[this.fileNum];
    private URL[]        urls               = new URL[this.fileNum];
    private final String basePath           = "build/resources/main/";
    private long         sleepTimeTmr       = 5000;
    private final String sutFederate        = "sutFederate";
    private final String suteFederate       = "suteFederate";


    public TcParamTmr() {
        // Initiate data

        this.fddFiles[0] = new File(this.basePath + "RPR-Switches_v2.0_draft19.10.xml");
        this.fddFiles[1] = new File(this.basePath + "RPR-Base_v2.0_draft19.10.xml");
        this.fddFiles[2] = new File(this.basePath + "RPR-Physical_v2.0_draft19.10.xml");
        this.fddFiles[3] = new File(this.basePath + "RPR-Aggregate_v2.0_draft19.10.xml");
        this.fddFiles[4] = new File(this.basePath + "NETN-Base_v1.0.2.xml");
        this.fddFiles[5] = new File(this.basePath + "NETN-Physical_v1.1.2.xml");
        this.fddFiles[6] = new File(this.basePath + "NETN-Aggregate_v1.0.4.xml");
        this.fddFiles[7] = new File(this.basePath + "TMR_v1.1.3.xml");
        this.fddFiles[8] = new File(this.basePath + "CBRN_v1.1.7.xml");
        this.fddFiles[9] = new File(this.basePath + "MRM_v1.1.1.xml");
        for (int i = 0; i < this.fileNum; i++) {
            try {
                this.urls[i] = this.fddFiles[i].toURI().toURL();
            }
            catch (final MalformedURLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }


    /**
     * @return the federation name
     */
    @Override
    public String getFederationName() {
        return this.federation_name;
    }


    /**
     * @return name of sut federate
     */
    public String getSutFederate() {
        return this.sutFederate;
    }


    /**
     * @return name of sute federate
     */
    public String getSuteFederate() {
        return this.suteFederate;
    }


    /**
     * @return the RTI host value
     */
    public String getRtiHost() {
        return this.rtiHost;
    }


    /**
     * @return the settings designator
     */
    @Override
    public String getSettingsDesignator() {
        return this.settingsDesignator;
    }


    /**
     * @return value of sleep time for tmr
     */
    public long getSleepTimeTmr() {
        return this.sleepTimeTmr;
    }


    /**
     * @return the urls
     */
    @Override
    public URL[] getUrls() {
        return this.urls;
    }
}
