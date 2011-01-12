package fr.certu.chouette.exchange.xml.neptune;

import java.util.ArrayList;
import java.util.List;

import chouette.schema.ChouetteLineDescription;
import chouette.schema.ChouettePTNetworkTypeType;
import fr.certu.chouette.model.neptune.Company;
import fr.certu.chouette.model.neptune.Line;
import fr.certu.chouette.model.neptune.Route;

/**
 * note : repartir du fr.certu.chouette.service.validation.util.MainSchemaProducer 
 * 
 * @author michel
 *
 */
public class NeptuneConverter
{
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
		LineProducer producer = new LineProducer();
		Line line = producer.produce(xmlLine);
		
		List<Line> lines = new ArrayList<Line>();
		lines.add(line);
		return lines;
	}
	
	public List<Route> extractRoutes(ChouettePTNetworkTypeType rootObject) 
	{
		ChouetteLineDescription lineDescription = rootObject.getChouetteLineDescription();
		chouette.schema.ChouetteRoute[] xmlRoutes = lineDescription.getChouetteRoute();
		
		// modele des producer : voir package fr.certu.chouette.service.validation.util
		RouteProducer producer = new RouteProducer();
		List<Route> routes = new ArrayList<Route>();

		for(chouette.schema.ChouetteRoute xmlRoute : xmlRoutes){
			Route route = producer.produce(xmlRoute);
			routes.add(route);
		}
		
		return routes;
	}

	public List<Company> extractCompanies(ChouettePTNetworkTypeType rootObject) {
		chouette.schema.Company[] xmlCompanies = rootObject.getCompany();
		
		// modele des producer : voir package fr.certu.chouette.service.validation.util
		CompanyProducer producer = new CompanyProducer();
		List<Company> companies = new ArrayList<Company>();

		for(chouette.schema.Company xmlCompany : xmlCompanies){
			Company company = producer.produce(xmlCompany);
			companies.add(company);
		}
		
		return companies;
	}

	
}
