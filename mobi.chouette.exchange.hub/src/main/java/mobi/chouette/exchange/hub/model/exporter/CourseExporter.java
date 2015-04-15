package mobi.chouette.exchange.hub.model.exporter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import mobi.chouette.exchange.hub.model.HubCourse;

public class CourseExporter extends ExporterImpl<HubCourse> implements
		HubConverter {

	public static enum FIELDS {
		numero, service_voiture, type_materiel, code_arret, heure, code_ligne,
		code_chemin, type, sens, validite, graphique, identifiant_arret,
		identifiant_renvoi,code_periode,categorie,identifiant;
	};

	public static final String FILENAME = "COURSE.TXT";

	public CourseExporter(String path) throws IOException {
		super(path);
	}

	@Override
	public void writeHeader() throws IOException {
		write("COURSE");
	}

	@Override
	public void export(HubCourse bean) throws IOException {
		write(CONVERTER.to(_context, bean));
	}

	public static Converter<String, HubCourse> CONVERTER = new Converter<String, HubCourse>() {


		@Override
		public String to(Context context, HubCourse input) {
			String result = null;
			List<String> values = new ArrayList<String>();
			values.add(NUMBER_CONVERTER.to(context, FIELDS.numero,
					input.getNumero(), true));
			values.add(NUMBER_CONVERTER.to(context, FIELDS.service_voiture,
					input.getServiceVoiture(), false));
			values.add(NUMBER_CONVERTER.to(context, FIELDS.type_materiel,
					input.getTypeMateriel(), false));
			values.add(STRING_CONVERTER.to(context, FIELDS.code_arret,
					input.getCodeArret(), false));
			values.add(NUMBER_CONVERTER.to(context, FIELDS.heure,
					input.getHeure(), false));
			values.add(STRING_CONVERTER.to(context, FIELDS.code_ligne,
					input.getCodeLigne(), true));
			values.add(STRING_CONVERTER.to(context, FIELDS.code_chemin,
					input.getCodeChemin(), true));
			values.add(STRING_CONVERTER.to(context, FIELDS.type,
					input.getType(), true));
			values.add(NUMBER_CONVERTER.to(context, FIELDS.sens,
					input.getSens(), true));
			values.add(NUMBER_CONVERTER.to(context, FIELDS.validite,
					input.getValidite(), true));
			values.add(STRING_CONVERTER.to(context, FIELDS.graphique,
					input.getGraphique(), false));
			values.add(NUMBER_CONVERTER.to(context, FIELDS.identifiant_arret,
					input.getIdentifiantArret(), true));
			StringBuffer list = new StringBuffer();
			for (Integer idRenvoi : input.getIdentifiantsRenvoi()) {
				list.append("|");
				list.append(NUMBER_CONVERTER.to(context, FIELDS.identifiant_renvoi,
					idRenvoi, true));
			}
			if (list.length() > 0) list.deleteCharAt(0);
			values.add(list.toString());
			list = new StringBuffer();
			for (String code : input.getCodesPeriode()) {
				list.append("|");
				list.append(STRING_CONVERTER.to(context, FIELDS.code_periode,
						code, true));
			}
			if (list.length() > 0) list.deleteCharAt(0);
			values.add(list.toString());
			values.add(NUMBER_CONVERTER.to(context, FIELDS.categorie,
					input.getCategorie(), false));
			values.add(NUMBER_CONVERTER.to(context, FIELDS.identifiant,
					input.getIdentifiant(), false));
			result = Tokenizer.untokenize(values);
			return result;
		}

	};

	public static class DefaultExporterFactory extends ExporterFactory {

		@Override
		protected Exporter<HubCourse> create(String path) throws IOException {
			return new CourseExporter(path);
		}
	}

	static {
		ExporterFactory factory = new DefaultExporterFactory();
		ExporterFactory.factories.put(CourseExporter.class.getName(), factory);
	}

}