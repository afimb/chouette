package mobi.chouette.common;

import java.sql.Time;
import java.util.Calendar;
import java.util.TimeZone;

public class TimeUtil {
	
	public static Time substract(Time thisDeparture, Time firstDeparture) {
		Calendar firstCal = Calendar.getInstance(TimeZone.getDefault());
		firstCal.setTime(firstDeparture);
		Calendar secondCal = Calendar.getInstance(TimeZone.getDefault());
		secondCal.setTime(thisDeparture);
		secondCal.add(Calendar.HOUR_OF_DAY, 0-firstCal.get(Calendar.HOUR_OF_DAY));
		secondCal.add(Calendar.MINUTE, 0-firstCal.get(Calendar.MINUTE));
		secondCal.add(Calendar.SECOND, 0-firstCal.get(Calendar.SECOND));
		return new Time(secondCal.getTime().getTime());
	}

    public static Time valueOf(int numberOfSeconds) {
	String timeStr = "";
	int realH = numberOfSeconds / (60 * 60);
	int h = realH % 24;
	if (h < 10)
	    timeStr += "0"+h+":";
	else
	    timeStr += ""+h+":";
	int m = (numberOfSeconds % (60*60)) / 60;
	if (m < 10)
	    timeStr += "0"+m+":";
	else
	    timeStr += ""+m+":";
	int s = numberOfSeconds % 60;
	if (s < 10)
	    timeStr += "0"+s;
	else
	    timeStr += ""+s;
	return Time.valueOf(timeStr);
    }
}
