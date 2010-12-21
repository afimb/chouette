/*
 * This class was automatically generated with 
 * <a href="http://www.castor.org">Castor 1.3.0.1</a>, using an XML
 * Schema.
 * $Id$
 */

package chouette.schema.types;

/**
 * Values for Hire Facility
 * 
 * @version $Revision$ $Date$
 */
@SuppressWarnings("serial")
public enum HireFacilityEnumeration implements java.io.Serializable {


      //------------------/
     //- Enum Constants -/
    //------------------/

    /**
     * Constant UNKNOWN
     */
    UNKNOWN("unknown"),
    /**
     * Constant CARHIRE
     */
    CARHIRE("carHire"),
    /**
     * Constant MOTORCYCLEHIRE
     */
    MOTORCYCLEHIRE("motorCycleHire"),
    /**
     * Constant CYCLEHIRE
     */
    CYCLEHIRE("cycleHire"),
    /**
     * Constant TAXI
     */
    TAXI("taxi"),
    /**
     * Constant RECREATIONDEVICEHIRE
     */
    RECREATIONDEVICEHIRE("recreationDeviceHire");

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

    private HireFacilityEnumeration(final java.lang.String value) {
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
    public static chouette.schema.types.HireFacilityEnumeration fromValue(
            final java.lang.String value) {
        for (HireFacilityEnumeration c: HireFacilityEnumeration.values()) {
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
