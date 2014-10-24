package fr.certu.chouette.exchange.gtfs.refactor.exporter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import fr.certu.chouette.exchange.gtfs.refactor.importer.GtfsConverter;
import fr.certu.chouette.exchange.gtfs.refactor.model.GtfsTrip;

public class TripExporter extends ExporterImpl<GtfsTrip> implements
      GtfsConverter
{
   public static enum FIELDS
   {
      route_id, service_id, trip_id, trip_headsign, trip_short_name, direction_id, shape_id;
   };

   public static final String FILENAME = "trips.txt";

   public TripExporter(String name) throws IOException
   {
      super(name);
   }

   @Override
   public void writeHeader() throws IOException
   {
      write(FIELDS.values());
   }

   @Override
   public void export(GtfsTrip bean) throws IOException
   {
      write(TRIP_CONVERTER.to(bean));
   }

   public static Converter<String, GtfsTrip> TRIP_CONVERTER = new Converter<String, GtfsTrip>()
   {

      @Override
      public GtfsTrip from(String input)
      {
         GtfsTrip bean = new GtfsTrip();
         List<String> values = Tokenizer.tokenize(input);

         int i = 0;
         bean.setRouteId(STRING_CONVERTER.from(values.get(i++), true));
         bean.setServiceId(STRING_CONVERTER.from(values.get(i++), true));
         bean.setTripId(STRING_CONVERTER.from(values.get(i++), true));
         bean.setTripHeadSign(STRING_CONVERTER.from(values.get(i++), false));
         bean.setTripShortName(STRING_CONVERTER.from(values.get(i++), false));
         bean.setDirectionId(DIRECTIONTYPE_CONVERTER.from(values.get(i++),
               false));
         bean.setBlockId(STRING_CONVERTER.from(values.get(i++), false));
         bean.setShapeId(STRING_CONVERTER.from(values.get(i++), false));
         bean.setWheelchairAccessible(WHEELCHAIRACCESSIBLETYPE_CONVERTER.from(
               values.get(i++), false));
         bean.setBikesAllowed(BIKESALLOWEDTYPE_CONVERTER.from(values.get(i++),
               false));

         return bean;
      }

      @Override
      public String to(GtfsTrip input)
      {
         String result = null;
         List<String> values = new ArrayList<String>();
         values.add(STRING_CONVERTER.to(input.getRouteId()));
         values.add(STRING_CONVERTER.to(input.getServiceId()));
         values.add(STRING_CONVERTER.to(input.getTripId()));
         values.add(STRING_CONVERTER.to(input.getTripHeadSign()));
         values.add(STRING_CONVERTER.to(input.getTripShortName()));
         values.add(DIRECTIONTYPE_CONVERTER.to(input.getDirectionId()));
         values.add(STRING_CONVERTER.to(input.getBlockId()));
         values.add(STRING_CONVERTER.to(input.getShapeId()));
         values.add(WHEELCHAIRACCESSIBLETYPE_CONVERTER.to(input
               .getWheelchairAccessible()));
         values.add(BIKESALLOWEDTYPE_CONVERTER.to(input.getBikesAllowed()));

         result = Tokenizer.untokenize(values);
         return result;
      }

   };

   public static class DefaultExporterFactory extends ExporterFactory
   {

      @Override
      protected Exporter create(String path) throws IOException
      {
         return new TripExporter(path);
      }
   }

   static
   {
      ExporterFactory factory = new DefaultExporterFactory();
      ExporterFactory.factories.put(TripExporter.class.getName(), factory);
   }

}
