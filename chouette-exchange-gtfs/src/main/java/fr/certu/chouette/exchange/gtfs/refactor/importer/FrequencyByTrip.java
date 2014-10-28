package fr.certu.chouette.exchange.gtfs.refactor.importer;

import java.io.IOException;

import fr.certu.chouette.exchange.gtfs.refactor.importer.GtfsException.ERROR;
import fr.certu.chouette.exchange.gtfs.refactor.importer.TripIndex.FIELDS;
import fr.certu.chouette.exchange.gtfs.refactor.model.GtfsFrequency;

public class FrequencyByTrip extends IndexImpl<GtfsFrequency> implements
      GtfsConverter
{

   public static enum FIELDS
   {
      trip_id, start_time, end_time, headway_secs, exact_times;
   };

   public static final String FILENAME = "frequencies.txt";
   public static final String KEY = FIELDS.trip_id.name();

   private GtfsFrequency bean = new GtfsFrequency();
   private String[] array = new String[FIELDS.values().length];
   private String _tripId = null;

   public FrequencyByTrip(String name) throws IOException
   {
      super(name, KEY, false);
   }

   @Override
   protected GtfsFrequency build(GtfsIterator reader, Context context)
   {
      int i = 0;
      for (FIELDS field : FIELDS.values())
      {
         array[i++] = getField(reader, field.name());
      }

      i = 0;
      int id = (int) context.get(Context.ID);
      bean.setTripId(STRING_CONVERTER.from(context, FIELDS.trip_id, array[i++],
            true));
      bean.setStartTime(GTFSTIME_CONVERTER.from(context, FIELDS.start_time,
            array[i++], true));
      bean.setEndTime(GTFSTIME_CONVERTER.from(context, FIELDS.end_time,
            array[i++], true));
      bean.setHeadwaySecs(POSITIVE_INTEGER_CONVERTER.from(context, FIELDS.headway_secs,
            array[i++], true));
      bean.setExactTimes(BOOLEAN_CONVERTER.from(context, FIELDS.exact_times,
            array[i++], false, false));

      return bean;
   }

   @Override
   public boolean validate(GtfsFrequency bean, GtfsImporter dao)
   {
      boolean result = true;
      String tripId = bean.getTripId();
      if (!tripId.equals(_tripId))
      {
         if (!dao.getTripById().containsKey(tripId))
         {
            throw new GtfsException(getPath(), bean.getId(),
                  FIELDS.trip_id.name(), ERROR.MISSING_FOREIGN_KEY,
                  bean.getTripId());
         }
         _tripId = tripId;
      }

      return result;
   }

   public static class DefaultImporterFactory extends IndexFactory
   {
      @Override
      protected Index create(String name) throws IOException
      {
         return new FrequencyByTrip(name);
      }
   }

   static
   {
      IndexFactory factory = new DefaultImporterFactory();
      IndexFactory.factories.put(FrequencyByTrip.class.getName(), factory);
   }

}
