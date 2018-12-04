package mobi.chouette.model;

import java.util.Arrays;
import java.util.TreeSet;

import mobi.chouette.model.type.DayTypeEnum;

import org.joda.time.LocalDate;
import org.testng.Assert;
import org.testng.annotations.Test;

public class VehicleJourneyTest {

	LocalDate mar1Sunday = new LocalDate(2020, 3, 1);
	LocalDate mar2Monday = new LocalDate(2020, 3, 2);
	LocalDate mar3Tuesday = new LocalDate(2020, 3, 3);
	LocalDate mar4Wednesday = new LocalDate(2020, 3, 4);
	LocalDate mar5Thursday = new LocalDate(2020, 3, 5);
	LocalDate mar6Friday = new LocalDate(2020, 3, 6);
	LocalDate mar7Saturday = new LocalDate(2020, 3, 7);


	@Test
	public void getActiveDates_whenIncludedAndExcludedDatesAndPeriods_thenCombineByApplyingInclusionAndThenExclusions() {

		Timetable excludedDates = new TimetableBuilder().withExcludedDate(mar1Sunday).withExcludedDate(mar2Monday).build();
		Timetable includedDates = new TimetableBuilder().withIncludedDate(mar2Monday).withIncludedDate(mar3Tuesday).withIncludedDate(mar4Wednesday).build();
		Timetable withWednesdaysAndThursdays = new TimetableBuilder().withDayTypes(DayTypeEnum.Wednesday, DayTypeEnum.Thursday).withPeriod(mar1Sunday, mar7Saturday).build();
		Timetable withIncludedDatesAndExcludedDatesAndSaturdays = new TimetableBuilder().withDayTypes(DayTypeEnum.Saturday).withPeriod(mar1Sunday, mar7Saturday).withIncludedDate(mar2Monday)
				.withExcludedDate(mar6Friday).build();

		VehicleJourney vehicleJourney = new VehicleJourney();
		vehicleJourney.getTimetables().addAll(Arrays.asList(excludedDates, includedDates, withWednesdaysAndThursdays, withIncludedDatesAndExcludedDatesAndSaturdays));

		Assert.assertEquals(vehicleJourney.getActiveDates(), new TreeSet<>(Arrays.asList(mar3Tuesday, mar4Wednesday, mar5Thursday, mar7Saturday)));

	}


	private class TimetableBuilder {

		Timetable timetable = new Timetable();

		TimetableBuilder withExcludedDate(LocalDate date) {
			CalendarDay calendarDay = new CalendarDay();
			calendarDay.setDate(date);
			calendarDay.setIncluded(false);
			timetable.addCalendarDay(calendarDay);
			return this;
		}

		TimetableBuilder withIncludedDate(LocalDate date) {
			CalendarDay calendarDay = new CalendarDay();
			calendarDay.setDate(date);
			calendarDay.setIncluded(true);
			timetable.addCalendarDay(calendarDay);
			return this;
		}

		TimetableBuilder withPeriod(LocalDate from, LocalDate to) {
			Period period = new Period();
			period.setStartDate(from);
			period.setEndDate(to);
			timetable.addPeriod(period);
			return this;
		}

		TimetableBuilder withDayTypes(DayTypeEnum... dayTypes) {
			for (DayTypeEnum dayType : dayTypes) {
				timetable.addDayType(dayType);
			}
			return this;
		}

		Timetable build() {
			return timetable;
		}
	}


	private Timetable timetable() {
		Timetable timetable = new Timetable();

		return timetable;
	}
}
