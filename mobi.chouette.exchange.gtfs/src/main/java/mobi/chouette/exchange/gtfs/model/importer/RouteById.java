package mobi.chouette.exchange.gtfs.model.importer;

import java.awt.Color;
import java.io.IOException;

import mobi.chouette.exchange.gtfs.model.GtfsAgency;
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
	protected GtfsRoute build(GtfsIterator reader, Context context) {
		int i = 0;
		for (FIELDS field : FIELDS.values()) {
			array[i++] = getField(reader, field.name());
		}

		i = 0;
		int id = (int) context.get(Context.ID);
		bean.setId(id);
		bean.setRouteId(STRING_CONVERTER.from(context, FIELDS.route_id,
				array[i++], true));
		bean.setAgencyId(STRING_CONVERTER.from(context, FIELDS.agency_id,
				array[i++], false));
		bean.setRouteShortName(STRING_CONVERTER.from(context,
				FIELDS.route_short_name, array[i++], true));
		bean.setRouteLongName(STRING_CONVERTER.from(context,
				FIELDS.route_long_name, array[i++], bean.getRouteShortName() != null));
		bean.setRouteDesc(STRING_CONVERTER.from(context, FIELDS.route_desc,
				array[i++], false));
		bean.setRouteType(ROUTETYPE_CONVERTER.from(context, FIELDS.route_type,
				array[i++], true));
		bean.setRouteUrl(URL_CONVERTER.from(context, FIELDS.route_url,
				array[i++], false));
		bean.setRouteColor(COLOR_CONVERTER.from(context, FIELDS.route_color,
				array[i++], Color.WHITE, false));
		bean.setRouteTextColor(COLOR_CONVERTER.from(context,
				FIELDS.route_text_color, array[i++], Color.BLACK, false));

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
