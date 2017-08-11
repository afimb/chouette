package mobi.chouette.exchange.regtopp.importer.parser.v11;

import java.time.DayOfWeek;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Context;
import mobi.chouette.exchange.importer.Parser;
import mobi.chouette.exchange.importer.ParserFactory;
import mobi.chouette.exchange.regtopp.importer.RegtoppImportParameters;
import mobi.chouette.exchange.regtopp.importer.RegtoppImporter;
import mobi.chouette.exchange.regtopp.importer.index.v11.DaycodeById;
import mobi.chouette.exchange.regtopp.importer.parser.ObjectIdCreator;
import mobi.chouette.exchange.regtopp.model.v11.RegtoppDayCodeDKO;
import mobi.chouette.exchange.regtopp.model.v11.RegtoppDayCodeHeaderDKO;
import mobi.chouette.model.CalendarDay;
import mobi.chouette.model.Period;
import mobi.chouette.model.Timetable;
import mobi.chouette.model.type.DayTypeEnum;
import mobi.chouette.model.util.NamingUtil;
import mobi.chouette.model.util.ObjectFactory;
import mobi.chouette.model.util.Referential;

import org.joda.time.DateTimeConstants;
import org.joda.time.LocalDate;
import org.rutebanken.helper.calendar.CalendarPatternAnalyzer;

import static mobi.chouette.common.Constant.*;

@Log4j
public class RegtoppTimetableParser implements Parser {


	@Override
	public void parse(Context context) throws Exception {


		Referential referential = (Referential) context.get(REFERENTIAL);

		// Add all calendar entries to referential
		RegtoppImporter importer = (RegtoppImporter) context.get(PARSER);
		RegtoppImportParameters configuration = (RegtoppImportParameters) context.get(CONFIGURATION);
		DaycodeById dayCodeIndex = (DaycodeById) importer.getDayCodeById();

		RegtoppDayCodeHeaderDKO header = dayCodeIndex.getHeader();
		LocalDate calStartDate = header.getDate();

		for (RegtoppDayCodeDKO entry : dayCodeIndex) {
			convertTimetable(referential, configuration, calStartDate, entry, header);
		}

		LocalDate latestCalEndDate = null;
		// Find latest date in timetables, update Timetable start/end to reflect this
		for(Timetable t : referential.getTimetables().values()) {
			if(latestCalEndDate == null || t.getEndOfPeriod().isAfter(latestCalEndDate)) {
				latestCalEndDate = t.getEndOfPeriod();
			}
		}
		// Update cal enddate with found date
		for(Timetable t : referential.getTimetables().values()) {
			t.setEndOfPeriod(latestCalEndDate);
		}
		
	}

	public Timetable convertTimetable(Referential referential, RegtoppImportParameters configuration, LocalDate calStartDate, RegtoppDayCodeDKO entry, RegtoppDayCodeHeaderDKO header) {

		String chouetteTimetableId = ObjectIdCreator.createTimetableId(configuration, entry.getAdminCode(), entry.getDayCodeId(), header);

		Timetable timetable = ObjectFactory.getTimetable(referential, chouetteTimetableId);

		String dayCodesBinaryArray = entry.getDayCode();

		boolean[] includedDays = computeIncludedDays(dayCodesBinaryArray);

		java.time.LocalDate calStartLocalDate= java.time.LocalDate.of(calStartDate.getYear(),calStartDate.getMonthOfYear(),calStartDate.getDayOfMonth());
		Set<DayOfWeek> significantDaysInWeek = new CalendarPatternAnalyzer().computeSignificantDays(calStartLocalDate, includedDays);
		Set<DayTypeEnum> significantDayTypes = new HashSet<>();
		for(DayOfWeek d : significantDaysInWeek) {
			significantDayTypes.add(convertFromDayOfWeek(d));
		}
		
		
		if (significantDaysInWeek.isEmpty()) {
			// Add separate dates
			for (int i = 0; i < includedDays.length; i++) {
				if (includedDays[i]) {
					LocalDate currentDate = calStartDate.plusDays(i);
					timetable.addCalendarDay(new CalendarDay(currentDate, true));
				}
			}

		} else {
			// Add day types
			for (DayTypeEnum dayType : significantDayTypes) {
				timetable.addDayType(dayType);
			}

			// Add extra inclusions and exclusions
			for (int i = 0; i < includedDays.length; i++) {
				// Find type of day
				DayTypeEnum dayType = convertFromJodaTimeDayType(calStartDate.plusDays(i).getDayOfWeek());

				// If not included, add extra day
				if (includedDays[i] && !significantDayTypes.contains(dayType)) {
					LocalDate currentDate =calStartDate.plusDays(i);
					timetable.addCalendarDay(new CalendarDay(currentDate, true));
				}

				// If excluded but included in pattern, add exclusion to day
				if (!includedDays[i] && significantDayTypes.contains(dayType)) {
					LocalDate currentDate = calStartDate.plusDays(i);
					timetable.addCalendarDay(new CalendarDay(currentDate, false));
				}
			}
		}

		LocalDate endDate = calStartDate.plusDays(includedDays.length);

		timetable.setStartOfPeriod(calStartDate);
		timetable.setEndOfPeriod(endDate);
		Period period = new Period(calStartDate, endDate);
		timetable.getPeriods().add(period);

		NamingUtil.setDefaultName(timetable);

		timetable.setFilled(true);
		return timetable;
	}

	private boolean[] computeIncludedDays(String includedArray) {
		int length = includedArray.length();
		boolean[] initialIncludedArray = new boolean[length];

		// Convert to boolean array
		for (int i = 0; i < length; i++) {
			initialIncludedArray[i] = includedArray.charAt(i) == '1';
		}

		// Shorten array
		int lastSignificantDay = 0;
		for (int i = length - 1; i >= 0; i--) {
			if (initialIncludedArray[i]) {
				lastSignificantDay = i;
				break;
			}
		}

		if (lastSignificantDay == 0) {
			return new boolean[0]; // Empty
		} else {
			return Arrays.copyOf(initialIncludedArray, lastSignificantDay + 1);
		}
	}

	private DayTypeEnum convertFromJodaTimeDayType(int dayType) {
		switch (dayType) {
			case DateTimeConstants.MONDAY:
				return DayTypeEnum.Monday;
			case DateTimeConstants.TUESDAY:
				return DayTypeEnum.Tuesday;
			case DateTimeConstants.WEDNESDAY:
				return DayTypeEnum.Wednesday;
			case DateTimeConstants.THURSDAY:
				return DayTypeEnum.Thursday;
			case DateTimeConstants.FRIDAY:
				return DayTypeEnum.Friday;
			case DateTimeConstants.SATURDAY:
				return DayTypeEnum.Saturday;
			case DateTimeConstants.SUNDAY:
				return DayTypeEnum.Sunday;
			default:
				return null;
		}

	}
	private DayTypeEnum convertFromDayOfWeek(DayOfWeek dayType) {
		switch (dayType) {
			case MONDAY:
				return DayTypeEnum.Monday;
			case TUESDAY:
				return DayTypeEnum.Tuesday;
			case WEDNESDAY:
				return DayTypeEnum.Wednesday;
			case THURSDAY:
				return DayTypeEnum.Thursday;
			case FRIDAY:
				return DayTypeEnum.Friday;
			case SATURDAY:
				return DayTypeEnum.Saturday;
			case SUNDAY:
				return DayTypeEnum.Sunday;
			default:
				return null;
		}

	}

	static {
		ParserFactory.register(RegtoppTimetableParser.class.getName(), new ParserFactory() {
			@Override
			protected Parser create() {
				return new RegtoppTimetableParser();
			}
		});
	}

}
