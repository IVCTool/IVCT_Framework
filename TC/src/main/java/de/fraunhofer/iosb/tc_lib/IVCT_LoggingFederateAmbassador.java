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
import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import org.slf4j.Logger;


/**
 * A wrapper to add additional functionality to rti calls e.g. automatic logging
 * e.g. combine rti calls to make test cases more compact
 *
 * @author Johannes Mulder (Fraunhofer IOSB)
 */
public final class IVCT_LoggingFederateAmbassador implements FederateAmbassador {
    private IVCT_NullFederateAmbassador myFederateAmbassador;
    private Logger                      logger;


    ////////////////////////////////////
    //Federation Management Services //
    ////////////////////////////////////

    /**
     * @param theFederateAmbassador
     * @param logger
     */
    public IVCT_LoggingFederateAmbassador(final FederateAmbassador theFederateAmbassador, final Logger logger) {
        this.myFederateAmbassador = (IVCT_NullFederateAmbassador) theFederateAmbassador;
        this.logger = logger;
    }


    // 4.4
    /**
     * {@inheritDoc}
     */
    @Override
    public void connectionLost(final String faultDescription) throws FederateInternalError {
        this.logger.info("connectionLost " + faultDescription);
        this.myFederateAmbassador.connectionLost(faultDescription);
    }


    // 4.8
    /**
     * {@inheritDoc}
     */
    @Override
    public void reportFederationExecutions(final FederationExecutionInformationSet theFederationExecutionInformationSet) throws FederateInternalError {
        this.logger.info("reportFederationExecutions " + theFederationExecutionInformationSet.toString());
        this.myFederateAmbassador.reportFederationExecutions(theFederationExecutionInformationSet);
    }


    //4.12
    /**
     * {@inheritDoc}
     */
    @Override
    public void synchronizationPointRegistrationSucceeded(final String synchronizationPointLabel) throws FederateInternalError {
        this.logger.info("synchronizationPointRegistrationSucceeded " + synchronizationPointLabel);
        this.myFederateAmbassador.synchronizationPointRegistrationSucceeded(synchronizationPointLabel);
    }


    //4.12
    /**
     * {@inheritDoc}
     */
    @Override
    public void synchronizationPointRegistrationFailed(final String synchronizationPointLabel, final SynchronizationPointFailureReason reason) throws FederateInternalError {
        this.logger.info("synchronizationPointRegistrationFailed " + synchronizationPointLabel + reason.toString());
        this.myFederateAmbassador.synchronizationPointRegistrationFailed(synchronizationPointLabel, reason);
    }


    //4.13
    /**
     * {@inheritDoc}
     */
    @Override
    public void announceSynchronizationPoint(final String synchronizationPointLabel, final byte[] userSuppliedTag) throws FederateInternalError {
        this.logger.info("announceSynchronizationPoint " + synchronizationPointLabel + " " + Arrays.toString(userSuppliedTag));
        this.myFederateAmbassador.announceSynchronizationPoint(synchronizationPointLabel, userSuppliedTag);
    }


    //4.15
    /**
     * {@inheritDoc}
     */
    @Override
    public void federationSynchronized(final String synchronizationPointLabel, final FederateHandleSet failedToSyncSet) throws FederateInternalError {
        this.logger.info("federationSynchronized " + synchronizationPointLabel + " " + failedToSyncSet.toString());
        this.myFederateAmbassador.federationSynchronized(synchronizationPointLabel, failedToSyncSet);
    }


    //4.17
    /**
     * {@inheritDoc}
     */
    @Override
    public void initiateFederateSave(final String label) throws FederateInternalError {
        this.logger.info("initiateFederateSave " + label);
        this.myFederateAmbassador.initiateFederateSave(label);
    }


    //4.17
    /**
     * {@inheritDoc}
     */
    @Override
    public void initiateFederateSave(final String label, final LogicalTime time) throws FederateInternalError {
        this.logger.info("initiateFederateSave " + label + " " + time.toString());
        this.myFederateAmbassador.initiateFederateSave(label, time);
    }


    // 4.20
    /**
     * {@inheritDoc}
     */
    @Override
    public void federationSaved() throws FederateInternalError {
        this.logger.info("federationSaved");
        this.myFederateAmbassador.federationSaved();
    }


    // 4.20
    /**
     * {@inheritDoc}
     */
    @Override
    public void federationNotSaved(final SaveFailureReason reason) throws FederateInternalError {
        this.logger.info("federationNotSaved " + reason.toString());
        this.myFederateAmbassador.federationNotSaved(reason);
    }


    // 4.23
    /**
     * {@inheritDoc}
     */
    @Override
    public void federationSaveStatusResponse(final FederateHandleSaveStatusPair[] response) throws FederateInternalError {
        this.logger.info("federationSaveStatusResponse " + response.toString());
        this.myFederateAmbassador.federationSaveStatusResponse(response);
    }


    // 4.25
    /**
     * {@inheritDoc}
     */
    @Override
    public void requestFederationRestoreSucceeded(final String label) throws FederateInternalError {
        this.logger.info("requestFederationRestoreSucceeded " + label);
        this.myFederateAmbassador.requestFederationRestoreSucceeded(label);
    }


    // 4.25
    /**
     * {@inheritDoc}
     */
    @Override
    public void requestFederationRestoreFailed(final String label) throws FederateInternalError {
        this.logger.info("requestFederationRestoreFailed " + label);
        this.myFederateAmbassador.requestFederationRestoreFailed(label);
    }


    // 4.26
    /**
     * {@inheritDoc}
     */
    @Override
    public void federationRestoreBegun() throws FederateInternalError {
        this.logger.info("federationRestoreBegun");
        this.myFederateAmbassador.federationRestoreBegun();
    }


    // 4.27
    /**
     * {@inheritDoc}
     */
    @Override
    public void initiateFederateRestore(final String label, final String federateName, final FederateHandle federateHandle) throws FederateInternalError {
        this.logger.info("initiateFederateRestore " + label + " " + federateName + " " + federateHandle.toString());
        this.myFederateAmbassador.initiateFederateRestore(label, federateName, federateHandle);
    }


    // 4.29
    /**
     * {@inheritDoc}
     */
    @Override
    public void federationRestored() throws FederateInternalError {
        this.logger.info("federationRestored");
        this.myFederateAmbassador.federationRestored();
    }


    // 4.29
    /**
     * {@inheritDoc}
     */
    @Override
    public void federationNotRestored(final RestoreFailureReason reason) throws FederateInternalError {
        this.logger.info("federationNotRestored " + reason.toString());
        this.myFederateAmbassador.federationNotRestored(reason);
    }


    // 4.32
    /**
     * {@inheritDoc}
     */
    @Override
    public void federationRestoreStatusResponse(final FederateRestoreStatus[] response) throws FederateInternalError {
        this.logger.info("federationRestoreStatusResponse " + response.toString());
        this.myFederateAmbassador.federationRestoreStatusResponse(response);
    }

    /////////////////////////////////////
    //Declaration Management Services //
    /////////////////////////////////////


    // 5.10
    /**
     * {@inheritDoc}
     */
    @Override
    public void startRegistrationForObjectClass(final ObjectClassHandle theClass) throws FederateInternalError {
        this.logger.info("startRegistrationForObjectClass " + theClass.toString());
        this.myFederateAmbassador.startRegistrationForObjectClass(theClass);
    }


    // 5.11
    /**
     * {@inheritDoc}
     */
    @Override
    public void stopRegistrationForObjectClass(final ObjectClassHandle theClass) throws FederateInternalError {
        this.logger.info("stopRegistrationForObjectClass " + theClass.toString());
        this.myFederateAmbassador.stopRegistrationForObjectClass(theClass);
    }


    // 5.12
    /**
     * {@inheritDoc}
     */
    @Override
    public void turnInteractionsOn(final InteractionClassHandle theHandle) throws FederateInternalError {
        this.logger.info("turnInteractionsOn " + theHandle.toString());
        this.myFederateAmbassador.turnInteractionsOn(theHandle);
    }


    // 5.13
    /**
     * {@inheritDoc}
     */
    @Override
    public void turnInteractionsOff(final InteractionClassHandle theHandle) throws FederateInternalError {
        this.logger.info("turnInteractionsOff " + theHandle);
        this.myFederateAmbassador.turnInteractionsOff(theHandle);
    }

    ////////////////////////////////
    //Object Management Services //
    ////////////////////////////////


    // 6.3
    /**
     * {@inheritDoc}
     */
    @Override
    public void objectInstanceNameReservationSucceeded(final String objectName) throws FederateInternalError {
        this.logger.info("objectInstanceNameReservationSucceeded " + objectName);
        this.myFederateAmbassador.objectInstanceNameReservationSucceeded(objectName);
    }


    // 6.3
    /**
     * {@inheritDoc}
     */
    @Override
    public void objectInstanceNameReservationFailed(final String objectName) throws FederateInternalError {
        this.logger.info("objectInstanceNameReservationFailed " + objectName);
        this.myFederateAmbassador.objectInstanceNameReservationFailed(objectName);
    }


    // 6.6
    /**
     * {@inheritDoc}
     */
    @Override
    public void multipleObjectInstanceNameReservationSucceeded(final Set<String> objectNames) throws FederateInternalError {
        this.logger.info("multipleObjectInstanceNameReservationSucceeded " + objectNames.toString());
        this.myFederateAmbassador.multipleObjectInstanceNameReservationSucceeded(objectNames);
    }


    // 6.6
    /**
     * {@inheritDoc}
     */
    @Override
    public void multipleObjectInstanceNameReservationFailed(final Set<String> objectNames) throws FederateInternalError {
        this.logger.info("multipleObjectInstanceNameReservationFailed " + objectNames.toString());
        this.myFederateAmbassador.multipleObjectInstanceNameReservationFailed(objectNames);
    }


    // 6.9
    /**
     * {@inheritDoc}
     */
    @Override
    public void discoverObjectInstance(final ObjectInstanceHandle theObject, final ObjectClassHandle theObjectClass, final String objectName) throws FederateInternalError {
        this.logger.info("discoverObjectInstance " + theObject.toString() + " " + theObjectClass.toString() + " " + objectName);
        this.myFederateAmbassador.discoverObjectInstance(theObject, theObjectClass, objectName);
    }


    // 6.9
    /**
     * {@inheritDoc}
     */
    @Override
    public void discoverObjectInstance(final ObjectInstanceHandle theObject, final ObjectClassHandle theObjectClass, final String objectName, final FederateHandle producingFederate) throws FederateInternalError {
        this.logger.info("discoverObjectInstance " + theObject.toString() + " " + theObjectClass.toString() + " " + objectName + " " + producingFederate.toString());
        this.myFederateAmbassador.discoverObjectInstance(theObject, theObjectClass, objectName, producingFederate);
    }


    private String printAttributeHandleValueMap(final AttributeHandleValueMap theAttributes) {
        final StringBuilder stringBuilder = new StringBuilder();
        for (final Map.Entry entry: theAttributes.entrySet()) {
            stringBuilder.append(entry.getKey() + " ");
        }
        return stringBuilder.toString();
    }


    // 6.11
    /**
     * {@inheritDoc}
     */
    @Override
    public void reflectAttributeValues(final ObjectInstanceHandle theObject, final AttributeHandleValueMap theAttributes, final byte[] userSuppliedTag, final OrderType sentOrdering, final TransportationTypeHandle theTransport, final SupplementalReflectInfo reflectInfo) throws FederateInternalError {
        final String attributesStr = this.printAttributeHandleValueMap(theAttributes);
        this.logger.info("reflectAttributeValues " + theObject.toString() + " " + attributesStr.toString() + " " + Arrays.toString(userSuppliedTag) + " " + sentOrdering.toString() + " " + theTransport.toString() + " " + reflectInfo.toString());
        this.myFederateAmbassador.reflectAttributeValues(theObject, theAttributes, userSuppliedTag, sentOrdering, theTransport, reflectInfo);
    }


    // 6.11
    /**
     * {@inheritDoc}
     */
    @Override
    public void reflectAttributeValues(final ObjectInstanceHandle theObject, final AttributeHandleValueMap theAttributes, final byte[] userSuppliedTag, final OrderType sentOrdering, final TransportationTypeHandle theTransport, final LogicalTime theTime, final OrderType receivedOrdering, final SupplementalReflectInfo reflectInfo) throws FederateInternalError {
        final String attributesStr = this.printAttributeHandleValueMap(theAttributes);
        this.logger.info("reflectAttributeValues " + theObject.toString() + " " + attributesStr.toString() + " " + Arrays.toString(userSuppliedTag) + " " + sentOrdering.toString() + " " + theTransport.toString() + " " + theTime.toString() + " " + receivedOrdering.toString() + " " + reflectInfo.toString());
        this.myFederateAmbassador.reflectAttributeValues(theObject, theAttributes, userSuppliedTag, sentOrdering, theTransport, theTime, receivedOrdering, reflectInfo);
    }


    // 6.11
    /**
     * {@inheritDoc}
     */
    @Override
    public void reflectAttributeValues(final ObjectInstanceHandle theObject, final AttributeHandleValueMap theAttributes, final byte[] userSuppliedTag, final OrderType sentOrdering, final TransportationTypeHandle theTransport, final LogicalTime theTime, final OrderType receivedOrdering, final MessageRetractionHandle retractionHandle, final SupplementalReflectInfo reflectInfo) throws FederateInternalError {
        final String attributesStr = this.printAttributeHandleValueMap(theAttributes);
        this.logger.info("reflectAttributeValues " + theObject.toString() + " " + attributesStr.toString() + " " + Arrays.toString(userSuppliedTag) + " " + sentOrdering.toString() + " " + theTransport.toString() + " " + theTime.toString() + " " + receivedOrdering.toString() + " " + retractionHandle.toString() + " " + reflectInfo.toString());
        this.myFederateAmbassador.reflectAttributeValues(theObject, theAttributes, userSuppliedTag, sentOrdering, theTransport, theTime, receivedOrdering, retractionHandle, reflectInfo);
    }


    private String printParameterHandleValueMap(final ParameterHandleValueMap theAttributes) {
        final StringBuilder stringBuilder = new StringBuilder();
        for (final Map.Entry entry: theAttributes.entrySet()) {
            stringBuilder.append(entry.getKey() + " ");
        }
        return stringBuilder.toString();
    }


    // 6.13
    /**
     * {@inheritDoc}
     */
    @Override
    public void receiveInteraction(final InteractionClassHandle interactionClass, final ParameterHandleValueMap theParameters, final byte[] userSuppliedTag, final OrderType sentOrdering, final TransportationTypeHandle theTransport, final SupplementalReceiveInfo receiveInfo) throws FederateInternalError {
        final String parametersStr = this.printParameterHandleValueMap(theParameters);
        this.logger.info("receiveInteraction " + interactionClass.toString() + " " + parametersStr.toString() + " " + Arrays.toString(userSuppliedTag) + " " + sentOrdering.toString() + " " + theTransport.toString() + " " + receiveInfo.toString());
        this.myFederateAmbassador.receiveInteraction(interactionClass, theParameters, userSuppliedTag, sentOrdering, theTransport, receiveInfo);
    }


    // 6.13
    /**
     * {@inheritDoc}
     */
    @Override
    public void receiveInteraction(final InteractionClassHandle interactionClass, final ParameterHandleValueMap theParameters, final byte[] userSuppliedTag, final OrderType sentOrdering, final TransportationTypeHandle theTransport, final LogicalTime theTime, final OrderType receivedOrdering, final SupplementalReceiveInfo receiveInfo) throws FederateInternalError {
        final String parametersStr = this.printParameterHandleValueMap(theParameters);
        this.logger.info("receiveInteraction " + interactionClass.toString() + " " + parametersStr.toString() + " " + Arrays.toString(userSuppliedTag) + " " + sentOrdering.toString() + " " + theTransport.toString() + " " + theTime.toString() + " " + receivedOrdering.toString() + " " + receiveInfo.toString());
        this.myFederateAmbassador.receiveInteraction(interactionClass, theParameters, userSuppliedTag, sentOrdering, theTransport, theTime, receivedOrdering, receiveInfo);
    }


    // 6.13
    /**
     * {@inheritDoc}
     */
    @Override
    public void receiveInteraction(final InteractionClassHandle interactionClass, final ParameterHandleValueMap theParameters, final byte[] userSuppliedTag, final OrderType sentOrdering, final TransportationTypeHandle theTransport, final LogicalTime theTime, final OrderType receivedOrdering, final MessageRetractionHandle retractionHandle, final SupplementalReceiveInfo receiveInfo) throws FederateInternalError {
        final String parametersStr = this.printParameterHandleValueMap(theParameters);
        this.logger.info("receiveInteraction " + interactionClass.toString() + " " + parametersStr.toString() + " " + Arrays.toString(userSuppliedTag) + " " + sentOrdering.toString() + " " + theTransport.toString() + " " + receivedOrdering.toString() + " " + retractionHandle.toString() + " " + receiveInfo.toString());
        this.myFederateAmbassador.receiveInteraction(interactionClass, theParameters, userSuppliedTag, sentOrdering, theTransport, theTime, receivedOrdering, retractionHandle, receiveInfo);
    }


    // 6.15
    /**
     * {@inheritDoc}
     */
    @Override
    public void removeObjectInstance(final ObjectInstanceHandle theObject, final byte[] userSuppliedTag, final OrderType sentOrdering, final SupplementalRemoveInfo removeInfo) throws FederateInternalError {
        this.logger.info("removeObjectInstance " + theObject.toString() + " " + Arrays.toString(userSuppliedTag) + " " + sentOrdering.toString() + " " + removeInfo.toString());
        this.myFederateAmbassador.removeObjectInstance(theObject, userSuppliedTag, sentOrdering, removeInfo);
    }


    // 6.15
    /**
     * {@inheritDoc}
     */
    @Override
    public void removeObjectInstance(final ObjectInstanceHandle theObject, final byte[] userSuppliedTag, final OrderType sentOrdering, final LogicalTime theTime, final OrderType receivedOrdering, final SupplementalRemoveInfo removeInfo) throws FederateInternalError {
        this.logger.info("removeObjectInstance " + theObject.toString() + " " + Arrays.toString(userSuppliedTag) + " " + sentOrdering.toString() + " " + theTime.toString() + " " + receivedOrdering.toString() + " " + removeInfo.toString());
        this.myFederateAmbassador.removeObjectInstance(theObject, userSuppliedTag, sentOrdering, theTime, receivedOrdering, removeInfo);
    }


    // 6.15
    /**
     * {@inheritDoc}
     */
    @Override
    public void removeObjectInstance(final ObjectInstanceHandle theObject, final byte[] userSuppliedTag, final OrderType sentOrdering, final LogicalTime theTime, final OrderType receivedOrdering, final MessageRetractionHandle retractionHandle, final SupplementalRemoveInfo removeInfo) throws FederateInternalError {
        this.logger.info("removeObjectInstance " + theObject.toString() + " " + Arrays.toString(userSuppliedTag) + " " + sentOrdering.toString() + " " + theTime.toString() + " " + receivedOrdering.toString() + " " + retractionHandle.toString() + " " + removeInfo.toString());
        this.myFederateAmbassador.removeObjectInstance(theObject, userSuppliedTag, sentOrdering, theTime, receivedOrdering, retractionHandle, removeInfo);
    }


    // 6.17
    /**
     * {@inheritDoc}
     */
    @Override
    public void attributesInScope(final ObjectInstanceHandle theObject, final AttributeHandleSet theAttributes) throws FederateInternalError {
        this.logger.info("attributesInScope " + theObject.toString() + " " + theAttributes.toString());
        this.myFederateAmbassador.attributesInScope(theObject, theAttributes);
    }


    // 6.18
    /**
     * {@inheritDoc}
     */
    @Override
    public void attributesOutOfScope(final ObjectInstanceHandle theObject, final AttributeHandleSet theAttributes) throws FederateInternalError {
        this.logger.info("attributesOutOfScope " + theObject.toString() + " " + theAttributes.toString());
        this.myFederateAmbassador.attributesOutOfScope(theObject, theAttributes);
    }


    // 6.20
    /**
     * {@inheritDoc}
     */
    @Override
    public void provideAttributeValueUpdate(final ObjectInstanceHandle theObject, final AttributeHandleSet theAttributes, final byte[] userSuppliedTag) throws FederateInternalError {
        this.logger.info("provideAttributeValueUpdate " + theObject.toString() + " " + theAttributes.toString() + " " + Arrays.toString(userSuppliedTag));
        this.myFederateAmbassador.provideAttributeValueUpdate(theObject, theAttributes, userSuppliedTag);
    }


    // 6.21
    /**
     * {@inheritDoc}
     */
    @Override
    public void turnUpdatesOnForObjectInstance(final ObjectInstanceHandle theObject, final AttributeHandleSet theAttributes) throws FederateInternalError {
        this.logger.info("turnUpdatesOnForObjectInstance " + theObject.toString() + " " + theAttributes.toString());
        this.myFederateAmbassador.turnUpdatesOnForObjectInstance(theObject, theAttributes);
    }


    // 6.21
    /**
     * {@inheritDoc}
     */
    @Override
    public void turnUpdatesOnForObjectInstance(final ObjectInstanceHandle theObject, final AttributeHandleSet theAttributes, final String updateRateDesignator) throws FederateInternalError {
        this.logger.info("turnUpdatesOnForObjectInstance " + theObject.toString() + " " + theAttributes.toString() + " " + updateRateDesignator);
        this.myFederateAmbassador.turnUpdatesOnForObjectInstance(theObject, theAttributes, updateRateDesignator);
    }


    // 6.22
    /**
     * {@inheritDoc}
     */
    @Override
    public void turnUpdatesOffForObjectInstance(final ObjectInstanceHandle theObject, final AttributeHandleSet theAttributes) throws FederateInternalError {
        this.logger.info("turnUpdatesOffForObjectInstance " + theObject.toString() + " " + theAttributes.toString());
        this.myFederateAmbassador.turnUpdatesOffForObjectInstance(theObject, theAttributes);
    }


    // 6.24
    /**
     * {@inheritDoc}
     */
    @Override
    public void confirmAttributeTransportationTypeChange(final ObjectInstanceHandle theObject, final AttributeHandleSet theAttributes, final TransportationTypeHandle theTransportation) throws FederateInternalError {
        this.logger.info("confirmAttributeTransportationTypeChange " + theObject.toString() + " " + theAttributes.toString() + " " + theTransportation.toString());
        this.myFederateAmbassador.confirmAttributeTransportationTypeChange(theObject, theAttributes, theTransportation);
    }


    // 6.26
    /**
     * {@inheritDoc}
     */
    @Override
    public void reportAttributeTransportationType(final ObjectInstanceHandle theObject, final AttributeHandle theAttribute, final TransportationTypeHandle theTransportation) throws FederateInternalError {
        this.logger.info("reportAttributeTransportationType " + theObject.toString() + " " + theAttribute.toString() + " " + theTransportation.toString());
        this.myFederateAmbassador.reportAttributeTransportationType(theObject, theAttribute, theTransportation);
    }


    // 6.28
    /**
     * {@inheritDoc}
     */
    @Override
    public void confirmInteractionTransportationTypeChange(final InteractionClassHandle theInteraction, final TransportationTypeHandle theTransportation) throws FederateInternalError {
        this.logger.info("confirmInteractionTransportationTypeChange " + theInteraction.toString() + " " + theTransportation.toString());
        this.myFederateAmbassador.confirmInteractionTransportationTypeChange(theInteraction, theTransportation);
    }


    // 6.30
    /**
     * {@inheritDoc}
     */
    @Override
    public void reportInteractionTransportationType(final FederateHandle theFederate, final InteractionClassHandle theInteraction, final TransportationTypeHandle theTransportation) throws FederateInternalError {
        this.logger.info("reportInteractionTransportationType " + theFederate.toString() + " " + theInteraction.toString() + " " + theTransportation.toString());
        this.myFederateAmbassador.reportInteractionTransportationType(theFederate, theInteraction, theTransportation);
    }

    ///////////////////////////////////
    //Ownership Management Services //
    ///////////////////////////////////


    // 7.4
    /**
     * {@inheritDoc}
     */
    @Override
    public void requestAttributeOwnershipAssumption(final ObjectInstanceHandle theObject, final AttributeHandleSet offeredAttributes, final byte[] userSuppliedTag) throws FederateInternalError {
        this.logger.info("requestAttributeOwnershipAssumption " + theObject.toString() + " " + offeredAttributes + " " + Arrays.toString(userSuppliedTag));
        this.myFederateAmbassador.requestAttributeOwnershipAssumption(theObject, offeredAttributes, userSuppliedTag);
    }


    // 7.5
    /**
     * {@inheritDoc}
     */
    @Override
    public void requestDivestitureConfirmation(final ObjectInstanceHandle theObject, final AttributeHandleSet offeredAttributes) throws FederateInternalError {
        this.logger.info("requestDivestitureConfirmation " + theObject.toString() + " " + offeredAttributes.toString());
        this.myFederateAmbassador.requestDivestitureConfirmation(theObject, offeredAttributes);
    }


    // 7.7
    /**
     * {@inheritDoc}
     */
    @Override
    public void attributeOwnershipAcquisitionNotification(final ObjectInstanceHandle theObject, final AttributeHandleSet securedAttributes, final byte[] userSuppliedTag) throws FederateInternalError {
        this.logger.info("attributeOwnershipAcquisitionNotification " + theObject.toString() + " " + securedAttributes.toString() + " " + Arrays.toString(userSuppliedTag));
        this.myFederateAmbassador.attributeOwnershipAcquisitionNotification(theObject, securedAttributes, userSuppliedTag);
    }


    // 7.10
    /**
     * {@inheritDoc}
     */
    @Override
    public void attributeOwnershipUnavailable(final ObjectInstanceHandle theObject, final AttributeHandleSet theAttributes) throws FederateInternalError {
        this.logger.info("attributeOwnershipUnavailable " + theObject.toString() + " " + theAttributes.toString());
        this.myFederateAmbassador.attributeOwnershipUnavailable(theObject, theAttributes);
    }


    // 7.11
    /**
     * {@inheritDoc}
     */
    @Override
    public void requestAttributeOwnershipRelease(final ObjectInstanceHandle theObject, final AttributeHandleSet candidateAttributes, final byte[] userSuppliedTag) throws FederateInternalError {
        this.logger.info("requestAttributeOwnershipRelease " + theObject.toString() + " " + candidateAttributes.toString() + " " + Arrays.toString(userSuppliedTag));
        this.myFederateAmbassador.requestAttributeOwnershipRelease(theObject, candidateAttributes, userSuppliedTag);
    }


    // 7.16
    /**
     * {@inheritDoc}
     */
    @Override
    public void confirmAttributeOwnershipAcquisitionCancellation(final ObjectInstanceHandle theObject, final AttributeHandleSet theAttributes) throws FederateInternalError {
        this.logger.info("confirmAttributeOwnershipAcquisitionCancellation " + theObject.toString() + " " + theAttributes.toString());
        this.myFederateAmbassador.confirmAttributeOwnershipAcquisitionCancellation(theObject, theAttributes);
    }


    // 7.18
    /**
     * {@inheritDoc}
     */
    @Override
    public void informAttributeOwnership(final ObjectInstanceHandle theObject, final AttributeHandle theAttribute, final FederateHandle theOwner) throws FederateInternalError {
        this.logger.info("informAttributeOwnership " + theObject.toString() + " " + theAttribute.toString() + " " + theOwner.toString());
        this.myFederateAmbassador.informAttributeOwnership(theObject, theAttribute, theOwner);
    }


    // 7.18
    /**
     * {@inheritDoc}
     */
    @Override
    public void attributeIsNotOwned(final ObjectInstanceHandle theObject, final AttributeHandle theAttribute) throws FederateInternalError {
        this.logger.info("attributeIsNotOwned " + theObject.toString() + " " + theAttribute.toString());
        this.myFederateAmbassador.attributeIsNotOwned(theObject, theAttribute);
    }


    // 7.18
    /**
     * {@inheritDoc}
     */
    @Override
    public void attributeIsOwnedByRTI(final ObjectInstanceHandle theObject, final AttributeHandle theAttribute) throws FederateInternalError {
        this.logger.info("attributeIsOwnedByRTI " + theObject.toString() + " " + theAttribute.toString());
        this.myFederateAmbassador.attributeIsOwnedByRTI(theObject, theAttribute);
    }

    //////////////////////////////
    //Time Management Services //
    //////////////////////////////


    // 8.3
    /**
     * {@inheritDoc}
     */
    @Override
    public void timeRegulationEnabled(final LogicalTime time) throws FederateInternalError {
        this.logger.info("timeRegulationEnabled " + time.toString());
        this.myFederateAmbassador.timeRegulationEnabled(time);
    }


    // 8.6
    /**
     * {@inheritDoc}
     */
    @Override
    public void timeConstrainedEnabled(final LogicalTime time) throws FederateInternalError {
        this.logger.info("timeConstrainedEnabled " + time.toString());
        this.myFederateAmbassador.timeConstrainedEnabled(time);
    }


    // 8.13
    /**
     * {@inheritDoc}
     */
    @Override
    public void timeAdvanceGrant(final LogicalTime theTime) throws FederateInternalError {
        this.logger.info("timeAdvanceGrant " + theTime.toString());
        this.myFederateAmbassador.timeAdvanceGrant(theTime);
    }


    // 8.22
    /**
     * {@inheritDoc}
     */
    @Override
    public void requestRetraction(final MessageRetractionHandle theHandle) throws FederateInternalError {
        this.logger.info("requestRetraction " + theHandle.toString());
        this.myFederateAmbassador.requestRetraction(theHandle);
    }
}
