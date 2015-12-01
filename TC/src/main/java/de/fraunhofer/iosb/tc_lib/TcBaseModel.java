package de.fraunhofer.iosb.tc_lib;

import hla.rti1516e.AttributeHandle;
import hla.rti1516e.AttributeHandleSet;
import hla.rti1516e.AttributeHandleValueMap;
import hla.rti1516e.CallbackModel;
import hla.rti1516e.FederateAmbassador;
import hla.rti1516e.FederateHandle;
import hla.rti1516e.InteractionClassHandle;
import hla.rti1516e.ObjectClassHandle;
import hla.rti1516e.ObjectInstanceHandle;
import hla.rti1516e.OrderType;
import hla.rti1516e.ParameterHandle;
import hla.rti1516e.ParameterHandleValueMap;
import hla.rti1516e.TransportationTypeHandle;
import hla.rti1516e.encoding.ByteWrapper;
import hla.rti1516e.encoding.DecoderException;
import hla.rti1516e.encoding.EncoderFactory;
import hla.rti1516e.encoding.HLAunicodeString;
import hla.rti1516e.exceptions.AlreadyConnected;
import hla.rti1516e.exceptions.CallNotAllowedFromWithinCallback;
import hla.rti1516e.exceptions.ConnectionFailed;
import hla.rti1516e.exceptions.InvalidLocalSettingsDesignator;
import hla.rti1516e.exceptions.RTIexception;
import hla.rti1516e.exceptions.RTIinternalError;
import hla.rti1516e.exceptions.UnsupportedCallbackModel;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.Vector;
import org.slf4j.Logger;


/**
 * @author Johannes Mulder (Fraunhofer IOSB)
 */
public class TcBaseModel implements IVCT_BaseModel {
    protected Logger                                                   logger;
    private AttributeHandle                                            _attributeIdName;
    private EncoderFactory                                             _encoderFactory;
    private InteractionClassHandle                                     _messageId;
    private ObjectInstanceHandle                                       _userId;
    private ParameterHandle                                            _parameterIdSender;
    private ParameterHandle                                            _parameterIdText;
    private IVCT_RTIambassador                                         ivct_rti;
    private String                                                     _username;
    private final Map<ObjectInstanceHandle, ObjectClassHandle>         discoveredObjects        = new HashMap<ObjectInstanceHandle, ObjectClassHandle>();
    private final Map<ObjectInstanceHandle, UUID>                      objectUUIDmap            = new HashMap<ObjectInstanceHandle, UUID>();
    private final Map<ObjectClassHandle, Map<String, AttributeHandle>> objectAttributesmap      = new HashMap<ObjectClassHandle, Map<String, AttributeHandle>>();
    private final Map<ObjectClassHandle, AttributeHandleSet>           objectClassAttributesMap = new HashMap<ObjectClassHandle, AttributeHandleSet>();


    /**
     * @param logger reference to the logger
     * @param ivct_rti reference to RTIAmbassador
     */
    public TcBaseModel(final Logger logger, final IVCT_RTIambassador ivct_rti) {
        this.ivct_rti = ivct_rti;
        this.logger = logger;
    }


    /** {@inheritDoc} */
    @Override
    public FederateHandle initiateRti(final String federateName, final FederateAmbassador federateReference, final IVCT_TcParam tcParam) {
        return this.ivct_rti.initiateRti(tcParam, federateReference, federateName);
    }


    /**
     * @param federateReference
     * @param callbackModel
     * @param localSettingsDesignator
     */
    @Override
    public void connect(final FederateAmbassador federateReference, final CallbackModel callbackModel, final String localSettingsDesignator) {
        try {
            this.ivct_rti.connect(federateReference, callbackModel, localSettingsDesignator);
        }
        catch (ConnectionFailed | InvalidLocalSettingsDesignator | UnsupportedCallbackModel | AlreadyConnected | CallNotAllowedFromWithinCallback | RTIinternalError ex) {

            ex.printStackTrace();
        }
    }


    /** {@inheritDoc} */
    @Override
    public void terminateRti(final IVCT_TcParam tcParam) {
        this.ivct_rti.terminateRti(tcParam);
    }


    /**
     * -------------------------------------------------------------------------
     * -- Add an object class with its attributes to the map.
     * -------------------------------------------------------------------------
     * --
     *
     * @param theObjectClassHandle
     * @param theAttributeHandleSet
     */
    public void addObjectClassAttributes(final ObjectClassHandle theObjectClassHandle, final AttributeHandleSet theAttributeHandleSet) {
        AttributeHandleSet attributeHandleSet;

        // Clean up any old value.
        attributeHandleSet = this.objectClassAttributesMap.get(theObjectClassHandle);
        if (attributeHandleSet != null) {
            this.objectClassAttributesMap.remove(theObjectClassHandle);
        }

        // Add the item.
        this.objectClassAttributesMap.put(theObjectClassHandle, theAttributeHandleSet);
    }


    /**
     * @param objectClassHandle
     * @param attributeName
     * @param attributeHandle
     */
    public void addObjectClassNameAttribute(final ObjectClassHandle objectClassHandle, final String attributeName, final AttributeHandle attributeHandle) {
        Map<String, AttributeHandle> attributeNameHandleMap;
        attributeNameHandleMap = this.objectAttributesmap.get(objectClassHandle);
        if (attributeNameHandleMap == null) {
            attributeNameHandleMap = new HashMap<String, AttributeHandle>();
            attributeNameHandleMap.put(attributeName, attributeHandle);
            this.objectAttributesmap.put(objectClassHandle, attributeNameHandleMap);
            return;
        }
        final AttributeHandle handle = attributeNameHandleMap.get(attributeName);
        if (handle == null) {
            attributeNameHandleMap.put(attributeName, attributeHandle);
        }
    }


    /**
     * @param ivct_rti
     * @param encoderFactory
     */
    public void addRti(final IVCT_RTIambassador ivct_rti, final EncoderFactory encoderFactory) {
        this.ivct_rti = ivct_rti;
        this._encoderFactory = encoderFactory;
    }


    /**
     * @param theObject
     * @param theObjectClass
     * @param objectName
     */
    public void discoverObjectInstance(final ObjectInstanceHandle theObject, final ObjectClassHandle theObjectClass, final String objectName) {
        if (!this.objectUUIDmap.containsKey(theObject)) {
            this.discoveredObjects.put(theObject, theObjectClass);
        }
    }


    /**
     * @param theObjectClassHandle
     * @return AttributeHandleSet
     */
    public AttributeHandleSet getObjectClassAttributes(final ObjectClassHandle theObjectClassHandle) {
        AttributeHandleSet attributeHandleSet;

        attributeHandleSet = this.objectClassAttributesMap.get(theObjectClassHandle);
        return attributeHandleSet;
    }


    /**
     * @param objectUUIDs
     * @return Vector&lt;ObjectInstanceHandle&gt;
     */
    public Vector<ObjectInstanceHandle> getObjectInstances(final Vector<UUID> objectUUIDs) {
        final Vector<ObjectInstanceHandle> ret = new Vector<ObjectInstanceHandle>();

        for (final Map.Entry<ObjectInstanceHandle, UUID> entry: this.objectUUIDmap.entrySet()) {
            final UUID uid = entry.getValue();
            for (final UUID u: objectUUIDs) {
                if (u.equals(uid)) {
                    ret.add(entry.getKey());
                }
            }
        }
        return ret;
    }


    /**
     * @param objectUUIDs
     */
    public void printObjectInstances(final Vector<UUID> objectUUIDs) {
        final Vector<ObjectInstanceHandle> ret = new Vector<ObjectInstanceHandle>();
        boolean gotObjectId;

        for (final UUID u: objectUUIDs) {
            gotObjectId = false;
            for (final Map.Entry<ObjectInstanceHandle, UUID> entry: this.objectUUIDmap.entrySet()) {
                final UUID uid = entry.getValue();
                if (u.equals(uid)) {
                    gotObjectId = true;
                    this.logger.info("Instance = " + uid + " (" + entry.getKey() + ")");
                    ret.add(entry.getKey());
                }
            }
            if (gotObjectId == false) {
                this.logger.info("Instance = " + u);
            }
        }
    }


    /**
     * @param objectUUID
     */
    public void printUuid(final UUID objectUUID) {
        boolean gotObjectId = false;

        for (final Map.Entry<ObjectInstanceHandle, UUID> entry: this.objectUUIDmap.entrySet()) {
            final UUID uid = entry.getValue();
            if (objectUUID.equals(uid)) {
                gotObjectId = true;
                this.logger.info("Instance = " + uid + " (" + entry.getKey() + ")");
            }
        }

        if (gotObjectId == false) {
            this.logger.info("Instance = " + objectUUID);
        }
    }


    /**
     * @param theObject
     * @param theAttributes
     * @param userSuppliedTag
     */
    public void provideAttributeValueUpdate(final ObjectInstanceHandle theObject, final AttributeHandleSet theAttributes, final byte[] userSuppliedTag) {
        if (theObject.equals(this._userId) && theAttributes.contains(this._attributeIdName)) {
            try {
                final AttributeHandleValueMap attributeValues = this.ivct_rti.getAttributeHandleValueMapFactory().create(1);
                final HLAunicodeString nameEncoder = this._encoderFactory.createHLAunicodeString(this._username);
                attributeValues.put(this._attributeIdName, nameEncoder.toByteArray());
                this.ivct_rti.updateAttributeValues(this._userId, attributeValues, null);
            }
            catch (final RTIexception ignored) {
                // do nothing
            }
        }
    }


    /**
     * @param interactionClass
     * @param theParameters
     * @param userSuppliedTag
     * @param sentOrdering
     * @param theTransport
     * @param receiveInfo
     */
    public void receiveInteraction(final InteractionClassHandle interactionClass, final ParameterHandleValueMap theParameters, final byte[] userSuppliedTag, final OrderType sentOrdering, final TransportationTypeHandle theTransport, final FederateAmbassador.SupplementalReceiveInfo receiveInfo) {
        if (interactionClass.equals(this._messageId)) {
            if (!theParameters.containsKey(this._parameterIdText)) {
                this.logger.error("Bad message received: No text.");
                return;
            }
            if (!theParameters.containsKey(this._parameterIdSender)) {
                this.logger.error("Bad message received: No sender.");
                return;
            }
            try {
                final HLAunicodeString messageDecoder = this._encoderFactory.createHLAunicodeString();
                final HLAunicodeString senderDecoder = this._encoderFactory.createHLAunicodeString();
                messageDecoder.decode(theParameters.get(this._parameterIdText));
                senderDecoder.decode(theParameters.get(this._parameterIdSender));
                final String message = messageDecoder.getValue();
                final String sender = senderDecoder.getValue();

                this.logger.info(sender + ": " + message + "> ");
            }
            catch (final DecoderException e) {
                this.logger.error("Failed to decode incoming interaction");
            }
        }
    }


    /**
     * @param theObject
     * @param theAttributes
     * @param userSuppliedTag
     * @param sentOrdering
     * @param theTransport
     * @param reflectInfo
     */
    public void reflectAttributeValues(final ObjectInstanceHandle theObject, final AttributeHandleValueMap theAttributes, final byte[] userSuppliedTag, final OrderType sentOrdering, final TransportationTypeHandle theTransport, final FederateAmbassador.SupplementalReflectInfo reflectInfo) {
        final ObjectClassHandle och = this.discoveredObjects.get(theObject);
        final Map<String, AttributeHandle> nameAtt = this.objectAttributesmap.get(och);
        final AttributeHandle uniqueID = nameAtt.get("UniqueID");

        for (final AttributeHandleValueMap.Entry<AttributeHandle, byte[]> entry: theAttributes.entrySet()) {
            final AttributeHandle ah = entry.getKey();
            if (ah == uniqueID) {
                final ByteWrapper value = theAttributes.getValueReference(entry.getKey());
                final int len = value.remaining();
                if (value.remaining() < 16) {
                    this.logger.error("UNIQUE ID VALUE TOO SHORT " + len);
                    continue;
                }
                final byte[] dest = new byte[16];
                value.get(dest);
                final UUID uid = UUID.nameUUIDFromBytes(dest);
                this.objectUUIDmap.put(theObject, uid);
                //				logger.info(theObject.toString() + " : " + uid);
                break;
            }
        }
    }


    /**
     * @param theObject
     * @param userSuppliedTag
     * @param sentOrdering
     * @param removeInfo
     */
    public void removeObjectInstance(final ObjectInstanceHandle theObject, final byte[] userSuppliedTag, final OrderType sentOrdering, final FederateAmbassador.SupplementalRemoveInfo removeInfo) {
        final UUID member = this.objectUUIDmap.remove(theObject);
        if (member != null) {
            this.logger.info("[" + member + " has left]");
        }
        final ObjectClassHandle och = this.discoveredObjects.remove(theObject);
        if (och != null) {
            this.logger.info(theObject + " has left]");
        }
    }
}
