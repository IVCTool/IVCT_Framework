/*
Copyright 2015, Johannes Mulder (Fraunhofer IOSB)

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/

package de.fraunhofer.iosb.tc_lib;

import hla.rti1516e.CallbackModel;
import hla.rti1516e.FederateAmbassador;
import hla.rti1516e.FederateHandle;
import hla.rti1516e.ResignAction;
import hla.rti1516e.encoding.EncoderFactory;
import hla.rti1516e.exceptions.AlreadyConnected;
import hla.rti1516e.exceptions.CallNotAllowedFromWithinCallback;
import hla.rti1516e.exceptions.ConnectionFailed;
import hla.rti1516e.exceptions.CouldNotCreateLogicalTimeFactory;
import hla.rti1516e.exceptions.CouldNotOpenFDD;
import hla.rti1516e.exceptions.ErrorReadingFDD;
import hla.rti1516e.exceptions.FederateAlreadyExecutionMember;
import hla.rti1516e.exceptions.FederateIsExecutionMember;
import hla.rti1516e.exceptions.FederateNotExecutionMember;
import hla.rti1516e.exceptions.FederateOwnsAttributes;
import hla.rti1516e.exceptions.FederatesCurrentlyJoined;
import hla.rti1516e.exceptions.FederationExecutionDoesNotExist;
import hla.rti1516e.exceptions.InconsistentFDD;
import hla.rti1516e.exceptions.InvalidLocalSettingsDesignator;
import hla.rti1516e.exceptions.InvalidResignAction;
import hla.rti1516e.exceptions.NotConnected;
import hla.rti1516e.exceptions.OwnershipAcquisitionPending;
import hla.rti1516e.exceptions.RTIinternalError;
import hla.rti1516e.exceptions.RestoreInProgress;
import hla.rti1516e.exceptions.SaveInProgress;
import hla.rti1516e.exceptions.UnsupportedCallbackModel;

import org.slf4j.Logger;


/**
 * @author mul (Fraunhofer IOSB)
 */
public class IVCT_BaseModel extends IVCT_NullFederateAmbassador {

    protected IVCT_RTIambassador ivct_rti;
    protected EncoderFactory _encoderFactory;
    private Logger logger;
    private IVCT_TcParam ivct_TcParam;
    private String settingsDesignator;
    private String federationName;  
    private String sutFederateName;  


    /**
     * @param logger logger
     * @param ivct_TcParam ivct_TcParam
     */
    public IVCT_BaseModel(final Logger logger, final IVCT_TcParam ivct_TcParam) {
        super(logger);
        this.logger = logger;
        ivct_rti = IVCT_RTI_Factory.getIVCT_RTI(logger);
        _encoderFactory = ivct_rti.getEncoderFactory();
        this.ivct_TcParam = ivct_TcParam;
    }

    public void startup() {
    }

    public void shutdown() {
        terminateRti();
    }

    /**
     * @param federateName federate name
     * @param federateReference federate reference
     * @return federate handle
     */
    public FederateHandle initiateRti(final String federateName, final FederateAmbassador federateReference) {
        // Connect to rti
        try {
            if ((this.settingsDesignator == null) || this.settingsDesignator.equals("")) {
                this.logger.info("initiateRTI: settingsDesignator is empty, using installation defaults");
                ivct_rti.connect(federateReference, CallbackModel.HLA_IMMEDIATE);
            }
            else {
                this.logger.info("initiateRTI: settingsDesignator is set to {}", this.settingsDesignator);
                ivct_rti.connect(federateReference, CallbackModel.HLA_IMMEDIATE, this.settingsDesignator);
            }
        }
        catch (AlreadyConnected e) {
            this.logger.warn("initiateRti: AlreadyConnected (ignored)");
        }
        catch (ConnectionFailed | InvalidLocalSettingsDesignator | UnsupportedCallbackModel | CallNotAllowedFromWithinCallback | RTIinternalError e) {
            this.logger.error("initiateRti: ", e);
            return null;
        }

        // Create federation execution using tc_param foms
        try {
        	ivct_rti.createFederationExecution(this.federationName, this.ivct_TcParam.getUrls(), "HLAfloat64Time");
        }
        catch (CouldNotCreateLogicalTimeFactory | InconsistentFDD | ErrorReadingFDD | CouldNotOpenFDD | NotConnected | RTIinternalError e) {
            this.logger.error("initiateRti: ", e);
            return null;
        }

        // Join federation execution
        try {
            return ivct_rti.joinFederationExecution(federateName, this.federationName, this.ivct_TcParam.getUrls());
        }
        catch (CouldNotCreateLogicalTimeFactory | FederationExecutionDoesNotExist | InconsistentFDD | ErrorReadingFDD | CouldNotOpenFDD | SaveInProgress | RestoreInProgress | FederateAlreadyExecutionMember | NotConnected | CallNotAllowedFromWithinCallback | RTIinternalError e) {
            this.logger.error("initiateRti: ", e);
        	return null;
        }
    }


    /**
     * Standard function to terminate the RTI
     */
    public void terminateRti() {
    	
        // Resign federation execution
        try {
        	ivct_rti.resignFederationExecution(ResignAction.DELETE_OBJECTS_THEN_DIVEST);
        }
        catch (NotConnected e) {
            this.logger.info("terminateRti: NotConnected (ignored)");
    		return;
        }
        catch (InvalidResignAction | OwnershipAcquisitionPending | FederateOwnsAttributes | FederateNotExecutionMember | CallNotAllowedFromWithinCallback | RTIinternalError e) {
    		this.logger.warn("resignFederationExecution exception={}", e.getMessage());
        }

        // Destroy federation execution
        try {
        	ivct_rti.destroyFederationExecution(this.federationName);
        }
        catch (final FederatesCurrentlyJoined e1) {
            this.logger.info("terminateRti: FederatesCurrentlyJoined (ignored)");
        }
        catch (NotConnected e) {
            this.logger.info("terminateRti: NotConnected (ignored)");
    		return;
        }
        catch (FederationExecutionDoesNotExist | RTIinternalError e) {
    		this.logger.error("destroyFederationExecution exception={}", e.getMessage());
        }

        // Disconnect from rti
        try {
        	ivct_rti.disconnect();
        }
        catch (FederateIsExecutionMember | CallNotAllowedFromWithinCallback | RTIinternalError e) {
    		this.logger.error("disconnect exception={}", e.getMessage());
        }
    }


    public void setSettingsDesignator(String settingsDesignator) {
        this.settingsDesignator = settingsDesignator;
    }


    public void setFederationName(String federationName) {
        this.federationName = federationName;
    }


	public String getSutFederateName() {
		return sutFederateName;
	}


	public void setSutFederateName(String sutFederateName) {
		this.sutFederateName = sutFederateName;
	}
}
