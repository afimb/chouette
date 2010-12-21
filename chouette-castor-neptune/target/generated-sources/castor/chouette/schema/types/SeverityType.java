/*
 * This class was automatically generated with 
 * <a href="http://www.castor.org">Castor 1.3.0.1</a>, using an XML
 * Schema.
 * $Id$
 */

package chouette.schema.types;

/**
 * Severity of a status/situation
 *  
 * 
 * @version $Revision$ $Date$
 */
@SuppressWarnings("serial")
public enum SeverityType implements java.io.Serializable {


      //------------------/
     //- Enum Constants -/
    //------------------/

    /**
     * Constant EXTREMELYSEVERE
     */
    EXTREMELYSEVERE("ExtremelySevere"),
    /**
     * Constant VERYSEVERE
     */
    VERYSEVERE("VerySevere"),
    /**
     * Constant SEVERE
     */
    SEVERE("Severe"),
    /**
     * Constant LOWSEVERITY
     */
    LOWSEVERITY("LowSeverity"),
    /**
     * Constant LOWESTSEVERITY
     */
    LOWESTSEVERITY("LowestSeverity"),
    /**
     * Constant NOTPROVIDED
     */
    NOTPROVIDED("NotProvided");

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

    private SeverityType(final java.lang.String value) {
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
    public static chouette.schema.types.SeverityType fromValue(
            final java.lang.String value) {
        for (SeverityType c: SeverityType.values()) {
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
