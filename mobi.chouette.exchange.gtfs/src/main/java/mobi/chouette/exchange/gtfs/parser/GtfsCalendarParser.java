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
import mobi.chouette.exchange.gtfs.importer.GtfsImportParameters;
import mobi.chouette.exchange.gtfs.model.GtfsCalendar;
import mobi.chouette.exchange.gtfs.model.GtfsCalendarDate;
import mobi.chouette.exchange.gtfs.model.importer.CalendarDateByService;
import mobi.chouette.exchange.gtfs.model.importer.GtfsException;
import mobi.chouette.exchange.gtfs.model.importer.GtfsImporter;
import mobi.chouette.exchange.gtfs.model.importer.Index;
import mobi.chouette.exchange.gtfs.validation.Constant;
import mobi.chouette.exchange.gtfs.validation.ValidationReporter;
import mobi.chouette.exchange.importer.Parser;
import mobi.chouette.exchange.importer.ParserFactory;
import mobi.chouette.exchange.importer.Validator;
import mobi.chouette.model.CalendarDay;
import mobi.chouette.model.Period;
import mobi.chouette.model.Timetable;
import mobi.chouette.model.type.DayTypeEnum;
import mobi.chouette.model.util.ObjectFactory;
import mobi.chouette.model.util.Referential;

@Log4j
public class GtfsCalendarParser implements Parser, Validator, Constant {

	@Override
	public void validate(Context context) throws Exception {
		ValidationReporter validationReporter = (ValidationReporter) context.get(GTFS_REPORTER);
		validationReporter.getExceptions().clear();
		
		Index<GtfsCalendar> calendarParser = validateCalendar(context);
		validateCalendarDates(context, calendarParser);
	}
	
	private Index<GtfsCalendar> validateCalendar(Context context) throws Exception {
		GtfsImporter importer = (GtfsImporter) context.get(PARSER);
		ValidationReporter validationReporter = (ValidationReporter) context.get(GTFS_REPORTER);
			
		// calendar.txt
		Index<GtfsCalendar> calendarParser = null;
		if (importer.hasCalendarImporter()) { // the file "calendar.txt" exists ?
			validationReporter.reportSuccess(context, GTFS_1_GTFS_Calendar_1, GTFS_CALENDAR_FILE);
		
			try { // Read and check the header line of the file "calendar.txt"
				calendarParser = importer.getCalendarByService(); // return new CalendarByService("/.../calendar.txt", "service_id") { /** super(...) */
				//   IndexImpl<GtfsCalendar>(_path = "/.../calendar.txt", _key = "service_id", _value = "", _unique = true) {
				//     initialize() /** read the lines of file _path */
				//   }
				// }
			} catch (Exception ex ) {
				if (ex instanceof GtfsException) {
					validationReporter.reportError(context, (GtfsException)ex, GTFS_CALENDAR_FILE);
				} else {
					validationReporter.throwUnknownError(context, ex, GTFS_CALENDAR_FILE);
				}
			}
			
			validationReporter.validate(context, GTFS_CALENDAR_FILE, GtfsException.ERROR.INVALID_HEADER_FILE_FORMAT);
			validationReporter.validate(context, GTFS_CALENDAR_FILE, GtfsException.ERROR.EMPTY_HEADER_FIELD);
			validationReporter.validate(context, GTFS_CALENDAR_FILE, GtfsException.ERROR.DUPLICATE_HEADER_FIELD);
			validationReporter.validate(context, GTFS_CALENDAR_FILE, GtfsException.ERROR.MISSING_FIELD);
			validationReporter.validate(context, GTFS_CALENDAR_FILE, GtfsException.ERROR.DUPLICATE_FIELD);
			validationReporter.validate(context, GTFS_CALENDAR_FILE, GtfsException.ERROR.INVALID_FILE_FORMAT);
			validationReporter.validate(context, GTFS_CALENDAR_FILE, GtfsException.ERROR.MISSING_FILE);
			validationReporter.validate(context, GTFS_CALENDAR_FILE, GtfsException.ERROR.SYSTEM);
		
			if (calendarParser == null) { // importer.getCalendarByService() fails for any other reason
				validationReporter.throwUnknownError(context, new Exception("Cannot instantiate CalendarByService class"), GTFS_CALENDAR_FILE);
			} else {
				validationReporter.validateUnknownError(context);
			}
			
			if (!calendarParser.getErrors().isEmpty()) {
				validationReporter.reportErrors(context, calendarParser.getErrors(), GTFS_CALENDAR_FILE);
				calendarParser.getErrors().clear();
			}
			
			validationReporter.validate(context, GTFS_CALENDAR_FILE, GtfsException.ERROR.EXTRA_SPACE_IN_HEADER_FIELD);
			validationReporter.validate(context, GTFS_CALENDAR_FILE, GtfsException.ERROR.HTML_TAG_IN_HEADER_FIELD);
			validationReporter.validate(context, GTFS_CALENDAR_FILE, GtfsException.ERROR.EXTRA_HEADER_FIELD);
			validationReporter.validate(context, GTFS_CALENDAR_FILE, GtfsException.ERROR.MISSING_REQUIRED_FIELDS);
		
			if (calendarParser.getLength() == 0) {
				validationReporter.reportUnsuccess(context, GTFS_1_GTFS_Calendar_1, GTFS_CALENDAR_FILE);
				//validationReporter.reportError(context, new GtfsException(GTFS_CALENDAR_FILE, 1, null, GtfsException.ERROR.FILE_WITH_NO_ENTRY, null, null), GTFS_CALENDAR_FILE);
			} else {
				validationReporter.validate(context, GTFS_CALENDAR_FILE, GtfsException.ERROR.FILE_WITH_NO_ENTRY);
			}
			
			for (GtfsCalendar bean : calendarParser) {
				try {
					calendarParser.validate(bean, importer);
				} catch (Exception ex) {
					if (ex instanceof GtfsException) {
						validationReporter.reportError(context, (GtfsException)ex, GTFS_CALENDAR_FILE);
					} else {
						validationReporter.throwUnknownError(context, ex, GTFS_CALENDAR_FILE);
					}
				}
				validationReporter.reportErrors(context, bean.getErrors(), GTFS_CALENDAR_FILE);
			}
		} else {
			validationReporter.reportUnsuccess(context, GTFS_1_GTFS_Calendar_1, GTFS_CALENDAR_FILE);
		}
		return calendarParser;
	}
	
	private void validateCalendarDates(Context context, Index<GtfsCalendar> calendarParser) throws Exception {
		GtfsImporter importer = (GtfsImporter) context.get(PARSER);
		ValidationReporter validationReporter = (ValidationReporter) context.get(GTFS_REPORTER);
		
		// calendar_dates.txt
		Index<GtfsCalendarDate> calendarDateParser = null;
		if (importer.hasCalendarDateImporter()) { // the file "calendar_dates.txt" exists ?
			validationReporter.reportSuccess(context, GTFS_1_GTFS_Calendar_1, GTFS_CALENDAR_DATES_FILE);
		
			try { // Read and check the header line of the file "calendar_dates.txt"
				calendarDateParser = importer.getCalendarDateByService(); // return new CalendarDateByService("/.../calendar_dates.txt", "service_id") { /** super(...) */
				//   IndexImpl<GtfsCalendarDate>(_path = "/.../calendar_dates.txt", _key = "service_id", _value = "", _unique = true) {
				//     initialize() /** read the lines of file _path */
				//   }
				// }
			} catch (Exception ex ) {
				if (ex instanceof GtfsException) {
					validationReporter.reportError(context, (GtfsException)ex, GTFS_CALENDAR_DATES_FILE);
				} else {
					validationReporter.throwUnknownError(context, ex, GTFS_CALENDAR_DATES_FILE);
				}
			}			
			
			validationReporter.validate(context, GTFS_CALENDAR_DATES_FILE, GtfsException.ERROR.INVALID_HEADER_FILE_FORMAT);
			validationReporter.validate(context, GTFS_CALENDAR_DATES_FILE, GtfsException.ERROR.EMPTY_HEADER_FIELD);
			validationReporter.validate(context, GTFS_CALENDAR_DATES_FILE, GtfsException.ERROR.DUPLICATE_HEADER_FIELD);
			validationReporter.validate(context, GTFS_CALENDAR_DATES_FILE, GtfsException.ERROR.MISSING_FIELD);
			validationReporter.validate(context, GTFS_CALENDAR_DATES_FILE, GtfsException.ERROR.DUPLICATE_FIELD);
			validationReporter.validate(context, GTFS_CALENDAR_DATES_FILE, GtfsException.ERROR.INVALID_FILE_FORMAT);
			validationReporter.validate(context, GTFS_CALENDAR_DATES_FILE, GtfsException.ERROR.MISSING_FILE);
			validationReporter.validate(context, GTFS_CALENDAR_DATES_FILE, GtfsException.ERROR.SYSTEM);

		
			if (calendarDateParser == null) { // importer.getCalendarDateByService() fails for any other reason
				validationReporter.throwUnknownError(context, new Exception("Cannot instantiate CalendarDateByService class"), GTFS_CALENDAR_DATES_FILE);
			} else {
				validationReporter.validateUnknownError(context);
			}
			
			if (!calendarDateParser.getErrors().isEmpty()) {
				validationReporter.reportErrors(context, calendarDateParser.getErrors(), GTFS_CALENDAR_DATES_FILE);
				calendarDateParser.getErrors().clear();
			}
			
			validationReporter.validate(context, GTFS_CALENDAR_DATES_FILE, GtfsException.ERROR.EXTRA_SPACE_IN_HEADER_FIELD);
			validationReporter.validate(context, GTFS_CALENDAR_DATES_FILE, GtfsException.ERROR.HTML_TAG_IN_HEADER_FIELD);
			validationReporter.validate(context, GTFS_CALENDAR_DATES_FILE, GtfsException.ERROR.EXTRA_HEADER_FIELD);
			validationReporter.validate(context, GTFS_CALENDAR_DATES_FILE, GtfsException.ERROR.MISSING_REQUIRED_FIELDS);
		
			if (calendarDateParser.getLength() == 0) {
				validationReporter.reportUnsuccess(context, GTFS_1_GTFS_Calendar_1, GTFS_CALENDAR_DATES_FILE);
//				validationReporter.reportError(context, new GtfsException(GTFS_CALENDAR_DATES_FILE, 1, null, GtfsException.ERROR.FILE_WITH_NO_ENTRY, null, null), GTFS_CALENDAR_DATES_FILE);
			} else {
				validationReporter.validate(context, GTFS_CALENDAR_DATES_FILE, GtfsException.ERROR.FILE_WITH_NO_ENTRY);
			}
			
			CalendarDateByService.hashCodes.clear();
			for (GtfsCalendarDate bean : calendarDateParser) {
				try {
					calendarDateParser.validate(bean, importer);
				} catch (Exception ex) {
					if (ex instanceof GtfsException) {
						validationReporter.reportError(context, (GtfsException)ex, GTFS_CALENDAR_DATES_FILE);
					} else {
						validationReporter.throwUnknownError(context, ex, GTFS_CALENDAR_DATES_FILE);
					}
				}
				validationReporter.reportErrors(context, bean.getErrors(), GTFS_CALENDAR_DATES_FILE);
			}
		} else {
			validationReporter.reportUnsuccess(context, GTFS_1_GTFS_Calendar_1, GTFS_CALENDAR_DATES_FILE);
		}
		
		if (!importer.hasCalendarImporter() && !importer.hasCalendarDateImporter()) {
			validationReporter.reportFailure(context, GTFS_1_GTFS_Calendar_1, GTFS_CALENDAR_FILE, GTFS_CALENDAR_DATES_FILE);
		} else if ( (calendarDateParser == null && calendarParser.getLength() == 0) ||
				(calendarParser == null && calendarDateParser.getLength() == 0) ||
				(calendarParser != null && calendarDateParser != null && calendarParser.getLength() == 0 && calendarDateParser.getLength() == 0) ) {
			validationReporter.reportError(context, new GtfsException(GTFS_CALENDAR_FILE, 1, null, GtfsException.ERROR.FILE_WITH_NO_ENTRY, null, null), GTFS_CALENDAR_FILE);
		} else {
			validationReporter.validate(context, GTFS_CALENDAR_FILE, GtfsException.ERROR.FILE_WITH_NO_ENTRY);
			validationReporter.validate(context, GTFS_CALENDAR_DATES_FILE, GtfsException.ERROR.FILE_WITH_NO_ENTRY);
		}
	}

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

	public static final String AFTER_MIDNIGHT_SUFFIX = "_after_midnight";

	private static final Comparator<Period> PERIOD_COMPARATOR = new Comparator<Period>() {

		@Override
		public int compare(Period o1, Period o2) {
			return o1.getStartDate().compareTo(o2.getStartDate());
		}
	};

	static {
		ParserFactory.register(GtfsCalendarParser.class.getName(), new ParserFactory() {
			@Override
			protected Parser create() {
				return new GtfsCalendarParser();
			}
		});
	}
}
