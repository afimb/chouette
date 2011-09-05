package fr.certu.chouette.service.fichier.formatinterne.impl.producteur;

import fr.certu.chouette.echange.ILectureEchange;
import fr.certu.chouette.modele.Itineraire;
import fr.certu.chouette.service.fichier.formatinterne.IFournisseurId;
import fr.certu.chouette.service.fichier.formatinterne.IGestionFichier;
import fr.certu.chouette.service.fichier.formatinterne.impl.IProducteurSpecifique;
import fr.certu.chouette.service.fichier.formatinterne.modele.IEtatDifference;
import fr.certu.chouette.service.identification.IIdentificationManager;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

public class ProducteurItineraire implements IProducteurSpecifique {
	
	private IFournisseurId         fournisseurId;
	private IGestionFichier        gestionFichier;
	private IIdentificationManager identificationManager;
	
	public ProducteurItineraire(final IIdentificationManager identificationManager, final IFournisseurId fournisseurId, final IGestionFichier gestionFichier) {
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
		List<Itineraire> itineraires = echange.getItineraires();
		List<Itineraire> nouveauxItineraires = new ArrayList<Itineraire>();
		if (itineraires != null)
		for (Itineraire itineraire : itineraires) {
			String objectId = itineraire.getObjectId();
			Long idItineraire = resultat.get(objectId);
			if (idItineraire == null) {
				if ((!incremental) || ((incremental) && (!etatDifference.isObjectIdItineraireConnu(objectId)))) {
					idItineraire = new Long(fournisseurId.getNouvelId(objectId));
					nouveauxItineraires.add(itineraire);
				}
				else
					idItineraire = etatDifference.getIdItineraireConnu(objectId);
				resultat.put(itineraire.getObjectId(), idItineraire);
			}
			itineraire.setIdLigne(echange.getLigne().getId());
			itineraire.setId(idItineraire);
			String tridentIdRetour = itineraire.getChouetteRoute().getWayBackRouteId();
			if (tridentIdRetour != null) {
				Long idRetour = resultat.get(tridentIdRetour);
				if (idRetour == null) {
					if ((!incremental) || ((incremental) && (!etatDifference.isObjectIdItineraireConnu(tridentIdRetour)))) {
						idRetour = new Long(fournisseurId.getNouvelId(tridentIdRetour));
						for (Itineraire itineraireRetour : itineraires)
							if (itineraireRetour.getObjectId().equals(tridentIdRetour)) {
								nouveauxItineraires.add(itineraireRetour);
								break;
							}
					}
					else
						idRetour = etatDifference.getIdItineraireConnu(tridentIdRetour);
					resultat.put(tridentIdRetour, idRetour);
				}
				itineraire.setIdRetour(idRetour);
			}
		}
		List<String[]> contenu = null;
		contenu = traduire(majIdentification, nouveauxItineraires);
		gestionFichier.produire(contenu, gestionFichier.getCheminFichierItineraire());
		return resultat;
	}

	private List<String[]> traduire(final boolean majIdentification, final List<Itineraire> itineraires) {
		List<String[]> contenu = new ArrayList<String[]>();
		for (Itineraire itineraireUnitaire : itineraires) {
			List<String> champs = new ArrayList<String>();
			String objectId = majIdentification ? identificationManager.getIdFonctionnel("Route", itineraireUnitaire) : itineraireUnitaire.getObjectId();
			champs.add(itineraireUnitaire.getId().toString());
			champs.add(gestionFichier.getChamp(itineraireUnitaire.getIdRetour()));
			champs.add(itineraireUnitaire.getIdLigne().toString());
			champs.add(gestionFichier.getChamp(objectId));
			champs.add(gestionFichier.getChamp(itineraireUnitaire.getObjectVersion()));
			champs.add(gestionFichier.getChamp(itineraireUnitaire.getCreationTime()));
			champs.add(gestionFichier.getChamp(itineraireUnitaire.getCreatorId()));
			champs.add(gestionFichier.getChamp(itineraireUnitaire.getName()));
			champs.add(gestionFichier.getChamp(itineraireUnitaire.getPublishedName()));
			champs.add(gestionFichier.getChamp(itineraireUnitaire.getNumber()));
			champs.add(gestionFichier.getChamp(itineraireUnitaire.getDirection()));
			champs.add(gestionFichier.getChamp(itineraireUnitaire.getComment()));
			champs.add(gestionFichier.getChamp(itineraireUnitaire.getWayBack()));
			contenu.add((String[])champs.toArray(new String[]{}));
		}
		return contenu;
	}
}
