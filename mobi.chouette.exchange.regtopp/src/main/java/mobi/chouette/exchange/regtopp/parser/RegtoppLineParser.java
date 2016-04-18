package mobi.chouette.exchange.regtopp.parser;

import static mobi.chouette.common.Constant.CONFIGURATION;
import static mobi.chouette.common.Constant.MAIN_VALIDATION_REPORT;
import static mobi.chouette.common.Constant.PARSER;
import static mobi.chouette.common.Constant.REFERENTIAL;
import static mobi.chouette.exchange.regtopp.RegtoppConstant.REGTOPP_REPORTER;
import static mobi.chouette.exchange.regtopp.validation.Constant.REGTOPP_FILE_TIX;

import java.sql.Time;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.joda.time.Duration;
import org.joda.time.LocalTime;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Context;
import mobi.chouette.exchange.importer.Parser;
import mobi.chouette.exchange.importer.ParserFactory;
import mobi.chouette.exchange.importer.Validator;
import mobi.chouette.exchange.regtopp.RegtoppConstant;
import mobi.chouette.exchange.regtopp.importer.RegtoppImportParameters;
import mobi.chouette.exchange.regtopp.importer.version.Regtopp12NovusVersionHandler;
import mobi.chouette.exchange.regtopp.importer.version.VersionHandler;
import mobi.chouette.exchange.regtopp.model.enums.AnnouncementType;
import mobi.chouette.exchange.regtopp.model.enums.DirectionType;
import mobi.chouette.exchange.regtopp.model.enums.TransportType;
import mobi.chouette.exchange.regtopp.model.importer.parser.FileParserValidationError;
import mobi.chouette.exchange.regtopp.model.importer.parser.RegtoppException;
import mobi.chouette.exchange.regtopp.model.importer.parser.RegtoppImporter;
import mobi.chouette.exchange.regtopp.model.importer.parser.index.Index;
import mobi.chouette.exchange.regtopp.model.v12.RegtoppDestinationDST;
import mobi.chouette.exchange.regtopp.model.v12.RegtoppFootnoteMRK;
import mobi.chouette.exchange.regtopp.model.v12.RegtoppLineLIN;
import mobi.chouette.exchange.regtopp.model.v12.RegtoppRouteTMS;
import mobi.chouette.exchange.regtopp.model.v12.RegtoppTripIndexTIX;
import mobi.chouette.exchange.regtopp.validation.RegtoppValidationReporter;
import mobi.chouette.exchange.validation.report.CheckPoint;
import mobi.chouette.exchange.validation.report.ValidationReport;
import mobi.chouette.model.Company;
import mobi.chouette.model.Footnote;
import mobi.chouette.model.JourneyPattern;
import mobi.chouette.model.Line;
import mobi.chouette.model.Network;
import mobi.chouette.model.Route;
import mobi.chouette.model.StopArea;
import mobi.chouette.model.StopPoint;
import mobi.chouette.model.Timetable;
import mobi.chouette.model.VehicleJourney;
import mobi.chouette.model.VehicleJourneyAtStop;
import mobi.chouette.model.type.AlightingPossibilityEnum;
import mobi.chouette.model.type.BoardingAlightingPossibilityEnum;
import mobi.chouette.model.type.BoardingPossibilityEnum;
import mobi.chouette.model.type.PTDirectionEnum;
import mobi.chouette.model.type.TransportModeNameEnum;
import mobi.chouette.model.util.ObjectFactory;
import mobi.chouette.model.util.ObjectIdTypes;
import mobi.chouette.model.util.Referential;

@Log4j
public class RegtoppLineParser implements Parser, Validator {

	@Setter
	private String lineId = null;

	/*
	 * Validation rules of type I and II are checked during this step, and results are stored in reports.
	 */
	// TODO. Rename this function "parse(Context context)".
	@Override
	public void validate(Context context) throws Exception {

		// Konsistenssjekker, kjøres før parse-metode.

		// Det som kan sjekkes her er at antall poster stemmer og at alle referanser til andre filer er gyldige

		RegtoppImporter importer = (RegtoppImporter) context.get(PARSER);
		RegtoppValidationReporter validationReporter = (RegtoppValidationReporter) context.get(REGTOPP_REPORTER);
		validationReporter.getExceptions().clear();

		ValidationReport mainReporter = (ValidationReport) context.get(MAIN_VALIDATION_REPORT);

		mainReporter.getCheckPoints().add(new CheckPoint(REGTOPP_FILE_TIX, CheckPoint.RESULT.UNCHECK, CheckPoint.SEVERITY.ERROR));

		if (importer.hasTIXImporter()) {
			validationReporter.reportSuccess(context, REGTOPP_FILE_TIX, RegtoppTripIndexTIX.FILE_EXTENSION);

			Index<RegtoppTripIndexTIX> index = importer.getTripIndex();

			if (index.getLength() == 0) {
				FileParserValidationError fileError = new FileParserValidationError(RegtoppTripIndexTIX.FILE_EXTENSION, 0, null,
						RegtoppException.ERROR.FILE_WITH_NO_ENTRY, null, "Empty file");
				validationReporter.reportError(context, new RegtoppException(fileError), RegtoppTripIndexTIX.FILE_EXTENSION);
			}

			for (RegtoppTripIndexTIX bean : index) {
				try {
					// Call index validator
					index.validate(bean, importer);
				} catch (Exception ex) {
					if (ex instanceof RegtoppException) {
						validationReporter.reportError(context, (RegtoppException) ex, RegtoppTripIndexTIX.FILE_EXTENSION);
					} else {
						validationReporter.throwUnknownError(context, ex, RegtoppTripIndexTIX.FILE_EXTENSION);
					}
				}
				validationReporter.reportErrors(context, bean.getErrors(), RegtoppTripIndexTIX.FILE_EXTENSION);
				validationReporter.validate(context, RegtoppTripIndexTIX.FILE_EXTENSION, bean.getOkTests());
			}
		}

	}

	/*
	 * Validation rules of type III are checked at this step.
	 */
	// TODO. Rename this function "translate(Context context)" or "produce(Context context)", ...
	@Override
	public void parse(Context context) throws Exception {

		// Her tar vi allerede konsistenssjekkede data (ref validate-metode over) og bygger opp tilsvarende struktur i chouette.
		// Merk at import er linje-sentrisk, så man skal i denne klassen returnerer 1 line med x antall routes og stoppesteder, journeypatterns osv

		Referential referential = (Referential) context.get(REFERENTIAL);

		// Clear any previous data as this referential is reused / TODO
		if (referential != null) {
			referential.clear(true);
		}

		RegtoppImporter importer = (RegtoppImporter) context.get(PARSER);
		RegtoppImportParameters configuration = (RegtoppImportParameters) context.get(CONFIGURATION);

		String chouetteLineId = AbstractConverter.composeObjectId(configuration.getObjectIdPrefix(), Line.LINE_KEY, lineId);

		// Create the actual Chouette Line and put it in the "referential" space (which is later used by the LineImporterCommand)
		Line line = ObjectFactory.getLine(referential, chouetteLineId);

		// Find line number (TODO check if index exists)
		Index<RegtoppLineLIN> lineById = importer.getLineById();
		RegtoppLineLIN regtoppLine = lineById.getValue(lineId);
		if (regtoppLine != null) {
			line.setName(regtoppLine.getName());
			line.setPublishedName(regtoppLine.getName());
		}

		List<Footnote> footnotes = line.getFootnotes();

		Index<RegtoppDestinationDST> destinationIndex = importer.getDestinationById();

		// Add routes and journey patterns
		Index<RegtoppRouteTMS> routeIndex = importer.getRouteIndex();

		for (RegtoppRouteTMS routeSegment : routeIndex) {
			if (lineId.equals(routeSegment.getLineId())) {

				// Add network
				String chouetteNetworkId = AbstractConverter.composeObjectId(configuration.getObjectIdPrefix(), ObjectIdTypes.PTNETWORK_KEY,
						routeSegment.getAdminCode());
				Network ptNetwork = ObjectFactory.getPTNetwork(referential, chouetteNetworkId);
				if (!ptNetwork.isFilled()) {
					ptNetwork.setSourceIdentifier("Regtopp");
					ptNetwork.setName(routeSegment.getAdminCode());
					ptNetwork.setRegistrationNumber(routeSegment.getAdminCode());
					ptNetwork.setFilled(true);
				}
				line.setNetwork(ptNetwork);

				// Add authority company
				String chouetteCompanyId = AbstractConverter.composeObjectId(configuration.getObjectIdPrefix(), ObjectIdTypes.COMPANY_KEY,
						routeSegment.getAdminCode());
				Company company = ObjectFactory.getCompany(referential, chouetteCompanyId);
				if (!company.isFilled()) {
					company.setRegistrationNumber(routeSegment.getAdminCode());
					company.setName("Authority " + routeSegment.getAdminCode());
					company.setCode(routeSegment.getAdminCode());
					company.setFilled(true);
				}
				line.setCompany(company);

				// Add footnoe to line
				addFootnote(routeSegment.getRemarkId(), null, footnotes, importer);

				RouteKey routeKey = new RouteKey(routeSegment.getLineId(), routeSegment.getDirection(), routeSegment.getRouteId());

				// Create route
				String chouetteRouteId = AbstractConverter.composeObjectId(configuration.getObjectIdPrefix(), ObjectIdTypes.ROUTE_KEY, routeKey.toString());
				Route route = ObjectFactory.getRoute(referential, chouetteRouteId);
				if (!route.isFilled()) {
					// Filled = only a flag to indicate that we no longer should write data to this entity
					RegtoppDestinationDST arrivalText = destinationIndex.getValue(routeSegment.getDestinationId());
					if (arrivalText != null) {
						route.setName(arrivalText.getDestinationText());
					}

					route.setDirection(routeSegment.getDirection() == DirectionType.Outbound ? PTDirectionEnum.A : PTDirectionEnum.R);

					// TODO UNSURE
					route.setNumber(routeSegment.getRouteId());
					route.setLine(line);

					// Black magic
					route.setWayBack(routeSegment.getDirection() == DirectionType.Outbound ? "A" : "R");

					route.setFilled(true);

				}

				// Create journey pattern
				String chouetteJourneyPatternId = AbstractConverter.composeObjectId(configuration.getObjectIdPrefix(), ObjectIdTypes.JOURNEYPATTERN_KEY,
						routeKey.toString());

				JourneyPattern journeyPattern = ObjectFactory.getJourneyPattern(referential, chouetteJourneyPatternId);
				journeyPattern.setRoute(route);

				// Create stop point
				String chouetteStopPointId = AbstractConverter.composeObjectId(configuration.getObjectIdPrefix(), ObjectIdTypes.STOPPOINT_KEY,
						routeKey + routeSegment.getSequenceNumberStop());

				StopPoint stopPoint = createStopPoint(referential, context, routeSegment, chouetteStopPointId);

				// Add stop point to journey pattern AND route (for now)
				journeyPattern.addStopPoint(stopPoint);
				route.getStopPoints().add(stopPoint);

			}
		}

		Comparator<StopPoint> stopPointSequenceComparator = new Comparator<StopPoint>() {
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

		Set<TransportModeNameEnum> detectedTransportModes = new HashSet<TransportModeNameEnum>();

		// Add VehicleJourneys
		Index<RegtoppTripIndexTIX> tripIndex = importer.getTripIndex();
		for (RegtoppTripIndexTIX trip : tripIndex) {
			if (trip.getLineId().equals(lineId)) {
				if (trip.getNotificationType() == AnnouncementType.Announced) {

					// This is where we get the line number
					line.setNumber(trip.getLineNumberVisible());

					String tripKey = trip.getLineId() + trip.getTripId();
					RouteKey routeKey = new RouteKey(trip.getLineId(), trip.getDirection(), trip.getRouteIdRef());

					String chouetteVehicleJourneyId = AbstractConverter.composeObjectId(configuration.getObjectIdPrefix(), ObjectIdTypes.VEHICLEJOURNEY_KEY,
							tripKey);
					VehicleJourney vehicleJourney = ObjectFactory.getVehicleJourney(referential, chouetteVehicleJourneyId);

					// Add authority company
					String chouetteOperatorId = AbstractConverter.composeObjectId(configuration.getObjectIdPrefix(), ObjectIdTypes.COMPANY_KEY,
							trip.getOperatorCode());
					Company operator = ObjectFactory.getCompany(referential, chouetteOperatorId);
					operator.setRegistrationNumber(trip.getOperatorCode());
					operator.setName("Operator " + trip.getOperatorCode());
					operator.setCode(trip.getOperatorCode());
					vehicleJourney.setCompany(operator);

					addFootnote(trip.getFootnoteId1Ref(), vehicleJourney, footnotes, importer);
					addFootnote(trip.getFootnoteId2Ref(), vehicleJourney, footnotes, importer);

					RegtoppDestinationDST departureText = destinationIndex.getValue(trip.getDestinationIdDepartureRef()); // Turens bestemmelsessted
					RegtoppDestinationDST arrivalText = destinationIndex.getValue(trip.getDestinationIdArrivalRef()); // Turens startsted

					// TODO unsure
					if (departureText != null && arrivalText != null) {
						vehicleJourney.setPublishedJourneyName(arrivalText.getDestinationText() + " -> " + departureText.getDestinationText());
					} else if (departureText != null) {
						vehicleJourney.setPublishedJourneyName(departureText.getDestinationText());
					}

					vehicleJourney.setPublishedJourneyIdentifier(StringUtils.trimToNull(trip.getLineNumberVisible()));
					TransportType typeOfService = trip.getTypeOfService();
					TransportModeNameEnum transportMode = convertTypeOfService(typeOfService);
					vehicleJourney.setTransportMode(transportMode);

					detectedTransportModes.add(transportMode);

					String chouetteRouteId = AbstractConverter.composeObjectId(configuration.getObjectIdPrefix(), ObjectIdTypes.ROUTE_KEY, routeKey.toString());
					Route route = ObjectFactory.getRoute(referential, chouetteRouteId);

					String chouetteJourneyPatternId = AbstractConverter.composeObjectId(configuration.getObjectIdPrefix(), ObjectIdTypes.JOURNEYPATTERN_KEY,
							routeKey.toString());
					JourneyPattern journeyPattern = ObjectFactory.getJourneyPattern(referential, chouetteJourneyPatternId);

					vehicleJourney.setJourneyPattern(journeyPattern);
					vehicleJourney.setRoute(route);

					// Duration since midnight
					// Link to timetable
					String chouetteTimetableId = AbstractConverter.composeObjectId(configuration.getObjectIdPrefix(), ObjectIdTypes.TIMETABLE_KEY,
							trip.getDayCodeRef());

					Duration tripDepartureTime = trip.getDepartureTime();
					if (tripDepartureTime.getStandardSeconds() >= 24 * 60 * 60) {
						// After midnight
						chouetteTimetableId += RegtoppTimetableParser.AFTER_MIDNIGHT_SUFFIX;
					}
					Timetable timetable = ObjectFactory.getTimetable(referential, chouetteTimetableId);
					timetable.addVehicleJourney(vehicleJourney);

					// TODO this must be precomputed instead of iterating over tens of thousands of records for each trip.
					for (RegtoppRouteTMS vehicleStop : importer.getRouteIndex()) {
						if (vehicleStop.getLineId().equals(lineId)) {
							if (vehicleStop.getRouteId().equals(trip.getRouteIdRef())) {
								if (vehicleStop.getDirection() == trip.getDirection()) {

									VehicleJourneyAtStop vehicleJourneyAtStop = ObjectFactory.getVehicleJourneyAtStop();
									vehicleJourneyAtStop.setVehicleJourney(vehicleJourney);

									// Default = board and alight
									vehicleJourneyAtStop.setBoardingAlightingPossibility(BoardingAlightingPossibilityEnum.BoardAndAlight);

									// TODO verify this
									if (vehicleStop.getDriverTimeArrival() != null) {
										vehicleJourneyAtStop.setArrivalTime(calculateTripVisitTime(tripDepartureTime, vehicleStop.getDriverTimeArrival()));
									} else {
										vehicleJourneyAtStop.setArrivalTime(calculateTripVisitTime(tripDepartureTime, vehicleStop.getDriverTimeDeparture()));
										vehicleJourneyAtStop.setBoardingAlightingPossibility(BoardingAlightingPossibilityEnum.BoardOnly);
									}

									if (vehicleStop.getDriverTimeDeparture() != null) {
										vehicleJourneyAtStop.setDepartureTime(calculateTripVisitTime(tripDepartureTime, vehicleStop.getDriverTimeDeparture()));
									} else {
										vehicleJourneyAtStop.setDepartureTime(calculateTripVisitTime(tripDepartureTime, vehicleStop.getDriverTimeArrival()));
										vehicleJourneyAtStop.setBoardingAlightingPossibility(BoardingAlightingPossibilityEnum.AlightOnly);
									}

									String chouetteStopPointId = AbstractConverter.composeObjectId(configuration.getObjectIdPrefix(),
											ObjectIdTypes.STOPPOINT_KEY, routeKey + vehicleStop.getSequenceNumberStop());

									StopPoint stopPoint = ObjectFactory.getStopPoint(referential, chouetteStopPointId);
									vehicleJourneyAtStop.setStopPoint(stopPoint);

								}
							}
						}
					}

				} else {
					log.info("Skipping unannouced trip: " + trip);
				}
			}
		}

		if (detectedTransportModes.size() == 1) {
			// Only one transport mode used for all routes/journeys
			line.setTransportModeName(detectedTransportModes.iterator().next());
		} else {
			line.setTransportModeName(TransportModeNameEnum.Other);
			line.setComment("Multiple transport modes: " + StringUtils.join(detectedTransportModes.toArray()));
		}

		// Link line to footnotes
		for (Footnote f : footnotes) {
			f.setLine(line);
		}

		// Post processing
		processRoutes(referential.getRoutes().values(), configuration);

	}

	@AllArgsConstructor
	@EqualsAndHashCode
	class RouteKey {
		@Getter
		String lineId;
		@Getter
		DirectionType direction;
		@Getter
		String routeId;

		public String toString() {
			return lineId + direction + routeId;
		}

		public RouteKey(String combined) {
			lineId = combined.substring(0, 4);
			direction = DirectionType.parseString(combined.substring(4, 5));
			routeId = combined.substring(5, 7);
		}
	}

	public static Time calculateTripVisitTime(Duration tripDepartureTime, Duration timeSinceTripDepatureTime) {
		// TODO Ugly ugly ugly

		LocalTime localTime = new LocalTime(0, 0, 0, 0)
				.plusSeconds((int) (tripDepartureTime.getStandardSeconds() + timeSinceTripDepatureTime.getStandardSeconds()));

		java.sql.Time sqlTime = new java.sql.Time(localTime.getHourOfDay(), localTime.getMinuteOfHour(), localTime.getSecondOfMinute());

		return sqlTime;

	}

	private TransportModeNameEnum convertTypeOfService(TransportType typeOfService) {
		switch (typeOfService) {
		case AirplaneOrAirportExpress:
			return TransportModeNameEnum.RapidTransit;
		case ExpressCoach:
			return TransportModeNameEnum.Coach;
		case FerryBoat:
			return TransportModeNameEnum.Ferry;
		case LocalBus:
			return TransportModeNameEnum.Bus;
		case Subway:
			return TransportModeNameEnum.Metro;
		case Train:
			return TransportModeNameEnum.LocalTrain;
		case Tram:
			return TransportModeNameEnum.Tramway;
		case Various:
			return TransportModeNameEnum.Other;
		default:
			return TransportModeNameEnum.Other;
		}
	}

	private StopPoint createStopPoint(Referential referential, Context context, RegtoppRouteTMS routeSegment, String chouetteStopPointId) throws Exception {

		RegtoppImporter importer = (RegtoppImporter) context.get(PARSER);
		RegtoppImportParameters configuration = (RegtoppImportParameters) context.get(CONFIGURATION);
		VersionHandler versionHandler = (VersionHandler) context.get(RegtoppConstant.VERSION_HANDLER);

		StopPoint stopPoint = ObjectFactory.getStopPoint(referential, chouetteStopPointId);
		stopPoint.setPosition(Integer.parseInt(routeSegment.getSequenceNumberStop()));

		String regtoppId = versionHandler.createStopPointId(routeSegment);
		String chouetteStopAreaId = AbstractConverter.composeObjectId(configuration.getObjectIdPrefix(), ObjectIdTypes.STOPAREA_KEY,
				regtoppId);

		StopArea stopArea = ObjectFactory.getStopArea(referential, chouetteStopAreaId);

		stopPoint.setContainedInStopArea(stopArea);

		return stopPoint;
	}

	private void addFootnote(String footnoteId, VehicleJourney vehicleJourney, List<Footnote> footnotes, RegtoppImporter importer) throws Exception {
		if (!"000".equals(footnoteId)) {
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

	private boolean footnoteAlreadyAdded(List<Footnote> addedFootnotes, String footnoteId) {
		for (Footnote existing : addedFootnotes) {
			if (existing.getCode().equals(footnoteId)) {
				return true;
			}
		}
		return false;
	}

	private void processRoutes(Collection<Route> values, RegtoppImportParameters configuration) {

		// Link opposite routes together
		for (Route r : values) {
			if (r.getOppositeRoute() == null) {
				RouteKey key = new RouteKey(AbstractConverter.extractOriginalId(r.getObjectId()));
				RouteKey oppositeKey = new RouteKey(key.getLineId(), key.getDirection().getOppositeDirection(), key.getRouteId());
				String oppositeObjectId = AbstractConverter.composeObjectId(configuration.getObjectIdPrefix(), ObjectIdTypes.ROUTE_KEY, oppositeKey.toString());
				for (Route opposite : values) {
					if (opposite.getObjectId().equals(oppositeObjectId)) {
						// Link routes
						r.setOppositeRoute(opposite);
						opposite.setOppositeRoute(r);
						break;
					}
				}
			}
		}

		for (Route route : values) {
			if (route.getName() == null) {
				// Set to last stop
				List<StopPoint> stopPoints = route.getStopPoints();
				if (stopPoints != null && !stopPoints.isEmpty()) {
					String lastStopAreaName = stopPoints.get(stopPoints.size() - 1).getContainedInStopArea().getName();
					route.setName(lastStopAreaName);
				}
			}

			route.setPublishedName(route.getName());

			for (JourneyPattern jp : route.getJourneyPatterns()) {

				// Set arrival and departure

				jp.setName(route.getName());
			}

			// default direction and wayback = R if opposite Route = A, else A

			if (route.getDirection() == null) {
				PTDirectionEnum oppositeDirection = route.getOppositeRoute() != null ? route.getOppositeRoute().getDirection() : PTDirectionEnum.R;
				route.setDirection(getOppositeDirection(oppositeDirection));
			}
			if (route.getWayBack() == null) {
				route.setWayBack(route.getOppositeRoute() != null && route.getWayBack().equals("A") ? "R" : "A");
			}

			processBoardingAlightingForRoute(route);

		}
	}

	private void processBoardingAlightingForRoute(Route route) {
		boolean invalidData = false;
		boolean usefullData = false;

		b1: for (JourneyPattern jp : route.getJourneyPatterns()) {
			for (VehicleJourney vj : jp.getVehicleJourneys()) {
				for (VehicleJourneyAtStop vjas : vj.getVehicleJourneyAtStops()) {
					if (!updateStopPoint(vjas)) {
						invalidData = true;
						break b1;
					}
				}
			}
		}
		if (!invalidData) {
			// check if every stoppoints were updated, complete missing ones to
			// normal; if all normal clean all
			for (StopPoint sp : route.getStopPoints()) {
				if (sp.getForAlighting() == null)
					sp.setForAlighting(AlightingPossibilityEnum.normal);
				if (sp.getForBoarding() == null)
					sp.setForBoarding(BoardingPossibilityEnum.normal);
			}
			for (StopPoint sp : route.getStopPoints()) {
				if (!sp.getForAlighting().equals(AlightingPossibilityEnum.normal)) {
					usefullData = true;
					break;
				}
				if (!sp.getForBoarding().equals(BoardingPossibilityEnum.normal)) {
					usefullData = true;
					break;
				}
			}

		}
		if (invalidData || !usefullData) {
			// remove useless informations
			for (StopPoint sp : route.getStopPoints()) {
				sp.setForAlighting(null);
				sp.setForBoarding(null);
			}
		}

	}

	private boolean updateStopPoint(VehicleJourneyAtStop vjas) {
		StopPoint sp = vjas.getStopPoint();
		BoardingPossibilityEnum forBoarding = getForBoarding(vjas.getBoardingAlightingPossibility());
		AlightingPossibilityEnum forAlighting = getForAlighting(vjas.getBoardingAlightingPossibility());
		if (sp.getForBoarding() != null && !sp.getForBoarding().equals(forBoarding))
			return false;
		if (sp.getForAlighting() != null && !sp.getForAlighting().equals(forAlighting))
			return false;
		sp.setForBoarding(forBoarding);
		sp.setForAlighting(forAlighting);
		return true;
	}

	private AlightingPossibilityEnum getForAlighting(BoardingAlightingPossibilityEnum boardingAlightingPossibility) {
		if (boardingAlightingPossibility == null)
			return AlightingPossibilityEnum.normal;
		switch (boardingAlightingPossibility) {
		case BoardAndAlight:
			return AlightingPossibilityEnum.normal;
		case AlightOnly:
			return AlightingPossibilityEnum.normal;
		case BoardOnly:
			return AlightingPossibilityEnum.forbidden;
		case NeitherBoardOrAlight:
			return AlightingPossibilityEnum.forbidden;
		case BoardAndAlightOnRequest:
			return AlightingPossibilityEnum.request_stop;
		case AlightOnRequest:
			return AlightingPossibilityEnum.request_stop;
		case BoardOnRequest:
			return AlightingPossibilityEnum.normal;
		}
		return null;
	}

	private BoardingPossibilityEnum getForBoarding(BoardingAlightingPossibilityEnum boardingAlightingPossibility) {
		if (boardingAlightingPossibility == null)
			return BoardingPossibilityEnum.normal;
		switch (boardingAlightingPossibility) {
		case BoardAndAlight:
			return BoardingPossibilityEnum.normal;
		case AlightOnly:
			return BoardingPossibilityEnum.forbidden;
		case BoardOnly:
			return BoardingPossibilityEnum.normal;
		case NeitherBoardOrAlight:
			return BoardingPossibilityEnum.forbidden;
		case BoardAndAlightOnRequest:
			return BoardingPossibilityEnum.request_stop;
		case AlightOnRequest:
			return BoardingPossibilityEnum.normal;
		case BoardOnRequest:
			return BoardingPossibilityEnum.request_stop;
		}
		return null;
	}

	private PTDirectionEnum getOppositeDirection(PTDirectionEnum direction) {
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
		ParserFactory.register(RegtoppLineParser.class.getName(), new ParserFactory() {
			@Override
			protected Parser create() {
				return new RegtoppLineParser();
			}
		});
	}

}
