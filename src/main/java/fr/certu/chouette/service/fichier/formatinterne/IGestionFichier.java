package fr.certu.chouette.service.fichier.formatinterne;

import java.util.List;

public interface IGestionFichier {
	
	public String getChamp(Object o);
	public void produire(List<String[]> contenu, String nomFichier);
	public void produire(List<String[]> contenu, String nomFichier, boolean append);
	public String getCheminFichierMission();
	public String getCheminFichierTransporteur();
	public String getCheminFichierReseau();
	public String getCheminFichierLigne();
	public String getCheminFichierItineraire();
	public String getCheminFichierArretLogique();
	public String getCheminFichierZoneGenerique();
	public String getCheminFichierTableauMarche();
	public String getCheminFichierTableauMarcheCalendrier();
	public String getCheminFichierTableauMarchePeriode();
	public String getCheminFichierCourse();
	public String getCheminFichierTableauMarcheCourse();
	public String getCheminFichierHoraire();
	public String getCheminFichierCorrespondance();
	public String getCheminFichierItl();
	public String getCheminFichierItlStoparea();
}
