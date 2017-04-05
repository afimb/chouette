package mobi.chouette.exchange.netexprofile.exporter.producer;

import mobi.chouette.common.Context;
import mobi.chouette.model.*;
import mobi.chouette.model.JourneyPattern;
import mobi.chouette.model.Line;
import mobi.chouette.model.Network;
import mobi.chouette.model.Route;
import mobi.chouette.model.StopArea;
import mobi.chouette.model.VehicleJourney;
import org.apache.commons.lang.StringUtils;
import org.rutebanken.netex.model.*;

import java.math.BigInteger;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static mobi.chouette.exchange.netexprofile.Constant.PRODUCING_CONTEXT;
import static mobi.chouette.exchange.netexprofile.exporter.producer.NetexProducerUtils.isSet;
import static mobi.chouette.exchange.netexprofile.util.NetexObjectIdTypes.*;

public class NetexProducer {

    public static final String OBJECT_ID_SPLIT_CHAR = ":";
    public static final String NETEX_DATA_OJBECT_VERSION = "0";

    public static ObjectFactory netexFactory = null;

    static {
        try {
            netexFactory = new ObjectFactory();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String netexId(NeptuneIdentifiedObject model) {
        return model == null ? null : model.objectIdPrefix() + OBJECT_ID_SPLIT_CHAR + netexModelName(model) + OBJECT_ID_SPLIT_CHAR + model.objectIdSuffix();
    }

    public static String netexId(String objectIdPrefix, String elementName, String objectIdSuffix) {
        return objectIdPrefix + OBJECT_ID_SPLIT_CHAR + elementName + OBJECT_ID_SPLIT_CHAR + objectIdSuffix;
    }

    public static String netexModelName(NeptuneIdentifiedObject model) {
        if (model == null)
            return null;
        if (model instanceof StopArea) {
            return "StopArea";
        } else if (model instanceof AccessPoint) {
            return "AccessPoint";
        } else if (model instanceof Company) {
            return "Operator";
        } else if (model instanceof AccessLink) {
            return "AccessLink";
        } else if (model instanceof StopPoint) {
            return "StopPoint";
        } else if (model instanceof Network) {
            return "GroupOfLine";
        } else if (model instanceof Line) {
            return "Line";
        } else if (model instanceof Route) {
            return "Route";
        } else if (model instanceof GroupOfLine) {
            return "GroupOfLine";
        } else if (model instanceof JourneyPattern) {
            return "JourneyPattern";
        } else if (model instanceof ConnectionLink) {
            return "ConnectionLink";
        } else if (model instanceof Timetable) {
            return "Timetable";
        } else if (model instanceof VehicleJourney) {
            return "ServiceJourney";
        } else {
            return null;
        }
    }

    protected MultilingualString getMultilingualString(String value) {
        return netexFactory.createMultilingualString()
                .withValue(value);
    }

    public static void resetContext(Context context) {
        Context parsingContext = (Context) context.get(PRODUCING_CONTEXT);
        if (parsingContext != null) {
            for (String key : parsingContext.keySet()) {
                Context localContext = (Context) parsingContext.get(key);
                localContext.clear();
            }
        }
    }

    public static Context getObjectContext(Context context, String localContextName, String objectId) {
        Context parsingContext = (Context) context.get(PRODUCING_CONTEXT);
        if (parsingContext == null) {
            parsingContext = new Context();
            context.put(PRODUCING_CONTEXT, parsingContext);
        }

        Context localContext = (Context) parsingContext.get(localContextName);
        if (localContext == null) {
            localContext = new Context();
            parsingContext.put(localContextName, localContext);
        }

        Context objectContext = (Context) localContext.get(objectId);
        if (objectContext == null) {
            objectContext = new Context();
            localContext.put(objectId, objectContext);
        }

        return objectContext;
    }

    public String objectIdPrefix(String objectId) {
        return objectId.split(OBJECT_ID_SPLIT_CHAR).length > 2 ? objectId.split(OBJECT_ID_SPLIT_CHAR)[0].trim() : "";
    }

    public String objectIdSuffix(String objectId) {
        return objectId.split(OBJECT_ID_SPLIT_CHAR).length > 2 ? objectId.split(OBJECT_ID_SPLIT_CHAR)[2].trim() : "";
    }

    protected AvailabilityCondition createAvailabilityCondition(mobi.chouette.model.Line line) {
        String availabilityConditionId = netexId(line.objectIdPrefix(), AVAILABILITY_CONDITION_KEY, line.objectIdSuffix());
        AvailabilityCondition availabilityCondition = netexFactory.createAvailabilityCondition();
        availabilityCondition.setVersion(line.getObjectVersion() > 0 ? String.valueOf(line.getObjectVersion()) : NETEX_DATA_OJBECT_VERSION);
        availabilityCondition.setId(availabilityConditionId);

        availabilityCondition.setFromDate(OffsetDateTime.now(ZoneId.systemDefault())); // TODO fix correct from date, for now using dummy dates
        availabilityCondition.setToDate(availabilityCondition.getFromDate().plusMonths(1L)); // TODO fix correct to date, for now using dummy dates
        return availabilityCondition;
    }

    protected Set<RoutePoint> createRoutePoints(List<Route> routes) {
        Set<RoutePoint> routePoints = new HashSet<>();
        Set<String> distinctRoutePointIds = new HashSet<>();

        for (mobi.chouette.model.Route route : routes) {
            for (StopPoint stopPoint : route.getStopPoints()) {
                String[] idSuffixSplit = StringUtils.splitByWholeSeparator(stopPoint.objectIdSuffix(), "-");
                String stopPointIdSuffix = idSuffixSplit[idSuffixSplit.length - 1];
                String routePointId = netexId(stopPoint.objectIdPrefix(), ROUTE_POINT_KEY, stopPointIdSuffix);

                if (!distinctRoutePointIds.contains(routePointId)) {
                    RoutePoint routePoint = createRoutePoint(routePointId, stopPoint, stopPointIdSuffix);
                    routePoints.add(routePoint);
                    distinctRoutePointIds.add(routePointId);
                }
            }
        }
        return routePoints;
    }

    private RoutePoint createRoutePoint(String routePointId, StopPoint stopPoint, String stopPointIdSuffix) {
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
        return routePoint;
    }

    protected Set<ScheduledStopPoint> createScheduledStopPoints(List<mobi.chouette.model.Route> routes) {
        Set<ScheduledStopPoint> scheduledStopPoints = new HashSet<>();
        Set<String> distinctStopPointIds = new HashSet<>();

        for (mobi.chouette.model.Route route : routes) {
            for (StopPoint stopPoint : route.getStopPoints()) {
                String[] idSuffixSplit = StringUtils.splitByWholeSeparator(stopPoint.objectIdSuffix(), "-");
                String stopPointIdSuffix = idSuffixSplit[idSuffixSplit.length - 1];
                String stopPointId = netexId(stopPoint.objectIdPrefix(), STOP_POINT_KEY, stopPointIdSuffix);

                if (!distinctStopPointIds.contains(stopPointId)) {
                    ScheduledStopPoint scheduledStopPoint = createScheduledStopPoint(stopPoint, stopPointId);
                    scheduledStopPoints.add(scheduledStopPoint);
                    distinctStopPointIds.add(stopPointId);
                }
            }
        }
        return scheduledStopPoints;
    }

    private ScheduledStopPoint createScheduledStopPoint(StopPoint stopPoint, String stopPointId) {
        ScheduledStopPoint scheduledStopPoint = netexFactory.createScheduledStopPoint();
        scheduledStopPoint.setVersion(stopPoint.getObjectVersion() > 0 ? String.valueOf(stopPoint.getObjectVersion()) : NETEX_DATA_OJBECT_VERSION);
        scheduledStopPoint.setId(stopPointId);

        if (isSet(stopPoint.getContainedInStopArea().getName())) {
            scheduledStopPoint.setName(getMultilingualString(stopPoint.getContainedInStopArea().getName()));
        }

        return scheduledStopPoint;
    }

    protected Set<PassengerStopAssignment> createStopAssignments(List<mobi.chouette.model.Route> routes) {
        Set<PassengerStopAssignment> stopAssignments = new HashSet<>();
        Set<String> distinctStopPointIdRefs = new HashSet<>();

        int index = 1;
        for (mobi.chouette.model.Route route : routes) {
            for (StopPoint stopPoint : route.getStopPoints()) {
                String[] idSuffixSplit = StringUtils.splitByWholeSeparator(stopPoint.objectIdSuffix(), "-");
                String stopPointIdSuffix = idSuffixSplit[idSuffixSplit.length - 1];
                String stopPointIdRef = netexId(stopPoint.objectIdPrefix(), STOP_POINT_KEY, stopPointIdSuffix);

                if (!distinctStopPointIdRefs.contains(stopPointIdRef)) {
                    PassengerStopAssignment stopAssignment = createStopAssignment(stopPoint, stopPointIdSuffix, index, stopPointIdRef);
                    stopAssignments.add(stopAssignment);
                    distinctStopPointIdRefs.add(stopPointIdRef);
                    index++;
                }
            }
        }

        return stopAssignments;
    }

    private PassengerStopAssignment createStopAssignment(StopPoint stopPoint, String stopPointIdSuffix, int order, String stopPointIdRef) {
        String pointVersion = stopPoint.getObjectVersion() > 0 ? String.valueOf(stopPoint.getObjectVersion()) : NETEX_DATA_OJBECT_VERSION;
        String stopAssignmentId = netexId(stopPoint.objectIdPrefix(), PASSENGER_STOP_ASSIGNMENT_KEY, stopPointIdSuffix);

        PassengerStopAssignment stopAssignment = netexFactory.createPassengerStopAssignment()
                .withVersion(pointVersion)
                .withId(stopAssignmentId)
                .withOrder(new BigInteger(Integer.toString(order)));

        ScheduledStopPointRefStructure scheduledStopPointRefStruct = netexFactory.createScheduledStopPointRefStructure()
                .withVersion(pointVersion)
                .withRef(stopPointIdRef);
        stopAssignment.setScheduledStopPointRef(scheduledStopPointRefStruct);

        if (isSet(stopPoint.getContainedInStopArea())) {
            if (isSet(stopPoint.getContainedInStopArea().getParent())) {
                mobi.chouette.model.StopArea parentStopArea = stopPoint.getContainedInStopArea().getParent();
                String stopPlaceVersion = parentStopArea.getObjectVersion() > 0 ? String.valueOf(parentStopArea.getObjectVersion()) : NETEX_DATA_OJBECT_VERSION;
                String stopPlaceIdRef = netexId(parentStopArea.objectIdPrefix(), STOP_PLACE_KEY, parentStopArea.objectIdSuffix());

                StopPlaceRefStructure stopPlaceRefStruct = netexFactory.createStopPlaceRefStructure()
                        .withVersion(stopPlaceVersion)
                        .withRef(stopPlaceIdRef);
                stopAssignment.setStopPlaceRef(stopPlaceRefStruct);
            }

            mobi.chouette.model.StopArea containedInStopArea = stopPoint.getContainedInStopArea();
            String quayVersion = containedInStopArea.getObjectVersion() > 0 ? String.valueOf(containedInStopArea.getObjectVersion()) : NETEX_DATA_OJBECT_VERSION;
            String quayIdRef = netexId(containedInStopArea.objectIdPrefix(), QUAY_KEY, containedInStopArea.objectIdSuffix());

            QuayRefStructure quayRefStruct = netexFactory.createQuayRefStructure()
                    .withVersion(quayVersion)
                    .withRef(quayIdRef);
            stopAssignment.setQuayRef(quayRefStruct);
        }

        return stopAssignment;
    }

}
