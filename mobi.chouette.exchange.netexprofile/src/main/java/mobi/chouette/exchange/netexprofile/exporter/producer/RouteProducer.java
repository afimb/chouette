package mobi.chouette.exchange.netexprofile.exporter.producer;

import mobi.chouette.common.Context;
import mobi.chouette.model.StopPoint;
import org.apache.commons.lang.StringUtils;
import org.rutebanken.netex.model.*;

import java.util.List;

import static mobi.chouette.exchange.netexprofile.exporter.producer.NetexProducerUtils.isSet;
import static mobi.chouette.exchange.netexprofile.exporter.producer.NetexProducerUtils.netexId;
import static mobi.chouette.exchange.netexprofile.util.NetexObjectIdTypes.*;

public class RouteProducer extends NetexProducer implements NetexEntityProducer<org.rutebanken.netex.model.Route, mobi.chouette.model.Route> {

    @Override
    public org.rutebanken.netex.model.Route produce(Context context, mobi.chouette.model.Route neptuneRoute) {
        org.rutebanken.netex.model.Route netexRoute = netexFactory.createRoute();
        netexRoute.setVersion(neptuneRoute.getObjectVersion() > 0 ? String.valueOf(neptuneRoute.getObjectVersion()) : NETEX_DATA_OJBECT_VERSION);

        String routeId = netexId(neptuneRoute.objectIdPrefix(), ROUTE_KEY, neptuneRoute.objectIdSuffix());
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

        PointsOnRoute_RelStructure pointsOnRoute = netexFactory.createPointsOnRoute_RelStructure();
        List<StopPoint> stopPoints = neptuneRoute.getStopPoints();
        String[] idSequence = NetexProducerUtils.generateIdSequence(stopPoints.size());

        for (int i = 0; i < stopPoints.size(); i++) {
            StopPoint stopPoint = stopPoints.get(i);
            String pointOnRouteIdSuffix = neptuneRoute.objectIdSuffix() + StringUtils.leftPad(idSequence[i], 2, "0");
            String pointOnRouteId = netexId(neptuneRoute.objectIdPrefix(), POINT_ON_ROUTE_KEY, pointOnRouteIdSuffix);
            String routeVersion = neptuneRoute.getObjectVersion() > 0 ? String.valueOf(neptuneRoute.getObjectVersion()) : NETEX_DATA_OJBECT_VERSION;

            PointOnRoute pointOnRoute = netexFactory.createPointOnRoute()
                    .withVersion(routeVersion)
                    .withId(pointOnRouteId);
            pointsOnRoute.getPointOnRoute().add(pointOnRoute);

            String[] idSuffixSplit = StringUtils.splitByWholeSeparator(stopPoint.objectIdSuffix(), "-");
            String stopPointIdSuffix = idSuffixSplit[idSuffixSplit.length - 1];
            String stopPointIdRef = netexId(stopPoint.objectIdPrefix(), ROUTE_POINT_KEY, stopPointIdSuffix);

            RoutePointRefStructure routePointRefStruct = netexFactory.createRoutePointRefStructure()
                    .withVersion(routeVersion)
                    .withRef(stopPointIdRef);
            pointOnRoute.setPointRef(netexFactory.createRoutePointRef(routePointRefStruct));
        }

        netexRoute.setPointsInSequence(pointsOnRoute);

        if (isSet(neptuneRoute.getOppositeRoute())) {
            RouteRefStructure routeRefStruct = netexFactory.createRouteRefStructure();
            routeRefStruct.setVersion(neptuneRoute.getOppositeRoute().getObjectVersion() > 0 ? String.valueOf(neptuneRoute.getOppositeRoute().getObjectVersion()) : NETEX_DATA_OJBECT_VERSION);

            String inverseRouteIdRef = netexId(neptuneRoute.getOppositeRoute().objectIdPrefix(), ROUTE_KEY, neptuneRoute.getOppositeRoute().objectIdSuffix());
            routeRefStruct.setRef(inverseRouteIdRef);

            netexRoute.setInverseRouteRef(routeRefStruct);
        }

        return netexRoute;
    }

}
