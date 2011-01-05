package fr.certu.chouette.model.neptune.type;

public enum LongLatTypeEnum {
	      //------------------/
	     //- Enum Constants -/
	    //------------------/

	    /**
	     * Constant WGS84
	     */
	    WGS84("WGS84"),
	    /**
	     * Constant WGS92
	     */
	    WGS92("WGS92"),
	    /**
	     * Constant STANDARD
	     */
	    STANDARD("Standard");

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

	    private LongLatTypeEnum(final java.lang.String value) {
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
	    public static LongLatTypeEnum fromValue(
	            final java.lang.String value) {
	        for (LongLatTypeEnum c: LongLatTypeEnum.values()) {
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
