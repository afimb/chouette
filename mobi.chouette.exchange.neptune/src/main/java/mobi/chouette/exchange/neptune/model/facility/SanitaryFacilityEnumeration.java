package mobi.chouette.exchange.neptune.model.facility;

import java.io.Serializable;

public enum SanitaryFacilityEnumeration implements Serializable
{

   // ------------------/
   // - Enum Constants -/
   // ------------------/

   /**
    * Constant UNKNOWN
    */
   UNKNOWN("unknown"),
   /**
    * Constant PTI23_22
    */
   PTI23_22("pti23_22"),
   /**
    * Constant TOILET
    */
   TOILET("toilet"),
   /**
    * Constant PTI23_23
    */
   PTI23_23("pti23_23"),
   /**
    * Constant NOTOILET
    */
   NOTOILET("noToilet"),
   /**
    * Constant SHOWER
    */
   SHOWER("shower"),
   /**
    * Constant WHEELCHAIRACCCESSTOILET
    */
   WHEELCHAIRACCCESSTOILET("wheelchairAcccessToilet"),
   /**
    * Constant BABYCHANGE
    */
   BABYCHANGE("babyChange");

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

   private SanitaryFacilityEnumeration(final java.lang.String value)
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
   public static SanitaryFacilityEnumeration fromValue(
         final java.lang.String value)
   {
      for (SanitaryFacilityEnumeration c : SanitaryFacilityEnumeration.values())
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
