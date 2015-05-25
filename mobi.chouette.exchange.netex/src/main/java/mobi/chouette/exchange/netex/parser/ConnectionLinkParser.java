package mobi.chouette.exchange.netex.parser;

import java.math.BigDecimal;
import java.sql.Time;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Context;
import mobi.chouette.common.XPPUtil;
import mobi.chouette.exchange.importer.Parser;
import mobi.chouette.exchange.importer.ParserFactory;
import mobi.chouette.exchange.importer.ParserUtils;
import mobi.chouette.exchange.netex.Constant;
import mobi.chouette.model.ConnectionLink;
import mobi.chouette.model.StopArea;
import mobi.chouette.model.type.ConnectionLinkTypeEnum;
import mobi.chouette.model.util.ObjectFactory;
import mobi.chouette.model.util.Referential;

import org.xmlpull.v1.XmlPullParser;

@Log4j
public class ConnectionLinkParser implements Parser, Constant {

	private static final String CHILD_TAG = "connections";

	@Override
	public void parse(Context context) throws Exception {
		XmlPullParser xpp = (XmlPullParser) context.get(PARSER);

		xpp.require(XmlPullParser.START_TAG, null, CHILD_TAG);
		context.put(COLUMN_NUMBER, xpp.getColumnNumber());
		context.put(LINE_NUMBER, xpp.getLineNumber());

		while (xpp.nextTag() == XmlPullParser.START_TAG) {
			if (xpp.getName().equals("SiteConnection")) {
				parseServicePattern(context);
			} else {
				XPPUtil.skipSubTree(log, xpp);
			}
		}
	}

	private void parseServicePattern(Context context) throws Exception {

		XmlPullParser xpp = (XmlPullParser) context.get(PARSER);
		Referential referential = (Referential) context.get(REFERENTIAL);

		xpp.require(XmlPullParser.START_TAG, null, "SiteConnection");
		context.put(COLUMN_NUMBER, xpp.getColumnNumber());
		context.put(LINE_NUMBER, xpp.getLineNumber());

		String id = xpp.getAttributeValue(null, ID);
		ConnectionLink connectionLink = ObjectFactory.getConnectionLink(
				referential, id);

		Integer version = Integer.valueOf(xpp.getAttributeValue(null, VERSION));
		connectionLink.setObjectVersion(version != null ? version : 0);

		while (xpp.nextTag() == XmlPullParser.START_TAG) {
			
			if (xpp.getName().equals(NAME)) {
				connectionLink.setName(xpp.nextText());
			} else if (xpp.getName().equals("Description")) {
				connectionLink.setComment(xpp.nextText());
			} else if (xpp.getName().equals("Distance")) {
				BigDecimal value = ParserUtils.getBigDecimal(xpp.nextText());
				connectionLink.setLinkDistance(value);
			} else if (xpp.getName().equals("TransferDuration")) {
				while (xpp.nextTag() == XmlPullParser.START_TAG) {
					if (xpp.getName().equals("Covered")) {
						ConnectionLinkTypeEnum value = NetexUtils
								.toConnectionLinkType(xpp.nextText());
						connectionLink.setLinkType(value);
					} else if (xpp.getName().equals("DefaultDuration")) {
						Time value = ParserUtils.getSQLDuration(xpp.nextText());
						connectionLink.setDefaultDuration(value);
					} else if (xpp.getName()
							.equals("FrequentTravellerDuration")) {
						Time value = ParserUtils.getSQLDuration(xpp.nextText());
						connectionLink.setFrequentTravellerDuration(value);
					} else if (xpp.getName().equals(
							"OccasionalTravellerDuration")) {
						Time value = ParserUtils.getSQLDuration(xpp.nextText());
						connectionLink.setOccasionalTravellerDuration(value);
					} else if (xpp.getName().equals(
							"MobilityRestrictedTravellerDuration")) {
						Time value = ParserUtils.getSQLDuration(xpp.nextText());
						connectionLink
								.setMobilityRestrictedTravellerDuration(value);
					} else {
						XPPUtil.skipSubTree(log, xpp);
					}
				}
			}

			else if (xpp.getName().equals("From")) {

				while (xpp.nextTag() == XmlPullParser.START_TAG) {
					if (xpp.getName().equals("StopPlaceRef")) {
						String ref = xpp.getAttributeValue(null, REF);
						StopArea startOfLink = ObjectFactory.getStopArea(
								referential, ref);
						connectionLink.setStartOfLink(startOfLink);
						XPPUtil.skipSubTree(log, xpp);
					} else {
						XPPUtil.skipSubTree(log, xpp);
					}
				}
			} else if (xpp.getName().equals("To")) {

				while (xpp.nextTag() == XmlPullParser.START_TAG) {
					if (xpp.getName().equals("StopPlaceRef")) {
						String ref = xpp.getAttributeValue(null, REF);
						StopArea endOfLink = ObjectFactory.getStopArea(
								referential, ref);
						connectionLink.setEndOfLink(endOfLink);
						XPPUtil.skipSubTree(log, xpp);
					} else {
						XPPUtil.skipSubTree(log, xpp);
					}
				}
			} else {
				XPPUtil.skipSubTree(log, xpp);
			}
		}
		connectionLink.setFilled(true);
	}

	static {
		ParserFactory.register(ConnectionLinkParser.class.getName(),
				new ParserFactory() {
					private ConnectionLinkParser instance = new ConnectionLinkParser();

					@Override
					protected Parser create() {
						return instance;
					}
				});
	}

}
