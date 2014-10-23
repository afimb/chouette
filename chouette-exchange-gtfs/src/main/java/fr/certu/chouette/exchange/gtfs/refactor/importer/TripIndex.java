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

   protected GtfsTrip _bean = new GtfsTrip();
   protected String[] _array = new String[FIELDS.values().length];
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
         _array[i++] = getField(reader, field.name());
      }

      i = 0;
      _bean.setId(id);
      _bean.setRouteId(STRING_CONVERTER.from(_array[i++], true));
      _bean.setServiceId(STRING_CONVERTER.from(_array[i++], true));
      _bean.setTripId(STRING_CONVERTER.from(_array[i++], true));
      _bean.setTripHeadSign(STRING_CONVERTER.from(_array[i++], false));
      _bean.setTripShortName(STRING_CONVERTER.from(_array[i++], false));
      _bean.setDirectionId(DIRECTIONTYPE_CONVERTER.from(_array[i++], false));
      _bean.setBlockId(STRING_CONVERTER.from(_array[i++], false));
      _bean.setShapeId(STRING_CONVERTER.from(_array[i++], false));
      _bean.setWheelchairAccessible(WHEELCHAIRACCESSIBLETYPE_CONVERTER.from(
            _array[i++], false));
      _bean.setBikesAllowed(BIKESALLOWEDTYPE_CONVERTER.from(_array[i++], false));

      return _bean;
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
