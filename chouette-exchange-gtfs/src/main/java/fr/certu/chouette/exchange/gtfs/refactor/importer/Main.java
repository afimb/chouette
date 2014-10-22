package fr.certu.chouette.exchange.gtfs.refactor.importer;

import lombok.extern.log4j.Log4j;

import org.apache.log4j.BasicConfigurator;

import com.jamonapi.Monitor;
import com.jamonapi.MonitorFactory;

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

   private void parse(GtfsImporter dao, String name)
   {

      try
      {

         Monitor monitor = MonitorFactory.start();
         Importer parser = dao.geImporter(name);
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
      parse(dao, StopTimesImporter.FILENAME);
      parse(dao, TripsImporter.FILENAME);
      parse(dao, RoutesImporter.FILENAME);
      parse(dao, StopsImporter.FILENAME);
      parse(dao, CalendarDatesImporter.FILENAME);
      parse(dao, CalendarImporter.FILENAME);
      parse(dao, TransfersImporter.FILENAME);
      parse(dao, AgencyImporter.FILENAME);

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
