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

import org.apache.log4j.Logger;

import fr.certu.chouette.common.ChouetteException;
import fr.certu.chouette.filter.DetailLevelEnum;
import fr.certu.chouette.filter.Filter;
import fr.certu.chouette.model.neptune.JourneyPattern;
import fr.certu.chouette.model.neptune.PTLink;
import fr.certu.chouette.model.neptune.Route;
import fr.certu.chouette.model.neptune.StopPoint;
import fr.certu.chouette.model.user.User;
import fr.certu.chouette.plugin.report.Report;
import fr.certu.chouette.plugin.validation.ValidationParameters;
import fr.certu.chouette.plugin.validation.ValidationReport;

/**
 * @author michel
 *
 */
@SuppressWarnings("unchecked")
public class RouteManager extends AbstractNeptuneManager<Route> 
{
	private static final Logger logger = Logger.getLogger(RouteManager.class);

	public RouteManager() 
	{
		super(Route.class);
	}

	@Override
	protected Report propagateValidation(User user, List<Route> beans,
			ValidationParameters parameters,boolean propagate) 
	throws ChouetteException 
	{
		Report globalReport = new ValidationReport();

		// aggregate dependent objects for validation
		List<PTLink> ptLinks = new ArrayList<PTLink>();
		List<JourneyPattern> journeyPatterns = new ArrayList<JourneyPattern>();
		for (Route route : beans) 
		{
			if (route.getPtLinks() != null)
				ptLinks.addAll(route.getPtLinks());
			if (route.getJourneyPatterns() != null)
				journeyPatterns.addAll(route.getJourneyPatterns());

		}

		// propagate validation on PTLinks
		if (ptLinks.size() > 0)
		{
			Report report = null;
			AbstractNeptuneManager<PTLink> manager = (AbstractNeptuneManager<PTLink>) getManager(PTLink.class);
			if (manager.canValidate())
			{
				report = manager.validate(user, ptLinks, parameters,propagate);
			}
			else
			{
				report = manager.propagateValidation(user, ptLinks, parameters,propagate);
			}
			if (report != null)
			{
				globalReport.addAll(report.getItems());
				globalReport.updateStatus(report.getStatus());
			}
		}

		// propagate validation on journey patterns
		if (journeyPatterns.size() > 0)
		{
			Report report = null;
			AbstractNeptuneManager<JourneyPattern> manager = (AbstractNeptuneManager<JourneyPattern>) getManager(JourneyPattern.class);
			if (manager.canValidate())
			{
				report = manager.validate(user, journeyPatterns, parameters,propagate);
			}
			else
			{
				report = manager.propagateValidation(user, journeyPatterns, parameters,propagate);
			}
			if (report != null)
			{
				globalReport.addAll(report.getItems());
				globalReport.updateStatus(report.getStatus());
			}
		}		

		return globalReport;
	}
	@Override
	public void remove(User user,Route route,boolean propagate) throws ChouetteException
	{
		logger.debug("deleting Route = "+route.getObjectId());
		INeptuneManager<JourneyPattern> jpManager = (INeptuneManager<JourneyPattern>) getManager(JourneyPattern.class);
		INeptuneManager<PTLink> ptLinkManager = (INeptuneManager<PTLink>)getManager(PTLink.class);
		INeptuneManager<StopPoint> stopPointManager = (INeptuneManager<StopPoint>)getManager(StopPoint.class);
		Filter filter = Filter.getNewEqualsFilter("route.id", route.getId());
		DetailLevelEnum level = DetailLevelEnum.ATTRIBUTE;
		List<JourneyPattern> jps = jpManager.getAll(user, filter, level);
		if(jps != null && !jps.isEmpty())
			jpManager.removeAll(user, jps,propagate);
		List<PTLink> ptLinks = ptLinkManager.getAll(user, filter, level);
		if(ptLinks != null && !ptLinks.isEmpty())
			ptLinkManager.removeAll(user, ptLinks,propagate);
		List<StopPoint> stopPoints = stopPointManager.getAll(user, filter, level);
		if(stopPoints != null && !stopPoints.isEmpty())
			stopPointManager.removeAll(user, stopPoints,propagate);
		super.remove(user, route,propagate);			
	}

	@Override
	protected Logger getLogger() 
	{
		return logger;
	}

	@Override
	public void saveAll(User user, List<Route> routes, boolean propagate) throws ChouetteException 
	{
		INeptuneManager<JourneyPattern> jpManager = (INeptuneManager<JourneyPattern>) getManager(JourneyPattern.class);
		INeptuneManager<PTLink> ptLinkManager = (INeptuneManager<PTLink>) getManager(PTLink.class);
		INeptuneManager<StopPoint> stopPointManager = (INeptuneManager<StopPoint>) getManager(StopPoint.class);

		super.saveOrUpdateAll(user, routes);

		if(propagate)
		{
			List<JourneyPattern> journeyPatterns = new ArrayList<JourneyPattern>();
			List<StopPoint> stopPoints  = new ArrayList<StopPoint>();
			List<PTLink> links = new ArrayList<PTLink>();
			for (Route route : routes)
			{
				List<JourneyPattern> patterns =route.getJourneyPatterns();
				if(patterns != null && !journeyPatterns.containsAll(patterns))
					journeyPatterns.addAll(route.getJourneyPatterns());
				
				List<StopPoint> points = route.getStopPoints();
				if(points != null && !stopPoints.containsAll(points))
					stopPoints.addAll(route.getStopPoints());
				List<PTLink> ptLinks = route.getPtLinks(); 
				if(ptLinks != null && !links.containsAll(ptLinks))
					links.addAll(route.getPtLinks());
			}
			if(journeyPatterns != null)
				jpManager.saveAll(user, journeyPatterns,propagate);

			if(stopPoints != null)
				stopPointManager.saveAll(user, stopPoints,propagate);
			if(links != null)
				ptLinkManager.saveAll(user, links,propagate);
		}
	}
}
