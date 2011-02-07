package fr.certu.chouette.model.neptune.type;

public enum UserNeedEnum implements java.io.Serializable {


      //------------------/
     //- Enum Constants -/
    //------------------/

    /**
     * Constant ALLERGIC
     */
    ALLERGIC("allergic"),
    /**
     * Constant HEARTCONDITION
     */
    HEARTCONDITION("heartCondition"),
    /**
     * Constant OTHERMEDICALNEED
     */
    OTHERMEDICALNEED("otherMedicalNeed"),
    /**
     * Constant VISUALIMPAIRMENT
     */
    VISUALIMPAIRMENT("visualImpairment"),
    /**
     * Constant AUDITORYIMPAIRMENT
     */
    AUDITORYIMPAIRMENT("auditoryImpairment"),
    /**
     * Constant COGNITIVEINPUTIMPAIRMENT
     */
    COGNITIVEINPUTIMPAIRMENT("cognitiveInputImpairment"),
    /**
     * Constant AVERSETOLIFTS
     */
    AVERSETOLIFTS("averseToLifts"),
    /**
     * Constant AVERSETOESCALATORS
     */
    AVERSETOESCALATORS("averseToEscalators"),
    /**
     * Constant AVERSETOCONFINEDSPACES
     */
    AVERSETOCONFINEDSPACES("averseToConfinedSpaces"),
    /**
     * Constant AVERSETOCROWDS
     */
    AVERSETOCROWDS("averseToCrowds"),
    /**
     * Constant OTHERPSYCHOSENSORYNEED
     */
    OTHERPSYCHOSENSORYNEED("otherPsychosensoryNeed"),
    /**
     * Constant LUGGAGEENCUMBERED
     */
    LUGGAGEENCUMBERED("luggageEncumbered"),
    /**
     * Constant PUSHCHAIR
     */
    PUSHCHAIR("pushchair"),
    /**
     * Constant BAGGAGETROLLEY
     */
    BAGGAGETROLLEY("baggageTrolley"),
    /**
     * Constant OVERSIZEBAGGAGE
     */
    OVERSIZEBAGGAGE("oversizeBaggage"),
    /**
     * Constant GUIDEDOG
     */
    GUIDEDOG("guideDog"),
    /**
     * Constant OTHERANIMAL
     */
    OTHERANIMAL("otherAnimal"),
    /**
     * Constant OTHERENCUMBRANCE
     */
    OTHERENCUMBRANCE("otherEncumbrance"),
    /**
     * Constant WHEELCHAIR
     */
    WHEELCHAIR("wheelchair"),
    /**
     * Constant ASSISTEDWHEELCHAIR
     */
    ASSISTEDWHEELCHAIR("assistedWheelchair"),
    /**
     * Constant MOTORIZEDWHEELCHAIR
     */
    MOTORIZEDWHEELCHAIR("motorizedWheelchair"),
    /**
     * Constant WALKINGFRAME
     */
    WALKINGFRAME("walkingFrame"),
    /**
     * Constant RESTRICTEDMOBILITY
     */
    RESTRICTEDMOBILITY("restrictedMobility"),
    /**
     * Constant OTHERMOBILITYNEED
     */
    OTHERMOBILITYNEED("otherMobilityNeed");

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

    private UserNeedEnum(final java.lang.String value) {
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
    public static UserNeedEnum fromValue(
            final java.lang.String value) {
        for (UserNeedEnum c: UserNeedEnum.values()) {
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
