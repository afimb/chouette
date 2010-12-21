/*
 * This class was automatically generated with 
 * <a href="http://www.castor.org">Castor 1.3.0.1</a>, using an XML
 * Schema.
 * $Id$
 */

package chouette.schema.types;

/**
 * Enumeration containing all the possible location Type
 *  
 * 
 * @version $Revision$ $Date$
 */
@SuppressWarnings("serial")
public enum LocationTypeType implements java.io.Serializable {


      //------------------/
     //- Enum Constants -/
    //------------------/

    /**
     * Constant BUSSTOPPOINT
     */
    BUSSTOPPOINT("BusStopPoint"),
    /**
     * Constant ARIPORTSTOPPOINT
     */
    ARIPORTSTOPPOINT("AriportStopPoint"),
    /**
     * Constant TRAMSTOPPOINT
     */
    TRAMSTOPPOINT("TramStopPoint"),
    /**
     * Constant METROSTOPPOINT
     */
    METROSTOPPOINT("MetroStopPoint"),
    /**
     * Constant RAILWAYSTOPPOINT
     */
    RAILWAYSTOPPOINT("RailwayStopPoint"),
    /**
     * Constant ROADJUNCTION
     */
    ROADJUNCTION("RoadJunction"),
    /**
     * Constant MIXED
     */
    MIXED("Mixed"),
    /**
     * Constant ADDRESS
     */
    ADDRESS("Address"),
    /**
     * Constant INTERMEDIATEROADPOINT
     */
    INTERMEDIATEROADPOINT("IntermediateRoadPoint"),
    /**
     * Constant STOPAREA
     */
    STOPAREA("StopArea"),
    /**
     * Constant POINTOFINTEREST
     */
    POINTOFINTEREST("PointOfInterest");

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

    private LocationTypeType(final java.lang.String value) {
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
    public static chouette.schema.types.LocationTypeType fromValue(
            final java.lang.String value) {
        for (LocationTypeType c: LocationTypeType.values()) {
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
