/*
 * This class was automatically generated with 
 * <a href="http://www.castor.org">Castor 1.3.0.1</a>, using an XML
 * Schema.
 * $Id$
 */

package chouette.schema.types;

/**
 * Values for Access Facility
 * 
 * @version $Revision$ $Date$
 */
@SuppressWarnings("serial")
public enum AccessFacilityEnumeration implements java.io.Serializable {


      //------------------/
     //- Enum Constants -/
    //------------------/

    /**
     * Constant UNKNOWN
     */
    UNKNOWN("unknown"),
    /**
     * Constant LIFT
     */
    LIFT("lift"),
    /**
     * Constant ESCALATOR
     */
    ESCALATOR("escalator"),
    /**
     * Constant TRAVELATOR
     */
    TRAVELATOR("travelator"),
    /**
     * Constant RAMP
     */
    RAMP("ramp"),
    /**
     * Constant STAIRS
     */
    STAIRS("stairs"),
    /**
     * Constant SHUTTLE
     */
    SHUTTLE("shuttle"),
    /**
     * Constant NARROWENTRANCE
     */
    NARROWENTRANCE("narrowEntrance"),
    /**
     * Constant BARRIER
     */
    BARRIER("barrier"),
    /**
     * Constant PALLETACCESS_LOWFLOOR
     */
    PALLETACCESS_LOWFLOOR("palletAccess_lowFloor"),
    /**
     * Constant VALIDATOR
     */
    VALIDATOR("validator");

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

    private AccessFacilityEnumeration(final java.lang.String value) {
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
    public static chouette.schema.types.AccessFacilityEnumeration fromValue(
            final java.lang.String value) {
        for (AccessFacilityEnumeration c: AccessFacilityEnumeration.values()) {
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
