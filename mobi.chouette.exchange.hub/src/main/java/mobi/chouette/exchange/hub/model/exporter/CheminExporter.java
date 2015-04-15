package mobi.chouette.exchange.hub.model.exporter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import mobi.chouette.exchange.hub.model.HubChemin;
import mobi.chouette.exchange.hub.model.HubChemin.ArretChemin;

public class CheminExporter extends ExporterImpl<HubChemin> implements
		HubConverter {

	public static enum FIELDS {
		code_ligne, code_chemin, identifiant, nom, sens, type, code_representation, arrets, code,distance;
	};

	public static final String FILENAME = "CHEMIN.TXT";

	public CheminExporter(String path) throws IOException {
		super(path);
	}

	@Override
	public void writeHeader() throws IOException {
		write("CHEMIN");
	}

	@Override
	public void export(HubChemin bean) throws IOException {
		write(CONVERTER.to(_context, bean));
	}

	public static Converter<String, HubChemin> CONVERTER = new Converter<String, HubChemin>() {


		@Override
		public String to(Context context, HubChemin input) {
			String result = null;
			List<String> values = new ArrayList<String>();
			values.add(STRING_CONVERTER.to(context, FIELDS.code_ligne,
					input.getCodeLigne(), true));
			values.add(STRING_CONVERTER.to(context, FIELDS.code_chemin,
					input.getCodeChemin(), true));
			values.add(NUMBER_CONVERTER.to(context, FIELDS.identifiant,
					input.getIdentifiant(), false));
			values.add(STRING_CONVERTER.to(context, FIELDS.nom,
					input.getNom(), false));
			values.add(NUMBER_CONVERTER.to(context, FIELDS.sens,
					input.getSens(), true));
			values.add(STRING_CONVERTER.to(context, FIELDS.type,
					input.getType(), true));
			values.add(NUMBER_CONVERTER.to(context, FIELDS.code_representation,
					input.getCodeRepresentation(), false));
			// arrêts
			for (ArretChemin arret : input.getArrets()) {
				values.add(STRING_CONVERTER.to(context, FIELDS.code,
						arret.getCode(), false));
				values.add(NUMBER_CONVERTER.to(context, FIELDS.identifiant,
						arret.getIdentifiant(), true));
				values.add(NUMBER_CONVERTER.to(context, FIELDS.distance,
						arret.getDistance(), false));
				values.add(STRING_CONVERTER.to(context, FIELDS.type,
						arret.getType(), false));				
			}
			result = Tokenizer.untokenize(values);
			return result;
		}

	};

	public static class DefaultExporterFactory extends ExporterFactory {

		@Override
		protected Exporter<HubChemin> create(String path) throws IOException {
			return new CheminExporter(path);
		}
	}

	static {
		ExporterFactory factory = new DefaultExporterFactory();
		ExporterFactory.factories.put(CheminExporter.class.getName(), factory);
	}

}