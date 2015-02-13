package mobi.chouette.exchange.neptune.parser;

import java.math.BigDecimal;
import java.util.Date;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Context;
import mobi.chouette.exchange.importer.Parser;
import mobi.chouette.exchange.importer.ParserFactory;
import mobi.chouette.exchange.importer.ParserUtils;
import mobi.chouette.exchange.importer.XPPUtil;
import mobi.chouette.exchange.neptune.Constant;
import mobi.chouette.exchange.neptune.model.NeptuneObjectFactory;
import mobi.chouette.exchange.neptune.model.PTLink;
import mobi.chouette.model.StopPoint;
import mobi.chouette.model.util.ObjectFactory;
import mobi.chouette.model.util.Referential;

import org.xmlpull.v1.XmlPullParser;

@Log4j
public class PtLinkParser implements Parser, Constant {
	private static final String CHILD_TAG = "PtLink";

	@Override
	public void parse(Context context) throws Exception {

		XmlPullParser xpp = (XmlPullParser) context.get(PARSER);
		Referential referential = (Referential) context.get(REFERENTIAL);
		NeptuneObjectFactory factory =  (NeptuneObjectFactory) context.get(NEPTUNE_OBJECT_FACTORY);

		xpp.require(XmlPullParser.START_TAG, null, CHILD_TAG);
		context.put(COLUMN_NUMBER, xpp.getColumnNumber());
		context.put(LINE_NUMBER, xpp.getLineNumber());

		PTLink ptLink = null;
		while (xpp.nextTag() == XmlPullParser.START_TAG) {

			if (xpp.getName().equals("objectId")) {
				String objectId = ParserUtils.getText(xpp.nextText());
				ptLink = factory.getPTLink(objectId);
			} else if (xpp.getName().equals("objectVersion")) {
				Integer version = ParserUtils.getInt(xpp.nextText());
				ptLink.setObjectVersion(version);
			} else if (xpp.getName().equals("creationTime")) {
				Date creationTime = ParserUtils.getSQLDateTime(xpp.nextText());
				ptLink.setCreationTime(creationTime);
			} else if (xpp.getName().equals("creatorId")) {
				ptLink.setCreatorId(ParserUtils.getText(xpp.nextText()));
			} else if (xpp.getName().equals("startOfLink")) {
				String objectId = ParserUtils.getText(xpp.nextText());
				StopPoint startOfLink = ObjectFactory.getStopPoint(referential,
						objectId);
				ptLink.setStartOfLink(startOfLink);
			} else if (xpp.getName().equals("endOfLink")) {
				String objectId = ParserUtils.getText(xpp.nextText());
				StopPoint endOfLink = ObjectFactory.getStopPoint(referential,
						objectId);
				ptLink.setEndOfLink(endOfLink);
			} else if (xpp.getName().equals("linkDistance")) {
				BigDecimal value = ParserUtils.getBigDecimal(xpp.nextText());
				ptLink.setLinkDistance(value);
			} else {
				XPPUtil.skipSubTree(log, xpp);
			}
		}
	}

	static {
		ParserFactory.register(PtLinkParser.class.getName(),
				new ParserFactory() {
					private PtLinkParser instance = new PtLinkParser();

					@Override
					protected Parser create() {
						return instance;
					}
				});
	}
}
