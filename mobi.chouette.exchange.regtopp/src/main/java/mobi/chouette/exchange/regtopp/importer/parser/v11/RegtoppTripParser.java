package mobi.chouette.exchange.regtopp.importer.parser.v11;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Context;
import mobi.chouette.exchange.importer.Parser;
import mobi.chouette.exchange.importer.ParserFactory;
import mobi.chouette.exchange.regtopp.RegtoppConstant;
import mobi.chouette.exchange.regtopp.importer.RegtoppImportParameters;
import mobi.chouette.exchange.regtopp.importer.RegtoppImporter;
import mobi.chouette.exchange.regtopp.importer.index.Index;
import mobi.chouette.exchange.regtopp.importer.index.v11.DaycodeById;
import mobi.chouette.exchange.regtopp.importer.parser.LineSpecificParser;
import mobi.chouette.exchange.regtopp.importer.parser.ObjectIdCreator;
import mobi.chouette.exchange.regtopp.importer.parser.RouteKey;
import mobi.chouette.exchange.regtopp.importer.parser.TripVisitTimeCalculator;
import mobi.chouette.exchange.regtopp.model.AbstractRegtoppTripIndexTIX;
import mobi.chouette.exchange.regtopp.model.enums.TransportType;
import mobi.chouette.exchange.regtopp.model.v11.*;
import mobi.chouette.model.*;
import mobi.chouette.model.type.BoardingAlightingPossibilityEnum;
import mobi.chouette.model.type.TransportModeNameEnum;
import mobi.chouette.model.util.ObjectFactory;
import mobi.chouette.model.util.Referential;
import org.apache.commons.lang.StringUtils;
import org.joda.time.Duration;

import java.sql.Time;
import java.util.List;

import static mobi.chouette.common.Constant.*;

@Log4j
public class RegtoppTripParser extends LineSpecificParser {

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
		String calendarStartDate = (String) context.get(RegtoppConstant.CALENDAR_START_DATE);

		String chouetteLineId = ObjectIdCreator.createLineId(configuration, lineId, calendarStartDate);
		Line line = ObjectFactory.getLine(referential, chouetteLineId);
		List<Footnote> footnotes = line.getFootnotes();

		Index<RegtoppDestinationDST> destinationIndex = importer.getDestinationById();
		Index<RegtoppRouteTDA> routeIndex = importer.getRouteSegmentByLineNumber();

		DaycodeById dayCodeIndex = (DaycodeById) importer.getDayCodeById();
		RegtoppDayCodeHeaderDKO dayCodeHeader = dayCodeIndex.getHeader();

		// Add VehicleJourneys
		Index<AbstractRegtoppTripIndexTIX> tripIndex = importer.getTripIndex();
		for (AbstractRegtoppTripIndexTIX abstractTrip : tripIndex) {
			if (abstractTrip.getLineId().equals(lineId)) {
				RegtoppTripIndexTIX trip = (RegtoppTripIndexTIX) abstractTrip;

				try {
					// This is where we get the line number
					line.setNumber(trip.getLineNumberVisible());

					RouteKey routeKey = new RouteKey(trip.getLineId(), trip.getDirection(), trip.getRouteIdRef(),calendarStartDate);
					String chouetteRouteId = ObjectIdCreator.createRouteId(configuration, routeKey);
					Route route = ObjectFactory.getRoute(referential, chouetteRouteId);

					String chouetteJourneyPatternId = ObjectIdCreator.createJourneyPatternId(configuration,routeKey);
					JourneyPattern journeyPattern = ObjectFactory.getJourneyPattern(referential, chouetteJourneyPatternId);

					String chouetteVehicleJourneyId = ObjectIdCreator.createVehicleJourneyId(configuration, trip.getLineId(), trip.getTripId(), calendarStartDate);
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

					boolean byRequestOnly = false;
					if (trip.getTypeOfService() == TransportType.FlexibleBus) {
						byRequestOnly = true;
						line.setFlexibleService(Boolean.TRUE);
					}

					// Link to timetable
					linkVehicleJourneyToTimetable(referential, configuration, trip, vehicleJourney, dayCodeHeader);

					for (StopPoint p : journeyPattern.getStopPoints()) {
						// Warn: Hack. Using comment as temporary holder
						RegtoppRouteTDA vehicleStop = routeIndex.getValue(p.getComment());
						try {
							addVehicleJourneyAtStop(vehicleJourney, trip.getDepartureTime(), p, vehicleStop.getDriverTimeArrival(),
									vehicleStop.getDriverTimeDeparture(), byRequestOnly);

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

		estimateMissingPassingTimes(referential);
	}

	protected void estimateMissingPassingTimes(Referential referential) {
		for (VehicleJourney vj : referential.getVehicleJourneys().values()) {
			for (int i = 0; i < vj.getVehicleJourneyAtStops().size(); i++) {
				VehicleJourneyAtStop vStop = vj.getVehicleJourneyAtStops().get(i);
				if (vStop.getDepartureTime() == null && vStop.getArrivalTime() == null) {
					// Estimate
					if (i == 0) {
						// First stop
						// Use second stop time
						VehicleJourneyAtStop after = vj.getVehicleJourneyAtStops().get(i + 1);
						vStop.setDepartureTime(after.getDepartureTime());
						vStop.setArrivalTime(after.getArrivalTime());
					} else if (i == vj.getVehicleJourneyAtStops().size() - 1) {
						// Last stop
						VehicleJourneyAtStop before = vj.getVehicleJourneyAtStops().get(i - 1);
						vStop.setDepartureTime(before.getDepartureTime());
						vStop.setArrivalTime(before.getArrivalTime());
					} else {
						// In the middle of journey pattern
						VehicleJourneyAtStop before = vj.getVehicleJourneyAtStops().get(i - 1);
						VehicleJourneyAtStop after = vj.getVehicleJourneyAtStops().get(i + 1);

						vStop.setArrivalTime(interpolate(before.getArrivalTime(), after.getArrivalTime()));
						vStop.setDepartureTime(interpolate(before.getDepartureTime(), after.getDepartureTime()));
					}
				}
			}
		}
	}

	protected Time interpolate(Time start, Time end) {
		Time t = null;

		if (start != null && end != null) {
			long duration = end.getTime() - start.getTime();
			t = new Time(start.getTime() + (duration / 2));
		}
		return t;
	}

	protected void linkVehicleJourneyToTimetable(Referential referential, RegtoppImportParameters configuration, AbstractRegtoppTripIndexTIX trip,
			VehicleJourney vehicleJourney, RegtoppDayCodeHeaderDKO header) {

		String chouetteTimetableId = ObjectIdCreator.createTimetableId(configuration, trip.getAdminCode(), trip.getDayCodeRef(), header);


		Timetable timetable = ObjectFactory.getTimetable(referential, chouetteTimetableId);
		timetable.addVehicleJourney(vehicleJourney);
	}

	protected Company createOperator(Referential referential, RegtoppImportParameters configuration, String operatorCode) {
		String chouetteOperatorId = ObjectIdCreator.createOperatorId(configuration, operatorCode);
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
			Duration driverTimeDeparture, boolean byRequestOnly) {
		VehicleJourneyAtStop vehicleJourneyAtStop = ObjectFactory.getVehicleJourneyAtStop();
		vehicleJourneyAtStop.setVehicleJourney(vehicleJourney);

		// Default = board and alight
		vehicleJourneyAtStop.setBoardingAlightingPossibility(BoardingAlightingPossibilityEnum.BoardAndAlight);

		if (driverTimeArrival == null && driverTimeDeparture == null) {
			// Both 999
			vehicleJourneyAtStop.setBoardingAlightingPossibility(BoardingAlightingPossibilityEnum.NeitherBoardOrAlight);
			// What to do with passing times?
		} else {
			if (driverTimeArrival != null) {
				setArrival(tripDepartureTime, driverTimeArrival, vehicleJourneyAtStop);
			} else {
				setArrival(tripDepartureTime, driverTimeDeparture, vehicleJourneyAtStop);
				vehicleJourneyAtStop.setBoardingAlightingPossibility(BoardingAlightingPossibilityEnum.BoardOnly);
			}

			if (driverTimeDeparture != null) {
				setDeparture(tripDepartureTime, driverTimeDeparture, vehicleJourneyAtStop);
			} else {
				setDeparture(tripDepartureTime, driverTimeArrival, vehicleJourneyAtStop);
				vehicleJourneyAtStop.setBoardingAlightingPossibility(BoardingAlightingPossibilityEnum.AlightOnly);
			}
		}

		vehicleJourneyAtStop.setStopPoint(p);

		if (byRequestOnly) {
			// Override alighting/boarding
			vehicleJourneyAtStop.setBoardingAlightingPossibility(BoardingAlightingPossibilityEnum.BoardAndAlightOnRequest);
		}

		return vehicleJourneyAtStop;
	}

	private void setArrival(Duration tripDepartureTime, Duration driverTimeArrival, VehicleJourneyAtStop vehicleJourneyAtStop) {
		TripVisitTimeCalculator.TripVisitTime tripVisitTime = TripVisitTimeCalculator.calculateTripVisitTime(tripDepartureTime, driverTimeArrival);
		vehicleJourneyAtStop.setArrivalTime(tripVisitTime.getTime());
		vehicleJourneyAtStop.setArrivalDayOffset(tripVisitTime.getDayOffset());
	}

	private void setDeparture(Duration tripDepartureTime, Duration driverTimeDeparture, VehicleJourneyAtStop vehicleJourneyAtStop) {
		TripVisitTimeCalculator.TripVisitTime tripVisitTime =  TripVisitTimeCalculator.calculateTripVisitTime(tripDepartureTime, driverTimeDeparture);
		vehicleJourneyAtStop.setDepartureTime(tripVisitTime.getTime());
		vehicleJourneyAtStop.setDepartureDayOffset(tripVisitTime.getDayOffset());
	}

	protected TransportModeNameEnum convertTypeOfService(TransportType typeOfService) {
		switch (typeOfService) {
		case AirplaneOrAirportExpress:
		case ExpressCoach:
			return TransportModeNameEnum.Coach;
		case FerryBoat:
			return TransportModeNameEnum.Ferry;
		case SchoolBus:
		case FlexibleBus:
		case Bus:
			return TransportModeNameEnum.Bus;
		case Subway:
			return TransportModeNameEnum.Metro;
		case Train:
			return TransportModeNameEnum.Train;
		case Tram:
			return TransportModeNameEnum.Tramway;
		case Various:
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
