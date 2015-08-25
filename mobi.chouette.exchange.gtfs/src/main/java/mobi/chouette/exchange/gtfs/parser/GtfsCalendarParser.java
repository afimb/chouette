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
import mobi.chouette.exchange.gtfs.importer.Constant;
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
import mobi.chouette.exchange.validation.report.CheckPoint;
import mobi.chouette.exchange.validation.report.Location;
import mobi.chouette.exchange.validation.report.ValidationReport;
import mobi.chouette.model.CalendarDay;
import mobi.chouette.model.Period;
import mobi.chouette.model.Timetable;
import mobi.chouette.model.type.DayTypeEnum;
import mobi.chouette.model.util.ObjectFactory;
import mobi.chouette.model.util.Referential;

@Log4j
public class GtfsCalendarParser extends GtfsParser implements Parser, Validator, Constant {

	public static final String AFTER_MIDNIGHT_SUFFIX = "_after_midnight";

	private static final Comparator<Period> PERIOD_COMPARATOR = new Comparator<Period>() {

		@Override
		public int compare(Period o1, Period o2) {

			return o1.getStartDate().compareTo(o2.getStartDate());
		}

	};

	@Override
	public void parse(Context context) throws Exception {

		Referential referential = (Referential) context.get(REFERENTIAL);
		GtfsImporter importer = (GtfsImporter) context.get(PARSER);
		GtfsImportParameters configuration = (GtfsImportParameters) context.get(CONFIGURATION);

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
		GtfsImporter importer = (GtfsImporter) context.get(PARSER);
		ActionReport report = (ActionReport) context.get(REPORT);
		ValidationReport validationReport = (ValidationReport) context.get(MAIN_VALIDATION_REPORT);
		
		if (!importer.hasCalendarImporter() && !importer.hasCalendarDateImporter()) {
			// Add to report
			report.addFileInfo(GTFS_CALENDAR_DATES_FILE, FILE_STATE.ERROR, new FileError(FileError.CODE.FILE_NOT_FOUND, "One of the files \"calendar.txt\" or \"calendar_dates.txt\" must be provided (rules 1-GTFS-Calendar-1)"));
			// Add to validation report checkpoint 1-GTFS-Calendar-1
			Location[] locations = new Location[2];
			locations[0] = new Location(GTFS_CALENDAR_FILE, "calendar-failure");
			locations[1] = new Location(GTFS_CALENDAR_DATES_FILE, "calendar-dates-failure");
			validationReport.addDetail(GTFS_1_GTFS_Calendar_1, locations, "One of the files \"calendar.txt\" or \"calendar_dates.txt\" must be provided", CheckPoint.RESULT.NOK);
			// Stop parsing and render reports (1-GTFS-StopTime-1 is fatal)
			throw new Exception("One of the files \"calendar.txt\" or \"calendar_dates.txt\" must be provided");
		}
		
		// calendar.txt
		if (importer.hasCalendarImporter()) {
			// Add to report
			report.addFileInfo(GTFS_CALENDAR_FILE, FILE_STATE.OK);

			Index<GtfsCalendar> parser = null;
			try { // Read and check the header line of the file "calendar.txt"
				parser = importer.getCalendarByService();
			} catch (Exception ex ) {
				if (ex instanceof GtfsException) {
					reportError(report, validationReport, (GtfsException)ex, GTFS_CALENDAR_FILE);
				} else {
					throwUnknownError(report, validationReport, GTFS_CALENDAR_FILE);
				}
			}
			
			if (parser == null || parser.getLength() == 0) { // importer.getCalendarByService() fails for any other reason
				throwUnknownError(report, validationReport, GTFS_CALENDAR_FILE);
			}

			parser.getErrors().clear();
			try {
				for (GtfsCalendar bean : parser) {
					reportErrors(report, validationReport, bean.getErrors(), GTFS_CALENDAR_FILE);
					parser.validate(bean, importer);
				}
			} catch (Exception ex) {
				AbstractConverter.populateFileError(new FileInfo(GTFS_CALENDAR_FILE, FILE_STATE.ERROR), ex);
				throw ex;
			}
		}
		
		// calendar_dates.txt
		if (importer.hasCalendarDateImporter()) {
			// Add to report
			report.addFileInfo(GTFS_CALENDAR_DATES_FILE, FILE_STATE.OK);

			Index<GtfsCalendarDate> parser = null;
			try { // Read and check the header line of the file "calendar_dates.txt"
				parser = importer.getCalendarDateByService();
			} catch (Exception ex ) {
				if (ex instanceof GtfsException) {
					reportError(report, validationReport, (GtfsException)ex, GTFS_CALENDAR_DATES_FILE);
				} else {
					throwUnknownError(report, validationReport, GTFS_CALENDAR_DATES_FILE);
				}
			}
			
			if (parser == null || parser.getLength() == 0) { // importer.getCalendarDateByService() fails for any other reason
				throwUnknownError(report, validationReport, GTFS_CALENDAR_DATES_FILE);
			}

			parser.getErrors().clear();
			try {
				for (GtfsCalendarDate bean : parser) {
					reportErrors(report, validationReport, bean.getErrors(), GTFS_CALENDAR_DATES_FILE);
					parser.validate(bean, importer);
				}
			} catch (Exception ex) {
				AbstractConverter.populateFileError(new FileInfo(GTFS_CALENDAR_DATES_FILE, FILE_STATE.ERROR), ex);
				throw ex;
			}
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
		timetable.setFilled(true);
	}

	public void addCalendarDay(Timetable timetable, GtfsCalendarDate date) {
		timetable.addCalendarDay(new CalendarDay(date.getDate(),
				!GtfsCalendarDate.ExceptionType.Removed.equals(date.getExceptionType())));
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
			timetable.getPeriods().add(clonePeriodAfterMidnight(period));
		}

		for (CalendarDay calendarDay : source.getCalendarDays()) {
			timetable.getCalendarDays().add(cloneDateAfterMidnight(calendarDay));
		}
		return timetable;
	}

	private Period clonePeriodAfterMidnight(Period source) {
		Period result = new Period();

		result.setStartDate(new Date(source.getStartDate().getTime() + Timetable.ONE_DAY));
		result.setEndDate(new Date(source.getEndDate().getTime() + Timetable.ONE_DAY));

		return result;
	}

	private Date cloneDateAfterMidnight(Date source) {
		return new Date(source.getTime() + Timetable.ONE_DAY);
	}

	private CalendarDay cloneDateAfterMidnight(CalendarDay source) {
		return new CalendarDay(cloneDateAfterMidnight(source.getDate()), source.getIncluded().booleanValue());
	}

	static {
		ParserFactory.register(GtfsCalendarParser.class.getName(), new ParserFactory() {

			@Override
			protected Parser create() {
				return new GtfsCalendarParser();
			}
		});
	}

}
