package fr.certu.chouette.exchange.gtfs.export.producer;

import java.sql.Date;
import java.util.Calendar;

import org.apache.log4j.Logger;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.Assert;
import org.testng.Reporter;
import org.testng.annotations.Test;

import fr.certu.chouette.common.ChouetteException;
import fr.certu.chouette.exchange.gtfs.exporter.producer.GtfsServiceProducer;
import fr.certu.chouette.exchange.gtfs.exporter.report.GtfsReport;
import fr.certu.chouette.exchange.gtfs.model.GtfsCalendar;
import fr.certu.chouette.exchange.gtfs.model.GtfsCalendarDate;
import fr.certu.chouette.model.neptune.CalendarDay;
import fr.certu.chouette.model.neptune.Period;
import fr.certu.chouette.model.neptune.Timetable;
import fr.certu.chouette.model.neptune.type.DayTypeEnum;

@ContextConfiguration(locations = { "classpath:testContext.xml",
      "classpath*:chouetteContext.xml" })
public class GtfsExportCalendarProducerTests extends
      AbstractTestNGSpringContextTests
{
   private static final Logger logger = Logger
         .getLogger(GtfsExportCalendarProducerTests.class);

   @Test(groups = { "Producers" }, description = "test timetable with period")
   public void verifyCalendarProducer1() throws ChouetteException
   {
      Calendar c = Calendar.getInstance();
      c.set(Calendar.YEAR, 2013);
      c.set(Calendar.MONTH, Calendar.JULY);
      c.set(Calendar.DAY_OF_MONTH, 1);
      c.set(Calendar.HOUR_OF_DAY, 12);

//      GtfsServiceProducer producer = new GtfsServiceProducer();
//
//      GtfsReport report = new GtfsReport(GtfsReport.KEY.EXPORT);
//      Timetable neptuneObject = new Timetable();
//      neptuneObject.setObjectId("GTFS:Timetable:1234");
//      neptuneObject.setComment("name");
//      neptuneObject.addDayType(DayTypeEnum.Monday);
//      neptuneObject.addDayType(DayTypeEnum.Saturday);
//      Date startDate = new Date(c.getTimeInMillis());
//      c.add(Calendar.DATE, 15);
//      Date endDate = new Date(c.getTimeInMillis());
//      Period period = new Period(startDate, endDate);
//      neptuneObject.addPeriod(period);
//
//      GtfsCalendar gtfsObject = producer.produce(neptuneObject, report);
//      System.out.println("verifyCalendarProducer1");
//      System.out.println(gtfsObject);
//
//      Assert.assertEquals(gtfsObject.getServiceId(),
//            toGtfsId(neptuneObject.getObjectId()),
//            "timetable id must be correcty set");
//      Assert.assertEquals(gtfsObject.getStartDate(), startDate,
//            "start date must be correcty set");
//      Assert.assertEquals(gtfsObject.getEndDate(), endDate,
//            "end date must be correcty set");
//      Assert.assertTrue(gtfsObject.isMonday(), "monday must be true");
//      Assert.assertFalse(gtfsObject.isTuesday(), "tuesday must be false");
//      Assert.assertFalse(gtfsObject.isWednesday(), "wednesday must be false");
//      Assert.assertFalse(gtfsObject.isThursday(), "thursday must be false");
//      Assert.assertFalse(gtfsObject.isFriday(), "friday must be false");
//      Assert.assertTrue(gtfsObject.isSaturday(), "saturday must be true");
//      Assert.assertFalse(gtfsObject.isSunday(), "sunday must be false");

   }

   @Test(groups = { "Producers" }, description = "test timetable with dates")
   public void verifyCalendarProducer2() throws ChouetteException
   {
      Calendar c = Calendar.getInstance();
      c.set(Calendar.YEAR, 2013);
      c.set(Calendar.MONTH, Calendar.JULY);
      c.set(Calendar.DAY_OF_MONTH, 1);
      c.set(Calendar.HOUR_OF_DAY, 12);

//      GtfsServiceProducer producer = new GtfsServiceProducer();
//
//      GtfsReport report = new GtfsReport(GtfsReport.KEY.EXPORT);
//      Timetable neptuneObject = new Timetable();
//      neptuneObject.setObjectId("GTFS:Timetable:1234");
//      neptuneObject.setComment("name");
//      for (int i = 0; i < 5; i++)
//      {
//         Date date = new Date(c.getTimeInMillis());
//         neptuneObject.addCalendarDay(new CalendarDay(date, true));
//         c.add(Calendar.DATE, 3);
//      }
//
//      GtfsCalendar gtfsObject = producer.produce(neptuneObject, report);
//      System.out.println("verifyCalendarProducer2");
//      System.out.println(gtfsObject);
//
//      Assert.assertEquals(gtfsObject.getServiceId(),
//            toGtfsId(neptuneObject.getObjectId()),
//            "service id must be correcty set");
//      Assert.assertNull(gtfsObject.getStartDate(), "start date must be null");
//      Assert.assertNull(gtfsObject.getEndDate(), "end date must be null");
//      Assert.assertFalse(gtfsObject.isMonday(), "monday must be false");
//      Assert.assertFalse(gtfsObject.isTuesday(), "tuesday must be false");
//      Assert.assertFalse(gtfsObject.isWednesday(), "wednesday must be false");
//      Assert.assertFalse(gtfsObject.isThursday(), "thursday must be false");
//      Assert.assertFalse(gtfsObject.isFriday(), "friday must be false");
//      Assert.assertFalse(gtfsObject.isSaturday(), "saturday must be false");
//      Assert.assertFalse(gtfsObject.isSunday(), "sunday must be false");
//      Assert.assertEquals(gtfsObject.getCalendarDates().size(), 5,
//            "calendar must have 5 dates");
//      c.set(Calendar.YEAR, 2013);
//      c.set(Calendar.MONTH, Calendar.JULY);
//      c.set(Calendar.DAY_OF_MONTH, 1);
//      for (GtfsCalendarDate gtfsCalendarDate : gtfsObject.getCalendarDates())
//      {
//         Date date = new Date(c.getTimeInMillis());
//         c.add(Calendar.DATE, 3);
//         Assert.assertEquals(gtfsCalendarDate.getServiceId(),
//               toGtfsId(neptuneObject.getObjectId()),
//               "service id must be correcty set");
//         Assert.assertEquals(gtfsCalendarDate.getDate(), date,
//               "calendar date must be correctly");
//         Assert.assertEquals(gtfsCalendarDate.getExceptionType(), 1,
//               "calendar date must be inclusive");
//      }

   }

   @Test(groups = { "Producers" }, description = "test timetable with period and dates")
   public void verifyCalendarProducer3() throws ChouetteException
   {
//      Calendar c = Calendar.getInstance();
//      c.set(Calendar.YEAR, 2013);
//      c.set(Calendar.MONTH, Calendar.JULY);
//      c.set(Calendar.DAY_OF_MONTH, 1);
//      c.set(Calendar.HOUR_OF_DAY, 12);
//
//      GtfsServiceProducer producer = new GtfsServiceProducer();
//
//      GtfsReport report = new GtfsReport(GtfsReport.KEY.EXPORT);
//      Timetable neptuneObject = new Timetable();
//      neptuneObject.setObjectId("GTFS:Timetable:1234");
//      neptuneObject.setComment("name");
//      neptuneObject.addDayType(DayTypeEnum.Tuesday);
//      neptuneObject.addDayType(DayTypeEnum.Wednesday);
//      neptuneObject.addDayType(DayTypeEnum.Thursday);
//      neptuneObject.addDayType(DayTypeEnum.Friday);
//      neptuneObject.addDayType(DayTypeEnum.Sunday);
//      Date startDate = new Date(c.getTimeInMillis());
//      c.add(Calendar.DATE, 15);
//      Date endDate = new Date(c.getTimeInMillis());
//      Period period = new Period(startDate, endDate);
//      neptuneObject.addPeriod(period);
//      c.add(Calendar.DATE, 15);
//      for (int i = 0; i < 5; i++)
//      {
//         Date date = new Date(c.getTimeInMillis());
//         neptuneObject.addCalendarDay(new CalendarDay(date, true));
//         c.add(Calendar.DATE, 3);
//      }
//
//      GtfsCalendar gtfsObject = producer.produce(neptuneObject, report);
//      System.out.println("verifyCalendarProducer3");
//      System.out.println(gtfsObject);
//
//      Assert.assertEquals(gtfsObject.getServiceId(),
//            toGtfsId(neptuneObject.getObjectId()),
//            "service id must be correcty set");
//      Assert.assertEquals(gtfsObject.getStartDate(), startDate,
//            "start date must be correcty set");
//      Assert.assertEquals(gtfsObject.getEndDate(), endDate,
//            "end date must be correcty set");
//      Assert.assertFalse(gtfsObject.isMonday(), "monday must be false");
//      Assert.assertTrue(gtfsObject.isTuesday(), "tuesday must be true");
//      Assert.assertTrue(gtfsObject.isWednesday(), "wednesday must be true");
//      Assert.assertTrue(gtfsObject.isThursday(), "thursday must be true");
//      Assert.assertTrue(gtfsObject.isFriday(), "friday must be true");
//      Assert.assertFalse(gtfsObject.isSaturday(), "saturday must be false");
//      Assert.assertTrue(gtfsObject.isSunday(), "sunday must be true");
//      Assert.assertEquals(gtfsObject.getCalendarDates().size(), 5,
//            "calendar must have 5 dates");
//      c.set(Calendar.YEAR, 2013);
//      c.set(Calendar.MONTH, Calendar.JULY);
//      c.set(Calendar.DAY_OF_MONTH, 1);
//      c.add(Calendar.DATE, 30);
//      for (GtfsCalendarDate gtfsCalendarDate : gtfsObject.getCalendarDates())
//      {
//         Date date = new Date(c.getTimeInMillis());
//         c.add(Calendar.DATE, 3);
//         Assert.assertEquals(gtfsCalendarDate.getServiceId(),
//               toGtfsId(neptuneObject.getObjectId()),
//               "service id must be correcty set");
//         Assert.assertEquals(gtfsCalendarDate.getDate(), date,
//               "calendar date must be correctly");
//         Assert.assertEquals(gtfsCalendarDate.getExceptionType(), 1,
//               "calendar date must be inclusive");
//      }

   }

   @Test(groups = { "Producers" }, description = "test timetable with 2 periods with few interval between")
   public void verifyCalendarProducer4() throws ChouetteException
   {
//      Calendar c = Calendar.getInstance();
//      c.set(Calendar.YEAR, 2013);
//      c.set(Calendar.MONTH, Calendar.JULY);
//      c.set(Calendar.DAY_OF_MONTH, 1);
//      c.set(Calendar.HOUR_OF_DAY, 12);
//
//      GtfsServiceProducer producer = new GtfsServiceProducer();
//
//      GtfsReport report = new GtfsReport(GtfsReport.KEY.EXPORT);
//      Timetable neptuneObject = new Timetable();
//      neptuneObject.setObjectId("GTFS:Timetable:1234");
//      neptuneObject.setComment("name");
//      neptuneObject.addDayType(DayTypeEnum.Monday);
//      neptuneObject.addDayType(DayTypeEnum.Tuesday);
//      neptuneObject.addDayType(DayTypeEnum.Thursday);
//      neptuneObject.addDayType(DayTypeEnum.Friday);
//      neptuneObject.addDayType(DayTypeEnum.Saturday);
//      neptuneObject.addDayType(DayTypeEnum.Sunday);
//      Date startDate1 = new Date(c.getTimeInMillis());
//      c.add(Calendar.DATE, 30);
//      Date endDate1 = new Date(c.getTimeInMillis());
//      Period period1 = new Period(startDate1, endDate1);
//      neptuneObject.addPeriod(period1);
//      c.add(Calendar.DATE, 15);
//      Date startDate2 = new Date(c.getTimeInMillis());
//      c.add(Calendar.DATE, 30);
//      Date endDate2 = new Date(c.getTimeInMillis());
//      Period period2 = new Period(startDate2, endDate2);
//      neptuneObject.addPeriod(period2);
//
//      Reporter.log(neptuneObject.toString());
//
//      GtfsCalendar gtfsObject = producer.produce(neptuneObject, report);
//      Reporter.log("verifyCalendarProducer4");
//      Reporter.log(gtfsObject.toString());
//
//      Assert.assertEquals(gtfsObject.getServiceId(),
//            toGtfsId(neptuneObject.getObjectId()),
//            "service id must be correcty set");
//      Assert.assertEquals(gtfsObject.getStartDate(), null,
//            "start date must be correcty set");
//      Assert.assertEquals(gtfsObject.getEndDate(), null,
//            "end date must be correcty set");
//      Assert.assertTrue(gtfsObject.isMonday(), "monday must be true");
//      Assert.assertTrue(gtfsObject.isTuesday(), "tuesday must be true");
//      Assert.assertFalse(gtfsObject.isWednesday(), "wednesday must be false");
//      Assert.assertTrue(gtfsObject.isThursday(), "thursday must be true");
//      Assert.assertTrue(gtfsObject.isFriday(), "friday must be true");
//      Assert.assertTrue(gtfsObject.isSaturday(), "saturday must be true");
//      Assert.assertTrue(gtfsObject.isSunday(), "sunday must be true");
//      Assert.assertEquals(gtfsObject.getCalendarDates().size(), 53,
//            "calendar must have 53 dates");
//      c.set(Calendar.YEAR, 2013);
//      c.set(Calendar.MONTH, Calendar.JULY);
//      c.set(Calendar.DAY_OF_MONTH, 1);
//      if (c.get(Calendar.DAY_OF_WEEK) == Calendar.WEDNESDAY)
//         c.add(Calendar.DATE, 1);
//      for (GtfsCalendarDate gtfsCalendarDate : gtfsObject.getCalendarDates())
//      {
//         Date date = new Date(c.getTimeInMillis());
//         c.add(Calendar.DATE, 1);
//         if (c.get(Calendar.DAY_OF_MONTH) == 31
//               && c.get(Calendar.MONTH) == Calendar.JULY)
//            c.add(Calendar.DATE, 15);
//         if (c.get(Calendar.DAY_OF_WEEK) == Calendar.WEDNESDAY)
//            c.add(Calendar.DATE, 1);
//         Assert.assertEquals(gtfsCalendarDate.getServiceId(),
//               toGtfsId(neptuneObject.getObjectId()),
//               "service id must be correcty set");
//         Assert.assertEquals(gtfsCalendarDate.getDate(), date,
//               "calendar date must be correctly");
//         Assert.assertEquals(gtfsCalendarDate.getExceptionType(), 1,
//               "calendar date must be inclusive");
//      }

   }

   @Test(groups = { "Producers" }, description = "test timetable with 2 periods with big interval between")
   public void verifyCalendarProducer5() throws ChouetteException
   {
//      Calendar c = Calendar.getInstance();
//      c.set(Calendar.YEAR, 2013);
//      c.set(Calendar.MONTH, Calendar.JULY);
//      c.set(Calendar.DAY_OF_MONTH, 1);
//      c.set(Calendar.HOUR_OF_DAY, 12);
//
//      GtfsServiceProducer producer = new GtfsServiceProducer();
//
//      GtfsReport report = new GtfsReport(GtfsReport.KEY.EXPORT);
//      Timetable neptuneObject = new Timetable();
//      neptuneObject.setObjectId("GTFS:Timetable:1234");
//      neptuneObject.setComment("name");
//      neptuneObject.addDayType(DayTypeEnum.Monday);
//      neptuneObject.addDayType(DayTypeEnum.Tuesday);
//      neptuneObject.addDayType(DayTypeEnum.Thursday);
//      neptuneObject.addDayType(DayTypeEnum.Friday);
//      neptuneObject.addDayType(DayTypeEnum.Saturday);
//      neptuneObject.addDayType(DayTypeEnum.Sunday);
//      Date startDate1 = new Date(c.getTimeInMillis());
//      c.add(Calendar.DATE, 15);
//      Date endDate1 = new Date(c.getTimeInMillis());
//      Period period1 = new Period(startDate1, endDate1);
//      neptuneObject.addPeriod(period1);
//      c.add(Calendar.DATE, 60);
//      Date startDate2 = new Date(c.getTimeInMillis());
//      c.add(Calendar.DATE, 15);
//      Date endDate2 = new Date(c.getTimeInMillis());
//      Period period2 = new Period(startDate2, endDate2);
//      neptuneObject.addPeriod(period2);
//
//      GtfsCalendar gtfsObject = producer.produce(neptuneObject, report);
//      System.out.println("verifyCalendarProducer5");
//      System.out.println(gtfsObject);
//
//      Assert.assertEquals(gtfsObject.getServiceId(),
//            toGtfsId(neptuneObject.getObjectId()),
//            "service id must be correcty set");
//      Assert.assertNull(gtfsObject.getStartDate(),
//            "start date must be correcty set");
//      Assert.assertNull(gtfsObject.getEndDate(),
//            "end date must be correcty set");
//      Assert.assertTrue(gtfsObject.isMonday(), "monday must be true");
//      Assert.assertTrue(gtfsObject.isTuesday(), "tuesday must be true");
//      Assert.assertFalse(gtfsObject.isWednesday(), "wednesday must be false");
//      Assert.assertTrue(gtfsObject.isThursday(), "thursday must be true");
//      Assert.assertTrue(gtfsObject.isFriday(), "friday must be true");
//      Assert.assertTrue(gtfsObject.isSaturday(), "saturday must be true");
//      Assert.assertTrue(gtfsObject.isSunday(), "sunday must be true");
//      Assert.assertEquals(gtfsObject.getCalendarDates().size(), 28,
//            "calendar must have 28 dates");
//      c.set(Calendar.YEAR, 2013);
//      c.set(Calendar.MONTH, Calendar.JULY);
//      c.set(Calendar.DAY_OF_MONTH, 1);
//      if (c.get(Calendar.DAY_OF_WEEK) == Calendar.WEDNESDAY)
//         c.add(Calendar.DATE, 1);
//      int cpt = 0;
//      for (GtfsCalendarDate gtfsCalendarDate : gtfsObject.getCalendarDates())
//      {
//         Date date = new Date(c.getTimeInMillis());
//         cpt++;
//         if (cpt == 14)
//         {
//            c.add(Calendar.DATE, 59);
//         }
//         c.add(Calendar.DATE, 1);
//         if (c.get(Calendar.DAY_OF_WEEK) == Calendar.WEDNESDAY)
//            c.add(Calendar.DATE, 1);
//         Assert.assertEquals(gtfsCalendarDate.getServiceId(),
//               toGtfsId(neptuneObject.getObjectId()),
//               "service id must be correcty set");
//         Assert.assertEquals(gtfsCalendarDate.getDate(), date,
//               "calendar date must be correctly");
//         Assert.assertEquals(gtfsCalendarDate.getExceptionType(), 1,
//               "calendar date must be inclusive");
//      }

   }

   protected String toGtfsId(String neptuneId)
   {
      String[] tokens = neptuneId.split(":");
      return tokens[2];
   }

   // private void printItems(String indent,List<ReportItem> items)
   // {
   // if (items == null) return;
   // for (ReportItem item : items)
   // {
   // System.out.println(indent+item.getStatus().name()+" : "+item.getLocalizedMessage());
   // printItems(indent+"   ",item.getItems());
   // }
   //
   // }

}
