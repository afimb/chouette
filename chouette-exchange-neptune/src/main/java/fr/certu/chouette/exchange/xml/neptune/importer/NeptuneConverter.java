package fr.certu.chouette.exchange.xml.neptune.importer;


import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;
import chouette.schema.ChouetteLineDescription;
import chouette.schema.ChouettePTNetworkTypeType;
import fr.certu.chouette.exchange.xml.neptune.importer.producer.AreaCentroidProducer;
import fr.certu.chouette.exchange.xml.neptune.importer.producer.CompanyProducer;
import fr.certu.chouette.exchange.xml.neptune.importer.producer.ConnectionLinkProducer;
import fr.certu.chouette.exchange.xml.neptune.importer.producer.JourneyPatternProducer;
import fr.certu.chouette.exchange.xml.neptune.importer.producer.LineProducer;
import fr.certu.chouette.exchange.xml.neptune.importer.producer.PTLinkProducer;
import fr.certu.chouette.exchange.xml.neptune.importer.producer.PTNetworkProducer;
import fr.certu.chouette.exchange.xml.neptune.importer.producer.RouteProducer;
import fr.certu.chouette.exchange.xml.neptune.importer.producer.StopAreaProducer;
import fr.certu.chouette.exchange.xml.neptune.importer.producer.StopPointProducer;
import fr.certu.chouette.exchange.xml.neptune.importer.producer.TimetableProducer;
import fr.certu.chouette.exchange.xml.neptune.importer.producer.VehicleJourneyProducer;
import fr.certu.chouette.model.neptune.AreaCentroid;
import fr.certu.chouette.model.neptune.Company;
import fr.certu.chouette.model.neptune.ConnectionLink;
import fr.certu.chouette.model.neptune.JourneyPattern;
import fr.certu.chouette.model.neptune.Line;
import fr.certu.chouette.model.neptune.PTLink;
import fr.certu.chouette.model.neptune.PTNetwork;
import fr.certu.chouette.model.neptune.Route;
import fr.certu.chouette.model.neptune.StopArea;
import fr.certu.chouette.model.neptune.StopPoint;
import fr.certu.chouette.model.neptune.Timetable;
import fr.certu.chouette.model.neptune.VehicleJourney;

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
	
	
	public List<Line> extractLines(ChouettePTNetworkTypeType rootObject) 
	{
		ChouetteLineDescription lineDescription = rootObject.getChouetteLineDescription();
		chouette.schema.Line xmlLine = lineDescription.getLine();
		
		// modele des producer : voir package fr.certu.chouette.service.validation.util
		Line line = lineProducer.produce(xmlLine);
		
		List<Line> lines = new ArrayList<Line>();
		lines.add(line);
		return lines;
	}
	
	public List<Route> extractRoutes(ChouettePTNetworkTypeType rootObject) 
	{
		ChouetteLineDescription lineDescription = rootObject.getChouetteLineDescription();
		chouette.schema.ChouetteRoute[] xmlRoutes = lineDescription.getChouetteRoute();
		
		// modele des producer : voir package fr.certu.chouette.service.validation.util
		
		List<Route> routes = new ArrayList<Route>();

		for(chouette.schema.ChouetteRoute xmlRoute : xmlRoutes){
			Route route = routeProducer.produce(xmlRoute);
			routes.add(route);
		}
		
		return routes;
	}

	public List<Company> extractCompanies(ChouettePTNetworkTypeType rootObject) {
		chouette.schema.Company[] xmlCompanies = rootObject.getCompany();
		
		// modele des producer : voir package fr.certu.chouette.service.validation.util
		List<Company> companies = new ArrayList<Company>();

		for(chouette.schema.Company xmlCompany : xmlCompanies){
			Company company = companyProducer.produce(xmlCompany);
			companies.add(company);
		}
		
		return companies;
	}

	public PTNetwork extractPTNetwork(ChouettePTNetworkTypeType rootObject) {
		chouette.schema.PTNetwork xmlPTNetwork = rootObject.getPTNetwork();
		
		// modele des producer : voir package fr.certu.chouette.service.validation.util
		PTNetwork ptNetwork = networkProducer.produce(xmlPTNetwork);
		
		return ptNetwork;
	}

	public List<JourneyPattern> extractJourneyPatterns(ChouettePTNetworkTypeType rootObject) {
		ChouetteLineDescription lineDescription = rootObject.getChouetteLineDescription();
		chouette.schema.JourneyPattern[] xmlJourneyPatterns = lineDescription.getJourneyPattern();
		
		// modele des producer : voir package fr.certu.chouette.service.validation.util
		
		List<JourneyPattern> journeyPatterns = new ArrayList<JourneyPattern>();

		for(chouette.schema.JourneyPattern xmlJourneyPattern : xmlJourneyPatterns){
			JourneyPattern journeyPattern = journeyPatternProducer.produce(xmlJourneyPattern);
			journeyPatterns.add(journeyPattern);
		}
		
		return journeyPatterns;
	}
	
	public List<PTLink> extractPTLinks(ChouettePTNetworkTypeType rootObject) {
		ChouetteLineDescription lineDescription = rootObject.getChouetteLineDescription();
		chouette.schema.PtLink[] xmlPTLinks = lineDescription.getPtLink();
		
		// modele des producer : voir package fr.certu.chouette.service.validation.util
		
		List<PTLink> ptLinks = new ArrayList<PTLink>();

		for(chouette.schema.PtLink xmlPTLink : xmlPTLinks){
			PTLink ptLink = ptLinkProducer.produce(xmlPTLink);
			ptLinks.add(ptLink);
		}
		
		return ptLinks;
	}
	
	public List<VehicleJourney> extractVehicleJourneys(ChouettePTNetworkTypeType rootObject) {
		ChouetteLineDescription lineDescription = rootObject.getChouetteLineDescription();
		chouette.schema.VehicleJourney[] xmlVehicleJourneys = lineDescription.getVehicleJourney();
		
		// modele des producer : voir package fr.certu.chouette.service.validation.util
		
		List<VehicleJourney> vehicleJourneys = new ArrayList<VehicleJourney>();

		for(chouette.schema.VehicleJourney xmlVehicleJourney : xmlVehicleJourneys){
			VehicleJourney vehicleJourney = vehicleJourneyProducer.produce(xmlVehicleJourney);
			vehicleJourneys.add(vehicleJourney);
		}
		
		return vehicleJourneys;
	}
	
	public List<StopPoint> extractStopPoints(ChouettePTNetworkTypeType rootObject) {
		ChouetteLineDescription lineDescription = rootObject.getChouetteLineDescription();
		chouette.schema.StopPoint[] xmlStopPoints = lineDescription.getStopPoint();
		
		// modele des producer : voir package fr.certu.chouette.service.validation.util
		
		List<StopPoint> stopPoints = new ArrayList<StopPoint>();

		for(chouette.schema.StopPoint xmlStopPoint : xmlStopPoints){
			StopPoint stopPoint = stopPointProducer.produce(xmlStopPoint);
			stopPoints.add(stopPoint);
		}
		
		return stopPoints;
	}

	public List<StopArea> extractStopAreas(ChouettePTNetworkTypeType rootObject) {
		chouette.schema.StopArea[] xmlStopAreas = rootObject.getChouetteArea().getStopArea();
		
		// modele des producer : voir package fr.certu.chouette.service.validation.util
		
		List<StopArea> stopAreas = new ArrayList<StopArea>();

		for(chouette.schema.StopArea xmlStopArea : xmlStopAreas){
			StopArea stopArea = stopAreaProducer.produce(xmlStopArea);
			stopAreas.add(stopArea);
		}
		
		return stopAreas;
	}
	
	public List<AreaCentroid> extractAreaCentroids(ChouettePTNetworkTypeType rootObject) {
	chouette.schema.AreaCentroid[] xmlAreaCentroids = rootObject.getChouetteArea().getAreaCentroid();
		
		// modele des producer : voir package fr.certu.chouette.service.validation.util
		
		List<AreaCentroid> areaCentroids = new ArrayList<AreaCentroid>();

		for(chouette.schema.AreaCentroid xmlAreaCentroid : xmlAreaCentroids){
			AreaCentroid areaCentroid = areaCentroidProducer.produce(xmlAreaCentroid);
			areaCentroids.add(areaCentroid);
		}
		
		return areaCentroids;
	}
	

	public List<ConnectionLink> extractConnectionLinks(ChouettePTNetworkTypeType rootObject) {
		chouette.schema.ConnectionLink[] xmlConnectionLinks = rootObject.getConnectionLink();
		
		// modele des producer : voir package fr.certu.chouette.service.validation.util
		
		List<ConnectionLink> connectionLinks = new ArrayList<ConnectionLink>();

		for(chouette.schema.ConnectionLink xmlConnectionLink : xmlConnectionLinks){
			ConnectionLink connectionLink = connectionLinkProducer.produce(xmlConnectionLink);
			connectionLinks.add(connectionLink);
		}
		
		return connectionLinks;
	}
	
	public List<Timetable> extractTimetables(ChouettePTNetworkTypeType rootObject) {
		chouette.schema.Timetable[] xmlTimetables = rootObject.getTimetable();
		
		// modele des producer : voir package fr.certu.chouette.service.validation.util
		
		List<Timetable> timetables = new ArrayList<Timetable>();

		for(chouette.schema.Timetable xmlTimetable : xmlTimetables){
			Timetable timetable = timetableProducer.produce(xmlTimetable);
			timetables.add(timetable);
		}
		
		return timetables;
	}
}
