/**
 * Projet CHOUETTE
 *
 * ce projet est sous license libre
 * voir LICENSE.txt pour plus de details
 *
 */

package fr.certu.chouette.exchange.gtfs.exporter.producer;

import java.sql.Time;

import org.apache.log4j.Logger;

import fr.certu.chouette.exchange.gtfs.exporter.report.GtfsReport;
import fr.certu.chouette.exchange.gtfs.refactor.exporter.GtfsExporter;
import fr.certu.chouette.exchange.gtfs.refactor.model.GtfsStopTime;
import fr.certu.chouette.exchange.gtfs.refactor.model.GtfsTime;
import fr.certu.chouette.exchange.gtfs.refactor.model.GtfsTrip;
import fr.certu.chouette.model.neptune.JourneyPattern;
import fr.certu.chouette.model.neptune.Line;
import fr.certu.chouette.model.neptune.Route;
import fr.certu.chouette.model.neptune.Timetable;
import fr.certu.chouette.model.neptune.VehicleJourney;
import fr.certu.chouette.model.neptune.VehicleJourneyAtStop;

/**
 * produce Trips and stop_times for vehicleJourney
 * <p>
 * when vehicleJourney is on multiple timetables, it will be cloned for each
 * 
 * @ TODO : refactor to produce one calendar for each timetable groups
 */
public class GtfsTripProducer extends
      AbstractProducer
{
   private static final Logger logger = Logger
         .getLogger(GtfsTripProducer.class);


   GtfsTrip trip = new GtfsTrip();
   GtfsStopTime time = new GtfsStopTime();
   

   public GtfsTripProducer(GtfsExporter exporter)
   {
      super(exporter);
      // TODO Auto-generated constructor stub
   }

   /**
    * produce stoptimes for vehiclejourneyatstops @ TODO see how to manage ITL
    * 
    * @param vj
    * @return list of stoptimes
    */
   private boolean saveTimes(VehicleJourney vj, GtfsReport report, String prefix)
   {
      Integer tomorrowArrival = Integer.valueOf(0);
      Time previousArrival = null;
      Integer tomorrowDeparture = Integer.valueOf(0);
      Time previousDeparture = null;
      String tripId = toGtfsId(vj.getObjectId(),prefix);
      time.setTripId(tripId);
      for (VehicleJourneyAtStop vjas : vj.getVehicleJourneyAtStops())
      {
         time.setStopId(toGtfsId(vjas.getStopPoint().getContainedInStopArea()
               .getObjectId(),prefix));
         Time arrival = vjas.getArrivalTime();
         if (arrival == null)
            arrival = vjas.getDepartureTime();
         if (tomorrowArrival != 1 && previousArrival != null
               && previousArrival.after(arrival))
         {
            tomorrowArrival = Integer.valueOf(1); // after midnight
         }
         previousArrival = arrival;
         time.setArrivalTime(new GtfsTime(arrival, tomorrowArrival));
         Time departure = vjas.getDepartureTime();
         if (tomorrowDeparture != 1 && previousDeparture != null
               && previousDeparture.after(departure))
         {
            tomorrowDeparture = Integer.valueOf(1); // after midnight
         }
         time.setDepartureTime(new GtfsTime(departure, tomorrowDeparture));
         previousDeparture = departure;
         time.setStopSequence((int) vjas.getStopPoint().getPosition());

         // time.setStopHeadsign();
         // time.setPickUpType();
         // time.setDropOffType();
         try
         {
            getExporter().getStopTimeExporter().export(time);
         }
         catch (Exception e)
         {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return false;
         }
         
      }
      return true;
   }

   /**
    * convert vehicle journey to trip for a specific timetable
    * 
    * @param vj
    *           vehicle journey
    * @param timetableId
    *           timetable id
    * @param times
    *           stoptimes model
    * @param multipleTimetable
    *           vehicle journey with multiple timetables
    * @return gtfs trip
    */
   public boolean save(VehicleJourney vj, Timetable timetable, GtfsReport report, String prefix)
   {

      String tripId = toGtfsId(vj.getObjectId(), prefix);

      trip.setTripId(tripId);

      JourneyPattern jp = vj.getJourneyPattern();
      Route route = vj.getRoute();
      Line line = route.getLine();
      trip.setRouteId(toGtfsId(line.getObjectId(),prefix));
      if ("R".equals(route.getWayBack()))
      {
         trip.setDirectionId(GtfsTrip.DirectionType.Inbound);
      } else
      {
         trip.setDirectionId(GtfsTrip.DirectionType.Outbound);
      }

      trip.setServiceId(toGtfsId(timetable.getObjectId(),prefix));

      String name = vj.getPublishedJourneyName();
      if (isEmpty(name) && vj.getNumber() != null)
         name = "" + vj.getNumber();

      if (!isEmpty(name))
         trip.setTripShortName(name);
      else
         trip.setTripShortName(null);

      if (!isEmpty(jp.getPublishedName()))
         trip.setTripHeadSign(jp.getPublishedName());
      else
         trip.setTripShortName(null);

      if (vj.getMobilityRestrictedSuitability() != null)
         trip.setWheelchairAccessible(vj.getMobilityRestrictedSuitability() ? GtfsTrip.WheelchairAccessibleType.Allowed
               : GtfsTrip.WheelchairAccessibleType.Allowed);
      // trip.setBlockId(...);
      // trip.setShapeId(...);
      // trip.setBikeAllowed();
      
      try
      {
         getExporter().getTripExporter().export(trip);
      }
      catch (Exception e)
      {
         // TODO Auto-generated catch block
         e.printStackTrace();
         return false;
      }

      // add StopTimes
      saveTimes(vj,report,prefix);

      return true;
   }


   
}
