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
import fr.certu.chouette.model.neptune.AreaCentroid;
import fr.certu.chouette.model.neptune.ConnectionLink;
import fr.certu.chouette.model.neptune.Facility;
import fr.certu.chouette.model.neptune.NeptuneIdentifiedObject;
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
	private static final Logger logger = Logger.getLogger(StopAreaManager.class);

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
	protected Logger getLogger() 
	{
		return logger;
	}	

	@Override
	public void saveAll(User user, List<StopArea> stopAreas, boolean propagate,boolean fast) throws ChouetteException 
	{
		getLogger().debug("try to save "+stopAreas.size()+" StopAreas");

		List<StopArea> completeStopAreas = new ArrayList<StopArea>();
		List<AccessLink> accessLinks = new ArrayList<AccessLink>();
		List<ConnectionLink> connectionLinks = new ArrayList<ConnectionLink>();
// 		List<RestrictionConstraint> constraints = new ArrayList<RestrictionConstraint>();
		List<Facility> facilities = new ArrayList<Facility>();
		if (propagate)
		{
			List<StopArea> parents = getParents(stopAreas);
			completeStopAreas.addAll(parents);
			mergeCollection(completeStopAreas,stopAreas);

			for (StopArea stopArea : completeStopAreas) 
			{
				mergeCollection(accessLinks, stopArea.getAccessLinks());
				mergeCollection(connectionLinks, stopArea.getConnectionLinks());
// 				mergeCollection(constraints, stopArea.getRestrictionConstraints());
				mergeCollection(facilities, stopArea.getFacilities());
			}
			
			// add targetConnectionLink if not present
			List<StopArea> connected = new ArrayList<StopArea>();
			for (ConnectionLink connectionLink : connectionLinks) 
			{
				if (!completeStopAreas.contains(connectionLink.getStartOfLink()))
					connected.add(connectionLink.getStartOfLink());
				if (!completeStopAreas.contains(connectionLink.getEndOfLink()))
					connected.add(connectionLink.getEndOfLink());
			}
			parents = getParents(connected);
			mergeCollection(completeStopAreas, parents);
			mergeCollection(completeStopAreas, connected);
		}
		else
		{
			completeStopAreas = stopAreas;
		}
		
		super.saveAll(user, completeStopAreas,propagate,fast);

		if(propagate)
		{
			INeptuneManager<AccessLink> accessLinkManager = (INeptuneManager<AccessLink>) getManager(AccessLink.class);
			INeptuneManager<ConnectionLink> connectionLinkManager = (INeptuneManager<ConnectionLink>) getManager(ConnectionLink.class);
//			INeptuneManager<RestrictionConstraint> constraintManager = (INeptuneManager<RestrictionConstraint>) getManager(RestrictionConstraint.class);
			INeptuneManager<Facility> facilityManager = (INeptuneManager<Facility>) getManager(Facility.class);

			if(!accessLinks.isEmpty())
				accessLinkManager.saveAll(user, accessLinks, propagate,fast);
			if(!connectionLinks.isEmpty())
				connectionLinkManager.saveAll(user, connectionLinks, propagate,fast);
//			if(!constraints.isEmpty())
//				constraintManager.saveAll(user, constraints, propagate,fast);	
			if(!facilities.isEmpty())
				facilityManager.saveAll(user, facilities, propagate,fast);
		}
	}

	/**
	 * @param stopAreas
	 * @return
	 */
	private List<StopArea> getParents(List<StopArea> stopAreas) {
		List<StopArea> parents = new ArrayList<StopArea>();
		for (StopArea stopArea : stopAreas) 
		{
			addIfMissingInCollection(parents,stopArea.getParentStopArea());
		}
		if (!parents.isEmpty())
		{
			List<StopArea> granParents = getParents(parents);
			mergeCollection(granParents, parents);
			parents = granParents;
		}
		getLogger().debug("add "+parents.size()+" parents");
		return parents;
	}

	@Override
	public void completeObject(User user, StopArea stopArea)
	throws ChouetteException 
	{
		List<StopPoint> containsPoints = stopArea.getContainedStopPoints();
		if (containsPoints != null && !containsPoints.isEmpty())
		{
			for (StopPoint child : containsPoints) 
			{
				stopArea.addContainedStopId(child.getObjectId());
			}
		}
		List<StopArea> containsAreas = stopArea.getContainedStopAreas();
		if (containsAreas != null && !containsAreas.isEmpty())
		{
			for (StopArea child : containsAreas) 
			{
				stopArea.addContainedStopId(child.getObjectId());
			}
		}
		if (stopArea.getParentStopArea() != null)
		{
			stopArea.getParentStopArea().addContainedStopArea(stopArea);
			completeObject(user, stopArea.getParentStopArea());
		}
		if (stopArea.getAreaCentroid() != null)
		{
			AreaCentroid centroid = stopArea.getAreaCentroid();
			if (centroid.getObjectId() == null)
			{
				String[] ids = stopArea.getObjectId().split(":");
				centroid.setObjectId(ids[0]+":"+NeptuneIdentifiedObject.AREACENTROID_KEY+":"+ids[2]);

			}
			centroid.setContainedInStopArea(stopArea);
			centroid.setContainedInStopAreaId(stopArea.getObjectId());
			centroid.setName(stopArea.getName());
			stopArea.setAreaCentroidId(stopArea.getAreaCentroid().getObjectId());
		}
	}


}
