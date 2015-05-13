package mobi.chouette.exchange.gtfs.exporter;

import java.sql.Date;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import lombok.extern.log4j.Log4j;
import mobi.chouette.model.CalendarDay;
import mobi.chouette.model.ConnectionLink;
import mobi.chouette.model.JourneyPattern;
import mobi.chouette.model.Line;
import mobi.chouette.model.Period;
import mobi.chouette.model.Route;
import mobi.chouette.model.StopArea;
import mobi.chouette.model.StopPoint;
import mobi.chouette.model.Timetable;
import mobi.chouette.model.VehicleJourney;
import mobi.chouette.model.type.ChouetteAreaEnum;
import mobi.chouette.model.util.CopyUtil;
import mobi.chouette.model.util.NeptuneUtil;

@Log4j
public class GtfsDataCollector 
{
	public boolean collect(ExportableData collection, Line line, Date startDate, Date endDate)
	{
		boolean validLine = false;
		collection.setLine(null);
		collection.getRoutes().clear();
		collection.getJourneyPatterns().clear();
		collection.getStopPoints().clear();
		collection.getVehicleJourneys().clear();
		for (Route route : line.getRoutes()) 
		{
			boolean validRoute = false;
			for (JourneyPattern jp : route.getJourneyPatterns())
			{
				boolean validJourneyPattern = false;
				for (VehicleJourney vehicleJourney : jp.getVehicleJourneys())
				{
					if (startDate == null && endDate == null)
					{
						if (vehicleJourney.getTimetables() != null)
						{
							if (vehicleJourney.getRoute().getStopPoints().isEmpty())
							{
								log.error("route "+vehicleJourney.getRoute().getObjectId()+" has no stopPoints ");
							}
							else
							{
								collection.getTimetables().addAll(vehicleJourney.getTimetables());
								collection.getVehicleJourneys().add(vehicleJourney);
								validJourneyPattern = true;
								validRoute = true;
								validLine = true;
							}
						}
					}
					else
					{
						boolean isValid = false;
						for (Timetable timetable : vehicleJourney.getTimetables())
						{
							Timetable validTimetable = collection.findTimetable(timetable.getObjectId());
							if (validTimetable != null)
							{
								validTimetable.getVehicleJourneys().add(vehicleJourney);
								isValid = true;
							}
							else
							{
								validTimetable = timetable;
								if (startDate != null)
									validTimetable = reduceTimetable(timetable, startDate, true);
								if (validTimetable != null && endDate != null)
									validTimetable = reduceTimetable(validTimetable, endDate, false);
								if (validTimetable != null)
								{
									validTimetable.getVehicleJourneys().add(vehicleJourney);
									collection.getTimetables().add(validTimetable);
									isValid = true;
								}
							}
						}
						if (isValid)
						{
							collection.getVehicleJourneys().add(vehicleJourney);
							if (vehicleJourney.getCompany() != null)
							{
								collection.getCompanies().add(vehicleJourney.getCompany());
							}
							validJourneyPattern = true;
							validRoute = true;
							validLine = true;
						}
					}
				} // end vehiclejourney loop
				if (validJourneyPattern) collection.getJourneyPatterns().add(jp);
			}// end journeyPattern loop
			if (validRoute) 
			{
				collection.getRoutes().add(route);
				collection.getStopPoints().addAll(route.getStopPoints());
				for (StopPoint stopPoint : route.getStopPoints()) 
				{
					collectStopAreas(collection,stopPoint.getContainedInStopArea());
				}
			}
		}// end route loop
		if (validLine)
		{
			collection.setLine(line);
			collection.setNetwork(line.getNetwork());
			if (line.getCompany() != null)
			{
				collection.getCompanies().add(line.getCompany());
			}
		}
		completeSharedData(collection);
		return validLine;
	}

	public boolean collect(ExportableData collection, Collection<StopArea> stopAreas)
	{
		for (StopArea stopArea : stopAreas) {
			collectStopAreas(collection,stopArea);
		}
		completeSharedData(collection);
		return !collection.getPhysicalStops().isEmpty();

	}
	
	private void completeSharedData(ExportableData collection)
	{
		// force lazy dependencies to be loaded
		for (ConnectionLink link : collection.getConnectionLinks()) {
			collection.getSharedStops().add(link.getEndOfLink());
			collection.getSharedStops().add(link.getStartOfLink());
		}
	}

	private void collectStopAreas(ExportableData collection,StopArea stopArea)
	{
		if (stopArea.getAreaType().equals(ChouetteAreaEnum.BoardingPosition) ||
				stopArea.getAreaType().equals(ChouetteAreaEnum.Quay))
		{
			if (collection.getPhysicalStops().contains(stopArea)) return;
			collection.getPhysicalStops().add(stopArea);
			collection.getConnectionLinks().addAll(stopArea.getConnectionStartLinks());
			collection.getConnectionLinks().addAll(stopArea.getConnectionEndLinks());
			if (stopArea.getParent() != null) 
				collectStopAreas(collection,stopArea.getParent());
		}
		else if (stopArea.getAreaType().equals(ChouetteAreaEnum.CommercialStopPoint) )
		{
			if (collection.getCommercialStops().contains(stopArea)) return;
			collection.getCommercialStops().add(stopArea);
			collection.getConnectionLinks().addAll(stopArea.getConnectionStartLinks());
			collection.getConnectionLinks().addAll(stopArea.getConnectionEndLinks());
		}
	}

	/**
	 * produce a timetable reduced to a date
	 * 
	 * @param timetable
	 *           original timetable
	 * @param boundaryDate
	 *           boundary date
	 * @param before
	 *           true to eliminate before boundary date , false otherwise
	 * @return a copy reduced to date or null if reduced to nothing
	 */
	private Timetable reduceTimetable(Timetable timetable, Date boundaryDate, boolean before)
	{
		Timetable reduced = CopyUtil.copy(timetable);
		// reduced.getVehicleJourneys().addAll(timetable.getVehicleJourneys());
		
		List<CalendarDay> dates = reduced.getCalendarDays();
		for (Iterator<CalendarDay> iterator = dates.iterator(); iterator.hasNext();)
		{
			CalendarDay date = iterator.next();
			if (date == null)
			{
				iterator.remove();
			}
			else if (checkDate(date, boundaryDate, before))
			{
				iterator.remove();
			}
		}
		List<Period> periods = reduced.getPeriods();
		for (Iterator<Period> iterator = periods.iterator(); iterator.hasNext();)
		{
			Period period = iterator.next();
			if (checkPeriod(period, boundaryDate, before))
			{
				iterator.remove();
			}
			else
			{
				shortenPeriod(period, boundaryDate, before);
			}
		}
		if (dates.isEmpty() && periods.isEmpty())
		{
			return null;
		}
		NeptuneUtil.computeLimitOfPeriods(reduced);
		return reduced;

	}

	/**
	 * check period if partially out of bounds and reduce it to bounds
	 * 
	 * @param period
	 * @param boundaryDate
	 * @param before
	 * @return true if period has been modified
	 */
	private boolean shortenPeriod(Period period, Date boundaryDate, boolean before)
	{
		boolean ret = false;
		if (before && period.getStartDate().before(boundaryDate))
		{
			ret = true;
			period.setStartDate(boundaryDate);
		}
		if (!before && period.getEndDate().after(boundaryDate))
		{
			ret = true;
			period.setEndDate(boundaryDate);
		}
		return ret;
	}

	/**
	 * check if period is totally out of bounds
	 * 
	 * @param period
	 * @param boundaryDate
	 * @param before
	 * @return
	 */
	private boolean checkPeriod(Period period, Date boundaryDate, boolean before)
	{
		if (before)
		{
			return period.getEndDate().before(boundaryDate);
		}
		return period.getStartDate().after(boundaryDate);
	}

	/**
	 * check if date is out of bounds
	 * 
	 * @param date
	 * @param boundaryDate
	 * @param before
	 * @return
	 */
	private boolean checkDate(CalendarDay date, Date boundaryDate, boolean before)
	{
		if (before)
		{
			return date.getDate().before(boundaryDate);
		}
		return date.getDate().after(boundaryDate);
	}

}
