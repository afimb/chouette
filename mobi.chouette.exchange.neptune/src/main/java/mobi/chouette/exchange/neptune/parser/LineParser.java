package mobi.chouette.exchange.neptune.parser;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Context;
import mobi.chouette.exchange.neptune.Constant;
import mobi.chouette.model.Line;
import mobi.chouette.model.Route;
import mobi.chouette.model.type.TransportModeNameEnum;
import mobi.chouette.model.type.UserNeedEnum;

import org.xmlpull.v1.XmlPullParser;

import com.google.common.base.Predicate;
import com.google.common.collect.Maps;

@Log4j
public class LineParser implements Parser, Constant {
	private static final String CHILD_TAG = "Line";

	@Override
	public void parse(Context context) throws Exception {

		XmlPullParser xpp = (XmlPullParser) context.get(XPP);
		Referential referential = (Referential) context.get(REFERENTIAL);

		xpp.require(XmlPullParser.START_TAG, null, CHILD_TAG);

		Line line = null;
		while (xpp.nextTag() == XmlPullParser.START_TAG) {

			if (xpp.getName().equals("objectId")) {
				String objectId = NeptuneUtils.getText(xpp.nextText());
				line = ObjectFactory.getLine(referential, objectId);
			} else if (xpp.getName().equals("objectVersion")) {
				Integer version = NeptuneUtils.getInt(xpp.nextText());
				line.setObjectVersion(version);
			} else if (xpp.getName().equals("creationTime")) {
				Date creationTime = NeptuneUtils.getSQLDateTime(xpp.nextText());
				line.setCreationTime(creationTime);
			} else if (xpp.getName().equals("creatorId")) {
				line.setCreatorId(NeptuneUtils.getText(xpp.nextText()));
			} else if (xpp.getName().equals("name")) {
				line.setName(NeptuneUtils.getText(xpp.nextText()));
			} else if (xpp.getName().equals("number")) {
				line.setNumber(NeptuneUtils.getText(xpp.nextText()));
			} else if (xpp.getName().equals("publishedName")) {
				line.setPublishedName(NeptuneUtils.getText(xpp.nextText()));
			} else if (xpp.getName().equals("transportModeName")) {
				TransportModeNameEnum value = NeptuneUtils.getEnum(
						TransportModeNameEnum.class, xpp.nextText());
				line.setTransportModeName(value);
			} else if (xpp.getName().equals("LineEnd")) {
				String objectId = NeptuneUtils.getText(xpp.nextText());

			} else if (xpp.getName().equals("routeId")) {
				String objectId = NeptuneUtils.getText(xpp.nextText());
				Route route = ObjectFactory.getRoute(referential, objectId);

			} else if (xpp.getName().equals("ptNetworkIdShortcut")) {
				final String objectId = NeptuneUtils.getText(xpp.nextText());

				// remove lines
				Map<String, Line> removed = Maps.filterEntries(
						referential.getLines(),
						new Predicate<Map.Entry<String, Line>>() {
							@Override
							public boolean apply(Entry<String, Line> input) {
								boolean result = false;
								Line line = input.getValue();

								if (!line.getPtNetwork().getObjectId()
										.equals(objectId)) {
									line.setPTNetwork(null);
									result = true;
								}
								return result;
							}
						});

				for (String key : removed.keySet()) {
					referential.getLines().remove(key);
				}

			} else if (xpp.getName().equals("registration")) {
				while (xpp.nextTag() == XmlPullParser.START_TAG) {
					if (xpp.getName().equals("registrationNumber")) {
						line.setRegistrationNumber(NeptuneUtils.getText(xpp
								.nextText()));
					} else {
						XPPUtil.skipSubTree(log, xpp);
					}
				}
			} else if (xpp.getName().equals("comment")) {
				line.setComment(NeptuneUtils.getText(xpp.nextText()));
			} else if (xpp.getName().equals("LineExtension")) {

				while (xpp.nextTag() == XmlPullParser.START_TAG) {
					if (xpp.getName().equals("mobilityRestrictedSuitability")) {
						line.setRegistrationNumber(NeptuneUtils.getText(xpp
								.nextText()));
					} else if (xpp.getName().equals(
							"accessibilitySuitabilityDetails")) {
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
						line.setUserNeeds(userNeeds);
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
		ParserFactory.register(LineParser.class.getName(), new ParserFactory() {
			private LineParser instance = new LineParser();

			@Override
			protected Parser create() {
				return instance;
			}
		});
	}
}
