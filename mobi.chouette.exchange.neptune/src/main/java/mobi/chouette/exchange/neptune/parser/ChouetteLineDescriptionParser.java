package mobi.chouette.exchange.neptune.parser;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Context;
import mobi.chouette.exchange.neptune.Constant;

import org.xmlpull.v1.XmlPullParser;

@Log4j
public class ChouetteLineDescriptionParser implements Parser, Constant {
	private static final String CHILD_TAG = "ChouetteLineDescription";

	@Override
	public void parse(Context context) throws Exception {

		XmlPullParser xpp = (XmlPullParser) context.get(XPP);
		Referential referential = (Referential) context.get(REFERENTIAL);

		xpp.require(XmlPullParser.START_TAG, null, CHILD_TAG);

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

			} else if (xpp.getName().equals("JourneyPattern")) {

			} else if (xpp.getName().equals("VehicleJourney")) {

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
