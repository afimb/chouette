package mobi.chouette.exchange.exporter;

import java.sql.Date;
import java.util.Calendar;

import mobi.chouette.common.Constant;
import mobi.chouette.exchange.report.ReportConstant;
import mobi.chouette.model.CalendarDay;
import mobi.chouette.model.Period;
import mobi.chouette.model.Timetable;

import org.testng.Assert;
import org.testng.annotations.Test;

public class DataCollectorTests implements Constant, ReportConstant {

	private Timetable getTimetable() {
		Timetable t = new Timetable();
		t.setObjectId("test:Timetable:1");
		t.setObjectVersion(1);
		t.setComment("test");

		Calendar c = Calendar.getInstance();
		c.add(Calendar.DATE, -3);
		Date startDate = new Date(c.getTimeInMillis());
		c.add(Calendar.DATE, 6);
		Date endDate = new Date(c.getTimeInMillis());
		t.addPeriod(new Period(startDate, endDate));
		c.add(Calendar.DATE, 2);
		t.addCalendarDay(new CalendarDay(new Date(c.getTimeInMillis()), true));
		t.setIntDayTypes(1023);
		t.computeLimitOfPeriods();
		return t;
	}

//	@Test(groups = { "DataCollector" }, description = "Reduce Timetable")
//	public void verifyCheckDatesParameter() throws Exception {
//		DataCollector collector = new DataCollector();
//		Timetable t = getTimetable();
//		Calendar c = Calendar.getInstance();
//		Date boundaryDate = new Date(c.getTimeInMillis());
//		Timetable r = collector.reduceTimetable(t, boundaryDate, true);
//		Assert.assertEquals(r.getPeriods().get(0).getStartDate(),boundaryDate,"calendar period should be reduced on left");
//		Assert.assertEquals(r.getStartOfPeriod(),boundaryDate,"calendar should be reduced on left");
//		Assert.assertEquals(r.getCalendarDays().size(),1, "calendar should have calendarDays");
//		 t = getTimetable();
//		 r = collector.reduceTimetable(t, boundaryDate, false);
//		Assert.assertEquals(r.getPeriods().get(0).getEndDate(),boundaryDate,"calendar period should be reduced on left");
//		Assert.assertEquals(r.getEndOfPeriod(),boundaryDate,"calendar should be reduced on left");
//		Assert.assertEquals(r.getCalendarDays().size(), 0, "calendar should not have calendarDays");
//	}


}