package mobi.chouette.exchange.netexprofile.exporter.producer;

import java.math.BigInteger;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Context;
import mobi.chouette.exchange.netexprofile.Constant;
import mobi.chouette.exchange.netexprofile.ConversionUtil;
import mobi.chouette.exchange.netexprofile.exporter.ExportableData;
import mobi.chouette.model.StopPoint;
import mobi.chouette.model.type.PTDirectionEnum;

import org.rutebanken.netex.model.*;

import static mobi.chouette.exchange.netexprofile.exporter.producer.NetexProducerUtils.isSet;
import static mobi.chouette.exchange.netexprofile.exporter.producer.NetexProducerUtils.netexId;
import static mobi.chouette.exchange.netexprofile.util.NetexObjectIdTypes.*;


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


		LineRefStructure lineRefStruct = netexFactory.createLineRefStructure();
		NetexProducerUtils.populateReference(neptuneRoute.getLine(), lineRefStruct, true);
		netexRoute.setLineRef(netexFactory.createLineRef(lineRefStruct));

		
		PointsOnRoute_RelStructure pointsOnRoute = netexFactory.createPointsOnRoute_RelStructure();

		for (StopPoint stopPoint : neptuneRoute.getStopPoints()) {
			if (stopPoint != null) {

				// TODO refactor
				String pointVersion = neptuneRoute.getObjectVersion() > 0 ? String.valueOf(neptuneRoute.getObjectVersion()) : NETEX_DEFAULT_OBJECT_VERSION;
				String pointOnRouteIdSuffix = stopPoint.objectIdSuffix() + "-" + stopPoint.getPosition();
				String pointOnRouteId = netexId(stopPoint.objectIdPrefix(), POINT_ON_ROUTE, pointOnRouteIdSuffix);

				PointOnRoute pointOnRoute = netexFactory.createPointOnRoute().withVersion(pointVersion).withId(pointOnRouteId).withOrder(BigInteger.ONE);
				pointsOnRoute.getPointOnRoute().add(pointOnRoute);

				if (stopPoint.getContainedInStopArea() != null) {
					String routePointIdSuffix = stopPoint.getContainedInStopArea().objectIdSuffix();
					String routePointId = netexId(stopPoint.objectIdPrefix(), ROUTE_POINT, routePointIdSuffix);

					RoutePointRefStructure routePointRefStruct = netexFactory.createRoutePointRefStructure().withRef(routePointId).withVersion(pointVersion);
					pointOnRoute.setPointRef(netexFactory.createRoutePointRef(routePointRefStruct));
				} else {
					throw new RuntimeException(
							"StopPoint with id : " + stopPoint.getObjectId() + " is not contained in a StopArea. Cannot produce RoutePoint reference.");
				}
			}
		}

		netexRoute.setPointsInSequence(pointsOnRoute);

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
