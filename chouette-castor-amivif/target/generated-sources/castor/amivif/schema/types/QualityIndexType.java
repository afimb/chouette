/*
 * This class was automatically generated with 
 * <a href="http://www.castor.org">Castor 1.3.0.1</a>, using an XML
 * Schema.
 * $Id$
 */

package amivif.schema.types;

/**
 * Quality of a status/situation indications
 *  
 * 
 * @version $Revision$ $Date$
 */
@SuppressWarnings("serial")
public enum QualityIndexType implements java.io.Serializable {


      //------------------/
     //- Enum Constants -/
    //------------------/

    /**
     * Constant CERTAIN
     */
    CERTAIN("Certain"),
    /**
     * Constant VERYRELIABLE
     */
    VERYRELIABLE("VeryReliable"),
    /**
     * Constant RELIABLE
     */
    RELIABLE("Reliable"),
    /**
     * Constant PROBABLYRELIABLE
     */
    PROBABLYRELIABLE("ProbablyReliable"),
    /**
     * Constant UNCONFIRMED
     */
    UNCONFIRMED("Unconfirmed");

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

    private QualityIndexType(final java.lang.String value) {
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
    public static amivif.schema.types.QualityIndexType fromValue(
            final java.lang.String value) {
        for (QualityIndexType c: QualityIndexType.values()) {
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
