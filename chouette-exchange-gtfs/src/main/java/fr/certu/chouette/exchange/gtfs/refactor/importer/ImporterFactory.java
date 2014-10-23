package fr.certu.chouette.exchange.gtfs.refactor.importer;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public abstract class ImporterFactory
{

   public static Map<String,ImporterFactory> factories = new HashMap<String,ImporterFactory>();

   protected abstract Importer create(String path) throws IOException;

   public static final Importer build(String path, String clazz) throws ClassNotFoundException, IOException
         
   {
      if (!factories.containsKey(clazz))
      {
         Class.forName(clazz);
         if (!factories.containsKey(clazz))
            throw new ClassNotFoundException(clazz);
      }
      return ((ImporterFactory) factories.get(clazz)).create(path);
   }
}
