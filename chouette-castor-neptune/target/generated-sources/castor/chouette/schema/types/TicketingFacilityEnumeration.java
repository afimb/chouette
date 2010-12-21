/*
 * This class was automatically generated with 
 * <a href="http://www.castor.org">Castor 1.3.0.1</a>, using an XML
 * Schema.
 * $Id$
 */

package chouette.schema.types;

/**
 * Values for Ticketing Facility
 * 
 * @version $Revision$ $Date$
 */
@SuppressWarnings("serial")
public enum TicketingFacilityEnumeration implements java.io.Serializable {


      //------------------/
     //- Enum Constants -/
    //------------------/

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

      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    /**
     * Field value.
     */
    private final java.lang.String value;


      //----------------/
     //- Constructors -/
    //----------------/

    private TicketingFacilityEnumeration(final java.lang.String value) {
        this.value = value;
    }


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * Method fromValue.
     * 
     * @param value
     * @return the constant for this value
     */
    public static chouette.schema.types.TicketingFacilityEnumeration fromValue(
            final java.lang.String value) {
        for (TicketingFacilityEnumeration c: TicketingFacilityEnumeration.values()) {
            if (c.value.equals(value)) {
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
    public void setValue(
            final java.lang.String value) {
    }

    /**
     * Method toString.
     * 
     * @return the value of this constant
     */
    public java.lang.String toString(
    ) {
        return this.value;
    }

    /**
     * Method value.
     * 
     * @return the value of this constant
     */
    public java.lang.String value(
    ) {
        return this.value;
    }

}
