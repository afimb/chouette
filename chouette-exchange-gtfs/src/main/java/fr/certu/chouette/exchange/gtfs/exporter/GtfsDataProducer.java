/**
 * Projet CHOUETTE
 *
 * ce projet est sous license libre
 * voir LICENSE.txt pour plus de details
 *
 */

package fr.certu.chouette.exchange.gtfs.exporter;

import java.util.Iterator;
import java.util.TimeZone;

import org.apache.log4j.Logger;

import lombok.Setter;


import fr.certu.chouette.exchange.gtfs.exporter.producer.IGtfsProducer;
import fr.certu.chouette.exchange.gtfs.model.GtfsAgency;
import fr.certu.chouette.exchange.gtfs.model.GtfsCalendar;
import fr.certu.chouette.exchange.gtfs.model.GtfsRoute;
import fr.certu.chouette.exchange.gtfs.model.GtfsStop;
import fr.certu.chouette.exchange.gtfs.model.GtfsTrip;
import fr.certu.chouette.model.neptune.Company;
import fr.certu.chouette.model.neptune.Route;
import fr.certu.chouette.model.neptune.StopArea;
import fr.certu.chouette.model.neptune.Timetable;
import fr.certu.chouette.model.neptune.VehicleJourney;

/**
 * 
 */
public class GtfsDataProducer
{
   private static final Logger logger = Logger.getLogger(GtfsDataProducer.class);
   @Setter private IGtfsProducer<GtfsCalendar,Timetable> calendarProducer;
   @Setter private IGtfsProducer<GtfsTrip,VehicleJourney> tripProducer;
   @Setter private IGtfsProducer<GtfsAgency,Company> agencyProducer;
   @Setter private IGtfsProducer<GtfsStop,StopArea> stopProducer;
   @Setter private IGtfsProducer<GtfsRoute,Route> routeProducer;

   public GtfsData produce(NeptuneData neptuneData,TimeZone timeZone)
   {
      GtfsData gtfsData = new GtfsData();
      // add calendars
      gtfsData.getCalendars().addAll(calendarProducer.produceAll(neptuneData.getTimetables()));
      // add calendarDates and remove calendar without period
      for (Iterator<GtfsCalendar> iterator = gtfsData.getCalendars().iterator(); iterator.hasNext();)
      {
         GtfsCalendar calendar = iterator.next();
         gtfsData.getCalendardates().addAll(calendar.getCalendarDates());

         if (!calendar.hasPeriod()) 
         {
            logger.info("calendar "+calendar.getServiceId()+" has no period : removed (may be only calendar_dates)");
            iterator.remove();

         }
      }
      // add trips
      gtfsData.getTrip().addAll(tripProducer.produceAll(neptuneData.getVehicleJourneys()));

      // add stopTimes and frequencies
      for (GtfsTrip trip : gtfsData.getTrip())
      {
         gtfsData.getStoptimes().addAll(trip.getStopTimes());
         gtfsData.getFrequencies().addAll(trip.getFrequencies());
      }

      // add routes
      gtfsData.getRoutes().addAll(routeProducer.produceAll(neptuneData.getRoutes()));

      // add stops
      gtfsData.getStops().addAll(stopProducer.produceAll(neptuneData.getPhysicalStops()));

      // add agencies
      gtfsData.getAgencies().addAll(agencyProducer.produceAll(neptuneData.getCompanies()));
      for (GtfsAgency agency : gtfsData.getAgencies())
      {
         agency.setAgencyTimezone(timeZone);
      }

      return gtfsData;
   }

}
