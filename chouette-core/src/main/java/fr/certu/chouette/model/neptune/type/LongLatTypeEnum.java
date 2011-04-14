package fr.certu.chouette.model.neptune.type;

public enum LongLatTypeEnum {
	      //------------------/
	     //- Enum Constants -/
	    //------------------/

	    /**
	     * Constant WGS84
	     */
	    WGS84("WGS84",4326),
	    /**
	     * Constant WGS92
	     */
	    WGS92("WGS92",0),
	    /**
	     * Constant STANDARD same as WGS84
	     */
	    STANDARD("Standard",4326);

	      //--------------------------/
	     //- Class/Member Variables -/
	    //--------------------------/

	    /**
	     * Field value.
	     */
	    private final java.lang.String value;
		private int epsgCode;


	      //----------------/
	     //- Constructors -/
	    //----------------/

	    private LongLatTypeEnum(final java.lang.String value,final int epsgCode) {
	        this.value = value;
	        this.epsgCode = epsgCode;
		
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
	    public void setValue(final java.lang.String value) {
	    }

	    
	    
	    /**
	     * Method toString.
	     * 
	     * @return the value of this constant
	     */
	    public java.lang.String toString() {
	        return this.value;
	    }

	    /**
	     * Method value.
	     * 
	     * @return the value of this constant
	     */
	    public java.lang.String value() {
	        return this.value;
	    }
	    
	    public int epsgCode()
	    {
            return epsgCode;
	    }
	}
