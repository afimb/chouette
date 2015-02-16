package mobi.chouette.exchange.neptune.model.facility;

import java.io.Serializable;

public enum PassengerInformationFacilityEnumeration implements Serializable
{
   // ------------------/
   // - Enum Constants -/
   // ------------------/

   /**
    * Constant UNKNOWN
    */
   UNKNOWN("unknown"),
   /**
    * Constant NEXTSTOPINDICATOR
    */
   NEXTSTOPINDICATOR("nextStopIndicator"),
   /**
    * Constant STOPANNOUNCEMENTS
    */
   STOPANNOUNCEMENTS("stopAnnouncements"),
   /**
    * Constant PASSENGERINFORMATIONDISPLAY
    */
   PASSENGERINFORMATIONDISPLAY("passengerInformationDisplay"),
   /**
    * Constant AUDIOINFORMATION
    */
   AUDIOINFORMATION("audioInformation"),
   /**
    * Constant VISUALINFORMATION
    */
   VISUALINFORMATION("visualInformation"),
   /**
    * Constant TACTILEPLATFORMEDGES
    */
   TACTILEPLATFORMEDGES("tactilePlatformEdges"),
   /**
    * Constant TACTILEINFORMATION
    */
   TACTILEINFORMATION("tactileInformation"),
   /**
    * Constant WALKINGGUIDANCE
    */
   WALKINGGUIDANCE("walkingGuidance"),
   /**
    * Constant JOURNEYPLANNING
    */
   JOURNEYPLANNING("journeyPlanning"),
   /**
    * Constant LOSTFOUND
    */
   LOSTFOUND("lostFound"),
   /**
    * Constant INFORMATIONDESK
    */
   INFORMATIONDESK("informationDesk"),
   /**
    * Constant INTERACTIVEKIOSK_DISPLAY
    */
   INTERACTIVEKIOSK_DISPLAY("interactiveKiosk-Display"),
   /**
    * Constant PRINTEDPUBLICNOTICE
    */
   PRINTEDPUBLICNOTICE("printedPublicNotice");

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

   private PassengerInformationFacilityEnumeration(final java.lang.String value)
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
   public static PassengerInformationFacilityEnumeration fromValue(
         final java.lang.String value)
   {
      for (PassengerInformationFacilityEnumeration c : PassengerInformationFacilityEnumeration
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
