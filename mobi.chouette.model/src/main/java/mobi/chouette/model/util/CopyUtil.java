package mobi.chouette.model.util;

import java.sql.Date;
import java.util.ArrayList;

import mobi.chouette.model.AccessLink;
import mobi.chouette.model.AccessPoint;
import mobi.chouette.model.CalendarDay;
import mobi.chouette.model.Company;
import mobi.chouette.model.ConnectionLink;
import mobi.chouette.model.GroupOfLine;
import mobi.chouette.model.JourneyPattern;
import mobi.chouette.model.Line;
import mobi.chouette.model.Network;
import mobi.chouette.model.Period;
import mobi.chouette.model.Route;
import mobi.chouette.model.StopArea;
import mobi.chouette.model.StopPoint;
import mobi.chouette.model.Timetable;
import mobi.chouette.model.VehicleJourney;


public abstract class CopyUtil {

	public static Network copy(Network object)
	{
		throw new RuntimeException("not implemented");
	}
	
	public static Company copy(Company object)
	{
		throw new RuntimeException("not implemented");
	}
	
	public static GroupOfLine copy(GroupOfLine object)
	{
		throw new RuntimeException("not implemented");
	}
	
	public static StopArea copy(StopArea object)
	{
		throw new RuntimeException("not implemented");
	}
	
	public static AccessPoint copy(AccessPoint object)
	{
		throw new RuntimeException("not implemented");
	}
	
	public static AccessLink copy(AccessLink object)
	{
		throw new RuntimeException("not implemented");
	}
	
	public static ConnectionLink copy(ConnectionLink object)
	{
		throw new RuntimeException("not implemented");
	}
	
	public static Timetable copy(Timetable object)
	{
	      Timetable tm = new Timetable();
	      tm.setObjectId(object.getObjectId());
	      tm.setObjectVersion(object.getObjectVersion());
	      tm.setComment(object.getComment());
	      tm.setVersion(object.getVersion());
	      tm.setIntDayTypes(object.getIntDayTypes());
	      tm.setStartOfPeriod(object.getStartOfPeriod());
	      tm.setEndOfPeriod(object.getEndOfPeriod());
	      tm.setPeriods(new ArrayList<Period>());
	      for (Period period : object.getPeriods())
	      {
	         tm.addPeriod(new Period((Date)(period.getStartDate().clone()),(Date)(period.getEndDate().clone())));
	      }
	      tm.setCalendarDays(new ArrayList<CalendarDay>());
	      for (CalendarDay day : object.getCalendarDays())
	      {
	         tm.addCalendarDay(new CalendarDay((Date)(day.getDate().clone()), day.getIncluded()));
	      }
	      return tm;

	}
	public static StopPoint copy(StopPoint object)
	{
		throw new RuntimeException("not implemented");
	}
	
	public static Line copy(Line object)
	{
		throw new RuntimeException("not implemented");
	}
	
	public static Route copy(Route object)
	{
		throw new RuntimeException("not implemented");
	}
	
	public static JourneyPattern copy(JourneyPattern object)
	{
		throw new RuntimeException("not implemented");
	}
	
	public static VehicleJourney copy(VehicleJourney object)
	{
		throw new RuntimeException("not implemented");
	}
}
