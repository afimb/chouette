package mobi.chouette.exchange.gtfs.model.importer;

import java.io.IOException;
import java.util.Map;

import mobi.chouette.common.HTMLTagValidator;
import mobi.chouette.exchange.gtfs.model.GtfsFrequency;

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
		for (String fieldName : fields.keySet()) {
			if (fieldName != null) {
				if (!fieldName.equals(fieldName.trim())) {
					// extra spaces in end fields are tolerated : 1-GTFS-CSV-7 warning
					getErrors().add(new GtfsException(_path, 1, getIndex(fieldName), fieldName.trim(), GtfsException.ERROR.EXTRA_SPACE_IN_HEADER_FIELD, null, fieldName));
				}
				
				if (HTMLTagValidator.validate(fieldName.trim())) {
					getErrors().add(new GtfsException(_path, 1, getIndex(fieldName), fieldName.trim(), GtfsException.ERROR.HTML_TAG_IN_HEADER_FIELD, null, null));
				}
				
				boolean fieldNameIsExtra = true;
				for (FIELDS field : FIELDS.values()) {
					if (fieldName.trim().equals(field.name())) {
						fieldNameIsExtra = false;
						break;
					}
				}
				if (fieldNameIsExtra) {
					// extra fields are tolerated : 1-GTFS-Frequency-7 warning
					getErrors().add(new GtfsException(_path, 1, getIndex(fieldName), fieldName, GtfsException.ERROR.EXTRA_HEADER_FIELD, null, null));
				}
			}
		}

		// checks for ubiquitous header fields : 1-GTFS-Frequency-1 error
		if ( fields.get(FIELDS.trip_id.name()) == null ||
				fields.get(FIELDS.start_time.name()) == null ||
				fields.get(FIELDS.end_time.name()) == null ||
				fields.get(FIELDS.headway_secs.name()) == null) {
			
			String name = "";
			if (fields.get(FIELDS.trip_id.name()) == null)
				name = FIELDS.trip_id.name();
			else if (fields.get(FIELDS.start_time.name()) == null)
				name = FIELDS.start_time.name();
			else if (fields.get(FIELDS.end_time.name()) == null)
				name = FIELDS.end_time.name();
			else if (fields.get(FIELDS.headway_secs.name()) == null)
				name = FIELDS.headway_secs.name();
			
			throw new GtfsException(_path, 1, name, GtfsException.ERROR.MISSING_REQUIRED_FIELDS, null, null);
		}
	}

	@Override
	protected GtfsFrequency build(GtfsIterator reader, Context context) {
		int i = 0;
		for (FIELDS field : FIELDS.values()) {
			array[i++] = getField(reader, field.name());
		}

		i = 0;
		int id = (int) context.get(Context.ID);
		String value = null;
		bean.setId(id);
		clearBean();
		//bean.getErrors().clear();
		
		value = array[i++]; testExtraSpace(FIELDS.trip_id.name(), value, bean);
		if (value == null || value.trim().isEmpty()) {
			bean.getErrors().add(new GtfsException(_path, id, getIndex(FIELDS.trip_id.name()), FIELDS.trip_id.name(), GtfsException.ERROR.MISSING_REQUIRED_VALUES, null, null));
		} else {
			bean.setTripId(STRING_CONVERTER.from(context, FIELDS.trip_id, value, true));
		}
		
		value = array[i++]; testExtraSpace(FIELDS.start_time.name(), value, bean);
		if (value == null || value.trim().isEmpty()) {
			bean.getErrors().add(new GtfsException(_path, id, getIndex(FIELDS.start_time.name()), FIELDS.start_time.name(), GtfsException.ERROR.MISSING_REQUIRED_VALUES, null, null));
		} else {
			try {
				bean.setStartTime(GTFSTIME_CONVERTER.from(context, FIELDS.start_time, value, true));
			} catch(GtfsException ex) {
				bean.getErrors().add(new GtfsException(_path, id, getIndex(FIELDS.start_time.name()), FIELDS.start_time.name(), GtfsException.ERROR.INVALID_FORMAT, null, value));
			}
		}
		
		value = array[i++]; testExtraSpace(FIELDS.end_time.name(), value, bean);
		if (value == null || value.trim().isEmpty()) {
			bean.getErrors().add(new GtfsException(_path, id, getIndex(FIELDS.end_time.name()), FIELDS.end_time.name(), GtfsException.ERROR.MISSING_REQUIRED_VALUES, null, null));
		} else {
			try {
				bean.setEndTime(GTFSTIME_CONVERTER.from(context, FIELDS.end_time, value, true));
			} catch(GtfsException ex) {
				bean.getErrors().add(new GtfsException(_path, id, getIndex(FIELDS.end_time.name()), FIELDS.end_time.name(), GtfsException.ERROR.INVALID_FORMAT, null, value));
			}
		}
		
		value = array[i++]; testExtraSpace(FIELDS.headway_secs.name(), value, bean);
		if (value == null || value.trim().isEmpty()) {
			bean.getErrors().add(new GtfsException(_path, id, getIndex(FIELDS.headway_secs.name()), FIELDS.headway_secs.name(), GtfsException.ERROR.MISSING_REQUIRED_VALUES, null, null));
		} else {
			try {
				bean.setHeadwaySecs(POSITIVE_INTEGER_CONVERTER.from(context, FIELDS.headway_secs, value, true));
			} catch(GtfsException ex) {
				bean.getErrors().add(new GtfsException(_path, id, getIndex(FIELDS.headway_secs.name()), FIELDS.headway_secs.name(), GtfsException.ERROR.INVALID_FORMAT, null, value));
			}
		}
		
		value = array[i++]; testExtraSpace(FIELDS.exact_times.name(), value, bean);
		if (value != null && !value.trim().isEmpty()) {
			try {
				bean.setExactTimes(BOOLEAN_CONVERTER.from(context, FIELDS.exact_times, value, false, false));
			} catch(GtfsException ex) {
				bean.getErrors().add(new GtfsException(_path, id, getIndex(FIELDS.exact_times.name()), FIELDS.exact_times.name(), GtfsException.ERROR.INVALID_FORMAT, null, value));
			}
		}
			
		return bean;
	}

	private void clearBean() {
		//bean.getErrors().clear();
		bean.setId(null);
		bean.setEndTime(null);
		bean.setExactTimes(null);
		bean.setHeadwaySecs(null);
		bean.setStartTime(null);
		bean.setTripId(null);
	}

	@Override
	public boolean validate(GtfsFrequency bean, GtfsImporter dao) {
		boolean result = true;
//		String tripId = bean.getTripId();
//		if (!tripId.equals(_tripId)) {
//			if (!dao.getTripById().containsKey(tripId)) {
//				throw new GtfsException(getPath(), bean.getId(),
//						FIELDS.trip_id.name(), ERROR.MISSING_FOREIGN_KEY,
//						"TODO", bean.getTripId());
//			}
//			_tripId = tripId;
//		}

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
