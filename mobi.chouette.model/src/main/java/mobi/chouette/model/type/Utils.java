package mobi.chouette.model.type;

import java.util.EnumSet;

/**
 * tools on enums
 */
public class Utils
{

   /**
    * find enum value ignoring case
    * 
    * @param text enum value name
    * @param cls enum class name
    * @return enum found (null if not found)
    */
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
