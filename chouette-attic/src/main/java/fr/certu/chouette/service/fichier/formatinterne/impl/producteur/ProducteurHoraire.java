package fr.certu.chouette.service.fichier.formatinterne.impl.producteur;

import fr.certu.chouette.echange.ILectureEchange;
import fr.certu.chouette.modele.Horaire;
import fr.certu.chouette.service.commun.CodeDetailIncident;
import fr.certu.chouette.service.commun.CodeIncident;
import fr.certu.chouette.service.commun.ServiceException;
import fr.certu.chouette.service.fichier.formatinterne.IFournisseurId;
import fr.certu.chouette.service.fichier.formatinterne.IGestionFichier;
import fr.certu.chouette.service.fichier.formatinterne.impl.IProducteurSpecifique;
import fr.certu.chouette.service.fichier.formatinterne.modele.IEtatDifference;
import fr.certu.chouette.service.identification.IIdentificationManager;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class ProducteurHoraire implements IProducteurSpecifique {
	
	private static final SimpleDateFormat       formatDate            = new SimpleDateFormat("HH:mm:ss");
	private              IFournisseurId         fournisseurId;
	private              IGestionFichier        gestionFichier;
	@SuppressWarnings("unused")
	private              IIdentificationManager identificationManager;
	
	public ProducteurHoraire(final IIdentificationManager identificationManager, final IFournisseurId fournisseurId, final IGestionFichier gestionFichier) {
		super();
		this.fournisseurId         = fournisseurId;
		this.gestionFichier        = gestionFichier;
		this.identificationManager = identificationManager;
	}
	
	public Map<String, Long> produire(final boolean majIdentification, final ILectureEchange echange, final IEtatDifference etatDifference, final Map<String, Long> idParObjectId) {
		return produire(majIdentification, echange, etatDifference, idParObjectId, false);
	}
	
	public Map<String, Long> produire(final boolean majIdentification, final ILectureEchange echange, final IEtatDifference etatDifference, final Map<String, Long> idParObjectId, boolean incremental) {
		List<Horaire> horaires = echange.getHoraires();
		List<Horaire> horairesNouveaux = new ArrayList<Horaire>();
		for (Horaire horaire : horaires) {
		    if (horaire.getVehicleJourneyId() == null || horaire.getStopPointId() == null)
			continue;
			String objectIdCourse = horaire.getVehicleJourneyId();
			String objectIdArret = horaire.getStopPointId();
			Long idCourse = getIdObligatoire(objectIdCourse, idParObjectId);
			Long idArret = getIdObligatoire(objectIdArret, idParObjectId);
			Long idHoraire = new Long(fournisseurId.getNouvelId(horaire.getVehicleJourneyId(), horaire.getStopPointId()));
			horaire.setId(idHoraire);
			horaire.setIdArret(idArret);
			horaire.setIdCourse(idCourse);
			if ((!incremental) || ((incremental) && (!etatDifference.isObjectIdCourseConnue(objectIdCourse))))
				horairesNouveaux.add(horaire);
		}
		int len = horairesNouveaux.size();
		int l1 = 0;
		int l2 = 1000;
		if (l2 > len)
			l2 = len;
		gestionFichier.produire(new ArrayList<String[]>(), gestionFichier.getCheminFichierHoraire(), false);
		while (l1 < l2) {
			List<String[]> contenu = traduire(majIdentification, horairesNouveaux, l1, l2);
			gestionFichier.produire(contenu, gestionFichier.getCheminFichierHoraire(), true);
			l1 += 1000;
			l2 += 1000;
			if (l2 > len)
				l2 = len;
		}			
		return null;
	}
	
	private Long getIdObligatoire(String objectId, final Map<String, Long> idParObjectId) {
		Long id = idParObjectId.get(objectId);
		if (id == null)
			throw new ServiceException(CodeIncident.IDENTIFIANT_TRIDENT_INCONNU, CodeDetailIncident.DEFAULT,objectId);
		return id;
	}

	private List<String[]> traduire(final boolean majIdentification, final List<Horaire> horaires, int l1, int l2) {
		List<String[]> contenu = new ArrayList<String[]>(horaires.size());
		for (int i = l1; i < l2; i++) {
			Horaire horaire = horaires.get(i);
			List<String> champs = new ArrayList<String>();
			champs.add(horaire.getId().toString());
			champs.add(horaire.getIdCourse().toString());
			champs.add(horaire.getIdArret().toString());
			champs.add(horaire.isModifie()?"t":"f");
			if (horaire.getArrivalTime() != null)
				champs.add(gestionFichier.getChamp(getFormatDate(horaire.getArrivalTime())));
			else
				champs.add(gestionFichier.getChamp(getFormatDate(horaire.getDepartureTime())));
			champs.add(gestionFichier.getChamp(getFormatDate(horaire.getDepartureTime())));
			champs.add(gestionFichier.getChamp(getFormatDate(horaire.getWaitingTime())));
			champs.add(gestionFichier.getChamp(horaire.getConnectingServiceId()));
			champs.add(gestionFichier.getChamp(horaire.getBoardingAlightingPossibility()));
			champs.add(horaire.getDepart()?"t":"f");
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
