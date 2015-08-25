package mobi.chouette.exchange.gtfs.model.importer;

import java.io.IOException;
import java.util.Map;

import mobi.chouette.exchange.gtfs.model.GtfsTrip;
import mobi.chouette.exchange.gtfs.model.importer.GtfsException.ERROR;
import mobi.chouette.exchange.gtfs.model.importer.StopTimeByTrip.FIELDS;

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
	protected void checkRequiredFields(Map<String, Integer> fields) {
		// extra fields are tolerated : 1-GTFS-Trip-8 warning
		for (String fieldName : fields.keySet()) {
			if (fieldName != null) {
				boolean fieldNameIsExtra = true;
				for (FIELDS field : FIELDS.values()) {
					if (fieldName.trim().equals(field.name())) {
						fieldNameIsExtra = false;
						break;
					}
				}
				if (fieldNameIsExtra) {
					// add the warning to warnings
					Context context = new Context();
					context.put(Context.PATH, _path);
					context.put(Context.FIELD, fieldName);
					context.put(Context.ERROR, GtfsException.ERROR.EXTRA_HEADER_FIELD);
					getErrors().add(new GtfsException(context));
				}
			}
		}
		
		// checks for ubiquitous header fields : 1-GTFS-Trip-2 error
		if ( fields.get(FIELDS.trip_id.name()) == null ||
				fields.get(FIELDS.route_id.name()) == null ||
				fields.get(FIELDS.service_id.name()) == null) {
			Context context = new Context();
			context.put(Context.PATH, _path);
			context.put(Context.ERROR, GtfsException.ERROR.MISSING_REQUIRED_FIELDS);
			getErrors().add(new GtfsException(context));
		}
	}

	@Override
	protected GtfsTrip build(GtfsIterator reader, Context context) {
		int i = 0;

		for (FIELDS field : FIELDS.values()) {
			array[i++] = getField(reader, field.name());
		}

		i = 0;
		String value = null;
		int id = (int) context.get(Context.ID);
		bean.setId(id);
		bean.getErrors().clear();
		value = array[i++];
		bean.setRouteId(STRING_CONVERTER.from(context, FIELDS.route_id, value, true));
		value = array[i++];
		bean.setServiceId(STRING_CONVERTER.from(context, FIELDS.service_id, value, true));
		value = array[i++];
		bean.setTripId(STRING_CONVERTER.from(context, FIELDS.trip_id, value, true));
		value = array[i++];
		bean.setTripHeadSign(STRING_CONVERTER.from(context, FIELDS.trip_headsign, value, false));
		value = array[i++];
		bean.setTripShortName(STRING_CONVERTER.from(context, FIELDS.trip_short_name, value, false));
		value = array[i++];
		bean.setDirectionId(DIRECTIONTYPE_CONVERTER.from(context, FIELDS.direction_id, value, GtfsTrip.DirectionType.Outbound, false));
		value = array[i++];
		bean.setBlockId(STRING_CONVERTER.from(context, FIELDS.block_id, value, false));
		value = array[i++];
		bean.setShapeId(STRING_CONVERTER.from(context, FIELDS.shape_id, value, false));
		value = array[i++];
		bean.setWheelchairAccessible(WHEELCHAIRACCESSIBLETYPE_CONVERTER.from(context, FIELDS.wheelchair_accessible, value, false));
		value = array[i++];
		bean.setBikesAllowed(BIKESALLOWEDTYPE_CONVERTER.from(context, FIELDS.bikes_allowed, value, false));

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
			boolean okCalendar = (dao.hasCalendarImporter() && dao.getCalendarByService().containsKey(serviceId)) ;
			boolean okCalendarDate = (dao.hasCalendarDateImporter() && dao.getCalendarDateByService().containsKey(serviceId)) ;
			if (!okCalendar
					&& !okCalendarDate) {
				throw new GtfsException(getPath(), bean.getId(),
						FIELDS.service_id.name(), ERROR.MISSING_FOREIGN_KEY,
						"TODO", bean.getServiceId());
			}

			_serviceId = serviceId;
		}

		return result;
	}

}
