package mobi.chouette.common;


import com.google.common.collect.Sets;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

public class CalendarPatternAnalyzerTest {
	CalendarPatternAnalyzer analyzer = new CalendarPatternAnalyzer();
	LocalDate calStartDate = LocalDate.of(2016, 1, 18); // Jan 18 2016


	@Test
	public void testParseFromStringEmptyPattern() {
		Set<DayOfWeek> significantDays = analyzer.computeSignificantDays(calStartDate, includedDaysAsBoolArray(emptyPattern));
		Assert.assertTrue(significantDays.isEmpty());
	}

	@Test
	public void testParseFromStringSundayPattern() {
		Set<DayOfWeek> significantDays = analyzer.computeSignificantDays(calStartDate, includedDaysAsBoolArray(sundayPattern));
		Assert.assertEquals(significantDays, Sets.newHashSet(DayOfWeek.SUNDAY));
	}

	@Test
	public void testParseFromStringSaturdayPattern() {
		Set<DayOfWeek> significantDays = analyzer.computeSignificantDays(calStartDate, includedDaysAsBoolArray(saturdayPattern));
		Assert.assertEquals(significantDays, Sets.newHashSet(DayOfWeek.SATURDAY));
	}

	@Test
	public void testParseFromStringFridayPattern() {
		Set<DayOfWeek> significantDays = analyzer.computeSignificantDays(calStartDate, includedDaysAsBoolArray(fridayPattern));
		Assert.assertEquals(significantDays, Sets.newHashSet(DayOfWeek.FRIDAY));
	}

	@Test
	public void testParseFromStringWednesdayPattern() {
		Set<DayOfWeek> significantDays = analyzer.computeSignificantDays(calStartDate, includedDaysAsBoolArray(wednesdayPattern));
		Assert.assertEquals(significantDays, Sets.newHashSet(DayOfWeek.WEDNESDAY));
	}

	@Test
	public void testSingleDayPattern() {
		Set<DayOfWeek> significantDays = analyzer.computeSignificantDays(calStartDate, includedDaysAsBoolArray(noPattern));
		Assert.assertTrue(significantDays.isEmpty());
	}

	@Test
	public void testParseFromStringTuesdayToFridayPattern() {
		Set<DayOfWeek> significantDays = analyzer.computeSignificantDays(calStartDate, includedDaysAsBoolArray(tuesdayToFridayPattern));
		Assert.assertEquals(significantDays, Sets.newHashSet(DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY, DayOfWeek.THURSDAY, DayOfWeek.FRIDAY));
	}

	@Test
	public void testParseFromDatesEmptyPattern() {
		Set<DayOfWeek> significantDays = analyzer.computeSignificantDays(calStartDate, includedDaysAsBoolArray(emptyPattern));
		Assert.assertTrue(significantDays.isEmpty());
	}

	@Test
	public void testParseFromDatesSundayPattern() {
		Set<DayOfWeek> significantDays = analyzer.computeSignificantDays(includedDaysAsLocalDates(calStartDate, sundayPattern));
		Assert.assertEquals(significantDays, Sets.newHashSet(DayOfWeek.SUNDAY));
	}

	@Test
	public void testParseFromDatesSaturdayPattern() {
		Set<DayOfWeek> significantDays = analyzer.computeSignificantDays(includedDaysAsLocalDates(calStartDate, saturdayPattern));
		Assert.assertEquals(significantDays, Sets.newHashSet(DayOfWeek.SATURDAY));
	}

	@Test
	public void testParseFromDatesFridayPattern() {
		Set<DayOfWeek> significantDays = analyzer.computeSignificantDays(includedDaysAsLocalDates(calStartDate, fridayPattern));
		Assert.assertEquals(significantDays, Sets.newHashSet(DayOfWeek.FRIDAY));
	}

	@Test
	public void testParseFromDatesWednesdayPattern() {
		Set<DayOfWeek> significantDays = analyzer.computeSignificantDays(includedDaysAsLocalDates(calStartDate, wednesdayPattern));
		Assert.assertEquals(significantDays, Sets.newHashSet(DayOfWeek.WEDNESDAY));
	}

	@Test
	public void testParseFromDatesNoPattern() {
		Set<DayOfWeek> significantDays = analyzer.computeSignificantDays(includedDaysAsLocalDates(calStartDate, noPattern));
		Assert.assertTrue(significantDays.isEmpty());
	}

	@Test
	public void testParseFromDatesTuesdayToFridayPattern() {
		Set<DayOfWeek> significantDays = analyzer.computeSignificantDays(includedDaysAsLocalDates(calStartDate, tuesdayToFridayPattern));
		Assert.assertEquals(significantDays, Sets.newHashSet(DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY, DayOfWeek.THURSDAY, DayOfWeek.FRIDAY));
	}

	@Test
	public void testComputeValidityIntervalNoDatesIncluded() {
		Assert.assertNull(analyzer.computeValidityInterval(new HashSet<>()));
	}

	@Test
	public void testComputeValidityIntervalNoPattern() {
		Assert.assertNull(analyzer.computeValidityInterval(includedDaysAsLocalDates(calStartDate, noPattern)));
	}

	@Test
	public void testComputeValidityIntervalSundayPattern() {
		CalendarPatternAnalyzer.ValidityInterval interval = analyzer.computeValidityInterval(includedDaysAsLocalDates(calStartDate, sundayPattern));
		Assert.assertEquals(interval.from, LocalDate.of(2016, 2, 1));
		Assert.assertEquals(interval.to, LocalDate.of(2016, 6, 25));
	}

	@Test
	public void testComputeValidityIntervalTuesdayToFridayPattern() {
		CalendarPatternAnalyzer.ValidityInterval interval = analyzer.computeValidityInterval(includedDaysAsLocalDates(calStartDate, tuesdayToFridayPattern));
		Assert.assertEquals(interval.from, LocalDate.of(2016, 1, 16));
		Assert.assertEquals(interval.to, LocalDate.of(2016, 6, 20));
	}

	String everyDayPattern ="111111111111111111111111111111111111111111111111111111111111111111111111111";


	@Test
	public void testComputeValidityIntervalEveryDayPattern() {
		CalendarPatternAnalyzer.ValidityInterval interval = analyzer.computeValidityInterval(includedDaysAsLocalDates(calStartDate, everyDayPattern));
		Assert.assertEquals(interval.from, calStartDate);
		Assert.assertEquals(interval.to, calStartDate.plusDays(74)); // TODO verify
	}

	String emptyPattern =
			"00000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000";

	String sundayPattern =
			"00000000000000000000100000010000001000000100000010000001000000100000000000001000000100000010000001000000100010010000001100000100000010000001000000100000010000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000";
	String saturdayPattern =
			"00000100000010000001000000100000010000001000000100000010000001000000000000010000001000000100000010000001000000100000010000001000000100000010000001000000100000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000";
	String noPattern =
			"00000000000000000000000000000000000001000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000";
	String fridayPattern =
			"00001000000100000010000001000000100000010000001000000100000010000000000000100000010000001000000100000010000001000000100000010000001000000100000010000001000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000";
	String wednesdayPattern =
			"00100000010000001000000100000010000001000000100000010000001000000000000010000001000000100000010000001000000100000010000001000000100000010000001000000100000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000";
	String tuesdayToFridayPattern =
			"01111000111100011110001111000111100011110001111000111100011110000000000111100011110001111000111100011110001101000111100001110001111000111100011110001111000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000";


	private boolean[] includedDaysAsBoolArray(String includedArray) {
		int length = includedArray.length();
		boolean[] initialIncludedArray = new boolean[length];

		// Convert to boolean array
		for (int i = 0; i < length; i++) {
			initialIncludedArray[i] = includedArray.charAt(i) == '1';
		}
		return initialIncludedArray;
	}

	private Set<LocalDate> includedDaysAsLocalDates(LocalDate startDate, String includedArray) {
		Set<LocalDate> dates = new HashSet<>();
		int i = 0;
		for (char c : includedArray.toCharArray()) {
			if (c == '1') {
				dates.add(startDate.plusDays(i));
			}
			i++;
		}

		return dates;
	}
}
