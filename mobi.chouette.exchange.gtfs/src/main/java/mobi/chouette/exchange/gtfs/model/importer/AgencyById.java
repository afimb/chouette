package mobi.chouette.exchange.gtfs.model.importer;

import java.io.IOException;
import java.util.Arrays;
import java.util.Locale;
import java.util.Map;

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

	public AgencyById(String name) throws IOException {
		super(name, KEY, GtfsAgency.DEFAULT_ID, true);
	}

	@Override
	protected void checkRequiredFields(Map<String, Integer> fields) {
		for (String fieldName : fields.keySet()) {
			if (fieldName != null) {
				if (!fieldName.equals(fieldName.trim())) {
					// extra spaces in end fields are tolerated : 1-GTFS-CSV-7
					// warning
					getErrors().add(
							new GtfsException(_path, 1, getIndex(fieldName), fieldName.trim(),
									GtfsException.ERROR.EXTRA_SPACE_IN_HEADER_FIELD, null, fieldName));
				}

				if (HTMLTagValidator.validate(fieldName.trim())) {
					getErrors().add(
							new GtfsException(_path, 1, getIndex(fieldName), fieldName.trim(),
									GtfsException.ERROR.HTML_TAG_IN_HEADER_FIELD, null, null));
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
					getErrors().add(
							new GtfsException(_path, 1, getIndex(fieldName), fieldName,
									GtfsException.ERROR.EXTRA_HEADER_FIELD, null, null));
				}
			}
		}

		// checks for ubiquitous header fields : 1-GTFS-Agency-2,
		// 1-GTFS-Agency-4 error
		if (fields.get(FIELDS.agency_id.name()) == null) {
			getErrors().add(
					new GtfsException(_path, 1, FIELDS.agency_id.name(), GtfsException.ERROR.MISSING_OPTIONAL_FIELD,
							null, null));
		} else {
			getOkTests().add(GtfsException.ERROR.MISSING_OPTIONAL_FIELD);
		}

		if (fields.get(FIELDS.agency_name.name()) == null || fields.get(FIELDS.agency_url.name()) == null
				|| fields.get(FIELDS.agency_timezone.name()) == null) {
			String name = "";
			if (fields.get(FIELDS.agency_name.name()) == null)
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
		clearBean();
		bean.setId(id);

		value = array[i++];
		if (withValidation) {
			testExtraSpace(FIELDS.agency_id.name(), value, bean);
			bean.getOkTests().add(GtfsException.ERROR.EXTRA_SPACE_IN_FIELD);
		}
		bean.setAgencyId(STRING_CONVERTER.from(context, FIELDS.agency_id, value, GtfsAgency.DEFAULT_ID, false));

		if (withValidation) {
			if (GtfsAgency.DEFAULT_ID.equals(bean.getAgencyId())) {
				if (getIndex(FIELDS.agency_id.name()) == null) {
					bean.getErrors().add(
							new GtfsException(_path, id, FIELDS.agency_id.name(), GtfsException.ERROR.DEFAULT_VALUE, null,
									null));
				} else {
					bean.getErrors().add(
							new GtfsException(_path, id, getIndex(FIELDS.agency_id.name()), FIELDS.agency_id.name(),
									GtfsException.ERROR.DEFAULT_VALUE, null, null));
				}
			} else {
				bean.getOkTests().add(GtfsException.ERROR.DEFAULT_VALUE);
			}
		}

		// check the existance of agency_name, agency_url and agency_timezone
		// values for this bean : 1-GTFS-Agency-5
		// 1-GTFS-Agency-5
		value = array[i++];
		if (withValidation)
			testExtraSpace(FIELDS.agency_name.name(), value, bean);
		if (value == null || value.trim().isEmpty()) {
			if (withValidation)
				bean.getErrors().add(
						new GtfsException(_path, id, getIndex(FIELDS.agency_name.name()), FIELDS.agency_name.name(),
								GtfsException.ERROR.MISSING_REQUIRED_VALUES, null, null));
		} else {
			if (withValidation)
				bean.getOkTests().add(GtfsException.ERROR.MISSING_REQUIRED_VALUES);
			bean.setAgencyName(STRING_CONVERTER.from(context, FIELDS.agency_name, value, true));
		}

		// 1-GTFS-Agency-5
		value = array[i++];
		if (withValidation)
			testExtraSpace(FIELDS.agency_url.name(), value, bean);
		if (value == null || value.trim().isEmpty()) {
			if (withValidation)
				bean.getErrors().add(
						new GtfsException(_path, id, getIndex(FIELDS.agency_url.name()), FIELDS.agency_url.name(),
								GtfsException.ERROR.MISSING_REQUIRED_VALUES, null, null));
		} else {
			try {
				bean.setAgencyUrl(URL_CONVERTER.from(context, FIELDS.agency_url, value, true));
			} catch (GtfsException e) {
				// 1-GTFS-Agency-7 warning
				if (withValidation)
					bean.getErrors().add(
							new GtfsException(_path, id, getIndex(FIELDS.agency_url.name()), FIELDS.agency_url.name(),
									GtfsException.ERROR.INVALID_FORMAT, null, value));
			} finally {
				if (withValidation)
					bean.getOkTests().add(GtfsException.ERROR.INVALID_FORMAT);
			}
		}

		// 1-GTFS-Agency-5
		value = array[i++];
		if (withValidation)
			testExtraSpace(FIELDS.agency_timezone.name(), value, bean);
		if (value == null || value.trim().isEmpty()) {
			if (withValidation)
				bean.getErrors().add(
						new GtfsException(_path, id, FIELDS.agency_timezone.name(),
								GtfsException.ERROR.MISSING_REQUIRED_VALUES, null, null));
		} else {
			try {
				bean.setAgencyTimezone(TIMEZONE_CONVERTER.from(context, FIELDS.agency_timezone, value, true));
			} catch (GtfsException e) {
				// 1-GTFS-Agency-6 warning
				if (withValidation)
					bean.getErrors().add(
							new GtfsException(_path, id, getIndex(FIELDS.agency_timezone.name()), FIELDS.agency_timezone
									.name(), GtfsException.ERROR.INVALID_FORMAT, null, value));
			} finally {
				if (withValidation)
					bean.getOkTests().add(GtfsException.ERROR.INVALID_FORMAT);
			}
		}

		value = array[i++];
		if (withValidation)
			testExtraSpace(FIELDS.agency_phone.name(), value, bean);
		if (value != null && !value.trim().isEmpty())
			bean.setAgencyPhone(STRING_CONVERTER.from(context, FIELDS.agency_phone, value, false));

		value = array[i++];
		if (withValidation)
			testExtraSpace(FIELDS.agency_lang.name(), value, bean);
		if (value != null && !value.trim().isEmpty())
			if (isUnknownAsIsoLanguage(value)) {
				// 1-GTFS-Agency-8 warning
				if (withValidation)
					bean.getErrors().add(
							new GtfsException(_path, id, getIndex(FIELDS.agency_lang.name()), FIELDS.agency_lang.name(),
									GtfsException.ERROR.INVALID_FORMAT, null, value));
			} else {
				if (withValidation)
					bean.getOkTests().add(GtfsException.ERROR.INVALID_FORMAT);
				bean.setAgencyLang(STRING_CONVERTER.from(context, FIELDS.agency_lang, value, false));
			}

		value = array[i++];
		if (withValidation)
			testExtraSpace(FIELDS.agency_fare_url.name(), value, bean);
		if (value != null && !value.trim().isEmpty()) {
			try {
				bean.setAgencyFareUrl(URL_CONVERTER.from(context, FIELDS.agency_fare_url, value, false));
			} catch (GtfsException e) {
				// 1-GTFS-Agency-9 warning
				if (withValidation)
					bean.getErrors().add(
						new GtfsException(_path, id, getIndex(FIELDS.agency_fare_url.name()), FIELDS.agency_fare_url
								.name(), GtfsException.ERROR.INVALID_FORMAT, null, value));
			} finally {
				if (withValidation)
					bean.getOkTests().add(GtfsException.ERROR.INVALID_FORMAT);
			}
		}

		return bean;
	}

	@Override
	public boolean validate(GtfsAgency bean, GtfsImporter dao) {
		boolean result = true;

		return result;
	}

	private void clearBean() {
		// bean.getErrors().clear();
		bean.setId(null);
		bean.setAgencyFareUrl(null);
		bean.setAgencyId(null);
		bean.setAgencyLang(null);
		bean.setAgencyName(null);
		bean.setAgencyPhone(null);
		bean.setAgencyTimezone(null);
		bean.setAgencyUrl(null);
	}

	private boolean isUnknownAsIsoLanguage(String lang) {
		if (lang == null)
			return true;
		if (!lang.toUpperCase().equals(lang) && !lang.toLowerCase().equals(lang))
			return true;
		return !Arrays.asList(Locale.getISOLanguages()).contains(lang.toLowerCase());
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
