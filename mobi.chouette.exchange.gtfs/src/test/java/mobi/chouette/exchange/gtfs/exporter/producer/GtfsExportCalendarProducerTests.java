package mobi.chouette.exchange.gtfs.exporter.producer;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import mobi.chouette.core.ChouetteException;
import mobi.chouette.exchange.gtfs.exporter.producer.mock.GtfsExporterMock;
import mobi.chouette.exchange.gtfs.model.GtfsCalendar;
import mobi.chouette.exchange.gtfs.model.GtfsCalendarDate;
import mobi.chouette.exchange.gtfs.model.GtfsCalendarDate.ExceptionType;
import mobi.chouette.exchange.gtfs.model.exporter.CalendarDateExporter;
import mobi.chouette.exchange.gtfs.model.exporter.CalendarExporter;
import mobi.chouette.exchange.gtfs.model.importer.Context;
import mobi.chouette.model.CalendarDay;
import mobi.chouette.model.Period;
import mobi.chouette.model.Timetable;
import mobi.chouette.model.type.DayTypeEnum;

import org.testng.Assert;
import org.testng.Reporter;
import org.testng.annotations.Test;

public class GtfsExportCalendarProducerTests 
{

   private GtfsExporterMock mock = new GtfsExporterMock();
   private GtfsServiceProducer producer = new GtfsServiceProducer(mock);
   private Context context = new Context();

   @Test(groups = { "Producers" }, description = "test timetable with period")
   public void verifyCalendarProducer1() throws ChouetteException
   {
      mock.reset();
      Calendar c = Calendar.getInstance();
      c.set(Calendar.YEAR, 2013);
      c.set(Calendar.MONTH, Calendar.JULY);
      c.set(Calendar.DAY_OF_MONTH, 1);
      c.set(Calendar.HOUR_OF_DAY, 12);

      Timetable neptuneObject = new Timetable();
      neptuneObject.setObjectId("GTFS:Timetable:1234");
      neptuneObject.setComment("name");
      neptuneObject.addDayType(DayTypeEnum.Monday);
      neptuneObject.addDayType(DayTypeEnum.Saturday);
      Date startDate = new Date(c.getTimeInMillis());
      c.add(Calendar.DATE, 15);
      Date endDate = new Date(c.getTimeInMillis());
      Period period = new Period(startDate, endDate);
      neptuneObject.addPeriod(period);

      List<Timetable> tms = new ArrayList<>();
      tms.add(neptuneObject);
      producer.save(tms,  "GTFS",false);
      Reporter.log("verifyCalendarProducer1");
      Assert.assertEquals(mock.getExportedCalendars().size(),1,"Calendar must be returned");
      GtfsCalendar gtfsObject = mock.getExportedCalendars().get(0);
      Reporter.log(CalendarExporter.CONVERTER.to(context, gtfsObject));

      Assert.assertEquals(gtfsObject.getServiceId(), toGtfsId(neptuneObject.getObjectId()), "timetable id must be correcty set");
      Assert.assertEquals(gtfsObject.getStartDate(), startDate, "start date must be correcty set");
      Assert.assertEquals(gtfsObject.getEndDate(), endDate, "end date must be correcty set");
      Assert.assertTrue(gtfsObject.getMonday(), "monday must be true");
      Assert.assertFalse(gtfsObject.getTuesday(), "tuesday must be false");
      Assert.assertFalse(gtfsObject.getWednesday(), "wednesday must be false");
      Assert.assertFalse(gtfsObject.getThursday(), "thursday must be false");
      Assert.assertFalse(gtfsObject.getFriday(), "friday must be false");
      Assert.assertTrue(gtfsObject.getSaturday(), "saturday must be true");
      Assert.assertFalse(gtfsObject.getSunday(), "sunday must be false");

   }

   @Test(groups = { "Producers" }, description = "test timetable with dates")
   public void verifyCalendarProducer2() throws ChouetteException
   {
      mock.reset();
      Calendar c = Calendar.getInstance();
      c.set(Calendar.YEAR, 2013);
      c.set(Calendar.MONTH, Calendar.JULY);
      c.set(Calendar.DAY_OF_MONTH, 1);
      c.set(Calendar.HOUR_OF_DAY, 12);

      Timetable neptuneObject = new Timetable();
      neptuneObject.setObjectId("GTFS:Timetable:1234");
      neptuneObject.setComment("name");
      for (int i = 0; i < 5; i++)
      {
         Date date = new Date(c.getTimeInMillis());
         neptuneObject.addCalendarDay(new CalendarDay(date, true));
         c.add(Calendar.DATE, 3);
      }
      Reporter.log(neptuneObject.toString());

      List<Timetable> tms = new ArrayList<>();
      tms.add(neptuneObject);
      producer.save(tms,  "GTFS",false);
      Reporter.log("verifyCalendarProducer2");

      Assert.assertEquals(mock.getExportedCalendars().size(), 0, "no calendar produced");

      Assert.assertEquals(mock.getExportedCalendarDates().size(), 5, "calendar must have 5 dates");
      c.set(Calendar.YEAR, 2013);
      c.set(Calendar.MONTH, Calendar.JULY);
      c.set(Calendar.DAY_OF_MONTH, 1);
      for (GtfsCalendarDate gtfsCalendarDate : mock.getExportedCalendarDates())
      {
         Reporter.log(CalendarDateExporter.CONVERTER.to(context,gtfsCalendarDate));
         Date date = new Date(c.getTimeInMillis());
         c.add(Calendar.DATE, 3);
         Assert.assertEquals(gtfsCalendarDate.getServiceId(), toGtfsId(neptuneObject.getObjectId()), "service id must be correcty set");
         Assert.assertEquals(gtfsCalendarDate.getDate(), date, "calendar date must be correctly");
         Assert.assertEquals(gtfsCalendarDate.getExceptionType(), ExceptionType.Added, "calendar date must be inclusive");
      }

   }

   @Test(groups = { "Producers" }, description = "test timetable with period and dates")
   public void verifyCalendarProducer3() throws ChouetteException
   {
      mock.reset();
      Calendar c = Calendar.getInstance();
      c.set(Calendar.YEAR, 2013);
      c.set(Calendar.MONTH, Calendar.JULY);
      c.set(Calendar.DAY_OF_MONTH, 1);
      c.set(Calendar.HOUR_OF_DAY, 12);

      Timetable neptuneObject = new Timetable();
      neptuneObject.setObjectId("GTFS:Timetable:1234");
      neptuneObject.setComment("name");
      neptuneObject.addDayType(DayTypeEnum.Tuesday);
      neptuneObject.addDayType(DayTypeEnum.Wednesday);
      neptuneObject.addDayType(DayTypeEnum.Thursday);
      neptuneObject.addDayType(DayTypeEnum.Friday);
      neptuneObject.addDayType(DayTypeEnum.Sunday);
      Date startDate = new Date(c.getTimeInMillis());
      c.add(Calendar.DATE, 15);
      Date endDate = new Date(c.getTimeInMillis());
      Period period = new Period(startDate, endDate);
      neptuneObject.addPeriod(period);
      c.add(Calendar.DATE, 15);
      for (int i = 0; i < 5; i++)
      {
         Date date = new Date(c.getTimeInMillis());
         neptuneObject.addCalendarDay(new CalendarDay(date, true));
         c.add(Calendar.DATE, 3);
      }

      List<Timetable> tms = new ArrayList<>();
      tms.add(neptuneObject);
      producer.save(tms,  "GTFS",false);
      Reporter.log("verifyCalendarProducer3");
      Assert.assertEquals(mock.getExportedCalendars().size(),1,"Calendar must be returned");
      GtfsCalendar gtfsObject = mock.getExportedCalendars().get(0);
      Reporter.log(CalendarExporter.CONVERTER.to(context, gtfsObject));

      Assert.assertEquals(gtfsObject.getServiceId(), toGtfsId(neptuneObject.getObjectId()), "service id must be correcty set");
      Assert.assertEquals(gtfsObject.getStartDate(), startDate, "start date must be correcty set");
      Assert.assertEquals(gtfsObject.getEndDate(), endDate, "end date must be correcty set");
      Assert.assertFalse(gtfsObject.getMonday(), "monday must be false");
      Assert.assertTrue(gtfsObject.getTuesday(), "tuesday must be true");
      Assert.assertTrue(gtfsObject.getWednesday(), "wednesday must be true");
      Assert.assertTrue(gtfsObject.getThursday(), "thursday must be true");
      Assert.assertTrue(gtfsObject.getFriday(), "friday must be true");
      Assert.assertFalse(gtfsObject.getSaturday(), "saturday must be false");
      Assert.assertTrue(gtfsObject.getSunday(), "sunday must be true");
      Assert.assertEquals(mock.getExportedCalendarDates().size(), 5, "calendar must have 5 dates");
      c.set(Calendar.YEAR, 2013);
      c.set(Calendar.MONTH, Calendar.JULY);
      c.set(Calendar.DAY_OF_MONTH, 1);
      c.add(Calendar.DATE, 30);
      for (GtfsCalendarDate gtfsCalendarDate : mock.getExportedCalendarDates())
      {
         Reporter.log(CalendarDateExporter.CONVERTER.to(context,gtfsCalendarDate));
         Date date = new Date(c.getTimeInMillis());
         c.add(Calendar.DATE, 3);
         Assert.assertEquals(gtfsCalendarDate.getServiceId(), toGtfsId(neptuneObject.getObjectId()), "service id must be correcty set");
         Assert.assertEquals(gtfsCalendarDate.getDate(), date, "calendar date must be correctly");
         Assert.assertEquals(gtfsCalendarDate.getExceptionType(), ExceptionType.Added, "calendar date must be inclusive");
      }

   }

   @Test(groups = { "Producers" }, description = "test timetable with 2 periods")
   public void verifyCalendarProducer4() throws ChouetteException
   {
      mock.reset();
      Calendar c = Calendar.getInstance();
      c.set(Calendar.YEAR, 2013);
      c.set(Calendar.MONTH, Calendar.JULY);
      c.set(Calendar.DAY_OF_MONTH, 1);
      c.set(Calendar.HOUR_OF_DAY, 12);

      Timetable neptuneObject = new Timetable();
      neptuneObject.setObjectId("GTFS:Timetable:1234");
      neptuneObject.setComment("name");
      neptuneObject.addDayType(DayTypeEnum.Monday);
      neptuneObject.addDayType(DayTypeEnum.Tuesday);
      neptuneObject.addDayType(DayTypeEnum.Thursday);
      neptuneObject.addDayType(DayTypeEnum.Friday);
      neptuneObject.addDayType(DayTypeEnum.Saturday);
      neptuneObject.addDayType(DayTypeEnum.Sunday);
      Date startDate1 = new Date(c.getTimeInMillis());
      c.add(Calendar.DATE, 15);
      Date endDate1 = new Date(c.getTimeInMillis());
      Period period1 = new Period(startDate1, endDate1);
      neptuneObject.addPeriod(period1);
      c.add(Calendar.DATE, 60);
      Date startDate2 = new Date(c.getTimeInMillis());
      c.add(Calendar.DATE, 15);
      Date endDate2 = new Date(c.getTimeInMillis());
      Period period2 = new Period(startDate2, endDate2);
      neptuneObject.addPeriod(period2);

      List<Timetable> tms = new ArrayList<>();
      tms.add(neptuneObject);
      producer.save(tms,  "GTFS",false);
      Reporter.log("verifyCalendarProducer4");

      Assert.assertEquals(mock.getExportedCalendars().size(), 0, "no calendar produced");

      Assert.assertEquals(mock.getExportedCalendarDates().size(), 28, "calendar must have 28 dates");
      c.set(Calendar.YEAR, 2013);
      c.set(Calendar.MONTH, Calendar.JULY);
      c.set(Calendar.DAY_OF_MONTH, 1);
      if (c.get(Calendar.DAY_OF_WEEK) == Calendar.WEDNESDAY)
         c.add(Calendar.DATE, 1);
      int cpt = 0;
      for (GtfsCalendarDate gtfsCalendarDate : mock.getExportedCalendarDates())
      {
         Reporter.log(CalendarDateExporter.CONVERTER.to(context,gtfsCalendarDate));
         Date date = new Date(c.getTimeInMillis());
         cpt++;
         if (cpt == 14)
         {
            c.add(Calendar.DATE, 59);
         }
         c.add(Calendar.DATE, 1);
         if (c.get(Calendar.DAY_OF_WEEK) == Calendar.WEDNESDAY)
            c.add(Calendar.DATE, 1);
         Assert.assertEquals(gtfsCalendarDate.getServiceId(), toGtfsId(neptuneObject.getObjectId()), "service id must be correcty set");
         Assert.assertEquals(gtfsCalendarDate.getDate(), date, "calendar date must be correctly");
         Assert.assertEquals(gtfsCalendarDate.getExceptionType(), ExceptionType.Added, "calendar date must be inclusive");
      }

   }

   protected String toGtfsId(String neptuneId)
   {
      String[] tokens = neptuneId.split(":");
      return tokens[2];
   }

}
