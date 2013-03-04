package fr.certu.chouette.model;

import java.sql.Date;
import java.util.Calendar;

import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.Assert;
import org.testng.annotations.Test;

import fr.certu.chouette.model.neptune.Period;
import fr.certu.chouette.model.neptune.Timetable;
import fr.certu.chouette.model.neptune.type.DayTypeEnum;
@ContextConfiguration(locations={"classpath:chouetteContext.xml"})
public class TimetableTests extends AbstractTestNGSpringContextTests
{

	@Test(groups = { "model" } , description = "complete should initialize startOfPeriod and endOfPeriod")
	public void verifyCompleteWithOnlyDates()
	{
		Timetable t = new Timetable();

		Calendar c = Calendar.getInstance();

		Date firstDate = new Date(c.getTimeInMillis());
		t.addCalendarDay(firstDate);
		for (int i = 0; i < 5; i++)
		{
			c.add(Calendar.DATE, 2);
			t.addCalendarDay(new Date(c.getTimeInMillis()));
		}
		c.add(Calendar.DATE, 2);
		Date lastDate = new Date(c.getTimeInMillis());
		t.addCalendarDay(lastDate);
		Assert.assertNull(t.getStartOfPeriod(),"before complete, startOfPeriod should be null");
		Assert.assertNull(t.getEndOfPeriod(),"before complete, endOfPeriod should be null");
		t.complete();
		Assert.assertEquals(t.getStartOfPeriod(), firstDate, "after complete, startOfPeriod should containd firstDate ");
		Assert.assertEquals(t.getEndOfPeriod(), lastDate, "after complete, endOfPeriod should containd lastDate ");
	}

	@Test(groups = { "model" } , description = "complete should initialize startOfPeriod and endOfPeriod")
	public void verifyCompleteWithOnePeriod()
	{


		Calendar c = Calendar.getInstance();
		c.set(Calendar.YEAR,2013);
		c.set(Calendar.MONTH,Calendar.FEBRUARY);
		c.set(Calendar.DAY_OF_MONTH,4); // set to monday

		Date firstMonday = new Date(c.getTimeInMillis());
		c.add(Calendar.DATE, 2);
		Date firstWednesday = new Date(c.getTimeInMillis());
		c.add(Calendar.DATE, 2);
		Date firstFriday = new Date(c.getTimeInMillis());

		{
			Timetable t = new Timetable();
			t.addPeriod(new Period(firstMonday, firstFriday));
			t.addDayType(DayTypeEnum.MONDAY);
			t.addDayType(DayTypeEnum.TUESDAY);
			t.addDayType(DayTypeEnum.WEDNESDAY);
			t.addDayType(DayTypeEnum.THURSDAY);
			t.addDayType(DayTypeEnum.FRIDAY);
			Assert.assertNull(t.getStartOfPeriod(),"before complete, startOfPeriod should be null");
			Assert.assertNull(t.getEndOfPeriod(),"before complete, endOfPeriod should be null");
			t.complete();
			Assert.assertEquals(t.getStartOfPeriod(), firstMonday, "after complete, startOfPeriod should containd firstMonday ");
			Assert.assertEquals(t.getEndOfPeriod(), firstFriday, "after complete, endOfPeriod should containd firstFriday ");
		}
		{
			Timetable t = new Timetable();
			t.addPeriod(new Period(firstMonday, firstFriday));
			t.addDayType(DayTypeEnum.WEDNESDAY);
			Assert.assertNull(t.getStartOfPeriod(),"before complete, startOfPeriod should be null");
			Assert.assertNull(t.getEndOfPeriod(),"before complete, endOfPeriod should be null");
			t.complete();
			Assert.assertEquals(t.getStartOfPeriod(), firstWednesday, "after complete, startOfPeriod should containd firstWednesday ");
			Assert.assertEquals(t.getEndOfPeriod(), firstWednesday, "after complete, endOfPeriod should containd firstWednesday ");
		}
	}

	@Test(groups = { "model" } , description = "complete should initialize startOfPeriod and endOfPeriod")
	public void verifyCompleteWithPeriodsAndDates()
	{
		Calendar c = Calendar.getInstance();
		c.set(Calendar.YEAR,2013);
		c.set(Calendar.MONTH,Calendar.FEBRUARY);
		c.set(Calendar.DAY_OF_MONTH,4); // set to monday

		Date firstMonday = new Date(c.getTimeInMillis());
		c.add(Calendar.DATE, 2);
		Date firstWednesday = new Date(c.getTimeInMillis());
		c.add(Calendar.DATE, 2);
		Date firstFriday = new Date(c.getTimeInMillis());

		{
			Timetable t = new Timetable();
			t.addPeriod(new Period(firstMonday, firstFriday));
			t.addDayType(DayTypeEnum.MONDAY);
			t.addDayType(DayTypeEnum.FRIDAY);
			t.addCalendarDay(firstWednesday);
			Assert.assertNull(t.getStartOfPeriod(),"before complete, startOfPeriod should be null");
			Assert.assertNull(t.getEndOfPeriod(),"before complete, endOfPeriod should be null");
			t.complete();
			Assert.assertEquals(t.getStartOfPeriod(), firstMonday, "after complete, startOfPeriod should containd firstMonday ");
			Assert.assertEquals(t.getEndOfPeriod(), firstFriday, "after complete, endOfPeriod should containd firstFriday ");
		}
		{
			Timetable t = new Timetable();
			t.addPeriod(new Period(firstMonday, firstFriday));
			t.addDayType(DayTypeEnum.WEDNESDAY);
			t.addCalendarDay(firstMonday);
			t.addCalendarDay(firstFriday);
			Assert.assertNull(t.getStartOfPeriod(),"before complete, startOfPeriod should be null");
			Assert.assertNull(t.getEndOfPeriod(),"before complete, endOfPeriod should be null");
			t.complete();
			Assert.assertEquals(t.getStartOfPeriod(), firstMonday, "after complete, startOfPeriod should containd firstMonday ");
			Assert.assertEquals(t.getEndOfPeriod(), firstFriday, "after complete, endOfPeriod should containd firstFriday ");
		}
	}


	@Test(groups = { "model" } , description = "complete should initialize startOfPeriod and endOfPeriod")
	public void verifyCompleteWithOneDate()
	{
		Timetable t = new Timetable();

		Calendar c = Calendar.getInstance();

		Date firstDate = new Date(c.getTimeInMillis());
		t.addCalendarDay(firstDate);
		Assert.assertNull(t.getStartOfPeriod(),"before complete, startOfPeriod should be null");
		Assert.assertNull(t.getEndOfPeriod(),"before complete, endOfPeriod should be null");
		t.complete();
		Assert.assertEquals(t.getStartOfPeriod(), firstDate, "after complete, startOfPeriod should containd firstDate ");
		Assert.assertEquals(t.getEndOfPeriod(), firstDate, "after complete, endOfPeriod should containd firstDate ");
	}

	@Test(groups = { "model" } , description = "complete should initialize startOfPeriod and endOfPeriod")
	public void verifyCompleteWithEmptyTimetable()
	{
		Timetable t = new Timetable();

		Assert.assertNull(t.getStartOfPeriod(),"before complete, startOfPeriod should be null");
		Assert.assertNull(t.getEndOfPeriod(),"before complete, endOfPeriod should be null");
		t.complete();
		Assert.assertNull(t.getStartOfPeriod(),"after complete, startOfPeriod should be null");
		Assert.assertNull(t.getEndOfPeriod(),"after complete, endOfPeriod should be null");
	}



}
