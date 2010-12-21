package fr.certu.chouette.service.importateur.monoitineraire.csv.impl;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;

import fr.certu.chouette.dao.IModificationSpecifique;
import fr.certu.chouette.service.commun.CodeDetailIncident;
import fr.certu.chouette.service.commun.CodeIncident;
import fr.certu.chouette.service.commun.ServiceException;
import fr.certu.chouette.service.database.ICourseManager;
import fr.certu.chouette.service.database.IHoraireManager;
import fr.certu.chouette.service.database.impl.modele.EtatMajHoraire;
import fr.certu.chouette.service.importateur.monoitineraire.csv.IImportHorairesManager;

public class ImportHorairesManager implements IImportHorairesManager {

	private static SimpleDateFormat dateFormat = new SimpleDateFormat("H:m:s");
	
	private ICourseManager courseManager;
	private IHoraireManager horaireManager;
	private IModificationSpecifique modificationSpecifique;

	private class CourseImportee{
		private long idCourse;
		private LinkedHashMap<Long, String> horairesArrivee = new LinkedHashMap<Long, String>();
		private LinkedHashMap<Long, String> horairesDepart = new LinkedHashMap<Long, String>();

		
		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder();
			sb.append("idCourse : ").append(idCourse).append("\n");
			Iterator<Entry<Long, String>> horairesArriveeIt = horairesArrivee.entrySet().iterator();
			Iterator<Entry<Long, String>> horairesDepartIt = horairesDepart.entrySet().iterator();
			while(horairesArriveeIt.hasNext() && horairesDepartIt.hasNext()){
				Entry<Long, String> horaireDepart = horairesDepartIt.next();
				Entry<Long, String> horaireArrivee = horairesArriveeIt.next();
				sb.append(horaireDepart.getKey()).append(" : ").append(horaireDepart.getValue()).append(", ").append(horaireArrivee.getValue()).append("\n");
			}
			return sb.toString();
		}
		
		public List<EtatMajHoraire> creerEtatsMajHoraire(){
			List<EtatMajHoraire> etatMajHoraires = new ArrayList<EtatMajHoraire>();
			
			Iterator<Entry<Long, String>> horairesArriveeIt = horairesArrivee.entrySet().iterator();
			Iterator<Entry<Long, String>> horairesDepartIt = horairesDepart.entrySet().iterator();
			while(horairesArriveeIt.hasNext() && horairesDepartIt.hasNext()){
				Entry<Long, String> horaireDepart = horairesDepartIt.next();
				Entry<Long, String> horaireArrivee = horairesArriveeIt.next();
				if(!horaireArrivee.getValue().equals("") && !horaireDepart.getValue().equals("")){
					Date dateArrivee;
					Date dateDepart;
					try {
						dateArrivee = dateFormat.parse(horaireArrivee.getValue());
						dateDepart = dateFormat.parse(horaireDepart.getValue());
					} catch (ParseException e) {
						throw new ServiceException(CodeIncident.ERR_CSV_FORMAT_INVALIDE,CodeDetailIncident.VEHICLEJOURNEYATSTOP,e);
					}
					EtatMajHoraire etatMajHoraire = EtatMajHoraire.getCreation(horaireDepart.getKey(), idCourse, dateDepart, dateArrivee);
					etatMajHoraires.add(etatMajHoraire);
				}
			}
			return etatMajHoraires;
		}
	}
	/* (non-Javadoc)
	 * @see fr.certu.chouette.service.importateur.monoitineraire.csv.impl.IImportHorairesManager#importer(java.util.List)
	 */
	public void importer(List<String[]> donnees){
		int nbColsArrets = 2;
		int nbLignesTitres = 2;
		int nbCols = donnees.get(0).length;
		int nbLignes = donnees.size();
		long idItineraire = Long.parseLong(donnees.get(0)[0]);
		
		List<CourseImportee> coursesImportees = new ArrayList<CourseImportee>();
		for(int i = nbColsArrets; i < nbCols-1 ; i+=2){
			CourseImportee courseImportee = new CourseImportee();
			courseImportee.idCourse = Long.parseLong(donnees.get(0)[i]);
			for(int j = nbLignesTitres ; j < nbLignes ; j++){
				Long idArret = Long.parseLong(donnees.get(j)[1]);
				String horaireArrivee = donnees.get(j)[i];
				String horaireDepart = donnees.get(j)[i+1];
				courseImportee.horairesArrivee.put(idArret, horaireArrivee);
				courseImportee.horairesDepart.put(idArret, horaireDepart);
			}
			coursesImportees.add(courseImportee);
		}
		
		modificationSpecifique.supprimerHorairesItineraire(idItineraire);
		
		List<EtatMajHoraire> etatMajHorairesItineraire = new ArrayList<EtatMajHoraire>();
		for(CourseImportee courseImportee : coursesImportees){
			List<EtatMajHoraire> etatMajHorairesCourse = courseImportee.creerEtatsMajHoraire();
			etatMajHorairesItineraire.addAll(etatMajHorairesCourse);
		}
		horaireManager.modifier(etatMajHorairesItineraire);
		
	}
	
	public ICourseManager getCourseManager() {
		return courseManager;
	}
	
	public void setCourseManager(ICourseManager courseManager) {
		this.courseManager = courseManager;
	}

	public IHoraireManager getHoraireManager() {
		return horaireManager;
	}

	public void setHoraireManager(IHoraireManager horaireManager) {
		this.horaireManager = horaireManager;
	}

	public IModificationSpecifique getModificationSpecifique() {
		return modificationSpecifique;
	}

	public void setModificationSpecifique(
			IModificationSpecifique modificationSpecifique) {
		this.modificationSpecifique = modificationSpecifique;
	}
	
	
	
}
