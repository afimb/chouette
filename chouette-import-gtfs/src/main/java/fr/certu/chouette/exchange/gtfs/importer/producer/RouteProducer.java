package fr.certu.chouette.exchange.gtfs.importer.producer;

import org.apache.log4j.Logger;

import fr.certu.chouette.exchange.gtfs.model.GtfsRoute;
import fr.certu.chouette.model.neptune.Route;
import fr.certu.chouette.plugin.report.ReportItem;

public class RouteProducer extends AbstractModelProducer<Route, GtfsRoute> 
{
	private static Logger logger = Logger.getLogger(RouteProducer.class);
	public Route produce(GtfsRoute gtfsRoute,ReportItem report) 
	{
		Route route = new Route();
		
		// objectId, objectVersion, creatorId, creationTime
		route.setObjectId(composeIncrementalObjectId( Route.ROUTE_KEY, gtfsRoute.getRouteId(),logger));
		
		// Name optional
		route.setName(getNonEmptyTrimedString(gtfsRoute.getRouteLongName()));
								
		// PublishedName optional
		route.setPublishedName(getNonEmptyTrimedString(gtfsRoute.getRouteLongName()));
		
		// Comment optional
		route.setComment(getNonEmptyTrimedString(gtfsRoute.getRouteDesc()));
	   if (route.getComment() != null && route.getComment().length() > 255) 
	       route.setComment(route.getComment().substring(0,255));

		return route;
	}
}
