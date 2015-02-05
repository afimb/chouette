package mobi.chouette.exchange.gtfs.model.importer;

import java.io.IOException;

import mobi.chouette.exchange.gtfs.model.GtfsStopTime;
import mobi.chouette.exchange.gtfs.model.GtfsStopTime.DropOffType;
import mobi.chouette.exchange.gtfs.model.GtfsStopTime.PickupType;
import mobi.chouette.exchange.gtfs.model.importer.GtfsException.ERROR;

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
	protected GtfsStopTime build(GtfsIterator reader, Context context) {

		int i = 0;
		for (FIELDS field : FIELDS.values()) {
			_array[i++] = getField(reader, field.name());
		}

		i = 0;
		int id = (int) context.get(Context.ID);
		_bean.setId(id);
		_bean.setTripId(STRING_CONVERTER.from(context, FIELDS.trip_id,
				_array[i++], true));
		_bean.setStopId(STRING_CONVERTER.from(context, FIELDS.stop_id,
				_array[i++], true));
		_bean.setStopSequence(INTEGER_CONVERTER.from(context,
				FIELDS.stop_sequence, _array[i++], true));
		_bean.setArrivalTime(GTFSTIME_CONVERTER.from(context,
				FIELDS.arrival_time, _array[i++], true));
		_bean.setDepartureTime(GTFSTIME_CONVERTER.from(context,
				FIELDS.departure_time, _array[i++], true));
		_bean.setStopHeadsign(STRING_CONVERTER.from(context,
				FIELDS.stop_headsign, _array[i++], false));
		_bean.setPickupType(PICKUP_CONVERTER.from(context, FIELDS.pickup_type,
				_array[i++], PickupType.Scheduled, false));
		_bean.setDropOffType(DROPOFFTYPE_CONVERTER
				.from(context, FIELDS.drop_off_type, _array[i++],
						DropOffType.Scheduled, false));
		_bean.setShapeDistTraveled(FLOAT_CONVERTER.from(context,
				FIELDS.shape_dist_traveled, _array[i++], false));

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
