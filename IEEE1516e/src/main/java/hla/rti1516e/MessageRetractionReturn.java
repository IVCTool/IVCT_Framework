/*
 * The IEEE hereby grants a general, royalty-free license to copy, distribute,
 * display and make derivative works from this material, for all purposes,
 * provided that any use of the material contains the following
 * attribution: "Reprinted with permission from IEEE 1516.1(TM)-2010".
 * Should you require additional information, contact the Manager, Standards
 * Intellectual Property, IEEE Standards Association (stds-ipr@ieee.org).
 */

//File: MessageRetractionReturn.java

/**
 * Record returned by updateAttributeValues, sendInteraction, and deleteObject
 */

package hla.rti1516e;

import java.io.Serializable;

public final class MessageRetractionReturn
   implements Serializable {
   public MessageRetractionReturn(boolean rhiv, MessageRetractionHandle mrh)
   {
      retractionHandleIsValid = rhiv;
      handle = mrh;
   }

   public final boolean retractionHandleIsValid;
   public final MessageRetractionHandle handle;
}

//end MessageRetractionReturn
