package fr.certu.chouette.service.fichier.formatinterne.impl.producteur;

import fr.certu.chouette.echange.ILectureEchange;
import fr.certu.chouette.modele.Correspondance;
import fr.certu.chouette.service.fichier.formatinterne.IFournisseurId;
import fr.certu.chouette.service.fichier.formatinterne.IGestionFichier;
import fr.certu.chouette.service.fichier.formatinterne.impl.IProducteurSpecifique;
import fr.certu.chouette.service.fichier.formatinterne.modele.IEtatDifference;
import fr.certu.chouette.service.identification.IIdentificationManager;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

public class ProducteurCorrespondance implements IProducteurSpecifique {
	
	private static final SimpleDateFormat       formatDate            = new SimpleDateFormat("HH:mm:ss");
	private              IFournisseurId         fournisseurId;
	private              IGestionFichier        gestionFichier;
	private              IIdentificationManager identificationManager;
	
	public ProducteurCorrespondance(final IIdentificationManager identificationManager, final IFournisseurId fournisseurId, final IGestionFichier gestionFichier) {
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
		List<Correspondance> correspondances = echange.getCorrespondances();
		List<Correspondance> correspondancesNouvelles = new ArrayList<Correspondance>();
		for (Correspondance correspondance : correspondances) {
			String departObjectId = correspondance.getStartOfLink();
			String arriveeObjectId = correspondance.getEndOfLink();
			Long idDepart = getIdZoneGenerique(departObjectId, etatDifference, idParObjectId);
			Long idArrivee = getIdZoneGenerique(arriveeObjectId, etatDifference, idParObjectId);
			if ((idDepart != null) && (idArrivee != null)) {
				String objectId = correspondance.getObjectId();
				Long idCorrespondance = null;
				if (etatDifference.isObjectIdCorrespondanceConnue(objectId))
					idCorrespondance = etatDifference.getIdCorrespondanceConnue(objectId);
				else {
					idCorrespondance = new Long(fournisseurId.getNouvelId(objectId));
					correspondancesNouvelles.add(correspondance);
				}
				correspondance.setId(idCorrespondance);
				correspondance.setIdArrivee(idArrivee);
				correspondance.setIdDepart(idDepart);
				resultat.put(objectId, idCorrespondance);
			}
		}
		List<String[]> contenu = traduire(majIdentification, correspondancesNouvelles);
		gestionFichier.produire(contenu, gestionFichier.getCheminFichierCorrespondance());
		return resultat;
	}
	
	private Long getIdZoneGenerique(String objectId, final IEtatDifference etatDifference, final Map<String, Long> idParObjectId) {
		Long idZoneGenerique = idParObjectId.get(objectId);
		if (idZoneGenerique == null)
			idZoneGenerique = etatDifference.getIdZoneGeneriqueConnue(objectId);
		return idZoneGenerique;
	}

	private List<String[]> traduire(final boolean majIdentification, final List<Correspondance> correspondances) {
		List<String[]> contenu = new ArrayList<String[]>();
		for (Correspondance correspondance : correspondances) {
			List<String> champs = new ArrayList<String>();
			String objectId = majIdentification ? identificationManager.getIdFonctionnel("ConnectionLink", correspondance) : correspondance.getObjectId();
			champs.add(correspondance.getId().toString());
			champs.add(gestionFichier.getChamp(correspondance.getIdDepart()));
			champs.add(gestionFichier.getChamp(correspondance.getIdArrivee()));
			champs.add(gestionFichier.getChamp(objectId));
			champs.add(gestionFichier.getChamp(correspondance.getObjectVersion()));
			champs.add(gestionFichier.getChamp(correspondance.getCreationTime()));
			champs.add(gestionFichier.getChamp(correspondance.getCreatorId()));
			champs.add(gestionFichier.getChamp(correspondance.getName()));
			champs.add(gestionFichier.getChamp(correspondance.getComment()));
			champs.add(gestionFichier.getChamp(correspondance.getLinkDistance()));
			champs.add(gestionFichier.getChamp(correspondance.getLinkType()));
			champs.add(gestionFichier.getChamp(getFormatDate(correspondance.getDefaultDuration())));
			champs.add(gestionFichier.getChamp(getFormatDate(correspondance.getFrequentTravellerDuration())));
			champs.add(gestionFichier.getChamp(getFormatDate(correspondance.getOccasionalTravellerDuration())));
			champs.add(gestionFichier.getChamp(getFormatDate(correspondance.getMobilityRestrictedTravellerDuration())));
			champs.add(gestionFichier.getChamp(correspondance.getMobilityRestrictedSuitability()));
			champs.add(gestionFichier.getChamp(correspondance.getStairsAvailability()));
			champs.add(gestionFichier.getChamp(correspondance.getLiftAvailability()));
			contenu.add((String[])champs.toArray(new String[]{}));
		}
		return contenu;
	}
	
	private String getFormatDate(Date date) {
		if (date == null)
			return null;
		return formatDate.format(date);
	}
}
