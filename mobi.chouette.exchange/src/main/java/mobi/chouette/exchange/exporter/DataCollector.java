package mobi.chouette.exchange.exporter;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import lombok.extern.log4j.Log4j;
import mobi.chouette.model.AccessLink;
import mobi.chouette.model.AccessPoint;
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
public class DataCollector {

	private static final DayTypeEnum[] dayTypeForDayOfWeek = new DayTypeEnum[8];

	static {
		dayTypeForDayOfWeek[Calendar.SUNDAY] = DayTypeEnum.Sunday;
		dayTypeForDayOfWeek[Calendar.MONDAY] = DayTypeEnum.Monday;
		dayTypeForDayOfWeek[Calendar.TUESDAY] = DayTypeEnum.Tuesday;
		dayTypeForDayOfWeek[Calendar.WEDNESDAY] = DayTypeEnum.Wednesday;
		dayTypeForDayOfWeek[Calendar.THURSDAY] = DayTypeEnum.Thursday;
		dayTypeForDayOfWeek[Calendar.FRIDAY] = DayTypeEnum.Friday;
		dayTypeForDayOfWeek[Calendar.SATURDAY] = DayTypeEnum.Saturday;
	}

	protected boolean collect(ExportableData collection, Line line, Date startDate, Date endDate,
			boolean skipNoCoordinate, boolean followLinks) {
		boolean validLine = false;
		collection.setLine(null);
		collection.getRoutes().clear();
		collection.getJourneyPatterns().clear();
		collection.getStopPoints().clear();
		collection.getVehicleJourneys().clear();
		for (Route route : line.getRoutes()) {
			boolean validRoute = false;
			if (route.getStopPoints().isEmpty())
				continue;
			for (JourneyPattern jp : route.getJourneyPatterns()) {
				boolean validJourneyPattern = false;
				if (jp.getStopPoints().isEmpty())
					continue; // no stops
				if (jp.getDepartureStopPoint() == null || jp.getArrivalStopPoint() == null) {
					NeptuneUtil.refreshDepartureArrivals(jp);
				}
				for (VehicleJourney vehicleJourney : jp.getVehicleJourneys()) {
					if (vehicleJourney.getVehicleJourneyAtStops().isEmpty()) {
						continue;
					}
					if (startDate == null && endDate == null) {
						boolean isValid = false;
						for (Timetable timetable : vehicleJourney.getTimetables()) {

							if (collection.getTimetables().contains(timetable)) {
								isValid = true;
							} else {
								if (!timetable.getPeriods().isEmpty() || !timetable.getCalendarDays().isEmpty()) {
									collection.getTimetables().add(timetable);
									isValid = true;
								}
							}
						}
						if (isValid) {
							collection.getTimetables().addAll(vehicleJourney.getTimetables());
							collection.getVehicleJourneys().add(vehicleJourney);
							validJourneyPattern = true;
							validRoute = true;
							validLine = true;
						}
					} else {
						boolean isValid = false;
						for (Timetable timetable : vehicleJourney.getTimetables()) {
							if (collection.getTimetables().contains(timetable)) {
								isValid = true;
							} else if (collection.getExcludedTimetables().contains(timetable)){
								isValid = false;
							}
							else {
								
								if (startDate == null)
									isValid = timetable.isActiveBefore(endDate);
								else if (endDate == null)
									isValid = timetable.isActiveAfter(startDate);
								else 
									isValid = timetable.isActiveOnPeriod(startDate, endDate);
								if (isValid)
									collection.getTimetables().add(timetable);
								else
									collection.getExcludedTimetables().add(timetable);
							}
						}
						if (isValid) {
							collection.getVehicleJourneys().add(vehicleJourney);
							if (vehicleJourney.getCompany() != null) {
								collection.getCompanies().add(vehicleJourney.getCompany());
							}
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
				route.getOppositeRoute(); // to avoid lazy loading afterward
				collection.getStopPoints().addAll(route.getStopPoints());
				for (StopPoint stopPoint : route.getStopPoints()) {
					collectStopAreas(collection, stopPoint.getContainedInStopArea(), skipNoCoordinate, followLinks);
				}
			}
		}// end route loop
		if (validLine) {
			collection.setLine(line);
			collection.getNetworks().add(line.getNetwork());
			if (line.getCompany() != null) {
				collection.getCompanies().add(line.getCompany());
			}
			if (line.getGroupOfLines() != null) {
				collection.getGroupOfLines().addAll(line.getGroupOfLines());
			}
			if (!line.getRoutingConstraints().isEmpty()) {
				collection.getStopAreas().addAll(line.getRoutingConstraints());
			}
		}
		completeSharedData(collection);
		return validLine;
	}

	protected boolean collect(ExportableData collection, Collection<StopArea> stopAreas, boolean skipNoCoordinate,
			boolean followLinks) {
		for (StopArea stopArea : stopAreas) {
			collectStopAreas(collection, stopArea, skipNoCoordinate, followLinks);
		}
		completeSharedData(collection);
		return !collection.getPhysicalStops().isEmpty();

	}

	protected void completeSharedData(ExportableData collection) {
		// force lazy dependencies to be loaded
		for (ConnectionLink link : collection.getConnectionLinks()) {
			collection.getSharedStops().add(link.getEndOfLink());
			collection.getSharedStops().add(link.getStartOfLink());
		}
	}

	protected void collectStopAreas(ExportableData collection, StopArea stopArea, boolean skipNoCoordinate,
			boolean followLinks) {
		if (collection.getStopAreas().contains(stopArea))
			return;
		if (!skipNoCoordinate || stopArea.hasCoordinates()) {
			collection.getStopAreas().add(stopArea);
			switch (stopArea.getAreaType()) {
			case BoardingPosition:
				collection.getBoardingPositions().add(stopArea);
				break;
			case Quay:
				collection.getQuays().add(stopArea);
				break;
			case CommercialStopPoint:
				collection.getCommercialStops().add(stopArea);
				break;
			case StopPlace:
				collection.getStopPlaces().add(stopArea);
				break;
			default:
			}
			addConnectionLinks(collection, stopArea.getConnectionStartLinks(), skipNoCoordinate, followLinks);
			addConnectionLinks(collection, stopArea.getConnectionEndLinks(), skipNoCoordinate, followLinks);
			addAccessPoints(collection, stopArea.getAccessPoints(), skipNoCoordinate);
			addAccessLinks(collection, stopArea.getAccessLinks());
			if (stopArea.getParent() != null)
				collectStopAreas(collection, stopArea.getParent(), skipNoCoordinate, followLinks);
		}
	}

	protected void addConnectionLinks(ExportableData collection, List<ConnectionLink> links, boolean skipNoCoordinate,
			boolean followLinks) {
		for (ConnectionLink link : links) {
			if (collection.getConnectionLinks().contains(link))
				continue;
			if (link.getStartOfLink() == null || link.getEndOfLink() == null)
				continue;
			if (!link.getStartOfLink().hasCoordinates() || !link.getEndOfLink().hasCoordinates())
				continue;
			collection.getConnectionLinks().add(link);
			if (followLinks) {
				collectStopAreas(collection, link.getStartOfLink(), skipNoCoordinate, followLinks);
				collectStopAreas(collection, link.getEndOfLink(), skipNoCoordinate, followLinks);
			}
		}
	}

	protected void addAccessLinks(ExportableData collection, List<AccessLink> links) {
		for (AccessLink link : links) {
			if (collection.getAccessLinks().contains(link))
				continue;
			if (link.getAccessPoint() == null)
				continue;
			if (!link.getAccessPoint().hasCoordinates())
				continue;
			collection.getAccessLinks().add(link);
		}
	}

	protected void addAccessPoints(ExportableData collection, List<AccessPoint> accessPoints, boolean skipNoCoordinate) {
		for (AccessPoint point : accessPoints) {
			if (collection.getAccessPoints().contains(point))
				continue;
			if (skipNoCoordinate && !point.hasCoordinates())
				continue;
			collection.getAccessPoints().add(point);
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

		List<CalendarDay> dates = new ArrayList<CalendarDay>(timetable.getCalendarDays());
		for (Iterator<CalendarDay> iterator = dates.iterator(); iterator.hasNext();) {
			CalendarDay date = iterator.next();
			if (date == null) {
				iterator.remove();
			} else if (checkDate(date, boundaryDate, before)) {
				iterator.remove();
			}
		}
		List<Period> periods = new ArrayList<Period>();
		for (Period period : timetable.getPeriods()) {
			periods.add(new Period(period.getStartDate(), period.getEndDate()));
		}
		for (Iterator<Period> iterator = periods.iterator(); iterator.hasNext();) {
			Period period = iterator.next();
			if (checkPeriod(period, boundaryDate, before)) {
				iterator.remove();
			} else {
				Period p = shortenPeriod(period, boundaryDate, before, reduced.getDayTypes());
				if (p == null)
					iterator.remove();
				else {
					log.warn("period shorted to " + p.getStartDate() + " " + p.getEndDate());

				}
			}
		}
		if (dates.isEmpty() && periods.isEmpty()) {
			return null;
		}
		reduced.setCalendarDays(dates);
		reduced.setPeriods(periods);
		reduced.computeLimitOfPeriods();
		log.info("timetable "+reduced.getComment()+ "exported for "+reduced.getStartOfPeriod()+" "+reduced.getEndOfPeriod());
		return reduced;

	}

	/**
	 * check period if partially out of bounds and reduce it to bounds
	 * 
	 * @param period
	 * @param boundaryDate
	 * @param before
	 * @param dayTypes
	 * @return true if period has been modified
	 */
	private Period shortenPeriod(Period period, Date boundaryDate, boolean before, List<DayTypeEnum> dayTypes) {
		if (before && period.getStartDate().before(boundaryDate)) {
			period.setStartDate(boundaryDate);
		}
		if (!before && period.getEndDate().after(boundaryDate)) {
			period.setEndDate(boundaryDate);
		}

		return reducePeriod(period, dayTypes);
	}

	private Period reducePeriod(Period period, List<DayTypeEnum> dayTypes) {
		Period p = reducePeriodStartDate(period, dayTypes);
		if (p != null)
			p = reducePeriodEndDate(p, dayTypes);
		return p;
	}

	private Period reducePeriodStartDate(Period period, List<DayTypeEnum> dayTypes) {
		Date date = new Date(period.getStartDate().getTime());
		DayTypeEnum type = dayTypeEnumFor(date);
		while (!date.after(period.getEndDate()) && !dayTypes.contains(type)) {
			date.setTime(date.getTime() + 86400000L);
			type = dayTypeEnumFor(date);
		}
		if (date.after(period.getEndDate())) {
			date = new Date(period.getEndDate().getTime());
			if (dayTypes.contains(dayTypeEnumFor(date))) {
				return new Period(date, date);
			}
			return null;
		}
		return new Period(date, new Date(period.getEndDate().getTime()));
	}

	private Period reducePeriodEndDate(Period period, List<DayTypeEnum> dayTypes) {
		Date date = new Date(period.getEndDate().getTime());
		DayTypeEnum type = dayTypeEnumFor(date);
		while (!date.before(period.getStartDate()) && !dayTypes.contains(type)) {
			date.setTime(date.getTime() - 86400000L);
			type = dayTypeEnumFor(date);
		}
		if (date.before(period.getStartDate())) {
			date = new Date(period.getStartDate().getTime());
			if (dayTypes.contains(dayTypeEnumFor(date))) {
				return new Period(date, date);
			}
			return null;
		}
		return new Period(new Date(period.getStartDate().getTime()), date);
	}

	private DayTypeEnum dayTypeEnumFor(Date date) {
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		c.set(Calendar.HOUR_OF_DAY, 12);
		int j = c.get(Calendar.DAY_OF_WEEK);
		return dayTypeForDayOfWeek[j];
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

}
