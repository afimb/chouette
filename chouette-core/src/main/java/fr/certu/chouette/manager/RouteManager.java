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
import fr.certu.chouette.model.neptune.JourneyPattern;
import fr.certu.chouette.model.neptune.Line;
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
	public void remove(User user,Route route) throws ChouetteException{
		INeptuneManager<JourneyPattern> jpManager = (INeptuneManager<JourneyPattern>) getManager(JourneyPattern.class);
		INeptuneManager<PTLink> ptLinkManager = (INeptuneManager<PTLink>)getManager(PTLink.class);
		INeptuneManager<StopPoint> stopPointManager = (INeptuneManager<StopPoint>)getManager(StopPoint.class);
		if(route.getJourneyPatterns() != null)
			jpManager.removeAll(null, route.getJourneyPatterns());
		if(route.getPtLinks() != null)
			ptLinkManager.removeAll(null, route.getPtLinks());
		if(route.getStopPoints() != null)
			stopPointManager.removeAll(null, route.getStopPoints());
		super.remove(null, route);			
	}
}
