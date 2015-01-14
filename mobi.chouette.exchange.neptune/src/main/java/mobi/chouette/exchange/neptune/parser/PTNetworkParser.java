package mobi.chouette.exchange.neptune.parser;

import java.util.Date;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Context;
import mobi.chouette.exchange.neptune.Constant;
import mobi.chouette.model.Line;
import mobi.chouette.model.PTNetwork;
import mobi.chouette.model.type.PTNetworkSourceTypeEnum;

import org.xmlpull.v1.XmlPullParser;

@Log4j
public class PTNetworkParser implements Parser, Constant {
	private static final String CHILD_TAG = "PTNetwork";

	@Override
	public void parse(Context context) throws Exception {

		XmlPullParser xpp = (XmlPullParser) context.get(XPP);
		Referential referential = (Referential) context.get(REFERENTIAL);

		xpp.require(XmlPullParser.START_TAG, null, CHILD_TAG);

		PTNetwork network = null;
		while (xpp.nextTag() == XmlPullParser.START_TAG) {

			if (xpp.getName().equals("objectId")) {
				String objectId = NeptuneUtils.getText(xpp.nextText());
				network = ObjectFactory.getPTNetwork(referential, objectId);
			} else if (xpp.getName().equals("objectVersion")) {
				Integer version = NeptuneUtils.getInt(xpp.nextText());
				network.setObjectVersion(version);
			} else if (xpp.getName().equals("creationTime")) {
				Date creationTime = NeptuneUtils.getSQLDateTime(xpp.nextText());
				network.setCreationTime(creationTime);
			} else if (xpp.getName().equals("creatorId")) {
				network.setCreatorId(NeptuneUtils.getText(xpp.nextText()));
			} else if (xpp.getName().equals("versionDate")) {
				Date versionDate = NeptuneUtils.getSQLDate(xpp.nextText());
				network.setVersionDate(versionDate);
			} else if (xpp.getName().equals("description")) {
				network.setDescription(NeptuneUtils.getText(xpp.nextText()));
			} else if (xpp.getName().equals("name")) {
				network.setName(NeptuneUtils.getText(xpp.nextText()));
			} else if (xpp.getName().equals("registration")) {
				while (xpp.nextTag() == XmlPullParser.START_TAG) {
					if (xpp.getName().equals("registrationNumber")) {
						network.setRegistrationNumber(NeptuneUtils.getText(xpp
								.nextText()));
					} else {
						XPPUtil.skipSubTree(log, xpp);
					}
				}
			} else if (xpp.getName().equals("sourceName")) {
				network.setSourceName(NeptuneUtils.getText(xpp.nextText()));
			} else if (xpp.getName().equals("sourceIdentifier")) {
				network.setSourceIdentifier(NeptuneUtils.getText(xpp.nextText()));
			} else if (xpp.getName().equals("sourceType")) {
				PTNetworkSourceTypeEnum type = PTNetworkSourceTypeEnum
						.valueOf(NeptuneUtils.getText(xpp.nextText()));
				network.setSourceType(type);
			} else if (xpp.getName().equals("comment")) {
				network.setComment(NeptuneUtils.getText(xpp.nextText()));
			} else if (xpp.getName().equals("lineId")) {
				String objectId = NeptuneUtils.getText(xpp.nextText());
				Line line = ObjectFactory.getLine(referential, objectId);
				line.setPTNetwork(network);
			} else {
				XPPUtil.skipSubTree(log, xpp);
			}
		}
	}

	static {
		ParserFactory.register(PTNetworkParser.class.getName(),
				new ParserFactory() {
					private PTNetworkParser instance = new PTNetworkParser();

					@Override
					protected Parser create() {
						return instance;
					}
				});
	}
}
