package mobi.chouette.exchange.gtfs.model.exporter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import mobi.chouette.exchange.gtfs.model.GtfsFrequency;
import mobi.chouette.exchange.gtfs.model.importer.Context;
import mobi.chouette.exchange.gtfs.model.importer.GtfsConverter;

public class FrequencyExporter extends ExporterImpl<GtfsFrequency> implements
		GtfsConverter {
	public static enum FIELDS {
		trip_id, start_time, end_time, headway_secs, exact_times;
	};

	public static final String FILENAME = "frequencies.txt";

	public FrequencyExporter(String name) throws IOException {
		super(name);
	}

	@Override
	public void writeHeader() throws IOException {
		write(FIELDS.values());
	}

	@Override
	public void export(GtfsFrequency bean) throws IOException {
		write(CONVERTER.to(_context, bean));
	}

	public static Converter<String, GtfsFrequency> CONVERTER = new Converter<String, GtfsFrequency>() {

		@Override
		public GtfsFrequency from(Context context, String input) {
			GtfsFrequency bean = new GtfsFrequency();
			List<String> values = Tokenizer.tokenize(input);

			int i = 0;
			bean.setTripId(STRING_CONVERTER.from(context, FIELDS.trip_id,
					values.get(i++), true));
			bean.setStartTime(GTFSTIME_CONVERTER.from(context,
					FIELDS.start_time, values.get(i++), true));
			bean.setEndTime(GTFSTIME_CONVERTER.from(context, FIELDS.end_time,
					values.get(i++), true));
			bean.setHeadwaySecs(INTEGER_CONVERTER.from(context,
					FIELDS.headway_secs, values.get(i++), true));
			bean.setExactTimes(BOOLEAN_CONVERTER.from(context,
					FIELDS.exact_times, values.get(i++), false, false));

			return bean;
		}

		@Override
		public String to(Context context, GtfsFrequency input) {
			String result = null;
			List<String> values = new ArrayList<String>();
			values.add(STRING_CONVERTER.to(context, FIELDS.trip_id,
					input.getTripId(), true));
			values.add(GTFSTIME_CONVERTER.to(context, FIELDS.start_time,
					input.getStartTime(), true));
			values.add(GTFSTIME_CONVERTER.to(context, FIELDS.end_time,
					input.getEndTime(), true));
			values.add(INTEGER_CONVERTER.to(context, FIELDS.headway_secs,
					input.getHeadwaySecs(), true));
			values.add(BOOLEAN_CONVERTER.to(context, FIELDS.exact_times,
					input.getExactTimes(), false));

			result = Tokenizer.untokenize(values);
			return result;
		}

	};

	public static class DefaultExporterFactory extends ExporterFactory {

		@SuppressWarnings({ "unchecked", "rawtypes" })
		@Override
		protected Exporter create(String path) throws IOException {
			return new FrequencyExporter(path);
		}
	}

	static {
		ExporterFactory factory = new DefaultExporterFactory();
		ExporterFactory.factories.put(FrequencyExporter.class.getName(),
				factory);
	}
}
