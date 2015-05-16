package mobi.chouette.exchange.gtfs.parser;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Context;
import mobi.chouette.exchange.gtfs.Constant;
import mobi.chouette.exchange.gtfs.importer.GtfsImportParameters;
import mobi.chouette.exchange.gtfs.model.GtfsCalendar;
import mobi.chouette.exchange.gtfs.model.GtfsCalendarDate;
import mobi.chouette.exchange.gtfs.model.importer.GtfsException;
import mobi.chouette.exchange.gtfs.model.importer.GtfsImporter;
import mobi.chouette.exchange.gtfs.model.importer.Index;
import mobi.chouette.exchange.importer.Parser;
import mobi.chouette.exchange.importer.ParserFactory;
import mobi.chouette.exchange.importer.Validator;
import mobi.chouette.exchange.report.ActionReport;
import mobi.chouette.exchange.report.FileError;
import mobi.chouette.exchange.report.FileInfo;
import mobi.chouette.exchange.report.FileInfo.FILE_STATE;
import mobi.chouette.model.CalendarDay;
import mobi.chouette.model.Period;
import mobi.chouette.model.Timetable;
import mobi.chouette.model.type.DayTypeEnum;
import mobi.chouette.model.util.ObjectFactory;
import mobi.chouette.model.util.Referential;

@Log4j
public class GtfsCalendarParser implements Parser, Validator, Constant {

	public static final String AFTER_MIDNIGHT_SUFFIX = "_after_midnight";

	private static long dayOffest = 24 * 3600000; // one day in milliseconds
	private static final Comparator<Period> PERIOD_COMPARATOR = new Comparator<Period>() {

		@Override
		public int compare(Period o1, Period o2) {

			return o1.getStartDate().compareTo(o2.getStartDate());
		}

	};

	private Referential referential;
	private GtfsImporter importer;
	private GtfsImportParameters configuration;

	@Override
	public void parse(Context context) throws Exception {

		referential = (Referential) context.get(REFERENTIAL);
		importer = (GtfsImporter) context.get(PARSER);
		configuration = (GtfsImportParameters) context.get(CONFIGURATION);

		if (importer.hasCalendarImporter()) {
			for (GtfsCalendar gtfsCalendar : importer.getCalendarByService()) {

				String objectId = AbstractConverter.composeObjectId(configuration.getObjectIdPrefix(),
						Timetable.TIMETABLE_KEY, gtfsCalendar.getServiceId(), log);
				Timetable timetable = ObjectFactory.getTimetable(referential, objectId);
				convert(context, gtfsCalendar, timetable);
			}
		}

		if (importer.hasCalendarDateImporter()) {

			for (String serviceId : importer.getCalendarDateByService().keys()) {

				String objectId = AbstractConverter.composeObjectId(configuration.getObjectIdPrefix(),
						Timetable.TIMETABLE_KEY, serviceId, log);

				Timetable timetable = referential.getTimetables().get(objectId);
				if (timetable == null) {
					timetable = ObjectFactory.getTimetable(referential, objectId);
					convert(context, createDummyCalandar(), timetable);
				}
				for (GtfsCalendarDate gtfsCalendarDate : importer.getCalendarDateByService().values(serviceId)) {
					addCalendarDay(timetable, gtfsCalendarDate);
				}
				setComment(timetable);
			}
		}

		List<Timetable> list = new ArrayList<Timetable>();
		for (Timetable timetable : referential.getTimetables().values()) {
			list.add(cloneTimetableAfterMidnight(timetable));
		}

		for (Timetable timetable : list) {
			referential.getSharedTimetables().put(timetable.getObjectId(), timetable);
			referential.getTimetables().put(timetable.getObjectId(), timetable);
		}

	}

	@Override
	public void validate(Context context) throws Exception {

		referential = (Referential) context.get(REFERENTIAL);
		importer = (GtfsImporter) context.get(PARSER);
		configuration = (GtfsImportParameters) context.get(CONFIGURATION);
		ActionReport report = (ActionReport) context.get(REPORT);

		boolean found = false;
		// calendar.txt
		if (importer.hasCalendarImporter()) {
			FileInfo file = new FileInfo(GTFS_CALENDAR_FILE,FILE_STATE.OK);
			report.getFiles().add(file);
			try {
				Index<GtfsCalendar> calendarParser = importer.getCalendarByService();
				for (GtfsCalendar gtfsCalendar : calendarParser) {
					calendarParser.validate(gtfsCalendar, importer);
				}
				found = true;
			} catch (Exception ex) {
				AbstractConverter.populateFileError(file, ex);
				throw ex;
			}
		}

		// calendar_dates.txt
		if (importer.hasCalendarDateImporter()) {
			FileInfo file = new FileInfo(GTFS_CALENDAR_DATES_FILE,FILE_STATE.OK);
			report.getFiles().add(file);
			try {
				Index<GtfsCalendarDate> calendarDateParser = importer.getCalendarDateByService();
				for (GtfsCalendarDate gtfsCalendarDate : calendarDateParser) {
					calendarDateParser.validate(gtfsCalendarDate, importer);
				}
				found = true;
			} catch (Exception ex) {
				AbstractConverter.populateFileError(file, ex);
				throw ex;
			}
		}

		if (!found)
		{
			FileInfo file = new FileInfo(GTFS_CALENDAR_FILE,FILE_STATE.OK);
			report.getFiles().add(file);
			file.addError(new FileError(FileError.CODE.FILE_NOT_FOUND, "missing calendar.txt and calendar_dates.txt"));
			file = new FileInfo(GTFS_CALENDAR_DATES_FILE,FILE_STATE.OK);
			report.getFiles().add(file);
			file.addError(new FileError(FileError.CODE.FILE_NOT_FOUND, "missing calendar.txt and calendar_dates.txt"));

			throw new GtfsException("calendar.txt or calendar_dates.txt", 0, null, GtfsException.ERROR.MISSING_FILE,
					null, null);
		}
	}

	protected void convert(Context context, GtfsCalendar gtfsCalendar, Timetable timetable) {

		List<DayTypeEnum> dayTypes = new ArrayList<DayTypeEnum>();
		if (gtfsCalendar.getMonday())
			dayTypes.add(DayTypeEnum.Monday);
		if (gtfsCalendar.getTuesday())
			dayTypes.add(DayTypeEnum.Tuesday);
		if (gtfsCalendar.getWednesday())
			dayTypes.add(DayTypeEnum.Wednesday);
		if (gtfsCalendar.getThursday())
			dayTypes.add(DayTypeEnum.Thursday);
		if (gtfsCalendar.getFriday())
			dayTypes.add(DayTypeEnum.Friday);
		if (gtfsCalendar.getSaturday())
			dayTypes.add(DayTypeEnum.Saturday);
		if (gtfsCalendar.getSunday())
			dayTypes.add(DayTypeEnum.Sunday);
		timetable.setDayTypes(dayTypes);

		if (gtfsCalendar.getStartDate() != null && gtfsCalendar.getEndDate() != null) {
			Period period = new Period();
			period.setStartDate(gtfsCalendar.getStartDate());
			period.setEndDate(gtfsCalendar.getEndDate());
			timetable.addPeriod(period);
		}

		List<Period> periods = timetable.getPeriods();
		if (periods != null)
			Collections.sort(periods, PERIOD_COMPARATOR);
		setComment(timetable);
	}

	public void addCalendarDay(Timetable timetable, GtfsCalendarDate date) {
		timetable.addCalendarDay(new CalendarDay(date.getDate(),
				date.getExceptionType() != GtfsCalendarDate.ExceptionType.Removed));
	}

	/**
	 * produce a comment with first date, end date and maybe applicable days
	 * 
	 * @param timetable
	 */
	public void setComment(Timetable timetable) {
		SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy");
		String monday = (timetable.getDayTypes().contains(DayTypeEnum.Monday)) ? "Mo" : "..";
		String tuesday = (timetable.getDayTypes().contains(DayTypeEnum.Tuesday)) ? "Tu" : "..";
		String wednesday = (timetable.getDayTypes().contains(DayTypeEnum.Wednesday)) ? "We" : "..";
		String thursday = (timetable.getDayTypes().contains(DayTypeEnum.Thursday)) ? "Th" : "..";
		String friday = (timetable.getDayTypes().contains(DayTypeEnum.Friday)) ? "Fr" : "..";
		String saturday = (timetable.getDayTypes().contains(DayTypeEnum.Saturday)) ? "Sa" : "..";
		String sunday = (timetable.getDayTypes().contains(DayTypeEnum.Sunday)) ? "Su" : "..";

		Date firstDate = null;
		Date lastDate = null;
		if (timetable.getPeriods() != null && !timetable.getPeriods().isEmpty()) {
			for (Period period : timetable.getPeriods()) {
				if (firstDate == null || period.getStartDate().before(firstDate))
					firstDate = period.getStartDate();
				if (lastDate == null || period.getEndDate().after(lastDate))
					lastDate = period.getEndDate();
			}
		}
		if (timetable.getCalendarDays() != null && !timetable.getCalendarDays().isEmpty()) {
			Calendar cal = Calendar.getInstance();
			for (Date date : timetable.getPeculiarDates()) {
				cal.setTime(date);
				if (cal.get(Calendar.DAY_OF_WEEK) == Calendar.MONDAY)
					monday = "Mo";
				if (cal.get(Calendar.DAY_OF_WEEK) == Calendar.TUESDAY)
					tuesday = "Tu";
				if (cal.get(Calendar.DAY_OF_WEEK) == Calendar.WEDNESDAY)
					wednesday = "We";
				if (cal.get(Calendar.DAY_OF_WEEK) == Calendar.THURSDAY)
					thursday = "Th";
				if (cal.get(Calendar.DAY_OF_WEEK) == Calendar.FRIDAY)
					friday = "Fr";
				if (cal.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY)
					saturday = "Sa";
				if (cal.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY)
					sunday = "Su";
				if (firstDate == null || date.before(firstDate))
					firstDate = date;
				if (lastDate == null || date.after(lastDate))
					lastDate = date;
			}
		}

		// security if timetable is empty
		if (firstDate != null && lastDate != null) {
			String comment = "From " + format.format(firstDate) + " to " + format.format(lastDate) + " : " + monday
					+ tuesday + wednesday + thursday + friday + saturday + sunday;
			timetable.setComment(comment);
		} else {
			timetable.setComment("Empty timetable");
		}
	}

	private GtfsCalendar createDummyCalandar() {
		GtfsCalendar calendar = new GtfsCalendar();
		calendar.setMonday(false);
		calendar.setTuesday(false);
		calendar.setWednesday(false);
		calendar.setThursday(false);
		calendar.setFriday(false);
		calendar.setSaturday(false);
		calendar.setSunday(false);
		return calendar;
	}

	private Timetable cloneTimetableAfterMidnight(Timetable source) {
		Timetable timetable = new Timetable();
		timetable.setObjectId(source.getObjectId() + AFTER_MIDNIGHT_SUFFIX);
		timetable.setComment(source.getComment() + " (after midnight)");
		timetable.setVersion(source.getVersion());

		List<DayTypeEnum> dayTypes = new ArrayList<DayTypeEnum>();
		for (DayTypeEnum dayType : source.getDayTypes()) {
			switch (dayType) {
			case Monday:
				dayTypes.add(DayTypeEnum.Tuesday);
				break;
			case Tuesday:
				dayTypes.add(DayTypeEnum.Wednesday);
				break;
			case Wednesday:
				dayTypes.add(DayTypeEnum.Thursday);
				break;
			case Thursday:
				dayTypes.add(DayTypeEnum.Friday);
				break;
			case Friday:
				dayTypes.add(DayTypeEnum.Saturday);
				break;
			case Saturday:
				dayTypes.add(DayTypeEnum.Sunday);
				break;
			case Sunday:
				dayTypes.add(DayTypeEnum.Monday);
				break;
			default:
				break;
			}
		}
		timetable.setDayTypes(dayTypes);

		for (Period period : source.getPeriods()) {
			timetable.addPeriod(clonePeriodAfterMidnight(period));
		}

		for (CalendarDay calendarDay : source.getCalendarDays()) {
			timetable.addCalendarDay(cloneDateAfterMidnight(calendarDay));
		}
		return timetable;
	}

	private Period clonePeriodAfterMidnight(Period source) {
		Period result = new Period();

		result.setStartDate(new Date(source.getStartDate().getTime() + dayOffest));
		result.setEndDate(new Date(source.getEndDate().getTime() + dayOffest));

		return result;
	}

	private Date cloneDateAfterMidnight(Date source) {
		return new Date(source.getTime() + dayOffest);
	}

	private CalendarDay cloneDateAfterMidnight(CalendarDay source) {
		return new CalendarDay(cloneDateAfterMidnight(source.getDate()), source.getIncluded());
	}

	static {
		ParserFactory.register(GtfsCalendarParser.class.getName(), new ParserFactory() {
			private GtfsCalendarParser instance = new GtfsCalendarParser();

			@Override
			protected Parser create() {
				return instance;
			}
		});
	}

}
