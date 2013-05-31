/**
 * Projet CHOUETTE
 *
 * ce projet est sous license libre
 * voir LICENSE.txt pour plus de details
 *
 */

package fr.certu.chouette.exchange.gtfs.importer;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import lombok.Getter;

import org.apache.log4j.Logger;

import au.com.bytecode.opencsv.CSVReader;
import fr.certu.chouette.exchange.gtfs.importer.util.DbList;
import fr.certu.chouette.exchange.gtfs.model.GtfsAgency;
import fr.certu.chouette.exchange.gtfs.model.GtfsBean;
import fr.certu.chouette.exchange.gtfs.model.GtfsCalendar;
import fr.certu.chouette.exchange.gtfs.model.GtfsCalendarDate;
import fr.certu.chouette.exchange.gtfs.model.GtfsFrequency;
import fr.certu.chouette.exchange.gtfs.model.GtfsNetwork;
import fr.certu.chouette.exchange.gtfs.model.GtfsRoute;
import fr.certu.chouette.exchange.gtfs.model.GtfsShape;
import fr.certu.chouette.exchange.gtfs.model.GtfsStop;
import fr.certu.chouette.exchange.gtfs.model.GtfsStopTime;
import fr.certu.chouette.exchange.gtfs.model.GtfsTransfer;
import fr.certu.chouette.exchange.gtfs.model.GtfsTrip;
import fr.certu.chouette.exchange.gtfs.model.factory.GtfsAgencyFactory;
import fr.certu.chouette.exchange.gtfs.model.factory.GtfsBeanFactory;
import fr.certu.chouette.exchange.gtfs.model.factory.GtfsCalendarDateFactory;
import fr.certu.chouette.exchange.gtfs.model.factory.GtfsCalendarFactory;
import fr.certu.chouette.exchange.gtfs.model.factory.GtfsFrequencyFactory;
import fr.certu.chouette.exchange.gtfs.model.factory.GtfsRouteFactory;
import fr.certu.chouette.exchange.gtfs.model.factory.GtfsShapeFactory;
import fr.certu.chouette.exchange.gtfs.model.factory.GtfsStopFactory;
import fr.certu.chouette.exchange.gtfs.model.factory.GtfsStopTimeFactory;
import fr.certu.chouette.exchange.gtfs.model.factory.GtfsTransferFactory;
import fr.certu.chouette.exchange.gtfs.model.factory.GtfsTripFactory;

/**
 * @author michel
 *
 */
public class GtfsData 
{
   private static final Logger logger = Logger.getLogger(GtfsData.class);
   @Getter private GtfsNetwork network;
   @Getter private DbList<GtfsAgency> agencies; 
   @Getter private DbList<GtfsCalendar> calendars; 
   @Getter private DbList<GtfsCalendarDate> calendarDates;
   @Getter private DbList<GtfsFrequency> frequencies;
   @Getter private DbList<GtfsRoute> routes; 
   @Getter private DbList<GtfsStop> stops; 
   @Getter private DbList<GtfsStopTime> stopTimes; 
   @Getter private DbList<GtfsTrip> trips;
   @Getter private DbList<GtfsShape> shapes;
   @Getter private DbList<GtfsTransfer> transfers;

   private GtfsAgencyFactory agencyFactory ;
   private GtfsCalendarDateFactory calendarDateFactory ;
   private GtfsCalendarFactory calendarFactory ;
   private GtfsFrequencyFactory frequencyFactory ;
   private GtfsRouteFactory routeFactory ;
   private GtfsShapeFactory shapeFactory ;
   private GtfsStopFactory stopFactory ;
   private GtfsStopTimeFactory stopTimeFactory ;
   private GtfsTransferFactory transferFactory ;
   private GtfsTripFactory tripFactory ;

   private Connection conn = null;
   private String dbName;
   private boolean optimizeMemory;

   public GtfsData(String prefix, boolean optimize)
   {
      optimizeMemory = optimize;
      agencyFactory = new GtfsAgencyFactory();
      calendarDateFactory = new GtfsCalendarDateFactory();
      calendarFactory = new GtfsCalendarFactory();
      frequencyFactory = new GtfsFrequencyFactory();
      routeFactory = new GtfsRouteFactory();
      shapeFactory = new GtfsShapeFactory();
      stopFactory = new GtfsStopFactory();
      stopTimeFactory = new GtfsStopTimeFactory();
      transferFactory = new GtfsTransferFactory();
      tripFactory = new GtfsTripFactory();

      if (optimizeMemory)
      {
         try
         {
            SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
            // TODO paramétrer le répertoire
            dbName = "/tmp/"+prefix+"_"+df.format(Calendar.getInstance().getTime())+"_gtfs.db";
            File f = new File("/tmp");
            if (!f.exists())
            {
               f.mkdirs();
            }
            Class.forName("org.sqlite.JDBC");
            conn = DriverManager.getConnection("jdbc:sqlite:"+dbName);
            conn.setAutoCommit(true);
            Statement stmt = conn.createStatement();
            agencyFactory.initDb(stmt);
            calendarFactory.initDb(stmt);
            calendarDateFactory.initDb(stmt);
            frequencyFactory.initDb(stmt);
            routeFactory.initDb(stmt);
            shapeFactory.initDb(stmt);
            stopFactory.initDb(stmt);
            stopTimeFactory.initDb(stmt);
            transferFactory.initDb(stmt);
            tripFactory.initDb(stmt);
            stmt.close(); 
            conn.setAutoCommit(false);
            f = new File(dbName);
            if (f.exists())
            {
               f.deleteOnExit();
            }
         }
         catch (SQLException e)
         {
            logger.error("fail to create sqlite database",e);
            throw new RuntimeException("gtfs database init failed");
         }
         catch (ClassNotFoundException e)
         {
            logger.error("fail to create sqlite database",e);
            throw new RuntimeException(e.getMessage());
         }
      }

      agencies = new DbList<GtfsAgency>(conn,agencyFactory,optimizeMemory);
      calendarDates = new DbList<GtfsCalendarDate>(conn,calendarDateFactory,optimizeMemory);
      calendars = new DbList<GtfsCalendar>(conn,calendarFactory,optimizeMemory);
      frequencies = new DbList<GtfsFrequency>(conn,frequencyFactory,optimizeMemory);
      routes = new DbList<GtfsRoute>(conn,routeFactory,optimizeMemory);
      stops = new DbList<GtfsStop>(conn,stopFactory,optimizeMemory);
      stopTimes = new DbList<GtfsStopTime>(conn,stopTimeFactory,optimizeMemory);
      trips = new DbList<GtfsTrip>(conn,tripFactory,optimizeMemory);
      shapes = new DbList<GtfsShape>(conn,shapeFactory,optimizeMemory);
      transfers = new DbList<GtfsTransfer>(conn,transferFactory,optimizeMemory);

   }

   public void loadNetwork(String objectIdPrefix) 
   {
      network = new GtfsNetwork(objectIdPrefix);
   }

   private <T extends GtfsBean> void loadGtfsBean(String fileName,String beanName,InputStream input,GtfsBeanFactory<T> factory,DbList<T> beans) throws Exception
   {
      // List<String[]> fileLines = loadFile(input);
      CSVReader reader = new CSVReader(new InputStreamReader(input, "UTF-8"), ',', '"' );
      String[] csvLine = reader.readNext();

      int columns = csvLine.length;

      factory.initHeader(csvLine);

      int lineNumber = 2;
      while ((csvLine = reader.readNext()) != null)
      {
         if (csvLine.length == columns)
         {
            beans.add(factory.getNewGtfsBean(lineNumber++,csvLine));
         }
         else
         {
            if (csvLine.length == 1 && csvLine[0].trim().isEmpty())
            {
               logger.debug(fileName+" : line "+(lineNumber++)+"empty, ignored");
            }
            else
            {
               logger.warn(fileName+" : line "+(lineNumber++)+" has wrong number of columns, ignored");
            }
         }
         if (lineNumber % 10000 == 0)
         {
            logger.debug(fileName+" : csv lines procedeed :"+lineNumber);

         }
      }
      logger.info(beanName+" loaded :"+beans.size());
      reader.close();
      beans.flush();

   }

   public void loadAgencies(InputStream input) throws Exception
   {
      loadGtfsBean("agency.txt","agencies",input,new GtfsAgencyFactory(),agencies);
   }


   public void loadCalendarDates(InputStream input) throws Exception
   {
      loadGtfsBean("calendar_dates.txt","calendar dates",input,new GtfsCalendarDateFactory(),calendarDates);
   }

   public void loadCalendars(InputStream input) throws Exception
   {
      loadGtfsBean("calendar.txt","calendars",input,new GtfsCalendarFactory(),calendars);
   }

   public void loadFrequencies(InputStream input) throws Exception
   {
      loadGtfsBean("frequencies.txt","frequencies",input,new GtfsFrequencyFactory(),frequencies);
   }
   public void loadRoutes(InputStream input) throws Exception
   {
      loadGtfsBean("routes.txt","routes",input,new GtfsRouteFactory(),routes);
   } 
   public void loadStops(InputStream input) throws Exception
   {
      loadGtfsBean("stops.txt","stops",input,new GtfsStopFactory(),stops);
   }
   public void loadStopTimes(InputStream input) throws Exception
   {
      loadGtfsBean("stop_times.txt","stopTimes",input,new GtfsStopTimeFactory(),stopTimes);
   }
   public void loadTrips(InputStream input) throws Exception
   {
      loadGtfsBean("trips.txt","trips",input,new GtfsTripFactory(),trips);
   }
   public void loadShapes(InputStream input) throws Exception
   {
      loadGtfsBean("shapes.txt","shapes",input,new GtfsShapeFactory(),shapes);
   }
   public void loadTransfers(InputStream input) throws Exception
   {
      loadGtfsBean("transfers.txt","transfers",input,new GtfsTransferFactory(),transfers);
   }

   public boolean connect()
   {
      boolean ok = true;
      Map<String,GtfsRoute> routeMap = new HashMap<String, GtfsRoute>();
      Map<String,GtfsAgency> agencyMap = new HashMap<String, GtfsAgency>();
      //      Map<String,GtfsTrip> tripMap = new HashMap<String, GtfsTrip>();
      Map<String,GtfsCalendar> calendarMap = new HashMap<String, GtfsCalendar>();
      Map<String,GtfsStop> stopMap = new HashMap<String, GtfsStop>();
      for (GtfsAgency agency : agencies.getAll()) 
      {
         agencyMap.put(agency.getAgencyId(),agency);
      }
      for (GtfsStop stop : stops.getAll())
      {
         stopMap.put(stop.getStopId(),stop);
      }
      for (GtfsCalendar calendar : calendars.getAll())
      {
         calendarMap.put(calendar.getServiceId(),calendar);
      }
      for (GtfsCalendarDate date : calendarDates.getAll())
      {
         GtfsCalendar calendar = calendarMap.get(date.getServiceId());
         if (calendar == null)
         {
            calendar= new GtfsCalendar();
            calendar.setServiceId(date.getServiceId());
            calendarMap.put(date.getServiceId(),calendar);
            calendars.add(calendar);
            logger.info("calendar created from date "+date.getServiceId());
         }
         
         // calendar.addCalendarDate(date); // connection made by NeptuneConverter

      }
      calendars.flush();
      for (GtfsRoute route : routes.getAll())
      {
         routeMap.put(route.getRouteId(), route);
         GtfsAgency agency = agencyMap.get(route.getAgencyId());
         if (agency == null)
         {
            logger.error("Route line "+route.getFileLineNumber()+" : routeId = "+route.getRouteId()+" has no agency");
            ok = false;
         }
         else
         {
            route.setAgency(agency);
         }
      }
      return ok;
   }

   protected void finalize() throws Throwable
   {
      super.finalize();
      if (optimizeMemory)
      {
         try
         {
            conn.close();
         }
         catch (SQLException e) 
         {
            // let data on cache
         }
      }
   }

}
