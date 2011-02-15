package fr.certu.chouette.exchange.xml.neptune.exporter.producer;

import java.util.ArrayList;
import java.util.List;

import chouette.schema.AccessibilitySuitabilityDetails;
import chouette.schema.AccessibilitySuitabilityDetailsItem;
import chouette.schema.LineExtension;
import chouette.schema.RouteExtension;
import chouette.schema.UserNeedGroup;
import chouette.schema.types.EncumbranceEnumeration;
import chouette.schema.types.MedicalNeedEnumeration;
import chouette.schema.types.MobilityEnumeration;
import chouette.schema.types.PTDirectionType;
import chouette.schema.types.PyschosensoryNeedEnumeration;
import chouette.schema.types.TransportModeNameType;
import fr.certu.chouette.model.neptune.Line;
import fr.certu.chouette.model.neptune.NeptuneIdentifiedObject;
import fr.certu.chouette.model.neptune.PTNetwork;
import fr.certu.chouette.model.neptune.Route;
import fr.certu.chouette.model.neptune.type.PTDirectionEnum;
import fr.certu.chouette.model.neptune.type.TransportModeNameEnum;
import fr.certu.chouette.model.neptune.type.UserNeedEnum;

public class RouteProducer extends AbstractCastorNeptuneProducer<chouette.schema.ChouetteRoute, Route> {

	@Override
	public chouette.schema.ChouetteRoute produce(Route route) {
		chouette.schema.ChouetteRoute castorRoute = new chouette.schema.ChouetteRoute();
		
		//
		populateFromModel(castorRoute, route);
		
		castorRoute.setComment(route.getComment());
		castorRoute.setName(route.getName());
		castorRoute.setNumber(route.getNumber());
		castorRoute.setPublishedName(route.getPublishedName());
		
		try {
		PTDirectionEnum direction = route.getDirection();
			if(direction != null){
				castorRoute.setDirection(PTDirectionType.fromValue(direction.value()));
			}
		} catch (IllegalArgumentException e) {
			// TODO generate report
		}
		
		castorRoute.setJourneyPatternId(NeptuneIdentifiedObject.extractObjectIds(route.getJourneyPatterns()));
		castorRoute.setPtLinkId(NeptuneIdentifiedObject.extractObjectIds(route.getPtLinks()));
		
		//castorRoute.setWayBackRouteId(route.getOppositeRouteId()); ??
		
		RouteExtension castorRouteExtension = new RouteExtension();
		castorRouteExtension.setWayBack(route.getWayBack());
		castorRoute.setRouteExtension(castorRouteExtension);
		
		return castorRoute;
	}

}
