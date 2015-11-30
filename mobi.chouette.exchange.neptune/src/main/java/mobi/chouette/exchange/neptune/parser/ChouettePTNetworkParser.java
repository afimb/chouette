package mobi.chouette.exchange.neptune.parser;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Constant;
import mobi.chouette.common.Context;
import mobi.chouette.common.XPPUtil;
import mobi.chouette.exchange.importer.Parser;
import mobi.chouette.exchange.importer.ParserFactory;

import org.xmlpull.v1.XmlPullParser;

@Log4j
public class ChouettePTNetworkParser implements Parser, Constant {
	private static final String CHILD_TAG = "ChouettePTNetwork";

	@Override
	public void parse(Context context) throws Exception {

		XmlPullParser xpp = (XmlPullParser) context.get(PARSER);

		XPPUtil.nextStartTag(xpp, CHILD_TAG);

		xpp.require(XmlPullParser.START_TAG, null, CHILD_TAG);
		context.put(COLUMN_NUMBER, xpp.getColumnNumber());
		context.put(LINE_NUMBER, xpp.getLineNumber());

		while (xpp.nextTag() == XmlPullParser.START_TAG) {
			if (xpp.getName().equals("PTNetwork")) {
				Parser parser = ParserFactory.create(PTNetworkParser.class
						.getName());
				parser.parse(context);
			} else if (xpp.getName().equals("GroupOfLine")) {
				Parser parser = ParserFactory.create(GroupOfLineParser.class
						.getName());
				parser.parse(context);
			} else if (xpp.getName().equals("Company")) {
				Parser parser = ParserFactory.create(CompanyParser.class
						.getName());
				parser.parse(context);
			} else if (xpp.getName().equals("ChouetteArea")) {
				Parser parser = ParserFactory.create(ChouetteAreaParser.class
						.getName());
				parser.parse(context);
			} else if (xpp.getName().equals("ConnectionLink")) {
				Parser parser = ParserFactory.create(ConnectionLinkParser.class
						.getName());
				parser.parse(context);
			} else if (xpp.getName().equals("Timetable")) {
				Parser parser = ParserFactory.create(TimetableParser.class
						.getName());
				parser.parse(context);
			} else if (xpp.getName().equals("TimeSlot")) {
				Parser parser = ParserFactory.create(TimeSlotParser.class
						.getName());
				parser.parse(context);
			} else if (xpp.getName().equals("ChouetteLineDescription")) {
				Parser parser = ParserFactory
						.create(ChouetteLineDescriptionParser.class.getName());
				parser.parse(context);
			} else if (xpp.getName().equals("Facility")) {
				// TODO [DSU] Facility
				XPPUtil.skipSubTree(log, xpp);
				
			} else if (xpp.getName().equals("AccessPoint")) {
				Parser parser = ParserFactory.create(AccessPointParser.class
						.getName());
				parser.parse(context);
			} else if (xpp.getName().equals("AccessLink")) {
				Parser parser = ParserFactory.create(AccessLinkParser.class
						.getName());
				parser.parse(context);
			} else {
				XPPUtil.skipSubTree(log, xpp);
			}
		}
	}

	static {
		ParserFactory.register(ChouettePTNetworkParser.class.getName(),
				new ParserFactory() {
					private ChouettePTNetworkParser instance = new ChouettePTNetworkParser();

					@Override
					protected Parser create() {
						return instance;
					}
				});
	}
}
