package mobi.chouette.exchange.regtopp.parser.v12;

import static mobi.chouette.common.Constant.CONFIGURATION;
import static mobi.chouette.common.Constant.PARSER;
import static mobi.chouette.common.Constant.REFERENTIAL;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.joda.time.Duration;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Context;
import mobi.chouette.exchange.importer.Parser;
import mobi.chouette.exchange.importer.ParserFactory;
import mobi.chouette.exchange.regtopp.importer.RegtoppImportParameters;
import mobi.chouette.exchange.regtopp.model.AbstractRegtoppTripIndexTIX;
import mobi.chouette.exchange.regtopp.model.enums.AnnouncementType;
import mobi.chouette.exchange.regtopp.model.enums.TransportType;
import mobi.chouette.exchange.regtopp.model.importer.parser.RegtoppImporter;
import mobi.chouette.exchange.regtopp.model.importer.parser.index.Index;
import mobi.chouette.exchange.regtopp.model.v11.RegtoppDestinationDST;
import mobi.chouette.exchange.regtopp.model.v12.RegtoppRouteTMS;
import mobi.chouette.exchange.regtopp.model.v12.RegtoppTripIndexTIX;
import mobi.chouette.exchange.regtopp.parser.AbstractConverter;
import mobi.chouette.exchange.regtopp.parser.RouteKey;
import mobi.chouette.exchange.regtopp.parser.v11.RegtoppTimetableParser;
import mobi.chouette.model.Company;
import mobi.chouette.model.Footnote;
import mobi.chouette.model.JourneyPattern;
import mobi.chouette.model.Line;
import mobi.chouette.model.Route;
import mobi.chouette.model.StopPoint;
import mobi.chouette.model.Timetable;
import mobi.chouette.model.VehicleJourney;
import mobi.chouette.model.VehicleJourneyAtStop;
import mobi.chouette.model.type.TransportModeNameEnum;
import mobi.chouette.model.util.ObjectFactory;
import mobi.chouette.model.util.ObjectIdTypes;
import mobi.chouette.model.util.Referential;

@Log4j
public class RegtoppTripParser extends mobi.chouette.exchange.regtopp.parser.v11.RegtoppTripParser {

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

		// Add VehicleJourneys
		Index<AbstractRegtoppTripIndexTIX> tripIndex = importer.getTripIndex();
		for (AbstractRegtoppTripIndexTIX abstractTrip : tripIndex) {
			if (abstractTrip.getLineId().equals(lineId)) {
				RegtoppTripIndexTIX trip = (RegtoppTripIndexTIX) abstractTrip;
				
				if (trip.getNotificationType() == AnnouncementType.Announced) {

					// This is where we get the line number
					line.setNumber(trip.getLineNumberVisible());


					RouteKey routeKey = new RouteKey(trip.getLineId(), trip.getDirection(), trip.getRouteIdRef());
					String chouetteRouteId = AbstractConverter.composeObjectId(configuration.getObjectIdPrefix(), ObjectIdTypes.ROUTE_KEY, routeKey.toString());
					Route route = ObjectFactory.getRoute(referential, chouetteRouteId);

					String tripKey = trip.getLineId() + trip.getTripId();
					String chouetteVehicleJourneyId = AbstractConverter.composeObjectId(configuration.getObjectIdPrefix(), ObjectIdTypes.VEHICLEJOURNEY_KEY,
							tripKey);
					
					VehicleJourney vehicleJourney = ObjectFactory.getVehicleJourney(referential, chouetteVehicleJourneyId);

					// Add authority company
					String chouetteOperatorId = AbstractConverter.composeObjectId(configuration.getObjectIdPrefix(), ObjectIdTypes.COMPANY_KEY,
							trip.getOperatorCode());
					Company operator = ObjectFactory.getCompany(referential, chouetteOperatorId);
					if (!operator.isFilled()) {
						operator.setRegistrationNumber(trip.getOperatorCode());
						operator.setName("Operator " + trip.getOperatorCode());
						operator.setCode(trip.getOperatorCode());
						operator.setFilled(true);
					}
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

									String chouetteStopPointId = AbstractConverter.composeObjectId(configuration.getObjectIdPrefix(),
											ObjectIdTypes.STOPPOINT_KEY, routeKey + vehicleStop.getSequenceNumberStop());

									StopPoint stopPoint = ObjectFactory.getStopPoint(referential, chouetteStopPointId);
									addVehicleJourneyAtStop(vehicleJourney, tripDepartureTime, stopPoint,
											vehicleStop.getDriverTimeArrival(), vehicleStop.getDriverTimeDeparture());
								}
							}
						}
					}

				} else {
					// TODO log info message in action report about this
					log.info("Skipping unannouced trip: " + trip);
				}
			}
		}
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
