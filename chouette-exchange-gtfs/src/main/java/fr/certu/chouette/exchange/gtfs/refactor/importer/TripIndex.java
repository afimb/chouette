package fr.certu.chouette.exchange.gtfs.refactor.importer;

import java.io.IOException;

import fr.certu.chouette.exchange.gtfs.refactor.model.GtfsTrip;

public abstract  class TripIndex extends IndexImpl<GtfsTrip> implements GtfsConverter
{

   public static enum FIELDS
   {
      route_id, service_id, trip_id, trip_headsign, trip_short_name, direction_id, block_id, shape_id, wheelchair_accessible, bikes_allowed;
   };

   public static final String FILENAME = "trips.txt";

   protected GtfsTrip bean = new GtfsTrip();
   protected String[] array = new String[FIELDS.values().length];
   protected String _routeId = null;


   public TripIndex(String name, String id, boolean unique) throws IOException
   {
      super(name, id, unique);
   }

   @Override
   protected GtfsTrip build(GtfsIterator reader, int id)
   {
      int i = 0;
      for (FIELDS field : FIELDS.values())
      {
         array[i++] = getField(reader, field.name());
      }

      i = 0;
      bean.setId(id);
      bean.setRouteId(STRING_CONVERTER.from(array[i++], true));
      bean.setServiceId(STRING_CONVERTER.from(array[i++], true));
      bean.setTripId(STRING_CONVERTER.from(array[i++], true));
      bean.setTripHeadSign(STRING_CONVERTER.from(array[i++], false));
      bean.setTripShortName(STRING_CONVERTER.from(array[i++], false));
      bean.setDirectionId(DIRECTIONTYPE_CONVERTER.from(array[i++], false));
      bean.setBlockId(STRING_CONVERTER.from(array[i++], false));
      bean.setShapeId(STRING_CONVERTER.from(array[i++], false));
      bean.setWheelchairAccessible(WHEELCHAIRACCESSIBLETYPE_CONVERTER.from(
            array[i++], false));
      bean.setBikesAllowed(BIKESALLOWEDTYPE_CONVERTER.from(array[i++], false));

      return bean;
   }

   @Override
   public boolean validate(GtfsTrip bean, GtfsImporter dao)
   {
      boolean result = true;
      String routeId = bean.getRouteId();
      if (!routeId.equals(_routeId))
      {
         if (!dao.getRouteById().containsKey(routeId))
         {
            throw new GtfsException("[DSU] error route_id : " + routeId);
         }
         _routeId = routeId;
      }

      return result;     
   }

 
}
