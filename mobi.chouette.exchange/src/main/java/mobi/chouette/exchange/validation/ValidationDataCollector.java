package mobi.chouette.exchange.validation;

import java.util.Collection;
import java.util.Map;

import mobi.chouette.model.AccessLink;
import mobi.chouette.model.AccessPoint;
import mobi.chouette.model.ConnectionLink;
import mobi.chouette.model.GroupOfLine;
import mobi.chouette.model.JourneyPattern;
import mobi.chouette.model.Line;
import mobi.chouette.model.NeptuneIdentifiedObject;
import mobi.chouette.model.Route;
import mobi.chouette.model.StopArea;
import mobi.chouette.model.StopPoint;
import mobi.chouette.model.Timetable;
import mobi.chouette.model.VehicleJourney;
import mobi.chouette.model.util.Referential;

public class ValidationDataCollector {
	
	
	
	public void collect(ValidationData collection, Line line) 
	{
		collect(collection,line,new Referential());
	}
	
	public void collect(ValidationData collection, Line line, Referential cache)
	{
		collection.clear();
		for (Route route : line.getRoutes()) {
			for (JourneyPattern jp : route.getJourneyPatterns()) {
				for (VehicleJourney vehicleJourney : jp.getVehicleJourneys()) {
					if (vehicleJourney.getTimetables() != null) {
						addAllTimeTables(collection, vehicleJourney.getTimetables(),cache);
					}
					updateId(vehicleJourney, cache.getVehicleJourneys());
					collection.getVehicleJourneys().add( vehicleJourney);
					if (vehicleJourney.getCompany() != null) {
						updateId(vehicleJourney.getCompany(), cache.getCompanies());
						collection.getCompanyIds().add(vehicleJourney.getCompany().getObjectId());
					}
				} // end vehiclejourney loop
				updateId(jp, cache.getJourneyPatterns());
				collection.getJourneyPatterns().add( jp);
			} // end journeyPattern loop
			updateId(route, cache.getRoutes());
			collection.getRoutes().add(route);
			addAllStopPoints(collection, route.getStopPoints(),cache);
			for (StopPoint stopPoint : route.getStopPoints()) {
				collectStopAreas(collection, stopPoint.getContainedInStopArea(),cache);
			}
		}// end route loop
		updateId(line, cache.getLines());
		collection.getLineIds().add(line.getObjectId());
		updateId(line.getNetwork(), cache.getPtNetworks());
		collection.getNetworkIds().add(line.getNetwork().getObjectId());
		if (line.getCompany() != null) {
			updateId(line.getCompany(), cache.getCompanies());
			collection.getCompanyIds().add(line.getCompany().getObjectId());
		}
		if (line.getGroupOfLines() != null) {
			addAllGroupOfLines(collection, line.getGroupOfLines(),cache);
		}
		if (!line.getRoutingConstraints().isEmpty()) {
			addAllRoutingConstraints(collection, line.getRoutingConstraints(),cache);
		}
		collection.setCurrentLine(line);
		collection.getLineIds().add(line.getObjectId());
		return;
	}

	private void collectStopAreas(ValidationData collection, StopArea stopArea, Referential cache) {
		if (collection.getStopAreaIds().contains(stopArea.getObjectId()))
			return;
		updateId(stopArea, cache.getStopAreas());
		collection.getStopAreaIds().add(stopArea.getObjectId());
		addAllConnectionLinks(collection, stopArea.getConnectionStartLinks(),cache);
		addAllConnectionLinks(collection, stopArea.getConnectionEndLinks(),cache);
		addAllAccessPoints(collection, stopArea.getAccessPoints(),cache);
		addAllAccessLinks(collection, stopArea.getAccessLinks(),cache);
		if (stopArea.getParent() != null)
			collectStopAreas(collection, stopArea.getParent(),cache);
	}

	private void addAllTimeTables(ValidationData collection, Collection<Timetable> data, Referential cache) {
		for (Timetable object : data) {
			updateId(object, cache.getTimetables());
			collection.getTimetableIds().add(object.getObjectId());
		}
	}

	private void addAllGroupOfLines(ValidationData collection, Collection<GroupOfLine> data, Referential cache) {
		for (GroupOfLine object : data) {
			updateId(object, cache.getGroupOfLines());
			collection.getGroupOfLineIds().add(object.getObjectId());
		}

	}

	private void addAllRoutingConstraints(ValidationData collection, Collection<StopArea> data, Referential cache) {
		for (StopArea object : data) {
			updateId(object, cache.getStopAreas());
			collection.getStopAreaIds().add(object.getObjectId());
		}

	}

	private void addAllStopPoints(ValidationData collection, Collection<StopPoint> data, Referential cache) {
		for (StopPoint object : data) {
			updateId(object, cache.getStopPoints());
			collection.getStopPoints().add( object);
		}

	}

	private void addAllConnectionLinks(ValidationData collection, Collection<ConnectionLink> data, Referential cache) {
		for (ConnectionLink object : data) {
			updateId(object, cache.getConnectionLinks());
			collection.getConnectionLinkIds().add(object.getObjectId());
		}

	}

	private void addAllAccessPoints(ValidationData collection, Collection<AccessPoint> data, Referential cache) {
		for (AccessPoint object : data) {
			updateId(object, cache.getAccessPoints());
			collection.getAccessPointIds().add(object.getObjectId());
		}

	}

	private void addAllAccessLinks(ValidationData collection, Collection<AccessLink> data, Referential cache) {
		for (AccessLink object : data) {
			updateId(object, cache.getAccessLinks());
			collection.getAccessLinkIds().add(object.getObjectId());
		}

	}

	private void updateId(NeptuneIdentifiedObject object,Map<String,? extends NeptuneIdentifiedObject> map)
	{
		if (object.getId() == null)
		{
			NeptuneIdentifiedObject cached = map.get(object.getObjectId());
			if (cached != null) object.setId(cached.getId());
		}
	}
	
}
