package fr.certu.chouette.exchange.xml.neptune.importer.producer;

import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

public class JAXBContextCache {

   private Map<String, JAXBContext> jaxbContextCache = new HashMap<>();

   public JAXBContextCache() {
   }

   public JAXBContext getContext(String contextPath) throws JAXBException {
      synchronized (jaxbContextCache) {
         JAXBContext ret = jaxbContextCache.get(contextPath);
         if (ret == null) {
            ret = JAXBContext.newInstance(contextPath);
            jaxbContextCache.put(contextPath, ret);
         }
         return ret;
      }
   }
}
