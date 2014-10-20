package fr.certu.chouette.exchange.gtfs.refactor.marshaller;

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

public class Marshaller
{
   private String _path;

   private GtfsMarshaller<GtfsAgency> agencyMarshaller;
   private GtfsMarshaller<GtfsCalendar> calendarMarshaller;
   private GtfsMarshaller<GtfsCalendarDate> calendarDateMarshaller;
   private GtfsMarshaller<GtfsFrequency> frequencyMarshaller;
   private GtfsMarshaller<GtfsRoute> routeyMarshaller;
   private GtfsMarshaller<GtfsStop> stopMarshaller;
   private GtfsMarshaller<GtfsStopTime> stopTimeMarshaller;
   private GtfsMarshaller<GtfsTransfer> transferMarshaller;
   private GtfsMarshaller<GtfsTrip> tripMarshaller;

   public Marshaller(String path)
   {
      _path = path;
   }

   public GtfsMarshaller<GtfsAgency> getAgencyMarshaller() throws Exception
   {
      if (agencyMarshaller == null)
      {
         agencyMarshaller = MarshallerFactory.build(Paths.get(_path,
               AgencyMarshaller.FILENAME).toString());
      }
      return agencyMarshaller;
   }

   public GtfsMarshaller<GtfsCalendar> getCalendarMarshaller() throws Exception
   {
      if (calendarMarshaller == null)
      {
         calendarMarshaller = MarshallerFactory.build(Paths.get(_path,
               CalendarMarshaller.FILENAME).toString());
      }
      return calendarMarshaller;
   }

   public GtfsMarshaller<GtfsCalendarDate> getCalendarDateMarshaller()
         throws Exception
   {
      if (calendarDateMarshaller == null)
      {
         calendarDateMarshaller = MarshallerFactory.build(Paths.get(_path,
               CalendarDateMarshaller.FILENAME).toString());
      }
      return calendarDateMarshaller;
   }

   public GtfsMarshaller<GtfsFrequency> getFrequencyMarshaller()
         throws Exception
   {
      if (frequencyMarshaller == null)
      {
         frequencyMarshaller = MarshallerFactory.build(Paths.get(_path,
               FrequencyMarshaller.FILENAME).toString());
      }
      return frequencyMarshaller;
   }

   public GtfsMarshaller<GtfsRoute> getRouteMarshaller() throws Exception
   {
      if (routeyMarshaller == null)
      {
         routeyMarshaller = MarshallerFactory.build(Paths.get(_path,
               RouteMarshaller.FILENAME).toString());
      }
      return routeyMarshaller;
   }

   public GtfsMarshaller<GtfsStop> getStopMarshaller() throws Exception
   {
      if (stopMarshaller == null)
      {
         stopMarshaller = MarshallerFactory.build(Paths.get(_path,
               StopMarshaller.FILENAME).toString());
      }
      return stopMarshaller;
   }

   public GtfsMarshaller<GtfsStopTime> getStopTimeMarshaller() throws Exception
   {
      if (stopTimeMarshaller == null)
      {
         stopTimeMarshaller = MarshallerFactory.build(Paths.get(_path,
               StopTimeMarshaller.FILENAME).toString());
      }
      return stopTimeMarshaller;
   }

   public GtfsMarshaller<GtfsTransfer> getTransferMarshaller() throws Exception
   {
      if (transferMarshaller == null)
      {
         transferMarshaller = MarshallerFactory.build(Paths.get(_path,
               TransferMarshaller.FILENAME).toString());
      }
      return transferMarshaller;
   }

   public GtfsMarshaller<GtfsTrip> getTripMarshaller() throws Exception
   {
      if (tripMarshaller == null)
      {
         tripMarshaller = MarshallerFactory.build(Paths.get(_path,
               TripMarshaller.FILENAME).toString());
      }
      return tripMarshaller;
   }

}
