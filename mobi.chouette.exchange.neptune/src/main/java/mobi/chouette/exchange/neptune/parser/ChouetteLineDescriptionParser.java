package mobi.chouette.exchange.neptune.parser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Context;
import mobi.chouette.common.XPPUtil;
import mobi.chouette.exchange.importer.Parser;
import mobi.chouette.exchange.importer.ParserFactory;
import mobi.chouette.exchange.neptune.Constant;
import mobi.chouette.exchange.neptune.model.NeptuneObjectFactory;
import mobi.chouette.exchange.neptune.model.PTLink;
import mobi.chouette.model.Route;
import mobi.chouette.model.util.Referential;

import org.xmlpull.v1.XmlPullParser;

@Log4j
public class ChouetteLineDescriptionParser implements Parser, Constant {
	private static final String CHILD_TAG = "ChouetteLineDescription";

	@Override
	public void parse(Context context) throws Exception {

		XmlPullParser xpp = (XmlPullParser) context.get(PARSER);
		Referential referential = (Referential) context.get(REFERENTIAL);
		NeptuneObjectFactory factory = (NeptuneObjectFactory) context
				.get(NEPTUNE_OBJECT_FACTORY);

		xpp.require(XmlPullParser.START_TAG, null, CHILD_TAG);
		context.put(COLUMN_NUMBER, xpp.getColumnNumber());
		context.put(LINE_NUMBER, xpp.getLineNumber());

		boolean initialized = false;

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
			} else if (xpp.getName().equals("ITL")) {
				Parser parser = ParserFactory.create(ITLParser.class
						.getName());
				parser.parse(context);
			} else if (xpp.getName().equals("PtLink")) {
				Parser parser = ParserFactory.create(PtLinkParser.class
						.getName());
				parser.parse(context);
			} else if (xpp.getName().equals("JourneyPattern")) {

				if (!initialized) {
					for (Route route : referential.getRoutes().values()) {
						List<PTLink> list = factory.getPTLinksOnRoute(route);
						List<PTLink> sorted = sortPtLinks(list);

						int position = 0;
						for (int i = 0; i < sorted.size(); i++) {
							PTLink ptLink = sorted.get(i);
							if (i == 0) {
								ptLink.getStartOfLink().setPosition(position++);
								ptLink.getStartOfLink().setRoute(route);
							}
							ptLink.getEndOfLink().setPosition(position++);
							ptLink.getEndOfLink().setRoute(route);
						}
					}
					initialized = true;
				}

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

	private List<PTLink> sortPtLinks(List<PTLink> ptLinks) {
		if (ptLinks == null || ptLinks.isEmpty())
			return ptLinks;
		Map<String, PTLink> linkByStart = new HashMap<String, PTLink>();
		Map<String, PTLink> linkByEnd = new HashMap<String, PTLink>();

		for (PTLink ptLink : ptLinks) {
			if (ptLink.getStartOfLink() != null)
				linkByStart.put(ptLink.getStartOfLink().getObjectId(), ptLink);
			if (ptLink.getEndOfLink() != null)
				linkByEnd.put(ptLink.getEndOfLink().getObjectId(), ptLink);
		}

		// find first stop id
		Set<String> starts = new HashSet<String>();
		starts.addAll(linkByStart.keySet());
		starts.removeAll(linkByEnd.keySet());
		// starts must contains only first stop

		List<PTLink> sortedLinks = new ArrayList<PTLink>();

		if (!starts.isEmpty()) {
			String start = starts.toArray(new String[0])[0];
			PTLink link = linkByStart.get(start);
			while (link != null) {
				sortedLinks.add(link);
				start = link.getEndOfLink().getObjectId();
				link = linkByStart.remove(start);
			}
		}

		return sortedLinks;
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
