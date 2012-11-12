package fr.certu.chouette.exchange.xml.neptune.exporter.producer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import chouette.schema.RouteExtension;
import chouette.schema.types.PTDirectionType;
import fr.certu.chouette.model.neptune.NeptuneIdentifiedObject;
import fr.certu.chouette.model.neptune.PTLink;
import fr.certu.chouette.model.neptune.Route;
import fr.certu.chouette.model.neptune.type.PTDirectionEnum;

public class RouteProducer extends AbstractCastorNeptuneProducer<chouette.schema.ChouetteRoute, Route> {

	@Override
	public chouette.schema.ChouetteRoute produce(Route route) {
		chouette.schema.ChouetteRoute castorRoute = new chouette.schema.ChouetteRoute();
		
		//
		populateFromModel(castorRoute, route);
		
		castorRoute.setComment(getNotEmptyString(route.getComment()));
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
		
		castorRoute.setPtLinkId(NeptuneIdentifiedObject.extractObjectIds(sort(route.getPtLinks())));
		
		castorRoute.setWayBackRouteId(route.getWayBackRouteId());
		
		RouteExtension castorRouteExtension = new RouteExtension();
		castorRouteExtension.setWayBack(route.getWayBack());
		castorRoute.setRouteExtension(castorRouteExtension);
		
		return castorRoute;
	}
	
	private List<PTLink> sort(List<PTLink> links)
	{
	   List<PTLink> sorted = new ArrayList<PTLink>();
	   
	   sorted.addAll(links);
	   
	   Collections.sort(sorted,new PtLinkSorter());
	   
	   return sorted;
	}

	private class PtLinkSorter implements Comparator<PTLink>
   {
      @Override
      public int compare(PTLink o1, PTLink o2)
      {
         return o1.getStartOfLink().getPosition() - o2.getStartOfLink().getPosition();
      }
	   
	}
	
}
