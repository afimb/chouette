package fr.certu.chouette.exchange.gtfs.refactor.exporter;

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

public class GtfsExporter
{
   private String _path;

   private Exporter<GtfsAgency> agencyExporter;
   private Exporter<GtfsCalendar> calendarExporter;
   private Exporter<GtfsCalendarDate> calendarDateExporter;
   private Exporter<GtfsFrequency> frequencyExporter;
   private Exporter<GtfsRoute> routeyExporter;
   private Exporter<GtfsStop> stopExporter;
   private Exporter<GtfsStopTime> stopTimeExporter;
   private Exporter<GtfsTransfer> transferExporter;
   private Exporter<GtfsTrip> tripExporter;

   public GtfsExporter(String path)
   {
      _path = path;
   }

   public Exporter<GtfsAgency> getAgencyExporter() throws Exception
   {
      if (agencyExporter == null)
      {
         agencyExporter = ExporterFactory.build(Paths.get(_path,
               AgencyExporter.FILENAME).toString());
      }
      return agencyExporter;
   }

   public Exporter<GtfsCalendar> getCalendarExporter() throws Exception
   {
      if (calendarExporter == null)
      {
         calendarExporter = ExporterFactory.build(Paths.get(_path,
               CalendarExporter.FILENAME).toString());
      }
      return calendarExporter;
   }

   public Exporter<GtfsCalendarDate> getCalendarDateExporter() throws Exception
   {
      if (calendarDateExporter == null)
      {
         calendarDateExporter = ExporterFactory.build(Paths.get(_path,
               CalendarDateExporter.FILENAME).toString());
      }
      return calendarDateExporter;
   }

   public Exporter<GtfsFrequency> getFrequencyExporter() throws Exception
   {
      if (frequencyExporter == null)
      {
         frequencyExporter = ExporterFactory.build(Paths.get(_path,
               FrequencyExporter.FILENAME).toString());
      }
      return frequencyExporter;
   }

   public Exporter<GtfsRoute> getRouteExporter() throws Exception
   {
      if (routeyExporter == null)
      {
         routeyExporter = ExporterFactory.build(Paths.get(_path,
               RouteExporter.FILENAME).toString());
      }
      return routeyExporter;
   }

   public Exporter<GtfsStop> getStopExporter() throws Exception
   {
      if (stopExporter == null)
      {
         stopExporter = ExporterFactory.build(Paths.get(_path,
               StopExporter.FILENAME).toString());
      }
      return stopExporter;
   }

   public Exporter<GtfsStopTime> getStopTimeExporter() throws Exception
   {
      if (stopTimeExporter == null)
      {
         stopTimeExporter = ExporterFactory.build(Paths.get(_path,
               StopTimeExporter.FILENAME).toString());
      }
      return stopTimeExporter;
   }

   public Exporter<GtfsTransfer> getTransferExporter() throws Exception
   {
      if (transferExporter == null)
      {
         transferExporter = ExporterFactory.build(Paths.get(_path,
               TransferExporter.FILENAME).toString());
      }
      return transferExporter;
   }

   public Exporter<GtfsTrip> getTripExporter() throws Exception
   {
      if (tripExporter == null)
      {
         tripExporter = ExporterFactory.build(Paths.get(_path,
               TripExporter.FILENAME).toString());
      }
      return tripExporter;
   }

}
