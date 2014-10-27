package fr.certu.chouette.exchange.gtfs.refactor.exporter;

import java.awt.Color;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import fr.certu.chouette.exchange.gtfs.refactor.importer.GtfsConverter;
import fr.certu.chouette.exchange.gtfs.refactor.model.GtfsRoute;

public class RouteExporter extends ExporterImpl<GtfsRoute> implements
      GtfsConverter
{
   public static enum FIELDS
   {
      route_id, Route_id, route_short_name, route_long_name, route_desc, route_type, route_url, route_color, route_text_color;
   };

   public static final String FILENAME = "routes.txt";

   public RouteExporter(String name) throws IOException
   {
      super(name);
   }

   @Override
   public void writeHeader() throws IOException
   {
      write(FIELDS.values());
   }

   @Override
   public void export(GtfsRoute bean) throws IOException
   {
      write(CONVERTER.to(bean));
   }

   public static Converter<String, GtfsRoute> CONVERTER = new Converter<String, GtfsRoute>()
   {

      @Override
      public GtfsRoute from(String input)
      {
         GtfsRoute bean = new GtfsRoute();
         List<String> values = Tokenizer.tokenize(input);

         int i = 0;
         bean.setRouteId(STRING_CONVERTER.from(values.get(i++), true));
         bean.setAgencyId(STRING_CONVERTER.from(values.get(i++), false));
         bean.setRouteShortName(STRING_CONVERTER.from(values.get(i++), true));
         bean.setRouteLongName(STRING_CONVERTER.from(values.get(i++), true));
         bean.setRouteDesc(STRING_CONVERTER.from(values.get(i++), false));
         bean.setRouteType(ROUTETYPE_CONVERTER.from(values.get(i++), true));
         bean.setRouteUrl(URL_CONVERTER.from(values.get(i++), false));
         bean.setRouteColor(COLOR_CONVERTER.from(values.get(i++), Color.WHITE,
               false));
         bean.setRouteTextColor(COLOR_CONVERTER.from(values.get(i++),
               Color.BLACK, false));

         return bean;
      }

      @Override
      public String to(GtfsRoute input)
      {
         String result = null;
         List<String> values = new ArrayList<String>();
         values.add(STRING_CONVERTER.to(input.getRouteId()));
         values.add(STRING_CONVERTER.to(input.getAgencyId()));
         values.add(STRING_CONVERTER.to(input.getRouteShortName()));
         values.add(STRING_CONVERTER.to(input.getRouteLongName()));
         values.add(STRING_CONVERTER.to(input.getRouteDesc()));
         values.add(ROUTETYPE_CONVERTER.to(input.getRouteType()));
         values.add(URL_CONVERTER.to(input.getRouteUrl()));
         values.add(COLOR_CONVERTER.to(input.getRouteColor()));
         values.add(COLOR_CONVERTER.to(input.getRouteTextColor()));

         result = Tokenizer.untokenize(values);
         return result;
      }

   };

   public static class DefaultExporterFactory extends ExporterFactory
   {

      @Override
      protected Exporter create(String path) throws IOException
      {
         return new RouteExporter(path);
      }
   }

   static
   {
      ExporterFactory factory = new DefaultExporterFactory();
      ExporterFactory.factories.put(RouteExporter.class.getName(), factory);
   }
}
