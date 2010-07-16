package fr.certu.chouette.dao.jdbc;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigInteger;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import fr.certu.chouette.dao.IPurge;

public class Purge extends HibernateDaoSupport implements IPurge {

	private static final Logger logger = Logger.getLogger(Purge.class);
	private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	private String databaseSchema;

	@Override
	public PurgeReport purgeAllItems(Date boundaryDate, boolean before) throws SQLException {
		HashMap<String, String> summary = new LinkedHashMap<String, String>();
				
		PurgeBoundaryState boundaryState = PurgeBoundaryState.getBeforeState(before);
		
		PurgeReport purgeReport = new PurgeReport(boundaryDate, before);
		
		String purge = sdf.format(boundaryDate);
		
		//set next date to create new limit in period
		Calendar cal = Calendar.getInstance();
		cal.setTime(boundaryDate);
		cal.add(Calendar.DAY_OF_MONTH, boundaryState.getIncrement());
		Date nextDate = cal.getTime();
		String nextPurge = sdf.format(nextDate);

		final Session session = getSession();
		
		//count vehicleJourneyAtStop before purge
		String countVehicleJourneyAtStop = "SELECT COUNT(*) FROM "+ databaseSchema +".vehiclejourneyatstop ;";
		int numberOfVehicleJourneyAtStopBeforePurge = ((BigInteger)session.createSQLQuery(countVehicleJourneyAtStop).uniqueResult()).intValue();
		
		//count physical StopPoint before purge
		String countPhysicalStopPoint = "SELECT COUNT(*) FROM "+ databaseSchema +".stoparea WHERE (areatype = 'BoardingPosition' OR areatype = 'Quay');";
		int numberOfPhysicalStopPointBeforePurge = ((BigInteger)session.createSQLQuery(countPhysicalStopPoint).uniqueResult()).intValue();
		
		//count commercial StopPoint before purge
		String countCommercialStopPoint = "SELECT COUNT(*) FROM "+ databaseSchema +".stoparea WHERE (areatype = 'CommercialStopPoint');";
		int numberOfCommercialStopPointBeforePurge = ((BigInteger)session.createSQLQuery(countCommercialStopPoint).uniqueResult()).intValue();
		
		//count ConnectionLink before purge
		String countConnectionLink = "SELECT COUNT(*) FROM "+ databaseSchema +".connectionlink ;";
		int numberOfConnectionLinkBeforePurge = ((BigInteger)session.createSQLQuery(countConnectionLink).uniqueResult()).intValue();
		
		//delete dates in purge period
		String deleteDates = "DELETE FROM " + databaseSchema + ".timetable_date WHERE date " + boundaryState.getComparisonOperator() + " '" + purge + "';";
		int numberOfDates = session.createSQLQuery(deleteDates).executeUpdate();
		summary.put("dates", numberOfDates+"");

		//renumbering of timetable_date positions in timetable
		String selectDates = "SELECT timetableid, \"position\" FROM " + databaseSchema + ".timetable_date ORDER BY timetableid, \"position\";";
		List<Object[]> datesList = session.createSQLQuery(selectDates).list();
		String timetableId = null;
		int count = 0;
		for (Object[] date : datesList)
		{
			String id = ((BigInteger)date[0]).toString();
			int position = (Integer)date[1];
			if (id.equals(timetableId))
			{
				count++;
			}
			else
			{
				count = 0;
			}
			timetableId = id;
			String updateDates2 = "UPDATE " + databaseSchema + ".timetable_date SET \"position\" = '" + count + "' WHERE timetableid = '" + id + "' AND \"position\" = '" + position + "';";
			session.createSQLQuery(updateDates2).executeUpdate();
		}

		//delete periods in purge period 
		String deletePeriodes = "DELETE FROM " + databaseSchema + ".timetable_period WHERE " + boundaryState.getSecondBound() + " " + boundaryState.getComparisonOperator() + " '" + purge + "';";
		int numberOfPeriodes = session.createSQLQuery(deletePeriodes).executeUpdate();
		summary.put("periods", numberOfPeriodes+"");

		//updating boundary date of periods with a part in purge period
		String updatePeriodes = "UPDATE " + databaseSchema + ".timetable_period SET " + boundaryState.getFirstBound() + " = '" + nextPurge + "' WHERE " + boundaryState.getFirstBound() + " " + boundaryState.getComparisonOperator() + " '" + purge + "';";
		int numberOfPeriodesUpdates = session.createSQLQuery(updatePeriodes).executeUpdate();
		summary.put("shorten.periods", numberOfPeriodesUpdates+"");

		//renumbering of timetable_periods in timetable
		String selectPeriodes = "SELECT timetableid, \"position\" FROM " + databaseSchema + ".timetable_period ORDER BY timetableid, \"position\";";
		List<Object[]> periodesList = session.createSQLQuery(selectPeriodes).list();
		timetableId = null;
		count = 0;
		for(Object[] period : periodesList)
		{
			String id = ((BigInteger)period[0]).toString();
			int position = (Integer)period[1];
			if (id.equals(timetableId))
			{
				count++;
			}
			else
			{
				count = 0;
			}
			timetableId = id;
			String updatePeriodes2 = "UPDATE " + databaseSchema + ".timetable_period SET \"position\" = '" + count + "' WHERE timetableid = '" + id + "' AND \"position\" = '" + position + "';";
			session.createSQLQuery(updatePeriodes2).executeUpdate();
		}

		//delete timetableVehicleJourney links referencing timetables without dates of periods  
		String deleteLinks = "DELETE FROM " + databaseSchema + ".timetablevehiclejourney WHERE timetableId NOT IN ((SELECT timetableid FROM " + databaseSchema + ".timetable_date) UNION (SELECT timetableid FROM " + databaseSchema + ".timetable_period));";
		int numberOfLinks = session.createSQLQuery(deleteLinks).executeUpdate();
		summary.put("timetable.link", numberOfLinks+"");


		//delete TM not connected with dates or periods
		String deleteTMs = "DELETE FROM " + databaseSchema + ".timetable WHERE id NOT IN ((SELECT timetableId FROM " + databaseSchema + ".timetablevehiclejourney) UNION (SELECT timetableId FROM " + databaseSchema + ".timetable_period) UNION (SELECT timetableId FROM " + databaseSchema + ".timetable_date));";
		int numberOfTMs = session.createSQLQuery(deleteTMs).executeUpdate();
		summary.put("timetable", numberOfTMs+"");
		
		//delete vehicleJourneyAtStop which aren't referenced in vehicleJourney table
		String deleteHoraires = "DELETE FROM " + databaseSchema + ".vehiclejourneyatstop WHERE vehicleJourneyId NOT IN (SELECT vehicleJourneyId FROM " + databaseSchema + ".timetablevehiclejourney);";
		int numberOfHoraires = session.createSQLQuery(deleteHoraires).executeUpdate();
		summary.put("vehicleJourneyAtStop", numberOfHoraires+"");

		//delete vehicleJourney which aren't referenced in timetableVehicleJourney table
		String deleteCourses = "DELETE FROM " + databaseSchema + ".vehiclejourney WHERE id NOT IN (SELECT vehicleJourneyId FROM " + databaseSchema + ".timetablevehiclejourney);";
		int numberOfCourses = session.createSQLQuery(deleteCourses).executeUpdate();
		summary.put("vehicleJourney", numberOfCourses+"");

		//delete journeyPatterns which aren't referenced in journeyPatterns table
		String deleteMissions = "DELETE FROM " + databaseSchema + ".journeypattern WHERE id NOT IN (SELECT journeyPatternId FROM " + databaseSchema + ".vehiclejourney);";
		int numberOfMissions = session.createSQLQuery(deleteMissions).executeUpdate();
		summary.put("journeyPattern", numberOfMissions+"");

		//list physical connection links to delete
		String listPhysicalConnectionLinks = "SELECT name, objectid FROM " + databaseSchema + ".connectionlink WHERE departureid IN (SELECT stopareaId FROM " + databaseSchema + ".stoppoint WHERE routeId NOT IN (SELECT routeId FROM " + databaseSchema + ".vehiclejourney)) OR arrivalid IN (SELECT stopareaId FROM " + databaseSchema + ".stoppoint WHERE routeId NOT IN (SELECT routeId FROM " + databaseSchema + ".vehiclejourney));";
		List<Object[]> physicalConnectionLinkList = session.createSQLQuery(listPhysicalConnectionLinks).list();
		for(Object[] physicalConnectionLink : physicalConnectionLinkList)
		{
			String name = (String)(physicalConnectionLink[0]); 
			String objectId = (String)(physicalConnectionLink[1]);
			purgeReport.addConnectionLink(name, objectId);
		}
		
		//connection links (Quay or BoardingPosition)
		String deleteConnectionLinks = "DELETE FROM " + databaseSchema + ".connectionlink WHERE departureid IN (SELECT stopareaId FROM " + databaseSchema + ".stoppoint WHERE routeId NOT IN (SELECT routeId FROM " + databaseSchema + ".vehiclejourney)) OR arrivalid IN (SELECT stopareaId FROM " + databaseSchema + ".stoppoint WHERE routeId NOT IN (SELECT routeId FROM " + databaseSchema + ".vehiclejourney));";
		int numberOfConnectionLinks = session.createSQLQuery(deleteConnectionLinks).executeUpdate();

		//delete stoppoint which aren't referenced in routes table
		String deleteArretItineraires = "DELETE FROM " + databaseSchema + ".stoppoint WHERE routeId NOT IN (SELECT routeId FROM " + databaseSchema + ".vehiclejourney);";
		int numberOfArretItineraires = session.createSQLQuery(deleteArretItineraires).executeUpdate();
		summary.put("stoppointOnRoute", numberOfArretItineraires+"");

		//delete routes which aren't referenced in vehicleJourneys table
		String deleteItineraires = "DELETE FROM " + databaseSchema + ".route WHERE id NOT IN (SELECT routeId FROM " + databaseSchema + ".vehiclejourney);";
		int numberOfItineraires = session.createSQLQuery(deleteItineraires).executeUpdate();
		summary.put("route", numberOfItineraires+"");
		//LES RETOUR DES ITINERAIRES ????

		//list physical StopPlaces to delete
		String listArretPhysiques = "SELECT name, registrationnumber FROM " + databaseSchema + ".stoparea WHERE (areatype = 'BoardingPosition' OR areatype = 'Quay') AND (id NOT IN (SELECT stopAreaId FROM " + databaseSchema + ".stoppoint));";
		List<Object[]> arretsPhysiquesList = session.createSQLQuery(listArretPhysiques).list();
		for(Object[] arretPhysique : arretsPhysiquesList)
		{
			String name = (String)(arretPhysique[0]); 
			String registrationNumber = (String)(arretPhysique[1]);
			purgeReport.addPhysicalStopPoint(name, registrationNumber);
		}
		
		//delete physical StopPlaces which aren't referenced in stoppoint table
		String deleteArretPhysiques = "DELETE FROM " + databaseSchema + ".stoparea WHERE (areatype = 'BoardingPosition' OR areatype = 'Quay') AND (id NOT IN (SELECT stopAreaId FROM " + databaseSchema + ".stoppoint));";
		int numberOfArretPhysiques = session.createSQLQuery(deleteArretPhysiques).executeUpdate();
		summary.put("boardingPosition", numberOfArretPhysiques+"");

		//list commercial connection links to delete
		String listCommercialConnectionLinks = "SELECT name, objectid FROM " + databaseSchema + ".connectionlink WHERE departureid IN (SELECT id FROM " + databaseSchema + ".stoparea WHERE (areatype = 'CommercialStopPoint') AND (id NOT IN (SELECT parentId FROM " + databaseSchema + ".stoparea WHERE parentId IS NOT NULL))) OR arrivalid IN (SELECT id FROM " + databaseSchema + ".stoparea WHERE (areatype = 'CommercialStopPoint') AND (id NOT IN (SELECT parentId FROM " + databaseSchema + ".stoparea WHERE parentId IS NOT NULL)));";
		List<Object[]> commercialConnectionLinkList = session.createSQLQuery(listCommercialConnectionLinks).list();
		for(Object[] commercialConnectionLink : commercialConnectionLinkList)
		{
			String name = (String)(commercialConnectionLink[0]); 
			String objectId = (String)(commercialConnectionLink[1]);
			purgeReport.addConnectionLink(name, objectId);
		}
		
		//connection links (CommercialLinks)
		String deleteCommercialConnectionLinks = "DELETE FROM " + databaseSchema + ".connectionlink WHERE departureid IN (SELECT id FROM " + databaseSchema + ".stoparea WHERE (areatype = 'CommercialStopPoint') AND (id NOT IN (SELECT parentId FROM " + databaseSchema + ".stoparea WHERE parentId IS NOT NULL))) OR arrivalid IN (SELECT id FROM " + databaseSchema + ".stoparea WHERE (areatype = 'CommercialStopPoint') AND (id NOT IN (SELECT parentId FROM " + databaseSchema + ".stoparea WHERE parentId IS NOT NULL)));";
		numberOfConnectionLinks += session.createSQLQuery(deleteCommercialConnectionLinks).executeUpdate();
		
		//list commercial StopPlaces to delete
		String listArretCommercials = "SELECT name, registrationnumber FROM " + databaseSchema + ".stoparea WHERE (areatype = 'CommercialStopPoint') AND (id NOT IN (SELECT parentId FROM " + databaseSchema + ".stoparea WHERE parentId IS NOT NULL));";
		List<Object[]> arretsCommercialsList = session.createSQLQuery(listArretCommercials).list();
		StringBuilder sb = new StringBuilder();
		for(Object[] arretCommercial : arretsCommercialsList)
		{
			String name = (String)(arretCommercial[0]); 
			String registrationNumber = (String)(arretCommercial[1]);
			purgeReport.addCommercialStopPoint(name, registrationNumber);
			sb.append(name+", ");
		}
		
		//delete commercial stopPlaces which have no children stoparea
		String deleteArretCommercials = "DELETE FROM " + databaseSchema + ".stoparea WHERE (areatype = 'CommercialStopPoint') AND (id NOT IN (SELECT parentId FROM " + databaseSchema + ".stoparea WHERE parentId IS NOT NULL));";
		int numberOfArretCommercials = session.createSQLQuery(deleteArretCommercials).executeUpdate();
		summary.put("commercialStopPlace", numberOfArretCommercials+"");

		//list stopPlace connection links to delete
		String listStopPlaceConnectionLinks = "SELECT name, objectid FROM " + databaseSchema + ".connectionlink WHERE departureid IN (SELECT id FROM " + databaseSchema + ".stoparea WHERE (areatype = 'StopPlace') AND (id NOT IN (SELECT parentId FROM " + databaseSchema + ".stoparea WHERE parentId IS NOT NULL))) OR arrivalid IN (SELECT id FROM " + databaseSchema + ".stoparea WHERE (areatype = 'StopPlace') AND (id NOT IN (SELECT parentId FROM " + databaseSchema + ".stoparea WHERE parentId IS NOT NULL)));";
		List<Object[]> stopPlaceConnectionLinkList = session.createSQLQuery(listStopPlaceConnectionLinks).list();
		for(Object[] stopPlaceConnectionLink : stopPlaceConnectionLinkList)
		{
			String name = (String)(stopPlaceConnectionLink[0]); 
			String objectId = (String)(stopPlaceConnectionLink[1]);
			purgeReport.addConnectionLink(name, objectId);
		}
		
		//connection links (StopPlacesLinks)
		String deleteStopPlaceConnectionLinks = "DELETE FROM " + databaseSchema + ".connectionlink WHERE departureid IN (SELECT id FROM " + databaseSchema + ".stoparea WHERE (areatype = 'StopPlace') AND (id NOT IN (SELECT parentId FROM " + databaseSchema + ".stoparea WHERE parentId IS NOT NULL))) OR arrivalid IN (SELECT id FROM " + databaseSchema + ".stoparea WHERE (areatype = 'StopPlace') AND (id NOT IN (SELECT parentId FROM " + databaseSchema + ".stoparea WHERE parentId IS NOT NULL)));";
		numberOfConnectionLinks += session.createSQLQuery(deleteStopPlaceConnectionLinks).executeUpdate();

		//delete stopplace stoparea which have no children stoparea
		String deletePolesEchanges = "DELETE FROM " + databaseSchema + ".stoparea WHERE (areatype = 'StopPlace') AND (id NOT IN (SELECT parentId FROM " + databaseSchema + ".stoparea WHERE parentId IS NOT NULL));";
		int numberOfPolesEchanges = session.createSQLQuery(deletePolesEchanges).executeUpdate();
		//in Chouette 2 stopplaces can be chained...
		numberOfConnectionLinks += session.createSQLQuery(deleteStopPlaceConnectionLinks).executeUpdate();
		numberOfPolesEchanges += session.createSQLQuery(deletePolesEchanges).executeUpdate();
		summary.put("stopPlace", numberOfPolesEchanges+"");
		summary.put("connectionLink", numberOfConnectionLinks+"");
		
		String listLignes = "SELECT name, registrationnumber FROM " + databaseSchema + ".line WHERE id NOT IN (SELECT lineId FROM " + databaseSchema + ".route);";
		List<Object[]> lignesList = session.createSQLQuery(listLignes).list();
		StringBuilder sb2 = new StringBuilder();
		for(Object[] ligne : lignesList)
		{
			String name = (String)(ligne[0]); 
			String registrationNumber = (String)(ligne[1]);
			
			purgeReport.addLine(name, registrationNumber);
			sb2.append(name+" ("+registrationNumber+"), ");
			
		}
		
		//delete lines which have no routes
		String deleteLignes = "DELETE FROM " + databaseSchema + ".line WHERE id NOT IN (SELECT lineId FROM " + databaseSchema + ".route);";
		int numberOfLignes = session.createSQLQuery(deleteLignes).executeUpdate();
		summary.put("lines", numberOfLignes+"");

		//count vehicleJourneyAtStop after purge
		int numberOfVehicleJourneyAtStopAfterPurge = ((BigInteger)session.createSQLQuery(countVehicleJourneyAtStop).uniqueResult()).intValue();
		
		//count physical StopPoint after purge
		int numberOfPhysicalStopPointAfterPurge = ((BigInteger)session.createSQLQuery(countPhysicalStopPoint).uniqueResult()).intValue();
		
		//count commercial StopPoint after purge
		int numberOfCommercialStopPointAfterPurge = ((BigInteger)session.createSQLQuery(countCommercialStopPoint).uniqueResult()).intValue();
		
		//count ConnectionLink after purge
		int numberOfConnectionLinkAfterPurge = ((BigInteger)session.createSQLQuery(countConnectionLink).uniqueResult()).intValue();
		
		//report counts and listings
		/*summary.put("vehicleJourneyAtStop.count.before", numberOfVehicleJourneyAtStopBeforePurge+"");
		summary.put("vehicleJourneyAtStop.count.after", numberOfVehicleJourneyAtStopAfterPurge+"");
		summary.put("lines.list", sb2.toString());
		summary.put("commercialStopPlace.list", sb.toString());*/
		
		purgeReport.addCount("vehicle_journey_at_stop_count#", numberOfVehicleJourneyAtStopBeforePurge, numberOfVehicleJourneyAtStopAfterPurge);
		purgeReport.addCount("physical_stop_points_count#", numberOfPhysicalStopPointBeforePurge, numberOfPhysicalStopPointAfterPurge);
		purgeReport.addCount("commercial_stop_points_count#", numberOfCommercialStopPointBeforePurge, numberOfCommercialStopPointAfterPurge);
		purgeReport.addCount("connection_link_count#", numberOfConnectionLinkBeforePurge, numberOfConnectionLinkAfterPurge);
		
		purgeReport.setSummary(summary);
		
		return purgeReport;
	}
	
	public void setDatabaseSchema(String databaseSchema) {
		this.databaseSchema = databaseSchema;
	}
	
	private enum PurgeBoundaryState {
		BEFORE("<=","periodStart","periodEnd",1),
		AFTER(">=","periodEnd","periodStart",-1);
		
		private final String sign;
		private final String firstBound;
		private final String secondBound;
		private final int increment;
		
		private PurgeBoundaryState(String sign, String firstBound, String secondBound, int increment){
			this.sign = sign;
			this.firstBound = firstBound;
			this.secondBound = secondBound;
			this.increment = increment;
		}
		
		public String getComparisonOperator() {
			return sign;
		}

		public String getFirstBound() {
			return firstBound;
		}

		public String getSecondBound() {
			return secondBound;
		}

		public int getIncrement() {
			return increment;
		}
		
		public static PurgeBoundaryState getBeforeState(boolean before){
			if(before){
				return PurgeBoundaryState.BEFORE;
			}
			return PurgeBoundaryState.AFTER;
		}
	}
}
