package fr.certu.chouette.model.neptune.type;

public enum ChouetteAreaEnum 
{
	      //------------------/
	     //- Enum Constants -/
	    //------------------/

	    /**
	     * Constant QUAY
	     */
	    QUAY("Quay"),
	    /**
	     * Constant BOARDINGPOSITION
	     */
	    BOARDINGPOSITION("BoardingPosition"),
	    /**
	     * Constant COMMERCIALSTOPPOINT
	     */
	    COMMERCIALSTOPPOINT("CommercialStopPoint"),
	    /**
	     * Constant STOPPLACE
	     */
	    STOPPLACE("StopPlace"),
	    /**
	     * Constant ITL
	     */
	    ITL("ITL");

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

	    private ChouetteAreaEnum(final java.lang.String value) {
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
	    public static ChouetteAreaEnum fromValue(
	            final java.lang.String value) {
	        for (ChouetteAreaEnum c: ChouetteAreaEnum.values()) {
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

