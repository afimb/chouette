package fr.certu.chouette.util;

import chouette.schema.types.DayTypeType;
import java.util.Calendar;
import java.util.Date;

public final class TableauMarcheUtils {
	
	public static int ONE_WEEK_INT_DAY_TYPE = 0;
	
	static {
		ONE_WEEK_INT_DAY_TYPE = getOneWeekIntDayType();
	}
	
	public static int getIntervalIntDayType (final Date intervalStartDay, final Date intervalEndDay) {
		if (intervalStartDay == null)
			throw new NullPointerException ("LA DATE DE DEBUT D'INTERVAL NE PEUT ETRE EGAL A / null");
		if (intervalEndDay == null)
			return ONE_WEEK_INT_DAY_TYPE;
		int periodDaysType = 0;
		Date cursor = intervalStartDay;
		int cursorIteration = 0;
		do {
			periodDaysType += getIntDayType(cursor);
			cursor = DateUtils.getDayIncreasedByOneDay(cursor);
			cursorIteration ++;
		} while (!cursor.after(intervalEndDay) && cursorIteration < 7);
		return periodDaysType;
	}
	
	public static int getIntDayType (final Date day) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(day);
		switch (calendar.get(Calendar.DAY_OF_WEEK)) {
		case Calendar.MONDAY	:
			return (int)Math.pow(2, DayTypeType.MONDAY_TYPE);
		case Calendar.TUESDAY	:
			return (int)Math.pow(2, DayTypeType.TUESDAY_TYPE);
		case Calendar.WEDNESDAY	:
			return (int)Math.pow(2, DayTypeType.WEDNESDAY_TYPE);
		case Calendar.THURSDAY	:
			return (int)Math.pow(2, DayTypeType.THURSDAY_TYPE);
		case Calendar.FRIDAY	:
			return (int)Math.pow(2, DayTypeType.FRIDAY_TYPE);
		case Calendar.SATURDAY	:
			return (int)Math.pow(2, DayTypeType.SATURDAY_TYPE);
		case Calendar.SUNDAY	:
			return (int)Math.pow(2, DayTypeType.SUNDAY_TYPE);
		}
		return 0;
	}
	
	private static int getOneWeekIntDayType () {
		return	(int)Math.pow(2, DayTypeType.MONDAY_TYPE) + (int)Math.pow(2, DayTypeType.TUESDAY_TYPE) +
				(int)Math.pow(2, DayTypeType.WEDNESDAY_TYPE) + (int)Math.pow(2, DayTypeType.THURSDAY_TYPE) +
				(int)Math.pow(2, DayTypeType.FRIDAY_TYPE) + (int)Math.pow(2, DayTypeType.SATURDAY_TYPE) +
				(int)Math.pow(2, DayTypeType.SUNDAY_TYPE);
	}
}
