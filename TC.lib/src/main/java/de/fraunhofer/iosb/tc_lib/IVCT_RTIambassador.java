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

import hla.rti1516e.AttributeHandle;
import hla.rti1516e.AttributeHandleFactory;
import hla.rti1516e.AttributeHandleSet;
import hla.rti1516e.AttributeHandleSetFactory;
import hla.rti1516e.AttributeHandleValueMap;
import hla.rti1516e.AttributeHandleValueMapFactory;
import hla.rti1516e.AttributeSetRegionSetPairList;
import hla.rti1516e.AttributeSetRegionSetPairListFactory;
import hla.rti1516e.CallbackModel;
import hla.rti1516e.DimensionHandle;
import hla.rti1516e.DimensionHandleFactory;
import hla.rti1516e.DimensionHandleSet;
import hla.rti1516e.DimensionHandleSetFactory;
import hla.rti1516e.FederateAmbassador;
import hla.rti1516e.FederateHandle;
import hla.rti1516e.FederateHandleFactory;
import hla.rti1516e.FederateHandleSet;
import hla.rti1516e.FederateHandleSetFactory;
import hla.rti1516e.InteractionClassHandle;
import hla.rti1516e.InteractionClassHandleFactory;
import hla.rti1516e.LogicalTime;
import hla.rti1516e.LogicalTimeFactory;
import hla.rti1516e.LogicalTimeInterval;
import hla.rti1516e.MessageRetractionHandle;
import hla.rti1516e.MessageRetractionReturn;
import hla.rti1516e.ObjectClassHandle;
import hla.rti1516e.ObjectClassHandleFactory;
import hla.rti1516e.ObjectInstanceHandle;
import hla.rti1516e.ObjectInstanceHandleFactory;
import hla.rti1516e.OrderType;
import hla.rti1516e.ParameterHandle;
import hla.rti1516e.ParameterHandleFactory;
import hla.rti1516e.ParameterHandleValueMap;
import hla.rti1516e.ParameterHandleValueMapFactory;
import hla.rti1516e.RTIambassador;
import hla.rti1516e.RangeBounds;
import hla.rti1516e.RegionHandle;
import hla.rti1516e.RegionHandleSet;
import hla.rti1516e.RegionHandleSetFactory;
import hla.rti1516e.ResignAction;
import hla.rti1516e.ServiceGroup;
import hla.rti1516e.TimeQueryReturn;
import hla.rti1516e.TransportationTypeHandle;
import hla.rti1516e.TransportationTypeHandleFactory;
import hla.rti1516e.encoding.EncoderFactory;
import hla.rti1516e.exceptions.AlreadyConnected;
import hla.rti1516e.exceptions.AsynchronousDeliveryAlreadyDisabled;
import hla.rti1516e.exceptions.AsynchronousDeliveryAlreadyEnabled;
import hla.rti1516e.exceptions.AttributeAcquisitionWasNotRequested;
import hla.rti1516e.exceptions.AttributeAlreadyBeingAcquired;
import hla.rti1516e.exceptions.AttributeAlreadyBeingChanged;
import hla.rti1516e.exceptions.AttributeAlreadyBeingDivested;
import hla.rti1516e.exceptions.AttributeAlreadyOwned;
import hla.rti1516e.exceptions.AttributeDivestitureWasNotRequested;
import hla.rti1516e.exceptions.AttributeNotDefined;
import hla.rti1516e.exceptions.AttributeNotOwned;
import hla.rti1516e.exceptions.AttributeNotPublished;
import hla.rti1516e.exceptions.AttributeRelevanceAdvisorySwitchIsOff;
import hla.rti1516e.exceptions.AttributeRelevanceAdvisorySwitchIsOn;
import hla.rti1516e.exceptions.AttributeScopeAdvisorySwitchIsOff;
import hla.rti1516e.exceptions.AttributeScopeAdvisorySwitchIsOn;
import hla.rti1516e.exceptions.CallNotAllowedFromWithinCallback;
import hla.rti1516e.exceptions.ConnectionFailed;
import hla.rti1516e.exceptions.CouldNotCreateLogicalTimeFactory;
import hla.rti1516e.exceptions.CouldNotOpenFDD;
import hla.rti1516e.exceptions.CouldNotOpenMIM;
import hla.rti1516e.exceptions.DeletePrivilegeNotHeld;
import hla.rti1516e.exceptions.DesignatorIsHLAstandardMIM;
import hla.rti1516e.exceptions.ErrorReadingFDD;
import hla.rti1516e.exceptions.ErrorReadingMIM;
import hla.rti1516e.exceptions.FederateAlreadyExecutionMember;
import hla.rti1516e.exceptions.FederateHandleNotKnown;
import hla.rti1516e.exceptions.FederateHasNotBegunSave;
import hla.rti1516e.exceptions.FederateIsExecutionMember;
import hla.rti1516e.exceptions.FederateNameAlreadyInUse;
import hla.rti1516e.exceptions.FederateNotExecutionMember;
import hla.rti1516e.exceptions.FederateOwnsAttributes;
import hla.rti1516e.exceptions.FederateServiceInvocationsAreBeingReportedViaMOM;
import hla.rti1516e.exceptions.FederateUnableToUseTime;
import hla.rti1516e.exceptions.FederatesCurrentlyJoined;
import hla.rti1516e.exceptions.FederationExecutionAlreadyExists;
import hla.rti1516e.exceptions.FederationExecutionDoesNotExist;
import hla.rti1516e.exceptions.IllegalName;
import hla.rti1516e.exceptions.InTimeAdvancingState;
import hla.rti1516e.exceptions.InconsistentFDD;
import hla.rti1516e.exceptions.InteractionClassAlreadyBeingChanged;
import hla.rti1516e.exceptions.InteractionClassNotDefined;
import hla.rti1516e.exceptions.InteractionClassNotPublished;
import hla.rti1516e.exceptions.InteractionParameterNotDefined;
import hla.rti1516e.exceptions.InteractionRelevanceAdvisorySwitchIsOff;
import hla.rti1516e.exceptions.InteractionRelevanceAdvisorySwitchIsOn;
import hla.rti1516e.exceptions.InvalidAttributeHandle;
import hla.rti1516e.exceptions.InvalidDimensionHandle;
import hla.rti1516e.exceptions.InvalidFederateHandle;
import hla.rti1516e.exceptions.InvalidInteractionClassHandle;
import hla.rti1516e.exceptions.InvalidLocalSettingsDesignator;
import hla.rti1516e.exceptions.InvalidLogicalTime;
import hla.rti1516e.exceptions.InvalidLookahead;
import hla.rti1516e.exceptions.InvalidMessageRetractionHandle;
import hla.rti1516e.exceptions.InvalidObjectClassHandle;
import hla.rti1516e.exceptions.InvalidOrderName;
import hla.rti1516e.exceptions.InvalidOrderType;
import hla.rti1516e.exceptions.InvalidParameterHandle;
import hla.rti1516e.exceptions.InvalidRangeBound;
import hla.rti1516e.exceptions.InvalidRegion;
import hla.rti1516e.exceptions.InvalidRegionContext;
import hla.rti1516e.exceptions.InvalidResignAction;
import hla.rti1516e.exceptions.InvalidServiceGroup;
import hla.rti1516e.exceptions.InvalidTransportationName;
import hla.rti1516e.exceptions.InvalidTransportationType;
import hla.rti1516e.exceptions.InvalidUpdateRateDesignator;
import hla.rti1516e.exceptions.LogicalTimeAlreadyPassed;
import hla.rti1516e.exceptions.MessageCanNoLongerBeRetracted;
import hla.rti1516e.exceptions.NameNotFound;
import hla.rti1516e.exceptions.NameSetWasEmpty;
import hla.rti1516e.exceptions.NoAcquisitionPending;
import hla.rti1516e.exceptions.NotConnected;
import hla.rti1516e.exceptions.ObjectClassNotDefined;
import hla.rti1516e.exceptions.ObjectClassNotPublished;
import hla.rti1516e.exceptions.ObjectClassRelevanceAdvisorySwitchIsOff;
import hla.rti1516e.exceptions.ObjectClassRelevanceAdvisorySwitchIsOn;
import hla.rti1516e.exceptions.ObjectInstanceNameInUse;
import hla.rti1516e.exceptions.ObjectInstanceNameNotReserved;
import hla.rti1516e.exceptions.ObjectInstanceNotKnown;
import hla.rti1516e.exceptions.OwnershipAcquisitionPending;
import hla.rti1516e.exceptions.RTIinternalError;
import hla.rti1516e.exceptions.RegionDoesNotContainSpecifiedDimension;
import hla.rti1516e.exceptions.RegionInUseForUpdateOrSubscription;
import hla.rti1516e.exceptions.RegionNotCreatedByThisFederate;
import hla.rti1516e.exceptions.RequestForTimeConstrainedPending;
import hla.rti1516e.exceptions.RequestForTimeRegulationPending;
import hla.rti1516e.exceptions.RestoreInProgress;
import hla.rti1516e.exceptions.RestoreNotInProgress;
import hla.rti1516e.exceptions.RestoreNotRequested;
import hla.rti1516e.exceptions.SaveInProgress;
import hla.rti1516e.exceptions.SaveNotInProgress;
import hla.rti1516e.exceptions.SaveNotInitiated;
import hla.rti1516e.exceptions.SynchronizationPointLabelNotAnnounced;
import hla.rti1516e.exceptions.TimeConstrainedAlreadyEnabled;
import hla.rti1516e.exceptions.TimeConstrainedIsNotEnabled;
import hla.rti1516e.exceptions.TimeRegulationAlreadyEnabled;
import hla.rti1516e.exceptions.TimeRegulationIsNotEnabled;
import hla.rti1516e.exceptions.UnsupportedCallbackModel;
import java.net.URL;
import java.util.Arrays;
import java.util.Set;
import org.slf4j.Logger;


/**
 * Provide functions to give added-value rti calls e.g. add logging to each rti
 * call e.g. combine connect, create and join within one call e.g. combine
 * resign and destroy within one call
 *
 * @author Johannes Mulder
 */
public class IVCT_RTIambassador implements RTIambassador {
    private RTIambassador      _rtiAmbassador;
    private EncoderFactory     encoderFactory;
    private Logger             logger;


    /**
     * @param theRTIAmbassador reference to the rti ambassador
     * @param encoderFactory encoder factory
     * @param logger reference to the logger
     */
    public IVCT_RTIambassador(final RTIambassador theRTIAmbassador, final EncoderFactory encoderFactory, final Logger logger) {
        this._rtiAmbassador = theRTIAmbassador;
        this.encoderFactory = encoderFactory;
        this.logger = logger;
    }


    /**
     * @return the encoder factory
     */
    public EncoderFactory getEncoderFactory() {
        return this.encoderFactory;
    }


    // 4.2
    /**
     * @param federateReference a reference to a user programmed callback
     * @param callbackModel the type of callback
     * @param localSettingsDesignator the settings for the rti
     */
    public void connect(final FederateAmbassador federateReference, final CallbackModel callbackModel, final String localSettingsDesignator) throws ConnectionFailed, InvalidLocalSettingsDesignator, UnsupportedCallbackModel, AlreadyConnected, CallNotAllowedFromWithinCallback, RTIinternalError {
		this.logger.trace("connect federateReference=" + federateReference.toString() + ", callbackModel=" + callbackModel.toString() + ", localSettingsDesignator=" + localSettingsDesignator);
		try {
			this._rtiAmbassador.connect(federateReference, callbackModel, localSettingsDesignator);
		} catch (ConnectionFailed | InvalidLocalSettingsDesignator | UnsupportedCallbackModel | AlreadyConnected | CallNotAllowedFromWithinCallback | RTIinternalError e) {
			this.logger.error("connect exception=" + e.getMessage());
			throw e;
		}
    }


    // 4.2
    /**
     * @param federateReference a reference to a user programmed callback
     * @param callbackModel the type of callback
     */
    public void connect(final FederateAmbassador federateReference, final CallbackModel callbackModel) throws ConnectionFailed, InvalidLocalSettingsDesignator, UnsupportedCallbackModel, AlreadyConnected, CallNotAllowedFromWithinCallback, RTIinternalError {
    	this.logger.trace("connect federateReference=" + federateReference.toString() + ", callbackModel=" + callbackModel.toString());
    	try {
    		this._rtiAmbassador.connect(federateReference, callbackModel);
    	} catch (ConnectionFailed | InvalidLocalSettingsDesignator | UnsupportedCallbackModel | AlreadyConnected | CallNotAllowedFromWithinCallback | RTIinternalError e) {
    		this.logger.error("connect exception=" + e.getMessage());
    		throw e;
    	}
    }


    // 4.3
    /**
     * @throws FederateIsExecutionMember federate is still joined
     * @throws CallNotAllowedFromWithinCallback a callback is in process
     * @throws RTIinternalError some rti internal error
     */
    public void disconnect() throws FederateIsExecutionMember, CallNotAllowedFromWithinCallback, RTIinternalError {
    	this.logger.trace("disconnect");
    	try {
    		this._rtiAmbassador.disconnect();
    	} catch (FederateIsExecutionMember | CallNotAllowedFromWithinCallback | RTIinternalError e) {
    		this.logger.error("disconnect exception=" + e.getMessage());
    		throw e;
    	}
    }


    //4.5
    /**
     * @param federationExecutionName federation execution name
     * @param fomModules fom modules
     * @param mimModule mim module
     * @param logicalTimeImplementationName logical time implementation name
     * @throws CouldNotCreateLogicalTimeFactory could not create logical time factory
     * @throws InconsistentFDD inconsistent fdd
     * @throws ErrorReadingFDD error reading fdd
     * @throws CouldNotOpenFDD could not open fdd
     * @throws ErrorReadingMIM error reading mim
     * @throws CouldNotOpenMIM could not open mim
     * @throws DesignatorIsHLAstandardMIM designator is hla standard mim
     * @throws NotConnected not connected
     * @throws RTIinternalError rti internal error
     */
    public void createFederationExecution(final String federationExecutionName, final URL[] fomModules, final URL mimModule, final String logicalTimeImplementationName) throws CouldNotCreateLogicalTimeFactory, InconsistentFDD, ErrorReadingFDD, CouldNotOpenFDD, ErrorReadingMIM, CouldNotOpenMIM, DesignatorIsHLAstandardMIM, NotConnected, RTIinternalError {
        this.logger.trace("createFederationExecution federationExecutionName=" + federationExecutionName + ", fomModules=" + Arrays.toString(fomModules) + ", mimModule=" + mimModule.toString() + ", logicalTimeImplementationName=" + logicalTimeImplementationName);
        try {
        	this._rtiAmbassador.createFederationExecution(federationExecutionName, fomModules, mimModule, logicalTimeImplementationName);
        } catch (FederationExecutionAlreadyExists e) {
        	this.logger.warn("createFederationExecution: {} already exists (ignored)", federationExecutionName);
        }
        catch (CouldNotCreateLogicalTimeFactory | InconsistentFDD | ErrorReadingFDD | CouldNotOpenFDD | ErrorReadingMIM | CouldNotOpenMIM | DesignatorIsHLAstandardMIM | NotConnected | RTIinternalError e) {
        	this.logger.error("createFederationExecution exception=" + e.getMessage());
        	throw e;
        }
    }


    //4.5
    /**
     * @param federationExecutionName federation execution name
     * @param fomModules fom modules
     * @param logicalTimeImplementationName logical time implementation name
     * @throws CouldNotCreateLogicalTimeFactory could not createLogical time factory
     * @throws InconsistentFDD inconsistent fdd
     * @throws ErrorReadingFDD error reading fdd
     * @throws CouldNotOpenFDD could not open fdd
     * @throws NotConnected not connected
     * @throws RTIinternalError rti internal error
     */
    public void createFederationExecution(final String federationExecutionName, final URL[] fomModules, final String logicalTimeImplementationName) throws CouldNotCreateLogicalTimeFactory, InconsistentFDD, ErrorReadingFDD, CouldNotOpenFDD, NotConnected, RTIinternalError {
        this.logger.trace("createFederationExecution federationExecutionName=" + federationExecutionName + ", fomModules=" + Arrays.toString(fomModules) + ", logicalTimeImplementationName=" + logicalTimeImplementationName);
        try {
        	this._rtiAmbassador.createFederationExecution(federationExecutionName, fomModules, logicalTimeImplementationName);
        } catch (FederationExecutionAlreadyExists e) {
        	this.logger.warn("createFederationExecution: {} already exists (ignored)", federationExecutionName);
        }
        catch (CouldNotCreateLogicalTimeFactory | InconsistentFDD | ErrorReadingFDD | CouldNotOpenFDD | NotConnected | RTIinternalError e) {
        	this.logger.error("createFederationExecution exception=" + e.getMessage());
        	throw e;
        }
    }


    //4.5
    public void createFederationExecution(final String federationExecutionName, final URL[] fomModules, final URL mimModule) throws InconsistentFDD, ErrorReadingFDD, CouldNotOpenFDD, ErrorReadingMIM, CouldNotOpenMIM, DesignatorIsHLAstandardMIM, NotConnected, RTIinternalError {
        this.logger.trace("createFederationExecution federationExecutionName=" + federationExecutionName + ", fomModules=" + Arrays.toString(fomModules) + ", mimModule=" + mimModule.toString());
        try {
        	this._rtiAmbassador.createFederationExecution(federationExecutionName, fomModules, mimModule);
        } catch (FederationExecutionAlreadyExists e) {
        	this.logger.warn("createFederationExecution: {} already exists (ignored)", federationExecutionName);
        }
        catch (InconsistentFDD | ErrorReadingFDD | CouldNotOpenFDD | ErrorReadingMIM | CouldNotOpenMIM | DesignatorIsHLAstandardMIM | NotConnected | RTIinternalError e) {
        	this.logger.error("createFederationExecution exception=" + e.getMessage());
        	throw e;
        }
    }


    //4.5
    public void createFederationExecution(final String federationExecutionName, final URL[] fomModules) throws InconsistentFDD, ErrorReadingFDD, CouldNotOpenFDD, NotConnected, RTIinternalError {
        this.logger.trace("createFederationExecution federationExecutionName=" + federationExecutionName + ", fomModules=" + Arrays.toString(fomModules));
        try {
        	this._rtiAmbassador.createFederationExecution(federationExecutionName, fomModules);
        } catch (FederationExecutionAlreadyExists e) {
        	this.logger.warn("createFederationExecution: {} already exists (ignored)", federationExecutionName);
        }
        catch (InconsistentFDD | ErrorReadingFDD | CouldNotOpenFDD | NotConnected | RTIinternalError e) {
        	this.logger.error("createFederationExecution exception=" + e.getMessage());
        	throw e;
        }
    }


    //4.5
    public void createFederationExecution(final String federationExecutionName, final URL fomModule) throws InconsistentFDD, ErrorReadingFDD, CouldNotOpenFDD, NotConnected, RTIinternalError {
        this.logger.trace("createFederationExecution federationExecutionName=" + federationExecutionName + ", fomModule=" + fomModule.toString());
        try {
        	this._rtiAmbassador.createFederationExecution(federationExecutionName, fomModule);
        } catch (FederationExecutionAlreadyExists e) {
        	this.logger.warn("createFederationExecution: {} already exists (ignored)", federationExecutionName);
        }
        catch (InconsistentFDD | ErrorReadingFDD | CouldNotOpenFDD | NotConnected | RTIinternalError e) {
        	this.logger.error("createFederationExecution exception=" + e.getMessage());
        	throw e;
        }
    }


    //4.6
    public void destroyFederationExecution(final String federationExecutionName) throws FederatesCurrentlyJoined, FederationExecutionDoesNotExist, NotConnected, RTIinternalError {
        this.logger.trace("destroyFederationExecution federationExecutionName=" + federationExecutionName);
        try {
        	this._rtiAmbassador.destroyFederationExecution(federationExecutionName);
        } catch (FederatesCurrentlyJoined | FederationExecutionDoesNotExist | NotConnected | RTIinternalError e) {
        	this.logger.error("destroyFederationExecution exception=" + e.getMessage());
        	throw e;
        }
    }


    // 4.7
    public void listFederationExecutions() throws NotConnected, RTIinternalError {
        this.logger.trace("listFederationExecutions");
        try {
        	this._rtiAmbassador.listFederationExecutions();
        } catch (NotConnected | RTIinternalError e) {
        	this.logger.error("listFederationExecutions exception=" + e.getMessage());
        	throw e;
        }
    }


    //4.9
    public FederateHandle joinFederationExecution(final String federateName, final String federateType, final String federationExecutionName, final URL[] additionalFomModules) throws CouldNotCreateLogicalTimeFactory, FederateNameAlreadyInUse, FederationExecutionDoesNotExist, InconsistentFDD, ErrorReadingFDD, CouldNotOpenFDD, SaveInProgress, RestoreInProgress, FederateAlreadyExecutionMember, NotConnected, CallNotAllowedFromWithinCallback, RTIinternalError {
        this.logger.trace("joinFederationExecution federateName=" + federateName + ", federateType=" + federateType + ", federationExecutionName=" + federationExecutionName + ", additionalFomModules=" + Arrays.toString(additionalFomModules));
        try {
            FederateHandle myFederateHandle;
        	myFederateHandle = this._rtiAmbassador.joinFederationExecution(federateName, federateType, federationExecutionName, additionalFomModules);
        	this.logger.trace("joinFederationExecution return " + myFederateHandle.toString());
        	return myFederateHandle;
        } catch (CouldNotCreateLogicalTimeFactory | FederateNameAlreadyInUse | FederationExecutionDoesNotExist | InconsistentFDD | ErrorReadingFDD | CouldNotOpenFDD | SaveInProgress | RestoreInProgress | FederateAlreadyExecutionMember | NotConnected | CallNotAllowedFromWithinCallback | RTIinternalError e) {
        	this.logger.error("joinFederationExecution exception=" + e.getMessage());
        	throw e;
        }
    }


    //4.9
    public FederateHandle joinFederationExecution(final String federateType, final String federationExecutionName, final URL[] additionalFomModules) throws CouldNotCreateLogicalTimeFactory, FederationExecutionDoesNotExist, InconsistentFDD, ErrorReadingFDD, CouldNotOpenFDD, SaveInProgress, RestoreInProgress, FederateAlreadyExecutionMember, NotConnected, CallNotAllowedFromWithinCallback, RTIinternalError {
    	this.logger.trace("joinFederationExecution federateType=" + federateType + ", federationExecutionName=" + federationExecutionName + ", additionalFomModules=" + Arrays.toString(additionalFomModules));
    	try {
    		final FederateHandle federateHandle = this._rtiAmbassador.joinFederationExecution(federateType, federationExecutionName, additionalFomModules);
    		this.logger.trace("joinFederationExecution return " + federateHandle.toString());
    		return federateHandle;
    	} catch (CouldNotCreateLogicalTimeFactory | FederationExecutionDoesNotExist | InconsistentFDD | ErrorReadingFDD | CouldNotOpenFDD | SaveInProgress | RestoreInProgress | FederateAlreadyExecutionMember | NotConnected | CallNotAllowedFromWithinCallback | RTIinternalError e) {
    		this.logger.error("joinFederationExecution exception=" + e.getMessage());
    		throw e;
    	}
    }


    //4.9
    public FederateHandle joinFederationExecution(final String federateName, final String federateType, final String federationExecutionName) throws CouldNotCreateLogicalTimeFactory, FederateNameAlreadyInUse, FederationExecutionDoesNotExist, SaveInProgress, RestoreInProgress, FederateAlreadyExecutionMember, NotConnected, CallNotAllowedFromWithinCallback, RTIinternalError {
    	this.logger.trace("joinFederationExecution federateName=" + federateName + ", federateType=" + federateType + ", federationExecutionName=" + federationExecutionName);
    	try {
    		final FederateHandle federateHandle = this._rtiAmbassador.joinFederationExecution(federateName, federateType, federationExecutionName);
    		this.logger.trace("joinFederationExecution return " + federateHandle.toString());
    		return federateHandle;
    	} catch (CouldNotCreateLogicalTimeFactory | FederateNameAlreadyInUse | FederationExecutionDoesNotExist | SaveInProgress | RestoreInProgress | FederateAlreadyExecutionMember | NotConnected | CallNotAllowedFromWithinCallback | RTIinternalError e) {
    		this.logger.error("joinFederationExecution exception=" + e.getMessage());
    		throw e;
    	}
    }


    //4.9
    public FederateHandle joinFederationExecution(final String federateType, final String federationExecutionName) throws CouldNotCreateLogicalTimeFactory, FederationExecutionDoesNotExist, SaveInProgress, RestoreInProgress, FederateAlreadyExecutionMember, NotConnected, CallNotAllowedFromWithinCallback, RTIinternalError {
    	this.logger.trace("joinFederationExecution federateType=" + federateType + ", federationExecutionName=" + federationExecutionName);
    	try {
    		final FederateHandle federateHandle = this._rtiAmbassador.joinFederationExecution(federateType, federationExecutionName);
    		this.logger.trace("joinFederationExecution return " + federateHandle.toString());
    		return federateHandle;
    	} catch (CouldNotCreateLogicalTimeFactory | FederationExecutionDoesNotExist | SaveInProgress | RestoreInProgress | FederateAlreadyExecutionMember | NotConnected | CallNotAllowedFromWithinCallback | RTIinternalError e) {
    		this.logger.error("joinFederationExecution exception=" + e.getMessage());
    		throw e;
    	}
    }


    //4.10
    public void resignFederationExecution(final ResignAction resignAction) throws InvalidResignAction, OwnershipAcquisitionPending, FederateOwnsAttributes, FederateNotExecutionMember, NotConnected, CallNotAllowedFromWithinCallback, RTIinternalError {
        this.logger.trace("resignFederationExecution resignAction=" + resignAction.toString());
        try {
        	this._rtiAmbassador.resignFederationExecution(resignAction);
        } catch (InvalidResignAction | OwnershipAcquisitionPending | FederateOwnsAttributes | FederateNotExecutionMember | NotConnected | CallNotAllowedFromWithinCallback | RTIinternalError e) {
        	this.logger.error("resignFederationExecution exception=" + e.getMessage());
        	throw e;
        }
    }


    //4.11
    public void registerFederationSynchronizationPoint(final String synchronizationPointLabel, final byte[] userSuppliedTag) throws SaveInProgress, RestoreInProgress, FederateNotExecutionMember, NotConnected, RTIinternalError {
        this.logger.trace("registerFederationSynchronizationPoint synchronizationPointLabel=" + synchronizationPointLabel + ", userSuppliedTag=" + Arrays.toString(userSuppliedTag));
        try {
        	this._rtiAmbassador.registerFederationSynchronizationPoint(synchronizationPointLabel, userSuppliedTag);
        } catch (SaveInProgress | RestoreInProgress | FederateNotExecutionMember | NotConnected | RTIinternalError e) {
        	this.logger.error("registerFederationSynchronizationPoint exception=" + e.getMessage());
        	throw e;
        }
    }


    //4.11
    public void registerFederationSynchronizationPoint(final String synchronizationPointLabel, final byte[] userSuppliedTag, final FederateHandleSet synchronizationSet) throws InvalidFederateHandle, SaveInProgress, RestoreInProgress, FederateNotExecutionMember, NotConnected, RTIinternalError {
        this.logger.trace("registerFederationSynchronizationPoint synchronizationPointLabel=" + synchronizationPointLabel + ", userSuppliedTag=" + Arrays.toString(userSuppliedTag) + ", synchronizationSet=" + synchronizationSet.toString());
        try {
        	this._rtiAmbassador.registerFederationSynchronizationPoint(synchronizationPointLabel, userSuppliedTag, synchronizationSet);
        } catch (InvalidFederateHandle | SaveInProgress | RestoreInProgress | FederateNotExecutionMember | NotConnected |  RTIinternalError e) {
        	this.logger.error("registerFederationSynchronizationPoint exception=" + e.getMessage());
        	throw e;
        }
    }


    //4.14
    public void synchronizationPointAchieved(final String synchronizationPointLabel) throws SynchronizationPointLabelNotAnnounced, SaveInProgress, RestoreInProgress, FederateNotExecutionMember, NotConnected, RTIinternalError {
        this.logger.trace("synchronizationPointAchieved synchronizationPointLabel=" + synchronizationPointLabel);
        try {
        	this._rtiAmbassador.synchronizationPointAchieved(synchronizationPointLabel);
        } catch (SynchronizationPointLabelNotAnnounced | SaveInProgress | RestoreInProgress | FederateNotExecutionMember | NotConnected | RTIinternalError e) {
        	this.logger.error("synchronizationPointAchieved exception=" + e.getMessage());
        	throw e;
        }
    }


    //4.14
    public void synchronizationPointAchieved(final String synchronizationPointLabel, final boolean successIndicator) throws SynchronizationPointLabelNotAnnounced, SaveInProgress, RestoreInProgress, FederateNotExecutionMember, NotConnected, RTIinternalError {
        this.logger.trace("synchronizationPointAchieved synchronizationPointLabel=" + synchronizationPointLabel + ", successIndicator= " + successIndicator);
        try {
        	this._rtiAmbassador.synchronizationPointAchieved(synchronizationPointLabel, successIndicator);
        } catch (SynchronizationPointLabelNotAnnounced | SaveInProgress | RestoreInProgress | FederateNotExecutionMember | NotConnected | RTIinternalError e) {
        	this.logger.error("synchronizationPointAchieved exception=" + e.getMessage());
        	throw e;
        }
    }


    // 4.16
    public void requestFederationSave(final String label) throws SaveInProgress, RestoreInProgress, FederateNotExecutionMember, NotConnected, RTIinternalError {
        this.logger.trace("requestFederationSave label=" + label);
        try {
        	this._rtiAmbassador.requestFederationSave(label);
        } catch (SaveInProgress | RestoreInProgress | FederateNotExecutionMember | NotConnected | RTIinternalError e) {
        	this.logger.error("requestFederationSave exception=" + e.getMessage());
        	throw e;
        }
    }


    // 4.16
    public void requestFederationSave(final String label, final LogicalTime theTime) throws LogicalTimeAlreadyPassed, InvalidLogicalTime, FederateUnableToUseTime, SaveInProgress, RestoreInProgress, FederateNotExecutionMember, NotConnected, RTIinternalError {
        this.logger.trace("requestFederationSave label=" + label + ", theTime=" + theTime.toString());
        try {
        	this._rtiAmbassador.requestFederationSave(label, theTime);
        } catch (LogicalTimeAlreadyPassed | InvalidLogicalTime | FederateUnableToUseTime | SaveInProgress | RestoreInProgress | FederateNotExecutionMember | NotConnected | RTIinternalError e) {
        	this.logger.error("requestFederationSave exception=" + e.getMessage());
        	throw e;
        }
    }


    // 4.18
    public void federateSaveBegun() throws SaveNotInitiated, RestoreInProgress, FederateNotExecutionMember, NotConnected, RTIinternalError {
        this.logger.trace("federateSaveBegun");
        try {
        	this._rtiAmbassador.federateSaveBegun();
        } catch (SaveNotInitiated | RestoreInProgress | FederateNotExecutionMember | NotConnected | RTIinternalError e) {
        	this.logger.error("federateSaveBegun exception=" + e.getMessage());
        	throw e;
        }
    }


    // 4.19
    public void federateSaveComplete() throws FederateHasNotBegunSave, RestoreInProgress, FederateNotExecutionMember, NotConnected, RTIinternalError {
        this.logger.trace("federateSaveComplete");
        try {
        	this._rtiAmbassador.federateSaveComplete();
        } catch (FederateHasNotBegunSave | RestoreInProgress | FederateNotExecutionMember | NotConnected | RTIinternalError e) {
        	this.logger.error("federateSaveComplete exception=" + e.getMessage());
        	throw e;
        }
    }


    // 4.19
    public void federateSaveNotComplete() throws FederateHasNotBegunSave, RestoreInProgress, FederateNotExecutionMember, NotConnected, RTIinternalError {
        this.logger.trace("federateSaveNotComplete");
        try {
        	this._rtiAmbassador.federateSaveNotComplete();
        } catch (FederateHasNotBegunSave | RestoreInProgress | FederateNotExecutionMember | NotConnected | RTIinternalError e) {
        	this.logger.error("federateSaveNotComplete exception=" + e.getMessage());
        	throw e;
        }
    }


    // 4.21
    public void abortFederationSave() throws SaveNotInProgress, FederateNotExecutionMember, NotConnected, RTIinternalError {
        this.logger.trace("abortFederationSave");
        try {
        	this._rtiAmbassador.abortFederationSave();
        } catch (SaveNotInProgress | FederateNotExecutionMember | NotConnected | RTIinternalError e) {
        	this.logger.error("abortFederationSave exception=" + e.getMessage());
        	throw e;
        }
    }


    // 4.22
    public void queryFederationSaveStatus() throws RestoreInProgress, FederateNotExecutionMember, NotConnected, RTIinternalError {
        this.logger.trace("queryFederationSaveStatus");
        try {
        	this._rtiAmbassador.queryFederationSaveStatus();
        } catch (RestoreInProgress | FederateNotExecutionMember | NotConnected | RTIinternalError e) {
        	this.logger.error("queryFederationSaveStatus exception=" + e.getMessage());
        	throw e;
        }
    }


    // 4.24
    public void requestFederationRestore(final String label) throws SaveInProgress, RestoreInProgress, FederateNotExecutionMember, NotConnected, RTIinternalError {
        this.logger.trace("requestFederationRestore label=" + label);
        try {
        	this._rtiAmbassador.requestFederationRestore(label);
        } catch (SaveInProgress | RestoreInProgress | FederateNotExecutionMember | NotConnected | RTIinternalError e) {
        	this.logger.error("requestFederationRestore exception=" + e.getMessage());
        	throw e;
        }
    }


    // 4.28
    public void federateRestoreComplete() throws RestoreNotRequested, SaveInProgress, FederateNotExecutionMember, NotConnected, RTIinternalError {
        this.logger.trace("federateRestoreComplete");
        try {
        	this._rtiAmbassador.federateRestoreComplete();
        } catch (RestoreNotRequested | SaveInProgress | FederateNotExecutionMember | NotConnected | RTIinternalError e) {
        	this.logger.error("federateRestoreComplete exception=" + e.getMessage());
        	throw e;
        }
    }


    // 4.28
    public void federateRestoreNotComplete() throws RestoreNotRequested, SaveInProgress, FederateNotExecutionMember, NotConnected, RTIinternalError {
        this.logger.trace("federateRestoreNotComplete");
        try {
        	this._rtiAmbassador.federateRestoreNotComplete();
        } catch (RestoreNotRequested | SaveInProgress | FederateNotExecutionMember | NotConnected | RTIinternalError e) {
        	this.logger.error("federateRestoreNotComplete exception=" + e.getMessage());
        	throw e;
        }
    }


    // 4.30
    public void abortFederationRestore() throws RestoreNotInProgress, FederateNotExecutionMember, NotConnected, RTIinternalError {
        this.logger.trace("abortFederationRestore");
        try {
        	this._rtiAmbassador.abortFederationRestore();
        } catch (RestoreNotInProgress | FederateNotExecutionMember | NotConnected | RTIinternalError e) {
        	this.logger.error("abortFederationRestore exception=" + e.getMessage());
        	throw e;
        }
    }


    // 4.31
    public void queryFederationRestoreStatus() throws SaveInProgress, FederateNotExecutionMember, NotConnected, RTIinternalError {
        this.logger.trace("queryFederationRestoreStatus");
        try {
        	this._rtiAmbassador.queryFederationRestoreStatus();
        } catch (SaveInProgress | FederateNotExecutionMember | NotConnected | RTIinternalError e) {
        	this.logger.error("queryFederationRestoreStatus exception=" + e.getMessage());
        	throw e;
        }
    }

    /////////////////////////////////////
    // Declaration Management Services //
    /////////////////////////////////////


    // 5.2
    public void publishObjectClassAttributes(final ObjectClassHandle theClass, final AttributeHandleSet attributeList) throws AttributeNotDefined, ObjectClassNotDefined, SaveInProgress, RestoreInProgress, FederateNotExecutionMember, NotConnected, RTIinternalError {
        this.logger.trace("publishObjectClassAttributes theClass=" + theClass.toString() + ", attributeList=" + attributeList.toString());
        try {
        	this._rtiAmbassador.publishObjectClassAttributes(theClass, attributeList);
        } catch (AttributeNotDefined | ObjectClassNotDefined | SaveInProgress | RestoreInProgress | FederateNotExecutionMember | NotConnected | RTIinternalError e) {
        	this.logger.error("publishObjectClassAttributes exception=" + e.getMessage());
        	throw e;
        }
    }


    // 5.3
    public void unpublishObjectClass(final ObjectClassHandle theClass) throws OwnershipAcquisitionPending, ObjectClassNotDefined, SaveInProgress, RestoreInProgress, FederateNotExecutionMember, NotConnected, RTIinternalError {
        this.logger.trace("unpublishObjectClass theClass=" + theClass.toString());
        try {
        	this._rtiAmbassador.unpublishObjectClass(theClass);
        } catch (OwnershipAcquisitionPending | ObjectClassNotDefined | SaveInProgress | RestoreInProgress | FederateNotExecutionMember | NotConnected | RTIinternalError e) {
        	this.logger.error("unpublishObjectClass exception=" + e.getMessage());
        	throw e;
        }
    }


    // 5.3
    public void unpublishObjectClassAttributes(final ObjectClassHandle theClass, final AttributeHandleSet attributeList) throws OwnershipAcquisitionPending, AttributeNotDefined, ObjectClassNotDefined, SaveInProgress, RestoreInProgress, FederateNotExecutionMember, NotConnected, RTIinternalError {
        this.logger.trace("unpublishObjectClassAttributes theClass=" + theClass.toString() + ", attributeList=" + attributeList.toString());
        try {
        	this._rtiAmbassador.unpublishObjectClassAttributes(theClass, attributeList);
        } catch (OwnershipAcquisitionPending | AttributeNotDefined | ObjectClassNotDefined | SaveInProgress | RestoreInProgress | FederateNotExecutionMember | NotConnected | RTIinternalError e) {
        	this.logger.error("unpublishObjectClassAttributes exception=" + e.getMessage());
        	throw e;
        }
    }


    // 5.4
    public void publishInteractionClass(final InteractionClassHandle theInteraction) throws InteractionClassNotDefined, SaveInProgress, RestoreInProgress, FederateNotExecutionMember, NotConnected, RTIinternalError {
        this.logger.trace("publishInteractionClass theInteraction=" + theInteraction.toString());
        try {
        	this._rtiAmbassador.publishInteractionClass(theInteraction);
        } catch (InteractionClassNotDefined | SaveInProgress | RestoreInProgress | FederateNotExecutionMember | NotConnected | RTIinternalError e) {
        	this.logger.error("publishInteractionClass exception=" + e.getMessage());
        	throw e;
        }
    }


    // 5.5
    public void unpublishInteractionClass(final InteractionClassHandle theInteraction) throws InteractionClassNotDefined, SaveInProgress, RestoreInProgress, FederateNotExecutionMember, NotConnected, RTIinternalError {
        this.logger.trace("unpublishInteractionClass theInteraction=" + theInteraction.toString());
        try {
        	this._rtiAmbassador.unpublishInteractionClass(theInteraction);
        } catch (InteractionClassNotDefined | SaveInProgress | RestoreInProgress | FederateNotExecutionMember | NotConnected | RTIinternalError e) {
        	this.logger.error("unpublishInteractionClass exception=" + e.getMessage());
        	throw e;
        }
    }


    // 5.6
    public void subscribeObjectClassAttributes(final ObjectClassHandle theClass, final AttributeHandleSet attributeList) throws AttributeNotDefined, ObjectClassNotDefined, SaveInProgress, RestoreInProgress, FederateNotExecutionMember, NotConnected, RTIinternalError {
        this.logger.trace("subscribeObjectClassAttributes theClass=" + theClass.toString() + ", attributeList=" + attributeList.toString());
        try {
        	this._rtiAmbassador.subscribeObjectClassAttributes(theClass, attributeList);
        } catch (AttributeNotDefined | ObjectClassNotDefined | SaveInProgress | RestoreInProgress | FederateNotExecutionMember | NotConnected | RTIinternalError e) {
        	this.logger.error("subscribeObjectClassAttributes exception=" + e.getMessage());
        	throw e;
        }
    }


    // 5.6
    public void subscribeObjectClassAttributes(final ObjectClassHandle theClass, final AttributeHandleSet attributeList, final String updateRateDesignator) throws AttributeNotDefined, ObjectClassNotDefined, InvalidUpdateRateDesignator, SaveInProgress, RestoreInProgress, FederateNotExecutionMember, NotConnected, RTIinternalError {
        this.logger.trace("subscribeObjectClassAttributes theClass=" + theClass.toString() + ", attributeList=" + attributeList.toString() + ", updateRateDesignator=" + updateRateDesignator);
        try {
        	this._rtiAmbassador.subscribeObjectClassAttributes(theClass, attributeList, updateRateDesignator);
        } catch (AttributeNotDefined | ObjectClassNotDefined | InvalidUpdateRateDesignator | SaveInProgress | RestoreInProgress | FederateNotExecutionMember | NotConnected | RTIinternalError e) {
        	this.logger.error("subscribeObjectClassAttributes exception=" + e.getMessage());
        	throw e;
        }
    }


    // 5.6
    public void subscribeObjectClassAttributesPassively(final ObjectClassHandle theClass, final AttributeHandleSet attributeList) throws AttributeNotDefined, ObjectClassNotDefined, SaveInProgress, RestoreInProgress, FederateNotExecutionMember, NotConnected, RTIinternalError {
        this.logger.trace("subscribeObjectClassAttributesPassively theClass=" + theClass.toString() + ", attributeList=" + attributeList.toString());
        try {
        	this._rtiAmbassador.subscribeObjectClassAttributesPassively(theClass, attributeList);
        } catch (AttributeNotDefined | ObjectClassNotDefined | SaveInProgress | RestoreInProgress | FederateNotExecutionMember | NotConnected | RTIinternalError e) {
        	this.logger.error("subscribeObjectClassAttributesPassively exception=" + e.getMessage());
        	throw e;
        }
    }


    // 5.6
    public void subscribeObjectClassAttributesPassively(final ObjectClassHandle theClass, final AttributeHandleSet attributeList, final String updateRateDesignator) throws AttributeNotDefined, ObjectClassNotDefined, InvalidUpdateRateDesignator, SaveInProgress, RestoreInProgress, FederateNotExecutionMember, NotConnected, RTIinternalError {
    	this.logger.trace("subscribeObjectClassAttributesPassively theClass=" + theClass.toString() + ", attributeList=" + attributeList.toString() + ", updateRateDesignator=" + updateRateDesignator);
    	try {
    		this._rtiAmbassador.subscribeObjectClassAttributesPassively(theClass, attributeList, updateRateDesignator);
    	} catch (AttributeNotDefined | ObjectClassNotDefined | InvalidUpdateRateDesignator | SaveInProgress | RestoreInProgress | FederateNotExecutionMember | NotConnected | RTIinternalError e) {
    		this.logger.error("subscribeObjectClassAttributesPassively exception=" + e.getMessage());
    		throw e;
    	}
    }


    // 5.7
    public void unsubscribeObjectClass(final ObjectClassHandle theClass) throws ObjectClassNotDefined, SaveInProgress, RestoreInProgress, FederateNotExecutionMember, NotConnected, RTIinternalError {
        this.logger.trace("unsubscribeObjectClass theClass=" + theClass.toString());
        try {
        	this._rtiAmbassador.unsubscribeObjectClass(theClass);
        } catch (ObjectClassNotDefined | SaveInProgress | RestoreInProgress | FederateNotExecutionMember | NotConnected | RTIinternalError e) {
        	this.logger.error("unsubscribeObjectClass exception=" + e.getMessage());
        	throw e;
        }
    }


    // 5.7
    public void unsubscribeObjectClassAttributes(final ObjectClassHandle theClass, final AttributeHandleSet attributeList) throws AttributeNotDefined, ObjectClassNotDefined, SaveInProgress, RestoreInProgress, FederateNotExecutionMember, NotConnected, RTIinternalError {
        this.logger.trace("unsubscribeObjectClassAttributes theClass=" + theClass.toString() + ", attributeList=" + attributeList.toString());
        try {
        	this._rtiAmbassador.unsubscribeObjectClassAttributes(theClass, attributeList);
        } catch (AttributeNotDefined | ObjectClassNotDefined | SaveInProgress | RestoreInProgress | FederateNotExecutionMember | NotConnected | RTIinternalError e) {
        	this.logger.error("unsubscribeObjectClassAttributes exception=" + e.getMessage());
        	throw e;
        }
    }


    // 5.8
    public void subscribeInteractionClass(final InteractionClassHandle theClass) throws FederateServiceInvocationsAreBeingReportedViaMOM, InteractionClassNotDefined, SaveInProgress, RestoreInProgress, FederateNotExecutionMember, NotConnected, RTIinternalError {
    	this.logger.trace("subscribeInteractionClass theClass=" + theClass.toString());
    	try {
    		this._rtiAmbassador.subscribeInteractionClass(theClass);
    	} catch (FederateServiceInvocationsAreBeingReportedViaMOM | InteractionClassNotDefined | SaveInProgress | RestoreInProgress | FederateNotExecutionMember | NotConnected | RTIinternalError e) {
    		this.logger.error("subscribeInteractionClass exception=" + e.getMessage());
    		throw e;
    	}
    }


    // 5.8
    public void subscribeInteractionClassPassively(final InteractionClassHandle theClass) throws FederateServiceInvocationsAreBeingReportedViaMOM, InteractionClassNotDefined, SaveInProgress, RestoreInProgress, FederateNotExecutionMember, NotConnected, RTIinternalError {
    	this.logger.trace("subscribeInteractionClassPassively theClass=" + theClass.toString());
    	try {
    		this._rtiAmbassador.subscribeInteractionClassPassively(theClass);
    	} catch (FederateServiceInvocationsAreBeingReportedViaMOM | InteractionClassNotDefined | SaveInProgress | RestoreInProgress | FederateNotExecutionMember | NotConnected | RTIinternalError e) {
    		this.logger.error("subscribeInteractionClassPassively exception=" + e.getMessage());
    		throw e;
    	}
    }


    // 5.9
    public void unsubscribeInteractionClass(final InteractionClassHandle theClass) throws InteractionClassNotDefined, SaveInProgress, RestoreInProgress, FederateNotExecutionMember, NotConnected, RTIinternalError {
    	this.logger.trace("unsubscribeInteractionClass theClass=" + theClass.toString());
    	try {
    		this._rtiAmbassador.unsubscribeInteractionClass(theClass);
    	} catch (InteractionClassNotDefined | SaveInProgress | RestoreInProgress | FederateNotExecutionMember | NotConnected | RTIinternalError e) {
    		this.logger.error("unsubscribeInteractionClass exception=" + e.getMessage());
    		throw e;
    	}
    }

    ////////////////////////////////
    // Object Management Services //
    ////////////////////////////////


    // 6.2
    public void reserveObjectInstanceName(final String theObjectName) throws IllegalName, SaveInProgress, RestoreInProgress, FederateNotExecutionMember, NotConnected, RTIinternalError {
        this.logger.trace("reserveObjectInstanceName theObjectName=" + theObjectName);
        try {
        	this._rtiAmbassador.reserveObjectInstanceName(theObjectName);
        } catch (IllegalName | SaveInProgress | RestoreInProgress | FederateNotExecutionMember | NotConnected | RTIinternalError e) {
        	this.logger.error("reserveObjectInstanceName exception=" + e.getMessage());
        	throw e;
        }
    }


    // 6.4
    public void releaseObjectInstanceName(final String theObjectInstanceName) throws ObjectInstanceNameNotReserved, SaveInProgress, RestoreInProgress, FederateNotExecutionMember, NotConnected, RTIinternalError {
        this.logger.trace("releaseObjectInstanceName theObjectInstanceName=" + theObjectInstanceName);
        try {
        	this._rtiAmbassador.releaseObjectInstanceName(theObjectInstanceName);
        } catch (ObjectInstanceNameNotReserved | SaveInProgress | RestoreInProgress | FederateNotExecutionMember | NotConnected | RTIinternalError e) {
        	this.logger.error("releaseObjectInstanceName exception=" + e.getMessage());
        	throw e;
        }
    }


    // 6.5
    public void reserveMultipleObjectInstanceName(final Set<String> theObjectNames) throws IllegalName, NameSetWasEmpty, SaveInProgress, RestoreInProgress, FederateNotExecutionMember, NotConnected, RTIinternalError {
        this.logger.trace("reserveMultipleObjectInstanceName theObjectNames=" + theObjectNames.toString());
        try {
        	this._rtiAmbassador.reserveMultipleObjectInstanceName(theObjectNames);
        } catch (IllegalName | NameSetWasEmpty | SaveInProgress | RestoreInProgress | FederateNotExecutionMember | NotConnected | RTIinternalError e) {
        	this.logger.error("reserveMultipleObjectInstanceName exception=" + e.getMessage());
        	throw e;
        }
    }


    // 6.7
    public void releaseMultipleObjectInstanceName(final Set<String> theObjectNames) throws ObjectInstanceNameNotReserved, SaveInProgress, RestoreInProgress, FederateNotExecutionMember, NotConnected, RTIinternalError {
        this.logger.trace("releaseMultipleObjectInstanceName theObjectNames=" + theObjectNames.toString());
        try {
        	this._rtiAmbassador.releaseMultipleObjectInstanceName(theObjectNames);
        } catch (ObjectInstanceNameNotReserved | SaveInProgress | RestoreInProgress | FederateNotExecutionMember | NotConnected | RTIinternalError e) {
        	this.logger.error("releaseMultipleObjectInstanceName exception=" + e.getMessage());
        	throw e;
        }
    }


    // 6.8
    public ObjectInstanceHandle registerObjectInstance(final ObjectClassHandle theClass) throws ObjectClassNotPublished, ObjectClassNotDefined, SaveInProgress, RestoreInProgress, FederateNotExecutionMember, NotConnected, RTIinternalError {
    	this.logger.trace("registerObjectInstance theClass=" + theClass.toString());
    	try {
    		final ObjectInstanceHandle objectInstanceHandle = this._rtiAmbassador.registerObjectInstance(theClass);
    		this.logger.trace("registerObjectInstance return " + objectInstanceHandle.toString());
    		return objectInstanceHandle;
    	} catch (ObjectClassNotPublished | ObjectClassNotDefined | SaveInProgress | RestoreInProgress | FederateNotExecutionMember | NotConnected | RTIinternalError e) {
    		this.logger.error("registerObjectInstance exception=" + e.getMessage());
    		throw e;
    	}
    }


    // 6.8
    public ObjectInstanceHandle registerObjectInstance(final ObjectClassHandle theClass, final String theObjectName) throws ObjectInstanceNameInUse, ObjectInstanceNameNotReserved, ObjectClassNotPublished, ObjectClassNotDefined, SaveInProgress, RestoreInProgress, FederateNotExecutionMember, NotConnected, RTIinternalError {
        this.logger.trace("registerObjectInstance theClass=" + theClass.toString() + ", theObjectName=" + theObjectName);
        try {
        	final ObjectInstanceHandle objectInstanceHandle = this._rtiAmbassador.registerObjectInstance(theClass, theObjectName);
        	this.logger.trace("registerObjectInstance return " + objectInstanceHandle.toString());
        	return objectInstanceHandle;
        } catch (ObjectInstanceNameInUse | ObjectInstanceNameNotReserved | ObjectClassNotPublished | ObjectClassNotDefined | SaveInProgress | RestoreInProgress | FederateNotExecutionMember | NotConnected | RTIinternalError e) {
        	this.logger.error("registerObjectInstance exception=" + e.getMessage());
        	throw e;
        }
    }


    // 6.10
    public void updateAttributeValues(final ObjectInstanceHandle theObject, final AttributeHandleValueMap theAttributes, final byte[] userSuppliedTag) throws AttributeNotOwned, AttributeNotDefined, ObjectInstanceNotKnown, SaveInProgress, RestoreInProgress, FederateNotExecutionMember, NotConnected, RTIinternalError {
        this.logger.trace("updateAttributeValues theObject=" + theObject.toString() + ", theAttributes=" + theAttributes.toString() + ", userSuppliedTag=" + Arrays.toString(userSuppliedTag));
        try {
        	this._rtiAmbassador.updateAttributeValues(theObject, theAttributes, userSuppliedTag);
        } catch (AttributeNotOwned | AttributeNotDefined | ObjectInstanceNotKnown | SaveInProgress | RestoreInProgress | FederateNotExecutionMember | NotConnected | RTIinternalError e) {
        	this.logger.error("updateAttributeValues exception=" + e.getMessage());
        	throw e;
        }
    }


    // 6.10
    public MessageRetractionReturn updateAttributeValues(final ObjectInstanceHandle theObject, final AttributeHandleValueMap theAttributes, final byte[] userSuppliedTag, final LogicalTime theTime) throws InvalidLogicalTime, AttributeNotOwned, AttributeNotDefined, ObjectInstanceNotKnown, SaveInProgress, RestoreInProgress, FederateNotExecutionMember, NotConnected, RTIinternalError {
        this.logger.trace("updateAttributeValues theObject=" + theObject.toString() + ", theAttributes=" + theAttributes.toString() + ", userSuppliedTag=" + Arrays.toString(userSuppliedTag) + ", theTime=" + theTime.toString());
        try {
        	final MessageRetractionReturn messageRetractionReturn = this._rtiAmbassador.updateAttributeValues(theObject, theAttributes, userSuppliedTag, theTime);
        	this.logger.trace("updateAttributeValues return " + messageRetractionReturn.toString());
        	return messageRetractionReturn;
        } catch (InvalidLogicalTime | AttributeNotOwned | AttributeNotDefined | ObjectInstanceNotKnown | SaveInProgress | RestoreInProgress | FederateNotExecutionMember | NotConnected | RTIinternalError e) {
        	this.logger.error("updateAttributeValues exception=" + e.getMessage());
        	throw e;
        }
    }


    // 6.12
    public void sendInteraction(final InteractionClassHandle theInteraction, final ParameterHandleValueMap theParameters, final byte[] userSuppliedTag) throws InteractionClassNotPublished, InteractionParameterNotDefined, InteractionClassNotDefined, SaveInProgress, RestoreInProgress, FederateNotExecutionMember, NotConnected, RTIinternalError {
        this.logger.trace("sendInteraction theInteraction=" + theInteraction.toString() + ", theParameters=" + theParameters.toString() + ", userSuppliedTag=" + Arrays.toString(userSuppliedTag));
        try {
        	this._rtiAmbassador.sendInteraction(theInteraction, theParameters, userSuppliedTag);
        } catch (InteractionClassNotPublished | InteractionParameterNotDefined | InteractionClassNotDefined | SaveInProgress | RestoreInProgress | FederateNotExecutionMember | NotConnected | RTIinternalError e) {
        	this.logger.error("sendInteraction exception=" + e.getMessage());
        	throw e;
        }
    }


    // 6.12
    public MessageRetractionReturn sendInteraction(final InteractionClassHandle theInteraction, final ParameterHandleValueMap theParameters, final byte[] userSuppliedTag, final LogicalTime theTime) throws InvalidLogicalTime, InteractionClassNotPublished, InteractionParameterNotDefined, InteractionClassNotDefined, SaveInProgress, RestoreInProgress, FederateNotExecutionMember, NotConnected, RTIinternalError {
        this.logger.trace("sendInteraction theInteraction=" + theInteraction.toString() + ", theParameters=" + theParameters.toString() + ", userSuppliedTag=" + Arrays.toString(userSuppliedTag) + ", theTime=" + theTime.toString());
        try {
        	final MessageRetractionReturn messageRetractionReturn = this._rtiAmbassador.sendInteraction(theInteraction, theParameters, userSuppliedTag, theTime);
        	this.logger.trace("sendInteraction return " + messageRetractionReturn.toString());
        	return messageRetractionReturn;
        } catch (InvalidLogicalTime | InteractionClassNotPublished | InteractionParameterNotDefined | InteractionClassNotDefined | SaveInProgress | RestoreInProgress | FederateNotExecutionMember | NotConnected | RTIinternalError e) {
        	this.logger.error("sendInteraction exception=" + e.getMessage());
        	throw e;
        }
    }


    // 6.14
    public void deleteObjectInstance(final ObjectInstanceHandle objectHandle, final byte[] userSuppliedTag) throws DeletePrivilegeNotHeld, ObjectInstanceNotKnown, SaveInProgress, RestoreInProgress, FederateNotExecutionMember, NotConnected, RTIinternalError {
        this.logger.trace("deleteObjectInstance objectHandle=" + objectHandle.toString() + ", userSuppliedTag=" + Arrays.toString(userSuppliedTag));
        try {
        	this._rtiAmbassador.deleteObjectInstance(objectHandle, userSuppliedTag);
        } catch (DeletePrivilegeNotHeld | ObjectInstanceNotKnown | SaveInProgress | RestoreInProgress | FederateNotExecutionMember | NotConnected | RTIinternalError e) {
        	this.logger.error("deleteObjectInstance exception=" + e.getMessage());
        	throw e;
        }
    }


    // 6.14
    public MessageRetractionReturn deleteObjectInstance(final ObjectInstanceHandle objectHandle, final byte[] userSuppliedTag, final LogicalTime theTime) throws InvalidLogicalTime, DeletePrivilegeNotHeld, ObjectInstanceNotKnown, SaveInProgress, RestoreInProgress, FederateNotExecutionMember, NotConnected, RTIinternalError {
        this.logger.trace("deleteObjectInstance objectHandle=" + objectHandle.toString() + ", userSuppliedTag=" + Arrays.toString(userSuppliedTag) + ", theTime=" + theTime.toString());
        try {
        	final MessageRetractionReturn messageRetractionReturn = this._rtiAmbassador.deleteObjectInstance(objectHandle, userSuppliedTag, theTime);
        	this.logger.trace("deleteObjectInstance return " + messageRetractionReturn.toString());
        	return messageRetractionReturn;
        } catch (InvalidLogicalTime | DeletePrivilegeNotHeld | ObjectInstanceNotKnown | SaveInProgress | RestoreInProgress | FederateNotExecutionMember | NotConnected | RTIinternalError e) {
        	this.logger.error("deleteObjectInstance exception=" + e.getMessage());
        	throw e;
        }
    }


    // 6.16
    public void localDeleteObjectInstance(final ObjectInstanceHandle objectHandle) throws OwnershipAcquisitionPending, FederateOwnsAttributes, ObjectInstanceNotKnown, SaveInProgress, RestoreInProgress, FederateNotExecutionMember, NotConnected, RTIinternalError {
        this.logger.trace("localDeleteObjectInstance objectHandle=" + objectHandle.toString());
        try {
        	this._rtiAmbassador.localDeleteObjectInstance(objectHandle);
        } catch (OwnershipAcquisitionPending | FederateOwnsAttributes | ObjectInstanceNotKnown | SaveInProgress | RestoreInProgress | FederateNotExecutionMember | NotConnected | RTIinternalError e) {
        	this.logger.error("localDeleteObjectInstance exception=" + e.getMessage());
        	throw e;
        }
    }


    // 6.19
    public void requestAttributeValueUpdate(final ObjectInstanceHandle theObject, final AttributeHandleSet theAttributes, final byte[] userSuppliedTag) throws AttributeNotDefined, ObjectInstanceNotKnown, SaveInProgress, RestoreInProgress, FederateNotExecutionMember, NotConnected, RTIinternalError {
        this.logger.trace("requestAttributeValueUpdate theObject=" + theObject.toString() + ", theAttributes=" + theAttributes.toString() + ", userSuppliedTag=" + Arrays.toString(userSuppliedTag));
        try {
        	this._rtiAmbassador.requestAttributeValueUpdate(theObject, theAttributes, userSuppliedTag);
        } catch (AttributeNotDefined | ObjectInstanceNotKnown | SaveInProgress | RestoreInProgress | FederateNotExecutionMember | NotConnected | RTIinternalError e) {
        	this.logger.error("requestAttributeValueUpdate exception=" + e.getMessage());
        	throw e;
        }
    }


    // 6.19
    public void requestAttributeValueUpdate(final ObjectClassHandle theClass, final AttributeHandleSet theAttributes, final byte[] userSuppliedTag) throws AttributeNotDefined, ObjectClassNotDefined, SaveInProgress, RestoreInProgress, FederateNotExecutionMember, NotConnected, RTIinternalError {
        this.logger.trace("requestAttributeValueUpdate theClass=" + theClass.toString() + ", theAttributes=" + theAttributes.toString() + ", userSuppliedTag=" + Arrays.toString(userSuppliedTag));
        try {
        	this._rtiAmbassador.requestAttributeValueUpdate(theClass, theAttributes, userSuppliedTag);
        } catch (AttributeNotDefined | ObjectClassNotDefined | SaveInProgress | RestoreInProgress | FederateNotExecutionMember | NotConnected | RTIinternalError e) {
        	this.logger.error("requestAttributeValueUpdate exception=" + e.getMessage());
        	throw e;
        }
    }


    // 6.23
    public void requestAttributeTransportationTypeChange(final ObjectInstanceHandle theObject, final AttributeHandleSet theAttributes, final TransportationTypeHandle theType) throws AttributeAlreadyBeingChanged, AttributeNotOwned, AttributeNotDefined, ObjectInstanceNotKnown, InvalidTransportationType, SaveInProgress, RestoreInProgress, FederateNotExecutionMember, NotConnected, RTIinternalError {
        this.logger.trace("requestAttributeTransportationTypeChange theObject=" + theObject.toString() + ", theAttributes=" + theAttributes.toString() + ", theType=" + theType.toString());
        try {
        	this._rtiAmbassador.requestAttributeTransportationTypeChange(theObject, theAttributes, theType);
        } catch (AttributeAlreadyBeingChanged | AttributeNotOwned | AttributeNotDefined | ObjectInstanceNotKnown | InvalidTransportationType | SaveInProgress | RestoreInProgress | FederateNotExecutionMember | NotConnected | RTIinternalError e) {
        	this.logger.error("requestAttributeTransportationTypeChange exception=" + e.getMessage());
        	throw e;
        }
    }


    // 6.25
    public void queryAttributeTransportationType(final ObjectInstanceHandle theObject, final AttributeHandle theAttribute) throws AttributeNotDefined, ObjectInstanceNotKnown, SaveInProgress, RestoreInProgress, FederateNotExecutionMember, NotConnected, RTIinternalError {
        this.logger.trace("queryAttributeTransportationType theObject=" + theObject.toString() + ", theAttribute=" + theAttribute.toString());
        try {
        	this._rtiAmbassador.queryAttributeTransportationType(theObject, theAttribute);
        } catch (AttributeNotDefined | ObjectInstanceNotKnown | SaveInProgress | RestoreInProgress | FederateNotExecutionMember | NotConnected | RTIinternalError e) {
        	this.logger.error("queryAttributeTransportationType exception=" + e.getMessage());
        	throw e;
        }
    }


    // 6.27
    public void requestInteractionTransportationTypeChange(final InteractionClassHandle theClass, final TransportationTypeHandle theType) throws InteractionClassAlreadyBeingChanged, InteractionClassNotPublished, InteractionClassNotDefined, InvalidTransportationType, SaveInProgress, RestoreInProgress, FederateNotExecutionMember, NotConnected, RTIinternalError {
        this.logger.trace("requestInteractionTransportationTypeChange theClass=" + theClass.toString() + ", theType=" + theType.toString());
        try {
        	this._rtiAmbassador.requestInteractionTransportationTypeChange(theClass, theType);
        } catch (InteractionClassAlreadyBeingChanged | InteractionClassNotPublished | InteractionClassNotDefined | InvalidTransportationType | SaveInProgress | RestoreInProgress | FederateNotExecutionMember | NotConnected | RTIinternalError e) {
        	this.logger.error("requestInteractionTransportationTypeChange exception=" + e.getMessage());
        	throw e;
        }
    }


    // 6.29
    public void queryInteractionTransportationType(final FederateHandle theFederate, final InteractionClassHandle theInteraction) throws InteractionClassNotDefined, SaveInProgress, RestoreInProgress, FederateNotExecutionMember, NotConnected, RTIinternalError {
        this.logger.trace("queryInteractionTransportationType theFederate=" + theFederate.toString() + ", theInteraction=" + theInteraction.toString());
        try {
        	this._rtiAmbassador.queryInteractionTransportationType(theFederate, theInteraction);
        } catch (InteractionClassNotDefined | SaveInProgress | RestoreInProgress | FederateNotExecutionMember | NotConnected | RTIinternalError e) {
        	this.logger.error("queryInteractionTransportationType exception=" + e.getMessage());
        	throw e;
        }
    }

    ///////////////////////////////////
    // Ownership Management Services //
    ///////////////////////////////////


    // 7.2
    public void unconditionalAttributeOwnershipDivestiture(final ObjectInstanceHandle theObject, final AttributeHandleSet theAttributes) throws AttributeNotOwned, AttributeNotDefined, ObjectInstanceNotKnown, SaveInProgress, RestoreInProgress, FederateNotExecutionMember, NotConnected, RTIinternalError {
        this.logger.trace("unconditionalAttributeOwnershipDivestiture theObject=" + theObject.toString() + ", theAttributes=" + theAttributes.toString());
        try {
        	this._rtiAmbassador.unconditionalAttributeOwnershipDivestiture(theObject, theAttributes);
        } catch (AttributeNotOwned | AttributeNotDefined | ObjectInstanceNotKnown | SaveInProgress | RestoreInProgress | FederateNotExecutionMember | NotConnected | RTIinternalError e) {
        	this.logger.error("unconditionalAttributeOwnershipDivestiture exception=" + e.getMessage());
        	throw e;
        }
    }


    // 7.3
    public void negotiatedAttributeOwnershipDivestiture(final ObjectInstanceHandle theObject, final AttributeHandleSet theAttributes, final byte[] userSuppliedTag) throws AttributeAlreadyBeingDivested, AttributeNotOwned, AttributeNotDefined, ObjectInstanceNotKnown, SaveInProgress, RestoreInProgress, FederateNotExecutionMember, NotConnected, RTIinternalError {
        this.logger.trace("negotiatedAttributeOwnershipDivestiture theObject=" + theObject.toString() + ", theAttributes=" + theAttributes.toString() + ", userSuppliedTag=" + Arrays.toString(userSuppliedTag));
        try {
        	this._rtiAmbassador.negotiatedAttributeOwnershipDivestiture(theObject, theAttributes, userSuppliedTag);
        } catch (AttributeAlreadyBeingDivested | AttributeNotOwned | AttributeNotDefined | ObjectInstanceNotKnown | SaveInProgress | RestoreInProgress | FederateNotExecutionMember | NotConnected | RTIinternalError e) {
        	this.logger.error("negotiatedAttributeOwnershipDivestiture exception=" + e.getMessage());
        	throw e;
        }
    }


    // 7.6
    public void confirmDivestiture(final ObjectInstanceHandle theObject, final AttributeHandleSet theAttributes, final byte[] userSuppliedTag) throws NoAcquisitionPending, AttributeDivestitureWasNotRequested, AttributeNotOwned, AttributeNotDefined, ObjectInstanceNotKnown, SaveInProgress, RestoreInProgress, FederateNotExecutionMember, NotConnected, RTIinternalError {
        this.logger.trace("confirmDivestiture theObject=" + theObject.toString() + ", theAttributes=" + theAttributes.toString() + ", userSuppliedTag=" + Arrays.toString(userSuppliedTag));
        try {
        	this._rtiAmbassador.confirmDivestiture(theObject, theAttributes, userSuppliedTag);
        } catch (NoAcquisitionPending | AttributeDivestitureWasNotRequested | AttributeNotOwned | AttributeNotDefined | ObjectInstanceNotKnown | SaveInProgress | RestoreInProgress | FederateNotExecutionMember | NotConnected | RTIinternalError e) {
        	this.logger.error("confirmDivestiture exception=" + e.getMessage());
        	throw e;
        }
    }


    // 7.8
    public void attributeOwnershipAcquisition(final ObjectInstanceHandle theObject, final AttributeHandleSet desiredAttributes, final byte[] userSuppliedTag) throws AttributeNotPublished, ObjectClassNotPublished, FederateOwnsAttributes, AttributeNotDefined, ObjectInstanceNotKnown, SaveInProgress, RestoreInProgress, FederateNotExecutionMember, NotConnected, RTIinternalError {
        this.logger.trace("attributeOwnershipAcquisition theObject=" + theObject.toString() + ", desiredAttributes=" + desiredAttributes.toString() + ", userSuppliedTag=" + Arrays.toString(userSuppliedTag));
        try {
        	this._rtiAmbassador.attributeOwnershipAcquisition(theObject, desiredAttributes, userSuppliedTag);
        } catch (AttributeNotPublished | ObjectClassNotPublished | FederateOwnsAttributes | AttributeNotDefined | ObjectInstanceNotKnown | SaveInProgress | RestoreInProgress | FederateNotExecutionMember | NotConnected | RTIinternalError e) {
        	this.logger.error("attributeOwnershipAcquisition exception=" + e.getMessage());
        	throw e;
        }
    }


    // 7.9
    public void attributeOwnershipAcquisitionIfAvailable(final ObjectInstanceHandle theObject, final AttributeHandleSet desiredAttributes) throws AttributeAlreadyBeingAcquired, AttributeNotPublished, ObjectClassNotPublished, FederateOwnsAttributes, AttributeNotDefined, ObjectInstanceNotKnown, SaveInProgress, RestoreInProgress, FederateNotExecutionMember, NotConnected, RTIinternalError {
        this.logger.trace("attributeOwnershipAcquisitionIfAvailable theObject=" + theObject.toString() + ", desiredAttributes=" + desiredAttributes.toString());
        try {
        	this._rtiAmbassador.attributeOwnershipAcquisitionIfAvailable(theObject, desiredAttributes);
        } catch (AttributeAlreadyBeingAcquired | AttributeNotPublished | ObjectClassNotPublished | FederateOwnsAttributes | AttributeNotDefined | ObjectInstanceNotKnown | SaveInProgress | RestoreInProgress | FederateNotExecutionMember | NotConnected | RTIinternalError e) {
        	this.logger.error("attributeOwnershipAcquisitionIfAvailable exception=" + e.getMessage());
        	throw e;
        }
    }


    // 7.12
    public void attributeOwnershipReleaseDenied(final ObjectInstanceHandle theObject, final AttributeHandleSet theAttributes) throws AttributeNotOwned, AttributeNotDefined, ObjectInstanceNotKnown, SaveInProgress, RestoreInProgress, FederateNotExecutionMember, NotConnected, RTIinternalError {
        this.logger.trace("attributeOwnershipReleaseDenied theObject=" + theObject.toString() + ", theAttributes=" + theAttributes.toString());
        try {
        	this._rtiAmbassador.attributeOwnershipReleaseDenied(theObject, theAttributes);
        } catch (AttributeNotOwned | AttributeNotDefined | ObjectInstanceNotKnown | SaveInProgress | RestoreInProgress | FederateNotExecutionMember | NotConnected | RTIinternalError e) {
        	this.logger.error("attributeOwnershipReleaseDenied exception=" + e.getMessage());
        	throw e;
        }
    }


    // 7.13
    public AttributeHandleSet attributeOwnershipDivestitureIfWanted(final ObjectInstanceHandle theObject, final AttributeHandleSet theAttributes) throws AttributeNotOwned, AttributeNotDefined, ObjectInstanceNotKnown, SaveInProgress, RestoreInProgress, FederateNotExecutionMember, NotConnected, RTIinternalError {
        this.logger.trace("attributeOwnershipDivestitureIfWanted theObject=" + theObject.toString() + ", theAttributes=" + theAttributes.toString());
        try {
        	final AttributeHandleSet attributeHandleSet = this._rtiAmbassador.attributeOwnershipDivestitureIfWanted(theObject, theAttributes);
        	this.logger.trace("attributeOwnershipDivestitureIfWanted return " + attributeHandleSet.toString());
        	return attributeHandleSet;
        } catch (AttributeNotOwned | AttributeNotDefined | ObjectInstanceNotKnown | SaveInProgress | RestoreInProgress | FederateNotExecutionMember | NotConnected | RTIinternalError e) {
        	this.logger.error("attributeOwnershipDivestitureIfWanted exception=" + e.getMessage());
        	throw e;
        }
    }


    // 7.14
    public void cancelNegotiatedAttributeOwnershipDivestiture(final ObjectInstanceHandle theObject, final AttributeHandleSet theAttributes) throws AttributeDivestitureWasNotRequested, AttributeNotOwned, AttributeNotDefined, ObjectInstanceNotKnown, SaveInProgress, RestoreInProgress, FederateNotExecutionMember, NotConnected, RTIinternalError {
        this.logger.trace("cancelNegotiatedAttributeOwnershipDivestiture theObject=" + theObject.toString() + ", theAttributes=" + theAttributes.toString());
        try {
        	this._rtiAmbassador.cancelNegotiatedAttributeOwnershipDivestiture(theObject, theAttributes);
        } catch (AttributeDivestitureWasNotRequested | AttributeNotOwned | AttributeNotDefined | ObjectInstanceNotKnown | SaveInProgress | RestoreInProgress | FederateNotExecutionMember | NotConnected | RTIinternalError e) {
        	this.logger.error("cancelNegotiatedAttributeOwnershipDivestiture exception=" + e.getMessage());
        	throw e;
        }
    }


    // 7.15
    public void cancelAttributeOwnershipAcquisition(final ObjectInstanceHandle theObject, final AttributeHandleSet theAttributes) throws AttributeAcquisitionWasNotRequested, AttributeAlreadyOwned, AttributeNotDefined, ObjectInstanceNotKnown, SaveInProgress, RestoreInProgress, FederateNotExecutionMember, NotConnected, RTIinternalError {
        this.logger.trace("cancelAttributeOwnershipAcquisition theObject=" + theObject.toString() + ", theAttributes=" + theAttributes.toString());
        try {
        	this._rtiAmbassador.cancelAttributeOwnershipAcquisition(theObject, theAttributes);
        } catch (AttributeAcquisitionWasNotRequested | AttributeAlreadyOwned | AttributeNotDefined | ObjectInstanceNotKnown | SaveInProgress | RestoreInProgress | FederateNotExecutionMember | NotConnected | RTIinternalError e) {
        	this.logger.error("cancelAttributeOwnershipAcquisition exception=" + e.getMessage());
        	throw e;
        }
    }


    // 7.17
    public void queryAttributeOwnership(final ObjectInstanceHandle theObject, final AttributeHandle theAttribute) throws AttributeNotDefined, ObjectInstanceNotKnown, SaveInProgress, RestoreInProgress, FederateNotExecutionMember, NotConnected, RTIinternalError {
        this.logger.trace("queryAttributeOwnership theObject=" + theObject.toString() + ", theAttribute=" + theAttribute.toString());
        try {
        	this._rtiAmbassador.queryAttributeOwnership(theObject, theAttribute);
        } catch (AttributeNotDefined | ObjectInstanceNotKnown | SaveInProgress | RestoreInProgress | FederateNotExecutionMember | NotConnected | RTIinternalError e) {
        	this.logger.error("queryAttributeOwnership exception=" + e.getMessage());
        	throw e;
        }
    }


    // 7.19
    public boolean isAttributeOwnedByFederate(final ObjectInstanceHandle theObject, final AttributeHandle theAttribute) throws AttributeNotDefined, ObjectInstanceNotKnown, SaveInProgress, RestoreInProgress, FederateNotExecutionMember, NotConnected, RTIinternalError {
        this.logger.trace("isAttributeOwnedByFederate theObject=" + theObject.toString() + ", theAttribute=" + theAttribute.toString());
        try {
        	final boolean bool = this._rtiAmbassador.isAttributeOwnedByFederate(theObject, theAttribute);
        	this.logger.trace("isAttributeOwnedByFederate return " + bool);
        	return bool;
        } catch (AttributeNotDefined | ObjectInstanceNotKnown | SaveInProgress | RestoreInProgress | FederateNotExecutionMember | NotConnected | RTIinternalError e) {
        	this.logger.error("isAttributeOwnedByFederate exception=" + e.getMessage());
        	throw e;
        }
    }

    //////////////////////////////
    // Time Management Services //
    //////////////////////////////


    // 8.2
    public void enableTimeRegulation(final LogicalTimeInterval theLookahead) throws InvalidLookahead, InTimeAdvancingState, RequestForTimeRegulationPending, TimeRegulationAlreadyEnabled, SaveInProgress, RestoreInProgress, FederateNotExecutionMember, NotConnected, RTIinternalError {
        this.logger.trace("enableTimeRegulation theLookahead=" + theLookahead.toString());
        try {
        	this._rtiAmbassador.enableTimeRegulation(theLookahead);
        } catch (InvalidLookahead | InTimeAdvancingState | RequestForTimeRegulationPending | TimeRegulationAlreadyEnabled | SaveInProgress | RestoreInProgress | FederateNotExecutionMember | NotConnected | RTIinternalError e) {
        	this.logger.error("enableTimeRegulation exception=" + e.getMessage());
        	throw e;
        }
    }


    // 8.4
    public void disableTimeRegulation() throws TimeRegulationIsNotEnabled, SaveInProgress, RestoreInProgress, FederateNotExecutionMember, NotConnected, RTIinternalError {
        this.logger.trace("disableTimeRegulation");
        try {
        	this._rtiAmbassador.disableTimeRegulation();
        } catch (TimeRegulationIsNotEnabled | SaveInProgress | RestoreInProgress | FederateNotExecutionMember | NotConnected | RTIinternalError e) {
        	this.logger.error("disableTimeRegulation exception=" + e.getMessage());
        	throw e;
        }
    }


    // 8.5
    public void enableTimeConstrained() throws InTimeAdvancingState, RequestForTimeConstrainedPending, TimeConstrainedAlreadyEnabled, SaveInProgress, RestoreInProgress, FederateNotExecutionMember, NotConnected, RTIinternalError {
        this.logger.trace("enableTimeConstrained");
        try {
        	this._rtiAmbassador.enableTimeConstrained();
        } catch (InTimeAdvancingState | RequestForTimeConstrainedPending | TimeConstrainedAlreadyEnabled | SaveInProgress | RestoreInProgress | FederateNotExecutionMember | NotConnected | RTIinternalError e) {
        	this.logger.error("enableTimeConstrained exception=" + e.getMessage());
        	throw e;
        }
    }


    // 8.7
    public void disableTimeConstrained() throws TimeConstrainedIsNotEnabled, SaveInProgress, RestoreInProgress, FederateNotExecutionMember, NotConnected, RTIinternalError {
        this.logger.trace("disableTimeConstrained");
        try {
        	this._rtiAmbassador.disableTimeConstrained();
        } catch (TimeConstrainedIsNotEnabled | SaveInProgress | RestoreInProgress | FederateNotExecutionMember | NotConnected | RTIinternalError e) {
        	this.logger.error("disableTimeConstrained exception=" + e.getMessage());
        	throw e;
        }
    }


    // 8.8
    public void timeAdvanceRequest(final LogicalTime theTime) throws LogicalTimeAlreadyPassed, InvalidLogicalTime, InTimeAdvancingState, RequestForTimeRegulationPending, RequestForTimeConstrainedPending, SaveInProgress, RestoreInProgress, FederateNotExecutionMember, NotConnected, RTIinternalError {
        this.logger.trace("timeAdvanceRequest theTime=" + theTime.toString());
        try {
        	this._rtiAmbassador.timeAdvanceRequest(theTime);
        } catch (LogicalTimeAlreadyPassed | InvalidLogicalTime | InTimeAdvancingState | RequestForTimeRegulationPending | RequestForTimeConstrainedPending | SaveInProgress | RestoreInProgress | FederateNotExecutionMember | NotConnected | RTIinternalError e) {
        	this.logger.error("timeAdvanceRequest exception=" + e.getMessage());
        	throw e;
        }
    }


    // 8.9
    public void timeAdvanceRequestAvailable(final LogicalTime theTime) throws LogicalTimeAlreadyPassed, InvalidLogicalTime, InTimeAdvancingState, RequestForTimeRegulationPending, RequestForTimeConstrainedPending, SaveInProgress, RestoreInProgress, FederateNotExecutionMember, NotConnected, RTIinternalError {
        this.logger.trace("timeAdvanceRequestAvailable theTime=" + theTime.toString());
        try {
        	this._rtiAmbassador.timeAdvanceRequestAvailable(theTime);
        } catch (LogicalTimeAlreadyPassed | InvalidLogicalTime | InTimeAdvancingState | RequestForTimeRegulationPending | RequestForTimeConstrainedPending | SaveInProgress | RestoreInProgress | FederateNotExecutionMember | NotConnected | RTIinternalError e) {
        	this.logger.error("timeAdvanceRequestAvailable exception=" + e.getMessage());
        	throw e;
        }
    }


    // 8.10
    public void nextMessageRequest(final LogicalTime theTime) throws LogicalTimeAlreadyPassed, InvalidLogicalTime, InTimeAdvancingState, RequestForTimeRegulationPending, RequestForTimeConstrainedPending, SaveInProgress, RestoreInProgress, FederateNotExecutionMember, NotConnected, RTIinternalError {
        this.logger.trace("nextMessageRequest theTime=" + theTime.toString());
        try {
        	this._rtiAmbassador.nextMessageRequest(theTime);
        } catch (LogicalTimeAlreadyPassed | InvalidLogicalTime | InTimeAdvancingState | RequestForTimeRegulationPending | RequestForTimeConstrainedPending | SaveInProgress | RestoreInProgress | FederateNotExecutionMember | NotConnected | RTIinternalError e) {
        	this.logger.error("nextMessageRequest exception=" + e.getMessage());
        	throw e;
        }
    }


    // 8.11
    public void nextMessageRequestAvailable(final LogicalTime theTime) throws LogicalTimeAlreadyPassed, InvalidLogicalTime, InTimeAdvancingState, RequestForTimeRegulationPending, RequestForTimeConstrainedPending, SaveInProgress, RestoreInProgress, FederateNotExecutionMember, NotConnected, RTIinternalError {
        this.logger.trace("nextMessageRequestAvailable theTime=" + theTime.toString());
        try {
        	this._rtiAmbassador.nextMessageRequestAvailable(theTime);
        } catch (LogicalTimeAlreadyPassed | InvalidLogicalTime | InTimeAdvancingState | RequestForTimeRegulationPending | RequestForTimeConstrainedPending | SaveInProgress | RestoreInProgress | FederateNotExecutionMember | NotConnected | RTIinternalError e) {
        	this.logger.error("nextMessageRequestAvailable exception=" + e.getMessage());
        	throw e;
        }
    }


    // 8.12
    public void flushQueueRequest(final LogicalTime theTime) throws LogicalTimeAlreadyPassed, InvalidLogicalTime, InTimeAdvancingState, RequestForTimeRegulationPending, RequestForTimeConstrainedPending, SaveInProgress, RestoreInProgress, FederateNotExecutionMember, NotConnected, RTIinternalError {
        this.logger.trace("flushQueueRequest theTime=" + theTime.toString());
        try {
        	this._rtiAmbassador.flushQueueRequest(theTime);
        } catch (LogicalTimeAlreadyPassed | InvalidLogicalTime | InTimeAdvancingState | RequestForTimeRegulationPending | RequestForTimeConstrainedPending | SaveInProgress | RestoreInProgress | FederateNotExecutionMember | NotConnected | RTIinternalError e) {
        	this.logger.error("flushQueueRequest exception=" + e.getMessage());
        	throw e;
        }
    }


    // 8.14
    public void enableAsynchronousDelivery() throws AsynchronousDeliveryAlreadyEnabled, SaveInProgress, RestoreInProgress, FederateNotExecutionMember, NotConnected, RTIinternalError {
        this.logger.trace("enableAsynchronousDelivery");
        try {
        	this._rtiAmbassador.enableAsynchronousDelivery();
        } catch (AsynchronousDeliveryAlreadyEnabled | SaveInProgress | RestoreInProgress | FederateNotExecutionMember | NotConnected | RTIinternalError e) {
        	this.logger.error("enableAsynchronousDelivery exception=" + e.getMessage());
        	throw e;
        }
    }


    // 8.15
    public void disableAsynchronousDelivery() throws AsynchronousDeliveryAlreadyDisabled, SaveInProgress, RestoreInProgress, FederateNotExecutionMember, NotConnected, RTIinternalError {
        this.logger.trace("disableAsynchronousDelivery");
        try {
        	this._rtiAmbassador.disableAsynchronousDelivery();
        } catch (AsynchronousDeliveryAlreadyDisabled | SaveInProgress | RestoreInProgress | FederateNotExecutionMember | NotConnected | RTIinternalError e) {
        	this.logger.error("disableAsynchronousDelivery exception=" + e.getMessage());
        	throw e;
        }
    }


    // 8.16
    public TimeQueryReturn queryGALT() throws SaveInProgress, RestoreInProgress, FederateNotExecutionMember, NotConnected, RTIinternalError {
        this.logger.trace("queryGALT");
        try {
        	final TimeQueryReturn timeQueryReturn = this._rtiAmbassador.queryGALT();
        	this.logger.trace("queryGALT return " + timeQueryReturn.toString());
        	return timeQueryReturn;
        } catch (SaveInProgress | RestoreInProgress | FederateNotExecutionMember | NotConnected | RTIinternalError e) {
        	this.logger.error("queryGALT exception=" + e.getMessage());
        	throw e;
        }
    }


    // 8.17
    public LogicalTime queryLogicalTime() throws SaveInProgress, RestoreInProgress, FederateNotExecutionMember, NotConnected, RTIinternalError {
        this.logger.trace("queryLogicalTime");
        try {
        	final LogicalTime logicalTime = this._rtiAmbassador.queryLogicalTime();
        	this.logger.trace("queryLogicalTime return " + logicalTime.toString());
        	return logicalTime;
        } catch (SaveInProgress | RestoreInProgress | FederateNotExecutionMember | NotConnected | RTIinternalError e) {
        	this.logger.error("queryLogicalTime exception=" + e.getMessage());
        	throw e;
        }
    }


    // 8.18
    public TimeQueryReturn queryLITS() throws SaveInProgress, RestoreInProgress, FederateNotExecutionMember, NotConnected, RTIinternalError {
        this.logger.trace("queryLITS");
        try {
        	final TimeQueryReturn timeQueryReturn = this._rtiAmbassador.queryLITS();
        	this.logger.trace("queryLITS return " + timeQueryReturn.toString());
        	return timeQueryReturn;
        } catch (SaveInProgress | RestoreInProgress | FederateNotExecutionMember | NotConnected | RTIinternalError e) {
        	this.logger.error("queryLITS exception=" + e.getMessage());
        	throw e;
        }
    }


    // 8.19
    public void modifyLookahead(final LogicalTimeInterval theLookahead) throws InvalidLookahead, InTimeAdvancingState, TimeRegulationIsNotEnabled, SaveInProgress, RestoreInProgress, FederateNotExecutionMember, NotConnected, RTIinternalError {
        this.logger.trace("modifyLookahead theLookahead=" + theLookahead.toString());
        try {
        	this._rtiAmbassador.modifyLookahead(theLookahead);
        } catch (InvalidLookahead | InTimeAdvancingState | TimeRegulationIsNotEnabled | SaveInProgress | RestoreInProgress | FederateNotExecutionMember | NotConnected | RTIinternalError e) {
        	this.logger.error("modifyLookahead exception=" + e.getMessage());
        	throw e;
        }
    }


    // 8.20
    public LogicalTimeInterval queryLookahead() throws TimeRegulationIsNotEnabled, SaveInProgress, RestoreInProgress, FederateNotExecutionMember, NotConnected, RTIinternalError {
        this.logger.trace("queryLookahead");
        try {
        	final LogicalTimeInterval logicalTimeInterval = this._rtiAmbassador.queryLookahead();
        	this.logger.trace("queryLookahead return " + logicalTimeInterval.toString());
        	return logicalTimeInterval;
        } catch (TimeRegulationIsNotEnabled | SaveInProgress | RestoreInProgress | FederateNotExecutionMember | NotConnected | RTIinternalError e) {
        	this.logger.error("queryLookahead exception=" + e.getMessage());
        	throw e;
        }
    }


    // 8.21
    public void retract(final MessageRetractionHandle theHandle) throws MessageCanNoLongerBeRetracted, InvalidMessageRetractionHandle, TimeRegulationIsNotEnabled, SaveInProgress, RestoreInProgress, FederateNotExecutionMember, NotConnected, RTIinternalError {
        this.logger.trace("retract theHandle=" + theHandle.toString());
        try {
        	this._rtiAmbassador.retract(theHandle);
        } catch (MessageCanNoLongerBeRetracted | InvalidMessageRetractionHandle | TimeRegulationIsNotEnabled | SaveInProgress | RestoreInProgress | FederateNotExecutionMember | NotConnected | RTIinternalError e) {
        	this.logger.error("retract exception=" + e.getMessage());
        	throw e;
        }
    }


    // 8.23
    public void changeAttributeOrderType(final ObjectInstanceHandle theObject, final AttributeHandleSet theAttributes, final OrderType theType) throws AttributeNotOwned, AttributeNotDefined, ObjectInstanceNotKnown, SaveInProgress, RestoreInProgress, FederateNotExecutionMember, NotConnected, RTIinternalError {
        this.logger.trace("changeAttributeOrderType theObject=" + theObject.toString() + ", theAttributes=" + theAttributes.toString() + ", theType=" + theType.toString());
        try {
        	this._rtiAmbassador.changeAttributeOrderType(theObject, theAttributes, theType);
        } catch (AttributeNotOwned | AttributeNotDefined | ObjectInstanceNotKnown | SaveInProgress | RestoreInProgress | FederateNotExecutionMember | NotConnected | RTIinternalError e) {
        	this.logger.error("changeAttributeOrderType exception=" + e.getMessage());
        	throw e;
        }
    }


    // 8.24
    public void changeInteractionOrderType(final InteractionClassHandle theClass, final OrderType theType) throws InteractionClassNotPublished, InteractionClassNotDefined, SaveInProgress, RestoreInProgress, FederateNotExecutionMember, NotConnected, RTIinternalError {
        this.logger.trace("changeInteractionOrderType theClass=" + theClass.toString() + ", theType=" + theType.toString());
        try {
        	this._rtiAmbassador.changeInteractionOrderType(theClass, theType);
        } catch (InteractionClassNotPublished | InteractionClassNotDefined | SaveInProgress | RestoreInProgress | FederateNotExecutionMember | NotConnected | RTIinternalError e) {
        	this.logger.error("changeInteractionOrderType exception=" + e.getMessage());
        	throw e;
        }
    }

    //////////////////////////////////
    // Data Distribution Management //
    //////////////////////////////////


    // 9.2
    public RegionHandle createRegion(final DimensionHandleSet dimensions) throws InvalidDimensionHandle, SaveInProgress, RestoreInProgress, FederateNotExecutionMember, NotConnected, RTIinternalError {
        this.logger.trace("createRegion dimensions=" + dimensions.toString());
        try {
        	final RegionHandle regionHandle = this._rtiAmbassador.createRegion(dimensions);
        	this.logger.trace("createRegion return " + regionHandle.toString());
        	return regionHandle;
        } catch (InvalidDimensionHandle | SaveInProgress | RestoreInProgress | FederateNotExecutionMember | NotConnected | RTIinternalError e) {
        	this.logger.error("createRegion exception=" + e.getMessage());
        	throw e;
        }
    }


    // 9.3
    public void commitRegionModifications(final RegionHandleSet regions) throws RegionNotCreatedByThisFederate, InvalidRegion, SaveInProgress, RestoreInProgress, FederateNotExecutionMember, NotConnected, RTIinternalError {
        this.logger.trace("commitRegionModifications regions=" + regions.toString());
        try {
        	this._rtiAmbassador.commitRegionModifications(regions);
        } catch (RegionNotCreatedByThisFederate | InvalidRegion | SaveInProgress | RestoreInProgress | FederateNotExecutionMember | NotConnected | RTIinternalError e) {
        	this.logger.error("commitRegionModifications exception=" + e.getMessage());
        	throw e;
        }
    }


    // 9.4
    public void deleteRegion(final RegionHandle theRegion) throws RegionInUseForUpdateOrSubscription, RegionNotCreatedByThisFederate, InvalidRegion, SaveInProgress, RestoreInProgress, FederateNotExecutionMember, NotConnected, RTIinternalError {
        this.logger.trace("deleteRegion theRegion=" + theRegion.toString());
        try {
        	this._rtiAmbassador.deleteRegion(theRegion);
        } catch (RegionInUseForUpdateOrSubscription | RegionNotCreatedByThisFederate | InvalidRegion | SaveInProgress | RestoreInProgress | FederateNotExecutionMember | NotConnected | RTIinternalError e) {
        	this.logger.error("deleteRegion exception=" + e.getMessage());
        	throw e;
        }
    }


    //9.5
    public ObjectInstanceHandle registerObjectInstanceWithRegions(final ObjectClassHandle theClass, final AttributeSetRegionSetPairList attributesAndRegions) throws InvalidRegionContext, RegionNotCreatedByThisFederate, InvalidRegion, AttributeNotPublished, ObjectClassNotPublished, AttributeNotDefined, ObjectClassNotDefined, SaveInProgress, RestoreInProgress, FederateNotExecutionMember, NotConnected, RTIinternalError {
        this.logger.trace("registerObjectInstanceWithRegions theClass=" + theClass.toString() + ", attributesAndRegions=" + attributesAndRegions.toString());
        try {
        	final ObjectInstanceHandle objectInstanceHandle = this._rtiAmbassador.registerObjectInstanceWithRegions(theClass, attributesAndRegions);
        	this.logger.trace("registerObjectInstanceWithRegions return " + objectInstanceHandle.toString());
        	return objectInstanceHandle;
        } catch (InvalidRegionContext | RegionNotCreatedByThisFederate | InvalidRegion | AttributeNotPublished | ObjectClassNotPublished | AttributeNotDefined | ObjectClassNotDefined | SaveInProgress | RestoreInProgress | FederateNotExecutionMember | NotConnected | RTIinternalError e) {
        	this.logger.error("registerObjectInstanceWithRegions exception=" + e.getMessage());
        	throw e;
        }
    }


    //9.5
    public ObjectInstanceHandle registerObjectInstanceWithRegions(final ObjectClassHandle theClass, final AttributeSetRegionSetPairList attributesAndRegions, final String theObject) throws ObjectInstanceNameInUse, ObjectInstanceNameNotReserved, InvalidRegionContext, RegionNotCreatedByThisFederate, InvalidRegion, AttributeNotPublished, ObjectClassNotPublished, AttributeNotDefined, ObjectClassNotDefined, SaveInProgress, RestoreInProgress, FederateNotExecutionMember, NotConnected, RTIinternalError {
        this.logger.trace("registerObjectInstanceWithRegions theClass=" + theClass.toString() + ", attributesAndRegions=" + attributesAndRegions.toString() + ", theObject=" + theObject.toString());
        try {
        	final ObjectInstanceHandle objectInstanceHandle = this._rtiAmbassador.registerObjectInstanceWithRegions(theClass, attributesAndRegions, theObject);
        	this.logger.trace("registerObjectInstanceWithRegions return " + objectInstanceHandle.toString());
        	return objectInstanceHandle;
        } catch (ObjectInstanceNameInUse | ObjectInstanceNameNotReserved | InvalidRegionContext | RegionNotCreatedByThisFederate | InvalidRegion | AttributeNotPublished | ObjectClassNotPublished | AttributeNotDefined | ObjectClassNotDefined | SaveInProgress | RestoreInProgress | FederateNotExecutionMember | NotConnected | RTIinternalError e) {
        	this.logger.error("registerObjectInstanceWithRegions exception=" + e.getMessage());
        	throw e;
        }
    }


    // 9.6
    public void associateRegionsForUpdates(final ObjectInstanceHandle theObject, final AttributeSetRegionSetPairList attributesAndRegions) throws InvalidRegionContext, RegionNotCreatedByThisFederate, InvalidRegion, AttributeNotDefined, ObjectInstanceNotKnown, SaveInProgress, RestoreInProgress, FederateNotExecutionMember, NotConnected, RTIinternalError {
        this.logger.trace("associateRegionsForUpdates theObject=" + theObject.toString() + ", attributesAndRegions=" + attributesAndRegions.toString());
        try {
        	this._rtiAmbassador.associateRegionsForUpdates(theObject, attributesAndRegions);
        } catch (InvalidRegionContext | RegionNotCreatedByThisFederate | InvalidRegion | AttributeNotDefined | ObjectInstanceNotKnown | SaveInProgress | RestoreInProgress | FederateNotExecutionMember | NotConnected | RTIinternalError e) {
        	this.logger.error("associateRegionsForUpdates exception=" + e.getMessage());
        	throw e;
        }
    }


    // 9.7
    public void unassociateRegionsForUpdates(final ObjectInstanceHandle theObject, final AttributeSetRegionSetPairList attributesAndRegions) throws RegionNotCreatedByThisFederate, InvalidRegion, AttributeNotDefined, ObjectInstanceNotKnown, SaveInProgress, RestoreInProgress, FederateNotExecutionMember, NotConnected, RTIinternalError {
        this.logger.trace("unassociateRegionsForUpdates theObject=" + theObject.toString() + ", attributesAndRegions=" + attributesAndRegions.toString());
        try {
        	this._rtiAmbassador.unassociateRegionsForUpdates(theObject, attributesAndRegions);
        } catch (RegionNotCreatedByThisFederate | InvalidRegion | AttributeNotDefined | ObjectInstanceNotKnown | SaveInProgress | RestoreInProgress | FederateNotExecutionMember | NotConnected | RTIinternalError e) {
        	this.logger.error("unassociateRegionsForUpdates exception=" + e.getMessage());
        	throw e;
        }
    }


    // 9.8
    public void subscribeObjectClassAttributesWithRegions(final ObjectClassHandle theClass, final AttributeSetRegionSetPairList attributesAndRegions) throws InvalidRegionContext, RegionNotCreatedByThisFederate, InvalidRegion, AttributeNotDefined, ObjectClassNotDefined, SaveInProgress, RestoreInProgress, FederateNotExecutionMember, NotConnected, RTIinternalError {
        this.logger.trace("subscribeObjectClassAttributesWithRegions theClass=" + theClass.toString() + ", attributesAndRegions=" + attributesAndRegions.toString());
        try {
        	this._rtiAmbassador.subscribeObjectClassAttributesWithRegions(theClass, attributesAndRegions);
        } catch (InvalidRegionContext | RegionNotCreatedByThisFederate | InvalidRegion | AttributeNotDefined | ObjectClassNotDefined | SaveInProgress | RestoreInProgress | FederateNotExecutionMember | NotConnected | RTIinternalError e) {
        	this.logger.error("subscribeObjectClassAttributesWithRegions exception=" + e.getMessage());
        	throw e;
        }
    }


    // 9.8
    public void subscribeObjectClassAttributesWithRegions(final ObjectClassHandle theClass, final AttributeSetRegionSetPairList attributesAndRegions, final String updateRateDesignator) throws InvalidRegionContext, RegionNotCreatedByThisFederate, InvalidRegion, AttributeNotDefined, ObjectClassNotDefined, InvalidUpdateRateDesignator, SaveInProgress, RestoreInProgress, FederateNotExecutionMember, NotConnected, RTIinternalError {
        this.logger.trace("subscribeObjectClassAttributesWithRegions theClass=" + theClass.toString() + ", attributesAndRegions=" + attributesAndRegions.toString() + ", updateRateDesignator=" + updateRateDesignator);
        try {
        	this._rtiAmbassador.subscribeObjectClassAttributesWithRegions(theClass, attributesAndRegions, updateRateDesignator);
        } catch (InvalidRegionContext | RegionNotCreatedByThisFederate | InvalidRegion | AttributeNotDefined | ObjectClassNotDefined | InvalidUpdateRateDesignator | SaveInProgress | RestoreInProgress | FederateNotExecutionMember | NotConnected | RTIinternalError e) {
        	this.logger.error("subscribeObjectClassAttributesWithRegions exception=" + e.getMessage());
        	throw e;
        }
    }


    // 9.8
    public void subscribeObjectClassAttributesPassivelyWithRegions(final ObjectClassHandle theClass, final AttributeSetRegionSetPairList attributesAndRegions) throws InvalidRegionContext, RegionNotCreatedByThisFederate, InvalidRegion, AttributeNotDefined, ObjectClassNotDefined, SaveInProgress, RestoreInProgress, FederateNotExecutionMember, NotConnected, RTIinternalError {
        this.logger.trace("subscribeObjectClassAttributesPassivelyWithRegions theClass=" + theClass.toString() + ", attributesAndRegions=" + attributesAndRegions.toString());
        try {
        	this._rtiAmbassador.subscribeObjectClassAttributesPassivelyWithRegions(theClass, attributesAndRegions);
        } catch (InvalidRegionContext | RegionNotCreatedByThisFederate | InvalidRegion | AttributeNotDefined | ObjectClassNotDefined | SaveInProgress | RestoreInProgress | FederateNotExecutionMember | NotConnected | RTIinternalError e) {
        	this.logger.error("subscribeObjectClassAttributesPassivelyWithRegions exception=" + e.getMessage());
        	throw e;
        }
    }


    // 9.8
    public void subscribeObjectClassAttributesPassivelyWithRegions(final ObjectClassHandle theClass, final AttributeSetRegionSetPairList attributesAndRegions, final String updateRateDesignator) throws InvalidRegionContext, RegionNotCreatedByThisFederate, InvalidRegion, AttributeNotDefined, ObjectClassNotDefined, InvalidUpdateRateDesignator, SaveInProgress, RestoreInProgress, FederateNotExecutionMember, NotConnected, RTIinternalError {
        this.logger.trace("subscribeObjectClassAttributesPassivelyWithRegions theClass=" + theClass.toString() + ", attributesAndRegions=" + attributesAndRegions.toString() + ", updateRateDesignator=" + updateRateDesignator);
        try {
        	this._rtiAmbassador.subscribeObjectClassAttributesPassivelyWithRegions(theClass, attributesAndRegions, updateRateDesignator);
        } catch (InvalidRegionContext | RegionNotCreatedByThisFederate | InvalidRegion | AttributeNotDefined | ObjectClassNotDefined | InvalidUpdateRateDesignator | SaveInProgress | RestoreInProgress | FederateNotExecutionMember | NotConnected | RTIinternalError e) {
        	this.logger.error("subscribeObjectClassAttributesPassivelyWithRegions exception=" + e.getMessage());
        	throw e;
        }
    }


    // 9.9
    public void unsubscribeObjectClassAttributesWithRegions(final ObjectClassHandle theClass, final AttributeSetRegionSetPairList attributesAndRegions) throws RegionNotCreatedByThisFederate, InvalidRegion, AttributeNotDefined, ObjectClassNotDefined, SaveInProgress, RestoreInProgress, FederateNotExecutionMember, NotConnected, RTIinternalError {
        this.logger.trace("unsubscribeObjectClassAttributesWithRegions theClass=" + theClass.toString() + ", attributesAndRegions=" + attributesAndRegions.toString());
        try {
        	this._rtiAmbassador.unsubscribeObjectClassAttributesWithRegions(theClass, attributesAndRegions);
        } catch (RegionNotCreatedByThisFederate | InvalidRegion | AttributeNotDefined | ObjectClassNotDefined | SaveInProgress | RestoreInProgress | FederateNotExecutionMember | NotConnected | RTIinternalError e) {
        	this.logger.error("unsubscribeObjectClassAttributesWithRegions exception=" + e.getMessage());
        	throw e;
        }
    }


    // 9.10
    public void subscribeInteractionClassWithRegions(final InteractionClassHandle theClass, final RegionHandleSet regions) throws FederateServiceInvocationsAreBeingReportedViaMOM, InvalidRegionContext, RegionNotCreatedByThisFederate, InvalidRegion, InteractionClassNotDefined, SaveInProgress, RestoreInProgress, FederateNotExecutionMember, NotConnected, RTIinternalError {
        this.logger.trace("subscribeInteractionClassWithRegions theClass=" + theClass.toString() + ", regions=" + regions.toString());
        try {
        	this._rtiAmbassador.subscribeInteractionClassWithRegions(theClass, regions);
        } catch (FederateServiceInvocationsAreBeingReportedViaMOM | InvalidRegionContext | RegionNotCreatedByThisFederate | InvalidRegion | InteractionClassNotDefined | SaveInProgress | RestoreInProgress | FederateNotExecutionMember | NotConnected | RTIinternalError e) {
        	this.logger.error("subscribeInteractionClassWithRegions exception=" + e.getMessage());
        	throw e;
        }
    }


    // 9.10
    public void subscribeInteractionClassPassivelyWithRegions(final InteractionClassHandle theClass, final RegionHandleSet regions) throws FederateServiceInvocationsAreBeingReportedViaMOM, InvalidRegionContext, RegionNotCreatedByThisFederate, InvalidRegion, InteractionClassNotDefined, SaveInProgress, RestoreInProgress, FederateNotExecutionMember, NotConnected, RTIinternalError {
        this.logger.trace("subscribeInteractionClassPassivelyWithRegions theClass=" + theClass.toString() + ", regions=" + regions.toString());
        try {
        	this._rtiAmbassador.subscribeInteractionClassPassivelyWithRegions(theClass, regions);
        } catch (FederateServiceInvocationsAreBeingReportedViaMOM | InvalidRegionContext | RegionNotCreatedByThisFederate | InvalidRegion | InteractionClassNotDefined | SaveInProgress | RestoreInProgress | FederateNotExecutionMember | NotConnected | RTIinternalError e) {
        	this.logger.error("subscribeInteractionClassPassivelyWithRegions exception=" + e.getMessage());
        	throw e;
        }
    }


    // 9.11
    public void unsubscribeInteractionClassWithRegions(final InteractionClassHandle theClass, final RegionHandleSet regions) throws RegionNotCreatedByThisFederate, InvalidRegion, InteractionClassNotDefined, SaveInProgress, RestoreInProgress, FederateNotExecutionMember, NotConnected, RTIinternalError {
        this.logger.trace("unsubscribeInteractionClassWithRegions theClass=" + theClass.toString() + ", regions=" + regions.toString());
        try {
        	this._rtiAmbassador.unsubscribeInteractionClassWithRegions(theClass, regions);
        } catch (RegionNotCreatedByThisFederate | InvalidRegion | InteractionClassNotDefined | SaveInProgress | RestoreInProgress | FederateNotExecutionMember | NotConnected | RTIinternalError e) {
        	this.logger.error("unsubscribeInteractionClassWithRegions exception=" + e.getMessage());
        	throw e;
        }
    }


    //9.12
    public void sendInteractionWithRegions(final InteractionClassHandle theInteraction, final ParameterHandleValueMap theParameters, final RegionHandleSet regions, final byte[] userSuppliedTag) throws InvalidRegionContext, RegionNotCreatedByThisFederate, InvalidRegion, InteractionClassNotPublished, InteractionParameterNotDefined, InteractionClassNotDefined, SaveInProgress, RestoreInProgress, FederateNotExecutionMember, NotConnected, RTIinternalError {
        this.logger.trace("sendInteractionWithRegions theInteraction=" + theInteraction.toString() + ", theParameters=" + theParameters.toString() + ", regions=" + regions.toString() + ", userSuppliedTag=" + Arrays.toString(userSuppliedTag));
        try {
        	this._rtiAmbassador.sendInteractionWithRegions(theInteraction, theParameters, regions, userSuppliedTag);
        } catch (InvalidRegionContext | RegionNotCreatedByThisFederate | InvalidRegion | InteractionClassNotPublished | InteractionParameterNotDefined | InteractionClassNotDefined | SaveInProgress | RestoreInProgress | FederateNotExecutionMember | NotConnected | RTIinternalError e) {
        	this.logger.error("sendInteractionWithRegions exception=" + e.getMessage());
        	throw e;
        }
    }


    //9.12
    public MessageRetractionReturn sendInteractionWithRegions(final InteractionClassHandle theInteraction, final ParameterHandleValueMap theParameters, final RegionHandleSet regions, final byte[] userSuppliedTag, final LogicalTime theTime) throws InvalidLogicalTime, InvalidRegionContext, RegionNotCreatedByThisFederate, InvalidRegion, InteractionClassNotPublished, InteractionParameterNotDefined, InteractionClassNotDefined, SaveInProgress, RestoreInProgress, FederateNotExecutionMember, NotConnected, RTIinternalError {
        this.logger.trace("sendInteractionWithRegions theInteraction=" + theInteraction.toString() + ", theParameters=" + theParameters.toString() + ", regions=" + regions.toString() + ", userSuppliedTag=" + Arrays.toString(userSuppliedTag) + ", theTime=" + theTime.toString());
        try {
        	final MessageRetractionReturn messageRetractionReturn = this._rtiAmbassador.sendInteractionWithRegions(theInteraction, theParameters, regions, userSuppliedTag, theTime);
        	this.logger.trace("sendInteractionWithRegions return " + messageRetractionReturn.toString());
        	return messageRetractionReturn;
        } catch (InvalidLogicalTime | InvalidRegionContext | RegionNotCreatedByThisFederate | InvalidRegion | InteractionClassNotPublished | InteractionParameterNotDefined | InteractionClassNotDefined | SaveInProgress | RestoreInProgress | FederateNotExecutionMember | NotConnected | RTIinternalError e) {
        	this.logger.error("sendInteractionWithRegions exception=" + e.getMessage());
        	throw e;
        }
    }


    // 9.13
    public void requestAttributeValueUpdateWithRegions(final ObjectClassHandle theClass, final AttributeSetRegionSetPairList attributesAndRegions, final byte[] userSuppliedTag) throws InvalidRegionContext, RegionNotCreatedByThisFederate, InvalidRegion, AttributeNotDefined, ObjectClassNotDefined, SaveInProgress, RestoreInProgress, FederateNotExecutionMember, NotConnected, RTIinternalError {
        this.logger.trace("requestAttributeValueUpdateWithRegions theClass=" + theClass.toString() + ", attributesAndRegions=" + attributesAndRegions.toString() + ", userSuppliedTag=" + Arrays.toString(userSuppliedTag));
        try {
        	this._rtiAmbassador.requestAttributeValueUpdateWithRegions(theClass, attributesAndRegions, userSuppliedTag);
        } catch (InvalidRegionContext | RegionNotCreatedByThisFederate | InvalidRegion | AttributeNotDefined | ObjectClassNotDefined | SaveInProgress | RestoreInProgress | FederateNotExecutionMember | NotConnected | RTIinternalError e) {
        	this.logger.error("requestAttributeValueUpdateWithRegions exception=" + e.getMessage());
        	throw e;
        }
    }

    //////////////////////////
    // RTI Support Services //
    //////////////////////////


    // 10.2
    public ResignAction getAutomaticResignDirective() throws FederateNotExecutionMember, NotConnected, RTIinternalError {
        this.logger.trace("getAutomaticResignDirective");
        try {
        	final ResignAction resignAction = this._rtiAmbassador.getAutomaticResignDirective();
        	this.logger.trace("getAutomaticResignDirective return " + resignAction.toString());
        	return resignAction;
        } catch (FederateNotExecutionMember | NotConnected | RTIinternalError e) {
        	this.logger.error("getAutomaticResignDirective exception=" + e.getMessage());
        	throw e;
        }
    }


    // 10.3
    public void setAutomaticResignDirective(final ResignAction resignAction) throws InvalidResignAction, FederateNotExecutionMember, NotConnected, RTIinternalError {
        this.logger.trace("setAutomaticResignDirective resignAction=" + resignAction.toString());
        try {
        	this._rtiAmbassador.setAutomaticResignDirective(resignAction);
        } catch (InvalidResignAction | FederateNotExecutionMember | NotConnected | RTIinternalError e) {
        	this.logger.error("setAutomaticResignDirective exception=" + e.getMessage());
        	throw e;
        }
    }


    // 10.4
    public FederateHandle getFederateHandle(final String theName) throws NameNotFound, FederateNotExecutionMember, NotConnected, RTIinternalError {
        this.logger.trace("getFederateHandle theName=" + theName);
        try {
        	final FederateHandle federateHandle = this._rtiAmbassador.getFederateHandle(theName);
        	this.logger.trace("getFederateHandle return " + federateHandle.toString());
        	return federateHandle;
        } catch (NameNotFound | FederateNotExecutionMember | NotConnected | RTIinternalError e) {
        	this.logger.error("getFederateHandle exception=" + e.getMessage());
        	throw e;
        }
    }


    // 10.5
    public String getFederateName(final FederateHandle theHandle) throws InvalidFederateHandle, FederateHandleNotKnown, FederateNotExecutionMember, NotConnected, RTIinternalError {
        this.logger.trace("getFederateName theHandle=" + theHandle.toString());
        try {
        	final String str = this._rtiAmbassador.getFederateName(theHandle);
        	this.logger.trace("getFederateName return " + str);
        	return str;
        } catch (InvalidFederateHandle | FederateHandleNotKnown | FederateNotExecutionMember | NotConnected | RTIinternalError e) {
        	this.logger.error("getFederateName exception=" + e.getMessage());
        	throw e;
        }
    }


    // 10.6
    public ObjectClassHandle getObjectClassHandle(final String theName) throws NameNotFound, FederateNotExecutionMember, NotConnected, RTIinternalError {
        this.logger.trace("getObjectClassHandle theName=" + theName);
        try {
        	final ObjectClassHandle objectClassHandle = this._rtiAmbassador.getObjectClassHandle(theName);
        	this.logger.trace("getObjectClassHandle return " + objectClassHandle.toString());
        	return objectClassHandle;
        } catch (NameNotFound | FederateNotExecutionMember | NotConnected | RTIinternalError e) {
        	this.logger.error("getObjectClassHandle exception=" + e.getMessage());
        	throw e;
        }
    }


    // 10.7
    public String getObjectClassName(final ObjectClassHandle theHandle) throws InvalidObjectClassHandle, FederateNotExecutionMember, NotConnected, RTIinternalError {
        this.logger.trace("getObjectClassName theHandle=" + theHandle.toString());
        try {
        	final String str = this._rtiAmbassador.getObjectClassName(theHandle);
        	this.logger.trace("getObjectClassName return " + str);
        	return str;
        } catch (InvalidObjectClassHandle | FederateNotExecutionMember | NotConnected | RTIinternalError e) {
        	this.logger.error("getObjectClassName exception=" + e.getMessage());
        	throw e;
        }
    }


    // 10.8
    public ObjectClassHandle getKnownObjectClassHandle(final ObjectInstanceHandle theObject) throws ObjectInstanceNotKnown, FederateNotExecutionMember, NotConnected, RTIinternalError {
        this.logger.trace("getKnownObjectClassHandle theObject=" + theObject.toString());
        try {
        	final ObjectClassHandle objectClassHandle = this._rtiAmbassador.getKnownObjectClassHandle(theObject);
        	this.logger.trace("getKnownObjectClassHandle return " + objectClassHandle.toString());
        	return objectClassHandle;
        } catch (ObjectInstanceNotKnown | FederateNotExecutionMember | NotConnected | RTIinternalError e) {
        	this.logger.error("getKnownObjectClassHandle exception=" + e.getMessage());
        	throw e;
        }
    }


    // 10.9
    public ObjectInstanceHandle getObjectInstanceHandle(final String theName) throws ObjectInstanceNotKnown, FederateNotExecutionMember, NotConnected, RTIinternalError {
        this.logger.trace("getObjectInstanceHandle theName=" + theName);
        try {
        	final ObjectInstanceHandle objectInstanceHandle = this._rtiAmbassador.getObjectInstanceHandle(theName);
        	this.logger.trace("getObjectInstanceHandle return " + objectInstanceHandle.toString());
        	return objectInstanceHandle;
        } catch (ObjectInstanceNotKnown | FederateNotExecutionMember | NotConnected | RTIinternalError e) {
        	this.logger.error("getObjectInstanceHandle exception=" + e.getMessage());
        	throw e;
        }
    }


    // 10.10
    public String getObjectInstanceName(final ObjectInstanceHandle theHandle) throws ObjectInstanceNotKnown, FederateNotExecutionMember, NotConnected, RTIinternalError {
        this.logger.trace("getObjectInstanceName theHandle=" + theHandle.toString());
        try {
        	final String str = this._rtiAmbassador.getObjectInstanceName(theHandle);
        	this.logger.trace("getObjectInstanceName return " + str);
        	return str;
        } catch (ObjectInstanceNotKnown | FederateNotExecutionMember | NotConnected | RTIinternalError e) {
        	this.logger.error("getObjectInstanceName exception=" + e.getMessage());
        	throw e;
        }
    }


    // 10.11
    public AttributeHandle getAttributeHandle(final ObjectClassHandle whichClass, final String theName) throws NameNotFound, InvalidObjectClassHandle, FederateNotExecutionMember, NotConnected, RTIinternalError {
    	this.logger.trace("getAttributeHandle whichClass=" + whichClass.toString() + ", theName=" + theName);
    	try {
    		final AttributeHandle attributeHandle = this._rtiAmbassador.getAttributeHandle(whichClass, theName);
    		this.logger.trace("getAttributeHandle return " + attributeHandle.toString());
    		return attributeHandle;
    	} catch (NameNotFound | InvalidObjectClassHandle | FederateNotExecutionMember | NotConnected | RTIinternalError e) {
    		this.logger.error("getAttributeHandle exception=" + e.getMessage());
    		throw e;
    	}
    }


    // 10.12
    public String getAttributeName(final ObjectClassHandle whichClass, final AttributeHandle theHandle) throws AttributeNotDefined, InvalidAttributeHandle, InvalidObjectClassHandle, FederateNotExecutionMember, NotConnected, RTIinternalError {
        this.logger.trace("getAttributeName whichClass=" + whichClass.toString() + ", theHandle=" + theHandle.toString());
        try {
        	final String str = this._rtiAmbassador.getAttributeName(whichClass, theHandle);
        	this.logger.trace("getAttributeName return " + str);
        	return str;
        } catch (AttributeNotDefined | InvalidAttributeHandle | InvalidObjectClassHandle | FederateNotExecutionMember | NotConnected | RTIinternalError e) {
        	this.logger.error("getAttributeName exception=" + e.getMessage());
        	throw e;
        }
    }


    // 10.13
    public double getUpdateRateValue(final String updateRateDesignator) throws InvalidUpdateRateDesignator, FederateNotExecutionMember, NotConnected, RTIinternalError {
        this.logger.trace("getUpdateRateValue updateRateDesignator=" + updateRateDesignator);
        try {
        	final double d = this._rtiAmbassador.getUpdateRateValue(updateRateDesignator);
        	this.logger.trace("getUpdateRateValue return " + d);
        	return d;
        } catch (InvalidUpdateRateDesignator | FederateNotExecutionMember | NotConnected | RTIinternalError e) {
        	this.logger.error("getUpdateRateValue exception=" + e.getMessage());
        	throw e;
        }
    }


    // 10.14
    public double getUpdateRateValueForAttribute(final ObjectInstanceHandle theObject, final AttributeHandle theAttribute) throws ObjectInstanceNotKnown, AttributeNotDefined, FederateNotExecutionMember, NotConnected, RTIinternalError {
        this.logger.trace("getUpdateRateValueForAttribute theObject=" + theObject.toString() + ", theAttribute=" + theAttribute.toString());
        try {
        	final double d = this._rtiAmbassador.getUpdateRateValueForAttribute(theObject, theAttribute);
        	this.logger.trace("getUpdateRateValueForAttribute return " + d);
        	return d;
        } catch (ObjectInstanceNotKnown | AttributeNotDefined | FederateNotExecutionMember | NotConnected | RTIinternalError e) {
        	this.logger.error("getUpdateRateValueForAttribute exception=" + e.getMessage());
        	throw e;
        }
    }


    // 10.15
    public InteractionClassHandle getInteractionClassHandle(final String theName) throws NameNotFound, FederateNotExecutionMember, NotConnected, RTIinternalError {
        this.logger.trace("getInteractionClassHandle theName=" + theName);
        try {
        	final InteractionClassHandle interactionClassHandle = this._rtiAmbassador.getInteractionClassHandle(theName);
        	this.logger.trace("getInteractionClassHandle return " + interactionClassHandle.toString());
        	return interactionClassHandle;
        } catch (NameNotFound | FederateNotExecutionMember | NotConnected | RTIinternalError e) {
        	this.logger.error("getInteractionClassHandle exception=" + e.getMessage());
        	throw e;
        }
    }


    // 10.16
    public String getInteractionClassName(final InteractionClassHandle theHandle) throws InvalidInteractionClassHandle, FederateNotExecutionMember, NotConnected, RTIinternalError {
    	this.logger.trace("getInteractionClassName theHandle=" + theHandle.toString());
    	try {
    		final String str = this._rtiAmbassador.getInteractionClassName(theHandle);
    		this.logger.trace("getInteractionClassName return " + str);
    		return str;
    	} catch (InvalidInteractionClassHandle | FederateNotExecutionMember | NotConnected | RTIinternalError e) {
    		this.logger.error("getInteractionClassName exception=" + e.getMessage());
    		throw e;
    	}
    }


    // 10.17
    public ParameterHandle getParameterHandle(final InteractionClassHandle whichClass, final String theName) throws NameNotFound, InvalidInteractionClassHandle, FederateNotExecutionMember, NotConnected, RTIinternalError {
    	this.logger.trace("getParameterHandle whichClass=" + whichClass.toString() + ", theName=" + theName);
    	try {
    		final ParameterHandle parameterHandle = this._rtiAmbassador.getParameterHandle(whichClass, theName);
    		this.logger.trace("getParameterHandle return " + parameterHandle.toString());
    		return parameterHandle;
    	} catch (NameNotFound | InvalidInteractionClassHandle | FederateNotExecutionMember | NotConnected | RTIinternalError e) {
    		this.logger.error("getParameterHandle exception=" + e.getMessage());
    		throw e;
    	}
    }


    // 10.18
    public String getParameterName(final InteractionClassHandle whichClass, final ParameterHandle theHandle) throws InteractionParameterNotDefined, InvalidParameterHandle, InvalidInteractionClassHandle, FederateNotExecutionMember, NotConnected, RTIinternalError {
        this.logger.trace("getParameterName whichClass=" + whichClass.toString() + ", theHandle=" + theHandle.toString());
    	try {
    		String str = this._rtiAmbassador.getParameterName(whichClass, theHandle);
    		this.logger.trace("getParameterName return " + str);
    		return str;
    	} catch (InteractionParameterNotDefined | InvalidParameterHandle | InvalidInteractionClassHandle | FederateNotExecutionMember | NotConnected | RTIinternalError e) {
    		this.logger.error("getParameterName exception=" + e.getMessage());
    		throw e;
    	}
    }


    // 10.19
    public OrderType getOrderType(final String theName) throws InvalidOrderName, FederateNotExecutionMember, NotConnected, RTIinternalError {
    	this.logger.trace("getOrderType theName=" + theName);
    	try {
    		OrderType orderType = this._rtiAmbassador.getOrderType(theName);
    		this.logger.trace("getOrderType return " + orderType.toString());
    		return orderType;
    	} catch (InvalidOrderName | FederateNotExecutionMember | NotConnected | RTIinternalError e) {
    		this.logger.error("getOrderType exception=" + e.getMessage());
    		throw e;
    	}
    }


    // 10.20
    public String getOrderName(final OrderType theType) throws InvalidOrderType, FederateNotExecutionMember, NotConnected, RTIinternalError {
    	this.logger.trace("getOrderName theType=" + theType.toString());
    	try {
    		String str =  this._rtiAmbassador.getOrderName(theType);
    		this.logger.trace("getOrderName return " + str);
    		return str;
    	} catch (InvalidOrderType | FederateNotExecutionMember | NotConnected | RTIinternalError e) {
    		this.logger.error("getOrderName exception=" + e.getMessage());
    		throw e;
    	}
    }


    // 10.21
    public TransportationTypeHandle getTransportationTypeHandle(final String theName) throws InvalidTransportationName, FederateNotExecutionMember, NotConnected, RTIinternalError {
    	this.logger.trace("getTransportationTypeHandle theName=" + theName);
    	try {
    		TransportationTypeHandle transportationTypeHandle = this._rtiAmbassador.getTransportationTypeHandle(theName);
    		this.logger.trace("getTransportationTypeHandle return " + transportationTypeHandle.toString());
    		return transportationTypeHandle;
    	} catch (InvalidTransportationName | FederateNotExecutionMember | NotConnected | RTIinternalError e) {
    		this.logger.error("getTransportationTypeHandle exception=" + e.getMessage());
    		throw e;
    	}
    }


    // 10.22
    public String getTransportationTypeName(final TransportationTypeHandle theHandle) throws InvalidTransportationType, FederateNotExecutionMember, NotConnected, RTIinternalError {
    	this.logger.trace("getTransportationTypeName theHandle=" + theHandle.toString());
    	try {
    		String str =  this._rtiAmbassador.getTransportationTypeName(theHandle);
    		this.logger.trace("getTransportationTypeName return " + str);
    		return str;
    	} catch (InvalidTransportationType | FederateNotExecutionMember | NotConnected | RTIinternalError e) {
    		this.logger.error("getTransportationTypeName exception=" + e.getMessage());
    		throw e;
    	}
    }


    // 10.23
    public DimensionHandleSet getAvailableDimensionsForClassAttribute(final ObjectClassHandle whichClass, final AttributeHandle theHandle) throws AttributeNotDefined, InvalidAttributeHandle, InvalidObjectClassHandle, FederateNotExecutionMember, NotConnected, RTIinternalError {
    	this.logger.trace("getAvailableDimensionsForClassAttribute whichClass=" + whichClass.toString() + ", theHandle=" + theHandle.toString());
    	try {
    		DimensionHandleSet dimensionHandleSet =  this._rtiAmbassador.getAvailableDimensionsForClassAttribute(whichClass, theHandle);
    		this.logger.trace("getAvailableDimensionsForClassAttribute return " + dimensionHandleSet.toString());
    		return dimensionHandleSet;
    	} catch (AttributeNotDefined | InvalidAttributeHandle | InvalidObjectClassHandle | FederateNotExecutionMember | NotConnected | RTIinternalError e) {
    		this.logger.error("getAvailableDimensionsForClassAttribute exception=" + e.getMessage());
    		throw e;
    	}
    }


    // 10.24
    public DimensionHandleSet getAvailableDimensionsForInteractionClass(final InteractionClassHandle theHandle) throws InvalidInteractionClassHandle, FederateNotExecutionMember, NotConnected, RTIinternalError {
    	this.logger.trace("getAvailableDimensionsForInteractionClass theHandle=" + theHandle.toString());
    	try {
    		DimensionHandleSet dimensionHandleSet = this._rtiAmbassador.getAvailableDimensionsForInteractionClass(theHandle);
    		this.logger.trace("getAvailableDimensionsForInteractionClass return " + dimensionHandleSet.toString());
    		return dimensionHandleSet;
    	} catch (InvalidInteractionClassHandle | FederateNotExecutionMember | NotConnected | RTIinternalError e) {
    		this.logger.error("getAvailableDimensionsForInteractionClass exception=" + e.getMessage());
    		throw e;
    	}
    }


    // 10.25
    public DimensionHandle getDimensionHandle(final String theName) throws NameNotFound, FederateNotExecutionMember, NotConnected, RTIinternalError {
    	this.logger.trace("getDimensionHandle theName=" + theName);
    	try {
    		DimensionHandle dimensionHandle = this._rtiAmbassador.getDimensionHandle(theName);
    		this.logger.trace("getDimensionHandle return " + dimensionHandle.toString());
    		return dimensionHandle;
    	} catch (NameNotFound | FederateNotExecutionMember | NotConnected | RTIinternalError e) {
    		this.logger.error("getDimensionHandle exception=" + e.getMessage());
    		throw e;
    	}
    }


    // 10.26
    public String getDimensionName(final DimensionHandle theHandle) throws InvalidDimensionHandle, FederateNotExecutionMember, NotConnected, RTIinternalError {
    	this.logger.trace("getDimensionName theHandle=" + theHandle.toString());
    	try {
    		String str = this._rtiAmbassador.getDimensionName(theHandle);
    		this.logger.trace("getDimensionName return " + str);
    		return str;
    	} catch (InvalidDimensionHandle | FederateNotExecutionMember | NotConnected | RTIinternalError e) {
    		this.logger.error("getDimensionName exception=" + e.getMessage());
    		throw e;
    	}
    }


    // 10.27
    public long getDimensionUpperBound(final DimensionHandle theHandle) throws InvalidDimensionHandle, FederateNotExecutionMember, NotConnected, RTIinternalError {
    	this.logger.trace("getDimensionUpperBound theHandle=" + theHandle.toString());
    	try {
    		long upperBound = this._rtiAmbassador.getDimensionUpperBound(theHandle);
    		this.logger.trace("getDimensionUpperBound return " + upperBound);
    		return upperBound;
    	} catch (InvalidDimensionHandle | FederateNotExecutionMember | NotConnected | RTIinternalError e) {
    		this.logger.error("getDimensionUpperBound exception=" + e.getMessage());
    		throw e;
    	}
    }


    // 10.28
    public DimensionHandleSet getDimensionHandleSet(final RegionHandle region) throws InvalidRegion, SaveInProgress, RestoreInProgress, FederateNotExecutionMember, NotConnected, RTIinternalError {
    	this.logger.trace("getDimensionHandleSet region=" + region.toString());
    	try {
    		DimensionHandleSet dimensionHandleSet = this._rtiAmbassador.getDimensionHandleSet(region);
    		this.logger.trace("getDimensionHandleSet return " + dimensionHandleSet.toString());
    		return dimensionHandleSet;
    	} catch (InvalidRegion | SaveInProgress | RestoreInProgress | FederateNotExecutionMember | NotConnected | RTIinternalError e) {
    		this.logger.error("getDimensionHandleSet exception=" + e.getMessage());
    		throw e;
    	}
    }


    // 10.29
    public RangeBounds getRangeBounds(final RegionHandle region, final DimensionHandle dimension) throws RegionDoesNotContainSpecifiedDimension, InvalidRegion, SaveInProgress, RestoreInProgress, FederateNotExecutionMember, NotConnected, RTIinternalError {
    	this.logger.trace("getRangeBounds region=" + region.toString() + ", dimension=" + dimension.toString());
    	try {
    		RangeBounds rangeBounds = this._rtiAmbassador.getRangeBounds(region, dimension);
    		this.logger.trace("getRangeBounds return " + rangeBounds.toString());
    		return rangeBounds;
    	} catch (RegionDoesNotContainSpecifiedDimension | InvalidRegion | SaveInProgress | RestoreInProgress | FederateNotExecutionMember | NotConnected | RTIinternalError e) {
    		this.logger.error("getRangeBounds exception=" + e.getMessage());
    		throw e;
    	}
    }


    // 10.30
    public void setRangeBounds(final RegionHandle region, final DimensionHandle dimension, final RangeBounds bounds) throws InvalidRangeBound, RegionDoesNotContainSpecifiedDimension, RegionNotCreatedByThisFederate, InvalidRegion, SaveInProgress, RestoreInProgress, FederateNotExecutionMember, NotConnected, RTIinternalError {
    	this.logger.trace("setRangeBounds region=" + region.toString() + ", dimension=" + dimension.toString() + ", bounds=" + bounds.toString());
    	try {
    		this._rtiAmbassador.setRangeBounds(region, dimension, bounds);
    	} catch (InvalidRangeBound | RegionDoesNotContainSpecifiedDimension | RegionNotCreatedByThisFederate | InvalidRegion | SaveInProgress | RestoreInProgress | FederateNotExecutionMember | NotConnected | RTIinternalError e) {
    		this.logger.error("setRangeBounds exception=" + e.getMessage());
    		throw e;
    	}
    }


    // 10.31
    public long normalizeFederateHandle(final FederateHandle federateHandle) throws InvalidFederateHandle, FederateNotExecutionMember, NotConnected, RTIinternalError {
    	this.logger.trace("normalizeFederateHandle federateHandle=" + federateHandle.toString());
    	try {
    		long normalizedFederateHandle = this._rtiAmbassador.normalizeFederateHandle(federateHandle);
    		this.logger.trace("normalizeFederateHandle return " + normalizedFederateHandle);
    		return normalizedFederateHandle;
    	} catch (InvalidFederateHandle | FederateNotExecutionMember | NotConnected | RTIinternalError e) {
    		this.logger.error("normalizeFederateHandle exception=" + e.getMessage());
    		throw e;
    	}
    }


    // 10.32
    public long normalizeServiceGroup(final ServiceGroup group) throws InvalidServiceGroup, FederateNotExecutionMember, NotConnected, RTIinternalError {
    	this.logger.trace("normalizeServiceGroup group=" + group.toString());
    	try {
    		long normalizedServiceGroup = this._rtiAmbassador.normalizeServiceGroup(group);
    		this.logger.trace("normalizeServiceGroup return " + normalizedServiceGroup);
    		return normalizedServiceGroup;
    	} catch (InvalidServiceGroup | FederateNotExecutionMember | NotConnected | RTIinternalError e) {
    		this.logger.error("normalizeServiceGroup exception=" + e.getMessage());
    		throw e;
    	}
    }


    // 10.33
    public void enableObjectClassRelevanceAdvisorySwitch() throws ObjectClassRelevanceAdvisorySwitchIsOn, SaveInProgress, RestoreInProgress, FederateNotExecutionMember, NotConnected, RTIinternalError {
    	this.logger.trace("enableObjectClassRelevanceAdvisorySwitch");
    	try {
    		this._rtiAmbassador.enableObjectClassRelevanceAdvisorySwitch();
    	} catch (ObjectClassRelevanceAdvisorySwitchIsOn | SaveInProgress | RestoreInProgress | FederateNotExecutionMember | NotConnected | RTIinternalError e) {
    		this.logger.error("enableObjectClassRelevanceAdvisorySwitch exception=" + e.getMessage());
    		throw e;
    	}
    }


    // 10.34
    public void disableObjectClassRelevanceAdvisorySwitch() throws ObjectClassRelevanceAdvisorySwitchIsOff, SaveInProgress, RestoreInProgress, FederateNotExecutionMember, NotConnected, RTIinternalError {
    	this.logger.trace("disableObjectClassRelevanceAdvisorySwitch");
    	try {
    		this._rtiAmbassador.disableObjectClassRelevanceAdvisorySwitch();
    	} catch (ObjectClassRelevanceAdvisorySwitchIsOff | SaveInProgress | RestoreInProgress | FederateNotExecutionMember | NotConnected | RTIinternalError e) {
    		this.logger.error("disableObjectClassRelevanceAdvisorySwitch exception=" + e.getMessage());
    		throw e;
    	}
    }


    // 10.35
    public void enableAttributeRelevanceAdvisorySwitch() throws AttributeRelevanceAdvisorySwitchIsOn, SaveInProgress, RestoreInProgress, FederateNotExecutionMember, NotConnected, RTIinternalError {
        this.logger.trace("enableAttributeRelevanceAdvisorySwitch");
        try {
        	this._rtiAmbassador.enableAttributeRelevanceAdvisorySwitch();
        } catch (AttributeRelevanceAdvisorySwitchIsOn | SaveInProgress | RestoreInProgress | FederateNotExecutionMember | NotConnected | RTIinternalError e) {
        	this.logger.error("enableAttributeRelevanceAdvisorySwitch exception=" + e.getMessage());
        	throw e;
        }
    }


    // 10.36
    public void disableAttributeRelevanceAdvisorySwitch() throws AttributeRelevanceAdvisorySwitchIsOff, SaveInProgress, RestoreInProgress, FederateNotExecutionMember, NotConnected, RTIinternalError {
        this.logger.trace("disableAttributeRelevanceAdvisorySwitch");
        try {
        	this._rtiAmbassador.disableAttributeRelevanceAdvisorySwitch();
        } catch (AttributeRelevanceAdvisorySwitchIsOff | SaveInProgress | RestoreInProgress | FederateNotExecutionMember | NotConnected | RTIinternalError e) {
        	this.logger.error("disableAttributeRelevanceAdvisorySwitch exception=" + e.getMessage());
        	throw e;
        }
    }


    // 10.37
    public void enableAttributeScopeAdvisorySwitch() throws AttributeScopeAdvisorySwitchIsOn, SaveInProgress, RestoreInProgress, FederateNotExecutionMember, NotConnected, RTIinternalError {
        this.logger.trace("enableAttributeScopeAdvisorySwitch");
        try {
        	this._rtiAmbassador.enableAttributeScopeAdvisorySwitch();
        } catch (AttributeScopeAdvisorySwitchIsOn | SaveInProgress | RestoreInProgress | FederateNotExecutionMember | NotConnected | RTIinternalError e) {
        	this.logger.error("enableAttributeScopeAdvisorySwitch exception=" + e.getMessage());
        	throw e;
        }
    }


    // 10.38
    public void disableAttributeScopeAdvisorySwitch() throws AttributeScopeAdvisorySwitchIsOff, SaveInProgress, RestoreInProgress, FederateNotExecutionMember, NotConnected, RTIinternalError {
        this.logger.trace("disableAttributeScopeAdvisorySwitch");
        try {
        	this._rtiAmbassador.disableAttributeScopeAdvisorySwitch();
        } catch (AttributeScopeAdvisorySwitchIsOff | SaveInProgress | RestoreInProgress | FederateNotExecutionMember | NotConnected | RTIinternalError e) {
        	this.logger.error("disableAttributeScopeAdvisorySwitch exception=" + e.getMessage());
        	throw e;
        }
    }


    // 10.39
    public void enableInteractionRelevanceAdvisorySwitch() throws InteractionRelevanceAdvisorySwitchIsOn, SaveInProgress, RestoreInProgress, FederateNotExecutionMember, NotConnected, RTIinternalError {
        this.logger.trace("enableInteractionRelevanceAdvisorySwitch");
        try {
        	this._rtiAmbassador.enableInteractionRelevanceAdvisorySwitch();
        } catch (InteractionRelevanceAdvisorySwitchIsOn | SaveInProgress | RestoreInProgress | FederateNotExecutionMember | NotConnected | RTIinternalError e) {
        	this.logger.error("enableInteractionRelevanceAdvisorySwitch exception=" + e.getMessage());
        	throw e;
        }
    }


    // 10.40
    public void disableInteractionRelevanceAdvisorySwitch() throws InteractionRelevanceAdvisorySwitchIsOff, SaveInProgress, RestoreInProgress, FederateNotExecutionMember, NotConnected, RTIinternalError {
        this.logger.trace("disableInteractionRelevanceAdvisorySwitch");
        try {
        	this._rtiAmbassador.disableInteractionRelevanceAdvisorySwitch();
        } catch (InteractionRelevanceAdvisorySwitchIsOff | SaveInProgress | RestoreInProgress | FederateNotExecutionMember | NotConnected | RTIinternalError e) {
        	this.logger.error("disableInteractionRelevanceAdvisorySwitch exception=" + e.getMessage());
        	throw e;
        }
    }


    // 10.41
    public boolean evokeCallback(final double approximateMinimumTimeInSeconds) throws CallNotAllowedFromWithinCallback, RTIinternalError {
        this.logger.trace("evokeCallback approximateMinimumTimeInSeconds=" + approximateMinimumTimeInSeconds);
    	try {
    		boolean b = this._rtiAmbassador.evokeCallback(approximateMinimumTimeInSeconds);
    		this.logger.trace("evokeCallback return " + b);
    		return b;
    	} catch (CallNotAllowedFromWithinCallback |  RTIinternalError e) {
    		this.logger.error("evokeCallback exception=" + e.getMessage());
    		throw e;
    	}
    }


    // 10.42
    public boolean evokeMultipleCallbacks(final double approximateMinimumTimeInSeconds, final double approximateMaximumTimeInSeconds) throws CallNotAllowedFromWithinCallback, RTIinternalError {
        this.logger.trace("evokeMultipleCallbacks approximateMinimumTimeInSeconds=" + approximateMinimumTimeInSeconds + ", approximateMaximumTimeInSeconds=" + approximateMaximumTimeInSeconds);
    	try {
    		boolean b = this._rtiAmbassador.evokeMultipleCallbacks(approximateMinimumTimeInSeconds, approximateMaximumTimeInSeconds);
    		this.logger.trace("evokeMultipleCallbacks return " + b);
    		return b;
    	} catch (CallNotAllowedFromWithinCallback |  RTIinternalError e) {
    		this.logger.error("evokeMultipleCallbacks exception=" + e.getMessage());
    		throw e;
    	}
    }


    // 10.43
    public void enableCallbacks() throws SaveInProgress, RestoreInProgress, RTIinternalError {
        this.logger.trace("enableCallbacks");
        try {
        	this._rtiAmbassador.enableCallbacks();
        } catch (SaveInProgress | RestoreInProgress | RTIinternalError e) {
        	this.logger.error("enableCallbacks exception=" + e.getMessage());
        	throw e;
        }
    }


    // 10.44
    public void disableCallbacks() throws SaveInProgress, RestoreInProgress, RTIinternalError {
        this.logger.trace("disableCallbacks");
        try {
        	this._rtiAmbassador.disableCallbacks();
        } catch (SaveInProgress | RestoreInProgress | RTIinternalError e) {
        	this.logger.error("disableCallbacks exception=" + e.getMessage());
        	throw e;
        }
    }


    //API-specific services
    public AttributeHandleFactory getAttributeHandleFactory() throws FederateNotExecutionMember, NotConnected {
        this.logger.trace("getAttributeHandleFactory");
        try {
        	return this._rtiAmbassador.getAttributeHandleFactory();
        } catch (FederateNotExecutionMember |  NotConnected e) {
        	this.logger.error("getAttributeHandleFactory exception=" + e.getMessage());
        	throw e;
        }
    }


    public AttributeHandleSetFactory getAttributeHandleSetFactory() throws FederateNotExecutionMember, NotConnected {
        this.logger.trace("getAttributeHandleSetFactory");
        try {
        	return this._rtiAmbassador.getAttributeHandleSetFactory();
        } catch (FederateNotExecutionMember | NotConnected e) {
        	this.logger.error("getAttributeHandleSetFactory exception=" + e.getMessage());
        	throw e;
        }
    }


    public AttributeHandleValueMapFactory getAttributeHandleValueMapFactory() throws FederateNotExecutionMember, NotConnected {
        this.logger.trace("getAttributeHandleValueMapFactory");
        try {
        	return this._rtiAmbassador.getAttributeHandleValueMapFactory();
        } catch (FederateNotExecutionMember | NotConnected e) {
        	this.logger.error("getAttributeHandleValueMapFactory exception=" + e.getMessage());
        	throw e;
        }
    }


    public AttributeSetRegionSetPairListFactory getAttributeSetRegionSetPairListFactory() throws FederateNotExecutionMember, NotConnected {
        this.logger.trace("getAttributeSetRegionSetPairListFactory");
        try {
        	return this._rtiAmbassador.getAttributeSetRegionSetPairListFactory();
        } catch (FederateNotExecutionMember | NotConnected e) {
        	this.logger.error("getAttributeSetRegionSetPairListFactory exception=" + e.getMessage());
        	throw e;
        }
    }


    public DimensionHandleFactory getDimensionHandleFactory() throws FederateNotExecutionMember, NotConnected {
        this.logger.trace("getDimensionHandleFactory");
        try {
        	return this._rtiAmbassador.getDimensionHandleFactory();
        } catch (FederateNotExecutionMember | NotConnected e) {
        	this.logger.error("getDimensionHandleFactory exception=" + e.getMessage());
        	throw e;
        }
    }


    public DimensionHandleSetFactory getDimensionHandleSetFactory() throws FederateNotExecutionMember, NotConnected {
        this.logger.trace("getDimensionHandleSetFactory");
        try {
        	return this._rtiAmbassador.getDimensionHandleSetFactory();
        } catch (FederateNotExecutionMember | NotConnected e) {
        	this.logger.error("getDimensionHandleSetFactory exception=" + e.getMessage());
        	throw e;
        }
    }


    public FederateHandleFactory getFederateHandleFactory() throws FederateNotExecutionMember, NotConnected {
        this.logger.trace("getFederateHandleFactory");
        try {
        	return this._rtiAmbassador.getFederateHandleFactory();
        } catch (FederateNotExecutionMember | NotConnected e) {
        	this.logger.error("getFederateHandleFactory exception=" + e.getMessage());
        	throw e;
        }
    }


    public FederateHandleSetFactory getFederateHandleSetFactory() throws FederateNotExecutionMember, NotConnected {
        this.logger.trace("getFederateHandleSetFactory");
        try {
        	return this._rtiAmbassador.getFederateHandleSetFactory();
        } catch (FederateNotExecutionMember | NotConnected e) {
        	this.logger.error("getFederateHandleSetFactory exception=" + e.getMessage());
        	throw e;
        }
    }


    public InteractionClassHandleFactory getInteractionClassHandleFactory() throws FederateNotExecutionMember, NotConnected {
        this.logger.trace("getInteractionClassHandleFactory");
        try {
        	return this._rtiAmbassador.getInteractionClassHandleFactory();
        } catch (FederateNotExecutionMember | NotConnected e) {
        	this.logger.error("getInteractionClassHandleFactory exception=" + e.getMessage());
        	throw e;
        }
    }


    public ObjectClassHandleFactory getObjectClassHandleFactory() throws FederateNotExecutionMember, NotConnected {
        this.logger.trace("getObjectClassHandleFactory");
        try {
        	return this._rtiAmbassador.getObjectClassHandleFactory();
        } catch (FederateNotExecutionMember | NotConnected e) {
        	this.logger.error("getObjectClassHandleFactory exception=" + e.getMessage());
        	throw e;
        }
    }


    public ObjectInstanceHandleFactory getObjectInstanceHandleFactory() throws FederateNotExecutionMember, NotConnected {
        this.logger.trace("getObjectInstanceHandleFactory");
        try {
        	return this._rtiAmbassador.getObjectInstanceHandleFactory();
        } catch (FederateNotExecutionMember | NotConnected e) {
        	this.logger.error("getObjectInstanceHandleFactory exception=" + e.getMessage());
        	throw e;
        }
    }


    public ParameterHandleFactory getParameterHandleFactory() throws FederateNotExecutionMember, NotConnected {
        this.logger.trace("getParameterHandleFactory");
        try {
        	return this._rtiAmbassador.getParameterHandleFactory();
        } catch (FederateNotExecutionMember | NotConnected e) {
        	this.logger.error("getParameterHandleFactory exception=" + e.getMessage());
        	throw e;
        }
    }


    public ParameterHandleValueMapFactory getParameterHandleValueMapFactory() throws FederateNotExecutionMember, NotConnected {
        this.logger.trace("getParameterHandleValueMapFactory");
        try {
        	return this._rtiAmbassador.getParameterHandleValueMapFactory();
        } catch (FederateNotExecutionMember | NotConnected e) {
        	this.logger.error("getParameterHandleValueMapFactory exception=" + e.getMessage());
        	throw e;
        }
    }


    public RegionHandleSetFactory getRegionHandleSetFactory() throws FederateNotExecutionMember, NotConnected {
        this.logger.trace("getRegionHandleSetFactory");
        try {
        	return this._rtiAmbassador.getRegionHandleSetFactory();
        } catch (FederateNotExecutionMember | NotConnected e) {
        	this.logger.error("getRegionHandleSetFactory exception=" + e.getMessage());
        	throw e;
        }
    }


    public TransportationTypeHandleFactory getTransportationTypeHandleFactory() throws FederateNotExecutionMember, NotConnected {
        this.logger.trace("getTransportationTypeHandleFactory");
        try {
        	return this._rtiAmbassador.getTransportationTypeHandleFactory();
        } catch (FederateNotExecutionMember | NotConnected e) {
        	this.logger.error("getTransportationTypeHandleFactory exception=" + e.getMessage());
        	throw e;
        }
    }


    public String getHLAversion() {
        this.logger.trace("getHLAversion");
        String str = this._rtiAmbassador.getHLAversion();
		this.logger.error("getHLAversion return " + str);
        return str;
    }


    public LogicalTimeFactory getTimeFactory() throws FederateNotExecutionMember, NotConnected {
        this.logger.trace("getTimeFactory");
        try {
        	return this._rtiAmbassador.getTimeFactory();
        } catch (FederateNotExecutionMember | NotConnected e) {
        	this.logger.error("getTimeFactory exception=" + e.getMessage());
        	throw e;
        }
    }
}
