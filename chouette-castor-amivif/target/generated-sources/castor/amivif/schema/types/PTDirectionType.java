/*
 * This class was automatically generated with 
 * <a href="http://www.castor.org">Castor 1.3.0.1</a>, using an XML
 * Schema.
 * $Id$
 */

package amivif.schema.types;

/**
 * Enumeration containing all the possible directions on a PT
 * Network
 *  
 * 
 * @version $Revision$ $Date$
 */
@SuppressWarnings("serial")
public enum PTDirectionType implements java.io.Serializable {


      //------------------/
     //- Enum Constants -/
    //------------------/

    /**
     * Constant NORTH
     */
    NORTH("North"),
    /**
     * Constant NORTHEAST
     */
    NORTHEAST("NorthEast"),
    /**
     * Constant EAST
     */
    EAST("East"),
    /**
     * Constant SOUTHEAST
     */
    SOUTHEAST("SouthEast"),
    /**
     * Constant SOUTH
     */
    SOUTH("South"),
    /**
     * Constant SOUTHWEST
     */
    SOUTHWEST("SouthWest"),
    /**
     * Constant WEST
     */
    WEST("West"),
    /**
     * Constant NORTHWEST
     */
    NORTHWEST("NorthWest"),
    /**
     * Constant CLOCKWISE
     */
    CLOCKWISE("ClockWise"),
    /**
     * Constant COUNTERCLOCKWISE
     */
    COUNTERCLOCKWISE("CounterClockWise"),
    /**
     * Constant A
     */
    A("A"),
    /**
     * Constant R
     */
    R("R");

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

    private PTDirectionType(final java.lang.String value) {
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
    public static amivif.schema.types.PTDirectionType fromValue(
            final java.lang.String value) {
        for (PTDirectionType c: PTDirectionType.values()) {
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
