package mobi.chouette.exchange.gtfs.model.importer;

import java.io.IOException;
import java.util.Map;

import mobi.chouette.common.HTMLTagValidator;
import mobi.chouette.exchange.gtfs.model.GtfsCalendar;

public class CalendarByService extends IndexImpl<GtfsCalendar> implements GtfsConverter {

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
					// extra fields are tolerated : 1-GTFS-Calendar-14 warning
					getErrors().add(new GtfsException(_path, 1, getIndex(fieldName), fieldName, GtfsException.ERROR.EXTRA_HEADER_FIELD, null, null));
				}
			}
		}

		// checks for ubiquitous header fields : 1-GTFS-Trip-2 error
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
			
			String name = "";
			if ( fields.get(FIELDS.service_id.name()) == null)
				name = FIELDS.service_id.name();
			if ( fields.get(FIELDS.monday.name()) == null)
				name = FIELDS.monday.name();
			if ( fields.get(FIELDS.tuesday.name()) == null)
					name = FIELDS.tuesday.name();
			if ( fields.get(FIELDS.wednesday.name()) == null)
				name = FIELDS.wednesday.name();
			if ( fields.get(FIELDS.thursday.name()) == null)
				name = FIELDS.thursday.name();
			if ( fields.get(FIELDS.friday.name()) == null)
				name = FIELDS.friday.name();
			if ( fields.get(FIELDS.saturday.name()) == null)
				name = FIELDS.saturday.name();
			if ( fields.get(FIELDS.sunday.name()) == null)
				name = FIELDS.sunday.name();
			if ( fields.get(FIELDS.start_date.name()) == null)
				name = FIELDS.start_date.name();
			if ( fields.get(FIELDS.end_date.name()) == null)
				name = FIELDS.end_date.name();
			
			throw new GtfsException(_path, 1, name, GtfsException.ERROR.MISSING_REQUIRED_FIELDS, null, null);
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
		clearBean();
		bean.setId(id);
		bean.getErrors().clear();
		
		value = array[i++]; testExtraSpace(FIELDS.service_id.name(), value, bean);
		if (value == null || value.trim().isEmpty()) {
			throw new GtfsException(_path, id, getIndex(FIELDS.service_id.name()), FIELDS.service_id.name(), GtfsException.ERROR.MISSING_REQUIRED_VALUES, null, null);
		} else {
			bean.setServiceId(STRING_CONVERTER.from(context, FIELDS.service_id, value, true));
		}
		
		boolean hasAvalidDay = false;
		value = array[i++]; testExtraSpace(FIELDS.monday.name(), value, bean);
		if (value == null || value.trim().isEmpty()) {
			if (withValidation)
				bean.getErrors().add(new GtfsException(_path, id, getIndex(FIELDS.monday.name()), FIELDS.monday.name(), GtfsException.ERROR.MISSING_REQUIRED_VALUES, null, null));
		} else {
			try {
				bean.setMonday(BOOLEAN_CONVERTER.from(context, FIELDS.monday, value, true));
				hasAvalidDay = hasAvalidDay || bean.getMonday();
			} catch(GtfsException ex) {
				if (withValidation)
					bean.getErrors().add(new GtfsException(_path, id, getIndex(FIELDS.monday.name()), FIELDS.monday.name(), GtfsException.ERROR.INVALID_FORMAT, null, value));
			}
		}
		
		value = array[i++]; testExtraSpace(FIELDS.tuesday.name(), value, bean);
		if (value == null || value.trim().isEmpty()) {
			if (withValidation)
				bean.getErrors().add(new GtfsException(_path, id, getIndex(FIELDS.tuesday.name()), FIELDS.tuesday.name(), GtfsException.ERROR.MISSING_REQUIRED_VALUES, null, null));
		} else {
			try {
				bean.setTuesday(BOOLEAN_CONVERTER.from(context, FIELDS.tuesday, value, true));
				hasAvalidDay = hasAvalidDay || bean.getTuesday();
			} catch(GtfsException ex) {
				if (withValidation)
					bean.getErrors().add(new GtfsException(_path, id, getIndex(FIELDS.tuesday.name()), FIELDS.tuesday.name(), GtfsException.ERROR.INVALID_FORMAT, null, value));
			}
		}
		
		value = array[i++]; testExtraSpace(FIELDS.wednesday.name(), value, bean);
		if (value == null || value.trim().isEmpty()) {
			if (withValidation)
				bean.getErrors().add(new GtfsException(_path, id, getIndex(FIELDS.wednesday.name()), FIELDS.wednesday.name(), GtfsException.ERROR.MISSING_REQUIRED_VALUES, null, null));
		} else {
			try {
				bean.setWednesday(BOOLEAN_CONVERTER.from(context, FIELDS.wednesday, value, true));
				hasAvalidDay = hasAvalidDay || bean.getWednesday();
			} catch(GtfsException ex) {
				if (withValidation)
					bean.getErrors().add(new GtfsException(_path, id, getIndex(FIELDS.wednesday.name()), FIELDS.wednesday.name(), GtfsException.ERROR.INVALID_FORMAT, null, value));
			}
		}
		
		value = array[i++]; testExtraSpace(FIELDS.thursday.name(), value, bean);
		if (value == null || value.trim().isEmpty()) {
			if (withValidation)
				bean.getErrors().add(new GtfsException(_path, id, getIndex(FIELDS.thursday.name()), FIELDS.thursday.name(), GtfsException.ERROR.MISSING_REQUIRED_VALUES, null, null));
		} else {
			try {
				bean.setThursday(BOOLEAN_CONVERTER.from(context, FIELDS.thursday, value, true));
				hasAvalidDay = hasAvalidDay || bean.getThursday();
			} catch(GtfsException ex) {
				if (withValidation)
					bean.getErrors().add(new GtfsException(_path, id, getIndex(FIELDS.thursday.name()), FIELDS.thursday.name(), GtfsException.ERROR.INVALID_FORMAT, null, value));
			}
		}
		
		value = array[i++]; testExtraSpace(FIELDS.friday.name(), value, bean);
		if (value == null || value.trim().isEmpty()) {
			if (withValidation)
				bean.getErrors().add(new GtfsException(_path, id, getIndex(FIELDS.friday.name()), FIELDS.friday.name(), GtfsException.ERROR.MISSING_REQUIRED_VALUES, null, null));
		} else {
			try {
				bean.setFriday(BOOLEAN_CONVERTER.from(context, FIELDS.friday, value, true));
				hasAvalidDay = hasAvalidDay || bean.getFriday();
			} catch(GtfsException ex) {
				if (withValidation)
					bean.getErrors().add(new GtfsException(_path, id, getIndex(FIELDS.friday.name()), FIELDS.friday.name(), GtfsException.ERROR.INVALID_FORMAT, null, value));
			}
		}
		
		value = array[i++]; testExtraSpace(FIELDS.saturday.name(), value, bean);
		if (value == null || value.trim().isEmpty()) {
			if (withValidation)
				bean.getErrors().add(new GtfsException(_path, id, getIndex(FIELDS.saturday.name()), FIELDS.saturday.name(), GtfsException.ERROR.MISSING_REQUIRED_VALUES, null, null));
		} else {
			try {
				bean.setSaturday(BOOLEAN_CONVERTER.from(context, FIELDS.saturday, value, true));
				hasAvalidDay = hasAvalidDay || bean.getSaturday();
			} catch(GtfsException ex) {
				if (withValidation)
					bean.getErrors().add(new GtfsException(_path, id, getIndex(FIELDS.saturday.name()), FIELDS.saturday.name(), GtfsException.ERROR.INVALID_FORMAT, null, value));
			}
		}
		
		value = array[i++]; testExtraSpace(FIELDS.sunday.name(), value, bean);
		if (value == null || value.trim().isEmpty()) {
			if (withValidation)
				bean.getErrors().add(new GtfsException(_path, id, getIndex(FIELDS.sunday.name()), FIELDS.sunday.name(), GtfsException.ERROR.MISSING_REQUIRED_VALUES, null, null));
		} else {
			try {
				bean.setSunday(BOOLEAN_CONVERTER.from(context, FIELDS.sunday, value, true));
				hasAvalidDay = hasAvalidDay || bean.getSunday();
			} catch(GtfsException ex) {
				if (withValidation)
					bean.getErrors().add(new GtfsException(_path, id, getIndex(FIELDS.sunday.name()), FIELDS.sunday.name(), GtfsException.ERROR.INVALID_FORMAT, null, value));
			}
		}
		
		value = array[i++]; testExtraSpace(FIELDS.start_date.name(), value, bean);
		if (value == null || value.trim().isEmpty()) {
			if (withValidation)
				bean.getErrors().add(new GtfsException(_path, id, getIndex(FIELDS.start_date.name()), FIELDS.start_date.name(), GtfsException.ERROR.MISSING_REQUIRED_VALUES, null, null));
		} else {
			try {
				bean.setStartDate(DATE_CONVERTER.from(context, FIELDS.start_date, value, true));
			} catch(GtfsException ex) {
				if (withValidation)
					bean.getErrors().add(new GtfsException(_path, id, getIndex(FIELDS.start_date.name()), FIELDS.start_date.name(), GtfsException.ERROR.INVALID_FORMAT, null, value));
			}
		}
		
		value = array[i++]; testExtraSpace(FIELDS.end_date.name(), value, bean);
		if (value == null || value.trim().isEmpty()) {
			if (withValidation)
				bean.getErrors().add(new GtfsException(_path, id, getIndex(FIELDS.end_date.name()), FIELDS.end_date.name(), GtfsException.ERROR.MISSING_REQUIRED_VALUES, null, null));
		} else {
			try {
				bean.setEndDate(DATE_CONVERTER.from(context, FIELDS.end_date, value, true));
			} catch(GtfsException ex) {
				if (withValidation)
					bean.getErrors().add(new GtfsException(_path, id, getIndex(FIELDS.end_date.name()), FIELDS.end_date.name(), GtfsException.ERROR.INVALID_FORMAT, null, value));
			}
		}
		
		// monday || .. || sunday = true
		if (hasAvalidDay) {
			bean.getOkTests().add(GtfsException.ERROR.ALL_DAYS_ARE_INVALID);
		} else {
			if (withValidation)
				bean.getErrors().add(new GtfsException(_path, id, FIELDS.service_id.name(), GtfsException.ERROR.ALL_DAYS_ARE_INVALID, null, bean.getServiceId()));
		}
		
		// startDate <= Enddate
		if (bean.getStartDate() != null && bean.getEndDate() != null) {
			if (bean.getStartDate().after(bean.getEndDate())) {
				if (withValidation)
					bean.getErrors().add(new GtfsException(_path, id, getIndex(FIELDS.start_date.name()), FIELDS.start_date.name(), GtfsException.ERROR.START_DATE_AFTER_END_DATE, null, bean.getServiceId()));
			} else {
				bean.getOkTests().add(GtfsException.ERROR.START_DATE_AFTER_END_DATE);
			}
		}

		return bean;
	}

	@Override
	public boolean validate(GtfsCalendar bean, GtfsImporter dao) {
		return true;
	}

	private void clearBean() {
		//bean.getErrors().clear();
		bean.setId(null);
		bean.setEndDate(null);
		bean.setFriday(null);
		bean.setMonday(null);
		bean.setSaturday(null);
		bean.setServiceId(null);
		bean.setStartDate(null);
		bean.setSunday(null);
		bean.setThursday(null);
		bean.setTuesday(null);
		bean.setWednesday(null);
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
