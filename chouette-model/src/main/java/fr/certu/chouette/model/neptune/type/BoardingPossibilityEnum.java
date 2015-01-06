package fr.certu.chouette.model.neptune.type;

/**
 * Boarding Possibility values
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
