package mobi.chouette.exchange.gtfs.model.importer;

import java.io.IOException;
import java.util.Map;

import mobi.chouette.common.HTMLTagValidator;
import mobi.chouette.exchange.gtfs.model.GtfsTrip;

public abstract class TripIndex extends IndexImpl<GtfsTrip> implements GtfsConverter {

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
		for (String fieldName : fields.keySet()) {
			if (fieldName != null) {
				if (!fieldName.equals(fieldName.trim())) {
					// extra spaces in end fields are tolerated : 1-GTFS-CSV-7
					// warning
					getErrors().add(
							new GtfsException(_path, 1, getIndex(fieldName), fieldName.trim(),
									GtfsException.ERROR.EXTRA_SPACE_IN_HEADER_FIELD, null, fieldName));
				}

				if (HTMLTagValidator.validate(fieldName.trim())) {
					getErrors().add(
							new GtfsException(_path, 1, getIndex(fieldName), fieldName.trim(),
									GtfsException.ERROR.HTML_TAG_IN_HEADER_FIELD, null, null));
				}

				boolean fieldNameIsExtra = true;
				for (FIELDS field : FIELDS.values()) {
					if (fieldName.trim().equals(field.name())) {
						fieldNameIsExtra = false;
						break;
					}
				}
				if (fieldNameIsExtra) {
					// extra fields are tolerated : 1-GTFS-Trip-8 warning
					getErrors().add(
							new GtfsException(_path, 1, getIndex(fieldName), fieldName,
									GtfsException.ERROR.EXTRA_HEADER_FIELD, null, null));
				}
			}
		}

		// checks for ubiquitous header fields : 1-GTFS-Trip-2 error
		if (fields.get(FIELDS.trip_id.name()) == null || fields.get(FIELDS.route_id.name()) == null
				|| fields.get(FIELDS.service_id.name()) == null) {

			String name = "";
			if (fields.get(FIELDS.trip_id.name()) == null)
				name = FIELDS.trip_id.name();
			else if (fields.get(FIELDS.route_id.name()) == null)
				name = FIELDS.route_id.name();
			else if (fields.get(FIELDS.service_id.name()) == null)
				name = FIELDS.service_id.name();

			throw new GtfsException(_path, 1, name, GtfsException.ERROR.MISSING_REQUIRED_FIELDS, null, null);
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
		clearBean();
		bean.setId(id);
		bean.getErrors().clear();

		value = array[i++];
		testExtraSpace(FIELDS.route_id.name(), value, bean);
		if (value == null || value.trim().isEmpty()) {
			if (withValidation)
				bean.getErrors().add(
					new GtfsException(_path, id, getIndex(FIELDS.route_id.name()), FIELDS.route_id.name(),
							GtfsException.ERROR.MISSING_REQUIRED_VALUES, null, null));
		} else {
			bean.setRouteId(STRING_CONVERTER.from(context, FIELDS.route_id, value, true));
		}

		value = array[i++];
		testExtraSpace(FIELDS.service_id.name(), value, bean);
		if (value == null || value.trim().isEmpty()) {
			if (withValidation)
				bean.getErrors().add(
					new GtfsException(_path, id, getIndex(FIELDS.service_id.name()), FIELDS.service_id.name(),
							GtfsException.ERROR.MISSING_REQUIRED_VALUES, null, null));
		} else {
			bean.setServiceId(STRING_CONVERTER.from(context, FIELDS.service_id, value, true));
		}

		value = array[i++];
		testExtraSpace(FIELDS.trip_id.name(), value, bean);
		if (value == null || value.trim().isEmpty()) {
			if (withValidation)
				bean.getErrors().add(
					new GtfsException(_path, id, getIndex(FIELDS.trip_id.name()), FIELDS.trip_id.name(),
							GtfsException.ERROR.MISSING_REQUIRED_VALUES, null, null));
		} else {
			bean.setTripId(STRING_CONVERTER.from(context, FIELDS.trip_id, value, true));
		}

		value = array[i++];
		testExtraSpace(FIELDS.trip_headsign.name(), value, bean);
		bean.setTripHeadSign(STRING_CONVERTER.from(context, FIELDS.trip_headsign, value, false));

		value = array[i++];
		testExtraSpace(FIELDS.trip_short_name.name(), value, bean);
		bean.setTripShortName(STRING_CONVERTER.from(context, FIELDS.trip_short_name, value, false));

		value = array[i++];
		testExtraSpace(FIELDS.direction_id.name(), value, bean);
		try {
			bean.setDirectionId(DIRECTIONTYPE_CONVERTER.from(context, FIELDS.direction_id, value,
					GtfsTrip.DirectionType.Outbound, false));
		} catch (GtfsException ex) {
			if (withValidation)
				bean.getErrors().add(
					new GtfsException(_path, id, getIndex(FIELDS.direction_id.name()), FIELDS.direction_id.name(),
							GtfsException.ERROR.INVALID_FORMAT, null, value));
		}

		value = array[i++];
		testExtraSpace(FIELDS.block_id.name(), value, bean);
		bean.setBlockId(STRING_CONVERTER.from(context, FIELDS.block_id, value, false));

		value = array[i++];
		testExtraSpace(FIELDS.shape_id.name(), value, bean);
		bean.setShapeId(STRING_CONVERTER.from(context, FIELDS.shape_id, value, false));

		value = array[i++];
		testExtraSpace(FIELDS.wheelchair_accessible.name(), value, bean);
		if (value != null && !value.trim().isEmpty()) {
			try {
				bean.setWheelchairAccessible(WHEELCHAIRACCESSIBLETYPE_CONVERTER.from(context,
						FIELDS.wheelchair_accessible, value, false));
			} catch (GtfsException ex) {
				if (withValidation)
					bean.getErrors().add(
						new GtfsException(_path, id, getIndex(FIELDS.wheelchair_accessible.name()),
								FIELDS.wheelchair_accessible.name(), GtfsException.ERROR.INVALID_FORMAT, null, value));
			}
		}

		value = array[i++];
		testExtraSpace(FIELDS.bikes_allowed.name(), value, bean);
		if (value != null && !value.trim().isEmpty()) {
			try {
				bean.setBikesAllowed(BIKESALLOWEDTYPE_CONVERTER.from(context, FIELDS.bikes_allowed, value, false));
			} catch (GtfsException ex) {
				if (withValidation)
					bean.getErrors().add(
						new GtfsException(_path, id, getIndex(FIELDS.bikes_allowed.name()),
								FIELDS.bikes_allowed.name(), GtfsException.ERROR.INVALID_FORMAT, null, value));
			}
		}

		return bean;
	}

	@Override
	public boolean validate(GtfsTrip bean, GtfsImporter dao) {
		boolean result = true;

		if (isPresent(bean.getRouteId()))
			if (dao.getRouteById().containsKey(bean.getRouteId())) {
				bean.getOkTests().add(GtfsException.ERROR.UNREFERENCED_ID);				
			} else {
				bean.getErrors().add(
						new GtfsException(_path, bean.getId(), getIndex(FIELDS.route_id.name()),
								FIELDS.route_id.name(), GtfsException.ERROR.UNREFERENCED_ID, bean.getTripId(), bean.getRouteId()));
				result = false;
			}

		if (isPresent(bean.getServiceId()))
			if (isCalendar(dao, bean.getServiceId())) {
				bean.getOkTests().add(GtfsException.ERROR.UNREFERENCED_ID);
			} else {
				bean.getErrors().add(
						new GtfsException(_path, bean.getId(), getIndex(FIELDS.service_id.name()), FIELDS.service_id
								.name(), GtfsException.ERROR.UNREFERENCED_ID, bean.getTripId(), bean.getServiceId()));
				result = false;
			}

		if (isPresent(bean.getShapeId())) {
			if (dao.hasShapeImporter() && dao.getShapeById().containsKey(bean.getShapeId())) {
				bean.getOkTests().add(GtfsException.ERROR.UNREFERENCED_ID);
			} else {
				bean.getErrors().add(
						new GtfsException(_path, bean.getId(), getIndex(FIELDS.shape_id.name()),
								FIELDS.shape_id.name(), GtfsException.ERROR.UNREFERENCED_ID, bean.getTripId(), bean.getShapeId()));
				result = false;
			}
		}

		return result;
	}

	private boolean isCalendar(GtfsImporter dao, String serviceId) {
		if (dao.hasCalendarImporter() && dao.getCalendarByService().containsKey(serviceId))
			return true;
		if (dao.hasCalendarDateImporter() && dao.getCalendarDateByService().containsKey(serviceId))
			return true;
		return false;
	}

	private void clearBean() {
		// bean.getErrors().clear();
		bean.setId(null);
		bean.setBikesAllowed(null);
		bean.setBlockId(null);
		bean.setDirectionId(null);
		bean.setRouteId(null);
		bean.setServiceId(null);
		bean.setShapeId(null);
		bean.setTripHeadSign(null);
		bean.setTripId(null);
		bean.setTripShortName(null);
		bean.setWheelchairAccessible(null);

	}
}
