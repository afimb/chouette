/*
 * This class was automatically generated with 
 * <a href="http://www.castor.org">Castor 1.3.0.1</a>, using an XML
 * Schema.
 * $Id$
 */

package chouette.schema.types;

/**
 * Enumeration of specific Medical needs
 * 
 * @version $Revision$ $Date$
 */
@SuppressWarnings("serial")
public enum MedicalNeedEnumeration implements java.io.Serializable {


      //------------------/
     //- Enum Constants -/
    //------------------/

    /**
     * Constant ALLERGIC
     */
    ALLERGIC("allergic"),
    /**
     * Constant HEARTCONDITION
     */
    HEARTCONDITION("heartCondition"),
    /**
     * Constant OTHERMEDICALNEED
     */
    OTHERMEDICALNEED("otherMedicalNeed");

      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    /**
     * Field value.
     */
    private final java.lang.String value;


      //----------------/
     //- Constructors -/
    //----------------/

    private MedicalNeedEnumeration(final java.lang.String value) {
        this.value = value;
    }


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * Method fromValue.
     * 
     * @param value
     * @return the constant for this value
     */
    public static chouette.schema.types.MedicalNeedEnumeration fromValue(
            final java.lang.String value) {
        for (MedicalNeedEnumeration c: MedicalNeedEnumeration.values()) {
            if (c.value.equals(value)) {
                return c;
            }
        }
        throw new IllegalArgumentException(value);
    }

    /**
     * 
     * 
     * @param value
     */
    public void setValue(
            final java.lang.String value) {
    }

    /**
     * Method toString.
     * 
     * @return the value of this constant
     */
    public java.lang.String toString(
    ) {
        return this.value;
    }

    /**
     * Method value.
     * 
     * @return the value of this constant
     */
    public java.lang.String value(
    ) {
        return this.value;
    }

}
