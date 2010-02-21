package fr.certu.chouette.service.fichier.formatinterne.impl.producteur;

import fr.certu.chouette.echange.ILectureEchange;
import fr.certu.chouette.modele.Ligne;
import fr.certu.chouette.service.fichier.formatinterne.IFournisseurId;
import fr.certu.chouette.service.fichier.formatinterne.IGestionFichier;
import fr.certu.chouette.service.fichier.formatinterne.impl.IProducteurSpecifique;
import fr.certu.chouette.service.fichier.formatinterne.modele.IEtatDifference;
import fr.certu.chouette.service.identification.IIdentificationManager;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

public class ProducteurLigne implements IProducteurSpecifique {
	
	private IFournisseurId         fournisseurId;
	private IGestionFichier        gestionFichier;
	private IIdentificationManager identificationManager;
	
	public ProducteurLigne(final IIdentificationManager identificationManager, final IFournisseurId fournisseurId, final IGestionFichier gestionFichier) {
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
		Ligne ligne = echange.getLigne();
		Long idLigne = etatDifference.getIdLigneConnue();
		if (!etatDifference.isLigneConnue())
			idLigne = new Long(fournisseurId.getNouvelId(ligne.getObjectId()));
		ligne.setId(idLigne);
		if ((!incremental) || (incremental && !etatDifference.isLigneConnue())) {
			List<String[]> contenu = traduire(majIdentification, ligne);
			gestionFichier.produire(contenu, gestionFichier.getCheminFichierLigne());
		}
		resultat.put(ligne.getObjectId(), idLigne);
		return resultat;
	}
	
	private List<String[]> traduire(final boolean majIdentification, final Ligne ligne) {
		List<String> champs = new ArrayList<String>();
		String objectId = majIdentification ? identificationManager.getIdFonctionnel("Line", ligne) : ligne.getObjectId();
		champs.add(ligne.getId().toString());
		champs.add(gestionFichier.getChamp(ligne.getIdReseau()));
		champs.add(gestionFichier.getChamp(ligne.getIdTransporteur()));
		champs.add(gestionFichier.getChamp(objectId));
		champs.add(gestionFichier.getChamp(ligne.getObjectVersion()));
		champs.add(gestionFichier.getChamp(ligne.getCreationTime()));
		champs.add(gestionFichier.getChamp(ligne.getCreatorId()));
		champs.add(gestionFichier.getChamp(ligne.getName()));
		champs.add(gestionFichier.getChamp(ligne.getNumber()));
		champs.add(gestionFichier.getChamp(ligne.getPublishedName()));
		champs.add(gestionFichier.getChamp(ligne.getTransportModeName()));
		champs.add(gestionFichier.getChamp(ligne.getRegistrationNumber()));
		champs.add(gestionFichier.getChamp(ligne.getComment()));
		List<String[]> contenu = new ArrayList<String[]>();
		contenu.add((String[])champs.toArray(new String[]{}));
		return contenu;
	}
}
