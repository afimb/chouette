package fr.certu.chouette.model.neptune.type;

import java.util.EnumSet;

public class Utils
{

   public static <T extends Enum<T>> T valueOfIgnoreCase(String text,
         Class<T> cls)
   {
      T result = null;
      for (T item : EnumSet.allOf(cls))
      {
         if (item.name().equalsIgnoreCase(text))
         {
            result = item;
            break;
         }
      }
      return result;
   }
}
