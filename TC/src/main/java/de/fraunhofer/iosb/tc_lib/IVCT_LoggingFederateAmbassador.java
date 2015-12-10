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
	 * @param theFederateAmbassador the federate ambassador
	 * @param logger logger
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
		this.logger.info("connectionLost faultDescription=" + faultDescription);
		try {
			this.myFederateAmbassador.connectionLost(faultDescription);
		} catch (FederateInternalError e) {
			this.logger.error("connectionLost exception=" + e.getMessage());
			throw e;
		}
	}


	// 4.8
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void reportFederationExecutions(final FederationExecutionInformationSet theFederationExecutionInformationSet) throws FederateInternalError {
		this.logger.info("reportFederationExecutions theFederationExecutionInformationSet=" + theFederationExecutionInformationSet.toString());
		try {
			this.myFederateAmbassador.reportFederationExecutions(theFederationExecutionInformationSet);
		} catch (FederateInternalError e) {
			this.logger.error("reportFederationExecutions exception=" + e.getMessage());
			throw e;			
		}
	}


	//4.12
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void synchronizationPointRegistrationSucceeded(final String synchronizationPointLabel) throws FederateInternalError {
		this.logger.info("synchronizationPointRegistrationSucceeded synchronizationPointLabel=" + synchronizationPointLabel);
		try {
			this.myFederateAmbassador.synchronizationPointRegistrationSucceeded(synchronizationPointLabel);
		} catch (FederateInternalError e) {
			this.logger.error("synchronizationPointRegistrationSucceeded exception=" + e.getMessage());
			throw e;
		}
	}


	//4.12
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void synchronizationPointRegistrationFailed(final String synchronizationPointLabel, final SynchronizationPointFailureReason reason) throws FederateInternalError {
		this.logger.info("synchronizationPointRegistrationFailed synchronizationPointLabel=" + synchronizationPointLabel + reason.toString());
		try {
			this.myFederateAmbassador.synchronizationPointRegistrationFailed(synchronizationPointLabel, reason);
		} catch (FederateInternalError e) {
			this.logger.error("synchronizationPointRegistrationFailed exception=" + e.getMessage());
			throw e;
		}
	}


	//4.13
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void announceSynchronizationPoint(final String synchronizationPointLabel, final byte[] userSuppliedTag) throws FederateInternalError {
		this.logger.info("announceSynchronizationPoint synchronizationPointLabel=" + synchronizationPointLabel + ", userSuppliedTag=" + Arrays.toString(userSuppliedTag));
		try {
			this.myFederateAmbassador.announceSynchronizationPoint(synchronizationPointLabel, userSuppliedTag);
		} catch (FederateInternalError e) {
			this.logger.error("announceSynchronizationPoint exception=" + e.getMessage());
			throw e;
		}
	}


	//4.15
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void federationSynchronized(final String synchronizationPointLabel, final FederateHandleSet failedToSyncSet) throws FederateInternalError {
		this.logger.info("federationSynchronized synchronizationPointLabel=" + synchronizationPointLabel + ", failedToSyncSet=" + failedToSyncSet.toString());
		try {
			this.myFederateAmbassador.federationSynchronized(synchronizationPointLabel, failedToSyncSet);
		} catch (FederateInternalError e) {
			this.logger.error("federationSynchronized exception=" + e.getMessage());
			throw e;
		}
	}


	//4.17
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void initiateFederateSave(final String label) throws FederateInternalError {
		this.logger.info("initiateFederateSave label=" + label);
		try {
			this.myFederateAmbassador.initiateFederateSave(label);
		} catch (FederateInternalError e) {
			this.logger.error("initiateFederateSave exception=" + e.getMessage());
			throw e;
		}
	}


	//4.17
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void initiateFederateSave(final String label, final LogicalTime time) throws FederateInternalError {
		this.logger.info("initiateFederateSave label=" + label + ", time=" + time.toString());
		try {
			this.myFederateAmbassador.initiateFederateSave(label, time);
		} catch (FederateInternalError e) {
			this.logger.error("initiateFederateSave exception=" + e.getMessage());
			throw e;
		}
	}


	// 4.20
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void federationSaved() throws FederateInternalError {
		this.logger.info("federationSaved");
		try {
			this.myFederateAmbassador.federationSaved();
		} catch (FederateInternalError e) {
			this.logger.error("federationSaved exception=" + e.getMessage());
			throw e;
		}
	}


	// 4.20
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void federationNotSaved(final SaveFailureReason reason) throws FederateInternalError {
		this.logger.info("federationNotSaved reason=" + reason.toString());
		try {
			this.myFederateAmbassador.federationNotSaved(reason);
		} catch (FederateInternalError e) {
			this.logger.error("federationNotSaved exception=" + e.getMessage());
			throw e;
		}
	}


	// 4.23
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void federationSaveStatusResponse(final FederateHandleSaveStatusPair[] response) throws FederateInternalError {
		this.logger.info("federationSaveStatusResponse response=" + response.toString());
		try {
			this.myFederateAmbassador.federationSaveStatusResponse(response);
		} catch (FederateInternalError e) {
			this.logger.error("federationSaveStatusResponse exception=" + e.getMessage());
			throw e;
		}
	}


	// 4.25
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void requestFederationRestoreSucceeded(final String label) throws FederateInternalError {
		this.logger.info("requestFederationRestoreSucceeded label=" + label);
		try {
			this.myFederateAmbassador.requestFederationRestoreSucceeded(label);
		} catch (FederateInternalError e) {
			this.logger.error("requestFederationRestoreSucceeded exception=" + e.getMessage());
			throw e;
		}
	}


	// 4.25
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void requestFederationRestoreFailed(final String label) throws FederateInternalError {
		this.logger.info("requestFederationRestoreFailed label=" + label);
		try {
			this.myFederateAmbassador.requestFederationRestoreFailed(label);
		} catch (FederateInternalError e) {
			this.logger.error("requestFederationRestoreFailed exception=" + e.getMessage());
			throw e;
		}
	}


	// 4.26
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void federationRestoreBegun() throws FederateInternalError {
		this.logger.info("federationRestoreBegun");
		try {
			this.myFederateAmbassador.federationRestoreBegun();
		} catch (FederateInternalError e) {
			this.logger.error("federationRestoreBegun exception=" + e.getMessage());
			throw e;
		}
	}


	// 4.27
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void initiateFederateRestore(final String label, final String federateName, final FederateHandle federateHandle) throws FederateInternalError {
		this.logger.info("initiateFederateRestore label=" + label + ", federateName=" + federateName + ", federateHandle=" + federateHandle.toString());
		try {
			this.myFederateAmbassador.initiateFederateRestore(label, federateName, federateHandle);
		} catch (FederateInternalError e) {
			this.logger.error("initiateFederateRestore exception=" + e.getMessage());
			throw e;
		}
	}


	// 4.29
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void federationRestored() throws FederateInternalError {
		this.logger.info("federationRestored");
		try {
			this.myFederateAmbassador.federationRestored();
		} catch (FederateInternalError e) {
			this.logger.error("federationRestored exception=" + e.getMessage());
			throw e;
		}
	}


	// 4.29
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void federationNotRestored(final RestoreFailureReason reason) throws FederateInternalError {
		this.logger.info("federationNotRestored reason=" + reason.toString());
		try {
			this.myFederateAmbassador.federationNotRestored(reason);
		} catch (FederateInternalError e) {
			this.logger.error("federationNotRestored exception=" + e.getMessage());
			throw e;
		}
	}


	// 4.32
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void federationRestoreStatusResponse(final FederateRestoreStatus[] response) throws FederateInternalError {
		this.logger.info("federationRestoreStatusResponse response=" + response.toString());
		try {
			this.myFederateAmbassador.federationRestoreStatusResponse(response);
		} catch (FederateInternalError e) {
			this.logger.error("federationRestoreStatusResponse exception=" + e.getMessage());
			throw e;
		}
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
		this.logger.info("startRegistrationForObjectClass theClass=" + theClass.toString());
		try {
			this.myFederateAmbassador.startRegistrationForObjectClass(theClass);
		} catch (FederateInternalError e) {
			this.logger.error("startRegistrationForObjectClass exception=" + e.getMessage());
			throw e;
		}
	}


	// 5.11
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void stopRegistrationForObjectClass(final ObjectClassHandle theClass) throws FederateInternalError {
		this.logger.info("stopRegistrationForObjectClass theClass=" + theClass.toString());
		try {
			this.myFederateAmbassador.stopRegistrationForObjectClass(theClass);
		} catch (FederateInternalError e) {
			this.logger.error("stopRegistrationForObjectClass exception=" + e.getMessage());
			throw e;
		}
	}


	// 5.12
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void turnInteractionsOn(final InteractionClassHandle theHandle) throws FederateInternalError {
		this.logger.info("turnInteractionsOn theHandle=" + theHandle.toString());
		try {
			this.myFederateAmbassador.turnInteractionsOn(theHandle);
		} catch (FederateInternalError e) {
			this.logger.error("turnInteractionsOn exception=" + e.getMessage());
			throw e;
		}
	}


	// 5.13
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void turnInteractionsOff(final InteractionClassHandle theHandle) throws FederateInternalError {
		this.logger.info("turnInteractionsOff theHandle=" + theHandle);
		try {
			this.myFederateAmbassador.turnInteractionsOff(theHandle);
		} catch (FederateInternalError e) {
			this.logger.error("turnInteractionsOff exception=" + e.getMessage());
			throw e;
		}
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
		this.logger.info("objectInstanceNameReservationSucceeded objectName=" + objectName);
		try {
			this.myFederateAmbassador.objectInstanceNameReservationSucceeded(objectName);
		} catch (FederateInternalError e) {
			this.logger.error("objectInstanceNameReservationSucceeded exception=" + e.getMessage());
			throw e;
		}
	}


	// 6.3
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void objectInstanceNameReservationFailed(final String objectName) throws FederateInternalError {
		this.logger.info("objectInstanceNameReservationFailed objectName=" + objectName);
		try {
			this.myFederateAmbassador.objectInstanceNameReservationFailed(objectName);
		} catch (FederateInternalError e) {
			this.logger.error("objectInstanceNameReservationFailed exception=" + e.getMessage());
			throw e;
		}
	}


	// 6.6
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void multipleObjectInstanceNameReservationSucceeded(final Set<String> objectNames) throws FederateInternalError {
		this.logger.info("multipleObjectInstanceNameReservationSucceeded objectNames=" + objectNames.toString());
		try {
			this.myFederateAmbassador.multipleObjectInstanceNameReservationSucceeded(objectNames);
		} catch (FederateInternalError e) {
			this.logger.error("multipleObjectInstanceNameReservationSucceeded exception=" + e.getMessage());
			throw e;
		}
	}


	// 6.6
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void multipleObjectInstanceNameReservationFailed(final Set<String> objectNames) throws FederateInternalError {
		this.logger.info("multipleObjectInstanceNameReservationFailed objectNames=" + objectNames.toString());
		try {
			this.myFederateAmbassador.multipleObjectInstanceNameReservationFailed(objectNames);
		} catch (FederateInternalError e) {
			this.logger.error("multipleObjectInstanceNameReservationFailed exception=" + e.getMessage());
			throw e;
		}
	}


	// 6.9
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void discoverObjectInstance(final ObjectInstanceHandle theObject, final ObjectClassHandle theObjectClass, final String objectName) throws FederateInternalError {
		this.logger.info("discoverObjectInstance theObject=" + theObject.toString() + ", theObjectClass=" + theObjectClass.toString() + ", objectName" + objectName);
		try {
			this.myFederateAmbassador.discoverObjectInstance(theObject, theObjectClass, objectName);
		} catch (FederateInternalError e) {
			this.logger.error("discoverObjectInstance exception=" + e.getMessage());
			throw e;
		}
	}


	// 6.9
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void discoverObjectInstance(final ObjectInstanceHandle theObject, final ObjectClassHandle theObjectClass, final String objectName, final FederateHandle producingFederate) throws FederateInternalError {
		this.logger.info("discoverObjectInstance theObject=" + theObject.toString() + ", theObjectClass=" + theObjectClass.toString() + ", objectName=" + objectName + ", producingFederate" + producingFederate.toString());
		try {
			this.myFederateAmbassador.discoverObjectInstance(theObject, theObjectClass, objectName, producingFederate);
		} catch (FederateInternalError e) {
			this.logger.error("discoverObjectInstance exception=" + e.getMessage());
			throw e;
		}
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
		this.logger.info("reflectAttributeValues theObject=" + theObject.toString() + ", theAttributes=" + attributesStr.toString() + ", userSuppliedTag=" + Arrays.toString(userSuppliedTag) + ", sentOrdering=" + sentOrdering.toString() + ", theTransport=" + theTransport.toString() + ", reflectInfo=" + reflectInfo.toString());
		try {
			this.myFederateAmbassador.reflectAttributeValues(theObject, theAttributes, userSuppliedTag, sentOrdering, theTransport, reflectInfo);
		} catch (FederateInternalError e) {
			this.logger.error("reflectAttributeValues exception=" + e.getMessage());
			throw e;
		}
	}


	// 6.11
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void reflectAttributeValues(final ObjectInstanceHandle theObject, final AttributeHandleValueMap theAttributes, final byte[] userSuppliedTag, final OrderType sentOrdering, final TransportationTypeHandle theTransport, final LogicalTime theTime, final OrderType receivedOrdering, final SupplementalReflectInfo reflectInfo) throws FederateInternalError {
		final String attributesStr = this.printAttributeHandleValueMap(theAttributes);
		this.logger.info("reflectAttributeValues theObject=" + theObject.toString() + ", theAttributes=" + attributesStr.toString() + ", userSuppliedTag=" + Arrays.toString(userSuppliedTag) + ", sentOrdering=" + sentOrdering.toString() + ", theTransport=" + theTransport.toString() + ", theTime=" + theTime.toString() + ", receivedOrdering=" + receivedOrdering.toString() + ", reflectInfo=" + reflectInfo.toString());
		try {
			this.myFederateAmbassador.reflectAttributeValues(theObject, theAttributes, userSuppliedTag, sentOrdering, theTransport, theTime, receivedOrdering, reflectInfo);
		} catch (FederateInternalError e) {
			this.logger.error("reflectAttributeValues exception=" + e.getMessage());
			throw e;
		}
	}


	// 6.11
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void reflectAttributeValues(final ObjectInstanceHandle theObject, final AttributeHandleValueMap theAttributes, final byte[] userSuppliedTag, final OrderType sentOrdering, final TransportationTypeHandle theTransport, final LogicalTime theTime, final OrderType receivedOrdering, final MessageRetractionHandle retractionHandle, final SupplementalReflectInfo reflectInfo) throws FederateInternalError {
		final String attributesStr = this.printAttributeHandleValueMap(theAttributes);
		this.logger.info("reflectAttributeValues theObject=" + theObject.toString() + ", theAttributes=" + attributesStr.toString() + ", userSuppliedTag=" + Arrays.toString(userSuppliedTag) + ", sentOrdering=" + sentOrdering.toString() + ", theTransport=" + theTransport.toString() + ", theTime=" + theTime.toString() + ", receivedOrdering=" + receivedOrdering.toString() + ", retractionHandle=" + retractionHandle.toString() + ", reflectInfo=" + reflectInfo.toString());
		try {
			this.myFederateAmbassador.reflectAttributeValues(theObject, theAttributes, userSuppliedTag, sentOrdering, theTransport, theTime, receivedOrdering, retractionHandle, reflectInfo);
		} catch (FederateInternalError e) {
			this.logger.error("reflectAttributeValues exception=" + e.getMessage());
			throw e;
		}
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
		this.logger.info("receiveInteraction interactionClass=" + interactionClass.toString() + ", theParameters=" + parametersStr.toString() + ", userSuppliedTag=" + Arrays.toString(userSuppliedTag) + ", sentOrdering=" + sentOrdering.toString() + ", theTransport=" + theTransport.toString() + ", receiveInfo=" + receiveInfo.toString());
		try {
			this.myFederateAmbassador.receiveInteraction(interactionClass, theParameters, userSuppliedTag, sentOrdering, theTransport, receiveInfo);
		} catch (FederateInternalError e) {
			this.logger.error("receiveInteraction exception=" + e.getMessage());
			throw e;
		}
	}


	// 6.13
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void receiveInteraction(final InteractionClassHandle interactionClass, final ParameterHandleValueMap theParameters, final byte[] userSuppliedTag, final OrderType sentOrdering, final TransportationTypeHandle theTransport, final LogicalTime theTime, final OrderType receivedOrdering, final SupplementalReceiveInfo receiveInfo) throws FederateInternalError {
		final String parametersStr = this.printParameterHandleValueMap(theParameters);
		this.logger.info("receiveInteraction interactionClass=" + interactionClass.toString() + ", theParameters=" + parametersStr.toString() + ", userSuppliedTag=" + Arrays.toString(userSuppliedTag) + ", sentOrdering=" + sentOrdering.toString() + ", theTransport=" + theTransport.toString() + ", theTime=" + theTime.toString() + ", receivedOrdering=" + receivedOrdering.toString() + ", receiveInfo=" + receiveInfo.toString());
		try {
			this.myFederateAmbassador.receiveInteraction(interactionClass, theParameters, userSuppliedTag, sentOrdering, theTransport, theTime, receivedOrdering, receiveInfo);
		} catch (FederateInternalError e) {
			this.logger.error("receiveInteraction exception=" + e.getMessage());
			throw e;
		}
	}


	// 6.13
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void receiveInteraction(final InteractionClassHandle interactionClass, final ParameterHandleValueMap theParameters, final byte[] userSuppliedTag, final OrderType sentOrdering, final TransportationTypeHandle theTransport, final LogicalTime theTime, final OrderType receivedOrdering, final MessageRetractionHandle retractionHandle, final SupplementalReceiveInfo receiveInfo) throws FederateInternalError {
		final String parametersStr = this.printParameterHandleValueMap(theParameters);
		this.logger.info("receiveInteraction interactionClass=" + interactionClass.toString() + ", theParameters=" + parametersStr.toString() + ", userSuppliedTag=" + Arrays.toString(userSuppliedTag) + ", sentOrdering=" + sentOrdering.toString() + ", theTransport=" + theTransport.toString() + ", receivedOrdering=" + receivedOrdering.toString() + ", retractionHandle=" + retractionHandle.toString() + ", receiveInfo=" + receiveInfo.toString());
		try {
			this.myFederateAmbassador.receiveInteraction(interactionClass, theParameters, userSuppliedTag, sentOrdering, theTransport, theTime, receivedOrdering, retractionHandle, receiveInfo);
		} catch (FederateInternalError e) {
			this.logger.error("receiveInteraction exception=" + e.getMessage());
			throw e;
		}
	}


	// 6.15
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void removeObjectInstance(final ObjectInstanceHandle theObject, final byte[] userSuppliedTag, final OrderType sentOrdering, final SupplementalRemoveInfo removeInfo) throws FederateInternalError {
		this.logger.info("removeObjectInstance theObject=" + theObject.toString() + ", userSuppliedTag=" + Arrays.toString(userSuppliedTag) + ", sentOrdering=" + sentOrdering.toString() + ", removeInfo=" + removeInfo.toString());
		try {
			this.myFederateAmbassador.removeObjectInstance(theObject, userSuppliedTag, sentOrdering, removeInfo);
		} catch (FederateInternalError e) {
			this.logger.error("removeObjectInstance exception=" + e.getMessage());
			throw e;
		}
	}


	// 6.15
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void removeObjectInstance(final ObjectInstanceHandle theObject, final byte[] userSuppliedTag, final OrderType sentOrdering, final LogicalTime theTime, final OrderType receivedOrdering, final SupplementalRemoveInfo removeInfo) throws FederateInternalError {
		this.logger.info("removeObjectInstance " + theObject.toString() + " " + Arrays.toString(userSuppliedTag) + " " + sentOrdering.toString() + " " + theTime.toString() + " " + receivedOrdering.toString() + " " + removeInfo.toString());
		try {
			this.myFederateAmbassador.removeObjectInstance(theObject, userSuppliedTag, sentOrdering, theTime, receivedOrdering, removeInfo);
		} catch (FederateInternalError e) {
			this.logger.error("removeObjectInstance exception=" + e.getMessage());
			throw e;
		}
	}


	// 6.15
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void removeObjectInstance(final ObjectInstanceHandle theObject, final byte[] userSuppliedTag, final OrderType sentOrdering, final LogicalTime theTime, final OrderType receivedOrdering, final MessageRetractionHandle retractionHandle, final SupplementalRemoveInfo removeInfo) throws FederateInternalError {
		this.logger.info("removeObjectInstance theObject=" + theObject.toString() + ", userSuppliedTag=" + Arrays.toString(userSuppliedTag) + ", sentOrdering=" + sentOrdering.toString() + ", theTime=" + theTime.toString() + ", receivedOrdering=" + receivedOrdering.toString() + ", retractionHandle=" + retractionHandle.toString() + ", removeInfo=" + removeInfo.toString());
		try {
			this.myFederateAmbassador.removeObjectInstance(theObject, userSuppliedTag, sentOrdering, theTime, receivedOrdering, retractionHandle, removeInfo);
		} catch (FederateInternalError e) {
			this.logger.error("removeObjectInstance exception=" + e.getMessage());
			throw e;
		}
	}


	// 6.17
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void attributesInScope(final ObjectInstanceHandle theObject, final AttributeHandleSet theAttributes) throws FederateInternalError {
		this.logger.info("attributesInScope theObject=" + theObject.toString() + ", theAttributes=" + theAttributes.toString());
		try {
			this.myFederateAmbassador.attributesInScope(theObject, theAttributes);
		} catch (FederateInternalError e) {
			this.logger.error("attributesInScope exception=" + e.getMessage());
			throw e;
		}
	}


	// 6.18
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void attributesOutOfScope(final ObjectInstanceHandle theObject, final AttributeHandleSet theAttributes) throws FederateInternalError {
		this.logger.info("attributesOutOfScope theObject=" + theObject.toString() + ", theAttributes=" + theAttributes.toString());
		try {
			this.myFederateAmbassador.attributesOutOfScope(theObject, theAttributes);
		} catch (FederateInternalError e) {
			this.logger.error("attributesOutOfScope exception=" + e.getMessage());
			throw e;
		}
	}


	// 6.20
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void provideAttributeValueUpdate(final ObjectInstanceHandle theObject, final AttributeHandleSet theAttributes, final byte[] userSuppliedTag) throws FederateInternalError {
		this.logger.info("provideAttributeValueUpdate theObject=" + theObject.toString() + ", theAttributes=" + theAttributes.toString() + ", userSuppliedTag=" + Arrays.toString(userSuppliedTag));
		try {
			this.myFederateAmbassador.provideAttributeValueUpdate(theObject, theAttributes, userSuppliedTag);
		} catch (FederateInternalError e) {
			this.logger.error("provideAttributeValueUpdate exception=" + e.getMessage());
			throw e;
		}
	}


	// 6.21
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void turnUpdatesOnForObjectInstance(final ObjectInstanceHandle theObject, final AttributeHandleSet theAttributes) throws FederateInternalError {
		this.logger.info("turnUpdatesOnForObjectInstance theObject=" + theObject.toString() + ", theAttributes=" + theAttributes.toString());
		try {
			this.myFederateAmbassador.turnUpdatesOnForObjectInstance(theObject, theAttributes);
		} catch (FederateInternalError e) {
			this.logger.error("turnUpdatesOnForObjectInstance exception=" + e.getMessage());
			throw e;
		}
	}


	// 6.21
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void turnUpdatesOnForObjectInstance(final ObjectInstanceHandle theObject, final AttributeHandleSet theAttributes, final String updateRateDesignator) throws FederateInternalError {
		this.logger.info("turnUpdatesOnForObjectInstance theObject=" + theObject.toString() + ", theAttributes=" + theAttributes.toString() + ", updateRateDesignator=" + updateRateDesignator);
		try {
			this.myFederateAmbassador.turnUpdatesOnForObjectInstance(theObject, theAttributes, updateRateDesignator);
		} catch (FederateInternalError e) {
			this.logger.error("turnUpdatesOnForObjectInstance exception=" + e.getMessage());
			throw e;
		}
	}


	// 6.22
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void turnUpdatesOffForObjectInstance(final ObjectInstanceHandle theObject, final AttributeHandleSet theAttributes) throws FederateInternalError {
		this.logger.info("turnUpdatesOffForObjectInstance theObject=" + theObject.toString() + ", theAttributes=" + theAttributes.toString());
		try {
			this.myFederateAmbassador.turnUpdatesOffForObjectInstance(theObject, theAttributes);
		} catch (FederateInternalError e) {
			this.logger.error("turnUpdatesOffForObjectInstance exception=" + e.getMessage());
			throw e;
		}
	}


	// 6.24
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void confirmAttributeTransportationTypeChange(final ObjectInstanceHandle theObject, final AttributeHandleSet theAttributes, final TransportationTypeHandle theTransportation) throws FederateInternalError {
		this.logger.info("confirmAttributeTransportationTypeChange theObject=" + theObject.toString() + ", theAttributes=" + theAttributes.toString() + ", theTransportation=" + theTransportation.toString());
		try {
			this.myFederateAmbassador.confirmAttributeTransportationTypeChange(theObject, theAttributes, theTransportation);
		} catch (FederateInternalError e) {
			this.logger.error("confirmAttributeTransportationTypeChange exception=" + e.getMessage());
			throw e;
		}
	}


	// 6.26
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void reportAttributeTransportationType(final ObjectInstanceHandle theObject, final AttributeHandle theAttribute, final TransportationTypeHandle theTransportation) throws FederateInternalError {
		this.logger.info("reportAttributeTransportationType theObject=" + theObject.toString() + ", theAttribute=" + theAttribute.toString() + ", theTransportation=" + theTransportation.toString());
		try {
			this.myFederateAmbassador.reportAttributeTransportationType(theObject, theAttribute, theTransportation);
		} catch (FederateInternalError e) {
			this.logger.error("reportAttributeTransportationType exception=" + e.getMessage());
			throw e;
		}
	}


	// 6.28
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void confirmInteractionTransportationTypeChange(final InteractionClassHandle theInteraction, final TransportationTypeHandle theTransportation) throws FederateInternalError {
		this.logger.info("confirmInteractionTransportationTypeChange theInteraction=" + theInteraction.toString() + ", theTransportation=" + theTransportation.toString());
		try {
			this.myFederateAmbassador.confirmInteractionTransportationTypeChange(theInteraction, theTransportation);
		} catch (FederateInternalError e) {
			this.logger.error("confirmInteractionTransportationTypeChange exception=" + e.getMessage());
			throw e;
		}
	}


	// 6.30
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void reportInteractionTransportationType(final FederateHandle theFederate, final InteractionClassHandle theInteraction, final TransportationTypeHandle theTransportation) throws FederateInternalError {
		this.logger.info("reportInteractionTransportationType theFederate=" + theFederate.toString() + ", theInteraction=" + theInteraction.toString() + ", theTransportation=" + theTransportation.toString());
		try {
			this.myFederateAmbassador.reportInteractionTransportationType(theFederate, theInteraction, theTransportation);
		} catch (FederateInternalError e) {
			this.logger.error("reportInteractionTransportationType exception=" + e.getMessage());
			throw e;
		}
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
		this.logger.info("requestAttributeOwnershipAssumption theObject=" + theObject.toString() + ", offeredAttributes=" + offeredAttributes + ", userSuppliedTag=" + Arrays.toString(userSuppliedTag));
		try {
			this.myFederateAmbassador.requestAttributeOwnershipAssumption(theObject, offeredAttributes, userSuppliedTag);
		} catch (FederateInternalError e) {
			this.logger.error("requestAttributeOwnershipAssumption exception=" + e.getMessage());
			throw e;
		}
	}


	// 7.5
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void requestDivestitureConfirmation(final ObjectInstanceHandle theObject, final AttributeHandleSet offeredAttributes) throws FederateInternalError {
		this.logger.info("requestDivestitureConfirmation theObject=" + theObject.toString() + ", offeredAttributes=" + offeredAttributes.toString());
		try {
			this.myFederateAmbassador.requestDivestitureConfirmation(theObject, offeredAttributes);
		} catch (FederateInternalError e) {
			this.logger.error("requestDivestitureConfirmation exception=" + e.getMessage());
			throw e;
		}
	}


	// 7.7
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void attributeOwnershipAcquisitionNotification(final ObjectInstanceHandle theObject, final AttributeHandleSet securedAttributes, final byte[] userSuppliedTag) throws FederateInternalError {
		this.logger.info("attributeOwnershipAcquisitionNotification theObject=" + theObject.toString() + ", securedAttributes=" + securedAttributes.toString() + ", userSuppliedTag=" + Arrays.toString(userSuppliedTag));
		try {
			this.myFederateAmbassador.attributeOwnershipAcquisitionNotification(theObject, securedAttributes, userSuppliedTag);
		} catch (FederateInternalError e) {
			this.logger.error("attributeOwnershipAcquisitionNotification exception=" + e.getMessage());
			throw e;
		}
	}


	// 7.10
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void attributeOwnershipUnavailable(final ObjectInstanceHandle theObject, final AttributeHandleSet theAttributes) throws FederateInternalError {
		this.logger.info("attributeOwnershipUnavailable theObject=" + theObject.toString() + ", theAttributes=" + theAttributes.toString());
		try {
			this.myFederateAmbassador.attributeOwnershipUnavailable(theObject, theAttributes);
		} catch (FederateInternalError e) {
			this.logger.error("attributeOwnershipUnavailable exception=" + e.getMessage());
			throw e;
		}
	}


	// 7.11
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void requestAttributeOwnershipRelease(final ObjectInstanceHandle theObject, final AttributeHandleSet candidateAttributes, final byte[] userSuppliedTag) throws FederateInternalError {
		this.logger.info("requestAttributeOwnershipRelease theObject=" + theObject.toString() + ", candidateAttributes=" + candidateAttributes.toString() + ", userSuppliedTag=" + Arrays.toString(userSuppliedTag));
		try {
			this.myFederateAmbassador.requestAttributeOwnershipRelease(theObject, candidateAttributes, userSuppliedTag);
		} catch (FederateInternalError e) {
			this.logger.error("requestAttributeOwnershipRelease exception=" + e.getMessage());
			throw e;
		}
	}


	// 7.16
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void confirmAttributeOwnershipAcquisitionCancellation(final ObjectInstanceHandle theObject, final AttributeHandleSet theAttributes) throws FederateInternalError {
		this.logger.info("confirmAttributeOwnershipAcquisitionCancellation theObject=" + theObject.toString() + ", theAttributes=" + theAttributes.toString());
		try {
			this.myFederateAmbassador.confirmAttributeOwnershipAcquisitionCancellation(theObject, theAttributes);
		} catch (FederateInternalError e) {
			this.logger.error("confirmAttributeOwnershipAcquisitionCancellation exception=" + e.getMessage());
			throw e;
		}
	}


	// 7.18
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void informAttributeOwnership(final ObjectInstanceHandle theObject, final AttributeHandle theAttribute, final FederateHandle theOwner) throws FederateInternalError {
		this.logger.info("informAttributeOwnership theObject=" + theObject.toString() + ", theAttribute=" + theAttribute.toString() + ", theOwner=" + theOwner.toString());
		try {
			this.myFederateAmbassador.informAttributeOwnership(theObject, theAttribute, theOwner);
		} catch (FederateInternalError e) {
			this.logger.error("informAttributeOwnership exception=" + e.getMessage());
			throw e;
		}
	}


	// 7.18
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void attributeIsNotOwned(final ObjectInstanceHandle theObject, final AttributeHandle theAttribute) throws FederateInternalError {
		this.logger.info("attributeIsNotOwned theObject=" + theObject.toString() + ", theAttribute=" + theAttribute.toString());
		try {
			this.myFederateAmbassador.attributeIsNotOwned(theObject, theAttribute);
		} catch (FederateInternalError e) {
			this.logger.error("attributeIsNotOwned exception=" + e.getMessage());
			throw e;
		}
	}


	// 7.18
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void attributeIsOwnedByRTI(final ObjectInstanceHandle theObject, final AttributeHandle theAttribute) throws FederateInternalError {
		this.logger.info("attributeIsOwnedByRTI theObject=" + theObject.toString() + ", theAttribute=" + theAttribute.toString());
		try {
			this.myFederateAmbassador.attributeIsOwnedByRTI(theObject, theAttribute);
		} catch (FederateInternalError e) {
			this.logger.error("attributeIsOwnedByRTI exception=" + e.getMessage());
			throw e;
		}
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
		this.logger.info("timeRegulationEnabled time=" + time.toString());
		try {
			this.myFederateAmbassador.timeRegulationEnabled(time);
		} catch (FederateInternalError e) {
			this.logger.error("timeRegulationEnabled exception=" + e.getMessage());
			throw e;
		}
	}


	// 8.6
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void timeConstrainedEnabled(final LogicalTime time) throws FederateInternalError {
		this.logger.info("timeConstrainedEnabled time=" + time.toString());
		try {
			this.myFederateAmbassador.timeConstrainedEnabled(time);
		} catch (FederateInternalError e) {
			this.logger.error("timeConstrainedEnabled exception=" + e.getMessage());
			throw e;
		}
	}


	// 8.13
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void timeAdvanceGrant(final LogicalTime theTime) throws FederateInternalError {
		this.logger.info("timeAdvanceGrant theTime=" + theTime.toString());
		try {
			this.myFederateAmbassador.timeAdvanceGrant(theTime);
		} catch (FederateInternalError e) {
			this.logger.error("timeAdvanceGrant exception=" + e.getMessage());
			throw e;
		}
	}


	// 8.22
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void requestRetraction(final MessageRetractionHandle theHandle) throws FederateInternalError {
		this.logger.info("requestRetraction theHandle=" + theHandle.toString());
		try {
			this.myFederateAmbassador.requestRetraction(theHandle);
		} catch (FederateInternalError e) {
			this.logger.error("requestRetraction requestRetraction=" + e.getMessage());
			throw e;
		}
	}
}
