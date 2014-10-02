/**
 * Projet CHOUETTE
 *
 * ce projet est sous license libre
 * voir LICENSE.txt pour plus de details
 *
 */

package fr.certu.chouette.plugin.exchange;

import java.util.HashMap;
import java.util.Map;

/**
 *
 */
@SuppressWarnings("rawtypes")
public class SharedImportedData
{
   private Map<Class, Map<String, Object>> keyMapMap = new HashMap<Class, Map<String, Object>>();

   public SharedImportedData()
   {
   }

   public void add(Class clazz, String key, Object object)
   {
      Map<String, Object> map = (Map<String, Object>) keyMapMap.get(clazz);
      if (map == null)
      {
         map = new HashMap<String, Object>();
         keyMapMap.put(clazz, map);
      }
      map.put(key, object);

   }

   public Object get(Class clazz, String key)
   {
      Map<String, Object> map = (Map<String, Object>) keyMapMap.get(clazz);
      if (map == null)
         return null;
      return map.get(key);
   }

}
