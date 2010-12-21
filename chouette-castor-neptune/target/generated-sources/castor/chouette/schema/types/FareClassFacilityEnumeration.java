/*
 * This class was automatically generated with 
 * <a href="http://www.castor.org">Castor 1.3.0.1</a>, using an XML
 * Schema.
 * $Id$
 */

package chouette.schema.types;

/**
 * Values for FareClass Facility: TPEG pti_table 23.
 * 
 * @version $Revision$ $Date$
 */
@SuppressWarnings("serial")
public enum FareClassFacilityEnumeration implements java.io.Serializable {


      //------------------/
     //- Enum Constants -/
    //------------------/

    /**
     * Constant UNKNOWN
     */
    UNKNOWN("unknown"),
    /**
     * Constant PTI23_0
     */
    PTI23_0("pti23_0"),
    /**
     * Constant UNKNOWN_2
     */
    UNKNOWN_2("unknown_2"),
    /**
     * Constant PTI23_6
     */
    PTI23_6("pti23_6"),
    /**
     * Constant FIRSTCLASS
     */
    FIRSTCLASS("firstClass"),
    /**
     * Constant PTI23_7
     */
    PTI23_7("pti23_7"),
    /**
     * Constant SECONDCLASS
     */
    SECONDCLASS("secondClass"),
    /**
     * Constant PTI23_8
     */
    PTI23_8("pti23_8"),
    /**
     * Constant THIRDCLASS
     */
    THIRDCLASS("thirdClass"),
    /**
     * Constant PTI23_9
     */
    PTI23_9("pti23_9"),
    /**
     * Constant ECONOMYCLASS
     */
    ECONOMYCLASS("economyClass"),
    /**
     * Constant PTI23_10
     */
    PTI23_10("pti23_10"),
    /**
     * Constant BUSINESSCLASS
     */
    BUSINESSCLASS("businessClass");

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

    private FareClassFacilityEnumeration(final java.lang.String value) {
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
    public static chouette.schema.types.FareClassFacilityEnumeration fromValue(
            final java.lang.String value) {
        for (FareClassFacilityEnumeration c: FareClassFacilityEnumeration.values()) {
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
