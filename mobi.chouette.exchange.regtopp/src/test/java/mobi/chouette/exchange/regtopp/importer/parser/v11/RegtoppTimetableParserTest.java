package mobi.chouette.exchange.regtopp.importer.parser.v11;

import java.util.List;

import org.joda.time.DateMidnight;
import org.joda.time.LocalDate;
import org.testng.Assert;
import org.testng.annotations.Test;

import mobi.chouette.exchange.regtopp.importer.RegtoppImportParameters;
import mobi.chouette.exchange.regtopp.importer.parser.v11.RegtoppTimetableParser;
import mobi.chouette.exchange.regtopp.model.v11.RegtoppDayCodeDKO;
import mobi.chouette.model.CalendarDay;
import mobi.chouette.model.Timetable;
import mobi.chouette.model.type.DayTypeEnum;
import mobi.chouette.model.util.Referential;

public class RegtoppTimetableParserTest {

	@Test
	public void testParseEmptyTimetable() {

		RegtoppTimetableParser parser = new RegtoppTimetableParser();
		// Populate with test data
		RegtoppDayCodeDKO entry = create(
				"00000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000");
		LocalDate calStartDate = new LocalDate(2016, 1, 18); // Jan 18 2016

		RegtoppImportParameters configuration = new RegtoppImportParameters();
		Referential referential = new Referential();
		Timetable timetable = parser.convertTimetable(referential, configuration, calStartDate, entry);

		Assert.assertNotNull(timetable);
		Assert.assertEquals(new DateMidnight(timetable.getStartOfPeriod()), calStartDate.toDateMidnight());
		Assert.assertEquals(new DateMidnight(timetable.getEndOfPeriod()), calStartDate.toDateMidnight());

		Assert.assertEquals(1, timetable.getPeriods().size());

	}

	@Test
	public void testParseSundayTimetable() {

		RegtoppTimetableParser parser = new RegtoppTimetableParser();
		// Populate with test data
		RegtoppDayCodeDKO entry = create(
				"00000010000001000000100000010000001000000100000010000001000000100000000000001000000100000010000001000000100010010000001100000100000010000001000000100000010000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000");
		LocalDate calStartDate = new LocalDate(2016, 1, 18); // Jan 18 2016

		RegtoppImportParameters configuration = new RegtoppImportParameters();
		Referential referential = new Referential();
		Timetable timetable = parser.convertTimetable(referential, configuration, calStartDate, entry);

		Assert.assertEquals(new DateMidnight(timetable.getStartOfPeriod()), calStartDate.toDateMidnight());
		Assert.assertEquals(new DateMidnight(timetable.getEndOfPeriod()), calStartDate.plusDays(154).toDateMidnight());

		Assert.assertEquals(1, timetable.getPeriods().size());
		List<DayTypeEnum> dayTypes = timetable.getDayTypes();
		Assert.assertEquals(dayTypes.size(), 1);
		Assert.assertTrue(dayTypes.contains(DayTypeEnum.Sunday),dayTypes.toString());
	}

	@Test
	public void testParseSaturdayTimetable() {

		RegtoppTimetableParser parser = new RegtoppTimetableParser();
		// Populate with test data
		RegtoppDayCodeDKO entry = create(
				"00000100000010000001000000100000010000001000000100000010000001000000000000010000001000000100000010000001000000100000010000001000000100000010000001000000100000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000");
		LocalDate calStartDate = new LocalDate(2016, 1, 18); // Jan 18 2016

		RegtoppImportParameters configuration = new RegtoppImportParameters();
		Referential referential = new Referential();
		Timetable timetable = parser.convertTimetable(referential, configuration, calStartDate, entry);

		Assert.assertEquals(new DateMidnight(timetable.getEndOfPeriod()), calStartDate.plusDays(153).toDateMidnight());

		Assert.assertEquals(1, timetable.getPeriods().size());
		List<DayTypeEnum> dayTypes = timetable.getDayTypes();
		Assert.assertEquals(dayTypes.size(), 1);
		Assert.assertTrue(dayTypes.contains(DayTypeEnum.Saturday),dayTypes.toString());
	}

	@Test
	public void testParseFridayTimetable() {

		RegtoppTimetableParser parser = new RegtoppTimetableParser();
		// Populate with test data
		RegtoppDayCodeDKO entry = create(
				"00001000000100000010000001000000100000010000001000000100000010000000000000100000010000001000000100000010000001000000100000010000001000000100000010000001000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000");
		LocalDate calStartDate = new LocalDate(2016, 1, 18); // Jan 18 2016

		RegtoppImportParameters configuration = new RegtoppImportParameters();
		Referential referential = new Referential();
		Timetable timetable = parser.convertTimetable(referential, configuration, calStartDate, entry);

		Assert.assertEquals(new DateMidnight(timetable.getEndOfPeriod()), calStartDate.plusDays(152).toDateMidnight());

		Assert.assertEquals(1, timetable.getPeriods().size());
		List<DayTypeEnum> dayTypes = timetable.getDayTypes();
		Assert.assertEquals(dayTypes.size(), 1);
		Assert.assertTrue(dayTypes.contains(DayTypeEnum.Friday),dayTypes.toString());
	}

	@Test
	public void testParseWednesdayTimetable() {

		RegtoppTimetableParser parser = new RegtoppTimetableParser();
		// Populate with test data
		RegtoppDayCodeDKO entry = create(
				"00100000010000001000000100000010000001000000100000010000001000000000000010000001000000100000010000001000000100000010000001000000100000010000001000000100000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000");
		LocalDate calStartDate = new LocalDate(2016, 1, 18); // Jan 18 2016

		RegtoppImportParameters configuration = new RegtoppImportParameters();
		Referential referential = new Referential();
		Timetable timetable = parser.convertTimetable(referential, configuration, calStartDate, entry);

		Assert.assertEquals(new DateMidnight(timetable.getEndOfPeriod()), calStartDate.plusDays(150).toDateMidnight());

		Assert.assertEquals(1, timetable.getPeriods().size());
		List<DayTypeEnum> dayTypes = timetable.getDayTypes();
		Assert.assertEquals(dayTypes.size(), 1);
		Assert.assertTrue(dayTypes.contains(DayTypeEnum.Wednesday),dayTypes.toString());
	}
	
	@Test
	public void testSingleDayTimetable() {

		RegtoppTimetableParser parser = new RegtoppTimetableParser();
		// Populate with test data
		RegtoppDayCodeDKO entry = create(
				"00000000000000000000000000000000000001000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000");
		LocalDate calStartDate = new LocalDate(2016, 03, 29); // Match 29 2016

		RegtoppImportParameters configuration = new RegtoppImportParameters();
		Referential referential = new Referential();
		Timetable timetable = parser.convertTimetable(referential, configuration, calStartDate, entry);

		Assert.assertEquals(new DateMidnight(timetable.getEndOfPeriod()), calStartDate.plusDays(38).toDateMidnight());

		Assert.assertEquals(1, timetable.getPeriods().size());
		List<DayTypeEnum> dayTypes = timetable.getDayTypes();
		Assert.assertEquals(dayTypes.size(), 0);
		List<CalendarDay> calendarDays = timetable.getCalendarDays();
		Assert.assertEquals(1,calendarDays.size());
	}
	
	@Test
	public void testParseTuesdayToFridayTimetable() {

		RegtoppTimetableParser parser = new RegtoppTimetableParser();
		// Populate with test data
		RegtoppDayCodeDKO entry = create(
				"01111000111100011110001111000111100011110001111000111100011110000000000111100011110001111000111100011110001101000111100001110001111000111100011110001111000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000");
		LocalDate calStartDate = new LocalDate(2016, 1, 18); // Jan 18 2016

		RegtoppImportParameters configuration = new RegtoppImportParameters();
		Referential referential = new Referential();
		Timetable timetable = parser.convertTimetable(referential, configuration, calStartDate, entry);

		Assert.assertEquals(new DateMidnight(timetable.getEndOfPeriod()), calStartDate.plusDays(152).toDateMidnight());

		Assert.assertEquals(1, timetable.getPeriods().size());
		List<DayTypeEnum> dayTypes = timetable.getDayTypes();
		Assert.assertEquals(dayTypes.size(), 4);
		Assert.assertTrue(dayTypes.contains(DayTypeEnum.Tuesday),dayTypes.toString());
		Assert.assertTrue(dayTypes.contains(DayTypeEnum.Wednesday),dayTypes.toString());
		Assert.assertTrue(dayTypes.contains(DayTypeEnum.Thursday),dayTypes.toString());
		Assert.assertTrue(dayTypes.contains(DayTypeEnum.Friday),dayTypes.toString());
	}
	
	private RegtoppDayCodeDKO create(String dayCode) {
		RegtoppDayCodeDKO entry = new RegtoppDayCodeDKO();
		entry.setDayCode(dayCode);
		entry.setDayCodeId("0001");
		return entry;
	}

}
