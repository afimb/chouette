package fr.certu.chouette.exchange.gtfs.refactor.importer;

import lombok.extern.log4j.Log4j;

import org.apache.log4j.BasicConfigurator;

import com.jamonapi.Monitor;
import com.jamonapi.MonitorFactory;

@Log4j
public class Main
{

   private int _count;
   private static final String PATH = "/opt/tmp/RATP/";

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

         // for (Iterator<String> keys = parser.keyIterator();
         // keys.hasNext();) {
         // String key = keys.next();
         // for (Iterator<StopTime> values = parser.valuesIterator(key);
         // values
         // .hasNext();) {
         // StopTime bean = values.next();
         // // System.out.println("[DSU] value : " + bean);
         // _count++;
         // }
         // }

         // for (Iterator<GtfsStopTime> values = parser.valuesIterator("10052");
         // values
         // .hasNext();)
         // {
         // GtfsStopTime bean = values.next();
         // System.out.println("[DSU] value : " + bean);
         // _count++;
         // }

         // System.out.println("[DSU] !!!! value : " +
         // parser.getValue("10052"));
         log.debug("[DSU] get " + _count + " object " + monitor.stop());

      } catch (Exception e)
      {
         // TODO Auto-generated catch block
         e.printStackTrace();
      }
   }

   private void execute()
   {
      GtfsImporter dao = new GtfsImporter(PATH);
      parse(dao, StopTimeByTrip.FILENAME, StopTimeByTrip.FILENAME,
            StopTimeByTrip.class);
      parse(dao, TripById.FILENAME, TripById.FILENAME, TripById.class);
      parse(dao, "trip_by_route", TripById.FILENAME, TripByRoute.class);
      parse(dao, "trip_by_service", TripById.FILENAME, TripByRoute.class);

      parse(dao, RouteById.FILENAME, RouteById.FILENAME, RouteById.class);
      parse(dao, StopById.FILENAME, StopById.FILENAME, StopById.class);
      parse(dao, CalendarDateByService.FILENAME,
            CalendarDateByService.FILENAME, CalendarDateByService.class);
      parse(dao, CalendarByService.FILENAME, CalendarByService.FILENAME,
            CalendarByService.class);
      parse(dao, TransferByFromStop.FILENAME, TransferByFromStop.FILENAME,
            TransferByFromStop.class);
      parse(dao, AgencyById.FILENAME, AgencyById.FILENAME, AgencyById.class);
      // parse(dao, FrequenciesImporter.FILENAME,FrequenciesImporter.FILENAME,
      // FrequenciesImporter.class);

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
