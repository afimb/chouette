/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package fr.certu.chouette.struts;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;
import java.util.Set;
import javax.servlet.ServletContext;
import org.apache.log4j.Logger;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.web.context.support.ServletContextPropertyPlaceholderConfigurer;
import org.springframework.web.context.support.ServletContextResourceLoader;

/**
 *
 * @author luc
 */
public class ChouettePropertyPlaceholderConfigurer extends ServletContextPropertyPlaceholderConfigurer {

    private static final Logger log4jLogger = Logger.getLogger(ChouettePropertyPlaceholderConfigurer.class);
    private ServletContext servletContext;
    private Resource[] locations;
    private String chouette_env="";

    @Override
    protected void loadProperties(Properties props) throws IOException {
        Set<String> pathNames = servletContext.getResourcePaths("/");
        String contextName = null;
        if (pathNames != null)
            for (String pathName : pathNames) {
                String realPath = servletContext.getRealPath(pathName);
                if (contextName == null) {
                    contextName = realPath;
                    if (contextName.endsWith(File.separator))
                        contextName = contextName.substring(0, contextName.length()-1);
                    contextName = contextName.substring(0, contextName.lastIndexOf(File.separator));
                    contextName = contextName.substring(contextName.lastIndexOf(File.separator)+1);
                }
                if (realPath.endsWith("WEB-INF"))
                    chouette_env = realPath+"/classes/geoportail/";
                else if (realPath.endsWith("WEB-INF/"))
                    chouette_env = realPath+"classes/geoportail/";
                else if (realPath.endsWith("WEB-INF"))
                    chouette_env = realPath+"\\classes\\geoportail\\";
                else if (realPath.endsWith("WEB-INF\\"))
                    chouette_env = realPath+"classes\\geoportail\\";
            }
        if (contextName != null) {
            String chouetteConfigRootPath =  System.getProperty("CHOUETTE_CONFIG_ROOT_DIR");
            if (chouetteConfigRootPath == null)
                if ("linux".equalsIgnoreCase(System.getProperty("os.name")))
                    chouetteConfigRootPath = servletContext.getInitParameter("chouetteLinuxConfigRootPath");
                else if ("windows".equalsIgnoreCase(System.getProperty("os.name")))
                    chouetteConfigRootPath = servletContext.getInitParameter("chouetteWindowsConfigRootPath");
            if (chouetteConfigRootPath != null) {
                if (!chouetteConfigRootPath.endsWith(File.separator))
                    chouetteConfigRootPath += File.separator;
                String chouetteConfig = chouetteConfigRootPath + contextName + File.separator + "chouette.properties";
                if ((new File(chouetteConfig)).exists())
                    addLocation(new FileSystemResource(File.separator+chouetteConfig));
            }
        }
        log4jLogger.info("loadProperties from " + Arrays.toString(locations));
        super.loadProperties(props);
        getEnvProperties(props.getProperty("chouette.env"));
        log4jLogger.info("loaded : " + props);
    }
    
    protected void addLocation(Resource resource) {
        // TODO Use ArrayUtils
        List<Resource> locationList = new ArrayList(Arrays.asList(locations));
        locationList.add(resource);
        this.locations = locationList.toArray(locations);
        this.setLocations(locations);
    }

    protected ServletContextResourceLoader getResourceLoader() {
        return new ServletContextResourceLoader(servletContext);
    }

    @Override
    public void setServletContext(ServletContext servletContext) {
        this.servletContext = servletContext;
        super.setServletContext(servletContext);
    }

    @Override
    public void setLocations(Resource[] locations) {
	this.locations = locations;
        super.setLocations(locations);
    }

    private void getEnvProperties(String fileName) {
        ResourceBundle resourceBundle = null;
       try {
            if (fileName == null)
                resourceBundle = new PropertyResourceBundle(new InputStreamReader(new FileInputStream(chouette_env+"chouette_env.properties"), "UTF-8"));
            else
                resourceBundle = new PropertyResourceBundle(new InputStreamReader(new FileInputStream(fileName), "UTF-8"));
        }
        catch (FileNotFoundException ex) {
            log4jLogger.error("File not found "+fileName);
        }
        catch(IOException e) {
            log4jLogger.error("IO Exception while reading "+fileName);
        }
        Enumeration<String> keys = resourceBundle.getKeys();
        while (keys.hasMoreElements()) {
            String key = keys.nextElement();
            String value = resourceBundle.getString(key);
            if ((value.indexOf('/') >= 0) || (value.indexOf('\\') >= 0))
                value = chouette_env + value;
            System.setProperty(key, value);
        }
    }
}
