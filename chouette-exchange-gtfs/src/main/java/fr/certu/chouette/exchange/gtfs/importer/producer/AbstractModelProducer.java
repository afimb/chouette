package fr.certu.chouette.exchange.gtfs.importer.producer;

import java.net.URL;
import java.util.TimeZone;

import lombok.Getter;
import lombok.Setter;

import org.apache.log4j.Logger;

import fr.certu.chouette.exchange.gtfs.refactor.model.GtfsObject;
import fr.certu.chouette.model.neptune.NeptuneObject;

public abstract class AbstractModelProducer<T extends NeptuneObject, U extends GtfsObject> extends AbstractProducer implements IModelProducer<T, U>
{

   @Getter
   @Setter
   private static String prefix;
   
   
   @Getter
   @Setter
   private static String incrementalPrefix = "";
   
   private static int nullIdCount = 0;

   public static String composeObjectId(String type, String id, Logger logger)
   {
      if (id == null)
      {
         logger.error("id null for " + type);
         id = "NULL_" + nullIdCount;
         nullIdCount++;
      }
      String[] tokens = id.split("\\.");
      if (tokens.length == 2)
      {
         // id should be produced by Chouette
         return tokens[0].trim().replaceAll("[^a-zA-Z_0-9]", "_") + ":" + type + ":" + tokens[1].trim().replaceAll("[^a-zA-Z_0-9\\-]", "_");
      }
      return prefix + ":" + type + ":" + id.trim().replaceAll("[^a-zA-Z_0-9\\-]", "_");
   }

   public static String composeIncrementalObjectId(String type, String id, Logger logger)
   {
      if (id == null)
      {
         logger.error("id null for " + type);
         id = "NULL_" + nullIdCount;
         nullIdCount++;
      }
      String[] tokens = id.split("\\.");
      if (tokens.length == 2)
      {
         // id should be produced by Chouette
         return tokens[0].trim().replaceAll("[^a-zA-Z_0-9]", "_") + ":" + type + ":" + incrementalPrefix + tokens[1].trim().replaceAll("[^a-zA-Z_0-9\\-]", "_");
      }
      return prefix + ":" + type + ":" + incrementalPrefix + id.trim().replaceAll("[^a-zA-Z_0-9\\-]", "_");
   }


   public static String toString(URL url)
   {
      if (url == null)
         return null;
      return url.toString();
   }

   public static String toString(TimeZone tz)
   {
      if (tz == null)
         return null;
      return tz.getID();
   }

}
