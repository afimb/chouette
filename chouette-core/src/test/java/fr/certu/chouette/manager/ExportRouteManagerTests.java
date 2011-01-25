package fr.certu.chouette.manager;

import org.testng.annotations.BeforeMethod;

import fr.certu.chouette.model.neptune.Route;

public class ExportRouteManagerTests extends AbstractExportManagerTests<Route> {

    
	@BeforeMethod (alwaysRun=true)
    public void createManager()
    {
		Route bean = new Route();
		bean.setId(Long.valueOf(1));
		bean.setObjectId("TestNG:Route:1");
    	initManager("Route","routeManager",bean);
    }
    
    
    
}
