package mobi.chouette.exchange.neptune.parser;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Context;
import mobi.chouette.common.XPPUtil;
import mobi.chouette.exchange.importer.Parser;
import mobi.chouette.exchange.importer.ParserFactory;
import mobi.chouette.exchange.importer.ParserUtils;
import mobi.chouette.exchange.neptune.Constant;
import mobi.chouette.exchange.neptune.JsonExtension;
import mobi.chouette.exchange.neptune.model.NeptuneObjectFactory;
import mobi.chouette.exchange.neptune.model.TimeSlot;
import mobi.chouette.exchange.neptune.validation.VehicleJourneyValidator;
import mobi.chouette.exchange.validation.ValidatorFactory;
import mobi.chouette.model.Company;
import mobi.chouette.model.JourneyFrequency;
import mobi.chouette.model.JourneyPattern;
import mobi.chouette.model.Route;
import mobi.chouette.model.StopPoint;
import mobi.chouette.model.VehicleJourney;
import mobi.chouette.model.VehicleJourneyAtStop;
import mobi.chouette.model.type.BoardingAlightingPossibilityEnum;
import mobi.chouette.model.type.JourneyCategoryEnum;
import mobi.chouette.model.type.TransportModeNameEnum;
import mobi.chouette.model.util.NeptuneUtil;
import mobi.chouette.model.util.ObjectFactory;
import mobi.chouette.model.util.ObjectIdTypes;
import mobi.chouette.model.util.Referential;

import org.joda.time.Duration;
import org.joda.time.LocalDateTime;
import org.joda.time.LocalTime;
import org.joda.time.Seconds;
import org.xmlpull.v1.XmlPullParser;
//import mobi.chouette.common.Constant;

@Log4j
public class VehicleJourneyParser implements Parser, Constant, JsonExtension {
	private static final String CHILD_TAG = "VehicleJourney";

	private static final Comparator<VehicleJourneyAtStop> VEHICLE_JOURNEY_AT_STOP_COMPARATOR = new Comparator<VehicleJourneyAtStop>() {

		@Override
		public int compare(VehicleJourneyAtStop o1, VehicleJourneyAtStop o2) {
			StopPoint p1 = o1.getStopPoint();
			StopPoint p2 = o2.getStopPoint();
			if (p1 != null && p2 != null) {
				int pos1 = p1.getPosition() == null ? 0 : p1.getPosition().intValue();
				int pos2 = p2.getPosition() == null ? 0 : p2.getPosition().intValue();
				return pos1 - pos2;
			}
			return 0;
		}
	};
	
	private Duration headwayFrequency = null;

	@Override
	public void parse(Context context) throws Exception {

		XmlPullParser xpp = (XmlPullParser) context.get(PARSER);
		Referential referential = (Referential) context.get(REFERENTIAL);
		NeptuneObjectFactory factory =  (NeptuneObjectFactory) context.get(NEPTUNE_OBJECT_FACTORY);

		xpp.require(XmlPullParser.START_TAG, null, CHILD_TAG);
		int columnNumber = xpp.getColumnNumber();
		int lineNumber = xpp.getLineNumber();
		
		AtomicInteger vehicleJourneyAtStopCounter = new AtomicInteger(0);

		VehicleJourneyValidator validator = (VehicleJourneyValidator) ValidatorFactory.create(
				VehicleJourneyValidator.class.getName(), context);

		VehicleJourney vehicleJourney = null;
		JourneyFrequency journeyFrequency = null;
		String objectId = null;
		while (xpp.nextTag() == XmlPullParser.START_TAG) {

			if (xpp.getName().equals("objectId")) {
				objectId = ParserUtils.getText(xpp.nextText());
				vehicleJourney = ObjectFactory.getVehicleJourney(referential, objectId);
				vehicleJourney.setFilled(true);
			} else if (xpp.getName().equals("objectVersion")) {
				Integer version = ParserUtils.getInt(xpp.nextText());
				vehicleJourney.setObjectVersion(version);
			} else if (xpp.getName().equals("creationTime")) {
				LocalDateTime creationTime = ParserUtils.getLocalDateTime(xpp.nextText());
				vehicleJourney.setCreationTime(creationTime);
			} else if (xpp.getName().equals("creatorId")) {
				vehicleJourney.setCreatorId(ParserUtils.getText(xpp.nextText()));
			} else if (xpp.getName().equals("comment")) {
				vehicleJourney.setComment(ParserUtils.getText(xpp.nextText()));
			} else if (xpp.getName().equals("facility")) {
				vehicleJourney.setFacility(ParserUtils.getText(xpp.nextText()));
			} else if (xpp.getName().equals("journeyPatternId")) {
				String journeyPatternId = ParserUtils.getText(xpp.nextText());
				validator.addJourneyPatternId(context, objectId, journeyPatternId);
				JourneyPattern journeyPattern = ObjectFactory.getJourneyPattern(referential, journeyPatternId);
				vehicleJourney.setJourneyPattern(journeyPattern);
			} else if (xpp.getName().equals("number")) {
				Long value = ParserUtils.getLong(xpp.nextText());
				vehicleJourney.setNumber(value);
			} else if (xpp.getName().equals("operatorId")) {
				String operatorId = ParserUtils.getText(xpp.nextText());
				validator.addOperatorId(context, objectId, operatorId);
				Company company = ObjectFactory.getCompany(referential, operatorId);
				vehicleJourney.setCompany(company);
			} else if (xpp.getName().equals("publishedJourneyIdentifier")) {
				vehicleJourney.setPublishedJourneyIdentifier(ParserUtils.getText(xpp.nextText()));
			} else if (xpp.getName().equals("publishedJourneyName")) {
				vehicleJourney.setPublishedJourneyName(ParserUtils.getText(xpp.nextText()));
			} else if (xpp.getName().equals("routeId")) {
				String routeId = ParserUtils.getText(xpp.nextText());
				validator.addRouteId(context, objectId, routeId);
				Route route = ObjectFactory.getRoute(referential, routeId);
				vehicleJourney.setRoute(route);
			} else if (xpp.getName().equals("lineIdShortcut")) {
				String lineIdShortcut = ParserUtils.getText(xpp.nextText());
				validator.addLineIdShortcut(context, objectId, lineIdShortcut);
			} else if (xpp.getName().equals("timeSlotId")) {
				String timeSlotId = ParserUtils.getText(xpp.nextText());
				validator.addTimeSlotId(context, objectId, timeSlotId);
				
				vehicleJourney.setJourneyCategory(JourneyCategoryEnum.Frequency);
				TimeSlot timeSlot = factory.getTimeSlot(timeSlotId);
				journeyFrequency = new JourneyFrequency();
				journeyFrequency.setVehicleJourney(vehicleJourney);
				journeyFrequency.setTimeband(ObjectFactory.getTimeband(referential, timeSlotId));
				journeyFrequency.setFirstDepartureTime(timeSlot.getFirstDepartureTimeInSlot());
				journeyFrequency.setLastDepartureTime(timeSlot.getLastDepartureTimeInSlot());
				if (headwayFrequency != null)
					journeyFrequency.setScheduledHeadwayInterval(headwayFrequency);
				
			} else if (xpp.getName().equals("transportMode")) {
				TransportModeNameEnum value = ParserUtils.getEnum(TransportModeNameEnum.class, xpp.nextText());
				vehicleJourney.setTransportMode(value);
			} else if (xpp.getName().equals("vehicleTypeIdentifier")) {
				vehicleJourney.setVehicleTypeIdentifier(xpp.nextText());
			} else if (xpp.getName().equals("vehicleJourneyAtStop")) {
				parseVehicleJourneyAtStop(context, vehicleJourney, journeyFrequency,vehicleJourneyAtStopCounter);
			} else {
				XPPUtil.skipSubTree(log, xpp);
			}
		}
		validator.addLocation(context, vehicleJourney, lineNumber, columnNumber);

		Collections.sort(vehicleJourney.getVehicleJourneyAtStops(), VEHICLE_JOURNEY_AT_STOP_COMPARATOR);
		setVehicleJourneyAtStopListOffset(vehicleJourney.getVehicleJourneyAtStops());
		validator.addLocation(context, vehicleJourney, lineNumber, columnNumber);
	}

	private void parseVehicleJourneyAtStop(Context context, VehicleJourney vehicleJourney, JourneyFrequency journeyFrequency, AtomicInteger vehicleJourneyAtStopCounter) throws Exception {

		XmlPullParser xpp = (XmlPullParser) context.get(PARSER);
		Referential referential = (Referential) context.get(REFERENTIAL);

		xpp.require(XmlPullParser.START_TAG, null, "vehicleJourneyAtStop");
		context.put(COLUMN_NUMBER, xpp.getColumnNumber());
		context.put(LINE_NUMBER, xpp.getLineNumber());

		String objectId = NeptuneUtil.convertIdType(vehicleJourney, ObjectIdTypes.VEHICLE_JOURNEY_AT_STOP_KEY)+"-"+vehicleJourneyAtStopCounter.incrementAndGet();
		
		VehicleJourneyAtStop vehicleJourneyAtStop = ObjectFactory.getVehicleJourneyAtStop(referential,objectId);

		VehicleJourneyValidator validator = (VehicleJourneyValidator) ValidatorFactory.create(
				VehicleJourneyValidator.class.getName(), context);

		Context vehicleJourneyAtStopContext = validator.addVehicleJourneyAtStopContext(context,
				vehicleJourney.getObjectId());
		int columnNumber = xpp.getColumnNumber();
		int lineNumber = xpp.getLineNumber();
		validator.addVehicleJourneyAtStopLocation(vehicleJourneyAtStopContext, lineNumber, columnNumber);
		String stopPointId = null;
		while (xpp.nextTag() == XmlPullParser.START_TAG) {
			if (xpp.getName().equals("vehicleJourneyId")) {
				String vehicleJourneyId = ParserUtils.getText(xpp.nextText());
				validator.addVehicleJourneyId(vehicleJourneyAtStopContext, vehicleJourneyId);
				vehicleJourneyAtStop.setVehicleJourney(vehicleJourney);
			} else if (xpp.getName().equals("boardingAlightingPossibility")) {
				BoardingAlightingPossibilityEnum value = ParserUtils.getEnum(BoardingAlightingPossibilityEnum.class,
						xpp.nextText());
				vehicleJourneyAtStop.setBoardingAlightingPossibility(value);
			} else if (xpp.getName().equals("stopPointId")) {
				stopPointId = ParserUtils.getText(xpp.nextText());
				validator.addStopPointId(vehicleJourneyAtStopContext, stopPointId);
				StopPoint stopPoint = ObjectFactory.getStopPoint(referential, stopPointId);
				vehicleJourneyAtStop.setStopPoint(stopPoint);
			} else if (xpp.getName().equals("order")) {
				Integer value = ParserUtils.getInt(xpp.nextText());
				validator.addOrder(vehicleJourneyAtStopContext, value);
			} else if (xpp.getName().equals("elapseDuration")) {
				Duration value = ParserUtils.getDuration(xpp.nextText());
				validator.addElapseDuration(vehicleJourneyAtStopContext, value);
				// Use the elapseDuration to compute departureTime and arrivalTime
				LocalTime time = new LocalTime(value.getMillis());
				vehicleJourneyAtStop.setDepartureTime(time);
				vehicleJourneyAtStop.setArrivalTime(time);
			} else if (xpp.getName().equals("arrivalTime")) {
				LocalTime value = ParserUtils.getLocalTime(xpp.nextText());
				vehicleJourneyAtStop.setArrivalTime(value);
			} else if (xpp.getName().equals("departureTime")) {
				LocalTime value = ParserUtils.getLocalTime(xpp.nextText());
				vehicleJourneyAtStop.setDepartureTime(value);
			} else if (xpp.getName().equals("waitingTime")) {
				Duration value = ParserUtils.getDurationFromTime(xpp.nextText());
				validator.addWaitingTime(vehicleJourneyAtStopContext, value);
			} else if (xpp.getName().equals("headwayFrequency")) {
				headwayFrequency = ParserUtils.getDuration(xpp.nextText());
				validator.addHeadwayFrequency(vehicleJourneyAtStopContext, headwayFrequency);
				if (journeyFrequency != null)
					journeyFrequency.setScheduledHeadwayInterval(headwayFrequency);
			} else {
				XPPUtil.skipSubTree(log, xpp);
			}
		}
		// protection from missing arrival time (mandatory for Chouette)
		if (vehicleJourneyAtStop.getArrivalTime() == null)
		{
			vehicleJourneyAtStop.setArrivalTime(vehicleJourneyAtStop.getDepartureTime());
		}
	}


	static {
		ParserFactory.register(VehicleJourneyParser.class.getName(), new ParserFactory() {
			private VehicleJourneyParser instance = new VehicleJourneyParser();

			@Override
			protected Parser create() {
				return instance;
			}
		});
	}
	
	/**
	 * Set the correct offset depending on journey stops times
	 * @param lstVehicleJourneyAtStop
	 */
	private void setVehicleJourneyAtStopListOffset(List<VehicleJourneyAtStop> lstVehicleJourneyAtStop) {
		VehicleJourneyAtStop previous_vjas = null;
		int currentArrivalOffset = 0;
		int currentDepartureOffset = 0;
		
		if (lstVehicleJourneyAtStop != null) {
			for (VehicleJourneyAtStop vjas: lstVehicleJourneyAtStop) {
				/** First stop */
				if(previous_vjas == null) {
					/** Check Offset between first arrival departure time */
					if(checkIfDiffAfterMidnight(vjas.getArrivalTime(), vjas.getDepartureTime())) {
						currentDepartureOffset += 1;
					}	
				}
				else {
					/** Check Offset between previous and current arrival time */
					if(checkIfDiffAfterMidnight(previous_vjas.getArrivalTime(), vjas.getArrivalTime())) {
						currentArrivalOffset += 1;
					}
					
					/** Check Offset between previous and current departure time */
					if(checkIfDiffAfterMidnight(previous_vjas.getDepartureTime(), vjas.getDepartureTime())) {
						currentDepartureOffset += 1;
					}
				}
				
				vjas.setArrivalDayOffset(currentArrivalOffset);
				vjas.setDepartureDayOffset(currentDepartureOffset);
				
				previous_vjas = vjas;
				
			}
		}
	}

	/**
	 * Check if lastTime belongs to the next day
	 *
	 * @param firstTime
	 * @param lastTime
	 * @return
	 */
	private boolean checkIfDiffAfterMidnight(LocalTime firstTime, LocalTime lastTime) {
		return lastTime.isBefore(firstTime);
	}
	
	
	
}
