package fr.certu.chouette.service.fichier.formatinterne.impl;

import fr.certu.chouette.service.fichier.formatinterne.IChargeur;
import fr.certu.chouette.service.fichier.formatinterne.IGestionFichier;
import fr.certu.chouette.service.fichier.formatinterne.INettoyeurLigne;
import fr.certu.chouette.service.fichier.formatinterne.modele.IEtatDifference;
import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import org.apache.log4j.Logger;

public class Chargeur implements IChargeur {
	
	private static final Logger          logger         = Logger.getLogger(Chargeur.class);
	private              IGestionFichier gestionFichier;
	private              INettoyeurLigne nettoyeurLigne;
	
	public void charger(IEtatDifference etatDifference, Connection connexion) {
		charger(etatDifference, connexion, false);
	}
	
	public void charger(IEtatDifference etatDifference, Connection connexion, boolean incremental) {
		try {
			if (etatDifference.isLigneConnue()) {
				nettoyeurLigne.setConnexion(connexion);
				nettoyeurLigne.nettoyer(etatDifference.getIdLigneConnue(), incremental);
			}
			Statement stmt = connexion.createStatement();
			stmt.execute("SET client_encoding to 'UNICODE'");
			if (!etatDifference.isTransporteurConnu())
				charger(stmt, "company", gestionFichier.getCheminFichierTransporteur());
			if (!etatDifference.isReseauConnu())
				charger(stmt, "ptnetwork", gestionFichier.getCheminFichierReseau());
			if ((!incremental) || (incremental && !etatDifference.isLigneConnue())) {
				charger(stmt, "line", gestionFichier.getCheminFichierLigne());
				logger.debug("chargement ligne OK");
			}
			if ((!incremental) || (incremental && etatDifference.isLigneConnue() && etatDifference.containsItineraireInconnu())
					|| (incremental && !etatDifference.isLigneConnue())) {
				charger(stmt, "route", gestionFichier.getCheminFichierItineraire());
				logger.debug("chargement itinéraire OK");
			}
			if (etatDifference.containsZoneGeneriqueInconnue()) {
				charger(stmt, "stoparea", gestionFichier.getCheminFichierZoneGenerique());
				logger.debug("chargement zone générique OK");
			}
			if (etatDifference.containsCorrespondanceInconnue()) {
				charger(stmt, "connectionlink", gestionFichier.getCheminFichierCorrespondance());
				logger.debug("chargement correspondance OK");
			}
			if ((!incremental) || (incremental && etatDifference.isLigneConnue() && etatDifference.containsArretInconnu())
					|| (incremental && !etatDifference.isLigneConnue())) {
				charger(stmt, "stoppoint", gestionFichier.getCheminFichierArretLogique());
				logger.debug("chargement arrêt logique OK");
			}
			if ((!incremental) || (incremental && etatDifference.isLigneConnue() && etatDifference.containsMissionInconnue())
					|| (incremental && !etatDifference.isLigneConnue())) {
				charger(stmt, "journeypattern", gestionFichier.getCheminFichierMission());
				logger.debug("chargement mission OK");
			}
			if ((!incremental) || (incremental && etatDifference.isLigneConnue() && etatDifference.containsCourseInconnue())
					|| (incremental && !etatDifference.isLigneConnue())) {
				charger(stmt, "vehiclejourney", gestionFichier.getCheminFichierCourse());
				logger.debug("chargement course OK");
			}
			if ((!incremental) || (incremental && etatDifference.isLigneConnue() && etatDifference.containsCourseInconnue())
					|| (incremental && !etatDifference.isLigneConnue())) {
				charger(stmt, "vehiclejourneyatstop", gestionFichier.getCheminFichierHoraire());
				logger.debug("chargement horaire OK");
			}
			if (etatDifference.containsTMInconnu()) {
				charger(stmt, "timetable", gestionFichier.getCheminFichierTableauMarche());
				charger(stmt, "timetable_date", gestionFichier.getCheminFichierTableauMarcheCalendrier());
				charger(stmt, "timetable_period", gestionFichier.getCheminFichierTableauMarchePeriode());
			}
			charger(stmt, "timetablevehiclejourney", gestionFichier.getCheminFichierTableauMarcheCourse());
			charger(stmt, "routingConstraint", gestionFichier.getCheminFichierItl());
			charger(stmt, "routingConstraint_stoparea", gestionFichier.getCheminFichierItlStoparea());
		}
		catch(Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	private void charger(Statement stmt, String nomTable, String cheminRelatif) throws SQLException {
		File fichier = new File(cheminRelatif);
		String chemin = fichier.getAbsolutePath();
		if (File.separator.equals("\\")) {
			chemin = chemin.replaceAll("\\\\", "/");
			logger.debug("chemin="+chemin);
		}
		stmt.executeUpdate("COPY "+ getNettoyeurLigne().getDatabaseSchema() + "." + nomTable + " FROM '"+chemin+"';");
		fichier.delete();
	}
	
	public void setGestionFichier(IGestionFichier gestionFichier) {
		this.gestionFichier = gestionFichier;
	}
	
	public void setNettoyeurLigne(INettoyeurLigne nettoyeurLigne) {
		this.nettoyeurLigne = nettoyeurLigne;
	}
	
	public INettoyeurLigne getNettoyeurLigne()
	{
		return nettoyeurLigne;
	}
}
