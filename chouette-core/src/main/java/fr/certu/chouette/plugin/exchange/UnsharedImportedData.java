package fr.certu.chouette.plugin.exchange;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import fr.certu.chouette.plugin.exchange.xml.exception.ExchangeException;
import fr.certu.chouette.plugin.exchange.xml.exception.ExchangeExceptionCode;
import fr.certu.chouette.plugin.exchange.xml.exception.ExchangeRuntimeException;

/**
 * memorize every unsharable objects for chained imports to localize conflict
 * 
 * @author michel
 * 
 */
public class UnsharedImportedData
{
   private Map<String, Map<String, String>> mapMap = new HashMap<String, Map<String, String>>();

   public UnsharedImportedData()
   {
   }

   public void add(Object bean, String parentId) throws ExchangeException
   {
      String objectId = getObjectId(bean);
      Map<String, String> map = mapMap.get(bean.getClass().getName());
      if (map == null)
      {
         map = new HashMap<String, String>();
         mapMap.put(bean.getClass().getName(), map);
      }

      if (map.containsKey(objectId))
      {
         throw new ExchangeException(
               ExchangeExceptionCode.DUPPLICATE_OBJECT_ID, objectId, parentId,
               map.get(objectId));
      }
      map.put(objectId, parentId);
   }

   private String getObjectId(Object bean)
   {
      Class<? extends Object> c = bean.getClass();
      try
      {
         Method m = c.getMethod("getObjectId");
         return (String) m.invoke(bean);
      } catch (Exception e)
      {
         throw new ExchangeRuntimeException(
               ExchangeExceptionCode.UNVALID_OBJECT_TYPE, c.getName());
      }
   }

   public String get(Object bean)
   {
      Map<String, String> map = mapMap.get(bean.getClass());
      return map.get(getObjectId(bean));
   }

}
