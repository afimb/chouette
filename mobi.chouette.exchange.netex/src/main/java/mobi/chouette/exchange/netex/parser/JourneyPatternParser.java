package mobi.chouette.exchange.netex.parser;

import java.util.List;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Context;
import mobi.chouette.common.XPPUtil;
import mobi.chouette.exchange.importer.Parser;
import mobi.chouette.exchange.importer.ParserFactory;
import mobi.chouette.exchange.netex.Constant;
import mobi.chouette.model.JourneyPattern;
import mobi.chouette.model.Route;
import mobi.chouette.model.StopPoint;
import mobi.chouette.model.util.ObjectFactory;
import mobi.chouette.model.util.Referential;

import org.xmlpull.v1.XmlPullParser;

@Log4j
public class JourneyPatternParser implements Parser, Constant {

	private static final String CHILD_TAG = "servicePatterns";

	@Override
	public void parse(Context context) throws Exception {

		XmlPullParser xpp = (XmlPullParser) context.get(PARSER);

		xpp.require(XmlPullParser.START_TAG, null, CHILD_TAG);
		context.put(COLUMN_NUMBER, xpp.getColumnNumber());
		context.put(LINE_NUMBER, xpp.getLineNumber());

		while (xpp.nextTag() == XmlPullParser.START_TAG) {
			if (xpp.getName().equals("ServicePattern")) {
				parseServicePattern(context);
			} else {
				XPPUtil.skipSubTree(log, xpp);
			}
		}
	}

	private void parseServicePattern(Context context) throws Exception {
		XmlPullParser xpp = (XmlPullParser) context.get(PARSER);
		Referential referential = (Referential) context.get(REFERENTIAL);

		xpp.require(XmlPullParser.START_TAG, null, "ServicePattern");
		context.put(COLUMN_NUMBER, xpp.getColumnNumber());
		context.put(LINE_NUMBER, xpp.getLineNumber());

		String id = xpp.getAttributeValue(null, ID);
		JourneyPattern journeyPattern = ObjectFactory.getJourneyPattern(
				referential, id);

		Integer version = Integer.valueOf(xpp.getAttributeValue(null, VERSION));
		journeyPattern.setObjectVersion(version != null ? version : 0);

		while (xpp.nextTag() == XmlPullParser.START_TAG) {

			if (xpp.getName().equals(NAME)) {
				journeyPattern.setName(xpp.nextText());
			} else if (xpp.getName().equals("ShortName")) {
				journeyPattern.setName(xpp.nextText());
			} else if (xpp.getName().equals("PrivateCode")) {
				journeyPattern.setName(xpp.nextText());
			} else if (xpp.getName().equals("RouteRef")) {
				String ref = xpp.getAttributeValue(null, REF);
				Route route = ObjectFactory.getRoute(referential, ref);
				journeyPattern.setRoute(route);
				XPPUtil.skipSubTree(log, xpp);
			} else if (xpp.getName().equals("RouteRef")) {
				String ref = xpp.getAttributeValue(null, REF);
				Route route = ObjectFactory.getRoute(referential, ref);
				journeyPattern.setRoute(route);
				XPPUtil.skipSubTree(log, xpp);
			} else if (xpp.getName().equals("pointsInSequence")) {
				parseStopPointInJourneyPatterns(context, journeyPattern);
			} else {
				XPPUtil.skipSubTree(log, xpp);
			}
		}

		referential.getJourneyPatterns().put(journeyPattern.getObjectId(),
				journeyPattern);
		journeyPattern.setFilled(true);
	}

	private void parseStopPointInJourneyPatterns(Context context,
			JourneyPattern journeyPattern) throws Exception {
		XmlPullParser xpp = (XmlPullParser) context.get(PARSER);
		Referential referential = (Referential) context.get(REFERENTIAL);

		xpp.require(XmlPullParser.START_TAG, null, "pointsInSequence");
		context.put(COLUMN_NUMBER, xpp.getColumnNumber());
		context.put(LINE_NUMBER, xpp.getLineNumber());

		while (xpp.nextTag() == XmlPullParser.START_TAG) {
			if (xpp.getName().equals("StopPointInJourneyPattern")) {

				while (xpp.nextTag() == XmlPullParser.START_TAG) {
					if (xpp.getName().equals("ScheduledStopPointRef")) {
						String ref = xpp.getAttributeValue(null, REF);
						StopPoint stopPoint = ObjectFactory.getStopPoint(
								referential, ref);
						journeyPattern.addStopPoint(stopPoint);
						XPPUtil.skipSubTree(log, xpp);
					} else {
						XPPUtil.skipSubTree(log, xpp);
					}
				}
			} else {
				XPPUtil.skipSubTree(log, xpp);
			}
		}

		List<StopPoint> list = journeyPattern.getStopPoints();
		if (list != null && list.size() > 0) {
			StopPoint departureStopPoint = list.get(0);
			journeyPattern.setDepartureStopPoint(departureStopPoint);

			StopPoint arrivalStopPoint = list.get(list.size() - 1);
			journeyPattern.setArrivalStopPoint(arrivalStopPoint);
		}

	}

	static {
		ParserFactory.register(JourneyPatternParser.class.getName(),
				new ParserFactory() {
					private JourneyPatternParser instance = new JourneyPatternParser();

					@Override
					protected Parser create() {
						return instance;
					}
				});
	}

}
