package fr.certu.chouette.service.fichier.formatinterne.impl;

import fr.certu.chouette.echange.ILectureEchange;
import fr.certu.chouette.modele.Correspondance;
import fr.certu.chouette.modele.Periode;
import fr.certu.chouette.modele.PositionGeographique;
import fr.certu.chouette.modele.Reseau;
import fr.certu.chouette.modele.TableauMarche;
import fr.certu.chouette.modele.Transporteur;
import fr.certu.chouette.service.commun.CodeIncident;
import fr.certu.chouette.service.commun.ServiceException;
import fr.certu.chouette.service.fichier.formatinterne.IGestionModification;
import fr.certu.chouette.service.fichier.formatinterne.modele.IEtatDifference;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;

public class GestionModification implements IGestionModification {
	
	private static final SimpleDateFormat  sdfHoraire     = new SimpleDateFormat("HH:mm:ss");
	private static final Logger            logger         = Logger.getLogger(GestionModification.class);
	private static final char              QUOTE          = '\'';
	private              IEtatDifference   etatDifference;
	private              Statement         statement;
	private              Map<String, Long> idParObjectId;
	private String databaseSchema;
	
	public void setEtatDifference(IEtatDifference etatDifference) {
		this.etatDifference = etatDifference;
	}
	
	public void setConnexion(final Connection connexion) {
		try {
			statement = connexion.createStatement();
			statement.execute("SET client_encoding to 'UNICODE'");
		} 
		catch (SQLException e) {
			throw new ServiceException(CodeIncident.CONNEXION_BASE, e);
		}
	}
	
	public void setIdParObjectId(Map<String, Long> idParObjectId) {
		this.idParObjectId = idParObjectId;
	}
	
	public void modifier(final ILectureEchange echange) {
		modifier(echange, false);
	}
	
	public void modifier(final ILectureEchange echange, boolean incremental) {
		try {
			logger.debug("Modification des données.");
			if (incremental) {
				//TODO. Mise à jour des données ...
				//modifierLigne(echange);
			}
			modifierTransporteur(echange);
			modifierReseau(echange);
			modifierTableauMarche(echange);
			modifierZonesGeneriques(echange);
			modifierCorrespondances(echange);
		}
		catch(Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	private void modifierTransporteur(final ILectureEchange echange) throws SQLException {
		if (etatDifference.isTransporteurConnu()) {
			Transporteur transporteur = echange.getTransporteur();
			StringBuffer buffer = new StringBuffer("UPDATE " + getDatabaseSchema() + ".company SET ");
			buffer.append(getSQLClause("objectversion", transporteur.getObjectVersion()));
			buffer.append(", ");
			buffer.append(getSQLClause("creationtime", transporteur.getCreationTime()));
			buffer.append(", ");
			buffer.append(getSQLClause("creatorid", transporteur.getCreatorId()));
			buffer.append(", ");
			buffer.append(getSQLClause("name", transporteur.getName()));
			buffer.append(", ");
			buffer.append(getSQLClause("shortname", transporteur.getShortName()));
			buffer.append(", ");
			buffer.append(getSQLClause("organizationalunit", transporteur.getOrganisationalUnit()));
			buffer.append(", ");
			buffer.append(getSQLClause("operatingdepartmentname", transporteur.getOperatingDepartmentName()));
			buffer.append(", ");
			buffer.append(getSQLClause("code", transporteur.getCode()));
			buffer.append(", ");
			buffer.append(getSQLClause("phone", transporteur.getPhone()));
			buffer.append(", ");
			buffer.append(getSQLClause("fax", transporteur.getFax()));
			buffer.append(", ");
			buffer.append(getSQLClause("email", transporteur.getEmail()));
			buffer.append(" WHERE registrationnumber='");
			buffer.append(transporteur.getRegistrationNumber());
			buffer.append("';");
			statement.executeUpdate(buffer.toString());
		}
	}
	
	private void modifierReseau(final ILectureEchange echange) throws SQLException {
		if (etatDifference.isReseauConnu()) {
			Reseau reseau = echange.getReseau();
			StringBuffer buffer = new StringBuffer("UPDATE " + getDatabaseSchema() + ".ptnetwork SET ");
			buffer.append(getSQLClause("objectversion", reseau.getObjectVersion()));
			buffer.append(", ");
			buffer.append(getSQLClause("creationtime", reseau.getCreationTime()));
			buffer.append(", ");
			buffer.append(getSQLClause("creatorid", reseau.getCreatorId()));
			buffer.append(", ");
			buffer.append(getSQLClause("versiondate", reseau.getVersionDate()));
			buffer.append(", ");
			buffer.append(getSQLClause("description", reseau.getDescription()));
			buffer.append(", ");
			buffer.append(getSQLClause("name", reseau.getName()));
			buffer.append(", ");
			buffer.append(getSQLClause("sourcename", reseau.getSourceName()));
			buffer.append(", ");
			buffer.append(getSQLClause("sourceidentifier", reseau.getSourceIdentifier()));
			buffer.append(", ");
			buffer.append(getSQLClause("comment", reseau.getComment()));
			buffer.append(" WHERE registrationnumber='");
			buffer.append(reseau.getRegistrationNumber());
			buffer.append("';");
			statement.executeUpdate(buffer.toString());
		}
	}
	
	private void modifierTableauMarche(final ILectureEchange echange) throws SQLException {
		List<TableauMarche> tableauxMarche = echange.getTableauxMarche();
		for (TableauMarche marche : tableauxMarche) {
			String objectId = marche.getObjectId();
			if (etatDifference.isObjectIdTableauMarcheConnu(objectId)) {
				Long idTM = etatDifference.getIdTableauMarcheConnu(objectId);
				StringBuffer buffer = new StringBuffer("UPDATE " + getDatabaseSchema() + ".timetable SET ");
				buffer.append(getSQLClause("objectversion", marche.getObjectVersion()));
				buffer.append(", ");
				buffer.append(getSQLClause("creationtime", marche.getCreationTime()));
				buffer.append(", ");
				buffer.append(getSQLClause("creatorid", marche.getCreatorId()));
				buffer.append(", ");
				buffer.append(getSQLClause("version", marche.getVersion()));
				buffer.append(", ");
				buffer.append(getSQLClause("comment", marche.getComment()));
				buffer.append(" WHERE id=");
				buffer.append(idTM);
				buffer.append(";");
				//logger.debug(buffer.toString());
				statement.executeUpdate(buffer.toString());
				statement.executeUpdate("DELETE FROM " + getDatabaseSchema() + ".timetable_date WHERE timetableid="+idTM+";");
				// parcours des jours calendaires
				int totalCalendaire = marche.getTotalDates();
				List<java.util.Date> dates = marche.getDates();
				for (int i = 0; i < totalCalendaire; i++) {
					java.util.Date jour = dates.get(i);
					StringBuffer buf = new StringBuffer("INSERT INTO " + getDatabaseSchema() + ".timetable_date (timetableid, date, position) values (");
					buf.append(idTM);
					buf.append(", '");
					buf.append(jour.toString());
					buf.append("', ");
					buf.append(i);
					buf.append(");");
					//logger.debug(buf.toString());
					statement.executeUpdate(buf.toString());
				}
				statement.executeUpdate("DELETE FROM " + getDatabaseSchema() + ".timetable_period WHERE timetableid="+idTM+";");
				// parcours des jours périodes
				int totalPeriode = marche.getTotalPeriodes();
				List<Periode> periodes = marche.getPeriodes();
				for (int i = 0; i < totalPeriode; i++) {
					Periode periode = periodes.get(i);
					StringBuffer buf = new StringBuffer("INSERT INTO " + getDatabaseSchema() + ".timetable_period (timetableid, periodStart, periodEnd, position) values (");
					buf.append(idTM);
					buf.append(", '");
					buf.append(periode.debut.toString());
					buf.append("', '");
					buf.append(periode.fin.toString());
					buf.append("', ");
					buf.append(i);
					buf.append(");");
					//logger.debug(buf.toString());
					statement.executeUpdate(buf.toString());
				}
			}
		}
	}
	
	private void modifierZonesGeneriques(final ILectureEchange echange) throws SQLException {
		List<PositionGeographique> positionsGeographiques = echange.getPositionsGeographiques();
		for (PositionGeographique geoPosition : positionsGeographiques) {
			String objectId = geoPosition.getObjectId();
			if (etatDifference.isObjectIdZoneGeneriqueConnue(objectId)) {
				final Long idPhysique = etatDifference.getIdZoneGeneriqueConnue(objectId);
				String objectIdParent = echange.getZoneParente(objectId);
				Long idZoneParente = (objectIdParent==null)?null:idParObjectId.get(objectIdParent);
				StringBuffer buffer = new StringBuffer("UPDATE " + getDatabaseSchema() + ".stoparea SET ");
				buffer.append(getSQLClause("parentId", idZoneParente));
				buffer.append(", ");
				buffer.append(getSQLClause("objectversion", geoPosition.getObjectVersion()));
				buffer.append(", ");
				buffer.append(getSQLClause("creationtime", geoPosition.getCreationTime()));
				buffer.append(", ");
				buffer.append(getSQLClause("creatorid", geoPosition.getCreatorId()));
				buffer.append(", ");
				buffer.append(getSQLClause("name", geoPosition.getName()));
				buffer.append(", ");
				buffer.append(getSQLClause("comment", geoPosition.getComment()));
				buffer.append(", ");
				buffer.append(getSQLClause("areaType", geoPosition.getAreaType()));
				buffer.append(", ");
				buffer.append(getSQLClause("registrationNumber", geoPosition.getRegistrationNumber()));
				buffer.append(", ");
				buffer.append(getSQLClause("nearestTopicName", geoPosition.getNearestTopicName()));
				buffer.append(", ");
				buffer.append(getSQLClause("fareCode", geoPosition.getFareCode()));
				buffer.append(", ");
				buffer.append(getSQLClause("longitude", geoPosition.getLongitude()));
				buffer.append(", ");
				buffer.append(getSQLClause("latitude", geoPosition.getLatitude()));
				buffer.append(", ");
				buffer.append(getSQLClause("longLatType", geoPosition.getLongLatType()));
				buffer.append(", ");
				buffer.append(getSQLClause("x", geoPosition.getX()));
				buffer.append(", ");
				buffer.append(getSQLClause("y", geoPosition.getY()));
				buffer.append(", ");
				buffer.append(getSQLClause("projectionType", geoPosition.getProjectionType()));
				buffer.append(", ");
				buffer.append(getSQLClause("streetName", geoPosition.getStreetName()));
				buffer.append(", ");
				buffer.append(getSQLClause("countryCode", geoPosition.getCountryCode()));
				buffer.append(" WHERE id=");
				buffer.append(idPhysique);
				//logger.debug(buffer.toString());
				statement.executeUpdate(buffer.toString());
			}
		}
	}
	
	private void modifierCorrespondances(final ILectureEchange echange) throws SQLException {
		List<Correspondance> correspondances = echange.getCorrespondances();
		for (Correspondance correspondance : correspondances) {
			String objectId = correspondance.getObjectId();
			if (etatDifference.isObjectIdCorrespondanceConnue(objectId)) {
				final Long idCorrespondance = etatDifference.getIdCorrespondanceConnue(objectId);
				StringBuffer buffer = new StringBuffer("UPDATE " + getDatabaseSchema() + ".connectionlink SET ");
				buffer.append(getSQLClause("objectversion", correspondance.getObjectVersion()));
				buffer.append(", ");
				buffer.append(getSQLClause("creationtime", correspondance.getCreationTime()));
				buffer.append(", ");
				buffer.append(getSQLClause("creatorid", correspondance.getCreatorId()));
				buffer.append(", ");
				buffer.append(getSQLClause("name", correspondance.getName()));
				buffer.append(", ");
				buffer.append(getSQLClause("comment", correspondance.getComment()));
				buffer.append(", ");
				buffer.append(getSQLClause("linkdistance", correspondance.getLinkDistance()));
				buffer.append(", ");
				buffer.append(getSQLClause("linktype", correspondance.getLinkType()));
				buffer.append(", ");
				buffer.append(getSQLClause("defaultduration", getHoraire(correspondance.getDefaultDuration())));
				buffer.append(", ");
				buffer.append(getSQLClause("frequenttravellerduration", getHoraire(correspondance.getFrequentTravellerDuration())));
				buffer.append(", ");
				buffer.append(getSQLClause("occasionaltravellerduration", getHoraire(correspondance.getOccasionalTravellerDuration())));
				buffer.append(", ");
				buffer.append(getSQLClause("mobilityrestrictedtravellerduration", getHoraire(correspondance.getMobilityRestrictedTravellerDuration())));
				buffer.append(", ");
				buffer.append(getSQLClause("mobilityrestrictedsuitability", correspondance.getMobilityRestrictedSuitability()));
				buffer.append(", ");
				buffer.append(getSQLClause("stairsavailability", correspondance.getStairsAvailability()));
				buffer.append(", ");
				buffer.append(getSQLClause("liftavailability", correspondance.getLiftAvailability()));
				buffer.append(" WHERE id=");
				buffer.append(idCorrespondance);
				//logger.debug(buffer.toString());
				statement.executeUpdate(buffer.toString());
			}
		}
	}
	
	private String getSQLClause(String nom, Object valeur) {
		StringBuffer buffer = new StringBuffer(nom);
		if (valeur==null)
			buffer.append("=null ");
		else {
			buffer.append("='");
			buffer.append(escapeLine(valeur.toString()));
			buffer.append("' ");
		}
		return buffer.toString();
	}
	
	static private String escapeLine(String s) {
		String retvalue = s;
		if (s.indexOf(String.valueOf(QUOTE)) != -1) {
			StringBuffer hold = new StringBuffer();
			char c;
			for (int i = 0; i < s.length(); i++)
				if ((c = s.charAt(i)) == QUOTE) {
					hold.append("\\");
					hold.append(QUOTE);
				}
				else
					hold.append(c);
			retvalue = hold.toString();
		}
		return retvalue;
	}
	
	private String getHoraire(Date date) {
		if (date == null)
			return null;
		return sdfHoraire.format(date);
	}

	public void setDatabaseSchema(String databaseSchema) {
		this.databaseSchema = databaseSchema;
	}

	public String getDatabaseSchema() {
		return databaseSchema;
	}
}
