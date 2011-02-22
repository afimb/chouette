/**
 * Projet CHOUETTE
 *
 * ce projet est sous license libre
 * voir LICENSE.txt pour plus de details
 *
 */
package fr.certu.chouette.manager;

import java.util.ArrayList;
import java.util.List;

import fr.certu.chouette.common.ChouetteException;
import fr.certu.chouette.model.neptune.Company;
import fr.certu.chouette.model.neptune.Line;
import fr.certu.chouette.model.neptune.NeptuneIdentifiedObject;
import fr.certu.chouette.model.neptune.PTNetwork;
import fr.certu.chouette.model.neptune.Route;
import fr.certu.chouette.model.user.User;
import fr.certu.chouette.plugin.report.Report;
import fr.certu.chouette.plugin.validation.ValidationParameters;

/**
 * 
 */
public class LineManager extends AbstractNeptuneManager<Line>
{

	public LineManager()
	{
		super(Line.class);
	}

	@SuppressWarnings("unchecked")
	@Override
	public Report validate(User user, List<Line> beans,
			ValidationParameters parameters) throws ChouetteException 
	{
	    Report globalReport = super.validate(user, beans, parameters);
	    
	    // aggregate dependent objects for validation
	    List<PTNetwork> networks = new ArrayList<PTNetwork>();
	    List<Company> companies = new ArrayList<Company>();
	    List<Route> routes = new ArrayList<Route>();
	    for (Line line : beans) 
	    {
	    	if (line.getPtNetwork() != null)
	    	   networks.add(line.getPtNetwork());
	    	if (line.getCompany() != null)
		    	   companies.add(line.getCompany());
	    	if (line.getRoutes() != null)
	    	{
	    		routes.addAll(line.getRoutes());
	    	}
		}

	    // propagate validation on networks
	    INeptuneManager<PTNetwork> networkManager = (INeptuneManager<PTNetwork>) getManager(PTNetwork.class);
	    if (networkManager.canValidate())
	    {
	    	Report report = networkManager.validate(user, networks, parameters);
	    	globalReport.addAll(report.getItems());
	    	globalReport.updateStatus(report.getStatus());
	    }
	    
	    // propagate validation on companies
	    INeptuneManager<Company> companyManager = (INeptuneManager<Company>) getManager(Company.class);
	    if (companyManager.canValidate())
	    {
	    	Report report = companyManager.validate(user, companies, parameters);
	    	globalReport.addAll(report.getItems());
	    	globalReport.updateStatus(report.getStatus());
	    }
	    
	    // propagate validation on routes
	    INeptuneManager<Route> routeManager = (INeptuneManager<Route>) getManager(Route.class);
	    if (routeManager.canValidate())
	    {
	    	Report report = routeManager.validate(user, routes, parameters);
	    	globalReport.addAll(report.getItems());
	    	globalReport.updateStatus(report.getStatus());
	    }
	    
	    return globalReport;
	}
	
	
	
	

}
