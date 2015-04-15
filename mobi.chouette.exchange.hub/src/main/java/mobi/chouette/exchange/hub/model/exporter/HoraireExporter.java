package mobi.chouette.exchange.hub.model.exporter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import mobi.chouette.exchange.hub.model.HubHoraire;

public class HoraireExporter extends ExporterImpl<HubHoraire> implements
		HubConverter {

	public static enum FIELDS {
		code_arret, heure, type, numero_course, numero_mission, 
		identifiant_arret, identifiant;
	};

	public static final String FILENAME = "HORAIRE.TXT";

	public HoraireExporter(String path) throws IOException {
		super(path);
	}

	@Override
	public void writeHeader() throws IOException {
		write("HORAIRE");
	}

	@Override
	public void export(HubHoraire bean) throws IOException {
		write(CONVERTER.to(_context, bean));
	}

	public static Converter<String, HubHoraire> CONVERTER = new Converter<String, HubHoraire>() {


		@Override
		public String to(Context context, HubHoraire input) {
			String result = null;
			List<String> values = new ArrayList<String>();
			values.add(STRING_CONVERTER.to(context, FIELDS.code_arret,
					input.getCodeArret(), false));
			values.add(NUMBER_CONVERTER.to(context, FIELDS.heure,
					input.getHeure(), true));
			values.add(STRING_CONVERTER.to(context, FIELDS.type,
					input.getType(), true));
			values.add(NUMBER_CONVERTER.to(context, FIELDS.numero_course,
					input.getNumeroCourse(), true));
			values.add(NUMBER_CONVERTER.to(context, FIELDS.numero_mission,
					input.getNumeroMission(), false));
			values.add(NUMBER_CONVERTER.to(context, FIELDS.identifiant_arret,
					input.getIdentifiantArret(), true));
			values.add(NUMBER_CONVERTER.to(context, FIELDS.identifiant,
					input.getIdentifiant(), false));
			result = Tokenizer.untokenize(values);
			return result;
		}

	};

	public static class DefaultExporterFactory extends ExporterFactory {

		@Override
		protected Exporter<HubHoraire> create(String path) throws IOException {
			return new HoraireExporter(path);
		}
	}

	static {
		ExporterFactory factory = new DefaultExporterFactory();
		ExporterFactory.factories.put(HoraireExporter.class.getName(), factory);
	}

}