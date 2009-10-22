package fr.certu.chouette.service.fichier.formatinterne.impl;

import fr.certu.chouette.modele.Itineraire;
import fr.certu.chouette.service.database.ILigneManager;
import fr.certu.chouette.service.fichier.formatinterne.INettoyeurLigne;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;

public class NettoyeurLigne implements INettoyeurLigne {
	
	private static final Logger             logger = Logger.getLogger(NettoyeurLigne.class);
	private              ILigneManager      ligneManager;
	private              Connection         connexion;
	
	public void nettoyer(Long ligneId) {
		nettoyer(ligneId, false);
	}
	
	public void nettoyer(Long ligneId, boolean incremental) {
		//TODO. NE PAS OUBLIER DE NETOYER LES TMs
		if (incremental) {
			/*
			try {
				String    timetable_dateSQL = "DELETE FROM timetable_date WHERE tobedeleted='true';";
				Statement timetable_dateSt  = connexion.createStatement();
				timetable_dateSt.executeUpdate(timetable_dateSQL);
				String    timetable_periodSQL = "DELETE FROM timetable_period WHERE tobedeleted='true';";
				Statement timetable_periodSt  = connexion.createStatement();
				timetable_periodSt.executeUpdate(timetable_periodSQL);
				String    timetablevehiclejourneySQL = "DELETE FROM timetablevehiclejourney WHERE tobedeleted='true';";
				Statement timetablevehiclejourneySt  = connexion.createStatement();
				timetablevehiclejourneySt.executeUpdate(timetablevehiclejourneySQL);
				String    timetableSQL = "DELETE FROM timetable WHERE tobedeleted='true';";
				Statement timetableSt  = connexion.createStatement();
				timetableSt.executeUpdate(timetableSQL);
				String    vehiclejourneyatstopSQL = "DELETE FROM vehiclejourneyatstop WHERE tobedeleted='true';";
				Statement vehiclejourneyatstopSt  = connexion.createStatement();
				vehiclejourneyatstopSt.executeUpdate(vehiclejourneyatstopSQL);
				String    vehiclejourneySQL = "DELETE FROM vehiclejourney WHERE tobedeleted='true';";
				Statement vehiclejourneySt  = connexion.createStatement();
				vehiclejourneySt.executeUpdate(vehiclejourneySQL);
				String    journeypatternSQL = "DELETE FROM journeypattern WHERE tobedeleted='true';";
				Statement journeypatternSt  = connexion.createStatement();
				journeypatternSt.executeUpdate(journeypatternSQL);
				String    stoppointSQL = "DELETE FROM stoppoint WHERE tobedeleted='true';";
				Statement stoppointSt  = connexion.createStatement();
				stoppointSt.executeUpdate(stoppointSQL);
				*/
				// TODO. effacer les zones, itl et correspondances ...
			    /*
				String    routeSQL = "DELETE FROM route WHERE tobedeleted='true';";
				Statement routeSt  = connexion.createStatement();
				routeSt.executeUpdate(routeSQL);
			}
			catch(Exception e) {
				throw new RuntimeException(e);
			}*/
		}
		else {
			try {
				List<Itineraire> itineraires = ligneManager.getItinerairesLigne(ligneId);
				List<List<Long>> superItinerairesIds = new ArrayList<List<Long>>();
				int count = 0;
				List<Long> itineraireIds = new ArrayList<Long>();
				superItinerairesIds.add(itineraireIds);
				for (Itineraire itineraire : itineraires) {
					count++;
					itineraireIds.add(itineraire.getId());
					if (count == 40) {
						count = 0;
						itineraireIds = new ArrayList<Long>();
						superItinerairesIds.add(itineraireIds);
					}
				}
				Statement stmt = connexion.createStatement();
				for (List<Long> _itineraireIds : superItinerairesIds ) {
					if (_itineraireIds.size()>0) {
						String clause = "select vv.id from vehiclejourney as vv where vv.iditineraire in ("+getSQLlist(_itineraireIds)+")";
						String reqHoraires = "delete from vehiclejourneyatstop where idcourse in ("+clause+")";
						//logger.debug(reqHoraires);
						stmt.executeUpdate(reqHoraires);
						String reqTM = "delete from timetablevehiclejourney where idcourse in ("+clause+")";
						//logger.debug(reqTM);
						stmt.executeUpdate(reqTM);
						String clauseMissions = "select vv.idmission from vehiclejourney as vv where vv.iditineraire in ("+getSQLlist(_itineraireIds)+") group by vv.idmission";
						/****************************************************/
						/** IL FAUT EFFACSE LES COURSES AVANT LES MISSIONS **/
						ResultSet rs = stmt.executeQuery(clauseMissions);
						String idMissions = "";
						boolean drapeau = false;
						while (rs.next())
							if (rs.getObject(1) != null) {
								if (drapeau)
									idMissions += " , ";
								drapeau = true;
								idMissions += "'"+rs.getLong(1)+"'";
							}
						/****************************************************/
						String reqCourses = "delete from vehiclejourney  where iditineraire in ("+getSQLlist(_itineraireIds)+");";
						logger.debug(reqCourses);
						stmt.executeUpdate(reqCourses);
						if (idMissions.length() > 0) {
							String reqMissions = "delete from journeypattern where id in ("+idMissions+" );";
							logger.debug(reqMissions);
							stmt.executeUpdate(reqMissions);
						}
						stmt.executeUpdate("delete from stoppoint where idItineraire in ("+getSQLlist(_itineraireIds)+");");
					}
				}
				stmt.executeUpdate("delete from route where idligne="+ligneId+";");
				stmt.executeUpdate("delete from itl_stoparea where iditl in (select i.id from itl i where i.idligne="+ligneId+");");
				stmt.executeUpdate("delete from itl where idligne="+ligneId+";");
				stmt.executeUpdate("delete from line where id="+ligneId+";");
			}
			catch(Exception e) {
				throw new RuntimeException(e);
			}
		}
	}
	
	public void setLigneManager(ILigneManager ligneManager) {
		this.ligneManager = ligneManager;
	}
	
	private String getSQLlist(List<Long> ids) {
		StringBuffer sqlBuff = new StringBuffer(); 
		int total = ids.size();
		for (int i = 0; i < total; i++) {
			sqlBuff.append(ids.get(i));
			if (i<(total-1))
				sqlBuff.append(",");
		}
		return sqlBuff.toString();
	}
	
	public void setConnexion(Connection connexion) {
		this.connexion = connexion;
	}
}
