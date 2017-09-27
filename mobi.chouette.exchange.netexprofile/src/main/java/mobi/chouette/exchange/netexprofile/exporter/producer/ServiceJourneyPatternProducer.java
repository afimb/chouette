package mobi.chouette.exchange.netexprofile.exporter.producer;

import mobi.chouette.common.Context;
import mobi.chouette.exchange.netexprofile.Constant;
import mobi.chouette.exchange.netexprofile.ConversionUtil;
import mobi.chouette.exchange.netexprofile.exporter.ExportableNetexData;
import mobi.chouette.model.Route;
import mobi.chouette.model.StopPoint;
import mobi.chouette.model.type.AlightingPossibilityEnum;
import mobi.chouette.model.type.BoardingPossibilityEnum;
import org.rutebanken.netex.model.*;

import java.math.BigInteger;
import java.util.Comparator;
import java.util.List;

import static mobi.chouette.exchange.netexprofile.exporter.producer.NetexProducerUtils.isSet;

public class ServiceJourneyPatternProducer extends NetexProducer implements NetexEntityProducer<org.rutebanken.netex.model.ServiceJourneyPattern, mobi.chouette.model.JourneyPattern> {

    @Override
    public org.rutebanken.netex.model.ServiceJourneyPattern produce(Context context, mobi.chouette.model.JourneyPattern neptuneJourneyPattern) {
        org.rutebanken.netex.model.ServiceJourneyPattern netexJourneyPattern = netexFactory.createServiceJourneyPattern();

        ExportableNetexData exportableNetexData = (ExportableNetexData) context.get(Constant.EXPORTABLE_NETEX_DATA);

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
        
		NoticeProducer.addNoticeAndNoticeAssignments(context, exportableNetexData, exportableNetexData.getNoticeAssignmentsServiceFrame(), neptuneJourneyPattern.getFootnotes(), neptuneJourneyPattern);

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
					NetexProducerUtils.populateReference(stopPoint.getScheduledStopPoint(), stopPointRefStruct, true);
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

        		NoticeProducer.addNoticeAndNoticeAssignments(context, exportableNetexData, exportableNetexData.getNoticeAssignmentsServiceFrame(), stopPoint.getFootnotes(), stopPoint);

				pointsInJourneyPattern.getPointInJourneyPatternOrStopPointInJourneyPatternOrTimingPointInJourneyPattern().add(stopPointInJourneyPattern);
			}
		}


        netexJourneyPattern.setPointsInSequence(pointsInJourneyPattern);
        return netexJourneyPattern;
    }

}
