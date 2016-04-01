package mobi.chouette.exchange.regtopp.parser;

import java.sql.Time;

import org.apache.commons.lang.StringUtils;
import org.joda.time.Duration;

import lombok.Setter;
import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Context;
import mobi.chouette.exchange.importer.Parser;
import mobi.chouette.exchange.importer.ParserFactory;
import mobi.chouette.exchange.importer.Validator;
import mobi.chouette.exchange.regtopp.importer.RegtoppImportParameters;
import mobi.chouette.exchange.regtopp.model.RegtoppDayCodeDKO;
import mobi.chouette.exchange.regtopp.model.RegtoppDestinationDST;
import mobi.chouette.exchange.regtopp.model.RegtoppFootnoteMRK;
import mobi.chouette.exchange.regtopp.model.RegtoppLineLIN;
import mobi.chouette.exchange.regtopp.model.RegtoppRouteTMS;
import mobi.chouette.exchange.regtopp.model.RegtoppStopHPL;
import mobi.chouette.exchange.regtopp.model.RegtoppTripIndexTIX;
import mobi.chouette.exchange.regtopp.model.enums.AnnouncementType;
import mobi.chouette.exchange.regtopp.model.importer.parser.FileParserValidationError;
import mobi.chouette.exchange.regtopp.model.importer.parser.RegtoppException;
import mobi.chouette.exchange.regtopp.model.importer.parser.RegtoppImporter;
import mobi.chouette.exchange.regtopp.model.importer.parser.index.Index;
import mobi.chouette.exchange.regtopp.validation.Constant;
import mobi.chouette.exchange.regtopp.validation.RegtoppValidationReporter;
import mobi.chouette.exchange.validation.report.CheckPoint;
import mobi.chouette.exchange.validation.report.ValidationReport;
import mobi.chouette.model.Footnote;
import mobi.chouette.model.JourneyPattern;
import mobi.chouette.model.Line;
import mobi.chouette.model.Route;
import mobi.chouette.model.StopArea;
import mobi.chouette.model.StopPoint;
import mobi.chouette.model.VehicleJourney;
import mobi.chouette.model.VehicleJourneyAtStop;
import mobi.chouette.model.util.ObjectFactory;
import mobi.chouette.model.util.Referential;

@Log4j
public class RegtoppLineParser implements Parser, Validator, Constant {

	@Setter
	private String lineId = null;

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
			}
		}
	}

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

		String chouetteLineId = AbstractConverter.composeObjectId(configuration.getObjectIdPrefix(), Line.LINE_KEY, lineId, log);

		// Create the actual Chouette Line and put it in the "referential" space (which is later used by the LineImporterCommand)
		Line line = ObjectFactory.getLine(referential, chouetteLineId);

		// Find line number (TODO check if index exists)
		Index<RegtoppLineLIN> lineById = importer.getLineById();
		RegtoppLineLIN regtoppLine = lineById.getValue(lineId);
		if (regtoppLine != null) {
			line.setName(regtoppLine.getName());
			line.setNumber(line.getName()); // TODO set both fields, must check whether this is necessary or just plain stupid
		}

		// Get index over the TMS file
		// Index<RegtoppRouteTMS> routeIndex = importer.getRouteById();

		// Get index over all footnotes MRK file
		Index<RegtoppFootnoteMRK> footnoteIndex = importer.getFootnoteById();
		Index<RegtoppDestinationDST> destinationIndex = importer.getDestinationById();
		Index<RegtoppDayCodeDKO> dayCodeIndex = importer.getDayCodeById();

		// Add routes and journey patterns
		Index<RegtoppRouteTMS> routeIndex = importer.getRouteIndex();

		for (RegtoppRouteTMS routeSegment : routeIndex) {
			if (lineId.equals(routeSegment.getLineId())) {
				String routeKey = routeSegment.getLineId() + routeSegment.getDirection() + routeSegment.getRouteId();

				// Create route
				String chouetteRouteId = AbstractConverter.composeObjectId(configuration.getObjectIdPrefix(), Route.ROUTE_KEY, routeKey, log);
				Route route = ObjectFactory.getRoute(referential, chouetteRouteId);
				route.setLine(line);

				// Create journey pattern
				String chouetteJourneyPatternId = AbstractConverter.composeObjectId(configuration.getObjectIdPrefix(), Route.JOURNEYPATTERN_KEY, routeKey, log);

				JourneyPattern journeyPattern = ObjectFactory.getJourneyPattern(referential, chouetteJourneyPatternId);
				journeyPattern.setRoute(route);

				// Create stop point
				String chouetteStopPointId = AbstractConverter.composeObjectId(configuration.getObjectIdPrefix(), Route.STOPPOINT_KEY, routeKey, log);

				StopPoint stopPoint = createStopPoint(referential, context, routeSegment, chouetteStopPointId);

				// Add stop point to journey pattern AND route (for now)
				journeyPattern.addStopPoint(stopPoint);
				route.getStopPoints().add(stopPoint);

			}
		}

		// Add VehicleJourneys
		Index<RegtoppTripIndexTIX> tripIndex = importer.getTripIndex();
		for (RegtoppTripIndexTIX trip : tripIndex) {
			if (trip.getLineId().equals(lineId)) {
				if (trip.getNotificationType() == AnnouncementType.Announced) {

					String tripKey = trip.getLineId() + trip.getTripId();
					String routeKey = trip.getLineId() + trip.getDirection() + trip.getRouteId();

					String chouetteVehicleJourneyId = AbstractConverter.composeObjectId(configuration.getObjectIdPrefix(), Route.VEHICLEJOURNEY_KEY, tripKey,
							log);
					VehicleJourney vehicleJourney = ObjectFactory.getVehicleJourney(referential, chouetteVehicleJourneyId);

					addFootnote(trip.getRemarkId1(), vehicleJourney, importer);
					addFootnote(trip.getRemarkId2(), vehicleJourney, importer);

					RegtoppDestinationDST arrivalText = destinationIndex.getValue(trip.getDestinationIdArrival());

					// TODO unsure
					if (arrivalText != null) {
						vehicleJourney.setPublishedJourneyName(arrivalText.getDestinationText());
					}

					vehicleJourney.setPublishedJourneyIdentifier(StringUtils.trimToNull(trip.getLineNumberVisible()));

					String chouetteRouteId = AbstractConverter.composeObjectId(configuration.getObjectIdPrefix(), Route.ROUTE_KEY, routeKey, log);
					Route route = ObjectFactory.getRoute(referential, chouetteRouteId);

					String chouetteJourneyPatternId = AbstractConverter.composeObjectId(configuration.getObjectIdPrefix(), Route.JOURNEYPATTERN_KEY, routeKey,
							log);
					JourneyPattern journeyPattern = ObjectFactory.getJourneyPattern(referential, chouetteJourneyPatternId);

					vehicleJourney.setJourneyPattern(journeyPattern);
					vehicleJourney.setRoute(route);

					String chouetteStopPointId = AbstractConverter.composeObjectId(configuration.getObjectIdPrefix(), Route.STOPPOINT_KEY, routeKey, log);

					// Duration since midnight
					Duration tripDepartureTime = trip.getDepartureTime();

					for (RegtoppRouteTMS vehicleStop : routeIndex) {
						if (vehicleStop.getLineId().equals(lineId)) {
							if (vehicleStop.getRouteId().equals(trip.getRouteId())) {
								if (vehicleStop.getDirection() == trip.getDirection()) {
									VehicleJourneyAtStop vehicleJourneyAtStop = ObjectFactory.getVehicleJourneyAtStop();
									StopPoint stopPoint = referential.getStopPoints().get(chouetteStopPointId);
									vehicleJourneyAtStop.setStopPoint(stopPoint);

									Duration arrivalTime = tripDepartureTime.plus(vehicleStop.getDriverTimeArrival());
									Duration departureTime = tripDepartureTime.plus(vehicleStop.getDriverTimeDeparture());
									
									// TODO verify this
									vehicleJourneyAtStop.setArrivalTime(new Time(arrivalTime.getMillis()));
									vehicleJourneyAtStop.setDepartureTime(new Time(departureTime.getMillis()));

								}
							}
						}
					}
				} else {
					log.info("Skipping unannouced trip: " + trip);
				}
			}
		}
	}

	private StopPoint createStopPoint(Referential referential, Context context, RegtoppRouteTMS routeSegment, String chouetteStopPointId) throws Exception {

		RegtoppImporter importer = (RegtoppImporter) context.get(PARSER);
		RegtoppImportParameters configuration = (RegtoppImportParameters) context.get(CONFIGURATION);

		StopPoint stopPoint = ObjectFactory.getStopPoint(referential, chouetteStopPointId);
		stopPoint.setPosition(routeSegment.getSequenceNumberStop());

		String chouetteStopAreaId = AbstractConverter.composeObjectId(configuration.getObjectIdPrefix(), Route.STOPAREA_KEY, routeSegment.getStopId(), log);

		StopArea stopArea = createStopArea(referential, routeSegment, importer, chouetteStopAreaId);

		stopPoint.setContainedInStopArea(stopArea);

		return stopPoint;
	}

	private StopArea createStopArea(Referential referential, RegtoppRouteTMS routeSegment, RegtoppImporter importer, String chouetteStopAreaId)
			throws Exception {
		StopArea stopArea = ObjectFactory.getStopArea(referential, chouetteStopAreaId);

		if (stopArea.getX() == null) {
			// Not initialized
			Index<RegtoppStopHPL> stopById = importer.getStopById();
			RegtoppStopHPL stop = stopById.getValue(routeSegment.getStopId());

			// TODO coordinate system conversion
			stopArea.setX(stop.getStopLon());
			stopArea.setY(stop.getStopLat());
			stopArea.setName(stop.getFullName());
		}
		return stopArea;
	}

	private void addFootnote(String remarkId1, VehicleJourney vehicleJourney, RegtoppImporter importer) throws Exception {
		if (!"000".equals(remarkId1)) {

			Index<RegtoppFootnoteMRK> index = importer.getFootnoteById();
			RegtoppFootnoteMRK footnote1 = index.getValue(remarkId1);

			Footnote f = new Footnote();
			f.setLabel(footnote1.getDescription());
			f.setKey(footnote1.getFootnoteId());

			vehicleJourney.getFootnotes().add(f);
		}
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
