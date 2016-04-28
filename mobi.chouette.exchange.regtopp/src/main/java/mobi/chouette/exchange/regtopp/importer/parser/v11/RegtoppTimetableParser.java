package mobi.chouette.exchange.regtopp.importer.parser.v11;

import static mobi.chouette.common.Constant.CONFIGURATION;
import static mobi.chouette.common.Constant.PARSER;
import static mobi.chouette.common.Constant.REFERENTIAL;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.joda.time.DateTimeConstants;
import org.joda.time.LocalDate;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Context;
import mobi.chouette.exchange.importer.Parser;
import mobi.chouette.exchange.importer.ParserFactory;
import mobi.chouette.exchange.regtopp.importer.RegtoppImportParameters;
import mobi.chouette.exchange.regtopp.importer.RegtoppImporter;
import mobi.chouette.exchange.regtopp.importer.index.v11.DaycodeById;
import mobi.chouette.exchange.regtopp.importer.parser.AbstractConverter;
import mobi.chouette.exchange.regtopp.model.v11.RegtoppDayCodeDKO;
import mobi.chouette.exchange.regtopp.model.v11.RegtoppDayCodeHeaderDKO;
import mobi.chouette.model.CalendarDay;
import mobi.chouette.model.Period;
import mobi.chouette.model.Timetable;
import mobi.chouette.model.type.DayTypeEnum;
import mobi.chouette.model.util.NamingUtil;
import mobi.chouette.model.util.ObjectFactory;
import mobi.chouette.model.util.ObjectIdTypes;
import mobi.chouette.model.util.Referential;

@Log4j
public class RegtoppTimetableParser implements Parser {

	private static final int ERROR_MARGIN = 5;
	private static final int MIN_PERCENTAGE_ALL_DAYS_DETECTED = 90;
	private static final int MIN_DAYS_FOR_PATTERN = 5;

	

	@Override
	public void parse(Context context) throws Exception {

		// Her tar vi allerede konsistenssjekkede data (ref validate-metode over) og bygger opp tilsvarende struktur i chouette.
		// Merk at import er linje-sentrisk, så man skal i denne klassen returnerer 1 line med x antall routes og stoppesteder, journeypatterns osv

		Referential referential = (Referential) context.get(REFERENTIAL);

		// Clear any previous data as this referential is reused / TODO

		// Add all calendar entries to referential
		RegtoppImporter importer = (RegtoppImporter) context.get(PARSER);
		RegtoppImportParameters configuration = (RegtoppImportParameters) context.get(CONFIGURATION);
		DaycodeById dayCodeIndex = (DaycodeById) importer.getDayCodeById();

		RegtoppDayCodeHeaderDKO header = dayCodeIndex.getHeader();
		LocalDate calStartDate = header.getDate();
		
		for (RegtoppDayCodeDKO entry : dayCodeIndex) {
			Timetable timetable = convertTimetable(referential, configuration, calStartDate, entry);
			Timetable cloneTimetableAfterMidnight = cloneTimetableAfterMidnight(timetable);

			referential.getSharedTimetables().put(cloneTimetableAfterMidnight.getObjectId(), cloneTimetableAfterMidnight);

		}

	}

	public Timetable convertTimetable(Referential referential, RegtoppImportParameters configuration, LocalDate calStartDate, RegtoppDayCodeDKO entry) {
		String chouetteTimetableId = AbstractConverter.composeObjectId(configuration.getObjectIdPrefix(), ObjectIdTypes.TIMETABLE_KEY, entry.getDayCodeId());

		Timetable timetable = ObjectFactory.getTimetable(referential, chouetteTimetableId);

		String dayCodesBinaryArray = entry.getDayCode();

		boolean[] includedDays = computeIncludedDays(dayCodesBinaryArray);
		Set<DayTypeEnum> significantDaysInWeek = computeSignificantDays(calStartDate, includedDays);

		if (significantDaysInWeek.isEmpty()) {
			// Add separate dates
			for (int i = 0; i < includedDays.length; i++) {
				if (includedDays[i]) {
					java.sql.Date currentDate = new java.sql.Date(calStartDate.plusDays(i).toDateMidnight().toDate().getTime());
					timetable.addCalendarDay(new CalendarDay(currentDate, true));
				}
			}

		} else {
			// Add day types
			for (DayTypeEnum dayType : significantDaysInWeek) {
				timetable.addDayType(dayType);
			}

			// Add extra inclusions and exclusions
			for (int i = 0; i < includedDays.length; i++) {
				// Find type of day
				DayTypeEnum dayType = convertFromJodaTimeDayType(calStartDate.plusDays(i).getDayOfWeek());

				// If not included, add extra day
				if (includedDays[i] && !significantDaysInWeek.contains(dayType)) {
					java.sql.Date currentDate = new java.sql.Date(calStartDate.plusDays(i).toDateMidnight().toDate().getTime());
					timetable.addCalendarDay(new CalendarDay(currentDate, true));
				}

				// If excluded but included in pattern, add exclusion to day
				if (!includedDays[i] && significantDaysInWeek.contains(dayType)) {
					java.sql.Date currentDate = new java.sql.Date(calStartDate.plusDays(i).toDateMidnight().toDate().getTime());
					timetable.addCalendarDay(new CalendarDay(currentDate, false));
				}
			}
		}

		java.sql.Date startDate = new java.sql.Date(calStartDate.toDateMidnight().toDate().getTime());
		java.sql.Date endDate = new java.sql.Date(calStartDate.plusDays(includedDays.length).toDateMidnight().toDate().getTime());

		timetable.setStartOfPeriod(startDate);
		timetable.setEndOfPeriod(endDate);
		Period period = new Period(startDate, endDate);
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

	@ToString
	private class WeekDayEntry {

		@Getter
		int count = 0;

		@Setter
		@Getter
		double percentage = 0;

		@Getter
		DayTypeEnum dayType;

		public WeekDayEntry(DayTypeEnum dayType) {
			this.dayType = dayType;
		}

		public void addCount() {
			count++;
		}

	}

	private Set<DayTypeEnum> computeSignificantDays(LocalDate d, boolean[] included) {

		Set<DayTypeEnum> significantDays = new HashSet<DayTypeEnum>();

		Map<DayTypeEnum, WeekDayEntry> dayMap = new HashMap<DayTypeEnum, WeekDayEntry>();

		dayMap.put(DayTypeEnum.Monday, new WeekDayEntry(DayTypeEnum.Monday));
		dayMap.put(DayTypeEnum.Tuesday, new WeekDayEntry(DayTypeEnum.Tuesday));
		dayMap.put(DayTypeEnum.Wednesday, new WeekDayEntry(DayTypeEnum.Wednesday));
		dayMap.put(DayTypeEnum.Thursday, new WeekDayEntry(DayTypeEnum.Thursday));
		dayMap.put(DayTypeEnum.Friday, new WeekDayEntry(DayTypeEnum.Friday));
		dayMap.put(DayTypeEnum.Saturday, new WeekDayEntry(DayTypeEnum.Saturday));
		dayMap.put(DayTypeEnum.Sunday, new WeekDayEntry(DayTypeEnum.Sunday));

		// Count hits for each day type
		for (int i = 0; i < included.length; i++) {
			int dayOfWeek = d.plusDays(i).getDayOfWeek();
			if (included[i]) {
				dayMap.get(convertFromJodaTimeDayType(dayOfWeek)).addCount();
			}
		}

		// compute percentages
		int totalDaysIncluded = 0;
		for (WeekDayEntry entry : dayMap.values()) {
			totalDaysIncluded += entry.getCount();
		}

		if (totalDaysIncluded > MIN_DAYS_FOR_PATTERN) {

			for (WeekDayEntry entry : dayMap.values()) {
				entry.setPercentage(((double) entry.getCount()) * 100 / (double) totalDaysIncluded);
			}

			// Try to find patterns
			List<WeekDayEntry> entries = new ArrayList<WeekDayEntry>(dayMap.values());

			Collections.sort(entries, new Comparator<WeekDayEntry>() {

				@Override
				public int compare(WeekDayEntry o1, WeekDayEntry o2) {
					return (int) (o2.getPercentage() - o1.getPercentage());
				}
			});

			// i = number of days attempted to merge together
			for (int i = 1; i < DateTimeConstants.DAYS_PER_WEEK; i++) {
				double minDayPercentage = (double) (MIN_PERCENTAGE_ALL_DAYS_DETECTED - ERROR_MARGIN) / (double) i; // for i=2 this means 42.5 for each day type

				// Start from 0
				double totalDayPercentage = 0;
				boolean allDaysAboveMinDayPercentage = true;
				for (int j = 0; j < i; j++) {
					double percentage = entries.get(j).getPercentage();
					if (percentage < minDayPercentage) {
						allDaysAboveMinDayPercentage &= false;
					}
					totalDayPercentage += percentage;
				}

				if (allDaysAboveMinDayPercentage && totalDayPercentage > MIN_PERCENTAGE_ALL_DAYS_DETECTED) {
					for (int j = 0; j < i; j++) {
						significantDays.add(entries.get(j).getDayType());
					}
					// Found match
					break;
				}
			}
		} else {
			log.debug("Too few days to extract pattern, expected at least " + MIN_DAYS_FOR_PATTERN + " but only got " + totalDaysIncluded);
		}

		return significantDays;
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

	static {
		ParserFactory.register(RegtoppTimetableParser.class.getName(), new ParserFactory() {
			@Override
			protected Parser create() {
				return new RegtoppTimetableParser();
			}
		});
	}

}
