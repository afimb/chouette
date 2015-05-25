package mobi.chouette.exchange.netex.parser;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Context;
import mobi.chouette.common.XPPUtil;
import mobi.chouette.exchange.importer.Parser;
import mobi.chouette.exchange.importer.ParserFactory;
import mobi.chouette.exchange.importer.ParserUtils;
import mobi.chouette.exchange.netex.Constant;
import mobi.chouette.model.StopArea;
import mobi.chouette.model.type.ChouetteAreaEnum;
import mobi.chouette.model.type.LongLatTypeEnum;
import mobi.chouette.model.util.ObjectFactory;
import mobi.chouette.model.util.Referential;

import org.xmlpull.v1.XmlPullParser;

@Log4j
public class StopAreaParser implements Parser, Constant {

	private Map<String, Properties> tariffZones;

	@Override
	public void parse(Context context) throws Exception {

		XmlPullParser xpp = (XmlPullParser) context.get(PARSER);
		Referential referential = (Referential) context.get(REFERENTIAL);

		if ((xpp.getEventType() == XmlPullParser.START_TAG)
				&& xpp.getName().equals("tariffZones")) {
			while (xpp.nextTag() == XmlPullParser.START_TAG) {
				if (xpp.getName().equals("TariffZone")) {
					parseTariffZone(context);
				} else {
					XPPUtil.skipSubTree(log, xpp);
				}
			}
		} else if ((xpp.getEventType() == XmlPullParser.START_TAG)
				&& xpp.getName().equals("stopPlaces")) {
			Map<String, String> map = new HashMap<String, String>();
			while (xpp.nextTag() == XmlPullParser.START_TAG) {
				if (xpp.getName().equals("StopPlace")) {
					parseStopPlace(context, map);
				} else {
					XPPUtil.skipSubTree(log, xpp);
				}
			}

			for (Entry<String, String> item : map.entrySet()) {
				StopArea child = ObjectFactory.getStopArea(referential,
						item.getKey());
				StopArea parent = ObjectFactory.getStopArea(referential,
						item.getValue());
				if (parent != null) {
					parent.setAreaType(ChouetteAreaEnum.StopPlace);
					child.setParent(parent);
				}
			}
		}
	}

	private void parseTariffZone(Context context) throws Exception {
		XmlPullParser xpp = (XmlPullParser) context.get(PARSER);

		xpp.require(XmlPullParser.START_TAG, null, "TariffZone");
		context.put(COLUMN_NUMBER, xpp.getColumnNumber());
		context.put(LINE_NUMBER, xpp.getLineNumber());

		tariffZones = new HashMap<String, Properties>();

		String id = xpp.getAttributeValue(null, ID);
		Properties properties = new Properties();
		tariffZones.put(id, properties);

		while (xpp.nextTag() == XmlPullParser.START_TAG) {
			if (xpp.getName().equals(NAME)) {
				properties.put(NAME, xpp.nextText());
			} else {
				XPPUtil.skipSubTree(log, xpp);
			}
		}
	}

	private void parseStopPlace(Context context, Map<String, String> map)
			throws Exception, IOException, ParseException {
		XmlPullParser xpp = (XmlPullParser) context.get(PARSER);
		Referential referential = (Referential) context.get(REFERENTIAL);

		xpp.require(XmlPullParser.START_TAG, null, "StopPlace");
		context.put(COLUMN_NUMBER, xpp.getColumnNumber());
		context.put(LINE_NUMBER, xpp.getLineNumber());

		String id = xpp.getAttributeValue(null, ID);
		StopArea stopArea = ObjectFactory.getStopArea(referential, id);
		stopArea.setAreaType(ChouetteAreaEnum.CommercialStopPoint);

		Integer version = Integer.valueOf(xpp.getAttributeValue(null, VERSION));
		stopArea.setObjectVersion(version != null ? version : 0);

		while (xpp.nextTag() == XmlPullParser.START_TAG) {
			if (xpp.getName().equals(NAME)) {
				stopArea.setName(xpp.nextText());
			} else if (xpp.getName().equals("Description")) {
				stopArea.setComment(xpp.nextText());
			} else if (xpp.getName().equals("Landmark")) {
				stopArea.setNearestTopicName(xpp.nextText());
			} else if (xpp.getName().equals("PrivateCode")) {
				stopArea.setRegistrationNumber(xpp.nextText());
			} else if (xpp.getName().equals("Centroid")) {
				parseCentroid(context, stopArea);
			} else if (xpp.getName().equals("ParentZoneRef")) {
				String ref = xpp.getAttributeValue(null, REF);
				if (ref != null) {
					map.put(stopArea.getObjectId(), ref);
				}
				XPPUtil.skipSubTree(log, xpp);
			} else if (xpp.getName().equals("PostalAddress")) {
				parsePostalAddress(context, stopArea);
			} else if (xpp.getName().equals("tariffZones")) {
				parseTariffZoneRefs(context, stopArea);
			} else if (xpp.getName().equals("quays")) {
				while (xpp.nextTag() == XmlPullParser.START_TAG) {
					if (xpp.getName().equals("Quay")) {
						parseQuay(context, stopArea);
					} else {
						XPPUtil.skipSubTree(log, xpp);
					}
				}
			} else {
				XPPUtil.skipSubTree(log, xpp);
			}
		}
		stopArea.setFilled(true);
	}

	private void parseQuay(Context context, StopArea parent) throws Exception {
		XmlPullParser xpp = (XmlPullParser) context.get(PARSER);
		Referential referential = (Referential) context.get(REFERENTIAL);

		xpp.require(XmlPullParser.START_TAG, null, "Quay");
		context.put(COLUMN_NUMBER, xpp.getColumnNumber());
		context.put(LINE_NUMBER, xpp.getLineNumber());

		String id = xpp.getAttributeValue(null, ID);
		StopArea stopArea = ObjectFactory.getStopArea(referential, id);
		stopArea.setAreaType(ChouetteAreaEnum.Quay);
		stopArea.setParent(parent);

		Integer version = Integer.valueOf(xpp.getAttributeValue(null, VERSION));
		stopArea.setObjectVersion(version != null ? version : 0);

		while (xpp.nextTag() == XmlPullParser.START_TAG) {
			if (xpp.getName().equals(NAME)) {
				stopArea.setName(xpp.nextText());
			} else if (xpp.getName().equals("Description")) {
				stopArea.setComment(xpp.nextText());
			} else if (xpp.getName().equals("Landmark")) {
				stopArea.setNearestTopicName(xpp.nextText());
			} else if (xpp.getName().equals("PrivateCode")) {
				stopArea.setRegistrationNumber(xpp.nextText());
			} else if (xpp.getName().equals("Centroid")) {
				parseCentroid(context, stopArea);
			} else if (xpp.getName().equals("PostalAddress")) {
				parsePostalAddress(context, stopArea);
			} else if (xpp.getName().equals("tariffZones")) {
				parseTariffZoneRefs(context, stopArea);
			} else {
				XPPUtil.skipSubTree(log, xpp);
			}
		}
		stopArea.setFilled(true);
	}

	private void parsePostalAddress(Context context, StopArea stopArea)
			throws Exception {
		XmlPullParser xpp = (XmlPullParser) context.get(PARSER);

		xpp.require(XmlPullParser.START_TAG, null, "PostalAddress");
		context.put(COLUMN_NUMBER, xpp.getColumnNumber());
		context.put(LINE_NUMBER, xpp.getLineNumber());

		while (xpp.nextTag() == XmlPullParser.START_TAG) {
			if (xpp.getName().equals("PostCode")) {
				stopArea.setCountryCode(xpp.nextText());
			} else if (xpp.getName().equals("AddressLine1")) {
				stopArea.setStreetName(xpp.nextText());
			} else {
				XPPUtil.skipSubTree(log, xpp);
			}
		}
	}

	private void parseCentroid(Context context, StopArea stopArea)
			throws Exception {
		XmlPullParser xpp = (XmlPullParser) context.get(PARSER);

		xpp.require(XmlPullParser.START_TAG, null, "Centroid");
		context.put(COLUMN_NUMBER, xpp.getColumnNumber());
		context.put(LINE_NUMBER, xpp.getLineNumber());

		while (xpp.nextTag() == XmlPullParser.START_TAG) {
			if (xpp.getName().equals("Location")) {

				while (xpp.nextTag() == XmlPullParser.START_TAG) {
					if (xpp.getName().equals("Longitude")) {
						String value = xpp.nextText();
						BigDecimal longitude = ParserUtils.getBigDecimal(value);
						stopArea.setLongitude(longitude);
					} else if (xpp.getName().equals("Latitude")) {
						String value = xpp.nextText();
						BigDecimal latitude = ParserUtils.getBigDecimal(value);
						stopArea.setLatitude(latitude);
					} else if (xpp.getName().equals("pos")) {
						String projectedType = xpp.getAttributeValue(null,
								"srsName");
						String position = xpp.nextText();
						BigDecimal x = ParserUtils.getX(position);
						BigDecimal y = ParserUtils.getY(position);
						if (projectedType != null && x != null && y != null) {
							stopArea.setProjectionType(projectedType);
							stopArea.setX(x);
							stopArea.setY(y);
						}

					} else {
						XPPUtil.skipSubTree(log, xpp);
					}
				}
				if (stopArea.getLongitude() != null
						&& stopArea.getLatitude() != null) {
					stopArea.setLongLatType(LongLatTypeEnum.WGS84);
				} else {
					stopArea.setLongitude(null);
					stopArea.setLatitude(null);
				}
			} else {
				XPPUtil.skipSubTree(log, xpp);
			}
		}
	}

	private void parseTariffZoneRefs(Context context, StopArea stopArea)
			throws Exception {
		XmlPullParser xpp = (XmlPullParser) context.get(PARSER);

		xpp.require(XmlPullParser.START_TAG, null, "tariffZones");
		context.put(COLUMN_NUMBER, xpp.getColumnNumber());
		context.put(LINE_NUMBER, xpp.getLineNumber());

		while (xpp.nextTag() == XmlPullParser.START_TAG) {
			if (xpp.getName().equals("TariffZoneRef")) {
				String ref = xpp.getAttributeValue(null, REF);
				Properties properties = tariffZones.get(ref);
				if (properties != null) {
					String tariffName = properties.getProperty(NAME);
					if (tariffName != null) {
						try {
							stopArea.setFareCode(Integer.parseInt(tariffName));
						} catch (Exception ignored) {
						}
					}
				}
				XPPUtil.skipSubTree(log, xpp);

			} else {
				XPPUtil.skipSubTree(log, xpp);
			}
		}
	}

	static {
		ParserFactory.register(StopAreaParser.class.getName(),
				new ParserFactory() {
					private StopAreaParser instance = new StopAreaParser();

					@Override
					protected Parser create() {
						return instance;
					}
				});
	}
}
