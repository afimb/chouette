package fr.certu.chouette.model.neptune.type;

public enum ConnectionLinkTypeEnum 
{
	      //------------------/
	     //- Enum Constants -/
	    //------------------/

	    /**
	     * Constant UNDERGROUND
	     */
	    UNDERGROUND("Underground"),
	    /**
	     * Constant MIXED
	     */
	    MIXED("Mixed"),
	    /**
	     * Constant OVERGROUND
	     */
	    OVERGROUND("Overground");

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

	    private ConnectionLinkTypeEnum(final java.lang.String value) {
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
	    public static ConnectionLinkTypeEnum fromValue(
	            final java.lang.String value) {
	        for (ConnectionLinkTypeEnum c: ConnectionLinkTypeEnum.values()) {
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

