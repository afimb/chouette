package mobi.chouette.exchange.regtopp.importer.parser.v11;

import static mobi.chouette.common.Constant.CONFIGURATION;
import static mobi.chouette.common.Constant.MAIN_VALIDATION_REPORT;
import static mobi.chouette.common.Constant.PARSER;
import static mobi.chouette.common.Constant.REFERENTIAL;
import static mobi.chouette.exchange.regtopp.RegtoppConstant.REGTOPP_REPORTER;
import static mobi.chouette.exchange.regtopp.validation.Constant.REGTOPP_FILE_TIX;

import java.sql.Time;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.joda.time.Duration;
import org.joda.time.LocalTime;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Context;
import mobi.chouette.exchange.importer.Parser;
import mobi.chouette.exchange.importer.ParserFactory;
import mobi.chouette.exchange.regtopp.importer.RegtoppImportParameters;
import mobi.chouette.exchange.regtopp.importer.RegtoppImporter;
import mobi.chouette.exchange.regtopp.importer.index.Index;
import mobi.chouette.exchange.regtopp.importer.parser.AbstractConverter;
import mobi.chouette.exchange.regtopp.importer.parser.FileParserValidationError;
import mobi.chouette.exchange.regtopp.importer.parser.LineSpecificParser;
import mobi.chouette.exchange.regtopp.importer.parser.RouteKey;
import mobi.chouette.exchange.regtopp.model.AbstractRegtoppTripIndexTIX;
import mobi.chouette.exchange.regtopp.model.enums.TransportType;
import mobi.chouette.exchange.regtopp.model.v11.RegtoppDestinationDST;
import mobi.chouette.exchange.regtopp.model.v11.RegtoppFootnoteMRK;
import mobi.chouette.exchange.regtopp.model.v11.RegtoppRouteTDA;
import mobi.chouette.exchange.regtopp.model.v11.RegtoppTripIndexTIX;
import mobi.chouette.exchange.regtopp.validation.RegtoppException;
import mobi.chouette.exchange.regtopp.validation.RegtoppValidationReporter;
import mobi.chouette.exchange.validation.report.ValidationReport;
import mobi.chouette.model.Company;
import mobi.chouette.model.Footnote;
import mobi.chouette.model.JourneyPattern;
import mobi.chouette.model.Line;
import mobi.chouette.model.Route;
import mobi.chouette.model.StopPoint;
import mobi.chouette.model.Timetable;
import mobi.chouette.model.VehicleJourney;
import mobi.chouette.model.VehicleJourneyAtStop;
import mobi.chouette.model.type.BoardingAlightingPossibilityEnum;
import mobi.chouette.model.type.TransportModeNameEnum;
import mobi.chouette.model.util.ObjectFactory;
import mobi.chouette.model.util.ObjectIdTypes;
import mobi.chouette.model.util.Referential;

@Log4j
public class RegtoppTripParser extends LineSpecificParser {

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

		validateTIXIndex(context, importer, validationReporter);
	}

	protected void validateTIXIndex(Context context, RegtoppImporter importer, RegtoppValidationReporter validationReporter) throws Exception {
		if (importer.hasTIXImporter()) {
			validationReporter.reportSuccess(context, REGTOPP_FILE_TIX, RegtoppTripIndexTIX.FILE_EXTENSION);

			Index<AbstractRegtoppTripIndexTIX> index = importer.getTripIndex();

			if (index.getLength() == 0) {
				FileParserValidationError fileError = new FileParserValidationError(RegtoppTripIndexTIX.FILE_EXTENSION, 0, null,
						RegtoppException.ERROR.FILE_WITH_NO_ENTRY, null, "Empty file");
				validationReporter.reportError(context, new RegtoppException(fileError), RegtoppTripIndexTIX.FILE_EXTENSION);
			}

			for (AbstractRegtoppTripIndexTIX bean : index) {
				try {
					// Call index validator
					index.validate(bean, importer);
				} catch (Exception ex) {
					log.error(ex);
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

		RegtoppImporter importer = (RegtoppImporter) context.get(PARSER);
		RegtoppImportParameters configuration = (RegtoppImportParameters) context.get(CONFIGURATION);

		String chouetteLineId = AbstractConverter.composeObjectId(configuration.getObjectIdPrefix(), Line.LINE_KEY, lineId);
		Line line = ObjectFactory.getLine(referential, chouetteLineId);
		List<Footnote> footnotes = line.getFootnotes();

		Index<RegtoppDestinationDST> destinationIndex = importer.getDestinationById();
		Index<RegtoppRouteTDA> routeIndex = importer.getRouteSegmentByLineNumber();

		// Add VehicleJourneys
		Index<AbstractRegtoppTripIndexTIX> tripIndex = importer.getTripIndex();
		for (AbstractRegtoppTripIndexTIX abstractTrip : tripIndex) {
			if (abstractTrip.getLineId().equals(lineId)) {
				RegtoppTripIndexTIX trip = (RegtoppTripIndexTIX) abstractTrip;

				try {
					// This is where we get the line number
					line.setNumber(trip.getLineNumberVisible());

					RouteKey routeKey = new RouteKey(trip.getLineId(), trip.getDirection(), trip.getRouteIdRef());
					String chouetteRouteId = AbstractConverter.composeObjectId(configuration.getObjectIdPrefix(), ObjectIdTypes.ROUTE_KEY, routeKey.toString());
					Route route = ObjectFactory.getRoute(referential, chouetteRouteId);

					String tripKey = trip.getLineId() + trip.getTripId();
					String chouetteVehicleJourneyId = AbstractConverter.composeObjectId(configuration.getObjectIdPrefix(), ObjectIdTypes.VEHICLEJOURNEY_KEY,
							tripKey);

					String chouetteJourneyPatternId = AbstractConverter.composeObjectId(configuration.getObjectIdPrefix(), ObjectIdTypes.JOURNEYPATTERN_KEY,
							routeKey.toString());
					JourneyPattern journeyPattern = ObjectFactory.getJourneyPattern(referential, chouetteJourneyPatternId);
					
					VehicleJourney vehicleJourney = ObjectFactory.getVehicleJourney(referential, chouetteVehicleJourneyId);

					// Add operator company
					Company operator = createOperator(referential, configuration, trip.getOperatorCode());
					vehicleJourney.setCompany(operator);

					addFootnote(trip.getFootnoteId1Ref(), vehicleJourney, footnotes, importer);
					addFootnote(trip.getFootnoteId2Ref(), vehicleJourney, footnotes, importer);

					RegtoppDestinationDST departureText = destinationIndex.getValue(trip.getDestinationIdDepartureRef()); // Turens bestemmelsessted

					// TODO unsure
					if (departureText != null) {
						vehicleJourney.setPublishedJourneyName(departureText.getDestinationText());
					}

					vehicleJourney.setPublishedJourneyIdentifier(StringUtils.trimToNull(trip.getLineNumberVisible()));
					vehicleJourney.setTransportMode(convertTypeOfService(trip.getTypeOfService()));
					vehicleJourney.setJourneyPattern(journeyPattern);
					vehicleJourney.setRoute(route);

					// Link to timetable
					Duration tripDepartureTime = linkVehicleJourneyToTimetable(referential, configuration, trip, vehicleJourney);

					for (StopPoint p : journeyPattern.getStopPoints()) {
						// Warn: Hack. Using comment as temporary holder
						RegtoppRouteTDA vehicleStop = routeIndex.getValue(p.getComment());
						try {
							addVehicleJourneyAtStop(vehicleJourney, tripDepartureTime, p,
									vehicleStop.getDriverTimeArrival(), vehicleStop.getDriverTimeDeparture());
							
						} catch (Exception e) {
							log.error("Error parsing vehicleStop: " + vehicleStop, e);
						}

					}
				} catch (Exception e) {
					log.error("Error parsing trip: " + trip, e);
					throw e;
				}
			}
		}
	}

	protected Duration linkVehicleJourneyToTimetable(Referential referential, RegtoppImportParameters configuration, AbstractRegtoppTripIndexTIX trip,
			VehicleJourney vehicleJourney) {
		String chouetteTimetableId = AbstractConverter.composeObjectId(configuration.getObjectIdPrefix(), ObjectIdTypes.TIMETABLE_KEY,
				trip.getDayCodeRef());

		// Duration since midnight
		Duration tripDepartureTime = trip.getDepartureTime();
		if (tripDepartureTime.getStandardSeconds() >= 24 * 60 * 60) {
			// After midnight
			chouetteTimetableId += RegtoppTimetableParser.AFTER_MIDNIGHT_SUFFIX;
		}
		Timetable timetable = ObjectFactory.getTimetable(referential, chouetteTimetableId);
		timetable.addVehicleJourney(vehicleJourney);
		return tripDepartureTime;
	}

	protected Company createOperator(Referential referential, RegtoppImportParameters configuration, String operatorCode) {
		String chouetteOperatorId = AbstractConverter.composeObjectId(configuration.getObjectIdPrefix(), ObjectIdTypes.COMPANY_KEY,
				operatorCode);
		Company operator = ObjectFactory.getCompany(referential, chouetteOperatorId);
		if (!operator.isFilled()) {
			operator.setRegistrationNumber(operatorCode);
			operator.setName("Operator " + operatorCode);
			operator.setCode(operatorCode);
			operator.setFilled(true);
		}
		return operator;
	}

	protected VehicleJourneyAtStop addVehicleJourneyAtStop(VehicleJourney vehicleJourney, Duration tripDepartureTime, StopPoint p, Duration driverTimeArrival,
			Duration driverTimeDeparture) {
		VehicleJourneyAtStop vehicleJourneyAtStop = ObjectFactory.getVehicleJourneyAtStop();
		vehicleJourneyAtStop.setVehicleJourney(vehicleJourney);

		// Default = board and alight
		vehicleJourneyAtStop.setBoardingAlightingPossibility(BoardingAlightingPossibilityEnum.BoardAndAlight);

		if (driverTimeArrival == null && driverTimeDeparture == null) {
			// Both 999
			vehicleJourneyAtStop.setBoardingAlightingPossibility(BoardingAlightingPossibilityEnum.NeitherBoardOrAlight);
			// What to do with passing times?
		} else {

			// TODO verify this
			if (driverTimeArrival != null) {
				vehicleJourneyAtStop.setArrivalTime(calculateTripVisitTime(tripDepartureTime, driverTimeArrival));
			} else {
				vehicleJourneyAtStop.setArrivalTime(calculateTripVisitTime(tripDepartureTime, driverTimeDeparture));
				vehicleJourneyAtStop.setBoardingAlightingPossibility(BoardingAlightingPossibilityEnum.BoardOnly);
			}

			if (driverTimeDeparture != null) {
				vehicleJourneyAtStop.setDepartureTime(calculateTripVisitTime(tripDepartureTime, driverTimeDeparture));
			} else {
				vehicleJourneyAtStop.setDepartureTime(calculateTripVisitTime(tripDepartureTime, driverTimeArrival));
				vehicleJourneyAtStop.setBoardingAlightingPossibility(BoardingAlightingPossibilityEnum.AlightOnly);
			}

		}
		vehicleJourneyAtStop.setStopPoint(p);
		return vehicleJourneyAtStop;
	}

	public static Time calculateTripVisitTime(Duration tripDepartureTime, Duration timeSinceTripDepatureTime) {
		// TODO Ugly ugly ugly

		LocalTime localTime = new LocalTime(0, 0, 0, 0)
				.plusSeconds((int) (tripDepartureTime.getStandardSeconds() + timeSinceTripDepatureTime.getStandardSeconds()));

		java.sql.Time sqlTime = new java.sql.Time(localTime.getHourOfDay(), localTime.getMinuteOfHour(), localTime.getSecondOfMinute());

		return sqlTime;

	}

	public static TransportModeNameEnum convertTypeOfService(TransportType typeOfService) {
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

	public void addFootnote(String footnoteId, VehicleJourney vehicleJourney, List<Footnote> footnotes, RegtoppImporter importer) throws Exception {
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

	public static boolean footnoteAlreadyAdded(List<Footnote> addedFootnotes, String footnoteId) {
		for (Footnote existing : addedFootnotes) {
			if (existing.getCode().equals(footnoteId)) {
				return true;
			}
		}
		return false;
	}

	static {
		ParserFactory.register(RegtoppTripParser.class.getName(), new ParserFactory() {
			@Override
			protected Parser create() {
				return new RegtoppTripParser();
			}
		});
	}

}
