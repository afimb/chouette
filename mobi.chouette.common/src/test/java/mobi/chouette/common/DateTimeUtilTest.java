package mobi.chouette.common;

import java.sql.Date;
import java.sql.Time;
import java.util.Calendar;

import org.testng.Assert;
import org.testng.annotations.Test;

public class DateTimeUtilTest {

	@Test(groups = { "dateTimeUtil" }, description = "dateText")
	public void testDateText() throws Exception {
		Calendar c = Calendar.getInstance();
		
		c.set(Calendar.YEAR, 2015);
		c.set(Calendar.MONTH, Calendar.APRIL);
		c.set(Calendar.DATE, 1);
		c.set(Calendar.HOUR_OF_DAY, 12);

		Date date = new Date(c.getTimeInMillis());
		String dateText = DateTimeUtil.getDateText(date);
		Assert.assertEquals(dateText, "01/04/2015", "date format");

	}
	
	@Test(groups = { "dateTimeUtil" }, description = "timeText")
	public void testTimeText() throws Exception {

		Time t = new Time((2*3600+15*60+20)*1000);
		String timeText = DateTimeUtil.getTimeText(t);
		// Assert.assertEquals(timeText, "03:15:20", "time format");

	}


}
