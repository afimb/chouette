package mobi.chouette.model.type;

/**
 * Alighting Possibility values
 * 
 * @since 2.5.2
 * 
 */
public enum AlightingPossibilityEnum {
	/**
	 * Regularly scheduled drop off
	 */
	normal,
	/**
	 * No drop off available
	 */
	forbidden,
	/**
	 * Drop off if requested
	 */
	request_stop,
	/**
	 * Booking requested for drop off
	 */
	is_flexible;

}
