package mobi.chouette.exchange.neptune.parser;

import java.util.Date;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Context;
import mobi.chouette.exchange.neptune.importer.Constant;
import mobi.chouette.importer.Parser;
import mobi.chouette.importer.ParserFactory;
import mobi.chouette.importer.ParserUtils;
import mobi.chouette.importer.XPPUtil;
import mobi.chouette.model.StopArea;
import mobi.chouette.model.StopPoint;
import mobi.chouette.model.util.ObjectFactory;
import mobi.chouette.model.util.Referential;

import org.xmlpull.v1.XmlPullParser;

@Log4j
public class StopPointParser implements Parser, Constant {
	private static final String CHILD_TAG = "StopPoint";

	@Override
	public void parse(Context context) throws Exception {

		XmlPullParser xpp = (XmlPullParser) context.get(XPP);
		Referential referential = (Referential) context.get(REFERENTIAL);

		xpp.require(XmlPullParser.START_TAG, null, CHILD_TAG);
		context.put(COLUMN_NUMBER, xpp.getColumnNumber());
		context.put(LINE_NUMBER, xpp.getLineNumber());

		StopPoint stopPoint = null;
		while (xpp.nextTag() == XmlPullParser.START_TAG) {

			if (xpp.getName().equals("objectId")) {
				String objectId = ParserUtils.getText(xpp.nextText());
				stopPoint = ObjectFactory.getStopPoint(referential, objectId);
			} else if (xpp.getName().equals("objectVersion")) {
				Integer version = ParserUtils.getInt(xpp.nextText());
				stopPoint.setObjectVersion(version);
			} else if (xpp.getName().equals("creationTime")) {
				Date creationTime = ParserUtils.getSQLDateTime(xpp.nextText());
				stopPoint.setCreationTime(creationTime);
			} else if (xpp.getName().equals("creatorId")) {
				stopPoint.setCreatorId(ParserUtils.getText(xpp.nextText()));
			} else if (xpp.getName().equals("name")) {
				stopPoint.setName(ParserUtils.getText(xpp.nextText()));
			} else if (xpp.getName().equals("containedIn")) {
				String objectId = ParserUtils.getText(xpp.nextText());
				StopArea stopArea = ObjectFactory.getStopArea(referential,
						objectId);
				stopPoint.setContainedInStopArea(stopArea);
			} else if (xpp.getName().equals("lineIdShortcut")) {
				String objectId = ParserUtils.getText(xpp.nextText());
				// TODO lineIdShortcut
			} else if (xpp.getName().equals("ptNetworkIdShortcut")) {
				String objectId = ParserUtils.getText(xpp.nextText());
				// TODO ptNetworkIdShortcut
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
