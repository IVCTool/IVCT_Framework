package de.fraunhofer.iosb.tc_lib;

import hla.rti1516e.FederateAmbassador;
import hla.rti1516e.FederateHandle;
import org.slf4j.Logger;


/**
 * @author mul (Fraunhofer IOSB)
 */
public class IVCT_BaseModel extends IVCT_NullFederateAmbassador {

    private FederateHandle     federateHandle;
    private IVCT_RTIambassador ivct_rti;


    /**
     * @param ivct_rti ivct rti
     * @param logger logger
     */
    public IVCT_BaseModel(final IVCT_RTIambassador ivct_rti, final Logger logger) {
        super(logger);
        this.ivct_rti = ivct_rti;
    }


    /**
     * @return the federate handle for this federate
     */
    public FederateHandle getFederateHandle() {
        return this.federateHandle;
    }


    /**
     * @param federateName federate name
     * @param federateReference federate reference
     * @param tcParam test case parameters
     * @return federate handle
     */
    public FederateHandle initiateRti(final String federateName, final FederateAmbassador federateReference, final IVCT_TcParam tcParam) {
        this.federateHandle = this.ivct_rti.initiateRti(tcParam, federateReference, federateName);
        return this.federateHandle;
    }


    /**
     * @param tcParam the test case parameters
     */
    public void terminateRti(final IVCT_TcParam tcParam) {
        this.ivct_rti.terminateRti(tcParam);
    }
}
