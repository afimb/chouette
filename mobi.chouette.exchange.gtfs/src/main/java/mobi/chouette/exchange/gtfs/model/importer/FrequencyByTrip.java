package mobi.chouette.exchange.gtfs.model.importer;

import java.io.IOException;
import java.util.Map;

import mobi.chouette.exchange.gtfs.model.GtfsFrequency;
import mobi.chouette.exchange.gtfs.model.importer.GtfsException.ERROR;

public class FrequencyByTrip extends IndexImpl<GtfsFrequency> implements
		GtfsConverter {

	public static enum FIELDS {
		trip_id, start_time, end_time, headway_secs, exact_times;
	};

	public static final String FILENAME = "frequencies.txt";
	public static final String KEY = FIELDS.trip_id.name();

	private GtfsFrequency bean = new GtfsFrequency();
	private String[] array = new String[FIELDS.values().length];
	private String _tripId = null;

	public FrequencyByTrip(String name) throws IOException {
		super(name, KEY, false);
	}
	
	@Override
	protected void checkRequiredFields(Map<String, Integer> fields) {
		// extra fields are tolerated : 1-GTFS-Frequency-7 warning
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
		
		// checks for ubiquitous header fields : 1-GTFS-Frequency-1 error
		if ( fields.get(FIELDS.trip_id.name()) == null ||
				fields.get(FIELDS.start_time.name()) == null ||
				fields.get(FIELDS.end_time.name()) == null ||
				fields.get(FIELDS.headway_secs.name()) == null) {
			Context context = new Context();
			context.put(Context.PATH, _path);
			context.put(Context.ERROR, GtfsException.ERROR.MISSING_REQUIRED_FIELDS);
			getErrors().add(new GtfsException(context));
		}
	}

	@Override
	protected GtfsFrequency build(GtfsIterator reader, Context context) {
		int i = 0;
		for (FIELDS field : FIELDS.values()) {
			array[i++] = getField(reader, field.name());
		}

		i = 0;
		// int id = (int) context.get(Context.ID);
		String value = null;
		bean.getErrors().clear();
		value = array[i++];
		bean.setTripId(STRING_CONVERTER.from(context, FIELDS.trip_id, value, true));
		value = array[i++];
		bean.setStartTime(GTFSTIME_CONVERTER.from(context, FIELDS.start_time, value, true));
		value = array[i++];
		bean.setEndTime(GTFSTIME_CONVERTER.from(context, FIELDS.end_time, value, true));
		value = array[i++];
		bean.setHeadwaySecs(POSITIVE_INTEGER_CONVERTER.from(context, FIELDS.headway_secs, value, true));
		value = array[i++];
		bean.setExactTimes(BOOLEAN_CONVERTER.from(context, FIELDS.exact_times, value, false, false));

		return bean;
	}

	@Override
	public boolean validate(GtfsFrequency bean, GtfsImporter dao) {
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

		return result;
	}

	public static class DefaultImporterFactory extends IndexFactory {
		@SuppressWarnings("rawtypes")
		@Override
		protected Index create(String name) throws IOException {
			return new FrequencyByTrip(name);
		}
	}

	static {
		IndexFactory factory = new DefaultImporterFactory();
		IndexFactory.factories.put(FrequencyByTrip.class.getName(), factory);
	}

}
