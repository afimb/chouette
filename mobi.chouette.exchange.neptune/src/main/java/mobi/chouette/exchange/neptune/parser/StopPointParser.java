package mobi.chouette.exchange.neptune.parser;

import java.util.Date;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Context;
import mobi.chouette.exchange.neptune.Constant;
import mobi.chouette.model.StopPoint;

import org.xmlpull.v1.XmlPullParser;

@Log4j
public class StopPointParser implements Parser, Constant {
	private static final String CHILD_TAG = "StopPoint";

	@Override
	public void parse(Context context) throws Exception {

		XmlPullParser xpp = (XmlPullParser) context.get(XPP);
		Referential referential = (Referential) context.get(REFERENTIAL);

		xpp.require(XmlPullParser.START_TAG, null, CHILD_TAG);

		StopPoint stopPoint = null;
		while (xpp.nextTag() == XmlPullParser.START_TAG) {

			if (xpp.getName().equals("objectId")) {
				String objectId = NeptuneUtils.getText(xpp.nextText());
				stopPoint = ObjectFactory.getStopPoint(referential, objectId);
			} else if (xpp.getName().equals("objectVersion")) {
				Integer version = NeptuneUtils.getInt(xpp.nextText());
				stopPoint.setObjectVersion(version);
			} else if (xpp.getName().equals("creationTime")) {
				Date creationTime = NeptuneUtils.getSQLDateTime(xpp.nextText());
				stopPoint.setCreationTime(creationTime);
			} else if (xpp.getName().equals("creatorId")) {
				stopPoint.setCreatorId(NeptuneUtils.getText(xpp.nextText()));
			} else if (xpp.getName().equals("name")) {
				stopPoint.setName(NeptuneUtils.getText(xpp.nextText()));
			} else {
				XPPUtil.skipSubTree(log, xpp);
			}
		}
	}

	static {
		ParserFactory.register(StopPointParser.class.getName(),
				new ParserFactory() {
					private StopPointParser instance = new StopPointParser();

					@Override
					protected Parser create() {
						return instance;
					}
				});
	}
}
