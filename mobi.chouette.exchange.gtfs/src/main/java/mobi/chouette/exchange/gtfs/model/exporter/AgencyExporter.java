package mobi.chouette.exchange.gtfs.model.exporter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import mobi.chouette.exchange.gtfs.model.GtfsAgency;
import mobi.chouette.exchange.gtfs.model.importer.Context;
import mobi.chouette.exchange.gtfs.model.importer.GtfsConverter;

public class AgencyExporter extends ExporterImpl<GtfsAgency> implements
		GtfsConverter {

	public static enum FIELDS {
		agency_id, agency_name, agency_url, agency_timezone, agency_phone; // ,
																			// agency_lang
																			// ,
																			// agency_fare_url;
	};

	public static final String FILENAME = "agency.txt";

	public AgencyExporter(String path) throws IOException {
		super(path);
	}

	@Override
	public void writeHeader() throws IOException {
		write(FIELDS.values());
	}

	@Override
	public void export(GtfsAgency bean) throws IOException {
		write(CONVERTER.to(_context, bean));
	}

	public static Converter<String, GtfsAgency> CONVERTER = new Converter<String, GtfsAgency>() {

		@Override
		public GtfsAgency from(Context context, String input) {
			GtfsAgency bean = new GtfsAgency();
			List<String> values = Tokenizer.tokenize(input);

			int i = 0;
			bean.setAgencyId(STRING_CONVERTER.from(context, FIELDS.agency_id,
					values.get(i++), false));
			bean.setAgencyName(STRING_CONVERTER.from(context,
					FIELDS.agency_name, values.get(i++), true));
			bean.setAgencyUrl(URL_CONVERTER.from(context, FIELDS.agency_url,
					values.get(i++), true));
			bean.setAgencyTimezone(TIMEZONE_CONVERTER.from(context,
					FIELDS.agency_timezone, values.get(i++), true));
			bean.setAgencyPhone(STRING_CONVERTER.from(context,
					FIELDS.agency_phone, values.get(i++), false));
			// bean.setAgencyLang(STRING_CONVERTER.from(context,
			// FIELDS.agency_lang,
			// values.get(i++), false));
			// bean.setAgencyFareUrl(URL_CONVERTER.from(context,
			// FIELDS.agency_fare_url, values.get(i++), false));

			return bean;
		}

		@Override
		public String to(Context context, GtfsAgency input) {
			String result = null;
			List<String> values = new ArrayList<String>();
			values.add(STRING_CONVERTER.to(context, FIELDS.agency_id,
					input.getAgencyId(), false));
			values.add(STRING_CONVERTER.to(context, FIELDS.agency_name,
					input.getAgencyName(), true));
			values.add(URL_CONVERTER.to(context, FIELDS.agency_url,
					input.getAgencyUrl(), true));
			values.add(TIMEZONE_CONVERTER.to(context, FIELDS.agency_timezone,
					input.getAgencyTimezone(), true));
			values.add(STRING_CONVERTER.to(context, FIELDS.agency_phone,
					input.getAgencyPhone(), false));
			// values.add(STRING_CONVERTER.to(context, FIELDS.agency_lang,
			// input.getAgencyLang(), false));
			// values.add(URL_CONVERTER.to(context, FIELDS.agency_fare_url,
			// input.getAgencyFareUrl(), false));

			result = Tokenizer.untokenize(values);
			return result;
		}

	};

	public static class DefaultExporterFactory extends ExporterFactory {

		@Override
		protected Exporter<GtfsAgency> create(String path) throws IOException {
			return new AgencyExporter(path);
		}
	}

	static {
		ExporterFactory factory = new DefaultExporterFactory();
		ExporterFactory.factories.put(AgencyExporter.class.getName(), factory);
	}

}