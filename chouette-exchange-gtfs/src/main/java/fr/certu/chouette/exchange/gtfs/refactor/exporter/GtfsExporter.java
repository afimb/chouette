package fr.certu.chouette.exchange.gtfs.refactor.exporter;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import lombok.extern.log4j.Log4j;
import fr.certu.chouette.exchange.gtfs.refactor.importer.Context;
import fr.certu.chouette.exchange.gtfs.refactor.importer.GtfsException;
import fr.certu.chouette.exchange.gtfs.refactor.importer.GtfsException.ERROR;
import fr.certu.chouette.exchange.gtfs.refactor.model.GtfsAgency;
import fr.certu.chouette.exchange.gtfs.refactor.model.GtfsCalendar;
import fr.certu.chouette.exchange.gtfs.refactor.model.GtfsCalendarDate;
import fr.certu.chouette.exchange.gtfs.refactor.model.GtfsFrequency;
import fr.certu.chouette.exchange.gtfs.refactor.model.GtfsObject;
import fr.certu.chouette.exchange.gtfs.refactor.model.GtfsRoute;
import fr.certu.chouette.exchange.gtfs.refactor.model.GtfsStop;
import fr.certu.chouette.exchange.gtfs.refactor.model.GtfsStopTime;
import fr.certu.chouette.exchange.gtfs.refactor.model.GtfsTransfer;
import fr.certu.chouette.exchange.gtfs.refactor.model.GtfsTrip;

@Log4j
public class GtfsExporter implements GtfsExporterInterface
{
   public static enum EXPORTER
   {
      AGENCY, CALENDAR, CALENDAR_DATE, FREQUENCY, ROUTE, STOP, STOP_TIME, TRANSFER, TRIP;
   }

   private String _path;
   private Map<String, Exporter<GtfsObject>> _map = new HashMap<String, Exporter<GtfsObject>>();

   public GtfsExporter(String path)
   {
      _path = path;
   }

   public void dispose()
   {
      for (Exporter exporter : _map.values())
      {
         try
         {
            exporter.dispose();
         } catch (IOException e)
         {
            log.error(e);
         }
      }
      _map.clear();
   }

   public Exporter getExporter(String name, String path, Class clazz)
   {
      Exporter result = _map.get(name);

      if (result == null)
      {
         try
         {
            result = ExporterFactory.build(Paths.get(_path, path).toString(),
                  clazz.getName());
            _map.put(name, result);
         } catch (ClassNotFoundException | IOException e)
         {
            Context context = new Context();
            context.put(Context.PATH, _path);
            context.put(Context.ERROR, ERROR.SYSTEM);
            throw new GtfsException(context, e);
         }

      }
      return result;
   }

   public Exporter<GtfsAgency> getAgencyExporter() throws Exception
   {
      return getExporter(EXPORTER.AGENCY.name(), AgencyExporter.FILENAME,
            AgencyExporter.class);

   }

   public Exporter<GtfsCalendarDate> getCalendarDateExporter() throws Exception
   {
      return getExporter(EXPORTER.CALENDAR_DATE.name(),
            CalendarDateExporter.FILENAME, CalendarDateExporter.class);
   }

   public Exporter<GtfsCalendar> getCalendarExporter() throws Exception
   {
      return getExporter(EXPORTER.CALENDAR.name(), CalendarExporter.FILENAME,
            CalendarExporter.class);
   }

   public Exporter<GtfsFrequency> getFrequencyExporter() throws Exception
   {
      return getExporter(EXPORTER.FREQUENCY.name(), FrequencyExporter.FILENAME,
            FrequencyExporter.class);
   }

   public Exporter<GtfsRoute> getRouteExporter() throws Exception
   {
      return getExporter(EXPORTER.ROUTE.name(), RouteExporter.FILENAME,
            RouteExporter.class);
   }

   public Exporter<GtfsStop> getStopExporter() throws Exception
   {
      return getExporter(EXPORTER.STOP.name(), StopExporter.FILENAME,
            StopExporter.class);
   }
   
   public Exporter<GtfsStop> getStopExtendedExporter() throws Exception
   {
      return getExporter(EXPORTER.STOP.name(), StopExporter.FILENAME,
            StopExtendedExporter.class);
   }

   public Exporter<GtfsStopTime> getStopTimeExporter() throws Exception
   {
      return getExporter(EXPORTER.STOP_TIME.name(), StopTimeExporter.FILENAME,
            StopTimeExporter.class);
   }

   public Exporter<GtfsTransfer> getTransferExporter() throws Exception
   {
      return getExporter(EXPORTER.TRANSFER.name(), TransferExporter.FILENAME,
            TransferExporter.class);
   }

   public Exporter<GtfsTrip> getTripExporter() throws Exception
   {
      return getExporter(EXPORTER.TRIP.name(), TripExporter.FILENAME,
            TripExporter.class);
   }

}
