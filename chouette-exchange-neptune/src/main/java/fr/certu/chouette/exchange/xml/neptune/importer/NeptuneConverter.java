/**
 * Projet CHOUETTE
 *
 * ce projet est sous license libre
 * voir LICENSE.txt pour plus de details
 *
 */
package fr.certu.chouette.exchange.xml.neptune.importer;


import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;
import chouette.schema.ChouetteLineDescription;
import chouette.schema.ChouettePTNetworkTypeType;
import fr.certu.chouette.exchange.xml.neptune.importer.producer.AccessLinkProducer;
import fr.certu.chouette.exchange.xml.neptune.importer.producer.AccessPointProducer;
import fr.certu.chouette.exchange.xml.neptune.importer.producer.AreaCentroidProducer;
import fr.certu.chouette.exchange.xml.neptune.importer.producer.CompanyProducer;
import fr.certu.chouette.exchange.xml.neptune.importer.producer.ConnectionLinkProducer;
import fr.certu.chouette.exchange.xml.neptune.importer.producer.FacilityProducer;
import fr.certu.chouette.exchange.xml.neptune.importer.producer.GroupOfLineProducer;
import fr.certu.chouette.exchange.xml.neptune.importer.producer.JourneyPatternProducer;
import fr.certu.chouette.exchange.xml.neptune.importer.producer.LineProducer;
import fr.certu.chouette.exchange.xml.neptune.importer.producer.PTLinkProducer;
import fr.certu.chouette.exchange.xml.neptune.importer.producer.PTNetworkProducer;
import fr.certu.chouette.exchange.xml.neptune.importer.producer.RouteProducer;
import fr.certu.chouette.exchange.xml.neptune.importer.producer.StopAreaProducer;
import fr.certu.chouette.exchange.xml.neptune.importer.producer.StopPointProducer;
import fr.certu.chouette.exchange.xml.neptune.importer.producer.TimeSlotProducer;
import fr.certu.chouette.exchange.xml.neptune.importer.producer.TimetableProducer;
import fr.certu.chouette.exchange.xml.neptune.importer.producer.VehicleJourneyProducer;
import fr.certu.chouette.exchange.xml.neptune.report.NeptuneReportItem;
import fr.certu.chouette.model.neptune.AccessLink;
import fr.certu.chouette.model.neptune.AccessPoint;
import fr.certu.chouette.model.neptune.AreaCentroid;
import fr.certu.chouette.model.neptune.Company;
import fr.certu.chouette.model.neptune.ConnectionLink;
import fr.certu.chouette.model.neptune.Facility;
import fr.certu.chouette.model.neptune.GroupOfLine;
import fr.certu.chouette.model.neptune.JourneyPattern;
import fr.certu.chouette.model.neptune.Line;
import fr.certu.chouette.model.neptune.PTLink;
import fr.certu.chouette.model.neptune.PTNetwork;
import fr.certu.chouette.model.neptune.RestrictionConstraint;
import fr.certu.chouette.model.neptune.Route;
import fr.certu.chouette.model.neptune.StopArea;
import fr.certu.chouette.model.neptune.StopPoint;
import fr.certu.chouette.model.neptune.TimeSlot;
import fr.certu.chouette.model.neptune.Timetable;
import fr.certu.chouette.model.neptune.VehicleJourney;
import fr.certu.chouette.plugin.report.Report;
import fr.certu.chouette.plugin.report.ReportItem;
import org.apache.log4j.Logger;

/**
 * note : repartir du fr.certu.chouette.service.validation.util.MainSchemaProducer 
 * 
 * @author michel
 *
 */
public class NeptuneConverter
{
	@Getter @Setter private LineProducer lineProducer;
	@Getter @Setter private RouteProducer routeProducer;
	@Getter @Setter private PTNetworkProducer networkProducer;
	@Getter @Setter private CompanyProducer companyProducer;
	@Getter @Setter private JourneyPatternProducer journeyPatternProducer;
	@Getter @Setter private PTLinkProducer ptLinkProducer;
	@Getter @Setter private VehicleJourneyProducer vehicleJourneyProducer;
	@Getter @Setter private StopPointProducer stopPointProducer;
	@Getter @Setter private StopAreaProducer stopAreaProducer;
	@Getter @Setter private AreaCentroidProducer areaCentroidProducer;
	@Getter @Setter private ConnectionLinkProducer connectionLinkProducer;
	@Getter @Setter private TimetableProducer timetableProducer;

	@Getter @Setter private AccessLinkProducer accessLinkProducer;
	@Getter @Setter private AccessPointProducer accessPointProducer;
	@Getter @Setter private GroupOfLineProducer groupOfLineProducer;
	@Getter @Setter private FacilityProducer facilityProducer;
	@Getter @Setter private TimeSlotProducer timeSlotProducer;
        private static Logger logger = Logger.getLogger(NeptuneConverter.class);

	public Line extractLine(ChouettePTNetworkTypeType rootObject, ReportItem parentReport) 
	{
		ReportItem report = new NeptuneReportItem(NeptuneReportItem.KEY.PARSE_OBJECT, Report.STATE.OK,"Line");
		ChouetteLineDescription lineDescription = rootObject.getChouetteLineDescription();
		chouette.schema.Line xmlLine = lineDescription.getLine();

		// modele des producer : voir package fr.certu.chouette.service.validation.util
		Line line = lineProducer.produce(xmlLine,report);

		int count = (line == null? 0 : 1);
		ReportItem countItem = new NeptuneReportItem(NeptuneReportItem.KEY.OBJECT_COUNT, Report.STATE.OK,Integer.toString(count));
		report.addItem(countItem);
		parentReport.addItem(report);
		return line;
	}

	public List<Route> extractRoutes(ChouettePTNetworkTypeType rootObject, ReportItem parentReport) 
	{
		ReportItem report = new NeptuneReportItem(NeptuneReportItem.KEY.PARSE_OBJECT, Report.STATE.OK,"ChouetteRoute");
		ChouetteLineDescription lineDescription = rootObject.getChouetteLineDescription();
		chouette.schema.ChouetteRoute[] xmlRoutes = lineDescription.getChouetteRoute();

		// modele des producer : voir package fr.certu.chouette.service.validation.util

		List<Route> routes = new ArrayList<Route>();

		for(chouette.schema.ChouetteRoute xmlRoute : xmlRoutes)
		{
			Route route = routeProducer.produce(xmlRoute, report);
			routes.add(route);
		}

		int count = (routes == null? 0 : routes.size());
		ReportItem countItem = new NeptuneReportItem(NeptuneReportItem.KEY.OBJECT_COUNT, Report.STATE.OK,Integer.toString(count));
		report.addItem(countItem);
		parentReport.addItem(report);
		return routes;
	}

	public List<Company> extractCompanies(ChouettePTNetworkTypeType rootObject, ReportItem parentReport) 
	{
		ReportItem report = new NeptuneReportItem(NeptuneReportItem.KEY.PARSE_OBJECT, Report.STATE.OK,"Company");
		chouette.schema.Company[] xmlCompanies = rootObject.getCompany();

		// modele des producer : voir package fr.certu.chouette.service.validation.util
		List<Company> companies = new ArrayList<Company>();

		for(chouette.schema.Company xmlCompany : xmlCompanies){
			Company company = companyProducer.produce(xmlCompany, report);
			companies.add(company);
		}

		int count = (companies == null? 0 : companies.size());
		ReportItem countItem = new NeptuneReportItem(NeptuneReportItem.KEY.OBJECT_COUNT, Report.STATE.OK,Integer.toString(count));
		report.addItem(countItem);
		parentReport.addItem(report);
		return companies;
	}

	public PTNetwork extractPTNetwork(ChouettePTNetworkTypeType rootObject, ReportItem parentReport) 
	{
		ReportItem report = new NeptuneReportItem(NeptuneReportItem.KEY.PARSE_OBJECT, Report.STATE.OK,"PTNetwork");
		chouette.schema.PTNetwork xmlPTNetwork = rootObject.getPTNetwork();

		// modele des producer : voir package fr.certu.chouette.service.validation.util
		PTNetwork ptNetwork = networkProducer.produce(xmlPTNetwork, report);

		int count = (ptNetwork == null? 0 : 1);
		ReportItem countItem = new NeptuneReportItem(NeptuneReportItem.KEY.OBJECT_COUNT, Report.STATE.OK,Integer.toString(count));
		report.addItem(countItem);
		parentReport.addItem(report);
		return ptNetwork;
	}

	public List<JourneyPattern> extractJourneyPatterns(ChouettePTNetworkTypeType rootObject, ReportItem parentReport) 
	{
		ReportItem report = new NeptuneReportItem(NeptuneReportItem.KEY.PARSE_OBJECT, Report.STATE.OK,"JourneyPattern");
		ChouetteLineDescription lineDescription = rootObject.getChouetteLineDescription();
		chouette.schema.JourneyPattern[] xmlJourneyPatterns = lineDescription.getJourneyPattern();

		// modele des producer : voir package fr.certu.chouette.service.validation.util

		List<JourneyPattern> journeyPatterns = new ArrayList<JourneyPattern>();

		for(chouette.schema.JourneyPattern xmlJourneyPattern : xmlJourneyPatterns){
			JourneyPattern journeyPattern = journeyPatternProducer.produce(xmlJourneyPattern, report);
			journeyPatterns.add(journeyPattern);
		}

		int count = (journeyPatterns == null? 0 : journeyPatterns.size());
		ReportItem countItem = new NeptuneReportItem(NeptuneReportItem.KEY.OBJECT_COUNT, Report.STATE.OK,Integer.toString(count));
		report.addItem(countItem);
		parentReport.addItem(report);
		return journeyPatterns;
	}

	public List<PTLink> extractPTLinks(ChouettePTNetworkTypeType rootObject, ReportItem parentReport) 
	{
		ReportItem report = new NeptuneReportItem(NeptuneReportItem.KEY.PARSE_OBJECT, Report.STATE.OK,"PtLink");
		ChouetteLineDescription lineDescription = rootObject.getChouetteLineDescription();
		chouette.schema.PtLink[] xmlPTLinks = lineDescription.getPtLink();

		// modele des producer : voir package fr.certu.chouette.service.validation.util

		List<PTLink> ptLinks = new ArrayList<PTLink>();

		for(chouette.schema.PtLink xmlPTLink : xmlPTLinks){
			PTLink ptLink = ptLinkProducer.produce(xmlPTLink, report);
			ptLinks.add(ptLink);
		}

		int count = (ptLinks == null? 0 : ptLinks.size());
		ReportItem countItem = new NeptuneReportItem(NeptuneReportItem.KEY.OBJECT_COUNT, Report.STATE.OK,Integer.toString(count));
		report.addItem(countItem);
		parentReport.addItem(report);
		return ptLinks;
	}

	public List<VehicleJourney> extractVehicleJourneys(ChouettePTNetworkTypeType rootObject, ReportItem parentReport) 
	{
		ReportItem report = new NeptuneReportItem(NeptuneReportItem.KEY.PARSE_OBJECT, Report.STATE.OK,"VehicleJourney");
		ChouetteLineDescription lineDescription = rootObject.getChouetteLineDescription();
		chouette.schema.VehicleJourney[] xmlVehicleJourneys = lineDescription.getVehicleJourney();

		// modele des producer : voir package fr.certu.chouette.service.validation.util

		List<VehicleJourney> vehicleJourneys = new ArrayList<VehicleJourney>();

		for(chouette.schema.VehicleJourney xmlVehicleJourney : xmlVehicleJourneys){
			VehicleJourney vehicleJourney = vehicleJourneyProducer.produce(xmlVehicleJourney, report);
			vehicleJourneys.add(vehicleJourney);
		}

		int count = (vehicleJourneys == null? 0 : vehicleJourneys.size());
		ReportItem countItem = new NeptuneReportItem(NeptuneReportItem.KEY.OBJECT_COUNT, Report.STATE.OK,Integer.toString(count));
		report.addItem(countItem);
		parentReport.addItem(report);
		return vehicleJourneys;
	}

	public List<StopPoint> extractStopPoints(ChouettePTNetworkTypeType rootObject, ReportItem parentReport) 
	{
		ReportItem report = new NeptuneReportItem(NeptuneReportItem.KEY.PARSE_OBJECT, Report.STATE.OK,"StopPoint");
		ChouetteLineDescription lineDescription = rootObject.getChouetteLineDescription();
		chouette.schema.StopPoint[] xmlStopPoints = lineDescription.getStopPoint();

		// modele des producer : voir package fr.certu.chouette.service.validation.util

		List<StopPoint> stopPoints = new ArrayList<StopPoint>();

		for(chouette.schema.StopPoint xmlStopPoint : xmlStopPoints){
			StopPoint stopPoint = stopPointProducer.produce(xmlStopPoint, report);
			stopPoints.add(stopPoint);
		}

		int count = (stopPoints == null? 0 : stopPoints.size());
		ReportItem countItem = new NeptuneReportItem(NeptuneReportItem.KEY.OBJECT_COUNT, Report.STATE.OK,Integer.toString(count));
		report.addItem(countItem);
		parentReport.addItem(report);
		return stopPoints;
	}

	public List<StopArea> extractStopAreas(ChouettePTNetworkTypeType rootObject, ReportItem parentReport) 
	{
		ReportItem report = new NeptuneReportItem(NeptuneReportItem.KEY.PARSE_OBJECT, Report.STATE.OK,"StopArea");
		chouette.schema.StopArea[] xmlStopAreas = rootObject.getChouetteArea().getStopArea();
		ChouetteLineDescription lineDescription = rootObject.getChouetteLineDescription();
		chouette.schema.ITL[] itls = lineDescription.getITL();
		// modele des producer : voir package fr.certu.chouette.service.validation.util

		List<StopArea> stopAreas = new ArrayList<StopArea>();
                
                List<chouette.schema.ITL> usedItls = new ArrayList<chouette.schema.ITL>();
		for(chouette.schema.StopArea xmlStopArea : xmlStopAreas){
			StopArea stopArea = stopAreaProducer.produce(xmlStopArea, report);
			for (chouette.schema.ITL itl : itls) {
				if(stopArea.getObjectId().equals(itl.getAreaId())){
					RestrictionConstraint constraint = new RestrictionConstraint();
					constraint.setAreaId(itl.getAreaId());
					constraint.setLineIdShortCut(itl.getLineIdShortCut());
					constraint.setName(itl.getName());
					stopArea.addRestrictionConstraint(constraint);
                                        usedItls.add(itl);
                                        logger.debug("ITL "+itl.getName()+" ("+itl.getAreaId()+","+itl.getLineIdShortCut()+") HAS A STOP AREA.");
				}
			}
			
			stopAreas.add(stopArea);
		}
                
                StopArea.setUnvalidRestrictionConstraints(null);
                for (chouette.schema.ITL itl : itls) {
                    if (!usedItls.contains(itl)) {
                        RestrictionConstraint constraint = new RestrictionConstraint();
			constraint.setAreaId(itl.getAreaId());
			constraint.setLineIdShortCut(itl.getLineIdShortCut());
                        constraint.setName(itl.getName());
                        StopArea.addUnvalidRestrictionConstraint(constraint);
                        logger.debug("ITL "+itl.getName()+" ("+itl.getAreaId()+","+itl.getLineIdShortCut()+") HAS NO STOP AREA.");
                    }
                }

		int count = (stopAreas == null? 0 : stopAreas.size());
		ReportItem countItem = new NeptuneReportItem(NeptuneReportItem.KEY.OBJECT_COUNT, Report.STATE.OK,Integer.toString(count));
		report.addItem(countItem);
		parentReport.addItem(report);
		return stopAreas;
	}

	public List<AreaCentroid> extractAreaCentroids(ChouettePTNetworkTypeType rootObject, ReportItem parentReport) 
	{
		ReportItem report = new NeptuneReportItem(NeptuneReportItem.KEY.PARSE_OBJECT, Report.STATE.OK,"AreaCentroid");
		chouette.schema.AreaCentroid[] xmlAreaCentroids = rootObject.getChouetteArea().getAreaCentroid();

		// modele des producer : voir package fr.certu.chouette.service.validation.util

		List<AreaCentroid> areaCentroids = new ArrayList<AreaCentroid>();

		for(chouette.schema.AreaCentroid xmlAreaCentroid : xmlAreaCentroids){
			AreaCentroid areaCentroid = areaCentroidProducer.produce(xmlAreaCentroid, report);
			areaCentroids.add(areaCentroid);
		}

		int count = (areaCentroids == null? 0 : areaCentroids.size());
		ReportItem countItem = new NeptuneReportItem(NeptuneReportItem.KEY.OBJECT_COUNT, Report.STATE.OK,Integer.toString(count));
		report.addItem(countItem);
		parentReport.addItem(report);
		return areaCentroids;
	}


	public List<ConnectionLink> extractConnectionLinks(ChouettePTNetworkTypeType rootObject, ReportItem parentReport)
	{
		ReportItem report = new NeptuneReportItem(NeptuneReportItem.KEY.PARSE_OBJECT, Report.STATE.OK,"ConnectionLink");
		chouette.schema.ConnectionLink[] xmlConnectionLinks = rootObject.getConnectionLink();

		// modele des producer : voir package fr.certu.chouette.service.validation.util

		List<ConnectionLink> connectionLinks = new ArrayList<ConnectionLink>();

		for(chouette.schema.ConnectionLink xmlConnectionLink : xmlConnectionLinks){
			ConnectionLink connectionLink = connectionLinkProducer.produce(xmlConnectionLink, report);
			connectionLinks.add(connectionLink);
		}

		int count = (connectionLinks == null? 0 : connectionLinks.size());
		ReportItem countItem = new NeptuneReportItem(NeptuneReportItem.KEY.OBJECT_COUNT, Report.STATE.OK,Integer.toString(count));
		report.addItem(countItem);
		parentReport.addItem(report);
		return connectionLinks;
	}

	public List<Timetable> extractTimetables(ChouettePTNetworkTypeType rootObject, ReportItem parentReport) 
	{
		ReportItem report = new NeptuneReportItem(NeptuneReportItem.KEY.PARSE_OBJECT, Report.STATE.OK,"Timetable");
		chouette.schema.Timetable[] xmlTimetables = rootObject.getTimetable();

		// modele des producer : voir package fr.certu.chouette.service.validation.util

		List<Timetable> timetables = new ArrayList<Timetable>();

		for(chouette.schema.Timetable xmlTimetable : xmlTimetables){
			Timetable timetable = timetableProducer.produce(xmlTimetable, report);
			timetables.add(timetable);
		}

		int count = (timetables == null? 0 : timetables.size());
		ReportItem countItem = new NeptuneReportItem(NeptuneReportItem.KEY.OBJECT_COUNT, Report.STATE.OK,Integer.toString(count));
		report.addItem(countItem);
		parentReport.addItem(report);
		return timetables;
	}

	public List<AccessLink> extractAccessLinks(ChouettePTNetworkTypeType rootObject, ReportItem parentReport)
	{
		ReportItem report = new NeptuneReportItem(NeptuneReportItem.KEY.PARSE_OBJECT, Report.STATE.OK,"AccessLink");
		chouette.schema.AccessLink[] xmlAccessLinks = rootObject.getAccessLink();

		// modele des producer : voir package fr.certu.chouette.service.validation.util

		List<AccessLink> accessLinks = new ArrayList<AccessLink>();

		for(chouette.schema.AccessLink xmlAccessLink : xmlAccessLinks){
			AccessLink accessLink = accessLinkProducer.produce(xmlAccessLink, report);
			accessLinks.add(accessLink);
		}

		int count = (accessLinks == null? 0 : accessLinks.size());
		ReportItem countItem = new NeptuneReportItem(NeptuneReportItem.KEY.OBJECT_COUNT, Report.STATE.OK,Integer.toString(count));
		report.addItem(countItem);
		parentReport.addItem(report);
		return accessLinks;
	}

	public List<AccessPoint> extractAccessPoints(ChouettePTNetworkTypeType rootObject, ReportItem parentReport)
	{
		ReportItem report = new NeptuneReportItem(NeptuneReportItem.KEY.PARSE_OBJECT, Report.STATE.OK,"AccessLink");
		chouette.schema.AccessPoint[] xmlAccessPoints = rootObject.getAccessPoint();

		// modele des producer : voir package fr.certu.chouette.service.validation.util

		List<AccessPoint> accessPoints = new ArrayList<AccessPoint>();

		for(chouette.schema.AccessPoint xmlAccessPoint : xmlAccessPoints){
			AccessPoint accessPoint = accessPointProducer.produce(xmlAccessPoint, report);
			accessPoints.add(accessPoint);
		}

		int count = (accessPoints == null? 0 : accessPoints.size());
		ReportItem countItem = new NeptuneReportItem(NeptuneReportItem.KEY.OBJECT_COUNT, Report.STATE.OK,Integer.toString(count));
		report.addItem(countItem);
		parentReport.addItem(report);
		return accessPoints;
	}

	public List<GroupOfLine> extractGroupOfLines(ChouettePTNetworkTypeType rootObject, ReportItem parentReport){
		List<GroupOfLine> groupOfLines = new ArrayList<GroupOfLine>();

		ReportItem report = new NeptuneReportItem(NeptuneReportItem.KEY.PARSE_OBJECT, Report.STATE.OK,"GroupOfLine");
		chouette.schema.GroupOfLine[] xmlGroupOfLines = rootObject.getGroupOfLine();
		for (chouette.schema.GroupOfLine xmlGroupOfLine : xmlGroupOfLines) {
			GroupOfLine groupOfLine = groupOfLineProducer.produce(xmlGroupOfLine, report);
			groupOfLines.add(groupOfLine);
		}

		int count = (groupOfLines == null? 0 : groupOfLines.size());
		ReportItem countItem = new NeptuneReportItem(NeptuneReportItem.KEY.OBJECT_COUNT, Report.STATE.OK,Integer.toString(count));
		report.addItem(countItem);
		parentReport.addItem(report);
		return groupOfLines;
	}
	
	public List<Facility> extractFacilities(ChouettePTNetworkTypeType rootObject, ReportItem parentReport){
		List<Facility> facilities = new ArrayList<Facility>();
		ReportItem report = new NeptuneReportItem(NeptuneReportItem.KEY.PARSE_OBJECT, Report.STATE.OK,"Facility");
		chouette.schema.Facility[] xmlFacilities = rootObject.getFacility();
		for (chouette.schema.Facility xmlFacility : xmlFacilities) {
			Facility facility = facilityProducer.produce(xmlFacility, report);
			facilities.add(facility);
		}
		int count = (facilities == null ? 0 : facilities.size());
		ReportItem countItem = new NeptuneReportItem(NeptuneReportItem.KEY.OBJECT_COUNT, Report.STATE.OK,Integer.toString(count));
		report.addItem(countItem);
		parentReport.addItem(report);
		return facilities;
	}
	
	public List<TimeSlot> extractTimeSlots(ChouettePTNetworkTypeType rootObject,ReportItem parentReport){
		List<TimeSlot> timeSlots = new ArrayList<TimeSlot>();
		ReportItem report = new NeptuneReportItem(NeptuneReportItem.KEY.PARSE_OBJECT, Report.STATE.OK,"TimeSlot");
		chouette.schema.TimeSlot[] xmlTimeSlots = rootObject.getTimeSlot();
		for (chouette.schema.TimeSlot xmlTimeSlot : xmlTimeSlots) {
			TimeSlot timeSlot = timeSlotProducer.produce(xmlTimeSlot, report);
			timeSlots.add(timeSlot);			
		}
		int count = (timeSlots == null ? 0 : timeSlots.size());
		ReportItem countItem = new NeptuneReportItem(NeptuneReportItem.KEY.OBJECT_COUNT, Report.STATE.OK,Integer.toString(count));
		report.addItem(countItem);
		parentReport.addItem(report);
		
		return timeSlots;
	}
}
