package mobi.chouette.common;

import java.sql.Time;
import java.util.Calendar;
import java.util.TimeZone;

public class TimeUtil {
	
	public static Time substract(Time thisDeparture, Time firstDeparture) {
		Calendar firstCal = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
		firstCal.setTime(firstDeparture);
		int firstHour = firstCal.get(Calendar.HOUR_OF_DAY);
		int firstMinute = firstCal.get(Calendar.MINUTE);
		int firstSecond = firstCal.get(Calendar.SECOND);
		Calendar secondCal = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
		secondCal.setTime(thisDeparture);
		int secondHour = secondCal.get(Calendar.HOUR_OF_DAY);
		int secondMinute = secondCal.get(Calendar.MINUTE);
		int secondSecond = secondCal.get(Calendar.SECOND);
		
		long timeInMilliseconds = (
				((secondHour * 60 + secondMinute) * 60 + secondSecond) - 
				((firstHour * 60 + firstMinute) * 60 + firstSecond)
				) * 1000;
		
		return new Time(timeInMilliseconds);
	}
}
