package mobi.chouette.exchange.gtfs.model.exporter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import mobi.chouette.exchange.gtfs.model.GtfsCalendar;
import mobi.chouette.exchange.gtfs.model.importer.Context;
import mobi.chouette.exchange.gtfs.model.importer.GtfsConverter;

public class CalendarExporter extends ExporterImpl<GtfsCalendar> implements
		GtfsConverter {
	public static enum FIELDS {
		service_id, monday, tuesday, wednesday, thursday, friday, saturday, sunday, start_date, end_date;
	};

	public static final String FILENAME = "calendar.txt";

	public CalendarExporter(String name) throws IOException {
		super(name);
	}

	@Override
	public void writeHeader() throws IOException {
		write(FIELDS.values());
	}

	@Override
	public void export(GtfsCalendar bean) throws IOException {
		write(CONVERTER.to(_context, bean));
	}

	public static Converter<String, GtfsCalendar> CONVERTER = new Converter<String, GtfsCalendar>() {

		@Override
		public GtfsCalendar from(Context context, String input) {

			GtfsCalendar bean = new GtfsCalendar();
			List<String> values = Tokenizer.tokenize(input);

			int i = 0;
			bean.setServiceId(STRING_CONVERTER.from(context, FIELDS.service_id,
					values.get(i++), true));
			bean.setMonday(BOOLEAN_CONVERTER.from(context, FIELDS.monday,
					values.get(i++), true));
			bean.setTuesday(BOOLEAN_CONVERTER.from(context, FIELDS.tuesday,
					values.get(i++), true));
			bean.setWednesday(BOOLEAN_CONVERTER.from(context, FIELDS.wednesday,
					values.get(i++), true));
			bean.setThursday(BOOLEAN_CONVERTER.from(context, FIELDS.thursday,
					values.get(i++), true));
			bean.setFriday(BOOLEAN_CONVERTER.from(context, FIELDS.friday,
					values.get(i++), true));
			bean.setSaturday(BOOLEAN_CONVERTER.from(context, FIELDS.saturday,
					values.get(i++), true));
			bean.setSunday(BOOLEAN_CONVERTER.from(context, FIELDS.sunday,
					values.get(i++), true));
			bean.setStartDate(DATE_CONVERTER.from(context, FIELDS.start_date,
					values.get(i++), true));
			bean.setEndDate(DATE_CONVERTER.from(context, FIELDS.end_date,
					values.get(i++), true));

			return bean;
		}

		@Override
		public String to(Context context, GtfsCalendar input) {
			String result = null;
			List<String> values = new ArrayList<String>();
			values.add(STRING_CONVERTER.to(context, FIELDS.service_id,
					input.getServiceId(), true));
			values.add(BOOLEAN_CONVERTER.to(context, FIELDS.monday,
					input.getMonday(), true));
			values.add(BOOLEAN_CONVERTER.to(context, FIELDS.tuesday,
					input.getTuesday(), true));
			values.add(BOOLEAN_CONVERTER.to(context, FIELDS.wednesday,
					input.getWednesday(), true));
			values.add(BOOLEAN_CONVERTER.to(context, FIELDS.thursday,
					input.getThursday(), true));
			values.add(BOOLEAN_CONVERTER.to(context, FIELDS.friday,
					input.getFriday(), true));
			values.add(BOOLEAN_CONVERTER.to(context, FIELDS.saturday,
					input.getSaturday(), true));
			values.add(BOOLEAN_CONVERTER.to(context, FIELDS.sunday,
					input.getSunday(), true));
			values.add(DATE_CONVERTER.to(context, FIELDS.start_date,
					input.getStartDate(), true));
			values.add(DATE_CONVERTER.to(context, FIELDS.end_date,
					input.getEndDate(), true));

			result = Tokenizer.untokenize(values);
			return result;
		}

	};

	public static class DefaultExporterFactory extends ExporterFactory {

		@SuppressWarnings({ "unchecked", "rawtypes" })
		@Override
		protected Exporter create(String path) throws IOException {
			return new CalendarExporter(path);
		}
	}

	static {
		ExporterFactory factory = new DefaultExporterFactory();
		ExporterFactory.factories
				.put(CalendarExporter.class.getName(), factory);
	}

}
