package mobi.chouette.exchange.neptune.parser;

import java.util.Date;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Constant;
import mobi.chouette.common.Context;
import mobi.chouette.common.XPPUtil;
import mobi.chouette.exchange.importer.Parser;
import mobi.chouette.exchange.importer.ParserFactory;
import mobi.chouette.exchange.importer.ParserUtils;
import mobi.chouette.exchange.neptune.validation.JourneyPatternValidator;
import mobi.chouette.exchange.validation.ValidatorFactory;
import mobi.chouette.model.JourneyPattern;
import mobi.chouette.model.Route;
import mobi.chouette.model.StopPoint;
import mobi.chouette.model.util.NeptuneUtil;
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
		int columnNumber =  xpp.getColumnNumber();
		int lineNumber =  xpp.getLineNumber();

		JourneyPatternValidator validator = (JourneyPatternValidator) ValidatorFactory.create(JourneyPatternValidator.class.getName(), context);

		JourneyPattern journeyPattern = null;
		String objectId = null;
		while (xpp.nextTag() == XmlPullParser.START_TAG) {

			if (xpp.getName().equals("objectId")) {
				objectId = ParserUtils.getText(xpp.nextText());
				journeyPattern = ObjectFactory.getJourneyPattern(referential,
						objectId);
				journeyPattern.setFilled(true);
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
				String routeId = ParserUtils.getText(xpp.nextText());
				validator.addRouteId(context, objectId, routeId);
				Route route = ObjectFactory.getRoute(referential, routeId);
				journeyPattern.setRoute(route);
			} else if (xpp.getName().equals("origin")) {
				String origin = ParserUtils.getText(xpp.nextText());
				StopPoint departureStopPoint = ObjectFactory.getStopPoint(
						referential, origin);
				journeyPattern.setDepartureStopPoint(departureStopPoint);

			} else if (xpp.getName().equals("destination")) {
				String destination = ParserUtils.getText(xpp.nextText());
				StopPoint arrivalStopPoint = ObjectFactory.getStopPoint(
						referential, destination);
				journeyPattern.setArrivalStopPoint(arrivalStopPoint);

			} else if (xpp.getName().equals("stopPointList")) {
				String stopPointId = ParserUtils.getText(xpp.nextText());
				validator.addStopPointList(context, objectId, stopPointId);
				StopPoint stopPoint = ObjectFactory.getStopPoint(referential,
						stopPointId);
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
				String lineIdShortcut = ParserUtils.getText(xpp.nextText());
				validator.addLineIdShortcut(context, objectId, lineIdShortcut);
			} else {
				XPPUtil.skipSubTree(log, xpp);
			}
		}
		validator.addLocation(context, journeyPattern, lineNumber, columnNumber);
		NeptuneUtil.refreshDepartureArrivals(journeyPattern);
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
