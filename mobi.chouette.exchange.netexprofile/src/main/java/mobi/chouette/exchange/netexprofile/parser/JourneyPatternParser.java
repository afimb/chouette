package mobi.chouette.exchange.netexprofile.parser;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Context;
import mobi.chouette.exchange.importer.Parser;
import mobi.chouette.exchange.importer.ParserFactory;
import mobi.chouette.exchange.netexprofile.importer.util.NetexObjectUtil;
import mobi.chouette.exchange.netexprofile.importer.util.NetexReferential;
import mobi.chouette.model.Route;
import mobi.chouette.model.StopPoint;
import mobi.chouette.model.util.ObjectFactory;
import mobi.chouette.model.util.Referential;
import org.rutebanken.netex.model.*;

import javax.xml.bind.JAXBElement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Log4j
public class JourneyPatternParser extends AbstractParser {

    public static final String LOCAL_CONTEXT = "JourneyPatternContext";
    public static final String JOURNEY_PATTERN_ID = "journeyPatternId";

    @Override
    public void initReferentials(Context context) throws Exception {
        NetexReferential referential = (NetexReferential) context.get(NETEX_REFERENTIAL);

        JourneyPatternsInFrame_RelStructure contextData = (JourneyPatternsInFrame_RelStructure) context.get(NETEX_LINE_DATA_CONTEXT);
        List<JAXBElement<?>> journeyPatternElements = contextData.getJourneyPattern_OrJourneyPatternView();

        for (JAXBElement<?> journeyPatternElement : journeyPatternElements) {
            org.rutebanken.netex.model.JourneyPattern journeyPattern = (org.rutebanken.netex.model.JourneyPattern) journeyPatternElement.getValue();
            String objectId = journeyPattern.getId();

            // 1. initialize stop points in journey pattern references
            PointsInJourneyPattern_RelStructure pointsInSequenceStruct = journeyPattern.getPointsInSequence();

            if (pointsInSequenceStruct != null) {
                List<PointInLinkSequence_VersionedChildStructure> pointsInLinkSequence = pointsInSequenceStruct.getPointInJourneyPatternOrStopPointInJourneyPatternOrTimingPointInJourneyPattern();

                for (PointInLinkSequence_VersionedChildStructure pointInLinkSequence : pointsInLinkSequence) {
                    StopPointInJourneyPattern stopPointInJourneyPattern = (StopPointInJourneyPattern) pointInLinkSequence;
                    String stopPointObjectId = stopPointInJourneyPattern.getId();
                    NetexObjectUtil.addStopPointInJourneyPatternReference(referential, stopPointObjectId, stopPointInJourneyPattern);
                }
            }

            NetexObjectUtil.addJourneyPatternReference(referential, objectId, journeyPattern);
        }
    }

    @Override
    public void parse(Context context) throws Exception {
        Referential chouetteReferential = (Referential) context.get(REFERENTIAL);
        NetexReferential netexReferential = (NetexReferential) context.get(NETEX_REFERENTIAL);
        Context parsingContext = (Context) context.get(PARSING_CONTEXT);
        Context routeContext = (Context) parsingContext.get(RouteParser.LOCAL_CONTEXT);
        Context stopPointContext = (Context) parsingContext.get(StopPointParser.LOCAL_CONTEXT);

        Collection<org.rutebanken.netex.model.JourneyPattern> netexJourneyPatterns = netexReferential.getJourneyPatterns().values();

        for (org.rutebanken.netex.model.JourneyPattern netexJourneyPattern : netexJourneyPatterns) {
            String netexJourneyPatternId = netexJourneyPattern.getId();
            String chouetteJourneyPatternId = netexJourneyPattern.getId(); // TODO generate neptune id with creator here
            mobi.chouette.model.JourneyPattern chouetteJourneyPattern = ObjectFactory.getJourneyPattern(chouetteReferential, chouetteJourneyPatternId);
            addJourneyPatternIdRef(context, netexJourneyPatternId, chouetteJourneyPatternId);

            RouteRefStructure routeRefStruct = netexJourneyPattern.getRouteRef();
            Context routeObjectContext = (Context) routeContext.get(routeRefStruct.getRef());
            String chouetteRouteId = (String) routeObjectContext.get(RouteParser.ROUTE_ID);
            Route route = ObjectFactory.getRoute(chouetteReferential, chouetteRouteId);

            chouetteJourneyPattern.setRoute(route);
            chouetteJourneyPattern.setPublishedName(route.getPublishedName());

            // TODO consider if this is necessary, maybe better to parse and handle directly in this class, and not adding to referential
            Collection<StopPointInJourneyPattern> stopPointsInJourneyPatternsColl = netexReferential.getStopPointsInJourneyPattern().values();
            List<StopPointInJourneyPattern> stopPointsInJourneyPattern = new ArrayList(stopPointsInJourneyPatternsColl);


            // probably most correct to do it this way, we must not parse/convert all stop points found for all journey patterns for every occurrence of a journey pattern
            // TODO should probably remove the following section from initReferentials method above

            // mandatory, null check not necessary, because consistency check done in validators
            PointsInJourneyPattern_RelStructure pointsInSequenceStruct = netexJourneyPattern.getPointsInSequence();

            List<PointInLinkSequence_VersionedChildStructure> pointsInLinkSequence = pointsInSequenceStruct
                    .getPointInJourneyPatternOrStopPointInJourneyPatternOrTimingPointInJourneyPattern();

            // TODO how to preserver order, see comparator below, but change id with order
            // TODO beware that there are references from ServiceJourneys to StopPointInJourneyPatterns, change TimetableParser

            for (PointInLinkSequence_VersionedChildStructure pointInLinkSequence : pointsInLinkSequence) {
                StopPointInJourneyPattern stopPointInJourneyPattern = (StopPointInJourneyPattern) pointInLinkSequence;
                ScheduledStopPointRefStructure scheduledStopPointRefStruct = stopPointInJourneyPattern.getScheduledStopPointRef().getValue();

                Context stopPointObjectContext = (Context) stopPointContext.get(scheduledStopPointRefStruct.getRef());
                String chouetteStopPointId = (String) stopPointObjectContext.get(StopPointParser.STOP_POINT_ID);
                StopPoint stopPoint = ObjectFactory.getStopPoint(chouetteReferential, chouetteStopPointId);

                chouetteJourneyPattern.addStopPoint(stopPoint);
            }

            // TODO find out if we need to set the following or not, its not set in regtopp
/*
            List<StopPoint> stopPoints = chouetteJourneyPattern.getStopPoints();

            if (stopPoints != null && stopPoints.size() > 0) {
                StopPoint departureStopPoint = stopPoints.get(0);
                chouetteJourneyPattern.setDepartureStopPoint(departureStopPoint);
                StopPoint arrivalStopPoint = stopPoints.get(stopPoints.size() - 1);
                chouetteJourneyPattern.setArrivalStopPoint(arrivalStopPoint);
                // TODO: how to handle elements ForAlighting and ForBoarding in chouette?
            }
*/

            // TODO: add all remaining optional elements, for now we only support RouteRef and pointsInSequence. See: https://rutebanken.atlassian.net/wiki/display/PUBLIC/network#network-JourneyPattern

            // necessary?
            //chouetteReferential.getJourneyPatterns().put(chouetteJourneyPattern.getObjectId(), chouetteJourneyPattern);

            // TODO find out if this call is needed, see below method, and neptune journey pattern parser, line 105
            // refreshDepartureArrivals(chouetteJourneyPattern);

            // TODO also check out the equivalent method in RegtoppRouteParser, line 119-121
            // sortStopPoints(chouetteReferential);

            // TODO also check out the following, which is called in RegtoppRouteParser after setting stop points on a journey pattern
            // updateRouteNames(referential, configuration);

            chouetteJourneyPattern.setFilled(true);
        }
    }

    private void addJourneyPatternIdRef(Context context, String objectId, String journeyPatternId) {
        Context objectContext = getObjectContext(context, LOCAL_CONTEXT, objectId);
        objectContext.put(JOURNEY_PATTERN_ID, journeyPatternId);
    }

/*
    protected void updateRouteNames(Referential referential, RegtoppImportParameters configuration) {

        for (Route route : referential.getRoutes().values()) {
            if (route.getName() == null) {
                // Set to last stop
                List<StopPoint> stopPoints = route.getStopPoints();
                if (stopPoints != null && !stopPoints.isEmpty()) {
                    StopArea lastStopArea = stopPoints.get(stopPoints.size() - 1).getContainedInStopArea();
                    if (lastStopArea.getParent() == null) {
                        route.setName(lastStopArea.getName());
                    } else {
                        route.setName(lastStopArea.getParent().getName());
                    }
                }
            }

            route.setPublishedName(route.getName());

            for (mobi.chouette.model.JourneyPattern jp : route.getJourneyPatterns()) {

                // Set arrival and departure

                jp.setName(route.getName());
            }

            // default direction and wayback = R if opposite Route = A, else A
        }
    }
*/


/*
    protected void sortStopPoints(Referential referential) {
        Comparator<StopPoint> stopPointSequenceComparator = new Comparator<StopPoint>() {
            @Override
            public int compare(StopPoint arg0, StopPoint arg1) {
                return arg0.getPosition().compareTo(arg1.getPosition());
            }
        };

        // Sort stopPoints on JourneyPattern
        Collection<mobi.chouette.model.JourneyPattern> journeyPatterns = referential.getJourneyPatterns().values();
        for (mobi.chouette.model.JourneyPattern jp : journeyPatterns) {
            List<StopPoint> stopPoints = jp.getStopPoints();
            Collections.sort(stopPoints, stopPointSequenceComparator);
            jp.setDepartureStopPoint(stopPoints.get(0));
            jp.setArrivalStopPoint(stopPoints.get(stopPoints.size() - 1));
        }

        // Sort stopPoints on route
        Collection<Route> routes = referential.getRoutes().values();
        for (Route r : routes) {
            List<StopPoint> stopPoints = r.getStopPoints();
            Collections.sort(stopPoints, stopPointSequenceComparator);
        }
    }
*/

    /**
     * update departure and arrival of JourneyPattern <br/>
     * to be used after stopPoints update
     */
/*
    public static void refreshDepartureArrivals(mobi.chouette.model.JourneyPattern jp) {
        List<StopPoint> stopPoints = jp.getStopPoints();
        if (stopPoints == null || stopPoints.isEmpty()) {
            jp.setDepartureStopPoint(null);
            jp.setArrivalStopPoint(null);
        } else {
            for (StopPoint stopPoint : stopPoints) {
                if (stopPoint.getPosition() == null) {
                    log.warn("stopPoint without position " + stopPoint.getObjectId());
                    return;
                }
            }
            Collections.sort(jp.getStopPoints(), new Comparator<StopPoint>() {

                @Override
                public int compare(StopPoint arg0, StopPoint arg1) {
                    return arg0.getPosition().intValue() - arg1.getPosition().intValue();
                }
            });
            jp.setDepartureStopPoint(stopPoints.get(0));
            jp.setArrivalStopPoint(stopPoints.get(stopPoints.size() - 1));
        }
    }
*/

    static {
        ParserFactory.register(JourneyPatternParser.class.getName(), new ParserFactory() {
            private JourneyPatternParser instance = new JourneyPatternParser();

            @Override
            protected Parser create() {
                return instance;
            }
        });
    }

}
