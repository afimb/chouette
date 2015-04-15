package mobi.chouette.exchange.hub.model.exporter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import mobi.chouette.exchange.hub.model.HubArret;

public class ArretExporter extends ExporterImpl<HubArret> implements
		HubConverter {

	public static enum FIELDS {
		code, code_insee, commentaire, commune, description, identifiant, nom, nom_reduit, type,x,y;
	};

	public static final String FILENAME = "ARRET.TXT";

	public ArretExporter(String path) throws IOException {
		super(path);
	}

	@Override
	public void writeHeader() throws IOException {
		write("ARRET");
	}

	@Override
	public void export(HubArret bean) throws IOException {
		write(CONVERTER.to(_context, bean));
	}

	public static Converter<String, HubArret> CONVERTER = new Converter<String, HubArret>() {


		@Override
		public String to(Context context, HubArret input) {
			String result = null;
			boolean physique = input.getType().startsWith("N");
			List<String> values = new ArrayList<String>();
			values.add(STRING_CONVERTER.to(context, FIELDS.code,
					input.getCode(), true));
			values.add(STRING_CONVERTER.to(context, FIELDS.nom,
					input.getNom(), true));
			values.add(STRING_CONVERTER.to(context, FIELDS.description,
					input.getDescription(), false));
			values.add(STRING_CONVERTER.to(context, FIELDS.type,
					input.getType(), true));
			values.add(STRING_CONVERTER.to(context, FIELDS.nom_reduit,
					input.getNomReduit(), physique));
			values.add(NUMBER_CONVERTER.to(context, FIELDS.x,
					input.getX(), false));
			values.add(NUMBER_CONVERTER.to(context, FIELDS.y,
					input.getY(), false));
			values.add(STRING_CONVERTER.to(context, FIELDS.commune,
					input.getCommune(), true));
			values.add(NUMBER_CONVERTER.to(context, FIELDS.code_insee,
					input.getCodeInsee(), true));
			values.add(STRING_CONVERTER.to(context, FIELDS.commentaire,
					input.getCommentaire(), false));
			values.add(NUMBER_CONVERTER.to(context, FIELDS.identifiant,
					input.getIdentifiant(), physique));
			result = Tokenizer.untokenize(values);
			return result;
		}

	};

	public static class DefaultExporterFactory extends ExporterFactory {

		@Override
		protected Exporter<HubArret> create(String path) throws IOException {
			return new ArretExporter(path);
		}
	}

	static {
		ExporterFactory factory = new DefaultExporterFactory();
		ExporterFactory.factories.put(ArretExporter.class.getName(), factory);
	}

}