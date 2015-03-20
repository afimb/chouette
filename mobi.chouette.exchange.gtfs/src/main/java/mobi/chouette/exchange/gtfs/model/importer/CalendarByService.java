package mobi.chouette.exchange.gtfs.model.importer;

import java.io.IOException;

import mobi.chouette.exchange.gtfs.model.GtfsCalendar;

public class CalendarByService extends IndexImpl<GtfsCalendar> implements
		GtfsConverter {

	public static enum FIELDS {
		service_id, monday, tuesday, wednesday, thursday, friday, saturday, sunday, start_date, end_date;
	};

	public static final String FILENAME = "calendar.txt";
	public static final String KEY = FIELDS.service_id.name();

	private GtfsCalendar bean = new GtfsCalendar();
	private String[] array = new String[FIELDS.values().length];

	public CalendarByService(String name) throws IOException {
		super(name, KEY);
	}

	@Override
	protected GtfsCalendar build(GtfsIterator reader, Context context) {
		int i = 0;
		for (FIELDS field : FIELDS.values()) {
			array[i++] = getField(reader, field.name());
		}

		i = 0;
		int id = (int) context.get(Context.ID);
		bean.setId(id);
		bean.setServiceId(STRING_CONVERTER.from(context, FIELDS.service_id,
				array[i++], true));
		bean.setMonday(BOOLEAN_CONVERTER.from(context, FIELDS.monday,
				array[i++], true));
		bean.setTuesday(BOOLEAN_CONVERTER.from(context, FIELDS.tuesday,
				array[i++], true));
		bean.setWednesday(BOOLEAN_CONVERTER.from(context, FIELDS.wednesday,
				array[i++], true));
		bean.setThursday(BOOLEAN_CONVERTER.from(context, FIELDS.thursday,
				array[i++], true));
		bean.setFriday(BOOLEAN_CONVERTER.from(context, FIELDS.friday,
				array[i++], true));
		bean.setSaturday(BOOLEAN_CONVERTER.from(context, FIELDS.saturday,
				array[i++], true));
		bean.setSunday(BOOLEAN_CONVERTER.from(context, FIELDS.sunday,
				array[i++], true));
		bean.setStartDate(DATE_CONVERTER.from(context, FIELDS.start_date,
				array[i++], true));
		bean.setEndDate(DATE_CONVERTER.from(context, FIELDS.end_date,
				array[i++], true));

		return bean;
	}

	@Override
	public boolean validate(GtfsCalendar bean, GtfsImporter dao) {
		return true;
	}

	public static class DefaultImporterFactory extends IndexFactory {
		@SuppressWarnings("rawtypes")
		@Override
		protected Index create(String name) throws IOException {
			return new CalendarByService(name);
		}
	}

	static {
		IndexFactory factory = new DefaultImporterFactory();
		IndexFactory.factories.put(CalendarByService.class.getName(), factory);
	}

}
