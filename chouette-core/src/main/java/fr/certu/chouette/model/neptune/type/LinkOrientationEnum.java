package fr.certu.chouette.model.neptune.type;

public enum LinkOrientationEnum {

	ACCESSPOINT_TO_STOPAREA("accesspoint_to_stoparea"),
	STOPAREA_TO_ACCESSPOINT("stoparea_to_accesspoint");

	private final String value;


	private LinkOrientationEnum(final String value) {
		this.value = value;
	}

	public static LinkOrientationEnum fromValue(
			final String value) {
		for (LinkOrientationEnum l: LinkOrientationEnum.values()) {
			if (l.value.equals(value)) {
				return l;
			}
		}
		throw new IllegalArgumentException(value);
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
