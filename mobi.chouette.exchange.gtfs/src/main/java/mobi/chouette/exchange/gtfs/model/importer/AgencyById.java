package mobi.chouette.exchange.gtfs.model.importer;

import java.io.IOException;
//import java.util.ArrayList;
import java.util.Arrays;
//import java.util.List;
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
		
//		// extra fields are tolerated : 1-GTFS-Agency-10 warning
//		List<String> extraFields = new ArrayList<String>();
//		for (String fieldName : fields.keySet()) {
//			if (fieldName != null) {
//				boolean fieldNameIsExtra = true;
//				for (FIELDS field : FIELDS.values()) {
//					if (fieldName.trim().equals(field.name())) {
//						fieldNameIsExtra = false;
//						break;
//					}
//				}
//				if (fieldNameIsExtra) {
//					extraFields.add(fieldName);
//				}
//			}
//		}
//		
//		if ( extraFields.size() > 0 ) {
//			Context context = new Context();
//			context.put(Context.PATH, _path);
//			context.put(Context.ERROR, GtfsException.ERROR.MISSING_REQUIRED_FIELDS);
//			throw new GtfsException(context);	
//		}
		
		// checks for ubiquitous header fields : 1-GTFS-Agency-4 error
		if ( fields.get(FIELDS.agency_name.name()) == null ||
				fields.get(FIELDS.agency_url.name()) == null ||
				fields.get(FIELDS.agency_timezone.name()) == null) {
			Context context = new Context();
			context.put(Context.PATH, _path);
			context.put(Context.ERROR, GtfsException.ERROR.MISSING_REQUIRED_FIELDS);
			throw new GtfsException(context);
		}
	}

	@Override
	protected GtfsAgency build(GtfsIterator reader, Context context) {
		int i = 0;
		for (FIELDS field : FIELDS.values()) {
			// array[i++] may be ""
			array[i++] = getField(reader, field.name());
		}

		i = 0;
		int id = (int) context.get(Context.ID);
		bean.getWarnings().clear();
		bean.setId(id);
		bean.setAgencyId(STRING_CONVERTER.from(context, FIELDS.agency_id, array[i++], GtfsAgency.DEFAULT_ID, false));
		///////// check the existance of agency_name, agency_url and agency_timezone values for this bean : 1-GTFS-Agency-5
		
		bean.setAgencyName(STRING_CONVERTER.from(context, FIELDS.agency_name, array[i++], true));
		try {
			bean.setAgencyUrl(URL_CONVERTER.from(context, FIELDS.agency_url, array[i++], true));
		} catch (GtfsException e) {
			// 1-GTFS-Agency-7  warning
		}
		try {
			bean.setAgencyTimezone(TIMEZONE_CONVERTER.from(context,FIELDS.agency_timezone, array[i++], true));
		} catch (GtfsException e) {
			// 1-GTFS-Agency-6  warning
		}
		bean.setAgencyPhone(STRING_CONVERTER.from(context, FIELDS.agency_phone, array[i++], false));
		bean.setAgencyLang(STRING_CONVERTER.from(context, FIELDS.agency_lang, array[i++], false));
		try {
			bean.setAgencyFareUrl(URL_CONVERTER.from(context, FIELDS.agency_fare_url, array[i++], false));
		} catch (GtfsException e) {
			//1-GTFS-Agency-9   warning
		}
		
		return bean;
	}

	@Override
	public boolean validate(GtfsAgency bean, GtfsImporter dao) {
		boolean result = true;
		
		String lang = bean.getAgencyLang();
		if (isUnknownAsIsoLanguage(lang)) {
			// 1-GTFS-Agency-8  warning
			result = false;
		}
		
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
