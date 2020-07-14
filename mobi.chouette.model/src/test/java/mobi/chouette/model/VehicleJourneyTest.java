package mobi.chouette.model;

import java.util.Arrays;
import java.util.Date;
import java.util.TreeSet;

import mobi.chouette.model.type.DayTypeEnum;

import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.joda.time.LocalTime;
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

	private final LocalDate firstOfJuly2020 = new LocalDate(2020, 7, 1);
	private final LocalDate tenthOfJuly2020 = new LocalDate(2020, 7, 10);
	private final LocalDate fifteenthOfJuly2020 = new LocalDate(2020, 7, 15);
	private final LocalDate fifteenthOfAugust2020 = new LocalDate(2020, 8, 15);
	private final LocalDate seventeenthOfAugust = new LocalDate(2020, 8, 17);
	private final LocalDate thirtiethOfAugust2020 = new LocalDate(2020, 8, 30);


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

	/**
	 * When the period does not overlap any timetable and there is no day offset then the vehicle journey has no active timetable.
	 */
	@Test
	public void testActivePeriodWithoutDayOffset() {

		VehicleJourney vehicleJourney = new VehicleJourney();

		TimetableBuilder timeTableBuilder = new TimetableBuilder();
		Timetable timetable = timeTableBuilder
				.withPeriod(firstOfJuly2020, tenthOfJuly2020)
				.withDayTypes(DayTypeEnum.Monday)
				.build();

		vehicleJourney.getTimetables().add(timetable);

		VehicleJourneyAtStop departureVehicleJourneyAtStop = new VehicleJourneyAtStop();
		departureVehicleJourneyAtStop.setVehicleJourney(vehicleJourney);
		departureVehicleJourneyAtStop.setDepartureTime(new LocalTime(8,30,0));

		VehicleJourneyAtStop arrivalVehicleJourneyAtStop = new VehicleJourneyAtStop();
		arrivalVehicleJourneyAtStop.setVehicleJourney(vehicleJourney);
		arrivalVehicleJourneyAtStop.setArrivalTime(new LocalTime(9,30,0));

		Assert.assertTrue(vehicleJourney.hasStops(), "The vehicle journey should have at least 2 stops");
		Assert.assertTrue(vehicleJourney.hasTimetables(), "The vehicle journey should have at least one timetable");

		Assert.assertFalse(vehicleJourney.hasActiveTimetablesOnPeriod(fifteenthOfJuly2020, fifteenthOfAugust2020), "The vehicle journey should not have an active timetable on the period");

	}

	/**
	 * When the period does not overlap any timetable but there is a day offset at arrival that moves the effective
	 * start date of the period then the vehicle journey has an active timetable.
	 */
	@Test
	public void testActivePeriodWithDayOffsetAtArrival() {

		VehicleJourney vehicleJourney = new VehicleJourney();

		TimetableBuilder timeTableBuilder = new TimetableBuilder();
		Timetable timetable = timeTableBuilder
				.withPeriod(firstOfJuly2020, tenthOfJuly2020)
				.withDayTypes(DayTypeEnum.Monday)
				.build();

		vehicleJourney.getTimetables().add(timetable);

		VehicleJourneyAtStop departureVehicleJourneyAtStop = new VehicleJourneyAtStop();
		departureVehicleJourneyAtStop.setVehicleJourney(vehicleJourney);
		departureVehicleJourneyAtStop.setDepartureTime(new LocalTime(8,30,0));

		VehicleJourneyAtStop arrivalVehicleJourneyAtStop = new VehicleJourneyAtStop();
		arrivalVehicleJourneyAtStop.setVehicleJourney(vehicleJourney);
		arrivalVehicleJourneyAtStop.setArrivalTime(new LocalTime(7,30,0));
		arrivalVehicleJourneyAtStop.setArrivalDayOffset(11);

		Assert.assertTrue(vehicleJourney.hasStops(), "The vehicle journey should have at least 2 stops");
		Assert.assertTrue(vehicleJourney.hasTimetables(), "The vehicle journey should have at least one timetable");

		Assert.assertTrue(vehicleJourney.hasActiveTimetablesOnPeriod(fifteenthOfJuly2020, fifteenthOfAugust2020), "The vehicle journey should have at least one active timetable on the period");

	}

	/**
	 * When the period does not overlap any timetable but there is a day offset at departure that moves the effective
	 * start date of the period then the vehicle journey has an active timetable.
	 */
	@Test
	public void testActivePeriodWithDayOffsetAtDeparture() {

		VehicleJourney vehicleJourney = new VehicleJourney();

		TimetableBuilder timeTableBuilder = new TimetableBuilder();
		Timetable timetable = timeTableBuilder
				.withPeriod(seventeenthOfAugust, thirtiethOfAugust2020)
				.withDayTypes(DayTypeEnum.Monday)
				.build();

		vehicleJourney.getTimetables().add(timetable);

		VehicleJourneyAtStop departureVehicleJourneyAtStop = new VehicleJourneyAtStop();
		departureVehicleJourneyAtStop.setVehicleJourney(vehicleJourney);
		departureVehicleJourneyAtStop.setDepartureTime(new LocalTime(8,30,0));
		departureVehicleJourneyAtStop.setDepartureDayOffset(-11);

		VehicleJourneyAtStop arrivalVehicleJourneyAtStop = new VehicleJourneyAtStop();
		arrivalVehicleJourneyAtStop.setVehicleJourney(vehicleJourney);
		arrivalVehicleJourneyAtStop.setArrivalTime(new LocalTime(7,30,0));

		Assert.assertTrue(vehicleJourney.hasStops(), "The vehicle journey should have at least 2 stops");
		Assert.assertTrue(vehicleJourney.hasTimetables(), "The vehicle journey should have at least one timetable");

		Assert.assertTrue(vehicleJourney.hasActiveTimetablesOnPeriod(fifteenthOfJuly2020, fifteenthOfAugust2020), "The vehicle journey should have at least one active timetable on the period");

	}

	/**
	 * Positive day offsets at departure can move a timetable out of the period.
	 */
	@Test

	public void testActivePeriodWithPositiveDayOffsetAtDeparture() {

		VehicleJourney vehicleJourney = new VehicleJourney();

		TimetableBuilder timeTableBuilder = new TimetableBuilder();
		Timetable timetable = timeTableBuilder
				.withPeriod(fifteenthOfAugust2020, thirtiethOfAugust2020)
				.withDayTypes(DayTypeEnum.Monday, DayTypeEnum.Tuesday, DayTypeEnum.Wednesday, DayTypeEnum.Thursday, DayTypeEnum.Friday, DayTypeEnum.Saturday, DayTypeEnum.Sunday)
				.build();

		vehicleJourney.getTimetables().add(timetable);

		VehicleJourneyAtStop departureVehicleJourneyAtStop = new VehicleJourneyAtStop();
		departureVehicleJourneyAtStop.setVehicleJourney(vehicleJourney);
		departureVehicleJourneyAtStop.setDepartureTime(new LocalTime(8,30,0));
		departureVehicleJourneyAtStop.setDepartureDayOffset(1);

		VehicleJourneyAtStop arrivalVehicleJourneyAtStop = new VehicleJourneyAtStop();
		arrivalVehicleJourneyAtStop.setVehicleJourney(vehicleJourney);
		arrivalVehicleJourneyAtStop.setArrivalTime(new LocalTime(9,30,0));
		departureVehicleJourneyAtStop.setArrivalDayOffset(1);

		Assert.assertTrue(vehicleJourney.hasStops(), "The vehicle journey should have at least 2 stops");
		Assert.assertTrue(vehicleJourney.hasTimetables(), "The vehicle journey should have at least one timetable");

		Assert.assertFalse(vehicleJourney.hasActiveTimetablesOnPeriod(fifteenthOfJuly2020, fifteenthOfAugust2020), "The vehicle journey should not have an active timetable on the period");

	}

	/**
	 * Negative day offsets at arrival can move a timetable out of the period.
	 */

	@Test

	public void testActivePeriodWithNegativeDayOffsetAtArrival() {

		VehicleJourney vehicleJourney = new VehicleJourney();

		TimetableBuilder timeTableBuilder = new TimetableBuilder();
		Timetable timetable = timeTableBuilder
				.withPeriod(tenthOfJuly2020, fifteenthOfJuly2020)
				.withDayTypes(DayTypeEnum.Monday, DayTypeEnum.Tuesday, DayTypeEnum.Wednesday, DayTypeEnum.Thursday, DayTypeEnum.Friday, DayTypeEnum.Saturday, DayTypeEnum.Sunday)
				.build();

		vehicleJourney.getTimetables().add(timetable);

		VehicleJourneyAtStop departureVehicleJourneyAtStop = new VehicleJourneyAtStop();
		departureVehicleJourneyAtStop.setVehicleJourney(vehicleJourney);
		departureVehicleJourneyAtStop.setDepartureTime(new LocalTime(8,30,0));
		departureVehicleJourneyAtStop.setDepartureDayOffset(-1);

		VehicleJourneyAtStop arrivalVehicleJourneyAtStop = new VehicleJourneyAtStop();
		arrivalVehicleJourneyAtStop.setVehicleJourney(vehicleJourney);
		arrivalVehicleJourneyAtStop.setArrivalTime(new LocalTime(9,30,0));
		arrivalVehicleJourneyAtStop.setArrivalDayOffset(-1);

		Assert.assertTrue(vehicleJourney.hasStops(), "The vehicle journey should have at least 2 stops");
		Assert.assertTrue(vehicleJourney.hasTimetables(), "The vehicle journey should have at least one timetable");

		Assert.assertFalse(vehicleJourney.hasActiveTimetablesOnPeriod(fifteenthOfJuly2020, fifteenthOfAugust2020), "The vehicle journey should not have an active timetable on the period");

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
