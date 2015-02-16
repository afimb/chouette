package mobi.chouette.exchange.neptune.model.facility;

import java.io.Serializable;

public enum PassengerCommsFacilityEnumeration implements Serializable
{

   // ------------------/
   // - Enum Constants -/
   // ------------------/

   /**
    * Constant UNKNOWN
    */
   UNKNOWN("unknown"),
   /**
    * Constant FACCOMMS_1
    */
   FACCOMMS_1("faccomms_1"),
   /**
    * Constant PASSENGERWIFI
    */
   PASSENGERWIFI("passengerWifi"),
   /**
    * Constant PTI23_21
    */
   PTI23_21("pti23_21"),
   /**
    * Constant TELEPHONE
    */
   TELEPHONE("telephone"),
   /**
    * Constant PTI23_14
    */
   PTI23_14("pti23_14"),
   /**
    * Constant AUDIOSERVICES
    */
   AUDIOSERVICES("audioServices"),
   /**
    * Constant PTI23_15
    */
   PTI23_15("pti23_15"),
   /**
    * Constant VIDEOSERVICES
    */
   VIDEOSERVICES("videoServices"),
   /**
    * Constant PTI23_25
    */
   PTI23_25("pti23_25"),
   /**
    * Constant BUSINESSSERVICES
    */
   BUSINESSSERVICES("businessServices"),
   /**
    * Constant INTERNET
    */
   INTERNET("internet"),
   /**
    * Constant POSTOFFICE
    */
   POSTOFFICE("postoffice"),
   /**
    * Constant LETTERBOX
    */
   LETTERBOX("letterbox");

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

   private PassengerCommsFacilityEnumeration(final java.lang.String value)
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
   public static PassengerCommsFacilityEnumeration fromValue(
         final java.lang.String value)
   {
      for (PassengerCommsFacilityEnumeration c : PassengerCommsFacilityEnumeration
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
