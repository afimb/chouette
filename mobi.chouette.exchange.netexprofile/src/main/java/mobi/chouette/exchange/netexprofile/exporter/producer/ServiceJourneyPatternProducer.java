package mobi.chouette.exchange.netexprofile.exporter.producer;

import static mobi.chouette.exchange.netexprofile.exporter.producer.NetexProducerUtils.isSet;

import java.math.BigInteger;
import java.util.Comparator;
import java.util.List;

import org.rutebanken.netex.model.DestinationDisplayRefStructure;
import org.rutebanken.netex.model.KeyValueStructure;
import org.rutebanken.netex.model.PointsInJourneyPattern_RelStructure;
import org.rutebanken.netex.model.PrivateCodeStructure;
import org.rutebanken.netex.model.RouteRefStructure;
import org.rutebanken.netex.model.ScheduledStopPointRefStructure;
import org.rutebanken.netex.model.StopPointInJourneyPattern;

import mobi.chouette.common.Context;
import mobi.chouette.exchange.netexprofile.ConversionUtil;
import mobi.chouette.model.Route;
import mobi.chouette.model.StopPoint;
import mobi.chouette.model.type.AlightingPossibilityEnum;
import mobi.chouette.model.type.BoardingPossibilityEnum;


public class ServiceJourneyPatternProducer extends NetexProducer implements NetexEntityProducer<org.rutebanken.netex.model.ServiceJourneyPattern, mobi.chouette.model.JourneyPattern> {

    @Override
    public org.rutebanken.netex.model.ServiceJourneyPattern produce(Context context, mobi.chouette.model.JourneyPattern neptuneJourneyPattern) {
        org.rutebanken.netex.model.ServiceJourneyPattern netexJourneyPattern = netexFactory.createServiceJourneyPattern();

        NetexProducerUtils.populateId(neptuneJourneyPattern, netexJourneyPattern);

        if (isSet(neptuneJourneyPattern.getComment())) {
            KeyValueStructure keyValueStruct = netexFactory.createKeyValueStructure()
                    .withKey("Comment")
                    .withValue(neptuneJourneyPattern.getComment());
            netexJourneyPattern.setKeyList(netexFactory.createKeyListStructure().withKeyValue(keyValueStruct));
        }

        if (isSet(neptuneJourneyPattern.getName())) {
            netexJourneyPattern.setName(ConversionUtil.getMultiLingualString(neptuneJourneyPattern.getName()));
        }

        if (isSet(neptuneJourneyPattern.getPublishedName())) {
            netexJourneyPattern.setShortName(ConversionUtil.getMultiLingualString(neptuneJourneyPattern.getPublishedName()));
        }

        if (isSet(neptuneJourneyPattern.getRegistrationNumber())) {
            PrivateCodeStructure privateCodeStruct = netexFactory.createPrivateCodeStructure();
            privateCodeStruct.setValue(neptuneJourneyPattern.getRegistrationNumber());
            netexJourneyPattern.setPrivateCode(privateCodeStruct);
        }

        Route route = neptuneJourneyPattern.getRoute();
        RouteRefStructure routeRefStruct = netexFactory.createRouteRefStructure();
        NetexProducerUtils.populateReference(route, routeRefStruct, true);

        netexJourneyPattern.setRouteRef(routeRefStruct);

        PointsInJourneyPattern_RelStructure pointsInJourneyPattern = netexFactory.createPointsInJourneyPattern_RelStructure();
        List<StopPoint> stopPoints = neptuneJourneyPattern.getStopPoints();
        stopPoints.sort(Comparator.comparingInt(StopPoint::getPosition));
        
       

		for (int i = 0; i < stopPoints.size(); i++) {
			StopPoint stopPoint = stopPoints.get(i);

			if (stopPoint != null) {
				StopPointInJourneyPattern stopPointInJourneyPattern = netexFactory.createStopPointInJourneyPattern();
				NetexProducerUtils.populateId(stopPoint, stopPointInJourneyPattern);

				if (stopPoint.getScheduledStopPoint() != null) {
					ScheduledStopPointRefStructure stopPointRefStruct = netexFactory.createScheduledStopPointRefStructure();
					NetexProducerUtils.populateReference(stopPoint.getScheduledStopPoint(), stopPointRefStruct, false);
					stopPointInJourneyPattern.setScheduledStopPointRef(netexFactory.createScheduledStopPointRef(stopPointRefStruct));
				}

				BoardingPossibilityEnum forBoarding = stopPoint.getForBoarding();
				AlightingPossibilityEnum forAlighting = stopPoint.getForAlighting();

				if (forBoarding != null && forAlighting != null) {
					if (forBoarding.equals(BoardingPossibilityEnum.normal) && forAlighting.equals(AlightingPossibilityEnum.forbidden)) {
						stopPointInJourneyPattern.setForAlighting(false);
					}
					if (forAlighting.equals(AlightingPossibilityEnum.normal) && forBoarding.equals(BoardingPossibilityEnum.forbidden)) {
						stopPointInJourneyPattern.setForBoarding(false);
					}
				}

				stopPointInJourneyPattern.setOrder(BigInteger.valueOf(i + 1));

				if (stopPoint.getDestinationDisplay() != null) {
					DestinationDisplayRefStructure destinationDisplayRef = netexFactory.createDestinationDisplayRefStructure();
					destinationDisplayRef.setRef(stopPoint.getDestinationDisplay().getObjectId());
					stopPointInJourneyPattern.setDestinationDisplayRef(destinationDisplayRef);
				}

				pointsInJourneyPattern.getPointInJourneyPatternOrStopPointInJourneyPatternOrTimingPointInJourneyPattern().add(stopPointInJourneyPattern);
			}
		}


        netexJourneyPattern.setPointsInSequence(pointsInJourneyPattern);
        return netexJourneyPattern;
    }

}
