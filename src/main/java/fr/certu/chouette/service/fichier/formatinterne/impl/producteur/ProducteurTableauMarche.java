package fr.certu.chouette.service.fichier.formatinterne.impl.producteur;

import fr.certu.chouette.echange.ILectureEchange;
import fr.certu.chouette.modele.LienTMCourse;
import fr.certu.chouette.modele.Periode;
import fr.certu.chouette.modele.TableauMarche;
import fr.certu.chouette.service.commun.CodeDetailIncident;
import fr.certu.chouette.service.commun.CodeIncident;
import fr.certu.chouette.service.commun.ServiceException;
import fr.certu.chouette.service.fichier.formatinterne.IFournisseurId;
import fr.certu.chouette.service.fichier.formatinterne.IGestionFichier;
import fr.certu.chouette.service.fichier.formatinterne.impl.IProducteurSpecifique;
import fr.certu.chouette.service.fichier.formatinterne.modele.IEtatDifference;
import fr.certu.chouette.service.identification.IIdentificationManager;
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

public class ProducteurTableauMarche implements IProducteurSpecifique {
	
	private static final Logger    logger         = Logger.getLogger(ProducteurTableauMarche.class);
	private IFournisseurId         fournisseurId;
	private IGestionFichier        gestionFichier;
	private IIdentificationManager identificationManager;
	
	public ProducteurTableauMarche(final IIdentificationManager identificationManager, final IFournisseurId fournisseurId, final IGestionFichier gestionFichier) {
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
		List<LienTMCourse> liensTMCourse = new ArrayList<LienTMCourse>();
		List<TableauMarche> tableauxMarche = echange.getTableauxMarche();
		List<TableauMarche> nvTableauxMarche = new ArrayList<TableauMarche>();
		for (TableauMarche marche : tableauxMarche) {
			String objectIdTM = marche.getObjectId();
			if (!etatDifference.isObjectIdTableauMarcheConnu(objectIdTM)) {
				Long idTableauMarche = new Long(fournisseurId.getNouvelId(objectIdTM));
				marche.setId(idTableauMarche);
				resultat.put(objectIdTM, idTableauMarche);
				nvTableauxMarche.add(marche);
			}
			int totalTMCourse = marche.getVehicleJourneyIdCount();
			for (int i = 0; i < totalTMCourse; i++) {
				String objectIdCourse = marche.getVehicleJourneyId(i);
				if (objectIdCourse == null)
					throw new ServiceException(CodeIncident.DONNEE_INVALIDE, CodeDetailIncident.TIMETABLE_MISSINGVEHICLEJOURNEY,marche.getObjectId(),i);
				Long idCourse = idParObjectId.get(objectIdCourse);
				if (idCourse == null) // idCourse d'une autre ligne que celle ci
					continue;
				Long idLien = new Long(fournisseurId.getNouvelId(objectIdCourse, objectIdTM));
				Long idTM = null;
				if (etatDifference.isObjectIdTableauMarcheConnu(objectIdTM))
					idTM = etatDifference.getIdTableauMarcheConnu(objectIdTM);
				else
					idTM = resultat.get(objectIdTM);
				LienTMCourse lienTMCourse = new LienTMCourse();
				lienTMCourse.setId(idLien);
				lienTMCourse.setIdCourse(idCourse);
				lienTMCourse.setIdTableauMarche(idTM);
				liensTMCourse.add(lienTMCourse);
			}
		}
		List<String[]> contenuNouveauxTM = traduireTM(majIdentification, nvTableauxMarche);
		gestionFichier.produire(contenuNouveauxTM, gestionFichier.getCheminFichierTableauMarche());
		List<String[]> contenuNouveauxTMCalendrier = traduireTMCalendrier(nvTableauxMarche);
		gestionFichier.produire(contenuNouveauxTMCalendrier, gestionFichier.getCheminFichierTableauMarcheCalendrier());
		List<String[]> contenuNouveauxTMPeriode = traduireTMPeriode(nvTableauxMarche);
		gestionFichier.produire(contenuNouveauxTMPeriode, gestionFichier.getCheminFichierTableauMarchePeriode());
		List<String[]> contenuLienCourseTM = traduireLienTMCourse(liensTMCourse);
		gestionFichier.produire(contenuLienCourseTM, gestionFichier.getCheminFichierTableauMarcheCourse());
		return resultat;
	}
	
	private List<String[]> traduireLienTMCourse(List<LienTMCourse> liensTMCourse) {
		List<String[]> contenu = new ArrayList<String[]>();
		for (LienTMCourse lien : liensTMCourse) {
			String[] champs = new String[ 3];
			int indice = 0;
			champs[indice++] = lien.getId().toString();
			champs[indice++] = lien.getIdTableauMarche().toString();
			champs[indice++] = lien.getIdCourse().toString();
			contenu.add(champs);
		}
		return contenu;
	}
	
	private List<String[]> traduireTMPeriode(List<TableauMarche> tableauxMarche) {
		List<String[]> contenu = new ArrayList<String[]>();
		for (TableauMarche tableauMarche : tableauxMarche) {
			String idTM = tableauMarche.getId().toString();
			List<Periode> periodes = tableauMarche.getPeriodes();
			int totalPeriode = tableauMarche.getTotalPeriodes();
			for (int i = 0; i < totalPeriode; i++) {
				String[] champs = new String[ 4];
				int indice = 0;
				champs[indice++] = idTM;
				champs[indice++] = gestionFichier.getChamp(periodes.get(i).getDebut());
				champs[indice++] = gestionFichier.getChamp(periodes.get(i).getFin());
				champs[indice++] = String.valueOf(i);
				contenu.add(champs);
			}
		}
		return contenu;
	}
	
	private List<String[]> traduireTMCalendrier(List<TableauMarche> tableauxMarche) {
		List<String[]> contenu = new ArrayList<String[]>();
		for (TableauMarche tableauMarche : tableauxMarche) {
			String idTM = tableauMarche.getId().toString();
			List<Date> dates = tableauMarche.getDates();
			int totalCalendrier = tableauMarche.getTotalDates();
			for (int i = 0; i < totalCalendrier; i++) {
				String[] champs = new String[ 3];
				int indice = 0;
				champs[indice++] = idTM;
				champs[indice++] = gestionFichier.getChamp(dates.get(i));
				champs[indice++] = String.valueOf(i);
				contenu.add(champs);
			}
		}
		return contenu;
	}
	
	private List<String[]> traduireTM(final boolean majIdentification, final List<TableauMarche> tableauxMarche) {
		List<String[]> contenu = new ArrayList<String[]>();
		for (TableauMarche tableauMarche : tableauxMarche) {
			List<String> champs = new ArrayList<String>();
			String objectId = majIdentification ? identificationManager.getIdFonctionnel("Timetable", tableauMarche) : tableauMarche.getObjectId();
			champs.add(tableauMarche.getId().toString());
			champs.add(gestionFichier.getChamp(objectId));
			champs.add(gestionFichier.getChamp(tableauMarche.getObjectVersion()));
			champs.add(gestionFichier.getChamp(tableauMarche.getCreationTime()));
			champs.add(gestionFichier.getChamp(tableauMarche.getCreatorId()));
			champs.add(gestionFichier.getChamp(tableauMarche.getVersion()));
			champs.add(gestionFichier.getChamp(tableauMarche.getComment()));
			champs.add(gestionFichier.getChamp(tableauMarche.getIntDayTypes()));
			contenu.add((String[])champs.toArray(new String[]{}));
			if (majIdentification)
				identificationManager.getDictionaryObjectId().addObjectIdParReference(tableauMarche.getComment(), objectId);
		}
		return contenu;
	}
}
