package mobi.chouette.exchange.neptune.parser;

import java.math.BigDecimal;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Constant;
import mobi.chouette.common.Context;
import mobi.chouette.common.XPPUtil;
import mobi.chouette.exchange.importer.Parser;
import mobi.chouette.exchange.importer.ParserFactory;
import mobi.chouette.exchange.importer.ParserUtils;
import mobi.chouette.exchange.neptune.validation.AccessLinkValidator;
import mobi.chouette.exchange.validation.ValidatorFactory;
import mobi.chouette.model.AccessLink;
import mobi.chouette.model.AccessPoint;
import mobi.chouette.model.StopArea;
import mobi.chouette.model.type.ConnectionLinkTypeEnum;
import mobi.chouette.model.type.LinkOrientationEnum;
import mobi.chouette.model.type.UserNeedEnum;
import mobi.chouette.model.util.ObjectFactory;
import mobi.chouette.model.util.Referential;

import org.xmlpull.v1.XmlPullParser;

@Log4j
public class AccessLinkParser implements Parser, Constant {
	private static final String CHILD_TAG = "AccessLink";

	@Override
	public void parse(Context context) throws Exception {

		XmlPullParser xpp = (XmlPullParser) context.get(PARSER);
		Referential referential = (Referential) context.get(REFERENTIAL);

		xpp.require(XmlPullParser.START_TAG, null, CHILD_TAG);
		int columnNumber =  xpp.getColumnNumber();
		int lineNumber =  xpp.getLineNumber();
		
		AccessLinkValidator validator = (AccessLinkValidator) ValidatorFactory.create(AccessLinkValidator.class.getName(), context);
		AccessLink accessLink = null;
		String objectId = null;
		while (xpp.nextTag() == XmlPullParser.START_TAG) {

			if (xpp.getName().equals("objectId")) {
			    objectId = ParserUtils.getText(xpp.nextText());
				accessLink = ObjectFactory.getAccessLink(referential, objectId);
				accessLink.setFilled(true);
			} else if (xpp.getName().equals("objectVersion")) {
				Integer version = ParserUtils.getInt(xpp.nextText());
				accessLink.setObjectVersion(version);
			} else if (xpp.getName().equals("creationTime")) {
				Date creationTime = ParserUtils.getSQLDateTime(xpp.nextText());
				accessLink.setCreationTime(creationTime);
			} else if (xpp.getName().equals("creatorId")) {
				accessLink.setCreatorId(ParserUtils.getText(xpp.nextText()));
			} else if (xpp.getName().equals("name")) {
				accessLink.setName(ParserUtils.getText(xpp.nextText()));
			} else if (xpp.getName().equals("comment")) {
				accessLink.setComment(ParserUtils.getText(xpp.nextText()));
			} else if (xpp.getName().equals("startOfLink")) {
				String linkId = ParserUtils.getText(xpp.nextText());
				validator.addStartOfLinkId(context, objectId, linkId);
				if (referential.getStopAreas().containsKey(linkId)) {
					StopArea stopArea = ObjectFactory.getStopArea(referential,
							linkId);
					accessLink.setStopArea(stopArea);
					accessLink
							.setLinkOrientation(LinkOrientationEnum.StopAreaToAccessPoint);
				} else if (referential.getAccessPoints().containsKey(linkId)) {
					AccessPoint accessPoint = ObjectFactory.getAccessPoint(
							referential, linkId);
					accessLink.setAccessPoint(accessPoint);
					accessLink
							.setLinkOrientation(LinkOrientationEnum.AccessPointToStopArea);
				}
			} else if (xpp.getName().equals("endOfLink")) {
				String linkId = ParserUtils.getText(xpp.nextText());
				validator.addEndOfLinkId(context, objectId, linkId);
				if (referential.getStopAreas().containsKey(linkId)) {
					StopArea stopArea = ObjectFactory.getStopArea(referential,
							linkId);
					accessLink.setStopArea(stopArea);
				} else if (referential.getAccessPoints().containsKey(linkId)) {
					AccessPoint accessPoint = ObjectFactory.getAccessPoint(
							referential, linkId);
					accessLink.setAccessPoint(accessPoint);
				}
			} else if (xpp.getName().equals("linkDistance")) {
				BigDecimal value = ParserUtils.getBigDecimal(xpp.nextText());
				accessLink.setLinkDistance(value);
			} else if (xpp.getName().equals("liftAvailability")) {
				boolean value = ParserUtils.getBoolean(xpp.nextText());
				accessLink.setLiftAvailable(value);
			} else if (xpp.getName().equals("mobilityRestrictedSuitability")) {
				boolean value = ParserUtils.getBoolean(xpp.nextText());
				accessLink.setMobilityRestrictedSuitable(value);
			} else if (xpp.getName().equals("stairsAvailability")) {
				boolean value = ParserUtils.getBoolean(xpp.nextText());
				accessLink.setStairsAvailable(value);
			} else if (xpp.getName().equals("ConnectionLinkExtension")) {
				while (xpp.nextTag() == XmlPullParser.START_TAG) {

					if (xpp.getName().equals("accessibilitySuitabilityDetails")) {
						List<UserNeedEnum> userNeeds = new ArrayList<UserNeedEnum>();
						while (xpp.nextTag() == XmlPullParser.START_TAG) {
							if (xpp.getName().equals("MobilityNeed")
									|| xpp.getName()
											.equals("PsychosensoryNeed")
									|| xpp.getName().equals("MedicalNeed")) {
								UserNeedEnum userNeed = ParserUtils.getEnum(
										UserNeedEnum.class, xpp.nextText());
								if (userNeed != null) {
									userNeeds.add(userNeed);
								}
							} else {
								XPPUtil.skipSubTree(log, xpp);
							}
						}
						accessLink.setUserNeeds(userNeeds);
					} else {
						XPPUtil.skipSubTree(log, xpp);
					}
				}
			} else if (xpp.getName().equals("defaultDuration")) {
				Time value = ParserUtils.getSQLDuration(xpp.nextText());
				accessLink.setDefaultDuration(value);
			} else if (xpp.getName().equals("frequentTravellerDuration")) {
				Time value = ParserUtils.getSQLDuration(xpp.nextText());
				accessLink.setFrequentTravellerDuration(value);
			} else if (xpp.getName().equals("occasionalTravellerDuration")) {
				Time value = ParserUtils.getSQLDuration(xpp.nextText());
				accessLink.setOccasionalTravellerDuration(value);
			} else if (xpp.getName().equals(
					"mobilityRestrictedTravellerDuration")) {
				Time value = ParserUtils.getSQLDuration(xpp.nextText());
				accessLink.setMobilityRestrictedTravellerDuration(value);
			} else if (xpp.getName().equals("linkType")) {
				ConnectionLinkTypeEnum value = ParserUtils.getEnum(
						ConnectionLinkTypeEnum.class, xpp.nextText());
				accessLink.setLinkType(value);
			} else {
				XPPUtil.skipSubTree(log, xpp);
			}
		}
		validator.addLocation(context, accessLink, lineNumber, columnNumber);
	}

	static {
		ParserFactory.register(AccessLinkParser.class.getName(),
				new ParserFactory() {
					private AccessLinkParser instance = new AccessLinkParser();

					@Override
					protected Parser create() {
						return instance;
					}
				});
	}
}
