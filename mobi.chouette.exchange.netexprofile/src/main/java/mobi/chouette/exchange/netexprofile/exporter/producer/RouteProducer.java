package mobi.chouette.exchange.netexprofile.exporter.producer;

import mobi.chouette.model.StopPoint;
import org.apache.commons.lang.StringUtils;
import org.rutebanken.netex.model.LineRefStructure;
import org.rutebanken.netex.model.PointOnRoute;
import org.rutebanken.netex.model.PointsOnRoute_RelStructure;
import org.rutebanken.netex.model.RoutePointRefStructure;

import java.util.Collection;

public class RouteProducer extends AbstractJaxbNetexProducer<org.rutebanken.netex.model.Route, mobi.chouette.model.Route> {

    //@Override
    public org.rutebanken.netex.model.Route produce(mobi.chouette.model.Route chouetteRoute,
            Collection<mobi.chouette.model.Route> exportableRoutes, boolean addExtension) {

        org.rutebanken.netex.model.Route netexRoute = netexFactory.createRoute();
        populateFromModel(netexRoute, chouetteRoute);

        netexRoute.setName(getMultilingualString(chouetteRoute.getName()));

        if (StringUtils.isNotEmpty(chouetteRoute.getPublishedName())) {
            netexRoute.setShortName(getMultilingualString(chouetteRoute.getPublishedName()));
        }

        LineRefStructure lineRefStruct = netexFactory.createLineRefStructure();
        lineRefStruct.setVersion(chouetteRoute.getLine().getObjectVersion() != null ?
                String.valueOf(chouetteRoute.getLine().getObjectVersion()) : NETEX_DATA_OJBECT_VERSION);
        lineRefStruct.setRef(chouetteRoute.getLine().getObjectId());
        netexRoute.setLineRef(netexFactory.createLineRef(lineRefStruct));

        PointsOnRoute_RelStructure pointsOnRoute = netexFactory.createPointsOnRoute_RelStructure();
        for (StopPoint stopPoint : chouetteRoute.getStopPoints()) {
            PointOnRoute pointOnRoute = netexFactory.createPointOnRoute()
                    .withVersion(NETEX_DATA_OJBECT_VERSION)
                    .withId("AVI:PointOnRoute:" + stopPoint.getObjectId()); // TODO fix this id to conform to the actual stop point
            pointsOnRoute.getPointOnRoute().add(pointOnRoute);

            RoutePointRefStructure routePointRefStruct = netexFactory.createRoutePointRefStructure()
                    .withVersion(NETEX_DATA_OJBECT_VERSION)
                    .withRef(stopPoint.getObjectId());
            pointOnRoute.setPointRef(netexFactory.createRoutePointRef(routePointRefStruct));
        }
        netexRoute.setPointsInSequence(pointsOnRoute);

        return netexRoute;
    }

}
