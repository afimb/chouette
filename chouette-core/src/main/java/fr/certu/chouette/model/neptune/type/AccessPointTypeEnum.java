package fr.certu.chouette.model.neptune.type;

public enum AccessPointTypeEnum 
{
	      //------------------/
	     //- Enum Constants -/
	    //------------------/

	    /**
	     * Constant IN
	     */
	    IN("In"),
	    /**
	     * Constant OUT
	     */
	    OUT("Out"),
	    /**
	     * Constant INOUT
	     */
	    INOUT("InOut");

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

	    private AccessPointTypeEnum(final java.lang.String value) {
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
	    public static AccessPointTypeEnum fromValue(
	            final java.lang.String value) {
	        for (AccessPointTypeEnum c: AccessPointTypeEnum.values()) {
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

