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
import fr.certu.chouette.exchange.gtfs.exporter.report.GtfsReport;
import fr.certu.chouette.exchange.gtfs.exporter.report.GtfsReportItem;
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
import fr.certu.chouette.plugin.report.Report;
import fr.certu.chouette.plugin.report.Report.STATE;

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

   public GtfsData produce(NeptuneData neptuneData,TimeZone timeZone, GtfsReport report) throws GtfsExportException
   {
      GtfsData gtfsData = new GtfsData();
      // add calendars
      gtfsData.getCalendars().addAll(calendarProducer.produceAll(neptuneData.getTimetables(),report));
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
      gtfsData.getTrip().addAll(tripProducer.produceAll(neptuneData.getVehicleJourneys(),report));

      // add stopTimes and frequencies
      for (GtfsTrip trip : gtfsData.getTrip())
      {
         gtfsData.getStoptimes().addAll(trip.getStopTimes());
         gtfsData.getFrequencies().addAll(trip.getFrequencies());
      }

      // add routes
      gtfsData.getRoutes().addAll(routeProducer.produceAll(neptuneData.getRoutes(),report));

      // add stops
      gtfsData.getStops().addAll(stopProducer.produceAll(neptuneData.getPhysicalStops(),report));

      // add agencies
      gtfsData.getAgencies().addAll(agencyProducer.produceAll(neptuneData.getCompanies(),report));
      for (GtfsAgency agency : gtfsData.getAgencies())
      {
         agency.setAgencyTimezone(timeZone);
      }
      if (report.getStatus().ordinal() >= Report.STATE.ERROR.ordinal()) 
         throw new GtfsExportException(GtfsExportExceptionCode.ERROR, "missing data");
      // check if no data for one or more types 
      boolean error = false;
      if (gtfsData.getAgencies().isEmpty()) 
      {
         logger.error("no company for agencies.txt");
         GtfsReportItem item = new GtfsReportItem(GtfsReportItem.KEY.EMPTY_DATA, STATE.ERROR, "Company");
         report.addItem(item);
         error= true;
      }
      if (gtfsData.getCalendars().isEmpty() && gtfsData.getCalendardates().isEmpty()) 
      {
         logger.error("no timetable for calendars.txt or calendar_dates.txt");
         GtfsReportItem item = new GtfsReportItem(GtfsReportItem.KEY.EMPTY_DATA, STATE.ERROR, "Timetable");
         report.addItem(item);
         error= true;
      }
      if (gtfsData.getRoutes().isEmpty()) 
      {
         logger.error("no route for routes.txt");
         GtfsReportItem item = new GtfsReportItem(GtfsReportItem.KEY.EMPTY_DATA, STATE.ERROR, "Route");
         report.addItem(item);
         error= true;
      }
      if (gtfsData.getTrip().isEmpty() || gtfsData.getStoptimes().isEmpty()) 
      {
         logger.error("no vehicleJourney for trips.txt or stoptimes.txt");
         GtfsReportItem item = new GtfsReportItem(GtfsReportItem.KEY.EMPTY_DATA, STATE.ERROR, "VehicleJourney");
         report.addItem(item);
         error= true;
      }
      if (gtfsData.getStops().isEmpty()) 
      {
         logger.error("no stopArea for stops.txt");
         GtfsReportItem item = new GtfsReportItem(GtfsReportItem.KEY.EMPTY_DATA, STATE.ERROR, "StopArea");
         report.addItem(item);
         error= true;
      }

      if (error) throw new GtfsExportException(GtfsExportExceptionCode.ERROR, "empty data");
      return gtfsData;
   }

}
