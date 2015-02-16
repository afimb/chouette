package mobi.chouette.exchange.neptune.model.facility;

import java.io.Serializable;

public enum ParkingFacilityEnumeration implements Serializable
{

   // ------------------/
   // - Enum Constants -/
   // ------------------/

   /**
    * Constant UNKNOWN
    */
   UNKNOWN("unknown"),
   /**
    * Constant CARPARK
    */
   CARPARK("carPark"),
   /**
    * Constant PARKANDRIDEPARK
    */
   PARKANDRIDEPARK("parkAndRidePark"),
   /**
    * Constant MOTORCYCLEPARK
    */
   MOTORCYCLEPARK("motorcyclePark"),
   /**
    * Constant CYCLEPARK
    */
   CYCLEPARK("cyclePark"),
   /**
    * Constant RENTALCARPARK
    */
   RENTALCARPARK("rentalCarPark"),
   /**
    * Constant COACHPARK
    */
   COACHPARK("coachPark");

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

   private ParkingFacilityEnumeration(final java.lang.String value)
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
   public static ParkingFacilityEnumeration fromValue(
         final java.lang.String value)
   {
      for (ParkingFacilityEnumeration c : ParkingFacilityEnumeration.values())
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
