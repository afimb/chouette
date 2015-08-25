package mobi.chouette.exchange.gtfs.model.importer;

import java.io.IOException;
import java.util.Map;

import mobi.chouette.exchange.gtfs.model.GtfsStopTime;
import mobi.chouette.exchange.gtfs.model.GtfsStopTime.DropOffType;
import mobi.chouette.exchange.gtfs.model.GtfsStopTime.PickupType;
import mobi.chouette.exchange.gtfs.model.importer.GtfsException.ERROR;
import mobi.chouette.exchange.gtfs.model.importer.RouteById.FIELDS;

public class StopTimeByTrip extends IndexImpl<GtfsStopTime> implements
		GtfsConverter {

	public static enum FIELDS {
		trip_id, stop_id, stop_sequence, arrival_time, departure_time, stop_headsign, pickup_type, drop_off_type, shape_dist_traveled
	};

	public static final String FILENAME = "stop_times.txt";
	public static final String KEY = FIELDS.trip_id.name();

	private GtfsStopTime _bean = new GtfsStopTime();
	private String[] _array = new String[FIELDS.values().length];

	private String _tripId = null;
	private String _stopId = null;

	public StopTimeByTrip(String name) throws IOException {
		super(name, KEY, false);
	}
	
	@Override
	protected void checkRequiredFields(Map<String, Integer> fields) {
		// extra fields are tolerated : 1-GTFS-StopTime-12 warning
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
		
		// checks for ubiquitous header fields : 1-GTFS-StopTime-2 error
		if ( fields.get(FIELDS.trip_id.name()) == null ||
				fields.get(FIELDS.stop_id.name()) == null ||
				fields.get(FIELDS.stop_sequence.name()) == null ||
				fields.get(FIELDS.arrival_time.name()) == null ||
				fields.get(FIELDS.departure_time.name()) == null) {
			Context context = new Context();
			context.put(Context.PATH, _path);
			context.put(Context.ERROR, GtfsException.ERROR.MISSING_REQUIRED_FIELDS);
			getErrors().add(new GtfsException(context));
		}
	}

	@Override
	protected GtfsStopTime build(GtfsIterator reader, Context context) {

		int i = 0;
		for (FIELDS field : FIELDS.values()) {
			_array[i++] = getField(reader, field.name());
		}

		i = 0;
		String value = null;
		int id = (int) context.get(Context.ID);
		_bean.getErrors().clear();
		_bean.setId(id);
		value = _array[i++];
		_bean.setTripId(STRING_CONVERTER.from(context, FIELDS.trip_id, value, true));
		value = _array[i++];
		_bean.setStopId(STRING_CONVERTER.from(context, FIELDS.stop_id, value, true));
		value = _array[i++];
		_bean.setStopSequence(INTEGER_CONVERTER.from(context, FIELDS.stop_sequence, value, true));
		value = _array[i++];
		_bean.setArrivalTime(GTFSTIME_CONVERTER.from(context, FIELDS.arrival_time, value, true));
		value = _array[i++];
		_bean.setDepartureTime(GTFSTIME_CONVERTER.from(context, FIELDS.departure_time, value, true));
		value = _array[i++];
		_bean.setStopHeadsign(STRING_CONVERTER.from(context, FIELDS.stop_headsign, value, false));
		value = _array[i++];
		_bean.setPickupType(PICKUP_CONVERTER.from(context, FIELDS.pickup_type, value, PickupType.Scheduled, false));
		value = _array[i++];
		_bean.setDropOffType(DROPOFFTYPE_CONVERTER.from(context, FIELDS.drop_off_type, value, DropOffType.Scheduled, false));
		value = _array[i++];
		_bean.setShapeDistTraveled(FLOAT_CONVERTER.from(context, FIELDS.shape_dist_traveled, value, false));

		return _bean;
	}

	@Override
	public boolean validate(GtfsStopTime bean, GtfsImporter dao) {
		boolean result = true;
		String tripId = bean.getTripId();
		if (!tripId.equals(_tripId)) {
			if (!dao.getTripById().containsKey(tripId)) {
				throw new GtfsException(getPath(), bean.getId(),
						FIELDS.trip_id.name(), ERROR.MISSING_FOREIGN_KEY,
						"TODO", bean.getTripId());
			}
			_tripId = tripId;
		}

		String stopId = bean.getStopId();
		if (!stopId.equals(_stopId)) {
			if (!dao.getStopById().containsKey(stopId)) {
				throw new GtfsException(getPath(), bean.getId(),
						FIELDS.stop_id.name(), ERROR.MISSING_FOREIGN_KEY,
						"TODO", bean.getStopId());
			}
			_stopId = stopId;
		}

		return result;
	}

	public static class DefaultImporterFactory extends IndexFactory {
		@Override
		protected Index<GtfsStopTime> create(String name) throws IOException {
			return new StopTimeByTrip(name);
		}
	}

	static {
		IndexFactory factory = new DefaultImporterFactory();
		IndexFactory.factories.put(StopTimeByTrip.class.getName(), factory);
	}

}
