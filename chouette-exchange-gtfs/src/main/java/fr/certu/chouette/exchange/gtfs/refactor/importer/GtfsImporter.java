package fr.certu.chouette.exchange.gtfs.refactor.importer;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

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

public class GtfsImporter
{
   public static enum INDEX
   {
      AGENCY_BY_ID, CALENDAR_BY_SERVICE, CALENDAR_DATE_BY_SERVICE, FREQUENCY_BY_TRIP, ROUTE_BY_ID, STOP_BY_ID, STOP_TIME_BY_TRIP, TRANSFER_BY_FROM_STOP, TRIP_BY_ID, TRIP_BY_ROUTE, TRIP_BY_SERVICE;
   }

   private String _path;
   private Map<String, Index<GtfsObject>> _map = new HashMap<String, Index<GtfsObject>>();

   public GtfsImporter(String path)
   {
      _path = path;
   }

   public void dispose()
   {
      for (Index importer : _map.values())
      {
         importer.dispose();
      }
      _map.clear();
   }

   public Index geImporter(String name, String path, Class clazz)
   {
      Index importer = _map.get(name);

      if (importer == null)
      {
         try
         {
            importer = IndexFactory.build(Paths.get(_path, path).toString(),
                  clazz.getName());
            _map.put(name, importer);
         } catch (ClassNotFoundException | IOException e)
         {
            Context context = new Context();
            context.put(Context.PATH, _path);
            context.put(Context.ERROR, ERROR.SYSTEM);
            throw new GtfsException(context, e);
         }

      }
      return importer;
   }

   public boolean hasAgencyImporter()
   {
      return hasImporter(AgencyById.FILENAME);
   }

   public boolean hasCalendarImporter()
   {
      return hasImporter(CalendarByService.FILENAME);
   }

   public boolean hasCalendarDateImporter()
   {
      return hasImporter(CalendarDateByService.FILENAME);
   }

   public boolean hasFrequencyImporter()
   {
      return hasImporter(FrequencyByTrip.FILENAME);
   }

   public boolean hasRouteImporter()
   {
      return hasImporter(RouteById.FILENAME);
   }

   public boolean hasStopImporter()
   {
      return hasImporter(StopById.FILENAME);
   }

   public boolean hasStopTimeImporter()
   {
      return hasImporter(StopTimeByTrip.FILENAME);
   }

   public boolean hasTransferImporter()
   {
      return hasImporter(TransferByFromStop.FILENAME);
   }

   public boolean hasTripImporter()
   {
      return hasImporter(TripById.FILENAME);
   }

   private boolean hasImporter(String filename)
   {
      File f = new File(_path, filename);
      return f.exists();
   }

   public Index<GtfsAgency> getAgencyById()
   {
      return geImporter(INDEX.AGENCY_BY_ID.name(), AgencyById.FILENAME,
            AgencyById.class);
   }

   public Index<GtfsCalendar> getCalendarByService()
   {
      return geImporter(INDEX.CALENDAR_BY_SERVICE.name(),
            CalendarByService.FILENAME, CalendarByService.class);
   }

   public Index<GtfsCalendarDate> getCalendarDateByService()
   {
      return geImporter(INDEX.CALENDAR_DATE_BY_SERVICE.name(),
            CalendarDateByService.FILENAME, CalendarDateByService.class);
   }

   public Index<GtfsFrequency> getFrequencyByTrip()
   {
      return geImporter(INDEX.FREQUENCY_BY_TRIP.name(),
            FrequencyByTrip.FILENAME, FrequencyByTrip.class);
   }

   public Index<GtfsRoute> getRouteById()
   {
      return geImporter(INDEX.ROUTE_BY_ID.name(), RouteById.FILENAME,
            RouteById.class);
   }

   public Index<GtfsStop> getStopById()
   {
      return geImporter(INDEX.STOP_BY_ID.name(), StopById.FILENAME,
            StopById.class);
   }

   public Index<GtfsStopTime> getStopTimeByTrip()
   {
      return geImporter(INDEX.STOP_TIME_BY_TRIP.name(),
            StopTimeByTrip.FILENAME, StopTimeByTrip.class);
   }

   public Index<GtfsTransfer> getTransferByFromStop()
   {
      return geImporter(INDEX.TRANSFER_BY_FROM_STOP.name(),
            TransferByFromStop.FILENAME, TransferByFromStop.class);
   }

   public Index<GtfsTrip> getTripById()
   {
      return geImporter(INDEX.TRIP_BY_ID.name(), TripById.FILENAME,
            TripById.class);
   }

   public Index<GtfsTrip> getTripByRoute()
   {
      return geImporter(INDEX.TRIP_BY_ROUTE.name(), TripById.FILENAME,
            TripByRoute.class);
   }

   public Index<GtfsTrip> getTripByService()
   {
      return geImporter(INDEX.TRIP_BY_SERVICE.name(), TripById.FILENAME,
            TripByRoute.class);
   }

}
