package mobi.chouette.common;


import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.log4j.Log4j;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Log4j
public class CalendarPatternAnalyzer {
	private static final int DAYS_PER_WEEK = 7;
	private static final int ERROR_MARGIN = 5;
	private static final int MIN_PERCENTAGE_ALL_DAYS_DETECTED = 90;
	private static final int MIN_DAYS_FOR_PATTERN = 5;


	public ValidityInterval computeValidityInterval(Set<LocalDate> includedDays) {
		Set<DayOfWeek> significantDays = computeSignificantDays(includedDays);
		if (!significantDays.isEmpty()) {

			TreeSet<LocalDate> sortedMatchingDays =
					includedDays.stream().filter(d -> significantDays.contains(d.getDayOfWeek())).collect(Collectors.toCollection(TreeSet::new));
			if (!sortedMatchingDays.isEmpty()) {
				LocalDate start = calculateStartOfValidityIntervalInclusive(sortedMatchingDays.first(), significantDays);
				LocalDate end = calculateEndOfValidityIntervalExclusive(sortedMatchingDays.last(), significantDays);

				return new ValidityInterval(start, end);
			}
		}

		return null;
	}


	public Set<DayOfWeek> computeSignificantDays(Set<LocalDate> includedDays) {
		Map<DayOfWeek, WeekDayEntry> dayMap = initWeekDayMap();

		includedDays.forEach(d -> dayMap.get(d.getDayOfWeek()).addCount());
		return computeSignificantDays(dayMap);
	}


	public Set<DayOfWeek> computeSignificantDays(LocalDate d, boolean[] included) {
		Map<DayOfWeek, WeekDayEntry> dayMap = initWeekDayMap();

		// Count hits for each day type
		for (int i = 0; i < included.length; i++) {
			DayOfWeek dayOfWeek = d.plusDays(i).getDayOfWeek();
			if (included[i]) {
				dayMap.get(dayOfWeek).addCount();
			}
		}
		return computeSignificantDays(dayMap);
	}

	private Set<DayOfWeek> computeSignificantDays(Map<DayOfWeek, WeekDayEntry> dayMap) {

		Set<DayOfWeek> significantDays = new HashSet<>();
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
			List<WeekDayEntry> entries = new ArrayList<>(dayMap.values());

			Collections.sort(entries, (o1, o2) -> (int) (o2.getPercentage() - o1.getPercentage()));

			// i = number of days attempted to merge together
			for (int i = 1; i < DAYS_PER_WEEK; i++) {
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

	/**
	 * Interval considered valid until from day after previous expected date before first pattern matching day
	 */
	private LocalDate calculateStartOfValidityIntervalInclusive(LocalDate firstMatchingDay, Set<DayOfWeek> expectedDays) {
		for (int i = 1; i <= DAYS_PER_WEEK; i++) {
			LocalDate candidate = firstMatchingDay.minusDays(i);
			if (expectedDays.contains(candidate.getDayOfWeek())) {
				return candidate.plusDays(1);
			}
		}
		return firstMatchingDay;
	}

	/**
	 * Interval considered valid until first expected date after last pattern matching day
	 */
	private LocalDate calculateEndOfValidityIntervalExclusive(LocalDate lastMatchingDay, Set<DayOfWeek> expectedDays) {
		for (int i = 1; i <= DAYS_PER_WEEK; i++) {
			LocalDate candidate = lastMatchingDay.plusDays(i);
			if (expectedDays.contains(candidate.getDayOfWeek())) {
				return candidate.minusDays(1);
			}
		}
		return lastMatchingDay;
	}

	private Map<DayOfWeek, WeekDayEntry> initWeekDayMap() {
		return Arrays.stream(DayOfWeek.values()).map(WeekDayEntry::new).collect(Collectors.toMap(WeekDayEntry::getDayType, Function.identity()));
	}

	public class ValidityInterval {
		public LocalDate from;
		public LocalDate to;

		public ValidityInterval(LocalDate from, LocalDate to) {
			this.from = from;
			this.to = to;
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
		DayOfWeek dayType;

		public WeekDayEntry(DayOfWeek dayType) {
			this.dayType = dayType;
		}

		public void addCount() {
			count++;
		}

	}
}
