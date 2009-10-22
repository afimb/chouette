package fr.certu.chouette.manager;

import java.io.File;

import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

public class SingletonManager
{
	/**
	 * Logger for this class
	 */
	private static final Logger logger = Logger
			.getLogger(SingletonManager.class);

   private static ApplicationContext applicationContext;

   public static ApplicationContext getApplicationContext()
   {
      return getApplicationContext( "classpath:.");
   }
   private static ApplicationContext getApplicationContext( String chemin)
   {
      if (SingletonManager.applicationContext == null)
      {
         try
         {
        	 logger.debug( "Chargement Spring Début");
            SingletonManager.applicationContext = new FileSystemXmlApplicationContext(
                  chemin+File.separator+"applicationContext.xml");
            logger.debug( "Chargement Spring Fini");
         }
         catch (Exception e)
         {
        	 logger.error("Erreur d'initialisation de spring", e);
         }
      }
      return applicationContext;
   }

}
