package fr.certu.chouette.model.neptune.type;

public enum LongLatTypeEnum
{

   WGS84(4326),
   WGS92(0),
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
