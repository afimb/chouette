/**
 * Projet CHOUETTE
 *
 * ce projet est sous license libre
 * voir LICENSE.txt pour plus de details
 *
 */

package fr.certu.chouette.exchange.gtfs.exporter.producer;

import java.sql.Date;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;

import fr.certu.chouette.exchange.gtfs.exporter.report.GtfsReport;
import fr.certu.chouette.exchange.gtfs.model.GtfsCalendar;
import fr.certu.chouette.exchange.gtfs.model.GtfsCalendarDate;
import fr.certu.chouette.model.neptune.CalendarDay;
import fr.certu.chouette.model.neptune.Period;
import fr.certu.chouette.model.neptune.Timetable;
import fr.certu.chouette.model.neptune.type.DayTypeEnum;

/**
 * convert Timetable to Gtfs Calendar and CalendarDate
 * <p>
 * optimise multiple period timetable with calendarDate inclusion or exclusion
 */
public class GtfsCalendarProducer extends AbstractProducer<GtfsCalendar, Timetable>
{
	private static final Logger logger = Logger.getLogger(GtfsCalendarProducer.class);
	private static final long ONE_DAY=3600000*24;

	@Override
	public GtfsCalendar produce(Timetable timetable,GtfsReport report)
	{
		GtfsCalendar calendar = new GtfsCalendar();

		String serviceId = toGtfsId(timetable.getObjectId());
		calendar.setServiceId(serviceId);

		if (timetable.getPeriods() != null && !timetable.getPeriods().isEmpty())
		{
			for (DayTypeEnum dayType : timetable.getDayTypes())
			{
				switch (dayType)
				{
				case Monday:
					calendar.setMonday(true);
					break;
				case Tuesday:
					calendar.setTuesday(true);
					break;
				case Wednesday:
					calendar.setWednesday(true);
					break;
				case Thursday:
					calendar.setThursday(true);
					break;
				case Friday:
					calendar.setFriday(true);
					break;
				case Saturday:
					calendar.setSaturday(true);
					break;
				case Sunday:
					calendar.setSunday(true);
					break;
				case WeekDay:
					calendar.setMonday(true);
					calendar.setTuesday(true);
					calendar.setWednesday(true);
					calendar.setThursday(true);
					calendar.setFriday(true);
					break;
				case WeekEnd:
					calendar.setSaturday(true);
					calendar.setSunday(true);
					break;
				default: 
					// nothing to do
				}
			}
			if (timetable.getPeriods().size() == 1)
			{
				Period period = timetable.getPeriods().get(0);
				calendar.setStartDate(period.getStartDate());
				calendar.setEndDate(period.getEndDate());
				if (timetable.getCalendarDays() != null && !timetable.getCalendarDays().isEmpty())
				{
					Set<CalendarDay> includedDates = new HashSet<CalendarDay>();
					includedDates.addAll(timetable.getCalendarDays()); 
					addDates(calendar, includedDates);
				}
			}
			else
			{
				// GTFS can't use multiple periods, converted as single dates
				Set<Date> excludedDates = new HashSet<Date>(timetable.getExcludedDates());
				Set<Date> includedDates = new HashSet<Date>(timetable.getPeculiarDates());

				for (Period period : timetable.getPeriods())
				{
					Date checkedDate = period.getStartDate();
					Date endDate = new Date(period.getEndDate().getTime()+ONE_DAY);
					while (checkedDate.before(endDate))
					{
						if (!excludedDates.contains(checkedDate) && !includedDates.contains(checkedDate)) 
						{
							if (checkValidDay(checkedDate, calendar)) 
							{
								includedDates.add(checkedDate);
							}
						}
						checkedDate = new Date(checkedDate.getTime()+ONE_DAY);
					}
				}

				// create only included CalendarDates
				addDates(calendar, includedDates, GtfsCalendarDate.INCLUDED);
			}
		}
		else if (timetable.getCalendarDays() != null && !timetable.getCalendarDays().isEmpty())
		{
			addDates(calendar, timetable.getCalendarDays());
		}
		else
		{
			logger.warn("timetable "+timetable.getObjectId()+" has no period nor calendarDays : rejected");
			return null;
		}

		if (!calendar.getCalendarDates().isEmpty())
		{
			Collections.sort(calendar.getCalendarDates());
		}
		
		return calendar;
	}

	private void addDates(GtfsCalendar calendar, Collection<Date> dates,
			int type) 
	{
		String serviceId = calendar.getServiceId();
		for (Date date : dates)
		{
			GtfsCalendarDate gtfsDate = new GtfsCalendarDate();
			gtfsDate.setCalendar(calendar);
			gtfsDate.setDate(date);
			gtfsDate.setServiceId(serviceId);
			gtfsDate.setExceptionType(type);
			calendar.addCalendarDate(gtfsDate);
		}

	}

	/**
	 * @param calendar
	 * @param addedDates
	 * @param type
	 */
	private void addDates(GtfsCalendar calendar, Collection<CalendarDay> addedDates)
	{
		String serviceId = calendar.getServiceId();
		for (CalendarDay date : addedDates)
		{
			GtfsCalendarDate gtfsDate = new GtfsCalendarDate();
			gtfsDate.setCalendar(calendar);
			gtfsDate.setDate(date.getDate());
			gtfsDate.setServiceId(serviceId);
			gtfsDate.setExceptionType(date.getIncluded()?GtfsCalendarDate.INCLUDED:GtfsCalendarDate.EXCLUDED);
			calendar.addCalendarDate(gtfsDate);
		}
	}

	/**
	 * @param checkedDate
	 * @param dayTypes
	 * @return
	 */
	private boolean checkValidDay(Date checkedDate, GtfsCalendar calendar )
	{
		boolean valid = false;
		// to avoid timezone 
		Calendar c = Calendar.getInstance();
		c.set(Calendar.HOUR_OF_DAY, 12);
		java.util.Date aDate = new java.util.Date(checkedDate.getTime());
		c.setTime(aDate);

		switch (c.get(Calendar.DAY_OF_WEEK))
		{
		case Calendar.MONDAY : 
			if (calendar.isMonday() ) valid = true;
			break;
		case Calendar.TUESDAY : 
			if (calendar.isTuesday() ) valid = true;
			break;
		case Calendar.WEDNESDAY : 
			if (calendar.isWednesday()) valid = true;
			break;
		case Calendar.THURSDAY : 
			if (calendar.isThursday() ) valid = true;
			break;
		case Calendar.FRIDAY : 
			if (calendar.isFriday() ) valid = true;
			break;
		case Calendar.SATURDAY : 
			if (calendar.isSaturday()) valid = true;
			break;
		case Calendar.SUNDAY : 
			if (calendar.isSunday() ) valid = true;
			break;
		}
		return valid;
	}

	@Override
	public List<GtfsCalendar> produceAll(Timetable neptuneObject,GtfsReport report)
	{
		throw new UnsupportedOperationException("not yet implemented");
	}

}
