package mobi.chouette.exchange.neptune.model.facility;

import java.io.Serializable;

public enum TicketingFacilityEnumeration implements Serializable
{

   // ------------------/
   // - Enum Constants -/
   // ------------------/

   /**
    * Constant UNKNOWN
    */
   UNKNOWN("unknown"),
   /**
    * Constant TICKETMACHINES
    */
   TICKETMACHINES("ticketMachines"),
   /**
    * Constant TICKETOFFICE
    */
   TICKETOFFICE("ticketOffice"),
   /**
    * Constant TICKETONDEMANDMACHINES
    */
   TICKETONDEMANDMACHINES("ticketOnDemandMachines"),
   /**
    * Constant TICKETSALES
    */
   TICKETSALES("ticketSales"),
   /**
    * Constant MOBILETICKETING
    */
   MOBILETICKETING("mobileTicketing"),
   /**
    * Constant TICKETCOLLECTION
    */
   TICKETCOLLECTION("ticketCollection"),
   /**
    * Constant CENTRALRESERVATIONS
    */
   CENTRALRESERVATIONS("centralReservations"),
   /**
    * Constant LOCALTICKETS
    */
   LOCALTICKETS("localTickets"),
   /**
    * Constant NATIONALTICKETS
    */
   NATIONALTICKETS("nationalTickets"),
   /**
    * Constant INTERNATIONALTICKETS
    */
   INTERNATIONALTICKETS("internationalTickets");

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

   private TicketingFacilityEnumeration(final java.lang.String value)
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
   public static TicketingFacilityEnumeration fromValue(
         final java.lang.String value)
   {
      for (TicketingFacilityEnumeration c : TicketingFacilityEnumeration
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
