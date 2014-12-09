package fr.certu.chouette.exchange.gtfs.importer.producer;

import java.sql.Time;

import fr.certu.chouette.exchange.gtfs.refactor.model.GtfsTime;

public abstract class AbstractProducer
{

   /**
    * @param source
    * @return
    */
   public static String getNonEmptyTrimedString(String source)
   {
      if (source == null)
         return null;
      String target = source.trim();
      return (target.length() == 0 ? null : target);
   }

   /**
    * @param gtfsTime
    * @return
    */
   public static Time getTime(GtfsTime gtfsTime)
   {
      if (gtfsTime == null)
         return null;

      Time time = gtfsTime.getTime();
      return time;
   }

}
