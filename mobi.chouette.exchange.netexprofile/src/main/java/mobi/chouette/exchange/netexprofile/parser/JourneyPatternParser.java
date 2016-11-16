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
import org.apache.commons.lang.StringUtils;
import org.rutebanken.netex.model.*;

import javax.xml.bind.JAXBElement;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.ArrayList;

@Log4j
public class JourneyPatternParser implements NetexParser {

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

        Collection<org.rutebanken.netex.model.JourneyPattern> netexJourneyPatterns = netexReferential.getJourneyPatterns().values();
        for (org.rutebanken.netex.model.JourneyPattern netexJourneyPattern : netexJourneyPatterns) {
            mobi.chouette.model.JourneyPattern chouetteJourneyPattern = ObjectFactory.getJourneyPattern(chouetteReferential, netexJourneyPattern.getId());

            // optional in netex! mandatory in chouette?
            RouteRefStructure routeRefStruct = netexJourneyPattern.getRouteRef();
            Route route = null;
            if (routeRefStruct != null) {
                route = ObjectFactory.getRoute(chouetteReferential, routeRefStruct.getRef());
                chouetteJourneyPattern.setRoute(route);
            }

            List<StopPointInJourneyPattern> stopPointsInJourneyPattern = new ArrayList(netexReferential.getStopPointsInJourneyPattern().values());

            // TODO find out how to always traverse the referential collections in correct order, mandatory for ordered stop points where position and order is crucial, for now sorting by id
            Comparator<StopPointInJourneyPattern> stopPointsInJourneyPatternComparator = new Comparator<StopPointInJourneyPattern>() {
                @Override
                public int compare(StopPointInJourneyPattern stopPointInJourneyPattern1, StopPointInJourneyPattern stopPointInJourneyPattern2) {
                    return stopPointInJourneyPattern1.getId().compareTo(stopPointInJourneyPattern2.getId());
                }
            };
            Collections.sort(stopPointsInJourneyPattern, stopPointsInJourneyPatternComparator);

            int index = 1;
            for (StopPointInJourneyPattern stopPointInJourneyPattern : stopPointsInJourneyPattern) {
                StopPoint stopPoint = null;
                JAXBElement<? extends ScheduledStopPointRefStructure> scheduledStopPointRefElement = stopPointInJourneyPattern.getScheduledStopPointRef();
                if (scheduledStopPointRefElement != null) {
                    ScheduledStopPointRefStructure scheduledStopPointRefStruct = scheduledStopPointRefElement.getValue();
                    if (scheduledStopPointRefStruct != null) {
                        String scheduledStopPointRefValue = scheduledStopPointRefStruct.getRef();
                        if (StringUtils.isNotEmpty(scheduledStopPointRefValue)) {
                            // TODO fix the ids
                            String chouetteStopPointId =  scheduledStopPointRefValue + "-" + index;
                            stopPoint = ObjectFactory.getStopPoint(chouetteReferential, chouetteStopPointId);
                            chouetteJourneyPattern.addStopPoint(stopPoint);
                        }
                    }
                }
                index++;
            }

            List<StopPoint> stopPoints = chouetteJourneyPattern.getStopPoints();
            if (stopPoints != null && stopPoints.size() > 0) {
                StopPoint departureStopPoint = stopPoints.get(0);
                chouetteJourneyPattern.setDepartureStopPoint(departureStopPoint);
                StopPoint arrivalStopPoint = stopPoints.get(stopPoints.size() - 1);
                chouetteJourneyPattern.setArrivalStopPoint(arrivalStopPoint);
                // TODO: how to handle elements ForAlighting and ForBoarding in chouette?
            }

            // TODO: add all remaining optional elements, for now we only support RouteRef and pointsInSequence. See: https://rutebanken.atlassian.net/wiki/display/PUBLIC/network#network-JourneyPattern

            chouetteJourneyPattern.setFilled(true);
            chouetteReferential.getJourneyPatterns().put(chouetteJourneyPattern.getObjectId(), chouetteJourneyPattern);
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
