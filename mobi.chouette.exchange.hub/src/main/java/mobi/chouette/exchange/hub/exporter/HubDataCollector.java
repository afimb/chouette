package mobi.chouette.exchange.hub.exporter;

import java.sql.Date;
import java.util.ArrayList;
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
import mobi.chouette.model.type.DayTypeEnum;
import mobi.chouette.model.util.NeptuneUtil;

@Log4j
public class HubDataCollector {
	public boolean collect(ExportableData collection, Line line, Date startDate, Date endDate) {
		boolean validLine = false;
		collection.setLine(null);
		collection.setPmrFootenoteId(-1);
		collection.getRoutes().clear();
		collection.getJourneyPatterns().clear();
		collection.getStopPoints().clear();
		collection.getVehicleJourneys().clear();
		if (isTrue(line.getMobilityRestrictedSuitable()))
			collection.setPmrFootenoteId(0);
		
		for (Route route : line.getRoutes()) {
			boolean validRoute = false;
			for (JourneyPattern jp : route.getJourneyPatterns()) {
				boolean validJourneyPattern = false;
				for (VehicleJourney vehicleJourney : jp.getVehicleJourneys()) {
					if (startDate == null && endDate == null) {
						if (vehicleJourney.getTimetables() != null) {
							if (vehicleJourney.getRoute().getStopPoints().isEmpty()) {
								log.error("route " + vehicleJourney.getRoute().getObjectId() + " has no stopPoints ");
							} else {
								collection.getTimetables().addAll(vehicleJourney.getTimetables());
								collection.getVehicleJourneys().add(vehicleJourney);
								validJourneyPattern = true;
								validRoute = true;
								validLine = true;
								if (isTrue(vehicleJourney.getMobilityRestrictedSuitability()))
									collection.setPmrFootenoteId(0);
							}
						}
					} else {
						boolean isValid = false;
						for (Timetable timetable : vehicleJourney.getTimetables()) {
							if (collection.getTimetables().contains(timetable)) {
								isValid = true;
							} else {
								Timetable validTimetable = timetable;
								if (startDate != null)
									validTimetable = reduceTimetable(timetable, startDate, true);
								if (validTimetable != null && endDate != null)
									validTimetable = reduceTimetable(validTimetable, endDate, false);
								if (validTimetable != null) {
									collection.getTimetables().add(validTimetable);
									isValid = true;
								}
							}
						}
						if (isValid) {
							collection.getVehicleJourneys().add(vehicleJourney);
							if (isTrue(vehicleJourney.getMobilityRestrictedSuitability()))
								collection.setPmrFootenoteId(0);
							validJourneyPattern = true;
							validRoute = true;
							validLine = true;
						}
					}
				} // end vehiclejourney loop
				if (validJourneyPattern)
					collection.getJourneyPatterns().add(jp);
			}// end journeyPattern loop
			if (validRoute) {
				collection.getRoutes().add(route);
				collection.getStopPoints().addAll(route.getStopPoints());
				for (StopPoint stopPoint : route.getStopPoints()) {
					collectStopAreas(collection, stopPoint.getContainedInStopArea(),true);
				}
			}
		}// end route loop
		if (validLine) {
			collection.setLine(line);
			collection.getNetworks().add(line.getNetwork());
			if (line.getCompany() != null) {
				collection.getCompanies().add(line.getCompany());
			}
			if (!line.getGroupOfLines().isEmpty())
			{
				collection.getGroupOfLines().add(line.getGroupOfLines().get(0));
			}
		}
		return validLine;
	}

	private void collectStopAreas(ExportableData collection, StopArea stopArea, boolean followLinks) {
		if (collection.getStopAreas().contains(stopArea))
			return;
		collection.getStopAreas().add(stopArea);
		switch (stopArea.getAreaType()) {
		case BoardingPosition:
		case Quay:
			collection.getPhysicalStopPoints().add(stopArea);
			if (followLinks)
			{
				addConnectionLinks(collection,stopArea.getConnectionStartLinks());
				addConnectionLinks(collection,stopArea.getConnectionEndLinks());
			}
			if (stopArea.getParent() != null)
				collectStopAreas(collection, stopArea.getParent(), false);
			break;
		case CommercialStopPoint:
			collection.getCommercialStopPoints().add(stopArea);
			break;
		default:

		}

	}


	private void addConnectionLinks(ExportableData collection, List<ConnectionLink> links)
	{
		for (ConnectionLink link : links) 
		{
			if (collection.getConnectionLinks().contains(link)) continue;
			if (link.getStartOfLink() == null || link.getEndOfLink() == null) continue;
			if (!link.getStartOfLink().hasCoordinates() || !link.getEndOfLink().hasCoordinates() ) continue;
			if (link.getLinkDistance() == null) continue;
			collection.getConnectionLinks().add(link);
			collectStopAreas(collection, link.getStartOfLink(), false);
			collectStopAreas(collection, link.getEndOfLink(), false);
		}
	}


	/**
	 * produce a timetable reduced to a date
	 * 
	 * @param timetable
	 *            original timetable
	 * @param boundaryDate
	 *            boundary date
	 * @param before
	 *            true to eliminate before boundary date , false otherwise
	 * @return a copy reduced to date or null if reduced to nothing
	 */
	private Timetable reduceTimetable(Timetable timetable, Date boundaryDate, boolean before) {
		Timetable reduced = new Timetable();
		reduced.setDayTypes(new ArrayList<DayTypeEnum>(timetable.getDayTypes()));
		reduced.setObjectId(timetable.getObjectId());
		reduced.setObjectVersion(timetable.getObjectVersion());
		reduced.setCreationTime(timetable.getCreationTime());
		reduced.setComment(timetable.getComment());
		reduced.setVehicleJourneys(timetable.getVehicleJourneys());

		List<CalendarDay> dates = new ArrayList<CalendarDay>(timetable.getCalendarDays());
		for (Iterator<CalendarDay> iterator = dates.iterator(); iterator.hasNext();) {
			CalendarDay date = iterator.next();
			if (date == null) {
				iterator.remove();
			} else if (checkDate(date, boundaryDate, before)) {
				iterator.remove();
			}
		}
		List<Period> periods = new ArrayList<Period>(timetable.getPeriods());
		for (Iterator<Period> iterator = periods.iterator(); iterator.hasNext();) {
			Period period = iterator.next();
			if (checkPeriod(period, boundaryDate, before)) {
				iterator.remove();
			} else {
				shortenPeriod(period, boundaryDate, before);
			}
		}
		if (dates.isEmpty() && periods.isEmpty()) {
			return null;
		}
		reduced.setCalendarDays(dates);
		reduced.setPeriods(periods);
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
	private boolean shortenPeriod(Period period, Date boundaryDate, boolean before) {
		boolean ret = false;
		if (before && period.getStartDate().before(boundaryDate)) {
			ret = true;
			period.setStartDate(boundaryDate);
		}
		if (!before && period.getEndDate().after(boundaryDate)) {
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
	private boolean checkPeriod(Period period, Date boundaryDate, boolean before) {
		if (before) {
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
	private boolean checkDate(CalendarDay date, Date boundaryDate, boolean before) {
		if (before) {
			return date.getDate().before(boundaryDate);
		}
		return date.getDate().after(boundaryDate);
	}

    private boolean isTrue(Boolean b) {
		return b != null && b;
	}

}