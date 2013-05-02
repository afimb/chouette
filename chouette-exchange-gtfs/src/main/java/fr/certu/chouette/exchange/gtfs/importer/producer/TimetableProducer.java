package fr.certu.chouette.exchange.gtfs.importer.producer;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.log4j.Logger;

import fr.certu.chouette.exchange.gtfs.model.GtfsCalendar;
import fr.certu.chouette.exchange.gtfs.model.GtfsCalendarDate;
import fr.certu.chouette.model.neptune.Period;
import fr.certu.chouette.model.neptune.Timetable;
import fr.certu.chouette.model.neptune.type.DayTypeEnum;
import fr.certu.chouette.plugin.report.ReportItem;

public class TimetableProducer extends AbstractModelProducer<Timetable, GtfsCalendar> {
	private static Logger logger = Logger.getLogger(TimetableProducer.class);
	
	private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
	
	@Override
	public Timetable produce(GtfsCalendar gtfsCalendar,ReportItem report) 
	{
		Timetable timetable= new Timetable();

		// objectId, objectVersion, creatorId, creationTime
		timetable.setObjectId(composeIncrementalObjectId( Timetable.TIMETABLE_KEY, gtfsCalendar.getServiceId(),logger));


		if (gtfsCalendar.isMonday()) timetable.addDayType(DayTypeEnum.MONDAY);
		if (gtfsCalendar.isTuesday()) timetable.addDayType(DayTypeEnum.TUESDAY);
		if (gtfsCalendar.isWednesday()) timetable.addDayType(DayTypeEnum.WEDNESDAY);
		if (gtfsCalendar.isThursday()) timetable.addDayType(DayTypeEnum.THURSDAY);
		if (gtfsCalendar.isFriday()) timetable.addDayType(DayTypeEnum.FRIDAY);
		if (gtfsCalendar.isSaturday()) timetable.addDayType(DayTypeEnum.SATURDAY);
		if (gtfsCalendar.isSunday()) timetable.addDayType(DayTypeEnum.SUNDAY);

		if (gtfsCalendar.getStartDate() != null && gtfsCalendar.getEndDate() != null)
		{
			Period period = new Period();
			period.setStartDate(gtfsCalendar.getStartDate());
			period.setEndDate(gtfsCalendar.getEndDate());
			timetable.addPeriod(period);
		}
		else
		{
			// logger.info("service without period "+gtfsCalendar.getServiceId());
		}

		if (!gtfsCalendar.getCalendarDates().isEmpty())
		{
			for (GtfsCalendarDate date : gtfsCalendar.getCalendarDates())
			{
				if (date.getExceptionType() == GtfsCalendarDate.INCLUDED)
				{
					timetable.addCalendarDay(date.getDate() );
				}
				else
				{
					Date calendarDay = date.getDate();
					if (timetable.getPeriods() == null || timetable.getPeriods().isEmpty())
					{
						logger.warn("service exclude date without defined period "+gtfsCalendar.getServiceId());
					}
					else
					{
						// logger.info("exclude date from service "+gtfsCalendar.getServiceId());
						boolean found = false;
						Period aNewPeriod = null;
						for (Period period : timetable.getPeriods())
						{
							if (calendarDay.after(period.getStartDate()) && calendarDay.before(period.getEndDate()))
							{
								found = true;
								Calendar cal = Calendar.getInstance();
								cal.setTime(calendarDay);
								aNewPeriod = new Period();
								aNewPeriod.setEndDate(period.getEndDate());
								cal.add(Calendar.DATE, -1);
								period.setEndDate(new Date(cal.getTimeInMillis()));
								cal.add(Calendar.DATE, 2);
								aNewPeriod.setStartDate(new Date(cal.getTimeInMillis()));
							}
							else if (calendarDay.equals(period.getStartDate()))
							{
								found = true;
								Calendar cal = Calendar.getInstance();
								cal.setTime(calendarDay);
								cal.add(Calendar.DATE, 1);
								period.setStartDate(new Date(cal.getTimeInMillis()));
							}
							else if (calendarDay.equals(period.getEndDate()))
							{
								found = true;
								Calendar cal = Calendar.getInstance();
								cal.setTime(calendarDay);
								cal.add(Calendar.DATE, -1);
								period.setEndDate(new Date(cal.getTimeInMillis()));
							}
						}
						if (!found)
						{
							logger.warn("service exclude date within no defined period "+gtfsCalendar.getServiceId());
							logger.warn("   date "+dateFormat.format(calendarDay));
							for (Period period : timetable.getPeriods()) 
							{
								logger.warn("   Period "+period.toString());
							}
						}
						if (aNewPeriod != null)
						{
							timetable.addPeriod(aNewPeriod);
						}
					}
				}
			}
		}
		List<Period> periods = timetable.getPeriods();
		if (periods != null) Collections.sort(periods, new PeriodSorter());
		buildComment(timetable);
		return timetable;
	}

	/**
	 * produce a comment with first date, end date and maybe applicable days
	 * 
	 * @param timetable
	 */
	private void buildComment(Timetable timetable)
	{
		SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy");
		String monday = (timetable.getDayTypes().contains(DayTypeEnum.MONDAY)) ? "Mo":"..";
		String tuesday = (timetable.getDayTypes().contains(DayTypeEnum.TUESDAY)) ? "Tu":"..";
		String wednesday = (timetable.getDayTypes().contains(DayTypeEnum.WEDNESDAY)) ? "We":"..";
		String thursday = (timetable.getDayTypes().contains(DayTypeEnum.THURSDAY)) ? "Th":"..";
		String friday = (timetable.getDayTypes().contains(DayTypeEnum.FRIDAY)) ? "Fr":"..";
		String saturday = (timetable.getDayTypes().contains(DayTypeEnum.SATURDAY)) ? "Sa":"..";
		String sunday = (timetable.getDayTypes().contains(DayTypeEnum.SUNDAY)) ? "Su":"..";

		Date firstDate = null;
		Date lastDate = null;
		if (timetable.getPeriods() != null && !timetable.getPeriods().isEmpty())
		{
			for (Period period : timetable.getPeriods())
			{
				if (firstDate == null || period.getStartDate().before(firstDate)) firstDate = period.getStartDate();
				if (lastDate == null || period.getEndDate().after(lastDate)) lastDate = period.getEndDate();
			}
		}
		if (timetable.getCalendarDays() != null && !timetable.getCalendarDays().isEmpty())
		{
			Calendar cal = Calendar.getInstance();
			for (Date date : timetable.getCalendarDays())
			{
				cal.setTime(date);
				if (cal.get(Calendar.DAY_OF_WEEK) == Calendar.MONDAY) monday = "Mo";
				if (cal.get(Calendar.DAY_OF_WEEK) == Calendar.TUESDAY) tuesday = "Tu";
				if (cal.get(Calendar.DAY_OF_WEEK) == Calendar.WEDNESDAY) wednesday = "We";
				if (cal.get(Calendar.DAY_OF_WEEK) == Calendar.THURSDAY) thursday = "Th";
				if (cal.get(Calendar.DAY_OF_WEEK) == Calendar.FRIDAY) friday = "Fr";
				if (cal.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY) saturday = "Sa";
				if (cal.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) sunday = "Su";
				if (firstDate == null || date.before(firstDate)) firstDate = date;
				if (lastDate == null || date.after(lastDate)) lastDate = date;
			}
		}

		// security if timetable is empty
		if (firstDate != null && lastDate != null)
		{
			String comment = "From " + format.format(firstDate) + " to " + format.format(lastDate)+" : "+monday+tuesday+wednesday+thursday+friday+saturday+sunday;
			timetable.setComment(comment);
		}
		else
		{
			timetable.setComment("Empty timetable");
		}
	}

	private class PeriodSorter implements Comparator<Period>
	{

		@Override
		public int compare(Period o1, Period o2)
		{

			return o1.getStartDate().compareTo(o2.getStartDate());
		}

	}
}
