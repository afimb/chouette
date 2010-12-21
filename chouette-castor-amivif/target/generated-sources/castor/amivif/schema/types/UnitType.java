/*
 * This class was automatically generated with 
 * <a href="http://www.castor.org">Castor 1.3.0.1</a>, using an XML
 * Schema.
 * $Id$
 */

package amivif.schema.types;

/**
 * Enumeration containing all the possible units for measurement
 *  
 * 
 * @version $Revision$ $Date$
 */
@SuppressWarnings("serial")
public enum UnitType implements java.io.Serializable {


      //------------------/
     //- Enum Constants -/
    //------------------/

    /**
     * Constant DEGREESCELSIUS
     */
    DEGREESCELSIUS("DegreesCelsius"),
    /**
     * Constant CENTIMETER
     */
    CENTIMETER("Centimeter"),
    /**
     * Constant DEGREE
     */
    DEGREE("Degree"),
    /**
     * Constant HOUR
     */
    HOUR("Hour"),
    /**
     * Constant HECTOPASCALS
     */
    HECTOPASCALS("Hectopascals"),
    /**
     * Constant KILOMETERSPERHOUR
     */
    KILOMETERSPERHOUR("KilometersPerHour"),
    /**
     * Constant KILOMETER
     */
    KILOMETER("Kilometer"),
    /**
     * Constant CUBICMETER
     */
    CUBICMETER("CubicMeter"),
    /**
     * Constant MILLIMETERSPERHOUR
     */
    MILLIMETERSPERHOUR("MillimetersPerHour"),
    /**
     * Constant MILLIMETER
     */
    MILLIMETER("Millimeter"),
    /**
     * Constant METER
     */
    METER("Meter"),
    /**
     * Constant METERSPERSECOND
     */
    METERSPERSECOND("MetersPerSecond"),
    /**
     * Constant PERCENTAGE
     */
    PERCENTAGE("Percentage"),
    /**
     * Constant SECOND
     */
    SECOND("Second"),
    /**
     * Constant TONNE
     */
    TONNE("Tonne"),
    /**
     * Constant HRMINSEC
     */
    HRMINSEC("HrMinSec"),
    /**
     * Constant PERIODOFTIME
     */
    PERIODOFTIME("PeriodOfTime");

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

    private UnitType(final java.lang.String value) {
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
    public static amivif.schema.types.UnitType fromValue(
            final java.lang.String value) {
        for (UnitType c: UnitType.values()) {
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
