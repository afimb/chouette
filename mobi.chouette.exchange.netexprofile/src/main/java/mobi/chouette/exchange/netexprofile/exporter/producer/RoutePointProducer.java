package mobi.chouette.exchange.netexprofile.exporter.producer;

import mobi.chouette.model.StopPoint;
import org.rutebanken.netex.model.PointProjection;
import org.rutebanken.netex.model.PointRefStructure;
import org.rutebanken.netex.model.Projections_RelStructure;
import org.rutebanken.netex.model.RoutePoint;

public class RoutePointProducer extends AbstractJaxbNetexProducer<RoutePoint, StopPoint> {

    //@Override
    public RoutePoint produce(StopPoint stopPoint, boolean addExtension) {
        RoutePoint routePoint = netexFactory.createRoutePoint();
        populateFromModel(routePoint, stopPoint);

        PointRefStructure pointRefStruct = netexFactory.createPointRefStructure()
                .withVersion(NETEX_DATA_OJBECT_VERSION)
                .withRef("AVI:ScheduledStopPoint:" + stopPoint.getObjectId());

        PointProjection pointProjection = netexFactory.createPointProjection()
                .withVersion(NETEX_DATA_OJBECT_VERSION)
                .withId("AVI:PointProjection:" + stopPoint.getObjectId())
                .withProjectedPointRef(pointRefStruct);

        Projections_RelStructure projections = netexFactory.createProjections_RelStructure()
                .withProjectionRefOrProjection(netexFactory.createPointProjection(pointProjection));
        routePoint.setProjections(projections);

        return routePoint;
    }

}
