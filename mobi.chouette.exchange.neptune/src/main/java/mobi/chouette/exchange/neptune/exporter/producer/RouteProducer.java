package mobi.chouette.exchange.neptune.exporter.producer;

import java.util.Collection;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Context;
import mobi.chouette.exchange.neptune.NeptuneChouetteIdGenerator;
import mobi.chouette.exchange.neptune.exporter.NeptuneExportParameters;
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
	public ChouetteRoute produce(Context context, Route route, Collection<Route> exportableRoutes, boolean addExtension)
	{
		ChouetteRoute jaxbRoute = tridentFactory
				.createChouettePTNetworkTypeChouetteLineDescriptionChouetteRoute();
		NeptuneExportParameters parameters = (NeptuneExportParameters) context.get(CONFIGURATION);
		NeptuneChouetteIdGenerator neptuneChouetteIdGenerator = (NeptuneChouetteIdGenerator) context.get(CHOUETTEID_GENERATOR);
		//
		populateFromModel(context, jaxbRoute, route);

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


		if (hasOppositeRoute(context, route, log) && exportableRoutes.contains(route.getOppositeRoute()))
		{
			jaxbRoute.setWayBackRouteId(neptuneChouetteIdGenerator.toSpecificFormatId(route.getOppositeRoute().getChouetteId(), parameters.getDefaultCodespace(), route.getOppositeRoute()));
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
