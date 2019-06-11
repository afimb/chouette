package mobi.chouette.exchange.netexprofile.exporter.producer;

import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Context;
import mobi.chouette.common.TimeUtil;
import mobi.chouette.exchange.netexprofile.Constant;
import mobi.chouette.exchange.netexprofile.ConversionUtil;
import mobi.chouette.exchange.netexprofile.exporter.ExportableNetexData;
import mobi.chouette.exchange.netexprofile.util.JtsGmlConverter;
import mobi.chouette.model.BookingArrangement;
import mobi.chouette.model.JourneyPattern;
import mobi.chouette.model.Route;
import mobi.chouette.model.RouteSection;
import mobi.chouette.model.ScheduledStopPoint;
import mobi.chouette.model.StopPoint;
import mobi.chouette.model.type.AlightingPossibilityEnum;
import mobi.chouette.model.type.BoardingPossibilityEnum;

import com.vividsolutions.jts.geom.LineString;
import net.opengis.gml._3.LineStringType;
import org.apache.commons.collections.CollectionUtils;
import org.rutebanken.netex.model.BookingArrangementsStructure;
import org.rutebanken.netex.model.DestinationDisplayRefStructure;
import org.rutebanken.netex.model.KeyValueStructure;
import org.rutebanken.netex.model.LinkSequenceProjection;
import org.rutebanken.netex.model.MultilingualString;
import org.rutebanken.netex.model.PointsInJourneyPattern_RelStructure;
import org.rutebanken.netex.model.PrivateCodeStructure;
import org.rutebanken.netex.model.RouteRefStructure;
import org.rutebanken.netex.model.ScheduledStopPointRefStructure;
import org.rutebanken.netex.model.ServiceLink;
import org.rutebanken.netex.model.ServiceLinkInJourneyPattern_VersionedChildStructure;
import org.rutebanken.netex.model.ServiceLinkRefStructure;
import org.rutebanken.netex.model.StopPointInJourneyPattern;

import static mobi.chouette.exchange.netexprofile.exporter.producer.NetexProducerUtils.createUniqueId;
import static mobi.chouette.exchange.netexprofile.exporter.producer.NetexProducerUtils.isSet;
import static mobi.chouette.exchange.netexprofile.util.NetexObjectIdTypes.LINK_SEQUENCE_PROJECTION;
import static mobi.chouette.exchange.netexprofile.util.NetexObjectIdTypes.SERVICE_LINK_IN_JOURNEY_PATTERN;

@Log4j
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

				if (stopPoint.getScheduledStopPoint() != null) {
					ScheduledStopPointRefStructure stopPointRefStruct = netexFactory.createScheduledStopPointRefStructure();
					NetexProducerUtils.populateReference(stopPoint.getScheduledStopPoint(), stopPointRefStruct, false);
					stopPointInJourneyPattern.setScheduledStopPointRef(netexFactory.createScheduledStopPointRef(stopPointRefStruct));
				}

				BoardingPossibilityEnum forBoarding = stopPoint.getForBoarding();
				AlightingPossibilityEnum forAlighting = stopPoint.getForAlighting();

				if (AlightingPossibilityEnum.forbidden.equals(forAlighting)) {
					stopPointInJourneyPattern.setForAlighting(false);
				}
				if (BoardingPossibilityEnum.forbidden.equals(forBoarding)) {
					stopPointInJourneyPattern.setForBoarding(false);
				}

				if (BoardingPossibilityEnum.request_stop.equals(forBoarding) || AlightingPossibilityEnum.request_stop.equals(forAlighting)) {
					stopPointInJourneyPattern.setRequestStop(true);
				}

				stopPointInJourneyPattern.setOrder(BigInteger.valueOf(i + 1));

				if (stopPoint.getDestinationDisplay() != null) {
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

		addLinksInSequence(neptuneJourneyPattern, netexJourneyPattern, context);
		netexJourneyPattern.setPointsInSequence(pointsInJourneyPattern);
		return netexJourneyPattern;
	}

	private void addLinksInSequence(JourneyPattern neptuneJourneyPattern, org.rutebanken.netex.model.JourneyPattern netexJourneyPattern, Context context) {

		if (!CollectionUtils.isEmpty(neptuneJourneyPattern.getRouteSections())) {

			int i = 1;
			netexJourneyPattern.withLinksInSequence(netexFactory.createLinksInJourneyPattern_RelStructure());
			for (RouteSection routeSection : neptuneJourneyPattern.getRouteSections()) {

				String routeSectionVersion = routeSection.getObjectVersion() > 0 ? String.valueOf(routeSection.getObjectVersion()) : NETEX_DEFAULT_OBJECT_VERSION;
				ServiceLinkInJourneyPattern_VersionedChildStructure serviceLinkInJourneyPattern = netexFactory.createServiceLinkInJourneyPattern_VersionedChildStructure()
						.withId(createUniqueId(context, SERVICE_LINK_IN_JOURNEY_PATTERN)).withOrder(BigInteger.valueOf(i++)).withVersion(routeSectionVersion);

				ServiceLinkRefStructure serviceLinkRefStructure = netexFactory.createServiceLinkRefStructure();
				NetexProducerUtils.populateReference(routeSection, serviceLinkRefStructure, false);
				serviceLinkInJourneyPattern.setServiceLinkRef(serviceLinkRefStructure);

				netexJourneyPattern.getLinksInSequence().getServiceLinkInJourneyPatternOrTimingLinkInJourneyPattern().add(serviceLinkInJourneyPattern);

				addServiceLink(routeSection, context);
			}
		}
	}

	protected void addServiceLink(RouteSection routeSection, Context context) {
		ExportableNetexData exportableNetexData = (ExportableNetexData) context.get(Constant.EXPORTABLE_NETEX_DATA);
		if (!exportableNetexData.getSharedServiceLinks().containsKey(routeSection.getObjectId())) {

			ServiceLink serviceLink = netexFactory.createServiceLink();
			NetexProducerUtils.populateId(routeSection, serviceLink);

			String routeSectionVersion = routeSection.getObjectVersion() > 0 ? String.valueOf(routeSection.getObjectVersion()) : NETEX_DEFAULT_OBJECT_VERSION;
			// Gml id must be unique, start with letter and not contain certain special characters.
			String gmlId = "LS_" + routeSection.getId();

			LineString geometry;
			if (routeSection.getNoProcessing()) {
				geometry = routeSection.getInputGeometry();
			} else {
				geometry = routeSection.getProcessedGeometry();
			}


			ScheduledStopPoint fromSSP = routeSection.getFromScheduledStopPoint();
			if (fromSSP != null) {
				ScheduledStopPointRefStructure fromPointRef = netexFactory.createScheduledStopPointRefStructure();
				NetexProducerUtils.populateReference(fromSSP, fromPointRef, true);
				serviceLink.setFromPointRef(fromPointRef);
			}

			ScheduledStopPoint toSSP = routeSection.getToScheduledStopPoint();
			if (toSSP != null) {
				ScheduledStopPointRefStructure topPointRef = netexFactory.createScheduledStopPointRefStructure();
				NetexProducerUtils.populateReference(toSSP, topPointRef, true);
				serviceLink.setToPointRef(topPointRef);
			}
			if (geometry != null) {

				// Only export geometry if route section is a reasonable match for stop areas in both ends. Mimics checks in validation phase which ideally
				// should be ERROR level and halt further processing. This is currently not realistic because too many parties are sending too many faulty
				// route sections and seem unable to fix or remove these without also removing valid ones.
				if (routeSection.isRouteSectionValid()) {

					LineStringType gmlLineString = JtsGmlConverter.fromJtsToGml(geometry, gmlId);
					LinkSequenceProjection linkSequenceProjection = netexFactory.createLinkSequenceProjection().withLineString(gmlLineString);

					linkSequenceProjection.withId(createUniqueId(context, LINK_SEQUENCE_PROJECTION)).withVersion(routeSectionVersion);
					serviceLink.setProjections(netexFactory.createProjections_RelStructure()
							.withProjectionRefOrProjection(netexFactory.createLinkSequenceProjection(linkSequenceProjection)));

				} else {
					log.info("Ignoring linestring for RouteSection with too great distance for from stop and/or to stop: " + routeSection.getObjectId());
				}
			}
			if (routeSection.getDistance() != null) {
				serviceLink.setDistance(routeSection.getDistance().setScale(6, RoundingMode.HALF_UP));
			}
			exportableNetexData.getSharedServiceLinks().put(serviceLink.getId(), serviceLink);
		}

	}

}
