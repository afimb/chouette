package mobi.chouette.exchange.neptune.model.facility;

import java.io.Serializable;

public enum AccommodationFacilityEnumeration implements Serializable
{
   // ------------------/
   // - Enum Constants -/
   // ------------------/

   /**
    * Constant UNKNOWN
    */
   UNKNOWN("unknown"),
   /**
    * Constant PTI23_3
    */
   PTI23_3("pti23_3"),
   /**
    * Constant SLEEPER
    */
   SLEEPER("sleeper"),
   /**
    * Constant PTI23_4
    */
   PTI23_4("pti23_4"),
   /**
    * Constant COUCHETTE
    */
   COUCHETTE("couchette"),
   /**
    * Constant PTI23_5
    */
   PTI23_5("pti23_5"),
   /**
    * Constant SPECIALSEATING
    */
   SPECIALSEATING("specialSeating"),
   /**
    * Constant PTI23_11
    */
   PTI23_11("pti23_11"),
   /**
    * Constant FREESEATING
    */
   FREESEATING("freeSeating"),
   /**
    * Constant PTI23_12
    */
   PTI23_12("pti23_12"),
   /**
    * Constant RECLININGSEATS
    */
   RECLININGSEATS("recliningSeats"),
   /**
    * Constant PTI23_13
    */
   PTI23_13("pti23_13"),
   /**
    * Constant BABYCOMPARTMENT
    */
   BABYCOMPARTMENT("babyCompartment"),
   /**
    * Constant FAMILYCARRIAGE
    */
   FAMILYCARRIAGE("familyCarriage");

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

   private AccommodationFacilityEnumeration(final java.lang.String value)
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
   public static AccommodationFacilityEnumeration fromValue(
         final java.lang.String value)
   {
      for (AccommodationFacilityEnumeration c : AccommodationFacilityEnumeration
            .values())
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
