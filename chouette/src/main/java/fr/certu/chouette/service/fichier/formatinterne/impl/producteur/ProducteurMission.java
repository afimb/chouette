package fr.certu.chouette.service.fichier.formatinterne.impl.producteur;

import fr.certu.chouette.echange.ILectureEchange;
import fr.certu.chouette.modele.Mission;
import fr.certu.chouette.service.fichier.formatinterne.IFournisseurId;
import fr.certu.chouette.service.fichier.formatinterne.IGestionFichier;
import fr.certu.chouette.service.fichier.formatinterne.impl.IProducteurSpecifique;
import fr.certu.chouette.service.fichier.formatinterne.modele.IEtatDifference;
import fr.certu.chouette.service.identification.IIdentificationManager;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

public class ProducteurMission implements IProducteurSpecifique {
	
	private IFournisseurId         fournisseurId;
	private IGestionFichier        gestionFichier;
	private IIdentificationManager identificationManager;
	
	public ProducteurMission(final IIdentificationManager identificationManager, final IFournisseurId fournisseurId, final IGestionFichier gestionFichier) {
		super();
		this.fournisseurId         = fournisseurId;
		this.gestionFichier        = gestionFichier;
		this.identificationManager = identificationManager;
	}
	
	public Map<String, Long> produire(final boolean majIdentification, final ILectureEchange echange, final IEtatDifference etatDifference, final Map<String, Long> idParObjectId) {
		return produire(majIdentification, echange, etatDifference, idParObjectId, false);
	}
	
	public Map<String, Long> produire(final boolean majIdentification, final ILectureEchange echange, final IEtatDifference etatDifference, final Map<String, Long> idParObjectId, boolean incremental) {
		Map<String, Long> resultat = new Hashtable<String, Long>();
		List<Mission> missions = echange.getMissions();
		List<Mission> missionsNouvelles = new ArrayList<Mission>();
		for (Mission mission : missions) {
			String missionObjectId = mission.getObjectId();
			Long idMission = null;
			if ((!incremental) || ((incremental) && (!etatDifference.isObjectIdMissionConnue(missionObjectId)))) {
				idMission = new Long(fournisseurId.getNouvelId(missionObjectId));
				missionsNouvelles.add(mission);
			}
			else
				idMission = etatDifference.getIdMissionConnue(missionObjectId);
			mission.setId(idMission);
			resultat.put(missionObjectId, idMission);
		}
		List<String[]> contenu = traduire(majIdentification, missionsNouvelles);
		gestionFichier.produire(contenu, gestionFichier.getCheminFichierMission());
		return resultat;
	}
	
	private List<String[]> traduire(final boolean majIdentification, final List<Mission> missions) {
		List<String[]> contenu = new ArrayList<String[]>(missions.size());
		for (Mission mission : missions) {
			List<String> champs = new ArrayList<String>();
			String objectId = majIdentification ? identificationManager.getIdFonctionnel("JourneyPattern", mission) : mission.getObjectId();
			champs.add(mission.getId().toString());
			champs.add(gestionFichier.getChamp(objectId));
			champs.add(gestionFichier.getChamp(mission.getObjectVersion()));
			champs.add(gestionFichier.getChamp(mission.getCreationTime()));
			champs.add(gestionFichier.getChamp(mission.getCreatorId()));
			champs.add(gestionFichier.getChamp(mission.getRegistrationNumber()));
			champs.add(gestionFichier.getChamp(mission.getName()));
			champs.add(gestionFichier.getChamp(mission.getPublishedName()));
			champs.add(gestionFichier.getChamp(mission.getComment()));
			contenu.add((String[])champs.toArray(new String[]{}));
		}
		return contenu;
	}
}
