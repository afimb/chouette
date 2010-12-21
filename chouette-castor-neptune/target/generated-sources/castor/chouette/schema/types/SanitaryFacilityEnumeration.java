/*
 * This class was automatically generated with 
 * <a href="http://www.castor.org">Castor 1.3.0.1</a>, using an XML
 * Schema.
 * $Id$
 */

package chouette.schema.types;

/**
 * Values for Sanitary Facility: TPEG pti_table 23.
 * 
 * @version $Revision$ $Date$
 */
@SuppressWarnings("serial")
public enum SanitaryFacilityEnumeration implements java.io.Serializable {


      //------------------/
     //- Enum Constants -/
    //------------------/

    /**
     * Constant UNKNOWN
     */
    UNKNOWN("unknown"),
    /**
     * Constant PTI23_22
     */
    PTI23_22("pti23_22"),
    /**
     * Constant TOILET
     */
    TOILET("toilet"),
    /**
     * Constant PTI23_23
     */
    PTI23_23("pti23_23"),
    /**
     * Constant NOTOILET
     */
    NOTOILET("noToilet"),
    /**
     * Constant SHOWER
     */
    SHOWER("shower"),
    /**
     * Constant WHEELCHAIRACCCESSTOILET
     */
    WHEELCHAIRACCCESSTOILET("wheelchairAcccessToilet"),
    /**
     * Constant BABYCHANGE
     */
    BABYCHANGE("babyChange");

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

    private SanitaryFacilityEnumeration(final java.lang.String value) {
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
    public static chouette.schema.types.SanitaryFacilityEnumeration fromValue(
            final java.lang.String value) {
        for (SanitaryFacilityEnumeration c: SanitaryFacilityEnumeration.values()) {
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
