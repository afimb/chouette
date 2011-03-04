/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package fr.certu.chouette.struts;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import javax.servlet.ServletContext;
import org.apache.log4j.Logger;
import org.springframework.core.io.Resource;
import org.springframework.web.context.support.ServletContextPropertyPlaceholderConfigurer;
import org.springframework.web.context.support.ServletContextResourceLoader;

/**
 *
 * @author luc
 */
public class ChouettePropertyPlaceholderConfigurer extends ServletContextPropertyPlaceholderConfigurer{

    private ServletContext servletContext;
    private Resource[] locations;

    @Override
    protected void loadProperties(Properties props) throws IOException {
        String chouetteConfig = servletContext.getInitParameter("chouetteConfig");
        if (chouetteConfig != null)
        {
          Logger.getLogger(getClass()).info("Load chouette config from " + chouetteConfig);
          addLocation(getResourceLoader().getResource(chouetteConfig));
        }
        Logger.getLogger(getClass()).info("loadProperties from " + Arrays.toString(locations));
        super.loadProperties(props);
        Logger.getLogger(getClass()).info("loaded : " + props);
    }
    
    protected void addLocation(Resource resource) {
        // TODO Use ArrayUtils
        List<Resource> locationList = new ArrayList(Arrays.asList(locations));
        locationList.add(resource);
        this.locations = locationList.toArray(locations);
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
