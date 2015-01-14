package mobi.chouette.exchange.neptune.parser;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Context;
import mobi.chouette.exchange.neptune.Constant;
import mobi.chouette.model.StopArea;
import mobi.chouette.model.StopPoint;
import mobi.chouette.model.type.ChouetteAreaEnum;
import mobi.chouette.model.type.LongLatTypeEnum;
import mobi.chouette.model.type.UserNeedEnum;

import org.xmlpull.v1.XmlPullParser;

@Log4j
public class ChouetteAreaParser implements Parser, Constant {
	private static final String CHILD_TAG = "ChouetteArea";

	@Override
	public void parse(Context context) throws Exception {

		XmlPullParser xpp = (XmlPullParser) context.get(XPP);
		Referential referential = (Referential) context.get(REFERENTIAL);

		xpp.require(XmlPullParser.START_TAG, null, CHILD_TAG);

		Map<String, String> map = new HashMap<String, String>();
		Map<String, String> inverse = new HashMap<String, String>();

		while (xpp.nextTag() == XmlPullParser.START_TAG) {

			if (xpp.getName().equals("StopArea")) {

				StopArea stopArea = null;
				List<String> contains = new ArrayList<String>();

				while (xpp.nextTag() == XmlPullParser.START_TAG) {

					if (xpp.getName().equals("objectId")) {
						String objectId = NeptuneUtils.getText(xpp.nextText());
						stopArea = ObjectFactory.getStopArea(referential,
								objectId);
					} else if (xpp.getName().equals("objectVersion")) {
						Integer version = NeptuneUtils.getInt(xpp.nextText());
						stopArea.setObjectVersion(version);
					} else if (xpp.getName().equals("creationTime")) {
						Date creationTime = NeptuneUtils.getSQLDateTime(xpp
								.nextText());
						stopArea.setCreationTime(creationTime);
					} else if (xpp.getName().equals("creatorId")) {
						stopArea.setCreatorId(NeptuneUtils.getText(xpp
								.nextText()));
					} else if (xpp.getName().equals("name")) {
						stopArea.setName(NeptuneUtils.getText(xpp.nextText()));
					} else if (xpp.getName().equals("comment")) {
						stopArea.setComment(NeptuneUtils.getText(xpp.nextText()));
					} else if (xpp.getName().equals("StopAreaExtension")) {

						while (xpp.nextTag() == XmlPullParser.START_TAG) {

							if (xpp.getName().equals("areaType")) {
								stopArea.setAreaType(NeptuneUtils.getEnum(
										ChouetteAreaEnum.class, xpp.nextText()));
								if (stopArea.getAreaType() == ChouetteAreaEnum.BoardingPosition
										|| stopArea.getAreaType() == ChouetteAreaEnum.Quay) {
									for (String objectId : contains) {
										StopPoint stopPoint = ObjectFactory
												.getStopPoint(referential,
														objectId);
										stopPoint
												.setContainedInStopArea(stopArea);
									}
								} else {
									for (String objectId : contains) {
										StopArea child = ObjectFactory
												.getStopArea(referential,
														objectId);
										child.setParent(stopArea);
									}
								}
							} else if (xpp.getName().equals("nearestTopicName")) {
								stopArea.setNearestTopicName(NeptuneUtils
										.getText(xpp.nextText()));
							} else if (xpp.getName().equals("fareCode")) {
								stopArea.setFareCode(NeptuneUtils.getInt(xpp
										.nextText()));
							} else if (xpp.getName().equals("registration")) {
								while (xpp.nextTag() == XmlPullParser.START_TAG) {
									if (xpp.getName().equals(
											"registrationNumber")) {
										stopArea.setRegistrationNumber(NeptuneUtils
												.getText(xpp.nextText()));
									} else {
										XPPUtil.skipSubTree(log, xpp);
									}
								}
							} else if (xpp.getName().equals(
									"mobilityRestrictedSuitability")) {
								stopArea.setMobilityRestrictedSuitable(NeptuneUtils
										.getBoolean(xpp.nextText()));
							} else if (xpp.getName().equals(
									"accessibilitySuitabilityDetails")) {
								List<UserNeedEnum> userNeeds = new ArrayList<UserNeedEnum>();
								while (xpp.nextTag() == XmlPullParser.START_TAG) {
									if (xpp.getName().equals("MobilityNeed")
											|| xpp.getName().equals(
													"PsychosensoryNeed")
											|| xpp.getName().equals(
													"MedicalNeed")
											|| xpp.getName().equals(
													"EncumbranceNeed")) {
										UserNeedEnum userNeed = NeptuneUtils
												.getEnum(UserNeedEnum.class,
														xpp.nextText());
										if (userNeed != null) {
											userNeeds.add(userNeed);
										}

									} else {
										XPPUtil.skipSubTree(log, xpp);
									}
								}
								stopArea.setUserNeeds(userNeeds);
							} else if (xpp.getName().equals(
									"stairsAvailability")) {
								stopArea.setStairsAvailable(NeptuneUtils
										.getBoolean(xpp.nextText()));
							} else if (xpp.getName().equals("liftAvailability")) {
								stopArea.setLiftAvailable(NeptuneUtils
										.getBoolean(xpp.nextText()));
							} else {
								XPPUtil.skipSubTree(log, xpp);
							}
						}
					} else if (xpp.getName().equals("contains")) {
						contains.add(NeptuneUtils.getText(xpp.nextText()));
					} else if (xpp.getName().equals("centroidOfArea")) {
						String key = stopArea.getObjectId();
						String value = NeptuneUtils.getText(xpp.nextText());
						map.put(key, value);
						inverse.put(value, key);

					} else {
						XPPUtil.skipSubTree(log, xpp);
					}
				}

			} else if (xpp.getName().equals("AreaCentroid")) {

				while (xpp.nextTag() == XmlPullParser.START_TAG) {

					StopArea stopArea = null;
					if (xpp.getName().equals("objectId")) {
						String objectId = inverse.get(NeptuneUtils.getText(xpp
								.nextText()));
						stopArea = ObjectFactory.getStopArea(referential,
								objectId);
					} else if (xpp.getName().equals("name")) {
						stopArea.setName(NeptuneUtils.getText(xpp.nextText()));
					} else if (xpp.getName().equals("comment")) {
						stopArea.setComment(NeptuneUtils.getText(xpp.nextText()));
					} else if (xpp.getName().equals("longLatType")) {
						stopArea.setLongLatType(NeptuneUtils.getEnum(
								LongLatTypeEnum.class, xpp.nextText()));
					} else if (xpp.getName().equals("latitude")) {
						stopArea.setLatitude(NeptuneUtils.getBigDecimal(xpp
								.nextText()));
					} else if (xpp.getName().equals("longitude")) {
						stopArea.setLongitude(NeptuneUtils.getBigDecimal(xpp
								.nextText()));
					} else if (xpp.getName().equals("containedIn")) {
						String objectId = NeptuneUtils.getText(xpp.nextText());
					} else if (xpp.getName().equals("address")) {

						while (xpp.nextTag() == XmlPullParser.START_TAG) {
							if (xpp.getName().equals("countryCode")) {
								stopArea.setCountryCode(NeptuneUtils
										.getText(xpp.nextText()));
							} else if (xpp.getName().equals("streetName")) {
								stopArea.setStreetName(NeptuneUtils.getText(xpp
										.nextText()));
							} else {
								XPPUtil.skipSubTree(log, xpp);
							}
						}
					} else if (xpp.getName().equals("projectedPoint")) {

						while (xpp.nextTag() == XmlPullParser.START_TAG) {
							if (xpp.getName().equals("X")) {
								BigDecimal value = NeptuneUtils
										.getBigDecimal(xpp.nextText());
								stopArea.setX(value);
							} else if (xpp.getName().equals("Y")) {
								BigDecimal value = NeptuneUtils
										.getBigDecimal(xpp.nextText());
								stopArea.setY(value);
							} else if (xpp.getName().equals("projectionType")) {
								String value = NeptuneUtils.getText(xpp
										.nextText());
								stopArea.setProjectionType(value);
							} else {
								XPPUtil.skipSubTree(log, xpp);
							}
						}
					} else {
						XPPUtil.skipSubTree(log, xpp);
					}
				}
			} else {
				XPPUtil.skipSubTree(log, xpp);
			}
		}
	}

	static {
		ParserFactory.register(ChouetteAreaParser.class.getName(),
				new ParserFactory() {
					private ChouetteAreaParser instance = new ChouetteAreaParser();

					@Override
					protected Parser create() {
						return instance;
					}
				});
	}
}
