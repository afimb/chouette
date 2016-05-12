package mobi.chouette.exchange.gtfs.model.exporter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import mobi.chouette.exchange.gtfs.model.GtfsCalendarDate;
import mobi.chouette.exchange.gtfs.model.importer.Context;
import mobi.chouette.exchange.gtfs.model.importer.GtfsConverter;

public class CalendarDateExporter extends ExporterImpl<GtfsCalendarDate>
		implements GtfsConverter {
	public static enum FIELDS {
		service_id, date, exception_type;
	};

	public static final String FILENAME = "calendar_dates.txt";

	public CalendarDateExporter(String name) throws IOException {
		super(name);
	}

	@Override
	public void writeHeader() throws IOException {
		write(FIELDS.values());
	}

	@Override
	public void export(GtfsCalendarDate bean) throws IOException {
		write(CONVERTER.to(_context, bean));
	}

	public static Converter<String, GtfsCalendarDate> CONVERTER = new Converter<String, GtfsCalendarDate>() {

		@Override
		public GtfsCalendarDate from(Context context, String input) {
			GtfsCalendarDate bean = new GtfsCalendarDate();
			List<String> values = Tokenizer.tokenize(input);

			int i = 0;
			bean.setServiceId(STRING_CONVERTER.from(context, FIELDS.service_id,
					values.get(i++), true));
			bean.setDate(DATE_CONVERTER.from(context, FIELDS.date,
					values.get(i++), true));
			bean.setExceptionType(EXCEPTIONTYPE_CONVERTER.from(context,
					FIELDS.exception_type, values.get(i++), true));

			return bean;
		}

		@Override
		public String to(Context context, GtfsCalendarDate input) {
			String result = null;
			List<String> values = new ArrayList<String>();
			values.add(STRING_CONVERTER.to(context, FIELDS.service_id,
					input.getServiceId(), true));
			values.add(DATE_CONVERTER.to(context, FIELDS.date, input.getDate(),
					true));
			values.add(EXCEPTIONTYPE_CONVERTER.to(context,
					FIELDS.exception_type, input.getExceptionType(), true));

			result = Tokenizer.untokenize(values);
			return result;
		}

	};

	public static class DefaultExporterFactory extends ExporterFactory {

		@SuppressWarnings({ "unchecked", "rawtypes" })
		@Override
		protected Exporter create(String path) throws IOException {
			return new CalendarDateExporter(path);
		}
	}

	static {
		ExporterFactory factory = new DefaultExporterFactory();
		ExporterFactory.factories.put(CalendarDateExporter.class.getName(),
				factory);
	}

}
