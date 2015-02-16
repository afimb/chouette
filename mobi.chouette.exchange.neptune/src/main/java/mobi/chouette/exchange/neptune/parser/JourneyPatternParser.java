package mobi.chouette.exchange.neptune.parser;

import java.util.Date;
import java.util.Map;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Constant;
import mobi.chouette.common.Context;
import mobi.chouette.exchange.importer.Parser;
import mobi.chouette.exchange.importer.ParserFactory;
import mobi.chouette.exchange.importer.ParserUtils;
import mobi.chouette.exchange.importer.XPPUtil;
import mobi.chouette.exchange.validation.report.FileLocation;
import mobi.chouette.model.JourneyPattern;
import mobi.chouette.model.Line;
import mobi.chouette.model.Route;
import mobi.chouette.model.StopPoint;
import mobi.chouette.model.util.ObjectFactory;
import mobi.chouette.model.util.Referential;

import org.xmlpull.v1.XmlPullParser;

@Log4j
public class JourneyPatternParser implements Parser, Constant {
	private static final String CHILD_TAG = "JourneyPattern";

	@Override
	public void parse(Context context) throws Exception {

		XmlPullParser xpp = (XmlPullParser) context.get(PARSER);
		Referential referential = (Referential) context.get(REFERENTIAL);

		xpp.require(XmlPullParser.START_TAG, null, CHILD_TAG);
		context.put(COLUMN_NUMBER, xpp.getColumnNumber());
		context.put(LINE_NUMBER, xpp.getLineNumber());
		
		Map<String,FileLocation> locations = (Map<String, FileLocation>) context.get(OBJECT_LOCALISATION);
		FileLocation location = new FileLocation((String) context.get(FILE_URL), xpp.getLineNumber(), xpp.getColumnNumber());

		JourneyPattern journeyPattern = null;
		while (xpp.nextTag() == XmlPullParser.START_TAG) {

			if (xpp.getName().equals("objectId")) {
				String objectId = ParserUtils.getText(xpp.nextText());
				journeyPattern = ObjectFactory.getJourneyPattern(referential,
						objectId);
				locations.put(objectId, location);
			} else if (xpp.getName().equals("objectVersion")) {
				Integer version = ParserUtils.getInt(xpp.nextText());
				journeyPattern.setObjectVersion(version);
			} else if (xpp.getName().equals("creationTime")) {
				Date creationTime = ParserUtils.getSQLDateTime(xpp.nextText());
				journeyPattern.setCreationTime(creationTime);
			} else if (xpp.getName().equals("creatorId")) {
				journeyPattern
						.setCreatorId(ParserUtils.getText(xpp.nextText()));
			} else if (xpp.getName().equals("name")) {
				journeyPattern.setName(ParserUtils.getText(xpp.nextText()));
			} else if (xpp.getName().equals("publishedName")) {
				journeyPattern.setPublishedName(ParserUtils.getText(xpp
						.nextText()));
			} else if (xpp.getName().equals("routeId")) {
				String objectId = ParserUtils.getText(xpp.nextText());
				Route route = ObjectFactory.getRoute(referential, objectId);
				journeyPattern.setRoute(route);
			} else if (xpp.getName().equals("origin")) {
				String objectId = ParserUtils.getText(xpp.nextText());
				StopPoint departureStopPoint = ObjectFactory.getStopPoint(
						referential, objectId);
				journeyPattern.setDepartureStopPoint(departureStopPoint);
				// TODO origin

			} else if (xpp.getName().equals("destination")) {
				String objectId = ParserUtils.getText(xpp.nextText());
				StopPoint arrivalStopPoint = ObjectFactory.getStopPoint(
						referential, objectId);
				journeyPattern.setArrivalStopPoint(arrivalStopPoint);
				// TODO destination

			} else if (xpp.getName().equals("stopPointList")) {
				String objectId = ParserUtils.getText(xpp.nextText());
				StopPoint stopPoint = ObjectFactory.getStopPoint(referential,
						objectId);
				journeyPattern.addStopPoint(stopPoint);
			} else if (xpp.getName().equals("registration")) {

				while (xpp.nextTag() == XmlPullParser.START_TAG) {
					if (xpp.getName().equals("registrationNumber")) {
						journeyPattern.setRegistrationNumber(ParserUtils
								.getText(xpp.nextText()));
					} else {
						XPPUtil.skipSubTree(log, xpp);
					}
				}
			} else if (xpp.getName().equals("comment")) {
				journeyPattern.setComment(ParserUtils.getText(xpp.nextText()));
			} else if (xpp.getName().equals("lineIdShortcut")) {
				String objectId = ParserUtils.getText(xpp.nextText());
				Line line = ObjectFactory.getLine(referential, objectId);
				// TODO lineIdShortcut
			} else {
				XPPUtil.skipSubTree(log, xpp);
			}
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
