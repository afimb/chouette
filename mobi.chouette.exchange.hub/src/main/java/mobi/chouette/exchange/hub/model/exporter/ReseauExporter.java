package mobi.chouette.exchange.hub.model.exporter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import mobi.chouette.exchange.hub.model.HubReseau;

public class ReseauExporter extends ExporterImpl<HubReseau> implements
		HubConverter {

	public static enum FIELDS {
		code, nom, identifiant;
	};

	public static final String FILENAME = "RESEAU.TXT";

	public ReseauExporter(String path) throws IOException {
		super(path);
	}

	@Override
	public void writeHeader() throws IOException {
		write("RESEAU");
	}

	@Override
	public void export(HubReseau bean) throws IOException {
		write(CONVERTER.to(_context, bean));
	}

	public static Converter<String, HubReseau> CONVERTER = new Converter<String, HubReseau>() {


		@Override
		public String to(Context context, HubReseau input) {
			String result = null;
			List<String> values = new ArrayList<String>();
			values.add(STRING_CONVERTER.to(context, FIELDS.code,
					input.getCode(), true));
			values.add(STRING_CONVERTER.to(context, FIELDS.nom,
					input.getNom(), true));
			values.add(NUMBER_CONVERTER.to(context, FIELDS.identifiant,
					input.getIdentifiant(), false));
			result = Tokenizer.untokenize(values);
			return result;
		}

	};

	public static class DefaultExporterFactory extends ExporterFactory {

		@Override
		protected Exporter<HubReseau> create(String path) throws IOException {
			return new ReseauExporter(path);
		}
	}

	static {
		ExporterFactory factory = new DefaultExporterFactory();
		ExporterFactory.factories.put(ReseauExporter.class.getName(), factory);
	}

}