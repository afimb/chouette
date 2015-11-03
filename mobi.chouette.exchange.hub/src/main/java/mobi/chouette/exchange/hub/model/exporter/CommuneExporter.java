package mobi.chouette.exchange.hub.model.exporter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import mobi.chouette.exchange.hub.model.HubCommune;

public class CommuneExporter extends ExporterImpl<HubCommune> implements
		HubConverter {

	public static enum FIELDS {
		code_insee, nom;
	};

	public static final String FILENAME = "COMMUNES.TXT";

	public CommuneExporter(String path) throws IOException {
		super(path);
	}

	@Override
	public void writeHeader() throws IOException {
		write("COMMUNES");
	}

	@Override
	public void export(HubCommune bean) throws IOException {
		write(CONVERTER.to(_context, bean));
	}

	public static Converter<String, HubCommune> CONVERTER = new Converter<String, HubCommune>() {


		@Override
		public String to(Context context, HubCommune input) {
			String result = null;
			List<String> values = new ArrayList<String>();
			values.add(NUMBER_CONVERTER.to(context, FIELDS.code_insee,
					input.getCodeInsee(), true));
			values.add(STRING_CONVERTER.to(context, FIELDS.nom,
					input.getNom(), true));
			result = Tokenizer.untokenize(values);
			return result;
		}

	};

	public static class DefaultExporterFactory extends ExporterFactory {

		@Override
		protected Exporter<HubCommune> create(String path) throws IOException {
			return new CommuneExporter(path);
		}
	}

	static {
		ExporterFactory factory = new DefaultExporterFactory();
		ExporterFactory.factories.put(CommuneExporter.class.getName(), factory);
	}

}
