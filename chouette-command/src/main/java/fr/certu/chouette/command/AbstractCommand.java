package fr.certu.chouette.command;

import java.util.List;
import java.util.Locale;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;
import fr.certu.chouette.manager.INeptuneManager;
import fr.certu.chouette.model.neptune.NeptuneIdentifiedObject;

public abstract class AbstractCommand
{
   public static enum ATTR_CMD
   {
      SET_VALUE, ADD_VALUE, REMOVE_VALUE, SET_REF, ADD_REF, REMOVE_REF
   };

   public static boolean verbose = false;

   public static Locale locale = Locale.getDefault();

   @Getter
   @Setter
   protected Map<String, INeptuneManager<NeptuneIdentifiedObject>> managers;

   /**
    * @param string
    * @return
    */
   public final String getSimpleString(Map<String, List<String>> parameters,
         String key)
   {
      List<String> values = parameters.get(key);
      if (values == null)
         throw new IllegalArgumentException("parameter -" + key
               + " of String type is required");
      if (values.size() > 1)
         throw new IllegalArgumentException("parameter -" + key
               + " of String type must be unique");
      return values.get(0);
   }

   /**
    * @param string
    * @return
    */
   public final String getSimpleString(Map<String, List<String>> parameters,
         String key, String defaultValue)
   {
      List<String> values = parameters.get(key);
      if (values == null)
         return defaultValue;
      if (values.size() > 1)
         throw new IllegalArgumentException("parameter -" + key
               + " of String type must be unique");
      return values.get(0);
   }

   /**
    * @param string
    * @return
    */
   public final boolean getBoolean(Map<String, List<String>> parameters,
         String key)
   {
      List<String> values = parameters.get(key);
      if (values == null)
         return false;
      if (values.size() > 1)
         throw new IllegalArgumentException("parameter -" + key
               + " of boolean type must be unique");
      return Boolean.parseBoolean(values.get(0));
   }

}
