package mobi.chouette.exchange.netexprofile.parser;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Context;
import mobi.chouette.exchange.importer.Parser;
import mobi.chouette.exchange.importer.ParserFactory;
import mobi.chouette.exchange.netexprofile.Constant;
import mobi.chouette.model.Route;
import mobi.chouette.model.StopPoint;
import mobi.chouette.model.util.ObjectFactory;
import mobi.chouette.model.util.Referential;
import org.apache.commons.lang.StringUtils;
import org.rutebanken.netex.model.*;

import javax.xml.bind.JAXBElement;
import java.util.List;
import java.util.Map;

@Log4j
public class JourneyPatternParser implements Parser, Constant {

    @Override
    public void parse(Context context) throws Exception {
        Referential referential = (Referential) context.get(REFERENTIAL);
        JourneyPatternsInFrame_RelStructure contextData = (JourneyPatternsInFrame_RelStructure) context.get(NETEX_LINE_DATA_CONTEXT);
        List<JAXBElement<?>> journeyPatternElements = contextData.getJourneyPattern_OrJourneyPatternView();
        for (JAXBElement<?> journeyPatternElement : journeyPatternElements) {
            org.rutebanken.netex.model.JourneyPattern journeyPattern = (org.rutebanken.netex.model.JourneyPattern) journeyPatternElement.getValue();
            parseJourneyPattern(context, referential, journeyPattern);
        }
    }

    private void parseJourneyPattern(Context context, Referential referential, org.rutebanken.netex.model.JourneyPattern netexJourneyPattern) {
        mobi.chouette.model.JourneyPattern chouetteJourneyPattern = ObjectFactory.getJourneyPattern(referential, netexJourneyPattern.getId());

        // optional
        RouteRefStructure routeRefStruct = netexJourneyPattern.getRouteRef();
        Route route = null;
        if (routeRefStruct != null) {
            route = ObjectFactory.getRoute(referential, routeRefStruct.getRef());
            chouetteJourneyPattern.setRoute(route);
        }

        // TODO: add separate parser for StopPointInJourneyPattern instances
        PointsInJourneyPattern_RelStructure pointsInSequenceStruct = netexJourneyPattern.getPointsInSequence();
        if (pointsInSequenceStruct != null) {
            List<PointInLinkSequence_VersionedChildStructure> pointsInLinkSequence = pointsInSequenceStruct.getPointInJourneyPatternOrStopPointInJourneyPatternOrTimingPointInJourneyPattern();
            for (PointInLinkSequence_VersionedChildStructure pointInLinkSequence : pointsInLinkSequence) {
                StopPointInJourneyPattern stopPointInJourneyPattern = (StopPointInJourneyPattern) pointInLinkSequence;
                Map<String, Object> cachedNetexData = (Map<String, Object>) context.get(NETEX_LINE_DATA_ID_CONTEXT);
                cachedNetexData.put(stopPointInJourneyPattern.getId(), stopPointInJourneyPattern);
                parseStopPointInJourneyPattern(referential, route, stopPointInJourneyPattern, chouetteJourneyPattern);
            }
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
        referential.getJourneyPatterns().put(chouetteJourneyPattern.getObjectId(), chouetteJourneyPattern);
    }

    private void parseStopPointInJourneyPattern(Referential referential, Route route, StopPointInJourneyPattern stopPointInJourneyPattern, mobi.chouette.model.JourneyPattern chouetteJourneyPattern) {
        StopPoint stopPoint = null;
        JAXBElement<? extends ScheduledStopPointRefStructure> scheduledStopPointRefElement = stopPointInJourneyPattern.getScheduledStopPointRef();
        if (scheduledStopPointRefElement != null) {
            ScheduledStopPointRefStructure scheduledStopPointRefStruct = scheduledStopPointRefElement.getValue();
            if (scheduledStopPointRefStruct != null) {
                String scheduledStopPointRefValue= scheduledStopPointRefStruct.getRef();
                if (StringUtils.isNotEmpty(scheduledStopPointRefValue)) {
                    stopPoint = ObjectFactory.getStopPoint(referential, scheduledStopPointRefValue);
                }
            }
        }
        chouetteJourneyPattern.addStopPoint(stopPoint);
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
