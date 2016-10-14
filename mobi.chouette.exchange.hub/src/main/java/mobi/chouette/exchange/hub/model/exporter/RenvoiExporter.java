package mobi.chouette.exchange.hub.model.exporter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import mobi.chouette.exchange.hub.model.HubRenvoi;

public class RenvoiExporter extends ExporterImpl<HubRenvoi> implements
		HubConverter {

	public static enum FIELDS {
		code, nom, identifiant;
	};

	public static final String FILENAME = "RENVOI.TXT";

	public RenvoiExporter(String path) throws IOException {
		super(path);
	}

	@Override
	public void writeHeader() throws IOException {
		write("RENVOI");
	}

	@Override
	public void export(HubRenvoi bean) throws IOException {
		write(CONVERTER.to(_context, bean));
	}

	public static Converter<String, HubRenvoi> CONVERTER = new Converter<String, HubRenvoi>() {


		@Override
		public String to(HubContext hubContext, HubRenvoi input) {
			String result = null;
			List<String> values = new ArrayList<String>();
			values.add(STRING_CONVERTER.to(hubContext, FIELDS.code,
					input.getCode(), true));
			values.add(STRING_CONVERTER.to(hubContext, FIELDS.nom,
					input.getNom(), true));
			values.add(NUMBER_CONVERTER.to(hubContext, FIELDS.identifiant,
					input.getIdentifiant(), false));
			result = Tokenizer.untokenize(values);
			return result;
		}

	};

	public static class DefaultExporterFactory extends ExporterFactory {

		@Override
		protected Exporter<HubRenvoi> create(String path) throws IOException {
			return new RenvoiExporter(path);
		}
	}

	static {
		ExporterFactory factory = new DefaultExporterFactory();
		ExporterFactory.factories.put(RenvoiExporter.class.getName(), factory);
	}

}