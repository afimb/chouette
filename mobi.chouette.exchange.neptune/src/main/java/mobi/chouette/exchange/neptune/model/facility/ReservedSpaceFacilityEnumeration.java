package mobi.chouette.exchange.neptune.model.facility;

import java.io.Serializable;

public enum ReservedSpaceFacilityEnumeration implements Serializable
{

   // ------------------/
   // - Enum Constants -/
   // ------------------/

   /**
    * Constant UNKNOWN
    */
   UNKNOWN("unknown"),
   /**
    * Constant LOUNGE
    */
   LOUNGE("lounge"),
   /**
    * Constant HALL
    */
   HALL("hall"),
   /**
    * Constant MEETINGPOINT
    */
   MEETINGPOINT("meetingpoint"),
   /**
    * Constant GROUPPOINT
    */
   GROUPPOINT("groupPoint"),
   /**
    * Constant RECEPTION
    */
   RECEPTION("reception"),
   /**
    * Constant SHELTER
    */
   SHELTER("shelter"),
   /**
    * Constant SEATS
    */
   SEATS("seats");

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

   private ReservedSpaceFacilityEnumeration(final java.lang.String value)
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
   public static ReservedSpaceFacilityEnumeration fromValue(
         final java.lang.String value)
   {
      for (ReservedSpaceFacilityEnumeration c : ReservedSpaceFacilityEnumeration
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
