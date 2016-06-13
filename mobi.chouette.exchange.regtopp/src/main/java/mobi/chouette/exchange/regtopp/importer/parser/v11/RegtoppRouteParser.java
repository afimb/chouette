package mobi.chouette.exchange.regtopp.importer.parser.v11;

import static mobi.chouette.common.Constant.CONFIGURATION;
import static mobi.chouette.common.Constant.PARSER;
import static mobi.chouette.common.Constant.REFERENTIAL;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Context;
import mobi.chouette.exchange.importer.Parser;
import mobi.chouette.exchange.importer.ParserFactory;
import mobi.chouette.exchange.regtopp.importer.RegtoppImportParameters;
import mobi.chouette.exchange.regtopp.importer.RegtoppImporter;
import mobi.chouette.exchange.regtopp.importer.index.Index;
import mobi.chouette.exchange.regtopp.importer.parser.AbstractConverter;
import mobi.chouette.exchange.regtopp.importer.parser.LineSpecificParser;
import mobi.chouette.exchange.regtopp.importer.parser.RouteKey;
import mobi.chouette.exchange.regtopp.model.AbstractRegtoppTripIndexTIX;
import mobi.chouette.exchange.regtopp.model.enums.DirectionType;
import mobi.chouette.exchange.regtopp.model.v11.RegtoppDestinationDST;
import mobi.chouette.exchange.regtopp.model.v11.RegtoppFootnoteMRK;
import mobi.chouette.exchange.regtopp.model.v11.RegtoppRouteTDA;
import mobi.chouette.exchange.regtopp.model.v11.RegtoppTripIndexTIX;
import mobi.chouette.model.Company;
import mobi.chouette.model.Footnote;
import mobi.chouette.model.JourneyPattern;
import mobi.chouette.model.Line;
import mobi.chouette.model.Network;
import mobi.chouette.model.Route;
import mobi.chouette.model.StopArea;
import mobi.chouette.model.StopPoint;
import mobi.chouette.model.VehicleJourney;
import mobi.chouette.model.type.PTDirectionEnum;
import mobi.chouette.model.util.ObjectFactory;
import mobi.chouette.model.util.ObjectIdTypes;
import mobi.chouette.model.util.Referential;

@Log4j
public class RegtoppRouteParser extends LineSpecificParser {

	/*
	 * Validation rules of type III are checked at this step.
	 */
	// TODO. Rename this function "translate(Context context)" or "produce(Context context)", ...
	@Override
	public void parse(Context context) throws Exception {

		Referential referential = (Referential) context.get(REFERENTIAL);
		RegtoppImporter importer = (RegtoppImporter) context.get(PARSER);
		RegtoppImportParameters configuration = (RegtoppImportParameters) context.get(CONFIGURATION);

		String chouetteLineId = AbstractConverter.composeObjectId(configuration.getObjectIdPrefix(), Line.LINE_KEY, lineId);
		Line line = ObjectFactory.getLine(referential, chouetteLineId);

		Index<RegtoppRouteTDA> routeIndex = importer.getRouteSegmentByLineNumber();
		Index<AbstractRegtoppTripIndexTIX> tripIndex = importer.getTripIndex();

		for (AbstractRegtoppTripIndexTIX abstractTrip : tripIndex) {
			if (abstractTrip.getLineId().equals(lineId)) {

				// Cast to 1.1D
				RegtoppTripIndexTIX trip = (RegtoppTripIndexTIX) abstractTrip;

				// Add network
				Network ptNetwork = addNetwork(referential, configuration, trip.getAdminCode());
				line.setNetwork(ptNetwork);

				// Add authority company
				Company company = addAuthority(referential, configuration, trip.getAdminCode());
				line.setCompany(company);

				// Create route
				RouteKey routeKey = new RouteKey(trip.getLineId(), trip.getDirection(), trip.getRouteIdRef());
				Route route = createRoute(context, line, trip.getDirection(), trip.getRouteIdRef(), trip.getDestinationIdDepartureRef(), routeKey);
				
				String chouetteJourneyPatternId = AbstractConverter.composeObjectId(configuration.getObjectIdPrefix(), ObjectIdTypes.JOURNEYPATTERN_KEY,
						routeKey.toString());

				JourneyPattern journeyPattern = ObjectFactory.getJourneyPattern(referential, chouetteJourneyPatternId);
				if (!journeyPattern.isFilled()) {
					journeyPattern.setRoute(route);
					journeyPattern.setPublishedName(route.getPublishedName());

					String firstStop = trip.getFirstStop();
					Integer numStops = trip.getNumStops();
					for (int i = 0; i < numStops; i++) {

						// TODO use another identifier as it causes duplicate stoppoints in route
						String lineNumber = StringUtils.leftPad("" + (Integer.parseInt(firstStop) + i), 7, "0");
						RegtoppRouteTDA routeSegment = routeIndex.getValue(lineNumber);
						
						// Create stop point
						String chouetteStopPointId = AbstractConverter.composeObjectId(configuration.getObjectIdPrefix(), ObjectIdTypes.STOPPOINT_KEY,
								routeKey.toString() + i);
						StopPoint stopPoint = ObjectFactory.getStopPoint(referential, chouetteStopPointId);
						stopPoint.setPosition(i);

						String chouetteStopAreaId = AbstractConverter.composeObjectId(configuration.getObjectIdPrefix(), ObjectIdTypes.STOPAREA_KEY,
								routeSegment.getStopId());

						StopArea stopArea = ObjectFactory.getStopArea(referential, chouetteStopAreaId);

						stopPoint.setContainedInStopArea(stopArea);

						// Warn: Using comment field as temporary storage for line pointer. Used for lookup when parsing passing times
						stopPoint.setComment(lineNumber);

						// Add stop point to journey pattern AND route (for now)
						journeyPattern.addStopPoint(stopPoint);
						route.getStopPoints().add(stopPoint);
					}
					journeyPattern.setFilled(true);
				}

			}
		}

		sortStopPoints(referential);
		updateRouteNames(referential, configuration);
		linkOppositeRoutes(referential, configuration);

	}

	protected Route createRoute(Context context, Line line, 
			DirectionType direction, String routeId, String destinationId,RouteKey routeKey) throws Exception {
		
		Referential referential = (Referential) context.get(REFERENTIAL);
		RegtoppImporter importer = (RegtoppImporter) context.get(PARSER);
		RegtoppImportParameters configuration = (RegtoppImportParameters) context.get(CONFIGURATION);
		
		Index<RegtoppDestinationDST> destinationIndex = importer.getDestinationById();
		
		String chouetteRouteId = AbstractConverter.composeObjectId(configuration.getObjectIdPrefix(), ObjectIdTypes.ROUTE_KEY, routeKey.toString());
		Route route = ObjectFactory.getRoute(referential, chouetteRouteId);
		if (!route.isFilled()) {
			// Filled = only a flag to indicate that we no longer should write data to this entity
			RegtoppDestinationDST arrivalText = destinationIndex.getValue(destinationId);
			if (arrivalText != null) {
				route.setName(arrivalText.getDestinationText());
				route.setPublishedName(route.getName());
			}

			route.setDirection(direction == DirectionType.Outbound ? PTDirectionEnum.A : PTDirectionEnum.R);

			// TODO UNSURE
			route.setNumber(routeId);
			route.setLine(line);

			// Black magic
			route.setWayBack(direction == DirectionType.Outbound ? "A" : "R");

			route.setFilled(true);

		}
		return route;
	}

	protected Network addNetwork(Referential referential, RegtoppImportParameters configuration, String adminCode) {
		String chouetteNetworkId = AbstractConverter.composeObjectId(configuration.getObjectIdPrefix(), ObjectIdTypes.PTNETWORK_KEY,
				adminCode);
		Network ptNetwork = ObjectFactory.getPTNetwork(referential, chouetteNetworkId);
		if (!ptNetwork.isFilled()) {
			ptNetwork.setSourceIdentifier("Regtopp");
			ptNetwork.setName(adminCode);
			ptNetwork.setRegistrationNumber(adminCode);
			ptNetwork.setFilled(true);
		}
		return ptNetwork;
	}
	
	protected Company addAuthority(Referential referential, RegtoppImportParameters configuration, String adminCode) {
		String chouetteCompanyId = AbstractConverter.composeObjectId(configuration.getObjectIdPrefix(), ObjectIdTypes.COMPANY_KEY,
				adminCode);
		Company company = ObjectFactory.getCompany(referential, chouetteCompanyId);
		if (!company.isFilled()) {
			company.setRegistrationNumber(adminCode);
			company.setName("Authority " + adminCode);
			company.setCode(adminCode);
			company.setFilled(true);
		}
		return company;
	}

	protected void addFootnote(String footnoteId, VehicleJourney vehicleJourney, List<Footnote> footnotes, RegtoppImporter importer) throws Exception {
		if (!"000".equals(footnoteId) && StringUtils.trimToNull(footnoteId) != null) {
			if (!footnoteAlreadyAdded(footnotes, footnoteId)) {
				Index<RegtoppFootnoteMRK> index = importer.getFootnoteById();
				RegtoppFootnoteMRK footnote = index.getValue(footnoteId);

				Footnote f = new Footnote();

				f.setLabel(footnote.getDescription());
				f.setKey(footnote.getFootnoteId());
				f.setCode(footnote.getFootnoteId());

				footnotes.add(f);
			}
			if (vehicleJourney != null) {
				for (Footnote existing : footnotes) {
					if (existing.getCode().equals(footnoteId)) {
						vehicleJourney.getFootnotes().add(existing);
					}
				}
			}
		}
	}


	protected boolean footnoteAlreadyAdded(List<Footnote> addedFootnotes, String footnoteId) {
		for (Footnote existing : addedFootnotes) {
			if (existing.getCode().equals(footnoteId)) {
				return true;
			}
		}
		return false;
	}

	protected void sortStopPoints(Referential referential) {
		Comparator<StopPoint> stopPointSequenceComparator = new Comparator<StopPoint>() {
			@Override
			public int compare(StopPoint arg0, StopPoint arg1) {
				return arg0.getPosition().compareTo(arg1.getPosition());
			}
		};

		// Sort stopPoints on JourneyPattern
		Collection<JourneyPattern> journeyPatterns = referential.getJourneyPatterns().values();
		for (JourneyPattern jp : journeyPatterns) {
			List<StopPoint> stopPoints = jp.getStopPoints();
			Collections.sort(stopPoints, stopPointSequenceComparator);
			jp.setDepartureStopPoint(stopPoints.get(0));
			jp.setArrivalStopPoint(stopPoints.get(stopPoints.size() - 1));
		}

		// Sort stopPoints on route
		Collection<Route> routes = referential.getRoutes().values();
		for (Route r : routes) {
			List<StopPoint> stopPoints = r.getStopPoints();
			Collections.sort(stopPoints, stopPointSequenceComparator);
		}
	}

	protected void linkOppositeRoutes(Referential referential, RegtoppImportParameters configuration) {

		// Link opposite routes together
		for (Route r : referential.getRoutes().values()) {
			if (r.getOppositeRoute() == null) {
				RouteKey key = new RouteKey(AbstractConverter.extractOriginalId(r.getObjectId()));
				RouteKey oppositeKey = new RouteKey(key.getLineId(), key.getDirection().getOppositeDirection(), key.getRouteId());
				String oppositeObjectId = AbstractConverter.composeObjectId(configuration.getObjectIdPrefix(), ObjectIdTypes.ROUTE_KEY, oppositeKey.toString());
				for (Route opposite : referential.getRoutes().values()) {
					if (opposite.getObjectId().equals(oppositeObjectId)) {
						// Link routes
						r.setOppositeRoute(opposite);
						opposite.setOppositeRoute(r);
						break;
					}
				}
			}
		}

		for (Route route : referential.getRoutes().values()) {
			// default direction and wayback = R if opposite Route = A, else A

			if (route.getDirection() == null) {
				PTDirectionEnum oppositeDirection = route.getOppositeRoute() != null ? route.getOppositeRoute().getDirection() : PTDirectionEnum.R;
				route.setDirection(getOppositeDirection(oppositeDirection));
			}
			if (route.getWayBack() == null) {
				route.setWayBack(route.getOppositeRoute() != null && route.getWayBack().equals("A") ? "R" : "A");
			}

		}
	}

	protected void updateRouteNames(Referential referential, RegtoppImportParameters configuration) {

		for (Route route : referential.getRoutes().values()) {
			if (route.getName() == null) {
				// Set to last stop
				List<StopPoint> stopPoints = route.getStopPoints();
				if (stopPoints != null && !stopPoints.isEmpty()) {
					StopArea lastStopArea = stopPoints.get(stopPoints.size() - 1).getContainedInStopArea();
					if(lastStopArea.getParent() == null) {
						route.setName(lastStopArea.getName());
					} else {
						route.setName(lastStopArea.getParent().getName());
					}
				}
			}

			route.setPublishedName(route.getName());

			for (JourneyPattern jp : route.getJourneyPatterns()) {

				// Set arrival and departure

				jp.setName(route.getName());
			}

			// default direction and wayback = R if opposite Route = A, else A
		}
	}

	protected PTDirectionEnum getOppositeDirection(PTDirectionEnum direction) {
		if (direction == null)
			return PTDirectionEnum.A;
		switch (direction) {
		case A:
			return PTDirectionEnum.R;
		case R:
			return PTDirectionEnum.A;
		case ClockWise:
			return PTDirectionEnum.CounterClockWise;
		case CounterClockWise:
			return PTDirectionEnum.ClockWise;
		case North:
			return PTDirectionEnum.South;
		case South:
			return PTDirectionEnum.North;
		case NorthWest:
			return PTDirectionEnum.SouthEast;
		case SouthWest:
			return PTDirectionEnum.NorthEast;
		case NorthEast:
			return PTDirectionEnum.SouthWest;
		case SouthEast:
			return PTDirectionEnum.NorthWest;
		case East:
			return PTDirectionEnum.West;
		case West:
			return PTDirectionEnum.East;
		}
		return PTDirectionEnum.A;

	}

	static {
		ParserFactory.register(RegtoppRouteParser.class.getName(), new ParserFactory() {
			@Override
			protected Parser create() {
				return new RegtoppRouteParser();
			}
		});
	}

}
