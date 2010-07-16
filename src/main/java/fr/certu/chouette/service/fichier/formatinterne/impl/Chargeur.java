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
	stmt.executeUpdate("COPY "+ getNettoyeurLigne().getDatabaseSchema() + "." + nomTable +
			   getProto(nomTable) + " FROM '"+chemin+"';");
	fichier.delete();
    }
    
    private String getProto(String nomTable) {
	String result = "(";
	if ("company".equals(nomTable)) {
	    result += "id, ";
 	    result += "objectid, ";
 	    result += "objectversion, ";
 	    result += "creationtime, ";
 	    result += "creatorid, ";
 	    result += "name, ";
 	    result += "shortname, ";
 	    result += "organizationalunit, ";
 	    result += "operatingdepartmentname, ";
 	    result += "code, ";
 	    result += "phone, ";
 	    result += "fax, ";
 	    result += "email, ";
 	    result += "registrationnumber";
	}
	else if ("connectionlink".equals(nomTable)) {
 	    result += "id, ";
 	    result += "departureid, ";
 	    result += "arrivalid, ";
 	    result += "objectid, ";
 	    result += "objectversion, ";
 	    result += "creationtime, ";
 	    result += "creatorid, ";
 	    result += "name, ";
 	    result += "comment, ";
 	    result += "linkdistance, ";
 	    result += "linktype, ";
 	    result += "defaultduration, ";
 	    result += "frequenttravellerduration, ";
 	    result += "occasionaltravellerduration, ";
 	    result += "mobilityrestrictedtravellerduration, ";
 	    result += "mobilityrestrictedsuitability, ";
 	    result += "stairsavailability, ";
 	    result += "liftavailability";
	}
	else if ("ptnetwork".equals(nomTable)) {
 	    result += "id, ";
 	    result += "objectid, ";
 	    result += "objectversion, ";
 	    result += "creationtime, ";
 	    result += "creatorid, ";
 	    result += "versiondate, ";
 	    result += "description, ";
 	    result += "name, ";
 	    result += "registrationnumber, ";
 	    result += "sourcename, ";
 	    result += "sourceidentifier, ";
 	    result += "comment";
	}
	else if ("line".equals(nomTable)) {
 	    result += "id, ";
 	    result += "ptnetworkid, ";
 	    result += "companyid, ";
 	    result += "objectid, ";
 	    result += "objectversion, ";
 	    result += "creationtime, ";
 	    result += "creatorid, ";
 	    result += "name, ";
 	    result += "number, ";
 	    result += "publishedname, ";
 	    result += "transportmodename, ";
 	    result += "registrationnumber, ";
 	    result += "comment";
	}
	else if ("route".equals(nomTable)) {
 	    result += "id, ";
 	    result += "oppositerouteid, ";
 	    result += "lineid, ";
 	    result += "objectid, ";
 	    result += "objectversion, ";
 	    result += "creationtime, ";
 	    result += "creatorid, ";
 	    result += "name, ";
 	    result += "publishedname, ";
 	    result += "number, ";
 	    result += "direction, ";
 	    result += "comment, ";
 	    result += "wayback";
	}
	else if ("stoparea".equals(nomTable)) {
 	    result += "id, ";
 	    result += "parentid, ";
 	    result += "objectid, ";
 	    result += "objectversion, ";
 	    result += "creationtime, ";
 	    result += "creatorid, ";
 	    result += "name, ";
 	    result += "comment, ";
 	    result += "areatype, ";
 	    result += "registrationnumber, ";
 	    result += "nearesttopicname, ";
 	    result += "farecode, ";
 	    result += "longitude, ";
 	    result += "latitude, ";
 	    result += "longlattype, ";
 	    result += "x, ";
 	    result += "y, ";
 	    result += "projectiontype, ";
 	    result += "countrycode, ";
 	    result += "streetname";
	}
	else if ("stoppoint".equals(nomTable)) {
 	    result += "id, ";
 	    result += "routeid, ";
 	    result += "stopareaid, ";
 	    result += "ismodified, ";
 	    result += "position, ";
 	    result += "objectid, ";
 	    result += "objectversion, ";
 	    result += "creationtime, ";
 	    result += "creatorid";
	}
	else if ("journeypattern".equals(nomTable)) {
 	    result += "id, ";
 	    result += "objectid, ";
 	    result += "objectversion, ";
 	    result += "creationtime, ";
 	    result += "creatorid, ";
 	    result += "registrationnumber, ";
 	    result += "name, ";
 	    result += "publishedname, ";
 	    result += "comment";
	}
	else if ("vehiclejourney".equals(nomTable)) {
 	    result += "id, ";
 	    result += "routeid, ";
 	    result += "journeypatternid, ";
 	    result += "objectid, ";
 	    result += "objectversion, ";
 	    result += "creationtime, ";
 	    result += "creatorid, ";
 	    result += "publishedjourneyname, ";
 	    result += "publishedjourneyidentifier, ";
 	    result += "transportmode, ";
 	    result += "vehicletypeidentifier, ";
 	    result += "statusvalue, ";
 	    result += "facility, ";
 	    result += "number, ";
 	    result += "comment";
	}
	else if ("vehiclejourneyatstop".equals(nomTable)) {
 	    result += "id, ";
 	    result += "vehiclejourneyid, ";
 	    result += "stoppointid, ";
 	    result += "ismodified, ";
 	    result += "arrivaltime, ";
 	    result += "departuretime, ";
 	    result += "waitingtime, ";
 	    result += "connectingserviceid, ";
 	    result += "boardingalightingpossibility, ";
 	    result += "isdeparture";
	}
	else if ("timetable".equals(nomTable)) {
 	    result += "id, ";
 	    result += "objectid, ";
 	    result += "objectversion, ";
 	    result += "creationtime, ";
 	    result += "creatorid, ";
 	    result += "version, ";
 	    result += "comment, ";
 	    result += "intdaytypes";
	}
	else if ("timetable_date".equals(nomTable)) {
 	    result += "timetableid, ";
 	    result += "date, ";
 	    result += "position";
	}
	else if ("timetable_period".equals(nomTable)) {
 	    result += "timetableid, ";
 	    result += "periodstart, ";
 	    result += "periodend, ";
 	    result += "position";
	}
	else if ("timetablevehiclejourney".equals(nomTable)) {
 	    result += "id, ";
 	    result += "timetableid, ";
 	    result += "vehiclejourneyid";
	}
	else if ("routingConstraint".equals(nomTable)) {
 	    result += "id, ";
 	    result += "objectid, ";
 	    result += "lineid, ";
 	    result += "name";
	}
	else if ("routingConstraint_stoparea".equals(nomTable)) {
 	    result += "routingconstraintid, ";
 	    result += "stopareaid, ";
 	    result += "position";
	}
	else
	    return "";
	result += ")";
	return result;
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
