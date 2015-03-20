package mobi.chouette.model.type;

/**
 * Boarding Possibility values
 * 
 * @since 2.5.2
 * 
 */
public enum BoardingPossibilityEnum
{
   /**
    * Regularly scheduled pickup
    */
   normal, 
   /**
    * No pickup available
    */
   forbidden, 
   /**
    * Pickup if requested
    */
   request_stop, 
   /**
    * Booking requested for pickup
    */
   is_flexible;

}
