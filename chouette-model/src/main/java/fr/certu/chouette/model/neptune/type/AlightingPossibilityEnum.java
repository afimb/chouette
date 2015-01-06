package fr.certu.chouette.model.neptune.type;

/**
 * Alighting Possibility values
 */
public enum AlightingPossibilityEnum
{
   /**
    * Regularly scheduled drop off
    */
   normal, 
   /**
    * No drop off available
    */
   forbidden, 
   /**
    * Drop off if requested
    */
   request_stop, 
   /**
    * Booking requested for drop off
    */
   is_flexible;

}
