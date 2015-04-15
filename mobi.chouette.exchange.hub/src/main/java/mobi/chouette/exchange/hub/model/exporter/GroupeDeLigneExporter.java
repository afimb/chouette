package mobi.chouette.exchange.hub.model.exporter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import mobi.chouette.exchange.hub.model.HubGroupeDeLigne;

public class GroupeDeLigneExporter extends ExporterImpl<HubGroupeDeLigne> implements
		HubConverter {

	public static enum FIELDS {
		code, nom, identifiant;
	};

	public static final String FILENAME = "GROUPELIGNE.TXT";

	public GroupeDeLigneExporter(String path) throws IOException {
		super(path);
	}

	@Override
	public void writeHeader() throws IOException {
		write("GROUPELIGNE");
	}

	@Override
	public void export(HubGroupeDeLigne bean) throws IOException {
		write(CONVERTER.to(_context, bean));
	}

	public static Converter<String, HubGroupeDeLigne> CONVERTER = new Converter<String, HubGroupeDeLigne>() {


		@Override
		public String to(Context context, HubGroupeDeLigne input) {
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
		protected Exporter<HubGroupeDeLigne> create(String path) throws IOException {
			return new GroupeDeLigneExporter(path);
		}
	}

	static {
		ExporterFactory factory = new DefaultExporterFactory();
		ExporterFactory.factories.put(GroupeDeLigneExporter.class.getName(), factory);
	}

}