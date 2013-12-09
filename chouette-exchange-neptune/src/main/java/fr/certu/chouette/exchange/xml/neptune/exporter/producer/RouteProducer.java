package fr.certu.chouette.exchange.xml.neptune.exporter.producer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.trident.schema.trident.ChouettePTNetworkType.ChouetteLineDescription.ChouetteRoute;
import org.trident.schema.trident.PTDirectionType;
import org.trident.schema.trident.RouteExtension;

import fr.certu.chouette.model.neptune.NeptuneIdentifiedObject;
import fr.certu.chouette.model.neptune.PTLink;
import fr.certu.chouette.model.neptune.Route;
import fr.certu.chouette.model.neptune.type.PTDirectionEnum;

public class RouteProducer extends AbstractJaxbNeptuneProducer<ChouetteRoute, Route> {

	@Override
	public ChouetteRoute produce(Route route) {
		ChouetteRoute jaxbRoute = tridentFactory.createChouettePTNetworkTypeChouetteLineDescriptionChouetteRoute();

		//
		populateFromModel(jaxbRoute, route);

		jaxbRoute.setComment(getNotEmptyString(route.getComment()));
		jaxbRoute.setName(route.getName());
		jaxbRoute.setNumber(route.getNumber());
		jaxbRoute.setPublishedName(route.getPublishedName());

		try {
			PTDirectionEnum direction = route.getDirection();
			if(direction != null){
				jaxbRoute.setDirection(PTDirectionType.fromValue(direction.value()));
			}
		} catch (IllegalArgumentException e) {
			// TODO generate report
		}

		jaxbRoute.getJourneyPatternId().addAll(NeptuneIdentifiedObject.extractObjectIds(route.getJourneyPatterns()));

		jaxbRoute.getPtLinkId().addAll(NeptuneIdentifiedObject.extractObjectIds(sort(route.getPtLinks())));

		jaxbRoute.setWayBackRouteId(route.getWayBackRouteId());

		RouteExtension castorRouteExtension = new RouteExtension();
		castorRouteExtension.setWayBack(route.getWayBack());
		jaxbRoute.setRouteExtension(castorRouteExtension);

		return jaxbRoute;
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
