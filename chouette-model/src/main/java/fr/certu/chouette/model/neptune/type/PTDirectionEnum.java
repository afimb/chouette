package fr.certu.chouette.model.neptune.type;


/**
 * Directions
 */
public enum PTDirectionEnum implements java.io.Serializable
{
   North, 
   NorthEast, 
   East, 
   SouthEast, 
   South, 
   SouthWest, 
   West, 
   NorthWest, 
   ClockWise, 
   CounterClockWise, 
   /**
    * outbounds
    */
   A, 
   /**
    * inbounds
    */
   R;

}
