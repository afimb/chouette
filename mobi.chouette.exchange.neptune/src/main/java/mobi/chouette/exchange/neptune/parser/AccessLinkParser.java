package mobi.chouette.exchange.neptune.parser;

import java.math.BigDecimal;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Context;
import mobi.chouette.exchange.neptune.Constant;
import mobi.chouette.model.AccessLink;
import mobi.chouette.model.AccessPoint;
import mobi.chouette.model.StopArea;
import mobi.chouette.model.type.ConnectionLinkTypeEnum;
import mobi.chouette.model.type.LinkOrientationEnum;
import mobi.chouette.model.type.UserNeedEnum;

import org.xmlpull.v1.XmlPullParser;

@Log4j
public class AccessLinkParser implements Parser, Constant {
	private static final String CHILD_TAG = "ConnectionLink";

	@Override
	public void parse(Context context) throws Exception {

		XmlPullParser xpp = (XmlPullParser) context.get(XPP);
		Referential referential = (Referential) context.get(REFERENTIAL);

		xpp.require(XmlPullParser.START_TAG, null, CHILD_TAG);

		AccessLink accessLink = null;
		while (xpp.nextTag() == XmlPullParser.START_TAG) {

			if (xpp.getName().equals("objectId")) {
				String objectId = NeptuneUtils.getText(xpp.nextText());
				accessLink = ObjectFactory.getAccessLink(referential, objectId);
			} else if (xpp.getName().equals("objectVersion")) {
				Integer version = NeptuneUtils.getInt(xpp.nextText());
				accessLink.setObjectVersion(version);
			} else if (xpp.getName().equals("creationTime")) {
				Date creationTime = NeptuneUtils.getSQLDateTime(xpp.nextText());
				accessLink.setCreationTime(creationTime);
			} else if (xpp.getName().equals("creatorId")) {
				accessLink.setCreatorId(NeptuneUtils.getText(xpp.nextText()));
			} else if (xpp.getName().equals("name")) {
				accessLink.setName(NeptuneUtils.getText(xpp.nextText()));
			} else if (xpp.getName().equals("comment")) {
				accessLink.setComment(NeptuneUtils.getText(xpp.nextText()));
			} else if (xpp.getName().equals("startOfLink")) {
				String objectId = NeptuneUtils.getText(xpp.nextText());
				if (referential.getStopAreas().containsKey(objectId)) {
					StopArea stopArea = ObjectFactory.getStopArea(referential,
							objectId);
					accessLink.setStopArea(stopArea);
					accessLink
							.setLinkOrientation(LinkOrientationEnum.StopAreaToAccessPoint);
				} else if (referential.getAccessPoint().containsKey(objectId)) {
					AccessPoint accessPoint = ObjectFactory.getAccessPoint(
							referential, objectId);
					accessLink.setAccessPoint(accessPoint);
					accessLink
							.setLinkOrientation(LinkOrientationEnum.AccessPointToStopArea);
				}
			} else if (xpp.getName().equals("endOfLink")) {
				String objectId = NeptuneUtils.getText(xpp.nextText());
				if (referential.getStopAreas().containsKey(objectId)) {
					StopArea stopArea = ObjectFactory.getStopArea(referential,
							objectId);
					accessLink.setStopArea(stopArea);
				} else if (referential.getAccessPoint().containsKey(objectId)) {
					AccessPoint accessPoint = ObjectFactory.getAccessPoint(
							referential, objectId);
					accessLink.setAccessPoint(accessPoint);
				}
			} else if (xpp.getName().equals("linkDistance")) {
				BigDecimal value = NeptuneUtils.getBigDecimal(xpp.nextText());
				accessLink.setLinkDistance(value);
			} else if (xpp.getName().equals("liftAvailability")) {
				boolean value = NeptuneUtils.getBoolean(xpp.nextText());
				accessLink.setLiftAvailable(value);
			} else if (xpp.getName().equals("mobilityRestrictedSuitability")) {
				boolean value = NeptuneUtils.getBoolean(xpp.nextText());
				accessLink.setMobilityRestrictedSuitable(value);
			} else if (xpp.getName().equals("stairsAvailability")) {
				boolean value = NeptuneUtils.getBoolean(xpp.nextText());
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
								UserNeedEnum userNeed = NeptuneUtils.getEnum(
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
				Time value = NeptuneUtils.getSQLDuration(xpp.nextText());
				accessLink.setDefaultDuration(value);
			} else if (xpp.getName().equals("frequentTravellerDuration")) {
				Time value = NeptuneUtils.getSQLDuration(xpp.nextText());
				accessLink.setFrequentTravellerDuration(value);
			} else if (xpp.getName().equals("occasionalTravellerDuration")) {
				Time value = NeptuneUtils.getSQLDuration(xpp.nextText());
				accessLink.setOccasionalTravellerDuration(value);
			} else if (xpp.getName().equals(
					"mobilityRestrictedTravellerDuration")) {
				Time value = NeptuneUtils.getSQLDuration(xpp.nextText());
				accessLink.setMobilityRestrictedTravellerDuration(value);
			} else if (xpp.getName().equals("linkType")) {
				ConnectionLinkTypeEnum value = NeptuneUtils.getEnum(
						ConnectionLinkTypeEnum.class, xpp.nextText());
				accessLink.setLinkType(value);
			} else {
				XPPUtil.skipSubTree(log, xpp);
			}
		}
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
