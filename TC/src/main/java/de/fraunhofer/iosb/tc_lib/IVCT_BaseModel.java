package de.fraunhofer.iosb.tc_lib;

import hla.rti1516e.CallbackModel;
import hla.rti1516e.FederateAmbassador;
import hla.rti1516e.FederateHandle;


/**
 * @author mul (Fraunhofer IOSB)
 */
public interface IVCT_BaseModel {
    /**
     * @param tcParam the test case parameters
     */
    public FederateHandle initiateRti(final String federateName, final FederateAmbassador federateReference, final IVCT_TcParam tcParam);


    /**
     * @param federateReference
     * @param callbackModel
     * @param localSettingsDesignator
     */
    public void connect(final FederateAmbassador federateReference, final CallbackModel callbackModel, final String localSettingsDesignator);


    /**
     * @param tcParam
     */
    public void terminateRti(final IVCT_TcParam tcParam);
}
