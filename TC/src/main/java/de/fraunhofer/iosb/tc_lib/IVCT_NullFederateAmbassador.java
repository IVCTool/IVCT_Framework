package de.fraunhofer.iosb.tc_lib;

import hla.rti1516e.AttributeHandle;
import hla.rti1516e.AttributeHandleSet;
import hla.rti1516e.AttributeHandleValueMap;
import hla.rti1516e.FederateAmbassador;
import hla.rti1516e.FederateHandle;
import hla.rti1516e.FederateHandleSaveStatusPair;
import hla.rti1516e.FederateHandleSet;
import hla.rti1516e.FederateRestoreStatus;
import hla.rti1516e.FederationExecutionInformationSet;
import hla.rti1516e.InteractionClassHandle;
import hla.rti1516e.LogicalTime;
import hla.rti1516e.MessageRetractionHandle;
import hla.rti1516e.ObjectClassHandle;
import hla.rti1516e.ObjectInstanceHandle;
import hla.rti1516e.OrderType;
import hla.rti1516e.ParameterHandleValueMap;
import hla.rti1516e.RestoreFailureReason;
import hla.rti1516e.SaveFailureReason;
import hla.rti1516e.SynchronizationPointFailureReason;
import hla.rti1516e.TransportationTypeHandle;
import hla.rti1516e.exceptions.FederateInternalError;
import java.util.Set;
import org.slf4j.Logger;


/**
 * An class to supply a minimal implementation of an interface. Only the
 * required methods will be overwritten in a derived class.
 *
 * @author Johannes Mulder (Fraunhofer IOSB)
 */
public class IVCT_NullFederateAmbassador implements FederateAmbassador {
    private Logger logger;


    ////////////////////////////////////
    //Federation Management Services //
    ////////////////////////////////////
    /**
     * @param logger
     */
    public IVCT_NullFederateAmbassador(final Logger logger) {
        this.logger = logger;
    }


    // 4.4
    @Override
    public void connectionLost(final String faultDescription) throws FederateInternalError {
        this.logger.warn("connectionLost not implemented");
    }


    // 4.8
    @Override
    public void reportFederationExecutions(final FederationExecutionInformationSet theFederationExecutionInformationSet) throws FederateInternalError {
        this.logger.warn("reportFederationExecutions not implemented");
    }


    //4.12
    @Override
    public void synchronizationPointRegistrationSucceeded(final String synchronizationPointLabel) throws FederateInternalError {
        this.logger.warn("synchronizationPointRegistrationSucceeded not implemented");
    }


    //4.12
    @Override
    public void synchronizationPointRegistrationFailed(final String synchronizationPointLabel, final SynchronizationPointFailureReason reason) throws FederateInternalError {
        this.logger.warn("synchronizationPointRegistrationFailed not implemented");
    }


    //4.13
    @Override
    public void announceSynchronizationPoint(final String synchronizationPointLabel, final byte[] userSuppliedTag) throws FederateInternalError {
        this.logger.warn("announceSynchronizationPoint not implemented");
    }


    //4.15
    @Override
    public void federationSynchronized(final String synchronizationPointLabel, final FederateHandleSet failedToSyncSet) throws FederateInternalError {
        this.logger.warn("federationSynchronized not implemented");
    }


    //4.17
    @Override
    public void initiateFederateSave(final String label) throws FederateInternalError {
        this.logger.warn("initiateFederateSave not implemented");
    }


    //4.17
    @Override
    public void initiateFederateSave(final String label, final LogicalTime time) throws FederateInternalError {
        this.logger.warn("initiateFederateSave not implemented");
    }


    // 4.20
    @Override
    public void federationSaved() throws FederateInternalError {
        this.logger.warn("federationSaved not implemented");
    }


    // 4.20
    @Override
    public void federationNotSaved(final SaveFailureReason reason) throws FederateInternalError {
        this.logger.warn("federationNotSaved not implemented");
    }


    // 4.23
    @Override
    public void federationSaveStatusResponse(final FederateHandleSaveStatusPair[] response) throws FederateInternalError {
        this.logger.warn("federationSaveStatusResponse not implemented");
    }


    // 4.25
    @Override
    public void requestFederationRestoreSucceeded(final String label) throws FederateInternalError {
        this.logger.warn("requestFederationRestoreSucceeded not implemented");
    }


    // 4.25
    @Override
    public void requestFederationRestoreFailed(final String label) throws FederateInternalError {
        this.logger.warn("requestFederationRestoreFailed not implemented");
    }


    // 4.26
    @Override
    public void federationRestoreBegun() throws FederateInternalError {
        this.logger.warn("federationRestoreBegun not implemented");
    }


    // 4.27
    @Override
    public void initiateFederateRestore(final String label, final String federateName, final FederateHandle federateHandle) throws FederateInternalError {
        this.logger.warn("initiateFederateRestore not implemented");
    }


    // 4.29
    @Override
    public void federationRestored() throws FederateInternalError {
        this.logger.warn("federationRestored not implemented");
    }


    // 4.29
    @Override
    public void federationNotRestored(final RestoreFailureReason reason) throws FederateInternalError {
        this.logger.warn("federationNotRestored not implemented");
    }


    // 4.32
    @Override
    public void federationRestoreStatusResponse(final FederateRestoreStatus[] response) throws FederateInternalError {
        this.logger.warn("federationRestoreStatusResponse not implemented");
    }

    /////////////////////////////////////
    //Declaration Management Services //
    /////////////////////////////////////


    // 5.10
    @Override
    public void startRegistrationForObjectClass(final ObjectClassHandle theClass) throws FederateInternalError {
        this.logger.warn("startRegistrationForObjectClass not implemented");
    }


    // 5.11
    @Override
    public void stopRegistrationForObjectClass(final ObjectClassHandle theClass) throws FederateInternalError {
        this.logger.warn("stopRegistrationForObjectClass not implemented");
    }


    // 5.12
    @Override
    public void turnInteractionsOn(final InteractionClassHandle theHandle) throws FederateInternalError {
        this.logger.warn("turnInteractionsOn not implemented");
    }


    // 5.13
    @Override
    public void turnInteractionsOff(final InteractionClassHandle theHandle) throws FederateInternalError {
        this.logger.warn("turnInteractionsOff not implemented");
    }

    ////////////////////////////////
    //Object Management Services //
    ////////////////////////////////


    // 6.3
    @Override
    public void objectInstanceNameReservationSucceeded(final String objectName) throws FederateInternalError {
        this.logger.warn("objectInstanceNameReservationSucceeded not implemented");
    }


    // 6.3
    @Override
    public void objectInstanceNameReservationFailed(final String objectName) throws FederateInternalError {
        this.logger.warn("objectInstanceNameReservationFailed not implemented");
    }


    // 6.6
    @Override
    public void multipleObjectInstanceNameReservationSucceeded(final Set<String> objectNames) throws FederateInternalError {
        this.logger.warn("multipleObjectInstanceNameReservationSucceeded not implemented");
    }


    // 6.6
    @Override
    public void multipleObjectInstanceNameReservationFailed(final Set<String> objectNames) throws FederateInternalError {
        this.logger.warn("multipleObjectInstanceNameReservationFailed not implemented");
    }


    // 6.9
    @Override
    public void discoverObjectInstance(final ObjectInstanceHandle theObject, final ObjectClassHandle theObjectClass, final String objectName) throws FederateInternalError {
        this.logger.warn("discoverObjectInstance not implemented");
    }


    // 6.9
    @Override
    public void discoverObjectInstance(final ObjectInstanceHandle theObject, final ObjectClassHandle theObjectClass, final String objectName, final FederateHandle producingFederate) throws FederateInternalError {
        this.logger.warn("discoverObjectInstance not implemented");
    }


    // 6.11
    @Override
    public void reflectAttributeValues(final ObjectInstanceHandle theObject, final AttributeHandleValueMap theAttributes, final byte[] userSuppliedTag, final OrderType sentOrdering, final TransportationTypeHandle theTransport, final SupplementalReflectInfo reflectInfo) throws FederateInternalError {
        this.logger.warn("reflectAttributeValues not implemented");
    }


    // 6.11
    @Override
    public void reflectAttributeValues(final ObjectInstanceHandle theObject, final AttributeHandleValueMap theAttributes, final byte[] userSuppliedTag, final OrderType sentOrdering, final TransportationTypeHandle theTransport, final LogicalTime theTime, final OrderType receivedOrdering, final SupplementalReflectInfo reflectInfo) throws FederateInternalError {
        this.logger.warn("reflectAttributeValues not implemented");
    }


    // 6.11
    @Override
    public void reflectAttributeValues(final ObjectInstanceHandle theObject, final AttributeHandleValueMap theAttributes, final byte[] userSuppliedTag, final OrderType sentOrdering, final TransportationTypeHandle theTransport, final LogicalTime theTime, final OrderType receivedOrdering, final MessageRetractionHandle retractionHandle, final SupplementalReflectInfo reflectInfo) throws FederateInternalError {
        this.logger.warn("reflectAttributeValues not implemented");
    }


    // 6.13
    @Override
    public void receiveInteraction(final InteractionClassHandle interactionClass, final ParameterHandleValueMap theParameters, final byte[] userSuppliedTag, final OrderType sentOrdering, final TransportationTypeHandle theTransport, final SupplementalReceiveInfo receiveInfo) throws FederateInternalError {
        this.logger.warn("receiveInteraction not implemented");
    }


    // 6.13
    @Override
    public void receiveInteraction(final InteractionClassHandle interactionClass, final ParameterHandleValueMap theParameters, final byte[] userSuppliedTag, final OrderType sentOrdering, final TransportationTypeHandle theTransport, final LogicalTime theTime, final OrderType receivedOrdering, final SupplementalReceiveInfo receiveInfo) throws FederateInternalError {
        this.logger.warn("receiveInteraction not implemented");
    }


    // 6.13
    @Override
    public void receiveInteraction(final InteractionClassHandle interactionClass, final ParameterHandleValueMap theParameters, final byte[] userSuppliedTag, final OrderType sentOrdering, final TransportationTypeHandle theTransport, final LogicalTime theTime, final OrderType receivedOrdering, final MessageRetractionHandle retractionHandle, final SupplementalReceiveInfo receiveInfo) throws FederateInternalError {
        this.logger.warn("receiveInteraction not implemented");
    }


    // 6.15
    @Override
    public void removeObjectInstance(final ObjectInstanceHandle theObject, final byte[] userSuppliedTag, final OrderType sentOrdering, final SupplementalRemoveInfo removeInfo) throws FederateInternalError {
        this.logger.warn("removeObjectInstance not implemented");
    }


    // 6.15
    @Override
    public void removeObjectInstance(final ObjectInstanceHandle theObject, final byte[] userSuppliedTag, final OrderType sentOrdering, final LogicalTime theTime, final OrderType receivedOrdering, final SupplementalRemoveInfo removeInfo) throws FederateInternalError {
        this.logger.warn("removeObjectInstance not implemented");
    }


    // 6.15
    @Override
    public void removeObjectInstance(final ObjectInstanceHandle theObject, final byte[] userSuppliedTag, final OrderType sentOrdering, final LogicalTime theTime, final OrderType receivedOrdering, final MessageRetractionHandle retractionHandle, final SupplementalRemoveInfo removeInfo) throws FederateInternalError {
        this.logger.warn("removeObjectInstance not implemented");
    }


    // 6.17
    @Override
    public void attributesInScope(final ObjectInstanceHandle theObject, final AttributeHandleSet theAttributes) throws FederateInternalError {
        this.logger.warn("attributesInScope not implemented");
    }


    // 6.18
    @Override
    public void attributesOutOfScope(final ObjectInstanceHandle theObject, final AttributeHandleSet theAttributes) throws FederateInternalError {
        this.logger.warn("attributesOutOfScope not implemented");
    }


    // 6.20
    @Override
    public void provideAttributeValueUpdate(final ObjectInstanceHandle theObject, final AttributeHandleSet theAttributes, final byte[] userSuppliedTag) throws FederateInternalError {
        this.logger.warn("provideAttributeValueUpdate not implemented");
    }


    // 6.21
    @Override
    public void turnUpdatesOnForObjectInstance(final ObjectInstanceHandle theObject, final AttributeHandleSet theAttributes) throws FederateInternalError {
        this.logger.warn("turnUpdatesOnForObjectInstance not implemented");
    }


    // 6.21
    @Override
    public void turnUpdatesOnForObjectInstance(final ObjectInstanceHandle theObject, final AttributeHandleSet theAttributes, final String updateRateDesignator) throws FederateInternalError {
        this.logger.warn("turnUpdatesOnForObjectInstance not implemented");
    }


    // 6.22
    @Override
    public void turnUpdatesOffForObjectInstance(final ObjectInstanceHandle theObject, final AttributeHandleSet theAttributes) throws FederateInternalError {
        this.logger.warn("turnUpdatesOffForObjectInstance not implemented");
    }


    // 6.24
    @Override
    public void confirmAttributeTransportationTypeChange(final ObjectInstanceHandle theObject, final AttributeHandleSet theAttributes, final TransportationTypeHandle theTransportation) throws FederateInternalError {
        this.logger.warn("confirmAttributeTransportationTypeChange not implemented");
    }


    // 6.26
    @Override
    public void reportAttributeTransportationType(final ObjectInstanceHandle theObject, final AttributeHandle theAttribute, final TransportationTypeHandle theTransportation) throws FederateInternalError {
        this.logger.warn("reportAttributeTransportationType not implemented");
    }


    // 6.28
    @Override
    public void confirmInteractionTransportationTypeChange(final InteractionClassHandle theInteraction, final TransportationTypeHandle theTransportation) throws FederateInternalError {
        this.logger.warn("confirmInteractionTransportationTypeChange not implemented");
    }


    // 6.30
    @Override
    public void reportInteractionTransportationType(final FederateHandle theFederate, final InteractionClassHandle theInteraction, final TransportationTypeHandle theTransportation) throws FederateInternalError {
        this.logger.warn("reportInteractionTransportationType not implemented");
    }

    ///////////////////////////////////
    //Ownership Management Services //
    ///////////////////////////////////


    // 7.4
    @Override
    public void requestAttributeOwnershipAssumption(final ObjectInstanceHandle theObject, final AttributeHandleSet offeredAttributes, final byte[] userSuppliedTag) throws FederateInternalError {
        this.logger.warn("requestAttributeOwnershipAssumption not implemented");
    }


    // 7.5
    @Override
    public void requestDivestitureConfirmation(final ObjectInstanceHandle theObject, final AttributeHandleSet offeredAttributes) throws FederateInternalError {
        this.logger.warn("requestDivestitureConfirmation not implemented");
    }


    // 7.7
    @Override
    public void attributeOwnershipAcquisitionNotification(final ObjectInstanceHandle theObject, final AttributeHandleSet securedAttributes, final byte[] userSuppliedTag) throws FederateInternalError {
        this.logger.warn("attributeOwnershipAcquisitionNotification not implemented");
    }


    // 7.10
    @Override
    public void attributeOwnershipUnavailable(final ObjectInstanceHandle theObject, final AttributeHandleSet theAttributes) throws FederateInternalError {
        this.logger.warn("attributeOwnershipUnavailable not implemented");
    }


    // 7.11
    @Override
    public void requestAttributeOwnershipRelease(final ObjectInstanceHandle theObject, final AttributeHandleSet candidateAttributes, final byte[] userSuppliedTag) throws FederateInternalError {
        this.logger.warn("requestAttributeOwnershipRelease not implemented");
    }


    // 7.16
    @Override
    public void confirmAttributeOwnershipAcquisitionCancellation(final ObjectInstanceHandle theObject, final AttributeHandleSet theAttributes) throws FederateInternalError {
        this.logger.warn("confirmAttributeOwnershipAcquisitionCancellation not implemented");
    }


    // 7.18
    @Override
    public void informAttributeOwnership(final ObjectInstanceHandle theObject, final AttributeHandle theAttribute, final FederateHandle theOwner) throws FederateInternalError {
        this.logger.warn("informAttributeOwnership not implemented");
    }


    // 7.18
    @Override
    public void attributeIsNotOwned(final ObjectInstanceHandle theObject, final AttributeHandle theAttribute) throws FederateInternalError {
        this.logger.warn("attributeIsNotOwned not implemented");
    }


    // 7.18
    @Override
    public void attributeIsOwnedByRTI(final ObjectInstanceHandle theObject, final AttributeHandle theAttribute) throws FederateInternalError {
        this.logger.warn("attributeIsOwnedByRTI not implemented");
    }

    //////////////////////////////
    //Time Management Services //
    //////////////////////////////


    // 8.3
    @Override
    public void timeRegulationEnabled(final LogicalTime time) throws FederateInternalError {
        this.logger.warn("timeRegulationEnabled not implemented");
    }


    // 8.6
    @Override
    public void timeConstrainedEnabled(final LogicalTime time) throws FederateInternalError {
        this.logger.warn("timeConstrainedEnabled not implemented");
    }


    // 8.13
    @Override
    public void timeAdvanceGrant(final LogicalTime theTime) throws FederateInternalError {
        this.logger.warn("timeAdvanceGrant not implemented");
    }


    // 8.22
    @Override
    public void requestRetraction(final MessageRetractionHandle theHandle) throws FederateInternalError {
        this.logger.warn("requestRetraction not implemented");
    }
}
