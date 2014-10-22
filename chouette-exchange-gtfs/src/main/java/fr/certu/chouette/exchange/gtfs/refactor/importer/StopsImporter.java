package fr.certu.chouette.exchange.gtfs.refactor.importer;

import java.io.IOException;

import fr.certu.chouette.exchange.gtfs.refactor.model.GtfsStop;

public class StopsImporter extends ImporterImpl<GtfsStop>
{

   public static enum FIELDS
   {
      stop_id, stop_code, stop_name, stop_desc, stop_lat, stop_lon, zone_id, stop_url, location_type, parent_station, stop_timezone, wheelchair_boarding;
   };

   public static final String FILENAME = "stops.txt";
   public static final String KEY = FIELDS.stop_id.name();

   public StopsImporter(String name) throws IOException
   {
      super(name, KEY);
   }

   @Override
   protected GtfsStop build(GtfsIterator _reader, int id)
   {
      // TODO Auto-generated method stub
      return null;
   }

   @Override
   public boolean validate(GtfsStop bean, GtfsImporter dao)
   {
      return true;
   }

   public static class DefaultImporterFactory extends ImporterFactory
   {
      @Override
      protected Importer create(String name) throws IOException
      {
         return new StopsImporter(name);
      }
   }

   static
   {
      ImporterFactory factory = new DefaultImporterFactory();
      ImporterFactory.factories.put(StopsImporter.class.getName(), factory);
   }
}
