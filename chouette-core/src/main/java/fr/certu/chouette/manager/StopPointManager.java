package fr.certu.chouette.manager;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;

import fr.certu.chouette.common.ChouetteException;
import fr.certu.chouette.filter.DetailLevelEnum;
import fr.certu.chouette.filter.Filter;
import fr.certu.chouette.model.neptune.ConnectionLink;
import fr.certu.chouette.model.neptune.Facility;
import fr.certu.chouette.model.neptune.Line;
import fr.certu.chouette.model.neptune.PTLink;
import fr.certu.chouette.model.neptune.PTNetwork;
import fr.certu.chouette.model.neptune.StopArea;
import fr.certu.chouette.model.neptune.StopPoint;
import fr.certu.chouette.model.neptune.VehicleJourney;
import fr.certu.chouette.model.neptune.VehicleJourneyAtStop;
import fr.certu.chouette.model.user.User;
import fr.certu.chouette.plugin.report.Report;
import fr.certu.chouette.plugin.validation.ValidationParameters;
import fr.certu.chouette.plugin.validation.ValidationReport;

@SuppressWarnings("unchecked")
public class StopPointManager extends AbstractNeptuneManager<StopPoint> 
{
	Logger logger = Logger.getLogger(StopPoint.class);
	public StopPointManager() 
	{
		super(StopPoint.class);
	}

	@Override
	protected Report propagateValidation(User user, List<StopPoint> beans,
			ValidationParameters parameters,boolean propagate) 
	throws ChouetteException 
	{
		Report globalReport = new ValidationReport();

		// aggregate dependent objects for validation
		Set<StopArea> areas = new HashSet<StopArea>();
		for (StopPoint bean : beans) 
		{
			if (bean.getContainedInStopArea() != null)
			{
				addParentHierarchy(areas,bean.getContainedInStopArea());
			}

		}

		// propagate validation on StopArea
		if (areas.size() > 0)
		{
			Report report = null;
			AbstractNeptuneManager<StopArea> manager = (AbstractNeptuneManager<StopArea>) getManager(StopArea.class);
			if (manager.canValidate())
			{
				report = manager.validate(user, Arrays.asList(areas.toArray(new StopArea[0])), parameters,propagate);
			}
			else
			{
				report = manager.propagateValidation(user, Arrays.asList(areas.toArray(new StopArea[0])), parameters,propagate);
			}
			if (report != null)
			{
				globalReport.addAll(report.getItems());
				globalReport.updateStatus(report.getStatus());
			}
		}


		return globalReport;
	}

	private void addParentHierarchy(Set<StopArea> areas,StopArea area) 
	{
		if (area == null) return;
		if (areas.contains(area)) return;
		areas.add(area);
		addParentHierarchy(areas,area.getParentStopArea());
		if (area.getConnectionLinks() != null)
		{
			for (ConnectionLink link : area.getConnectionLinks())
			{
				StopArea start = link.getStartOfLink();
				StopArea end = link.getEndOfLink();
				if (start != null && !areas.contains(start))
				{
					areas.add(start);
				}
				if (end != null && !areas.contains(end))
				{
					areas.add(end);
				}
			}
		}
		return ;
	}
	@Override
	public void remove(User user,StopPoint stopPoint,boolean propagate) throws ChouetteException
	{
		INeptuneManager<PTLink> ptLinkManager  = (INeptuneManager<PTLink>) getManager(PTLink.class);
		INeptuneManager<VehicleJourney> vjManager = (INeptuneManager<VehicleJourney>) getManager(VehicleJourney.class);
		INeptuneManager<Facility> facilityManager = (INeptuneManager<Facility>) getManager(Facility.class);

		DetailLevelEnum level = DetailLevelEnum.ATTRIBUTE;
		StopPoint next = get(user, Filter.getNewEqualsFilter("position", stopPoint.getPosition() +1), level);
		List<PTLink> ptLinks = ptLinkManager.getAll(user, Filter.getNewOrFilter(
				Filter.getNewEqualsFilter("startOfLink.id", stopPoint.getId()),
				Filter.getNewEqualsFilter("endOfLink.id", stopPoint.getId())), level); 
		if(ptLinks != null && !ptLinks.isEmpty())
		{
			int size = ptLinks.size(); 
			if(size > 1){
				for (PTLink ptLink : ptLinks) 
				{
					if(ptLink.getEndOfLink().getId().equals(stopPoint.getId())){
						ptLink.setEndOfLink(next);
						ptLinkManager.update(user, ptLink);
					}
					else
						ptLinkManager.remove(user, ptLink,propagate);
				}
			}else if(size == 1)
				ptLinkManager.remove(user, ptLinks.get(0),propagate);
		}
		Facility facility = facilityManager.get(user, Filter.getNewEqualsFilter("stopPoint.id", stopPoint.getId()), level);
		if(facility != null)
			facilityManager.remove(user, facility,propagate);
		List<VehicleJourney> vjs = vjManager.getAll(user, Filter.getNewEqualsFilter("route.id",
				stopPoint.getRoute().getId()), level);

		for (VehicleJourney vehicleJourney : vjs) 
		{
			List<VehicleJourneyAtStop> vAtStops = vehicleJourney.getVehicleJourneyAtStops();
			for (int i=0;i< vAtStops.size();i++) 
			{
				VehicleJourneyAtStop vAtStop = vAtStops.get(i);
				if(vAtStop.getStopPoint().equals(stopPoint)) 
				{
					if(vAtStop.isDeparture())
					{
						VehicleJourneyAtStop nextAStop =  i< vAtStops.size() ? vAtStops.get(i +1) : vAtStop;
						nextAStop.setDeparture(true);
					}
					vAtStops.remove(vAtStop);
				}
			}

			vjManager.update(null, vehicleJourney);
		}
		List<StopPoint> stopPoints4Route = getAll(user, Filter.getNewAndFilter(
				Filter.getNewEqualsFilter("route.id", stopPoint.getRoute().getId()),
				Filter.getNewGreaterFilter("position", stopPoint.getPosition())), level);
		for (StopPoint  sp : stopPoints4Route) 
		{
			sp.setPosition(sp.getPosition() - 1);
			update(user, sp);
		}
		super.remove(user, stopPoint,propagate);
	}

	@Override
	protected Logger getLogger() {
		return logger;
	}
	@Override
	public void completeObject(User user, StopPoint stopPoint) {
		PTNetwork ptNetwork = stopPoint.getPtNetwork();
		if(ptNetwork != null)
			stopPoint.setPtNetworkIdShortcut(ptNetwork.getObjectId());
		Line line = stopPoint.getLine();
		if(line != null)
			stopPoint.setLineIdShortcut(line.getObjectId());
	}
}
