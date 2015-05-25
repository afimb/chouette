package mobi.chouette.exchange.netex.parser;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Context;
import mobi.chouette.common.XPPUtil;
import mobi.chouette.exchange.importer.Parser;
import mobi.chouette.exchange.importer.ParserFactory;
import mobi.chouette.exchange.netex.Constant;
import mobi.chouette.model.GroupOfLine;
import mobi.chouette.model.util.ObjectFactory;
import mobi.chouette.model.util.Referential;

import org.xmlpull.v1.XmlPullParser;

@Log4j
public class GroupOfLineParser implements Parser, Constant {

	private static final String CHILD_TAG = "groupsOfLines";

	@Override
	public void parse(Context context) throws Exception {

		XmlPullParser xpp = (XmlPullParser) context.get(PARSER);

		xpp.require(XmlPullParser.START_TAG, null, CHILD_TAG);
		context.put(COLUMN_NUMBER, xpp.getColumnNumber());
		context.put(LINE_NUMBER, xpp.getLineNumber());

		while (xpp.nextTag() == XmlPullParser.START_TAG) {
			if (xpp.getName().equals("GroupOfLine")) {
				parseGroupOfLine(context);
			} else {
				XPPUtil.skipSubTree(log, xpp);
			}
		}
	}

	private void parseGroupOfLine(Context context) throws Exception {
		XmlPullParser xpp = (XmlPullParser) context.get(PARSER);
		Referential referential = (Referential) context.get(REFERENTIAL);

		xpp.require(XmlPullParser.START_TAG, null, "GroupOfLine");
		context.put(COLUMN_NUMBER, xpp.getColumnNumber());
		context.put(LINE_NUMBER, xpp.getLineNumber());

		String id = xpp.getAttributeValue(null, ID);
		GroupOfLine groupOfLine = ObjectFactory.getGroupOfLine(referential, id);

		Integer version = Integer.valueOf(xpp.getAttributeValue(null, VERSION));
		groupOfLine.setObjectVersion(version != null ? version : 0);

		while (xpp.nextTag() == XmlPullParser.START_TAG) {
			if (xpp.getName().equals(NAME)) {
				groupOfLine.setName(xpp.nextText());
			} else if (xpp.getName().equals("Description")) {
				groupOfLine.setComment(xpp.nextText());
			} else {
				XPPUtil.skipSubTree(log, xpp);
			}
		}
		groupOfLine.setFilled(true);
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
