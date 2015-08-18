package mobi.chouette.exchange.neptune.parser;

import java.util.Date;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Constant;
import mobi.chouette.common.Context;
import mobi.chouette.common.XPPUtil;
import mobi.chouette.exchange.importer.Parser;
import mobi.chouette.exchange.importer.ParserFactory;
import mobi.chouette.exchange.importer.ParserUtils;
import mobi.chouette.exchange.neptune.JsonExtension;
import mobi.chouette.exchange.neptune.validation.StopPointValidator;
import mobi.chouette.exchange.validation.ValidatorFactory;
import mobi.chouette.model.StopArea;
import mobi.chouette.model.StopPoint;
import mobi.chouette.model.type.AlightingPossibilityEnum;
import mobi.chouette.model.type.BoardingPossibilityEnum;
import mobi.chouette.model.type.LongLatTypeEnum;
import mobi.chouette.model.util.ObjectFactory;
import mobi.chouette.model.util.Referential;

import org.codehaus.jettison.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;

@Log4j
public class StopPointParser implements Parser, Constant, JsonExtension {
	private static final String CHILD_TAG = "StopPoint";

	@Override
	public void parse(Context context) throws Exception {

		XmlPullParser xpp = (XmlPullParser) context.get(PARSER);
		Referential referential = (Referential) context.get(REFERENTIAL);

		xpp.require(XmlPullParser.START_TAG, null, CHILD_TAG);
		int columnNumber = xpp.getColumnNumber();
		int lineNumber = xpp.getLineNumber();

		StopPointValidator validator = (StopPointValidator) ValidatorFactory.create(StopPointValidator.class.getName(),
				context);

		StopPoint stopPoint = null;
		String objectId = null;
		while (xpp.nextTag() == XmlPullParser.START_TAG) {

			if (xpp.getName().equals("objectId")) {
				objectId = ParserUtils.getText(xpp.nextText());
				stopPoint = ObjectFactory.getStopPoint(referential, objectId);
				stopPoint.setFilled(true);
				validator.addLocation(context, objectId, lineNumber, columnNumber);
			} else if (xpp.getName().equals("objectVersion")) {
				Integer version = ParserUtils.getInt(xpp.nextText());
				stopPoint.setObjectVersion(version);
			} else if (xpp.getName().equals("creationTime")) {
				Date creationTime = ParserUtils.getSQLDateTime(xpp.nextText());
				stopPoint.setCreationTime(creationTime);
			} else if (xpp.getName().equals("creatorId")) {
				stopPoint.setCreatorId(ParserUtils.getText(xpp.nextText()));
			} else if (xpp.getName().equals("comment")) {
				parseComment(ParserUtils.getText(xpp.nextText()),stopPoint);
			} else if (xpp.getName().equals("containedIn")) {
				String containedIn = ParserUtils.getText(xpp.nextText());
				validator.addContainedIn(context, objectId, containedIn);
				StopArea stopArea = ObjectFactory.getStopArea(referential, containedIn);
				stopPoint.setContainedInStopArea(stopArea);
			} else if (xpp.getName().equals("lineIdShortcut")) {
				String lineIdShortcut = ParserUtils.getText(xpp.nextText());
				validator.addLineIdShortcut(context, objectId, lineIdShortcut);
			} else if (xpp.getName().equals("ptNetworkIdShortcut")) {
				String ptNetworkIdShortcut = ParserUtils.getText(xpp.nextText());
				validator.addPtNetworkIdShortcut(context, objectId, ptNetworkIdShortcut);
			} else if (xpp.getName().equals("longLatType")) {
				LongLatTypeEnum longLatType = ParserUtils.getEnum(LongLatTypeEnum.class, xpp.nextText());
				validator.addLongLatType(context, objectId, longLatType);
			} else {
				XPPUtil.skipSubTree(log, xpp);
			}
		}
	}

	protected void parseComment(String comment, StopPoint point) {
//		if (comment != null && comment.trim().startsWith("{") && comment.trim().endsWith("}")) {
//			try {
//				// parse json comment
//				JSONObject json = new JSONObject(comment);
//				if (json.has(ROUTING_CONSTRAINTS)) {
//					JSONObject rc = json.getJSONObject(ROUTING_CONSTRAINTS);
//					if (rc.has(BOARDING)) {
//						try {
//							BoardingPossibilityEnum forBoarding = BoardingPossibilityEnum.valueOf(rc
//									.getString(BOARDING));
//							point.setForBoarding(forBoarding);
//						} catch (IllegalArgumentException e) {
//							log.error("unknown value " + rc.getString(BOARDING) + " for boarding");
//						}
//					}
//					if (rc.has(ALIGHTING)) {
//						try {
//							AlightingPossibilityEnum forAlighting = AlightingPossibilityEnum.valueOf(rc
//									.getString(ALIGHTING));
//							point.setForAlighting(forAlighting);
//						} catch (IllegalArgumentException e) {
//							log.error("unknown value " + rc.getString(ALIGHTING) + " for alighting");
//						}
//					}
//				}
//			} catch (Exception e1) {
//				log.warn("unparsable json : " + comment);
//			}
//		}
		point.setComment(comment);
	}

	static {
		ParserFactory.register(StopPointParser.class.getName(), new ParserFactory() {
			private StopPointParser instance = new StopPointParser();

			@Override
			protected Parser create() {
				return instance;
			}
		});
	}
}
