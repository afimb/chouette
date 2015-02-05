package mobi.chouette.exchange.gtfs.model.importer;

import java.io.IOException;

import mobi.chouette.exchange.gtfs.model.GtfsTrip;
import mobi.chouette.exchange.gtfs.model.importer.GtfsException.ERROR;

public abstract class TripIndex extends IndexImpl<GtfsTrip> implements
		GtfsConverter {

	public static enum FIELDS {
		route_id, service_id, trip_id, trip_headsign, trip_short_name, direction_id, block_id, shape_id, wheelchair_accessible, bikes_allowed;
	};

	public static final String FILENAME = "trips.txt";

	protected GtfsTrip bean = new GtfsTrip();
	protected String[] array = new String[FIELDS.values().length];
	protected String _routeId = null;
	protected String _serviceId = null;

	public TripIndex(String name, String id, boolean unique) throws IOException {
		super(name, id, unique);
	}

	@Override
	protected GtfsTrip build(GtfsIterator reader, Context context) {
		int i = 0;

		for (FIELDS field : FIELDS.values()) {
			array[i++] = getField(reader, field.name());
		}

		i = 0;
		int id = (int) context.get(Context.ID);
		bean.setId(id);
		bean.setRouteId(STRING_CONVERTER.from(context, FIELDS.route_id,
				array[i++], true));
		bean.setServiceId(STRING_CONVERTER.from(context, FIELDS.service_id,
				array[i++], true));
		bean.setTripId(STRING_CONVERTER.from(context, FIELDS.trip_id,
				array[i++], true));
		bean.setTripHeadSign(STRING_CONVERTER.from(context,
				FIELDS.trip_headsign, array[i++], false));
		bean.setTripShortName(STRING_CONVERTER.from(context,
				FIELDS.trip_short_name, array[i++], false));
		bean.setDirectionId(DIRECTIONTYPE_CONVERTER.from(context,
				FIELDS.direction_id, array[i++], false));
		bean.setBlockId(STRING_CONVERTER.from(context, FIELDS.block_id,
				array[i++], false));
		bean.setShapeId(STRING_CONVERTER.from(context, FIELDS.shape_id,
				array[i++], false));
		bean.setWheelchairAccessible(WHEELCHAIRACCESSIBLETYPE_CONVERTER.from(
				context, FIELDS.wheelchair_accessible, array[i++], false));
		bean.setBikesAllowed(BIKESALLOWEDTYPE_CONVERTER.from(context,
				FIELDS.bikes_allowed, array[i++], false));

		return bean;
	}

	@Override
	public boolean validate(GtfsTrip bean, GtfsImporter dao) {
		boolean result = true;
		String routeId = bean.getRouteId();
		if (!routeId.equals(_routeId)) {
			if (!dao.getRouteById().containsKey(routeId)) {
				throw new GtfsException(getPath(), bean.getId(),
						FIELDS.route_id.name(), ERROR.MISSING_FOREIGN_KEY,
						"TODO", bean.getRouteId());
			}
			_routeId = routeId;
		}

		String serviceId = bean.getServiceId();
		if (!serviceId.equals(_serviceId)) {
			if (!dao.getCalendarByService().containsKey(serviceId)
					&& !dao.getCalendarDateByService().containsKey(serviceId)) {
				throw new GtfsException(getPath(), bean.getId(),
						FIELDS.service_id.name(), ERROR.MISSING_FOREIGN_KEY,
						"TODO", bean.getServiceId());
			}

			_serviceId = serviceId;
		}

		return result;
	}

}
