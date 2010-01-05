package fr.certu.chouette.service.fichier.formatinterne.impl.producteur;

import fr.certu.chouette.echange.ILectureEchange;
import fr.certu.chouette.modele.ArretItineraire;
import fr.certu.chouette.service.fichier.formatinterne.IFournisseurId;
import fr.certu.chouette.service.fichier.formatinterne.IGestionFichier;
import fr.certu.chouette.service.fichier.formatinterne.impl.IProducteurSpecifique;
import fr.certu.chouette.service.fichier.formatinterne.modele.IEtatDifference;
import fr.certu.chouette.service.identification.IIdentificationManager;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

public class ProducteurArretItineraire implements IProducteurSpecifique {
	
	private IFournisseurId         fournisseurId;
	private IGestionFichier        gestionFichier;
	private IIdentificationManager identificationManager;

	public ProducteurArretItineraire(final IIdentificationManager identificationManager, final IFournisseurId fournisseurId, final IGestionFichier gestionFichier) {
		super();
		this.fournisseurId         = fournisseurId;
		this.gestionFichier        = gestionFichier;
		this.identificationManager = identificationManager;
	}
	
	public Map<String, Long> produire(final boolean majIdentification, final ILectureEchange echange, final IEtatDifference etatDifference, final Map<String, Long> idParObjectId)  {
		return produire(majIdentification, echange, etatDifference, idParObjectId, false);
	}
	
	public Map<String, Long> produire(final boolean majIdentification, final ILectureEchange echange, final IEtatDifference etatDifference, final Map<String, Long> idParObjectId, boolean incremental) {
		Map<String, Long> resultat = new Hashtable<String, Long>();
		List<ArretItineraire> arrets = echange.getArrets();
		List<ArretItineraire> arretsNouveaux = new ArrayList<ArretItineraire>();
		for (ArretItineraire arret : arrets) {
			String arretObjectId = arret.getObjectId();
			String physiqueObjectId = arret.getContainedIn();
			String itineraireObjectId = echange.getItineraireArret(arretObjectId);
			Long idItineraire = idParObjectId.get(itineraireObjectId);
			Long idPhysique = idParObjectId.get(physiqueObjectId);
			Long idArret = null;
			if ((!incremental) || ((incremental) && (!etatDifference.isObjectIdArretConnu(arretObjectId)))) {
				idArret = new Long(fournisseurId.getNouvelId(arretObjectId));
				arretsNouveaux.add(arret);
			}
			else
				idArret = etatDifference.getIdArretConnu(arretObjectId);
			arret.setId(idArret);
			arret.setIdItineraire(idItineraire);
			arret.setIdPhysique(idPhysique);
			resultat.put(arretObjectId, idArret);
		}
		List<String[]> contenu = traduire(majIdentification, arretsNouveaux);
		gestionFichier.produire(contenu, gestionFichier.getCheminFichierArretLogique());
		return resultat;
	}

	private List<String[]> traduire(final boolean majIdentification, final List<ArretItineraire> arrets) {
		List<String[]> contenu = new ArrayList<String[]>();
		for (ArretItineraire arret : arrets) {
			List<String> champs = new ArrayList<String>();
			String objectId = majIdentification ? identificationManager.getIdFonctionnel("StopPoint", arret) : arret.getObjectId();
			champs.add(arret.getId().toString());
			champs.add(arret.getIdItineraire().toString());
			champs.add(gestionFichier.getChamp(arret.getIdPhysique()));
			champs.add(arret.isModifie() ? "t" : "f");
			champs.add(String.valueOf(arret.getPosition()));
			champs.add(gestionFichier.getChamp(objectId));
			champs.add(gestionFichier.getChamp(arret.getObjectVersion()));
			champs.add(gestionFichier.getChamp(arret.getCreationTime()));
			champs.add(gestionFichier.getChamp(arret.getCreatorId()));
			contenu.add((String[])champs.toArray(new String[]{}));
		}
		return contenu;
	}
}
