package mobi.chouette.exchange.hub.model.exporter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import mobi.chouette.exchange.hub.model.HubCorrespondance;

public class CorrespondanceExporter extends ExporterImpl<HubCorrespondance> implements
		HubConverter {

	public static enum FIELDS {
		code_arret_1, identifiant_arret_1, code_arret_2, identifiant_arret_2,
		distance, temps_parcours, identifiant;
	};

	public static final String FILENAME = "CORRESPONDANCE.TXT";

	public CorrespondanceExporter(String path) throws IOException {
		super(path);
	}

	@Override
	public void writeHeader() throws IOException {
		write("CORRESPONDANCE");
	}

	@Override
	public void export(HubCorrespondance bean) throws IOException {
		write(CONVERTER.to(_context, bean));
	}

	public static Converter<String, HubCorrespondance> CONVERTER = new Converter<String, HubCorrespondance>() {


		@Override
		public String to(Context context, HubCorrespondance input) {
			String result = null;
			List<String> values = new ArrayList<String>();
			values.add(STRING_CONVERTER.to(context, FIELDS.code_arret_1,
					input.getCodeArret1(), false));
			values.add(NUMBER_CONVERTER.to(context, FIELDS.identifiant_arret_1,
					input.getIdentifiantArret1(), true));
			values.add(STRING_CONVERTER.to(context, FIELDS.code_arret_2,
					input.getCodeArret2(), false));
			values.add(NUMBER_CONVERTER.to(context, FIELDS.identifiant_arret_2,
					input.getIdentifiantArret2(), true));
			values.add(NUMBER_CONVERTER.to(context, FIELDS.distance,
					input.getDistance(), true));
			values.add(NUMBER_CONVERTER.to(context, FIELDS.temps_parcours,
					input.getTempsParcours(), false));
			values.add(NUMBER_CONVERTER.to(context, FIELDS.identifiant,
					input.getIdentifiant(), false));
			result = Tokenizer.untokenize(values);
			return result;
		}

	};

	public static class DefaultExporterFactory extends ExporterFactory {

		@Override
		protected Exporter<HubCorrespondance> create(String path) throws IOException {
			return new CorrespondanceExporter(path);
		}
	}

	static {
		ExporterFactory factory = new DefaultExporterFactory();
		ExporterFactory.factories.put(CorrespondanceExporter.class.getName(), factory);
	}

}