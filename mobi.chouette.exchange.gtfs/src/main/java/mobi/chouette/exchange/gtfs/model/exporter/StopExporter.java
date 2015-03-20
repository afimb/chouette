package mobi.chouette.exchange.gtfs.model.exporter;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import mobi.chouette.exchange.gtfs.model.GtfsStop;
import mobi.chouette.exchange.gtfs.model.GtfsStop.LocationType;
import mobi.chouette.exchange.gtfs.model.GtfsStop.WheelchairBoardingType;
import mobi.chouette.exchange.gtfs.model.importer.Context;
import mobi.chouette.exchange.gtfs.model.importer.GtfsConverter;

public class StopExporter extends ExporterImpl<GtfsStop> implements
		GtfsConverter {
	public static enum FIELDS {
		stop_id, stop_code, stop_name, stop_desc, stop_lat, stop_lon, zone_id, stop_url, location_type, parent_station, wheelchair_boarding, stop_timezone;
	};

	public static final String FILENAME = "stops.txt";

	public StopExporter(String name) throws IOException {
		super(name);
	}

	@Override
	public void writeHeader() throws IOException {
		write(FIELDS.values());
	}

	@Override
	public void export(GtfsStop bean) throws IOException {
		write(CONVERTER.to(_context, bean));
	}

	public static Converter<String, GtfsStop> CONVERTER = new Converter<String, GtfsStop>() {

		@Override
		public GtfsStop from(Context context, String input) {
			GtfsStop bean = new GtfsStop();
			List<String> values = Tokenizer.tokenize(input);

			int i = 0;
			bean.setStopId(STRING_CONVERTER.from(context, FIELDS.stop_id,
					values.get(i++), true));
			bean.setStopCode(STRING_CONVERTER.from(context, FIELDS.stop_code,
					values.get(i++), false));
			bean.setStopName(STRING_CONVERTER.from(context, FIELDS.stop_name,
					values.get(i++), true));
			bean.setStopDesc(STRING_CONVERTER.from(context, FIELDS.stop_desc,
					values.get(i++), false));
			bean.setStopLat(BigDecimal.valueOf(FLOAT_CONVERTER.from(context,
					FIELDS.stop_lat, values.get(i++), true)));
			bean.setStopLon(BigDecimal.valueOf(FLOAT_CONVERTER.from(context,
					FIELDS.stop_lon, values.get(i++), true)));
			bean.setZoneId(STRING_CONVERTER.from(context, FIELDS.zone_id,
					values.get(i++), false));
			bean.setStopUrl(URL_CONVERTER.from(context, FIELDS.stop_url,
					values.get(i++), false));
			bean.setLocationType(LOCATIONTYPE_CONVERTER.from(context,
					FIELDS.location_type, values.get(i++), LocationType.Stop,
					false));
			bean.setParentStation(STRING_CONVERTER.from(context,
					FIELDS.parent_station, values.get(i++), false));
			bean.setWheelchairBoarding(WHEELCHAIRBOARDINGTYPE_CONVERTER.from(
					context, FIELDS.wheelchair_boarding, values.get(i++),
					WheelchairBoardingType.NoInformation, false));
			// bean.setStopTimezone(TIMEZONE_CONVERTER.from(context,
			// FIELDS.stop_timezone, values.get(i++), false));

			return bean;
		}

		@Override
		public String to(Context context, GtfsStop input) {
			String result = null;
			List<String> values = new ArrayList<String>();
			values.add(STRING_CONVERTER.to(context, FIELDS.stop_id,
					input.getStopId(), true));
			values.add(STRING_CONVERTER.to(context, FIELDS.stop_code,
					input.getStopCode(), false));
			values.add(STRING_CONVERTER.to(context, FIELDS.stop_name,
					input.getStopName(), true));
			values.add(STRING_CONVERTER.to(context, FIELDS.stop_desc,
					input.getStopDesc(), false));
			values.add(FLOAT_CONVERTER.to(context, FIELDS.stop_lat, input
					.getStopLat().floatValue(), true));
			values.add(FLOAT_CONVERTER.to(context, FIELDS.stop_lon, input
					.getStopLon().floatValue(), true));
			values.add(STRING_CONVERTER.to(context, FIELDS.zone_id,
					input.getZoneId(), false));
			values.add(URL_CONVERTER.to(context, FIELDS.stop_url,
					input.getStopUrl(), false));
			values.add(LOCATIONTYPE_CONVERTER.to(context, FIELDS.location_type,
					input.getLocationType(), false));
			values.add(STRING_CONVERTER.to(context, FIELDS.parent_station,
					input.getParentStation(), false));
			values.add(WHEELCHAIRBOARDINGTYPE_CONVERTER.to(context,
					FIELDS.wheelchair_boarding, input.getWheelchairBoarding(),
					false));
			values.add(TIMEZONE_CONVERTER.to(context, FIELDS.stop_timezone,
					input.getStopTimezone(), false));

			result = Tokenizer.untokenize(values);
			return result;
		}

	};

	public static class DefaultExporterFactory extends ExporterFactory {

		@SuppressWarnings({ "unchecked", "rawtypes" })
		@Override
		protected Exporter create(String path) throws IOException {
			return new StopExporter(path);
		}
	}

	static {
		ExporterFactory factory = new DefaultExporterFactory();
		ExporterFactory.factories.put(StopExporter.class.getName(), factory);
	}
}
