package fr.certu.chouette.model.neptune.type;

public enum ServiceStatusValueEnum {

	//------------------/
	//- Enum Constants -/
	//------------------/

	/**
	 * Constant NORMAL
	 */
	NORMAL("Normal"),
	/**
	 * Constant DELAYED
	 */
	DELAYED("Delayed"),
	/**
	 * Constant CANCELLED
	 */
	CANCELLED("Cancelled"),
	/**
	 * Constant DISRUPTED
	 */
	DISRUPTED("Disrupted"),
	/**
	 * Constant REDUCEDSERVICE
	 */
	REDUCEDSERVICE("ReducedService"),
	/**
	 * Constant INCREASEDSERVICE
	 */
	INCREASEDSERVICE("IncreasedService"),
	/**
	 * Constant REROUTED
	 */
	REROUTED("Rerouted"),
	/**
	 * Constant NOTSTOPPING
	 */
	NOTSTOPPING("NotStopping"),
	/**
	 * Constant EARLY
	 */
	EARLY("Early");

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

	private ServiceStatusValueEnum(final java.lang.String value) {
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
	public static ServiceStatusValueEnum fromValue(
			final java.lang.String value) {
		for (ServiceStatusValueEnum c: ServiceStatusValueEnum.values()) {
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

