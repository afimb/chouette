package mobi.chouette.model.type;

/**
 * Boarding Alighting Possibility values
 */
public enum BoardingAlightingPossibilityEnum
{

   /**
    * Traveler can board and alight (default value)
    */
   BoardAndAlight, 
   /**
    * Traveler can only alight
    */
   AlightOnly, 
   /**
    * Traveler can only board
    */
   BoardOnly, 
   /**
    * Traveler can not alight nor board
    */
   NeitherBoardOrAlight, 
   /**
    * Traveler can board and alight only on request
    */
   BoardAndAlightOnRequest,
   /**
    * Traveler can alight only on request
    */
   AlightOnRequest, 
   /**
    * Traveler can board only on request
    */
   BoardOnRequest;

}
