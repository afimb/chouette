package fr.certu.chouette.exchange.gtfs.refactor.importer;


import java.io.File;
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
   
   public boolean hasAgencyImporter()
   {
      return hasImporter(AgencyImporter.FILENAME);
   }

   public boolean hasCalendarImporter()
   {
      return hasImporter(CalendarImporter.FILENAME);
   }

   public boolean hasCalendarDateImporter()
   {
      return hasImporter(CalendarDatesImporter.FILENAME);
   }

   public boolean hasFrequencyImporter()
   {
      return hasImporter(FrequenciesImporter.FILENAME);
   }

   public boolean hasRouteImporter()
   {
      return hasImporter(RoutesImporter.FILENAME);
   }

   public boolean hasStopImporter()
   {
      return hasImporter(StopsImporter.FILENAME);
   }

   public boolean hasStopTimeImporter()
   {
      return hasImporter(StopTimesImporter.FILENAME);
   }

   public boolean hasTransferImporter()
   {
      return hasImporter(TransfersImporter.FILENAME);
   }

   public boolean hasTripImporter()
   {
      return hasImporter(TripsImporter.FILENAME);
   }

   private boolean hasImporter(String filename)
   {
      File f = new File(_path,filename);
      return f.exists();
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
      return geImporter(FrequenciesImporter.FILENAME);
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
