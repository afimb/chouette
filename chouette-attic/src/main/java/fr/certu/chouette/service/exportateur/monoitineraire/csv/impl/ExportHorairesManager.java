package fr.certu.chouette.service.exportateur.monoitineraire.csv.impl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fr.certu.chouette.modele.ArretItineraire;
import fr.certu.chouette.modele.Course;
import fr.certu.chouette.modele.Horaire;
import fr.certu.chouette.modele.PositionGeographique;
import fr.certu.chouette.service.database.IArretItineraireManager;
import fr.certu.chouette.service.database.IItineraireManager;
import fr.certu.chouette.service.exportateur.monoitineraire.csv.IExportHorairesManager;


public class ExportHorairesManager implements IExportHorairesManager{
	
	private IItineraireManager itineraireManager;
	private IArretItineraireManager arretItineraireManager;
	
	private static SimpleDateFormat dateFormat = new SimpleDateFormat("H:m:s");
	
	public List<String[]> exporter(Long idItineraire){
		List<Horaire> horaires = this.itineraireManager.getHorairesItineraire(idItineraire);
		List<ArretItineraire> arrets = this.itineraireManager.getArretsItineraire(idItineraire);
		List<Course> courses = this.itineraireManager.getCoursesItineraire(idItineraire);
		List<Long> idArrets = new ArrayList<Long>(); 
		List<Long> idCourses = new ArrayList<Long>();
		List<String[]> exports = new ArrayList<String[]>();
		
		int nbColsArrets = 2;
		int nbLignesTitres = 2;
		int nbCols = 2*courses.size() + nbColsArrets;
		
		String[] titresCourses = new String[nbCols];
		titresCourses[0]=idItineraire.toString();
		for (int i = 0; i < courses.size(); i++) {
			Course course = courses.get(i);
			idCourses.add(course.getId());
			titresCourses[nbColsArrets+2*i] = course.getId().toString();
		}
		exports.add(titresCourses);
		
		String[] titresArrets = new String[nbCols];
		titresArrets[0] = "nom arret";
		titresArrets[1] = "id arret";
		for(int i = 0; i < courses.size(); i++) {
			titresArrets[nbColsArrets+2*i] = "heure arrivee";
			titresArrets[nbColsArrets+2*i+1] = "heure depart";
		}
		exports.add(titresArrets);
		
		Collections.sort(arrets,new Comparator<ArretItineraire>(){

			public int compare(ArretItineraire o1, ArretItineraire o2) {
				return o1.getPosition() - o2.getPosition();
			}
			
		});
		List<Long> idPhysiques = new ArrayList<Long>();
		for(ArretItineraire arret : arrets){
			idPhysiques.add(arret.getIdPhysique());
		}
		
		List<PositionGeographique> aps = arretItineraireManager.getArretsPhysiques(idPhysiques,null);
		
		Map<Long, PositionGeographique> arretsPhysiques = new HashMap<Long, PositionGeographique>();
		for(PositionGeographique arretPhysique : aps){
			arretsPhysiques.put(arretPhysique.getId(), arretPhysique);
		}
		
		for(int i = 0 ; i < arrets.size() ; i++){
			ArretItineraire arret = arrets.get(i);
			PositionGeographique arretPhysique = arretsPhysiques.get(arret.getIdPhysique());
			idArrets.add(arret.getId());
			String[] horairesArret = new String[nbCols];
			horairesArret[0] = arretPhysique.getName();
			horairesArret[1] = arret.getId().toString();
			exports.add(horairesArret);
		}
		
		for(Horaire horaire : horaires){
			int row = nbLignesTitres + idArrets.indexOf(horaire.getIdArret());
			int col = nbColsArrets + idCourses.indexOf(horaire.getIdCourse())*2;
			String[] horairesArret = exports.get(row);
			if(horaire.getArrivalTime() != null)
				horairesArret[col] = ExportHorairesManager.dateFormat.format(horaire.getArrivalTime());
			if(horaire.getDepartureTime() != null)
				horairesArret[col+1] = ExportHorairesManager.dateFormat.format(horaire.getDepartureTime());
		}
		
		return exports;
	}
	
	public IItineraireManager getItineraireManager() {
		return itineraireManager;
	}

	public void setItineraireManager(IItineraireManager itineraireManager) {
		this.itineraireManager = itineraireManager;
	}

	public IArretItineraireManager getArretItineraireManager() {
		return arretItineraireManager;
	}

	public void setArretItineraireManager(
			IArretItineraireManager arretItineraireManager) {
		this.arretItineraireManager = arretItineraireManager;
	}
	
}
