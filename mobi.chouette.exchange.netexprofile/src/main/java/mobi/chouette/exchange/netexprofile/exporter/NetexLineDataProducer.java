package mobi.chouette.exchange.netexprofile.exporter;

import static mobi.chouette.exchange.netexprofile.exporter.producer.CalendarProducer.DAY_TYPES_KEY;
import static mobi.chouette.exchange.netexprofile.exporter.producer.CalendarProducer.DAY_TYPE_ASSIGNMENTS_KEY;
import static mobi.chouette.exchange.netexprofile.exporter.producer.CalendarProducer.OPERATING_PERIODS_KEY;
import static mobi.chouette.exchange.netexprofile.exporter.producer.NetexProducerUtils.isSet;
import static mobi.chouette.exchange.netexprofile.exporter.producer.NetexProducerUtils.netexId;
import static mobi.chouette.exchange.netexprofile.util.NetexObjectIdTypes.AUTHORITY;
import static mobi.chouette.exchange.netexprofile.util.NetexObjectIdTypes.NOTICE;
import static mobi.chouette.exchange.netexprofile.util.NetexObjectIdTypes.NOTICE_ASSIGNMENT;
import static mobi.chouette.exchange.netexprofile.util.NetexObjectIdTypes.PASSENGER_STOP_ASSIGNMENT;
import static mobi.chouette.exchange.netexprofile.util.NetexObjectIdTypes.POINT_PROJECTION;
import static mobi.chouette.exchange.netexprofile.util.NetexObjectIdTypes.QUAY;
import static mobi.chouette.exchange.netexprofile.util.NetexObjectIdTypes.ROUTE_POINT;
import static mobi.chouette.exchange.netexprofile.util.NetexObjectIdTypes.SCHEDULED_STOP_POINT;

import java.io.File;
import java.math.BigInteger;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.bind.Marshaller;

import org.apache.commons.collections.CollectionUtils;
import org.rutebanken.netex.model.Authority;
import org.rutebanken.netex.model.AvailabilityCondition;
import org.rutebanken.netex.model.ContactStructure;
import org.rutebanken.netex.model.DataManagedObjectStructure;
import org.rutebanken.netex.model.DayType;
import org.rutebanken.netex.model.DayTypeAssignment;
import org.rutebanken.netex.model.DestinationDisplay;
import org.rutebanken.netex.model.DestinationDisplayRefStructure;
import org.rutebanken.netex.model.GroupOfLines;
import org.rutebanken.netex.model.Notice;
import org.rutebanken.netex.model.NoticeAssignment;
import org.rutebanken.netex.model.NoticeRefStructure;
import org.rutebanken.netex.model.OperatingPeriod;
import org.rutebanken.netex.model.Operator;
import org.rutebanken.netex.model.OrganisationTypeEnumeration;
import org.rutebanken.netex.model.PassengerStopAssignment;
import org.rutebanken.netex.model.PointProjection;
import org.rutebanken.netex.model.PointRefStructure;
import org.rutebanken.netex.model.Projections_RelStructure;
import org.rutebanken.netex.model.QuayRefStructure;
import org.rutebanken.netex.model.RoutePoint;
import org.rutebanken.netex.model.ScheduledStopPoint;
import org.rutebanken.netex.model.ScheduledStopPointRefStructure;
import org.rutebanken.netex.model.ServiceJourney;
import org.rutebanken.netex.model.StopPlace;
import org.rutebanken.netex.model.VersionOfObjectRefStructure;
import org.rutebanken.netex.model.Via_VersionedChildStructure;
import org.rutebanken.netex.model.Vias_RelStructure;

import mobi.chouette.common.Context;
import mobi.chouette.common.JobData;
import mobi.chouette.exchange.metadata.Metadata;
import mobi.chouette.exchange.metadata.NeptuneObjectPresenter;
import mobi.chouette.exchange.netexprofile.Constant;
import mobi.chouette.exchange.netexprofile.exporter.producer.CalendarProducer;
import mobi.chouette.exchange.netexprofile.exporter.producer.JourneyPatternProducer;
import mobi.chouette.exchange.netexprofile.exporter.producer.LineProducer;
import mobi.chouette.exchange.netexprofile.exporter.producer.NetexProducer;
import mobi.chouette.exchange.netexprofile.exporter.producer.NetworkProducer;
import mobi.chouette.exchange.netexprofile.exporter.producer.OperatorProducer;
import mobi.chouette.exchange.netexprofile.exporter.producer.RouteProducer;
import mobi.chouette.exchange.netexprofile.exporter.producer.ServiceJourneyInterchangeProducer;
import mobi.chouette.exchange.netexprofile.exporter.producer.ServiceJourneyProducer;
import mobi.chouette.exchange.netexprofile.exporter.producer.StopPlaceProducer;
import mobi.chouette.exchange.report.ActionReporter;
import mobi.chouette.exchange.report.IO_TYPE;
import mobi.chouette.model.Company;
import mobi.chouette.model.Footnote;
import mobi.chouette.model.GroupOfLine;
import mobi.chouette.model.Interchange;
import mobi.chouette.model.StopArea;
import mobi.chouette.model.StopPoint;

public class NetexLineDataProducer extends NetexProducer implements Constant {

	private static final String NSR_OBJECT_ID = "NSR:Authority:NSR";
	private static final String NSR_COMPANY_NUMBER = "917422575";
	private static final String NSR_NAME = "Nasjonal Stoppestedsregister";
	private static final String NSR_LEGAL_NAME = "NASJONAL STOPPESTEDSREGISTER";
	private static final String NSR_PHONE = "0047 236 20 000";

	private static OperatorProducer operatorProducer = new OperatorProducer();
	private static StopPlaceProducer stopPlaceProducer = new StopPlaceProducer();
	private static NetworkProducer networkProducer = new NetworkProducer();
	private static LineProducer lineProducer = new LineProducer();
	private static RouteProducer routeProducer = new RouteProducer();
	private static JourneyPatternProducer journeyPatternProducer = new JourneyPatternProducer();
	private static CalendarProducer calendarProducer = new CalendarProducer();
	private static ServiceJourneyProducer serviceJourneyProducer = new ServiceJourneyProducer();
	private static ServiceJourneyInterchangeProducer serviceJourneyInterchangeProducer = new ServiceJourneyInterchangeProducer();

	public void produce(Context context) throws Exception {
		ActionReporter reporter = ActionReporter.Factory.getInstance();
		JobData jobData = (JobData) context.get(JOB_DATA);
		Metadata metadata = (Metadata) context.get(METADATA);
		Path outputPath = Paths.get(jobData.getPathName(), OUTPUT);
		ExportableData exportableData = (ExportableData) context.get(EXPORTABLE_DATA);
		ExportableNetexData exportableNetexData = (ExportableNetexData) context.get(EXPORTABLE_NETEX_DATA);
		mobi.chouette.model.Line neptuneLine = exportableData.getLine();

		produceAndCollectLineData(context, exportableData, exportableNetexData);
		produceAndCollectSharedData(context, exportableData, exportableNetexData);

		String fileName = neptuneLine.getObjectId().replaceAll(":", "-") + (neptuneLine.getNumber() != null ? neptuneLine.getNumber() + "-" : "")
				+ (neptuneLine.getPublishedName() != null ? "-" + neptuneLine.getPublishedName().replace(' ', '_').replace('/', '_') : "") + ".xml";
		Path filePath = new File(outputPath.toFile(), fileName).toPath();

		Marshaller marshaller = (Marshaller) context.get(MARSHALLER);

		NetexFileWriter writer = new NetexFileWriter();
		writer.writeXmlFile(context, filePath, exportableData, exportableNetexData, NetexFragmentMode.LINE, marshaller);

		reporter.addFileReport(context, fileName, IO_TYPE.OUTPUT);

		if (metadata != null) {
			metadata.getResources().add(
					metadata.new Resource(fileName, NeptuneObjectPresenter.getName(neptuneLine.getNetwork()), NeptuneObjectPresenter.getName(neptuneLine)));
		}
	}

	@SuppressWarnings("unchecked")
	private void produceAndCollectLineData(Context context, ExportableData exportableData, ExportableNetexData exportableNetexData) {
		mobi.chouette.model.Line neptuneLine = exportableData.getLine();

		AvailabilityCondition availabilityCondition = createAvailabilityCondition(neptuneLine);
		exportableNetexData.setLineCondition(availabilityCondition);

		org.rutebanken.netex.model.Line netexLine = lineProducer.produce(context, neptuneLine);
		exportableNetexData.setLine(netexLine);

		for (mobi.chouette.model.Route neptuneRoute : neptuneLine.getRoutes()) {
			org.rutebanken.netex.model.Route netexRoute = routeProducer.produce(context, neptuneRoute);
			exportableNetexData.getRoutes().add(netexRoute);
		}

		for (mobi.chouette.model.Route route : neptuneLine.getRoutes()) {
			for (mobi.chouette.model.JourneyPattern neptuneJourneyPattern : route.getJourneyPatterns()) {
				org.rutebanken.netex.model.JourneyPattern netexJourneyPattern = journeyPatternProducer.produce(context, neptuneJourneyPattern);
				exportableNetexData.getJourneyPatterns().add(netexJourneyPattern);
			}
		}

		Map<String, List<? extends DataManagedObjectStructure>> calendarData = calendarProducer.produce(context, exportableData);

		List<DayType> dayTypes = (List<DayType>) calendarData.get(DAY_TYPES_KEY);
		exportableNetexData.getDayTypes().addAll(dayTypes);

		List<DayTypeAssignment> dayTypeAssignments = (List<DayTypeAssignment>) calendarData.get(DAY_TYPE_ASSIGNMENTS_KEY);
		exportableNetexData.getDayTypeAssignments().addAll(dayTypeAssignments);

		List<OperatingPeriod> operatingPeriods = (List<OperatingPeriod>) calendarData.get(OPERATING_PERIODS_KEY);
		exportableNetexData.getOperatingPeriods().addAll(operatingPeriods);

		for (mobi.chouette.model.VehicleJourney vehicleJourney : exportableData.getVehicleJourneys()) {
			ServiceJourney serviceJourney = serviceJourneyProducer.produce(context, vehicleJourney, exportableData.getLine());
			exportableNetexData.getServiceJourneys().add(serviceJourney);

			for (int i = 0; i < vehicleJourney.getFootnotes().size(); i++) {
				Footnote footnote = vehicleJourney.getFootnotes().get(i);

				String version = vehicleJourney.getObjectVersion() > 0 ? String.valueOf(vehicleJourney.getObjectVersion()) : NETEX_DATA_OJBECT_VERSION;
				String objectIdSuffix = vehicleJourney.objectIdSuffix() + "-" + i + 1;
				String noticeId = netexId(vehicleJourney.objectIdPrefix(), NOTICE, objectIdSuffix);
				String noticeAssignmentId = netexId(vehicleJourney.objectIdPrefix(), NOTICE_ASSIGNMENT, objectIdSuffix);

				Notice notice = netexFactory.createNotice().withVersion(version).withId(noticeId);

				if (isSet(footnote.getLabel())) {
					notice.setText(getMultilingualString(footnote.getLabel()));
				}
				if (isSet(footnote.getCode())) {
					notice.setPublicCode(footnote.getCode());
				}

				exportableNetexData.getNotices().add(notice);

				NoticeRefStructure noticeRefStruct = netexFactory.createNoticeRefStructure().withVersion(version).withRef(noticeId);

				VersionOfObjectRefStructure versionOfObjectRefStruct = netexFactory.createVersionOfObjectRefStructure().withVersion(version)
						.withRef(serviceJourney.getId());

				NoticeAssignment noticeAssignment = netexFactory.createNoticeAssignment().withVersion(version).withId(noticeAssignmentId)
						.withOrder(BigInteger.valueOf(i + 1)).withNoticeRef(noticeRefStruct).withNoticedObjectRef(versionOfObjectRefStruct);

				exportableNetexData.getNoticeAssignments().add(noticeAssignment);
			}

			for (Interchange interchange : vehicleJourney.getConsumerInterchanges()) {
				exportableNetexData.getServiceJourneyInterchanges().add(serviceJourneyInterchangeProducer.produce(context, interchange));
			}
		}
	}

	private void produceAndCollectSharedData(Context context, ExportableData exportableData, ExportableNetexData exportableNetexData) {
		NetexprofileExportParameters configuration = (NetexprofileExportParameters) context.get(CONFIGURATION);

		produceAndCollectCodespaces(context, exportableNetexData);

		mobi.chouette.model.Network neptuneNetwork = exportableData.getLine().getNetwork();
		org.rutebanken.netex.model.Network netexNetwork = exportableNetexData.getSharedNetworks().get(neptuneNetwork.getObjectId());

		if (netexNetwork == null) {
			netexNetwork = networkProducer.produce(context, neptuneNetwork);
			exportableNetexData.getSharedNetworks().put(neptuneNetwork.getObjectId(), netexNetwork);
		}
		if (CollectionUtils.isNotEmpty(exportableData.getLine().getGroupOfLines())) {
			GroupOfLine groupOfLine = exportableData.getLine().getGroupOfLines().get(0);
			GroupOfLines groupOfLines = exportableNetexData.getSharedGroupsOfLines().get(groupOfLine.getObjectId());

			if (groupOfLines == null) {
				groupOfLines = createGroupOfLines(groupOfLine);
				exportableNetexData.getSharedGroupsOfLines().put(groupOfLine.getObjectId(), groupOfLines);
			}
			if (netexNetwork.getGroupsOfLines() == null) {
				netexNetwork.setGroupsOfLines(netexFactory.createGroupsOfLinesInFrame_RelStructure());
			}
			if (!netexNetwork.getGroupsOfLines().getGroupOfLines().contains(groupOfLines)) {
				netexNetwork.getGroupsOfLines().getGroupOfLines().add(groupOfLines);
			}
		}

		AvailabilityCondition availabilityCondition = createAvailabilityCondition(neptuneNetwork);
		exportableNetexData.setCommonCondition(availabilityCondition);

		if (isSet(neptuneNetwork.getCompany())) {
			if (!exportableNetexData.getSharedAuthorities().containsKey(neptuneNetwork.getCompany().getObjectId())) {
				Authority networkAuthority = createNetworkAuthority(neptuneNetwork);
				exportableNetexData.getSharedAuthorities().put(neptuneNetwork.getCompany().getObjectId(), networkAuthority);
			}
		} else {
			String version = neptuneNetwork.getObjectVersion() > 0 ? String.valueOf(neptuneNetwork.getObjectVersion()) : NETEX_DATA_OJBECT_VERSION;
			String objectId = netexId(neptuneNetwork.objectIdPrefix(), AUTHORITY, neptuneNetwork.objectIdSuffix());

			if (!exportableNetexData.getSharedAuthorities().containsKey(objectId)) {
				Authority networkAuthority = createNetworkAuthority(version, objectId);
				exportableNetexData.getSharedAuthorities().put(objectId, networkAuthority);
			}
		}
		if (!exportableNetexData.getSharedAuthorities().containsKey(NSR_OBJECT_ID)) {
			Authority nsrAuthority = createNsrAuthority(neptuneNetwork);
			exportableNetexData.getSharedAuthorities().put(NSR_OBJECT_ID, nsrAuthority);
		}

		Company company = exportableData.getLine().getCompany();

		if (!exportableNetexData.getSharedOperators().containsKey(company.getObjectId())) {
			Operator operator = operatorProducer.produce(context, company);
			exportableNetexData.getSharedOperators().put(company.getObjectId(), operator);
		}

		if (configuration.isExportStops()) {
			Set<StopArea> stopAreas = new HashSet<>();
			stopAreas.addAll(exportableData.getStopPlaces());
			stopAreas.addAll(exportableData.getCommercialStops());

			for (mobi.chouette.model.StopArea stopArea : stopAreas) {
				if (!exportableNetexData.getSharedStopPlaces().containsKey(stopArea.getObjectId())) {
					StopPlace stopPlace = stopPlaceProducer.produce(context, stopArea);
					exportableNetexData.getSharedStopPlaces().put(stopArea.getObjectId(), stopPlace);
				}
			}
		}

		produceAndCollectRoutePoints(exportableData.getLine().getRoutes(), exportableNetexData);
		produceAndCollectScheduledStopPoints(exportableData.getLine().getRoutes(), exportableNetexData);
		produceAndCollectStopAssignments(exportableData.getLine().getRoutes(), exportableNetexData, configuration);
		produceAndCollectDestinationDisplays(exportableData.getLine().getRoutes(), exportableNetexData);
	}

	@SuppressWarnings("unchecked")
	private void produceAndCollectCodespaces(Context context, ExportableNetexData exportableNetexData) {
		Set<mobi.chouette.model.Codespace> validCodespaces = (Set<mobi.chouette.model.Codespace>) context.get(NETEX_VALID_CODESPACES);

		for (mobi.chouette.model.Codespace validCodespace : validCodespaces) {
			if (!exportableNetexData.getSharedCodespaces().containsKey(validCodespace.getXmlns())) {
				org.rutebanken.netex.model.Codespace netexCodespace = netexFactory.createCodespace().withId(validCodespace.getXmlns().toLowerCase())
						.withXmlns(validCodespace.getXmlns()).withXmlnsUrl(validCodespace.getXmlnsUrl());

				exportableNetexData.getSharedCodespaces().put(validCodespace.getXmlns(), netexCodespace);
			}
		}
	}

	private GroupOfLines createGroupOfLines(GroupOfLine groupOfLine) {
		GroupOfLines groupOfLines = netexFactory.createGroupOfLines();
		groupOfLines.setVersion(groupOfLine.getObjectVersion() > 0 ? String.valueOf(groupOfLine.getObjectVersion()) : NETEX_DATA_OJBECT_VERSION);
		groupOfLines.setId(groupOfLine.getObjectId());

		if (isSet(groupOfLine.getName())) {
			groupOfLines.setName(getMultilingualString(groupOfLine.getName()));
		}

		return groupOfLines;
	}

	private void produceAndCollectRoutePoints(List<mobi.chouette.model.Route> routes, ExportableNetexData exportableNetexData) {
		for (mobi.chouette.model.Route route : routes) {
			for (StopPoint stopPoint : route.getStopPoints()) {
				if (stopPoint != null) {
					if (isSet(stopPoint.getScheduledStopPoint().getContainedInStopArea())) {
						String routePointIdSuffix = stopPoint.getScheduledStopPoint().getContainedInStopArea().objectIdSuffix();
						String routePointId = netexId(route.objectIdPrefix(), ROUTE_POINT, routePointIdSuffix);

						if (!exportableNetexData.getSharedRoutePoints().containsKey(routePointId)) {
							RoutePoint routePoint = createRoutePoint(routePointId, stopPoint);
							exportableNetexData.getSharedRoutePoints().put(routePointId, routePoint);
						}
					} else {
						throw new RuntimeException(
								"StopPoint with id : " + stopPoint.getObjectId() + " is not contained in a StopArea. Cannot produce RoutePoint.");
					}
				}
			}
		}
	}

	private RoutePoint createRoutePoint(String routePointId, StopPoint stopPoint) {
		String pointVersion = stopPoint.getObjectVersion() > 0 ? String.valueOf(stopPoint.getObjectVersion()) : NETEX_DATA_OJBECT_VERSION;

		RoutePoint routePoint = netexFactory.createRoutePoint().withVersion(pointVersion).withId(routePointId);

		String containedInSuffix = stopPoint.getScheduledStopPoint().getContainedInStopArea().objectIdSuffix();
		String stopPointIdRef = netexId(stopPoint.objectIdPrefix(), SCHEDULED_STOP_POINT, containedInSuffix);
		String pointProjectionId = netexId(stopPoint.objectIdPrefix(), POINT_PROJECTION, containedInSuffix);

		PointRefStructure pointRefStruct = netexFactory.createPointRefStructure().withRef(stopPointIdRef).withVersion(pointVersion);

		PointProjection pointProjection = netexFactory.createPointProjection().withVersion(pointVersion).withId(pointProjectionId)
				.withProjectedPointRef(pointRefStruct);

		Projections_RelStructure projections = netexFactory.createProjections_RelStructure()
				.withProjectionRefOrProjection(netexFactory.createPointProjection(pointProjection));
		routePoint.setProjections(projections);

		return routePoint;
	}

	private void produceAndCollectScheduledStopPoints(List<mobi.chouette.model.Route> routes, ExportableNetexData exportableNetexData) {
		for (mobi.chouette.model.Route route : routes) {
			for (StopPoint stopPoint : route.getStopPoints()) {

				if (stopPoint != null) {
					if (isSet(stopPoint.getScheduledStopPoint().getContainedInStopArea())) {
						String scheduledStopPointIdSuffix = stopPoint.getScheduledStopPoint().getContainedInStopArea().objectIdSuffix();
						String scheduledStopPointId = netexId(stopPoint.objectIdPrefix(), SCHEDULED_STOP_POINT, scheduledStopPointIdSuffix);

						if (!exportableNetexData.getSharedStopPoints().containsKey(scheduledStopPointId)) {
							ScheduledStopPoint scheduledStopPoint = createScheduledStopPoint(stopPoint, scheduledStopPointId);
							exportableNetexData.getSharedStopPoints().put(scheduledStopPointId, scheduledStopPoint);
						}
					} else {
						throw new RuntimeException(
								"StopPoint with id : " + stopPoint.getObjectId() + " is not contained in a StopArea. Cannot produce ScheduledStopPoint.");
					}
				}
			}
		}
	}

	private void produceAndCollectDestinationDisplays(List<mobi.chouette.model.Route> routes, ExportableNetexData exportableNetexData) {
		for (mobi.chouette.model.Route route : routes) {
			for (StopPoint stopPoint : route.getStopPoints()) {
				if (stopPoint != null) {
					mobi.chouette.model.DestinationDisplay dd = stopPoint.getDestinationDisplay();
					if (dd != null) {
						addDestinationDisplay(dd, exportableNetexData);
					}
				}
			}
		}
	}

	protected void addDestinationDisplay(mobi.chouette.model.DestinationDisplay dd, ExportableNetexData exportableNetexData) {

		if (!exportableNetexData.getSharedDestinationDisplays().containsKey(dd.getObjectId())) {

			Integer objectVersion = dd.getObjectVersion();
			DestinationDisplay netexDestinationDisplay = netexFactory.createDestinationDisplay();
			netexDestinationDisplay.setId(dd.getObjectId());
			netexDestinationDisplay.setVersion(objectVersion > 0 ? String.valueOf(objectVersion) : NETEX_DATA_OJBECT_VERSION);

			if (isSet(dd.getName())) {
				netexDestinationDisplay.setName(getMultilingualString(dd.getName()));
			}
			if (isSet(dd.getFrontText())) {
				netexDestinationDisplay.setFrontText(getMultilingualString(dd.getFrontText()));
			}
			if (isSet(dd.getSideText())) {
				netexDestinationDisplay.setSideText(getMultilingualString(dd.getSideText()));
			}

			exportableNetexData.getSharedDestinationDisplays().put(dd.getObjectId(), netexDestinationDisplay);

			if (dd.getVias() != null && dd.getVias().size() > 0) {
				Vias_RelStructure vias = netexFactory.createVias_RelStructure();
				netexDestinationDisplay.setVias(vias);
				for (mobi.chouette.model.DestinationDisplay via : dd.getVias()) {

					// Recurse into vias, create if missing
					addDestinationDisplay(via, exportableNetexData);

					DestinationDisplayRefStructure ref = netexFactory.createDestinationDisplayRefStructure().withRef(via.getObjectId())
							.withVersion(via.getObjectVersion() > 0 ? String.valueOf(via.getObjectVersion()) : NETEX_DATA_OJBECT_VERSION);
					Via_VersionedChildStructure e = netexFactory.createVia_VersionedChildStructure().withDestinationDisplayRef(ref);
					netexDestinationDisplay.getVias().getVia().add(e);
				}
			}

		}

	}

	private ScheduledStopPoint createScheduledStopPoint(StopPoint stopPoint, String stopPointId) {
		Integer objectVersion = stopPoint.getObjectVersion();
		ScheduledStopPoint scheduledStopPoint = netexFactory.createScheduledStopPoint();
		scheduledStopPoint.setVersion(objectVersion > 0 ? String.valueOf(objectVersion) : NETEX_DATA_OJBECT_VERSION);
		scheduledStopPoint.setId(stopPointId);

		if (isSet(stopPoint.getScheduledStopPoint().getContainedInStopArea().getName())) {
			scheduledStopPoint.setName(getMultilingualString(stopPoint.getScheduledStopPoint().getContainedInStopArea().getName()));
		}

		return scheduledStopPoint;
	}

	private void produceAndCollectStopAssignments(List<mobi.chouette.model.Route> routes, ExportableNetexData exportableNetexData,
			NetexprofileExportParameters parameters) {
		int index = 1;
		for (mobi.chouette.model.Route route : routes) {
			for (StopPoint stopPoint : route.getStopPoints()) {

				if (stopPoint != null) {
					if (isSet(stopPoint.getScheduledStopPoint().getContainedInStopArea())) {
						String stopAssignmentIdSuffix = stopPoint.getScheduledStopPoint().getContainedInStopArea().objectIdSuffix();
						String stopAssignmentId = netexId(stopPoint.objectIdPrefix(), PASSENGER_STOP_ASSIGNMENT, stopAssignmentIdSuffix);

						if (!exportableNetexData.getSharedStopAssignments().containsKey(stopAssignmentId)) {
							PassengerStopAssignment stopAssignment = createStopAssignment(stopPoint, stopAssignmentId, index, parameters);
							exportableNetexData.getSharedStopAssignments().put(stopAssignmentId, stopAssignment);
							index++;
						}
					} else {
						throw new RuntimeException(
								"StopPoint with id : " + stopPoint.getObjectId() + " is not contained in a StopArea. Cannot produce StopAssignment.");
					}
				}
			}
		}
	}

	private PassengerStopAssignment createStopAssignment(StopPoint stopPoint, String stopAssignmentId, int order, NetexprofileExportParameters parameters) {
		String pointVersion = stopPoint.getObjectVersion() > 0 ? String.valueOf(stopPoint.getObjectVersion()) : NETEX_DATA_OJBECT_VERSION;

		PassengerStopAssignment stopAssignment = netexFactory.createPassengerStopAssignment().withVersion(pointVersion).withId(stopAssignmentId)
				.withOrder(new BigInteger(Integer.toString(order)));

		String stopPointIdRef = netexId(stopPoint.objectIdPrefix(), SCHEDULED_STOP_POINT, stopPoint.getScheduledStopPoint().getContainedInStopArea().objectIdSuffix());

		ScheduledStopPointRefStructure scheduledStopPointRefStruct = netexFactory.createScheduledStopPointRefStructure().withRef(stopPointIdRef)
				.withVersion(pointVersion);
		stopAssignment.setScheduledStopPointRef(scheduledStopPointRefStruct);

		if (isSet(stopPoint.getScheduledStopPoint().getContainedInStopArea())) {
			// if (isSet(stopPoint.getScheduledStopPoint().getContainedInStopArea().getParent())) {
			// mobi.chouette.model.StopArea parentStopArea = stopPoint.getScheduledStopPoint().getContainedInStopArea().getParent();
			// String stopPlaceIdRef = netexId(parentStopArea.objectIdPrefix(), STOP_PLACE, parentStopArea.objectIdSuffix());
			//
			// StopPlaceRefStructure stopPlaceRefStruct = netexFactory.createStopPlaceRefStructure().withRef(stopPlaceIdRef);
			// if(parameters.isExportStops()) {
			// stopPlaceRefStruct.withVersion(parentStopArea.getObjectVersion() > 0 ? String.valueOf(parentStopArea.getObjectVersion()) :
			// NETEX_DATA_OJBECT_VERSION);
			// }
			// stopAssignment.setStopPlaceRef(stopPlaceRefStruct);
			// }

			mobi.chouette.model.StopArea containedInStopArea = stopPoint.getScheduledStopPoint().getContainedInStopArea();
			String quayIdRef = netexId(containedInStopArea.objectIdPrefix(), QUAY, containedInStopArea.objectIdSuffix());

			QuayRefStructure quayRefStruct = netexFactory.createQuayRefStructure().withRef(quayIdRef);
			if (parameters.isExportStops()) {
				quayRefStruct.withVersion(
						containedInStopArea.getObjectVersion() > 0 ? String.valueOf(containedInStopArea.getObjectVersion()) : NETEX_DATA_OJBECT_VERSION);
			}
			stopAssignment.setQuayRef(quayRefStruct);
		}

		return stopAssignment;
	}

	private Authority createNetworkAuthority(mobi.chouette.model.Network network) {
		return createNetworkAuthority(network.getObjectVersion() > 0 ? String.valueOf(network.getObjectVersion()) : NETEX_DATA_OJBECT_VERSION,
				network.getCompany().getObjectId());
	}

	private Authority createNetworkAuthority(String version, String objectId) {
		return netexFactory.createAuthority().withVersion(version).withId(objectId).withCompanyNumber("999999999")
				.withName(getMultilingualString("Dummy Authority")).withLegalName(getMultilingualString("DUMMY AUTHORITY"))
				.withContactDetails(createContactStructure("0047 999 99 999", "http://www.dummy-authority.org/"))
				.withOrganisationType(OrganisationTypeEnumeration.AUTHORITY);
	}

	private Authority createNsrAuthority(mobi.chouette.model.Network network) {
		return netexFactory.createAuthority()
				.withVersion(network.getObjectVersion() > 0 ? String.valueOf(network.getObjectVersion()) : NETEX_DATA_OJBECT_VERSION).withId(NSR_OBJECT_ID)
				.withCompanyNumber(NSR_COMPANY_NUMBER).withName(getMultilingualString(NSR_NAME)).withLegalName(getMultilingualString(NSR_LEGAL_NAME))
				.withContactDetails(createContactStructure(NSR_PHONE, NSR_XMLNSURL)).withOrganisationType(OrganisationTypeEnumeration.AUTHORITY);
	}

	private ContactStructure createContactStructure(String phone, String url) {
		return netexFactory.createContactStructure().withPhone(phone).withUrl(url);
	}

}
