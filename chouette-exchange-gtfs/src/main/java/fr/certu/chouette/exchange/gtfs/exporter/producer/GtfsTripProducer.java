/**
 * Projet CHOUETTE
 *
 * ce projet est sous license libre
 * voir LICENSE.txt pour plus de details
 *
 */

package fr.certu.chouette.exchange.gtfs.exporter.producer;

import java.sql.Time;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import fr.certu.chouette.exchange.gtfs.exporter.report.GtfsReport;
import fr.certu.chouette.exchange.gtfs.model.GtfsStopTime;
import fr.certu.chouette.exchange.gtfs.model.GtfsTime;
import fr.certu.chouette.exchange.gtfs.model.GtfsTrip;
import fr.certu.chouette.model.neptune.Route;
import fr.certu.chouette.model.neptune.Timetable;
import fr.certu.chouette.model.neptune.VehicleJourney;
import fr.certu.chouette.model.neptune.VehicleJourneyAtStop;

/**
 * convert Timetable to Gtfs Calendar and CalendarDate
 * <p>
 * optimise multiple period timetable with calendarDate inclusion or exclusion
 */
public class GtfsTripProducer extends AbstractProducer<GtfsTrip, VehicleJourney>
{
	@Override
	public List<GtfsTrip> produceAll(Collection<VehicleJourney> neptuneObjects,GtfsReport report)
	{
		List<GtfsTrip> objects = new ArrayList<GtfsTrip>();
		for (VehicleJourney object : neptuneObjects)
		{
			objects.addAll(produceAll(object,report));
		}
		return objects;
	}


	@Override
	public List<GtfsTrip> produceAll(VehicleJourney vj,GtfsReport report)
	{
		List<GtfsTrip> trips = new ArrayList<GtfsTrip>();
		if (vj.getTimetables().isEmpty()) return trips;

		List<GtfsStopTime> times = produceTimes(vj);
		if (vj.getTimetables().size() == 1)
		{
			Timetable timetable = vj.getTimetables().get(0);
			trips.add(produce(vj,timetable.getObjectId(),times,false));
		}
		else
		{
			for (Timetable timetable : vj.getTimetables())
			{
				trips.add(produce(vj,timetable.getObjectId(),times,true));
			}
		}
		return trips;

	}

	private List<GtfsStopTime> produceTimes(VehicleJourney vj)
	{
		List<GtfsStopTime> times = new ArrayList<GtfsStopTime>();
		boolean tomorrowArrival = false;
		Time previousArrival = null;
		boolean tomorrowDeparture = false;
		Time previousDeparture = null;
		for (VehicleJourneyAtStop vjas : vj.getVehicleJourneyAtStops())
		{
			GtfsStopTime time = new GtfsStopTime();
			time.setStopId(toGtfsId(vjas.getStopPoint().getContainedInStopArea().getObjectId()));
			Time arrival = vjas.getArrivalTime();
			if (arrival == null) arrival = vjas.getDepartureTime();
			if (! tomorrowArrival && previousArrival != null && previousArrival.after(arrival))
			{
				tomorrowArrival = true; // after midnight
			}
			previousArrival = arrival;
			time.setArrivalTime(new GtfsTime(arrival, tomorrowArrival));
			Time departure = vjas.getDepartureTime();
			if (! tomorrowDeparture && previousDeparture != null && previousDeparture.after(departure))
			{
				tomorrowDeparture = true; // after midnight
			}
			time.setDepartureTime(new GtfsTime(departure, tomorrowDeparture));
			previousDeparture = departure;
			time.setStopSequence((int)vjas.getOrder());
			times.add(time);
		}
		return times;
	}

	private GtfsTrip produce(VehicleJourney vj,String timetableId, List<GtfsStopTime> times,boolean multipleTimetable)
	{

		GtfsTrip trip = new GtfsTrip();

		String tripId = toGtfsId(vj.getObjectId());
		if (multipleTimetable)
			tripId+="_"+timetableId.split(":")[2];

		trip.setTripId(tripId);

		// route = un aller-retour !  
		Route route = vj.getRoute();
		if ("R".equals(route.getWayBack().equals("R")) && route.getWayBackRouteId() != null)
		{
			trip.setRouteId(toGtfsId(route.getWayBackRouteId()));
			trip.setDirectionId(GtfsTrip.INBOUND);
		}
		else
		{
			trip.setRouteId(toGtfsId(route.getObjectId()));
			trip.setDirectionId(GtfsTrip.OUTBOUND);
		}

		trip.setServiceId(toGtfsId(timetableId));

		//trip.setTripHeadsign(...);
		String name = vj.getPublishedJourneyName();
		if (name == null && vj.getNumber() != null)
			name = ""+vj.getNumber();
		else
			name = "";
		if (name.trim().length() == 0 && vj.getComment() != null)
			name = vj.getComment();
		if (name.trim().length() != 0)
			trip.setTripShortName(name);
		//trip.setShapeId(...);

		// add StopTimes
		for (GtfsStopTime time : times)
		{
			GtfsStopTime copy = time.copy();
			copy.setTripId(tripId);
			trip.addStopTime(copy);
		}

		return trip;
	}


	@Override
	public GtfsTrip produce(VehicleJourney neptuneObject,GtfsReport report)
	{
		throw new UnsupportedOperationException("not yet implemented");
	}


}
