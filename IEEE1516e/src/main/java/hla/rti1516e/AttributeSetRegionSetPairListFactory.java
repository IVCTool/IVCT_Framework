/*
 * The IEEE hereby grants a general, royalty-free license to copy, distribute,
 * display and make derivative works from this material, for all purposes,
 * provided that any use of the material contains the following
 * attribution: "Reprinted with permission from IEEE 1516.1(TM)-2010".
 * Should you require additional information, contact the Manager, Standards
 * Intellectual Property, IEEE Standards Association (stds-ipr@ieee.org).
 */

//File: AttributeSetRegionSetPairListFactory.java
package hla.rti1516e;

import java.io.Serializable;

/**
 * Factory for AttributeHandleValuePairSet instances.
 */
public interface AttributeSetRegionSetPairListFactory extends Serializable {

   /**
    * Creates a new AttributeHandleValuePairSet instance with specified initial capacity.
    */
   AttributeSetRegionSetPairList create(int capacity);
}

//end AttributeSetRegionSetPairListFactory

