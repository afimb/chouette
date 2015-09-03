package mobi.chouette.exchange.gtfs.model.importer;

import java.awt.Color;
import java.io.IOException;
import java.util.Map;

import mobi.chouette.common.HTMLTagValidator;
import mobi.chouette.exchange.gtfs.model.GtfsAgency;
import mobi.chouette.exchange.gtfs.model.GtfsRoute;
import mobi.chouette.exchange.gtfs.model.importer.StopById.FIELDS;

public class RouteById extends IndexImpl<GtfsRoute> implements GtfsConverter {

	public static enum FIELDS {
		route_id, agency_id, route_short_name, route_long_name, route_desc, route_type, route_url, route_color, route_text_color;
	};

	public static final String FILENAME = "routes.txt";
	public static final String KEY = FIELDS.route_id.name();

	private GtfsRoute bean = new GtfsRoute();
	private String[] array = new String[FIELDS.values().length];

	public RouteById(String name) throws IOException {
		super(name, KEY);
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
					// extra fields are tolerated : 1-GTFS-Route-11 warning
					getErrors().add(new GtfsException(_path, 1, fieldName, GtfsException.ERROR.EXTRA_HEADER_FIELD, null, null));
				}
			}
		}

		// checks for ubiquitous header fields : 1-GTFS-Stop-2 error
		if ( fields.get(FIELDS.route_id.name()) == null ||
				(fields.get(FIELDS.route_long_name.name()) == null && fields.get(FIELDS.route_short_name.name()) == null) ||
				fields.get(FIELDS.route_type.name()) == null) {
			
			if (fields.get(FIELDS.route_id.name()) == null)
				throw new GtfsException(_path, 1, FIELDS.route_id.name(), GtfsException.ERROR.MISSING_REQUIRED_FIELDS, null, null);
			
			if (fields.get(FIELDS.route_long_name.name()) == null && fields.get(FIELDS.route_short_name.name()) == null)
				getErrors().add(new GtfsException(_path, 1, FIELDS.route_long_name.name(), GtfsException.ERROR.MISSING_REQUIRED_FIELDS2, null, null));
			
			if (fields.get(FIELDS.route_type.name()) == null)
				getErrors().add(new GtfsException(_path, 1, FIELDS.route_type.name(), GtfsException.ERROR.MISSING_REQUIRED_FIELDS, null, null));
		}
	}

	@Override
	protected GtfsRoute build(GtfsIterator reader, Context context) {
		int i = 0;
		for (FIELDS field : FIELDS.values()) {
			array[i++] = getField(reader, field.name());
		}

		i = 0;
		String value = null;
		int id = (int) context.get(Context.ID);
		bean.getErrors().clear();
		bean.setId(id);
		
		value = array[i++]; testExtraSpace(FIELDS.route_id.name(), value, bean);
		if (value != null && !value.trim().isEmpty()) {
			bean.setRouteId(STRING_CONVERTER.from(context, FIELDS.route_id, value, GtfsAgency.DEFAULT_ID, true));
		}
		
		value = array[i++]; testExtraSpace(FIELDS.agency_id.name(), value, bean);
		bean.setAgencyId(STRING_CONVERTER.from(context, FIELDS.agency_id, value, false));
		
		boolean noShotName = false;
		value = array[i++]; testExtraSpace(FIELDS.route_short_name.name(), value, bean);
		if (value == null || value.trim().isEmpty()) {
			noShotName = true;
		} else {
			bean.setRouteShortName(STRING_CONVERTER.from(context, FIELDS.route_short_name, value, false));
		}
		
		value = array[i++]; testExtraSpace(FIELDS.route_long_name.name(), value, bean);
		if (value == null || value.trim().isEmpty()) {
			if (noShotName)
				bean.getErrors().add(new GtfsException(_path, id, FIELDS.route_long_name.name(), GtfsException.ERROR.MISSING_REQUIRED_VALUES2, null, null));
		} else {
			bean.setRouteLongName(STRING_CONVERTER.from(context, FIELDS.route_long_name, value, bean.getRouteShortName() != null));
		}
		
		value = array[i++]; testExtraSpace(FIELDS.route_desc.name(), value, bean);
		bean.setRouteDesc(STRING_CONVERTER.from(context, FIELDS.route_desc, value, false));
		
		value = array[i++]; testExtraSpace(FIELDS.route_type.name(), value, bean);
		if (value == null || value.trim().isEmpty()) {
			bean.getErrors().add(new GtfsException(_path, id, FIELDS.route_type.name(), GtfsException.ERROR.MISSING_REQUIRED_VALUES, null, null));
		} else {
			try {
				bean.setRouteType(ROUTETYPE_CONVERTER.from(context, FIELDS.route_type, value, true));
			} catch(GtfsException e) {
				bean.getErrors().add(new GtfsException(_path, id, FIELDS.route_type.name(), GtfsException.ERROR.INVALID_ROUTE_TYPE, null, null));
			}
		}
		
		value = array[i++]; testExtraSpace(FIELDS.route_url.name(), value, bean);
		if (value != null && !value.trim().isEmpty()) {
			try {
				bean.setRouteUrl(URL_CONVERTER.from(context, FIELDS.route_url, value, false));
			} catch (GtfsException e) {
				// 1-GTFS-Route-7 warning
				bean.getErrors().add(new GtfsException(_path, id, FIELDS.route_url.name(), GtfsException.ERROR.INVALID_URL, null, null));			
			}
		}
		
		value = array[i++]; testExtraSpace(FIELDS.route_color.name(), value, bean);
		if (value != null && !value.trim().isEmpty()) {
			try {
				bean.setRouteColor(COLOR_CONVERTER.from(context, FIELDS.route_color, value, Color.WHITE, false));
			} catch (GtfsException e) {
				// 1-GTFS-Route-7 warning
				bean.getErrors().add(new GtfsException(_path, id, FIELDS.route_color.name(), GtfsException.ERROR.INVALID_COLOR, null, null));			
			}
		}
		
		value = array[i++]; testExtraSpace(FIELDS.route_text_color.name(), value, bean);
		if (value != null && !value.trim().isEmpty()) {
			try {
				bean.setRouteTextColor(COLOR_CONVERTER.from(context, FIELDS.route_text_color, value, Color.BLACK, false));
			} catch (GtfsException e) {
				// 1-GTFS-Route-7 warning
				bean.getErrors().add(new GtfsException(_path, id, FIELDS.route_color.name(), GtfsException.ERROR.INVALID_COLOR_TEXT, null, null));			
			}
		}

		return bean;
	}

	@Override
	public boolean validate(GtfsRoute bean, GtfsImporter dao) {
		return true;
	}

	public static class DefaultImporterFactory extends IndexFactory {
		@SuppressWarnings("rawtypes")
		@Override
		protected Index create(String name) throws IOException {
			return new RouteById(name);
		}
	}

	static {
		IndexFactory factory = new DefaultImporterFactory();
		IndexFactory.factories.put(RouteById.class.getName(), factory);
	}

}
