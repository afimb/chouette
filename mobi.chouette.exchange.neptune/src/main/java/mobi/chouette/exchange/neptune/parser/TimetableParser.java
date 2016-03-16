package mobi.chouette.exchange.neptune.parser;

import java.sql.Date;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Constant;
import mobi.chouette.common.Context;
import mobi.chouette.common.XPPUtil;
import mobi.chouette.exchange.importer.Parser;
import mobi.chouette.exchange.importer.ParserFactory;
import mobi.chouette.exchange.importer.ParserUtils;
import mobi.chouette.exchange.neptune.validation.TimetableValidator;
import mobi.chouette.exchange.validation.ValidatorFactory;
import mobi.chouette.model.CalendarDay;
import mobi.chouette.model.Period;
import mobi.chouette.model.Timetable;
import mobi.chouette.model.VehicleJourney;
import mobi.chouette.model.type.DayTypeEnum;
import mobi.chouette.model.util.ObjectFactory;
import mobi.chouette.model.util.Referential;

import org.xmlpull.v1.XmlPullParser;

@Log4j
public class TimetableParser implements Parser, Constant {
	private static final String CHILD_TAG = "Timetable";

	@Override
	public void parse(Context context) throws Exception {

		XmlPullParser xpp = (XmlPullParser) context.get(PARSER);
		Referential referential = (Referential) context.get(REFERENTIAL);

		xpp.require(XmlPullParser.START_TAG, null, CHILD_TAG);
		int columnNumber =  xpp.getColumnNumber();
		int lineNumber =  xpp.getLineNumber();
		
		TimetableValidator validator = (TimetableValidator) ValidatorFactory.create(TimetableValidator.class.getName(), context);
				

		Timetable timetable = null;
		String objectId = null;

		while (xpp.nextTag() == XmlPullParser.START_TAG) {

			if (xpp.getName().equals("objectId")) {
				 objectId = ParserUtils.getText(xpp.nextText());
				timetable = ObjectFactory.getTimetable(referential, objectId);
				timetable.setFilled(true);
			} else if (xpp.getName().equals("objectVersion")) {
				Integer version = ParserUtils.getInt(xpp.nextText());
				timetable.setObjectVersion(version);
			} else if (xpp.getName().equals("creationTime")) {
				Date creationTime = ParserUtils.getSQLDateTime(xpp.nextText());
				timetable.setCreationTime(creationTime);
			} else if (xpp.getName().equals("creatorId")) {
				timetable.setCreatorId(ParserUtils.getText(xpp.nextText()));
			} else if (xpp.getName().equals("comment")) {
				timetable.setComment(ParserUtils.getText(xpp.nextText()));
			} else if (xpp.getName().equals("version")) {
				timetable.setVersion(ParserUtils.getText(xpp.nextText()));
			} else if (xpp.getName().equals("dayType")) {
				DayTypeEnum value = ParserUtils.getEnum(DayTypeEnum.class,
						xpp.nextText());
				timetable.addDayType(value);
			} else if (xpp.getName().equals("calendarDay")) {
				Date date = ParserUtils.getSQLDate(xpp.nextText());
				CalendarDay value = new CalendarDay(date, true);
				timetable.addCalendarDay(value);
			} else if (xpp.getName().equals("period")) {
				Date startOfPeriod = null;
				Date endOfPeriod = null;
				while (xpp.nextTag() == XmlPullParser.START_TAG) {
					if (xpp.getName().equals("startOfPeriod")) {
						startOfPeriod = ParserUtils.getSQLDate(xpp.nextText());
					} else if (xpp.getName().equals("endOfPeriod")) {
						endOfPeriod = ParserUtils.getSQLDate(xpp.nextText());
					} else {
						XPPUtil.skipSubTree(log, xpp);
					}
				}
				Period period = new Period(startOfPeriod, endOfPeriod);
				timetable.addPeriod(period);
			} else if (xpp.getName().equals("vehicleJourneyId")) {
				String vehicleJourneyId = ParserUtils.getText(xpp.nextText());
				VehicleJourney vehicleJourney = ObjectFactory
						.getVehicleJourney(referential, vehicleJourneyId);
				timetable.addVehicleJourney(vehicleJourney);
				validator.addVehicleJourneyId(context, objectId, vehicleJourneyId);
			} else {
				XPPUtil.skipSubTree(log, xpp);
			}
		}
		validator.addLocation(context, timetable, lineNumber, columnNumber);
	}

	static {
		ParserFactory.register(TimetableParser.class.getName(),
				new ParserFactory() {
					private TimetableParser instance = new TimetableParser();

					@Override
					protected Parser create() {
						return instance;
					}
				});
	}
}
