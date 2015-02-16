package mobi.chouette.exchange.neptune.parser;

import java.util.Date;
import java.util.List;
import java.util.Map;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Context;
import mobi.chouette.exchange.importer.Parser;
import mobi.chouette.exchange.importer.ParserFactory;
import mobi.chouette.exchange.importer.ParserUtils;
import mobi.chouette.exchange.importer.XPPUtil;
import mobi.chouette.exchange.neptune.Constant;
import mobi.chouette.exchange.neptune.model.NeptuneObjectFactory;
import mobi.chouette.exchange.neptune.model.PTLink;
import mobi.chouette.exchange.validation.report.FileLocation;
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
		NeptuneObjectFactory factory = (NeptuneObjectFactory) context
				.get(NEPTUNE_OBJECT_FACTORY);

		xpp.require(XmlPullParser.START_TAG, null, CHILD_TAG);
		context.put(COLUMN_NUMBER, xpp.getColumnNumber());
		context.put(LINE_NUMBER, xpp.getLineNumber());
		
		Map<String,FileLocation> locations = (Map<String, FileLocation>) context.get(OBJECT_LOCALISATION);
		FileLocation location = new FileLocation((String) context.get(FILE_URL), xpp.getLineNumber(), xpp.getColumnNumber());

		Route route = null;
		while (xpp.nextTag() == XmlPullParser.START_TAG) {

			if (xpp.getName().equals("objectId")) {
				String objectId = ParserUtils.getText(xpp.nextText());
				route = ObjectFactory.getRoute(referential, objectId);
				locations.put(objectId, location);
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
			} else if (xpp.getName().equals("ptLinkId")) {
				String objectId = ParserUtils.getText(xpp.nextText());
				PTLink ptLink = factory.getPTLink(objectId);
				List<PTLink> list = factory.getPTLinksOnRoute(route);
				list.add(ptLink);
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
		
		List<PTLink> list = factory.getPTLinksOnRoute(route);
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
