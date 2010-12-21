/*
 * This class was automatically generated with 
 * <a href="http://www.castor.org">Castor 1.3.0.1</a>, using an XML
 * Schema.
 * $Id$
 */

package amivif.schema.types;

/**
 * Enumeration containing all the possible status of a PT service 
 * on a vehicle
 *  
 * 
 * @version $Revision$ $Date$
 */
@SuppressWarnings("serial")
public enum VehicleJourneyStatusValueType implements java.io.Serializable {


      //------------------/
     //- Enum Constants -/
    //------------------/

    /**
     * Constant NORMAL
     */
    NORMAL("Normal"),
    /**
     * Constant DELAYED
     */
    DELAYED("Delayed"),
    /**
     * Constant CANCELLED
     */
    CANCELLED("Cancelled"),
    /**
     * Constant REROUTED
     */
    REROUTED("Rerouted"),
    /**
     * Constant NOTSTOPPING
     */
    NOTSTOPPING("NotStopping"),
    /**
     * Constant EARLY
     */
    EARLY("Early");

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

    private VehicleJourneyStatusValueType(final java.lang.String value) {
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
    public static amivif.schema.types.VehicleJourneyStatusValueType fromValue(
            final java.lang.String value) {
        for (VehicleJourneyStatusValueType c: VehicleJourneyStatusValueType.values()) {
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
