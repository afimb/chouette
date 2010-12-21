/*
 * This class was automatically generated with 
 * <a href="http://www.castor.org">Castor 1.3.0.1</a>, using an XML
 * Schema.
 * $Id$
 */

package amivif.schema.types;

/**
 * Enumeration containing all the possible transport modes
 *  
 * 
 * @version $Revision$ $Date$
 */
@SuppressWarnings("serial")
public enum TransportModeNameType implements java.io.Serializable {


      //------------------/
     //- Enum Constants -/
    //------------------/

    /**
     * Constant AIR
     */
    AIR("Air"),
    /**
     * Constant TRAIN
     */
    TRAIN("Train"),
    /**
     * Constant LONGDISTANCETRAIN
     */
    LONGDISTANCETRAIN("LongDistanceTrain"),
    /**
     * Constant LONGDISTANCETRAIN_2
     */
    LONGDISTANCETRAIN_2("LongDistanceTrain_2"),
    /**
     * Constant LOCALTRAIN
     */
    LOCALTRAIN("LocalTrain"),
    /**
     * Constant RAPIDTRANSIT
     */
    RAPIDTRANSIT("RapidTransit"),
    /**
     * Constant METRO
     */
    METRO("Metro"),
    /**
     * Constant TRAMWAY
     */
    TRAMWAY("Tramway"),
    /**
     * Constant COACH
     */
    COACH("Coach"),
    /**
     * Constant BUS
     */
    BUS("Bus"),
    /**
     * Constant FERRY
     */
    FERRY("Ferry"),
    /**
     * Constant WATERBORNE
     */
    WATERBORNE("Waterborne"),
    /**
     * Constant PRIVATEVEHICLE
     */
    PRIVATEVEHICLE("PrivateVehicle"),
    /**
     * Constant WALK
     */
    WALK("Walk"),
    /**
     * Constant TROLLEYBUS
     */
    TROLLEYBUS("Trolleybus"),
    /**
     * Constant BICYCLE
     */
    BICYCLE("Bicycle"),
    /**
     * Constant SHUTTLE
     */
    SHUTTLE("Shuttle"),
    /**
     * Constant TAXI
     */
    TAXI("Taxi"),
    /**
     * Constant VAL
     */
    VAL("VAL"),
    /**
     * Constant OTHER
     */
    OTHER("Other");

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

    private TransportModeNameType(final java.lang.String value) {
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
    public static amivif.schema.types.TransportModeNameType fromValue(
            final java.lang.String value) {
        for (TransportModeNameType c: TransportModeNameType.values()) {
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
