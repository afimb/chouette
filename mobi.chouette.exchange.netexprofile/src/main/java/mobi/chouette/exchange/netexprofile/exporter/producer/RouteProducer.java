package mobi.chouette.exchange.netexprofile.exporter.producer;

import java.math.BigInteger;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Context;
import mobi.chouette.exchange.netexprofile.Constant;
import mobi.chouette.exchange.netexprofile.ConversionUtil;
import mobi.chouette.exchange.netexprofile.exporter.ExportableData;
import mobi.chouette.model.type.PTDirectionEnum;

import org.apache.commons.collections.CollectionUtils;
import org.rutebanken.netex.model.DirectionTypeEnumeration;
import org.rutebanken.netex.model.KeyListStructure;
import org.rutebanken.netex.model.KeyValueStructure;
import org.rutebanken.netex.model.PointOnRoute;
import org.rutebanken.netex.model.PointsOnRoute_RelStructure;
import org.rutebanken.netex.model.RoutePointRefStructure;
import org.rutebanken.netex.model.RouteRefStructure;

import static mobi.chouette.exchange.netexprofile.exporter.producer.NetexProducerUtils.isSet;
import static mobi.chouette.exchange.netexprofile.util.NetexObjectIdTypes.POINT_ON_ROUTE;

@Log4j
public class RouteProducer extends NetexProducer implements NetexEntityProducer<org.rutebanken.netex.model.Route, mobi.chouette.model.Route> {

	@Override
	public org.rutebanken.netex.model.Route produce(Context context, mobi.chouette.model.Route neptuneRoute) {
		org.rutebanken.netex.model.Route netexRoute = netexFactory.createRoute();
		NetexProducerUtils.populateId(neptuneRoute, netexRoute);

		netexRoute.setName(ConversionUtil.getMultiLingualString(neptuneRoute.getName()));
		netexRoute.setShortName(ConversionUtil.getMultiLingualString(neptuneRoute.getPublishedName()));

		if (isSet(neptuneRoute.getComment(), neptuneRoute.getNumber())) {
			KeyListStructure keyListStructure = netexFactory.createKeyListStructure();

			if (isSet(neptuneRoute.getComment())) {
				KeyValueStructure commentStruct = netexFactory.createKeyValueStructure().withKey("Comment").withValue(neptuneRoute.getComment());
				keyListStructure.getKeyValue().add(commentStruct);
			}

			if (isSet(neptuneRoute.getNumber())) {
				KeyValueStructure numberStruct = netexFactory.createKeyValueStructure().withKey("Number").withValue(neptuneRoute.getNumber());
				keyListStructure.getKeyValue().add(numberStruct);
			}

			netexRoute.setKeyList(keyListStructure);
		}

		netexRoute.setLineRef(NetexProducerUtils.createLineRef(neptuneRoute.getLine(), netexFactory));

		PointsOnRoute_RelStructure pointsOnRoute = netexFactory.createPointsOnRoute_RelStructure();

		int order = 1;
		for (mobi.chouette.model.RoutePoint neptuneRoutePoint : neptuneRoute.getRoutePoints()) {
			if (neptuneRoutePoint != null) {
				String pointVersion = neptuneRoutePoint.getObjectVersion() > 0 ? String.valueOf(neptuneRoutePoint.getObjectVersion()) : NETEX_DEFAULT_OBJECT_VERSION;
				String pointOnRouteId = NetexProducerUtils.createUniqueId(context, POINT_ON_ROUTE);

				PointOnRoute pointOnRoute = netexFactory.createPointOnRoute().withVersion(pointVersion).withId(pointOnRouteId).withOrder(BigInteger.valueOf(order++));
				pointsOnRoute.getPointOnRoute().add(pointOnRoute);
				RoutePointRefStructure routePointRefStruct = netexFactory.createRoutePointRefStructure().withRef(neptuneRoutePoint.getObjectId());
				pointOnRoute.setPointRef(netexFactory.createRoutePointRef(routePointRefStruct));
			}
		}

		if (!CollectionUtils.isEmpty(pointsOnRoute.getPointOnRoute())) {
			netexRoute.setPointsInSequence(pointsOnRoute);
		}

		ExportableData exportableData = (ExportableData) context.get(Constant.EXPORTABLE_DATA);
		if (isSet(neptuneRoute.getOppositeRoute()) && exportableData.getRoutes().contains(neptuneRoute.getOppositeRoute())) {
			RouteRefStructure routeRefStruct = netexFactory.createRouteRefStructure();
			NetexProducerUtils.populateReference(neptuneRoute.getOppositeRoute(), routeRefStruct, true);
			netexRoute.setInverseRouteRef(routeRefStruct);
		}

		netexRoute.setDirectionType(mapDirectionType(neptuneRoute.getDirection()));
		return netexRoute;
	}


	private DirectionTypeEnumeration mapDirectionType(PTDirectionEnum neptuneDirection) {
		if (neptuneDirection == null) {
			return null;
		}
		switch (neptuneDirection) {
			case A:
				return DirectionTypeEnumeration.OUTBOUND;
			case R:
				return DirectionTypeEnumeration.INBOUND;
			case ClockWise:
				return DirectionTypeEnumeration.CLOCKWISE;
			case CounterClockWise:
				return DirectionTypeEnumeration.ANTICLOCKWISE;
		}

		log.debug("Unable to map neptune direction to NeTEx: " + neptuneDirection);
		return null;
	}

}
