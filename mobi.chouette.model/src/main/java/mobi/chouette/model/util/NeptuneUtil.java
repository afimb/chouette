package mobi.chouette.model.util;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import mobi.chouette.model.CalendarDay;
import mobi.chouette.model.NeptuneIdentifiedObject;
import mobi.chouette.model.NeptuneLocalizedObject;
import mobi.chouette.model.NeptuneObject;
import mobi.chouette.model.Period;
import mobi.chouette.model.Route;
import mobi.chouette.model.StopArea;
import mobi.chouette.model.StopPoint;
import mobi.chouette.model.Timetable;
import mobi.chouette.model.type.DayTypeEnum;

public abstract class NeptuneUtil {

	/**
	 * Build a list of Neptune Ids (ObjectId) from a list of Neptune Objects
	 * 
	 * @param neptuneObjects
	 *            the list to parse
	 * @return the object ids list
	 */
	public static List<String> extractObjectIds(List<? extends NeptuneIdentifiedObject> neptuneObjects) {
		List<String> objectIds = new ArrayList<String>();
		if (neptuneObjects != null) {
			for (NeptuneIdentifiedObject neptuneObject : neptuneObjects) {
				if (neptuneObject != null) {
					String objectId = neptuneObject.getObjectId();
					if (objectId != null) {
						objectIds.add(objectId);
					}
				}
			}
		}

		return objectIds;
	}

	/**
	 * Build a map of objectIds (Id) from a list of Neptune Identified Objects
	 * 
	 * @param neptuneObjects
	 *            the list to parse
	 * @return the ids map
	 */
	public static <T extends NeptuneIdentifiedObject> Map<String, T> mapOnObjectIds(List<T> neptuneObjects) {
		Map<String, T> map = new HashMap<String, T>();
		if (neptuneObjects != null) {
			for (T neptuneObject : neptuneObjects) {
				if (neptuneObject != null) {
					String id = neptuneObject.getObjectId();
					if (id != null) {
						map.put(id, neptuneObject);
					}
				}
			}
		}
		return map;
	}

	/**
	 * Build a list of internal Ids (Id) from a list of Neptune Objects
	 * 
	 * @param neptuneObjects
	 *            the list to parse
	 * @return the ids list
	 */
	public static List<Long> extractIds(List<? extends NeptuneObject> neptuneObjects) {
		List<Long> ids = new ArrayList<Long>();
		if (neptuneObjects != null) {
			for (NeptuneObject neptuneObject : neptuneObjects) {
				if (neptuneObject != null) {
					Long id = neptuneObject.getId();
					if (id != null) {
						ids.add(id);
					}
				}
			}
		}

		return ids;
	}

	/**
	 * Build a map of internal Ids (Id) from a list of Neptune Objects
	 * 
	 * @param neptuneObjects
	 *            the list to parse
	 * @return the ids map
	 */
	public static <T extends NeptuneObject> Map<Long, T> mapOnIds(List<T> neptuneObjects) {
		Map<Long, T> map = new HashMap<Long, T>();
		if (neptuneObjects != null) {
			for (T neptuneObject : neptuneObjects) {
				if (neptuneObject != null) {
					Long id = neptuneObject.getId();
					if (id != null) {
						map.put(id, neptuneObject);
					}
				}
			}
		}
		return map;
	}

	/**
	 * project latitude and longitude on x and y if not already set<br/>
	 * clears projection if no projection is given
	 * 
	 * @param projectionType
	 *            type of projection (EPSG:xxx)
	 */
	public static void toProjection(NeptuneLocalizedObject object, String projectionType) {
		if (!object.hasCoordinates())
			return;

		String projection = null;
		if (projectionType == null || projectionType.isEmpty()) {
			object.setX(null);
			object.setY(null);
			object.setProjectionType(null);
			return;
		}
		if (object.hasProjection())
			return;
		projection = projectionType.toUpperCase();

		Coordinate p = new Coordinate(object.getLongitude(), object.getLatitude());
		Coordinate coordinate = CoordinateUtil.transform(Coordinate.WGS84, projection, p);
		if (coordinate != null) {
			object.setX(coordinate.x);
			object.setY(coordinate.y);
			object.setProjectionType(projection);
		}
	}

	public static List<StopArea> getStopAreaOfRoute(Route route) {
		ArrayList<StopArea> areas = new ArrayList<>();
		ArrayList<StopPoint> points = new ArrayList<>(route.getStopPoints());
		Collections.sort(points, new Comparator<StopPoint>() {

			@Override
			public int compare(StopPoint arg0, StopPoint arg1) {
				// TODO Auto-generated method stub
				return arg0.getPosition().intValue() - arg1.getPosition().intValue();
			}
		});
		for (StopPoint point : points) {
			areas.add(point.getContainedInStopArea());
		}
		return areas;
	}

	public static String changePrefix(String objectId, String prefix) {
		String[] tokens = objectId.split(":");
		return prefix + ":" + tokens[1] + ":" + tokens[2];
	}

	
	/**
	 * build a bitwise dayType mask for filtering
	 * 
	 * @param dayTypes
	 *            a list of included day types
	 * @return binary mask for selected day types
	 */
	public static int buildDayTypeMask(List<DayTypeEnum> dayTypes) {
		int value = 0;
		if (dayTypes == null)
			return value;
		for (DayTypeEnum dayType : dayTypes) {
			value += buildDayTypeMask(dayType);
		}
		return value;
	}

	/**
	 * build a bitwise dayType mask for filtering
	 * 
	 * @param dayType
	 *            the dayType to filter
	 * @return binary mask for a day type
	 */
	public static int buildDayTypeMask(DayTypeEnum dayType) {
		return (int) Math.pow(2, dayType.ordinal());
	}

	/**
	 * get peculiar dates
	 * 
	 * @return a list of active dates
	 */
	public static List<Date> getPeculiarDates(Timetable t) {
		List<Date> ret = new ArrayList<>();
		for (CalendarDay day : t.getCalendarDays()) {
			if (day.getIncluded())
				ret.add(day.getDate());
		}
		return ret;
	}

	/**
	 * get excluded dates
	 * 
	 * @return a list of excluded dates
	 */
	public static List<Date> getExcludedDates(Timetable t) {
		List<Date> ret = new ArrayList<>();
		for (CalendarDay day : t.getCalendarDays()) {
			if (!day.getIncluded())
				ret.add(day.getDate());
		}
		return ret;
	}

	/**
	 * check if a Timetable is active on a given date
	 * 
	 * @param aDay
	 * @return true if timetable is active on given date
	 */
	public static boolean isActiveOn(Timetable t,Date aDay) {
		if (t.getCalendarDays() != null) {
			CalendarDay includedDay = new CalendarDay(aDay, true);
			if (t.getCalendarDays().contains(includedDay))
				return true;
			CalendarDay excludedDay = new CalendarDay(aDay, false);
			if (t.getCalendarDays().contains(excludedDay))
				return false;
		}
		if (t.getIntDayTypes() != null && t.getIntDayTypes().intValue() != 0 && t.getPeriods() != null) {
			Calendar c = Calendar.getInstance();
			c.setTime(aDay);

			int aDayOfWeek = c.get(Calendar.DAY_OF_WEEK) - 1; // zero on sunday
			int aDayOfWeekFlag = buildDayTypeMask(Timetable.dayTypeByInt[aDayOfWeek]);
			if ((t.getIntDayTypes() & aDayOfWeekFlag) == aDayOfWeekFlag) {
				// check if day is in a period
				for (Period period : t.getPeriods()) {
					if (period.contains(aDay))
						return true;
				}
			}

		}
		return false;
	}

	/**
	 * calculate startOfPeriod and endOfPeriod form dates and periods
	 */
	public static  void computeLimitOfPeriods(Timetable t) {
		Date startOfPeriod = null;
		Date endOfPeriod = null;
		for (Period period : t.getPeriods()) {
			if (startOfPeriod == null || startOfPeriod.after(period.getStartDate())) {
				startOfPeriod = period.getStartDate();
			}
			if (endOfPeriod == null || endOfPeriod.before(period.getEndDate())) {
				endOfPeriod = period.getEndDate();
			}
		}
		// check DayType
		Calendar c = Calendar.getInstance();
		if (startOfPeriod != null && endOfPeriod != null) {
			while (startOfPeriod.before(endOfPeriod) && !isActiveOn(t,startOfPeriod)) {
				c.setTime(startOfPeriod);
				c.add(Calendar.DATE, 1);
				startOfPeriod.setTime(c.getTimeInMillis());
			}
			while (endOfPeriod.after(startOfPeriod) && !isActiveOn(t,endOfPeriod)) {
				c.setTime(endOfPeriod);
				c.add(Calendar.DATE, -1);
				endOfPeriod.setTime(c.getTimeInMillis());
			}
		}
		for (CalendarDay calendarDay : t.getCalendarDays()) {
			Date date = calendarDay.getDate();
			if (calendarDay.getIncluded()) {
				if (startOfPeriod == null || date.before(startOfPeriod))
					startOfPeriod = date;
				if (endOfPeriod == null || date.after(endOfPeriod))
					endOfPeriod = date;
			}
		}
		t.setStartOfPeriod(startOfPeriod);
		t.setEndOfPeriod(endOfPeriod);

	}

	/**
	 * return periods broken on excluded dates, for exports without date
	 * exclusion
	 * 
	 * @return periods
	 */
	public static  List<Period> getEffectivePeriods(Timetable t) {
		List<Date> dates = getExcludedDates(t);
		List<Period> effectivePeriods = new ArrayList<Period>();
		// copy periods
		for (Period period : t.getPeriods()) {
			effectivePeriods.add(new Period(period.getStartDate(), period.getEndDate()));
		}
		if (!effectivePeriods.isEmpty()) {
			for (Date aDay : dates) {
				// reduce or split periods around excluded date
				for (ListIterator<Period> iterator = effectivePeriods.listIterator(); iterator.hasNext();) {
					Period period = iterator.next();
					if (period.getStartDate().equals(aDay)) {
						period.getStartDate().setTime(period.getStartDate().getTime() + Timetable.ONE_DAY);
						if (period.getStartDate().after(period.getEndDate()))
							iterator.remove();
					} else if (period.getEndDate().equals(aDay)) {
						period.getEndDate().setTime(period.getEndDate().getTime() + Timetable.ONE_DAY);
						if (period.getStartDate().after(period.getEndDate()))
							iterator.remove();
					} else if (period.contains(aDay)) {
						// split period
						Period before = new Period(period.getStartDate(), new Date(aDay.getTime() - Timetable.ONE_DAY));
						period.setStartDate(new Date(aDay.getTime() + Timetable.ONE_DAY));
						iterator.add(before);
					}

				}
			}
		}
		Collections.sort(effectivePeriods);
		return effectivePeriods;
	}

}
