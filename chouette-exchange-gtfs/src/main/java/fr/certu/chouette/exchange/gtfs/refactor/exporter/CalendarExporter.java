package fr.certu.chouette.exchange.gtfs.refactor.exporter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import fr.certu.chouette.exchange.gtfs.refactor.importer.GtfsConverter;
import fr.certu.chouette.exchange.gtfs.refactor.model.GtfsCalendar;

public class CalendarExporter extends ExporterImpl<GtfsCalendar> implements
      GtfsConverter
{
   public static enum FIELDS
   {
      service_id, monday, tuesday, wednesday, thursday, friday, saturday, sunday, start_date, end_date;
   };

   public static final String FILENAME = "calendar.txt";

   public CalendarExporter(String name) throws IOException
   {
      super(name);
   }

   @Override
   public void writeHeader() throws IOException
   {
      write(FIELDS.values());
   }

   @Override
   public void export(GtfsCalendar bean) throws IOException
   {
      write(CONVERTER.to(bean));
   }

   public static Converter<String, GtfsCalendar> CONVERTER = new Converter<String, GtfsCalendar>()
   {

      @Override
      public GtfsCalendar from(String input)
      {

         GtfsCalendar bean = new GtfsCalendar();
         List<String> values = Tokenizer.tokenize(input);

         int i = 0;
         bean.setServiceId(STRING_CONVERTER.from(values.get(i++), true));
         bean.setMonday(BOOLEAN_CONVERTER.from(values.get(i++), true));
         bean.setTuesday(BOOLEAN_CONVERTER.from(values.get(i++), true));
         bean.setWednesday(BOOLEAN_CONVERTER.from(values.get(i++), true));
         bean.setThursday(BOOLEAN_CONVERTER.from(values.get(i++), true));
         bean.setFriday(BOOLEAN_CONVERTER.from(values.get(i++), true));
         bean.setSaturday(BOOLEAN_CONVERTER.from(values.get(i++), true));
         bean.setSunday(BOOLEAN_CONVERTER.from(values.get(i++), true));
         bean.setStartDate(DATE_CONVERTER.from(values.get(i++), true));
         bean.setEndDate(DATE_CONVERTER.from(values.get(i++), true));

         return bean;
      }

      @Override
      public String to(GtfsCalendar input)
      {
         String result = null;
         List<String> values = new ArrayList<String>();
         values.add(STRING_CONVERTER.to(input.getServiceId()));
         values.add(BOOLEAN_CONVERTER.to(input.getMonday()));
         values.add(BOOLEAN_CONVERTER.to(input.getTuesday()));
         values.add(BOOLEAN_CONVERTER.to(input.getWednesday()));
         values.add(BOOLEAN_CONVERTER.to(input.getThursday()));
         values.add(BOOLEAN_CONVERTER.to(input.getFriday()));
         values.add(BOOLEAN_CONVERTER.to(input.getSaturday()));
         values.add(BOOLEAN_CONVERTER.to(input.getSunday()));
         values.add(DATE_CONVERTER.to(input.getStartDate()));
         values.add(DATE_CONVERTER.to(input.getEndDate()));

         result = Tokenizer.untokenize(values);
         return result;
      }

   };

   public static class DefaultExporterFactory extends ExporterFactory
   {

      @Override
      protected Exporter create(String path) throws IOException
      {
         return new CalendarExporter(path);
      }
   }

   static
   {
      ExporterFactory factory = new DefaultExporterFactory();
      ExporterFactory.factories.put(CalendarExporter.class.getName(), factory);
   }

}
