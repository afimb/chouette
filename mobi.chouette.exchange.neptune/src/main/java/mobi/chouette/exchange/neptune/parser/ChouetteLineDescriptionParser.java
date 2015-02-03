package mobi.chouette.exchange.neptune.parser;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Constant;
import mobi.chouette.common.Context;
import mobi.chouette.exchange.importer.Parser;
import mobi.chouette.exchange.importer.ParserFactory;
import mobi.chouette.exchange.importer.XPPUtil;
import mobi.chouette.model.util.Referential;

import org.xmlpull.v1.XmlPullParser;

@Log4j
public class ChouetteLineDescriptionParser implements Parser, Constant {
	private static final String CHILD_TAG = "ChouetteLineDescription";

	@Override
	public void parse(Context context) throws Exception {

		XmlPullParser xpp = (XmlPullParser) context.get(PARSER);
		Referential referential = (Referential) context.get(REFERENTIAL);

		xpp.require(XmlPullParser.START_TAG, null, CHILD_TAG);
		context.put(COLUMN_NUMBER, xpp.getColumnNumber());
		context.put(LINE_NUMBER, xpp.getLineNumber());

		while (xpp.nextTag() == XmlPullParser.START_TAG) {

			if (xpp.getName().equals("Line")) {
				Parser parser = ParserFactory
						.create(LineParser.class.getName());
				parser.parse(context);
			} else if (xpp.getName().equals("ChouetteRoute")) {
				Parser parser = ParserFactory.create(ChouetteRouteParser.class
						.getName());
				parser.parse(context);
			} else if (xpp.getName().equals("StopPoint")) {
				Parser parser = ParserFactory.create(StopPointParser.class
						.getName());
				parser.parse(context);
			} else if (xpp.getName().equals("PtLink")) {
				Parser parser = ParserFactory.create(PtLinkParser.class
						.getName());
				parser.parse(context);
			} else if (xpp.getName().equals("JourneyPattern")) {
				Parser parser = ParserFactory.create(JourneyPatternParser.class
						.getName());
				parser.parse(context);
			} else if (xpp.getName().equals("VehicleJourney")) {
				Parser parser = ParserFactory.create(VehicleJourneyParser.class
						.getName());
				parser.parse(context);
			} else {
				XPPUtil.skipSubTree(log, xpp);
			}
		}
	}

	static {
		ParserFactory.register(ChouetteLineDescriptionParser.class.getName(),
				new ParserFactory() {
					private ChouetteLineDescriptionParser instance = new ChouetteLineDescriptionParser();

					@Override
					protected Parser create() {
						return instance;
					}
				});
	}
}
