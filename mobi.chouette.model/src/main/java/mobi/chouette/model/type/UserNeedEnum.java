package mobi.chouette.model.type;

/**
 * User needs
 *
 */
public enum UserNeedEnum implements java.io.Serializable
{

   // ------------------/
   // - Enum Constants -/
   // ------------------/

   /**
    * ALLERGIC
    */
   ALLERGIC("allergic", NeedCategoryEnum.MEDICAL),
   /**
    * HEARTCONDITION
    */
   HEART_CONDITION("heartCondition", NeedCategoryEnum.MEDICAL),
   /**
    * OTHERMEDICALNEED
    */
   OTHER_MEDICAL_NEED("otherMedicalNeed", NeedCategoryEnum.MEDICAL),
   /**
    * VISUALIMPAIRMENT
    */
   VISUAL_IMPAIRMENT("visualImpairment", NeedCategoryEnum.PSYCHOSENSORY),
   /**
    * AUDITORYIMPAIRMENT
    */
   AUDITORY_IMPAIRMENT("auditoryImpairment", NeedCategoryEnum.PSYCHOSENSORY),
   /**
    * COGNITIVEINPUTIMPAIRMENT
    */
   COGNITIVE_INPUT_IMPAIRMENT("cognitiveInputImpairment",
         NeedCategoryEnum.PSYCHOSENSORY),
   /**
    * AVERSETOLIFTS
    */
   AVERSE_TO_LIFTS("averseToLifts", NeedCategoryEnum.PSYCHOSENSORY),
   /**
    * AVERSETOESCALATORS
    */
   AVERSE_TO_ESCALATORS("averseToEscalators", NeedCategoryEnum.PSYCHOSENSORY),
   /**
    * AVERSETOCONFINEDSPACES
    */
   AVERSE_TO_CONFINED_SPACES("averseToConfinedSpaces",
         NeedCategoryEnum.PSYCHOSENSORY),
   /**
    * Constant AVERSETOCROWDS
    */
   AVERSE_TO_CROWDS("averseToCrowds", NeedCategoryEnum.PSYCHOSENSORY),
   /**
    * OTHERPSYCHOSENSORYNEED
    */
   OTHER_PSYCHOSENSORY_NEED("otherPsychosensoryNeed",
         NeedCategoryEnum.PSYCHOSENSORY),
   /**
    * LUGGAGEENCUMBERED
    */
   LUGGAGE_ENCUMBERED("luggageEncumbered", NeedCategoryEnum.ENCUMBRANCE),
   /**
    * PUSHCHAIR
    */
   PUSHCHAIR("pushchair", NeedCategoryEnum.ENCUMBRANCE),
   /**
    * Constant BAGGAGETROLLEY
    */
   BAGGAGE_TROLLEY("baggageTrolley", NeedCategoryEnum.ENCUMBRANCE),
   /**
    * OVERSIZEBAGGAGE
    */
   OVERSIZE_BAGGAGE("oversizeBaggage", NeedCategoryEnum.ENCUMBRANCE),
   /**
    * GUIDEDOG
    */
   GUIDE_DOG("guideDog", NeedCategoryEnum.ENCUMBRANCE),
   /**
    * OTHERANIMAL
    */
   OTHER_ANIMAL("otherAnimal", NeedCategoryEnum.ENCUMBRANCE),
   /**
    * OTHERENCUMBRANCE
    */
   OTHER_ENCUMBRANCE("otherEncumbrance", NeedCategoryEnum.ENCUMBRANCE),
   /**
    * WHEELCHAIR
    */
   WHEELCHAIR("wheelchair", NeedCategoryEnum.MOBILITY),
   /**
    * ASSISTEDWHEELCHAIR
    */
   ASSISTED_WHEELCHAIR("assistedWheelchair", NeedCategoryEnum.MOBILITY),
   /**
    * MOTORIZEDWHEELCHAIR
    */
   MOTORIZED_WHEELCHAIR("motorizedWheelchair", NeedCategoryEnum.MOBILITY),
   /**
    * WALKINGFRAME
    */
   WALKING_FRAME("walkingFrame", NeedCategoryEnum.MOBILITY),
   /**
    * RESTRICTEDMOBILITY
    */
   RESTRICTED_MOBILITY("restrictedMobility", NeedCategoryEnum.MOBILITY),
   /**
    * OTHERMOBILITYNEED
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

   /**
    * Need Categories
    *
    */
   public enum NeedCategoryEnum implements java.io.Serializable
   {

      // ------------------/
      // - Enum Constants -/
      // ------------------/

      /**
       * MEDICAL
       */
      MEDICAL("medical"),
      /**
       * PSYCHOSENSORY
       */
      PSYCHOSENSORY("psychosensory"),
      /**
       * ENCUMBRANCE
       */
      ENCUMBRANCE("encumbrance"),
      /**
       * MOBILITY
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
