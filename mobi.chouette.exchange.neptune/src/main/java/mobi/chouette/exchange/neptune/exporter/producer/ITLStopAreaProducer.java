package mobi.chouette.exchange.neptune.exporter.producer;

import org.trident.schema.trident.ChouetteAreaType;
import org.trident.schema.trident.StopAreaExtensionType;
import org.trident.schema.trident.ChouettePTNetworkType.ChouetteArea;

import mobi.chouette.exchange.neptune.JsonExtension;
import mobi.chouette.model.Line;
import mobi.chouette.model.RoutingConstraint;
import mobi.chouette.model.StopArea;
import mobi.chouette.model.type.ChouetteAreaEnum;

public class ITLStopAreaProducer extends AbstractJaxbNeptuneProducer<ChouetteArea.StopArea, RoutingConstraint> implements
JsonExtension {
	/**
	 * @param routingConstraint
	 * @return
	 */
	public ChouetteArea.StopArea produce(RoutingConstraint routingConstraint)
	{
		ChouetteArea.StopArea jaxbStopArea = tridentFactory
				.createChouettePTNetworkTypeChouetteAreaStopArea();

		populateFromModel(jaxbStopArea, routingConstraint);
		StopAreaExtensionType stopAreaExtension = tridentFactory.createStopAreaExtensionType();


		stopAreaExtension.setAreaType(ChouetteAreaType.ITL);


		jaxbStopArea.setName(routingConstraint.getName());
		jaxbStopArea.setStopAreaExtension(stopAreaExtension);


		return jaxbStopArea;
	}
}
