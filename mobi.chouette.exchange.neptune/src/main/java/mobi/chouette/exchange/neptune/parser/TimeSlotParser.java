package mobi.chouette.exchange.neptune.parser;

import java.sql.Date;

import org.xmlpull.v1.XmlPullParser;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Context;
import mobi.chouette.common.XPPUtil;
import mobi.chouette.exchange.importer.Parser;
import mobi.chouette.exchange.importer.ParserFactory;
import mobi.chouette.exchange.importer.ParserUtils;
import mobi.chouette.exchange.neptune.Constant;
import mobi.chouette.exchange.neptune.model.NeptuneObjectFactory;
import mobi.chouette.exchange.neptune.model.TimeSlot;
import mobi.chouette.exchange.neptune.validation.TimeSlotValidator;
import mobi.chouette.exchange.validation.ValidatorFactory;

@Log4j
public class TimeSlotParser implements Parser, Constant {

	private static final String CHILD_TAG = "TimeSlot";

	@Override
	public void parse(Context context) throws Exception {
		XmlPullParser xpp = (XmlPullParser) context.get(PARSER);
		NeptuneObjectFactory factory =  (NeptuneObjectFactory) context.get(NEPTUNE_OBJECT_FACTORY);

		xpp.require(XmlPullParser.START_TAG, null, CHILD_TAG);
		int columnNumber =  xpp.getColumnNumber();
		int lineNumber =  xpp.getLineNumber();
		
		TimeSlotValidator validator = (TimeSlotValidator) ValidatorFactory.create(TimeSlotValidator.class.getName(), context);
				

		TimeSlot timeSlot = null;
		String objectId = null;

		while (xpp.nextTag() == XmlPullParser.START_TAG) {
			if (xpp.getName().equals("objectId")) {
				objectId = ParserUtils.getText(xpp.nextText());
				timeSlot = factory.getTimeSlot(objectId);
				timeSlot.setFilled(true);
				validator.addLocation(context, objectId, lineNumber, columnNumber);
			} else if (xpp.getName().equals("objectVersion")) {
				Integer version = ParserUtils.getInt(xpp.nextText());
				timeSlot.setObjectVersion(version);
			} else if (xpp.getName().equals("creationTime")) {
				Date creationTime = ParserUtils.getSQLDateTime(xpp.nextText());
				timeSlot.setCreationTime(creationTime);
			} else if (xpp.getName().equals("creatorId")) {
				timeSlot.setCreatorId(ParserUtils.getText(xpp.nextText()));
			} else if (xpp.getName().equals("beginningSlotTime")) {
				timeSlot.setBeginningSlotTime(ParserUtils.getSQLTime(xpp.nextText()));
			} else if (xpp.getName().equals("endSlotTime")) {
				timeSlot.setEndSlotTime(ParserUtils.getSQLTime(xpp.nextText()));
			} else if (xpp.getName().equals("firstDepartureTimeInSlot")) {
				timeSlot.setFirstDepartureTimeInSlot(ParserUtils.getSQLTime(xpp.nextText()));
			} else if (xpp.getName().equals("lastDepartureTimeInSlot")) {
				timeSlot.setLastDepartureTimeInSlot(ParserUtils.getSQLTime(xpp.nextText()));
			} else {
				XPPUtil.skipSubTree(log, xpp);
			}
		}
	}

	static {
		ParserFactory.register(TimeSlotParser.class.getName(),
				new ParserFactory() {
					private TimeSlotParser instance = new TimeSlotParser();

					@Override
					protected Parser create() {
						return instance;
					}
				});
	}
}
