package mobi.chouette.exchange.netexprofile.exporter.producer;

import mobi.chouette.common.Context;
import mobi.chouette.model.*;
import mobi.chouette.model.StopArea;
import org.apache.commons.lang.StringUtils;
import org.rutebanken.netex.model.*;
import org.rutebanken.netex.model.Route;

import java.util.List;

import static mobi.chouette.exchange.netexprofile.exporter.producer.NetexProducerUtils.isSet;
import static mobi.chouette.exchange.netexprofile.exporter.producer.NetexProducerUtils.netexId;
import static mobi.chouette.exchange.netexprofile.util.NetexObjectIdTypes.*;

public class RouteProducer extends NetexProducer implements NetexEntityProducer<org.rutebanken.netex.model.Route, mobi.chouette.model.Route> {

    @Override
    public org.rutebanken.netex.model.Route produce(Context context, mobi.chouette.model.Route neptuneRoute) {
        org.rutebanken.netex.model.Route netexRoute = netexFactory.createRoute();
        netexRoute.setVersion(neptuneRoute.getObjectVersion() > 0 ? String.valueOf(neptuneRoute.getObjectVersion()) : NETEX_DATA_OJBECT_VERSION);

        String routeId = netexId(neptuneRoute.objectIdPrefix(), ROUTE, neptuneRoute.objectIdSuffix());
        netexRoute.setId(routeId);

        if (isSet(neptuneRoute.getComment(), neptuneRoute.getNumber())) {
            KeyListStructure keyListStructure = netexFactory.createKeyListStructure();

            if (isSet(neptuneRoute.getComment())){
                KeyValueStructure commentStruct = netexFactory.createKeyValueStructure()
                        .withKey("Comment")
                        .withValue(neptuneRoute.getComment());
                keyListStructure.getKeyValue().add(commentStruct);
            }

            if (isSet(neptuneRoute.getNumber())){
                KeyValueStructure numberStruct = netexFactory.createKeyValueStructure()
                        .withKey("Number")
                        .withValue(neptuneRoute.getNumber());
                keyListStructure.getKeyValue().add(numberStruct);
            }

            netexRoute.setKeyList(keyListStructure);
        }

        if (isSet(neptuneRoute.getName())) {
            netexRoute.setName(getMultilingualString(neptuneRoute.getName()));
        }

        if (isSet(neptuneRoute.getPublishedName())) {
            netexRoute.setShortName(getMultilingualString(neptuneRoute.getPublishedName()));
        }

        LineRefStructure lineRefStruct = netexFactory.createLineRefStructure();
        lineRefStruct.setVersion(neptuneRoute.getLine().getObjectVersion() != null ? String.valueOf(neptuneRoute.getLine().getObjectVersion()) : NETEX_DATA_OJBECT_VERSION);
        lineRefStruct.setRef(neptuneRoute.getLine().getObjectId());
        netexRoute.setLineRef(netexFactory.createLineRef(lineRefStruct));

        List<StopPoint> stopPoints = neptuneRoute.getStopPoints();
        String[] idSequence = NetexProducerUtils.generateIdSequence(stopPoints.size());
        PointsOnRoute_RelStructure pointsOnRoute = netexFactory.createPointsOnRoute_RelStructure();

        // NEW LOOP
        for (StopPoint stopPoint : neptuneRoute.getStopPoints()) {
            if (stopPoint != null) {
                String pointVersion = neptuneRoute.getObjectVersion() > 0 ? String.valueOf(neptuneRoute.getObjectVersion()) : NETEX_DATA_OJBECT_VERSION;
                String pointOnRouteIdSuffix = stopPoint.objectIdSuffix() + "-" + stopPoint.getPosition();
                String pointOnRouteId = netexId(stopPoint.objectIdPrefix(), POINT_ON_ROUTE, pointOnRouteIdSuffix);

                PointOnRoute pointOnRoute = netexFactory.createPointOnRoute()
                        .withVersion(pointVersion)
                        .withId(pointOnRouteId);
                pointsOnRoute.getPointOnRoute().add(pointOnRoute);

                if (stopPoint.getContainedInStopArea() != null) {
                    String routePointIdSuffix = stopPoint.getContainedInStopArea().objectIdSuffix();
                    String routePointId = netexId(neptuneRoute.objectIdPrefix(), ROUTE_POINT, routePointIdSuffix);

                    RoutePointRefStructure routePointRefStruct = netexFactory.createRoutePointRefStructure()
                            //.withVersion(pointVersion)
                            .withRef(routePointId);
                    pointOnRoute.setPointRef(netexFactory.createRoutePointRef(routePointRefStruct));
                } else {
                    throw new RuntimeException("StopPoint with id : " + stopPoint.getObjectId() + " is not contained in a StopArea. Cannot produce RoutePoint reference.");
                }
            }
        }

/*
        for (int i = 0; i < stopPoints.size(); i++) {
            StopPoint stopPoint = stopPoints.get(i);
            String pointOnRouteIdSuffix = neptuneRoute.objectIdSuffix() + StringUtils.leftPad(idSequence[i], 2, "0");
            String pointOnRouteId = netexId(neptuneRoute.objectIdPrefix(), POINT_ON_ROUTE, pointOnRouteIdSuffix);
            String routeVersion = neptuneRoute.getObjectVersion() > 0 ? String.valueOf(neptuneRoute.getObjectVersion()) : NETEX_DATA_OJBECT_VERSION;

            PointOnRoute pointOnRoute = netexFactory.createPointOnRoute()
                    .withVersion(routeVersion)
                    .withId(pointOnRouteId);
            pointsOnRoute.getPointOnRoute().add(pointOnRoute);

            String[] idSuffixSplit = StringUtils.splitByWholeSeparator(stopPoint.objectIdSuffix(), "-");
            String stopPointIdSuffix = idSuffixSplit[idSuffixSplit.length - 1];
            String stopPointIdRef = netexId(stopPoint.objectIdPrefix(), ROUTE_POINT, stopPointIdSuffix);

            RoutePointRefStructure routePointRefStruct = netexFactory.createRoutePointRefStructure()
                    .withVersion(routeVersion)
                    .withRef(stopPointIdRef);
            pointOnRoute.setPointRef(netexFactory.createRoutePointRef(routePointRefStruct));
        }
*/

        netexRoute.setPointsInSequence(pointsOnRoute);

        if (isSet(neptuneRoute.getOppositeRoute())) {
            RouteRefStructure routeRefStruct = netexFactory.createRouteRefStructure();
            routeRefStruct.setVersion(neptuneRoute.getOppositeRoute().getObjectVersion() > 0 ? String.valueOf(neptuneRoute.getOppositeRoute().getObjectVersion()) : NETEX_DATA_OJBECT_VERSION);

            String inverseRouteIdRef = netexId(neptuneRoute.getOppositeRoute().objectIdPrefix(), ROUTE, neptuneRoute.getOppositeRoute().objectIdSuffix());
            routeRefStruct.setRef(inverseRouteIdRef);

            netexRoute.setInverseRouteRef(routeRefStruct);
        }

        return netexRoute;
    }

}
