package mobi.chouette.exchange.gtfs.model.importer;

import java.awt.Color;
import java.io.IOException;
import java.util.Map;

import mobi.chouette.exchange.gtfs.model.GtfsRoute;

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
		// extra fields are tolerated : 1-GTFS-Route-10 warning
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
		
		// checks for ubiquitous header fields : 1-GTFS-Stop-2 error
		if ( fields.get(FIELDS.route_id.name()) == null ||
				fields.get(FIELDS.route_type.name()) == null) {
			Context context = new Context();
			context.put(Context.PATH, _path);
			context.put(Context.ERROR, GtfsException.ERROR.MISSING_REQUIRED_FIELDS);
			getErrors().add(new GtfsException(context));
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
		value = array[i++];
		bean.setRouteId(STRING_CONVERTER.from(context, FIELDS.route_id, value, true));
		value = array[i++];
		bean.setAgencyId(STRING_CONVERTER.from(context, FIELDS.agency_id, value, false));
		value = array[i++];
		bean.setRouteShortName(STRING_CONVERTER.from(context, FIELDS.route_short_name, value, false));
		value = array[i++];
		bean.setRouteLongName(STRING_CONVERTER.from(context, FIELDS.route_long_name, value, bean.getRouteShortName() != null));
		value = array[i++];
		bean.setRouteDesc(STRING_CONVERTER.from(context, FIELDS.route_desc, value, false));
		value = array[i++];
		bean.setRouteType(ROUTETYPE_CONVERTER.from(context, FIELDS.route_type, value, true));
		value = array[i++];
		bean.setRouteUrl(URL_CONVERTER.from(context, FIELDS.route_url, value, false));
		value = array[i++];
		bean.setRouteColor(COLOR_CONVERTER.from(context, FIELDS.route_color, value, Color.WHITE, false));
		value = array[i++];
		bean.setRouteTextColor(COLOR_CONVERTER.from(context, FIELDS.route_text_color, value, Color.BLACK, false));

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
