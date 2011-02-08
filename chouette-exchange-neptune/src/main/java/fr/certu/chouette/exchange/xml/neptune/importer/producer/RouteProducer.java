package fr.certu.chouette.exchange.xml.neptune.importer.producer;

import fr.certu.chouette.model.neptune.Route;
import fr.certu.chouette.model.neptune.type.PTDirectionEnum;

public class RouteProducer extends AbstractModelProducer<Route, chouette.schema.ChouetteRoute> {
	public Route produce(chouette.schema.ChouetteRoute xmlRoute){
		Route route = new Route();
		
		// objectId, objectVersion, creatorId, creationTime
		populateTridentObject(route, xmlRoute);
		
		// Name optional
		route.setName(getNonEmptyTrimedString(xmlRoute.getName()));
		
		// Direction optional
		if(xmlRoute.getDirection() != null){
			try {
				route.setDirection(PTDirectionEnum.fromValue(xmlRoute.getDirection().value()));
			} catch (IllegalArgumentException e) {
				// TODO: traiter le cas de non correspondance
			}
		}
		
		// JourneyPatternIds [1..w]
		String[] castorJourneyPatternIds = xmlRoute.getJourneyPatternId();
		for(String castorJourneyPatternId : castorJourneyPatternIds){
			String journeyPatternId = getNonEmptyTrimedString(castorJourneyPatternId);
			if(journeyPatternId == null){
				//TODO : tracer
			}
			else{
				route.addJourneyPatternId(journeyPatternId);
			}
		}
		
		// Number optional
		route.setNumber(getNonEmptyTrimedString(xmlRoute.getNumber()));
		
		// PTLinkIds [1..w]
		String[] castorPTLinkIds = xmlRoute.getPtLinkId();
		for(String castorPTLinkId : castorPTLinkIds){
			String ptLinkId = getNonEmptyTrimedString(castorPTLinkId);
			if(ptLinkId == null){
				//TODO : tracer
			}
			else{
				route.addPTLinkId(ptLinkId);
			}
		}
		
		// PublishedName optional
		route.setPublishedName(getNonEmptyTrimedString(xmlRoute.getPublishedName()));

		// WayBack optional
		route.setWayBack(getNonEmptyTrimedString(xmlRoute.getRouteExtension().getWayBack()));

		// Comment optional
		route.setComment(getNonEmptyTrimedString(xmlRoute.getComment()));
			
		return route;
	}
}
