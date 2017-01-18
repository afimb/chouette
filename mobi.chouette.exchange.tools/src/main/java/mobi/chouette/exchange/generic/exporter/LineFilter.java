package mobi.chouette.exchange.generic.exporter;

import java.util.Date;
import java.util.Iterator;

import lombok.extern.log4j.Log4j;
import mobi.chouette.model.JourneyPattern;
import mobi.chouette.model.Line;
import mobi.chouette.model.Route;
import mobi.chouette.model.Timetable;
import mobi.chouette.model.VehicleJourney;
import mobi.chouette.model.util.NeptuneUtil;

@Log4j
public class LineFilter {

	public boolean filter(Line line, Date startDate, Date endDate) {

		// Clean line
		for (Iterator<Route> routeI = line.getRoutes().iterator(); routeI.hasNext();) {
			Route route = routeI.next();
			if (route.getStopPoints().size() < 2) {
				routeI.remove();
				continue;

			}
			for (Iterator<JourneyPattern> jpI = route.getJourneyPatterns().iterator(); jpI.hasNext();) {
				JourneyPattern jp = jpI.next();
				if (jp.getStopPoints().size() < 2) {
					jpI.remove();
					continue; // no stops
				}
				if (jp.getDepartureStopPoint() == null || jp.getArrivalStopPoint() == null) {
					NeptuneUtil.refreshDepartureArrivals(jp);
				}
				for (Iterator<VehicleJourney> vjI = jp.getVehicleJourneys().iterator(); vjI.hasNext();) {
					VehicleJourney vehicleJourney = vjI.next();
					if (vehicleJourney.getVehicleJourneyAtStops().isEmpty()) {
						vjI.remove();
						continue;
					}
					if (startDate == null && endDate == null) {
						for (Iterator<Timetable> timetableI = vehicleJourney.getTimetables().iterator(); timetableI
								.hasNext();) {

							Timetable timetable = timetableI.next();

							if (timetable.getPeriods().isEmpty() && timetable.getCalendarDays().isEmpty()) {
								timetableI.remove();
							}
						}
						
						if(vehicleJourney.getTimetables().isEmpty()) {
							vjI.remove();
						}
					} else {
						for (Iterator<Timetable> timetableI = vehicleJourney.getTimetables().iterator(); timetableI
								.hasNext();) {

							Timetable timetable = timetableI.next();

							boolean validTimetable = isTimetableValid(timetable, startDate, endDate);
							if (!validTimetable) {
								timetableI.remove();
								continue;
							}
						}
						if(vehicleJourney.getTimetables().isEmpty()) {
							vjI.remove();
						}
					} // end vehiclejourney loop
				} // end journeyPattern loop
			}
		}

		// Recheck that there are at least 1 vehiclejourney
		// TODO
		return true;
	}

	private boolean isTimetableValid(Timetable timetable, Date startDate, Date endDate) {
		if (timetable.getPeriods().isEmpty() && timetable.getCalendarDays().isEmpty()) {
			return false;
		}

		if (startDate == null)
			return timetable.isActiveBefore(new java.sql.Date(endDate.getTime()));
		else if (endDate == null)
			return timetable.isActiveAfter(new java.sql.Date(startDate.getTime()));
		else
			return timetable.isActiveOnPeriod(new java.sql.Date(startDate.getTime()), new java.sql.Date(endDate.getTime()));

	}

}
