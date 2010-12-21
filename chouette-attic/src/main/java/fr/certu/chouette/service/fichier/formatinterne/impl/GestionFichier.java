package fr.certu.chouette.service.fichier.formatinterne.impl;

import au.com.bytecode.opencsv.CSVWriter;
import fr.certu.chouette.service.fichier.formatinterne.IGestionFichier;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.List;
import org.apache.log4j.Logger;

public class GestionFichier implements IGestionFichier {
	
    private static final Logger logger                     = Logger.getLogger(Chargeur.class);
	private              String repertoireTmp;
	private              String ficLigne;
	private              String ficItineraire;
	private              String ficTransporteur;
	private              String ficReseau;
	private              String ficArretLogique;
	private              String ficZoneGenerique;
	private              String ficCorrespondance;
	private              String ficTableauMarche;
	private              String ficTableauMarcheCalendrier;
	private              String ficTableauMarchePeriode;
	private              String ficCourse;
	private              String ficMission;
	private              String ficTableauMarcheCourse;
	private              String ficHoraire;
	private              String ficItl;
	private              String ficItlStoparea;
	
	private String getCheminFichier(String nom) {
		return repertoireTmp + File.separator + nom;
	}
	
	public String getChamp(Object o) {
		String nullVal = "\\N";
		return (o == null) ? nullVal : o.toString();
	}
	
	public void produire(List<String[]> contenu, String nomFichier) {
		produire(contenu, nomFichier, false);
	}
	
	public void produire(List<String[]> contenu, String nomFichier, boolean append) {
		CSVWriter csvWriter = null;
		OutputStreamWriter fileWriter = null;
		try {
			if (!append)
				logger.debug("Production du fichier : " + nomFichier);
			fileWriter = new OutputStreamWriter(new FileOutputStream(nomFichier, append), "UTF8");
			csvWriter = new CSVWriter(fileWriter,'\t', CSVWriter.NO_QUOTE_CHARACTER);
			if (!contenu.isEmpty())
				csvWriter.writeAll(contenu);
			csvWriter.close();
			fileWriter.close();
		}
		catch(Exception e) {
			throw new RuntimeException(e);
		}
		finally {
			if (csvWriter != null) {
				try {
					csvWriter.close();
				} 
				catch(Exception ex) {
				}
			}
			else if (fileWriter != null) {
				try {
					fileWriter.close();
				}
				catch(Exception ex){
				}
			}
		}
	}
	
	public String getCheminFichierMission() {
		return getCheminFichier(ficMission);
	}
	
	public String getCheminFichierItineraire() {
		return getCheminFichier(ficItineraire);
	}
	
	public String getCheminFichierArretLogique() {
		return getCheminFichier(ficArretLogique);
	}
	
	public String getCheminFichierZoneGenerique() {
		return getCheminFichier(ficZoneGenerique);
	}
	
	public String getCheminFichierLigne() {
		return getCheminFichier(ficLigne);
	}
	
	public String getCheminFichierReseau() {
		return getCheminFichier(ficReseau);
	}
	
	public String getCheminFichierTransporteur() {
		return getCheminFichier(ficTransporteur);
	}
	
	public String getCheminFichierTableauMarche() {
		return getCheminFichier(ficTableauMarche);
	}
	
	public String getCheminFichierTableauMarcheCalendrier() {
		return getCheminFichier(ficTableauMarcheCalendrier);
	}
	
	public String getCheminFichierTableauMarchePeriode() {
		return getCheminFichier(ficTableauMarchePeriode);
	}
	
	public String getCheminFichierCourse() {
		return getCheminFichier(ficCourse);
	}
	
	public String getCheminFichierTableauMarcheCourse() {
		return getCheminFichier(ficTableauMarcheCourse);
	}
	
	public String getCheminFichierHoraire() {
		return getCheminFichier(ficHoraire);
	}
	
	public String getCheminFichierCorrespondance() {
		return getCheminFichier(ficCorrespondance);
	}
	
	public String getCheminFichierItl() {
		return getCheminFichier(ficItl);
	}
	
	public String getCheminFichierItlStoparea() {
		return getCheminFichier(ficItlStoparea);
	}
	
	public void setFicCorrespondance(String ficCorrespondance) {
		this.ficCorrespondance = ficCorrespondance;
	}
	
	public void setFicTableauMarcheCalendrier(String ficTableauMarcheCalendrier) {
		this.ficTableauMarcheCalendrier = ficTableauMarcheCalendrier;
	}
	
	public void setFicTableauMarchePeriode(String ficTableauMarchePeriode) {
		this.ficTableauMarchePeriode = ficTableauMarchePeriode;
	}
	
	public void setFicTableauMarche(String ficTableauMarche) {
		this.ficTableauMarche = ficTableauMarche;
	}
	
	public void setFicItineraire(String ficItineraire) {
		this.ficItineraire = ficItineraire;
	}
	
	public void setFicCourse(String ficCourse) {
		this.ficCourse = ficCourse;
	}
	
	public void setFicMission(String ficMission) {
		this.ficMission = ficMission;
	}
	
	public void setFicHoraire(String ficHoraire) {
		this.ficHoraire = ficHoraire;
	}
	
	public void setFicTableauMarcheCourse(String ficTableauMarcheCourse) {
		this.ficTableauMarcheCourse = ficTableauMarcheCourse;
	}
	
	public void setFicArretLogique(String ficArretLogique) {
		this.ficArretLogique = ficArretLogique;
	}
	
	public void setFicZoneGenerique(String ficZoneGenerique) {
		this.ficZoneGenerique = ficZoneGenerique;
	}
	
	public void setFicLigne(String ficLigne) {
		this.ficLigne = ficLigne;
	}
	
	public void setFicReseau(String ficReseau) {
		this.ficReseau = ficReseau;
	}
	
	public void setFicTransporteur(String ficTransporteur) {
		this.ficTransporteur = ficTransporteur;
	}
	
	public void setRepertoireTmp(String repertoireTmp) {
		this.repertoireTmp = repertoireTmp;
	}
	
	public void setFicItl(String ficItl) {
		this.ficItl = ficItl;
	}
	
	public void setFicItlStoparea(String ficItlStoparea) {
		this.ficItlStoparea = ficItlStoparea;
	}
}
