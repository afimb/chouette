/*
 * This class was automatically generated with 
 * <a href="http://www.castor.org">Castor 1.3.0.1</a>, using an XML
 * Schema.
 * $Id$
 */

package chouette.schema.types;

/**
 * Values for Luggage Facility: TPEG pti_table 23.
 * 
 * @version $Revision$ $Date$
 */
@SuppressWarnings("serial")
public enum LuggageFacilityEnumeration implements java.io.Serializable {


      //------------------/
     //- Enum Constants -/
    //------------------/

    /**
     * Constant UNKNOWN
     */
    UNKNOWN("unknown"),
    /**
     * Constant PTI23_17
     */
    PTI23_17("pti23_17"),
    /**
     * Constant BIKECARRIAGE
     */
    BIKECARRIAGE("bikeCarriage"),
    /**
     * Constant BAGGAGESTORAGE
     */
    BAGGAGESTORAGE("baggageStorage"),
    /**
     * Constant LEFTLUGGAGE
     */
    LEFTLUGGAGE("leftLuggage"),
    /**
     * Constant PORTERAGE
     */
    PORTERAGE("porterage"),
    /**
     * Constant BAGGAGETROLLEYS
     */
    BAGGAGETROLLEYS("baggageTrolleys");

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

    private LuggageFacilityEnumeration(final java.lang.String value) {
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
    public static chouette.schema.types.LuggageFacilityEnumeration fromValue(
            final java.lang.String value) {
        for (LuggageFacilityEnumeration c: LuggageFacilityEnumeration.values()) {
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
