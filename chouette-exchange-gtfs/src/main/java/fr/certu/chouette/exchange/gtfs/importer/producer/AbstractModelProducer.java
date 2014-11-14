package fr.certu.chouette.exchange.gtfs.importer.producer;

import java.net.URL;

import org.apache.log4j.Logger;

import fr.certu.chouette.exchange.gtfs.refactor.model.GtfsObject;
import fr.certu.chouette.model.neptune.NeptuneObject;

public abstract class AbstractModelProducer<T extends NeptuneObject, U extends GtfsObject> extends AbstractProducer implements IModelProducer<T, U>
{

   private static String prefix;
   private static String incremental = "";
   private static int nullIdCount = 0;

   public String composeObjectId(String type, String id, Logger logger)
   {
      if (id == null)
      {
         logger.error("id null for " + type);
         id = "NULL_" + nullIdCount;
         nullIdCount++;
      }
      String[] tokens = id.split(".");
      if (tokens.length == 2)
      {
         // id should be produced by Chouette
         return tokens[0].trim().replaceAll("[^a-zA-Z_0-9]", "_") + ":" + type + ":" + tokens[1].trim().replaceAll("[^a-zA-Z_0-9\\-]", "_");
      }
      return prefix + ":" + type + ":" + id.trim().replaceAll("[^a-zA-Z_0-9\\-]", "_");
   }

   public String composeIncrementalObjectId(String type, String id, Logger logger)
   {
      if (id == null)
      {
         logger.error("id null for " + type);
         id = "NULL_" + nullIdCount;
         nullIdCount++;
      }
      String[] tokens = id.split(".");
      if (tokens.length == 2)
      {
         // id should be produced by Chouette
         return tokens[0].trim().replaceAll("[^a-zA-Z_0-9]", "_") + ":" + type + ":" + incremental + tokens[1].trim().replaceAll("[^a-zA-Z_0-9\\-]", "_");
      }
      return prefix + ":" + type + ":" + incremental + id.trim().replaceAll("[^a-zA-Z_0-9\\-]", "_");
   }

   public static void setIncrementalPrefix(String value)
   {
      incremental = value;
   }

   public static String getIncrementalPrefix()
   {
      return incremental;
   }

   public static void setPrefix(String value)
   {
      prefix = value;
   }

   public static String getPrefix()
   {
      return prefix;
   }

   public static String toString(URL url)
   {
      if (url == null)
         return null;
      return url.getPath();
   }

}
