package fr.certu.chouette.exchange.gtfs.refactor.exporter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import fr.certu.chouette.exchange.gtfs.refactor.importer.GtfsConverter;
import fr.certu.chouette.exchange.gtfs.refactor.model.GtfsStopTime;
import fr.certu.chouette.exchange.gtfs.refactor.model.GtfsStopTime.DropOffType;
import fr.certu.chouette.exchange.gtfs.refactor.model.GtfsStopTime.PickupType;

public class StopTimeExporter extends ExporterImpl<GtfsStopTime> implements
      GtfsConverter
{

   public static enum FIELDS
   {
      trip_id, stop_id, stop_sequence, arrival_time, departure_time, stop_headsign, pickup_type, drop_off_type, shape_dist_traveled
   };

   public static final String FILENAME = "stop_times.txt";

   public StopTimeExporter(String name) throws IOException
   {
      super(name);
   }

   @Override
   public void writeHeader() throws IOException
   {
      write(FIELDS.values());
   }

   @Override
   public void export(GtfsStopTime bean) throws IOException
   {
      write(CONVERTER.to(bean));
   }

   public static Converter<String, GtfsStopTime> CONVERTER = new Converter<String, GtfsStopTime>()
   {

      @Override
      public GtfsStopTime from(String input)
      {
         GtfsStopTime bean = new GtfsStopTime();
         List<String> values = Tokenizer.tokenize(input);

         int i = 0;
         bean.setTripId(STRING_CONVERTER.from(values.get(i++), true));
         bean.setStopId(STRING_CONVERTER.from(values.get(i++), true));
         bean.setStopSequence(INTEGER_CONVERTER.from(values.get(i++), true));
         bean.setArrivalTime(GTFSTIME_CONVERTER.from(values.get(i++), true));
         bean.setDepartureTime(GTFSTIME_CONVERTER.from(values.get(i++), true));
         bean.setStopHeadsign(STRING_CONVERTER.from(values.get(i++), false));
         bean.setPickupType(PICKUP_CONVERTER.from(values.get(i++),
               PickupType.Scheduled, false));
         bean.setDropOffType(DROPOFFTYPE_CONVERTER.from(values.get(i++),
               DropOffType.Scheduled, false));
         bean.setShapeDistTraveled(FLOAT_CONVERTER.from(values.get(i++), false));

         return bean;
      }

      @Override
      public String to(GtfsStopTime input)
      {
         String result = null;
         List<String> values = new ArrayList<String>();
         values.add(STRING_CONVERTER.to(input.getTripId()));
         values.add(STRING_CONVERTER.to(input.getStopId()));
         values.add(INTEGER_CONVERTER.to(input.getStopSequence()));
         values.add(GTFSTIME_CONVERTER.to(input.getArrivalTime()));
         values.add(GTFSTIME_CONVERTER.to(input.getDepartureTime()));
         values.add(STRING_CONVERTER.to(input.getStopHeadsign()));
         values.add(PICKUP_CONVERTER.to(input.getPickupType()));
         values.add(DROPOFFTYPE_CONVERTER.to(input.getDropOffType()));
         values.add(FLOAT_CONVERTER.to(input.getShapeDistTraveled()));

         result = Tokenizer.untokenize(values);
         return result;
      }

   };

   public static class DefaultExporterFactory extends ExporterFactory
   {

      @Override
      protected Exporter create(String path) throws IOException
      {
         return new StopTimeExporter(path);
      }
   }

   static
   {
      ExporterFactory factory = new DefaultExporterFactory();
      ExporterFactory.factories.put(StopTimeExporter.class.getName(), factory);
   }

}
