package fr.certu.chouette.model.neptune.type;

public enum LocationReferencingMethodEnum {

	      //------------------/
	     //- Enum Constants -/
	    //------------------/

	    /**
	     * Constant VALUE_1
	     */
	    VALUE_1("1"),
	    /**
	     * Constant VALUE_2
	     */
	    VALUE_2("2"),
	    /**
	     * Constant VALUE_3
	     */
	    VALUE_3("3"),
	    /**
	     * Constant VALUE_4
	     */
	    VALUE_4("4"),
	    /**
	     * Constant VALUE_5
	     */
	    VALUE_5("5"),
	    /**
	     * Constant VALUE_6
	     */
	    VALUE_6("6"),
	    /**
	     * Constant VALUE_7
	     */
	    VALUE_7("7"),
	    /**
	     * Constant VALUE_8
	     */
	    VALUE_8("8"),
	    /**
	     * Constant VALUE_9
	     */
	    VALUE_9("9"),
	    /**
	     * Constant VALUE_10
	     */
	    VALUE_10("10"),
	    /**
	     * Constant VALUE_11
	     */
	    VALUE_11("11");

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

	    private LocationReferencingMethodEnum(final java.lang.String value) {
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
	    public static LocationReferencingMethodEnum fromValue(
	            final java.lang.String value) {
	        for (LocationReferencingMethodEnum c: LocationReferencingMethodEnum.values()) {
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
