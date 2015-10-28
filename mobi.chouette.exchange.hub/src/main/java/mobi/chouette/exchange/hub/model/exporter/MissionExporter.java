package mobi.chouette.exchange.hub.model.exporter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import mobi.chouette.exchange.hub.model.HubMission;

public class MissionExporter extends ExporterImpl<HubMission> implements HubConverter{

	public static enum FIELDS {
		numero,
		code_arret_depart,
		heure_depart,
		code_arret_arrivee,
		heure_arrivee,
		categorie,
		service_voiture,
		service_agent,
		validite,
		forfaitise,
		temps_paye,
		code_ligne,
		code_chemin,
		nom,
		distance,
		type_materiel,
		nature,
		commentaire,
		type_materiel_utilise,
		graphique,
		identifiant_arret_depart,
		identifiant_arret_arrivee,
		code_periode,
		identifiant;
	};

	public static final String FILENAME = "MISSION.TXT";

	public MissionExporter(String path) throws IOException {
		super(path);
	}

	@Override
	public void writeHeader() throws IOException {
		write("MISSION");
	}

	@Override
	public void export(HubMission bean) throws IOException {
		write(CONVERTER.to(_context, bean));
	}

	public static Converter<String, HubMission> CONVERTER = new Converter<String, HubMission>() {


		@Override
		public String to(Context context, HubMission input) {
			String result = null;
			List<String> values = new ArrayList<String>();
			
			values.add(NUMBER_CONVERTER.to(context, FIELDS.numero,                    input.getNumero(), true));
			values.add(STRING_CONVERTER.to(context, FIELDS.code_arret_depart,         input.getCodeArretDepart(), true));
			values.add(NUMBER_CONVERTER.to(context, FIELDS.heure_depart,              input.getHeureDepart(), true));
			values.add(STRING_CONVERTER.to(context, FIELDS.code_arret_arrivee,        input.getCodeArretArrivee(), true));
			values.add(NUMBER_CONVERTER.to(context, FIELDS.heure_arrivee,             input.getHeureArrivee(), true));
			values.add(NUMBER_CONVERTER.to(context, FIELDS.categorie,                 input.getCategorie(), true));
			values.add(NUMBER_CONVERTER.to(context, FIELDS.service_voiture,           input.getServiceVoiture(), false));
			values.add(NUMBER_CONVERTER.to(context, FIELDS.service_agent,             input.getServiceAgent(), false));//true));
			values.add(NUMBER_CONVERTER.to(context, FIELDS.validite,                  input.getValidite(), true));
			values.add(STRING_CONVERTER.to(context, FIELDS.forfaitise,                input.getForfaitise(), true));
			values.add(NUMBER_CONVERTER.to(context, FIELDS.temps_paye,                input.getTempsPaye(), false));//true)); 
			values.add(STRING_CONVERTER.to(context, FIELDS.code_ligne,                input.getCodeLigne(), true));
			values.add(STRING_CONVERTER.to(context, FIELDS.code_chemin,               input.getCodeChemin(), false));
			values.add(STRING_CONVERTER.to(context, FIELDS.nom,                       input.getNom(), false));
			values.add(NUMBER_CONVERTER.to(context, FIELDS.distance,                  input.getDistance(), false));
			values.add(STRING_CONVERTER.to(context, FIELDS.type_materiel,             input.getTypeMateriel(), false));
			values.add(STRING_CONVERTER.to(context, FIELDS.nature,                    input.getNature(), false));
			values.add(STRING_CONVERTER.to(context, FIELDS.commentaire,               input.getCommentaire(), false));
			values.add(STRING_CONVERTER.to(context, FIELDS.type_materiel_utilise,     input.getTypeMaterielUtilise(), false));
			values.add(STRING_CONVERTER.to(context, FIELDS.graphique,                 input.getGraphique(), false));
			values.add(NUMBER_CONVERTER.to(context, FIELDS.identifiant_arret_depart,  input.getIdentifiantArretDepart(), false));
			values.add(NUMBER_CONVERTER.to(context, FIELDS.identifiant_arret_arrivee, input.getIdentifiantArretArrivee(), false));
			StringBuffer list = new StringBuffer();
			for (String code : input.getCodesPeriode()) {
				list.append("|");
				list.append(STRING_CONVERTER.to(context, FIELDS.code_periode, code, true));
			}
			if (list.length() > 0)
				list.deleteCharAt(0);
			values.add(list.toString());
			values.add(NUMBER_CONVERTER.to(context, FIELDS.identifiant,               input.getIdentifiant(), false));
			
			result = Tokenizer.untokenize(values);
			return result;
		}

	};

	public static class DefaultExporterFactory extends ExporterFactory {

		@Override
		protected Exporter<HubMission> create(String path) throws IOException {
			return new MissionExporter(path);
		}
	}

	static {
		ExporterFactory factory = new DefaultExporterFactory();
		ExporterFactory.factories.put(MissionExporter.class.getName(), factory);
	}
}
