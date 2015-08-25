package mobi.chouette.exchange.gtfs.model.importer;

import java.io.IOException;
import java.util.Map;

import mobi.chouette.exchange.gtfs.model.GtfsCalendar;

public class CalendarByService extends IndexImpl<GtfsCalendar> implements
		GtfsConverter {

	public static enum FIELDS {
		service_id, monday, tuesday, wednesday, thursday, friday, saturday, sunday, start_date, end_date;
	};

	public static final String FILENAME = "calendar.txt";
	public static final String KEY = FIELDS.service_id.name();

	private GtfsCalendar bean = new GtfsCalendar();
	private String[] array = new String[FIELDS.values().length];

	public CalendarByService(String name) throws IOException {
		super(name, KEY);
	}
	
	@Override
	protected void checkRequiredFields(Map<String, Integer> fields) {
		// extra fields are tolerated : 1-GTFS-Calendar-14 warning
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
		
		// checks for ubiquitous header fields : 1-GTFS-Calendar-2 error
		if ( fields.get(FIELDS.service_id.name()) == null ||
				fields.get(FIELDS.monday.name()) == null ||
				fields.get(FIELDS.tuesday.name()) == null ||
				fields.get(FIELDS.wednesday.name()) == null ||
				fields.get(FIELDS.thursday.name()) == null ||
				fields.get(FIELDS.friday.name()) == null ||
				fields.get(FIELDS.saturday.name()) == null ||
				fields.get(FIELDS.sunday.name()) == null ||
				fields.get(FIELDS.start_date.name()) == null ||
				fields.get(FIELDS.end_date.name()) == null) {
			Context context = new Context();
			context.put(Context.PATH, _path);
			context.put(Context.ERROR, GtfsException.ERROR.MISSING_REQUIRED_FIELDS);
			getErrors().add(new GtfsException(context));
		}
	}

	@Override
	protected GtfsCalendar build(GtfsIterator reader, Context context) {
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
		bean.setServiceId(STRING_CONVERTER.from(context, FIELDS.service_id, value, true));
		value = array[i++];
		bean.setMonday(BOOLEAN_CONVERTER.from(context, FIELDS.monday, value, true));
		value = array[i++];
		bean.setTuesday(BOOLEAN_CONVERTER.from(context, FIELDS.tuesday, value, true));
		value = array[i++];
		bean.setWednesday(BOOLEAN_CONVERTER.from(context, FIELDS.wednesday, value, true));
		value = array[i++];
		bean.setThursday(BOOLEAN_CONVERTER.from(context, FIELDS.thursday, value, true));
		value = array[i++];
		bean.setFriday(BOOLEAN_CONVERTER.from(context, FIELDS.friday, value, true));
		value = array[i++];
		bean.setSaturday(BOOLEAN_CONVERTER.from(context, FIELDS.saturday, value, true));
		value = array[i++];
		bean.setSunday(BOOLEAN_CONVERTER.from(context, FIELDS.sunday, value, true));
		value = array[i++];
		bean.setStartDate(DATE_CONVERTER.from(context, FIELDS.start_date, value, true));
		value = array[i++];
		bean.setEndDate(DATE_CONVERTER.from(context, FIELDS.end_date, value, true));

		return bean;
	}

	@Override
	public boolean validate(GtfsCalendar bean, GtfsImporter dao) {
		return true;
	}

	public static class DefaultImporterFactory extends IndexFactory {
		@SuppressWarnings("rawtypes")
		@Override
		protected Index create(String name) throws IOException {
			return new CalendarByService(name);
		}
	}

	static {
		IndexFactory factory = new DefaultImporterFactory();
		IndexFactory.factories.put(CalendarByService.class.getName(), factory);
	}

}
