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
public class IVCT_RTIambassador {
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
        catch (ConnectionFailed | InvalidLocalSettingsDesignator | UnsupportedCallbackModel | AlreadyConnected | CallNotAllowedFromWithinCallback | RTIinternalError e3) {
            // TODO Auto-generated catch block
            e3.printStackTrace();
        }

        // Create federation execution using tc_param foms
        try {
            this.createFederationExecution(tcParam.getFederationName(), tcParam.getUrls(), "HLAfloat64Time");
        }
        catch (final FederationExecutionAlreadyExists e3) {
            this.LOGGER.info("initiateRti: FederationExecutionAlreadyExists (ignored)");
        }
        catch (CouldNotCreateLogicalTimeFactory | InconsistentFDD | ErrorReadingFDD | CouldNotOpenFDD | NotConnected | RTIinternalError e3) {
            // TODO Auto-generated catch block
            e3.printStackTrace();
        }

        // Join federation execution
        try {
            //            return this.joinFederationExecution("TmrObserver", tcParam.getFederationName(), tcParam.getUrls());
            return this.joinFederationExecution(federateName, tcParam.getFederationName(), tcParam.getUrls());
        }
        catch (CouldNotCreateLogicalTimeFactory | FederationExecutionDoesNotExist | InconsistentFDD | ErrorReadingFDD | CouldNotOpenFDD | SaveInProgress | RestoreInProgress | FederateAlreadyExecutionMember | NotConnected | CallNotAllowedFromWithinCallback | RTIinternalError e3) {
            // TODO Auto-generated catch block
            e3.printStackTrace();
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
        catch (InvalidResignAction | OwnershipAcquisitionPending | FederateOwnsAttributes | FederateNotExecutionMember | NotConnected | CallNotAllowedFromWithinCallback | RTIinternalError e3) {
            // TODO Auto-generated catch block
            e3.printStackTrace();
        }

        // Destroy federation execution
        try {
            this.destroyFederationExecution(tcParam.getFederationName());
        }
        catch (final FederatesCurrentlyJoined e1) {
            this.LOGGER.info("terminateRti: FederatesCurrentlyJoined (ignored)");
        }
        catch (FederationExecutionDoesNotExist | NotConnected | RTIinternalError e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }

        // Disconnect from rti
        try {
            this.disconnect();
        }
        catch (FederateIsExecutionMember | CallNotAllowedFromWithinCallback | RTIinternalError e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
    }


    // 4.2
    /**
     * @param federateReference a reference to a user programmed callback
     * @param callbackModel the type of callback
     * @param localSettingsDesignator the settings for the rti
     */
    public void connect(final FederateAmbassador federateReference, final CallbackModel callbackModel, final String localSettingsDesignator) throws ConnectionFailed, InvalidLocalSettingsDesignator, UnsupportedCallbackModel, AlreadyConnected, CallNotAllowedFromWithinCallback, RTIinternalError {
        this.LOGGER.info("connect " + federateReference.toString() + " " + callbackModel.toString() + " " + localSettingsDesignator);
        this._fedAmbassador = federateReference;
        this._rtiAmbassador.connect(federateReference, callbackModel, localSettingsDesignator);
    }


    // 4.2
    /**
     * @param federateReference a reference to a user programmed callback
     * @param callbackModel the type of callback
     */
    public void connect(final FederateAmbassador federateReference, final CallbackModel callbackModel) throws ConnectionFailed, InvalidLocalSettingsDesignator, UnsupportedCallbackModel, AlreadyConnected, CallNotAllowedFromWithinCallback, RTIinternalError {
        this.LOGGER.info("connect " + federateReference.toString() + " " + callbackModel.toString());
        this._rtiAmbassador.connect(federateReference, callbackModel);
    }


    // 4.3
    /**
     * @throws FederateIsExecutionMember
     * @throws CallNotAllowedFromWithinCallback
     * @throws RTIinternalError
     */
    public void disconnect() throws FederateIsExecutionMember, CallNotAllowedFromWithinCallback, RTIinternalError {
        this.LOGGER.info("disconnect");
        this._rtiAmbassador.disconnect();
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
        this._rtiAmbassador.createFederationExecution(federationExecutionName, fomModules, mimModule, logicalTimeImplementationName);
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
        this._rtiAmbassador.createFederationExecution(federationExecutionName, fomModules, logicalTimeImplementationName);
    }


    //4.5
    public void createFederationExecution(final String federationExecutionName, final URL[] fomModules, final URL mimModule) throws InconsistentFDD, ErrorReadingFDD, CouldNotOpenFDD, ErrorReadingMIM, CouldNotOpenMIM, DesignatorIsHLAstandardMIM, FederationExecutionAlreadyExists, NotConnected, RTIinternalError {
        this.LOGGER.info("createFederationExecution " + federationExecutionName + " " + Arrays.toString(fomModules) + " " + mimModule.toString());
        this._rtiAmbassador.createFederationExecution(federationExecutionName, fomModules, mimModule);
    }


    //4.5
    public void createFederationExecution(final String federationExecutionName, final URL[] fomModules) throws InconsistentFDD, ErrorReadingFDD, CouldNotOpenFDD, FederationExecutionAlreadyExists, NotConnected, RTIinternalError {
        this.LOGGER.info("createFederationExecution " + federationExecutionName + " " + Arrays.toString(fomModules));
        this._rtiAmbassador.createFederationExecution(federationExecutionName, fomModules);
    }


    //4.5
    public void createFederationExecution(final String federationExecutionName, final URL fomModule) throws InconsistentFDD, ErrorReadingFDD, CouldNotOpenFDD, FederationExecutionAlreadyExists, NotConnected, RTIinternalError {
        this.LOGGER.info("createFederationExecution " + federationExecutionName + " " + fomModule.toString());
        this._rtiAmbassador.createFederationExecution(federationExecutionName, fomModule);
    }


    //4.6
    public void destroyFederationExecution(final String federationExecutionName) throws FederatesCurrentlyJoined, FederationExecutionDoesNotExist, NotConnected, RTIinternalError {
        this.LOGGER.info("destroyFederationExecution " + federationExecutionName);
        this._rtiAmbassador.destroyFederationExecution(federationExecutionName);
    }


    // 4.7
    public void listFederationExecutions() throws NotConnected, RTIinternalError {
        this.LOGGER.info("listFederationExecutions");
        this._rtiAmbassador.listFederationExecutions();
    }


    //4.9
    public FederateHandle joinFederationExecution(final String federateName, final String federateType, final String federationExecutionName, final URL[] additionalFomModules) throws CouldNotCreateLogicalTimeFactory, FederateNameAlreadyInUse, FederationExecutionDoesNotExist, InconsistentFDD, ErrorReadingFDD, CouldNotOpenFDD, SaveInProgress, RestoreInProgress, FederateAlreadyExecutionMember, NotConnected, CallNotAllowedFromWithinCallback, RTIinternalError {
        this.LOGGER.info("joinFederationExecution " + federateName + " " + federateType + " " + federationExecutionName + " " + Arrays.toString(additionalFomModules));
        this.myFederateHandle = this._rtiAmbassador.joinFederationExecution(federateName, federateType, federationExecutionName, additionalFomModules);
        this.LOGGER.info("joinFederationExecution return " + this.myFederateHandle.toString());
        return this.myFederateHandle;
    }


    //4.9
    public FederateHandle joinFederationExecution(final String federateType, final String federationExecutionName, final URL[] additionalFomModules) throws CouldNotCreateLogicalTimeFactory, FederationExecutionDoesNotExist, InconsistentFDD, ErrorReadingFDD, CouldNotOpenFDD, SaveInProgress, RestoreInProgress, FederateAlreadyExecutionMember, NotConnected, CallNotAllowedFromWithinCallback, RTIinternalError {
        this.LOGGER.info("joinFederationExecution " + federateType + " " + federationExecutionName + " " + Arrays.toString(additionalFomModules));
        final FederateHandle federateHandle = this._rtiAmbassador.joinFederationExecution(federateType, federationExecutionName, additionalFomModules);
        this.LOGGER.info("joinFederationExecution return " + federateHandle.toString());
        return federateHandle;
    }


    //4.9
    public FederateHandle joinFederationExecution(final String federateName, final String federateType, final String federationExecutionName) throws CouldNotCreateLogicalTimeFactory, FederateNameAlreadyInUse, FederationExecutionDoesNotExist, SaveInProgress, RestoreInProgress, FederateAlreadyExecutionMember, NotConnected, CallNotAllowedFromWithinCallback, RTIinternalError {
        this.LOGGER.info("joinFederationExecution " + federateName + " " + federateType + " " + federationExecutionName);
        final FederateHandle federateHandle = this._rtiAmbassador.joinFederationExecution(federateName, federateType, federationExecutionName);
        this.LOGGER.info("joinFederationExecution return " + federateHandle.toString());
        return federateHandle;
    }


    //4.9
    public FederateHandle joinFederationExecution(final String federateType, final String federationExecutionName) throws CouldNotCreateLogicalTimeFactory, FederationExecutionDoesNotExist, SaveInProgress, RestoreInProgress, FederateAlreadyExecutionMember, NotConnected, CallNotAllowedFromWithinCallback, RTIinternalError {
        this.LOGGER.info("joinFederationExecution " + federateType + " " + federationExecutionName);
        final FederateHandle federateHandle = this._rtiAmbassador.joinFederationExecution(federateType, federationExecutionName);
        this.LOGGER.info("joinFederationExecution return " + federateHandle.toString());
        return federateHandle;
    }


    //4.10
    public void resignFederationExecution(final ResignAction resignAction) throws InvalidResignAction, OwnershipAcquisitionPending, FederateOwnsAttributes, FederateNotExecutionMember, NotConnected, CallNotAllowedFromWithinCallback, RTIinternalError {
        this.LOGGER.info("resignFederationExecution " + resignAction.toString());
        this._rtiAmbassador.resignFederationExecution(resignAction);
    }


    //4.11
    public void registerFederationSynchronizationPoint(final String synchronizationPointLabel, final byte[] userSuppliedTag) throws SaveInProgress, RestoreInProgress, FederateNotExecutionMember, NotConnected, RTIinternalError {
        this.LOGGER.info("registerFederationSynchronizationPoint " + synchronizationPointLabel + " " + Arrays.toString(userSuppliedTag));
        this._rtiAmbassador.registerFederationSynchronizationPoint(synchronizationPointLabel, userSuppliedTag);
    }


    //4.11
    public void registerFederationSynchronizationPoint(final String synchronizationPointLabel, final byte[] userSuppliedTag, final FederateHandleSet synchronizationSet) throws InvalidFederateHandle, SaveInProgress, RestoreInProgress, FederateNotExecutionMember, NotConnected, RTIinternalError {
        this.LOGGER.info("registerFederationSynchronizationPoint " + synchronizationPointLabel + " " + Arrays.toString(userSuppliedTag) + " " + synchronizationSet.toString());
        this._rtiAmbassador.registerFederationSynchronizationPoint(synchronizationPointLabel, userSuppliedTag, synchronizationSet);
    }


    //4.14
    public void synchronizationPointAchieved(final String synchronizationPointLabel) throws SynchronizationPointLabelNotAnnounced, SaveInProgress, RestoreInProgress, FederateNotExecutionMember, NotConnected, RTIinternalError {
        this.LOGGER.info("synchronizationPointAchieved " + synchronizationPointLabel);
        this._rtiAmbassador.synchronizationPointAchieved(synchronizationPointLabel);
    }


    //4.14
    public void synchronizationPointAchieved(final String synchronizationPointLabel, final boolean successIndicator) throws SynchronizationPointLabelNotAnnounced, SaveInProgress, RestoreInProgress, FederateNotExecutionMember, NotConnected, RTIinternalError {
        this.LOGGER.info("synchronizationPointAchieved " + synchronizationPointLabel + " " + successIndicator);
        this._rtiAmbassador.synchronizationPointAchieved(synchronizationPointLabel, successIndicator);
    }


    // 4.16
    public void requestFederationSave(final String label) throws SaveInProgress, RestoreInProgress, FederateNotExecutionMember, NotConnected, RTIinternalError {
        this.LOGGER.info("requestFederationSave " + label);
        this._rtiAmbassador.requestFederationSave(label);
    }


    // 4.16
    public void requestFederationSave(final String label, final LogicalTime theTime) throws LogicalTimeAlreadyPassed, InvalidLogicalTime, FederateUnableToUseTime, SaveInProgress, RestoreInProgress, FederateNotExecutionMember, NotConnected, RTIinternalError {
        this.LOGGER.info("requestFederationSave " + label + " " + theTime.toString());
        this._rtiAmbassador.requestFederationSave(label, theTime);
    }


    // 4.18
    public void federateSaveBegun() throws SaveNotInitiated, RestoreInProgress, FederateNotExecutionMember, NotConnected, RTIinternalError {
        this.LOGGER.info("federateSaveBegun");
        this._rtiAmbassador.federateSaveBegun();
    }


    // 4.19
    public void federateSaveComplete() throws FederateHasNotBegunSave, RestoreInProgress, FederateNotExecutionMember, NotConnected, RTIinternalError {
        this.LOGGER.info("federateSaveComplete");
        this._rtiAmbassador.federateSaveComplete();
    }


    // 4.19
    public void federateSaveNotComplete() throws FederateHasNotBegunSave, RestoreInProgress, FederateNotExecutionMember, NotConnected, RTIinternalError {
        this.LOGGER.info("federateSaveNotComplete");
        this._rtiAmbassador.federateSaveNotComplete();
    }


    // 4.21
    public void abortFederationSave() throws SaveNotInProgress, FederateNotExecutionMember, NotConnected, RTIinternalError {
        this.LOGGER.info("abortFederationSave");
        this._rtiAmbassador.abortFederationSave();
    }


    // 4.22
    public void queryFederationSaveStatus() throws RestoreInProgress, FederateNotExecutionMember, NotConnected, RTIinternalError {
        this.LOGGER.info("queryFederationSaveStatus");
        this._rtiAmbassador.queryFederationSaveStatus();
    }


    // 4.24
    public void requestFederationRestore(final String label) throws SaveInProgress, RestoreInProgress, FederateNotExecutionMember, NotConnected, RTIinternalError {
        this.LOGGER.info("requestFederationRestore " + label);
        this._rtiAmbassador.requestFederationRestore(label);
    }


    // 4.28
    public void federateRestoreComplete() throws RestoreNotRequested, SaveInProgress, FederateNotExecutionMember, NotConnected, RTIinternalError {
        this.LOGGER.info("federateRestoreComplete");
        this._rtiAmbassador.federateRestoreComplete();
    }


    // 4.28
    public void federateRestoreNotComplete() throws RestoreNotRequested, SaveInProgress, FederateNotExecutionMember, NotConnected, RTIinternalError {
        this.LOGGER.info("federateRestoreNotComplete");
        this._rtiAmbassador.federateRestoreNotComplete();
    }


    // 4.30
    public void abortFederationRestore() throws RestoreNotInProgress, FederateNotExecutionMember, NotConnected, RTIinternalError {
        this.LOGGER.info("abortFederationRestore");
        this._rtiAmbassador.abortFederationRestore();
    }


    // 4.31
    public void queryFederationRestoreStatus() throws SaveInProgress, FederateNotExecutionMember, NotConnected, RTIinternalError {
        this.LOGGER.info("queryFederationRestoreStatus");
        this._rtiAmbassador.queryFederationRestoreStatus();
    }

    /////////////////////////////////////
    // Declaration Management Services //
    /////////////////////////////////////


    // 5.2
    public void publishObjectClassAttributes(final ObjectClassHandle theClass, final AttributeHandleSet attributeList) throws AttributeNotDefined, ObjectClassNotDefined, SaveInProgress, RestoreInProgress, FederateNotExecutionMember, NotConnected, RTIinternalError {
        this.LOGGER.info("publishObjectClassAttributes " + theClass.toString() + " " + attributeList.toString());
        this._rtiAmbassador.publishObjectClassAttributes(theClass, attributeList);
    }


    // 5.3
    public void unpublishObjectClass(final ObjectClassHandle theClass) throws OwnershipAcquisitionPending, ObjectClassNotDefined, SaveInProgress, RestoreInProgress, FederateNotExecutionMember, NotConnected, RTIinternalError {
        this.LOGGER.info("unpublishObjectClass " + theClass.toString());
        this._rtiAmbassador.unsubscribeObjectClass(theClass);
    }


    // 5.3
    public void unpublishObjectClassAttributes(final ObjectClassHandle theClass, final AttributeHandleSet attributeList) throws OwnershipAcquisitionPending, AttributeNotDefined, ObjectClassNotDefined, SaveInProgress, RestoreInProgress, FederateNotExecutionMember, NotConnected, RTIinternalError {
        this.LOGGER.info("unpublishObjectClassAttributes " + theClass.toString() + " " + attributeList.toString());
        this._rtiAmbassador.unsubscribeObjectClassAttributes(theClass, attributeList);
    }


    // 5.4
    public void publishInteractionClass(final InteractionClassHandle theInteraction) throws InteractionClassNotDefined, SaveInProgress, RestoreInProgress, FederateNotExecutionMember, NotConnected, RTIinternalError {
        this.LOGGER.info("publishInteractionClass " + theInteraction.toString());
        this._rtiAmbassador.publishInteractionClass(theInteraction);
    }


    // 5.5
    public void unpublishInteractionClass(final InteractionClassHandle theInteraction) throws InteractionClassNotDefined, SaveInProgress, RestoreInProgress, FederateNotExecutionMember, NotConnected, RTIinternalError {
        this.LOGGER.info("unpublishInteractionClass " + " " + theInteraction.toString());
        this._rtiAmbassador.unpublishInteractionClass(theInteraction);
        ;
    }


    // 5.6
    public void subscribeObjectClassAttributes(final ObjectClassHandle theClass, final AttributeHandleSet attributeList) throws AttributeNotDefined, ObjectClassNotDefined, SaveInProgress, RestoreInProgress, FederateNotExecutionMember, NotConnected, RTIinternalError {
        this.LOGGER.info("subscribeObjectClassAttributes " + theClass.toString() + attributeList.toString());
        this._rtiAmbassador.subscribeObjectClassAttributes(theClass, attributeList);
    }


    // 5.6
    public void subscribeObjectClassAttributes(final ObjectClassHandle theClass, final AttributeHandleSet attributeList, final String updateRateDesignator) throws AttributeNotDefined, ObjectClassNotDefined, InvalidUpdateRateDesignator, SaveInProgress, RestoreInProgress, FederateNotExecutionMember, NotConnected, RTIinternalError {
        this.LOGGER.info("subscribeObjectClassAttributes " + theClass.toString() + attributeList.toString() + " " + updateRateDesignator);
        this._rtiAmbassador.subscribeObjectClassAttributes(theClass, attributeList, updateRateDesignator);
    }


    // 5.6
    public void subscribeObjectClassAttributesPassively(final ObjectClassHandle theClass, final AttributeHandleSet attributeList) throws AttributeNotDefined, ObjectClassNotDefined, SaveInProgress, RestoreInProgress, FederateNotExecutionMember, NotConnected, RTIinternalError {
        this.LOGGER.info("subscribeObjectClassAttributesPassively " + theClass.toString() + attributeList.toString());
        this._rtiAmbassador.subscribeObjectClassAttributesPassively(theClass, attributeList);
    }


    // 5.6
    public void subscribeObjectClassAttributesPassively(final ObjectClassHandle theClass, final AttributeHandleSet attributeList, final String updateRateDesignator) throws AttributeNotDefined, ObjectClassNotDefined, InvalidUpdateRateDesignator, SaveInProgress, RestoreInProgress, FederateNotExecutionMember, NotConnected, RTIinternalError {
        this.LOGGER.info("subscribeObjectClassAttributesPassively " + theClass.toString() + attributeList.toString() + " " + updateRateDesignator);
        this._rtiAmbassador.subscribeObjectClassAttributesPassively(theClass, attributeList, updateRateDesignator);
    }


    // 5.7
    public void unsubscribeObjectClass(final ObjectClassHandle theClass) throws ObjectClassNotDefined, SaveInProgress, RestoreInProgress, FederateNotExecutionMember, NotConnected, RTIinternalError {
        this.LOGGER.info("unsubscribeObjectClass " + theClass.toString());
        this._rtiAmbassador.unsubscribeObjectClass(theClass);
    }


    // 5.7
    public void unsubscribeObjectClassAttributes(final ObjectClassHandle theClass, final AttributeHandleSet attributeList) throws AttributeNotDefined, ObjectClassNotDefined, SaveInProgress, RestoreInProgress, FederateNotExecutionMember, NotConnected, RTIinternalError {
        this.LOGGER.info("unsubscribeObjectClassAttributes " + theClass.toString() + " " + attributeList.toString());
        this._rtiAmbassador.unsubscribeObjectClassAttributes(theClass, attributeList);
    }


    // 5.8
    public void subscribeInteractionClass(final InteractionClassHandle theClass) throws FederateServiceInvocationsAreBeingReportedViaMOM, InteractionClassNotDefined, SaveInProgress, RestoreInProgress, FederateNotExecutionMember, NotConnected, RTIinternalError {
        this.LOGGER.info("subscribeInteractionClass " + theClass.toString());
        this._rtiAmbassador.subscribeInteractionClass(theClass);
    }


    // 5.8
    public void subscribeInteractionClassPassively(final InteractionClassHandle theClass) throws FederateServiceInvocationsAreBeingReportedViaMOM, InteractionClassNotDefined, SaveInProgress, RestoreInProgress, FederateNotExecutionMember, NotConnected, RTIinternalError {
        this.LOGGER.info("subscribeInteractionClassPassively " + theClass.toString());
        this._rtiAmbassador.subscribeInteractionClassPassively(theClass);
    }


    // 5.9
    public void unsubscribeInteractionClass(final InteractionClassHandle theClass) throws InteractionClassNotDefined, SaveInProgress, RestoreInProgress, FederateNotExecutionMember, NotConnected, RTIinternalError {
        this.LOGGER.info("unsubscribeInteractionClass " + theClass.toString());
        this._rtiAmbassador.unsubscribeInteractionClass(theClass);
    }

    ////////////////////////////////
    // Object Management Services //
    ////////////////////////////////


    // 6.2
    public void reserveObjectInstanceName(final String theObjectName) throws IllegalName, SaveInProgress, RestoreInProgress, FederateNotExecutionMember, NotConnected, RTIinternalError {
        this.LOGGER.info("reserveObjectInstanceName " + theObjectName);
        this._rtiAmbassador.reserveObjectInstanceName(theObjectName);
    }


    // 6.4
    public void releaseObjectInstanceName(final String theObjectInstanceName) throws ObjectInstanceNameNotReserved, SaveInProgress, RestoreInProgress, FederateNotExecutionMember, NotConnected, RTIinternalError {
        this.LOGGER.info("releaseObjectInstanceName " + theObjectInstanceName);
        this._rtiAmbassador.releaseObjectInstanceName(theObjectInstanceName);
    }


    // 6.5
    public void reserveMultipleObjectInstanceName(final Set<String> theObjectNames) throws IllegalName, NameSetWasEmpty, SaveInProgress, RestoreInProgress, FederateNotExecutionMember, NotConnected, RTIinternalError {
        this.LOGGER.info("reserveMultipleObjectInstanceName " + theObjectNames.toString());
        this._rtiAmbassador.reserveMultipleObjectInstanceName(theObjectNames);
    }


    // 6.7
    public void releaseMultipleObjectInstanceName(final Set<String> theObjectNames) throws ObjectInstanceNameNotReserved, SaveInProgress, RestoreInProgress, FederateNotExecutionMember, NotConnected, RTIinternalError {
        this.LOGGER.info("releaseMultipleObjectInstanceName " + theObjectNames.toString());
        this._rtiAmbassador.releaseMultipleObjectInstanceName(theObjectNames);
    }


    // 6.8
    public ObjectInstanceHandle registerObjectInstance(final ObjectClassHandle theClass) throws ObjectClassNotPublished, ObjectClassNotDefined, SaveInProgress, RestoreInProgress, FederateNotExecutionMember, NotConnected, RTIinternalError {
        this.LOGGER.info("registerObjectInstance " + theClass.toString());
        final ObjectInstanceHandle objectInstanceHandle = this._rtiAmbassador.registerObjectInstance(theClass);
        this.LOGGER.info("registerObjectInstance return " + objectInstanceHandle.toString());
        return objectInstanceHandle;
    }


    // 6.8
    public ObjectInstanceHandle registerObjectInstance(final ObjectClassHandle theClass, final String theObjectName) throws ObjectInstanceNameInUse, ObjectInstanceNameNotReserved, ObjectClassNotPublished, ObjectClassNotDefined, SaveInProgress, RestoreInProgress, FederateNotExecutionMember, NotConnected, RTIinternalError {
        this.LOGGER.info("registerObjectInstance " + theClass.toString() + " " + theObjectName);
        final ObjectInstanceHandle objectInstanceHandle = this._rtiAmbassador.registerObjectInstance(theClass, theObjectName);
        this.LOGGER.info("registerObjectInstance return " + objectInstanceHandle.toString());
        return objectInstanceHandle;
    }


    // 6.10
    public void updateAttributeValues(final ObjectInstanceHandle theObject, final AttributeHandleValueMap theAttributes, final byte[] userSuppliedTag) throws AttributeNotOwned, AttributeNotDefined, ObjectInstanceNotKnown, SaveInProgress, RestoreInProgress, FederateNotExecutionMember, NotConnected, RTIinternalError {
        this.LOGGER.info("updateAttributeValues " + theObject.toString() + " " + theAttributes.toString() + " " + Arrays.toString(userSuppliedTag));
        this._rtiAmbassador.updateAttributeValues(theObject, theAttributes, userSuppliedTag);
    }


    // 6.10
    public MessageRetractionReturn updateAttributeValues(final ObjectInstanceHandle theObject, final AttributeHandleValueMap theAttributes, final byte[] userSuppliedTag, final LogicalTime theTime) throws InvalidLogicalTime, AttributeNotOwned, AttributeNotDefined, ObjectInstanceNotKnown, SaveInProgress, RestoreInProgress, FederateNotExecutionMember, NotConnected, RTIinternalError {
        this.LOGGER.info("updateAttributeValues " + theObject.toString() + " " + theAttributes.toString() + " " + Arrays.toString(userSuppliedTag) + " " + theTime.toString());
        final MessageRetractionReturn messageRetractionReturn = this._rtiAmbassador.updateAttributeValues(theObject, theAttributes, userSuppliedTag, theTime);
        this.LOGGER.info("updateAttributeValues return " + messageRetractionReturn.toString());
        return messageRetractionReturn;
    }


    // 6.12
    public void sendInteraction(final InteractionClassHandle theInteraction, final ParameterHandleValueMap theParameters, final byte[] userSuppliedTag) throws InteractionClassNotPublished, InteractionParameterNotDefined, InteractionClassNotDefined, SaveInProgress, RestoreInProgress, FederateNotExecutionMember, NotConnected, RTIinternalError {
        this.LOGGER.info("sendInteraction " + theInteraction.toString() + " " + theParameters.toString() + " " + Arrays.toString(userSuppliedTag));
        this._rtiAmbassador.sendInteraction(theInteraction, theParameters, userSuppliedTag);
    }


    // 6.12
    public MessageRetractionReturn sendInteraction(final InteractionClassHandle theInteraction, final ParameterHandleValueMap theParameters, final byte[] userSuppliedTag, final LogicalTime theTime) throws InvalidLogicalTime, InteractionClassNotPublished, InteractionParameterNotDefined, InteractionClassNotDefined, SaveInProgress, RestoreInProgress, FederateNotExecutionMember, NotConnected, RTIinternalError {
        this.LOGGER.info("sendInteraction " + theInteraction.toString() + " " + theParameters.toString() + " " + Arrays.toString(userSuppliedTag) + " " + theTime.toString());
        final MessageRetractionReturn messageRetractionReturn = this._rtiAmbassador.sendInteraction(theInteraction, theParameters, userSuppliedTag, theTime);
        this.LOGGER.info("sendInteraction return " + messageRetractionReturn.toString());
        return messageRetractionReturn;
    }


    // 6.14
    public void deleteObjectInstance(final ObjectInstanceHandle objectHandle, final byte[] userSuppliedTag) throws DeletePrivilegeNotHeld, ObjectInstanceNotKnown, SaveInProgress, RestoreInProgress, FederateNotExecutionMember, NotConnected, RTIinternalError {
        this.LOGGER.info("deleteObjectInstance " + objectHandle.toString() + " " + Arrays.toString(userSuppliedTag));
        this._rtiAmbassador.deleteObjectInstance(objectHandle, userSuppliedTag);
    }


    // 6.14
    public MessageRetractionReturn deleteObjectInstance(final ObjectInstanceHandle objectHandle, final byte[] userSuppliedTag, final LogicalTime theTime) throws InvalidLogicalTime, DeletePrivilegeNotHeld, ObjectInstanceNotKnown, SaveInProgress, RestoreInProgress, FederateNotExecutionMember, NotConnected, RTIinternalError {
        this.LOGGER.info("deleteObjectInstance " + objectHandle.toString() + " " + Arrays.toString(userSuppliedTag) + " " + theTime.toString());
        final MessageRetractionReturn messageRetractionReturn = this._rtiAmbassador.deleteObjectInstance(objectHandle, userSuppliedTag, theTime);
        this.LOGGER.info("deleteObjectInstance return " + messageRetractionReturn.toString());
        return messageRetractionReturn;
    }


    // 6.16
    public void localDeleteObjectInstance(final ObjectInstanceHandle objectHandle) throws OwnershipAcquisitionPending, FederateOwnsAttributes, ObjectInstanceNotKnown, SaveInProgress, RestoreInProgress, FederateNotExecutionMember, NotConnected, RTIinternalError {
        this.LOGGER.info("localDeleteObjectInstance " + objectHandle.toString());
        this._rtiAmbassador.localDeleteObjectInstance(objectHandle);
    }


    // 6.19
    public void requestAttributeValueUpdate(final ObjectInstanceHandle theObject, final AttributeHandleSet theAttributes, final byte[] userSuppliedTag) throws AttributeNotDefined, ObjectInstanceNotKnown, SaveInProgress, RestoreInProgress, FederateNotExecutionMember, NotConnected, RTIinternalError {
        this.LOGGER.info("requestAttributeValueUpdate " + theObject.toString() + " " + theAttributes.toString() + " " + Arrays.toString(userSuppliedTag));
        this._rtiAmbassador.requestAttributeValueUpdate(theObject, theAttributes, userSuppliedTag);
    }


    // 6.19
    public void requestAttributeValueUpdate(final ObjectClassHandle theClass, final AttributeHandleSet theAttributes, final byte[] userSuppliedTag) throws AttributeNotDefined, ObjectClassNotDefined, SaveInProgress, RestoreInProgress, FederateNotExecutionMember, NotConnected, RTIinternalError {
        this.LOGGER.info("requestAttributeValueUpdate " + theClass.toString() + " " + theAttributes.toString() + " " + Arrays.toString(userSuppliedTag));
        this._rtiAmbassador.requestAttributeValueUpdate(theClass, theAttributes, userSuppliedTag);
    }


    // 6.23
    public void requestAttributeTransportationTypeChange(final ObjectInstanceHandle theObject, final AttributeHandleSet theAttributes, final TransportationTypeHandle theType) throws AttributeAlreadyBeingChanged, AttributeNotOwned, AttributeNotDefined, ObjectInstanceNotKnown, InvalidTransportationType, SaveInProgress, RestoreInProgress, FederateNotExecutionMember, NotConnected, RTIinternalError {
        this.LOGGER.info("requestAttributeTransportationTypeChange " + theObject.toString() + " " + theAttributes.toString() + " " + theType.toString());
        this._rtiAmbassador.requestAttributeTransportationTypeChange(theObject, theAttributes, theType);
    }


    // 6.25
    public void queryAttributeTransportationType(final ObjectInstanceHandle theObject, final AttributeHandle theAttribute) throws AttributeNotDefined, ObjectInstanceNotKnown, SaveInProgress, RestoreInProgress, FederateNotExecutionMember, NotConnected, RTIinternalError {
        this.LOGGER.info("queryAttributeTransportationType " + theObject.toString() + " " + theAttribute.toString());
        this._rtiAmbassador.queryAttributeTransportationType(theObject, theAttribute);
    }


    // 6.27
    public void requestInteractionTransportationTypeChange(final InteractionClassHandle theClass, final TransportationTypeHandle theType) throws InteractionClassAlreadyBeingChanged, InteractionClassNotPublished, InteractionClassNotDefined, InvalidTransportationType, SaveInProgress, RestoreInProgress, FederateNotExecutionMember, NotConnected, RTIinternalError {
        this.LOGGER.info("requestInteractionTransportationTypeChange " + theClass.toString() + " " + theType.toString());
        this._rtiAmbassador.requestInteractionTransportationTypeChange(theClass, theType);
    }


    // 6.29
    public void queryInteractionTransportationType(final FederateHandle theFederate, final InteractionClassHandle theInteraction) throws InteractionClassNotDefined, SaveInProgress, RestoreInProgress, FederateNotExecutionMember, NotConnected, RTIinternalError {
        this.LOGGER.info("queryInteractionTransportationType " + theFederate.toString() + " " + theInteraction.toString());
        this._rtiAmbassador.queryInteractionTransportationType(theFederate, theInteraction);
    }

    ///////////////////////////////////
    // Ownership Management Services //
    ///////////////////////////////////


    // 7.2
    public void unconditionalAttributeOwnershipDivestiture(final ObjectInstanceHandle theObject, final AttributeHandleSet theAttributes) throws AttributeNotOwned, AttributeNotDefined, ObjectInstanceNotKnown, SaveInProgress, RestoreInProgress, FederateNotExecutionMember, NotConnected, RTIinternalError {
        this.LOGGER.info("unconditionalAttributeOwnershipDivestiture " + theObject.toString() + " " + theAttributes.toString());
        this._rtiAmbassador.unconditionalAttributeOwnershipDivestiture(theObject, theAttributes);
    }


    // 7.3
    public void negotiatedAttributeOwnershipDivestiture(final ObjectInstanceHandle theObject, final AttributeHandleSet theAttributes, final byte[] userSuppliedTag) throws AttributeAlreadyBeingDivested, AttributeNotOwned, AttributeNotDefined, ObjectInstanceNotKnown, SaveInProgress, RestoreInProgress, FederateNotExecutionMember, NotConnected, RTIinternalError {
        this.LOGGER.info("negotiatedAttributeOwnershipDivestiture " + theObject.toString() + " " + theAttributes.toString() + " " + Arrays.toString(userSuppliedTag));
        this._rtiAmbassador.negotiatedAttributeOwnershipDivestiture(theObject, theAttributes, userSuppliedTag);
    }


    // 7.6
    public void confirmDivestiture(final ObjectInstanceHandle theObject, final AttributeHandleSet theAttributes, final byte[] userSuppliedTag) throws NoAcquisitionPending, AttributeDivestitureWasNotRequested, AttributeNotOwned, AttributeNotDefined, ObjectInstanceNotKnown, SaveInProgress, RestoreInProgress, FederateNotExecutionMember, NotConnected, RTIinternalError {
        this.LOGGER.info("confirmDivestiture " + theObject.toString() + " " + theAttributes.toString() + " " + Arrays.toString(userSuppliedTag));
        this._rtiAmbassador.confirmDivestiture(theObject, theAttributes, userSuppliedTag);
    }


    // 7.8
    public void attributeOwnershipAcquisition(final ObjectInstanceHandle theObject, final AttributeHandleSet desiredAttributes, final byte[] userSuppliedTag) throws AttributeNotPublished, ObjectClassNotPublished, FederateOwnsAttributes, AttributeNotDefined, ObjectInstanceNotKnown, SaveInProgress, RestoreInProgress, FederateNotExecutionMember, NotConnected, RTIinternalError {
        this.LOGGER.info("attributeOwnershipAcquisition " + theObject.toString() + " " + desiredAttributes.toString() + " " + Arrays.toString(userSuppliedTag));
        this._rtiAmbassador.attributeOwnershipAcquisition(theObject, desiredAttributes, userSuppliedTag);
    }


    // 7.9
    public void attributeOwnershipAcquisitionIfAvailable(final ObjectInstanceHandle theObject, final AttributeHandleSet desiredAttributes) throws AttributeAlreadyBeingAcquired, AttributeNotPublished, ObjectClassNotPublished, FederateOwnsAttributes, AttributeNotDefined, ObjectInstanceNotKnown, SaveInProgress, RestoreInProgress, FederateNotExecutionMember, NotConnected, RTIinternalError {
        this.LOGGER.info("attributeOwnershipAcquisitionIfAvailable " + theObject.toString() + " " + desiredAttributes.toString());
        this._rtiAmbassador.attributeOwnershipAcquisitionIfAvailable(theObject, desiredAttributes);
    }


    // 7.12
    public void attributeOwnershipReleaseDenied(final ObjectInstanceHandle theObject, final AttributeHandleSet theAttributes) throws AttributeNotOwned, AttributeNotDefined, ObjectInstanceNotKnown, SaveInProgress, RestoreInProgress, FederateNotExecutionMember, NotConnected, RTIinternalError {
        this.LOGGER.info("attributeOwnershipReleaseDenied " + theObject.toString() + " " + theAttributes.toString());
        this._rtiAmbassador.attributeOwnershipReleaseDenied(theObject, theAttributes);
    }


    // 7.13
    public AttributeHandleSet attributeOwnershipDivestitureIfWanted(final ObjectInstanceHandle theObject, final AttributeHandleSet theAttributes) throws AttributeNotOwned, AttributeNotDefined, ObjectInstanceNotKnown, SaveInProgress, RestoreInProgress, FederateNotExecutionMember, NotConnected, RTIinternalError {
        this.LOGGER.info("attributeOwnershipDivestitureIfWanted " + theObject.toString() + " " + theAttributes.toString());
        final AttributeHandleSet attributeHandleSet = this._rtiAmbassador.attributeOwnershipDivestitureIfWanted(theObject, theAttributes);
        this.LOGGER.info("attributeOwnershipDivestitureIfWanted return " + attributeHandleSet.toString());
        return attributeHandleSet;
    }


    // 7.14
    public void cancelNegotiatedAttributeOwnershipDivestiture(final ObjectInstanceHandle theObject, final AttributeHandleSet theAttributes) throws AttributeDivestitureWasNotRequested, AttributeNotOwned, AttributeNotDefined, ObjectInstanceNotKnown, SaveInProgress, RestoreInProgress, FederateNotExecutionMember, NotConnected, RTIinternalError {
        this.LOGGER.info("cancelNegotiatedAttributeOwnershipDivestiture " + theObject.toString() + " " + theAttributes.toString());
        this._rtiAmbassador.cancelNegotiatedAttributeOwnershipDivestiture(theObject, theAttributes);
    }


    // 7.15
    public void cancelAttributeOwnershipAcquisition(final ObjectInstanceHandle theObject, final AttributeHandleSet theAttributes) throws AttributeAcquisitionWasNotRequested, AttributeAlreadyOwned, AttributeNotDefined, ObjectInstanceNotKnown, SaveInProgress, RestoreInProgress, FederateNotExecutionMember, NotConnected, RTIinternalError {
        this.LOGGER.info("cancelAttributeOwnershipAcquisition " + theObject.toString() + " " + theAttributes.toString());
        this._rtiAmbassador.cancelAttributeOwnershipAcquisition(theObject, theAttributes);
    }


    // 7.17
    public void queryAttributeOwnership(final ObjectInstanceHandle theObject, final AttributeHandle theAttribute) throws AttributeNotDefined, ObjectInstanceNotKnown, SaveInProgress, RestoreInProgress, FederateNotExecutionMember, NotConnected, RTIinternalError {
        this.LOGGER.info("queryAttributeOwnership " + theObject.toString() + " " + theAttribute.toString());
        this._rtiAmbassador.queryAttributeOwnership(theObject, theAttribute);
    }


    // 7.19
    public boolean isAttributeOwnedByFederate(final ObjectInstanceHandle theObject, final AttributeHandle theAttribute) throws AttributeNotDefined, ObjectInstanceNotKnown, SaveInProgress, RestoreInProgress, FederateNotExecutionMember, NotConnected, RTIinternalError {
        this.LOGGER.info("isAttributeOwnedByFederate " + theObject.toString() + " " + theAttribute.toString());
        final boolean bool = this._rtiAmbassador.isAttributeOwnedByFederate(theObject, theAttribute);
        this.LOGGER.info("isAttributeOwnedByFederate return " + bool);
        return bool;
    }

    //////////////////////////////
    // Time Management Services //
    //////////////////////////////


    // 8.2
    public void enableTimeRegulation(final LogicalTimeInterval theLookahead) throws InvalidLookahead, InTimeAdvancingState, RequestForTimeRegulationPending, TimeRegulationAlreadyEnabled, SaveInProgress, RestoreInProgress, FederateNotExecutionMember, NotConnected, RTIinternalError {
        this.LOGGER.info("enableTimeRegulation " + theLookahead.toString());
        this._rtiAmbassador.enableTimeRegulation(theLookahead);
    }


    // 8.4
    public void disableTimeRegulation() throws TimeRegulationIsNotEnabled, SaveInProgress, RestoreInProgress, FederateNotExecutionMember, NotConnected, RTIinternalError {
        this.LOGGER.info("disableTimeRegulation");
        this._rtiAmbassador.disableTimeRegulation();
    }


    // 8.5
    public void enableTimeConstrained() throws InTimeAdvancingState, RequestForTimeConstrainedPending, TimeConstrainedAlreadyEnabled, SaveInProgress, RestoreInProgress, FederateNotExecutionMember, NotConnected, RTIinternalError {
        this.LOGGER.info("enableTimeConstrained");
        this._rtiAmbassador.enableTimeConstrained();
    }


    // 8.7
    public void disableTimeConstrained() throws TimeConstrainedIsNotEnabled, SaveInProgress, RestoreInProgress, FederateNotExecutionMember, NotConnected, RTIinternalError {
        this.LOGGER.info("disableTimeConstrained");
        this._rtiAmbassador.disableTimeConstrained();
    }


    // 8.8
    public void timeAdvanceRequest(final LogicalTime theTime) throws LogicalTimeAlreadyPassed, InvalidLogicalTime, InTimeAdvancingState, RequestForTimeRegulationPending, RequestForTimeConstrainedPending, SaveInProgress, RestoreInProgress, FederateNotExecutionMember, NotConnected, RTIinternalError {
        this.LOGGER.info("timeAdvanceRequest " + theTime.toString());
        this._rtiAmbassador.timeAdvanceRequest(theTime);
    }


    // 8.9
    public void timeAdvanceRequestAvailable(final LogicalTime theTime) throws LogicalTimeAlreadyPassed, InvalidLogicalTime, InTimeAdvancingState, RequestForTimeRegulationPending, RequestForTimeConstrainedPending, SaveInProgress, RestoreInProgress, FederateNotExecutionMember, NotConnected, RTIinternalError {
        this.LOGGER.info("timeAdvanceRequestAvailable " + theTime.toString());
        this._rtiAmbassador.timeAdvanceRequestAvailable(theTime);
    }


    // 8.10
    public void nextMessageRequest(final LogicalTime theTime) throws LogicalTimeAlreadyPassed, InvalidLogicalTime, InTimeAdvancingState, RequestForTimeRegulationPending, RequestForTimeConstrainedPending, SaveInProgress, RestoreInProgress, FederateNotExecutionMember, NotConnected, RTIinternalError {
        this.LOGGER.info("nextMessageRequest " + theTime.toString());
        this._rtiAmbassador.nextMessageRequest(theTime);
    }


    // 8.11
    public void nextMessageRequestAvailable(final LogicalTime theTime) throws LogicalTimeAlreadyPassed, InvalidLogicalTime, InTimeAdvancingState, RequestForTimeRegulationPending, RequestForTimeConstrainedPending, SaveInProgress, RestoreInProgress, FederateNotExecutionMember, NotConnected, RTIinternalError {
        this.LOGGER.info("nextMessageRequestAvailable " + theTime.toString());
        this._rtiAmbassador.nextMessageRequestAvailable(theTime);
    }


    // 8.12
    public void flushQueueRequest(final LogicalTime theTime) throws LogicalTimeAlreadyPassed, InvalidLogicalTime, InTimeAdvancingState, RequestForTimeRegulationPending, RequestForTimeConstrainedPending, SaveInProgress, RestoreInProgress, FederateNotExecutionMember, NotConnected, RTIinternalError {
        this.LOGGER.info("flushQueueRequest " + theTime.toString());
        this._rtiAmbassador.flushQueueRequest(theTime);
    }


    // 8.14
    public void enableAsynchronousDelivery() throws AsynchronousDeliveryAlreadyEnabled, SaveInProgress, RestoreInProgress, FederateNotExecutionMember, NotConnected, RTIinternalError {
        this.LOGGER.info("enableAsynchronousDelivery");
        this._rtiAmbassador.enableAsynchronousDelivery();
    }


    // 8.15
    public void disableAsynchronousDelivery() throws AsynchronousDeliveryAlreadyDisabled, SaveInProgress, RestoreInProgress, FederateNotExecutionMember, NotConnected, RTIinternalError {
        this.LOGGER.info("disableAsynchronousDelivery");
        this._rtiAmbassador.disableAsynchronousDelivery();
    }


    // 8.16
    public TimeQueryReturn queryGALT() throws SaveInProgress, RestoreInProgress, FederateNotExecutionMember, NotConnected, RTIinternalError {
        this.LOGGER.info("queryGALT");
        final TimeQueryReturn timeQueryReturn = this._rtiAmbassador.queryGALT();
        this.LOGGER.info("queryGALT return " + timeQueryReturn.toString());
        return timeQueryReturn;
    }


    // 8.17
    public LogicalTime queryLogicalTime() throws SaveInProgress, RestoreInProgress, FederateNotExecutionMember, NotConnected, RTIinternalError {
        this.LOGGER.info("queryLogicalTime");
        final LogicalTime logicalTime = this._rtiAmbassador.queryLogicalTime();
        this.LOGGER.info("queryLogicalTime return " + logicalTime.toString());
        return logicalTime;
    }


    // 8.18
    public TimeQueryReturn queryLITS() throws SaveInProgress, RestoreInProgress, FederateNotExecutionMember, NotConnected, RTIinternalError {
        this.LOGGER.info("queryLITS");
        final TimeQueryReturn timeQueryReturn = this._rtiAmbassador.queryLITS();
        this.LOGGER.info("queryLITS return " + timeQueryReturn.toString());
        return timeQueryReturn;
    }


    // 8.19
    public void modifyLookahead(final LogicalTimeInterval theLookahead) throws InvalidLookahead, InTimeAdvancingState, TimeRegulationIsNotEnabled, SaveInProgress, RestoreInProgress, FederateNotExecutionMember, NotConnected, RTIinternalError {
        this.LOGGER.info("modifyLookahead " + theLookahead.toString());
        this._rtiAmbassador.modifyLookahead(theLookahead);
    }


    // 8.20
    public LogicalTimeInterval queryLookahead() throws TimeRegulationIsNotEnabled, SaveInProgress, RestoreInProgress, FederateNotExecutionMember, NotConnected, RTIinternalError {
        this.LOGGER.info("queryLookahead");
        final LogicalTimeInterval logicalTimeInterval = this._rtiAmbassador.queryLookahead();
        this.LOGGER.info("queryLookahead return " + logicalTimeInterval.toString());
        return logicalTimeInterval;
    }


    // 8.21
    public void retract(final MessageRetractionHandle theHandle) throws MessageCanNoLongerBeRetracted, InvalidMessageRetractionHandle, TimeRegulationIsNotEnabled, SaveInProgress, RestoreInProgress, FederateNotExecutionMember, NotConnected, RTIinternalError {
        this.LOGGER.info("retract " + theHandle.toString());
        this._rtiAmbassador.retract(theHandle);
    }


    // 8.23
    public void changeAttributeOrderType(final ObjectInstanceHandle theObject, final AttributeHandleSet theAttributes, final OrderType theType) throws AttributeNotOwned, AttributeNotDefined, ObjectInstanceNotKnown, SaveInProgress, RestoreInProgress, FederateNotExecutionMember, NotConnected, RTIinternalError {
        this.LOGGER.info("changeAttributeOrderType " + theObject.toString() + " " + theAttributes.toString() + " " + theType.toString());
        this._rtiAmbassador.changeAttributeOrderType(theObject, theAttributes, theType);
    }


    // 8.24
    public void changeInteractionOrderType(final InteractionClassHandle theClass, final OrderType theType) throws InteractionClassNotPublished, InteractionClassNotDefined, SaveInProgress, RestoreInProgress, FederateNotExecutionMember, NotConnected, RTIinternalError {
        this.LOGGER.info("changeInteractionOrderType " + theClass.toString() + " " + theType.toString());
        this._rtiAmbassador.changeInteractionOrderType(theClass, theType);
    }

    //////////////////////////////////
    // Data Distribution Management //
    //////////////////////////////////


    // 9.2
    public RegionHandle createRegion(final DimensionHandleSet dimensions) throws InvalidDimensionHandle, SaveInProgress, RestoreInProgress, FederateNotExecutionMember, NotConnected, RTIinternalError {
        this.LOGGER.info("createRegion " + dimensions.toString());
        final RegionHandle regionHandle = this._rtiAmbassador.createRegion(dimensions);
        this.LOGGER.info("createRegion return " + regionHandle.toString());
        return regionHandle;
    }


    // 9.3
    public void commitRegionModifications(final RegionHandleSet regions) throws RegionNotCreatedByThisFederate, InvalidRegion, SaveInProgress, RestoreInProgress, FederateNotExecutionMember, NotConnected, RTIinternalError {
        this.LOGGER.info("commitRegionModifications " + regions.toString());
        this._rtiAmbassador.commitRegionModifications(regions);
    }


    // 9.4
    public void deleteRegion(final RegionHandle theRegion) throws RegionInUseForUpdateOrSubscription, RegionNotCreatedByThisFederate, InvalidRegion, SaveInProgress, RestoreInProgress, FederateNotExecutionMember, NotConnected, RTIinternalError {
        this.LOGGER.info("deleteRegion " + theRegion.toString());
        this._rtiAmbassador.deleteRegion(theRegion);
    }


    //9.5
    public ObjectInstanceHandle registerObjectInstanceWithRegions(final ObjectClassHandle theClass, final AttributeSetRegionSetPairList attributesAndRegions) throws InvalidRegionContext, RegionNotCreatedByThisFederate, InvalidRegion, AttributeNotPublished, ObjectClassNotPublished, AttributeNotDefined, ObjectClassNotDefined, SaveInProgress, RestoreInProgress, FederateNotExecutionMember, NotConnected, RTIinternalError {
        this.LOGGER.info("registerObjectInstanceWithRegions " + theClass.toString() + " " + attributesAndRegions.toString());
        final ObjectInstanceHandle objectInstanceHandle = this._rtiAmbassador.registerObjectInstanceWithRegions(theClass, attributesAndRegions);
        this.LOGGER.info("registerObjectInstanceWithRegions return " + objectInstanceHandle.toString());
        return objectInstanceHandle;
    }


    //9.5
    public ObjectInstanceHandle registerObjectInstanceWithRegions(final ObjectClassHandle theClass, final AttributeSetRegionSetPairList attributesAndRegions, final String theObject) throws ObjectInstanceNameInUse, ObjectInstanceNameNotReserved, InvalidRegionContext, RegionNotCreatedByThisFederate, InvalidRegion, AttributeNotPublished, ObjectClassNotPublished, AttributeNotDefined, ObjectClassNotDefined, SaveInProgress, RestoreInProgress, FederateNotExecutionMember, NotConnected, RTIinternalError {
        this.LOGGER.info("registerObjectInstanceWithRegions " + theClass.toString() + " " + attributesAndRegions.toString() + " " + theObject.toString());
        final ObjectInstanceHandle objectInstanceHandle = this._rtiAmbassador.registerObjectInstanceWithRegions(theClass, attributesAndRegions, theObject);
        this.LOGGER.info("registerObjectInstanceWithRegions return " + objectInstanceHandle.toString());
        return objectInstanceHandle;
    }


    // 9.6
    public void associateRegionsForUpdates(final ObjectInstanceHandle theObject, final AttributeSetRegionSetPairList attributesAndRegions) throws InvalidRegionContext, RegionNotCreatedByThisFederate, InvalidRegion, AttributeNotDefined, ObjectInstanceNotKnown, SaveInProgress, RestoreInProgress, FederateNotExecutionMember, NotConnected, RTIinternalError {
        this.LOGGER.info("associateRegionsForUpdates " + theObject.toString() + " " + attributesAndRegions.toString());
        this._rtiAmbassador.associateRegionsForUpdates(theObject, attributesAndRegions);
    }


    // 9.7
    public void unassociateRegionsForUpdates(final ObjectInstanceHandle theObject, final AttributeSetRegionSetPairList attributesAndRegions) throws RegionNotCreatedByThisFederate, InvalidRegion, AttributeNotDefined, ObjectInstanceNotKnown, SaveInProgress, RestoreInProgress, FederateNotExecutionMember, NotConnected, RTIinternalError {
        this.LOGGER.info("unassociateRegionsForUpdates " + theObject.toString() + " " + attributesAndRegions.toString());
        this._rtiAmbassador.unassociateRegionsForUpdates(theObject, attributesAndRegions);
    }


    // 9.8
    public void subscribeObjectClassAttributesWithRegions(final ObjectClassHandle theClass, final AttributeSetRegionSetPairList attributesAndRegions) throws InvalidRegionContext, RegionNotCreatedByThisFederate, InvalidRegion, AttributeNotDefined, ObjectClassNotDefined, SaveInProgress, RestoreInProgress, FederateNotExecutionMember, NotConnected, RTIinternalError {
        this.LOGGER.info("subscribeObjectClassAttributesWithRegions " + theClass.toString() + " " + attributesAndRegions.toString());
        this._rtiAmbassador.subscribeObjectClassAttributesWithRegions(theClass, attributesAndRegions);
    }


    // 9.8
    public void subscribeObjectClassAttributesWithRegions(final ObjectClassHandle theClass, final AttributeSetRegionSetPairList attributesAndRegions, final String updateRateDesignator) throws InvalidRegionContext, RegionNotCreatedByThisFederate, InvalidRegion, AttributeNotDefined, ObjectClassNotDefined, InvalidUpdateRateDesignator, SaveInProgress, RestoreInProgress, FederateNotExecutionMember, NotConnected, RTIinternalError {
        this.LOGGER.info("subscribeObjectClassAttributesWithRegions " + theClass.toString() + " " + attributesAndRegions.toString() + " " + updateRateDesignator);
        this._rtiAmbassador.subscribeObjectClassAttributesWithRegions(theClass, attributesAndRegions, updateRateDesignator);
    }


    // 9.8
    public void subscribeObjectClassAttributesPassivelyWithRegions(final ObjectClassHandle theClass, final AttributeSetRegionSetPairList attributesAndRegions) throws InvalidRegionContext, RegionNotCreatedByThisFederate, InvalidRegion, AttributeNotDefined, ObjectClassNotDefined, SaveInProgress, RestoreInProgress, FederateNotExecutionMember, NotConnected, RTIinternalError {
        this.LOGGER.info("subscribeObjectClassAttributesPassivelyWithRegions " + theClass.toString() + " " + attributesAndRegions.toString());
        this._rtiAmbassador.subscribeObjectClassAttributesPassivelyWithRegions(theClass, attributesAndRegions);
    }


    // 9.8
    public void subscribeObjectClassAttributesPassivelyWithRegions(final ObjectClassHandle theClass, final AttributeSetRegionSetPairList attributesAndRegions, final String updateRateDesignator) throws InvalidRegionContext, RegionNotCreatedByThisFederate, InvalidRegion, AttributeNotDefined, ObjectClassNotDefined, InvalidUpdateRateDesignator, SaveInProgress, RestoreInProgress, FederateNotExecutionMember, NotConnected, RTIinternalError {
        this.LOGGER.info("subscribeObjectClassAttributesPassivelyWithRegions " + theClass.toString() + " " + attributesAndRegions.toString() + " " + updateRateDesignator);
        this._rtiAmbassador.subscribeObjectClassAttributesPassivelyWithRegions(theClass, attributesAndRegions, updateRateDesignator);
    }


    // 9.9
    public void unsubscribeObjectClassAttributesWithRegions(final ObjectClassHandle theClass, final AttributeSetRegionSetPairList attributesAndRegions) throws RegionNotCreatedByThisFederate, InvalidRegion, AttributeNotDefined, ObjectClassNotDefined, SaveInProgress, RestoreInProgress, FederateNotExecutionMember, NotConnected, RTIinternalError {
        this.LOGGER.info("unsubscribeObjectClassAttributesWithRegions " + theClass.toString() + " " + attributesAndRegions.toString());
        this._rtiAmbassador.unsubscribeObjectClassAttributesWithRegions(theClass, attributesAndRegions);
    }


    // 9.10
    public void subscribeInteractionClassWithRegions(final InteractionClassHandle theClass, final RegionHandleSet regions) throws FederateServiceInvocationsAreBeingReportedViaMOM, InvalidRegionContext, RegionNotCreatedByThisFederate, InvalidRegion, InteractionClassNotDefined, SaveInProgress, RestoreInProgress, FederateNotExecutionMember, NotConnected, RTIinternalError {
        this.LOGGER.info("subscribeInteractionClassWithRegions " + theClass.toString() + " " + regions.toString());
        this._rtiAmbassador.subscribeInteractionClassWithRegions(theClass, regions);
    }


    // 9.10
    public void subscribeInteractionClassPassivelyWithRegions(final InteractionClassHandle theClass, final RegionHandleSet regions) throws FederateServiceInvocationsAreBeingReportedViaMOM, InvalidRegionContext, RegionNotCreatedByThisFederate, InvalidRegion, InteractionClassNotDefined, SaveInProgress, RestoreInProgress, FederateNotExecutionMember, NotConnected, RTIinternalError {
        this.LOGGER.info("subscribeInteractionClassPassivelyWithRegions " + theClass.toString() + " " + regions.toString());
        this._rtiAmbassador.subscribeInteractionClassPassivelyWithRegions(theClass, regions);
    }


    // 9.11
    public void unsubscribeInteractionClassWithRegions(final InteractionClassHandle theClass, final RegionHandleSet regions) throws RegionNotCreatedByThisFederate, InvalidRegion, InteractionClassNotDefined, SaveInProgress, RestoreInProgress, FederateNotExecutionMember, NotConnected, RTIinternalError {
        this.LOGGER.info("unsubscribeInteractionClassWithRegions " + theClass.toString() + " " + regions.toString());
        this._rtiAmbassador.unsubscribeInteractionClassWithRegions(theClass, regions);
    }


    //9.12
    public void sendInteractionWithRegions(final InteractionClassHandle theInteraction, final ParameterHandleValueMap theParameters, final RegionHandleSet regions, final byte[] userSuppliedTag) throws InvalidRegionContext, RegionNotCreatedByThisFederate, InvalidRegion, InteractionClassNotPublished, InteractionParameterNotDefined, InteractionClassNotDefined, SaveInProgress, RestoreInProgress, FederateNotExecutionMember, NotConnected, RTIinternalError {
        this.LOGGER.info("sendInteractionWithRegions " + theInteraction.toString() + " " + theParameters.toString() + " " + regions.toString() + " " + Arrays.toString(userSuppliedTag));
        this._rtiAmbassador.sendInteractionWithRegions(theInteraction, theParameters, regions, userSuppliedTag);
    }


    //9.12
    public MessageRetractionReturn sendInteractionWithRegions(final InteractionClassHandle theInteraction, final ParameterHandleValueMap theParameters, final RegionHandleSet regions, final byte[] userSuppliedTag, final LogicalTime theTime) throws InvalidLogicalTime, InvalidRegionContext, RegionNotCreatedByThisFederate, InvalidRegion, InteractionClassNotPublished, InteractionParameterNotDefined, InteractionClassNotDefined, SaveInProgress, RestoreInProgress, FederateNotExecutionMember, NotConnected, RTIinternalError {
        this.LOGGER.info("sendInteractionWithRegions " + theInteraction.toString() + " " + theParameters.toString() + " " + regions.toString() + " " + Arrays.toString(userSuppliedTag) + " " + theTime.toString());
        final MessageRetractionReturn messageRetractionReturn = this._rtiAmbassador.sendInteractionWithRegions(theInteraction, theParameters, regions, userSuppliedTag, theTime);
        this.LOGGER.info("sendInteractionWithRegions return " + messageRetractionReturn.toString());
        return messageRetractionReturn;
    }


    // 9.13
    public void requestAttributeValueUpdateWithRegions(final ObjectClassHandle theClass, final AttributeSetRegionSetPairList attributesAndRegions, final byte[] userSuppliedTag) throws InvalidRegionContext, RegionNotCreatedByThisFederate, InvalidRegion, AttributeNotDefined, ObjectClassNotDefined, SaveInProgress, RestoreInProgress, FederateNotExecutionMember, NotConnected, RTIinternalError {
        this.LOGGER.info("requestAttributeValueUpdateWithRegions " + theClass.toString() + " " + attributesAndRegions.toString() + " " + Arrays.toString(userSuppliedTag));
        this._rtiAmbassador.requestAttributeValueUpdateWithRegions(theClass, attributesAndRegions, userSuppliedTag);
    }

    //////////////////////////
    // RTI Support Services //
    //////////////////////////


    // 10.2
    public ResignAction getAutomaticResignDirective() throws FederateNotExecutionMember, NotConnected, RTIinternalError {
        this.LOGGER.info("getAutomaticResignDirective");
        final ResignAction resignAction = this._rtiAmbassador.getAutomaticResignDirective();
        this.LOGGER.info("getAutomaticResignDirective return " + resignAction.toString());
        return resignAction;
    }


    // 10.3
    public void setAutomaticResignDirective(final ResignAction resignAction) throws InvalidResignAction, FederateNotExecutionMember, NotConnected, RTIinternalError {
        this.LOGGER.info("setAutomaticResignDirective " + resignAction.toString());
        this._rtiAmbassador.setAutomaticResignDirective(resignAction);
    }


    // 10.4
    public FederateHandle getFederateHandle(final String theName) throws NameNotFound, FederateNotExecutionMember, NotConnected, RTIinternalError {
        this.LOGGER.info("getFederateHandle " + theName);
        final FederateHandle federateHandle = this._rtiAmbassador.getFederateHandle(theName);
        this.LOGGER.info("getFederateHandle return " + federateHandle.toString());
        return federateHandle;
    }


    // 10.5
    public String getFederateName(final FederateHandle theHandle) throws InvalidFederateHandle, FederateHandleNotKnown, FederateNotExecutionMember, NotConnected, RTIinternalError {
        this.LOGGER.info("getFederateName " + theHandle.toString());
        final String str = this._rtiAmbassador.getFederateName(theHandle);
        this.LOGGER.info("getFederateName return " + str);
        return str;
    }


    // 10.6
    public ObjectClassHandle getObjectClassHandle(final String theName) throws NameNotFound, FederateNotExecutionMember, NotConnected, RTIinternalError {
        this.LOGGER.info("getObjectClassHandle " + theName);
        final ObjectClassHandle objectClassHandle = this._rtiAmbassador.getObjectClassHandle(theName);
        this.LOGGER.info("getObjectClassHandle return " + objectClassHandle.toString());
        return objectClassHandle;
    }


    // 10.7
    public String getObjectClassName(final ObjectClassHandle theHandle) throws InvalidObjectClassHandle, FederateNotExecutionMember, NotConnected, RTIinternalError {
        this.LOGGER.info("getObjectClassName " + theHandle.toString());
        final String str = this._rtiAmbassador.getObjectClassName(theHandle);
        this.LOGGER.info("getObjectClassName return " + str);
        return str;
    }


    // 10.8
    public ObjectClassHandle getKnownObjectClassHandle(final ObjectInstanceHandle theObject) throws ObjectInstanceNotKnown, FederateNotExecutionMember, NotConnected, RTIinternalError {
        this.LOGGER.info("getKnownObjectClassHandle " + theObject.toString());
        final ObjectClassHandle objectClassHandle = this._rtiAmbassador.getKnownObjectClassHandle(theObject);
        this.LOGGER.info("getKnownObjectClassHandle return " + objectClassHandle.toString());
        return objectClassHandle;
    }


    // 10.9
    public ObjectInstanceHandle getObjectInstanceHandle(final String theName) throws ObjectInstanceNotKnown, FederateNotExecutionMember, NotConnected, RTIinternalError {
        this.LOGGER.info("getObjectInstanceHandle " + theName);
        final ObjectInstanceHandle objectInstanceHandle = this._rtiAmbassador.getObjectInstanceHandle(theName);
        this.LOGGER.info("getObjectInstanceHandle return " + objectInstanceHandle.toString());
        return objectInstanceHandle;
    }


    // 10.10
    public String getObjectInstanceName(final ObjectInstanceHandle theHandle) throws ObjectInstanceNotKnown, FederateNotExecutionMember, NotConnected, RTIinternalError {
        this.LOGGER.info("getObjectInstanceName " + theHandle.toString());
        final String str = this._rtiAmbassador.getObjectInstanceName(theHandle);
        this.LOGGER.info("getObjectInstanceName return " + str);
        return str;
    }


    // 10.11
    public AttributeHandle getAttributeHandle(final ObjectClassHandle whichClass, final String theName) throws NameNotFound, InvalidObjectClassHandle, FederateNotExecutionMember, NotConnected, RTIinternalError {
        this.LOGGER.info("getAttributeHandle " + whichClass.toString() + " " + theName);
        final AttributeHandle attributeHandle = this._rtiAmbassador.getAttributeHandle(whichClass, theName);
        this.LOGGER.info("getAttributeHandle return " + attributeHandle.toString());
        return attributeHandle;
    }


    // 10.12
    public String getAttributeName(final ObjectClassHandle whichClass, final AttributeHandle theHandle) throws AttributeNotDefined, InvalidAttributeHandle, InvalidObjectClassHandle, FederateNotExecutionMember, NotConnected, RTIinternalError {
        this.LOGGER.info("getAttributeName " + theHandle.toString());
        final String str = this._rtiAmbassador.getAttributeName(whichClass, theHandle);
        this.LOGGER.info("getAttributeName return " + str);
        return str;
    }


    // 10.13
    public double getUpdateRateValue(final String updateRateDesignator) throws InvalidUpdateRateDesignator, FederateNotExecutionMember, NotConnected, RTIinternalError {
        this.LOGGER.info("getUpdateRateValue " + updateRateDesignator);
        final double d = this._rtiAmbassador.getUpdateRateValue(updateRateDesignator);
        this.LOGGER.info("getUpdateRateValue return " + d);
        return d;
    }


    // 10.14
    public double getUpdateRateValueForAttribute(final ObjectInstanceHandle theObject, final AttributeHandle theAttribute) throws ObjectInstanceNotKnown, AttributeNotDefined, FederateNotExecutionMember, NotConnected, RTIinternalError {
        this.LOGGER.info("getUpdateRateValueForAttribute " + theObject.toString() + " " + theAttribute.toString());
        final double d = this._rtiAmbassador.getUpdateRateValueForAttribute(theObject, theAttribute);
        this.LOGGER.info("getUpdateRateValueForAttribute return " + d);
        return d;
    }


    // 10.15
    public InteractionClassHandle getInteractionClassHandle(final String theName) throws NameNotFound, FederateNotExecutionMember, NotConnected, RTIinternalError {
        this.LOGGER.info("getInteractionClassHandle " + theName);
        final InteractionClassHandle interactionClassHandle = this._rtiAmbassador.getInteractionClassHandle(theName);
        this.LOGGER.info("getInteractionClassHandle return " + interactionClassHandle.toString());
        return interactionClassHandle;
    }


    // 10.16
    public String getInteractionClassName(final InteractionClassHandle theHandle) throws InvalidInteractionClassHandle, FederateNotExecutionMember, NotConnected, RTIinternalError {
        this.LOGGER.info("getInteractionClassName " + theHandle.toString());
        return this._rtiAmbassador.getInteractionClassName(theHandle);
    }


    // 10.17
    public ParameterHandle getParameterHandle(final InteractionClassHandle whichClass, final String theName) throws NameNotFound, InvalidInteractionClassHandle, FederateNotExecutionMember, NotConnected, RTIinternalError {
        this.LOGGER.info("getParameterHandle " + whichClass.toString() + " " + theName);
        return this._rtiAmbassador.getParameterHandle(whichClass, theName);
    }


    // 10.18
    public String getParameterName(final InteractionClassHandle whichClass, final ParameterHandle theHandle) throws InteractionParameterNotDefined, InvalidParameterHandle, InvalidInteractionClassHandle, FederateNotExecutionMember, NotConnected, RTIinternalError {
        this.LOGGER.info("getParameterName " + whichClass.toString() + " " + theHandle.toString());
        return this._rtiAmbassador.getParameterName(whichClass, theHandle);
    }


    // 10.19
    public OrderType getOrderType(final String theName) throws InvalidOrderName, FederateNotExecutionMember, NotConnected, RTIinternalError {
        this.LOGGER.info("getOrderType " + theName);
        return this._rtiAmbassador.getOrderType(theName);
    }


    // 10.20
    public String getOrderName(final OrderType theType) throws InvalidOrderType, FederateNotExecutionMember, NotConnected, RTIinternalError {
        this.LOGGER.info("getOrderName " + theType.toString());
        return this._rtiAmbassador.getOrderName(theType);
    }


    // 10.21
    public TransportationTypeHandle getTransportationTypeHandle(final String theName) throws InvalidTransportationName, FederateNotExecutionMember, NotConnected, RTIinternalError {
        this.LOGGER.info("getTransportationTypeHandle " + theName);
        return this._rtiAmbassador.getTransportationTypeHandle(theName);
    }


    // 10.22
    public String getTransportationTypeName(final TransportationTypeHandle theHandle) throws InvalidTransportationType, FederateNotExecutionMember, NotConnected, RTIinternalError {
        this.LOGGER.info("getTransportationTypeName " + theHandle.toString());
        return this._rtiAmbassador.getTransportationTypeName(theHandle);
    }


    // 10.23
    public DimensionHandleSet getAvailableDimensionsForClassAttribute(final ObjectClassHandle whichClass, final AttributeHandle theHandle) throws AttributeNotDefined, InvalidAttributeHandle, InvalidObjectClassHandle, FederateNotExecutionMember, NotConnected, RTIinternalError {
        this.LOGGER.info("getAvailableDimensionsForClassAttribute " + whichClass.toString() + " " + theHandle.toString());
        return this._rtiAmbassador.getAvailableDimensionsForClassAttribute(whichClass, theHandle);
    }


    // 10.24
    public DimensionHandleSet getAvailableDimensionsForInteractionClass(final InteractionClassHandle theHandle) throws InvalidInteractionClassHandle, FederateNotExecutionMember, NotConnected, RTIinternalError {
        this.LOGGER.info("getAvailableDimensionsForInteractionClass " + theHandle.toString());
        return this._rtiAmbassador.getAvailableDimensionsForInteractionClass(theHandle);
    }


    // 10.25
    public DimensionHandle getDimensionHandle(final String theName) throws NameNotFound, FederateNotExecutionMember, NotConnected, RTIinternalError {
        this.LOGGER.info("getDimensionHandle " + theName);
        return this._rtiAmbassador.getDimensionHandle(theName);
    }


    // 10.26
    public String getDimensionName(final DimensionHandle theHandle) throws InvalidDimensionHandle, FederateNotExecutionMember, NotConnected, RTIinternalError {
        this.LOGGER.info("getDimensionName " + theHandle.toString());
        return this._rtiAmbassador.getDimensionName(theHandle);
    }


    // 10.27
    public long getDimensionUpperBound(final DimensionHandle theHandle) throws InvalidDimensionHandle, FederateNotExecutionMember, NotConnected, RTIinternalError {
        this.LOGGER.info("getDimensionUpperBound " + theHandle.toString());
        return this._rtiAmbassador.getDimensionUpperBound(theHandle);
    }


    // 10.28
    public DimensionHandleSet getDimensionHandleSet(final RegionHandle region) throws InvalidRegion, SaveInProgress, RestoreInProgress, FederateNotExecutionMember, NotConnected, RTIinternalError {
        this.LOGGER.info("getDimensionHandleSet " + region.toString());
        return this._rtiAmbassador.getDimensionHandleSet(region);
    }


    // 10.29
    public RangeBounds getRangeBounds(final RegionHandle region, final DimensionHandle dimension) throws RegionDoesNotContainSpecifiedDimension, InvalidRegion, SaveInProgress, RestoreInProgress, FederateNotExecutionMember, NotConnected, RTIinternalError {
        this.LOGGER.info("getRangeBounds " + region.toString() + " " + dimension.toString());
        return this._rtiAmbassador.getRangeBounds(region, dimension);
    }


    // 10.30
    public void setRangeBounds(final RegionHandle region, final DimensionHandle dimension, final RangeBounds bounds) throws InvalidRangeBound, RegionDoesNotContainSpecifiedDimension, RegionNotCreatedByThisFederate, InvalidRegion, SaveInProgress, RestoreInProgress, FederateNotExecutionMember, NotConnected, RTIinternalError {
        this.LOGGER.info("setRangeBounds " + region.toString() + " " + dimension.toString() + " " + bounds.toString());
        this._rtiAmbassador.setRangeBounds(region, dimension, bounds);
    }


    // 10.31
    public long normalizeFederateHandle(final FederateHandle federateHandle) throws InvalidFederateHandle, FederateNotExecutionMember, NotConnected, RTIinternalError {
        this.LOGGER.info("normalizeFederateHandle " + federateHandle.toString());
        return this._rtiAmbassador.normalizeFederateHandle(federateHandle);
    }


    // 10.32
    public long normalizeServiceGroup(final ServiceGroup group) throws InvalidServiceGroup, FederateNotExecutionMember, NotConnected, RTIinternalError {
        this.LOGGER.info("normalizeServiceGroup " + group.toString());
        return this._rtiAmbassador.normalizeServiceGroup(group);
    }


    // 10.33
    public void enableObjectClassRelevanceAdvisorySwitch() throws ObjectClassRelevanceAdvisorySwitchIsOn, SaveInProgress, RestoreInProgress, FederateNotExecutionMember, NotConnected, RTIinternalError {
        this.LOGGER.info("enableObjectClassRelevanceAdvisorySwitch");
        this._rtiAmbassador.enableObjectClassRelevanceAdvisorySwitch();
    }


    // 10.34
    public void disableObjectClassRelevanceAdvisorySwitch() throws ObjectClassRelevanceAdvisorySwitchIsOff, SaveInProgress, RestoreInProgress, FederateNotExecutionMember, NotConnected, RTIinternalError {
        this.LOGGER.info("disableObjectClassRelevanceAdvisorySwitch");
        this._rtiAmbassador.disableObjectClassRelevanceAdvisorySwitch();
    }


    // 10.35
    public void enableAttributeRelevanceAdvisorySwitch() throws AttributeRelevanceAdvisorySwitchIsOn, SaveInProgress, RestoreInProgress, FederateNotExecutionMember, NotConnected, RTIinternalError {
        this.LOGGER.info("enableAttributeRelevanceAdvisorySwitch");
        this._rtiAmbassador.enableAttributeRelevanceAdvisorySwitch();
    }


    // 10.36
    public void disableAttributeRelevanceAdvisorySwitch() throws AttributeRelevanceAdvisorySwitchIsOff, SaveInProgress, RestoreInProgress, FederateNotExecutionMember, NotConnected, RTIinternalError {
        this.LOGGER.info("disableAttributeRelevanceAdvisorySwitch");
        this._rtiAmbassador.disableAttributeRelevanceAdvisorySwitch();
    }


    // 10.37
    public void enableAttributeScopeAdvisorySwitch() throws AttributeScopeAdvisorySwitchIsOn, SaveInProgress, RestoreInProgress, FederateNotExecutionMember, NotConnected, RTIinternalError {
        this.LOGGER.info("enableAttributeScopeAdvisorySwitch");
        this._rtiAmbassador.enableAttributeScopeAdvisorySwitch();
    }


    // 10.38
    public void disableAttributeScopeAdvisorySwitch() throws AttributeScopeAdvisorySwitchIsOff, SaveInProgress, RestoreInProgress, FederateNotExecutionMember, NotConnected, RTIinternalError {
        this.LOGGER.info("disableAttributeScopeAdvisorySwitch");
        this._rtiAmbassador.disableAttributeScopeAdvisorySwitch();
    }


    // 10.39
    public void enableInteractionRelevanceAdvisorySwitch() throws InteractionRelevanceAdvisorySwitchIsOn, SaveInProgress, RestoreInProgress, FederateNotExecutionMember, NotConnected, RTIinternalError {
        this.LOGGER.info("enableInteractionRelevanceAdvisorySwitch");
        this._rtiAmbassador.enableInteractionRelevanceAdvisorySwitch();
    }


    // 10.40
    public void disableInteractionRelevanceAdvisorySwitch() throws InteractionRelevanceAdvisorySwitchIsOff, SaveInProgress, RestoreInProgress, FederateNotExecutionMember, NotConnected, RTIinternalError {
        this.LOGGER.info("disableInteractionRelevanceAdvisorySwitch");
        this._rtiAmbassador.disableInteractionRelevanceAdvisorySwitch();
    }


    // 10.41
    public boolean evokeCallback(final double approximateMinimumTimeInSeconds) throws CallNotAllowedFromWithinCallback, RTIinternalError {
        this.LOGGER.info("evokeCallback " + approximateMinimumTimeInSeconds);
        return this._rtiAmbassador.evokeCallback(approximateMinimumTimeInSeconds);
    }


    // 10.42
    public boolean evokeMultipleCallbacks(final double approximateMinimumTimeInSeconds, final double approximateMaximumTimeInSeconds) throws CallNotAllowedFromWithinCallback, RTIinternalError {
        this.LOGGER.info("evokeMultipleCallbacks " + approximateMinimumTimeInSeconds + " " + approximateMaximumTimeInSeconds);
        return this._rtiAmbassador.evokeMultipleCallbacks(approximateMinimumTimeInSeconds, approximateMaximumTimeInSeconds);
    }


    // 10.43
    public void enableCallbacks() throws SaveInProgress, RestoreInProgress, RTIinternalError {
        this.LOGGER.info("enableCallbacks");
        this._rtiAmbassador.enableCallbacks();
    }


    // 10.44
    public void disableCallbacks() throws SaveInProgress, RestoreInProgress, RTIinternalError {
        this.LOGGER.info("disableCallbacks");
        this._rtiAmbassador.disableCallbacks();
    }


    //API-specific services
    public AttributeHandleFactory getAttributeHandleFactory() throws FederateNotExecutionMember, NotConnected {
        this.LOGGER.info("getAttributeHandleFactory");
        return this._rtiAmbassador.getAttributeHandleFactory();
    }


    public AttributeHandleSetFactory getAttributeHandleSetFactory() throws FederateNotExecutionMember, NotConnected {
        this.LOGGER.info("getAttributeHandleSetFactory");
        return this._rtiAmbassador.getAttributeHandleSetFactory();
    }


    public AttributeHandleValueMapFactory getAttributeHandleValueMapFactory() throws FederateNotExecutionMember, NotConnected {
        this.LOGGER.info("getAttributeHandleValueMapFactory");
        return this._rtiAmbassador.getAttributeHandleValueMapFactory();
    }


    public AttributeSetRegionSetPairListFactory getAttributeSetRegionSetPairListFactory() throws FederateNotExecutionMember, NotConnected {
        this.LOGGER.info("getAttributeSetRegionSetPairListFactory");
        return this._rtiAmbassador.getAttributeSetRegionSetPairListFactory();
    }


    public DimensionHandleFactory getDimensionHandleFactory() throws FederateNotExecutionMember, NotConnected {
        this.LOGGER.info("getDimensionHandleFactory");
        return this._rtiAmbassador.getDimensionHandleFactory();
    }


    public DimensionHandleSetFactory getDimensionHandleSetFactory() throws FederateNotExecutionMember, NotConnected {
        this.LOGGER.info("getDimensionHandleSetFactory");
        return this._rtiAmbassador.getDimensionHandleSetFactory();
    }


    public FederateHandleFactory getFederateHandleFactory() throws FederateNotExecutionMember, NotConnected {
        this.LOGGER.info("getFederateHandleFactory");
        return this._rtiAmbassador.getFederateHandleFactory();
    }


    public FederateHandleSetFactory getFederateHandleSetFactory() throws FederateNotExecutionMember, NotConnected {
        this.LOGGER.info("getFederateHandleSetFactory");
        return this._rtiAmbassador.getFederateHandleSetFactory();
    }


    public InteractionClassHandleFactory getInteractionClassHandleFactory() throws FederateNotExecutionMember, NotConnected {
        this.LOGGER.info("getInteractionClassHandleFactory");
        return this._rtiAmbassador.getInteractionClassHandleFactory();
    }


    public ObjectClassHandleFactory getObjectClassHandleFactory() throws FederateNotExecutionMember, NotConnected {
        this.LOGGER.info("getObjectClassHandleFactory");
        return this._rtiAmbassador.getObjectClassHandleFactory();
    }


    public ObjectInstanceHandleFactory getObjectInstanceHandleFactory() throws FederateNotExecutionMember, NotConnected {
        this.LOGGER.info("getObjectInstanceHandleFactory");
        return this._rtiAmbassador.getObjectInstanceHandleFactory();
    }


    public ParameterHandleFactory getParameterHandleFactory() throws FederateNotExecutionMember, NotConnected {
        this.LOGGER.info("getParameterHandleFactory");
        return this._rtiAmbassador.getParameterHandleFactory();
    }


    public ParameterHandleValueMapFactory getParameterHandleValueMapFactory() throws FederateNotExecutionMember, NotConnected {
        this.LOGGER.info("getParameterHandleValueMapFactory");
        return this._rtiAmbassador.getParameterHandleValueMapFactory();
    }


    public RegionHandleSetFactory getRegionHandleSetFactory() throws FederateNotExecutionMember, NotConnected {
        this.LOGGER.info("getRegionHandleSetFactory");
        return this._rtiAmbassador.getRegionHandleSetFactory();
    }


    public TransportationTypeHandleFactory getTransportationTypeHandleFactory() throws FederateNotExecutionMember, NotConnected {
        this.LOGGER.info("getTransportationTypeHandleFactory");
        return this._rtiAmbassador.getTransportationTypeHandleFactory();
    }


    public String getHLAversion() {
        this.LOGGER.info("getHLAversion");
        return this._rtiAmbassador.getHLAversion();
    }


    public LogicalTimeFactory getTimeFactory() throws FederateNotExecutionMember, NotConnected {
        this.LOGGER.info("getTimeFactory");
        return this._rtiAmbassador.getTimeFactory();
    }
}
