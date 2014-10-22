package fr.certu.chouette.exchange.gtfs.refactor.importer;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

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
   private String _path;
   private Map<String, Importer<GtfsObject>> _map = new HashMap<String, Importer<GtfsObject>>();

   public GtfsImporter(String path)
   {
      _path = path;
   }

   public void dispose()
   {
      for (Importer importer : _map.values())
      {
         importer.dispose();
      }
      _map.clear();
   }

   public Importer geImporter(String name)
   {
      Importer importer = _map.get(name);

      if (importer == null)
      {
         try
         {
            importer = ImporterFactory.build(Paths.get(_path, name).toString());
            _map.put(name, importer);
         } catch (ClassNotFoundException | IOException e)
         {
            throw new GtfsException(e);
         }
        
      }
      return importer;
   }

   public Importer<GtfsAgency> getAgencyImporter()
   {
      return geImporter(AgencyImporter.FILENAME);
   }

   public Importer<GtfsCalendar> getCalendarImporter()
   {
      return geImporter(CalendarImporter.FILENAME);
   }

   public Importer<GtfsCalendarDate> getCalendarDateImporter()
   {
      return geImporter(CalendarDatesImporter.FILENAME);
   }

   public Importer<GtfsFrequency> getFrequencyImporter()
   {
      return geImporter(FrequencyImporter.FILENAME);
   }

   public Importer<GtfsRoute> getRouteImporter()
   {
      return geImporter(RoutesImporter.FILENAME);
   }

   public Importer<GtfsStop> getStopImporter()
   {
      return geImporter(StopsImporter.FILENAME);
   }

   public Importer<GtfsStopTime> getStopTimeImporter()
   {
      return geImporter(StopsImporter.FILENAME);
   }

   public Importer<GtfsTransfer> getTransferImporter()
   {
      return geImporter(TransfersImporter.FILENAME);
   }

   public Importer<GtfsTrip> getTripImporter()
   {
      return geImporter(TripsImporter.FILENAME);
   }

}
