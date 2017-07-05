package mobi.chouette.exchange.netexprofile.parser;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Context;
import mobi.chouette.exchange.importer.Parser;
import mobi.chouette.exchange.importer.ParserFactory;
import mobi.chouette.exchange.netexprofile.Constant;
import mobi.chouette.exchange.netexprofile.util.NetexObjectUtil;
import mobi.chouette.exchange.netexprofile.util.NetexReferential;
import mobi.chouette.model.DestinationDisplay;
import mobi.chouette.model.StopPoint;
import mobi.chouette.model.util.ObjectFactory;
import mobi.chouette.model.util.Referential;
import org.apache.commons.collections.CollectionUtils;
import org.rutebanken.netex.model.JourneyPatternsInFrame_RelStructure;
import org.rutebanken.netex.model.PointInLinkSequence_VersionedChildStructure;
import org.rutebanken.netex.model.StopPointInJourneyPattern;

import javax.xml.bind.JAXBElement;
import java.util.Comparator;
import java.util.List;

@Log4j
public class JourneyPatternParser extends NetexParser implements Parser, Constant {

    static final String LOCAL_CONTEXT = "JourneyPattern";
    static final String STOP_POINT_ID = "stopPointId";

    @Override
    public void parse(Context context) throws Exception {
        Referential referential = (Referential) context.get(REFERENTIAL);
        JourneyPatternsInFrame_RelStructure journeyPatternStruct = (JourneyPatternsInFrame_RelStructure) context.get(NETEX_LINE_DATA_CONTEXT);

        for (JAXBElement<?> journeyPatternElement : journeyPatternStruct.getJourneyPattern_OrJourneyPatternView()) {
            org.rutebanken.netex.model.JourneyPattern netexJourneyPattern = (org.rutebanken.netex.model.JourneyPattern) journeyPatternElement.getValue();
            mobi.chouette.model.JourneyPattern chouetteJourneyPattern = ObjectFactory.getJourneyPattern(referential, netexJourneyPattern.getId());

            chouetteJourneyPattern.setObjectVersion(NetexParserUtils.getVersion(netexJourneyPattern));

            String routeIdRef = netexJourneyPattern.getRouteRef().getRef();
            mobi.chouette.model.Route route = ObjectFactory.getRoute(referential, routeIdRef);
            chouetteJourneyPattern.setRoute(route);

            if (netexJourneyPattern.getName() != null) {
                chouetteJourneyPattern.setName(netexJourneyPattern.getName().getValue());
            } else {
                chouetteJourneyPattern.setName(route.getName());
            }

            if (netexJourneyPattern.getPrivateCode() != null) {
                chouetteJourneyPattern.setRegistrationNumber(netexJourneyPattern.getPrivateCode().getValue());
            }

            parseStopPointsInJourneyPattern(context, referential, netexJourneyPattern, chouetteJourneyPattern, route.getStopPoints());
            chouetteJourneyPattern.setFilled(true);
        }
    }

    private void parseStopPointsInJourneyPattern(Context context, Referential referential, org.rutebanken.netex.model.JourneyPattern netexJourneyPattern,
            mobi.chouette.model.JourneyPattern chouetteJourneyPattern, List<StopPoint> routeStopPoints) throws Exception {

        NetexReferential netexReferential = (NetexReferential) context.get(NETEX_REFERENTIAL);

        List<PointInLinkSequence_VersionedChildStructure> pointsInLinkSequence = netexJourneyPattern.getPointsInSequence()
                .getPointInJourneyPatternOrStopPointInJourneyPatternOrTimingPointInJourneyPattern();

        for (int i = 0; i < pointsInLinkSequence.size(); i++) {
            PointInLinkSequence_VersionedChildStructure pointInSequence = pointsInLinkSequence.get(i);
            StopPointInJourneyPattern pointInPattern = (StopPointInJourneyPattern) pointInSequence;

            String stopPointId = routeStopPoints.get(i).getObjectId();
            StopPoint stopPoint = ObjectFactory.getStopPoint(referential, stopPointId);
            
            if(pointInPattern.getDestinationDisplayRef() != null) {
            	DestinationDisplay destinationDisplay = ObjectFactory.getDestinationDisplay(referential, pointInPattern.getDestinationDisplayRef().getRef());
            	stopPoint.setDestinationDisplay(destinationDisplay);
            }
            
            
            chouetteJourneyPattern.addStopPoint(stopPoint);

            addStopPointId(context, pointInPattern.getId(), stopPointId);
            NetexObjectUtil.addStopPointInJourneyPatternRef(netexReferential, pointInPattern.getId(), pointInPattern);
        }

        List<StopPoint> patternStopPoints = chouetteJourneyPattern.getStopPoints();
        if (CollectionUtils.isNotEmpty(patternStopPoints)) {
            chouetteJourneyPattern.getStopPoints().sort(Comparator.comparingInt(StopPoint::getPosition));
            chouetteJourneyPattern.setDepartureStopPoint(patternStopPoints.get(0));
            chouetteJourneyPattern.setArrivalStopPoint(patternStopPoints.get(patternStopPoints.size() - 1));
        }
    }

    private void addStopPointId(Context context, String objectId, String stopPointId) {
        Context objectContext = getObjectContext(context, LOCAL_CONTEXT, objectId);
        objectContext.put(STOP_POINT_ID, stopPointId);
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
