package mobi.chouette.exchange.neptune.parser;

import java.math.BigDecimal;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Context;
import mobi.chouette.exchange.neptune.Constant;
import mobi.chouette.model.ConnectionLink;
import mobi.chouette.model.StopArea;
import mobi.chouette.model.type.ConnectionLinkTypeEnum;
import mobi.chouette.model.type.UserNeedEnum;

import org.xmlpull.v1.XmlPullParser;

@Log4j
public class ConnectionLinkParser implements Parser, Constant {
	private static final String CHILD_TAG = "ConnectionLink";

	@Override
	public void parse(Context context) throws Exception {

		XmlPullParser xpp = (XmlPullParser) context.get(XPP);
		Referential referential = (Referential) context.get(REFERENTIAL);

		xpp.require(XmlPullParser.START_TAG, null, CHILD_TAG);

		ConnectionLink connectionLink = null;
		while (xpp.nextTag() == XmlPullParser.START_TAG) {

			if (xpp.getName().equals("objectId")) {
				String objectId = NeptuneUtils.getText(xpp.nextText());
				connectionLink = ObjectFactory.getConnectionLink(referential,
						objectId);
			} else if (xpp.getName().equals("objectVersion")) {
				Integer version = NeptuneUtils.getInt(xpp.nextText());
				connectionLink.setObjectVersion(version);
			} else if (xpp.getName().equals("creationTime")) {
				Date creationTime = NeptuneUtils.getSQLDateTime(xpp.nextText());
				connectionLink.setCreationTime(creationTime);
			} else if (xpp.getName().equals("creatorId")) {
				connectionLink
						.setCreatorId(NeptuneUtils.getText(xpp.nextText()));
			} else if (xpp.getName().equals("name")) {
				connectionLink.setName(NeptuneUtils.getText(xpp.nextText()));
			} else if (xpp.getName().equals("comment")) {
				connectionLink.setComment(NeptuneUtils.getText(xpp.nextText()));
			} else if (xpp.getName().equals("startOfLink")) {
				String objectId = NeptuneUtils.getText(xpp.nextText());
				StopArea startOfLink = ObjectFactory.getStopArea(referential,
						objectId);
				connectionLink.setStartOfLink(startOfLink);
			} else if (xpp.getName().equals("endOfLink")) {
				String objectId = NeptuneUtils.getText(xpp.nextText());
				StopArea endOfLink = ObjectFactory.getStopArea(referential,
						objectId);
				connectionLink.setStartOfLink(endOfLink);
			} else if (xpp.getName().equals("linkDistance")) {
				BigDecimal value = NeptuneUtils.getBigDecimal(xpp.nextText());
				connectionLink.setLinkDistance(value);

			} else if (xpp.getName().equals("liftAvailability")) {
				boolean value = NeptuneUtils.getBoolean(xpp.nextText());
				connectionLink.setLiftAvailable(value);
			} else if (xpp.getName().equals("mobilityRestrictedSuitability")) {
				boolean value = NeptuneUtils.getBoolean(xpp.nextText());
				connectionLink.setMobilityRestrictedSuitable(value);
			} else if (xpp.getName().equals("stairsAvailability")) {
				boolean value = NeptuneUtils.getBoolean(xpp.nextText());
				connectionLink.setStairsAvailable(value);
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
						connectionLink.setUserNeeds(userNeeds);
					} else {
						XPPUtil.skipSubTree(log, xpp);
					}
				}
			} else if (xpp.getName().equals("defaultDuration")) {
				Time value = NeptuneUtils.getSQLDuration(xpp.nextText());
				connectionLink.setDefaultDuration(value);
			} else if (xpp.getName().equals("frequentTravellerDuration")) {
				Time value = NeptuneUtils.getSQLDuration(xpp.nextText());
				connectionLink.setFrequentTravellerDuration(value);
			} else if (xpp.getName().equals("occasionalTravellerDuration")) {
				Time value = NeptuneUtils.getSQLDuration(xpp.nextText());
				connectionLink.setOccasionalTravellerDuration(value);
			} else if (xpp.getName().equals(
					"mobilityRestrictedTravellerDuration")) {
				Time value = NeptuneUtils.getSQLDuration(xpp.nextText());
				connectionLink.setMobilityRestrictedTravellerDuration(value);
			} else if (xpp.getName().equals("linkType")) {
				ConnectionLinkTypeEnum value = NeptuneUtils.getEnum(
						ConnectionLinkTypeEnum.class, xpp.nextText());
				connectionLink.setLinkType(value);
			} else {
				XPPUtil.skipSubTree(log, xpp);
			}
		}
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
