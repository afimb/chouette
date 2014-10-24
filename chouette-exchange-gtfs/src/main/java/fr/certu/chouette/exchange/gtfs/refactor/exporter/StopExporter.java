package fr.certu.chouette.exchange.gtfs.refactor.exporter;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import fr.certu.chouette.exchange.gtfs.refactor.importer.GtfsConverter;
import fr.certu.chouette.exchange.gtfs.refactor.model.GtfsStop;
import fr.certu.chouette.exchange.gtfs.refactor.model.GtfsStop.LocationType;
import fr.certu.chouette.exchange.gtfs.refactor.model.GtfsStop.WheelchairBoardingType;

public class StopExporter extends ExporterImpl<GtfsStop> implements
      GtfsConverter
{
   public static enum FIELDS
   {
      stop_id, stop_code, stop_name, stop_desc, stop_lat, stop_lon, zone_id, stop_url, location_type, parent_station, stop_timezone, wheelchair_boarding;
   };

   public static final String FILENAME = "stops.txt";

   public StopExporter(String name) throws IOException
   {
      super(name);
   }

   @Override
   public void writeHeader() throws IOException
   {
      write(FIELDS.values());
   }

   @Override
   public void export(GtfsStop bean) throws IOException
   {
      write(STOP_CONVERTER.to(bean));
   }

   public static Converter<String, GtfsStop> STOP_CONVERTER = new Converter<String, GtfsStop>()
   {

      @Override
      public GtfsStop from(String input)
      {
         GtfsStop bean = new GtfsStop();
         List<String> values = Tokenizer.tokenize(input);

         int i = 0;
         bean.setStopId(STRING_CONVERTER.from(values.get(i++), true));
         bean.setStopCode(STRING_CONVERTER.from(values.get(i++), false));
         bean.setStopName(STRING_CONVERTER.from(values.get(i++), true));
         bean.setStopDesc(STRING_CONVERTER.from(values.get(i++), false));
         bean.setStopLat(BigDecimal.valueOf(FLOAT_CONVERTER.from(
               values.get(i++), true)));
         bean.setStopLon(BigDecimal.valueOf(FLOAT_CONVERTER.from(
               values.get(i++), true)));
         bean.setZoneId(STRING_CONVERTER.from(values.get(i++), false));
         bean.setStopUrl(URL_CONVERTER.from(values.get(i++), false));
         bean.setLocationType(LOCATIONTYPE_CONVERTER.from(values.get(i++),
               LocationType.Stop, false));
         bean.setParentStation(STRING_CONVERTER.from(values.get(i++), false));
         bean.setStopTimezone(TIMEZONE_CONVERTER.from(values.get(i++), false));
         bean.setWheelchairBoarding(WHEELCHAIRBOARDINGTYPE_CONVERTER.from(
               values.get(i++), WheelchairBoardingType.NoInformation, false));

         return bean;
      }

      @Override
      public String to(GtfsStop input)
      {
         String result = null;
         List<String> values = new ArrayList<String>();
         values.add(STRING_CONVERTER.to(input.getStopId()));
         values.add(STRING_CONVERTER.to(input.getStopCode()));
         values.add(STRING_CONVERTER.to(input.getStopName()));
         values.add(STRING_CONVERTER.to(input.getStopDesc()));
         values.add(FLOAT_CONVERTER.to(input.getStopLat().floatValue()));
         values.add(FLOAT_CONVERTER.to(input.getStopLon().floatValue()));
         values.add(STRING_CONVERTER.to(input.getZoneId()));
         values.add(URL_CONVERTER.to(input.getStopUrl()));
         values.add(LOCATIONTYPE_CONVERTER.to(input.getLocationType()));
         values.add(STRING_CONVERTER.to(input.getParentStation()));
         values.add(TIMEZONE_CONVERTER.to(input.getStopTimezone()));
         values.add(WHEELCHAIRBOARDINGTYPE_CONVERTER.to(input
               .getWheelchairBoarding()));

         result = Tokenizer.untokenize(values);
         return result;
      }

   };

   public static class DefaultExporterFactory extends ExporterFactory
   {

      @Override
      protected Exporter create(String path) throws IOException
      {
         return new StopExporter(path);
      }
   }

   static
   {
      ExporterFactory factory = new DefaultExporterFactory();
      ExporterFactory.factories.put(StopExporter.class.getName(), factory);
   }
}
