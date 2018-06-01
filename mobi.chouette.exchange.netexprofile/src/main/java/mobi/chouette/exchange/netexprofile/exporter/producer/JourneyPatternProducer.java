package mobi.chouette.exchange.netexprofile.exporter.producer;

import java.math.BigInteger;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import mobi.chouette.common.Context;
import mobi.chouette.common.TimeUtil;
import mobi.chouette.exchange.netexprofile.Constant;
import mobi.chouette.exchange.netexprofile.ConversionUtil;
import mobi.chouette.exchange.netexprofile.exporter.ExportableNetexData;
import mobi.chouette.model.BookingArrangement;
import mobi.chouette.model.Route;
import mobi.chouette.model.StopPoint;
import mobi.chouette.model.type.AlightingPossibilityEnum;
import mobi.chouette.model.type.BoardingPossibilityEnum;

import org.apache.commons.collections.CollectionUtils;
import org.rutebanken.netex.model.BookingArrangementsStructure;
import org.rutebanken.netex.model.DestinationDisplayRefStructure;
import org.rutebanken.netex.model.KeyValueStructure;
import org.rutebanken.netex.model.MultilingualString;
import org.rutebanken.netex.model.PointsInJourneyPattern_RelStructure;
import org.rutebanken.netex.model.PrivateCodeStructure;
import org.rutebanken.netex.model.RouteRefStructure;
import org.rutebanken.netex.model.ScheduledStopPointRefStructure;
import org.rutebanken.netex.model.StopPointInJourneyPattern;

import static mobi.chouette.exchange.netexprofile.exporter.producer.NetexProducerUtils.isSet;
import static mobi.chouette.exchange.netexprofile.exporter.producer.NetexProducerUtils.netexId;
import static mobi.chouette.exchange.netexprofile.util.NetexObjectIdTypes.SCHEDULED_STOP_POINT;

public class JourneyPatternProducer extends NetexProducer implements NetexEntityProducer<org.rutebanken.netex.model.JourneyPattern, mobi.chouette.model.JourneyPattern> {

	private static ContactStructureProducer contactStructureProducer = new ContactStructureProducer();

	@Override
	public org.rutebanken.netex.model.JourneyPattern produce(Context context, mobi.chouette.model.JourneyPattern neptuneJourneyPattern) {
		org.rutebanken.netex.model.JourneyPattern netexJourneyPattern = netexFactory.createJourneyPattern();

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

                if (isSet(stopPoint.getContainedInStopArea())) {
                	String stopPointIdSuffix = stopPoint.getContainedInStopArea().objectIdSuffix();
                    String stopPointIdRef = netexId(stopPoint.objectIdPrefix(), SCHEDULED_STOP_POINT, stopPointIdSuffix);

                    ScheduledStopPointRefStructure stopPointRefStruct = netexFactory.createScheduledStopPointRefStructure().withRef(stopPointIdRef);
                    stopPointInJourneyPattern.setScheduledStopPointRef(netexFactory.createScheduledStopPointRef(stopPointRefStruct));
                } else {
                    throw new RuntimeException("StopPoint with id : " + stopPoint.getObjectId() + " is not contained in a StopArea. Cannot produce ScheduledStopPoint reference.");
                }

                BoardingPossibilityEnum forBoarding = stopPoint.getForBoarding();
                AlightingPossibilityEnum forAlighting = stopPoint.getForAlighting();

				if (AlightingPossibilityEnum.forbidden.equals(forAlighting)){
					stopPointInJourneyPattern.setForAlighting(false);
				}
				if (BoardingPossibilityEnum.forbidden.equals(forBoarding)) {
					stopPointInJourneyPattern.setForBoarding(false);
				}

				if (BoardingPossibilityEnum.request_stop.equals(forBoarding) || AlightingPossibilityEnum.request_stop.equals(forAlighting)){
					stopPointInJourneyPattern.setRequestStop(true);
				}

				stopPointInJourneyPattern.setOrder(BigInteger.valueOf(i + 1));

                if(stopPoint.getDestinationDisplay() != null) {
                	DestinationDisplayRefStructure destinationDisplayRef = netexFactory.createDestinationDisplayRefStructure();
                	destinationDisplayRef.setRef(stopPoint.getDestinationDisplay().getObjectId());
                	stopPointInJourneyPattern.setDestinationDisplayRef(destinationDisplayRef);
                }

        		NoticeProducer.addNoticeAndNoticeAssignments(context, exportableNetexData, exportableNetexData.getNoticeAssignmentsServiceFrame(), stopPoint.getFootnotes(), stopPoint);

				BookingArrangement bookingArrangement = stopPoint.getBookingArrangement();
				if (bookingArrangement != null) {
					BookingArrangementsStructure netexBookingArrangement = new BookingArrangementsStructure();
					if (bookingArrangement.getBookingNote() != null) {
						netexBookingArrangement.setBookingNote(new MultilingualString().withValue(bookingArrangement.getBookingNote()));
					}
					netexBookingArrangement.setBookingAccess(ConversionUtil.toBookingAccess(bookingArrangement.getBookingAccess()));
					netexBookingArrangement.setBookWhen(ConversionUtil.toPurchaseWhen(bookingArrangement.getBookWhen()));
					if (!CollectionUtils.isEmpty(bookingArrangement.getBuyWhen())) {
						netexBookingArrangement.withBuyWhen(bookingArrangement.getBuyWhen().stream().map(ConversionUtil::toPurchaseMoment).collect(Collectors.toList()));
					}
					if (!CollectionUtils.isEmpty(bookingArrangement.getBookingMethods())) {
						netexBookingArrangement.withBookingMethods(bookingArrangement.getBookingMethods().stream().map(ConversionUtil::toBookingMethod).collect(Collectors.toList()));
					}
					netexBookingArrangement.setLatestBookingTime(TimeUtil.toLocalTimeFromJoda(bookingArrangement.getLatestBookingTime()));
					netexBookingArrangement.setMinimumBookingPeriod(TimeUtil.toDurationFromJodaDuration(bookingArrangement.getMinimumBookingPeriod()));

					netexBookingArrangement.setBookingContact(contactStructureProducer.produce(bookingArrangement.getBookingContact()));
					stopPointInJourneyPattern.setBookingArrangements(netexBookingArrangement);
				}

                pointsInJourneyPattern.getPointInJourneyPatternOrStopPointInJourneyPatternOrTimingPointInJourneyPattern().add(stopPointInJourneyPattern);
            }
        }

        netexJourneyPattern.setPointsInSequence(pointsInJourneyPattern);
        return netexJourneyPattern;
    }

}
