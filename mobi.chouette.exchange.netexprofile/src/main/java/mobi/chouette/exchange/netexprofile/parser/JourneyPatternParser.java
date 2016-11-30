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
            org.rutebanken.netex.model.JourneyPattern_VersionStructure journeyPattern = (org.rutebanken.netex.model.JourneyPattern_VersionStructure) journeyPatternElement.getValue();
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

        Collection<JourneyPattern_VersionStructure> netexJourneyPatterns = netexReferential.getJourneyPatterns().values();

        for (JourneyPattern_VersionStructure netexJourneyPattern : netexJourneyPatterns) {
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

            // TODO should probably remove the following section from initReferentials method above
            //      probably most correct to do it this way, we must not parse/convert all stop points found for all journey patterns for every occurrence of a journey pattern

            // mandatory, null check not necessary, because consistency check done in validators
            PointsInJourneyPattern_RelStructure pointsInSequenceStruct = netexJourneyPattern.getPointsInSequence();

            List<PointInLinkSequence_VersionedChildStructure> pointsInLinkSequence = pointsInSequenceStruct
                    .getPointInJourneyPatternOrStopPointInJourneyPatternOrTimingPointInJourneyPattern();

            for (PointInLinkSequence_VersionedChildStructure pointInLinkSequence : pointsInLinkSequence) {
                StopPointInJourneyPattern stopPointInJourneyPattern = (StopPointInJourneyPattern) pointInLinkSequence;
                ScheduledStopPointRefStructure scheduledStopPointRefStruct = stopPointInJourneyPattern.getScheduledStopPointRef().getValue();

                Context stopPointObjectContext = (Context) stopPointContext.get(scheduledStopPointRefStruct.getRef());
                String chouetteStopPointId = (String) stopPointObjectContext.get(StopPointParser.STOP_POINT_ID);
                StopPoint stopPoint = ObjectFactory.getStopPoint(chouetteReferential, chouetteStopPointId);

                chouetteJourneyPattern.addStopPoint(stopPoint);
            }

            List<StopPoint> addedStopPoints = chouetteJourneyPattern.getStopPoints();
            addedStopPoints.sort((o1, o2) -> o1.getPosition().compareTo(o2.getPosition()));
            chouetteJourneyPattern.setDepartureStopPoint(addedStopPoints.get(0));
            chouetteJourneyPattern.setArrivalStopPoint(addedStopPoints.get(addedStopPoints.size() - 1));

            // TODO: add all remaining optional elements, for now we only support RouteRef and pointsInSequence.
            //      See: https://rutebanken.atlassian.net/wiki/display/PUBLIC/network#network-JourneyPattern

            chouetteJourneyPattern.setFilled(true);
        }
    }

    private void addJourneyPatternIdRef(Context context, String objectId, String journeyPatternId) {
        Context objectContext = getObjectContext(context, LOCAL_CONTEXT, objectId);
        objectContext.put(JOURNEY_PATTERN_ID, journeyPatternId);
    }

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
