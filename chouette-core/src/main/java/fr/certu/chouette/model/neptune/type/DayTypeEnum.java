package fr.certu.chouette.model.neptune.type;

public enum DayTypeEnum {
	      //------------------/
	     //- Enum Constants -/
	    //------------------/

	    /**
	     * Constant WEEKDAY
	     */
	    WEEKDAY("WeekDay"),
	    /**
	     * Constant WEEKEND
	     */
	    WEEKEND("WeekEnd"),
	    /**
	     * Constant MONDAY
	     */
	    MONDAY("Monday"),
	    /**
	     * Constant TUESDAY
	     */
	    TUESDAY("Tuesday"),
	    /**
	     * Constant WEDNESDAY
	     */
	    WEDNESDAY("Wednesday"),
	    /**
	     * Constant THURSDAY
	     */
	    THURSDAY("Thursday"),
	    /**
	     * Constant FRIDAY
	     */
	    FRIDAY("Friday"),
	    /**
	     * Constant SATURDAY
	     */
	    SATURDAY("Saturday"),
	    /**
	     * Constant SUNDAY
	     */
	    SUNDAY("Sunday"),
	    /**
	     * Constant SCHOOLHOLLIDAY
	     */
	    SCHOOLHOLLIDAY("SchoolHolliday"),
	    /**
	     * Constant PUBLICHOLLIDAY
	     */
	    PUBLICHOLLIDAY("PublicHolliday"),
	    /**
	     * Constant MARKETDAY
	     */
	    MARKETDAY("MarketDay");

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

	    private DayTypeEnum(final java.lang.String value) {
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
	    public static DayTypeEnum fromValue(
	            final java.lang.String value) {
	        for (DayTypeEnum c: DayTypeEnum.values()) {
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
