package fr.certu.chouette.exchange.gtfs.importer.producer;

import java.sql.Date;
import java.util.Calendar;

import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.Assert;
import org.testng.annotations.Test;

import fr.certu.chouette.common.ChouetteException;
import fr.certu.chouette.exchange.gtfs.refactor.model.GtfsCalendar;
import fr.certu.chouette.model.neptune.Period;
import fr.certu.chouette.model.neptune.Timetable;

@ContextConfiguration(locations={"classpath:testContext.xml","classpath*:chouetteContext.xml"})
public class GtfsImportCalendarProducerTests extends AbstractTestNGSpringContextTests
{

	@Test (groups = {"Producers"}, description = "test timetable with period" )
	public void verifyTimetableProducer1() throws ChouetteException 
	{
		Calendar c = Calendar.getInstance();
		c.set(Calendar.YEAR, 2013);
		c.set(Calendar.MONTH, Calendar.JULY);
		c.set(Calendar.DAY_OF_MONTH, 1);
		c.set(Calendar.HOUR_OF_DAY,12);

		AbstractModelProducer.setPrefix("NINOXE");
		TimetableProducer producer = new TimetableProducer();

		GtfsCalendar gtfsObject = new GtfsCalendar();
		Date startDate = new Date(c.getTimeInMillis());
		gtfsObject.setStartDate(startDate);
		c.add(Calendar.MONTH, 6);
		Date endDate = new Date(c.getTimeInMillis());
		gtfsObject.setEndDate(endDate);
		gtfsObject.setServiceId("1");
      gtfsObject.setMonday(true);
      gtfsObject.setTuesday(false);
      gtfsObject.setWednesday(false);
      gtfsObject.setThursday(false);
      gtfsObject.setFriday(false);
      gtfsObject.setSaturday(false);
      gtfsObject.setSunday(false);
		Timetable neptuneObject = producer.produce(gtfsObject, null);
        
		Assert.assertEquals(neptuneObject.getObjectId(),"NINOXE:Timetable:1","timetable id must be correcty set");
		Assert.assertEquals(neptuneObject.getPeriods().size(),1,"timetable must have 1 period");
		Period period = neptuneObject.getPeriods().get(0);
		Assert.assertEquals(period.getStartDate(), startDate, "start date must be correcty set");
		Assert.assertEquals(period.getEndDate(), endDate, "end date must be correcty set");
		Assert.assertEquals(neptuneObject.getIntDayTypes(), Integer.valueOf(4) ,"only Monday must be set");
        gtfsObject.setMonday(false);
        gtfsObject.setTuesday(true);
		neptuneObject = producer.produce(gtfsObject, null);
		Assert.assertEquals(neptuneObject.getIntDayTypes(), Integer.valueOf(8) ,"only Tuesday must be set");
        gtfsObject.setTuesday(false);
        gtfsObject.setWednesday(true);
		neptuneObject = producer.produce(gtfsObject, null);
		Assert.assertEquals(neptuneObject.getIntDayTypes(), Integer.valueOf(16) ,"only Wednesday must be set");
        gtfsObject.setWednesday(false);
        gtfsObject.setThursday(true);
		neptuneObject = producer.produce(gtfsObject, null);
		Assert.assertEquals(neptuneObject.getIntDayTypes(), Integer.valueOf(32) ,"only Thursday must be set");
        gtfsObject.setThursday(false);
        gtfsObject.setFriday(true);
		neptuneObject = producer.produce(gtfsObject, null);
		Assert.assertEquals(neptuneObject.getIntDayTypes(), Integer.valueOf(64) ,"only Friday must be set");
        gtfsObject.setFriday(false);
        gtfsObject.setSaturday(true);
		neptuneObject = producer.produce(gtfsObject, null);
		Assert.assertEquals(neptuneObject.getIntDayTypes(), Integer.valueOf(128) ,"only Saturday must be set");
        gtfsObject.setSaturday(false);
        gtfsObject.setSunday(true);
		neptuneObject = producer.produce(gtfsObject, null);
		Assert.assertEquals(neptuneObject.getIntDayTypes(), Integer.valueOf(256) ,"only Sunday must be set");
		

	}


//	private void printItems(String indent,List<ReportItem> items) 
//	{
//		if (items == null) return;
//		for (ReportItem item : items) 
//		{
//			System.out.println(indent+item.getStatus().name()+" : "+item.getLocalizedMessage());
//			printItems(indent+"   ",item.getItems());
//		}
//
//	}

}
