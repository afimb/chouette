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
		switch (calendar.get(Calendar.DAY_OF_WEEK)) 
		{
			case Calendar.MONDAY	:
				return (int)Math.pow(2, DayTypeType.MONDAY.ordinal());
			case Calendar.TUESDAY	:
				return (int)Math.pow(2, DayTypeType.TUESDAY.ordinal());
			case Calendar.WEDNESDAY	:
				return (int)Math.pow(2, DayTypeType.WEDNESDAY.ordinal());
			case Calendar.THURSDAY	:
				return (int)Math.pow(2, DayTypeType.THURSDAY.ordinal());
			case Calendar.FRIDAY	:
				return (int)Math.pow(2, DayTypeType.FRIDAY.ordinal());
			case Calendar.SATURDAY	:
				return (int)Math.pow(2, DayTypeType.SATURDAY.ordinal());
			case Calendar.SUNDAY	:
				return (int)Math.pow(2, DayTypeType.SUNDAY.ordinal());
		}
		return 0;
	}
	
	private static int getOneWeekIntDayType () {
		return	(int)Math.pow(2, DayTypeType.MONDAY.ordinal()) + (int)Math.pow(2, DayTypeType.TUESDAY.ordinal()) +
				(int)Math.pow(2, DayTypeType.WEDNESDAY.ordinal()) + (int)Math.pow(2, DayTypeType.THURSDAY.ordinal()) +
				(int)Math.pow(2, DayTypeType.FRIDAY.ordinal()) + (int)Math.pow(2, DayTypeType.SATURDAY.ordinal()) +
				(int)Math.pow(2, DayTypeType.SUNDAY.ordinal());
	}
}
