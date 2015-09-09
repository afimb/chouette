package mobi.chouette.exchange.gtfs.model.importer;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;

import mobi.chouette.common.HTMLTagValidator;
import mobi.chouette.exchange.gtfs.model.GtfsStopTime;
import mobi.chouette.exchange.gtfs.model.GtfsStopTime.DropOffType;
import mobi.chouette.exchange.gtfs.model.GtfsStopTime.PickupType;

public class StopTimeByTrip extends IndexImpl<GtfsStopTime> implements GtfsConverter {

	public static enum FIELDS {
		trip_id, stop_id, stop_sequence, arrival_time, departure_time, stop_headsign, pickup_type, drop_off_type, shape_dist_traveled, timepoint
	};

	public static final String FILENAME = "stop_times.txt";
	public static final String KEY = FIELDS.trip_id.name();

//	private GtfsStopTime _bean = new GtfsStopTime();
	private String[] _array = new String[FIELDS.values().length];

//	private String _tripId = null;
//	private String _stopId = null;

	public StopTimeByTrip(String name) throws IOException {
		super(name, KEY, false);
	}
	
	@Override
	protected void checkRequiredFields(Map<String, Integer> fields) {
		for (String fieldName : fields.keySet()) {
			if (fieldName != null) {
				if (!fieldName.equals(fieldName.trim())) {
					// extra spaces in end fields are tolerated : 1-GTFS-CSV-7 warning
					getErrors().add(new GtfsException(_path, 1, fieldName, GtfsException.ERROR.EXTRA_SPACE_IN_HEADER_FIELD, null, null));
				}
				
				if (HTMLTagValidator.validate(fieldName.trim())) {
					getErrors().add(new GtfsException(_path, 1, fieldName.trim(), GtfsException.ERROR.HTML_TAG_IN_HEADER_FIELD, null, null));
				}
				
				boolean fieldNameIsExtra = true;
				for (FIELDS field : FIELDS.values()) {
					if (fieldName.trim().equals(field.name())) {
						fieldNameIsExtra = false;
						break;
					}
				}
				if (fieldNameIsExtra) {
					// extra fields are tolerated : 1-GTFS-StopTime-12 warning
					getErrors().add(new GtfsException(_path, 1, fieldName, GtfsException.ERROR.EXTRA_HEADER_FIELD, null, null));
				}
			}
		}

		// checks for ubiquitous header fields : 1-GTFS-StopTime-2 error
		if ( fields.get(FIELDS.stop_id.name()) == null ||
				fields.get(FIELDS.trip_id.name()) == null ||
				fields.get(FIELDS.departure_time.name()) == null ||
				fields.get(FIELDS.arrival_time.name()) == null ||
				fields.get(FIELDS.stop_sequence.name()) == null) {
			
			if (fields.get(FIELDS.stop_id.name()) == null)
				throw new GtfsException(_path, 1, FIELDS.stop_id.name(), GtfsException.ERROR.MISSING_REQUIRED_FIELDS, null, null);
			if (fields.get(FIELDS.trip_id.name()) == null)
				throw new GtfsException(_path, 1, FIELDS.trip_id.name(), GtfsException.ERROR.MISSING_REQUIRED_FIELDS, null, null);
			if (fields.get(FIELDS.departure_time.name()) == null)
				throw new GtfsException(_path, 1, FIELDS.departure_time.name(), GtfsException.ERROR.MISSING_REQUIRED_FIELDS, null, null);
			if (fields.get(FIELDS.arrival_time.name()) == null)
				throw new GtfsException(_path, 1, FIELDS.arrival_time.name(), GtfsException.ERROR.MISSING_REQUIRED_FIELDS, null, null);
			if (fields.get(FIELDS.stop_sequence.name()) == null)
				throw new GtfsException(_path, 1, FIELDS.stop_sequence.name(), GtfsException.ERROR.MISSING_REQUIRED_FIELDS, null, null);
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
		GtfsStopTime _bean = new GtfsStopTime();
		//_bean.getErrors().clear();
		_bean.setId(id);
		
		value = _array[i++]; testExtraSpace(FIELDS.trip_id.name(), value, _bean);
		if (value == null || value.trim().isEmpty()) {
			_bean.getErrors().add(new GtfsException(_path, id, FIELDS.trip_id.name(), GtfsException.ERROR.MISSING_REQUIRED_VALUES, null, null));
		} else {
			_bean.setTripId(STRING_CONVERTER.from(context, FIELDS.trip_id, value, true));
		}
		
		value = _array[i++]; testExtraSpace(FIELDS.stop_id.name(), value, _bean);
		if (value == null || value.trim().isEmpty()) {
			_bean.getErrors().add(new GtfsException(_path, id, FIELDS.stop_id.name(), GtfsException.ERROR.MISSING_REQUIRED_VALUES, null, null));
		} else {
			_bean.setStopId(STRING_CONVERTER.from(context, FIELDS.stop_id, value, true));
		}
		
		value = _array[i++]; testExtraSpace(FIELDS.stop_sequence.name(), value, _bean);
		if (value == null || value.trim().isEmpty()) {
			_bean.getErrors().add(new GtfsException(_path, id, FIELDS.stop_sequence.name(), GtfsException.ERROR.MISSING_REQUIRED_VALUES, null, null));
		} else {
			try {
				int stopSequence = INTEGER_CONVERTER.from(context, FIELDS.stop_sequence, value, true);
				if (stopSequence >= 0)
					_bean.setStopSequence(stopSequence);
				else
					_bean.getErrors().add(new GtfsException(_path, id, FIELDS.stop_sequence.name(), GtfsException.ERROR.INVALID_STOP_SEQUENCE, null, null));
			} catch(GtfsException e) {
				_bean.getErrors().add(new GtfsException(_path, id, FIELDS.stop_sequence.name(), GtfsException.ERROR.INVALID_STOP_SEQUENCE, null, null));
			}
		}
		
		boolean noArrivalTime = false;
		boolean noDepartureTime = false;
						
		value = _array[i++]; testExtraSpace(FIELDS.arrival_time.name(), value, _bean);
		if (value == null || value.trim().isEmpty()) {
			noArrivalTime = true;
		} else {
			try {
				_bean.setArrivalTime(GTFSTIME_CONVERTER.from(context, FIELDS.arrival_time, value, true));
			} catch(GtfsException e) {
				_bean.getErrors().add(new GtfsException(_path, id, FIELDS.arrival_time.name(), GtfsException.ERROR.INVALID_ARRIVAL_TIME, null, null));
			}
		}
		
		value = _array[i++]; testExtraSpace(FIELDS.departure_time.name(), value, _bean);
		if (value == null || value.trim().isEmpty()) {
			noDepartureTime = true;
		} else {
			try {
				_bean.setDepartureTime(GTFSTIME_CONVERTER.from(context, FIELDS.departure_time, value, true));
			} catch(GtfsException e) {
				_bean.getErrors().add(new GtfsException(_path, id, FIELDS.departure_time.name(), GtfsException.ERROR.INVALID_DEPARTURE_TIME, null, null));
			}
		}
		
		if (noArrivalTime && !noDepartureTime) {
			_bean.getErrors().add(new GtfsException(_path, id, FIELDS.arrival_time.name(), GtfsException.ERROR.MISSING_ARRIVAL_TIME, null, null));
		} else if (!noArrivalTime && noDepartureTime) {
			_bean.getErrors().add(new GtfsException(_path, id, FIELDS.departure_time.name(), GtfsException.ERROR.MISSING_DEPARTURE_TIME, null, null));
		} 
		
		value = _array[i++]; testExtraSpace(FIELDS.stop_headsign.name(), value, _bean);
		if (value != null && !value.trim().isEmpty()) {
			_bean.setStopHeadsign(STRING_CONVERTER.from(context, FIELDS.stop_headsign, value, false));
		}
		
		value = _array[i++]; testExtraSpace(FIELDS.pickup_type.name(), value, _bean);
		if (value != null && !value.trim().isEmpty()) {
			try {
				_bean.setPickupType(PICKUP_CONVERTER.from(context, FIELDS.pickup_type, value, PickupType.Scheduled, false));
			} catch(GtfsException e) {
				_bean.getErrors().add(new GtfsException(_path, id, FIELDS.pickup_type.name(), GtfsException.ERROR.INVALID_PICKUP_TYPE, null, null));
			}
		}
		
		value = _array[i++]; testExtraSpace(FIELDS.drop_off_type.name(), value, _bean);
		if (value != null && !value.trim().isEmpty()) {
			try {
				_bean.setDropOffType(DROPOFFTYPE_CONVERTER.from(context, FIELDS.drop_off_type, value, DropOffType.Scheduled, false));
			} catch(GtfsException e) {
				_bean.getErrors().add(new GtfsException(_path, id, FIELDS.drop_off_type.name(), GtfsException.ERROR.INVALID_DROP_OFF_TYPE, null, null));
			}
		}
		
		value = _array[i++]; testExtraSpace(FIELDS.shape_dist_traveled.name(), value, _bean);
		if (value != null && !value.trim().isEmpty()) {
			try {
				float shapeDistTraveled = FLOAT_CONVERTER.from(context, FIELDS.shape_dist_traveled, value, false);
				if (shapeDistTraveled < 0) {
					_bean.getErrors().add(new GtfsException(_path, id, FIELDS.shape_dist_traveled.name(), GtfsException.ERROR.INVALID_SHAPE_DIST_TRAVELED, null, null));
				}
				_bean.setShapeDistTraveled(shapeDistTraveled);
			} catch(GtfsException e) {
				_bean.getErrors().add(new GtfsException(_path, id, FIELDS.shape_dist_traveled.name(), GtfsException.ERROR.INVALID_SHAPE_DIST_TRAVELED, null, null));
			}
		}
		
		value = _array[i++]; testExtraSpace(FIELDS.timepoint.name(), value, _bean);
		if (value != null && !value.trim().isEmpty()) {
			try {
				int timepoint = INTEGER_CONVERTER.from(context, FIELDS.timepoint, value, false);
				if (timepoint < 0 || timepoint > 1) {
					_bean.getErrors().add(new GtfsException(_path, id, FIELDS.timepoint.name(), GtfsException.ERROR.INVALID_TIMEPOINT, null, null));
				}
				_bean.setTimepoint(timepoint);
			} catch(GtfsException e) {
				_bean.getErrors().add(new GtfsException(_path, id, FIELDS.timepoint.name(), GtfsException.ERROR.INVALID_TIMEPOINT, null, null));
			}
		}

		return _bean;
	}

	@Override
	public boolean validate(GtfsStopTime bean, GtfsImporter dao) {
		boolean result = true;
		
		if (bean.getTripId() == null || bean.getStopSequence() == null)
			return result;
		
		int id = bean.getId();
		String tripId = bean.getTripId();
		int stopSequence = bean.getStopSequence();
		
		Iterator<GtfsStopTime> iti = dao.getStopTimeByTrip().values(tripId).iterator();
		while ( iti.hasNext() ) {
			GtfsStopTime nextStopTime = iti.next();
			if (nextStopTime.getStopSequence() == null)
				continue;
			if (id != nextStopTime.getId() && stopSequence == nextStopTime.getStopSequence()) {
				result = false;
				bean.getErrors().add(new GtfsException(_path, nextStopTime.getId(), FIELDS.stop_sequence.name(), GtfsException.ERROR.DUPLICATE_STOP_SEQUENCE, null, null));
			}
		}
		
//		String tripId = bean.getTripId();
//		if (!tripId.equals(_tripId)) {
//			if (!dao.getTripById().containsKey(tripId)) {
//				throw new GtfsException(getPath(), bean.getId(),
//						FIELDS.trip_id.name(), ERROR.MISSING_FOREIGN_KEY,
//						"TODO", bean.getTripId());
//			}
//			_tripId = tripId;
//		}
//
//		String stopId = bean.getStopId();
//		if (!stopId.equals(_stopId)) {
//			if (!dao.getStopById().containsKey(stopId)) {
//				throw new GtfsException(getPath(), bean.getId(),
//						FIELDS.stop_id.name(), ERROR.MISSING_FOREIGN_KEY,
//						"TODO", bean.getStopId());
//			}
//			_stopId = stopId;
//		}

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
