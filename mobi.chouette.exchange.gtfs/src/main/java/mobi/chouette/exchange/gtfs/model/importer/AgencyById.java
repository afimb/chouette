package mobi.chouette.exchange.gtfs.model.importer;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import mobi.chouette.common.HTMLTagValidator;
import mobi.chouette.exchange.gtfs.model.GtfsAgency;

public class AgencyById extends IndexImpl<GtfsAgency> implements GtfsConverter {

	public static enum FIELDS {
		agency_id, agency_name, agency_url, agency_timezone, agency_phone, agency_lang, agency_fare_url;
	};

	public static final String FILENAME = "agency.txt";
	public static final String KEY = FIELDS.agency_id.name();

	private GtfsAgency bean = new GtfsAgency();
	private String[] array = new String[FIELDS.values().length];
	private Set<String> agencyIds = new HashSet<String>(); // Used to check relation with routes

	public AgencyById(String name) throws IOException {
		super(name, KEY, GtfsAgency.DEFAULT_ID, true);
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
					// extra fields are tolerated : 1-GTFS-Agency-10 warning
					getErrors().add(new GtfsException(_path, 1, fieldName, GtfsException.ERROR.EXTRA_HEADER_FIELD, null, null));
				}
			}
		}

		// checks for ubiquitous header fields : 1-GTFS-Agency-2, 1-GTFS-Agency-4 error
		if ( fields.get(FIELDS.agency_id.name()) == null ||
				fields.get(FIELDS.agency_name.name()) == null ||
				fields.get(FIELDS.agency_url.name()) == null ||
				fields.get(FIELDS.agency_timezone.name()) == null) {
			String name = "";
			if (fields.get(FIELDS.agency_id.name()) == null)
				name = FIELDS.agency_id.name();
			else if (fields.get(FIELDS.agency_name.name()) == null)
				name = FIELDS.agency_name.name();
			else if (fields.get(FIELDS.agency_url.name()) == null)
				name = FIELDS.agency_url.name();
			else if (fields.get(FIELDS.agency_timezone.name()) == null)
				name = FIELDS.agency_timezone.name();
			getErrors().add(new GtfsException(_path, 1, name, GtfsException.ERROR.MISSING_REQUIRED_FIELDS, null, null));
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
		//bean.getErrors().clear();
		bean.setId(id);
		
		value = array[i++]; testExtraSpace(FIELDS.agency_id.name(), value, bean);
		bean.getOkTests().add(GtfsException.ERROR.EXTRA_SPACE_IN_FIELD.name());
		if (value != null && !value.trim().isEmpty()) {
			agencyIds.add(value.trim());
			bean.setAgencyId(STRING_CONVERTER.from(context, FIELDS.agency_id, value, GtfsAgency.DEFAULT_ID, false));
		}
		
		if (GtfsAgency.DEFAULT_ID.equals(bean.getAgencyId())) {
			bean.getErrors().add(new GtfsException(_path, id, FIELDS.agency_name.name(), GtfsException.ERROR.DEFAULT_VALUE, null, null));
		} else {
			bean.getOkTests().add(GtfsException.ERROR.DEFAULT_VALUE.name());
		}
		
		// check the existance of agency_name, agency_url and agency_timezone values for this bean : 1-GTFS-Agency-5
		// 1-GTFS-Agency-5
		value = array[i++]; testExtraSpace(FIELDS.agency_name.name(), value, bean);
		if (value == null || value.trim().isEmpty()) {
			bean.getErrors().add(new GtfsException(_path, id, FIELDS.agency_name.name(), GtfsException.ERROR.MISSING_REQUIRED_VALUES, null, null));
		} else {
			bean.getOkTests().add(GtfsException.ERROR.MISSING_REQUIRED_VALUES.name());
			bean.setAgencyName(STRING_CONVERTER.from(context, FIELDS.agency_name, value, true));
		}
		
		// 1-GTFS-Agency-5
		value = array[i++]; testExtraSpace(FIELDS.agency_url.name(), value, bean);
		if (value == null || value.trim().isEmpty()) {
			bean.getErrors().add(new GtfsException(_path, id, FIELDS.agency_url.name(), GtfsException.ERROR.MISSING_REQUIRED_VALUES, null, null));
		} else {
			try {
				bean.setAgencyUrl(URL_CONVERTER.from(context, FIELDS.agency_url, value, true));
			} catch (GtfsException e) {
				// 1-GTFS-Agency-7  warning
				bean.getErrors().add(new GtfsException(_path, id, FIELDS.agency_url.name(), GtfsException.ERROR.INVALID_URL, null, null));			
			} finally {
				bean.getOkTests().add(GtfsException.ERROR.INVALID_URL.name());
			}
		}
		
		// 1-GTFS-Agency-5
		value = array[i++]; testExtraSpace(FIELDS.agency_timezone.name(), value, bean);
		if (value == null || value.trim().isEmpty()) {
			bean.getErrors().add(new GtfsException(_path, id, FIELDS.agency_timezone.name(), GtfsException.ERROR.MISSING_REQUIRED_VALUES, null, null));
		} else {
			try {
				bean.setAgencyTimezone(TIMEZONE_CONVERTER.from(context,FIELDS.agency_timezone, value, true));
			} catch (GtfsException e) {
				// 1-GTFS-Agency-6  warning
				bean.getErrors().add(new GtfsException(_path, id, FIELDS.agency_timezone.name(), GtfsException.ERROR.INVALID_TIMEZONE, null, null));			
			} finally {
				bean.getOkTests().add(GtfsException.ERROR.INVALID_TIMEZONE.name());
			}
		}
		
		value = array[i++]; testExtraSpace(FIELDS.agency_phone.name(), value, bean);
		if (value != null && !value.trim().isEmpty())
			bean.setAgencyPhone(STRING_CONVERTER.from(context, FIELDS.agency_phone, value, false));
		
		value = array[i++]; testExtraSpace(FIELDS.agency_lang.name(), value, bean);
		if (value != null && !value.trim().isEmpty())
			if (isUnknownAsIsoLanguage(value)) {
				//1-GTFS-Agency-8   warning
				bean.getErrors().add(new GtfsException(_path, id, FIELDS.agency_lang.name(), GtfsException.ERROR.INVALID_LANG, null, null));
			} else {
				bean.getOkTests().add(GtfsException.ERROR.INVALID_LANG.name());
				bean.setAgencyLang(STRING_CONVERTER.from(context, FIELDS.agency_lang, value, false));
			}
		
		value = array[i++]; testExtraSpace(FIELDS.agency_fare_url.name(), value, bean);
		if (value != null && !value.trim().isEmpty()) {
			try {
				bean.setAgencyFareUrl(URL_CONVERTER.from(context, FIELDS.agency_fare_url, value, false));
			} catch (GtfsException e) {
				//1-GTFS-Agency-9   warning
				bean.getErrors().add(new GtfsException(_path, id, FIELDS.agency_fare_url.name(), GtfsException.ERROR.INVALID_FARE_URL, null, null));
			} finally {
				bean.getOkTests().add(GtfsException.ERROR.INVALID_FARE_URL.name());
			}
		}
		
		return bean;
	}

	@Override
	public boolean validate(GtfsAgency bean, GtfsImporter dao) {
		boolean result = true;
		
//		String agencyId = bean.getAgencyId();
//		if (GtfsAgency.DEFAULT_ID.equals(agencyId))
//			return result;
//		Iterator<GtfsRoute> iti = dao.getRouteById().iterator();
//		while ( iti.hasNext() ) {
//			GtfsRoute nextRoute = iti.next();
//			if (nextRoute.getAgencyId() == null)
//				continue;
//			if (agencyId.equals(nextRoute.getAgencyId())) {
//				//bean.getErrors().add(new GtfsException(_path, nextStopTime.getId(), FIELDS.stop_sequence.name(), GtfsException.ERROR.DUPLICATE_STOP_SEQUENCE, null, null));
//				return true;
//			}
//		}

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
