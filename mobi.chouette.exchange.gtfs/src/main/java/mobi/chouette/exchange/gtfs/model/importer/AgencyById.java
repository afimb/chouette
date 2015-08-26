package mobi.chouette.exchange.gtfs.model.importer;

import java.io.IOException;
import java.util.Arrays;
import java.util.Locale;
import java.util.Map;

import mobi.chouette.exchange.gtfs.model.GtfsAgency;

public class AgencyById extends IndexImpl<GtfsAgency> implements GtfsConverter {

	public static enum FIELDS {
		agency_id, agency_name, agency_url, agency_timezone, agency_phone, agency_lang, agency_fare_url;
	};

	public static final String FILENAME = "agency.txt";
	public static final String KEY = FIELDS.agency_id.name();

	private GtfsAgency bean = new GtfsAgency();
	private String[] array = new String[FIELDS.values().length];

	public AgencyById(String name) throws IOException {
		super(name, KEY, "default", true);
	}
	
	@Override
	protected void checkRequiredFields(Map<String, Integer> fields) {
		// extra fields are tolerated : 1-GTFS-Agency-10 warning
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
		
		// checks for ubiquitous header fields : 1-GTFS-Agency-2, 1-GTFS-Agency-4 error
		if ( fields.get(FIELDS.agency_id.name()) == null ||
				fields.get(FIELDS.agency_name.name()) == null ||
				fields.get(FIELDS.agency_url.name()) == null ||
				fields.get(FIELDS.agency_timezone.name()) == null) {
			Context context = new Context();
			context.put(Context.PATH, _path);
			context.put(Context.ERROR, GtfsException.ERROR.MISSING_REQUIRED_FIELDS);
			getErrors().add(new GtfsException(context));
		}
	}

	@Override
	protected GtfsAgency build(GtfsIterator reader, Context context) {
		int i = 0;
		for (FIELDS field : FIELDS.values()) {
			array[i++] = getField(reader, field.name()); // array[i++] may be ""
		}

		i = 0;
		String value = null;
		int id = (int) context.get(Context.ID);
		bean.getErrors().clear();
		bean.setId(id);
		value = array[i++];
		
		// 1-GTFS-Agency-5
		if (value == null || value.isEmpty()) {
			Context contxt = new Context();
			contxt.put(Context.PATH, _path);
			contxt.put(Context.FIELD, FIELDS.agency_id);
			contxt.put(Context.ERROR, GtfsException.ERROR.MISSING_REQUIRED_VALUES);
			throw new GtfsException(contxt);
		}
		// 1-GTFS-Agency-3 test value is uniq ?
		//if (value) {
		//}
		bean.setAgencyId(STRING_CONVERTER.from(context, FIELDS.agency_id, value, GtfsAgency.DEFAULT_ID, false));
		
		// check the existance of agency_name, agency_url and agency_timezone values for this bean : 1-GTFS-Agency-5
		value = array[i++];
		// 1-GTFS-Agency-5
		if (value == null || value.isEmpty()) {
			Context contxt = new Context();
			contxt.put(Context.PATH, _path);
			contxt.put(Context.FIELD, FIELDS.agency_name);
			contxt.put(Context.ERROR, GtfsException.ERROR.MISSING_REQUIRED_VALUES);
			getErrors().add(new GtfsException(contxt));
		}
		bean.setAgencyName(STRING_CONVERTER.from(context, FIELDS.agency_name, value, true));
		value = array[i++];
		// 1-GTFS-Agency-5
		if (value == null || value.isEmpty()) {
			Context contxt = new Context();
			contxt.put(Context.PATH, _path);
			contxt.put(Context.FIELD, FIELDS.agency_url);
			contxt.put(Context.ERROR, GtfsException.ERROR.MISSING_REQUIRED_VALUES);
			getErrors().add(new GtfsException(contxt));
		}
		try {
			bean.setAgencyUrl(URL_CONVERTER.from(context, FIELDS.agency_url, value, true));
		} catch (GtfsException e) {
			// 1-GTFS-Agency-7  warning
			Context contxt = new Context();
			contxt.put(Context.PATH, _path);
			contxt.put(Context.FIELD, FIELDS.agency_url);
			contxt.put(Context.ERROR, GtfsException.ERROR.INVALID_URL);
			getErrors().add(new GtfsException(contxt));			
		}
		value = array[i++];
		// 1-GTFS-Agency-5
		if (value == null || value.isEmpty()) {
			Context contxt = new Context();
			contxt.put(Context.PATH, _path);
			contxt.put(Context.FIELD, FIELDS.agency_timezone);
			contxt.put(Context.ERROR, GtfsException.ERROR.MISSING_REQUIRED_VALUES);
			getErrors().add(new GtfsException(contxt));
		}
		try {
			bean.setAgencyTimezone(TIMEZONE_CONVERTER.from(context,FIELDS.agency_timezone, value, true));
		} catch (GtfsException e) {
			// 1-GTFS-Agency-6  warning
			Context contxt = new Context();
			contxt.put(Context.PATH, _path);
			contxt.put(Context.FIELD, FIELDS.agency_url);
			contxt.put(Context.ERROR, GtfsException.ERROR.INVALID_TIMEZONE);
			getErrors().add(new GtfsException(contxt));			
		}
		value = array[i++];
		if (value != null)
			bean.setAgencyPhone(STRING_CONVERTER.from(context, FIELDS.agency_phone, value, false));
		value = array[i++];
		if (value != null)
			if (isUnknownAsIsoLanguage(value)) {
				//1-GTFS-Agency-8   warning
				Context contxt = new Context();
				contxt.put(Context.PATH, _path);
				contxt.put(Context.FIELD, FIELDS.agency_url);
				contxt.put(Context.ERROR, GtfsException.ERROR.INVALID_LANG);
				getErrors().add(new GtfsException(contxt));
			} else {
				bean.setAgencyLang(STRING_CONVERTER.from(context, FIELDS.agency_lang, value, false));
			}
		value = array[i++];
		try {
			bean.setAgencyFareUrl(URL_CONVERTER.from(context, FIELDS.agency_fare_url, value, false));
		} catch (GtfsException e) {
			//1-GTFS-Agency-9   warning
			Context contxt = new Context();
			contxt.put(Context.PATH, _path);
			contxt.put(Context.FIELD, FIELDS.agency_url);
			contxt.put(Context.ERROR, GtfsException.ERROR.INVALID_FARE_URL);
			getErrors().add(new GtfsException(contxt));
		}
		
		return bean;
	}

	@Override
	public boolean validate(GtfsAgency bean, GtfsImporter dao) {
		boolean result = true;

		return result;
	}
	
	private boolean isUnknownAsIsoLanguage(String lang) {
		return lang == null || !Arrays.asList(Locale.getISOLanguages()).contains(lang);
	}

	public static class DefaultImporterFactory extends IndexFactory {
		@SuppressWarnings("rawtypes")
		@Override
		protected Index create(String name) throws IOException {
			return new AgencyById(name);
		}
	}

	static {
		IndexFactory factory = new DefaultImporterFactory();
		IndexFactory.factories.put(AgencyById.class.getName(), factory);
	}

}
