package mobi.chouette.exchange.netexprofile.exporter.producer;

import mobi.chouette.model.Route;
import mobi.chouette.model.StopPoint;
import mobi.chouette.model.type.AlightingPossibilityEnum;
import mobi.chouette.model.type.BoardingPossibilityEnum;
import org.apache.commons.lang.StringUtils;
import org.rutebanken.netex.model.*;

import java.math.BigInteger;
import java.util.List;

import static mobi.chouette.exchange.netexprofile.exporter.producer.NetexProducerUtils.isSet;
import static mobi.chouette.exchange.netexprofile.util.NetexObjectIdTypes.*;

public class JourneyPatternProducer extends NetexProducer implements NetexEntityProducer<org.rutebanken.netex.model.JourneyPattern, mobi.chouette.model.JourneyPattern> {

    @Override
    public org.rutebanken.netex.model.JourneyPattern produce(mobi.chouette.model.JourneyPattern neptuneJourneyPattern) {
        org.rutebanken.netex.model.JourneyPattern netexJourneyPattern = netexFactory.createJourneyPattern();
        netexJourneyPattern.setVersion(neptuneJourneyPattern.getObjectVersion() > 0 ? String.valueOf(neptuneJourneyPattern.getObjectVersion()) : NETEX_DATA_OJBECT_VERSION);

        String journeyPatternId = netexId(neptuneJourneyPattern.objectIdPrefix(), JOURNEY_PATTERN_KEY, neptuneJourneyPattern.objectIdSuffix());
        netexJourneyPattern.setId(journeyPatternId);

        if (isSet(neptuneJourneyPattern.getComment())) {
            KeyValueStructure keyValueStruct = netexFactory.createKeyValueStructure()
                    .withKey("Comment")
                    .withValue(neptuneJourneyPattern.getComment());
            netexJourneyPattern.setKeyList(netexFactory.createKeyListStructure().withKeyValue(keyValueStruct));
        }

        if (isSet(neptuneJourneyPattern.getName())) {
            netexJourneyPattern.setName(getMultilingualString(neptuneJourneyPattern.getName()));
        }

        if (isSet(neptuneJourneyPattern.getPublishedName())) {
            netexJourneyPattern.setShortName(getMultilingualString(neptuneJourneyPattern.getPublishedName()));
        }

        if (isSet(neptuneJourneyPattern.getRegistrationNumber())) {
            PrivateCodeStructure privateCodeStruct = netexFactory.createPrivateCodeStructure();
            privateCodeStruct.setValue(neptuneJourneyPattern.getRegistrationNumber());
            netexJourneyPattern.setPrivateCode(privateCodeStruct);
        }

        Route route = neptuneJourneyPattern.getRoute();
        RouteRefStructure routeRefStruct = netexFactory.createRouteRefStructure();
        routeRefStruct.setVersion(route.getObjectVersion() != null ? String.valueOf(route.getObjectVersion()) : NETEX_DATA_OJBECT_VERSION);

        String routeIdRef = netexId(route.objectIdPrefix(), ROUTE_KEY, route.objectIdSuffix());
        routeRefStruct.setRef(routeIdRef);

        netexJourneyPattern.setRouteRef(routeRefStruct);

        // TODO add points in sequence

        PointsInJourneyPattern_RelStructure pointsInJourneyPattern = netexFactory.createPointsInJourneyPattern_RelStructure();
        List<StopPoint> stopPoints = neptuneJourneyPattern.getStopPoints();
        String[] idSequence = NetexProducerUtils.generateIdSequence(stopPoints.size());

        for (int i = 0; i < stopPoints.size(); i++) {
            StopPoint stopPoint = stopPoints.get(i);

            StopPointInJourneyPattern stopPointInJourneyPattern = netexFactory.createStopPointInJourneyPattern();
            stopPointInJourneyPattern.setVersion(stopPoint.getObjectVersion() > 0 ? String.valueOf(stopPoint.getObjectVersion()) : NETEX_DATA_OJBECT_VERSION);

            String pointInPatternIdSuffix = neptuneJourneyPattern.objectIdSuffix() + StringUtils.leftPad(idSequence[i], 2, "0");
            String stopPointInJourneyPatternId = netexId(neptuneJourneyPattern.objectIdPrefix(), STOP_POINT_IN_JOURNEY_PATTERN_KEY, pointInPatternIdSuffix);
            stopPointInJourneyPattern.setId(stopPointInJourneyPatternId);

            String[] idSuffixSplit = StringUtils.splitByWholeSeparator(stopPoint.objectIdSuffix(), "-");
            String stopPointIdSuffix = idSuffixSplit[idSuffixSplit.length - 1];

            String stopRefVersion = stopPoint.getObjectVersion() > 0 ? String.valueOf(stopPoint.getObjectVersion()) : NETEX_DATA_OJBECT_VERSION;
            String stopPointIdRef = netexId(stopPoint.objectIdPrefix(), STOP_POINT_KEY, stopPointIdSuffix);

            ScheduledStopPointRefStructure stopPointRefStruct = netexFactory.createScheduledStopPointRefStructure()
                .withVersion(stopRefVersion) // TODO consider making this a boolean parameter
                .withRef(stopPointIdRef);

            stopPointInJourneyPattern.setScheduledStopPointRef(netexFactory.createScheduledStopPointRef(stopPointRefStruct));

            if (isSet(stopPoint.getForBoarding()) || isSet(stopPoint.getForAlighting())) {
                if (isSet(stopPoint.getForBoarding()) && stopPoint.getForBoarding().equals(BoardingPossibilityEnum.normal)) {
                    stopPointInJourneyPattern.setForBoarding(true);
                }
                if (isSet(stopPoint.getForAlighting()) && stopPoint.getForAlighting().equals(AlightingPossibilityEnum.normal)) {
                    stopPointInJourneyPattern.setForAlighting(true);
                }
            }

            stopPointInJourneyPattern.setOrder(BigInteger.valueOf(i + 1));
            pointsInJourneyPattern.getPointInJourneyPatternOrStopPointInJourneyPatternOrTimingPointInJourneyPattern().add(stopPointInJourneyPattern);
        }

        netexJourneyPattern.setPointsInSequence(pointsInJourneyPattern);
        return netexJourneyPattern;
    }

}
