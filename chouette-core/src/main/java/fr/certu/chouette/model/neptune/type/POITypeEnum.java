package fr.certu.chouette.model.neptune.type;

public enum POITypeEnum {
	      //------------------/
	     //- Enum Constants -/
	    //------------------/

	    /**
	     * Constant ACCOMMODATIONEATINGANDDRINKING
	     */
	    ACCOMMODATIONEATINGANDDRINKING("AccommodationEatingAndDrinking"),
	    /**
	     * Constant COMMERCIALSERVICES
	     */
	    COMMERCIALSERVICES("CommercialServices"),
	    /**
	     * Constant ATTRACTION
	     */
	    ATTRACTION("Attraction"),
	    /**
	     * Constant SPORTANDENTERTAINMENT
	     */
	    SPORTANDENTERTAINMENT("SportAndEntertainment"),
	    /**
	     * Constant EDUCATIONANDHEALTH
	     */
	    EDUCATIONANDHEALTH("EducationAndHealth"),
	    /**
	     * Constant PUBLICINFRASTRUCTURE
	     */
	    PUBLICINFRASTRUCTURE("PublicInfrastructure"),
	    /**
	     * Constant MANUFACTURINGANDPRODUCTION
	     */
	    MANUFACTURINGANDPRODUCTION("ManufacturingAndProduction"),
	    /**
	     * Constant WHOLESALE
	     */
	    WHOLESALE("Wholesale"),
	    /**
	     * Constant RETAIL
	     */
	    RETAIL("Retail"),
	    /**
	     * Constant TRANSPORT
	     */
	    TRANSPORT("Transport");

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

	    private POITypeEnum(final java.lang.String value) {
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
	    public static POITypeEnum fromValue(
	            final java.lang.String value) {
	        for (POITypeEnum c: POITypeEnum.values()) {
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

