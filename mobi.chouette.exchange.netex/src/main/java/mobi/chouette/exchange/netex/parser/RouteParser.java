package mobi.chouette.exchange.netex.parser;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Context;
import mobi.chouette.common.XPPUtil;
import mobi.chouette.exchange.importer.Parser;
import mobi.chouette.exchange.importer.ParserFactory;
import mobi.chouette.exchange.netex.Constant;
import mobi.chouette.model.Route;
import mobi.chouette.model.StopArea;
import mobi.chouette.model.StopPoint;
import mobi.chouette.model.util.ObjectFactory;
import mobi.chouette.model.util.Referential;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.util.XmlPullUtil;

@Log4j
public class RouteParser implements Parser, Constant {

	private Map<String, Properties> directions;

	@Override
	public void parse(Context context) throws Exception {

		XmlPullParser xpp = (XmlPullParser) context.get(PARSER);

		if ((xpp.getEventType() == XmlPullParser.START_TAG)
				&& xpp.getName().equals("directions")) {
			while (xpp.nextTag() == XmlPullParser.START_TAG) {
				if (xpp.getName().equals("Direction")) {
					parseDirection(context);
				} else {
					XPPUtil.skipSubTree(log, xpp);
				}
			}
		} else if ((xpp.getEventType() == XmlPullParser.START_TAG)
				&& xpp.getName().equals("routes")) {
			while (xpp.nextTag() == XmlPullParser.START_TAG) {
				if (xpp.getName().equals("Route")) {
					parseRoute(context);
				} else {
					XPPUtil.skipSubTree(log, xpp);
				}
			}
		} else if ((xpp.getEventType() == XmlPullParser.START_TAG)
				&& xpp.getName().equals("stopAssignments")) {
			while (xpp.nextTag() == XmlPullParser.START_TAG) {
				if (xpp.getName().equals("PassengerStopAssignment")) {
					parsePassengerStopAssignment(context);
				} else {
					XPPUtil.skipSubTree(log, xpp);
				}
			}
		}
	}

	private void parseDirection(Context context) throws Exception {
		XmlPullParser xpp = (XmlPullParser) context.get(PARSER);

		xpp.require(XmlPullParser.START_TAG, null, "Direction");
		context.put(COLUMN_NUMBER, xpp.getColumnNumber());
		context.put(LINE_NUMBER, xpp.getLineNumber());
		directions = new HashMap<String, Properties>();

		String id = xpp.getAttributeValue(null, ID);
		Properties properties = new Properties();
		directions.put(id, properties);

		while (xpp.nextTag() == XmlPullParser.START_TAG) {
			if (xpp.getName().equals("Name")) {
				properties.put(NAME, xpp.nextText());
			} else if (xpp.getName().equals("DirectionType")) {
				properties.put("DirectionType", xpp.nextText());
			} else {
				XPPUtil.skipSubTree(log, xpp);
			}
		}

		log.debug("[DSU] " + "Direction" + "\t" + id);
	}

	private void parsePassengerStopAssignment(Context context) throws Exception {
		XmlPullParser xpp = (XmlPullParser) context.get(PARSER);
		Referential referential = (Referential) context.get(REFERENTIAL);

		xpp.require(XmlPullParser.START_TAG, null, "PassengerStopAssignment");
		context.put(COLUMN_NUMBER, xpp.getColumnNumber());
		context.put(LINE_NUMBER, xpp.getLineNumber());

		String id = xpp.getAttributeValue(null, ID);

		String stopPointId = null;
		String stopAreaId = null;
		while (xpp.nextTag() == XmlPullParser.START_TAG) {

			if (xpp.getName().equals("ScheduledStopPointRef")) {
				stopPointId = xpp.getAttributeValue(null, REF);
				XPPUtil.skipSubTree(log, xpp);
			} else if (xpp.getName().equals("StopPlaceRef")) {
				stopAreaId = xpp.getAttributeValue(null, REF);
				XPPUtil.skipSubTree(log, xpp);
			} else if (xpp.getName().equals("QuayRef")) {
				// String ref = xpp.getAttributeValue(null, REF);
				XPPUtil.skipSubTree(log, xpp);
			} else {
				XPPUtil.skipSubTree(log, xpp);
			}
		}

		if (stopPointId != null && stopAreaId != null) {
			StopPoint stopPoint = ObjectFactory.getStopPoint(referential,
					stopPointId);
			StopArea stopArea = ObjectFactory.getStopArea(referential,
					stopAreaId);
			stopPoint.setContainedInStopArea(stopArea);
		}

		log.debug("[DSU] " + "PassengerStopAssignment" + "\t" + id);
	}

	private void parseRoute(Context context) throws Exception {
		XmlPullParser xpp = (XmlPullParser) context.get(PARSER);
		Referential referential = (Referential) context.get(REFERENTIAL);

		xpp.require(XmlPullParser.START_TAG, null, "Route");
		context.put(COLUMN_NUMBER, xpp.getColumnNumber());
		context.put(LINE_NUMBER, xpp.getLineNumber());

		String id = xpp.getAttributeValue(null, ID);
		Route route = ObjectFactory.getRoute(referential, id);

		Integer version = Integer.valueOf(xpp.getAttributeValue(null, VERSION));
		route.setObjectVersion(version != null ? version : 0);

		while (xpp.nextTag() == XmlPullParser.START_TAG) {
			if (xpp.getName().equals("keyList")) {
				parseKeyValues(context, route);
			} else if (xpp.getName().equals("Name")) {
				route.setName(xpp.nextText());
			} else if (xpp.getName().equals("ShortName")) {
				route.setPublishedName(xpp.nextText());
			} else if (xpp.getName().equals("DirectionRef")) {
				String ref = xpp.getAttributeValue(null, REF);
				Properties properties = directions.get(ref);
				if (properties != null) {

					String directionName = properties.getProperty(NAME);
					if (directionName != null) {
						route.setDirection(NetexUtils
								.toPTDirectionType(directionName));
					}
					String directionType = properties
							.getProperty("DirectionType");
					if (directionType != null) {
						route.setWayBack((directionType.equals("outbound")) ? "A"
								: "R");
					}
				}
				XPPUtil.skipSubTree(log, xpp);
			} else if (xpp.getName().equals("InverseRouteRef")) {
				String ref = xpp.getAttributeValue(null, REF);
				Route wayBackRoute = ObjectFactory.getRoute(referential, ref);
				if (wayBackRoute != null)
				{
					wayBackRoute.setOppositeRoute(route);
				}

				XPPUtil.skipSubTree(log, xpp);
			} else if (xpp.getName().equals("pointsInSequence")) {
				parsePointOnRoutes(context, route);
			} else {
				XPPUtil.skipSubTree(log, xpp);
			}
		}
		route.setFilled(true);
	}

	private void parsePointOnRoutes(Context context, Route route)
			throws Exception {
		XmlPullParser xpp = (XmlPullParser) context.get(PARSER);
		Referential referential = (Referential) context.get(REFERENTIAL);

		xpp.require(XmlPullParser.START_TAG, null, "pointsInSequence");
		context.put(COLUMN_NUMBER, xpp.getColumnNumber());
		context.put(LINE_NUMBER, xpp.getLineNumber());

		while (xpp.nextTag() == XmlPullParser.START_TAG) {
			if (xpp.getName().equals("PointOnRoute")) {
				String id = xpp.getAttributeValue(null, ID);
				StopPoint stopPoint = ObjectFactory.getStopPoint(referential,
						getStopPointObjectId(route, id));
				stopPoint.setRoute(route);
				stopPoint.setFilled(true);
				XPPUtil.skipSubTree(log, xpp);

			} else {
				XPPUtil.skipSubTree(log, xpp);
			}

		}
		for (StopPoint stopPoint : route.getStopPoints()) {
			stopPoint.setPosition(route.getStopPoints().indexOf(stopPoint));
		}
	}

	private void parseKeyValues(Context context, Route route) throws Exception {
		XmlPullParser xpp = (XmlPullParser) context.get(PARSER);

		xpp.require(XmlPullParser.START_TAG, null, "keyList");
		context.put(COLUMN_NUMBER, xpp.getColumnNumber());
		context.put(LINE_NUMBER, xpp.getLineNumber());

		while (xpp.nextTag() == XmlPullParser.START_TAG) {
			if (xpp.getName().equals("KeyValue")) {
				XmlPullUtil.nextStartTag(xpp, "Key");
				String key = xpp.nextText();
				if (key.equals("Comment")) {
					XmlPullUtil.nextStartTag(xpp, "Value");
					String value = xpp.nextText();
					route.setComment(value);
					XmlPullUtil.nextEndTag(xpp);
				} else if (key.equals("Number")) {
					XmlPullUtil.nextStartTag(xpp, "Value");
					String value = xpp.nextText();
					route.setNumber(value);
					XmlPullUtil.nextEndTag(xpp);
				} else {
					XPPUtil.skipSubTree(log, xpp);
				}
			} else {
				XPPUtil.skipSubTree(log, xpp);
			}
		}
	}

	private String getStopPointObjectId(Route route, String pointOnRouteId) {
		String prefix = NetexUtils.objectIdPrefix(route.getObjectId());

		Matcher m = Pattern.compile("\\S+:\\S+:(\\S+)-\\d+$").matcher(
				pointOnRouteId);
		if (!m.matches()) {
			throw new RuntimeException("PointOnRoute.id " + pointOnRouteId);
		}
		String id = m.group(1);

		return prefix + ":StopPoint:" + id;
	}

	static {
		ParserFactory.register(RouteParser.class.getName(),
				new ParserFactory() {
					private RouteParser instance = new RouteParser();

					@Override
					protected Parser create() {
						return instance;
					}
				});
	}

}
