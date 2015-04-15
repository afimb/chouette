package mobi.chouette.exchange.hub.model.exporter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import mobi.chouette.exchange.hub.model.HubSchema;
import mobi.chouette.exchange.hub.model.HubSchema.ArretSchema;;

public class SchemaExporter extends ExporterImpl<HubSchema> implements
		HubConverter {

	public static enum FIELDS {
		code_ligne, sens, identifiant, arrets, code_arret, identifiant_arret;
	};

	public static final String FILENAME = "SCHEMA.TXT";

	public SchemaExporter(String path) throws IOException {
		super(path);
	}

	@Override
	public void writeHeader() throws IOException {
		write("SCHEMA");
	}

	@Override
	public void export(HubSchema bean) throws IOException {
		write(CONVERTER.to(_context, bean));
	}

	public static Converter<String, HubSchema> CONVERTER = new Converter<String, HubSchema>() {


		@Override
		public String to(Context context, HubSchema input) {
			String result = null;
			List<String> values = new ArrayList<String>();
			values.add(STRING_CONVERTER.to(context, FIELDS.code_ligne,
					input.getCodeLigne(), true));
			values.add(NUMBER_CONVERTER.to(context, FIELDS.sens,
					input.getSens(), true));
			values.add(NUMBER_CONVERTER.to(context, FIELDS.identifiant,
					input.getIdentifiant(), false));
			// arrêts
			for (ArretSchema arret : input.getArrets()) {
				values.add(STRING_CONVERTER.to(context, FIELDS.code_arret,
						arret.getCode(), false));
				values.add(NUMBER_CONVERTER.to(context, FIELDS.identifiant_arret,
						arret.getIdentifiant(), true));
			}
			result = Tokenizer.untokenize(values);
			return result;
		}

	};

	public static class DefaultExporterFactory extends ExporterFactory {

		@Override
		protected Exporter<HubSchema> create(String path) throws IOException {
			return new SchemaExporter(path);
		}
	}

	static {
		ExporterFactory factory = new DefaultExporterFactory();
		ExporterFactory.factories.put(SchemaExporter.class.getName(), factory);
	}

}