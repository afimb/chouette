package mobi.chouette.exchange.gtfs.model.importer;

import java.io.IOException;
import java.util.Map;

import mobi.chouette.exchange.gtfs.model.GtfsCalendarDate;

public class CalendarDateByService extends IndexImpl<GtfsCalendarDate>
		implements GtfsConverter {

	public static enum FIELDS {
		service_id, date, exception_type;
	};

	public static final String FILENAME = "calendar_dates.txt";
	public static final String KEY = FIELDS.service_id.name();

	private GtfsCalendarDate bean = new GtfsCalendarDate();
	private String[] array = new String[FIELDS.values().length];

	public CalendarDateByService(String name) throws IOException {
		super(name, KEY, false);
	}
	
	@Override
	protected void checkRequiredFields(Map<String, Integer> fields) {
		// extra fields are tolerated : 1-GTFS-CalendarDate-7 warning
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
		
		// checks for ubiquitous header fields : 1-GTFS-CalendarDate-2 error
		if ( fields.get(FIELDS.service_id.name()) == null ||
				fields.get(FIELDS.date.name()) == null ||
				fields.get(FIELDS.exception_type.name()) == null) {
			Context context = new Context();
			context.put(Context.PATH, _path);
			context.put(Context.ERROR, GtfsException.ERROR.MISSING_REQUIRED_FIELDS);
			getErrors().add(new GtfsException(context));
		}
	}

	@Override
	protected GtfsCalendarDate build(GtfsIterator reader, Context context) {
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
		bean.setDate(DATE_CONVERTER.from(context, FIELDS.date, value, true));
		value = array[i++];
		bean.setExceptionType(EXCEPTIONTYPE_CONVERTER.from(context, FIELDS.exception_type, value, true));

		return bean;
	}

	@Override
	public boolean validate(GtfsCalendarDate bean, GtfsImporter dao) {
		return true;
	}

	public static class DefaultImporterFactory extends IndexFactory {
		@SuppressWarnings("rawtypes")
		@Override
		protected Index create(String name) throws IOException {
			return new CalendarDateByService(name);
		}
	}

	static {
		IndexFactory factory = new DefaultImporterFactory();
		IndexFactory.factories.put(CalendarDateByService.class.getName(),
				factory);
	}

}
