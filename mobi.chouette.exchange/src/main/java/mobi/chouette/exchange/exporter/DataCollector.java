package mobi.chouette.exchange.exporter;

import java.util.Collection;
import java.util.List;

import lombok.extern.log4j.Log4j;
import mobi.chouette.model.AccessLink;
import mobi.chouette.model.AccessPoint;
import mobi.chouette.model.ConnectionLink;
import mobi.chouette.model.JourneyPattern;
import mobi.chouette.model.Line;
import mobi.chouette.model.Route;
import mobi.chouette.model.StopArea;
import mobi.chouette.model.StopPoint;
import mobi.chouette.model.Timetable;
import mobi.chouette.model.VehicleJourney;
import mobi.chouette.model.VehicleJourneyAtStop;
import mobi.chouette.model.util.NeptuneUtil;

import org.joda.time.LocalDate;

@Log4j
public class DataCollector {

	protected boolean collect(ExportableData collection, Line line, LocalDate startDate, LocalDate endDate,
							  boolean skipNoCoordinate, boolean followLinks) {
		boolean validLine = false;
		collection.setLine(null);
		collection.getRoutes().clear();
		collection.getJourneyPatterns().clear();
		collection.getStopPoints().clear();
		collection.getVehicleJourneys().clear();
		collection.getFootnotes().clear();

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
							collection.getInterchanges().addAll(vehicleJourney.getFeederInterchanges());
							collection.getInterchanges().addAll(vehicleJourney.getConsumerInterchanges());
							for(VehicleJourneyAtStop vjas : vehicleJourney.getVehicleJourneyAtStops()) {
								collection.getFootnotes().addAll(vjas.getFootnotes());
							}
							collection.getFootnotes().addAll(vehicleJourney.getFootnotes());
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
							collection.getInterchanges().addAll(vehicleJourney.getFeederInterchanges());
							collection.getInterchanges().addAll(vehicleJourney.getConsumerInterchanges());
							collection.getFootnotes().addAll(vehicleJourney.getFootnotes());
							for(VehicleJourneyAtStop vjas : vehicleJourney.getVehicleJourneyAtStops()) {
								collection.getFootnotes().addAll(vjas.getFootnotes());
							}
							if (vehicleJourney.getCompany() != null) {
								collection.getCompanies().add(vehicleJourney.getCompany());
							}
							validJourneyPattern = true;
							validRoute = true;
							validLine = true;
						}
					}
				} // end vehiclejourney loop
				if (validJourneyPattern) {
					collection.getJourneyPatterns().add(jp);
					collection.getFootnotes().addAll(jp.getFootnotes());
				}
			}// end journeyPattern loop
			if (validRoute) {
				collection.getRoutes().add(route);
				route.getOppositeRoute(); // to avoid lazy loading afterward
				for (StopPoint stopPoint : route.getStopPoints()) {
					if (stopPoint == null)
						continue; // protection from missing stopPoint ranks
					collection.getStopPoints().add(stopPoint);
					collection.getFootnotes().addAll(stopPoint.getFootnotes());
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
			collection.getFootnotes().addAll(line.getFootnotes());
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
