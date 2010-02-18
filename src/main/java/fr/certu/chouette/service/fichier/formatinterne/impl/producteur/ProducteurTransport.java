package fr.certu.chouette.service.fichier.formatinterne.impl.producteur;

import fr.certu.chouette.echange.ILectureEchange;
import fr.certu.chouette.modele.Transporteur;
import fr.certu.chouette.service.fichier.formatinterne.IFournisseurId;
import fr.certu.chouette.service.fichier.formatinterne.IGestionFichier;
import fr.certu.chouette.service.fichier.formatinterne.impl.IProducteurSpecifique;
import fr.certu.chouette.service.fichier.formatinterne.modele.IEtatDifference;
import fr.certu.chouette.service.identification.IIdentificationManager;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

public class ProducteurTransport implements IProducteurSpecifique {
	
	private IFournisseurId         fournisseurId;
	private IGestionFichier        gestionFichier;
	private IIdentificationManager identificationManager;
	
	public ProducteurTransport(final IIdentificationManager identificationManager, final IFournisseurId fournisseurId, final IGestionFichier gestionFichier) {
		super();
		this.fournisseurId         = fournisseurId;
		this.gestionFichier        = gestionFichier;
		this.identificationManager = identificationManager;
	}
	
	public Map<String, Long> produire(final boolean majIdentification, final ILectureEchange echange, final IEtatDifference etatDifference, final Map<String, Long> idParObjectId) {
		return produire(majIdentification, echange, etatDifference, idParObjectId, false);
	}
	
	public Map<String, Long> produire(final boolean majIdentification, final ILectureEchange echange, final IEtatDifference etatDifference, final Map<String, Long> idParObjectId, boolean incremental) {
		Long idTransporteur = etatDifference.getIdTransporteurConnu();
		Transporteur transporteur = echange.getTransporteur();
		if (!etatDifference.isTransporteurConnu()) {
			idTransporteur = new Long(fournisseurId.getNouvelId(transporteur.getObjectId()));
			transporteur.setId(idTransporteur);
			List<String[]> contenu = traduire(majIdentification, transporteur);
			gestionFichier.produire(contenu, gestionFichier.getCheminFichierTransporteur());
		}
		Map<String, Long> resultat = new Hashtable<String, Long>();
		resultat.put(transporteur.getObjectId(), idTransporteur);
		return resultat;
	}
	
	private List<String[]> traduire(final boolean majIdentification, final Transporteur transporteur) {
		List<String[]> contenu = new ArrayList<String[]>();
		List<String> champs = new ArrayList<String>();
		String objectId = majIdentification ? identificationManager.getIdFonctionnel("Company", transporteur): transporteur.getObjectId();
		champs.add(transporteur.getId().toString());
		champs.add(gestionFichier.getChamp(objectId));
		champs.add(gestionFichier.getChamp(transporteur.getObjectVersion()));
		champs.add(gestionFichier.getChamp(transporteur.getCreationTime()));
		champs.add(gestionFichier.getChamp(transporteur.getCreatorId()));
		champs.add(gestionFichier.getChamp(transporteur.getName()));
		champs.add(gestionFichier.getChamp(transporteur.getShortName()));
		champs.add(gestionFichier.getChamp(transporteur.getOrganisationalUnit()));
		champs.add(gestionFichier.getChamp(transporteur.getOperatingDepartmentName()));
		champs.add(gestionFichier.getChamp(transporteur.getCode()));
		champs.add(gestionFichier.getChamp(transporteur.getPhone()));
		champs.add(gestionFichier.getChamp(transporteur.getFax()));
		champs.add(gestionFichier.getChamp(transporteur.getEmail()));
		champs.add(gestionFichier.getChamp(transporteur.getRegistrationNumber()));
		contenu.add((String[])champs.toArray(new String[]{}));
		return contenu;
	}
}
