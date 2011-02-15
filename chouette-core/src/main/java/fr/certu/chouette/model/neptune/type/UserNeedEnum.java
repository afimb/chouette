package fr.certu.chouette.model.neptune.type;

public enum UserNeedEnum implements java.io.Serializable {

	// ------------------/
	// - Enum Constants -/
	// ------------------/

	/**
	 * Constant ALLERGIC
	 */
	ALLERGIC("allergic",NeedCategoryEnum.MEDICAL),
	/**
	 * Constant HEARTCONDITION
	 */
	HEARTCONDITION("heartCondition",NeedCategoryEnum.MEDICAL),
	/**
	 * Constant OTHERMEDICALNEED
	 */
	OTHERMEDICALNEED("otherMedicalNeed",NeedCategoryEnum.MEDICAL),
	/**
	 * Constant VISUALIMPAIRMENT
	 */
	VISUALIMPAIRMENT("visualImpairment",NeedCategoryEnum.PSYCHOSENSORY),
	/**
	 * Constant AUDITORYIMPAIRMENT
	 */
	AUDITORYIMPAIRMENT("auditoryImpairment",NeedCategoryEnum.PSYCHOSENSORY),
	/**
	 * Constant COGNITIVEINPUTIMPAIRMENT
	 */
	COGNITIVEINPUTIMPAIRMENT("cognitiveInputImpairment",NeedCategoryEnum.PSYCHOSENSORY),
	/**
	 * Constant AVERSETOLIFTS
	 */
	AVERSETOLIFTS("averseToLifts",NeedCategoryEnum.PSYCHOSENSORY),
	/**
	 * Constant AVERSETOESCALATORS
	 */
	AVERSETOESCALATORS("averseToEscalators",NeedCategoryEnum.PSYCHOSENSORY),
	/**
	 * Constant AVERSETOCONFINEDSPACES
	 */
	AVERSETOCONFINEDSPACES("averseToConfinedSpaces",NeedCategoryEnum.PSYCHOSENSORY),
	/**
	 * Constant AVERSETOCROWDS
	 */
	AVERSETOCROWDS("averseToCrowds",NeedCategoryEnum.PSYCHOSENSORY),
	/**
	 * Constant OTHERPSYCHOSENSORYNEED
	 */
	OTHERPSYCHOSENSORYNEED("otherPsychosensoryNeed",NeedCategoryEnum.PSYCHOSENSORY),
	/**
	 * Constant LUGGAGEENCUMBERED
	 */
	LUGGAGEENCUMBERED("luggageEncumbered",NeedCategoryEnum.ENCUMBRANCE),
	/**
	 * Constant PUSHCHAIR
	 */
	PUSHCHAIR("pushchair",NeedCategoryEnum.ENCUMBRANCE),
	/**
	 * Constant BAGGAGETROLLEY
	 */
	BAGGAGETROLLEY("baggageTrolley",NeedCategoryEnum.ENCUMBRANCE),
	/**
	 * Constant OVERSIZEBAGGAGE
	 */
	OVERSIZEBAGGAGE("oversizeBaggage",NeedCategoryEnum.ENCUMBRANCE),
	/**
	 * Constant GUIDEDOG
	 */
	GUIDEDOG("guideDog",NeedCategoryEnum.ENCUMBRANCE),
	/**
	 * Constant OTHERANIMAL
	 */
	OTHERANIMAL("otherAnimal",NeedCategoryEnum.ENCUMBRANCE),
	/**
	 * Constant OTHERENCUMBRANCE
	 */
	OTHERENCUMBRANCE("otherEncumbrance",NeedCategoryEnum.ENCUMBRANCE),
	/**
	 * Constant WHEELCHAIR
	 */
	WHEELCHAIR("wheelchair",NeedCategoryEnum.MOBILITY),
	/**
	 * Constant ASSISTEDWHEELCHAIR
	 */
	ASSISTEDWHEELCHAIR("assistedWheelchair",NeedCategoryEnum.MOBILITY),
	/**
	 * Constant MOTORIZEDWHEELCHAIR
	 */
	MOTORIZEDWHEELCHAIR("motorizedWheelchair",NeedCategoryEnum.MOBILITY),
	/**
	 * Constant WALKINGFRAME
	 */
	WALKINGFRAME("walkingFrame",NeedCategoryEnum.MOBILITY),
	/**
	 * Constant RESTRICTEDMOBILITY
	 */
	RESTRICTEDMOBILITY("restrictedMobility",NeedCategoryEnum.MOBILITY),
	/**
	 * Constant OTHERMOBILITYNEED
	 */
	OTHERMOBILITYNEED("otherMobilityNeed",NeedCategoryEnum.MOBILITY);

	// --------------------------/
	// - Class/Member Variables -/
	// --------------------------/

	/**
	 * Field value.
	 */
	private final java.lang.String value;
	private final NeedCategoryEnum category;
	
	// ----------------/
	// - Constructors -/
	// ----------------/

	private UserNeedEnum(final java.lang.String value,NeedCategoryEnum category) {
		this.value = value;
		this.category = category;
	}

	// -----------/
	// - Methods -/
	// -----------/

	/**
	 * Method fromValue.
	 * 
	 * @param value
	 * @return the constant for this value
	 */
	public static UserNeedEnum fromValue(final java.lang.String value) {
		for (UserNeedEnum c : UserNeedEnum.values()) {
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
	
	/**
	 * Method category.
	 * 
	 * @return the category of this constant
	 */
	public NeedCategoryEnum category() {
		return this.category;
	}

	public enum NeedCategoryEnum implements java.io.Serializable {

		// ------------------/
		// - Enum Constants -/
		// ------------------/

		/**
		 * Constant MEDICAL
		 */
		MEDICAL("medical"),
		/**
		 * Constant PSYCHOSENSORY
		 */
		PSYCHOSENSORY("psychosensory"),
		/**
		 * Constant ENCUMBRANCE
		 */
		ENCUMBRANCE("encumbrance"),
		/**
		 * Constant MOBILITY
		 */
		MOBILITY("mobility");

		// --------------------------/
		// - Class/Member Variables -/
		// --------------------------/

		/**
		 * Field value.
		 */
		private final java.lang.String value;

		// ----------------/
		// - Constructors -/
		// ----------------/

		private NeedCategoryEnum(final java.lang.String value) {
			this.value = value;
		}

		// -----------/
		// - Methods -/
		// -----------/

		/**
		 * Method fromValue.
		 * 
		 * @param value
		 * @return the constant for this value
		 */
		public static NeedCategoryEnum fromValue(final java.lang.String value) {
			for (NeedCategoryEnum c : NeedCategoryEnum.values()) {
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
	}
}
