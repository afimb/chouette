package fr.certu.chouette.model.neptune.type;


/**
 * Allowed Longitude/Latitude types
 *
 */
public enum LongLatTypeEnum
{

   /**
    * EPSG:4326, only allowed value
    */
   WGS84(4326), 
   /**
    * prohibited (Neptune unknown value)
    */
   WGS92(0), 
   /**
    * prohibited (Neptune insignificant value)
    */
   Standard(4326);

   private int value;

   private LongLatTypeEnum(final int value)
   {
      this.value = value;
   }

   public int getValue()
   {
      return value;
   }
}
