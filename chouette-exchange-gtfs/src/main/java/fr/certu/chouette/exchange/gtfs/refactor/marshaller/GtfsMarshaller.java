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

public class GtfsMarshaller
{
   private String _path;

   private Marshaller<GtfsAgency> agencyMarshaller;
   private Marshaller<GtfsCalendar> calendarMarshaller;
   private Marshaller<GtfsCalendarDate> calendarDateMarshaller;
   private Marshaller<GtfsFrequency> frequencyMarshaller;
   private Marshaller<GtfsRoute> routeyMarshaller;
   private Marshaller<GtfsStop> stopMarshaller;
   private Marshaller<GtfsStopTime> stopTimeMarshaller;
   private Marshaller<GtfsTransfer> transferMarshaller;
   private Marshaller<GtfsTrip> tripMarshaller;

   public GtfsMarshaller(String path)
   {
      _path = path;
   }

   public Marshaller<GtfsAgency> getAgencyMarshaller() throws Exception
   {
      if (agencyMarshaller == null)
      {
         agencyMarshaller = MarshallerFactory.build(Paths.get(_path,
               AgencyMarshaller.FILENAME).toString());
      }
      return agencyMarshaller;
   }

   public Marshaller<GtfsCalendar> getCalendarMarshaller() throws Exception
   {
      if (calendarMarshaller == null)
      {
         calendarMarshaller = MarshallerFactory.build(Paths.get(_path,
               CalendarMarshaller.FILENAME).toString());
      }
      return calendarMarshaller;
   }

   public Marshaller<GtfsCalendarDate> getCalendarDateMarshaller()
         throws Exception
   {
      if (calendarDateMarshaller == null)
      {
         calendarDateMarshaller = MarshallerFactory.build(Paths.get(_path,
               CalendarDateMarshaller.FILENAME).toString());
      }
      return calendarDateMarshaller;
   }

   public Marshaller<GtfsFrequency> getFrequencyMarshaller()
         throws Exception
   {
      if (frequencyMarshaller == null)
      {
         frequencyMarshaller = MarshallerFactory.build(Paths.get(_path,
               FrequencyMarshaller.FILENAME).toString());
      }
      return frequencyMarshaller;
   }

   public Marshaller<GtfsRoute> getRouteMarshaller() throws Exception
   {
      if (routeyMarshaller == null)
      {
         routeyMarshaller = MarshallerFactory.build(Paths.get(_path,
               RouteMarshaller.FILENAME).toString());
      }
      return routeyMarshaller;
   }

   public Marshaller<GtfsStop> getStopMarshaller() throws Exception
   {
      if (stopMarshaller == null)
      {
         stopMarshaller = MarshallerFactory.build(Paths.get(_path,
               StopMarshaller.FILENAME).toString());
      }
      return stopMarshaller;
   }

   public Marshaller<GtfsStopTime> getStopTimeMarshaller() throws Exception
   {
      if (stopTimeMarshaller == null)
      {
         stopTimeMarshaller = MarshallerFactory.build(Paths.get(_path,
               StopTimeMarshaller.FILENAME).toString());
      }
      return stopTimeMarshaller;
   }

   public Marshaller<GtfsTransfer> getTransferMarshaller() throws Exception
   {
      if (transferMarshaller == null)
      {
         transferMarshaller = MarshallerFactory.build(Paths.get(_path,
               TransferMarshaller.FILENAME).toString());
      }
      return transferMarshaller;
   }

   public Marshaller<GtfsTrip> getTripMarshaller() throws Exception
   {
      if (tripMarshaller == null)
      {
         tripMarshaller = MarshallerFactory.build(Paths.get(_path,
               TripMarshaller.FILENAME).toString());
      }
      return tripMarshaller;
   }

}
