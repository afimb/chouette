package mobi.chouette.exchange.neptune.parser;

import java.math.BigDecimal;
import java.sql.Time;
import java.util.Date;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Constant;
import mobi.chouette.common.Context;
import mobi.chouette.common.XPPUtil;
import mobi.chouette.exchange.importer.Parser;
import mobi.chouette.exchange.importer.ParserFactory;
import mobi.chouette.exchange.importer.ParserUtils;
import mobi.chouette.exchange.neptune.validation.AccessPointValidator;
import mobi.chouette.exchange.validation.ValidatorFactory;
import mobi.chouette.model.AccessPoint;
import mobi.chouette.model.StopArea;
import mobi.chouette.model.type.AccessPointTypeEnum;
import mobi.chouette.model.type.LongLatTypeEnum;
import mobi.chouette.model.util.ObjectFactory;
import mobi.chouette.model.util.Referential;

import org.xmlpull.v1.XmlPullParser;

@Log4j
public class AccessPointParser implements Parser, Constant {
	private static final String CHILD_TAG = "AccessPoint";

	@Override
	public void parse(Context context) throws Exception {

		XmlPullParser xpp = (XmlPullParser) context.get(PARSER);
		Referential referential = (Referential) context.get(REFERENTIAL);

		xpp.require(XmlPullParser.START_TAG, null, CHILD_TAG);
		int columnNumber =  xpp.getColumnNumber();
		int lineNumber =  xpp.getLineNumber();
		
		AccessPointValidator validator = (AccessPointValidator) ValidatorFactory.create(AccessPointValidator.class.getName(), context);

		AccessPoint accessPoint = null;
		String objectId = null;
		while (xpp.nextTag() == XmlPullParser.START_TAG) {

			if (xpp.getName().equals("objectId")) {
				 objectId = ParserUtils.getText(xpp.nextText());
				accessPoint = ObjectFactory.getAccessPoint(referential,
						objectId);
				accessPoint.setFilled(true);
			} else if (xpp.getName().equals("objectVersion")) {
				Integer version = ParserUtils.getInt(xpp.nextText());
				accessPoint.setObjectVersion(version);
			} else if (xpp.getName().equals("creationTime")) {
				Date creationTime = ParserUtils.getSQLDateTime(xpp.nextText());
				accessPoint.setCreationTime(creationTime);
			} else if (xpp.getName().equals("creatorId")) {
				accessPoint.setCreatorId(ParserUtils.getText(xpp.nextText()));
			} else if (xpp.getName().equals("name")) {
				accessPoint.setName(ParserUtils.getText(xpp.nextText()));
			} else if (xpp.getName().equals("comment")) {
				accessPoint.setComment(ParserUtils.getText(xpp.nextText()));
			} else if (xpp.getName().equals("containedIn")) {
				String containedInId = ParserUtils.getText(xpp.nextText());
				validator.addContainedIn(context, objectId, containedInId);
				StopArea stopArea = ObjectFactory.getStopArea(referential,
						containedInId);
				accessPoint.setContainedIn(stopArea);
			} else if (xpp.getName().equals("address")) {

				while (xpp.nextTag() == XmlPullParser.START_TAG) {
					if (xpp.getName().equals("countryCode")) {
						accessPoint.setCountryCode(ParserUtils.getText(xpp
								.nextText()));
					} else if (xpp.getName().equals("streetName")) {
						accessPoint.setStreetName(ParserUtils.getText(xpp
								.nextText()));
					} else {
						XPPUtil.skipSubTree(log, xpp);
					}
				}
			} else if (xpp.getName().equals("longLatType")) {
				accessPoint.setLongLatType(ParserUtils.getEnum(
						LongLatTypeEnum.class, xpp.nextText()));
			} else if (xpp.getName().equals("latitude")) {
				accessPoint.setLatitude(ParserUtils.getBigDecimal(xpp
						.nextText()));
			} else if (xpp.getName().equals("longitude")) {
				accessPoint.setLongitude(ParserUtils.getBigDecimal(xpp
						.nextText()));

			} else if (xpp.getName().equals("projectedPoint")) {

				while (xpp.nextTag() == XmlPullParser.START_TAG) {
					if (xpp.getName().equals("X")) {
						BigDecimal value = ParserUtils.getBigDecimal(xpp
								.nextText());
						accessPoint.setX(value);
					} else if (xpp.getName().equals("Y")) {
						BigDecimal value = ParserUtils.getBigDecimal(xpp
								.nextText());
						accessPoint.setY(value);
					} else if (xpp.getName().equals("projectionType")) {
						String value = ParserUtils.getText(xpp.nextText());
						accessPoint.setProjectionType(value);
					} else {
						XPPUtil.skipSubTree(log, xpp);
					}
				}
			} else if (xpp.getName().equals("openingTime")) {
				Time value = ParserUtils.getSQLTime(xpp.nextText());
				accessPoint.setOpeningTime(value);
			} else if (xpp.getName().equals("closingTime")) {
				Time value = ParserUtils.getSQLTime(xpp.nextText());
				accessPoint.setClosingTime(value);
			} else if (xpp.getName().equals("type")) {
				AccessPointTypeEnum value = ParserUtils.getEnum(
						AccessPointTypeEnum.class, xpp.nextText());
				accessPoint.setType(value);
			} else if (xpp.getName().equals("LiftAvailability")) {
				Boolean value = ParserUtils.getBoolean(xpp.nextText());
				accessPoint.setLiftAvailable(value);
			} else if (xpp.getName().equals("mobilityRestrictedSuitability")) {
				Boolean value = ParserUtils.getBoolean(xpp.nextText());
				accessPoint.setMobilityRestrictedSuitable(value);
			} else if (xpp.getName().equals("stairsAvailability")) {
				Boolean value = ParserUtils.getBoolean(xpp.nextText());
				accessPoint.setStairsAvailable(value);
			} else {
				XPPUtil.skipSubTree(log, xpp);
			}
		}
		validator.addLocation(context, accessPoint, lineNumber, columnNumber);
	}

	static {
		ParserFactory.register(AccessPointParser.class.getName(),
				new ParserFactory() {
					private AccessPointParser instance = new AccessPointParser();

					@Override
					protected Parser create() {
						return instance;
					}
				});
	}
}
