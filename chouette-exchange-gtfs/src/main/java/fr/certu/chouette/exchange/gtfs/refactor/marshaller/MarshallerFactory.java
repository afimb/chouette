package fr.certu.chouette.exchange.gtfs.refactor.marshaller;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public abstract class MarshallerFactory
{

   public static Map factories = new HashMap();

   protected abstract GtfsMarshaller create(String path) throws IOException;

   public static final GtfsMarshaller build(String path)
         throws ClassNotFoundException, IOException
   {
      String clazz = getClassName(path);
      if (!factories.containsKey(clazz))
      {
         Class.forName(clazz);
         if (!factories.containsKey(clazz))
            throw new ClassNotFoundException(clazz);
      }
      return ((MarshallerFactory) factories.get(clazz)).create(path);
   }

   private static String getClassName(String path)
   {
      File fd = new File(path);
      String name = fd.getName();
      name = name.substring(0, name.lastIndexOf('.'));
      String array[] = name.split("_");
      StringBuffer result = new StringBuffer(MarshallerFactory.class
            .getPackage().getName());
      result.append('.');
      for (int i = 0; i < array.length; i++)
      {
         char[] buffer = new char[array[i].length()];
         array[i].toLowerCase().getChars(0, array[i].length(), buffer, 0);
         ;
         buffer[0] = Character.toUpperCase(buffer[0]);
         result.append(buffer);
      }
      result.append("Marshaller");
      return result.toString();
   }
}
