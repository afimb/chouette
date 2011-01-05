package fr.certu.chouette.model.neptune.type;

public enum BoardingAlightingPossibilityEnum {
	      //------------------/
	     //- Enum Constants -/
	    //------------------/

	    /**
	     * Constant BOARDANDALIGHT
	     */
	    BOARDANDALIGHT("BoardAndAlight"),
	    /**
	     * Constant ALIGHTONLY
	     */
	    ALIGHTONLY("AlightOnly"),
	    /**
	     * Constant BOARDONLY
	     */
	    BOARDONLY("BoardOnly"),
	    /**
	     * Constant NEITHERBOARDORALIGHT
	     */
	    NEITHERBOARDORALIGHT("NeitherBoardOrAlight"),
	    /**
	     * Constant BOARDANDALIGHTONREQUEST
	     */
	    BOARDANDALIGHTONREQUEST("BoardAndAlightOnRequest"),
	    /**
	     * Constant ALIGHTONREQUEST
	     */
	    ALIGHTONREQUEST("AlightOnRequest"),
	    /**
	     * Constant BOARDONREQUEST
	     */
	    BOARDONREQUEST("BoardOnRequest");

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

	    private BoardingAlightingPossibilityEnum(final java.lang.String value) {
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
	    public static BoardingAlightingPossibilityEnum fromValue(
	            final java.lang.String value) {
	        for (BoardingAlightingPossibilityEnum c: BoardingAlightingPossibilityEnum.values()) {
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

