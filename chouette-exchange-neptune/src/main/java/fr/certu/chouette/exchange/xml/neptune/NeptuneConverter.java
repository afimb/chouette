package fr.certu.chouette.exchange.xml.neptune;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

import chouette.schema.ChouetteLineDescription;
import chouette.schema.ChouettePTNetworkTypeType;
import fr.certu.chouette.model.neptune.Company;
import fr.certu.chouette.model.neptune.JourneyPattern;
import fr.certu.chouette.model.neptune.Line;
import fr.certu.chouette.model.neptune.PTLink;
import fr.certu.chouette.model.neptune.PTNetwork;
import fr.certu.chouette.model.neptune.Route;

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
	
//	@Getter @Setter private VehicleJourney vehicleJourneyProducer;
	
//	public List<T> extract(ChouettePTNetworkTypeType rootObject) 
//	{
//		ChouetteLineDescription lineDescription = rootObject.getChouetteLineDescription();
//		List<U> xmlList = lineDescription.getLine();
//		
//		// modele des producer : voir package fr.certu.chouette.service.validation.util
//		AbstractModelProducer<T, U> producer = ProducerFactory.getInstance().getProducer(Line.class);
//		List<T> list = new ArrayList<T>();
//
//		for(U xmlItem : xmlList){
//			T item = producer.produce(xmlItem);
//			list.add(item);
//		}
//		
//		return list;
//	}
	
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
}
