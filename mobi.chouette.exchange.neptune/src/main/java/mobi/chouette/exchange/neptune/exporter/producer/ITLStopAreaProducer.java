package mobi.chouette.exchange.neptune.exporter.producer;

import mobi.chouette.common.Context;
import mobi.chouette.exchange.neptune.JsonExtension;
import mobi.chouette.model.RoutingConstraint;

import org.trident.schema.trident.ChouetteAreaType;
import org.trident.schema.trident.ChouettePTNetworkType.ChouetteArea;
import org.trident.schema.trident.StopAreaExtensionType;

public class ITLStopAreaProducer extends AbstractJaxbNeptuneProducer<ChouetteArea.StopArea, RoutingConstraint> implements
JsonExtension {
	/**
	 * @param context job context
	 * @param routingConstraint exported routing  constraint
	 * @return StopArea Neptune Object (ITL type)
	 */
	public ChouetteArea.StopArea produce(Context context, RoutingConstraint routingConstraint)
	{
		ChouetteArea.StopArea jaxbStopArea = tridentFactory
				.createChouettePTNetworkTypeChouetteAreaStopArea();

		populateFromModel(context, jaxbStopArea, routingConstraint);
		StopAreaExtensionType stopAreaExtension = tridentFactory.createStopAreaExtensionType();


		stopAreaExtension.setAreaType(ChouetteAreaType.ITL);


		jaxbStopArea.setName(routingConstraint.getName());
		jaxbStopArea.setStopAreaExtension(stopAreaExtension);


		return jaxbStopArea;
	}
}
