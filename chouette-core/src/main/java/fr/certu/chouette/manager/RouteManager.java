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
import fr.certu.chouette.model.neptune.PTLink;
import fr.certu.chouette.model.neptune.Route;
import fr.certu.chouette.model.user.User;
import fr.certu.chouette.plugin.report.Report;
import fr.certu.chouette.plugin.validation.ValidationParameters;
import fr.certu.chouette.plugin.validation.ValidationReport;

/**
 * @author michel
 *
 */
public class RouteManager extends AbstractNeptuneManager<Route> 
{
	public RouteManager() 
	{
		super(Route.class);
	}

	@SuppressWarnings("unchecked")
	@Override
	protected Report propagateValidation(User user, List<Route> beans,
			ValidationParameters parameters) 
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
				report = manager.validate(user, ptLinks, parameters);
			}
			else
			{
				report = manager.propagateValidation(user, ptLinks, parameters);
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
				report = manager.validate(user, journeyPatterns, parameters);
			}
			else
			{
				report = manager.propagateValidation(user, journeyPatterns, parameters);
			}
			if (report != null)
			{
				globalReport.addAll(report.getItems());
				globalReport.updateStatus(report.getStatus());
			}
		}		

		return globalReport;
	}


}
