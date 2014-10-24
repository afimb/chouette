package fr.certu.chouette.exchange.gtfs.refactor.importer;

import java.util.Iterator;

import lombok.extern.log4j.Log4j;

import org.apache.log4j.BasicConfigurator;

import com.jamonapi.Monitor;
import com.jamonapi.MonitorFactory;

import fr.certu.chouette.exchange.gtfs.refactor.model.GtfsStopTime;

@Log4j
public class Main
{

   private int _count;
   private static final String PATH = "/opt/tmp/RENNES/";

   public static void main(String[] args)
   {
      BasicConfigurator.configure();
      Runtime runtime = Runtime.getRuntime();
      Main main = new Main();

      main.printMemory(runtime);
      Monitor monitor = MonitorFactory.start();
      main.execute();
      log.debug("[DSU] total : " + monitor.stop());
      main.printMemory(runtime);
   }

   private void parse(GtfsImporter dao, String name, String path, Class clazz)
   {

      try
      {

         Monitor monitor = MonitorFactory.start();
         Index parser = dao.geImporter(name, path, clazz);
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
      parse(dao, GtfsImporter.INDEX.TRIP_BY_SERVICE.name(), TripById.FILENAME,
            TripByService.class);

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

//      Monitor monitor = MonitorFactory.start();
//      Index<GtfsStopTime> parser = dao.getStopTimeByTrip();
//      for (Iterator<GtfsStopTime> values = parser.valuesIterator("6113969740881054"); values
//            .hasNext();)
//      {
//         GtfsStopTime bean = values.next();
//         System.out.println("[DSU] value : " + bean);
//         _count++;
//      }
//      log.debug("[DSU] get " + _count + " object " + monitor.stop());
//
//      monitor = MonitorFactory.start();
//      System.out.println("[DSU] !!!! value : "
//            + dao.getStopById().getValue("4035320"));
//      log.debug("[DSU] get " + _count + " object " + monitor.stop());

      dao.dispose();

   }

   public static void printMemory(Runtime runtime)
   {

      final int MB = 1024 * 1024;

      System.out.println("\n##### Heap utilization statistics [MB] #####");
      System.out.println("Used Memory:"
            + (runtime.totalMemory() - runtime.freeMemory()) / MB);
      System.out.println("Free Memory:" + runtime.freeMemory() / MB);
      System.out.println("Total Memory:" + runtime.totalMemory() / MB);
      System.out.println("Max Memory:" + runtime.maxMemory() / MB);

   }

}
