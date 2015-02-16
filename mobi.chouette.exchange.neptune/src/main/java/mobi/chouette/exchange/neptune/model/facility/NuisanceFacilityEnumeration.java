package mobi.chouette.exchange.neptune.model.facility;

import java.io.Serializable;

public enum NuisanceFacilityEnumeration implements Serializable
{

   // ------------------/
   // - Enum Constants -/
   // ------------------/

   /**
    * Constant UNKNOWN
    */
   UNKNOWN("unknown"),
   /**
    * Constant SMOKING
    */
   SMOKING("smoking"),
   /**
    * Constant NOSMOKING
    */
   NOSMOKING("noSmoking"),
   /**
    * Constant MOBILEPHONEUSEZONE
    */
   MOBILEPHONEUSEZONE("mobilePhoneUseZone"),
   /**
    * Constant MOBILEPHONEFREEZONE
    */
   MOBILEPHONEFREEZONE("mobilePhoneFreeZone");

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

   private NuisanceFacilityEnumeration(final java.lang.String value)
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
   public static NuisanceFacilityEnumeration fromValue(
         final java.lang.String value)
   {
      for (NuisanceFacilityEnumeration c : NuisanceFacilityEnumeration.values())
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
