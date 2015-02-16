package mobi.chouette.common;

import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class DateTimeUtils {

	public static final DateFormat dateTimeFormat = new SimpleDateFormat(
			"dd/MM/yyyy HH:mm:ss");

	public static final DateFormat dateFormat = new SimpleDateFormat(
			"dd/MM/yyyy");

	public static final DateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");

	public static String getDateText(Date date) {
		return dateTimeFormat.format(date);
	}

	public static String getTimeText(Time time) {
		return timeFormat.format(time);
	}

	public static String getDateTimeText(Timestamp date) {
		return dateTimeFormat.format(date);
	}

	public static Date today() {
		return today(null);
	}

	public static Date today(Date date) {
		Calendar calendar = GregorianCalendar.getInstance();
		if (date != null) {
			calendar.setTime(date);
		}
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		return new Date(calendar.getTimeInMillis());
	}

}
