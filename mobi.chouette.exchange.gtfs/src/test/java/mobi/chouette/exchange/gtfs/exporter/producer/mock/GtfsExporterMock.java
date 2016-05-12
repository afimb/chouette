package mobi.chouette.exchange.gtfs.exporter.producer.mock;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import mobi.chouette.exchange.gtfs.model.GtfsAgency;
import mobi.chouette.exchange.gtfs.model.GtfsCalendar;
import mobi.chouette.exchange.gtfs.model.GtfsCalendarDate;
import mobi.chouette.exchange.gtfs.model.GtfsFrequency;
import mobi.chouette.exchange.gtfs.model.GtfsRoute;
import mobi.chouette.exchange.gtfs.model.GtfsShape;
import mobi.chouette.exchange.gtfs.model.GtfsStop;
import mobi.chouette.exchange.gtfs.model.GtfsStopTime;
import mobi.chouette.exchange.gtfs.model.GtfsTransfer;
import mobi.chouette.exchange.gtfs.model.GtfsTrip;
import mobi.chouette.exchange.gtfs.model.exporter.Exporter;
import mobi.chouette.exchange.gtfs.model.exporter.GtfsExporterInterface;
import mobi.chouette.exchange.report.ActionReport;

import org.apache.commons.beanutils.BeanUtils;

public class GtfsExporterMock implements GtfsExporterInterface
{
   
   @Getter 
   List<GtfsAgency> exportedAgencies = new ArrayList<>();
   @Getter 
   List<GtfsCalendarDate> exportedCalendarDates = new ArrayList<>();
   @Getter 
   List<GtfsCalendar> exportedCalendars = new ArrayList<>();
   @Getter 
   List<GtfsFrequency> exportedFrequencies = new ArrayList<>();
   @Getter 
   List<GtfsRoute> exportedRoutes = new ArrayList<>();
   @Getter 
   List<GtfsShape> exportedShapes = new ArrayList<>();
   @Getter 
   List<GtfsStop> exportedStops = new ArrayList<>();
   @Getter 
   List<GtfsStopTime> exportedStopTimes = new ArrayList<>();
   @Getter 
   List<GtfsTransfer> exportedTransfers = new ArrayList<>();
   @Getter 
   List<GtfsTrip> exportedTrips = new ArrayList<>();
   
   AgencyExporterMock agencyMock = new AgencyExporterMock();
   CalendarDateExporterMock calendarDateMock = new CalendarDateExporterMock();
   CalendarExporterMock calendarMock = new CalendarExporterMock();
   FrequencyExporterMock frequencyMock = new FrequencyExporterMock();
   RouteExporterMock routeMock = new RouteExporterMock();
   ShapeExporterMock shapeMock = new ShapeExporterMock();
   StopExporterMock stopMock = new StopExporterMock();
   StopTimeExporterMock stopTimeMock = new StopTimeExporterMock();
   TransferExporterMock transferMock = new TransferExporterMock();
   TripExporterMock tripMock = new TripExporterMock();
   
   public void reset()
   {
      exportedAgencies.clear();
      exportedCalendarDates.clear();
      exportedCalendars.clear();
      exportedFrequencies.clear();
      exportedRoutes.clear();
      exportedShapes.clear();
      exportedStops.clear();
      exportedStopTimes.clear();
      exportedTransfers.clear();
      exportedTrips.clear();
   }

   @Override
   public Exporter<GtfsAgency> getAgencyExporter() throws Exception
   {
      return agencyMock;
   }

   @Override
   public Exporter<GtfsCalendarDate> getCalendarDateExporter() throws Exception
   {
      return calendarDateMock;
   }

   @Override
   public Exporter<GtfsCalendar> getCalendarExporter() throws Exception
   {
      return calendarMock;
   }

   @Override
   public Exporter<GtfsFrequency> getFrequencyExporter() throws Exception
   {
      return frequencyMock;
   }

   @Override
   public Exporter<GtfsRoute> getRouteExporter() throws Exception
   {
      return routeMock;
   }

   @Override
   public Exporter<GtfsShape> getShapeExporter() throws Exception
   {
      return shapeMock;
   }

   @Override
   public Exporter<GtfsStop> getStopExporter() throws Exception
   {
      return stopMock;
   }

   @Override
   public Exporter<GtfsStop> getStopExtendedExporter() throws Exception
   {
      return stopMock;
   }

   @Override
   public Exporter<GtfsStopTime> getStopTimeExporter() throws Exception
   {
      return stopTimeMock;
   }

   @Override
   public Exporter<GtfsTransfer> getTransferExporter() throws Exception
   {
      return transferMock;
   }

   @Override
   public Exporter<GtfsTrip> getTripExporter() throws Exception
   {
      return tripMock;
   }
   
   private abstract class ExporterMock<T> implements Exporter<T>
   {

      @Override
      public void dispose(ActionReport report) throws IOException
      {         
      }

      @Override
      public void writeHeader() throws IOException
      {         
      }

      @Override
      public void write(String text) throws IOException
      {         
      }
      
   }
   
   private class AgencyExporterMock extends ExporterMock<GtfsAgency>
   {
      @Override
      public void export(GtfsAgency bean) throws IOException
      {
         try
         {
            exportedAgencies.add((GtfsAgency) BeanUtils.cloneBean(bean));
         }
         catch (Exception e)
         {
            e.printStackTrace();
         }
      }
   }

   private class CalendarDateExporterMock extends ExporterMock<GtfsCalendarDate>
   {
      @Override
      public void export(GtfsCalendarDate bean) throws IOException
      {
         try
         {
            exportedCalendarDates.add((GtfsCalendarDate) BeanUtils.cloneBean(bean));
         }
         catch (Exception e)
         {
            e.printStackTrace();
         }
      }
   }

   private class CalendarExporterMock extends ExporterMock<GtfsCalendar>
   {
      @Override
      public void export(GtfsCalendar bean) throws IOException
      {
         try
         {
            exportedCalendars.add((GtfsCalendar) BeanUtils.cloneBean(bean));
         }
         catch (Exception e)
         {
            e.printStackTrace();
         }
      }
   }

   private class FrequencyExporterMock extends ExporterMock<GtfsFrequency>
   {
      @Override
      public void export(GtfsFrequency bean) throws IOException
      {
         try
         {
            exportedFrequencies.add((GtfsFrequency) BeanUtils.cloneBean(bean));
         }
         catch (Exception e)
         {
            e.printStackTrace();
         }
      }
   }
   
   private class RouteExporterMock extends ExporterMock<GtfsRoute>
   {
      @Override
      public void export(GtfsRoute bean) throws IOException
      {
         try
         {
            exportedRoutes.add((GtfsRoute) BeanUtils.cloneBean(bean));
         }
         catch (Exception e)
         {
            e.printStackTrace();
         }
      }
   }

   private class ShapeExporterMock extends ExporterMock<GtfsShape>
   {
      @Override
      public void export(GtfsShape bean) throws IOException
      {
         
         try
         {
            exportedShapes.add((GtfsShape) BeanUtils.cloneBean(bean));
         }
         catch (Exception e)
         {
            e.printStackTrace();
         }
      }
   }

   private class StopExporterMock extends ExporterMock<GtfsStop>
   {
      @Override
      public void export(GtfsStop bean) throws IOException
      {
         
         try
         {
            exportedStops.add((GtfsStop) BeanUtils.cloneBean(bean));
         }
         catch (Exception e)
         {
            e.printStackTrace();
         }
      }
   }
   
   private class StopTimeExporterMock extends ExporterMock<GtfsStopTime>
   {
      @Override
      public void export(GtfsStopTime bean) throws IOException
      {
         try
         {
            exportedStopTimes.add((GtfsStopTime) BeanUtils.cloneBean(bean));
         }
         catch (Exception e)
         {
            e.printStackTrace();
         }
      }
   }
   
   private class TransferExporterMock extends ExporterMock<GtfsTransfer>
   {
      @Override
      public void export(GtfsTransfer bean) throws IOException
      {
         try
         {
            exportedTransfers.add((GtfsTransfer) BeanUtils.cloneBean(bean));
         }
         catch (Exception e)
         {
            e.printStackTrace();
         }
      }
   }
   
   private class TripExporterMock extends ExporterMock<GtfsTrip>
   {
      @Override
      public void export(GtfsTrip bean) throws IOException
      {
         try
         {
            exportedTrips.add((GtfsTrip) BeanUtils.cloneBean(bean));
         }
         catch (Exception e)
         {
            e.printStackTrace();
         }
      }
   }

}
