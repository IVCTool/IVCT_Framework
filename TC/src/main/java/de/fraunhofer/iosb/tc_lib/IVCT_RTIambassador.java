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
    private Logger             LOGGER;
    private FederateAmbassador _fedAmbassador;
    private FederateHandle     myFederateHandle;


    /**
     * @param theRTIAmbassador reference to the rti ambassador
     * @param encoderFactory
     * @param LOGGER reference to the logger
     */
    public IVCT_RTIambassador(final RTIambassador theRTIAmbassador, final EncoderFactory encoderFactory, final Logger LOGGER) {
        this._rtiAmbassador = theRTIAmbassador;
        this.encoderFactory = encoderFactory;
        this.LOGGER = LOGGER;
        this.myFederateHandle = null;
    }


    /**
     * @return the encoder factory
     */
    public EncoderFactory getEncoderFactory() {
        return this.encoderFactory;
    }


    /**
     * @return value of the federate handle
     */
    public FederateHandle getMyFederateHandle() {
        return this.myFederateHandle;
    }


    /**
     * @param tcParam test parameters applying to the SuT
     * @param theFederateAmbassador the implementation of the federate
     * @return the federate handle
     */
    public FederateHandle initiateRti(final IVCT_TcParam tcParam, final FederateAmbassador theFederateAmbassador, final String federateName) {
        // Connect to rti
        try {
            this.connect(theFederateAmbassador, CallbackModel.HLA_IMMEDIATE, tcParam.getSettingsDesignator());
        }
        catch (ConnectionFailed | InvalidLocalSettingsDesignator | UnsupportedCallbackModel | AlreadyConnected | CallNotAllowedFromWithinCallback | RTIinternalError e) {
    		this.LOGGER.error("connect exception" + e.getMessage());
            return null;
        }

        // Create federation execution using tc_param foms
        try {
            this.createFederationExecution(tcParam.getFederationName(), tcParam.getUrls(), "HLAfloat64Time");
        }
        catch (final FederationExecutionAlreadyExists e) {
            this.LOGGER.warn("createFederationExecution: FederationExecutionAlreadyExists (ignored)");
        }
        catch (CouldNotCreateLogicalTimeFactory | InconsistentFDD | ErrorReadingFDD | CouldNotOpenFDD | NotConnected | RTIinternalError e) {
    		this.LOGGER.error("createFederationExecution exception" + e.getMessage());
            return null;
        }

        // Join federation execution
        try {
            return this.joinFederationExecution(federateName, tcParam.getFederationName(), tcParam.getUrls());
        }
        catch (CouldNotCreateLogicalTimeFactory | FederationExecutionDoesNotExist | InconsistentFDD | ErrorReadingFDD | CouldNotOpenFDD | SaveInProgress | RestoreInProgress | FederateAlreadyExecutionMember | NotConnected | CallNotAllowedFromWithinCallback | RTIinternalError e) {
    		this.LOGGER.error("joinFederationExecution exception" + e.getMessage());
        }

        return null;
    }


    /**
     * Terminate connection to rti
     *
     * @param tcParam test parameters applying to the SuT
     */
    public void terminateRti(final IVCT_TcParam tcParam) {
        // Resign federation execution
        try {
            this.resignFederationExecution(ResignAction.DELETE_OBJECTS_THEN_DIVEST);
        }
        catch (InvalidResignAction | OwnershipAcquisitionPending | FederateOwnsAttributes | FederateNotExecutionMember | NotConnected | CallNotAllowedFromWithinCallback | RTIinternalError e) {
    		this.LOGGER.warn("resignFederationExecution exception" + e.getMessage());
        }

        // Destroy federation execution
        try {
            this.destroyFederationExecution(tcParam.getFederationName());
        }
        catch (final FederatesCurrentlyJoined e1) {
            this.LOGGER.warn("terminateRti: FederatesCurrentlyJoined (ignored)");
        }
        catch (FederationExecutionDoesNotExist | NotConnected | RTIinternalError e) {
    		this.LOGGER.error("destroyFederationExecution exception" + e.getMessage());
        }

        // Disconnect from rti
        try {
            this.disconnect();
        }
        catch (FederateIsExecutionMember | CallNotAllowedFromWithinCallback | RTIinternalError e) {
    		this.LOGGER.error("disconnect exception" + e.getMessage());
        }
    }


    // 4.2
    /**
     * @param federateReference a reference to a user programmed callback
     * @param callbackModel the type of callback
     * @param localSettingsDesignator the settings for the rti
     */
    public void connect(final FederateAmbassador federateReference, final CallbackModel callbackModel, final String localSettingsDesignator) throws ConnectionFailed, InvalidLocalSettingsDesignator, UnsupportedCallbackModel, AlreadyConnected, CallNotAllowedFromWithinCallback, RTIinternalError {
		this.LOGGER.info("connect " + federateReference.toString() + " " + callbackModel.toString() + " "
				+ localSettingsDesignator);
		this._fedAmbassador = federateReference;
		try {
			this._rtiAmbassador.connect(federateReference, callbackModel, localSettingsDesignator);
		} catch (ConnectionFailed | InvalidLocalSettingsDesignator | UnsupportedCallbackModel | AlreadyConnected | CallNotAllowedFromWithinCallback | RTIinternalError e) {
			this.LOGGER.error("connect exception" + e.getMessage());
			throw e;
		}
    }


    // 4.2
    /**
     * @param federateReference a reference to a user programmed callback
     * @param callbackModel the type of callback
     */
    public void connect(final FederateAmbassador federateReference, final CallbackModel callbackModel) throws ConnectionFailed, InvalidLocalSettingsDesignator, UnsupportedCallbackModel, AlreadyConnected, CallNotAllowedFromWithinCallback, RTIinternalError {
        this.LOGGER.info("connect " + federateReference.toString() + " " + callbackModel.toString());
    	try {
        this._rtiAmbassador.connect(federateReference, callbackModel);
		} catch (ConnectionFailed | InvalidLocalSettingsDesignator | UnsupportedCallbackModel | AlreadyConnected | CallNotAllowedFromWithinCallback | RTIinternalError e) {
			this.LOGGER.error("connect exception" + e.getMessage());
			throw e;
		}
    }


    // 4.3
    /**
     * @throws FederateIsExecutionMember
     * @throws CallNotAllowedFromWithinCallback
     * @throws RTIinternalError
     */
    public void disconnect() throws FederateIsExecutionMember, CallNotAllowedFromWithinCallback, RTIinternalError {
        this.LOGGER.info("disconnect");
    	try {
        this._rtiAmbassador.disconnect();
		} catch (FederateIsExecutionMember | CallNotAllowedFromWithinCallback | RTIinternalError e) {
			this.LOGGER.error("disconnect exception" + e.getMessage());
			throw e;
		}
    }


    //4.5
    /**
     * @param federationExecutionName
     * @param fomModules
     * @param mimModule
     * @param logicalTimeImplementationName
     * @throws CouldNotCreateLogicalTimeFactory
     * @throws InconsistentFDD
     * @throws ErrorReadingFDD
     * @throws CouldNotOpenFDD
     * @throws ErrorReadingMIM
     * @throws CouldNotOpenMIM
     * @throws DesignatorIsHLAstandardMIM
     * @throws FederationExecutionAlreadyExists
     * @throws NotConnected
     * @throws RTIinternalError
     */
    public void createFederationExecution(final String federationExecutionName, final URL[] fomModules, final URL mimModule, final String logicalTimeImplementationName) throws CouldNotCreateLogicalTimeFactory, InconsistentFDD, ErrorReadingFDD, CouldNotOpenFDD, ErrorReadingMIM, CouldNotOpenMIM, DesignatorIsHLAstandardMIM, FederationExecutionAlreadyExists, NotConnected, RTIinternalError {
        this.LOGGER.info("createFederationExecution " + federationExecutionName + " " + Arrays.toString(fomModules) + " " + mimModule.toString() + " " + logicalTimeImplementationName);
    	try {
        this._rtiAmbassador.createFederationExecution(federationExecutionName, fomModules, mimModule, logicalTimeImplementationName);
		} catch (CouldNotCreateLogicalTimeFactory | InconsistentFDD | ErrorReadingFDD | CouldNotOpenFDD | ErrorReadingMIM | CouldNotOpenMIM | DesignatorIsHLAstandardMIM | FederationExecutionAlreadyExists | NotConnected | RTIinternalError e) {
			this.LOGGER.error("createFederationExecution exception" + e.getMessage());
			throw e;
		}
    }


    //4.5
    /**
     * @param federationExecutionName
     * @param fomModules
     * @param logicalTimeImplementationName
     * @throws CouldNotCreateLogicalTimeFactory
     * @throws InconsistentFDD
     * @throws ErrorReadingFDD
     * @throws CouldNotOpenFDD
     * @throws FederationExecutionAlreadyExists
     * @throws NotConnected
     * @throws RTIinternalError
     */
    public void createFederationExecution(final String federationExecutionName, final URL[] fomModules, final String logicalTimeImplementationName) throws CouldNotCreateLogicalTimeFactory, InconsistentFDD, ErrorReadingFDD, CouldNotOpenFDD, FederationExecutionAlreadyExists, NotConnected, RTIinternalError {
        this.LOGGER.info("createFederationExecution " + federationExecutionName + " " + Arrays.toString(fomModules) + " " + logicalTimeImplementationName);
    	try {
        this._rtiAmbassador.createFederationExecution(federationExecutionName, fomModules, logicalTimeImplementationName);
		} catch (CouldNotCreateLogicalTimeFactory | InconsistentFDD | ErrorReadingFDD | CouldNotOpenFDD | FederationExecutionAlreadyExists | NotConnected | RTIinternalError e) {
			this.LOGGER.error("createFederationExecution exception" + e.getMessage());
			throw e;
		}
    }


    //4.5
    public void createFederationExecution(final String federationExecutionName, final URL[] fomModules, final URL mimModule) throws InconsistentFDD, ErrorReadingFDD, CouldNotOpenFDD, ErrorReadingMIM, CouldNotOpenMIM, DesignatorIsHLAstandardMIM, FederationExecutionAlreadyExists, NotConnected, RTIinternalError {
        this.LOGGER.info("createFederationExecution " + federationExecutionName + " " + Arrays.toString(fomModules) + " " + mimModule.toString());
    	try {
        this._rtiAmbassador.createFederationExecution(federationExecutionName, fomModules, mimModule);
		} catch (InconsistentFDD | ErrorReadingFDD | CouldNotOpenFDD | ErrorReadingMIM | CouldNotOpenMIM | DesignatorIsHLAstandardMIM | FederationExecutionAlreadyExists | NotConnected | RTIinternalError e) {
			this.LOGGER.error("createFederationExecution exception" + e.getMessage());
			throw e;
		}
    }


    //4.5
    public void createFederationExecution(final String federationExecutionName, final URL[] fomModules) throws InconsistentFDD, ErrorReadingFDD, CouldNotOpenFDD, FederationExecutionAlreadyExists, NotConnected, RTIinternalError {
        this.LOGGER.info("createFederationExecution " + federationExecutionName + " " + Arrays.toString(fomModules));
    	try {
        this._rtiAmbassador.createFederationExecution(federationExecutionName, fomModules);
		} catch (InconsistentFDD | ErrorReadingFDD | CouldNotOpenFDD | FederationExecutionAlreadyExists | NotConnected | RTIinternalError e) {
			this.LOGGER.error("createFederationExecution exception" + e.getMessage());
			throw e;
		}
    }


    //4.5
    public void createFederationExecution(final String federationExecutionName, final URL fomModule) throws InconsistentFDD, ErrorReadingFDD, CouldNotOpenFDD, FederationExecutionAlreadyExists, NotConnected, RTIinternalError {
        this.LOGGER.info("createFederationExecution " + federationExecutionName + " " + fomModule.toString());
    	try {
        this._rtiAmbassador.createFederationExecution(federationExecutionName, fomModule);
		} catch (InconsistentFDD | ErrorReadingFDD | CouldNotOpenFDD | FederationExecutionAlreadyExists | NotConnected | RTIinternalError e) {
			this.LOGGER.error("createFederationExecution exception" + e.getMessage());
			throw e;
		}
    }


    //4.6
    public void destroyFederationExecution(final String federationExecutionName) throws FederatesCurrentlyJoined, FederationExecutionDoesNotExist, NotConnected, RTIinternalError {
        this.LOGGER.info("destroyFederationExecution " + federationExecutionName);
    	try {
        this._rtiAmbassador.destroyFederationExecution(federationExecutionName);
		} catch (FederatesCurrentlyJoined | FederationExecutionDoesNotExist | NotConnected | RTIinternalError e) {
			this.LOGGER.error("destroyFederationExecution exception" + e.getMessage());
			throw e;
		}
    }


    // 4.7
    public void listFederationExecutions() throws NotConnected, RTIinternalError {
        this.LOGGER.info("listFederationExecutions");
    	try {
        this._rtiAmbassador.listFederationExecutions();
		} catch (NotConnected | RTIinternalError e) {
			this.LOGGER.error("listFederationExecutions exception" + e.getMessage());
			throw e;
		}
    }


    //4.9
    public FederateHandle joinFederationExecution(final String federateName, final String federateType, final String federationExecutionName, final URL[] additionalFomModules) throws CouldNotCreateLogicalTimeFactory, FederateNameAlreadyInUse, FederationExecutionDoesNotExist, InconsistentFDD, ErrorReadingFDD, CouldNotOpenFDD, SaveInProgress, RestoreInProgress, FederateAlreadyExecutionMember, NotConnected, CallNotAllowedFromWithinCallback, RTIinternalError {
        this.LOGGER.info("joinFederationExecution " + federateName + " " + federateType + " " + federationExecutionName + " " + Arrays.toString(additionalFomModules));
    	try {
        this.myFederateHandle = this._rtiAmbassador.joinFederationExecution(federateName, federateType, federationExecutionName, additionalFomModules);
        this.LOGGER.info("joinFederationExecution return " + this.myFederateHandle.toString());
        return this.myFederateHandle;
		} catch (CouldNotCreateLogicalTimeFactory | FederateNameAlreadyInUse | FederationExecutionDoesNotExist | InconsistentFDD | ErrorReadingFDD | CouldNotOpenFDD | SaveInProgress | RestoreInProgress | FederateAlreadyExecutionMember | NotConnected | CallNotAllowedFromWithinCallback | RTIinternalError e) {
			this.LOGGER.error("joinFederationExecution exception" + e.getMessage());
			throw e;
		}
    }


    //4.9
    public FederateHandle joinFederationExecution(final String federateType, final String federationExecutionName, final URL[] additionalFomModules) throws CouldNotCreateLogicalTimeFactory, FederationExecutionDoesNotExist, InconsistentFDD, ErrorReadingFDD, CouldNotOpenFDD, SaveInProgress, RestoreInProgress, FederateAlreadyExecutionMember, NotConnected, CallNotAllowedFromWithinCallback, RTIinternalError {
    	this.LOGGER.info("joinFederationExecution " + federateType + " " + federationExecutionName + " " + Arrays.toString(additionalFomModules));
    	try {
    		final FederateHandle federateHandle = this._rtiAmbassador.joinFederationExecution(federateType, federationExecutionName, additionalFomModules);
    		this.LOGGER.info("joinFederationExecution return " + federateHandle.toString());
    		return federateHandle;
    	} catch (CouldNotCreateLogicalTimeFactory | FederationExecutionDoesNotExist | InconsistentFDD | ErrorReadingFDD | CouldNotOpenFDD | SaveInProgress | RestoreInProgress | FederateAlreadyExecutionMember | NotConnected | CallNotAllowedFromWithinCallback | RTIinternalError e) {
    		this.LOGGER.error("joinFederationExecution exception" + e.getMessage());
    		throw e;
    	}
    }


    //4.9
    public FederateHandle joinFederationExecution(final String federateName, final String federateType, final String federationExecutionName) throws CouldNotCreateLogicalTimeFactory, FederateNameAlreadyInUse, FederationExecutionDoesNotExist, SaveInProgress, RestoreInProgress, FederateAlreadyExecutionMember, NotConnected, CallNotAllowedFromWithinCallback, RTIinternalError {
    	this.LOGGER.info("joinFederationExecution " + federateName + " " + federateType + " " + federationExecutionName);
    	try {
    		final FederateHandle federateHandle = this._rtiAmbassador.joinFederationExecution(federateName, federateType, federationExecutionName);
    		this.LOGGER.info("joinFederationExecution return " + federateHandle.toString());
    		return federateHandle;
    	} catch (CouldNotCreateLogicalTimeFactory | FederateNameAlreadyInUse | FederationExecutionDoesNotExist | SaveInProgress | RestoreInProgress | FederateAlreadyExecutionMember | NotConnected | CallNotAllowedFromWithinCallback | RTIinternalError e) {
    		this.LOGGER.error("joinFederationExecution exception" + e.getMessage());
    		throw e;
    	}
    }


    //4.9
    public FederateHandle joinFederationExecution(final String federateType, final String federationExecutionName) throws CouldNotCreateLogicalTimeFactory, FederationExecutionDoesNotExist, SaveInProgress, RestoreInProgress, FederateAlreadyExecutionMember, NotConnected, CallNotAllowedFromWithinCallback, RTIinternalError {
    	this.LOGGER.info("joinFederationExecution " + federateType + " " + federationExecutionName);
    	try {
    		final FederateHandle federateHandle = this._rtiAmbassador.joinFederationExecution(federateType, federationExecutionName);
    		this.LOGGER.info("joinFederationExecution return " + federateHandle.toString());
    		return federateHandle;
    	} catch (CouldNotCreateLogicalTimeFactory | FederationExecutionDoesNotExist | SaveInProgress | RestoreInProgress | FederateAlreadyExecutionMember | NotConnected | CallNotAllowedFromWithinCallback | RTIinternalError e) {
    		this.LOGGER.error("joinFederationExecution exception" + e.getMessage());
    		throw e;
    	}
    }


    //4.10
    public void resignFederationExecution(final ResignAction resignAction) throws InvalidResignAction, OwnershipAcquisitionPending, FederateOwnsAttributes, FederateNotExecutionMember, NotConnected, CallNotAllowedFromWithinCallback, RTIinternalError {
        this.LOGGER.info("resignFederationExecution " + resignAction.toString());
    	try {
        this._rtiAmbassador.resignFederationExecution(resignAction);
    	} catch (InvalidResignAction | OwnershipAcquisitionPending | FederateOwnsAttributes | FederateNotExecutionMember | NotConnected | CallNotAllowedFromWithinCallback | RTIinternalError e) {
    		this.LOGGER.error("resignFederationExecution exception" + e.getMessage());
    		throw e;
    	}
    }


    //4.11
    public void registerFederationSynchronizationPoint(final String synchronizationPointLabel, final byte[] userSuppliedTag) throws SaveInProgress, RestoreInProgress, FederateNotExecutionMember, NotConnected, RTIinternalError {
        this.LOGGER.info("registerFederationSynchronizationPoint " + synchronizationPointLabel + " " + Arrays.toString(userSuppliedTag));
    	try {
        this._rtiAmbassador.registerFederationSynchronizationPoint(synchronizationPointLabel, userSuppliedTag);
    	} catch (SaveInProgress | RestoreInProgress | FederateNotExecutionMember | NotConnected | RTIinternalError e) {
    		this.LOGGER.error("registerFederationSynchronizationPoint exception" + e.getMessage());
    		throw e;
    	}
    }


    //4.11
    public void registerFederationSynchronizationPoint(final String synchronizationPointLabel, final byte[] userSuppliedTag, final FederateHandleSet synchronizationSet) throws InvalidFederateHandle, SaveInProgress, RestoreInProgress, FederateNotExecutionMember, NotConnected, RTIinternalError {
        this.LOGGER.info("registerFederationSynchronizationPoint " + synchronizationPointLabel + " " + Arrays.toString(userSuppliedTag) + " " + synchronizationSet.toString());
    	try {
        this._rtiAmbassador.registerFederationSynchronizationPoint(synchronizationPointLabel, userSuppliedTag, synchronizationSet);
    	} catch (InvalidFederateHandle | SaveInProgress | RestoreInProgress | FederateNotExecutionMember | NotConnected |  RTIinternalError e) {
    		this.LOGGER.error("registerFederationSynchronizationPoint exception" + e.getMessage());
    		throw e;
    	}
    }


    //4.14
    public void synchronizationPointAchieved(final String synchronizationPointLabel) throws SynchronizationPointLabelNotAnnounced, SaveInProgress, RestoreInProgress, FederateNotExecutionMember, NotConnected, RTIinternalError {
        this.LOGGER.info("synchronizationPointAchieved " + synchronizationPointLabel);
    	try {
        this._rtiAmbassador.synchronizationPointAchieved(synchronizationPointLabel);
    	} catch (SynchronizationPointLabelNotAnnounced | SaveInProgress | RestoreInProgress | FederateNotExecutionMember | NotConnected | RTIinternalError e) {
    		this.LOGGER.error("synchronizationPointAchieved exception" + e.getMessage());
    		throw e;
    	}
    }


    //4.14
    public void synchronizationPointAchieved(final String synchronizationPointLabel, final boolean successIndicator) throws SynchronizationPointLabelNotAnnounced, SaveInProgress, RestoreInProgress, FederateNotExecutionMember, NotConnected, RTIinternalError {
        this.LOGGER.info("synchronizationPointAchieved " + synchronizationPointLabel + " " + successIndicator);
    	try {
        this._rtiAmbassador.synchronizationPointAchieved(synchronizationPointLabel, successIndicator);
    	} catch (SynchronizationPointLabelNotAnnounced | SaveInProgress | RestoreInProgress | FederateNotExecutionMember | NotConnected | RTIinternalError e) {
    		this.LOGGER.error("synchronizationPointAchieved exception" + e.getMessage());
    		throw e;
    	}
    }


    // 4.16
    public void requestFederationSave(final String label) throws SaveInProgress, RestoreInProgress, FederateNotExecutionMember, NotConnected, RTIinternalError {
        this.LOGGER.info("requestFederationSave " + label);
    	try {
        this._rtiAmbassador.requestFederationSave(label);
    	} catch (SaveInProgress | RestoreInProgress | FederateNotExecutionMember | NotConnected | RTIinternalError e) {
    		this.LOGGER.error("requestFederationSave exception" + e.getMessage());
    		throw e;
    	}
    }


    // 4.16
    public void requestFederationSave(final String label, final LogicalTime theTime) throws LogicalTimeAlreadyPassed, InvalidLogicalTime, FederateUnableToUseTime, SaveInProgress, RestoreInProgress, FederateNotExecutionMember, NotConnected, RTIinternalError {
        this.LOGGER.info("requestFederationSave " + label + " " + theTime.toString());
    	try {
        this._rtiAmbassador.requestFederationSave(label, theTime);
    	} catch (LogicalTimeAlreadyPassed | InvalidLogicalTime | FederateUnableToUseTime | SaveInProgress | RestoreInProgress | FederateNotExecutionMember | NotConnected | RTIinternalError e) {
    		this.LOGGER.error("requestFederationSave exception" + e.getMessage());
    		throw e;
    	}
    }


    // 4.18
    public void federateSaveBegun() throws SaveNotInitiated, RestoreInProgress, FederateNotExecutionMember, NotConnected, RTIinternalError {
        this.LOGGER.info("federateSaveBegun");
    	try {
        this._rtiAmbassador.federateSaveBegun();
    	} catch (SaveNotInitiated | RestoreInProgress | FederateNotExecutionMember | NotConnected | RTIinternalError e) {
    		this.LOGGER.error("federateSaveBegun exception" + e.getMessage());
    		throw e;
    	}
    }


    // 4.19
    public void federateSaveComplete() throws FederateHasNotBegunSave, RestoreInProgress, FederateNotExecutionMember, NotConnected, RTIinternalError {
        this.LOGGER.info("federateSaveComplete");
    	try {
        this._rtiAmbassador.federateSaveComplete();
    	} catch (FederateHasNotBegunSave | RestoreInProgress | FederateNotExecutionMember | NotConnected | RTIinternalError e) {
    		this.LOGGER.error("federateSaveComplete exception" + e.getMessage());
    		throw e;
    	}
    }


    // 4.19
    public void federateSaveNotComplete() throws FederateHasNotBegunSave, RestoreInProgress, FederateNotExecutionMember, NotConnected, RTIinternalError {
        this.LOGGER.info("federateSaveNotComplete");
    	try {
        this._rtiAmbassador.federateSaveNotComplete();
    	} catch (FederateHasNotBegunSave | RestoreInProgress | FederateNotExecutionMember | NotConnected | RTIinternalError e) {
    		this.LOGGER.error("federateSaveNotComplete exception" + e.getMessage());
    		throw e;
    	}
    }


    // 4.21
    public void abortFederationSave() throws SaveNotInProgress, FederateNotExecutionMember, NotConnected, RTIinternalError {
        this.LOGGER.info("abortFederationSave");
    	try {
        this._rtiAmbassador.abortFederationSave();
    	} catch (SaveNotInProgress | FederateNotExecutionMember | NotConnected | RTIinternalError e) {
    		this.LOGGER.error("abortFederationSave exception" + e.getMessage());
    		throw e;
    	}
    }


    // 4.22
    public void queryFederationSaveStatus() throws RestoreInProgress, FederateNotExecutionMember, NotConnected, RTIinternalError {
        this.LOGGER.info("queryFederationSaveStatus");
    	try {
        this._rtiAmbassador.queryFederationSaveStatus();
    	} catch (RestoreInProgress | FederateNotExecutionMember | NotConnected | RTIinternalError e) {
    		this.LOGGER.error("queryFederationSaveStatus exception" + e.getMessage());
    		throw e;
    	}
    }


    // 4.24
    public void requestFederationRestore(final String label) throws SaveInProgress, RestoreInProgress, FederateNotExecutionMember, NotConnected, RTIinternalError {
        this.LOGGER.info("requestFederationRestore " + label);
    	try {
        this._rtiAmbassador.requestFederationRestore(label);
    	} catch (SaveInProgress | RestoreInProgress | FederateNotExecutionMember | NotConnected | RTIinternalError e) {
    		this.LOGGER.error("requestFederationRestore exception" + e.getMessage());
    		throw e;
    	}
    }


    // 4.28
    public void federateRestoreComplete() throws RestoreNotRequested, SaveInProgress, FederateNotExecutionMember, NotConnected, RTIinternalError {
        this.LOGGER.info("federateRestoreComplete");
    	try {
        this._rtiAmbassador.federateRestoreComplete();
    	} catch (RestoreNotRequested | SaveInProgress | FederateNotExecutionMember | NotConnected | RTIinternalError e) {
    		this.LOGGER.error("federateRestoreComplete exception" + e.getMessage());
    		throw e;
    	}
    }


    // 4.28
    public void federateRestoreNotComplete() throws RestoreNotRequested, SaveInProgress, FederateNotExecutionMember, NotConnected, RTIinternalError {
        this.LOGGER.info("federateRestoreNotComplete");
    	try {
        this._rtiAmbassador.federateRestoreNotComplete();
    	} catch (RestoreNotRequested | SaveInProgress | FederateNotExecutionMember | NotConnected | RTIinternalError e) {
    		this.LOGGER.error("federateRestoreNotComplete exception" + e.getMessage());
    		throw e;
    	}
    }


    // 4.30
    public void abortFederationRestore() throws RestoreNotInProgress, FederateNotExecutionMember, NotConnected, RTIinternalError {
        this.LOGGER.info("abortFederationRestore");
    	try {
        this._rtiAmbassador.abortFederationRestore();
    	} catch (RestoreNotInProgress | FederateNotExecutionMember | NotConnected | RTIinternalError e) {
    		this.LOGGER.error("abortFederationRestore exception" + e.getMessage());
    		throw e;
    	}
    }


    // 4.31
    public void queryFederationRestoreStatus() throws SaveInProgress, FederateNotExecutionMember, NotConnected, RTIinternalError {
        this.LOGGER.info("queryFederationRestoreStatus");
    	try {
        this._rtiAmbassador.queryFederationRestoreStatus();
    	} catch (SaveInProgress | FederateNotExecutionMember | NotConnected | RTIinternalError e) {
    		this.LOGGER.error("queryFederationRestoreStatus exception" + e.getMessage());
    		throw e;
    	}
    }

    /////////////////////////////////////
    // Declaration Management Services //
    /////////////////////////////////////


    // 5.2
    public void publishObjectClassAttributes(final ObjectClassHandle theClass, final AttributeHandleSet attributeList) throws AttributeNotDefined, ObjectClassNotDefined, SaveInProgress, RestoreInProgress, FederateNotExecutionMember, NotConnected, RTIinternalError {
        this.LOGGER.info("publishObjectClassAttributes " + theClass.toString() + " " + attributeList.toString());
    	try {
        this._rtiAmbassador.publishObjectClassAttributes(theClass, attributeList);
    	} catch (AttributeNotDefined | ObjectClassNotDefined | SaveInProgress | RestoreInProgress | FederateNotExecutionMember | NotConnected | RTIinternalError e) {
    		this.LOGGER.error("publishObjectClassAttributes exception" + e.getMessage());
    		throw e;
    	}
    }


    // 5.3
    public void unpublishObjectClass(final ObjectClassHandle theClass) throws OwnershipAcquisitionPending, ObjectClassNotDefined, SaveInProgress, RestoreInProgress, FederateNotExecutionMember, NotConnected, RTIinternalError {
        this.LOGGER.info("unpublishObjectClass " + theClass.toString());
    	try {
        this._rtiAmbassador.unpublishObjectClass(theClass);
    	} catch (OwnershipAcquisitionPending | ObjectClassNotDefined | SaveInProgress | RestoreInProgress | FederateNotExecutionMember | NotConnected | RTIinternalError e) {
    		this.LOGGER.error("unpublishObjectClass exception" + e.getMessage());
    		throw e;
    	}
    }


    // 5.3
    public void unpublishObjectClassAttributes(final ObjectClassHandle theClass, final AttributeHandleSet attributeList) throws OwnershipAcquisitionPending, AttributeNotDefined, ObjectClassNotDefined, SaveInProgress, RestoreInProgress, FederateNotExecutionMember, NotConnected, RTIinternalError {
        this.LOGGER.info("unpublishObjectClassAttributes " + theClass.toString() + " " + attributeList.toString());
    	try {
        this._rtiAmbassador.unpublishObjectClassAttributes(theClass, attributeList);
    	} catch (OwnershipAcquisitionPending | AttributeNotDefined | ObjectClassNotDefined | SaveInProgress | RestoreInProgress | FederateNotExecutionMember | NotConnected | RTIinternalError e) {
    		this.LOGGER.error("unpublishObjectClassAttributes exception" + e.getMessage());
    		throw e;
    	}
    }


    // 5.4
    public void publishInteractionClass(final InteractionClassHandle theInteraction) throws InteractionClassNotDefined, SaveInProgress, RestoreInProgress, FederateNotExecutionMember, NotConnected, RTIinternalError {
        this.LOGGER.info("publishInteractionClass " + theInteraction.toString());
    	try {
        this._rtiAmbassador.publishInteractionClass(theInteraction);
    	} catch (InteractionClassNotDefined | SaveInProgress | RestoreInProgress | FederateNotExecutionMember | NotConnected | RTIinternalError e) {
    		this.LOGGER.error("publishInteractionClass exception" + e.getMessage());
    		throw e;
    	}
    }


    // 5.5
    public void unpublishInteractionClass(final InteractionClassHandle theInteraction) throws InteractionClassNotDefined, SaveInProgress, RestoreInProgress, FederateNotExecutionMember, NotConnected, RTIinternalError {
        this.LOGGER.info("unpublishInteractionClass " + " " + theInteraction.toString());
    	try {
        this._rtiAmbassador.unpublishInteractionClass(theInteraction);
    	} catch (InteractionClassNotDefined | SaveInProgress | RestoreInProgress | FederateNotExecutionMember | NotConnected | RTIinternalError e) {
    		this.LOGGER.error("unpublishInteractionClass exception" + e.getMessage());
    		throw e;
    	}
    }


    // 5.6
    public void subscribeObjectClassAttributes(final ObjectClassHandle theClass, final AttributeHandleSet attributeList) throws AttributeNotDefined, ObjectClassNotDefined, SaveInProgress, RestoreInProgress, FederateNotExecutionMember, NotConnected, RTIinternalError {
        this.LOGGER.info("subscribeObjectClassAttributes " + theClass.toString() + attributeList.toString());
    	try {
        this._rtiAmbassador.subscribeObjectClassAttributes(theClass, attributeList);
    	} catch (AttributeNotDefined | ObjectClassNotDefined | SaveInProgress | RestoreInProgress | FederateNotExecutionMember | NotConnected | RTIinternalError e) {
    		this.LOGGER.error("subscribeObjectClassAttributes exception" + e.getMessage());
    		throw e;
    	}
    }


    // 5.6
    public void subscribeObjectClassAttributes(final ObjectClassHandle theClass, final AttributeHandleSet attributeList, final String updateRateDesignator) throws AttributeNotDefined, ObjectClassNotDefined, InvalidUpdateRateDesignator, SaveInProgress, RestoreInProgress, FederateNotExecutionMember, NotConnected, RTIinternalError {
        this.LOGGER.info("subscribeObjectClassAttributes " + theClass.toString() + attributeList.toString() + " " + updateRateDesignator);
    	try {
        this._rtiAmbassador.subscribeObjectClassAttributes(theClass, attributeList, updateRateDesignator);
    	} catch (AttributeNotDefined | ObjectClassNotDefined | InvalidUpdateRateDesignator | SaveInProgress | RestoreInProgress | FederateNotExecutionMember | NotConnected | RTIinternalError e) {
    		this.LOGGER.error("subscribeObjectClassAttributes exception" + e.getMessage());
    		throw e;
    	}
    }


    // 5.6
    public void subscribeObjectClassAttributesPassively(final ObjectClassHandle theClass, final AttributeHandleSet attributeList) throws AttributeNotDefined, ObjectClassNotDefined, SaveInProgress, RestoreInProgress, FederateNotExecutionMember, NotConnected, RTIinternalError {
        this.LOGGER.info("subscribeObjectClassAttributesPassively " + theClass.toString() + attributeList.toString());
    	try {
        this._rtiAmbassador.subscribeObjectClassAttributesPassively(theClass, attributeList);
    	} catch (AttributeNotDefined | ObjectClassNotDefined | SaveInProgress | RestoreInProgress | FederateNotExecutionMember | NotConnected | RTIinternalError e) {
    		this.LOGGER.error("subscribeObjectClassAttributesPassively exception" + e.getMessage());
    		throw e;
    	}
    }


    // 5.6
    public void subscribeObjectClassAttributesPassively(final ObjectClassHandle theClass, final AttributeHandleSet attributeList, final String updateRateDesignator) throws AttributeNotDefined, ObjectClassNotDefined, InvalidUpdateRateDesignator, SaveInProgress, RestoreInProgress, FederateNotExecutionMember, NotConnected, RTIinternalError {
    	this.LOGGER.info("subscribeObjectClassAttributesPassively " + theClass.toString() + attributeList.toString() + " " + updateRateDesignator);
    	try {
    		this._rtiAmbassador.subscribeObjectClassAttributesPassively(theClass, attributeList, updateRateDesignator);
    	} catch (AttributeNotDefined | ObjectClassNotDefined | InvalidUpdateRateDesignator | SaveInProgress | RestoreInProgress | FederateNotExecutionMember | NotConnected | RTIinternalError e) {
    		this.LOGGER.error("subscribeObjectClassAttributesPassively exception" + e.getMessage());
    		throw e;
    	}
    }


    // 5.7
    public void unsubscribeObjectClass(final ObjectClassHandle theClass) throws ObjectClassNotDefined, SaveInProgress, RestoreInProgress, FederateNotExecutionMember, NotConnected, RTIinternalError {
        this.LOGGER.info("unsubscribeObjectClass " + theClass.toString());
    	try {
        this._rtiAmbassador.unsubscribeObjectClass(theClass);
    	} catch (ObjectClassNotDefined | SaveInProgress | RestoreInProgress | FederateNotExecutionMember | NotConnected | RTIinternalError e) {
    		this.LOGGER.error("unsubscribeObjectClass exception" + e.getMessage());
    		throw e;
    	}
    }


    // 5.7
    public void unsubscribeObjectClassAttributes(final ObjectClassHandle theClass, final AttributeHandleSet attributeList) throws AttributeNotDefined, ObjectClassNotDefined, SaveInProgress, RestoreInProgress, FederateNotExecutionMember, NotConnected, RTIinternalError {
        this.LOGGER.info("unsubscribeObjectClassAttributes " + theClass.toString() + " " + attributeList.toString());
    	try {
        this._rtiAmbassador.unsubscribeObjectClassAttributes(theClass, attributeList);
    	} catch (AttributeNotDefined | ObjectClassNotDefined | SaveInProgress | RestoreInProgress | FederateNotExecutionMember | NotConnected | RTIinternalError e) {
    		this.LOGGER.error("unsubscribeObjectClassAttributes exception" + e.getMessage());
    		throw e;
    	}
    }


    // 5.8
    public void subscribeInteractionClass(final InteractionClassHandle theClass) throws FederateServiceInvocationsAreBeingReportedViaMOM, InteractionClassNotDefined, SaveInProgress, RestoreInProgress, FederateNotExecutionMember, NotConnected, RTIinternalError {
    	this.LOGGER.info("subscribeInteractionClass " + theClass.toString());
    	try {
    		this._rtiAmbassador.subscribeInteractionClass(theClass);
    	} catch (FederateServiceInvocationsAreBeingReportedViaMOM | InteractionClassNotDefined | SaveInProgress | RestoreInProgress | FederateNotExecutionMember | NotConnected | RTIinternalError e) {
    		this.LOGGER.error("subscribeInteractionClass exception" + e.getMessage());
    		throw e;
    	}
    }


    // 5.8
    public void subscribeInteractionClassPassively(final InteractionClassHandle theClass) throws FederateServiceInvocationsAreBeingReportedViaMOM, InteractionClassNotDefined, SaveInProgress, RestoreInProgress, FederateNotExecutionMember, NotConnected, RTIinternalError {
    	this.LOGGER.info("subscribeInteractionClassPassively " + theClass.toString());
    	try {
    		this._rtiAmbassador.subscribeInteractionClassPassively(theClass);
    	} catch (FederateServiceInvocationsAreBeingReportedViaMOM | InteractionClassNotDefined | SaveInProgress | RestoreInProgress | FederateNotExecutionMember | NotConnected | RTIinternalError e) {
    		this.LOGGER.error("subscribeInteractionClassPassively exception" + e.getMessage());
    		throw e;
    	}
    }


    // 5.9
    public void unsubscribeInteractionClass(final InteractionClassHandle theClass) throws InteractionClassNotDefined, SaveInProgress, RestoreInProgress, FederateNotExecutionMember, NotConnected, RTIinternalError {
    	this.LOGGER.info("unsubscribeInteractionClass " + theClass.toString());
    	try {
    		this._rtiAmbassador.unsubscribeInteractionClass(theClass);
    	} catch (InteractionClassNotDefined | SaveInProgress | RestoreInProgress | FederateNotExecutionMember | NotConnected | RTIinternalError e) {
    		this.LOGGER.error("unsubscribeInteractionClass exception" + e.getMessage());
    		throw e;
    	}
    }

    ////////////////////////////////
    // Object Management Services //
    ////////////////////////////////


    // 6.2
    public void reserveObjectInstanceName(final String theObjectName) throws IllegalName, SaveInProgress, RestoreInProgress, FederateNotExecutionMember, NotConnected, RTIinternalError {
        this.LOGGER.info("reserveObjectInstanceName " + theObjectName);
    	try {
        this._rtiAmbassador.reserveObjectInstanceName(theObjectName);
    	} catch (IllegalName | SaveInProgress | RestoreInProgress | FederateNotExecutionMember | NotConnected | RTIinternalError e) {
    		this.LOGGER.error("reserveObjectInstanceName exception" + e.getMessage());
    		throw e;
    	}
    }


    // 6.4
    public void releaseObjectInstanceName(final String theObjectInstanceName) throws ObjectInstanceNameNotReserved, SaveInProgress, RestoreInProgress, FederateNotExecutionMember, NotConnected, RTIinternalError {
        this.LOGGER.info("releaseObjectInstanceName " + theObjectInstanceName);
    	try {
        this._rtiAmbassador.releaseObjectInstanceName(theObjectInstanceName);
    	} catch (ObjectInstanceNameNotReserved | SaveInProgress | RestoreInProgress | FederateNotExecutionMember | NotConnected | RTIinternalError e) {
    		this.LOGGER.error("releaseObjectInstanceName exception" + e.getMessage());
    		throw e;
    	}
    }


    // 6.5
    public void reserveMultipleObjectInstanceName(final Set<String> theObjectNames) throws IllegalName, NameSetWasEmpty, SaveInProgress, RestoreInProgress, FederateNotExecutionMember, NotConnected, RTIinternalError {
        this.LOGGER.info("reserveMultipleObjectInstanceName " + theObjectNames.toString());
    	try {
        this._rtiAmbassador.reserveMultipleObjectInstanceName(theObjectNames);
    	} catch (IllegalName | NameSetWasEmpty | SaveInProgress | RestoreInProgress | FederateNotExecutionMember | NotConnected | RTIinternalError e) {
    		this.LOGGER.error("reserveMultipleObjectInstanceName exception" + e.getMessage());
    		throw e;
    	}
    }


    // 6.7
    public void releaseMultipleObjectInstanceName(final Set<String> theObjectNames) throws ObjectInstanceNameNotReserved, SaveInProgress, RestoreInProgress, FederateNotExecutionMember, NotConnected, RTIinternalError {
        this.LOGGER.info("releaseMultipleObjectInstanceName " + theObjectNames.toString());
    	try {
        this._rtiAmbassador.releaseMultipleObjectInstanceName(theObjectNames);
    	} catch (ObjectInstanceNameNotReserved | SaveInProgress | RestoreInProgress | FederateNotExecutionMember | NotConnected | RTIinternalError e) {
    		this.LOGGER.error("releaseMultipleObjectInstanceName exception" + e.getMessage());
    		throw e;
    	}
    }


    // 6.8
    public ObjectInstanceHandle registerObjectInstance(final ObjectClassHandle theClass) throws ObjectClassNotPublished, ObjectClassNotDefined, SaveInProgress, RestoreInProgress, FederateNotExecutionMember, NotConnected, RTIinternalError {
    	this.LOGGER.info("registerObjectInstance " + theClass.toString());
    	try {
    		final ObjectInstanceHandle objectInstanceHandle = this._rtiAmbassador.registerObjectInstance(theClass);
    		this.LOGGER.info("registerObjectInstance return " + objectInstanceHandle.toString());
    		return objectInstanceHandle;
    	} catch (ObjectClassNotPublished | ObjectClassNotDefined | SaveInProgress | RestoreInProgress | FederateNotExecutionMember | NotConnected | RTIinternalError e) {
    		this.LOGGER.error("registerObjectInstance exception" + e.getMessage());
    		throw e;
    	}
    }


    // 6.8
    public ObjectInstanceHandle registerObjectInstance(final ObjectClassHandle theClass, final String theObjectName) throws ObjectInstanceNameInUse, ObjectInstanceNameNotReserved, ObjectClassNotPublished, ObjectClassNotDefined, SaveInProgress, RestoreInProgress, FederateNotExecutionMember, NotConnected, RTIinternalError {
        this.LOGGER.info("registerObjectInstance " + theClass.toString() + " " + theObjectName);
    	try {
        final ObjectInstanceHandle objectInstanceHandle = this._rtiAmbassador.registerObjectInstance(theClass, theObjectName);
        this.LOGGER.info("registerObjectInstance return " + objectInstanceHandle.toString());
        return objectInstanceHandle;
    	} catch (ObjectInstanceNameInUse | ObjectInstanceNameNotReserved | ObjectClassNotPublished | ObjectClassNotDefined | SaveInProgress | RestoreInProgress | FederateNotExecutionMember | NotConnected | RTIinternalError e) {
    		this.LOGGER.error("registerObjectInstance exception" + e.getMessage());
    		throw e;
    	}
    }


    // 6.10
    public void updateAttributeValues(final ObjectInstanceHandle theObject, final AttributeHandleValueMap theAttributes, final byte[] userSuppliedTag) throws AttributeNotOwned, AttributeNotDefined, ObjectInstanceNotKnown, SaveInProgress, RestoreInProgress, FederateNotExecutionMember, NotConnected, RTIinternalError {
        this.LOGGER.info("updateAttributeValues " + theObject.toString() + " " + theAttributes.toString() + " " + Arrays.toString(userSuppliedTag));
    	try {
        this._rtiAmbassador.updateAttributeValues(theObject, theAttributes, userSuppliedTag);
    	} catch (AttributeNotOwned | AttributeNotDefined | ObjectInstanceNotKnown | SaveInProgress | RestoreInProgress | FederateNotExecutionMember | NotConnected | RTIinternalError e) {
    		this.LOGGER.error("updateAttributeValues exception" + e.getMessage());
    		throw e;
    	}
    }


    // 6.10
    public MessageRetractionReturn updateAttributeValues(final ObjectInstanceHandle theObject, final AttributeHandleValueMap theAttributes, final byte[] userSuppliedTag, final LogicalTime theTime) throws InvalidLogicalTime, AttributeNotOwned, AttributeNotDefined, ObjectInstanceNotKnown, SaveInProgress, RestoreInProgress, FederateNotExecutionMember, NotConnected, RTIinternalError {
        this.LOGGER.info("updateAttributeValues " + theObject.toString() + " " + theAttributes.toString() + " " + Arrays.toString(userSuppliedTag) + " " + theTime.toString());
    	try {
        final MessageRetractionReturn messageRetractionReturn = this._rtiAmbassador.updateAttributeValues(theObject, theAttributes, userSuppliedTag, theTime);
        this.LOGGER.info("updateAttributeValues return " + messageRetractionReturn.toString());
        return messageRetractionReturn;
    	} catch (InvalidLogicalTime | AttributeNotOwned | AttributeNotDefined | ObjectInstanceNotKnown | SaveInProgress | RestoreInProgress | FederateNotExecutionMember | NotConnected | RTIinternalError e) {
    		this.LOGGER.error("updateAttributeValues exception" + e.getMessage());
    		throw e;
    	}
    }


    // 6.12
    public void sendInteraction(final InteractionClassHandle theInteraction, final ParameterHandleValueMap theParameters, final byte[] userSuppliedTag) throws InteractionClassNotPublished, InteractionParameterNotDefined, InteractionClassNotDefined, SaveInProgress, RestoreInProgress, FederateNotExecutionMember, NotConnected, RTIinternalError {
        this.LOGGER.info("sendInteraction " + theInteraction.toString() + " " + theParameters.toString() + " " + Arrays.toString(userSuppliedTag));
    	try {
        this._rtiAmbassador.sendInteraction(theInteraction, theParameters, userSuppliedTag);
    	} catch (InteractionClassNotPublished | InteractionParameterNotDefined | InteractionClassNotDefined | SaveInProgress | RestoreInProgress | FederateNotExecutionMember | NotConnected | RTIinternalError e) {
    		this.LOGGER.error("sendInteraction exception" + e.getMessage());
    		throw e;
    	}
    }


    // 6.12
    public MessageRetractionReturn sendInteraction(final InteractionClassHandle theInteraction, final ParameterHandleValueMap theParameters, final byte[] userSuppliedTag, final LogicalTime theTime) throws InvalidLogicalTime, InteractionClassNotPublished, InteractionParameterNotDefined, InteractionClassNotDefined, SaveInProgress, RestoreInProgress, FederateNotExecutionMember, NotConnected, RTIinternalError {
        this.LOGGER.info("sendInteraction " + theInteraction.toString() + " " + theParameters.toString() + " " + Arrays.toString(userSuppliedTag) + " " + theTime.toString());
    	try {
        final MessageRetractionReturn messageRetractionReturn = this._rtiAmbassador.sendInteraction(theInteraction, theParameters, userSuppliedTag, theTime);
        this.LOGGER.info("sendInteraction return " + messageRetractionReturn.toString());
        return messageRetractionReturn;
    	} catch (InvalidLogicalTime | InteractionClassNotPublished | InteractionParameterNotDefined | InteractionClassNotDefined | SaveInProgress | RestoreInProgress | FederateNotExecutionMember | NotConnected | RTIinternalError e) {
    		this.LOGGER.error("sendInteraction exception" + e.getMessage());
    		throw e;
    	}
    }


    // 6.14
    public void deleteObjectInstance(final ObjectInstanceHandle objectHandle, final byte[] userSuppliedTag) throws DeletePrivilegeNotHeld, ObjectInstanceNotKnown, SaveInProgress, RestoreInProgress, FederateNotExecutionMember, NotConnected, RTIinternalError {
        this.LOGGER.info("deleteObjectInstance " + objectHandle.toString() + " " + Arrays.toString(userSuppliedTag));
    	try {
        this._rtiAmbassador.deleteObjectInstance(objectHandle, userSuppliedTag);
    	} catch (DeletePrivilegeNotHeld | ObjectInstanceNotKnown | SaveInProgress | RestoreInProgress | FederateNotExecutionMember | NotConnected | RTIinternalError e) {
    		this.LOGGER.error("deleteObjectInstance exception" + e.getMessage());
    		throw e;
    	}
    }


    // 6.14
    public MessageRetractionReturn deleteObjectInstance(final ObjectInstanceHandle objectHandle, final byte[] userSuppliedTag, final LogicalTime theTime) throws InvalidLogicalTime, DeletePrivilegeNotHeld, ObjectInstanceNotKnown, SaveInProgress, RestoreInProgress, FederateNotExecutionMember, NotConnected, RTIinternalError {
        this.LOGGER.info("deleteObjectInstance " + objectHandle.toString() + " " + Arrays.toString(userSuppliedTag) + " " + theTime.toString());
    	try {
        final MessageRetractionReturn messageRetractionReturn = this._rtiAmbassador.deleteObjectInstance(objectHandle, userSuppliedTag, theTime);
        this.LOGGER.info("deleteObjectInstance return " + messageRetractionReturn.toString());
        return messageRetractionReturn;
    	} catch (InvalidLogicalTime | DeletePrivilegeNotHeld | ObjectInstanceNotKnown | SaveInProgress | RestoreInProgress | FederateNotExecutionMember | NotConnected | RTIinternalError e) {
    		this.LOGGER.error("deleteObjectInstance exception" + e.getMessage());
    		throw e;
    	}
    }


    // 6.16
    public void localDeleteObjectInstance(final ObjectInstanceHandle objectHandle) throws OwnershipAcquisitionPending, FederateOwnsAttributes, ObjectInstanceNotKnown, SaveInProgress, RestoreInProgress, FederateNotExecutionMember, NotConnected, RTIinternalError {
        this.LOGGER.info("localDeleteObjectInstance " + objectHandle.toString());
    	try {
        this._rtiAmbassador.localDeleteObjectInstance(objectHandle);
    	} catch (OwnershipAcquisitionPending | FederateOwnsAttributes | ObjectInstanceNotKnown | SaveInProgress | RestoreInProgress | FederateNotExecutionMember | NotConnected | RTIinternalError e) {
    		this.LOGGER.error("localDeleteObjectInstance exception" + e.getMessage());
    		throw e;
    	}
    }


    // 6.19
    public void requestAttributeValueUpdate(final ObjectInstanceHandle theObject, final AttributeHandleSet theAttributes, final byte[] userSuppliedTag) throws AttributeNotDefined, ObjectInstanceNotKnown, SaveInProgress, RestoreInProgress, FederateNotExecutionMember, NotConnected, RTIinternalError {
        this.LOGGER.info("requestAttributeValueUpdate " + theObject.toString() + " " + theAttributes.toString() + " " + Arrays.toString(userSuppliedTag));
    	try {
        this._rtiAmbassador.requestAttributeValueUpdate(theObject, theAttributes, userSuppliedTag);
    	} catch (AttributeNotDefined | ObjectInstanceNotKnown | SaveInProgress | RestoreInProgress | FederateNotExecutionMember | NotConnected | RTIinternalError e) {
    		this.LOGGER.error("requestAttributeValueUpdate exception" + e.getMessage());
    		throw e;
    	}
    }


    // 6.19
    public void requestAttributeValueUpdate(final ObjectClassHandle theClass, final AttributeHandleSet theAttributes, final byte[] userSuppliedTag) throws AttributeNotDefined, ObjectClassNotDefined, SaveInProgress, RestoreInProgress, FederateNotExecutionMember, NotConnected, RTIinternalError {
        this.LOGGER.info("requestAttributeValueUpdate " + theClass.toString() + " " + theAttributes.toString() + " " + Arrays.toString(userSuppliedTag));
    	try {
        this._rtiAmbassador.requestAttributeValueUpdate(theClass, theAttributes, userSuppliedTag);
    	} catch (AttributeNotDefined | ObjectClassNotDefined | SaveInProgress | RestoreInProgress | FederateNotExecutionMember | NotConnected | RTIinternalError e) {
    		this.LOGGER.error("requestAttributeValueUpdate exception" + e.getMessage());
    		throw e;
    	}
    }


    // 6.23
    public void requestAttributeTransportationTypeChange(final ObjectInstanceHandle theObject, final AttributeHandleSet theAttributes, final TransportationTypeHandle theType) throws AttributeAlreadyBeingChanged, AttributeNotOwned, AttributeNotDefined, ObjectInstanceNotKnown, InvalidTransportationType, SaveInProgress, RestoreInProgress, FederateNotExecutionMember, NotConnected, RTIinternalError {
        this.LOGGER.info("requestAttributeTransportationTypeChange " + theObject.toString() + " " + theAttributes.toString() + " " + theType.toString());
    	try {
        this._rtiAmbassador.requestAttributeTransportationTypeChange(theObject, theAttributes, theType);
    	} catch (AttributeAlreadyBeingChanged | AttributeNotOwned | AttributeNotDefined | ObjectInstanceNotKnown | InvalidTransportationType | SaveInProgress | RestoreInProgress | FederateNotExecutionMember | NotConnected | RTIinternalError e) {
    		this.LOGGER.error("requestAttributeTransportationTypeChange exception" + e.getMessage());
    		throw e;
    	}
    }


    // 6.25
    public void queryAttributeTransportationType(final ObjectInstanceHandle theObject, final AttributeHandle theAttribute) throws AttributeNotDefined, ObjectInstanceNotKnown, SaveInProgress, RestoreInProgress, FederateNotExecutionMember, NotConnected, RTIinternalError {
        this.LOGGER.info("queryAttributeTransportationType " + theObject.toString() + " " + theAttribute.toString());
    	try {
        this._rtiAmbassador.queryAttributeTransportationType(theObject, theAttribute);
    	} catch (AttributeNotDefined | ObjectInstanceNotKnown | SaveInProgress | RestoreInProgress | FederateNotExecutionMember | NotConnected | RTIinternalError e) {
    		this.LOGGER.error("queryAttributeTransportationType exception" + e.getMessage());
    		throw e;
    	}
    }


    // 6.27
    public void requestInteractionTransportationTypeChange(final InteractionClassHandle theClass, final TransportationTypeHandle theType) throws InteractionClassAlreadyBeingChanged, InteractionClassNotPublished, InteractionClassNotDefined, InvalidTransportationType, SaveInProgress, RestoreInProgress, FederateNotExecutionMember, NotConnected, RTIinternalError {
        this.LOGGER.info("requestInteractionTransportationTypeChange " + theClass.toString() + " " + theType.toString());
    	try {
        this._rtiAmbassador.requestInteractionTransportationTypeChange(theClass, theType);
    	} catch (InteractionClassAlreadyBeingChanged | InteractionClassNotPublished | InteractionClassNotDefined | InvalidTransportationType | SaveInProgress | RestoreInProgress | FederateNotExecutionMember | NotConnected | RTIinternalError e) {
    		this.LOGGER.error("requestInteractionTransportationTypeChange exception" + e.getMessage());
    		throw e;
    	}
    }


    // 6.29
    public void queryInteractionTransportationType(final FederateHandle theFederate, final InteractionClassHandle theInteraction) throws InteractionClassNotDefined, SaveInProgress, RestoreInProgress, FederateNotExecutionMember, NotConnected, RTIinternalError {
        this.LOGGER.info("queryInteractionTransportationType " + theFederate.toString() + " " + theInteraction.toString());
    	try {
        this._rtiAmbassador.queryInteractionTransportationType(theFederate, theInteraction);
    	} catch (InteractionClassNotDefined | SaveInProgress | RestoreInProgress | FederateNotExecutionMember | NotConnected | RTIinternalError e) {
    		this.LOGGER.error("queryInteractionTransportationType exception" + e.getMessage());
    		throw e;
    	}
    }

    ///////////////////////////////////
    // Ownership Management Services //
    ///////////////////////////////////


    // 7.2
    public void unconditionalAttributeOwnershipDivestiture(final ObjectInstanceHandle theObject, final AttributeHandleSet theAttributes) throws AttributeNotOwned, AttributeNotDefined, ObjectInstanceNotKnown, SaveInProgress, RestoreInProgress, FederateNotExecutionMember, NotConnected, RTIinternalError {
        this.LOGGER.info("unconditionalAttributeOwnershipDivestiture " + theObject.toString() + " " + theAttributes.toString());
    	try {
        this._rtiAmbassador.unconditionalAttributeOwnershipDivestiture(theObject, theAttributes);
    	} catch (AttributeNotOwned | AttributeNotDefined | ObjectInstanceNotKnown | SaveInProgress | RestoreInProgress | FederateNotExecutionMember | NotConnected | RTIinternalError e) {
    		this.LOGGER.error("unconditionalAttributeOwnershipDivestiture exception" + e.getMessage());
    		throw e;
    	}
    }


    // 7.3
    public void negotiatedAttributeOwnershipDivestiture(final ObjectInstanceHandle theObject, final AttributeHandleSet theAttributes, final byte[] userSuppliedTag) throws AttributeAlreadyBeingDivested, AttributeNotOwned, AttributeNotDefined, ObjectInstanceNotKnown, SaveInProgress, RestoreInProgress, FederateNotExecutionMember, NotConnected, RTIinternalError {
        this.LOGGER.info("negotiatedAttributeOwnershipDivestiture " + theObject.toString() + " " + theAttributes.toString() + " " + Arrays.toString(userSuppliedTag));
    	try {
        this._rtiAmbassador.negotiatedAttributeOwnershipDivestiture(theObject, theAttributes, userSuppliedTag);
    	} catch (AttributeAlreadyBeingDivested | AttributeNotOwned | AttributeNotDefined | ObjectInstanceNotKnown | SaveInProgress | RestoreInProgress | FederateNotExecutionMember | NotConnected | RTIinternalError e) {
    		this.LOGGER.error("negotiatedAttributeOwnershipDivestiture exception" + e.getMessage());
    		throw e;
    	}
    }


    // 7.6
    public void confirmDivestiture(final ObjectInstanceHandle theObject, final AttributeHandleSet theAttributes, final byte[] userSuppliedTag) throws NoAcquisitionPending, AttributeDivestitureWasNotRequested, AttributeNotOwned, AttributeNotDefined, ObjectInstanceNotKnown, SaveInProgress, RestoreInProgress, FederateNotExecutionMember, NotConnected, RTIinternalError {
        this.LOGGER.info("confirmDivestiture " + theObject.toString() + " " + theAttributes.toString() + " " + Arrays.toString(userSuppliedTag));
    	try {
        this._rtiAmbassador.confirmDivestiture(theObject, theAttributes, userSuppliedTag);
    	} catch (NoAcquisitionPending | AttributeDivestitureWasNotRequested | AttributeNotOwned | AttributeNotDefined | ObjectInstanceNotKnown | SaveInProgress | RestoreInProgress | FederateNotExecutionMember | NotConnected | RTIinternalError e) {
    		this.LOGGER.error("confirmDivestiture exception" + e.getMessage());
    		throw e;
    	}
    }


    // 7.8
    public void attributeOwnershipAcquisition(final ObjectInstanceHandle theObject, final AttributeHandleSet desiredAttributes, final byte[] userSuppliedTag) throws AttributeNotPublished, ObjectClassNotPublished, FederateOwnsAttributes, AttributeNotDefined, ObjectInstanceNotKnown, SaveInProgress, RestoreInProgress, FederateNotExecutionMember, NotConnected, RTIinternalError {
        this.LOGGER.info("attributeOwnershipAcquisition " + theObject.toString() + " " + desiredAttributes.toString() + " " + Arrays.toString(userSuppliedTag));
    	try {
        this._rtiAmbassador.attributeOwnershipAcquisition(theObject, desiredAttributes, userSuppliedTag);
    	} catch (AttributeNotPublished | ObjectClassNotPublished | FederateOwnsAttributes | AttributeNotDefined | ObjectInstanceNotKnown | SaveInProgress | RestoreInProgress | FederateNotExecutionMember | NotConnected | RTIinternalError e) {
    		this.LOGGER.error("attributeOwnershipAcquisition exception" + e.getMessage());
    		throw e;
    	}
    }


    // 7.9
    public void attributeOwnershipAcquisitionIfAvailable(final ObjectInstanceHandle theObject, final AttributeHandleSet desiredAttributes) throws AttributeAlreadyBeingAcquired, AttributeNotPublished, ObjectClassNotPublished, FederateOwnsAttributes, AttributeNotDefined, ObjectInstanceNotKnown, SaveInProgress, RestoreInProgress, FederateNotExecutionMember, NotConnected, RTIinternalError {
        this.LOGGER.info("attributeOwnershipAcquisitionIfAvailable " + theObject.toString() + " " + desiredAttributes.toString());
    	try {
        this._rtiAmbassador.attributeOwnershipAcquisitionIfAvailable(theObject, desiredAttributes);
    	} catch (AttributeAlreadyBeingAcquired | AttributeNotPublished | ObjectClassNotPublished | FederateOwnsAttributes | AttributeNotDefined | ObjectInstanceNotKnown | SaveInProgress | RestoreInProgress | FederateNotExecutionMember | NotConnected | RTIinternalError e) {
    		this.LOGGER.error("attributeOwnershipAcquisitionIfAvailable exception" + e.getMessage());
    		throw e;
    	}
    }


    // 7.12
    public void attributeOwnershipReleaseDenied(final ObjectInstanceHandle theObject, final AttributeHandleSet theAttributes) throws AttributeNotOwned, AttributeNotDefined, ObjectInstanceNotKnown, SaveInProgress, RestoreInProgress, FederateNotExecutionMember, NotConnected, RTIinternalError {
        this.LOGGER.info("attributeOwnershipReleaseDenied " + theObject.toString() + " " + theAttributes.toString());
    	try {
        this._rtiAmbassador.attributeOwnershipReleaseDenied(theObject, theAttributes);
    	} catch (AttributeNotOwned | AttributeNotDefined | ObjectInstanceNotKnown | SaveInProgress | RestoreInProgress | FederateNotExecutionMember | NotConnected | RTIinternalError e) {
    		this.LOGGER.error("attributeOwnershipReleaseDenied exception" + e.getMessage());
    		throw e;
    	}
    }


    // 7.13
    public AttributeHandleSet attributeOwnershipDivestitureIfWanted(final ObjectInstanceHandle theObject, final AttributeHandleSet theAttributes) throws AttributeNotOwned, AttributeNotDefined, ObjectInstanceNotKnown, SaveInProgress, RestoreInProgress, FederateNotExecutionMember, NotConnected, RTIinternalError {
        this.LOGGER.info("attributeOwnershipDivestitureIfWanted " + theObject.toString() + " " + theAttributes.toString());
    	try {
        final AttributeHandleSet attributeHandleSet = this._rtiAmbassador.attributeOwnershipDivestitureIfWanted(theObject, theAttributes);
        this.LOGGER.info("attributeOwnershipDivestitureIfWanted return " + attributeHandleSet.toString());
        return attributeHandleSet;
    	} catch (AttributeNotOwned | AttributeNotDefined | ObjectInstanceNotKnown | SaveInProgress | RestoreInProgress | FederateNotExecutionMember | NotConnected | RTIinternalError e) {
    		this.LOGGER.error("attributeOwnershipDivestitureIfWanted exception" + e.getMessage());
    		throw e;
    	}
    }


    // 7.14
    public void cancelNegotiatedAttributeOwnershipDivestiture(final ObjectInstanceHandle theObject, final AttributeHandleSet theAttributes) throws AttributeDivestitureWasNotRequested, AttributeNotOwned, AttributeNotDefined, ObjectInstanceNotKnown, SaveInProgress, RestoreInProgress, FederateNotExecutionMember, NotConnected, RTIinternalError {
        this.LOGGER.info("cancelNegotiatedAttributeOwnershipDivestiture " + theObject.toString() + " " + theAttributes.toString());
    	try {
        this._rtiAmbassador.cancelNegotiatedAttributeOwnershipDivestiture(theObject, theAttributes);
    	} catch (AttributeDivestitureWasNotRequested | AttributeNotOwned | AttributeNotDefined | ObjectInstanceNotKnown | SaveInProgress | RestoreInProgress | FederateNotExecutionMember | NotConnected | RTIinternalError e) {
    		this.LOGGER.error("cancelNegotiatedAttributeOwnershipDivestiture exception" + e.getMessage());
    		throw e;
    	}
    }


    // 7.15
    public void cancelAttributeOwnershipAcquisition(final ObjectInstanceHandle theObject, final AttributeHandleSet theAttributes) throws AttributeAcquisitionWasNotRequested, AttributeAlreadyOwned, AttributeNotDefined, ObjectInstanceNotKnown, SaveInProgress, RestoreInProgress, FederateNotExecutionMember, NotConnected, RTIinternalError {
        this.LOGGER.info("cancelAttributeOwnershipAcquisition " + theObject.toString() + " " + theAttributes.toString());
    	try {
        this._rtiAmbassador.cancelAttributeOwnershipAcquisition(theObject, theAttributes);
    	} catch (AttributeAcquisitionWasNotRequested | AttributeAlreadyOwned | AttributeNotDefined | ObjectInstanceNotKnown | SaveInProgress | RestoreInProgress | FederateNotExecutionMember | NotConnected | RTIinternalError e) {
    		this.LOGGER.error("cancelAttributeOwnershipAcquisition exception" + e.getMessage());
    		throw e;
    	}
    }


    // 7.17
    public void queryAttributeOwnership(final ObjectInstanceHandle theObject, final AttributeHandle theAttribute) throws AttributeNotDefined, ObjectInstanceNotKnown, SaveInProgress, RestoreInProgress, FederateNotExecutionMember, NotConnected, RTIinternalError {
        this.LOGGER.info("queryAttributeOwnership " + theObject.toString() + " " + theAttribute.toString());
    	try {
        this._rtiAmbassador.queryAttributeOwnership(theObject, theAttribute);
    	} catch (AttributeNotDefined | ObjectInstanceNotKnown | SaveInProgress | RestoreInProgress | FederateNotExecutionMember | NotConnected | RTIinternalError e) {
    		this.LOGGER.error("queryAttributeOwnership exception" + e.getMessage());
    		throw e;
    	}
    }


    // 7.19
    public boolean isAttributeOwnedByFederate(final ObjectInstanceHandle theObject, final AttributeHandle theAttribute) throws AttributeNotDefined, ObjectInstanceNotKnown, SaveInProgress, RestoreInProgress, FederateNotExecutionMember, NotConnected, RTIinternalError {
        this.LOGGER.info("isAttributeOwnedByFederate " + theObject.toString() + " " + theAttribute.toString());
    	try {
        final boolean bool = this._rtiAmbassador.isAttributeOwnedByFederate(theObject, theAttribute);
        this.LOGGER.info("isAttributeOwnedByFederate return " + bool);
        return bool;
    	} catch (AttributeNotDefined | ObjectInstanceNotKnown | SaveInProgress | RestoreInProgress | FederateNotExecutionMember | NotConnected | RTIinternalError e) {
    		this.LOGGER.error("isAttributeOwnedByFederate exception" + e.getMessage());
    		throw e;
    	}
    }

    //////////////////////////////
    // Time Management Services //
    //////////////////////////////


    // 8.2
    public void enableTimeRegulation(final LogicalTimeInterval theLookahead) throws InvalidLookahead, InTimeAdvancingState, RequestForTimeRegulationPending, TimeRegulationAlreadyEnabled, SaveInProgress, RestoreInProgress, FederateNotExecutionMember, NotConnected, RTIinternalError {
        this.LOGGER.info("enableTimeRegulation " + theLookahead.toString());
    	try {
        this._rtiAmbassador.enableTimeRegulation(theLookahead);
    	} catch (InvalidLookahead | InTimeAdvancingState | RequestForTimeRegulationPending | TimeRegulationAlreadyEnabled | SaveInProgress | RestoreInProgress | FederateNotExecutionMember | NotConnected | RTIinternalError e) {
    		this.LOGGER.error("enableTimeRegulation exception" + e.getMessage());
    		throw e;
    	}
    }


    // 8.4
    public void disableTimeRegulation() throws TimeRegulationIsNotEnabled, SaveInProgress, RestoreInProgress, FederateNotExecutionMember, NotConnected, RTIinternalError {
        this.LOGGER.info("disableTimeRegulation");
    	try {
        this._rtiAmbassador.disableTimeRegulation();
    	} catch (TimeRegulationIsNotEnabled | SaveInProgress | RestoreInProgress | FederateNotExecutionMember | NotConnected | RTIinternalError e) {
    		this.LOGGER.error("disableTimeRegulation exception" + e.getMessage());
    		throw e;
    	}
    }


    // 8.5
    public void enableTimeConstrained() throws InTimeAdvancingState, RequestForTimeConstrainedPending, TimeConstrainedAlreadyEnabled, SaveInProgress, RestoreInProgress, FederateNotExecutionMember, NotConnected, RTIinternalError {
        this.LOGGER.info("enableTimeConstrained");
    	try {
        this._rtiAmbassador.enableTimeConstrained();
    	} catch (InTimeAdvancingState | RequestForTimeConstrainedPending | TimeConstrainedAlreadyEnabled | SaveInProgress | RestoreInProgress | FederateNotExecutionMember | NotConnected | RTIinternalError e) {
    		this.LOGGER.error("enableTimeConstrained exception" + e.getMessage());
    		throw e;
    	}
    }


    // 8.7
    public void disableTimeConstrained() throws TimeConstrainedIsNotEnabled, SaveInProgress, RestoreInProgress, FederateNotExecutionMember, NotConnected, RTIinternalError {
        this.LOGGER.info("disableTimeConstrained");
    	try {
        this._rtiAmbassador.disableTimeConstrained();
    	} catch (TimeConstrainedIsNotEnabled | SaveInProgress | RestoreInProgress | FederateNotExecutionMember | NotConnected | RTIinternalError e) {
    		this.LOGGER.error("disableTimeConstrained exception" + e.getMessage());
    		throw e;
    	}
    }


    // 8.8
    public void timeAdvanceRequest(final LogicalTime theTime) throws LogicalTimeAlreadyPassed, InvalidLogicalTime, InTimeAdvancingState, RequestForTimeRegulationPending, RequestForTimeConstrainedPending, SaveInProgress, RestoreInProgress, FederateNotExecutionMember, NotConnected, RTIinternalError {
        this.LOGGER.info("timeAdvanceRequest " + theTime.toString());
    	try {
        this._rtiAmbassador.timeAdvanceRequest(theTime);
    	} catch (LogicalTimeAlreadyPassed | InvalidLogicalTime | InTimeAdvancingState | RequestForTimeRegulationPending | RequestForTimeConstrainedPending | SaveInProgress | RestoreInProgress | FederateNotExecutionMember | NotConnected | RTIinternalError e) {
    		this.LOGGER.error("timeAdvanceRequest exception" + e.getMessage());
    		throw e;
    	}
    }


    // 8.9
    public void timeAdvanceRequestAvailable(final LogicalTime theTime) throws LogicalTimeAlreadyPassed, InvalidLogicalTime, InTimeAdvancingState, RequestForTimeRegulationPending, RequestForTimeConstrainedPending, SaveInProgress, RestoreInProgress, FederateNotExecutionMember, NotConnected, RTIinternalError {
        this.LOGGER.info("timeAdvanceRequestAvailable " + theTime.toString());
    	try {
        this._rtiAmbassador.timeAdvanceRequestAvailable(theTime);
    	} catch (LogicalTimeAlreadyPassed | InvalidLogicalTime | InTimeAdvancingState | RequestForTimeRegulationPending | RequestForTimeConstrainedPending | SaveInProgress | RestoreInProgress | FederateNotExecutionMember | NotConnected | RTIinternalError e) {
    		this.LOGGER.error("timeAdvanceRequestAvailable exception" + e.getMessage());
    		throw e;
    	}
    }


    // 8.10
    public void nextMessageRequest(final LogicalTime theTime) throws LogicalTimeAlreadyPassed, InvalidLogicalTime, InTimeAdvancingState, RequestForTimeRegulationPending, RequestForTimeConstrainedPending, SaveInProgress, RestoreInProgress, FederateNotExecutionMember, NotConnected, RTIinternalError {
        this.LOGGER.info("nextMessageRequest " + theTime.toString());
    	try {
        this._rtiAmbassador.nextMessageRequest(theTime);
    	} catch (LogicalTimeAlreadyPassed | InvalidLogicalTime | InTimeAdvancingState | RequestForTimeRegulationPending | RequestForTimeConstrainedPending | SaveInProgress | RestoreInProgress | FederateNotExecutionMember | NotConnected | RTIinternalError e) {
    		this.LOGGER.error("nextMessageRequest exception" + e.getMessage());
    		throw e;
    	}
    }


    // 8.11
    public void nextMessageRequestAvailable(final LogicalTime theTime) throws LogicalTimeAlreadyPassed, InvalidLogicalTime, InTimeAdvancingState, RequestForTimeRegulationPending, RequestForTimeConstrainedPending, SaveInProgress, RestoreInProgress, FederateNotExecutionMember, NotConnected, RTIinternalError {
        this.LOGGER.info("nextMessageRequestAvailable " + theTime.toString());
    	try {
        this._rtiAmbassador.nextMessageRequestAvailable(theTime);
    	} catch (LogicalTimeAlreadyPassed | InvalidLogicalTime | InTimeAdvancingState | RequestForTimeRegulationPending | RequestForTimeConstrainedPending | SaveInProgress | RestoreInProgress | FederateNotExecutionMember | NotConnected | RTIinternalError e) {
    		this.LOGGER.error("nextMessageRequestAvailable exception" + e.getMessage());
    		throw e;
    	}
    }


    // 8.12
    public void flushQueueRequest(final LogicalTime theTime) throws LogicalTimeAlreadyPassed, InvalidLogicalTime, InTimeAdvancingState, RequestForTimeRegulationPending, RequestForTimeConstrainedPending, SaveInProgress, RestoreInProgress, FederateNotExecutionMember, NotConnected, RTIinternalError {
        this.LOGGER.info("flushQueueRequest " + theTime.toString());
    	try {
        this._rtiAmbassador.flushQueueRequest(theTime);
    	} catch (LogicalTimeAlreadyPassed | InvalidLogicalTime | InTimeAdvancingState | RequestForTimeRegulationPending | RequestForTimeConstrainedPending | SaveInProgress | RestoreInProgress | FederateNotExecutionMember | NotConnected | RTIinternalError e) {
    		this.LOGGER.error("flushQueueRequest exception" + e.getMessage());
    		throw e;
    	}
    }


    // 8.14
    public void enableAsynchronousDelivery() throws AsynchronousDeliveryAlreadyEnabled, SaveInProgress, RestoreInProgress, FederateNotExecutionMember, NotConnected, RTIinternalError {
        this.LOGGER.info("enableAsynchronousDelivery");
    	try {
        this._rtiAmbassador.enableAsynchronousDelivery();
    	} catch (AsynchronousDeliveryAlreadyEnabled | SaveInProgress | RestoreInProgress | FederateNotExecutionMember | NotConnected | RTIinternalError e) {
    		this.LOGGER.error("enableAsynchronousDelivery exception" + e.getMessage());
    		throw e;
    	}
    }


    // 8.15
    public void disableAsynchronousDelivery() throws AsynchronousDeliveryAlreadyDisabled, SaveInProgress, RestoreInProgress, FederateNotExecutionMember, NotConnected, RTIinternalError {
        this.LOGGER.info("disableAsynchronousDelivery");
    	try {
        this._rtiAmbassador.disableAsynchronousDelivery();
    	} catch (AsynchronousDeliveryAlreadyDisabled | SaveInProgress | RestoreInProgress | FederateNotExecutionMember | NotConnected | RTIinternalError e) {
    		this.LOGGER.error("disableAsynchronousDelivery exception" + e.getMessage());
    		throw e;
    	}
    }


    // 8.16
    public TimeQueryReturn queryGALT() throws SaveInProgress, RestoreInProgress, FederateNotExecutionMember, NotConnected, RTIinternalError {
        this.LOGGER.info("queryGALT");
    	try {
        final TimeQueryReturn timeQueryReturn = this._rtiAmbassador.queryGALT();
        this.LOGGER.info("queryGALT return " + timeQueryReturn.toString());
        return timeQueryReturn;
    	} catch (SaveInProgress | RestoreInProgress | FederateNotExecutionMember | NotConnected | RTIinternalError e) {
    		this.LOGGER.error("queryGALT exception" + e.getMessage());
    		throw e;
    	}
    }


    // 8.17
    public LogicalTime queryLogicalTime() throws SaveInProgress, RestoreInProgress, FederateNotExecutionMember, NotConnected, RTIinternalError {
        this.LOGGER.info("queryLogicalTime");
    	try {
        final LogicalTime logicalTime = this._rtiAmbassador.queryLogicalTime();
        this.LOGGER.info("queryLogicalTime return " + logicalTime.toString());
        return logicalTime;
    	} catch (SaveInProgress | RestoreInProgress | FederateNotExecutionMember | NotConnected | RTIinternalError e) {
    		this.LOGGER.error("queryLogicalTime exception" + e.getMessage());
    		throw e;
    	}
    }


    // 8.18
    public TimeQueryReturn queryLITS() throws SaveInProgress, RestoreInProgress, FederateNotExecutionMember, NotConnected, RTIinternalError {
        this.LOGGER.info("queryLITS");
    	try {
        final TimeQueryReturn timeQueryReturn = this._rtiAmbassador.queryLITS();
        this.LOGGER.info("queryLITS return " + timeQueryReturn.toString());
        return timeQueryReturn;
    	} catch (SaveInProgress | RestoreInProgress | FederateNotExecutionMember | NotConnected | RTIinternalError e) {
    		this.LOGGER.error("queryLITS exception" + e.getMessage());
    		throw e;
    	}
    }


    // 8.19
    public void modifyLookahead(final LogicalTimeInterval theLookahead) throws InvalidLookahead, InTimeAdvancingState, TimeRegulationIsNotEnabled, SaveInProgress, RestoreInProgress, FederateNotExecutionMember, NotConnected, RTIinternalError {
        this.LOGGER.info("modifyLookahead " + theLookahead.toString());
    	try {
        this._rtiAmbassador.modifyLookahead(theLookahead);
    	} catch (InvalidLookahead | InTimeAdvancingState | TimeRegulationIsNotEnabled | SaveInProgress | RestoreInProgress | FederateNotExecutionMember | NotConnected | RTIinternalError e) {
    		this.LOGGER.error("modifyLookahead exception" + e.getMessage());
    		throw e;
    	}
    }


    // 8.20
    public LogicalTimeInterval queryLookahead() throws TimeRegulationIsNotEnabled, SaveInProgress, RestoreInProgress, FederateNotExecutionMember, NotConnected, RTIinternalError {
        this.LOGGER.info("queryLookahead");
    	try {
        final LogicalTimeInterval logicalTimeInterval = this._rtiAmbassador.queryLookahead();
        this.LOGGER.info("queryLookahead return " + logicalTimeInterval.toString());
        return logicalTimeInterval;
    	} catch (TimeRegulationIsNotEnabled | SaveInProgress | RestoreInProgress | FederateNotExecutionMember | NotConnected | RTIinternalError e) {
    		this.LOGGER.error("queryLookahead exception" + e.getMessage());
    		throw e;
    	}
    }


    // 8.21
    public void retract(final MessageRetractionHandle theHandle) throws MessageCanNoLongerBeRetracted, InvalidMessageRetractionHandle, TimeRegulationIsNotEnabled, SaveInProgress, RestoreInProgress, FederateNotExecutionMember, NotConnected, RTIinternalError {
        this.LOGGER.info("retract " + theHandle.toString());
    	try {
        this._rtiAmbassador.retract(theHandle);
    	} catch (MessageCanNoLongerBeRetracted | InvalidMessageRetractionHandle | TimeRegulationIsNotEnabled | SaveInProgress | RestoreInProgress | FederateNotExecutionMember | NotConnected | RTIinternalError e) {
    		this.LOGGER.error("retract exception" + e.getMessage());
    		throw e;
    	}
    }


    // 8.23
    public void changeAttributeOrderType(final ObjectInstanceHandle theObject, final AttributeHandleSet theAttributes, final OrderType theType) throws AttributeNotOwned, AttributeNotDefined, ObjectInstanceNotKnown, SaveInProgress, RestoreInProgress, FederateNotExecutionMember, NotConnected, RTIinternalError {
        this.LOGGER.info("changeAttributeOrderType " + theObject.toString() + " " + theAttributes.toString() + " " + theType.toString());
    	try {
        this._rtiAmbassador.changeAttributeOrderType(theObject, theAttributes, theType);
    	} catch (AttributeNotOwned | AttributeNotDefined | ObjectInstanceNotKnown | SaveInProgress | RestoreInProgress | FederateNotExecutionMember | NotConnected | RTIinternalError e) {
    		this.LOGGER.error("changeAttributeOrderType exception" + e.getMessage());
    		throw e;
    	}
    }


    // 8.24
    public void changeInteractionOrderType(final InteractionClassHandle theClass, final OrderType theType) throws InteractionClassNotPublished, InteractionClassNotDefined, SaveInProgress, RestoreInProgress, FederateNotExecutionMember, NotConnected, RTIinternalError {
        this.LOGGER.info("changeInteractionOrderType " + theClass.toString() + " " + theType.toString());
    	try {
        this._rtiAmbassador.changeInteractionOrderType(theClass, theType);
    	} catch (InteractionClassNotPublished | InteractionClassNotDefined | SaveInProgress | RestoreInProgress | FederateNotExecutionMember | NotConnected | RTIinternalError e) {
    		this.LOGGER.error("changeInteractionOrderType exception" + e.getMessage());
    		throw e;
    	}
    }

    //////////////////////////////////
    // Data Distribution Management //
    //////////////////////////////////


    // 9.2
    public RegionHandle createRegion(final DimensionHandleSet dimensions) throws InvalidDimensionHandle, SaveInProgress, RestoreInProgress, FederateNotExecutionMember, NotConnected, RTIinternalError {
        this.LOGGER.info("createRegion " + dimensions.toString());
    	try {
        final RegionHandle regionHandle = this._rtiAmbassador.createRegion(dimensions);
        this.LOGGER.info("createRegion return " + regionHandle.toString());
        return regionHandle;
    	} catch (InvalidDimensionHandle | SaveInProgress | RestoreInProgress | FederateNotExecutionMember | NotConnected | RTIinternalError e) {
    		this.LOGGER.error("createRegion exception" + e.getMessage());
    		throw e;
    	}
    }


    // 9.3
    public void commitRegionModifications(final RegionHandleSet regions) throws RegionNotCreatedByThisFederate, InvalidRegion, SaveInProgress, RestoreInProgress, FederateNotExecutionMember, NotConnected, RTIinternalError {
        this.LOGGER.info("commitRegionModifications " + regions.toString());
    	try {
        this._rtiAmbassador.commitRegionModifications(regions);
    	} catch (RegionNotCreatedByThisFederate | InvalidRegion | SaveInProgress | RestoreInProgress | FederateNotExecutionMember | NotConnected | RTIinternalError e) {
    		this.LOGGER.error("commitRegionModifications exception" + e.getMessage());
    		throw e;
    	}
    }


    // 9.4
    public void deleteRegion(final RegionHandle theRegion) throws RegionInUseForUpdateOrSubscription, RegionNotCreatedByThisFederate, InvalidRegion, SaveInProgress, RestoreInProgress, FederateNotExecutionMember, NotConnected, RTIinternalError {
        this.LOGGER.info("deleteRegion " + theRegion.toString());
    	try {
        this._rtiAmbassador.deleteRegion(theRegion);
    	} catch (RegionInUseForUpdateOrSubscription | RegionNotCreatedByThisFederate | InvalidRegion | SaveInProgress | RestoreInProgress | FederateNotExecutionMember | NotConnected | RTIinternalError e) {
    		this.LOGGER.error("deleteRegion exception" + e.getMessage());
    		throw e;
    	}
    }


    //9.5
    public ObjectInstanceHandle registerObjectInstanceWithRegions(final ObjectClassHandle theClass, final AttributeSetRegionSetPairList attributesAndRegions) throws InvalidRegionContext, RegionNotCreatedByThisFederate, InvalidRegion, AttributeNotPublished, ObjectClassNotPublished, AttributeNotDefined, ObjectClassNotDefined, SaveInProgress, RestoreInProgress, FederateNotExecutionMember, NotConnected, RTIinternalError {
        this.LOGGER.info("registerObjectInstanceWithRegions " + theClass.toString() + " " + attributesAndRegions.toString());
    	try {
        final ObjectInstanceHandle objectInstanceHandle = this._rtiAmbassador.registerObjectInstanceWithRegions(theClass, attributesAndRegions);
        this.LOGGER.info("registerObjectInstanceWithRegions return " + objectInstanceHandle.toString());
        return objectInstanceHandle;
    	} catch (InvalidRegionContext | RegionNotCreatedByThisFederate | InvalidRegion | AttributeNotPublished | ObjectClassNotPublished | AttributeNotDefined | ObjectClassNotDefined | SaveInProgress | RestoreInProgress | FederateNotExecutionMember | NotConnected | RTIinternalError e) {
    		this.LOGGER.error("registerObjectInstanceWithRegions exception" + e.getMessage());
    		throw e;
    	}
    }


    //9.5
    public ObjectInstanceHandle registerObjectInstanceWithRegions(final ObjectClassHandle theClass, final AttributeSetRegionSetPairList attributesAndRegions, final String theObject) throws ObjectInstanceNameInUse, ObjectInstanceNameNotReserved, InvalidRegionContext, RegionNotCreatedByThisFederate, InvalidRegion, AttributeNotPublished, ObjectClassNotPublished, AttributeNotDefined, ObjectClassNotDefined, SaveInProgress, RestoreInProgress, FederateNotExecutionMember, NotConnected, RTIinternalError {
        this.LOGGER.info("registerObjectInstanceWithRegions " + theClass.toString() + " " + attributesAndRegions.toString() + " " + theObject.toString());
    	try {
        final ObjectInstanceHandle objectInstanceHandle = this._rtiAmbassador.registerObjectInstanceWithRegions(theClass, attributesAndRegions, theObject);
        this.LOGGER.info("registerObjectInstanceWithRegions return " + objectInstanceHandle.toString());
        return objectInstanceHandle;
    	} catch (ObjectInstanceNameInUse | ObjectInstanceNameNotReserved | InvalidRegionContext | RegionNotCreatedByThisFederate | InvalidRegion | AttributeNotPublished | ObjectClassNotPublished | AttributeNotDefined | ObjectClassNotDefined | SaveInProgress | RestoreInProgress | FederateNotExecutionMember | NotConnected | RTIinternalError e) {
    		this.LOGGER.error("registerObjectInstanceWithRegions exception" + e.getMessage());
    		throw e;
    	}
    }


    // 9.6
    public void associateRegionsForUpdates(final ObjectInstanceHandle theObject, final AttributeSetRegionSetPairList attributesAndRegions) throws InvalidRegionContext, RegionNotCreatedByThisFederate, InvalidRegion, AttributeNotDefined, ObjectInstanceNotKnown, SaveInProgress, RestoreInProgress, FederateNotExecutionMember, NotConnected, RTIinternalError {
        this.LOGGER.info("associateRegionsForUpdates " + theObject.toString() + " " + attributesAndRegions.toString());
    	try {
        this._rtiAmbassador.associateRegionsForUpdates(theObject, attributesAndRegions);
    	} catch (InvalidRegionContext | RegionNotCreatedByThisFederate | InvalidRegion | AttributeNotDefined | ObjectInstanceNotKnown | SaveInProgress | RestoreInProgress | FederateNotExecutionMember | NotConnected | RTIinternalError e) {
    		this.LOGGER.error("associateRegionsForUpdates exception" + e.getMessage());
    		throw e;
    	}
    }


    // 9.7
    public void unassociateRegionsForUpdates(final ObjectInstanceHandle theObject, final AttributeSetRegionSetPairList attributesAndRegions) throws RegionNotCreatedByThisFederate, InvalidRegion, AttributeNotDefined, ObjectInstanceNotKnown, SaveInProgress, RestoreInProgress, FederateNotExecutionMember, NotConnected, RTIinternalError {
        this.LOGGER.info("unassociateRegionsForUpdates " + theObject.toString() + " " + attributesAndRegions.toString());
    	try {
        this._rtiAmbassador.unassociateRegionsForUpdates(theObject, attributesAndRegions);
    	} catch (RegionNotCreatedByThisFederate | InvalidRegion | AttributeNotDefined | ObjectInstanceNotKnown | SaveInProgress | RestoreInProgress | FederateNotExecutionMember | NotConnected | RTIinternalError e) {
    		this.LOGGER.error("unassociateRegionsForUpdates exception" + e.getMessage());
    		throw e;
    	}
    }


    // 9.8
    public void subscribeObjectClassAttributesWithRegions(final ObjectClassHandle theClass, final AttributeSetRegionSetPairList attributesAndRegions) throws InvalidRegionContext, RegionNotCreatedByThisFederate, InvalidRegion, AttributeNotDefined, ObjectClassNotDefined, SaveInProgress, RestoreInProgress, FederateNotExecutionMember, NotConnected, RTIinternalError {
        this.LOGGER.info("subscribeObjectClassAttributesWithRegions " + theClass.toString() + " " + attributesAndRegions.toString());
    	try {
        this._rtiAmbassador.subscribeObjectClassAttributesWithRegions(theClass, attributesAndRegions);
    	} catch (InvalidRegionContext | RegionNotCreatedByThisFederate | InvalidRegion | AttributeNotDefined | ObjectClassNotDefined | SaveInProgress | RestoreInProgress | FederateNotExecutionMember | NotConnected | RTIinternalError e) {
    		this.LOGGER.error("subscribeObjectClassAttributesWithRegions exception" + e.getMessage());
    		throw e;
    	}
    }


    // 9.8
    public void subscribeObjectClassAttributesWithRegions(final ObjectClassHandle theClass, final AttributeSetRegionSetPairList attributesAndRegions, final String updateRateDesignator) throws InvalidRegionContext, RegionNotCreatedByThisFederate, InvalidRegion, AttributeNotDefined, ObjectClassNotDefined, InvalidUpdateRateDesignator, SaveInProgress, RestoreInProgress, FederateNotExecutionMember, NotConnected, RTIinternalError {
        this.LOGGER.info("subscribeObjectClassAttributesWithRegions " + theClass.toString() + " " + attributesAndRegions.toString() + " " + updateRateDesignator);
    	try {
        this._rtiAmbassador.subscribeObjectClassAttributesWithRegions(theClass, attributesAndRegions, updateRateDesignator);
    	} catch (InvalidRegionContext | RegionNotCreatedByThisFederate | InvalidRegion | AttributeNotDefined | ObjectClassNotDefined | InvalidUpdateRateDesignator | SaveInProgress | RestoreInProgress | FederateNotExecutionMember | NotConnected | RTIinternalError e) {
    		this.LOGGER.error("subscribeObjectClassAttributesWithRegions exception" + e.getMessage());
    		throw e;
    	}
    }


    // 9.8
    public void subscribeObjectClassAttributesPassivelyWithRegions(final ObjectClassHandle theClass, final AttributeSetRegionSetPairList attributesAndRegions) throws InvalidRegionContext, RegionNotCreatedByThisFederate, InvalidRegion, AttributeNotDefined, ObjectClassNotDefined, SaveInProgress, RestoreInProgress, FederateNotExecutionMember, NotConnected, RTIinternalError {
        this.LOGGER.info("subscribeObjectClassAttributesPassivelyWithRegions " + theClass.toString() + " " + attributesAndRegions.toString());
    	try {
        this._rtiAmbassador.subscribeObjectClassAttributesPassivelyWithRegions(theClass, attributesAndRegions);
    	} catch (InvalidRegionContext | RegionNotCreatedByThisFederate | InvalidRegion | AttributeNotDefined | ObjectClassNotDefined | SaveInProgress | RestoreInProgress | FederateNotExecutionMember | NotConnected | RTIinternalError e) {
    		this.LOGGER.error("subscribeObjectClassAttributesPassivelyWithRegions exception" + e.getMessage());
    		throw e;
    	}
    }


    // 9.8
    public void subscribeObjectClassAttributesPassivelyWithRegions(final ObjectClassHandle theClass, final AttributeSetRegionSetPairList attributesAndRegions, final String updateRateDesignator) throws InvalidRegionContext, RegionNotCreatedByThisFederate, InvalidRegion, AttributeNotDefined, ObjectClassNotDefined, InvalidUpdateRateDesignator, SaveInProgress, RestoreInProgress, FederateNotExecutionMember, NotConnected, RTIinternalError {
        this.LOGGER.info("subscribeObjectClassAttributesPassivelyWithRegions " + theClass.toString() + " " + attributesAndRegions.toString() + " " + updateRateDesignator);
    	try {
        this._rtiAmbassador.subscribeObjectClassAttributesPassivelyWithRegions(theClass, attributesAndRegions, updateRateDesignator);
    	} catch (InvalidRegionContext | RegionNotCreatedByThisFederate | InvalidRegion | AttributeNotDefined | ObjectClassNotDefined | InvalidUpdateRateDesignator | SaveInProgress | RestoreInProgress | FederateNotExecutionMember | NotConnected | RTIinternalError e) {
    		this.LOGGER.error("subscribeObjectClassAttributesPassivelyWithRegions exception" + e.getMessage());
    		throw e;
    	}
    }


    // 9.9
    public void unsubscribeObjectClassAttributesWithRegions(final ObjectClassHandle theClass, final AttributeSetRegionSetPairList attributesAndRegions) throws RegionNotCreatedByThisFederate, InvalidRegion, AttributeNotDefined, ObjectClassNotDefined, SaveInProgress, RestoreInProgress, FederateNotExecutionMember, NotConnected, RTIinternalError {
        this.LOGGER.info("unsubscribeObjectClassAttributesWithRegions " + theClass.toString() + " " + attributesAndRegions.toString());
    	try {
        this._rtiAmbassador.unsubscribeObjectClassAttributesWithRegions(theClass, attributesAndRegions);
    	} catch (RegionNotCreatedByThisFederate | InvalidRegion | AttributeNotDefined | ObjectClassNotDefined | SaveInProgress | RestoreInProgress | FederateNotExecutionMember | NotConnected | RTIinternalError e) {
    		this.LOGGER.error("unsubscribeObjectClassAttributesWithRegions exception" + e.getMessage());
    		throw e;
    	}
    }


    // 9.10
    public void subscribeInteractionClassWithRegions(final InteractionClassHandle theClass, final RegionHandleSet regions) throws FederateServiceInvocationsAreBeingReportedViaMOM, InvalidRegionContext, RegionNotCreatedByThisFederate, InvalidRegion, InteractionClassNotDefined, SaveInProgress, RestoreInProgress, FederateNotExecutionMember, NotConnected, RTIinternalError {
        this.LOGGER.info("subscribeInteractionClassWithRegions " + theClass.toString() + " " + regions.toString());
    	try {
        this._rtiAmbassador.subscribeInteractionClassWithRegions(theClass, regions);
    	} catch (FederateServiceInvocationsAreBeingReportedViaMOM | InvalidRegionContext | RegionNotCreatedByThisFederate | InvalidRegion | InteractionClassNotDefined | SaveInProgress | RestoreInProgress | FederateNotExecutionMember | NotConnected | RTIinternalError e) {
    		this.LOGGER.error("subscribeInteractionClassWithRegions exception" + e.getMessage());
    		throw e;
    	}
    }


    // 9.10
    public void subscribeInteractionClassPassivelyWithRegions(final InteractionClassHandle theClass, final RegionHandleSet regions) throws FederateServiceInvocationsAreBeingReportedViaMOM, InvalidRegionContext, RegionNotCreatedByThisFederate, InvalidRegion, InteractionClassNotDefined, SaveInProgress, RestoreInProgress, FederateNotExecutionMember, NotConnected, RTIinternalError {
        this.LOGGER.info("subscribeInteractionClassPassivelyWithRegions " + theClass.toString() + " " + regions.toString());
    	try {
        this._rtiAmbassador.subscribeInteractionClassPassivelyWithRegions(theClass, regions);
    	} catch (FederateServiceInvocationsAreBeingReportedViaMOM | InvalidRegionContext | RegionNotCreatedByThisFederate | InvalidRegion | InteractionClassNotDefined | SaveInProgress | RestoreInProgress | FederateNotExecutionMember | NotConnected | RTIinternalError e) {
    		this.LOGGER.error("subscribeInteractionClassPassivelyWithRegions exception" + e.getMessage());
    		throw e;
    	}
    }


    // 9.11
    public void unsubscribeInteractionClassWithRegions(final InteractionClassHandle theClass, final RegionHandleSet regions) throws RegionNotCreatedByThisFederate, InvalidRegion, InteractionClassNotDefined, SaveInProgress, RestoreInProgress, FederateNotExecutionMember, NotConnected, RTIinternalError {
        this.LOGGER.info("unsubscribeInteractionClassWithRegions " + theClass.toString() + " " + regions.toString());
    	try {
        this._rtiAmbassador.unsubscribeInteractionClassWithRegions(theClass, regions);
    	} catch (RegionNotCreatedByThisFederate | InvalidRegion | InteractionClassNotDefined | SaveInProgress | RestoreInProgress | FederateNotExecutionMember | NotConnected | RTIinternalError e) {
    		this.LOGGER.error("unsubscribeInteractionClassWithRegions exception" + e.getMessage());
    		throw e;
    	}
    }


    //9.12
    public void sendInteractionWithRegions(final InteractionClassHandle theInteraction, final ParameterHandleValueMap theParameters, final RegionHandleSet regions, final byte[] userSuppliedTag) throws InvalidRegionContext, RegionNotCreatedByThisFederate, InvalidRegion, InteractionClassNotPublished, InteractionParameterNotDefined, InteractionClassNotDefined, SaveInProgress, RestoreInProgress, FederateNotExecutionMember, NotConnected, RTIinternalError {
        this.LOGGER.info("sendInteractionWithRegions " + theInteraction.toString() + " " + theParameters.toString() + " " + regions.toString() + " " + Arrays.toString(userSuppliedTag));
    	try {
        this._rtiAmbassador.sendInteractionWithRegions(theInteraction, theParameters, regions, userSuppliedTag);
    	} catch (InvalidRegionContext | RegionNotCreatedByThisFederate | InvalidRegion | InteractionClassNotPublished | InteractionParameterNotDefined | InteractionClassNotDefined | SaveInProgress | RestoreInProgress | FederateNotExecutionMember | NotConnected | RTIinternalError e) {
    		this.LOGGER.error("sendInteractionWithRegions exception" + e.getMessage());
    		throw e;
    	}
    }


    //9.12
    public MessageRetractionReturn sendInteractionWithRegions(final InteractionClassHandle theInteraction, final ParameterHandleValueMap theParameters, final RegionHandleSet regions, final byte[] userSuppliedTag, final LogicalTime theTime) throws InvalidLogicalTime, InvalidRegionContext, RegionNotCreatedByThisFederate, InvalidRegion, InteractionClassNotPublished, InteractionParameterNotDefined, InteractionClassNotDefined, SaveInProgress, RestoreInProgress, FederateNotExecutionMember, NotConnected, RTIinternalError {
        this.LOGGER.info("sendInteractionWithRegions " + theInteraction.toString() + " " + theParameters.toString() + " " + regions.toString() + " " + Arrays.toString(userSuppliedTag) + " " + theTime.toString());
    	try {
        final MessageRetractionReturn messageRetractionReturn = this._rtiAmbassador.sendInteractionWithRegions(theInteraction, theParameters, regions, userSuppliedTag, theTime);
        this.LOGGER.info("sendInteractionWithRegions return " + messageRetractionReturn.toString());
        return messageRetractionReturn;
    	} catch (InvalidLogicalTime | InvalidRegionContext | RegionNotCreatedByThisFederate | InvalidRegion | InteractionClassNotPublished | InteractionParameterNotDefined | InteractionClassNotDefined | SaveInProgress | RestoreInProgress | FederateNotExecutionMember | NotConnected | RTIinternalError e) {
    		this.LOGGER.error("sendInteractionWithRegions exception" + e.getMessage());
    		throw e;
    	}
    }


    // 9.13
    public void requestAttributeValueUpdateWithRegions(final ObjectClassHandle theClass, final AttributeSetRegionSetPairList attributesAndRegions, final byte[] userSuppliedTag) throws InvalidRegionContext, RegionNotCreatedByThisFederate, InvalidRegion, AttributeNotDefined, ObjectClassNotDefined, SaveInProgress, RestoreInProgress, FederateNotExecutionMember, NotConnected, RTIinternalError {
        this.LOGGER.info("requestAttributeValueUpdateWithRegions " + theClass.toString() + " " + attributesAndRegions.toString() + " " + Arrays.toString(userSuppliedTag));
    	try {
        this._rtiAmbassador.requestAttributeValueUpdateWithRegions(theClass, attributesAndRegions, userSuppliedTag);
    	} catch (InvalidRegionContext | RegionNotCreatedByThisFederate | InvalidRegion | AttributeNotDefined | ObjectClassNotDefined | SaveInProgress | RestoreInProgress | FederateNotExecutionMember | NotConnected | RTIinternalError e) {
    		this.LOGGER.error("requestAttributeValueUpdateWithRegions exception" + e.getMessage());
    		throw e;
    	}
    }

    //////////////////////////
    // RTI Support Services //
    //////////////////////////


    // 10.2
    public ResignAction getAutomaticResignDirective() throws FederateNotExecutionMember, NotConnected, RTIinternalError {
        this.LOGGER.info("getAutomaticResignDirective");
    	try {
        final ResignAction resignAction = this._rtiAmbassador.getAutomaticResignDirective();
        this.LOGGER.info("getAutomaticResignDirective return " + resignAction.toString());
        return resignAction;
    	} catch (FederateNotExecutionMember | NotConnected | RTIinternalError e) {
    		this.LOGGER.error("getAutomaticResignDirective exception" + e.getMessage());
    		throw e;
    	}
    }


    // 10.3
    public void setAutomaticResignDirective(final ResignAction resignAction) throws InvalidResignAction, FederateNotExecutionMember, NotConnected, RTIinternalError {
        this.LOGGER.info("setAutomaticResignDirective " + resignAction.toString());
    	try {
        this._rtiAmbassador.setAutomaticResignDirective(resignAction);
    	} catch (InvalidResignAction | FederateNotExecutionMember | NotConnected | RTIinternalError e) {
    		this.LOGGER.error("setAutomaticResignDirective exception" + e.getMessage());
    		throw e;
    	}
    }


    // 10.4
    public FederateHandle getFederateHandle(final String theName) throws NameNotFound, FederateNotExecutionMember, NotConnected, RTIinternalError {
        this.LOGGER.info("getFederateHandle " + theName);
    	try {
        final FederateHandle federateHandle = this._rtiAmbassador.getFederateHandle(theName);
        this.LOGGER.info("getFederateHandle return " + federateHandle.toString());
        return federateHandle;
    	} catch (NameNotFound | FederateNotExecutionMember | NotConnected | RTIinternalError e) {
    		this.LOGGER.error("getFederateHandle exception" + e.getMessage());
    		throw e;
    	}
    }


    // 10.5
    public String getFederateName(final FederateHandle theHandle) throws InvalidFederateHandle, FederateHandleNotKnown, FederateNotExecutionMember, NotConnected, RTIinternalError {
        this.LOGGER.info("getFederateName " + theHandle.toString());
    	try {
        final String str = this._rtiAmbassador.getFederateName(theHandle);
        this.LOGGER.info("getFederateName return " + str);
        return str;
    	} catch (InvalidFederateHandle | FederateHandleNotKnown | FederateNotExecutionMember | NotConnected | RTIinternalError e) {
    		this.LOGGER.error("getFederateName exception" + e.getMessage());
    		throw e;
    	}
    }


    // 10.6
    public ObjectClassHandle getObjectClassHandle(final String theName) throws NameNotFound, FederateNotExecutionMember, NotConnected, RTIinternalError {
        this.LOGGER.info("getObjectClassHandle " + theName);
    	try {
        final ObjectClassHandle objectClassHandle = this._rtiAmbassador.getObjectClassHandle(theName);
        this.LOGGER.info("getObjectClassHandle return " + objectClassHandle.toString());
        return objectClassHandle;
    	} catch (NameNotFound | FederateNotExecutionMember | NotConnected | RTIinternalError e) {
    		this.LOGGER.error("getObjectClassHandle exception" + e.getMessage());
    		throw e;
    	}
    }


    // 10.7
    public String getObjectClassName(final ObjectClassHandle theHandle) throws InvalidObjectClassHandle, FederateNotExecutionMember, NotConnected, RTIinternalError {
        this.LOGGER.info("getObjectClassName " + theHandle.toString());
    	try {
        final String str = this._rtiAmbassador.getObjectClassName(theHandle);
        this.LOGGER.info("getObjectClassName return " + str);
        return str;
    	} catch (InvalidObjectClassHandle | FederateNotExecutionMember | NotConnected | RTIinternalError e) {
    		this.LOGGER.error("getObjectClassName exception" + e.getMessage());
    		throw e;
    	}
    }


    // 10.8
    public ObjectClassHandle getKnownObjectClassHandle(final ObjectInstanceHandle theObject) throws ObjectInstanceNotKnown, FederateNotExecutionMember, NotConnected, RTIinternalError {
        this.LOGGER.info("getKnownObjectClassHandle " + theObject.toString());
    	try {
        final ObjectClassHandle objectClassHandle = this._rtiAmbassador.getKnownObjectClassHandle(theObject);
        this.LOGGER.info("getKnownObjectClassHandle return " + objectClassHandle.toString());
        return objectClassHandle;
    	} catch (ObjectInstanceNotKnown | FederateNotExecutionMember | NotConnected | RTIinternalError e) {
    		this.LOGGER.error("getKnownObjectClassHandle exception" + e.getMessage());
    		throw e;
    	}
    }


    // 10.9
    public ObjectInstanceHandle getObjectInstanceHandle(final String theName) throws ObjectInstanceNotKnown, FederateNotExecutionMember, NotConnected, RTIinternalError {
        this.LOGGER.info("getObjectInstanceHandle " + theName);
    	try {
        final ObjectInstanceHandle objectInstanceHandle = this._rtiAmbassador.getObjectInstanceHandle(theName);
        this.LOGGER.info("getObjectInstanceHandle return " + objectInstanceHandle.toString());
        return objectInstanceHandle;
    	} catch (ObjectInstanceNotKnown | FederateNotExecutionMember | NotConnected | RTIinternalError e) {
    		this.LOGGER.error("getObjectInstanceHandle exception" + e.getMessage());
    		throw e;
    	}
    }


    // 10.10
    public String getObjectInstanceName(final ObjectInstanceHandle theHandle) throws ObjectInstanceNotKnown, FederateNotExecutionMember, NotConnected, RTIinternalError {
        this.LOGGER.info("getObjectInstanceName " + theHandle.toString());
    	try {
        final String str = this._rtiAmbassador.getObjectInstanceName(theHandle);
        this.LOGGER.info("getObjectInstanceName return " + str);
        return str;
    	} catch (ObjectInstanceNotKnown | FederateNotExecutionMember | NotConnected | RTIinternalError e) {
    		this.LOGGER.error("getObjectInstanceName exception" + e.getMessage());
    		throw e;
    	}
    }


    // 10.11
    public AttributeHandle getAttributeHandle(final ObjectClassHandle whichClass, final String theName) throws NameNotFound, InvalidObjectClassHandle, FederateNotExecutionMember, NotConnected, RTIinternalError {
    	this.LOGGER.info("getAttributeHandle " + whichClass.toString() + " " + theName);
    	try {
    		final AttributeHandle attributeHandle = this._rtiAmbassador.getAttributeHandle(whichClass, theName);
    		this.LOGGER.info("getAttributeHandle return " + attributeHandle.toString());
    		return attributeHandle;
    	} catch (NameNotFound | InvalidObjectClassHandle | FederateNotExecutionMember | NotConnected | RTIinternalError e) {
    		this.LOGGER.error("getAttributeHandle exception" + e.getMessage());
    		throw e;
    	}
    }


    // 10.12
    public String getAttributeName(final ObjectClassHandle whichClass, final AttributeHandle theHandle) throws AttributeNotDefined, InvalidAttributeHandle, InvalidObjectClassHandle, FederateNotExecutionMember, NotConnected, RTIinternalError {
        this.LOGGER.info("getAttributeName " + theHandle.toString());
    	try {
        final String str = this._rtiAmbassador.getAttributeName(whichClass, theHandle);
        this.LOGGER.info("getAttributeName return " + str);
        return str;
    	} catch (AttributeNotDefined | InvalidAttributeHandle | InvalidObjectClassHandle | FederateNotExecutionMember | NotConnected | RTIinternalError e) {
    		this.LOGGER.error("getAttributeName exception" + e.getMessage());
    		throw e;
    	}
    }


    // 10.13
    public double getUpdateRateValue(final String updateRateDesignator) throws InvalidUpdateRateDesignator, FederateNotExecutionMember, NotConnected, RTIinternalError {
        this.LOGGER.info("getUpdateRateValue " + updateRateDesignator);
    	try {
        final double d = this._rtiAmbassador.getUpdateRateValue(updateRateDesignator);
        this.LOGGER.info("getUpdateRateValue return " + d);
        return d;
    	} catch (InvalidUpdateRateDesignator | FederateNotExecutionMember | NotConnected | RTIinternalError e) {
    		this.LOGGER.error("getUpdateRateValue exception" + e.getMessage());
    		throw e;
    	}
    }


    // 10.14
    public double getUpdateRateValueForAttribute(final ObjectInstanceHandle theObject, final AttributeHandle theAttribute) throws ObjectInstanceNotKnown, AttributeNotDefined, FederateNotExecutionMember, NotConnected, RTIinternalError {
        this.LOGGER.info("getUpdateRateValueForAttribute " + theObject.toString() + " " + theAttribute.toString());
    	try {
        final double d = this._rtiAmbassador.getUpdateRateValueForAttribute(theObject, theAttribute);
        this.LOGGER.info("getUpdateRateValueForAttribute return " + d);
        return d;
    	} catch (ObjectInstanceNotKnown | AttributeNotDefined | FederateNotExecutionMember | NotConnected | RTIinternalError e) {
    		this.LOGGER.error("getUpdateRateValueForAttribute exception" + e.getMessage());
    		throw e;
    	}
    }


    // 10.15
    public InteractionClassHandle getInteractionClassHandle(final String theName) throws NameNotFound, FederateNotExecutionMember, NotConnected, RTIinternalError {
        this.LOGGER.info("getInteractionClassHandle " + theName);
    	try {
        final InteractionClassHandle interactionClassHandle = this._rtiAmbassador.getInteractionClassHandle(theName);
        this.LOGGER.info("getInteractionClassHandle return " + interactionClassHandle.toString());
        return interactionClassHandle;
    	} catch (NameNotFound | FederateNotExecutionMember | NotConnected | RTIinternalError e) {
    		this.LOGGER.error("getInteractionClassHandle exception" + e.getMessage());
    		throw e;
    	}
    }


    // 10.16
    public String getInteractionClassName(final InteractionClassHandle theHandle) throws InvalidInteractionClassHandle, FederateNotExecutionMember, NotConnected, RTIinternalError {
    	this.LOGGER.info("getInteractionClassName " + theHandle.toString());
    	try {
    		final String str = this._rtiAmbassador.getInteractionClassName(theHandle);
    		this.LOGGER.info("getInteractionClassName return " + str);
    		return str;
    	} catch (InvalidInteractionClassHandle | FederateNotExecutionMember | NotConnected | RTIinternalError e) {
    		this.LOGGER.error("getInteractionClassName exception" + e.getMessage());
    		throw e;
    	}
    }


    // 10.17
    public ParameterHandle getParameterHandle(final InteractionClassHandle whichClass, final String theName) throws NameNotFound, InvalidInteractionClassHandle, FederateNotExecutionMember, NotConnected, RTIinternalError {
    	this.LOGGER.info("getParameterHandle " + whichClass.toString() + " " + theName);
    	try {
    		final ParameterHandle parameterHandle = this._rtiAmbassador.getParameterHandle(whichClass, theName);
    		this.LOGGER.info("getParameterHandle return " + parameterHandle.toString());
    		return parameterHandle;
    	} catch (NameNotFound | InvalidInteractionClassHandle | FederateNotExecutionMember | NotConnected | RTIinternalError e) {
    		this.LOGGER.error("getParameterHandle exception" + e.getMessage());
    		throw e;
    	}
    }


    // 10.18
    public String getParameterName(final InteractionClassHandle whichClass, final ParameterHandle theHandle) throws InteractionParameterNotDefined, InvalidParameterHandle, InvalidInteractionClassHandle, FederateNotExecutionMember, NotConnected, RTIinternalError {
        this.LOGGER.info("getParameterName " + whichClass.toString() + " " + theHandle.toString());
    	try {
    		String str = this._rtiAmbassador.getParameterName(whichClass, theHandle);
    		this.LOGGER.info("getParameterName return " + str);
    		return str;
    	} catch (InteractionParameterNotDefined | InvalidParameterHandle | InvalidInteractionClassHandle | FederateNotExecutionMember | NotConnected | RTIinternalError e) {
    		this.LOGGER.error("getParameterName exception" + e.getMessage());
    		throw e;
    	}
    }


    // 10.19
    public OrderType getOrderType(final String theName) throws InvalidOrderName, FederateNotExecutionMember, NotConnected, RTIinternalError {
    	this.LOGGER.info("getOrderType " + theName);
    	try {
    		OrderType orderType = this._rtiAmbassador.getOrderType(theName);
    		this.LOGGER.info("getOrderType return " + orderType.toString());
    		return orderType;
    	} catch (InvalidOrderName | FederateNotExecutionMember | NotConnected | RTIinternalError e) {
    		this.LOGGER.error("getOrderType exception" + e.getMessage());
    		throw e;
    	}
    }


    // 10.20
    public String getOrderName(final OrderType theType) throws InvalidOrderType, FederateNotExecutionMember, NotConnected, RTIinternalError {
    	this.LOGGER.info("getOrderName " + theType.toString());
    	try {
    		String str =  this._rtiAmbassador.getOrderName(theType);
    		this.LOGGER.info("getOrderName return " + str);
    		return str;
    	} catch (InvalidOrderType | FederateNotExecutionMember | NotConnected | RTIinternalError e) {
    		this.LOGGER.error("getOrderName exception" + e.getMessage());
    		throw e;
    	}
    }


    // 10.21
    public TransportationTypeHandle getTransportationTypeHandle(final String theName) throws InvalidTransportationName, FederateNotExecutionMember, NotConnected, RTIinternalError {
    	this.LOGGER.info("getTransportationTypeHandle " + theName);
    	try {
    		TransportationTypeHandle transportationTypeHandle = this._rtiAmbassador.getTransportationTypeHandle(theName);
    		this.LOGGER.info("getTransportationTypeHandle return " + transportationTypeHandle.toString());
    		return transportationTypeHandle;
    	} catch (InvalidTransportationName | FederateNotExecutionMember | NotConnected | RTIinternalError e) {
    		this.LOGGER.error("getTransportationTypeHandle exception" + e.getMessage());
    		throw e;
    	}
    }


    // 10.22
    public String getTransportationTypeName(final TransportationTypeHandle theHandle) throws InvalidTransportationType, FederateNotExecutionMember, NotConnected, RTIinternalError {
    	this.LOGGER.info("getTransportationTypeName " + theHandle.toString());
    	try {
    		String str =  this._rtiAmbassador.getTransportationTypeName(theHandle);
    		this.LOGGER.info("getTransportationTypeName return " + str);
    		return str;
    	} catch (InvalidTransportationType | FederateNotExecutionMember | NotConnected | RTIinternalError e) {
    		this.LOGGER.error("getTransportationTypeName exception" + e.getMessage());
    		throw e;
    	}
    }


    // 10.23
    public DimensionHandleSet getAvailableDimensionsForClassAttribute(final ObjectClassHandle whichClass, final AttributeHandle theHandle) throws AttributeNotDefined, InvalidAttributeHandle, InvalidObjectClassHandle, FederateNotExecutionMember, NotConnected, RTIinternalError {
    	this.LOGGER.info("getAvailableDimensionsForClassAttribute " + whichClass.toString() + " " + theHandle.toString());
    	try {
    		DimensionHandleSet dimensionHandleSet =  this._rtiAmbassador.getAvailableDimensionsForClassAttribute(whichClass, theHandle);
    		this.LOGGER.info("getAvailableDimensionsForClassAttribute return " + dimensionHandleSet.toString());
    		return dimensionHandleSet;
    	} catch (AttributeNotDefined | InvalidAttributeHandle | InvalidObjectClassHandle | FederateNotExecutionMember | NotConnected | RTIinternalError e) {
    		this.LOGGER.error("getAvailableDimensionsForClassAttribute exception" + e.getMessage());
    		throw e;
    	}
    }


    // 10.24
    public DimensionHandleSet getAvailableDimensionsForInteractionClass(final InteractionClassHandle theHandle) throws InvalidInteractionClassHandle, FederateNotExecutionMember, NotConnected, RTIinternalError {
    	this.LOGGER.info("getAvailableDimensionsForInteractionClass " + theHandle.toString());
    	try {
    		DimensionHandleSet dimensionHandleSet = this._rtiAmbassador.getAvailableDimensionsForInteractionClass(theHandle);
    		this.LOGGER.info("getAvailableDimensionsForInteractionClass return " + dimensionHandleSet.toString());
    		return dimensionHandleSet;
    	} catch (InvalidInteractionClassHandle | FederateNotExecutionMember | NotConnected | RTIinternalError e) {
    		this.LOGGER.error("getAvailableDimensionsForInteractionClass exception" + e.getMessage());
    		throw e;
    	}
    }


    // 10.25
    public DimensionHandle getDimensionHandle(final String theName) throws NameNotFound, FederateNotExecutionMember, NotConnected, RTIinternalError {
    	this.LOGGER.info("getDimensionHandle " + theName);
    	try {
    		DimensionHandle dimensionHandle = this._rtiAmbassador.getDimensionHandle(theName);
    		this.LOGGER.info("getDimensionHandle return " + dimensionHandle.toString());
    		return dimensionHandle;
    	} catch (NameNotFound | FederateNotExecutionMember | NotConnected | RTIinternalError e) {
    		this.LOGGER.error("getDimensionHandle exception" + e.getMessage());
    		throw e;
    	}
    }


    // 10.26
    public String getDimensionName(final DimensionHandle theHandle) throws InvalidDimensionHandle, FederateNotExecutionMember, NotConnected, RTIinternalError {
    	this.LOGGER.info("getDimensionName " + theHandle.toString());
    	try {
    		String str = this._rtiAmbassador.getDimensionName(theHandle);
    		this.LOGGER.info("getDimensionName return " + str);
    		return str;
    	} catch (InvalidDimensionHandle | FederateNotExecutionMember | NotConnected | RTIinternalError e) {
    		this.LOGGER.error("getDimensionName exception" + e.getMessage());
    		throw e;
    	}
    }


    // 10.27
    public long getDimensionUpperBound(final DimensionHandle theHandle) throws InvalidDimensionHandle, FederateNotExecutionMember, NotConnected, RTIinternalError {
    	this.LOGGER.info("getDimensionUpperBound " + theHandle.toString());
    	try {
    		long upperBound = this._rtiAmbassador.getDimensionUpperBound(theHandle);
    		this.LOGGER.info("getDimensionUpperBound return " + upperBound);
    		return upperBound;
    	} catch (InvalidDimensionHandle | FederateNotExecutionMember | NotConnected | RTIinternalError e) {
    		this.LOGGER.error("getDimensionUpperBound exception" + e.getMessage());
    		throw e;
    	}
    }


    // 10.28
    public DimensionHandleSet getDimensionHandleSet(final RegionHandle region) throws InvalidRegion, SaveInProgress, RestoreInProgress, FederateNotExecutionMember, NotConnected, RTIinternalError {
    	this.LOGGER.info("getDimensionHandleSet " + region.toString());
    	try {
    		DimensionHandleSet dimensionHandleSet = this._rtiAmbassador.getDimensionHandleSet(region);
    		this.LOGGER.info("getDimensionHandleSet return " + dimensionHandleSet.toString());
    		return dimensionHandleSet;
    	} catch (InvalidRegion | SaveInProgress | RestoreInProgress | FederateNotExecutionMember | NotConnected | RTIinternalError e) {
    		this.LOGGER.error("getDimensionHandleSet exception" + e.getMessage());
    		throw e;
    	}
    }


    // 10.29
    public RangeBounds getRangeBounds(final RegionHandle region, final DimensionHandle dimension) throws RegionDoesNotContainSpecifiedDimension, InvalidRegion, SaveInProgress, RestoreInProgress, FederateNotExecutionMember, NotConnected, RTIinternalError {
    	this.LOGGER.info("getRangeBounds " + region.toString() + " " + dimension.toString());
    	try {
    		RangeBounds rangeBounds = this._rtiAmbassador.getRangeBounds(region, dimension);
    		this.LOGGER.info("getRangeBounds return " + rangeBounds.toString());
    		return rangeBounds;
    	} catch (RegionDoesNotContainSpecifiedDimension | InvalidRegion | SaveInProgress | RestoreInProgress | FederateNotExecutionMember | NotConnected | RTIinternalError e) {
    		this.LOGGER.error("getRangeBounds exception" + e.getMessage());
    		throw e;
    	}
    }


    // 10.30
    public void setRangeBounds(final RegionHandle region, final DimensionHandle dimension, final RangeBounds bounds) throws InvalidRangeBound, RegionDoesNotContainSpecifiedDimension, RegionNotCreatedByThisFederate, InvalidRegion, SaveInProgress, RestoreInProgress, FederateNotExecutionMember, NotConnected, RTIinternalError {
    	this.LOGGER.info("setRangeBounds " + region.toString() + " " + dimension.toString() + " " + bounds.toString());
    	try {
    		this._rtiAmbassador.setRangeBounds(region, dimension, bounds);
    	} catch (InvalidRangeBound | RegionDoesNotContainSpecifiedDimension | RegionNotCreatedByThisFederate | InvalidRegion | SaveInProgress | RestoreInProgress | FederateNotExecutionMember | NotConnected | RTIinternalError e) {
    		this.LOGGER.error("setRangeBounds exception" + e.getMessage());
    		throw e;
    	}
    }


    // 10.31
    public long normalizeFederateHandle(final FederateHandle federateHandle) throws InvalidFederateHandle, FederateNotExecutionMember, NotConnected, RTIinternalError {
    	this.LOGGER.info("normalizeFederateHandle " + federateHandle.toString());
    	try {
    		long normalizedFederateHandle = this._rtiAmbassador.normalizeFederateHandle(federateHandle);
    		this.LOGGER.info("normalizeFederateHandle return " + normalizedFederateHandle);
    		return normalizedFederateHandle;
    	} catch (InvalidFederateHandle | FederateNotExecutionMember | NotConnected | RTIinternalError e) {
    		this.LOGGER.error("normalizeFederateHandle exception" + e.getMessage());
    		throw e;
    	}
    }


    // 10.32
    public long normalizeServiceGroup(final ServiceGroup group) throws InvalidServiceGroup, FederateNotExecutionMember, NotConnected, RTIinternalError {
    	this.LOGGER.info("normalizeServiceGroup " + group.toString());
    	try {
    		long normalizedServiceGroup = this._rtiAmbassador.normalizeServiceGroup(group);
    		this.LOGGER.info("normalizeServiceGroup return " + normalizedServiceGroup);
    		return normalizedServiceGroup;
    	} catch (InvalidServiceGroup | FederateNotExecutionMember | NotConnected | RTIinternalError e) {
    		this.LOGGER.error("normalizeServiceGroup exception" + e.getMessage());
    		throw e;
    	}
    }


    // 10.33
    public void enableObjectClassRelevanceAdvisorySwitch() throws ObjectClassRelevanceAdvisorySwitchIsOn, SaveInProgress, RestoreInProgress, FederateNotExecutionMember, NotConnected, RTIinternalError {
    	this.LOGGER.info("enableObjectClassRelevanceAdvisorySwitch");
    	try {
    		this._rtiAmbassador.enableObjectClassRelevanceAdvisorySwitch();
    	} catch (ObjectClassRelevanceAdvisorySwitchIsOn | SaveInProgress | RestoreInProgress | FederateNotExecutionMember | NotConnected | RTIinternalError e) {
    		this.LOGGER.error("enableObjectClassRelevanceAdvisorySwitch exception" + e.getMessage());
    		throw e;
    	}
    }


    // 10.34
    public void disableObjectClassRelevanceAdvisorySwitch() throws ObjectClassRelevanceAdvisorySwitchIsOff, SaveInProgress, RestoreInProgress, FederateNotExecutionMember, NotConnected, RTIinternalError {
    	this.LOGGER.info("disableObjectClassRelevanceAdvisorySwitch");
    	try {
    		this._rtiAmbassador.disableObjectClassRelevanceAdvisorySwitch();
    	} catch (ObjectClassRelevanceAdvisorySwitchIsOff | SaveInProgress | RestoreInProgress | FederateNotExecutionMember | NotConnected | RTIinternalError e) {
    		this.LOGGER.error("disableObjectClassRelevanceAdvisorySwitch exception" + e.getMessage());
    		throw e;
    	}
    }


    // 10.35
    public void enableAttributeRelevanceAdvisorySwitch() throws AttributeRelevanceAdvisorySwitchIsOn, SaveInProgress, RestoreInProgress, FederateNotExecutionMember, NotConnected, RTIinternalError {
        this.LOGGER.info("enableAttributeRelevanceAdvisorySwitch");
    	try {
        this._rtiAmbassador.enableAttributeRelevanceAdvisorySwitch();
    	} catch (AttributeRelevanceAdvisorySwitchIsOn | SaveInProgress | RestoreInProgress | FederateNotExecutionMember | NotConnected | RTIinternalError e) {
    		this.LOGGER.error("enableAttributeRelevanceAdvisorySwitch exception" + e.getMessage());
    		throw e;
    	}
    }


    // 10.36
    public void disableAttributeRelevanceAdvisorySwitch() throws AttributeRelevanceAdvisorySwitchIsOff, SaveInProgress, RestoreInProgress, FederateNotExecutionMember, NotConnected, RTIinternalError {
        this.LOGGER.info("disableAttributeRelevanceAdvisorySwitch");
    	try {
        this._rtiAmbassador.disableAttributeRelevanceAdvisorySwitch();
    	} catch (AttributeRelevanceAdvisorySwitchIsOff | SaveInProgress | RestoreInProgress | FederateNotExecutionMember | NotConnected | RTIinternalError e) {
    		this.LOGGER.error("disableAttributeRelevanceAdvisorySwitch exception" + e.getMessage());
    		throw e;
    	}
    }


    // 10.37
    public void enableAttributeScopeAdvisorySwitch() throws AttributeScopeAdvisorySwitchIsOn, SaveInProgress, RestoreInProgress, FederateNotExecutionMember, NotConnected, RTIinternalError {
        this.LOGGER.info("enableAttributeScopeAdvisorySwitch");
    	try {
        this._rtiAmbassador.enableAttributeScopeAdvisorySwitch();
    	} catch (AttributeScopeAdvisorySwitchIsOn | SaveInProgress | RestoreInProgress | FederateNotExecutionMember | NotConnected | RTIinternalError e) {
    		this.LOGGER.error("enableAttributeScopeAdvisorySwitch exception" + e.getMessage());
    		throw e;
    	}
    }


    // 10.38
    public void disableAttributeScopeAdvisorySwitch() throws AttributeScopeAdvisorySwitchIsOff, SaveInProgress, RestoreInProgress, FederateNotExecutionMember, NotConnected, RTIinternalError {
        this.LOGGER.info("disableAttributeScopeAdvisorySwitch");
    	try {
        this._rtiAmbassador.disableAttributeScopeAdvisorySwitch();
    	} catch (AttributeScopeAdvisorySwitchIsOff | SaveInProgress | RestoreInProgress | FederateNotExecutionMember | NotConnected | RTIinternalError e) {
    		this.LOGGER.error("disableAttributeScopeAdvisorySwitch exception" + e.getMessage());
    		throw e;
    	}
    }


    // 10.39
    public void enableInteractionRelevanceAdvisorySwitch() throws InteractionRelevanceAdvisorySwitchIsOn, SaveInProgress, RestoreInProgress, FederateNotExecutionMember, NotConnected, RTIinternalError {
        this.LOGGER.info("enableInteractionRelevanceAdvisorySwitch");
    	try {
        this._rtiAmbassador.enableInteractionRelevanceAdvisorySwitch();
    	} catch (InteractionRelevanceAdvisorySwitchIsOn | SaveInProgress | RestoreInProgress | FederateNotExecutionMember | NotConnected | RTIinternalError e) {
    		this.LOGGER.error("enableInteractionRelevanceAdvisorySwitch exception" + e.getMessage());
    		throw e;
    	}
    }


    // 10.40
    public void disableInteractionRelevanceAdvisorySwitch() throws InteractionRelevanceAdvisorySwitchIsOff, SaveInProgress, RestoreInProgress, FederateNotExecutionMember, NotConnected, RTIinternalError {
        this.LOGGER.info("disableInteractionRelevanceAdvisorySwitch");
    	try {
        this._rtiAmbassador.disableInteractionRelevanceAdvisorySwitch();
    	} catch (InteractionRelevanceAdvisorySwitchIsOff | SaveInProgress | RestoreInProgress | FederateNotExecutionMember | NotConnected | RTIinternalError e) {
    		this.LOGGER.error("disableInteractionRelevanceAdvisorySwitch exception" + e.getMessage());
    		throw e;
    	}
    }


    // 10.41
    public boolean evokeCallback(final double approximateMinimumTimeInSeconds) throws CallNotAllowedFromWithinCallback, RTIinternalError {
        this.LOGGER.info("evokeCallback " + approximateMinimumTimeInSeconds);
    	try {
    		boolean b = this._rtiAmbassador.evokeCallback(approximateMinimumTimeInSeconds);
    		this.LOGGER.info("evokeCallback return " + b);
    		return b;
    	} catch (CallNotAllowedFromWithinCallback |  RTIinternalError e) {
    		this.LOGGER.error("evokeCallback exception" + e.getMessage());
    		throw e;
    	}
    }


    // 10.42
    public boolean evokeMultipleCallbacks(final double approximateMinimumTimeInSeconds, final double approximateMaximumTimeInSeconds) throws CallNotAllowedFromWithinCallback, RTIinternalError {
        this.LOGGER.info("evokeMultipleCallbacks " + approximateMinimumTimeInSeconds + " " + approximateMaximumTimeInSeconds);
    	try {
    		boolean b = this._rtiAmbassador.evokeMultipleCallbacks(approximateMinimumTimeInSeconds, approximateMaximumTimeInSeconds);
    		this.LOGGER.info("evokeMultipleCallbacks return " + b);
    		return b;
    	} catch (CallNotAllowedFromWithinCallback |  RTIinternalError e) {
    		this.LOGGER.error("evokeMultipleCallbacks exception" + e.getMessage());
    		throw e;
    	}
    }


    // 10.43
    public void enableCallbacks() throws SaveInProgress, RestoreInProgress, RTIinternalError {
        this.LOGGER.info("enableCallbacks");
    	try {
        this._rtiAmbassador.enableCallbacks();
    	} catch (SaveInProgress | RestoreInProgress | RTIinternalError e) {
    		this.LOGGER.error("enableCallbacks exception" + e.getMessage());
    		throw e;
    	}
    }


    // 10.44
    public void disableCallbacks() throws SaveInProgress, RestoreInProgress, RTIinternalError {
        this.LOGGER.info("disableCallbacks");
    	try {
        this._rtiAmbassador.disableCallbacks();
    	} catch (SaveInProgress | RestoreInProgress | RTIinternalError e) {
    		this.LOGGER.error("disableCallbacks exception" + e.getMessage());
    		throw e;
    	}
    }


    //API-specific services
    public AttributeHandleFactory getAttributeHandleFactory() throws FederateNotExecutionMember, NotConnected {
        this.LOGGER.info("getAttributeHandleFactory");
    	try {
        return this._rtiAmbassador.getAttributeHandleFactory();
    	} catch (FederateNotExecutionMember |  NotConnected e) {
    		this.LOGGER.error("getAttributeHandleFactory exception" + e.getMessage());
    		throw e;
    	}
    }


    public AttributeHandleSetFactory getAttributeHandleSetFactory() throws FederateNotExecutionMember, NotConnected {
        this.LOGGER.info("getAttributeHandleSetFactory");
    	try {
        return this._rtiAmbassador.getAttributeHandleSetFactory();
    	} catch (FederateNotExecutionMember | NotConnected e) {
    		this.LOGGER.error("getAttributeHandleSetFactory exception" + e.getMessage());
    		throw e;
    	}
    }


    public AttributeHandleValueMapFactory getAttributeHandleValueMapFactory() throws FederateNotExecutionMember, NotConnected {
        this.LOGGER.info("getAttributeHandleValueMapFactory");
    	try {
        return this._rtiAmbassador.getAttributeHandleValueMapFactory();
    	} catch (FederateNotExecutionMember | NotConnected e) {
    		this.LOGGER.error("getAttributeHandleValueMapFactory exception" + e.getMessage());
    		throw e;
    	}
    }


    public AttributeSetRegionSetPairListFactory getAttributeSetRegionSetPairListFactory() throws FederateNotExecutionMember, NotConnected {
        this.LOGGER.info("getAttributeSetRegionSetPairListFactory");
    	try {
        return this._rtiAmbassador.getAttributeSetRegionSetPairListFactory();
    	} catch (FederateNotExecutionMember | NotConnected e) {
    		this.LOGGER.error("getAttributeSetRegionSetPairListFactory exception" + e.getMessage());
    		throw e;
    	}
    }


    public DimensionHandleFactory getDimensionHandleFactory() throws FederateNotExecutionMember, NotConnected {
        this.LOGGER.info("getDimensionHandleFactory");
    	try {
        return this._rtiAmbassador.getDimensionHandleFactory();
    	} catch (FederateNotExecutionMember | NotConnected e) {
    		this.LOGGER.error("getDimensionHandleFactory exception" + e.getMessage());
    		throw e;
    	}
    }


    public DimensionHandleSetFactory getDimensionHandleSetFactory() throws FederateNotExecutionMember, NotConnected {
        this.LOGGER.info("getDimensionHandleSetFactory");
    	try {
        return this._rtiAmbassador.getDimensionHandleSetFactory();
    	} catch (FederateNotExecutionMember | NotConnected e) {
    		this.LOGGER.error("getDimensionHandleSetFactory exception" + e.getMessage());
    		throw e;
    	}
    }


    public FederateHandleFactory getFederateHandleFactory() throws FederateNotExecutionMember, NotConnected {
        this.LOGGER.info("getFederateHandleFactory");
    	try {
        return this._rtiAmbassador.getFederateHandleFactory();
    	} catch (FederateNotExecutionMember | NotConnected e) {
    		this.LOGGER.error("getFederateHandleFactory exception" + e.getMessage());
    		throw e;
    	}
    }


    public FederateHandleSetFactory getFederateHandleSetFactory() throws FederateNotExecutionMember, NotConnected {
        this.LOGGER.info("getFederateHandleSetFactory");
    	try {
        return this._rtiAmbassador.getFederateHandleSetFactory();
    	} catch (FederateNotExecutionMember | NotConnected e) {
    		this.LOGGER.error("getFederateHandleSetFactory exception" + e.getMessage());
    		throw e;
    	}
    }


    public InteractionClassHandleFactory getInteractionClassHandleFactory() throws FederateNotExecutionMember, NotConnected {
        this.LOGGER.info("getInteractionClassHandleFactory");
    	try {
        return this._rtiAmbassador.getInteractionClassHandleFactory();
    	} catch (FederateNotExecutionMember | NotConnected e) {
    		this.LOGGER.error("getInteractionClassHandleFactory exception" + e.getMessage());
    		throw e;
    	}
    }


    public ObjectClassHandleFactory getObjectClassHandleFactory() throws FederateNotExecutionMember, NotConnected {
        this.LOGGER.info("getObjectClassHandleFactory");
    	try {
        return this._rtiAmbassador.getObjectClassHandleFactory();
    	} catch (FederateNotExecutionMember | NotConnected e) {
    		this.LOGGER.error("getObjectClassHandleFactory exception" + e.getMessage());
    		throw e;
    	}
    }


    public ObjectInstanceHandleFactory getObjectInstanceHandleFactory() throws FederateNotExecutionMember, NotConnected {
        this.LOGGER.info("getObjectInstanceHandleFactory");
    	try {
        return this._rtiAmbassador.getObjectInstanceHandleFactory();
    	} catch (FederateNotExecutionMember | NotConnected e) {
    		this.LOGGER.error("getObjectInstanceHandleFactory exception" + e.getMessage());
    		throw e;
    	}
    }


    public ParameterHandleFactory getParameterHandleFactory() throws FederateNotExecutionMember, NotConnected {
        this.LOGGER.info("getParameterHandleFactory");
    	try {
        return this._rtiAmbassador.getParameterHandleFactory();
    	} catch (FederateNotExecutionMember | NotConnected e) {
    		this.LOGGER.error("getParameterHandleFactory exception" + e.getMessage());
    		throw e;
    	}
    }


    public ParameterHandleValueMapFactory getParameterHandleValueMapFactory() throws FederateNotExecutionMember, NotConnected {
        this.LOGGER.info("getParameterHandleValueMapFactory");
    	try {
        return this._rtiAmbassador.getParameterHandleValueMapFactory();
    	} catch (FederateNotExecutionMember | NotConnected e) {
    		this.LOGGER.error("getParameterHandleValueMapFactory exception" + e.getMessage());
    		throw e;
    	}
    }


    public RegionHandleSetFactory getRegionHandleSetFactory() throws FederateNotExecutionMember, NotConnected {
        this.LOGGER.info("getRegionHandleSetFactory");
    	try {
        return this._rtiAmbassador.getRegionHandleSetFactory();
    	} catch (FederateNotExecutionMember | NotConnected e) {
    		this.LOGGER.error("getRegionHandleSetFactory exception" + e.getMessage());
    		throw e;
    	}
    }


    public TransportationTypeHandleFactory getTransportationTypeHandleFactory() throws FederateNotExecutionMember, NotConnected {
        this.LOGGER.info("getTransportationTypeHandleFactory");
    	try {
        return this._rtiAmbassador.getTransportationTypeHandleFactory();
    	} catch (FederateNotExecutionMember | NotConnected e) {
    		this.LOGGER.error("getTransportationTypeHandleFactory exception" + e.getMessage());
    		throw e;
    	}
    }


    public String getHLAversion() {
        this.LOGGER.info("getHLAversion");
        String str = this._rtiAmbassador.getHLAversion();
		this.LOGGER.error("getHLAversion return " + str);
        return str;
    }


    public LogicalTimeFactory getTimeFactory() throws FederateNotExecutionMember, NotConnected {
        this.LOGGER.info("getTimeFactory");
    	try {
        return this._rtiAmbassador.getTimeFactory();
    	} catch (FederateNotExecutionMember | NotConnected e) {
    		this.LOGGER.error("getTimeFactory exception" + e.getMessage());
    		throw e;
    	}
    }
}
