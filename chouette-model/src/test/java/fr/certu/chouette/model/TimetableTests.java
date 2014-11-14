package fr.certu.chouette.model;

import java.sql.Date;
import java.util.Calendar;
import java.util.List;

import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.Assert;
import org.testng.annotations.Test;

import fr.certu.chouette.model.neptune.CalendarDay;
import fr.certu.chouette.model.neptune.Period;
import fr.certu.chouette.model.neptune.Timetable;
import fr.certu.chouette.model.neptune.type.DayTypeEnum;

@ContextConfiguration(locations = { "classpath:testContext.xml" })
public class TimetableTests extends AbstractTestNGSpringContextTests
{

   @Test(groups = { "model" }, description = "complete should initialize startOfPeriod and endOfPeriod")
   public void verifyCompleteWithOnlyDates()
   {
      Timetable t = new Timetable();

      Calendar c = Calendar.getInstance();

      Date firstDate = new Date(c.getTimeInMillis());
      t.addCalendarDay(new CalendarDay(firstDate, true));
      for (int i = 0; i < 5; i++)
      {
         c.add(Calendar.DATE, 2);
         t.addCalendarDay(new CalendarDay(new Date(c.getTimeInMillis()), true));
      }
      c.add(Calendar.DATE, 2);
      Date lastDate = new Date(c.getTimeInMillis());
      t.addCalendarDay(new CalendarDay(lastDate, true));
      Assert.assertNull(t.getStartOfPeriod(),
            "before complete, startOfPeriod should be null");
      Assert.assertNull(t.getEndOfPeriod(),
            "before complete, endOfPeriod should be null");
      t.complete();
      Assert.assertEquals(t.getStartOfPeriod(), firstDate,
            "after complete, startOfPeriod should containd firstDate ");
      Assert.assertEquals(t.getEndOfPeriod(), lastDate,
            "after complete, endOfPeriod should containd lastDate ");
   }

   @Test(groups = { "model" }, description = "complete should initialize startOfPeriod and endOfPeriod")
   public void verifyCompleteWithOnePeriod()
   {

      Calendar c = Calendar.getInstance();
      c.set(Calendar.YEAR, 2013);
      c.set(Calendar.MONTH, Calendar.FEBRUARY);
      c.set(Calendar.DAY_OF_MONTH, 4); // set to monday

      Date firstMonday = new Date(c.getTimeInMillis());
      c.add(Calendar.DATE, 2);
      Date firstWednesday = new Date(c.getTimeInMillis());
      c.add(Calendar.DATE, 2);
      Date firstFriday = new Date(c.getTimeInMillis());

      {
         Timetable t = new Timetable();
         t.addPeriod(new Period(firstMonday, firstFriday));
         t.addDayType(DayTypeEnum.Monday);
         t.addDayType(DayTypeEnum.Tuesday);
         t.addDayType(DayTypeEnum.Thursday);
         t.addDayType(DayTypeEnum.Friday);
         Assert.assertNull(t.getStartOfPeriod(),
               "before complete, startOfPeriod should be null");
         Assert.assertNull(t.getEndOfPeriod(),
               "before complete, endOfPeriod should be null");
         t.complete();
         Assert.assertEquals(t.getStartOfPeriod(), firstMonday,
               "after complete, startOfPeriod should containd firstMonday ");
         Assert.assertEquals(t.getEndOfPeriod(), firstFriday,
               "after complete, endOfPeriod should containd firstFriday ");
      }
      {
         Timetable t = new Timetable();
         t.addPeriod(new Period(firstMonday, firstFriday));
         t.addDayType(DayTypeEnum.Wednesday);
         Assert.assertNull(t.getStartOfPeriod(),
               "before complete, startOfPeriod should be null");
         Assert.assertNull(t.getEndOfPeriod(),
               "before complete, endOfPeriod should be null");
         t.complete();
         Assert.assertEquals(t.getStartOfPeriod(), firstWednesday,
               "after complete, startOfPeriod should containd firstWednesday ");
         Assert.assertEquals(t.getEndOfPeriod(), firstWednesday,
               "after complete, endOfPeriod should containd firstWednesday ");
      }
   }

   @Test(groups = { "model" }, description = "complete should initialize startOfPeriod and endOfPeriod")
   public void verifyCompleteWithPeriodsAndDates()
   {
      Calendar c = Calendar.getInstance();
      c.set(Calendar.YEAR, 2013);
      c.set(Calendar.MONTH, Calendar.FEBRUARY);
      c.set(Calendar.DAY_OF_MONTH, 4); // set to monday

      Date firstMonday = new Date(c.getTimeInMillis());
      c.add(Calendar.DATE, 2);
      Date firstWednesday = new Date(c.getTimeInMillis());
      c.add(Calendar.DATE, 2);
      Date firstFriday = new Date(c.getTimeInMillis());

      {
         Timetable t = new Timetable();
         t.addPeriod(new Period(firstMonday, firstFriday));
         t.addDayType(DayTypeEnum.Monday);
         t.addDayType(DayTypeEnum.Friday);
         t.addCalendarDay(new CalendarDay(firstWednesday, true));
         Assert.assertNull(t.getStartOfPeriod(),
               "before complete, startOfPeriod should be null");
         Assert.assertNull(t.getEndOfPeriod(),
               "before complete, endOfPeriod should be null");
         t.complete();
         Assert.assertEquals(t.getStartOfPeriod(), firstMonday,
               "after complete, startOfPeriod should containd firstMonday ");
         Assert.assertEquals(t.getEndOfPeriod(), firstFriday,
               "after complete, endOfPeriod should containd firstFriday ");
      }
      {
         Timetable t = new Timetable();
         t.addPeriod(new Period(firstMonday, firstFriday));
         t.addDayType(DayTypeEnum.Wednesday);
         t.addCalendarDay(new CalendarDay(firstMonday, true));
         t.addCalendarDay(new CalendarDay(firstFriday, true));
         Assert.assertNull(t.getStartOfPeriod(),
               "before complete, startOfPeriod should be null");
         Assert.assertNull(t.getEndOfPeriod(),
               "before complete, endOfPeriod should be null");
         t.complete();
         Assert.assertEquals(t.getStartOfPeriod(), firstMonday,
               "after complete, startOfPeriod should containd firstMonday ");
         Assert.assertEquals(t.getEndOfPeriod(), firstFriday,
               "after complete, endOfPeriod should containd firstFriday ");
      }
   }

   @Test(groups = { "model" }, description = "complete should initialize startOfPeriod and endOfPeriod")
   public void verifyCompleteWithOneDate()
   {
      Timetable t = new Timetable();

      Calendar c = Calendar.getInstance();

      Date firstDate = new Date(c.getTimeInMillis());
      t.addCalendarDay(new CalendarDay(firstDate, true));
      Assert.assertNull(t.getStartOfPeriod(),
            "before complete, startOfPeriod should be null");
      Assert.assertNull(t.getEndOfPeriod(),
            "before complete, endOfPeriod should be null");
      t.complete();
      Assert.assertEquals(t.getStartOfPeriod(), firstDate,
            "after complete, startOfPeriod should containd firstDate ");
      Assert.assertEquals(t.getEndOfPeriod(), firstDate,
            "after complete, endOfPeriod should containd firstDate ");
   }

   @Test(groups = { "model" }, description = "complete should initialize startOfPeriod and endOfPeriod")
   public void verifyCompleteWithEmptyTimetable()
   {
      Timetable t = new Timetable();

      Assert.assertNull(t.getStartOfPeriod(),
            "before complete, startOfPeriod should be null");
      Assert.assertNull(t.getEndOfPeriod(),
            "before complete, endOfPeriod should be null");
      t.complete();
      Assert.assertNull(t.getStartOfPeriod(),
            "after complete, startOfPeriod should be null");
      Assert.assertNull(t.getEndOfPeriod(),
            "after complete, endOfPeriod should be null");
   }

   @Test(groups = { "model" }, description = "peculiarDates/excludedDates")
   public void verifyPeculiarDates()
   {
      Timetable t = new Timetable();
      Assert.assertEquals(t.getPeculiarDates().size(), 0,
            "timetable should not have peculiarDates");
      Assert.assertEquals(t.getExcludedDates().size(), 0,
            "timetable should not have excludedDates");

      Calendar c = Calendar.getInstance();
      c.set(Calendar.YEAR, 2013);
      c.set(Calendar.MONTH, Calendar.FEBRUARY);
      c.set(Calendar.DAY_OF_MONTH, 4); // set to monday

      t.addCalendarDay(new CalendarDay(new Date(c.getTimeInMillis()), true));
      c.add(Calendar.DATE, 1);
      t.addCalendarDay(new CalendarDay(new Date(c.getTimeInMillis()), false));
      c.add(Calendar.DATE, 1);
      t.addCalendarDay(new CalendarDay(new Date(c.getTimeInMillis()), true));
      c.add(Calendar.DATE, 1);
      t.addCalendarDay(new CalendarDay(new Date(c.getTimeInMillis()), false));
      c.add(Calendar.DATE, 1);
      t.addCalendarDay(new CalendarDay(new Date(c.getTimeInMillis()), true));

      Assert.assertEquals(t.getPeculiarDates().size(), 3,
            "timetable should have peculiarDates");
      Assert.assertEquals(t.getExcludedDates().size(), 2,
            "timetable should have excludedDates");
   }

   @Test(groups = { "model" }, description = "effectivePeriods")
   public void verifyRealPeriods()
   {
      Timetable t = new Timetable();
      Assert.assertEquals(t.getPeriods().size(), 0,
            "timetable should not have periods");
      Assert.assertEquals(t.getEffectivePeriods().size(), 0,
            "timetable should not have effectivePeriods");

      Calendar c = Calendar.getInstance();
      c.set(Calendar.YEAR, 2013);
      c.set(Calendar.MONTH, Calendar.FEBRUARY);
      c.set(Calendar.DAY_OF_MONTH, 4); // set to monday

      Date firstMonday = new Date(c.getTimeInMillis());
      c.add(Calendar.DATE, 1);
      Date firstTuesday = new Date(c.getTimeInMillis());
      c.add(Calendar.DATE, 1);
      Date firstWednesday = new Date(c.getTimeInMillis());
      c.add(Calendar.DATE, 1);
      Date firstThursday = new Date(c.getTimeInMillis());
      c.add(Calendar.DATE, 1);
      Date firstFriday = new Date(c.getTimeInMillis());

      t.addPeriod(new Period(firstMonday, firstFriday));
      Assert.assertEquals(t.getEffectivePeriods().size(), 1,
            "timetable should have effectivePeriod");

      t.addCalendarDay(new CalendarDay(firstWednesday, false));
      Assert.assertEquals(t.getEffectivePeriods().size(), 2,
            "timetable should have effectivePeriods");
      Assert.assertEquals(t.getPeriods().size(), 1,
            "timetable should have periods preserved");

      List<Period> periods = t.getEffectivePeriods();
      Assert.assertEquals(periods.get(0).getStartDate(), firstMonday,
            "firstPeriod should start on first day");
      Assert.assertEquals(periods.get(0).getEndDate(), firstTuesday,
            "firstPeriod should end a day before excluded");
      Assert.assertEquals(periods.get(1).getStartDate(), firstThursday,
            "lastPeriod should start a day after excluded");
      Assert.assertEquals(periods.get(1).getEndDate(), firstFriday,
            "lastPeriod should end on last day");

   }

   @Test(groups = { "model" }, description = "check maximum size of fields")
   public void verifyFieldTruncating()
   {
      Timetable obj = new Timetable();
      String longString = "long string with more than 256 chrs";
      while (longString.length() < 256) longString += "0123456789";
      obj.setObjectId("toto:TimeTable:"+longString);
      Assert.assertEquals(obj.getObjectId().length(),255, "objectId should be truncated");
      obj.setComment(longString);
      Assert.assertEquals(obj.getComment().length(),255, "comment should be truncated");
      obj.setVersion(longString);
      Assert.assertEquals(obj.getVersion().length(),255, "version should be truncated");
   }

}
