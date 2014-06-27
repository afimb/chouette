package fr.certu.chouette.exchange.xml.neptune.exporter.producer;

import java.sql.Date;
import java.util.ArrayList;
import java.util.ListIterator;

import org.trident.schema.trident.DayTypeType;
import org.trident.schema.trident.PeriodType;
import org.trident.schema.trident.TimetableType;

import fr.certu.chouette.model.neptune.CalendarDay;
import fr.certu.chouette.model.neptune.Period;
import fr.certu.chouette.model.neptune.Timetable;
import fr.certu.chouette.model.neptune.VehicleJourney;
import fr.certu.chouette.model.neptune.type.DayTypeEnum;

public class TimetableProducer extends AbstractJaxbNeptuneProducer<TimetableType, Timetable> 
{

	private static final long ONE_DAY = 3600000*24;
	
	@Override
	public TimetableType produce(Timetable timetable) 
	{
		TimetableType castorTimetable = tridentFactory.createTimetableType();

		//
		populateFromModel(castorTimetable, timetable);

		castorTimetable.setComment(getNotEmptyString(timetable.getComment()));
		castorTimetable.setVersion(timetable.getVersion());
		ArrayList<Period> periods = new ArrayList<Period>(timetable.getPeriods());

		for(CalendarDay calendarDay : timetable.getCalendarDays())
		{
			if(calendarDay != null)
			{
				if (calendarDay.getIncluded())
				{
					castorTimetable.getCalendarDay().add(toCalendar(calendarDay.getDate()));
				}
				else
				{
					Date aDay = calendarDay.getDate();
					// reduce or split periods around excluded date
					for (ListIterator<Period> iterator = periods.listIterator(); iterator
							.hasNext();) 
					{
						Period period = iterator.next();
						if (period.getStartDate().equals(aDay))
						{
							period.getStartDate().setTime(period.getStartDate().getTime()+ONE_DAY);
							if (period.getStartDate().after(period.getEndDate())) iterator.remove();
						}
						else if (period.getEndDate().equals(aDay))
						{
							period.getEndDate().setTime(period.getEndDate().getTime()+ONE_DAY);							
							if (period.getStartDate().after(period.getEndDate())) iterator.remove();
						}
						else if (period.contains(aDay))
						{
							// split period
							Period before = new Period(period.getStartDate(),new Date(aDay.getTime()-ONE_DAY));
							period.setStartDate(new Date(aDay.getTime()+ONE_DAY));
							iterator.add(before);
						}
						
					}
				}
			}
		}

		for(Period period : periods)
		{
			if(period != null)
			{
				PeriodType castorPeriod = tridentFactory.createPeriodType();
				castorPeriod.setStartOfPeriod(toCalendar(period.getStartDate()));
				castorPeriod.setEndOfPeriod(toCalendar(period.getEndDate()));
				castorTimetable.getPeriod().add(castorPeriod);
			}
		}

		if(timetable.getDayTypes() != null)
		{
			for(DayTypeEnum dayType : timetable.getDayTypes()){
				if(dayType != null)
				{
					try 
					{
						castorTimetable.getDayType().add(DayTypeType.fromValue(dayType.name()));						
					} 
					catch (IllegalArgumentException e) 
					{
						// TODO: handle exception
					}
				}
			}
		}
		if(timetable.getVehicleJourneys() != null)
		{
			for(VehicleJourney vehicleJourney : timetable.getVehicleJourneys()){
				castorTimetable.getVehicleJourneyId().add(getNonEmptyObjectId(vehicleJourney));
			}
		}

		return castorTimetable;
	}

}
