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

   private Importer<GtfsAgency> agencyDao;
   private Importer<GtfsCalendar> calendarDao;
   private Importer<GtfsCalendarDate> calendarDateDao;
   private Importer<GtfsFrequency> frequencyDao;
   private Importer<GtfsRoute> routeyDao;
   private Importer<GtfsStop> stopDao;
   private Importer<GtfsStopTime> stopTimeDao;
   private Importer<GtfsTransfer> transferDao;
   private Importer<GtfsTrip> tripDao;

   public GtfsImporter(String path)
   {
      _path = path;
   }

   public Importer<GtfsAgency> getAgencyDao() throws Exception
   {
      if (agencyDao == null)
      {
         agencyDao = ImporterFactory.build(Paths
               .get(_path, AgencyImporter.FILENAME).toString());
      }
      return agencyDao;
   }

   public Importer<GtfsCalendar> getCalendarDao() throws Exception
   {
      if (calendarDao == null)
      {
         calendarDao = ImporterFactory.build(Paths.get(_path,
               CalendarImporter.FILENAME).toString());
      }
      return calendarDao;
   }

   public Importer<GtfsCalendarDate> getCalendarDateDao() throws Exception
   {
      if (calendarDateDao == null)
      {
         calendarDateDao = ImporterFactory.build(Paths.get(_path,
               CalendarDateImporter.FILENAME).toString());
      }
      return calendarDateDao;
   }

   public Importer<GtfsFrequency> getFrequencyDao() throws Exception
   {
      if (frequencyDao == null)
      {
         frequencyDao = ImporterFactory.build(Paths.get(_path,
               FrequencyImporter.FILENAME).toString());
      }
      return frequencyDao;
   }

   public Importer<GtfsRoute> getRouteDao() throws Exception
   {
      if (routeyDao == null)
      {
         routeyDao = ImporterFactory.build(Paths.get(_path, RouteImporter.FILENAME)
               .toString());
      }
      return routeyDao;
   }

   public Importer<GtfsStop> getStopDao() throws Exception
   {
      if (stopDao == null)
      {
         stopDao = ImporterFactory.build(Paths.get(_path, StopImporter.FILENAME)
               .toString());
      }
      return stopDao;
   }

   public Importer<GtfsStopTime> getStopTimeDao() throws Exception
   {
      if (stopTimeDao == null)
      {
         stopTimeDao = ImporterFactory.build(Paths.get(_path,
               StopTimeImporter.FILENAME).toString());
      }
      return stopTimeDao;
   }

   public Importer<GtfsTransfer> getTransferDao() throws Exception
   {
      if (transferDao == null)
      {
         transferDao = ImporterFactory.build(Paths.get(_path,
               TransferImporter.FILENAME).toString());
      }
      return transferDao;
   }

   public Importer<GtfsTrip> getTripDao() throws Exception
   {
      if (tripDao == null)
      {
         tripDao = ImporterFactory.build(Paths.get(_path, TripImporter.FILENAME)
               .toString());
      }
      return tripDao;
   }

}
