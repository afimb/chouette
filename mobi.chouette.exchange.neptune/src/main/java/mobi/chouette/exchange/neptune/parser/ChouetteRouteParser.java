package mobi.chouette.exchange.neptune.parser;

import java.util.Date;
import java.util.List;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Context;
import mobi.chouette.common.XPPUtil;
import mobi.chouette.exchange.importer.Parser;
import mobi.chouette.exchange.importer.ParserFactory;
import mobi.chouette.exchange.importer.ParserUtils;
import mobi.chouette.exchange.neptune.Constant;
import mobi.chouette.exchange.neptune.NeptuneChouetteIdGenerator;
import mobi.chouette.exchange.neptune.importer.NeptuneImportParameters;
import mobi.chouette.exchange.neptune.model.NeptuneObjectFactory;
import mobi.chouette.exchange.neptune.model.PTLink;
import mobi.chouette.exchange.neptune.validation.ChouetteRouteValidator;
import mobi.chouette.exchange.validation.ValidatorFactory;
import mobi.chouette.model.JourneyPattern;
import mobi.chouette.model.Route;
import mobi.chouette.model.type.PTDirectionEnum;
import mobi.chouette.exchange.neptune.NeptuneChouetteIdObjectUtil;
import mobi.chouette.model.util.Referential;

import org.xmlpull.v1.XmlPullParser;

@Log4j
public class ChouetteRouteParser implements Parser, Constant {
	private static final String CHILD_TAG = "ChouetteRoute";

	@Override
	public void parse(Context context) throws Exception {

		XmlPullParser xpp = (XmlPullParser) context.get(PARSER);
		Referential referential = (Referential) context.get(REFERENTIAL);
		NeptuneObjectFactory factory = (NeptuneObjectFactory) context.get(NEPTUNE_OBJECT_FACTORY);
		
		NeptuneImportParameters parameters = (NeptuneImportParameters) context.get(CONFIGURATION);
		NeptuneChouetteIdGenerator neptuneChouetteIdGenerator = (NeptuneChouetteIdGenerator) context.get(CHOUETTEID_GENERATOR);

		xpp.require(XmlPullParser.START_TAG, null, CHILD_TAG);
		int columnNumber = xpp.getColumnNumber();
		int lineNumber = xpp.getLineNumber();

		ChouetteRouteValidator validator = (ChouetteRouteValidator) ValidatorFactory.create(
				ChouetteRouteValidator.class.getName(), context);

		Route route = null;
		String objectId = null;
		while (xpp.nextTag() == XmlPullParser.START_TAG) {
			if (xpp.getName().equals("objectId")) {
				objectId = ParserUtils.getText(xpp.nextText());
				route = NeptuneChouetteIdObjectUtil.getRoute(referential, neptuneChouetteIdGenerator.toChouetteId(objectId, parameters.getDefaultCodespace(),Route.class));
				route.setFilled(true);
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
				PTDirectionEnum value = ParserUtils.getEnum(PTDirectionEnum.class, xpp.nextText());
				route.setDirection(value);
			} else if (xpp.getName().equals("journeyPatternId")) {
				String journeyPatternId = ParserUtils.getText(xpp.nextText());
				validator.addJourneyPatternId(context, objectId, journeyPatternId);
				JourneyPattern journeyPattern = NeptuneChouetteIdObjectUtil.getJourneyPattern(referential, neptuneChouetteIdGenerator.toChouetteId(journeyPatternId, parameters.getDefaultCodespace(),JourneyPattern.class));
				journeyPattern.setRoute(route);
			} else if (xpp.getName().equals("number")) {
				route.setNumber(ParserUtils.getText(xpp.nextText()));
			} else if (xpp.getName().equals("ptLinkId")) {
				String ptLinkId = ParserUtils.getText(xpp.nextText());
				validator.addPtLinkId(context, objectId, ptLinkId);
				PTLink ptLink = factory.getPTLink(neptuneChouetteIdGenerator.toChouetteId(ptLinkId, parameters.getDefaultCodespace(),PTLink.class));
				List<PTLink> list = factory.getPTLinksOnRoute(route);
				list.add(ptLink);
				ptLink.setRoute(route);
			} else if (xpp.getName().equals("RouteExtension")) {
				while (xpp.nextTag() == XmlPullParser.START_TAG) {
					if (xpp.getName().equals("wayBack")) {
						String value = ParserUtils.getText(xpp.nextText()).toLowerCase().startsWith("a") ? "A" : "R";
						route.setWayBack(value);
					} else {
						XPPUtil.skipSubTree(log, xpp);
					}
				}
			} else if (xpp.getName().equals("wayBackRouteId")) {
				String wayBackRouteId = ParserUtils.getText(xpp.nextText());
				validator.addWayBackRouteId(context, objectId, wayBackRouteId);
				Route wayBackRoute = referential.getRoutes().get(wayBackRouteId);
				if (wayBackRoute != null) {
					wayBackRoute.setOppositeRoute(route);
				}

			} else if (xpp.getName().equals("comment")) {
				route.setComment(ParserUtils.getText(xpp.nextText()));
			} else {
				XPPUtil.skipSubTree(log, xpp);
			}
		}
		validator.addLocation(context, route, lineNumber, columnNumber);

	}

	static {
		ParserFactory.register(ChouetteRouteParser.class.getName(), new ParserFactory() {
			private ChouetteRouteParser instance = new ChouetteRouteParser();

			@Override
			protected Parser create() {
				return instance;
			}
		});
	}
}
