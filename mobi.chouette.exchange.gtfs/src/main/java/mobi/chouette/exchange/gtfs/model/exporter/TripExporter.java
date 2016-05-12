package mobi.chouette.exchange.gtfs.model.exporter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import mobi.chouette.exchange.gtfs.model.GtfsTrip;
import mobi.chouette.exchange.gtfs.model.importer.Context;
import mobi.chouette.exchange.gtfs.model.importer.GtfsConverter;

public class TripExporter extends ExporterImpl<GtfsTrip> implements
		GtfsConverter {
	public static enum FIELDS {
		route_id, service_id, trip_id, trip_headsign, trip_short_name, direction_id, wheelchair_accessible, shape_id; // ,
																											// block_id,
																											// bikes_allowed;
	};

	public static final String FILENAME = "trips.txt";

	public TripExporter(String name) throws IOException {
		super(name);
	}

	@Override
	public void writeHeader() throws IOException {
		write(FIELDS.values());
	}

	@Override
	public void export(GtfsTrip bean) throws IOException {
		write(CONVERTER.to(_context, bean));
	}

	public static Converter<String, GtfsTrip> CONVERTER = new Converter<String, GtfsTrip>() {

		@Override
		public GtfsTrip from(Context context, String input) {
			GtfsTrip bean = new GtfsTrip();
			List<String> values = Tokenizer.tokenize(input);

			int i = 0;
			bean.setRouteId(STRING_CONVERTER.from(context, FIELDS.route_id,
					values.get(i++), true));
			bean.setServiceId(STRING_CONVERTER.from(context, FIELDS.service_id,
					values.get(i++), true));
			bean.setTripId(STRING_CONVERTER.from(context, FIELDS.trip_id,
					values.get(i++), true));
			bean.setTripHeadSign(STRING_CONVERTER.from(context,
					FIELDS.trip_headsign, values.get(i++), false));
			bean.setTripShortName(STRING_CONVERTER.from(context,
					FIELDS.trip_short_name, values.get(i++), false));
			bean.setDirectionId(DIRECTIONTYPE_CONVERTER.from(context,
					FIELDS.direction_id, values.get(i++), false));
			bean.setWheelchairAccessible(WHEELCHAIRACCESSIBLETYPE_CONVERTER
					.from(context, FIELDS.wheelchair_accessible,
							values.get(i++), false));
			bean.setShapeId(STRING_CONVERTER.from(context, FIELDS.shape_id,
			values.get(i++), false));
			// bean.setBlockId(STRING_CONVERTER.from(context, FIELDS.block_id,
			// values.get(i++), false));
			// bean.setBikesAllowed(BIKESALLOWEDTYPE_CONVERTER.from(context,
			// FIELDS.bikes_allowed, values.get(i++), false));

			return bean;
		}

		@Override
		public String to(Context context, GtfsTrip input) {
			String result = null;
			List<String> values = new ArrayList<String>();
			values.add(STRING_CONVERTER.to(context, FIELDS.route_id,
					input.getRouteId(), true));
			values.add(STRING_CONVERTER.to(context, FIELDS.service_id,
					input.getServiceId(), true));
			values.add(STRING_CONVERTER.to(context, FIELDS.trip_id,
					input.getTripId(), true));
			values.add(STRING_CONVERTER.to(context, FIELDS.trip_headsign,
					input.getTripHeadSign(), false));
			values.add(STRING_CONVERTER.to(context, FIELDS.trip_short_name,
					input.getTripShortName(), false));
			values.add(DIRECTIONTYPE_CONVERTER.to(context, FIELDS.direction_id,
					input.getDirectionId(), false));
			values.add(WHEELCHAIRACCESSIBLETYPE_CONVERTER.to(context,
					FIELDS.wheelchair_accessible,
					input.getWheelchairAccessible(), false));
			values.add(STRING_CONVERTER.to(context, FIELDS.shape_id,
			input.getShapeId(), false));
			// values.add(STRING_CONVERTER.to(context, FIELDS.block_id,
			// input.getBlockId(), false));
			// values.add(BIKESALLOWEDTYPE_CONVERTER.to(context,
			// FIELDS.bikes_allowed, input.getBikesAllowed(), false));

			result = Tokenizer.untokenize(values);
			return result;
		}

	};

	public static class DefaultExporterFactory extends ExporterFactory {

		@SuppressWarnings({ "unchecked", "rawtypes" })
		@Override
		protected Exporter create(String path) throws IOException {
			return new TripExporter(path);
		}
	}

	static {
		ExporterFactory factory = new DefaultExporterFactory();
		ExporterFactory.factories.put(TripExporter.class.getName(), factory);
	}

}
