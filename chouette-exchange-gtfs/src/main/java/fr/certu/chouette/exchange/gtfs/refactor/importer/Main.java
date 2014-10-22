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

   private void parse(String name)
   {

      Importer<GtfsStopTime> parser;
      try
      {
         parser = ImporterFactory.build(name);

         Monitor monitor = MonitorFactory.start();
         _count = 0;

         for (GtfsStopTime bean : parser)
         {
            // System.out.println("[DSU] value : " + bean);
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

//          for (Iterator<GtfsStopTime> values = parser
//          .valuesIterator("10052"); values.hasNext();) {
//          GtfsStopTime bean = values.next();
//          System.out.println("[DSU] value : " + bean);
//          _count++;
//          }

         // System.out.println("[DSU] !!!! value : " +
         // parser.getValue("10052"));
         parser.dispose();
         log.debug("[DSU] get " + _count + " object " + monitor.stop());

      } catch (Exception e)
      {
         // TODO Auto-generated catch block
         e.printStackTrace();
      }
   }

   private void execute()
   {
      parse(PATH + StopTimesImporter.FILENAME);
      // parse(PATH + TripsParser.FILENAME);
      // parse(PATH + RoutesParser.FILENAME);
      // parse(PATH + StopsParser.FILENAME);
      // parse(PATH + CalendarDatesParser.FILENAME);
      // parse(PATH + CalendarParser.FILENAME);
      // parse(PATH + TransfersParser.FILENAME);
      // parse(PATH + AgencyParser.FILENAME);

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
