package mobi.chouette.exchange.neptune.parser;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Context;
import mobi.chouette.common.XPPUtil;
import mobi.chouette.exchange.importer.Parser;
import mobi.chouette.exchange.importer.ParserFactory;
import mobi.chouette.exchange.importer.ParserUtils;
import mobi.chouette.exchange.neptune.Constant;
import mobi.chouette.exchange.neptune.validation.ITLValidator;
import mobi.chouette.exchange.validation.ValidatorFactory;
import mobi.chouette.model.Line;
import mobi.chouette.model.StopArea;
import mobi.chouette.model.util.ObjectFactory;
import mobi.chouette.model.util.Referential;

import org.xmlpull.v1.XmlPullParser;

@Log4j
public class ITLParser implements Parser, Constant {
	private static final String CHILD_TAG = "ITL";

	@Override
	public void parse(Context context) throws Exception {

		XmlPullParser xpp = (XmlPullParser) context.get(PARSER);
		Referential referential = (Referential) context.get(REFERENTIAL);

		xpp.require(XmlPullParser.START_TAG, null, CHILD_TAG);
		int columnNumber =  xpp.getColumnNumber();
		int lineNumber =  xpp.getLineNumber();
		
		ITLValidator validator = (ITLValidator) ValidatorFactory.create(ITLValidator.class.getName(), context);

		Line line = getLine(referential);
		StopArea stopArea = null;
		String objectId = null;
		while (xpp.nextTag() == XmlPullParser.START_TAG) {
			if (xpp.getName().equals("areaId")) {
				objectId = ParserUtils.getText(xpp.nextText());
				stopArea = ObjectFactory.getStopArea(referential, objectId);
				if (line != null) line.addRoutingConstraint(stopArea);
			} else if (xpp.getName().equals("lineIdShortCut")) {
				String lineIdShortCut = ParserUtils.getText(xpp.nextText());
				validator.addLineId(context, objectId, lineIdShortCut);
			} else if (xpp.getName().equals("name")) {
				String name = ParserUtils.getText(xpp.nextText());
				validator.addName(context, objectId, name);
				if (stopArea.getName() == null) stopArea.setName(name);
			} else {
				XPPUtil.skipSubTree(log, xpp);
			}
		}
		validator.addLocation(context, stopArea, lineNumber, columnNumber);
	}
	
	private Line getLine(Referential referential)
	{
		for (Line line : referential.getLines().values()) 
		{
			if (line.isFilled()) return line;
		}
		return null;
	}

	static {
		ParserFactory.register(ITLParser.class.getName(),
				new ParserFactory() {
					private ITLParser instance = new ITLParser();

					@Override
					protected Parser create() {
						return instance;
					}
				});
	}
}
