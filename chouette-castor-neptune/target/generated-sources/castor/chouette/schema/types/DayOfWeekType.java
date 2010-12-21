/*
 * This class was automatically generated with 
 * <a href="http://www.castor.org">Castor 1.3.0.1</a>, using an XML
 * Schema.
 * $Id$
 */

package chouette.schema.types;

/**
 * Defines the list of the days of the week (in english!)
 *  
 * 
 * @version $Revision$ $Date$
 */
@SuppressWarnings("serial")
public enum DayOfWeekType implements java.io.Serializable {


      //------------------/
     //- Enum Constants -/
    //------------------/

    /**
     * Constant MONDAY
     */
    MONDAY("Monday"),
    /**
     * Constant TUESDAY
     */
    TUESDAY("Tuesday"),
    /**
     * Constant WEDNSDAY
     */
    WEDNSDAY("Wednsday"),
    /**
     * Constant THURSDAY
     */
    THURSDAY("Thursday"),
    /**
     * Constant FRIDAY
     */
    FRIDAY("Friday"),
    /**
     * Constant SATURDAY
     */
    SATURDAY("Saturday"),
    /**
     * Constant SUNDAY
     */
    SUNDAY("Sunday");

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

    private DayOfWeekType(final java.lang.String value) {
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
    public static chouette.schema.types.DayOfWeekType fromValue(
            final java.lang.String value) {
        for (DayOfWeekType c: DayOfWeekType.values()) {
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
