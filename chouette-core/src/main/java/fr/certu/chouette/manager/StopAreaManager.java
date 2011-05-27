/**
 * Projet CHOUETTE
 *
 * ce projet est sous license libre
 * voir LICENSE.txt pour plus de details
 *
 */

package fr.certu.chouette.manager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;

import fr.certu.chouette.common.ChouetteException;
import fr.certu.chouette.core.CoreException;
import fr.certu.chouette.core.CoreExceptionCode;
import fr.certu.chouette.filter.DetailLevelEnum;
import fr.certu.chouette.filter.Filter;
import fr.certu.chouette.model.neptune.AccessLink;
import fr.certu.chouette.model.neptune.ConnectionLink;
import fr.certu.chouette.model.neptune.Facility;
import fr.certu.chouette.model.neptune.RestrictionConstraint;
import fr.certu.chouette.model.neptune.StopArea;
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
public class StopAreaManager extends AbstractNeptuneManager<StopArea> 
{
	public StopAreaManager() 
	{
		super(StopArea.class);
	}

	@Override
	protected Report propagateValidation(User user, List<StopArea> beans,
			ValidationParameters parameters,boolean propagate) 
	throws ChouetteException 
	{
		Report globalReport = new ValidationReport();

		// aggregate dependent objects for validation
		Set<ConnectionLink> links = new HashSet<ConnectionLink>();
		for (StopArea bean : beans) 
		{
			if (bean.getConnectionLinks() != null)
			{
				links.addAll(bean.getConnectionLinks());
			}

		}

		// propagate validation on ConnectionLink
		if (links.size() > 0)
		{
			Report report = null;
			AbstractNeptuneManager<ConnectionLink> manager = (AbstractNeptuneManager<ConnectionLink>) getManager(ConnectionLink.class);
			if (manager.canValidate())
			{
				report = manager.validate(user, Arrays.asList(links.toArray(new ConnectionLink[0])), parameters,propagate);
			}
			else
			{
				report = manager.propagateValidation(user, Arrays.asList(links.toArray(new ConnectionLink[0])), parameters,propagate);
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
	public void remove(User user,StopArea stopArea,boolean propagate) throws ChouetteException
	{
		DetailLevelEnum level = DetailLevelEnum.ATTRIBUTE;
		INeptuneManager<ConnectionLink> clinkManager = (INeptuneManager<ConnectionLink>) getManager(ConnectionLink.class);
		INeptuneManager<StopPoint> spManager = (INeptuneManager<StopPoint>) getManager(StopPoint.class);
		INeptuneManager<AccessLink> alManager = (INeptuneManager<AccessLink>) getManager(AccessLink.class);
		INeptuneManager<Facility> facilityManager = (INeptuneManager<Facility>) getManager(Facility.class);
		List<StopPoint> stopPoints = spManager.getAll(user, Filter.getNewEqualsFilter("containedInStopArea.id", stopArea.getId()), level);
		if(stopPoints != null && !stopPoints.isEmpty())
			throw new CoreException(CoreExceptionCode.DELETE_IMPOSSIBLE,"can't be deleted because it has a stopPoints");

		List<ConnectionLink> cLinks = clinkManager.getAll(user, Filter.getNewOrFilter(
				Filter.getNewEqualsFilter("startOfLink.id",stopArea.getId()), 
				Filter.getNewEqualsFilter("endOfLink.id", stopArea.getId())),level); 
		if(cLinks != null && !cLinks.isEmpty())
			clinkManager.removeAll(user, cLinks,propagate);
		AccessLink accessLink = alManager.get(user, Filter.getNewEqualsFilter("stopArea.id", stopArea.getId()), level);
		if(accessLink != null)
			alManager.remove(null, accessLink,propagate);
		Facility facility = facilityManager.get(user, Filter.getNewEqualsFilter("stopArea.id", stopArea.getId()), level);
		if(facility != null)
			facilityManager.remove(user, facility,propagate);
		super.remove(user, stopArea,propagate);		
	}

	@Override
	protected Logger getLogger() {
		// TODO Auto-generated method stub
		return null;
	}	

	@Override
	public void saveAll(User user, List<StopArea> stopAreas, boolean propagate) throws ChouetteException 
	{
		super.saveAll(user, stopAreas,propagate);

		if(propagate)
		{
			INeptuneManager<AccessLink> accessLinkManager = (INeptuneManager<AccessLink>) getManager(AccessLink.class);
			INeptuneManager<ConnectionLink> connectionLinkManager = (INeptuneManager<ConnectionLink>) getManager(ConnectionLink.class);
			INeptuneManager<RestrictionConstraint> constraintManager = (INeptuneManager<RestrictionConstraint>) getManager(RestrictionConstraint.class);

			List<AccessLink> accessLinks = new ArrayList<AccessLink>();
			List<ConnectionLink> connectionLinks = new ArrayList<ConnectionLink>();
			List<RestrictionConstraint> constraints = new ArrayList<RestrictionConstraint>();
			for (StopArea stopArea : stopAreas) 
			{
				List<AccessLink> links = stopArea.getAccessLinks();
				if(links != null && !accessLinks.containsAll(links))
					accessLinks.addAll(links);
				if(stopArea.getConnectionLinks()!=null && !connectionLinks.containsAll(stopArea.getConnectionLinks()))
					connectionLinks.addAll(stopArea.getConnectionLinks());

				if(stopArea.getRestrictionConstraints() != null && !constraints.containsAll(stopArea.getRestrictionConstraints()))
					constraints.addAll(stopArea.getRestrictionConstraints());
			}

			if(!accessLinks.isEmpty())
				accessLinkManager.saveAll(user, accessLinks, propagate);
			if(!connectionLinks.isEmpty())
				connectionLinkManager.saveAll(user, connectionLinks, propagate);
			if(!constraints.isEmpty())
				constraintManager.saveAll(user, constraints, propagate);	
		}
	}
}
