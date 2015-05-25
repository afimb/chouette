package mobi.chouette.exchange.netex.parser;

import java.sql.Date;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Context;
import mobi.chouette.common.XPPUtil;
import mobi.chouette.exchange.importer.Parser;
import mobi.chouette.exchange.importer.ParserFactory;
import mobi.chouette.exchange.importer.ParserUtils;
import mobi.chouette.exchange.netex.Constant;
import mobi.chouette.model.CalendarDay;
import mobi.chouette.model.Period;
import mobi.chouette.model.Timetable;
import mobi.chouette.model.util.Referential;

import org.xmlpull.v1.XmlPullParser;

@Log4j
public class TimetableParser implements Parser, Constant {

	private static final String CHILD_TAG = "ServiceCalendarFrame";

	@Override
	public void parse(Context context) throws Exception {

		XmlPullParser xpp = (XmlPullParser) context.get(PARSER);
		Referential referential = (Referential) context.get(REFERENTIAL);

		xpp.require(XmlPullParser.START_TAG, null, CHILD_TAG);
		context.put(COLUMN_NUMBER, xpp.getColumnNumber());
		context.put(LINE_NUMBER, xpp.getLineNumber());

		xpp.require(XmlPullParser.START_TAG, null, "ServiceCalendarFrame");

		Timetable timetable = new Timetable();

		while (xpp.nextTag() == XmlPullParser.START_TAG) {
			if (xpp.getName().equals("dayTypes")) {
				parseDayTypes(context, timetable);
			} else if (xpp.getName().equals("operatingDays")) {
				parseOperatingDays(context, timetable);
			} else if (xpp.getName().equals("operatingPeriods")) {
				parseOperatingPeriods(context, timetable);
			} else {
				XPPUtil.skipSubTree(log, xpp);
			}
		}

		referential.getTimetables().put(timetable.getObjectId(), timetable);

		log.debug("[DSU] " + "ServiceCalendarFrame" + "\t"
				+ timetable.getObjectId());
		timetable.setFilled(true);
	}

	private void parseDayTypes(Context context, Timetable timetable)
			throws Exception {
		XmlPullParser xpp = (XmlPullParser) context.get(PARSER);

		xpp.require(XmlPullParser.START_TAG, null, "dayTypes");
		context.put(COLUMN_NUMBER, xpp.getColumnNumber());
		context.put(LINE_NUMBER, xpp.getLineNumber());

		while (xpp.nextTag() == XmlPullParser.START_TAG) {
			if (xpp.getName().equals("DayType")) {

				String id = xpp.getAttributeValue(null, ID);
				timetable.setObjectId(id);

				Integer version = Integer.valueOf(xpp.getAttributeValue(null,
						VERSION));
				timetable.setObjectVersion(version != null ? version : 0);

				while (xpp.nextTag() == XmlPullParser.START_TAG) {
					if (xpp.getName().equals(NAME)) {
						timetable.setVersion(xpp.nextText());
					} else if (xpp.getName().equals("ShortName")) {
						timetable.setComment(xpp.nextText());
					} else if (xpp.getName().equals("properties")) {
						parseDaysOfWeeks(context, timetable);
					} else {
						XPPUtil.skipSubTree(log, xpp);
					}
				}
			} else {
				XPPUtil.skipSubTree(log, xpp);
			}
		}
	}

	private void parseDaysOfWeeks(Context context, Timetable timetable)
			throws Exception {
		XmlPullParser xpp = (XmlPullParser) context.get(PARSER);

		xpp.require(XmlPullParser.START_TAG, null, "properties");
		context.put(COLUMN_NUMBER, xpp.getColumnNumber());
		context.put(LINE_NUMBER, xpp.getLineNumber());

		List<String> list = new ArrayList<String>();
		while (xpp.nextTag() == XmlPullParser.START_TAG) {
			if (xpp.getName().equals("PropertyOfDay")) {
				while (xpp.nextTag() == XmlPullParser.START_TAG) {
					if (xpp.getName().equals("DaysOfWeek")) {
						list.add(xpp.nextText());
					} else {
						XPPUtil.skipSubTree(log, xpp);
					}
				}
			} else {
				XPPUtil.skipSubTree(log, xpp);
			}
		}

		timetable.setDayTypes(NetexUtils.getDayTypes(list));
	}

	private void parseOperatingDays(Context context, Timetable timetable)
			throws Exception {
		XmlPullParser xpp = (XmlPullParser) context.get(PARSER);

		xpp.require(XmlPullParser.START_TAG, null, "operatingDays");
		context.put(COLUMN_NUMBER, xpp.getColumnNumber());
		context.put(LINE_NUMBER, xpp.getLineNumber());

		while (xpp.nextTag() == XmlPullParser.START_TAG) {
			if (xpp.getName().equals("OperatingDay")) {
				while (xpp.nextTag() == XmlPullParser.START_TAG) {
					if (xpp.getName().equals("CalendarDate")) {
						Date date = ParserUtils.getSQLDate(xpp.nextText());
						CalendarDay value = new CalendarDay(date, true);
						timetable.addCalendarDay(value);
					} else {
						XPPUtil.skipSubTree(log, xpp);
					}
				}
			} else {
				XPPUtil.skipSubTree(log, xpp);
			}
		}
	}

	private void parseOperatingPeriods(Context context, Timetable timetable)
			throws Exception {
		XmlPullParser xpp = (XmlPullParser) context.get(PARSER);

		xpp.require(XmlPullParser.START_TAG, null, "operatingPeriods");
		context.put(COLUMN_NUMBER, xpp.getColumnNumber());
		context.put(LINE_NUMBER, xpp.getLineNumber());

		while (xpp.nextTag() == XmlPullParser.START_TAG) {
			if (xpp.getName().equals("OperatingPeriod")) {
				Period period = new Period();
				timetable.getPeriods().add(period);

				while (xpp.nextTag() == XmlPullParser.START_TAG) {
					if (xpp.getName().equals("FromDate")) {
						try {
							period.setStartDate(NetexUtils.getSQLDateTime(xpp
									.nextText()));
						} catch (ParseException ignored) {
						}
					} else if (xpp.getName().equals("ToDate")) {
						try {
							period.setEndDate(NetexUtils.getSQLDateTime(xpp
									.nextText()));
						} catch (ParseException ignored) {
						}
					} else {
						XPPUtil.skipSubTree(log, xpp);
					}
				}
			} else {
				XPPUtil.skipSubTree(log, xpp);
			}
		}
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
