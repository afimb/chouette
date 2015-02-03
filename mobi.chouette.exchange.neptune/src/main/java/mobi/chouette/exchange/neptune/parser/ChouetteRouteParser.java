package mobi.chouette.exchange.neptune.parser;

import java.util.Date;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Constant;
import mobi.chouette.common.Context;
import mobi.chouette.exchange.importer.Parser;
import mobi.chouette.exchange.importer.ParserFactory;
import mobi.chouette.exchange.importer.ParserUtils;
import mobi.chouette.exchange.importer.XPPUtil;
import mobi.chouette.model.JourneyPattern;
import mobi.chouette.model.Route;
import mobi.chouette.model.type.PTDirectionEnum;
import mobi.chouette.model.util.ObjectFactory;
import mobi.chouette.model.util.Referential;

import org.xmlpull.v1.XmlPullParser;

@Log4j
public class ChouetteRouteParser implements Parser, Constant {
	private static final String CHILD_TAG = "ChouetteRoute";

	@Override
	public void parse(Context context) throws Exception {

		XmlPullParser xpp = (XmlPullParser) context.get(PARSER);
		Referential referential = (Referential) context.get(REFERENTIAL);

		xpp.require(XmlPullParser.START_TAG, null, CHILD_TAG);
		context.put(COLUMN_NUMBER, xpp.getColumnNumber());
		context.put(LINE_NUMBER, xpp.getLineNumber());

		Route route = null;
		while (xpp.nextTag() == XmlPullParser.START_TAG) {

			if (xpp.getName().equals("objectId")) {
				String objectId = ParserUtils.getText(xpp.nextText());
				route = ObjectFactory.getRoute(referential, objectId);
			} else if (xpp.getName().equals("objectVersion")) {
				Integer version = ParserUtils.getInt(xpp.nextText());
				route.setObjectVersion(version);
			} else if (xpp.getName().equals("creationTime")) {
				Date creationTime = ParserUtils.getSQLDateTime(xpp.nextText());
				route.setCreationTime(creationTime);
			} else if (xpp.getName().equals("creatorId")) {
				route.setCreatorId(ParserUtils.getText(xpp.nextText()));
			} else if (xpp.getName().equals("name")) {
				route.setName(ParserUtils.getText(xpp.nextText()));
			} else if (xpp.getName().equals("direction")) {
				PTDirectionEnum value = ParserUtils.getEnum(
						PTDirectionEnum.class, xpp.nextText());
				route.setDirection(value);
			} else if (xpp.getName().equals("journeyPatternId")) {
				String objectId = ParserUtils.getText(xpp.nextText());
				JourneyPattern journeyPattern = ObjectFactory
						.getJourneyPattern(referential, objectId);
				journeyPattern.setRoute(route);
			} else if (xpp.getName().equals("number")) {
				route.setNumber(ParserUtils.getText(xpp.nextText()));
			} else if (xpp.getName().equals("PtLinkId")) {
				String objectId = ParserUtils.getText(xpp.nextText());
				// TODO [DSU] PtLinkId
			} else if (xpp.getName().equals("RouteExtension")) {
				while (xpp.nextTag() == XmlPullParser.START_TAG) {
					if (xpp.getName().equals("wayBack")) {
						String value = ParserUtils.getText(xpp.nextText())
								.toLowerCase().startsWith("a") ? "A" : "R";
						route.setWayBack(value);
					} else {
						XPPUtil.skipSubTree(log, xpp);
					}
				}
			} else if (xpp.getName().equals("wayBackRouteId")) {
				String objectId = ParserUtils.getText(xpp.nextText());
				Route wayBackRoute = ObjectFactory.getRoute(referential,
						objectId);
				// TODO [DSU] wayBack oppositeRouteId
	
			} else if (xpp.getName().equals("comment")) {
				route.setComment(ParserUtils.getText(xpp.nextText()));
			} else {
				XPPUtil.skipSubTree(log, xpp);
			}
		}
	}

	static {
		ParserFactory.register(ChouetteRouteParser.class.getName(),
				new ParserFactory() {
					private ChouetteRouteParser instance = new ChouetteRouteParser();

					@Override
					protected Parser create() {
						return instance;
					}
				});
	}
}
