package mobi.chouette.exchange.validation;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import lombok.extern.log4j.Log4j;
import mobi.chouette.model.AccessLink;
import mobi.chouette.model.AccessPoint;
import mobi.chouette.model.ConnectionLink;
import mobi.chouette.model.GroupOfLine;
import mobi.chouette.model.JourneyPattern;
import mobi.chouette.model.Line;
import mobi.chouette.model.NeptuneIdentifiedObject;
import mobi.chouette.model.Network;
import mobi.chouette.model.Route;
import mobi.chouette.model.StopArea;
import mobi.chouette.model.StopPoint;
import mobi.chouette.model.Timetable;
import mobi.chouette.model.VehicleJourney;
import mobi.chouette.model.util.Referential;

@Log4j
public class ValidationDataCollector {

	public void collect(ValidationData collection, Line line) {
		collect(collection, line, new Referential());
	}

	public void collect(ValidationData collection, Line line, Referential cache) {
		collection.clear();
		collection.setCurrentLine(line);
		for (Route route : line.getRoutes()) {
			for (JourneyPattern jp : route.getJourneyPatterns()) {
				for (VehicleJourney vehicleJourney : jp.getVehicleJourneys()) {
					if (vehicleJourney.getTimetables() != null) {
						addAllTimeTables(collection, vehicleJourney.getTimetables(), cache);
					}
					updateId(vehicleJourney, cache.getVehicleJourneys());
					collection.getVehicleJourneys().add(vehicleJourney);
					if (vehicleJourney.getCompany() != null) {
						updateId(vehicleJourney.getCompany(), cache.getCompanies());
						collection.getCompanyIds().add(vehicleJourney.getCompany().getObjectId());
					}
				} // end vehicleJourney loop
				updateId(jp, cache.getJourneyPatterns());
				collection.getJourneyPatterns().add(jp);
			} // end journeyPattern loop
			updateId(route, cache.getRoutes());
			collection.getRoutes().add(route);
			addAllStopPoints(collection, route, cache);
			for (StopPoint stopPoint : route.getStopPoints()) {
				if (stopPoint != null) // protection from missing stopPoint ranks
					collectStopAreas(collection, stopPoint.getContainedInStopArea(), cache);
			}
		}// end route loop
		updateId(line, cache.getLines());
		collection.getLineIds().add(line.getObjectId());
		if (line.getNetwork() != null) {
			updateId(line.getNetwork(), cache.getPtNetworks());
			collection.getNetworkIds().add(line.getNetwork().getObjectId());
			collection.getNetworks().add(line.getNetwork());
		}
		if (line.getCompany() != null) {
			updateId(line.getCompany(), cache.getCompanies());
			collection.getCompanyIds().add(line.getCompany().getObjectId());
			collection.getCompanies().add(line.getCompany());
		}
		if (line.getGroupOfLines() != null) {
			addAllGroupOfLines(collection, line.getGroupOfLines(), cache);
		}
		if (!line.getRoutingConstraints().isEmpty()) {
			addAllRoutingConstraints(collection, line.getRoutingConstraints(), cache);
		}
		collection.getLines().add(cloneLine(line));
		return;
	}

	private void collectStopAreas(ValidationData collection, StopArea stopArea, Referential cache) {
		// add stoparea line collection
		Set<String> lineIds = collection.getLinesOfStopAreas().get(stopArea.getObjectId());
		if (lineIds == null) {
			lineIds = new HashSet<>();
			collection.getLinesOfStopAreas().put(stopArea.getObjectId(), lineIds);
		}
		lineIds.add(collection.getCurrentLine().getObjectId());
		if (collection.getStopAreaIds().contains(stopArea.getObjectId()))
			return;
		updateId(stopArea, cache.getStopAreas());
		collection.getStopAreaIds().add(stopArea.getObjectId());
		collection.getStopAreas().add(stopArea);
		addAllConnectionLinks(collection, stopArea.getConnectionStartLinks(), cache);
		addAllConnectionLinks(collection, stopArea.getConnectionEndLinks(), cache);
		addAllAccessPoints(collection, stopArea.getAccessPoints(), cache);
		addAllAccessLinks(collection, stopArea.getAccessLinks(), cache);
		if (stopArea.getParent() != null)
			collectStopAreas(collection, stopArea.getParent(), cache);
	}

	private void addAllTimeTables(ValidationData collection, Collection<Timetable> data, Referential cache) {
		for (Timetable object : data) {
			updateId(object, cache.getTimetables());
			collection.getTimetableIds().add(object.getObjectId());
			collection.getTimetables().add(object);
		}
	}

	private void addAllGroupOfLines(ValidationData collection, Collection<GroupOfLine> data, Referential cache) {
		for (GroupOfLine object : data) {
			updateId(object, cache.getGroupOfLines());
			collection.getGroupOfLineIds().add(object.getObjectId());
			collection.getGroupOfLines().add(object);
		}

	}

	private void addAllRoutingConstraints(ValidationData collection, Collection<StopArea> data, Referential cache) {
		for (StopArea object : data) {
			updateId(object, cache.getStopAreas());
			collection.getStopAreaIds().add(object.getObjectId());
			collection.getStopAreas().add(object);
		}

	}

	private void addAllStopPoints(ValidationData collection, Route route, Referential cache) {
		Collection<StopPoint> data = route.getStopPoints();
		for (StopPoint object : data) {
			if (object == null) {
				log.error("non continous sequence order in route " + route.getObjectId() + " stopPoints");
			} else {
				updateId(object, cache.getStopPoints());
				collection.getStopPoints().add(object);
			}
		}

	}

	private void addAllConnectionLinks(ValidationData collection, Collection<ConnectionLink> data, Referential cache) {
		for (ConnectionLink object : data) {
			updateId(object, cache.getConnectionLinks());
			collection.getConnectionLinkIds().add(object.getObjectId());
			collection.getConnectionLinks().add(object);
			if (object.getEndOfLink() != null)
				collection.getDummyStopAreas().add(object.getEndOfLink());
			if (object.getStartOfLink() != null)
				collection.getDummyStopAreas().add(object.getStartOfLink());
		}

	}

	private void addAllAccessPoints(ValidationData collection, Collection<AccessPoint> data, Referential cache) {
		for (AccessPoint object : data) {
			updateId(object, cache.getAccessPoints());
			collection.getAccessPointIds().add(object.getObjectId());
			collection.getAccessPoints().add(object);
		}

	}

	private void addAllAccessLinks(ValidationData collection, Collection<AccessLink> data, Referential cache) {
		for (AccessLink object : data) {
			updateId(object, cache.getAccessLinks());
			collection.getAccessLinkIds().add(object.getObjectId());
			collection.getAccessLinks().add(object);
		}

	}

	private void updateId(NeptuneIdentifiedObject object, Map<String, ? extends NeptuneIdentifiedObject> map) {
		if (object.getId() == null) {
			NeptuneIdentifiedObject cached = map.get(object.getObjectId());
			if (cached != null)
				object.setId(cached.getId());
		}
	}

	private Line cloneLine(Line source) {
		// clone line to prepare tests on shared lines
		Line target = new Line();
		target.setId(source.getId());
		target.setObjectId(source.getObjectId());
		target.setObjectVersion(source.getObjectVersion());
		target.setName(source.getName());
		target.setNumber(source.getNumber());
		target.setColor(source.getColor());
		target.setComment(source.getComment());
		target.setFlexibleService(source.getFlexibleService());
		target.setIntUserNeeds(source.getIntUserNeeds());
		target.setMobilityRestrictedSuitable(source.getMobilityRestrictedSuitable());
		target.setPublishedName(source.getPublishedName());
		target.setRegistrationNumber(source.getRegistrationNumber());
		target.setTextColor(source.getTextColor());
		target.setTransportModeName(source.getTransportModeName());
		target.setUrl(source.getUrl());

		// clone used dependencies
		target.setNetwork(cloneNetwork(source.getNetwork()));

		return target;
	}

	private Network cloneNetwork(Network source) {
		if (source == null)
			return null;
		Network target = new Network();
		target.setId(source.getId());
		target.setObjectId(source.getObjectId());
		target.setObjectVersion(source.getObjectVersion());
		target.setName(source.getName());
		return target;
	}

}
