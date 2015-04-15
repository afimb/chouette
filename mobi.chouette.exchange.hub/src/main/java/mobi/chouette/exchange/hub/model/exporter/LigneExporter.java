package mobi.chouette.exchange.hub.model.exporter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import mobi.chouette.exchange.hub.model.HubLigne;

public class LigneExporter extends ExporterImpl<HubLigne> implements
		HubConverter {

	public static enum FIELDS {
		code, code_commercial, nom, code_representation, code_sous_traitant, code_transporteur,
		code_reseau, code_groupe_de_ligne, identifiant;
	};

	public static final String FILENAME = "LIGNE.TXT";

	public LigneExporter(String path) throws IOException {
		super(path);
	}

	@Override
	public void writeHeader() throws IOException {
		write("LIGNE");
	}

	@Override
	public void export(HubLigne bean) throws IOException {
		write(CONVERTER.to(_context, bean));
	}

	public static Converter<String, HubLigne> CONVERTER = new Converter<String, HubLigne>() {


		@Override
		public String to(Context context, HubLigne input) {
			String result = null;
			List<String> values = new ArrayList<String>();
			values.add(STRING_CONVERTER.to(context, FIELDS.code,
					input.getCode(), true));
			values.add(STRING_CONVERTER.to(context, FIELDS.code_commercial,
					input.getCodeCommercial(), true));
			values.add(STRING_CONVERTER.to(context, FIELDS.nom,
					input.getNom(), false));
			values.add(NUMBER_CONVERTER.to(context, FIELDS.code_representation,
					input.getCodeRepresentation(), false));
			values.add(STRING_CONVERTER.to(context, FIELDS.code_sous_traitant,
					input.getCodeSousTraitant(), false));
			values.add(STRING_CONVERTER.to(context, FIELDS.code_transporteur,
					input.getCodeTransporteur(), true));
			values.add(STRING_CONVERTER.to(context, FIELDS.code_reseau,
					input.getCodeReseau(), true));
			values.add(STRING_CONVERTER.to(context, FIELDS.code_groupe_de_ligne,
					input.getCodeGroupeDeLigne(), true));
			values.add(NUMBER_CONVERTER.to(context, FIELDS.identifiant,
					input.getIdentifiant(), false));
			result = Tokenizer.untokenize(values);
			return result;
		}

	};

	public static class DefaultExporterFactory extends ExporterFactory {

		@Override
		protected Exporter<HubLigne> create(String path) throws IOException {
			return new LigneExporter(path);
		}
	}

	static {
		ExporterFactory factory = new DefaultExporterFactory();
		ExporterFactory.factories.put(LigneExporter.class.getName(), factory);
	}

}