package mobi.chouette.exchange.neptune.exporter.producer;

import java.util.Collection;

import lombok.extern.log4j.Log4j;
import mobi.chouette.model.Route;
import mobi.chouette.model.type.PTDirectionEnum;

import org.trident.schema.trident.ChouettePTNetworkType.ChouetteLineDescription.ChouetteRoute;
import org.trident.schema.trident.PTDirectionType;
import org.trident.schema.trident.RouteExtension;

@Log4j
public class RouteProducer extends
AbstractJaxbNeptuneProducer<ChouetteRoute, Route>
{

	//@Override
	public ChouetteRoute produce(Route route, Collection<Route> exportableRoutes, boolean addExtension)
	{
		ChouetteRoute jaxbRoute = tridentFactory
				.createChouettePTNetworkTypeChouetteLineDescriptionChouetteRoute();

		//
		populateFromModel(jaxbRoute, route);

		jaxbRoute.setComment(getNotEmptyString(route.getComment()));
		jaxbRoute.setName(route.getName());
		jaxbRoute.setNumber(route.getNumber());
		jaxbRoute.setPublishedName(route.getPublishedName());

		try
		{
			PTDirectionEnum direction = route.getDirection();
			if (direction != null)
			{
				jaxbRoute.setDirection(PTDirectionType.fromValue(direction.name()));
			}
		} catch (IllegalArgumentException e)
		{
			// TODO generate report
		}


		if (hasOppositeRoute(route, log) && exportableRoutes.contains(route.getOppositeRoute()))
		{
			jaxbRoute.setWayBackRouteId(route.getOppositeRoute().getObjectId());
		}

		if (route.getWayBack() != null)
		{
			RouteExtension castorRouteExtension = new RouteExtension();
			castorRouteExtension.setWayBack(route.getWayBack());
			jaxbRoute.setRouteExtension(castorRouteExtension);
		}

		return jaxbRoute;
	}


}
