package mobi.chouette.exchange.netex.parser;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Constant;
import mobi.chouette.common.Context;
import mobi.chouette.common.XPPUtil;
import mobi.chouette.exchange.importer.Parser;
import mobi.chouette.exchange.importer.ParserFactory;

import org.xmlpull.v1.XmlPullParser;

@Log4j
public class NetexParser implements Parser, Constant {

	private static final String CHILD_TAG = "frames";

	@Override
	public void parse(Context context) throws Exception {

		XmlPullParser xpp = (XmlPullParser) context.get(PARSER);

		XPPUtil.nextStartTag(xpp, CHILD_TAG);

		context.put(COLUMN_NUMBER, xpp.getColumnNumber());
		context.put(LINE_NUMBER, xpp.getLineNumber());

		while (xpp.nextTag() == XmlPullParser.START_TAG) {
			if (xpp.getName().equals("ResourceFrame")) {
				parseResourceFrame(context);
			} else if (xpp.getName().equals("ServiceFrame")) {
				parseServiceFrame(context);
			} else if (xpp.getName().equals("SiteFrame")) {
				parseSiteFrame(context);
			} else if (xpp.getName().equals("ServiceCalendarFrame")) {
				Parser timetableParser = ParserFactory
						.create(TimetableParser.class.getName());
				timetableParser.parse(context);
			} else if (xpp.getName().equals("TimetableFrame")) {
				parseTimetableFrame(context);
			} else {
				XPPUtil.skipSubTree(log,xpp);
			}
		}
	}

	private void parseResourceFrame(Context context) throws Exception {
		XmlPullParser xpp = (XmlPullParser) context.get(PARSER);

		while (xpp.nextTag() == XmlPullParser.START_TAG) {
			if (xpp.getName().equals("organisations")) {
				Parser companyParser = ParserFactory.create(CompanyParser.class
						.getName());
				companyParser.parse(context);
			} else {
				XPPUtil.skipSubTree(log,xpp);
			}
		}
	}

	private void parseServiceFrame(Context context) throws Exception {
		XmlPullParser xpp = (XmlPullParser) context.get(PARSER);

		while (xpp.nextTag() == XmlPullParser.START_TAG) {
			if (xpp.getName().equals("Network")) {
				Parser ptNetworkParser = ParserFactory
						.create(PTNetworkParser.class.getName());
				ptNetworkParser.parse(context);
			} else if (xpp.getName().equals("directions")) {
				Parser routeParser = ParserFactory.create(RouteParser.class
						.getName());
				routeParser.parse(context);
			} else if (xpp.getName().equals("routes")) {
				Parser routeParser = ParserFactory.create(RouteParser.class
						.getName());
				routeParser.parse(context);
			} else if (xpp.getName().equals("lines")) {
				Parser lineParser = ParserFactory.create(LineParser.class
						.getName());
				lineParser.parse(context);
			} else if (xpp.getName().equals("servicePatterns")) {
				Parser journeyPatternParser = ParserFactory
						.create(JourneyPatternParser.class.getName());
				journeyPatternParser.parse(context);
			} else if (xpp.getName().equals("connections")) {
				Parser connectionLinkarser = ParserFactory
						.create(ConnectionLinkParser.class.getName());
				connectionLinkarser.parse(context);
			} else if (xpp.getName().equals("tariffZones")) {
				Parser stopAreaParser = ParserFactory
						.create(StopAreaParser.class.getName());
				stopAreaParser.parse(context);
			} else if (xpp.getName().equals("stopAssignments")) {
				Parser routeParser = ParserFactory.create(RouteParser.class
						.getName());
				routeParser.parse(context);
			} else {
				XPPUtil.skipSubTree(log,xpp);
			}
		}
	}

	private void parseSiteFrame(Context context) throws Exception {
		XmlPullParser xpp = (XmlPullParser) context.get(PARSER);

		while (xpp.nextTag() == XmlPullParser.START_TAG) {
			if (xpp.getName().equals("stopPlaces")) {
				Parser stopAreaParser = ParserFactory
						.create(StopAreaParser.class.getName());
				stopAreaParser.parse(context);
			} else {
				XPPUtil.skipSubTree(log,xpp);
			}
		}
	}

	private void parseTimetableFrame(Context context) throws Exception {
		XmlPullParser xpp = (XmlPullParser) context.get(PARSER);

		while (xpp.nextTag() == XmlPullParser.START_TAG) {
			if (xpp.getName().equals("vehicleJourneys")) {
				Parser vehicleJourneyParser = ParserFactory
						.create(VehicleJourneyParser.class.getName());
				vehicleJourneyParser.parse(context);
			} else {
				XPPUtil.skipSubTree(log, xpp);
			}
		}
	}

	static {
		ParserFactory.register(NetexParser.class.getName(), new ParserFactory() {
			private NetexParser instance = new NetexParser();

			@Override
			protected Parser create() {
				return instance;
			}
		});
	}

}
