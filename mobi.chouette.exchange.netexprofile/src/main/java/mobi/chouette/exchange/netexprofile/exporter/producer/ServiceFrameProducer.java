package mobi.chouette.exchange.netexprofile.exporter.producer;

import mobi.chouette.exchange.netexprofile.exporter.ExportableData;
import mobi.chouette.model.StopPoint;
import org.apache.commons.lang.StringUtils;
import org.rutebanken.netex.model.*;

import java.util.HashSet;
import java.util.Set;

import static mobi.chouette.exchange.netexprofile.exporter.ModelTranslator.netexId;
import static mobi.chouette.exchange.netexprofile.exporter.NetexPublicationDeliveryProducer.NETEX_DATA_OJBECT_VERSION;
import static mobi.chouette.exchange.netexprofile.exporter.producer.AbstractNetexProducer.netexFactory;
import static mobi.chouette.exchange.netexprofile.exporter.producer.NetexProducerUtils.isSet;
import static mobi.chouette.exchange.netexprofile.util.NetexObjectIdTypes.*;

public class ServiceFrameProducer implements FrameProducer<ServiceFrame> {

    private static NetworkProducer networkProducer = new NetworkProducer();
    private static LineProducer lineProducer = new LineProducer();
    private static RouteProducer routeProducer = new RouteProducer();
    private static JourneyPatternProducer journeyPatternProducer = new JourneyPatternProducer();

    @Override
    public ServiceFrame produce(ExportableData data) {
        mobi.chouette.model.Line neptuneLine = data.getLine();
        mobi.chouette.model.Network neptuneNetwork = neptuneLine.getNetwork();

        String serviceFrameId = netexId(neptuneLine.objectIdPrefix(), SERVICE_FRAME_KEY, neptuneLine.objectIdSuffix());

        ServiceFrame serviceFrame = netexFactory.createServiceFrame()
                .withVersion("any")
                .withId(serviceFrameId);
        //.withDestinationDisplays(destinationDisplayStruct)

        // produce network
        org.rutebanken.netex.model.Network netexNetwork = networkProducer.produce(neptuneNetwork);
        serviceFrame.setNetwork(netexNetwork);

        // produce line
        org.rutebanken.netex.model.Line netexLine = lineProducer.produce(neptuneLine, data.getRoutes());

        LinesInFrame_RelStructure linesInFrameStruct = netexFactory.createLinesInFrame_RelStructure();
        linesInFrameStruct.getLine_().add(netexFactory.createLine(netexLine));
        serviceFrame.setLines(linesInFrameStruct);

        // produce route points

        Set<String> distinctRoutePointIds = new HashSet<>();
        RoutePointsInFrame_RelStructure routePointStruct = netexFactory.createRoutePointsInFrame_RelStructure();

        for (mobi.chouette.model.Route route : neptuneLine.getRoutes()) {
            for (StopPoint stopPoint : route.getStopPoints()) {
                String[] idSuffixSplit = StringUtils.splitByWholeSeparator(stopPoint.objectIdSuffix(), "-");
                String stopPointIdSuffix = idSuffixSplit[idSuffixSplit.length - 1];
                String routePointId = netexId(stopPoint.objectIdPrefix(), ROUTE_POINT_KEY, stopPointIdSuffix);

                if (!distinctRoutePointIds.contains(routePointId)) {
                    String pointVersion = stopPoint.getObjectVersion() > 0 ? String.valueOf(stopPoint.getObjectVersion()) : NETEX_DATA_OJBECT_VERSION;

                    RoutePoint routePoint = netexFactory.createRoutePoint()
                            .withVersion(pointVersion)
                            .withId(routePointId);

                    String stopPointIdRef = netexId(stopPoint.objectIdPrefix(), STOP_POINT_KEY, stopPointIdSuffix);

                    PointRefStructure pointRefStruct = netexFactory.createPointRefStructure()
                            .withVersion(pointVersion)
                            .withRef(stopPointIdRef);

                    String pointProjectionId = netexId(stopPoint.objectIdPrefix(), POINT_PROJECTION_KEY, stopPointIdSuffix);

                    PointProjection pointProjection = netexFactory.createPointProjection()
                            .withVersion(pointVersion)
                            .withId(pointProjectionId)
                            .withProjectedPointRef(pointRefStruct);

                    Projections_RelStructure projections = netexFactory.createProjections_RelStructure()
                            .withProjectionRefOrProjection(netexFactory.createPointProjection(pointProjection));
                    routePoint.setProjections(projections);

                    routePointStruct.getRoutePoint().add(routePoint);
                    distinctRoutePointIds.add(routePointId);
                }
            }
        }

        serviceFrame.setRoutePoints(routePointStruct);

        // produce routes
        RoutesInFrame_RelStructure routesInFrame = netexFactory.createRoutesInFrame_RelStructure();

        for (mobi.chouette.model.Route neptuneRoute : neptuneLine.getRoutes()) {
            org.rutebanken.netex.model.Route netexRoute = routeProducer.produce(neptuneRoute);
            routesInFrame.getRoute_().add(netexFactory.createRoute(netexRoute));
        }

        serviceFrame.setRoutes(routesInFrame);

        // produce scheduled stop points

        Set<String> distinctStopPointIds = new HashSet<>();
        ScheduledStopPointsInFrame_RelStructure scheduledStopPointsStruct = netexFactory.createScheduledStopPointsInFrame_RelStructure();

        for (mobi.chouette.model.Route route : neptuneLine.getRoutes()) {
            for (StopPoint stopPoint : route.getStopPoints()) {
                String[] idSuffixSplit = StringUtils.splitByWholeSeparator(stopPoint.objectIdSuffix(), "-");
                String stopPointIdSuffix = idSuffixSplit[idSuffixSplit.length - 1];
                String stopPointId = netexId(stopPoint.objectIdPrefix(), STOP_POINT_KEY, stopPointIdSuffix);

                if (!distinctStopPointIds.contains(stopPointId)) {
                    ScheduledStopPoint scheduledStopPoint = netexFactory.createScheduledStopPoint();
                    scheduledStopPoint.setVersion(stopPoint.getObjectVersion() > 0 ? String.valueOf(stopPoint.getObjectVersion()) : NETEX_DATA_OJBECT_VERSION);
                    scheduledStopPoint.setId(stopPointId);

                    if (isSet(stopPoint.getContainedInStopArea().getName())) {
                        scheduledStopPoint.setName(getMultilingualString(stopPoint.getContainedInStopArea().getName())); // TODO use the one in Abstract class instead
                    }

                    scheduledStopPointsStruct.getScheduledStopPoint().add(scheduledStopPoint);
                    distinctStopPointIds.add(stopPointId);
                }
            }
        }

        serviceFrame.setScheduledStopPoints(scheduledStopPointsStruct);

        // produce service patterns

        JourneyPatternsInFrame_RelStructure journeyPatternStruct = netexFactory.createJourneyPatternsInFrame_RelStructure();

        for (mobi.chouette.model.Route route : neptuneLine.getRoutes()) {
            for (mobi.chouette.model.JourneyPattern neptuneJourneyPattern : route.getJourneyPatterns()) {
                org.rutebanken.netex.model.JourneyPattern netexJourneyPattern = journeyPatternProducer.produce(neptuneJourneyPattern);
                journeyPatternStruct.getJourneyPattern_OrJourneyPatternView().add(netexFactory.createJourneyPattern(netexJourneyPattern));
            }
        }

        serviceFrame.setJourneyPatterns(journeyPatternStruct);

        return serviceFrame;
    }

    private MultilingualString getMultilingualString(String value) {
        return netexFactory.createMultilingualString()
                .withValue(value);
    }

}
