package fr.certu.chouette.service.fichier.formatinterne.impl;

import fr.certu.chouette.echange.ILectureEchange;
import fr.certu.chouette.modele.ArretItineraire;
import fr.certu.chouette.modele.Correspondance;
import fr.certu.chouette.modele.Course;
import fr.certu.chouette.modele.Itineraire;
import fr.certu.chouette.modele.Ligne;
import fr.certu.chouette.modele.Mission;
import fr.certu.chouette.modele.Periode;
import fr.certu.chouette.modele.Reseau;
import fr.certu.chouette.modele.TableauMarche;
import fr.certu.chouette.modele.Transporteur;
import fr.certu.chouette.service.fichier.formatinterne.IAnalyseurEtatInitial;
import fr.certu.chouette.service.fichier.formatinterne.IGestionSequence;
import fr.certu.chouette.service.fichier.formatinterne.modele.EtatDifference;
import fr.certu.chouette.service.fichier.formatinterne.modele.IEtatDifference;
import java.math.BigInteger;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import org.apache.log4j.Logger;

public class AnalyseurEtatInitial implements IAnalyseurEtatInitial  {
	
	private static final Logger           logger          = Logger.getLogger(AnalyseurEtatInitial.class);
	private              EtatDifference   etatDifference;
	private              IGestionSequence gestionSequence;
	private static final SimpleDateFormat sdf1            = new SimpleDateFormat("ddMMyy");	
	private static final SimpleDateFormat sdf2            = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");	
	private static final SimpleDateFormat sdf3            = new SimpleDateFormat("yyyy-MM-dd");
	
	private String databaseSchema; 
	
	public void setGestionSequence(IGestionSequence gestionSequence) 
	{
		this.gestionSequence = gestionSequence;
	}
	
	public IEtatDifference analyser(ILectureEchange lectureEchange, Connection connexion) 
	{
		return analyser(lectureEchange, connexion, false); 
	}
	
	public IEtatDifference analyser2(ILectureEchange lectureEchange, Connection connexion) 
	{
		return analyser2(lectureEchange, connexion, false); 
	}
	
	public IEtatDifference analyser(ILectureEchange lectureEchange, Connection connexion, boolean incremental) 
	{
		etatDifference = new EtatDifference();
		Ligne ligne = lectureEchange.getLigne();
		Reseau reseau = lectureEchange.getReseau();
		Transporteur transporteur = lectureEchange.getTransporteur();
		try {
			final String selectionLigne = "select id from " + getDatabaseSchema() 
				+ ".line where registrationnumber='"+ligne.getRegistrationNumber()+"';";
			Statement sqlStatement = connexion.createStatement();
			ResultSet rs = sqlStatement.executeQuery(selectionLigne);
			while (rs.next())
				etatDifference.setExLigne(Long.parseLong(rs.getObject(1).toString()));
			final String selectionReseau = "select id from " + getDatabaseSchema() 
				+ ".ptnetwork where registrationnumber='"+reseau.getRegistrationNumber()+"';";
			sqlStatement = connexion.createStatement();
			rs = sqlStatement.executeQuery(selectionReseau);
			while (rs.next()) 
				etatDifference.setExReseau(Long.parseLong(rs.getObject(1).toString()));
			final String selectionTransporteur = "select id from " + getDatabaseSchema() 
				+ ".company where registrationnumber='"+transporteur.getRegistrationNumber()+"';";
			sqlStatement = connexion.createStatement();
			rs = sqlStatement.executeQuery(selectionTransporteur);
			while (rs.next()) 
				etatDifference.setExTransporteur(Long.parseLong(rs.getObject(1).toString()));
			analyserZones(lectureEchange, connexion);
			analyserCorrespondances(lectureEchange, connexion);
			analyserTM(lectureEchange, connexion, incremental);
			if (incremental && (etatDifference.isLigneConnue())) {
				List<String> newItIds = analyserItineraires(lectureEchange, connexion);
				analyserMissions(lectureEchange, connexion);
				analyserCourses(lectureEchange, connexion);
				analyserArrets(lectureEchange, connexion, newItIds);
			}
			return etatDifference;
		}
		catch(Exception e) 
		{
			throw new RuntimeException(e);
		}
	}
	
	public IEtatDifference analyser2(ILectureEchange lectureEchange, Connection connexion, boolean incremental) 
	{
		etatDifference = new EtatDifference();
		Ligne ligne = lectureEchange.getLigne();
		Reseau reseau = lectureEchange.getReseau();
		Transporteur transporteur = lectureEchange.getTransporteur();
		try {
			final String selectionLigne = "select id from " + getDatabaseSchema() + ".line where registrationnumber='"+ligne.getRegistrationNumber()+"';";
			Statement sqlStatement = connexion.createStatement();
			ResultSet rs = sqlStatement.executeQuery(selectionLigne);
			while (rs.next())
				etatDifference.setExLigne(Long.parseLong(rs.getObject(1).toString()));
			final String selectionReseau = "select id from " + getDatabaseSchema() + ".ptnetwork where registrationnumber='"+reseau.getRegistrationNumber()+"';";
			sqlStatement = connexion.createStatement();
			rs = sqlStatement.executeQuery(selectionReseau);
			while (rs.next()) 
				etatDifference.setExReseau(Long.parseLong(rs.getObject(1).toString()));
			final String selectionTransporteur = "select id from " + getDatabaseSchema() + ".company where registrationnumber='"+transporteur.getRegistrationNumber()+"';";
			sqlStatement = connexion.createStatement();
			rs = sqlStatement.executeQuery(selectionTransporteur);
			while (rs.next()) 
				etatDifference.setExTransporteur(Long.parseLong(rs.getObject(1).toString()));
			analyserZones2(lectureEchange, connexion);
			analyserCorrespondances2(lectureEchange, connexion);
			analyserTM2(lectureEchange, connexion, incremental);
			if (incremental && (etatDifference.isLigneConnue())) {
				analyserItineraires(lectureEchange, connexion);
				analyserMissions(lectureEchange, connexion);
				analyserCourses(lectureEchange, connexion);
				analyserArrets(lectureEchange, connexion, null);
			}
			return etatDifference;
		}
		catch(Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	private void analyserZones(final ILectureEchange lectureEchange, final Connection connexion) throws SQLException {
		final List<String> objectIdZonesGeneriques = lectureEchange.getObjectIdZonesGeneriques();
		final String selectionZones = "SELECT t.objectid, t.id from " + getDatabaseSchema() + ".stoparea t;";
		final Statement sqlStatement = connexion.createStatement();
		final ResultSet rs = sqlStatement.executeQuery(selectionZones);
		final Map<String, Long> exZoneGeneriqueIdParObjectId = new Hashtable<String, Long>();
		while (rs.next()) {
			final String objectId = rs.getObject(1).toString();
			final Long id = Long.parseLong(rs.getObject(2).toString());
			exZoneGeneriqueIdParObjectId.put(objectId, id);
		}
		etatDifference.setExZoneGeneriqueIdParObjectId(exZoneGeneriqueIdParObjectId);
		final List<String> objIdZonesGeneriquesNouvelles = new ArrayList<String>(objectIdZonesGeneriques);
                if (!exZoneGeneriqueIdParObjectId.isEmpty())
                    objIdZonesGeneriquesNouvelles.removeAll(exZoneGeneriqueIdParObjectId.keySet());
		etatDifference.setNvObjectIdZoneGenerique(objIdZonesGeneriquesNouvelles);
	}
	
	private void analyserZones2(final ILectureEchange lectureEchange, final Connection connexion) throws SQLException {
		final List<String> objectIdZonesGeneriques = lectureEchange.getObjectIdZonesGeneriques();
		final String selectionZones = "SELECT t.objectid, t.id from " + getDatabaseSchema() + ".stoparea t;";
		final Statement sqlStatement = connexion.createStatement();
		final ResultSet rs = sqlStatement.executeQuery(selectionZones);
		final Map<String, Long> exZoneGeneriqueIdParObjectId = new Hashtable<String, Long>();
		while (rs.next()) {
			final String objectId = rs.getObject(1).toString();
			final Long id = Long.parseLong(rs.getObject(2).toString());
			exZoneGeneriqueIdParObjectId.put(objectId, id);
		}
		etatDifference.setExZoneGeneriqueIdParObjectId(exZoneGeneriqueIdParObjectId);
		final List<String> objIdZonesGeneriquesNouvelles = new ArrayList<String>(objectIdZonesGeneriques);
		objIdZonesGeneriquesNouvelles.removeAll(exZoneGeneriqueIdParObjectId.keySet());
		etatDifference.setNvObjectIdZoneGenerique(objIdZonesGeneriquesNouvelles);
	}
	
	private void analyserCorrespondances(final ILectureEchange lectureEchange, final Connection connexion) throws SQLException {
		final List<Correspondance> correspondances = lectureEchange.getCorrespondances();
		List<String> objectIdCorrespondances = new ArrayList<String>();
		if (correspondances != null)
			for (Correspondance correspondance : correspondances)
				objectIdCorrespondances.add(correspondance.getObjectId());
		final String selectionCorrespondances = "SELECT t.objectid, t.id FROM " + getDatabaseSchema() + ".connectionlink t;";
		final Statement sqlStatement = connexion.createStatement();
		final ResultSet rs = sqlStatement.executeQuery(selectionCorrespondances);
		final Map<String, Long> exCorrespondanceIdParObjectId = new Hashtable<String, Long>();
		while (rs.next()) {
			final String objectId = rs.getObject(1).toString();
			final Long id = Long.parseLong(rs.getObject(2).toString());
			exCorrespondanceIdParObjectId.put(objectId, id);
		}
		etatDifference.setExCorrespondanceIdParObjectId(exCorrespondanceIdParObjectId);
		final List<String> objIdCorrespondancesNouvelles = new ArrayList<String>(objectIdCorrespondances);
		objIdCorrespondancesNouvelles.removeAll(exCorrespondanceIdParObjectId.keySet());
		etatDifference.setNvObjectIdCorrespondance(objIdCorrespondancesNouvelles);
	}
	
	private void analyserCorrespondances2(final ILectureEchange lectureEchange, final Connection connexion) throws SQLException {
		final List<Correspondance> correspondances = lectureEchange.getCorrespondances();
		List<String> objectIdCorrespondances = new ArrayList<String>();
		if (correspondances != null)
			for (Correspondance correspondance : correspondances)
				objectIdCorrespondances.add(correspondance.getObjectId());
		final String selectionCorrespondances = "SELECT t.objectid, t.id from " + getDatabaseSchema() + ".connectionlink t;";
		final Statement sqlStatement = connexion.createStatement();
		final ResultSet rs = sqlStatement.executeQuery(selectionCorrespondances);
		final Map<String, Long> exCorrespondanceIdParObjectId = new Hashtable<String, Long>();
		while (rs.next()) {
			final String objectId = rs.getObject(1).toString();
			final Long id = Long.parseLong(rs.getObject(2).toString());
			exCorrespondanceIdParObjectId.put(objectId, id);
		}
		etatDifference.setExCorrespondanceIdParObjectId(exCorrespondanceIdParObjectId);
		final List<String> objIdCorrespondancesNouvelles = new ArrayList<String>(objectIdCorrespondances);
		objIdCorrespondancesNouvelles.removeAll(exCorrespondanceIdParObjectId.keySet());
		etatDifference.setNvObjectIdCorrespondance(objIdCorrespondancesNouvelles);
	}
	
	private void analyserTM(final ILectureEchange lectureEchange, final Connection connexion, final boolean incremental) throws SQLException {
		final List<TableauMarche> tableauxMarche = lectureEchange.getTableauxMarche();
		if (incremental) 
		{
			logger.debug("IMPORT INCREMENTAL DE LA LIGNE "+lectureEchange.getLigne().getNumber()+".");
			if ((tableauxMarche == null) || (tableauxMarche.size() <= 0))
				logger.warn("LA LIGNE "+lectureEchange.getLigne().getNumber()+" N'A PAS DE CALENDRIERS.");
			else
				analyserTM(lectureEchange, connexion);
		}
		final String selectionTableau = "SELECT t.objectid, t.id from " + getDatabaseSchema() + ".timetable t;";
		final Statement sqlStatement = connexion.createStatement();
		final ResultSet rs = sqlStatement.executeQuery(selectionTableau);
		final Map<String, Long> exTMIdParObjectId = new Hashtable<String, Long>();

		while (rs.next()) 
		{
			final String objectId = rs.getObject(1).toString();
			final Long id = Long.parseLong(rs.getObject(2).toString());
			exTMIdParObjectId.put(objectId, id);
		}
		etatDifference.setExTMIdParObjectId(exTMIdParObjectId);
		final List<String> nvObjectIdTM = new ArrayList<String>();
		for (TableauMarche marche : tableauxMarche) {
			final String objectId = marche.getObjectId();
			if (!exTMIdParObjectId.containsKey(objectId))
				nvObjectIdTM.add(objectId);
		}
		etatDifference.setNvObjectIdTM(nvObjectIdTM);
	}
	
	private void analyserTM2(final ILectureEchange lectureEchange, final Connection connexion, final boolean incremental) throws SQLException {
		final List<TableauMarche> tableauxMarche = lectureEchange.getTableauxMarche();
		if (incremental) {
			logger.debug("IMPORT INCREMENTAL DE LA LIGNE "+lectureEchange.getLigne().getNumber()+".");
			if ((tableauxMarche == null) || (tableauxMarche.size() <= 0))
				logger.warn("LA LIGNE "+lectureEchange.getLigne().getNumber()+" N'A PAS DE CALENDRIERS.");
			else
				analyserTM2(lectureEchange, connexion);
		}
		final String selectionTableau = "SELECT t.objectid, t.id from " + getDatabaseSchema() + ".timetable t;";
		final Statement sqlStatement = connexion.createStatement();
		final ResultSet rs = sqlStatement.executeQuery(selectionTableau);
		final Map<String, Long> exTMIdParObjectId = new Hashtable<String, Long>();
		while (rs.next()) {
			final String objectId = rs.getObject(1).toString();
			final Long id = Long.parseLong(rs.getObject(2).toString());
			exTMIdParObjectId.put(objectId, id);
		}
		etatDifference.setExTMIdParObjectId(exTMIdParObjectId);
		final List<String> nvObjectIdTM = new ArrayList<String>();
		for (TableauMarche marche : tableauxMarche) {
			final String objectId = marche.getObjectId();
			if (!exTMIdParObjectId.containsKey(objectId))
				nvObjectIdTM.add(objectId);
		}
		etatDifference.setNvObjectIdTM(nvObjectIdTM);
	}
	
	private void analyserTM(final ILectureEchange lectureEchange, final Connection connexion) throws SQLException {
		if (!etatDifference.isLigneConnue())
			return;
		
		long idLigne = etatDifference.getIdLigneConnue();
		
		Set<Long>            oldItinerairesIds           = new HashSet<Long>();
		Set<Long>            oldCoursesIds               = new HashSet<Long>();
		Map<Long, Set<Long>> coursesIdsParItinerairesId  = new HashMap<Long, Set<Long>>();
		Map<Long, Set<Long>> missionsIdsParItinerairesId = new HashMap<Long, Set<Long>>();
		Map<Long, Set<Long>> coursesIdsParMissionsId     = new HashMap<Long, Set<Long>>();
		String    selectionItineraires = "SELECT id from " + getDatabaseSchema() + ".route WHERE lineId='"+idLigne+"';";
		Statement selectionItinerairesStatement = connexion.createStatement();
		ResultSet selectionItinerairesResultSet = selectionItinerairesStatement.executeQuery(selectionItineraires);

		while (selectionItinerairesResultSet.next()) 
		{
			if (selectionItinerairesResultSet.getObject(1) == null)
				continue;
			String idItineraireSt = selectionItinerairesResultSet.getObject(1).toString();
			Long idItineraire = Long.valueOf(idItineraireSt);
			oldItinerairesIds.add(idItineraire);
			coursesIdsParItinerairesId.put(idItineraire, new HashSet<Long>());
			missionsIdsParItinerairesId.put(idItineraire, new HashSet<Long>());
			String selectionCourses = "SELECT id, journeyPatternId from " + getDatabaseSchema() + ".vehiclejourney WHERE routeid='"+idItineraireSt+"';";
			Statement selectionCoursesStatement = connexion.createStatement();
			ResultSet selectionCoursesResultSet = selectionCoursesStatement.executeQuery(selectionCourses);
			while (selectionCoursesResultSet.next()) {
				if (selectionCoursesResultSet.getObject(1) == null)
					continue;
				String idCourseSt = selectionCoursesResultSet.getObject(1).toString();
				Long idCourse = Long.valueOf(idCourseSt);
				oldCoursesIds.add(idCourse);
				coursesIdsParItinerairesId.get(idItineraire).add(idCourse);
				if (selectionCoursesResultSet.getObject(2) == null)
					continue;
				String idMissionSt = selectionCoursesResultSet.getObject(2).toString();
				Long idMission = Long.valueOf(idMissionSt);
				missionsIdsParItinerairesId.get(idItineraire).add(idMission);
				if (coursesIdsParMissionsId.get(idMission) == null)
					coursesIdsParMissionsId.put(idMission, new HashSet<Long>());
				coursesIdsParMissionsId.get(idMission).add(idCourse);
			}
		}
		
		if (oldCoursesIds.size() == 0)
			return;
		
		Set<Long>            oldTMIds           = new HashSet<Long>();
		Map<Long, Set<Date>> datesParTMIds      = new HashMap<Long, Set<Date>>();
		Map<Long, Set<Long>> coursesIdsParTMIds = new HashMap<Long, Set<Long>>();
		String selectionTMs = "SELECT timetableId, vehicleJourneyId from " + getDatabaseSchema() + ".timetablevehiclejourney WHERE vehicleJourneyId IN "+getSQLList(oldCoursesIds)+";";
		Statement selectionTMsStatement = connexion.createStatement();
		ResultSet selectionTMsResultSet = selectionTMsStatement.executeQuery(selectionTMs);
		while (selectionTMsResultSet.next()) {
			if (selectionTMsResultSet.getObject(1) == null)
				continue;
			if (selectionTMsResultSet.getObject(2) == null)
				continue;
			String tmIdSt = selectionTMsResultSet.getObject(1).toString();
			Long tmId = Long.valueOf(tmIdSt);
			oldTMIds.add(tmId);
			String courseIdSt = selectionTMsResultSet.getObject(2).toString();
			Long courseId = Long.valueOf(courseIdSt);
			if (coursesIdsParTMIds.get(tmId) == null)
				coursesIdsParTMIds.put(tmId, new HashSet<Long>());
			coursesIdsParTMIds.get(tmId).add(courseId);
		}
		for (Long tmId : oldTMIds) {
			datesParTMIds.put(tmId, new HashSet<Date>());
			String selectionDates = "SELECT date from " + getDatabaseSchema() + ".timetable_date WHERE timetableid = '"+tmId.toString()+"';";
			Statement selectionDatesStatement = connexion.createStatement();
			ResultSet selectionDatesResultSet = selectionDatesStatement.executeQuery(selectionDates);
			while (selectionDatesResultSet.next()) {
				if (selectionDatesResultSet.getObject(1) == null)
					continue;
				datesParTMIds.get(tmId).add(selectionDatesResultSet.getDate(1));
			}
			String selectionPeriodes = "SELECT periodStart, periodEnd, (SELECT intdaytypes from " + getDatabaseSchema() + ".timetable WHERE id=timetableid) from " + getDatabaseSchema() + ".timetable_period WHERE timetableid = '"+tmId.toString()+"';";
			Statement selectionPeriodesStatement = connexion.createStatement();
			ResultSet selectionPeriodesResultSet = selectionPeriodesStatement.executeQuery(selectionPeriodes);
			while (selectionPeriodesResultSet.next()) {
				if (selectionPeriodesResultSet.getObject(1) == null)
					continue;
				Date debut = selectionPeriodesResultSet.getDate(1);
				if (selectionPeriodesResultSet.getObject(2) == null)
					continue;
				Date fin = selectionPeriodesResultSet.getDate(2);
				int intDayTypes = 0;
				if (selectionPeriodesResultSet.getObject(3) != null)
					intDayTypes = selectionPeriodesResultSet.getInt(3);
				datesParTMIds.get(tmId).addAll(getDates(debut, fin, intDayTypes));
			}
		}
		
		Map<Long, Set<Date>> tousDatesParTMIds      = new HashMap<Long, Set<Date>>();
		String selectionTousTMs = "SELECT id from " + getDatabaseSchema() + ".timetable;";
		Statement selectionTousTMsStatement = connexion.createStatement();
		ResultSet selectionTousTMsResultSet = selectionTousTMsStatement.executeQuery(selectionTousTMs);
		while (selectionTousTMsResultSet.next()) {
			if (selectionTousTMsResultSet.getObject(1) == null)
				continue;
			Long tmId = selectionTousTMsResultSet.getLong(1);
			tousDatesParTMIds.put(tmId, new HashSet<Date>());
			String selectionDates = "SELECT date from " + getDatabaseSchema() + ".timetable_date WHERE timetableid = '"+tmId.toString()+"';";
			Statement selectionDatesStatement = connexion.createStatement();
			ResultSet selectionDatesResultSet = selectionDatesStatement.executeQuery(selectionDates);
			while (selectionDatesResultSet.next()) {
				if (selectionDatesResultSet.getObject(1) == null)
					continue;
				tousDatesParTMIds.get(tmId).add(selectionDatesResultSet.getDate(1));
			}
			String selectionPeriodes = "SELECT periodStart, periodEnd, (SELECT intdaytypes from " + getDatabaseSchema() + ".timetable WHERE id=timetableid) from " + getDatabaseSchema() + ".timetable_period WHERE timetableid = '"+tmId.toString()+"';";
			Statement selectionPeriodesStatement = connexion.createStatement();
			ResultSet selectionPeriodesResultSet = selectionPeriodesStatement.executeQuery(selectionPeriodes);
			while (selectionPeriodesResultSet.next()) {
				if (selectionPeriodesResultSet.getObject(1) == null)
					continue;
				Date debut = selectionPeriodesResultSet.getDate(1);
				if (selectionPeriodesResultSet.getObject(2) == null)
					continue;
				Date fin = selectionPeriodesResultSet.getDate(2);
				int intDayTypes = 0;
				if (selectionPeriodesResultSet.getObject(3) != null)
					intDayTypes = selectionPeriodesResultSet.getInt(3);
				tousDatesParTMIds.get(tmId).addAll(getDates(debut, fin, intDayTypes));
			}
		}
		
		Set<Date> newDates = getDates(lectureEchange.getTableauxMarche());
		Set<Long> coursesModifiees = new HashSet<Long>();
		Set<Long> tmModifiees = new HashSet<Long>();
		gestionSequence.setConnexion(connexion);
		gestionSequence.initialiser();

		for (Long tmId : oldTMIds) 
		{
			Set<Date> tmDates = datesParTMIds.get(tmId);
			Set<Date> diferenceDates = difference(tmDates, newDates);
			// 3 CAS se présentent :
			// CAS 1 : diferenceDates == tmDates                           : Ne rien faire
			// CAS 2 : diferenceDates == null                              : Décrocher
			// CAS 3 : diferenceDates != null && diferenceDates != tmDates : Décrocher // Racrocher
			if (equals(diferenceDates, tmDates))
			{
				//logger.debug("case 1 : diferenceDates == tmDates");
				continue;
			}
			if ((coursesIdsParTMIds.get(tmId) == null) || (coursesIdsParTMIds.get(tmId).size() == 0))				
				continue;
			coursesModifiees.addAll(coursesIdsParTMIds.get(tmId));
			tmModifiees.add(tmId);
			String decrochage = "DELETE from " + getDatabaseSchema() + ".timetablevehiclejourney WHERE timetableId='"+tmId.longValue()+"' AND vehicleJourneyId IN "+getSQLList(coursesIdsParTMIds.get(tmId))+";";
			////// TRANSACTIONNELL
			//String decrochage = "UPDATE timetablevehiclejourney SET tobedeleted='true' WHERE timetableId='"+tmId.longValue()+"' AND vehicleJourneyId IN "+getSQLList(coursesIdsParTMIds.get(tmId))+";";
			Statement decrochageStatement = connexion.createStatement();
			int nombreDeCoursesDecrochees = decrochageStatement.executeUpdate(decrochage);
			logger.debug("INCREMENTAL : NOMBRE DE COURSES DECROCHEES DE LA TM "+tmId.longValue()+" : "+nombreDeCoursesDecrochees+". La difference possede : "+diferenceDates.size()+" dates.");
			if (diferenceDates.size() == 0)
			{
				//logger.debug("diferenceDates.size() == 0");
				continue;
			}
			logger.debug("differenceDates" + diferenceDates);
			logger.debug("tmDates" + tmDates);
			long newTmId = 0l;
			for (Long key : tousDatesParTMIds.keySet()) {
                            if (equals(diferenceDates, tousDatesParTMIds.get(key))) {
                                newTmId = key.longValue();
                                //logger.debug("set newTmId to TMkey");
                                break;
                            }
                        }
			if (newTmId == 0) {
				String tmSQL = "SELECT objectid, \"comment\" from " + getDatabaseSchema() + ".timetable WHERE id = '"+tmId+"';";
				Statement tmStatement = connexion.createStatement();
				ResultSet tmResultSet = tmStatement.executeQuery(tmSQL);
				if (tmResultSet.next())
					if (tmResultSet.getObject(1) != null) {
						//
						connexion.createStatement().execute(
								"ALTER TABLE " + getDatabaseSchema() 
								+ ".timetable_date DISABLE TRIGGER ALL");
						
						//
						String tmObjectId = tmResultSet.getObject(1).toString();
						tmObjectId.substring(0, tmObjectId.lastIndexOf(':')+1);
						newTmId = gestionSequence.getNouvelId(tmObjectId);
						tousDatesParTMIds.put(newTmId, new HashSet<Date>());
						tousDatesParTMIds.get(newTmId).addAll(diferenceDates);
						diferenceDates = new TreeSet<Date>(diferenceDates);
						Date first = ((TreeSet<Date>)diferenceDates).first();
						int position = 0;
						String dateInsert = "INSERT INTO " + getDatabaseSchema() + ".timetable_date(timetableid, date, \"position\") VALUES ('"+newTmId+"', '"+sdf3.format(first)+"', '"+position+"');";
						Statement dateInsertStatement = connexion.createStatement();
						position++;
						Date last = ((TreeSet<Date>)diferenceDates).last();

						Date date = new Date(first.getTime()+ 24l*60l*60l*1000l);

						Date ceilling = ((TreeSet<Date>)diferenceDates).ceiling(date);
						String value = "1";
						while ((ceilling != null) && (ceilling.compareTo(last) <= 0)) {
							while (date.before(ceilling)) {
								value += "0";

								date = new Date(date.getTime()+ 24l*60l*60l*1000l);

							}
							value += "1";
							dateInsert = "INSERT INTO " + getDatabaseSchema() + ".timetable_date(timetableid, date, \"position\") VALUES ('"+newTmId+"', '"+sdf3.format(date)+"', '"+position+"');";
							dateInsertStatement = connexion.createStatement();
							dateInsertStatement.executeUpdate(dateInsert);

							position++;							
							date = new Date(date.getTime()+ 24l*60l*60l*1000l);
							ceilling = ((TreeSet<Date>)diferenceDates).ceiling(date);
						}

						BigInteger bigInt = new BigInteger(value);
						String firstDate = sdf1.format(first);
						String lastDate = sdf1.format(last);
						tmObjectId += firstDate + bigInt + lastDate;
						//logger.debug("tmObjectId" + tmObjectId);

						String comment = "FROM "+sdf3.format(first)+" TO "+sdf3.format(last) ;
						String nowDate = sdf2.format(Calendar.getInstance().getTime());
						String tmInsert = "INSERT INTO " + getDatabaseSchema() + ".timetable(id, objectid, objectversion, creationtime, \"comment\") VALUES ('"+newTmId+"', '"+tmObjectId+"', '1', '"+nowDate+"', '"+comment+"');";
						Statement tmInsertStatement = connexion.createStatement();
						tmInsertStatement.executeUpdate(tmInsert);
						//
						connexion.createStatement().execute("ALTER TABLE " + getDatabaseSchema() + ".timetable_date ENABLE TRIGGER ALL");
						//
					}
					else
						;// THIS CANNOT OCCUR
				else
					;// THIS CANNOT OCCUR
			}

			for (Long coId : coursesIdsParTMIds.get(tmId)) 
			{

				String idLien = ""+gestionSequence.getNouvelId("tmObjectId");
				String lienInsert = "INSERT INTO " + getDatabaseSchema() + ".timetablevehiclejourney(id, timetableId, vehicleJourneyId) VALUES('"+idLien+"', '"+newTmId+"', '"+coId.longValue()+"');";
				Statement lienInsertStatement = connexion.createStatement();
				lienInsertStatement.executeUpdate(lienInsert);
			}
		}
		gestionSequence.actualiser();
		/***********************************************************************************************************************/
		//connexion.commit();
		/***********************************************************************************************************************/
		// NETOYAGE : effacer les coursesModifiees qui n'ont plus de TMs (ainsi que leurs horaires, missions, itineraies , etc.)
		if (coursesModifiees.size() == 0)
			return;
		Set<Long> coursesAEffacer = new HashSet<Long>(coursesModifiees);
		String coursesAEffacerSQL = "SELECT DISTINCT vehicleJourneyId from " + getDatabaseSchema() + ".timetablevehiclejourney WHERE vehicleJourneyId IN "+getSQLList(coursesModifiees)+";";
		////// TRANSACTIONNELL
		//String coursesAEffacerSQL = "SELECT DISTINCT vehicleJourneyId FROM timetablevehiclejourney WHERE vehicleJourneyId IN "+getSQLList(coursesModifiees)+" AND tobedeleted='false';";
		Statement coursesAEffacerSt = connexion.createStatement();
		ResultSet coursesAEffacerRS = coursesAEffacerSt.executeQuery(coursesAEffacerSQL);
		while (coursesAEffacerRS.next())
			if (coursesAEffacerRS.getObject(1) != null)
				coursesAEffacer.remove(coursesAEffacerRS.getLong(1));
		if (coursesAEffacer.size() == 0)
			return;
		// EFFACER LES HORAIRES
		String effacerHoraire = "DELETE from " + getDatabaseSchema() + ".vehicleJourneyatstop WHERE vehicleJourneyId IN "+getSQLList(coursesAEffacer)+";";
		////// TRANSACTIONNELL
		//String effacerHoraire = "UPDATE vehicleJourneyatstop SET tobedeleted='true' WHERE vehicleJourneyId IN "+getSQLList(coursesAEffacer)+";";
		Statement effacerHoraireSt = connexion.createStatement();
		effacerHoraireSt.executeUpdate(effacerHoraire);
		// DETERMINER LES MISSIONS ET LES ITINERAIRES A EFFACER AVANT D'EFFACER LES courses;
		Set<Long> missionsAeffacer = new HashSet<Long>();
		Set<Long> itinerairesAeffacer = new HashSet<Long>();
		String missionItinerrairesSQL = "SELECT routeId, journeyPatternId from " + getDatabaseSchema() + ".vehiclejourney WHERE id IN "+getSQLList(coursesAEffacer)+";";
		Statement missionItinerrairesSt = connexion.createStatement();
		ResultSet missionItinerrairesRS = missionItinerrairesSt.executeQuery(missionItinerrairesSQL);
		while (missionItinerrairesRS.next()) {
			if (missionItinerrairesRS.getObject(1) != null)
				itinerairesAeffacer.add(Long.valueOf(missionItinerrairesRS.getObject(1).toString()));
			if (missionItinerrairesRS.getObject(2) != null)
				missionsAeffacer.add(Long.valueOf(missionItinerrairesRS.getObject(2).toString()));
		}
		// EFFACER LES COURSES
		String effacerCourses = "DELETE from " + getDatabaseSchema() + ".vehiclejourney WHERE id IN "+getSQLList(coursesAEffacer)+";";
		////// TRANSACTIONNELL
		//String effacerCourses = "UPDATE vehiclejourney SET tobedeleted='true' WHERE id IN "+getSQLList(coursesAEffacer)+";";
		Statement effacerCoursesSt = connexion.createStatement();
		effacerCoursesSt.executeUpdate(effacerCourses);
		logger.debug("COURSES EFFACEES : "+getSQLList(coursesAEffacer));
		/***********************************************************************************************************************/
		//connexion.commit();
		/***********************************************************************************************************************/
		// EFFACER LES MISSIONS
		if (missionsAeffacer.size() > 0) {
			Set<Long> missionsAeffacer2 = new HashSet<Long>();
			String missionsSQL = "SELECT journeyPatternId from " + getDatabaseSchema() + ".vehiclejourney WHERE journeyPatternId IN "+getSQLList(missionsAeffacer)+";";
			////// TRANSACTIONNELL
			//String missionsSQL = "SELECT journeyPatternId FROM vehiclejourney WHERE journeyPatternId IN "+getSQLList(missionsAeffacer)+" AND tobedeleted='false';";
			Statement missionsSt = connexion.createStatement();
			ResultSet missionsRS = missionsSt.executeQuery(missionsSQL);
			while (missionsRS.next())
				if (missionsRS.getObject(1) != null)
					missionsAeffacer2.add(Long.valueOf(missionsRS.getObject(1).toString()));
			missionsAeffacer.removeAll(missionsAeffacer2);
		}
		if (missionsAeffacer.size() > 0) {
			String effacerMissions = "DELETE from " + getDatabaseSchema() + ".journeypattern WHERE id IN "+getSQLList(missionsAeffacer)+";";
			////// TRANSACTIONNELL
			//String effacerMissions = "UPDATE journeypattern SET tobedeleted='true' WHERE id IN "+getSQLList(missionsAeffacer)+";";
			Statement effacerMissionsSt = connexion.createStatement();
			effacerMissionsSt.executeUpdate(effacerMissions);
		}
		// EFFACER LES ITINERAIRES ET LEURS ARRETS ET HORAIRES
		if (itinerairesAeffacer.size() > 0) {
			Set<Long> itinerairesAeffacer2 = new HashSet<Long>();
			String itinerairesSQL = "SELECT routeId from " + getDatabaseSchema() + ".vehiclejourney WHERE routeId IN "+getSQLList(itinerairesAeffacer)+";";
			////// TRANSACTIONNELL
			//String itinerairesSQL = "SELECT routeId FROM vehiclejourney WHERE routeId IN "+getSQLList(itinerairesAeffacer)+" AND tobedeleted='false';";
			Statement itinerairesSt = connexion.createStatement();
			ResultSet itinerairesRS = itinerairesSt.executeQuery(itinerairesSQL);
			while (itinerairesRS.next())
				if (itinerairesRS.getObject(1) != null)
					itinerairesAeffacer2.add(Long.valueOf(itinerairesRS.getObject(1).toString()));
			itinerairesAeffacer.removeAll(itinerairesAeffacer2);
		}
		Set<Long> arretsAEffacer = new HashSet<Long>();
		if (itinerairesAeffacer.size() > 0) {
			String arretsSQL = "SELECT id from " + getDatabaseSchema() + ".stoppoint WHERE routeId IN "+getSQLList(itinerairesAeffacer)+";";
			Statement arretsSt = connexion.createStatement();
			ResultSet arretsRS = arretsSt.executeQuery(arretsSQL);
			while (arretsRS.next())
				if (arretsRS.getObject(1) != null)
					arretsAEffacer.add(Long.valueOf(arretsRS.getObject(1).toString()));
		}
		if (arretsAEffacer.size() > 0) {
			String horairesSQL = "DELETE from " + getDatabaseSchema() + ".vehiclejourneyatstop WHERE stopPointId IN "+getSQLList(arretsAEffacer)+";";
			////// TRANSACTIONNELL
			//String horairesSQL = "UPDATE vehiclejourneyatstop SET tobedeleted='true' WHERE stopPointId IN "+getSQLList(arretsAEffacer)+";";
			Statement horairesSt = connexion.createStatement();
			if (horairesSt.executeUpdate(horairesSQL) > 0)
				logger.error("CES HORAIRES AURAIENT DUS ETRE DEJA EFFACES !!");
		}
		if (itinerairesAeffacer.size() > 0) {
			String arretsSQL = "DELETE from " + getDatabaseSchema() + ".stoppoint WHERE routeId IN "+getSQLList(itinerairesAeffacer)+";";
			////// TRANSACTIONNELL
			//String arretsSQL = "UPDATE stoppoint SET tobedeleted='true' WHERE routeId IN "+getSQLList(itinerairesAeffacer)+";";
			Statement arretsSt = connexion.createStatement();
			arretsSt.executeUpdate(arretsSQL);
			String itiSQL = "DELETE from " + getDatabaseSchema() + ".route WHERE id IN "+getSQLList(itinerairesAeffacer)+";";
			////// TRANSACTIONNELL
			//String itiSQL = "UPDATE route SET tobedeleted='true' WHERE id IN "+getSQLList(itinerairesAeffacer)+";";
			logger.debug("ITINERAIRES EFFACES : "+getSQLList(itinerairesAeffacer));
			Statement itiSt = connexion.createStatement();
			itiSt.executeUpdate(itiSQL);
			// TODO. Effacer les zones, itl et correspondances liées aux stoppoint effacées..
		}
		// NETOYAGE : effacer les tmModifiees qui n'ont plus de courses
		if (tmModifiees.size() == 0)
			return;
		Set<Long> tmAEffacer = new HashSet<Long>(tmModifiees);
		String tmAEffacerSQL = "SELECT DISTINCT timetableId from " + getDatabaseSchema() + ".timetablevehiclejourney WHERE timetableId IN "+getSQLList(tmModifiees)+";";
		////// TRANSACTIONNELL
		//String coursesAEffacerSQL = "SELECT DISTINCT timetableId FROM timetablevehiclejourney WHERE timetableId IN "+getSQLList(tmModifiees)+" AND tobedeleted='false';";
		Statement tmAEffacerSt = connexion.createStatement();
		ResultSet tmAEffacerRS = tmAEffacerSt.executeQuery(tmAEffacerSQL);
		while (tmAEffacerRS.next())
			if (tmAEffacerRS.getObject(1) != null)
				tmAEffacer.remove(tmAEffacerRS.getLong(1));
		if (tmAEffacer.size() == 0)
			return;
		final String effacerDateSQL = "DELETE from " + getDatabaseSchema() + ".timetable_date WHERE timetableid IN "+getSQLList(tmAEffacer)+";";
		////// TRANSACTIONNELL
		//final String effacerDateSQL = "UPDATE timetable_date SET tobedeleted='true' WHERE timetableid IN "+getSQLList(tmAEffacer)+";";
		final Statement effacerDateSt = connexion.createStatement();
		effacerDateSt.executeUpdate(effacerDateSQL);
		final String effacerPeriodSQL = "DELETE from " + getDatabaseSchema() + ".timetable_period WHERE timetableid IN "+getSQLList(tmAEffacer)+";";
		////// TRANSACTIONNELL
		//final String effacerPeriodSQL = "UPDATE timetable_period SET tobedeleted='true' WHERE timetableid IN "+getSQLList(tmAEffacer)+";";
		final Statement effacerPeriodSt = connexion.createStatement();
		effacerPeriodSt.executeUpdate(effacerPeriodSQL);
		String effacerTM = "DELETE from " + getDatabaseSchema() + ".timetable WHERE id IN "+getSQLList(tmAEffacer)+";";
		////// TRANSACTIONNELL
		//String effacerTM = "UPDATE timetable SET tobedeleted='true' WHERE id IN "+getSQLList(tmAEffacer)+";";
		Statement effacerTMSt = connexion.createStatement();
		effacerTMSt.executeUpdate(effacerTM);
		/***********************************************************************************************************************/
		//connexion.commit();
		/***********************************************************************************************************************/
	}
	
	private void analyserTM2(final ILectureEchange lectureEchange, final Connection connexion) throws SQLException {
		if (!etatDifference.isLigneConnue())
			return;
		
		long idLigne = etatDifference.getIdLigneConnue();
		
		Set<Long>            oldItinerairesIds           = new HashSet<Long>();
		Set<Long>            oldCoursesIds               = new HashSet<Long>();
		Map<Long, Set<Long>> coursesIdsParItinerairesId  = new HashMap<Long, Set<Long>>();
		Map<Long, Set<Long>> missionsIdsParItinerairesId = new HashMap<Long, Set<Long>>();
		Map<Long, Set<Long>> coursesIdsParMissionsId     = new HashMap<Long, Set<Long>>();
		String    selectionItineraires = "SELECT id from " + getDatabaseSchema() + ".route WHERE lineId='"+idLigne+"';";
		Statement selectionItinerairesStatement = connexion.createStatement();
		ResultSet selectionItinerairesResultSet = selectionItinerairesStatement.executeQuery(selectionItineraires);
		while (selectionItinerairesResultSet.next()) {
			if (selectionItinerairesResultSet.getObject(1) == null)
				continue;
			String idItineraireSt = selectionItinerairesResultSet.getObject(1).toString();
			Long idItineraire = Long.valueOf(idItineraireSt);
			oldItinerairesIds.add(idItineraire);
			coursesIdsParItinerairesId.put(idItineraire, new HashSet<Long>());
			missionsIdsParItinerairesId.put(idItineraire, new HashSet<Long>());
			String selectionCourses = "SELECT id, journeyPatternId from " + getDatabaseSchema() + ".vehiclejourney WHERE routeId='"+idItineraireSt+"';";
			Statement selectionCoursesStatement = connexion.createStatement();
			ResultSet selectionCoursesResultSet = selectionCoursesStatement.executeQuery(selectionCourses);
			while (selectionCoursesResultSet.next()) {
				if (selectionCoursesResultSet.getObject(1) == null)
					continue;
				String idCourseSt = selectionCoursesResultSet.getObject(1).toString();
				Long idCourse = Long.valueOf(idCourseSt);
				oldCoursesIds.add(idCourse);
				coursesIdsParItinerairesId.get(idItineraire).add(idCourse);
				if (selectionCoursesResultSet.getObject(2) == null)
					continue;
				String idMissionSt = selectionCoursesResultSet.getObject(2).toString();
				Long idMission = Long.valueOf(idMissionSt);
				missionsIdsParItinerairesId.get(idItineraire).add(idMission);
				if (coursesIdsParMissionsId.get(idMission) == null)
					coursesIdsParMissionsId.put(idMission, new HashSet<Long>());
				coursesIdsParMissionsId.get(idMission).add(idCourse);
			}
		}
		
		if (oldCoursesIds.size() == 0)
			return;
		
		Set<Long>            oldTMIds           = new HashSet<Long>();
		Map<Long, Set<Date>> datesParTMIds      = new HashMap<Long, Set<Date>>();
		Map<Long, Set<Long>> coursesIdsParTMIds = new HashMap<Long, Set<Long>>();
		String selectionTMs = "SELECT timetableId, vehicleJourneyId from " + getDatabaseSchema() + ".timetablevehiclejourney WHERE vehicleJourneyId IN "+getSQLList(oldCoursesIds)+";";
		Statement selectionTMsStatement = connexion.createStatement();
		ResultSet selectionTMsResultSet = selectionTMsStatement.executeQuery(selectionTMs);
		while (selectionTMsResultSet.next()) {
			if (selectionTMsResultSet.getObject(1) == null)
				continue;
			if (selectionTMsResultSet.getObject(2) == null)
				continue;
			String tmIdSt = selectionTMsResultSet.getObject(1).toString();
			Long tmId = Long.valueOf(tmIdSt);
			oldTMIds.add(tmId);
			String courseIdSt = selectionTMsResultSet.getObject(2).toString();
			Long courseId = Long.valueOf(courseIdSt);
			if (coursesIdsParTMIds.get(tmId) == null)
				coursesIdsParTMIds.put(tmId, new HashSet<Long>());
			coursesIdsParTMIds.get(tmId).add(courseId);
		}
		for (Long tmId : oldTMIds) {
			datesParTMIds.put(tmId, new HashSet<Date>());
			String selectionDates = "SELECT date from " + getDatabaseSchema() + ".timetable_date WHERE timetableid = '"+tmId.toString()+"';";
			Statement selectionDatesStatement = connexion.createStatement();
			ResultSet selectionDatesResultSet = selectionDatesStatement.executeQuery(selectionDates);
			while (selectionDatesResultSet.next()) {
				if (selectionDatesResultSet.getObject(1) == null)
					continue;
				datesParTMIds.get(tmId).add(selectionDatesResultSet.getDate(1));
			}
			String selectionPeriodes = "SELECT periodStart, periodEnd, (SELECT intdaytypes from " + getDatabaseSchema() + ".timetable WHERE id=timetableid) from " + getDatabaseSchema() + ".timetable_period WHERE timetableid = '"+tmId.toString()+"';";
			Statement selectionPeriodesStatement = connexion.createStatement();
			ResultSet selectionPeriodesResultSet = selectionPeriodesStatement.executeQuery(selectionPeriodes);
			while (selectionPeriodesResultSet.next()) {
				if (selectionPeriodesResultSet.getObject(1) == null)
					continue;
				Date debut = selectionPeriodesResultSet.getDate(1);
				if (selectionPeriodesResultSet.getObject(2) == null)
					continue;
				Date fin = selectionPeriodesResultSet.getDate(2);
				int intDayTypes = 0;
				if (selectionPeriodesResultSet.getObject(3) != null)
					intDayTypes = selectionPeriodesResultSet.getInt(3);
				datesParTMIds.get(tmId).addAll(getDates(debut, fin, intDayTypes));
			}
		}
		
		Map<Long, Set<Date>> tousDatesParTMIds      = new HashMap<Long, Set<Date>>();
		String selectionTousTMs = "SELECT id from " + getDatabaseSchema() + ".timetable;";
		Statement selectionTousTMsStatement = connexion.createStatement();
		ResultSet selectionTousTMsResultSet = selectionTousTMsStatement.executeQuery(selectionTousTMs);
		while (selectionTousTMsResultSet.next()) {
			if (selectionTousTMsResultSet.getObject(1) == null)
				continue;
			Long tmId = selectionTousTMsResultSet.getLong(1);
			tousDatesParTMIds.put(tmId, new HashSet<Date>());
			String selectionDates = "SELECT date from " + getDatabaseSchema() + ".timetable_date WHERE timetableid = '"+tmId.toString()+"';";
			Statement selectionDatesStatement = connexion.createStatement();
			ResultSet selectionDatesResultSet = selectionDatesStatement.executeQuery(selectionDates);
			while (selectionDatesResultSet.next()) {
				if (selectionDatesResultSet.getObject(1) == null)
					continue;
				tousDatesParTMIds.get(tmId).add(selectionDatesResultSet.getDate(1));
			}
			String selectionPeriodes = "SELECT periodStart, periodEnd, (SELECT intdaytypes from " + getDatabaseSchema() + ".timetable WHERE id=timetableid) from " + getDatabaseSchema() + ".timetable_period WHERE timetableid = '"+tmId.toString()+"';";
			Statement selectionPeriodesStatement = connexion.createStatement();
			ResultSet selectionPeriodesResultSet = selectionPeriodesStatement.executeQuery(selectionPeriodes);
			while (selectionPeriodesResultSet.next()) {
				if (selectionPeriodesResultSet.getObject(1) == null)
					continue;
				Date debut = selectionPeriodesResultSet.getDate(1);
				if (selectionPeriodesResultSet.getObject(2) == null)
					continue;
				Date fin = selectionPeriodesResultSet.getDate(2);
				int intDayTypes = 0;
				if (selectionPeriodesResultSet.getObject(3) != null)
					intDayTypes = selectionPeriodesResultSet.getInt(3);
				tousDatesParTMIds.get(tmId).addAll(getDates(debut, fin, intDayTypes));
			}
		}
		
		String correction = "UPDATE " + getDatabaseSchema() + ".timetablevehiclejourney SET tobedeleted='false';";
		Statement correctionStatement = connexion.createStatement();
		correctionStatement.executeUpdate(correction);
		
		Set<Date> newDates = getDates(lectureEchange.getTableauxMarche());
		Set<Long> coursesModifiees = new HashSet<Long>();
		Set<Long> tmModifiees = new HashSet<Long>();
		gestionSequence.setConnexion(connexion);
		gestionSequence.initialiser();

		for (Long tmId : oldTMIds) 
		{
			Set<Date> tmDates = datesParTMIds.get(tmId);
			Set<Date> diferenceDates = difference(tmDates, newDates);
			// 3 CAS se présentent :
			// CAS 1 : diferenceDates == tmDates                           : Ne rien faire
			// CAS 2 : diferenceDates == null                              : Décrocher
			// CAS 3 : diferenceDates != null && diferenceDates != tmDates : Décrocher // Racrocher
			if (equals(diferenceDates, tmDates))
				continue;
			if ((coursesIdsParTMIds.get(tmId) == null) || (coursesIdsParTMIds.get(tmId).size() == 0))
				continue;
			coursesModifiees.addAll(coursesIdsParTMIds.get(tmId));
			tmModifiees.add(tmId);
			String decrochage = "UPDATE " + getDatabaseSchema() + ".timetablevehiclejourney SET tobedeleted='true' WHERE timetableId='"+tmId.longValue()+"' AND vehicleJourneyId IN "+getSQLList(coursesIdsParTMIds.get(tmId))+";";
			Statement decrochageStatement = connexion.createStatement();
			int nombreDeCoursesDecrochees = decrochageStatement.executeUpdate(decrochage);
			logger.debug("INCREMENTAL : NOMBRE DE COURSES DECROCHEES DE LA TM "+tmId.longValue()+" : "+nombreDeCoursesDecrochees+". La difference possede : "+diferenceDates.size()+" dates.");
			if (diferenceDates.size() == 0)
				continue;
			
			long newTmId = 0l;
			for (Long key : tousDatesParTMIds.keySet())
				if (equals(diferenceDates, tousDatesParTMIds.get(key))) {
					newTmId = key.longValue();
					break;
				}
			if (newTmId == 0 ) {
				String tmSQL = "SELECT objectid, \"comment\" from " + getDatabaseSchema() + ".timetable WHERE id = '"+tmId+"';";
				Statement tmStatement = connexion.createStatement();
				ResultSet tmResultSet = tmStatement.executeQuery(tmSQL);
				if (tmResultSet.next())
					if (tmResultSet.getObject(1) != null) {
						String tmObjectId = tmResultSet.getObject(1).toString();
						tmObjectId.substring(0, tmObjectId.lastIndexOf(':')+1);
						newTmId = gestionSequence.getNouvelId(tmObjectId);
						tousDatesParTMIds.put(newTmId, new HashSet<Date>());
						tousDatesParTMIds.get(newTmId).addAll(diferenceDates);
						diferenceDates = new TreeSet<Date>(diferenceDates);
						Date first = ((TreeSet<Date>)diferenceDates).first();
						int position = 0;
						String dateInsert = "INSERT INTO " + getDatabaseSchema() + ".timetable_date(timetableid, date, \"position\") VALUES ('"+newTmId+"', '"+sdf3.format(first)+"', '"+position+"');";
						Statement dateInsertStatement = connexion.createStatement();
						dateInsertStatement.executeUpdate(dateInsert);
						position++;
						Date last = ((TreeSet<Date>)diferenceDates).last();
						Date date = new Date(first.getTime()+ 24l*60l*60l*1000l);
						Date ceilling = ((TreeSet<Date>)diferenceDates).ceiling(date);
						String value = "1";
						while (ceilling.compareTo(last) <= 0) {
							while (date.before(ceilling)) {
								value += "0";
								date = new Date(date.getTime()+ 24l*60l*60l*1000l);
							}
							value += "1";
							dateInsert = "INSERT INTO " + getDatabaseSchema() + ".timetable_date(timetableid, date, \"position\") VALUES ('"+newTmId+"', '"+sdf3.format(date)+"', '"+position+"');";
							dateInsertStatement = connexion.createStatement();
							dateInsertStatement.executeUpdate(dateInsert);
							position++;
							date = new Date(date.getTime()+ 24l*60l*60l*1000l);
							ceilling = ((TreeSet<Date>)diferenceDates).ceiling(date);
						}
						BigInteger bigInt = new BigInteger(value);
						String firstDate = sdf1.format(first);
						String lastDate = sdf1.format(last);
						tmObjectId += firstDate + bigInt + lastDate;
						String comment = "FROM "+sdf3.format(first)+" TO "+sdf3.format(last) ;
						String nowDate = sdf2.format(Calendar.getInstance().getTime());
						String tmInsert = "INSERT INTO " + getDatabaseSchema() + ".timetable(id, objectid, objectversion, creationtime, \"comment\") VALUES ('"+newTmId+"', '"+tmObjectId+"', '1', '"+nowDate+"', '"+comment+"');";
						Statement tmInsertStatement = connexion.createStatement();
						tmInsertStatement.executeUpdate(tmInsert);
					}
					else
						;// THIS CANNOT OCCUR
				else
					;// THIS CANNOT OCCUR
			}
			for (Long coId : coursesIdsParTMIds.get(tmId)) {
				String idLien = ""+gestionSequence.getNouvelId("tmObjectId");
				String lienInsert = "INSERT INTO " + getDatabaseSchema() + ".timetablevehiclejourney(id, timetableId, vehicleJourneyId) VALUES('"+idLien+"', '"+newTmId+"', '"+coId.longValue()+"');";
				Statement lienInsertStatement = connexion.createStatement();
				lienInsertStatement.executeUpdate(lienInsert);
			}
		}
		//gestionSequence.actualiser();
		/***********************************************************************************************************************/
		//connexion.commit();
		/***********************************************************************************************************************/
		// NETOYAGE : effacer les coursesModifiees qui n'ont plus de TMs (ainsi que leurs horaires, missions, itineraies , etc.)
		if (coursesModifiees.size() == 0)
			return;
		Set<Long> coursesAEffacer = new HashSet<Long>(coursesModifiees);
		String coursesAEffacerSQL = "SELECT DISTINCT vehicleJourneyId from " + getDatabaseSchema() + ".timetablevehiclejourney WHERE vehicleJourneyId IN "+getSQLList(coursesModifiees)+";";
		////// TRANSACTIONNELL
		//String coursesAEffacerSQL = "SELECT DISTINCT vehicleJourneyId FROM timetablevehiclejourney WHERE vehicleJourneyId IN "+getSQLList(coursesModifiees)+" AND tobedeleted='false';";
		Statement coursesAEffacerSt = connexion.createStatement();
		ResultSet coursesAEffacerRS = coursesAEffacerSt.executeQuery(coursesAEffacerSQL);
		while (coursesAEffacerRS.next())
			if (coursesAEffacerRS.getObject(1) != null)
				coursesAEffacer.remove(coursesAEffacerRS.getLong(1));
		if (coursesAEffacer.size() == 0)
			return;
		// EFFACER LES HORAIRES
		String effacerHoraire = "DELETE from " + getDatabaseSchema() + ".vehicleJourneyatstop WHERE vehicleJourneyId IN "+getSQLList(coursesAEffacer)+";";
		////// TRANSACTIONNELL
		//String effacerHoraire = "UPDATE vehicleJourneyatstop SET tobedeleted='true' WHERE vehicleJourneyId IN "+getSQLList(coursesAEffacer)+";";
		Statement effacerHoraireSt = connexion.createStatement();
		effacerHoraireSt.executeUpdate(effacerHoraire);
		// DETERMINER LES MISSIONS ET LES ITINERAIRES A EFFACER AVANT D'EFFACER LES courses;
		Set<Long> missionsAeffacer = new HashSet<Long>();
		Set<Long> itinerairesAeffacer = new HashSet<Long>();
		String missionItinerrairesSQL = "SELECT routeId, journeyPatternId from " + getDatabaseSchema() + ".vehiclejourney WHERE id IN "+getSQLList(coursesAEffacer)+";";
		Statement missionItinerrairesSt = connexion.createStatement();
		ResultSet missionItinerrairesRS = missionItinerrairesSt.executeQuery(missionItinerrairesSQL);
		while (missionItinerrairesRS.next()) {
			if (missionItinerrairesRS.getObject(1) != null)
				itinerairesAeffacer.add(Long.valueOf(missionItinerrairesRS.getObject(1).toString()));
			if (missionItinerrairesRS.getObject(2) != null)
				missionsAeffacer.add(Long.valueOf(missionItinerrairesRS.getObject(2).toString()));
		}
		// EFFACER LES COURSES
		String effacerCourses = "DELETE from " + getDatabaseSchema() + ".vehiclejourney WHERE id IN "+getSQLList(coursesAEffacer)+";";
		////// TRANSACTIONNELL
		//String effacerCourses = "UPDATE vehiclejourney SET tobedeleted='true' WHERE id IN "+getSQLList(coursesAEffacer)+";";
		Statement effacerCoursesSt = connexion.createStatement();
		effacerCoursesSt.executeUpdate(effacerCourses);
		logger.debug("COURSES EFFACEES : "+getSQLList(coursesAEffacer));
		/***********************************************************************************************************************/
		connexion.commit();
		/***********************************************************************************************************************/
		// EFFACER LES MISSIONS
		if (missionsAeffacer.size() > 0) {
			Set<Long> missionsAeffacer2 = new HashSet<Long>();
			String missionsSQL = "SELECT journeyPatternId from " + getDatabaseSchema() + ".vehiclejourney WHERE journeyPatternId IN "+getSQLList(missionsAeffacer)+";";
			////// TRANSACTIONNELL
			//String missionsSQL = "SELECT journeyPatternId FROM vehiclejourney WHERE journeyPatternId IN "+getSQLList(missionsAeffacer)+" AND tobedeleted='false';";
			Statement missionsSt = connexion.createStatement();
			ResultSet missionsRS = missionsSt.executeQuery(missionsSQL);
			while (missionsRS.next())
				if (missionsRS.getObject(1) != null)
					missionsAeffacer2.add(Long.valueOf(missionsRS.getObject(1).toString()));
			missionsAeffacer.removeAll(missionsAeffacer2);
		}
		if (missionsAeffacer.size() > 0) {
			String effacerMissions = "DELETE from " + getDatabaseSchema() + ".journeypattern WHERE id IN "+getSQLList(missionsAeffacer)+";";
			////// TRANSACTIONNELL
			//String effacerMissions = "UPDATE journeypattern SET tobedeleted='true' WHERE id IN "+getSQLList(missionsAeffacer)+";";
			Statement effacerMissionsSt = connexion.createStatement();
			effacerMissionsSt.executeUpdate(effacerMissions);
		}
		// EFFACER LES ITINERAIRES ET LEURS ARRETS ET HORAIRES
		if (itinerairesAeffacer.size() > 0) {
			Set<Long> itinerairesAeffacer2 = new HashSet<Long>();
			String itinerairesSQL = "SELECT routeId from " + getDatabaseSchema() + ".vehiclejourney WHERE routeId IN "+getSQLList(itinerairesAeffacer)+";";
			////// TRANSACTIONNELL
			//String itinerairesSQL = "SELECT routeId FROM vehiclejourney WHERE routeId IN "+getSQLList(itinerairesAeffacer)+" AND tobedeleted='false';";
			Statement itinerairesSt = connexion.createStatement();
			ResultSet itinerairesRS = itinerairesSt.executeQuery(itinerairesSQL);
			while (itinerairesRS.next())
				if (itinerairesRS.getObject(1) != null)
					itinerairesAeffacer2.add(Long.valueOf(itinerairesRS.getObject(1).toString()));
			itinerairesAeffacer.removeAll(itinerairesAeffacer2);
		}
		Set<Long> arretsAEffacer = new HashSet<Long>();
		if (itinerairesAeffacer.size() > 0) {
			String arretsSQL = "SELECT id from " + getDatabaseSchema() + ".stoppoint WHERE routeId IN "+getSQLList(itinerairesAeffacer)+";";
			Statement arretsSt = connexion.createStatement();
			ResultSet arretsRS = arretsSt.executeQuery(arretsSQL);
			while (arretsRS.next())
				if (arretsRS.getObject(1) != null)
					arretsAEffacer.add(Long.valueOf(arretsRS.getObject(1).toString()));
		}
		if (arretsAEffacer.size() > 0) {
			String horairesSQL = "DELETE from " + getDatabaseSchema() + ".vehiclejourneyatstop WHERE stopPointId IN "+getSQLList(arretsAEffacer)+";";
			////// TRANSACTIONNELL
			//String horairesSQL = "UPDATE vehiclejourneyatstop SET tobedeleted='true' WHERE stopPointId IN "+getSQLList(arretsAEffacer)+";";
			Statement horairesSt = connexion.createStatement();
			if (horairesSt.executeUpdate(horairesSQL) > 0)
				logger.error("CES HORAIRES AURAIENT DUS ETRE DEJA EFFACES !!");
		}
		if (itinerairesAeffacer.size() > 0) {
			String arretsSQL = "DELETE from " + getDatabaseSchema() + ".stoppoint WHERE routeId IN "+getSQLList(itinerairesAeffacer)+";";
			////// TRANSACTIONNELL
			//String arretsSQL = "UPDATE stoppoint SET tobedeleted='true' WHERE routeId IN "+getSQLList(itinerairesAeffacer)+";";
			Statement arretsSt = connexion.createStatement();
			arretsSt.executeUpdate(arretsSQL);
			String itiSQL = "DELETE from " + getDatabaseSchema() + ".route WHERE id IN "+getSQLList(itinerairesAeffacer)+";";
			////// TRANSACTIONNELL
			//String itiSQL = "UPDATE route SET tobedeleted='true' WHERE id IN "+getSQLList(itinerairesAeffacer)+";";
			logger.debug("ITINERAIRES EFFACES : "+getSQLList(itinerairesAeffacer));
			Statement itiSt = connexion.createStatement();
			itiSt.executeUpdate(itiSQL);
			// TODO. Effacer les zones, itl et correspondances liées aux stoppoint effacées..
		}
		// NETOYAGE : effacer les tmModifiees qui n'ont plus de courses
		if (tmModifiees.size() == 0)
			return;
		Set<Long> tmAEffacer = new HashSet<Long>(tmModifiees);
		String tmAEffacerSQL = "SELECT DISTINCT timetableId from " + getDatabaseSchema() + ".timetablevehiclejourney WHERE timetableId IN "+getSQLList(tmModifiees)+";";
		////// TRANSACTIONNELL
		//String coursesAEffacerSQL = "SELECT DISTINCT timetableId FROM timetablevehiclejourney WHERE timetableId IN "+getSQLList(tmModifiees)+" AND tobedeleted='false';";
		Statement tmAEffacerSt = connexion.createStatement();
		ResultSet tmAEffacerRS = tmAEffacerSt.executeQuery(tmAEffacerSQL);
		while (tmAEffacerRS.next())
			if (tmAEffacerRS.getObject(1) != null)
				tmAEffacer.remove(tmAEffacerRS.getLong(1));
		if (tmAEffacer.size() == 0)
			return;
		final String effacerDateSQL = "DELETE from " + getDatabaseSchema() + ".timetable_date WHERE timetableid IN "+getSQLList(tmAEffacer)+";";
		////// TRANSACTIONNELL
		//final String effacerDateSQL = "UPDATE timetable_date SET tobedeleted='true' WHERE timetableid IN "+getSQLList(tmAEffacer)+";";
		final Statement effacerDateSt = connexion.createStatement();
		effacerDateSt.executeUpdate(effacerDateSQL);
		final String effacerPeriodSQL = "DELETE from " + getDatabaseSchema() + ".timetable_period WHERE timetableid IN "+getSQLList(tmAEffacer)+";";
		////// TRANSACTIONNELL
		//final String effacerPeriodSQL = "UPDATE timetable_period SET tobedeleted='true' WHERE timetableid IN "+getSQLList(tmAEffacer)+";";
		final Statement effacerPeriodSt = connexion.createStatement();
		effacerPeriodSt.executeUpdate(effacerPeriodSQL);
		String effacerTM = "DELETE from " + getDatabaseSchema() + ".timetable WHERE id IN "+getSQLList(tmAEffacer)+";";
		////// TRANSACTIONNELL
		//String effacerTM = "UPDATE timetable SET tobedeleted='true' WHERE id IN "+getSQLList(tmAEffacer)+";";
		Statement effacerTMSt = connexion.createStatement();
		effacerTMSt.executeUpdate(effacerTM);
		/***********************************************************************************************************************/
		connexion.commit();
		/***********************************************************************************************************************/
	}
	
	private boolean equals(Set<Date> dates1, Set<Date> dates2) {
		if ((dates1 == null) && (dates2 == null))
			return true;
		if ((dates1 == null) || (dates2 == null))
			return false;
		for (Date date1 : dates1)
			if (!isIn(date1, dates2))
				return false;
		for (Date date2 : dates2)
			if (!isIn(date2, dates1))
				return false;		
		return true;
	}


	private boolean isIn(Date date1, Set<Date> dates2) 
	{
		for (Date date2 : dates2)
			if (date1.compareTo(date2) == 0)
				return true;
		return false;
	}


	private Set<Date> difference(Set<Date> tmDates, Set<Date> newDates) 
	{
		Set<Date> result = new HashSet<Date>();
		for (Date date : tmDates) {
			boolean isIn = true;
			for (Date newDate : newDates)
				if (date.compareTo(newDate) == 0) {
					isIn = false;
					break;
				}
			if (isIn)
				result.add(date);
		}
		return result;
	}
	
	private Set<Date> getDates(List<TableauMarche> tableauxMarche) 
	{
		Set<Date> result = new HashSet<Date>();
		for (TableauMarche tableauMarche : tableauxMarche)
			result.addAll(getDates(tableauMarche));
		return result;
	}
	
	private Set<Date> getDates(TableauMarche tableauMarche) {
		Set<Date> result = new HashSet<Date>();
		result.addAll(tableauMarche.getDates());
		for (Periode periode : tableauMarche.getPeriodes())
			result.addAll(getDates(periode.getDebut(), periode.getFin(), tableauMarche.getIntDayTypes().intValue()));
		return result;
	}

	private Set<Date> getDates(Date debut, Date fin, int intDayTypes) 
	{

		Set<Date> dates = new HashSet<Date>();
		fin = new Date(fin.getTime() + 86400000l);
		for (Date date = debut; date.before(fin); ) 
		{
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(date);
			switch (calendar.get(Calendar.DAY_OF_WEEK)) 
			{
				case Calendar.MONDAY:
					int monday = (int)Math.pow(2, chouette.schema.types.DayTypeType.MONDAY.ordinal());
					//logger.debug("MONDAY ordinal : " + chouette.schema.types.DayTypeType.MONDAY.ordinal());
					if ((intDayTypes & monday) == monday)
						dates.add(date);
					//else 
					//delete(date, dates);
					break;
				case Calendar.TUESDAY:
					int tuesday = (int)Math.pow(2, chouette.schema.types.DayTypeType.TUESDAY.ordinal());
					if ((intDayTypes & tuesday) == tuesday)
						dates.add(date);
					//else 
					//delete(date, dates);
					break;
				case Calendar.WEDNESDAY:
					int wednesday = (int)Math.pow(2, chouette.schema.types.DayTypeType.WEDNESDAY.ordinal());
					if ((intDayTypes & wednesday) == wednesday)
						dates.add(date);
					//else 
					//delete(date, dates);
					break;
				case Calendar.THURSDAY:
					int thursday = (int)Math.pow(2, chouette.schema.types.DayTypeType.THURSDAY.ordinal());
					if ((intDayTypes & thursday) == thursday)
						dates.add(date);
					//else 
					//delete(date, dates);
					break;
				case Calendar.FRIDAY:
					int friday = (int)Math.pow(2, chouette.schema.types.DayTypeType.FRIDAY.ordinal());
					if ((intDayTypes & friday) == friday)
						dates.add(date);
					//else 
					//delete(date, dates);
					break;
				case Calendar.SATURDAY:
					int saturday = (int)Math.pow(2, chouette.schema.types.DayTypeType.SATURDAY.ordinal());
					if ((intDayTypes & saturday) == saturday)
						dates.add(date);
					//else 
					//delete(date, dates);
					break;
				case Calendar.SUNDAY:
					int sunday = (int)Math.pow(2, chouette.schema.types.DayTypeType.SUNDAY.ordinal());
					if ((intDayTypes & sunday) == sunday)
						dates.add(date);
					//else 
					//delete(date, dates);
					break;
			}
			date = new Date(date.getTime() + 86400000l); //((long)24)*((long)60)*((long)60)*((long)1000));
		}
		return dates;
	}
	
	
	private String getSQLList(Set<Long> ids) {		//
		// EFFACER LES TM

		String sqlList = "(";
		if (ids != null)
			for (Long id : ids)
				sqlList += "'"+id.toString()+"', ";
		if (sqlList.length() > 2)
			sqlList = sqlList.substring(0, sqlList.length()-2);
		sqlList += ")";
		return sqlList;
	}
	
	private List<String> analyserItineraires(final ILectureEchange lectureEchange, final Connection connexion) throws SQLException {
		final List<Itineraire> itineraires = lectureEchange.getItineraires();
		List<String> objectIdItineraires = new ArrayList<String>();
		if (itineraires != null)
			for (Itineraire itineraire : itineraires)
				objectIdItineraires.add(itineraire.getObjectId());
		final String selectionItineraires = "SELECT r.objectid, r.id from " + getDatabaseSchema() + ".route r;";
		final Statement sqlStatement = connexion.createStatement();
		final ResultSet rs = sqlStatement.executeQuery(selectionItineraires);
		final Map<String, Long> exItineraireIdParObjectId = new Hashtable<String, Long>();
		final List<String> objIdItinerairesNouvelles = new ArrayList<String>(objectIdItineraires);
		while (rs.next()) {
			final String objectId = rs.getObject(1).toString();
			final Long id = Long.parseLong(rs.getObject(2).toString());
			exItineraireIdParObjectId.put(objectId, id);
			if (objIdItinerairesNouvelles.contains(objectId))
				objIdItinerairesNouvelles.remove(objectId);
		}
		etatDifference.setExItineraireIdParObjectId(exItineraireIdParObjectId);
		etatDifference.setNvObjectIdItineraire(objIdItinerairesNouvelles);
		return objIdItinerairesNouvelles;
	}
	
	private void analyserMissions(final ILectureEchange lectureEchange, final Connection connexion) throws SQLException {
		final List<Mission> missions = lectureEchange.getMissions();
		List<String> objectIdMissions = new ArrayList<String>();
		if (missions != null)
			for (Mission mission : missions)
				objectIdMissions.add(mission.getObjectId());
		final String selectionMissions = "SELECT j.objectid, j.id from " + getDatabaseSchema() + ".journeypattern j;";
		final Statement sqlStatement = connexion.createStatement();
		final ResultSet rs = sqlStatement.executeQuery(selectionMissions);
		final Map<String, Long> exMissionIdParObjectId = new Hashtable<String, Long>();
		final List<String> objIdMissionsNouvelles = new ArrayList<String>(objectIdMissions);
		while (rs.next()) {
			final String objectId = rs.getObject(1).toString();
			final Long id = Long.parseLong(rs.getObject(2).toString());
			exMissionIdParObjectId.put(objectId, id);
			while (objIdMissionsNouvelles.remove(objectId))
				;
		}
		etatDifference.setExMissionIdParObjectId(exMissionIdParObjectId);
		etatDifference.setNvObjectIdMission(objIdMissionsNouvelles);
	}
	
	private void analyserCourses(final ILectureEchange lectureEchange, final Connection connexion) throws SQLException {
		final List<Course> courses = lectureEchange.getCourses();
		List<String> objectIdCourses = new ArrayList<String>();
		if (courses != null)
			for (Course course : courses)
				objectIdCourses.add(course.getObjectId());
		final String selectionCourses = "SELECT v.objectid, v.id from " + getDatabaseSchema() + ".vehiclejourney v;";
		final Statement sqlStatement = connexion.createStatement();
		final ResultSet rs = sqlStatement.executeQuery(selectionCourses);
		final Map<String, Long> exCourseIdParObjectId = new Hashtable<String, Long>();
		final List<String> objIdCoursesNouvelles = new ArrayList<String>(objectIdCourses);
		while (rs.next()) {
			final String objectId = rs.getObject(1).toString();
			final Long id = Long.parseLong(rs.getObject(2).toString());
			exCourseIdParObjectId.put(objectId, id);
			while (objIdCoursesNouvelles.remove(objectId))
				;
		}
		etatDifference.setExCourseIdParObjectId(exCourseIdParObjectId);
		etatDifference.setNvObjectIdCourse(objIdCoursesNouvelles);
	}
	
	private void analyserArrets(final ILectureEchange lectureEchange, final Connection connexion, List<String> objIdItinerairesNouvelles) throws SQLException {
            String msg = "";
            final List<ArretItineraire> arrets = lectureEchange.getArrets();
            List<String> objectIdArrets = new ArrayList<String>();
            if (arrets != null)
                for (ArretItineraire arret : arrets)
                    objectIdArrets.add(arret.getObjectId());
                final String selectionArrets = "SELECT s.objectid, s.id, s.position from " + getDatabaseSchema() + ".stoppoint s;";
                final Statement sqlStatement = connexion.createStatement();
		final ResultSet rs = sqlStatement.executeQuery(selectionArrets);
		final Map<String, Long> exArretIdParObjectId = new Hashtable<String, Long>();
		final List<String> objIdArretsNouvelles = new ArrayList<String>(objectIdArrets);
		while (rs.next()) {
                    final String objectId = rs.getObject(1).toString();
                    final Long id = Long.parseLong(rs.getObject(2).toString());
                    final int position = Integer.parseInt(rs.getString(3));
                    exArretIdParObjectId.put(objectId, id);
                    if (objIdArretsNouvelles.contains(objectId)) {
                        for (ArretItineraire arret : arrets)
                            if (arret.getObjectId().equals(objectId)) {
                                if (arret.getPosition() != position) {
                                    String newMsg = arret.getObjectId();
                                    for (int i = 0; i < 5; i++)
                                        newMsg = newMsg.replaceFirst("SP", "_");
                                    newMsg = "\tL'arret ("+newMsg+") a changer de position dans son itineraire.";
                                    logger.error(newMsg);
                                    msg += newMsg + "\n";
                                }
                                break;
                            }
                        objIdArretsNouvelles.remove(objectId);
                    }
                }
		if (objIdArretsNouvelles.size() > 0)
                    for (String objectId : objIdArretsNouvelles) {
                        String code = objectId.substring(objectId.lastIndexOf(':'));
                        code = code.substring(0, code.lastIndexOf("SP"));
                        boolean conflict = true;
                        for (String itiObjectId : objIdItinerairesNouvelles) {
                            String newCode = itiObjectId.substring(itiObjectId.lastIndexOf(':'));
                            if (code.indexOf(newCode) >= 0) {
                                conflict = false;
                                continue;
                            }
                        }
                        if (conflict) {
                            for (int i = 0; i < 5; i++)
                                objectId = objectId.replaceFirst("SP", "_");
                            String newMsg = "\tL'arret ("+objectId+") a été ajouté à son itineraire.";
                            logger.error(newMsg);
                            msg += newMsg + "\n";
                        }
                    }
		etatDifference.setExArretIdParObjectId(exArretIdParObjectId);
		etatDifference.setNvObjectIdArret(objIdArretsNouvelles);
		if (msg.length() > 0)
                    ;//throw new SQLException(msg);
	}


	public void setDatabaseSchema(String databaseShema) {
            this.databaseSchema = databaseShema;
        }


	public String getDatabaseSchema() {
            return databaseSchema;
        }
}
