/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package fr.certu.chouette.struts;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
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

    @Override
    protected void loadProperties(Properties props) throws IOException {
        Set<String> pathNames = servletContext.getResourcePaths("/");
        String contextName = null;
        if ((pathNames != null) && (pathNames.size() > 0)) {
            contextName = servletContext.getRealPath(pathNames.iterator().next());
            if (contextName.endsWith(File.separator))
                contextName = contextName.substring(0, contextName.length()-1);
            contextName = contextName.substring(0, contextName.lastIndexOf(File.separator));
            contextName = contextName.substring(contextName.lastIndexOf(File.separator)+1);
        }
        if (contextName != null) {
            String chouetteConfigRootPath =  System.getProperty("CHOUETTE_CONFIG_ROOT_DIR");
            if (chouetteConfigRootPath == null)
                chouetteConfigRootPath = servletContext.getInitParameter("chouetteConfigRootPath");
            if (chouetteConfigRootPath != null) {
                if (!chouetteConfigRootPath.endsWith(File.separator))
                    chouetteConfigRootPath += File.separator;
                String chouetteConfig = chouetteConfigRootPath + contextName + File.separator + "chouette.properties";
                if ((new File(chouetteConfig)).exists()) {
                    addLocation(new FileSystemResource(File.separator+chouetteConfig));
                }
            }
        }
        log4jLogger.info("loadProperties from " + Arrays.toString(locations));
        super.loadProperties(props);
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

    public void setServletContext(ServletContext servletContext) {
        this.servletContext = servletContext;
        super.setServletContext(servletContext);
    }

    public void setLocations(Resource[] locations) {
	this.locations = locations;
        super.setLocations(locations);
    }
}
