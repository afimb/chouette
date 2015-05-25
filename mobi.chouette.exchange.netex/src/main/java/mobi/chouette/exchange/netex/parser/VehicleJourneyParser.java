package mobi.chouette.exchange.netex.parser;

import java.io.IOException;
import java.text.ParseException;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Context;
import mobi.chouette.common.XPPUtil;
import mobi.chouette.exchange.importer.Parser;
import mobi.chouette.exchange.importer.ParserFactory;
import mobi.chouette.exchange.netex.Constant;
import mobi.chouette.model.Company;
import mobi.chouette.model.JourneyPattern;
import mobi.chouette.model.Route;
import mobi.chouette.model.StopPoint;
import mobi.chouette.model.Timetable;
import mobi.chouette.model.VehicleJourney;
import mobi.chouette.model.VehicleJourneyAtStop;
import mobi.chouette.model.util.ObjectFactory;
import mobi.chouette.model.util.Referential;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

@Log4j
public class VehicleJourneyParser implements Parser, Constant {

	private static final String CHILD_TAG = "vehicleJourneys";

	@Override
	public void parse(Context context) throws Exception {

		XmlPullParser xpp = (XmlPullParser) context.get(PARSER);

		xpp.require(XmlPullParser.START_TAG, null, CHILD_TAG);
		context.put(COLUMN_NUMBER, xpp.getColumnNumber());
		context.put(LINE_NUMBER, xpp.getLineNumber());

		while (xpp.nextTag() == XmlPullParser.START_TAG) {
			if (xpp.getName().equals("ServiceJourney")) {
				parseServiceJourney(context);
			} else {
				XPPUtil.skipSubTree(log, xpp);
			}
		}
	}

	private void parseServiceJourney(Context context) throws Exception {
		XmlPullParser xpp = (XmlPullParser) context.get(PARSER);
		Referential referential = (Referential) context.get(REFERENTIAL);

		xpp.require(XmlPullParser.START_TAG, null, "ServiceJourney");
		context.put(COLUMN_NUMBER, xpp.getColumnNumber());
		context.put(LINE_NUMBER, xpp.getLineNumber());

		String id = xpp.getAttributeValue(null, ID);
		VehicleJourney vehicleJourney = ObjectFactory.getVehicleJourney(referential, id);

		Integer version = Integer.valueOf(xpp.getAttributeValue(null, VERSION));
		vehicleJourney.setObjectVersion(version != null ? version : 0);

		while (xpp.nextTag() == XmlPullParser.START_TAG) {
			if (xpp.getName().equals(NAME)) {
				vehicleJourney.setPublishedJourneyName(xpp.nextText());
			} else if (xpp.getName().equals("ShortName")) {
				vehicleJourney.setPublishedJourneyIdentifier(xpp.nextText());
			} else if (xpp.getName().equals("dayTypes")) {
				parseDayTypeRefs(context, vehicleJourney);
			} else if (xpp.getName().equals("RouteRef")) {
				String ref = xpp.getAttributeValue(null, REF);
				Route route = ObjectFactory.getRoute(referential, ref);
				vehicleJourney.setRoute(route);
				XPPUtil.skipSubTree(log, xpp);
			} else if (xpp.getName().equals("ServicePatternRef")) {
				String ref = xpp.getAttributeValue(null, REF);
				JourneyPattern journeyPattern = ObjectFactory.getJourneyPattern(referential, ref);
				vehicleJourney.setJourneyPattern(journeyPattern);
				XPPUtil.skipSubTree(log, xpp);
			} else if (xpp.getName().equals("OperatorRef")) {
				String ref = xpp.getAttributeValue(null, REF);
				Company company = ObjectFactory.getCompany(referential, ref);
				vehicleJourney.setCompany(company);
				XPPUtil.skipSubTree(log, xpp);
			} else if (xpp.getName().equals("trainNumbers")) {
				parseTrainNumberRefs(context, vehicleJourney);
			} else if (xpp.getName().equals("calls")) {
				parseCalls(context, vehicleJourney);
			} else {
				XPPUtil.skipSubTree(log, xpp);
			}
		}
		vehicleJourney.setFilled(true);
	}

	private void parseTrainNumberRefs(Context context, VehicleJourney vehicleJourney) throws Exception {
		XmlPullParser xpp = (XmlPullParser) context.get(PARSER);
		xpp.require(XmlPullParser.START_TAG, null, "trainNumbers");
		context.put(COLUMN_NUMBER, xpp.getColumnNumber());
		context.put(LINE_NUMBER, xpp.getLineNumber());

		while (xpp.nextTag() == XmlPullParser.START_TAG) {
			if (xpp.getName().equals("TrainNumberRef")) {
				String ref = xpp.getAttributeValue(null, REF);
				Long number = NetexUtils.getTrainNumber(ref);
				vehicleJourney.setNumber((number != null) ? number : null);
				XPPUtil.skipSubTree(log, xpp);
			} else {
				XPPUtil.skipSubTree(log, xpp);
			}
		}
	}

	private void parseDayTypeRefs(Context context, VehicleJourney vehicleJourney) throws Exception {
		XmlPullParser xpp = (XmlPullParser) context.get(PARSER);
		Referential referential = (Referential) context.get(REFERENTIAL);

		xpp.require(XmlPullParser.START_TAG, null, "dayTypes");
		context.put(COLUMN_NUMBER, xpp.getColumnNumber());
		context.put(LINE_NUMBER, xpp.getLineNumber());

		while (xpp.nextTag() == XmlPullParser.START_TAG) {
			if (xpp.getName().equals("DayTypeRef")) {
				String ref = xpp.getAttributeValue(null, REF);
				Timetable timetable = referential.getTimetables().get(ref);
				if (timetable != null) {
					vehicleJourney.getTimetables().add(timetable);
					// timetable.addVehicleJourney(vehicleJourney);
				}
				XPPUtil.skipSubTree(log, xpp);
			} else {
				XPPUtil.skipSubTree(log, xpp);
			}
		}
	}

	public void parseCalls(Context context, VehicleJourney vehicleJourney) throws Exception {
		XmlPullParser xpp = (XmlPullParser) context.get(PARSER);

		xpp.require(XmlPullParser.START_TAG, null, "calls");
		context.put(COLUMN_NUMBER, xpp.getColumnNumber());
		context.put(LINE_NUMBER, xpp.getLineNumber());

		while (xpp.nextTag() == XmlPullParser.START_TAG) {
			if (xpp.getName().equals("Call")) {
				parseCall(context, vehicleJourney);
			} else {
				XPPUtil.skipSubTree(log, xpp);
			}
		}
	}

	private void parseCall(Context context, VehicleJourney vehicleJourney) throws XmlPullParserException, IOException,
			ParseException {
		XmlPullParser xpp = (XmlPullParser) context.get(PARSER);
		Referential referential = (Referential) context.get(REFERENTIAL);

		xpp.require(XmlPullParser.START_TAG, null, "Call");
		context.put(COLUMN_NUMBER, xpp.getColumnNumber());
		context.put(LINE_NUMBER, xpp.getLineNumber());

		VehicleJourneyAtStop vehicleJourneyAtStop = new VehicleJourneyAtStop();

		String ref = null;
		while (xpp.nextTag() == XmlPullParser.START_TAG) {
			if (xpp.getName().equals("ScheduledStopPointRef")) {
				ref = xpp.getAttributeValue(null, REF);
				StopPoint stopPoint = ObjectFactory.getStopPoint(referential, ref);
				vehicleJourneyAtStop.setStopPoint(stopPoint);
				XPPUtil.skipSubTree(log, xpp);
			} else if (xpp.getName().equals("Arrival")) {
				vehicleJourneyAtStop.setArrivalTime(NetexUtils.getSQLTime(getTime(xpp)));
			} else if (xpp.getName().equals("Departure")) {
				vehicleJourneyAtStop.setDepartureTime(NetexUtils.getSQLTime(getTime(xpp)));
			} else {
				XPPUtil.skipSubTree(log, xpp);
			}
		}

		vehicleJourneyAtStop.setVehicleJourney(vehicleJourney);

		log.debug("[DSU] " + "Call" + "\t" + ref);
	}

	private String getTime(XmlPullParser xpp) throws XmlPullParserException, IOException {
		String result = null;
		while (xpp.nextTag() == XmlPullParser.START_TAG) {
			if (xpp.getName().equals("Time")) {
				result = xpp.nextText();
			} else {
				XPPUtil.skipSubTree(log, xpp);
			}
		}
		return result;
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

}
