package fr.certu.chouette.exchange.gtfs.refactor.importer;

import java.util.HashMap;
import java.util.Map;

import lombok.extern.log4j.Log4j;

import org.apache.log4j.BasicConfigurator;

import com.jamonapi.Monitor;
import com.jamonapi.MonitorFactory;

import fr.certu.chouette.exchange.gtfs.refactor.model.GtfsAgency;
import fr.certu.chouette.exchange.gtfs.refactor.model.GtfsCalendar;
import fr.certu.chouette.exchange.gtfs.refactor.model.GtfsCalendarDate;
import fr.certu.chouette.exchange.gtfs.refactor.model.GtfsRoute;
import fr.certu.chouette.exchange.gtfs.refactor.model.GtfsStop;
import fr.certu.chouette.exchange.gtfs.refactor.model.GtfsStopTime;
import fr.certu.chouette.exchange.gtfs.refactor.model.GtfsTransfer;
import fr.certu.chouette.exchange.gtfs.refactor.model.GtfsTrip;

@Log4j
public class Main
{

   private int _count;
   private static final String PATH = "/opt/tmp/RENNES/";

   public static void main(String[] args)
   {
      BasicConfigurator.configure();
      Main main = new Main();

      Monitor monitor = MonitorFactory.start();
      main.test();
      log.debug("[DSU] total : " + monitor.stop());
   }

   private void parse(GtfsImporter dao, String name, String path, Class clazz)
   {

      try
      {

         Monitor monitor = MonitorFactory.start();
         Index parser = dao.getImporter(name, path, clazz);
         _count = 0;
         for (Object bean : parser)
         {
            // System.out.println("[DSU] value : " + bean);
            parser.validate(bean, dao);
            _count++;
         }

         log.debug("[DSU] get " + _count + " object " + monitor.stop());

      } catch (Exception e)
      {
         e.printStackTrace();
      }
   }

   private void execute()
   {
      GtfsImporter dao = new GtfsImporter(PATH);

      // stop_times.txt
      parse(dao, GtfsImporter.INDEX.STOP_TIME_BY_TRIP.name(),
            StopTimeByTrip.FILENAME, StopTimeByTrip.class);

      // trips.txt
      parse(dao, GtfsImporter.INDEX.TRIP_BY_ID.name(), TripById.FILENAME,
            TripById.class);
      parse(dao, GtfsImporter.INDEX.TRIP_BY_ROUTE.name(), TripById.FILENAME,
            TripByRoute.class);
//      parse(dao, GtfsImporter.INDEX.TRIP_BY_SERVICE.name(), TripById.FILENAME,
//            TripByService.class);

      // routes.txt
      parse(dao, GtfsImporter.INDEX.ROUTE_BY_ID.name(), RouteById.FILENAME,
            RouteById.class);

      // stops.txt
      parse(dao, GtfsImporter.INDEX.STOP_BY_ID.name(), StopById.FILENAME,
            StopById.class);

      // calendar.txt
      parse(dao, GtfsImporter.INDEX.CALENDAR_DATE_BY_SERVICE.name(),
            CalendarDateByService.FILENAME, CalendarDateByService.class);

      // calendar_dates.txt
      parse(dao, GtfsImporter.INDEX.CALENDAR_BY_SERVICE.name(),
            CalendarByService.FILENAME, CalendarByService.class);

      // transfers.txt
      parse(dao, GtfsImporter.INDEX.TRANSFER_BY_FROM_STOP.name(),
            TransferByFromStop.FILENAME, TransferByFromStop.class);

      // agency.txt
      parse(dao, GtfsImporter.INDEX.AGENCY_BY_ID.name(), AgencyById.FILENAME,
            AgencyById.class);

      // frequencies.txt
      if (dao.hasFrequencyImporter())
      {
         parse(dao, GtfsImporter.INDEX.FREQUENCY_BY_TRIP.name(),
               FrequencyByTrip.FILENAME, FrequencyByTrip.class);
      }

      dao.dispose();

   }

   private void test()
   {

      printMemory();

      GtfsImporter dao = new GtfsImporter(PATH);

      Map<String, GtfsStop> _map = new HashMap<String, GtfsStop>();

      Index<GtfsRoute> routes = dao.getRouteById();
      for (GtfsRoute route : routes)
      {
         // System.out.println(route);
         routes.validate(route, dao);

         Index<GtfsAgency> agencies = dao.getAgencyById();
         GtfsAgency agency = agencies.getValue(route.getAgencyId());
         agencies.validate(agency, dao);
         // System.out.println(agency);

         Index<GtfsTrip> trips = dao.getTripByRoute();
         for (GtfsTrip trip : trips.values(route.getRouteId()))
         {

            // System.out.println(trip);
            trips.validate(trip, dao);

            Index<GtfsStopTime> stopTimes = dao.getStopTimeByTrip();
            for (GtfsStopTime stopTime : stopTimes.values(trip.getTripId()))
            {
               // System.out.println(stopTime);
               stopTimes.validate(stopTime, dao);

               GtfsStop stop = _map.get(stopTime.getStopId());
               if (stop == null)
               {
                  Index<GtfsStop> stops = dao.getStopById();
                  stop = stops.getValue(stopTime.getStopId());
                  stops.validate(stop, dao);
                  _map.put(stop.getStopId(), stop);
               }
            }

            Index<GtfsCalendar> calendars = dao.getCalendarByService();
            for (GtfsCalendar calendar : calendars.values(trip.getServiceId()))
            {
               System.out.println(calendar);
               calendars.validate(calendar, dao);
            }

            Index<GtfsCalendarDate> dates = dao.getCalendarDateByService();
            for (GtfsCalendarDate date : dates.values(trip.getServiceId()))
            {
               // System.out.println(date);
               dates.validate(date, dao);

            }

         }
      }

      if (dao.hasTransferImporter())
      {
         Index<GtfsTransfer> transfers = dao.getTransferByFromStop();
         for (GtfsTransfer transfer : transfers)
         {
            transfers.validate(transfer, dao);
            GtfsStop from = dao.getStopById()
                  .getValue(transfer.getFromStopId());
            GtfsStop to = dao.getStopById().getValue(transfer.getToStopId());
         }
      }
      printMemory();

      dao.dispose();

      printMemory();

   }

   public static void printMemory()
   {

      final int MB = 1024 * 1024;
      Runtime runtime = Runtime.getRuntime();

      runtime.gc();

      System.out.println("\n##### Heap utilization statistics [MB] #####");
      System.out.println("Used Memory:"
            + (runtime.totalMemory() - runtime.freeMemory()) / MB);
      System.out.println("Free Memory:" + runtime.freeMemory() / MB);
      System.out.println("Total Memory:" + runtime.totalMemory() / MB);
      System.out.println("Max Memory:" + runtime.maxMemory() / MB);

   }
}
