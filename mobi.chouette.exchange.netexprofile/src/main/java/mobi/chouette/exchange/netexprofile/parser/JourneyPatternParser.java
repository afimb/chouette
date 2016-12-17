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
import org.rutebanken.netex.model.JourneyPatternsInFrame_RelStructure;
import org.rutebanken.netex.model.PointInLinkSequence_VersionedChildStructure;
import org.rutebanken.netex.model.PointsInJourneyPattern_RelStructure;
import org.rutebanken.netex.model.StopPointInJourneyPattern;

import javax.xml.bind.JAXBElement;
import java.util.Comparator;
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

        JourneyPatternsInFrame_RelStructure contextData = (JourneyPatternsInFrame_RelStructure) context.get(NETEX_LINE_DATA_CONTEXT);
        List<JAXBElement<?>> journeyPatternElements = contextData.getJourneyPattern_OrJourneyPatternView();

        for (JAXBElement<?> journeyPatternElement : journeyPatternElements) {
            org.rutebanken.netex.model.JourneyPattern_VersionStructure netexJourneyPattern = (org.rutebanken.netex.model.JourneyPattern_VersionStructure) journeyPatternElement.getValue();
            mobi.chouette.model.JourneyPattern chouetteJourneyPattern = ObjectFactory.getJourneyPattern(chouetteReferential, netexJourneyPattern.getId());

            String routeIdRef = netexJourneyPattern.getRouteRef().getRef();
            Route route = ObjectFactory.getRoute(chouetteReferential, routeIdRef);
            chouetteJourneyPattern.setRoute(route);

            chouetteJourneyPattern.setPublishedName(route.getPublishedName());

            PointsInJourneyPattern_RelStructure pointsInSequenceStruct = netexJourneyPattern.getPointsInSequence();

            List<PointInLinkSequence_VersionedChildStructure> pointsInLinkSequence = pointsInSequenceStruct
                    .getPointInJourneyPatternOrStopPointInJourneyPatternOrTimingPointInJourneyPattern();

            for (PointInLinkSequence_VersionedChildStructure pointInLinkSequence : pointsInLinkSequence) {
                StopPointInJourneyPattern stopPointInJourneyPattern = (StopPointInJourneyPattern) pointInLinkSequence;
                String stopPointIdRef = stopPointInJourneyPattern.getScheduledStopPointRef().getValue().getRef();
                StopPoint stopPoint = ObjectFactory.getStopPoint(chouetteReferential, stopPointIdRef);

                // TODO cannot set the position on stop points when parsing common stop points, must be set when we are parsing journey patterns, move
    /*
                BigInteger stopPointOrder = null;
                for (StopPointInJourneyPattern stopPointInJourneyPattern : stopPointsInJourneyPattern) {
                    ScheduledStopPointRefStructure stopPointRefStruct = stopPointInJourneyPattern.getScheduledStopPointRef().getValue();
                    if (chouetteStopPointId.equals(stopPointRefStruct.getRef())) {
                        stopPointOrder = stopPointInJourneyPattern.getOrder();
                    }
                }
                chouetteStopPoint.setPosition(stopPointOrder.intValue());
    */
                chouetteJourneyPattern.addStopPoint(stopPoint);
            }

            List<StopPoint> addedStopPoints = chouetteJourneyPattern.getStopPoints();
            addedStopPoints.sort(Comparator.comparing(StopPoint::getPosition));
            chouetteJourneyPattern.setDepartureStopPoint(addedStopPoints.get(0));
            chouetteJourneyPattern.setArrivalStopPoint(addedStopPoints.get(addedStopPoints.size() - 1));

            // TODO: add all remaining optional elements, for now we only support RouteRef and pointsInSequence.
            //      See: https://rutebanken.atlassian.net/wiki/display/PUBLIC/network#network-JourneyPattern

            chouetteJourneyPattern.setFilled(true);
        }
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
