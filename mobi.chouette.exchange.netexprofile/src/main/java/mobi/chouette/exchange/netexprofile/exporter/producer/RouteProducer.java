package mobi.chouette.exchange.netexprofile.exporter.producer;

import mobi.chouette.model.*;
import org.apache.commons.lang.StringUtils;
import org.rutebanken.netex.model.*;
import org.rutebanken.netex.model.Route;

import java.util.Collection;
import java.util.List;

public class RouteProducer extends AbstractNetexProducer<Route, mobi.chouette.model.Route> {

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
        List<StopPoint> stopPoints = chouetteRoute.getStopPoints();
        String[] idSequence = NetexProducerUtils.generateIdSequence(stopPoints.size());

        for (int i = 0; i < stopPoints.size(); i++) {
            String pointOnRouteId = chouetteRoute.objectIdSuffix() + StringUtils.leftPad(idSequence[i], 2, "0");
            PointOnRoute pointOnRoute = netexFactory.createPointOnRoute()
                    .withVersion(chouetteRoute.getObjectVersion() > 0 ? String.valueOf(chouetteRoute.getObjectVersion()) : NETEX_DATA_OJBECT_VERSION)
                    .withId("AVI:PointOnRoute:" + pointOnRouteId);
            pointsOnRoute.getPointOnRoute().add(pointOnRoute);

            RoutePointRefStructure routePointRefStruct = netexFactory.createRoutePointRefStructure()
                    //.withVersion(NETEX_DATA_OJBECT_VERSION) // TODO enable when RoutePoints are fixed
                    .withRef(pointOnRouteId);
            pointOnRoute.setPointRef(netexFactory.createRoutePointRef(routePointRefStruct));
        }

        netexRoute.setPointsInSequence(pointsOnRoute);

        return netexRoute;
    }

}
