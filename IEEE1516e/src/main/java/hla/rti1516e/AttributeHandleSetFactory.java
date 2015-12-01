/*
 * The IEEE hereby grants a general, royalty-free license to copy, distribute,
 * display and make derivative works from this material, for all purposes,
 * provided that any use of the material contains the following
 * attribution: "Reprinted with permission from IEEE 1516.1(TM)-2010".
 * Should you require additional information, contact the Manager, Standards
 * Intellectual Property, IEEE Standards Association (stds-ipr@ieee.org).
 */

//File: AttributeHandleSetFactory.java
package hla.rti1516e;

import java.io.Serializable;

public interface AttributeHandleSetFactory extends Serializable {

   /**
    * return hla.rti1516.AttributeHandleSet newly created
    */
   AttributeHandleSet create();
}

//end AttributeHandleSetFactory



