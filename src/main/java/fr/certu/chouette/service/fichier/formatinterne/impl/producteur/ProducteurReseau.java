package fr.certu.chouette.service.fichier.formatinterne.impl.producteur;

import fr.certu.chouette.echange.ILectureEchange;
import fr.certu.chouette.modele.Reseau;
import fr.certu.chouette.service.fichier.formatinterne.IFournisseurId;
import fr.certu.chouette.service.fichier.formatinterne.IGestionFichier;
import fr.certu.chouette.service.fichier.formatinterne.impl.IProducteurSpecifique;
import fr.certu.chouette.service.fichier.formatinterne.modele.IEtatDifference;
import fr.certu.chouette.service.identification.IIdentificationManager;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

public class ProducteurReseau implements IProducteurSpecifique {
	
	private IFournisseurId         fournisseurId;
	private IGestionFichier        gestionFichier;
	private IIdentificationManager identificationManager;
	
	public ProducteurReseau(final IIdentificationManager identificationManager, final IFournisseurId fournisseurId, final IGestionFichier gestionFichier) {
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
		Reseau reseau = echange.getReseau();
		Long idReseau = etatDifference.getIdReseauConnu();
		if (!etatDifference.isReseauConnu()) {
			idReseau = new Long(fournisseurId.getNouvelId(reseau.getObjectId()));
			reseau.setId(idReseau);
			List<String[]> contenu = traduire(majIdentification, reseau);
			gestionFichier.produire(contenu, gestionFichier.getCheminFichierReseau());
		}
		resultat.put(reseau.getObjectId(), idReseau);
		return resultat;
	}

	private List<String[]>  traduire(final boolean majIdentification, final Reseau reseau) {
		List<String[]> contenu = new ArrayList<String[]>();
		List<String> champs = new ArrayList<String>();
		String objectId = majIdentification ? identificationManager.getIdFonctionnel("PtNetwork", reseau): reseau.getObjectId();
		champs.add(reseau.getId().toString());
		champs.add(gestionFichier.getChamp(objectId));
		champs.add(gestionFichier.getChamp(reseau.getObjectVersion()));
		champs.add(gestionFichier.getChamp(reseau.getCreationTime()));
		champs.add(gestionFichier.getChamp(reseau.getCreatorId()));
		champs.add(gestionFichier.getChamp(reseau.getVersionDate()));
		champs.add(gestionFichier.getChamp(reseau.getDescription()));
		champs.add(gestionFichier.getChamp(reseau.getName()));
		champs.add(gestionFichier.getChamp(reseau.getRegistrationNumber()));
		champs.add(gestionFichier.getChamp(reseau.getSourceName()));
		champs.add(gestionFichier.getChamp(reseau.getSourceIdentifier()));
		champs.add(gestionFichier.getChamp(reseau.getComment()));
		contenu.add((String[])champs.toArray(new String[]{}));
		return contenu;
	}
}
