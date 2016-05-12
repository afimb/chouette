package mobi.chouette.exchange.gtfs.model.exporter;

import java.awt.Color;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import mobi.chouette.exchange.gtfs.model.GtfsRoute;
import mobi.chouette.exchange.gtfs.model.importer.Context;
import mobi.chouette.exchange.gtfs.model.importer.GtfsConverter;

public class RouteExporter extends ExporterImpl<GtfsRoute> implements
		GtfsConverter {
	public static enum FIELDS {
		route_id, agency_id, route_short_name, route_long_name, route_desc, route_type, route_url, route_color, route_text_color;
	};

	public static final String FILENAME = "routes.txt";

	public RouteExporter(String name) throws IOException {
		super(name);
	}

	@Override
	public void writeHeader() throws IOException {
		write(FIELDS.values());
	}

	@Override
	public void export(GtfsRoute bean) throws IOException {
		write(CONVERTER.to(_context, bean));
	}

	public static Converter<String, GtfsRoute> CONVERTER = new Converter<String, GtfsRoute>() {

		@Override
		public GtfsRoute from(Context context, String input) {
			GtfsRoute bean = new GtfsRoute();
			List<String> values = Tokenizer.tokenize(input);

			int i = 0;
			bean.setRouteId(STRING_CONVERTER.from(context, FIELDS.route_id,
					values.get(i++), true));
			bean.setAgencyId(STRING_CONVERTER.from(context, FIELDS.agency_id,
					values.get(i++), false));
			bean.setRouteShortName(STRING_CONVERTER.from(context,
					FIELDS.route_short_name, values.get(i++), true));
			bean.setRouteLongName(STRING_CONVERTER.from(context,
					FIELDS.route_long_name, values.get(i++), true));
			bean.setRouteDesc(STRING_CONVERTER.from(context, FIELDS.route_desc,
					values.get(i++), false));
			bean.setRouteType(ROUTETYPE_CONVERTER.from(context,
					FIELDS.route_type, values.get(i++), true));
			bean.setRouteUrl(URL_CONVERTER.from(context, FIELDS.route_url,
					values.get(i++), false));
			bean.setRouteColor(COLOR_CONVERTER.from(context,
					FIELDS.route_color, values.get(i++), Color.WHITE, false));
			bean.setRouteTextColor(COLOR_CONVERTER.from(context,
					FIELDS.route_text_color, values.get(i++), Color.BLACK,
					false));

			return bean;
		}

		@Override
		public String to(Context context, GtfsRoute input) {
			String result = null;
			List<String> values = new ArrayList<String>();
			values.add(STRING_CONVERTER.to(context, FIELDS.route_id,
					input.getRouteId(), true));
			values.add(STRING_CONVERTER.to(context, FIELDS.agency_id,
					input.getAgencyId(), false));
			values.add(STRING_CONVERTER.to(context, FIELDS.route_short_name,
					input.getRouteShortName(), input.getRouteLongName() == null));
			values.add(STRING_CONVERTER.to(context, FIELDS.route_long_name,
					input.getRouteLongName(), input.getRouteShortName() == null));
			values.add(STRING_CONVERTER.to(context, FIELDS.route_desc,
					input.getRouteDesc(), false));
			values.add(ROUTETYPE_CONVERTER.to(context, FIELDS.route_type,
					input.getRouteType(), true));
			values.add(URL_CONVERTER.to(context, FIELDS.route_url,
					input.getRouteUrl(), false));
			values.add(COLOR_CONVERTER.to(context, FIELDS.route_color,
					input.getRouteColor(), false));
			values.add(COLOR_CONVERTER.to(context, FIELDS.route_text_color,
					input.getRouteTextColor(), false));

			result = Tokenizer.untokenize(values);
			return result;
		}

	};

	public static class DefaultExporterFactory extends ExporterFactory {

		@SuppressWarnings({ "unchecked", "rawtypes" })
		@Override
		protected Exporter create(String path) throws IOException {
			return new RouteExporter(path);
		}
	}

	static {
		ExporterFactory factory = new DefaultExporterFactory();
		ExporterFactory.factories.put(RouteExporter.class.getName(), factory);
	}
}
