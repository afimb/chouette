/*
 * This class was automatically generated with 
 * <a href="http://www.castor.org">Castor 1.3.0.1</a>, using an XML
 * Schema.
 * $Id$
 */

package chouette.schema.types;

/**
 * Values for Access Facility
 * 
 * @version $Revision$ $Date$
 */
@SuppressWarnings("serial")
public enum ParkingFacilityEnumeration implements java.io.Serializable {


      //------------------/
     //- Enum Constants -/
    //------------------/

    /**
     * Constant UNKNOWN
     */
    UNKNOWN("unknown"),
    /**
     * Constant CARPARK
     */
    CARPARK("carPark"),
    /**
     * Constant PARKANDRIDEPARK
     */
    PARKANDRIDEPARK("parkAndRidePark"),
    /**
     * Constant MOTORCYCLEPARK
     */
    MOTORCYCLEPARK("motorcyclePark"),
    /**
     * Constant CYCLEPARK
     */
    CYCLEPARK("cyclePark"),
    /**
     * Constant RENTALCARPARK
     */
    RENTALCARPARK("rentalCarPark"),
    /**
     * Constant COACHPARK
     */
    COACHPARK("coachPark");

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

    private ParkingFacilityEnumeration(final java.lang.String value) {
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
    public static chouette.schema.types.ParkingFacilityEnumeration fromValue(
            final java.lang.String value) {
        for (ParkingFacilityEnumeration c: ParkingFacilityEnumeration.values()) {
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
