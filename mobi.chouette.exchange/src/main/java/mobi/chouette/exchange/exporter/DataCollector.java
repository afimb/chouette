package mobi.chouette.exchange.exporter;

import java.sql.Date;
import java.util.Collection;
import java.util.List;

import lombok.extern.log4j.Log4j;
import mobi.chouette.model.AccessLink;
import mobi.chouette.model.AccessPoint;
import mobi.chouette.model.ConnectionLink;
import mobi.chouette.model.Footnote;
import mobi.chouette.model.JourneyPattern;
import mobi.chouette.model.Line;
import mobi.chouette.model.Route;
import mobi.chouette.model.StopArea;
import mobi.chouette.model.StopPoint;
import mobi.chouette.model.Timetable;
import mobi.chouette.model.VehicleJourney;
import mobi.chouette.model.util.NeptuneUtil;

@Log4j
public class DataCollector {

	protected boolean collect(ExportableData collection, Line line, Date startDate, Date endDate,
			boolean skipNoCoordinate, boolean followLinks) {
		boolean validLine = false;
		collection.setLine(null);
		collection.getRoutes().clear();
		collection.getJourneyPatterns().clear();
		collection.getStopPoints().clear();
		collection.getVehicleJourneys().clear();
		List<Footnote> notes = line.getFootnotes();
		
		for (Route route : line.getRoutes()) {
			boolean validRoute = false;
			if (route.getStopPoints().size() < 2)
				continue;
			for (JourneyPattern jp : route.getJourneyPatterns()) {
				boolean validJourneyPattern = false;
				if (jp.getStopPoints().size() < 2)
					continue; // no stops
				if (jp.getDepartureStopPoint() == null || jp.getArrivalStopPoint() == null) {
					NeptuneUtil.refreshDepartureArrivals(jp);
				}
				for (VehicleJourney vehicleJourney : jp.getVehicleJourneys()) {
					if (vehicleJourney.getVehicleJourneyAtStops().isEmpty()) {
						continue;
					}
					if (startDate == null && endDate == null) {
						boolean isValid = false;
						for (Timetable timetable : vehicleJourney.getTimetables()) {

							if (collection.getTimetables().contains(timetable)) {
								isValid = true;
							} else {
								if (!timetable.getPeriods().isEmpty() || !timetable.getCalendarDays().isEmpty()) {
									collection.getTimetables().add(timetable);
									isValid = true;
								}
							}
						}
						if (isValid) {
							collection.getTimetables().addAll(vehicleJourney.getTimetables());
							collection.getVehicleJourneys().add(vehicleJourney);
							validJourneyPattern = true;
							validRoute = true;
							validLine = true;
						}
					} else {
						boolean isValid = false;
						for (Timetable timetable : vehicleJourney.getTimetables()) {
							if (collection.getTimetables().contains(timetable)) {
								isValid = true;
							} else if (collection.getExcludedTimetables().contains(timetable)) {
								isValid = false;
							} else {

								if (startDate == null)
									isValid = timetable.isActiveBefore(endDate);
								else if (endDate == null)
									isValid = timetable.isActiveAfter(startDate);
								else
									isValid = timetable.isActiveOnPeriod(startDate, endDate);
								if (isValid)
									collection.getTimetables().add(timetable);
								else
									collection.getExcludedTimetables().add(timetable);
							}
						}
						if (isValid) {
							collection.getVehicleJourneys().add(vehicleJourney);
							if (vehicleJourney.getCompany() != null) {
								collection.getCompanies().add(vehicleJourney.getCompany());
							}
							validJourneyPattern = true;
							validRoute = true;
							validLine = true;
							boolean validNotes = true;
							for (Footnote note : vehicleJourney.getFootnotes()) {
								if (!notes.contains(note)) validNotes = false;
							}
							if (!validNotes)
							   log.warn("vehicle journey  has invalid foot notes");
						}
					}
				} // end vehiclejourney loop
				if (validJourneyPattern)
					collection.getJourneyPatterns().add(jp);
			}// end journeyPattern loop
			if (validRoute) {
				collection.getRoutes().add(route);
				route.getOppositeRoute(); // to avoid lazy loading afterward
				for (StopPoint stopPoint : route.getStopPoints()) {
					if (stopPoint == null)
						continue; // protection from missing stopPoint ranks
					collection.getStopPoints().add(stopPoint);
					collectStopAreas(collection, stopPoint.getContainedInStopArea(), skipNoCoordinate, followLinks);
				}
			}
		}// end route loop
		if (validLine) {
			collection.setLine(line);
			collection.getNetworks().add(line.getNetwork());
			if (line.getCompany() != null) {
				collection.getCompanies().add(line.getCompany());
			}
			if (line.getGroupOfLines() != null) {
				collection.getGroupOfLines().addAll(line.getGroupOfLines());
			}
			if (!line.getRoutingConstraints().isEmpty()) {
				collection.getStopAreas().addAll(line.getRoutingConstraints());
			}
		}
		completeSharedData(collection);
		return validLine;
	}

	protected boolean collect(ExportableData collection, Collection<StopArea> stopAreas, boolean skipNoCoordinate,
			boolean followLinks) {
		for (StopArea stopArea : stopAreas) {
			collectStopAreas(collection, stopArea, skipNoCoordinate, followLinks);
		}
		completeSharedData(collection);
		return !collection.getPhysicalStops().isEmpty();

	}

	protected void completeSharedData(ExportableData collection) {
		// force lazy dependencies to be loaded
		for (ConnectionLink link : collection.getConnectionLinks()) {
			collection.getSharedStops().add(link.getEndOfLink());
			collection.getSharedStops().add(link.getStartOfLink());
		}
	}

	protected void collectStopAreas(ExportableData collection, StopArea stopArea, boolean skipNoCoordinate,
			boolean followLinks) {
		if (collection.getStopAreas().contains(stopArea))
			return;
		if (!skipNoCoordinate || stopArea.hasCoordinates()) {
			collection.getStopAreas().add(stopArea);
			switch (stopArea.getAreaType()) {
			case BoardingPosition:
				collection.getBoardingPositions().add(stopArea);
				break;
			case Quay:
				collection.getQuays().add(stopArea);
				break;
			case CommercialStopPoint:
				collection.getCommercialStops().add(stopArea);
				break;
			case StopPlace:
				collection.getStopPlaces().add(stopArea);
				break;
			default:
			}
			addConnectionLinks(collection, stopArea.getConnectionStartLinks(), skipNoCoordinate, followLinks);
			addConnectionLinks(collection, stopArea.getConnectionEndLinks(), skipNoCoordinate, followLinks);
			addAccessPoints(collection, stopArea.getAccessPoints(), skipNoCoordinate);
			addAccessLinks(collection, stopArea.getAccessLinks());
			if (stopArea.getParent() != null)
				collectStopAreas(collection, stopArea.getParent(), skipNoCoordinate, followLinks);
		}
	}

	protected void addConnectionLinks(ExportableData collection, List<ConnectionLink> links, boolean skipNoCoordinate,
			boolean followLinks) {
		for (ConnectionLink link : links) {
			if (collection.getConnectionLinks().contains(link))
				continue;
			if (link.getStartOfLink() == null || link.getEndOfLink() == null)
				continue;
			if (!link.getStartOfLink().hasCoordinates() || !link.getEndOfLink().hasCoordinates())
				continue;
			collection.getConnectionLinks().add(link);
			if (followLinks) {
				collectStopAreas(collection, link.getStartOfLink(), skipNoCoordinate, followLinks);
				collectStopAreas(collection, link.getEndOfLink(), skipNoCoordinate, followLinks);
			}
		}
	}

	protected void addAccessLinks(ExportableData collection, List<AccessLink> links) {
		for (AccessLink link : links) {
			if (collection.getAccessLinks().contains(link))
				continue;
			if (link.getAccessPoint() == null)
				continue;
			if (!link.getAccessPoint().hasCoordinates())
				continue;
			collection.getAccessLinks().add(link);
		}
	}

	protected void addAccessPoints(ExportableData collection, List<AccessPoint> accessPoints, boolean skipNoCoordinate) {
		for (AccessPoint point : accessPoints) {
			if (collection.getAccessPoints().contains(point))
				continue;
			if (skipNoCoordinate && !point.hasCoordinates())
				continue;
			collection.getAccessPoints().add(point);
		}

	}

}
