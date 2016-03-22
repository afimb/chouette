package mobi.chouette.exchange.gtfs.model.importer;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import mobi.chouette.common.HTMLTagValidator;
import mobi.chouette.exchange.gtfs.model.GtfsCalendarDate;

public class CalendarDateByService extends IndexImpl<GtfsCalendarDate>
		implements GtfsConverter {

	public static enum FIELDS {
		service_id, date, exception_type;
	};

	public static final String FILENAME = "calendar_dates.txt";
	public static final String KEY = FIELDS.service_id.name();
	public static final Set<String> hashCodes = new HashSet<String>();

	private GtfsCalendarDate bean = new GtfsCalendarDate();
	private String[] array = new String[FIELDS.values().length];

	public CalendarDateByService(String name) throws IOException {
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
					// extra fields are tolerated : 1-GTFS-CalendarDate-7 warning
					getErrors().add(new GtfsException(_path, 1, getIndex(fieldName), fieldName, GtfsException.ERROR.EXTRA_HEADER_FIELD, null, null));
				}
			}
		}

		// checks for ubiquitous header fields : 1-GTFS-Trip-2 error
		if ( fields.get(FIELDS.service_id.name()) == null ||
				fields.get(FIELDS.date.name()) == null ||
				fields.get(FIELDS.exception_type.name()) == null) {
			
			String name = "";
			if ( fields.get(FIELDS.service_id.name()) == null)
				name = FIELDS.service_id.name();
			else if ( fields.get(FIELDS.date.name()) == null)
				name = FIELDS.date.name();
			else if ( fields.get(FIELDS.exception_type.name()) == null)
				name = FIELDS.exception_type.name();
			
			throw new GtfsException(_path, 1, name, GtfsException.ERROR.MISSING_REQUIRED_FIELDS, null, null);
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
		clearBean();
		bean.setId(id);
		bean.getErrors().clear();
		
		value = array[i++]; testExtraSpace(FIELDS.service_id.name(), value, bean);
		if (value == null || value.trim().isEmpty()) {
			throw new GtfsException(_path, id, getIndex(FIELDS.service_id.name()), FIELDS.service_id.name(), GtfsException.ERROR.MISSING_REQUIRED_VALUES, null, null);
		} else {
			bean.setServiceId(STRING_CONVERTER.from(context, FIELDS.service_id, value, true));
		}
		
		value = array[i++]; testExtraSpace(FIELDS.date.name(), value, bean);
		if (value == null || value.trim().isEmpty()) {
			if (withValidation)
				bean.getErrors().add(new GtfsException(_path, id, getIndex(FIELDS.date.name()), FIELDS.date.name(), GtfsException.ERROR.MISSING_REQUIRED_VALUES, null, null));
		} else {
			try {
				bean.setDate(DATE_CONVERTER.from(context, FIELDS.date, value, true));
			} catch(GtfsException ex) {
				if (withValidation)
					bean.getErrors().add(new GtfsException(_path, id, getIndex(FIELDS.date.name()), FIELDS.date.name(), GtfsException.ERROR.INVALID_FORMAT, null, value));
			}
		}
		
		value = array[i++]; testExtraSpace(FIELDS.exception_type.name(), value, bean);
		if (value == null || value.trim().isEmpty()) {
			if (withValidation)
				bean.getErrors().add(new GtfsException(_path, id, FIELDS.exception_type.name(), GtfsException.ERROR.MISSING_REQUIRED_VALUES, null, null));
		} else {
			try {
				bean.setExceptionType(EXCEPTIONTYPE_CONVERTER.from(context, FIELDS.exception_type, value, true));
			} catch(GtfsException ex) {
				if (withValidation)
					bean.getErrors().add(new GtfsException(_path, id, getIndex(FIELDS.exception_type.name()), FIELDS.exception_type.name(), GtfsException.ERROR.INVALID_FORMAT, null, value));
			}
		}
		
		return bean;
	}

	@Override
	public boolean validate(GtfsCalendarDate bean, GtfsImporter dao) {
		boolean result = true;
		
		if (bean.getDate() != null && bean.getServiceId() != null)
			result = hashCodes.add(bean.getServiceId()+"#"+bean.getDate().getTime());
		if (!result)
		{
			SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
			bean.getErrors().add(new GtfsException(_path, bean.getId(), getIndex(FIELDS.service_id.name()), FIELDS.service_id.name()+","+FIELDS.date.name(), GtfsException.ERROR.DUPLICATE_DOUBLE_KEY, null, bean.getServiceId()+","+format.format(bean.getDate())));
		}
		
		return result;
	}

	private void clearBean() {
		//bean.getErrors().clear();
		bean.setId(null);
		bean.setDate(null);
		bean.setExceptionType(null);
		bean.setServiceId(null);
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
