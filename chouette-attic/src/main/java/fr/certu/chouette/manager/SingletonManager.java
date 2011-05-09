package fr.certu.chouette.manager;

import java.io.File;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PropertiesLoaderUtils;

public class SingletonManager {

    /**
     * Logger for this class
     */
    private static final Logger logger = Logger.getLogger(SingletonManager.class);

    /*
     * Spring applicationContext
     */
    private static ApplicationContext applicationContext;
    /**
     * Spring properties
     */
    private static Properties props;

    public static ApplicationContext getApplicationContext() {
        return getApplicationContext("classpath:.");
    }

    private static ApplicationContext getApplicationContext(String chemin) {
        if (SingletonManager.applicationContext == null) {

            try {
                logger.debug("Chargement Spring Début");
                logger.error("Chargement Spring Début \""+chemin + File.separator + "chouetteContext.xml"+"\"");
                SingletonManager.applicationContext = new FileSystemXmlApplicationContext(/*chemin + File.separator + */"chouetteContext.xml");
                logger.debug("Chargement Spring Fini");
            } catch (Exception e) {
                logger.error("Erreur d'initialisation de spring", e);
            }
        }
        return applicationContext;
    }

    public static Properties getProps() throws Exception {

        if (null == SingletonManager.props) {
            Resource resource = getApplicationContext().getResource("classpath:./spring.properties");
            if (null == resource) {
                throw new Exception("null spring.properties resources");
            }
            SingletonManager.props = PropertiesLoaderUtils.loadProperties(resource);
        }
        return props;
    }

    public static String getSpringProperty(String propKey) throws Exception {

        String propValue = getProps().getProperty(propKey);
        return propValue;
    }
}
