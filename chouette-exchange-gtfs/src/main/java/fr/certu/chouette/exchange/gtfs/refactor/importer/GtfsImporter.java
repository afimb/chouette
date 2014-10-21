package fr.certu.chouette.exchange.gtfs.refactor.importer;

import java.nio.file.Paths;

import fr.certu.chouette.exchange.gtfs.refactor.model.GtfsAgency;
import fr.certu.chouette.exchange.gtfs.refactor.model.GtfsCalendar;
import fr.certu.chouette.exchange.gtfs.refactor.model.GtfsCalendarDate;
import fr.certu.chouette.exchange.gtfs.refactor.model.GtfsFrequency;
import fr.certu.chouette.exchange.gtfs.refactor.model.GtfsRoute;
import fr.certu.chouette.exchange.gtfs.refactor.model.GtfsStop;
import fr.certu.chouette.exchange.gtfs.refactor.model.GtfsStopTime;
import fr.certu.chouette.exchange.gtfs.refactor.model.GtfsTransfer;
import fr.certu.chouette.exchange.gtfs.refactor.model.GtfsTrip;

public class GtfsImporter
{
   private String _path;

   private Importer<GtfsAgency> agencyImporter;
   private Importer<GtfsCalendar> calendarImporter;
   private Importer<GtfsCalendarDate> calendarDateImporter;
   private Importer<GtfsFrequency> frequencyImporter;
   private Importer<GtfsRoute> routeyImporter;
   private Importer<GtfsStop> stopImporter;
   private Importer<GtfsStopTime> stopTimeImporter;
   private Importer<GtfsTransfer> transferImporter;
   private Importer<GtfsTrip> tripImporter;

   public GtfsImporter(String path)
   {
      _path = path;
   }

   void dispose()
   {
      if (agencyImporter != null)
      {
         agencyImporter.dispose();
      }
      if (calendarImporter != null)
      {
         calendarImporter.dispose();
      }
      if (calendarDateImporter != null)
      {
         calendarDateImporter.dispose();
      }
      if (frequencyImporter != null)
      {
         frequencyImporter.dispose();
      }
      if (routeyImporter != null)
      {
         routeyImporter.dispose();
      }
      if (stopImporter != null)
      {
         stopImporter.dispose();
      }
      if (stopTimeImporter != null)
      {
         stopTimeImporter.dispose();
      }
      if (transferImporter != null)
      {
         transferImporter.dispose();
      }
      if (tripImporter != null)
      {
         tripImporter.dispose();
      }
   }

   public Importer<GtfsAgency> getAgencyImporter() throws Exception
   {
      if (agencyImporter == null)
      {
         agencyImporter = ImporterFactory.build(Paths.get(_path,
               AgencyImporter.FILENAME).toString());
      }
      return agencyImporter;
   }

   public Importer<GtfsCalendar> getCalendarImporter() throws Exception
   {
      if (calendarImporter == null)
      {
         calendarImporter = ImporterFactory.build(Paths.get(_path,
               CalendarImporter.FILENAME).toString());
      }
      return calendarImporter;
   }

   public Importer<GtfsCalendarDate> getCalendarDateImporter() throws Exception
   {
      if (calendarDateImporter == null)
      {
         calendarDateImporter = ImporterFactory.build(Paths.get(_path,
               CalendarDateImporter.FILENAME).toString());
      }
      return calendarDateImporter;
   }

   public Importer<GtfsFrequency> getFrequencyImporter() throws Exception
   {
      if (frequencyImporter == null)
      {
         frequencyImporter = ImporterFactory.build(Paths.get(_path,
               FrequencyImporter.FILENAME).toString());
      }
      return frequencyImporter;
   }

   public Importer<GtfsRoute> getRouteImporter() throws Exception
   {
      if (routeyImporter == null)
      {
         routeyImporter = ImporterFactory.build(Paths.get(_path,
               RouteImporter.FILENAME).toString());
      }
      return routeyImporter;
   }

   public Importer<GtfsStop> getStopImporter() throws Exception
   {
      if (stopImporter == null)
      {
         stopImporter = ImporterFactory.build(Paths.get(_path,
               StopImporter.FILENAME).toString());
      }
      return stopImporter;
   }

   public Importer<GtfsStopTime> getStopTimeImporter() throws Exception
   {
      if (stopTimeImporter == null)
      {
         stopTimeImporter = ImporterFactory.build(Paths.get(_path,
               StopTimeImporter.FILENAME).toString());
      }
      return stopTimeImporter;
   }

   public Importer<GtfsTransfer> getTransferImporter() throws Exception
   {
      if (transferImporter == null)
      {
         transferImporter = ImporterFactory.build(Paths.get(_path,
               TransferImporter.FILENAME).toString());
      }
      return transferImporter;
   }

   public Importer<GtfsTrip> getTripImporter() throws Exception
   {
      if (tripImporter == null)
      {
         tripImporter = ImporterFactory.build(Paths.get(_path,
               TripImporter.FILENAME).toString());
      }
      return tripImporter;
   }

}
