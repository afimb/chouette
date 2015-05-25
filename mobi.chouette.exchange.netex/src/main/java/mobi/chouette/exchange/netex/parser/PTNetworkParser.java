package mobi.chouette.exchange.netex.parser;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Context;
import mobi.chouette.common.XPPUtil;
import mobi.chouette.exchange.importer.Parser;
import mobi.chouette.exchange.importer.ParserFactory;
import mobi.chouette.exchange.netex.Constant;
import mobi.chouette.model.Network;
import mobi.chouette.model.util.ObjectFactory;
import mobi.chouette.model.util.Referential;

import org.xmlpull.v1.XmlPullParser;

@Log4j
public class PTNetworkParser implements Parser, Constant {

	private static final String CHILD_TAG = "Network";

	@Override
	public void parse(Context context) throws Exception {

		XmlPullParser xpp = (XmlPullParser) context.get(PARSER);
		Referential referential = (Referential) context.get(REFERENTIAL);

		xpp.require(XmlPullParser.START_TAG, null, CHILD_TAG);
		context.put(COLUMN_NUMBER, xpp.getColumnNumber());
		context.put(LINE_NUMBER, xpp.getLineNumber());

		String id = xpp.getAttributeValue(null, ID);

		Network network = ObjectFactory.getPTNetwork(referential, id);

		Integer version = Integer.valueOf(xpp.getAttributeValue(null, VERSION));
		network.setObjectVersion(version != null ? version : 0);
		network.setVersionDate(NetexUtils.getDate(xpp.getAttributeValue(null,
				"changed")));

		while (xpp.nextTag() == XmlPullParser.START_TAG) {
			if (xpp.getName().equals("Name")) {
				network.setName(xpp.nextText());
			} else if (xpp.getName().equals("PrivateCode")) {
				network.setRegistrationNumber(xpp.nextText());
			} else if (xpp.getName().equals("Description")) {
				network.setComment(xpp.nextText());
			} else {
				XPPUtil.skipSubTree(log, xpp);
			}
		}
		network.setFilled(true);
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
