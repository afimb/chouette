package mobi.chouette.exchange.neptune.parser;

import java.util.Date;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Constant;
import mobi.chouette.common.Context;
import mobi.chouette.common.XPPUtil;
import mobi.chouette.exchange.importer.Parser;
import mobi.chouette.exchange.importer.ParserFactory;
import mobi.chouette.exchange.importer.ParserUtils;
import mobi.chouette.exchange.neptune.JsonExtension;
import mobi.chouette.exchange.neptune.validation.GroupOfLineValidator;
import mobi.chouette.exchange.validation.ValidatorFactory;
import mobi.chouette.model.GroupOfLine;
import mobi.chouette.model.Line;
import mobi.chouette.model.util.ObjectFactory;
import mobi.chouette.model.util.Referential;

import org.xmlpull.v1.XmlPullParser;

@Log4j
public class GroupOfLineParser implements Parser, Constant, JsonExtension {

	private static final String CHILD_TAG = "GroupOfLine";

	@Override
	public void parse(Context context) throws Exception {

		XmlPullParser xpp = (XmlPullParser) context.get(PARSER);
		Referential referential = (Referential) context.get(REFERENTIAL);

		xpp.require(XmlPullParser.START_TAG, null, CHILD_TAG);
		int columnNumber =  xpp.getColumnNumber();
		int lineNumber =  xpp.getLineNumber();
		
		GroupOfLineValidator validator = (GroupOfLineValidator) ValidatorFactory.create(GroupOfLineValidator.class.getName(), context);
				
		GroupOfLine groupOfLine = null;
		String objectId = null;
		while (xpp.nextTag() == XmlPullParser.START_TAG) {
			if (xpp.getName().equals("objectId")) {
				objectId = ParserUtils.getText(xpp.nextText());
				groupOfLine = ObjectFactory.getGroupOfLine(referential,
						objectId);
				groupOfLine.setFilled(true);
			} else if (xpp.getName().equals("objectVersion")) {
				Integer version = ParserUtils.getInt(xpp.nextText());
				groupOfLine.setObjectVersion(version);
			} else if (xpp.getName().equals("creationTime")) {
				Date creationTime = ParserUtils.getSQLDateTime(xpp.nextText());
				groupOfLine.setCreationTime(creationTime);
			} else if (xpp.getName().equals("creatorId")) {
				groupOfLine.setCreatorId(ParserUtils.getText(xpp.nextText()));
			} else if (xpp.getName().equals("name")) {
				groupOfLine.setName(ParserUtils.getText(xpp.nextText()));
			} else if (xpp.getName().equals("comment")) {
				groupOfLine.setComment(ParserUtils.getText(xpp.nextText()));
			} else if (xpp.getName().equals("lineId")) {
				String lineId = ParserUtils.getText(xpp.nextText());
				Line line = ObjectFactory.getLine(referential, lineId);
				groupOfLine.addLine(line);
				validator.addLineId(context, objectId, lineId);
			} else {
				XPPUtil.skipSubTree(log, xpp);
			}
		}
		validator.addLocation(context, groupOfLine, lineNumber, columnNumber);
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
