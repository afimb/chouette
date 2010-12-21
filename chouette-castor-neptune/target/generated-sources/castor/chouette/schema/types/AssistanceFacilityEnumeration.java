/*
 * This class was automatically generated with 
 * <a href="http://www.castor.org">Castor 1.3.0.1</a>, using an XML
 * Schema.
 * $Id$
 */

package chouette.schema.types;

/**
 * Values for Assistance Facility
 * 
 * @version $Revision$ $Date$
 */
@SuppressWarnings("serial")
public enum AssistanceFacilityEnumeration implements java.io.Serializable {


      //------------------/
     //- Enum Constants -/
    //------------------/

    /**
     * Constant UNKNOWN
     */
    UNKNOWN("unknown"),
    /**
     * Constant POLICE
     */
    POLICE("police"),
    /**
     * Constant FIRSTAID
     */
    FIRSTAID("firstAid"),
    /**
     * Constant SOSPOINT
     */
    SOSPOINT("sosPoint"),
    /**
     * Constant SPECIFICASSISTANCE
     */
    SPECIFICASSISTANCE("specificAssistance"),
    /**
     * Constant UNACCOMPANIEDMINORASSISTANCE
     */
    UNACCOMPANIEDMINORASSISTANCE("unaccompaniedMinorAssistance"),
    /**
     * Constant BOARDINGASSISTANCE
     */
    BOARDINGASSISTANCE("boardingAssistance");

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

    private AssistanceFacilityEnumeration(final java.lang.String value) {
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
    public static chouette.schema.types.AssistanceFacilityEnumeration fromValue(
            final java.lang.String value) {
        for (AssistanceFacilityEnumeration c: AssistanceFacilityEnumeration.values()) {
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
