/**
 * Projet CHOUETTE
 *
 * ce projet est sous license libre
 * voir LICENSE.txt pour plus de details
 *
 */

package mobi.chouette.exchange.gtfs.exporter.producer;

import java.sql.Time;

import lombok.extern.log4j.Log4j;
import mobi.chouette.exchange.gtfs.model.GtfsStopTime;
import mobi.chouette.exchange.gtfs.model.GtfsTime;
import mobi.chouette.exchange.gtfs.model.GtfsTrip;
import mobi.chouette.exchange.gtfs.model.exporter.GtfsExporterInterface;
import mobi.chouette.exchange.report.Report;
import mobi.chouette.model.JourneyPattern;
import mobi.chouette.model.Line;
import mobi.chouette.model.Route;
import mobi.chouette.model.VehicleJourney;
import mobi.chouette.model.VehicleJourneyAtStop;

/**
 * produce Trips and stop_times for vehicleJourney
 * <p>
 * when vehicleJourney is on multiple timetables, it will be cloned for each
 * 
 * @ TODO : refactor to produce one calendar for each timetable groups
 */
@Log4j
public class GtfsTripProducer extends
AbstractProducer
{


   GtfsTrip trip = new GtfsTrip();
   GtfsStopTime time = new GtfsStopTime();


   public GtfsTripProducer(GtfsExporterInterface exporter)
   {
      super(exporter);
   }

   /**
    * produce stoptimes for vehiclejourneyatstops @ TODO see how to manage ITL
    * 
    * @param vj
    * @param sharedPrefix 
    * @return list of stoptimes
    */
   private boolean saveTimes(VehicleJourney vj, Report report, String prefix, String sharedPrefix)
   {
      if (vj.getVehicleJourneyAtStops().isEmpty()) return false;
      Integer zero = Integer.valueOf(0);
      Integer one = Integer.valueOf(1);
      Integer tomorrowArrival = zero;
      Time previousArrival = null;
      Integer tomorrowDeparture = zero;
      Time previousDeparture = null;
      String tripId = toGtfsId(vj.getObjectId(),prefix);
      time.setTripId(tripId);
      for (VehicleJourneyAtStop vjas : vj.getVehicleJourneyAtStops())
      {
         time.setStopId(toGtfsId(vjas.getStopPoint().getContainedInStopArea()
               .getObjectId(),sharedPrefix));
         Time arrival = vjas.getArrivalTime();
         if (arrival == null)
            arrival = vjas.getDepartureTime();
         if (tomorrowArrival != one && previousArrival != null
               && previousArrival.after(arrival))
         {
            tomorrowArrival = one; // after midnight
         }
         previousArrival = arrival;
         time.setArrivalTime(new GtfsTime(arrival, tomorrowArrival));
         Time departure = vjas.getDepartureTime();
         if (tomorrowDeparture != one && previousDeparture != null
               && previousDeparture.after(departure))
         {
            tomorrowDeparture = one; // after midnight
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
            log.error(e.getMessage(),e);
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
    * @param sharedPrefix 
    * @param timetableId
    *           timetable id
    * @param times
    *           stoptimes model
    * @param multipleTimetable
    *           vehicle journey with multiple timetables
    * @return gtfs trip
    */
   public boolean save(VehicleJourney vj, String serviceId, Report report, String prefix, String sharedPrefix)
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

      trip.setServiceId(serviceId);

      String name = vj.getPublishedJourneyName();
      if (isEmpty(name) && vj.getNumber() != null && !vj.getNumber().equals(Long.valueOf(0)))
         name = "" + vj.getNumber();

      if (!isEmpty(name))
         trip.setTripShortName(name);
      else
         trip.setTripShortName(null);

      if (!isEmpty(jp.getPublishedName()))
         trip.setTripHeadSign(jp.getPublishedName());
      else
         trip.setTripHeadSign(null);

      if (vj.getMobilityRestrictedSuitability() != null)
         trip.setWheelchairAccessible(vj.getMobilityRestrictedSuitability() ? GtfsTrip.WheelchairAccessibleType.Allowed
               : GtfsTrip.WheelchairAccessibleType.NoAllowed);
      else
         trip.setWheelchairAccessible(GtfsTrip.WheelchairAccessibleType.NoInformation);
      // trip.setBlockId(...);
      // trip.setShapeId(...);
      // trip.setBikeAllowed();

      // add StopTimes
      if (saveTimes(vj,report,prefix,sharedPrefix))
      {
         try
         {
            getExporter().getTripExporter().export(trip);
         }
         catch (Exception e)
         {
            log.error(e.getMessage(),e);
            return false;
         }
      }
      return true;
   }



}
