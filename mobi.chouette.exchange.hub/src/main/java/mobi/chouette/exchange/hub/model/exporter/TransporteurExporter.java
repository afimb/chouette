package mobi.chouette.exchange.hub.model.exporter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import mobi.chouette.exchange.hub.model.HubTransporteur;

public class TransporteurExporter extends ExporterImpl<HubTransporteur> implements
		HubConverter {

	public static enum FIELDS {
		code, nom, identifiant;
	};

	public static final String FILENAME = "TRANSPORTEUR.TXT";

	public TransporteurExporter(String path) throws IOException {
		super(path);
	}

	@Override
	public void writeHeader() throws IOException {
		write("TRANSPORTEUR");
	}

	@Override
	public void export(HubTransporteur bean) throws IOException {
		write(CONVERTER.to(_context, bean));
	}

	public static Converter<String, HubTransporteur> CONVERTER = new Converter<String, HubTransporteur>() {


		@Override
		public String to(Context context, HubTransporteur input) {
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
		protected Exporter<HubTransporteur> create(String path) throws IOException {
			return new TransporteurExporter(path);
		}
	}

	static {
		ExporterFactory factory = new DefaultExporterFactory();
		ExporterFactory.factories.put(TransporteurExporter.class.getName(), factory);
	}

}