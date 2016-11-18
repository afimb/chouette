package mobi.chouette.exchange.gtfs.parser;

import java.sql.Date;
import java.util.ArrayList;
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
import mobi.chouette.exchange.gtfs.validation.GtfsValidationReporter;
import mobi.chouette.exchange.importer.Parser;
import mobi.chouette.exchange.importer.ParserFactory;
import mobi.chouette.exchange.importer.Validator;
import mobi.chouette.model.CalendarDay;
import mobi.chouette.model.Period;
import mobi.chouette.model.Timetable;
import mobi.chouette.model.type.DayTypeEnum;
import mobi.chouette.model.util.NamingUtil;
import mobi.chouette.exchange.gtfs.GtfsChouetteIdGenerator;
import mobi.chouette.exchange.gtfs.GtfsChouetteIdObjectFactory;
import mobi.chouette.model.util.Referential;

@Log4j
public class GtfsCalendarParser extends GtfsChouetteIdGenerator implements Parser, Validator, Constant {

	@Override
	public void validate(Context context) throws Exception {
		GtfsValidationReporter gtfsValidationReporter = (GtfsValidationReporter) context.get(GTFS_REPORTER);
		gtfsValidationReporter.getExceptions().clear();
		
		Index<GtfsCalendar> calendarParser = validateCalendar(context);
		validateCalendarDates(context, calendarParser);
	}
	
	private Index<GtfsCalendar> validateCalendar(Context context) throws Exception {
		GtfsImporter importer = (GtfsImporter) context.get(PARSER);
		GtfsValidationReporter gtfsValidationReporter = (GtfsValidationReporter) context.get(GTFS_REPORTER);
			
		// calendar.txt
		// log.info("validating calendars");

		Index<GtfsCalendar> calendarParser = null;
		if (importer.hasCalendarImporter()) { // the file "calendar.txt" exists ?
			gtfsValidationReporter.reportSuccess(context, GTFS_1_GTFS_Common_2, GTFS_CALENDAR_FILE);
		
			try { // Read and check the header line of the file "calendar.txt"
				calendarParser = importer.getCalendarByService(); 
			} catch (Exception ex ) {
				if (ex instanceof GtfsException) {
					gtfsValidationReporter.reportError(context, (GtfsException)ex, GTFS_CALENDAR_FILE);
				} else {
					gtfsValidationReporter.throwUnknownError(context, ex, GTFS_CALENDAR_FILE);
				}
			}
			
			gtfsValidationReporter.validateOkCSV(context, GTFS_CALENDAR_FILE);
		
			if (calendarParser == null) { // importer.getCalendarByService() fails for any other reason
				gtfsValidationReporter.throwUnknownError(context, new Exception("Cannot instantiate CalendarByService class"), GTFS_CALENDAR_FILE);
			} else {
				gtfsValidationReporter.validate(context, GTFS_CALENDAR_FILE, calendarParser.getOkTests());
				gtfsValidationReporter.validateUnknownError(context);
			}
			
			if (!calendarParser.getErrors().isEmpty()) {
				gtfsValidationReporter.reportErrors(context, calendarParser.getErrors(), GTFS_CALENDAR_FILE);
				calendarParser.getErrors().clear();
			}
			
			gtfsValidationReporter.validateOKGeneralSyntax(context, GTFS_CALENDAR_FILE);
		
			GtfsException fatalException = null;
			calendarParser.setWithValidation(true);
			for (GtfsCalendar bean : calendarParser) {
				try {
					calendarParser.validate(bean, importer);
				} catch (Exception ex) {
					if (ex instanceof GtfsException) {
						gtfsValidationReporter.reportError(context, (GtfsException)ex, GTFS_CALENDAR_FILE);
					} else {
						gtfsValidationReporter.throwUnknownError(context, ex, GTFS_CALENDAR_FILE);
					}
				}
				for(GtfsException ex : bean.getErrors()) {
					if (ex.isFatal())
						fatalException = ex;
				}
				gtfsValidationReporter.reportErrors(context, bean.getErrors(), GTFS_CALENDAR_FILE);
				gtfsValidationReporter.validate(context, GTFS_CALENDAR_FILE, bean.getOkTests());
			}
			calendarParser.setWithValidation(false);
			if (fatalException != null)
				throw fatalException;
		}
		return calendarParser;
	}
	
	private void validateCalendarDates(Context context, Index<GtfsCalendar> calendarParser) throws Exception {
		GtfsImporter importer = (GtfsImporter) context.get(PARSER);
		GtfsValidationReporter gtfsValidationReporter = (GtfsValidationReporter) context.get(GTFS_REPORTER);
		
		// calendar_dates.txt
		//		log.info("validating calendar dates");
		Index<GtfsCalendarDate> calendarDateParser = null;
		if (importer.hasCalendarDateImporter()) { // the file "calendar_dates.txt" exists ?
			gtfsValidationReporter.reportSuccess(context, GTFS_1_GTFS_Common_2, GTFS_CALENDAR_DATES_FILE);
		
			try { // Read and check the header line of the file "calendar_dates.txt"
				calendarDateParser = importer.getCalendarDateByService(); 
			} catch (Exception ex ) {
				if (ex instanceof GtfsException) {
					gtfsValidationReporter.reportError(context, (GtfsException)ex, GTFS_CALENDAR_DATES_FILE);
				} else {
					gtfsValidationReporter.throwUnknownError(context, ex, GTFS_CALENDAR_DATES_FILE);
				}
			}			
			
			gtfsValidationReporter.validateOkCSV(context, GTFS_CALENDAR_DATES_FILE);

		
			if (calendarDateParser == null) { // importer.getCalendarDateByService() fails for any other reason
				gtfsValidationReporter.throwUnknownError(context, new Exception("Cannot instantiate CalendarDateByService class"), GTFS_CALENDAR_DATES_FILE);
			} else {
				gtfsValidationReporter.validate(context, GTFS_CALENDAR_DATES_FILE, calendarDateParser.getOkTests());
				gtfsValidationReporter.validateUnknownError(context);
			}
			
			if (!calendarDateParser.getErrors().isEmpty()) {
				gtfsValidationReporter.reportErrors(context, calendarDateParser.getErrors(), GTFS_CALENDAR_DATES_FILE);
				calendarDateParser.getErrors().clear();
			}
			
			gtfsValidationReporter.validateOKGeneralSyntax(context, GTFS_CALENDAR_DATES_FILE);
			
			CalendarDateByService.hashCodes.clear();
			GtfsException fatalException = null;
			calendarDateParser.setWithValidation(true);
			for (GtfsCalendarDate bean : calendarDateParser) {
				try {
					calendarDateParser.validate(bean, importer);
				} catch (Exception ex) {
					if (ex instanceof GtfsException) {
						gtfsValidationReporter.reportError(context, (GtfsException)ex, GTFS_CALENDAR_DATES_FILE);
					} else {
						gtfsValidationReporter.throwUnknownError(context, ex, GTFS_CALENDAR_DATES_FILE);
					}
				}
				for(GtfsException ex : bean.getErrors()) {
					if (ex.isFatal())
						fatalException = ex;
				}
				gtfsValidationReporter.reportErrors(context, bean.getErrors(), GTFS_CALENDAR_DATES_FILE);
				gtfsValidationReporter.validate(context, GTFS_CALENDAR_DATES_FILE, bean.getOkTests());
			}
			calendarDateParser.setWithValidation(false);
			if (fatalException != null)
				throw fatalException;
		}
		
		if (!importer.hasCalendarImporter() && !importer.hasCalendarDateImporter()) {
			gtfsValidationReporter.reportError(context, new GtfsException(GTFS_CALENDAR_FILE, 1, null, GtfsException.ERROR.MISSING_FILES, null, null), GTFS_CALENDAR_FILE);
		} else if ( (calendarDateParser == null && calendarParser.getLength() == 0) ||
				(calendarParser == null && calendarDateParser.getLength() == 0) ||
				(calendarParser != null && calendarDateParser != null && calendarParser.getLength() == 0 && calendarDateParser.getLength() == 0) ) {
			gtfsValidationReporter.reportError(context, new GtfsException(GTFS_CALENDAR_FILE, 1, null, GtfsException.ERROR.FILES_WITH_NO_ENTRY, null, null), GTFS_CALENDAR_FILE);
		} else {
			gtfsValidationReporter.validate(context, GTFS_CALENDAR_FILE, GtfsException.ERROR.FILES_WITH_NO_ENTRY);
			gtfsValidationReporter.validate(context, GTFS_CALENDAR_DATES_FILE, GtfsException.ERROR.FILES_WITH_NO_ENTRY);
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
				Timetable timetable = GtfsChouetteIdObjectFactory.getTimetable(referential, toChouetteId(objectId, "default_codespace"));
				convert(context, gtfsCalendar, timetable);
			}
		}

		if (importer.hasCalendarDateImporter()) {

			for (String serviceId : importer.getCalendarDateByService().keys()) {

				String objectId = AbstractConverter.composeObjectId(configuration.getObjectIdPrefix(),
						Timetable.TIMETABLE_KEY, serviceId, log);

				Timetable timetable = referential.getTimetables().get(objectId);
				for (GtfsCalendarDate gtfsCalendarDate : importer.getCalendarDateByService().values(serviceId)) {
					if (timetable == null) {
						timetable = GtfsChouetteIdObjectFactory.getTimetable(referential, toChouetteId(objectId, "default_codespace"));
						convert(context, createDummyCalandar(gtfsCalendarDate.getId()), timetable);
					}
					addCalendarDay(timetable, gtfsCalendarDate);
				}
				NamingUtil.setDefaultName(timetable);
			}
		}

		List<Timetable> list = new ArrayList<Timetable>();
		for (Timetable timetable : referential.getTimetables().values()) {
			list.add(cloneTimetableAfterMidnight(timetable));
		}

		for (Timetable timetable : list) {
			referential.getSharedTimetables().put(timetable.getChouetteId(), timetable);
			referential.getTimetables().put(timetable.getChouetteId(), timetable);
		}
       list.clear();
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
		NamingUtil.setDefaultName(timetable);
		timetable.setFilled(true);

	}

	public void addCalendarDay(Timetable timetable, GtfsCalendarDate date) {
		timetable.addCalendarDay(new CalendarDay(date.getDate(),
				!GtfsCalendarDate.ExceptionType.Removed.equals(date.getExceptionType())));
	}


	private GtfsCalendar createDummyCalandar(Integer id ) {
		GtfsCalendar calendar = new GtfsCalendar();
		calendar.setId(id);
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
		timetable.getChouetteId().setObjectId(source.getChouetteId().getObjectId() + AFTER_MIDNIGHT_SUFFIX);
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
