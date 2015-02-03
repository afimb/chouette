package mobi.chouette.exchange.neptune.parser;

import java.sql.Time;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Constant;
import mobi.chouette.common.Context;
import mobi.chouette.exchange.importer.Parser;
import mobi.chouette.exchange.importer.ParserFactory;
import mobi.chouette.exchange.importer.ParserUtils;
import mobi.chouette.exchange.importer.XPPUtil;
import mobi.chouette.model.Company;
import mobi.chouette.model.JourneyPattern;
import mobi.chouette.model.Line;
import mobi.chouette.model.Route;
import mobi.chouette.model.StopPoint;
import mobi.chouette.model.VehicleJourney;
import mobi.chouette.model.VehicleJourneyAtStop;
import mobi.chouette.model.type.BoardingAlightingPossibilityEnum;
import mobi.chouette.model.type.ServiceStatusValueEnum;
import mobi.chouette.model.type.TransportModeNameEnum;
import mobi.chouette.model.util.ObjectFactory;
import mobi.chouette.model.util.Referential;

import org.xmlpull.v1.XmlPullParser;

@Log4j
public class VehicleJourneyParser implements Parser, Constant {
	private static final String CHILD_TAG = "VehicleJourney";

	private static final Comparator<VehicleJourneyAtStop> VEHICLE_JOURNEY_AT_STOP_COMPARATOR = new Comparator<VehicleJourneyAtStop>() {

		@Override
		public int compare(VehicleJourneyAtStop o1, VehicleJourneyAtStop o2) {
			StopPoint p1 = o1.getStopPoint();
			StopPoint p2 = o2.getStopPoint();
			if (p1 != null && p2 != null) {
				return p1.getPosition() - p2.getPosition();
			}
			return 0;
		}
	};

	@Override
	public void parse(Context context) throws Exception {

		XmlPullParser xpp = (XmlPullParser) context.get(PARSER);
		Referential referential = (Referential) context.get(REFERENTIAL);

		xpp.require(XmlPullParser.START_TAG, null, CHILD_TAG);
		context.put(COLUMN_NUMBER, xpp.getColumnNumber());
		context.put(LINE_NUMBER, xpp.getLineNumber());

		VehicleJourney vehicleJourney = null;
		while (xpp.nextTag() == XmlPullParser.START_TAG) {

			if (xpp.getName().equals("objectId")) {
				String objectId = ParserUtils.getText(xpp.nextText());
				vehicleJourney = ObjectFactory.getVehicleJourney(referential,
						objectId);
			} else if (xpp.getName().equals("objectVersion")) {
				Integer version = ParserUtils.getInt(xpp.nextText());
				vehicleJourney.setObjectVersion(version);
			} else if (xpp.getName().equals("creationTime")) {
				Date creationTime = ParserUtils.getSQLDateTime(xpp.nextText());
				vehicleJourney.setCreationTime(creationTime);
			} else if (xpp.getName().equals("creatorId")) {
				vehicleJourney
						.setCreatorId(ParserUtils.getText(xpp.nextText()));
			} else if (xpp.getName().equals("comment")) {
				vehicleJourney.setComment(ParserUtils.getText(xpp.nextText()));
			} else if (xpp.getName().equals("facility")) {
				vehicleJourney.setFacility(ParserUtils.getText(xpp.nextText()));
			} else if (xpp.getName().equals("journeyPatternId")) {
				String objectId = ParserUtils.getText(xpp.nextText());
				JourneyPattern journeyPattern = ObjectFactory
						.getJourneyPattern(referential, objectId);
				vehicleJourney.setJourneyPattern(journeyPattern);
			} else if (xpp.getName().equals("number")) {
				Long value = ParserUtils.getLong(xpp.nextText());
				vehicleJourney.setNumber(value);
			} else if (xpp.getName().equals("operatorId")) {
				String objectId = ParserUtils.getText(xpp.nextText());
				Company company = ObjectFactory.getCompany(referential,
						objectId);
				vehicleJourney.setCompany(company);
			} else if (xpp.getName().equals("publishedJourneyIdentifier")) {
				vehicleJourney.setPublishedJourneyIdentifier(ParserUtils
						.getText(xpp.nextText()));
			} else if (xpp.getName().equals("publishedJourneyName")) {
				vehicleJourney.setPublishedJourneyName(ParserUtils.getText(xpp
						.nextText()));
			} else if (xpp.getName().equals("routeId")) {
				String objectId = ParserUtils.getText(xpp.nextText());
				Route route = ObjectFactory.getRoute(referential, objectId);
				vehicleJourney.setRoute(route);
			} else if (xpp.getName().equals("lineIdShortcut")) {
				String objectId = ParserUtils.getText(xpp.nextText());
				Line line = ObjectFactory.getLine(referential, objectId);
				// TODO lineIdShortcut

			} else if (xpp.getName().equals("statusValue")) {
				ServiceStatusValueEnum value = ParserUtils.getEnum(
						ServiceStatusValueEnum.class, xpp.nextText());
				vehicleJourney.setServiceStatusValue(value);
			} else if (xpp.getName().equals("timeSlotId")) {
				String value = ParserUtils.getText(xpp.nextText());
				// TODO timeSlotId

			} else if (xpp.getName().equals("transportMode")) {
				TransportModeNameEnum value = ParserUtils.getEnum(
						TransportModeNameEnum.class, xpp.nextText());
				vehicleJourney.setTransportMode(value);
			} else if (xpp.getName().equals("vehicleTypeIdentifier")) {
				vehicleJourney.setVehicleTypeIdentifier(xpp.nextText());
			} else if (xpp.getName().equals("vehicleJourneyAtStop")) {
				parseVehicleJourneyAtStop(context, vehicleJourney);
			} else if (xpp.getName().equals("name")) {
				vehicleJourney.setName(ParserUtils.getText(xpp.nextText()));
			} else {
				XPPUtil.skipSubTree(log, xpp);
			}
		}

		Collections.sort(vehicleJourney.getVehicleJourneyAtStops(),
				VEHICLE_JOURNEY_AT_STOP_COMPARATOR);
	}

	private void parseVehicleJourneyAtStop(Context context,
			VehicleJourney vehicleJourney) throws Exception {

		XmlPullParser xpp = (XmlPullParser) context.get(PARSER);
		Referential referential = (Referential) context.get(REFERENTIAL);

		xpp.require(XmlPullParser.START_TAG, null, "vehicleJourneyAtStop");
		context.put(COLUMN_NUMBER, xpp.getColumnNumber());
		context.put(LINE_NUMBER, xpp.getLineNumber());

		while (xpp.nextTag() == XmlPullParser.START_TAG) {

			VehicleJourneyAtStop vehicleJourneyAtStop = ObjectFactory
					.getVehicleJourneyAtStop();

			if (xpp.getName().equals("vehicleJourneyId")) {
				String objectId = ParserUtils.getText(xpp.nextText());
				vehicleJourneyAtStop.setVehicleJourney(vehicleJourney);
			} else if (xpp.getName().equals("boardingAlightingPossibility")) {
				BoardingAlightingPossibilityEnum value = ParserUtils.getEnum(
						BoardingAlightingPossibilityEnum.class, xpp.nextText());
				vehicleJourneyAtStop.setBoardingAlightingPossibility(value);
			} else if (xpp.getName().equals("connectingServiceId")) {
				vehicleJourneyAtStop.setConnectingServiceId(xpp.nextText());
			} else if (xpp.getName().equals("stopPointId")) {
				String objectId = ParserUtils.getText(xpp.nextText());
				StopPoint stopPoint = ObjectFactory.getStopPoint(referential,
						objectId);
				vehicleJourneyAtStop.setStopPoint(stopPoint);
			} else if (xpp.getName().equals("order")) {
				Integer value = ParserUtils.getInt(xpp.nextText());
				// TODO order
			} else if (xpp.getName().equals("elapseDuration")) {
				Time value = ParserUtils.getSQLDuration(xpp.nextText());
				vehicleJourneyAtStop.setElapseDuration(value);
			} else if (xpp.getName().equals("arrivalTime")) {
				Time value = ParserUtils.getSQLTime(xpp.nextText());
				vehicleJourneyAtStop.setArrivalTime(value);
			} else if (xpp.getName().equals("departureTime")) {
				Time value = ParserUtils.getSQLTime(xpp.nextText());
				vehicleJourneyAtStop.setDepartureTime(value);
			} else if (xpp.getName().equals("waitingTime")) {
				Time value = ParserUtils.getSQLTime(xpp.nextText());
				vehicleJourneyAtStop.setWaitingTime(value);
			} else if (xpp.getName().equals("headwayFrequency")) {
				Time value = ParserUtils.getSQLDuration(xpp.nextText());
				vehicleJourneyAtStop.setHeadwayFrequency(value);
			} else {
				XPPUtil.skipSubTree(log, xpp);
			}
		}
	}

	static {
		ParserFactory.register(VehicleJourneyParser.class.getName(),
				new ParserFactory() {
					private VehicleJourneyParser instance = new VehicleJourneyParser();

					@Override
					protected Parser create() {
						return instance;
					}
				});
	}
}
