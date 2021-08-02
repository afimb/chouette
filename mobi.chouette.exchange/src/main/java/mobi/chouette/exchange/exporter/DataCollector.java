package mobi.chouette.exchange.exporter;

import lombok.extern.log4j.Log4j;
import mobi.chouette.model.AccessLink;
import mobi.chouette.model.AccessPoint;
import mobi.chouette.model.Block;
import mobi.chouette.model.ConnectionLink;
import mobi.chouette.model.DeadRun;
import mobi.chouette.model.Interchange;
import mobi.chouette.model.JourneyPattern;
import mobi.chouette.model.Line;
import mobi.chouette.model.Network;
import mobi.chouette.model.Route;
import mobi.chouette.model.StopArea;
import mobi.chouette.model.StopPoint;
import mobi.chouette.model.VehicleJourney;
import mobi.chouette.model.VehicleJourneyAtStop;
import org.joda.time.LocalDate;

import java.util.List;
import java.util.stream.Collectors;

@Log4j
public class DataCollector {

	protected LocalDate startDate;
	protected LocalDate endDate;
	protected boolean skipNoCoordinate;
	protected boolean followLinks;
	protected ExportableData collection;
	protected Line line;

	public DataCollector(ExportableData collection, Line line, LocalDate startDate, LocalDate endDate, boolean skipNoCoordinate, boolean followLinks) {
		this.collection = collection;
		this.line = line;
		this.startDate = startDate;
		this.endDate = endDate;
		this.skipNoCoordinate = skipNoCoordinate;
		this.followLinks = followLinks;
	}

	public boolean collect() {
		collection.setLine(null);
		collection.getRoutes().clear();
		collection.getJourneyPatterns().clear();
		collection.getStopPoints().clear();
		collection.getVehicleJourneys().clear();
		collection.getFootnotes().clear();

		boolean isValid = line.filter(startDate, endDate);

		if(isValid) {
			collectLine();
		}
		return isValid;
	}

	private void collectLine() {
		line.getRoutes().forEach(this::collectRoute);
		collection.setLine(line);
		Network network = line.getNetwork();
		if (network != null) {
			collection.getNetworks().add(network);
			if (network.getCompany() != null) { // Authority
				collection.getCompanies().add(network.getCompany());
			}
		}
		if (line.getCompany() != null) { // Operator
			collection.getCompanies().add(line.getCompany());
		}
		if (line.getGroupOfLines() != null) {
			collection.getGroupOfLines().addAll(line.getGroupOfLines());
		}
		if (!line.getRoutingConstraints().isEmpty()) {
			collection.getStopAreas().addAll(line.getRoutingConstraints());
		}
		collection.getFootnotes().addAll(line.getFootnotes());
		completeSharedData(collection);
	}

	private void collectRoute(Route route) {
		route.getJourneyPatterns().forEach(this::collectJourneyPattern);
		collection.getRoutes().add(route);
		route.getOppositeRoute(); // to avoid lazy loading afterward
		for (StopPoint stopPoint : route.getStopPoints()) {
			if (stopPoint == null)
				continue; // protection from missing stopPoint ranks
			collection.getStopPoints().add(stopPoint);
			if (stopPoint.getScheduledStopPoint().getContainedInStopAreaRef().getObject() != null)
				collectStopAreas(collection, stopPoint.getScheduledStopPoint().getContainedInStopAreaRef().getObject(), skipNoCoordinate, followLinks);
			collection.getFootnotes().addAll(stopPoint.getFootnotes());
		}
	}

	private void collectJourneyPattern(JourneyPattern journeyPattern) {
		journeyPattern.getVehicleJourneys().forEach(this::collectVehicleJourney);
		journeyPattern.getDeadRuns().forEach(this::collectDeadRun);
		collection.getJourneyPatterns().add(journeyPattern);
		collection.getFootnotes().addAll(journeyPattern.getFootnotes());
	}

	private void collectVehicleJourney(VehicleJourney vehicleJourney) {
		collection.getTimetables().addAll(vehicleJourney.getTimetables());
		collection.getDatedServiceJourneys().addAll(vehicleJourney.getDatedServiceJourneys());
		collection.getBlocks().addAll(vehicleJourney.getBlocks());
		collection.getTimetables().addAll(vehicleJourney.getBlocks().stream().map(Block::getTimetables).flatMap(List::stream).collect(Collectors.toList()));
		collection.getVehicleJourneys().add(vehicleJourney);
		collectInterchanges(collection, vehicleJourney, skipNoCoordinate, followLinks, startDate, endDate);
		collection.getFootnotes().addAll(vehicleJourney.getFootnotes());
		for (VehicleJourneyAtStop vjas : vehicleJourney.getVehicleJourneyAtStops()) {
			collection.getFootnotes().addAll(vjas.getFootnotes());
		}
		if (vehicleJourney.getCompany() != null) {
			collection.getCompanies().add(vehicleJourney.getCompany());
		}
	}

	private void collectDeadRun(DeadRun deadRun) {
		collection.getTimetables().addAll(deadRun.getTimetables());
		collection.getBlocks().addAll(deadRun.getBlocks());
		collection.getTimetables().addAll(deadRun.getBlocks().stream().map(Block::getTimetables).flatMap(List::stream).collect(Collectors.toList()));
		collection.getDeadRuns().add(deadRun);
	}

	private void collectInterchanges(ExportableData collection, VehicleJourney vehicleJourney, boolean skipNoCoordinate, boolean followLinks, LocalDate startDate, LocalDate endDate) {
		for (Interchange interchange : vehicleJourney.getConsumerInterchanges()) {
			if (interchange.getFeederVehicleJourney() != null && !interchange.getFeederVehicleJourney().isActiveOnPeriod(startDate, endDate)) {
				continue;
			}

			collection.getInterchanges().add(interchange);

			if (interchange.getFeederStopPoint() !=null && interchange.getFeederStopPoint().getContainedInStopAreaRef().getObject() != null) {
				collectStopAreas(collection, interchange.getFeederStopPoint().getContainedInStopAreaRef().getObject(), skipNoCoordinate, followLinks);
			}
		}
	}

	protected void completeSharedData(ExportableData collection) {
		// force lazy dependencies to be loaded
		for (ConnectionLink link : collection.getConnectionLinks()) {
			collectStopAreas(collection, link.getStartOfLink(), false, false);
			collectStopAreas(collection, link.getEndOfLink(), false, false);
		}
		for(Block block: collection.getBlocks()) {
			if(block.getStartPoint() != null &&  block.getStartPoint().getContainedInStopAreaRef().getObject() != null) {
				collectStopAreas(collection, block.getStartPoint().getContainedInStopAreaRef().getObject(), false, false);
			}
			if(block.getEndPoint() != null && block.getEndPoint().getContainedInStopAreaRef().getObject() != null) {
				collectStopAreas(collection, block.getEndPoint().getContainedInStopAreaRef().getObject(), false, false);
			}
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
