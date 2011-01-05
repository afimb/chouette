package fr.certu.chouette.model.neptune.type;

public enum LocationTypeEnum 
{

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

	    private LocationTypeEnum(final java.lang.String value) {
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
	    public static LocationTypeEnum fromValue(
	            final java.lang.String value) {
	        for (LocationTypeEnum c: LocationTypeEnum.values()) {
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
