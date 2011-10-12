package fr.certu.chouette.exchange.gtfs.model;

import java.text.SimpleDateFormat;

import lombok.Getter;
import lombok.Setter;

public abstract class GtfsBean 
{

   public static SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");


   @Getter @Setter int fileLineNumber = 0; 

   public abstract String getCSVLine();
   
   /* (non-Javadoc)
    * @see java.lang.Object#equals(java.lang.Object)
    */
   @Override
   public boolean equals(Object obj) 
   {
      return super.equals(obj);
   }

   /* (non-Javadoc)
    * @see java.lang.Object#hashCode()
    */
   @Override
   public int hashCode() 
   {
      return super.hashCode();
   }


}
