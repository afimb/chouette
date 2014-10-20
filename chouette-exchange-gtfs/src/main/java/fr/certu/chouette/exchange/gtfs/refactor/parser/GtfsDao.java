package fr.certu.chouette.exchange.gtfs.refactor.parser;

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

public class GtfsDao
{
   private String _path;

   private GtfsParser<GtfsAgency> agencyDao;
   private GtfsParser<GtfsCalendar> calendarDao;
   private GtfsParser<GtfsCalendarDate> calendarDateDao;
   private GtfsParser<GtfsFrequency> frequencyDao;
   private GtfsParser<GtfsRoute> routeyDao;
   private GtfsParser<GtfsStop> stopDao;
   private GtfsParser<GtfsStopTime> stopTimeDao;
   private GtfsParser<GtfsTransfer> transferDao;
   private GtfsParser<GtfsTrip> tripDao;

   public GtfsDao(String path)
   {
      _path = path;
   }

   public GtfsParser<GtfsAgency> getAgencyDao() throws Exception
   {
      if (agencyDao == null)
      {
         agencyDao = ParserFactory.build(Paths
               .get(_path, AgencyParser.FILENAME).toString());
      }
      return agencyDao;
   }

   public GtfsParser<GtfsCalendar> getCalendarDao() throws Exception
   {
      if (calendarDao == null)
      {
         calendarDao = ParserFactory.build(Paths.get(_path,
               CalendarParser.FILENAME).toString());
      }
      return calendarDao;
   }

   public GtfsParser<GtfsCalendarDate> getCalendarDateDao() throws Exception
   {
      if (calendarDateDao == null)
      {
         calendarDateDao = ParserFactory.build(Paths.get(_path,
               CalendarDateParser.FILENAME).toString());
      }
      return calendarDateDao;
   }

   public GtfsParser<GtfsFrequency> getFrequencyDao() throws Exception
   {
      if (frequencyDao == null)
      {
         frequencyDao = ParserFactory.build(Paths.get(_path,
               FrequencyParser.FILENAME).toString());
      }
      return frequencyDao;
   }

   public GtfsParser<GtfsRoute> getRouteDao() throws Exception
   {
      if (routeyDao == null)
      {
         routeyDao = ParserFactory.build(Paths.get(_path, RouteParser.FILENAME)
               .toString());
      }
      return routeyDao;
   }

   public GtfsParser<GtfsStop> getStopDao() throws Exception
   {
      if (stopDao == null)
      {
         stopDao = ParserFactory.build(Paths.get(_path, StopParser.FILENAME)
               .toString());
      }
      return stopDao;
   }

   public GtfsParser<GtfsStopTime> getStopTimeDao() throws Exception
   {
      if (stopTimeDao == null)
      {
         stopTimeDao = ParserFactory.build(Paths.get(_path,
               StopTimeParser.FILENAME).toString());
      }
      return stopTimeDao;
   }

   public GtfsParser<GtfsTransfer> getTransferDao() throws Exception
   {
      if (transferDao == null)
      {
         transferDao = ParserFactory.build(Paths.get(_path,
               TransferParser.FILENAME).toString());
      }
      return transferDao;
   }

   public GtfsParser<GtfsTrip> getTripDao() throws Exception
   {
      if (tripDao == null)
      {
         tripDao = ParserFactory.build(Paths.get(_path, TripParser.FILENAME)
               .toString());
      }
      return tripDao;
   }

}
