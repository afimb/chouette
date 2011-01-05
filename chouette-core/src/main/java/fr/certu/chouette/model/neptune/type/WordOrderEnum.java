package fr.certu.chouette.model.neptune.type;

public enum WordOrderEnum 
{


	      //------------------/
	     //- Enum Constants -/
	    //------------------/

	    /**
	     * Constant FROMTHEFIRSTTOTHELASTWORD
	     */
	    FROMTHEFIRSTTOTHELASTWORD("FromTheFirstToTheLastWord"),
	    /**
	     * Constant FROMTHELASTTOTHEFIRSTWORD
	     */
	    FROMTHELASTTOTHEFIRSTWORD("FromTheLastToTheFirstWord");

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

	    private WordOrderEnum(final java.lang.String value) {
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
	    public static WordOrderEnum fromValue(
	            final java.lang.String value) {
	        for (WordOrderEnum c: WordOrderEnum.values()) {
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
