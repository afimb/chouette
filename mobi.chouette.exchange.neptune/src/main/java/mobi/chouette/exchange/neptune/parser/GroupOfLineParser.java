package mobi.chouette.exchange.neptune.parser;

import java.util.Date;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Context;
import mobi.chouette.exchange.neptune.Constant;
import mobi.chouette.model.GroupOfLine;
import mobi.chouette.model.Line;

import org.xmlpull.v1.XmlPullParser;

@Log4j
public class GroupOfLineParser implements Parser, Constant {

	private static final String CHILD_TAG = "GroupOfLine";

	@Override
	public void parse(Context context) throws Exception {

		XmlPullParser xpp = (XmlPullParser) context.get(XPP);
		Referential referential = (Referential) context.get(REFERENTIAL);

		xpp.require(XmlPullParser.START_TAG, null, CHILD_TAG);

		GroupOfLine groupOfLine = null;
		while (xpp.nextTag() == XmlPullParser.START_TAG) {

			if (xpp.getName().equals("objectId")) {
				String objectId = NeptuneUtils.getText(xpp.nextText());
				groupOfLine = ObjectFactory.getGroupOfLine(referential,
						objectId);
			} else if (xpp.getName().equals("objectVersion")) {
				Integer version = NeptuneUtils.getInt(xpp.nextText());
				groupOfLine.setObjectVersion(version);
			} else if (xpp.getName().equals("creationTime")) {
				Date creationTime = NeptuneUtils.getSQLDateTime(xpp.nextText());
				groupOfLine.setCreationTime(creationTime);
			} else if (xpp.getName().equals("creatorId")) {
				groupOfLine.setCreatorId(NeptuneUtils.getText(xpp.nextText()));

			} else if (xpp.getName().equals("name")) {
				groupOfLine.setName(NeptuneUtils.getText(xpp.nextText()));
			} else if (xpp.getName().equals("comment")) {
				groupOfLine.setComment(NeptuneUtils.getText(xpp.nextText()));
			} else if (xpp.getName().equals("lineId")) {
				String objectId = NeptuneUtils.getText(xpp.nextText());
				Line line = ObjectFactory.getLine(referential, objectId);
				groupOfLine.addLine(line);
			} else {
				XPPUtil.skipSubTree(log, xpp);
			}
		}
	}

	static {
		ParserFactory.register(GroupOfLineParser.class.getName(),
				new ParserFactory() {
					private GroupOfLineParser instance = new GroupOfLineParser();

					@Override
					protected Parser create() {
						return instance;
					}
				});
	}
}
