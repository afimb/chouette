package fr.certu.chouette.model.neptune.type;

public enum UserNeedEnum implements java.io.Serializable
{

   // ------------------/
   // - Enum Constants -/
   // ------------------/

   /**
    * Constant ALLERGIC
    */
   ALLERGIC("allergic", NeedCategoryEnum.MEDICAL),
   /**
    * Constant HEARTCONDITION
    */
   HEART_CONDITION("heartCondition", NeedCategoryEnum.MEDICAL),
   /**
    * Constant OTHERMEDICALNEED
    */
   OTHER_MEDICAL_NEED("otherMedicalNeed", NeedCategoryEnum.MEDICAL),
   /**
    * Constant VISUALIMPAIRMENT
    */
   VISUAL_IMPAIRMENT("visualImpairment", NeedCategoryEnum.PSYCHOSENSORY),
   /**
    * Constant AUDITORYIMPAIRMENT
    */
   AUDITORY_IMPAIRMENT("auditoryImpairment", NeedCategoryEnum.PSYCHOSENSORY),
   /**
    * Constant COGNITIVEINPUTIMPAIRMENT
    */
   COGNITIVE_INPUT_IMPAIRMENT("cognitiveInputImpairment",
         NeedCategoryEnum.PSYCHOSENSORY),
   /**
    * Constant AVERSETOLIFTS
    */
   AVERSE_TO_LIFTS("averseToLifts", NeedCategoryEnum.PSYCHOSENSORY),
   /**
    * Constant AVERSETOESCALATORS
    */
   AVERSE_TO_ESCALATORS("averseToEscalators", NeedCategoryEnum.PSYCHOSENSORY),
   /**
    * Constant AVERSETOCONFINEDSPACES
    */
   AVERSE_TO_CONFINED_SPACES("averseToConfinedSpaces",
         NeedCategoryEnum.PSYCHOSENSORY),
   /**
    * Constant AVERSETOCROWDS
    */
   AVERSE_TO_CROWDS("averseToCrowds", NeedCategoryEnum.PSYCHOSENSORY),
   /**
    * Constant OTHERPSYCHOSENSORYNEED
    */
   OTHER_PSYCHOSENSORY_NEED("otherPsychosensoryNeed",
         NeedCategoryEnum.PSYCHOSENSORY),
   /**
    * Constant LUGGAGEENCUMBERED
    */
   LUGGAGE_ENCUMBERED("luggageEncumbered", NeedCategoryEnum.ENCUMBRANCE),
   /**
    * Constant PUSHCHAIR
    */
   PUSHCHAIR("pushchair", NeedCategoryEnum.ENCUMBRANCE),
   /**
    * Constant BAGGAGETROLLEY
    */
   BAGGAGE_TROLLEY("baggageTrolley", NeedCategoryEnum.ENCUMBRANCE),
   /**
    * Constant OVERSIZEBAGGAGE
    */
   OVERSIZE_BAGGAGE("oversizeBaggage", NeedCategoryEnum.ENCUMBRANCE),
   /**
    * Constant GUIDEDOG
    */
   GUIDE_DOG("guideDog", NeedCategoryEnum.ENCUMBRANCE),
   /**
    * Constant OTHERANIMAL
    */
   OTHER_ANIMAL("otherAnimal", NeedCategoryEnum.ENCUMBRANCE),
   /**
    * Constant OTHERENCUMBRANCE
    */
   OTHER_ENCUMBRANCE("otherEncumbrance", NeedCategoryEnum.ENCUMBRANCE),
   /**
    * Constant WHEELCHAIR
    */
   WHEELCHAIR("wheelchair", NeedCategoryEnum.MOBILITY),
   /**
    * Constant ASSISTEDWHEELCHAIR
    */
   ASSISTED_WHEELCHAIR("assistedWheelchair", NeedCategoryEnum.MOBILITY),
   /**
    * Constant MOTORIZEDWHEELCHAIR
    */
   MOTORIZED_WHEELCHAIR("motorizedWheelchair", NeedCategoryEnum.MOBILITY),
   /**
    * Constant WALKINGFRAME
    */
   WALKING_FRAME("walkingFrame", NeedCategoryEnum.MOBILITY),
   /**
    * Constant RESTRICTEDMOBILITY
    */
   RESTRICTED_MOBILITY("restrictedMobility", NeedCategoryEnum.MOBILITY),
   /**
    * Constant OTHERMOBILITYNEED
    */
   OTHER_MOBILITY_NEED("otherMobilityNeed", NeedCategoryEnum.MOBILITY);

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

   private UserNeedEnum(final java.lang.String value, NeedCategoryEnum category)
   {
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
   public static UserNeedEnum fromValue(final java.lang.String value)
   {
      for (UserNeedEnum c : UserNeedEnum.values())
      {
         if (c.value.equals(value))
         {
            return c;
         }
      }
      return valueOf(value);
   }

   /**
    * 
    * 
    * @param value
    */
   public void setValue(final java.lang.String value)
   {
   }

   /**
    * Method toString.
    * 
    * @return the value of this constant
    */
   public java.lang.String toString()
   {
      return this.value;
   }

   /**
    * Method value.
    * 
    * @return the value of this constant
    */
   public java.lang.String value()
   {
      return this.value;
   }

   /**
    * Method category.
    * 
    * @return the category of this constant
    */
   public NeedCategoryEnum category()
   {
      return this.category;
   }

   public enum NeedCategoryEnum implements java.io.Serializable
   {

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

      private NeedCategoryEnum(final java.lang.String value)
      {
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
      public static NeedCategoryEnum fromValue(final java.lang.String value)
      {
         for (NeedCategoryEnum c : NeedCategoryEnum.values())
         {
            if (c.value.equals(value))
            {
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
      public void setValue(final java.lang.String value)
      {
      }

      /**
       * Method toString.
       * 
       * @return the value of this constant
       */
      public java.lang.String toString()
      {
         return this.value;
      }

      /**
       * Method value.
       * 
       * @return the value of this constant
       */
      public java.lang.String value()
      {
         return this.value;
      }
   }
}
