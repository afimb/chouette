package fr.certu.chouette.exchange.gtfs.refactor.parser;

import java.io.IOException;

import fr.certu.chouette.exchange.gtfs.refactor.model.GtfsStopTime;
import fr.certu.chouette.exchange.gtfs.refactor.model.GtfsStopTime.DropOffType;
import fr.certu.chouette.exchange.gtfs.refactor.model.GtfsStopTime.PickupType;

public class StopTimeParser extends ParserImpl<GtfsStopTime> implements
      GtfsConverter
{

   public static enum FIELDS
   {
      trip_id, stop_id, stop_sequence, arrival_time, departure_time, stop_headsign, pickup_type, drop_off_type, shape_dist_traveled
   };

   public static final String FILENAME = "stop_times.txt";
   public static final String KEY = FIELDS.trip_id.name();

   private GtfsStopTime bean = new GtfsStopTime();

   public StopTimeParser(String name) throws IOException
   {
      super(name, KEY);
   }

   @Override
   protected GtfsStopTime build(GtfsReader reader, int id)
   {

      String tripId = getField(reader, FIELDS.trip_id.name());
      String stopId = getField(reader, FIELDS.stop_id.name());
      String stopSequence = getField(reader, FIELDS.stop_sequence.name());
      String arrivalTime = getField(reader, FIELDS.arrival_time.name());
      String departureTime = getField(reader, FIELDS.departure_time.name());
      String stopHeadsign = getField(reader, FIELDS.stop_headsign.name());
      String pickupType = getField(reader, FIELDS.pickup_type.name());
      String dropOffType = getField(reader, FIELDS.drop_off_type.name());
      String shapeDistTraveled = getField(reader,
            FIELDS.shape_dist_traveled.name());

      bean.setId(id);
      bean.setTripId(STRING_CONVERTER.from(tripId, true));
      bean.setStopId(STRING_CONVERTER.from(stopId, true));
      bean.setStopSequence(INTEGER_CONVERTER.from(stopSequence, true));
      bean.setArrivalTime(GTFSTIME_CONVERTER.from(arrivalTime, true));
      bean.setDepartureTime(GTFSTIME_CONVERTER.from(departureTime, true));
      bean.setStopHeadsign(STRING_CONVERTER.from(stopHeadsign, false));
      bean.setPickupType(PICKUP_CONVERTER.from(pickupType,
            PickupType.Scheduled, false));
      bean.setDropOffType(DROPOFFTYPE_CONVERTER.from(dropOffType,
            DropOffType.Scheduled, false));
      bean.setShapeDistTraveled(FLOAT_CONVERTER.from(shapeDistTraveled, false));

      return bean;
   }

   @Override
   public boolean validate(GtfsStopTime bean, GtfsDao dao)
   {
      return true;
   }

   public static class DefaultParserFactory extends ParserFactory
   {
      @Override
      protected GtfsParser<GtfsStopTime> create(String name) throws IOException
      {
         return new StopTimeParser(name);
      }
   }

   static
   {
      ParserFactory factory = new DefaultParserFactory();
      ParserFactory.factories.put(StopTimeParser.class.getName(), factory);
   }

}
