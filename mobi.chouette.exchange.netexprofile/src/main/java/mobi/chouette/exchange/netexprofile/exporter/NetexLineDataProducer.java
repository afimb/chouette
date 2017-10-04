package mobi.chouette.exchange.netexprofile.exporter;

import static mobi.chouette.exchange.netexprofile.exporter.producer.NetexProducerUtils.isSet;
import static mobi.chouette.exchange.netexprofile.exporter.producer.NetexProducerUtils.netexId;
import static mobi.chouette.exchange.netexprofile.util.NetexObjectIdTypes.NOTICE;
import static mobi.chouette.exchange.netexprofile.util.NetexObjectIdTypes.NOTICE_ASSIGNMENT;
import static mobi.chouette.exchange.netexprofile.util.NetexObjectIdTypes.PASSENGER_STOP_ASSIGNMENT;
import static mobi.chouette.exchange.netexprofile.util.NetexObjectIdTypes.POINT_PROJECTION;
import static mobi.chouette.exchange.netexprofile.util.NetexObjectIdTypes.ROUTE_POINT;
import static mobi.chouette.exchange.netexprofile.util.NetexObjectIdTypes.SCHEDULED_STOP_POINT;

import java.io.File;
import java.math.BigInteger;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.xml.bind.Marshaller;

import org.apache.commons.collections.CollectionUtils;
import org.rutebanken.netex.model.AvailabilityCondition;
import org.rutebanken.netex.model.DestinationDisplay;
import org.rutebanken.netex.model.DestinationDisplayRefStructure;
import org.rutebanken.netex.model.GroupOfLines;
import org.rutebanken.netex.model.Notice;
import org.rutebanken.netex.model.NoticeAssignment;
import org.rutebanken.netex.model.NoticeRefStructure;
import org.rutebanken.netex.model.Organisation_VersionStructure;
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
import mobi.chouette.exchange.netexprofile.ConversionUtil;
import mobi.chouette.exchange.netexprofile.exporter.producer.CalendarProducer;
import mobi.chouette.exchange.netexprofile.exporter.producer.LineProducer;
import mobi.chouette.exchange.netexprofile.exporter.producer.NetexProducer;
import mobi.chouette.exchange.netexprofile.exporter.producer.NetexProducerUtils;
import mobi.chouette.exchange.netexprofile.exporter.producer.NetworkProducer;
import mobi.chouette.exchange.netexprofile.exporter.producer.OrganisationProducer;
import mobi.chouette.exchange.netexprofile.exporter.producer.RouteProducer;
import mobi.chouette.exchange.netexprofile.exporter.producer.ServiceJourneyInterchangeProducer;
import mobi.chouette.exchange.netexprofile.exporter.producer.ServiceJourneyPatternProducer;
import mobi.chouette.exchange.netexprofile.exporter.producer.ServiceJourneyProducer;
import mobi.chouette.exchange.netexprofile.exporter.producer.StopPlaceProducer;
import mobi.chouette.exchange.report.ActionReporter;
import mobi.chouette.exchange.report.IO_TYPE;
import mobi.chouette.model.Company;
import mobi.chouette.model.Footnote;
import mobi.chouette.model.GroupOfLine;
import mobi.chouette.model.Interchange;
import mobi.chouette.model.JourneyPattern;
import mobi.chouette.model.StopArea;
import mobi.chouette.model.StopPoint;

import org.apache.commons.collections.CollectionUtils;
import org.rutebanken.netex.model.AvailabilityCondition;
import org.rutebanken.netex.model.DestinationDisplay;
import org.rutebanken.netex.model.DestinationDisplayRefStructure;
import org.rutebanken.netex.model.GroupOfLines;
import org.rutebanken.netex.model.Organisation_VersionStructure;
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
import org.rutebanken.netex.model.Via_VersionedChildStructure;
import org.rutebanken.netex.model.Vias_RelStructure;

import static mobi.chouette.exchange.netexprofile.exporter.producer.NetexProducerUtils.isSet;
import static mobi.chouette.exchange.netexprofile.exporter.producer.NetexProducerUtils.netexId;
import static mobi.chouette.exchange.netexprofile.util.NetexObjectIdTypes.*;

public class NetexLineDataProducer extends NetexProducer implements Constant {

	private static OrganisationProducer organisationProducer = new OrganisationProducer();
	private static StopPlaceProducer stopPlaceProducer = new StopPlaceProducer();
	private static NetworkProducer networkProducer = new NetworkProducer();
	private static LineProducer lineProducer = new LineProducer();
	private static RouteProducer routeProducer = new RouteProducer();
	private static ServiceJourneyPatternProducer journeyPatternProducer = new ServiceJourneyPatternProducer();
	private static CalendarProducer calendarProducer = new CalendarProducer();
	private static ServiceJourneyProducer serviceJourneyProducer = new ServiceJourneyProducer();
	private static ServiceJourneyInterchangeProducer serviceJourneyInterchangeProducer = new ServiceJourneyInterchangeProducer();

	public void produce(Context context) throws Exception {

		NetexprofileExportParameters parameters = (NetexprofileExportParameters) context.get(Constant.CONFIGURATION);

		ActionReporter reporter = ActionReporter.Factory.getInstance();
		JobData jobData = (JobData) context.get(JOB_DATA);
		Path outputPath = Paths.get(jobData.getPathName(), OUTPUT);
		ExportableData exportableData = (ExportableData) context.get(EXPORTABLE_DATA);
		ExportableNetexData exportableNetexData = (ExportableNetexData) context.get(EXPORTABLE_NETEX_DATA);
		mobi.chouette.model.Line neptuneLine = exportableData.getLine();

		produceAndCollectLineData(context, exportableData, exportableNetexData);
		produceAndCollectSharedData(context, exportableData, exportableNetexData);

		String fileName = ExportedFilenamer.createLineFilename(context, neptuneLine);
		reporter.addFileReport(context, fileName, IO_TYPE.OUTPUT);
		Path filePath = new File(outputPath.toFile(), fileName).toPath();

		Marshaller marshaller = (Marshaller) context.get(MARSHALLER);
		NetexFileWriter writer = new NetexFileWriter();
		writer.writeXmlFile(context, filePath, exportableData, exportableNetexData, NetexFragmentMode.LINE, marshaller);

		if (parameters.isAddMetadata()) {
			Metadata metadata = (Metadata) context.get(METADATA);
			if (metadata != null) {
				metadata.getResources().add(
						metadata.new Resource(fileName, NeptuneObjectPresenter.getName(neptuneLine.getNetwork()), NeptuneObjectPresenter.getName(neptuneLine)));
			}
		}
	}

	private void produceAndCollectLineData(Context context, ExportableData exportableData, ExportableNetexData exportableNetexData) {
		NetexprofileExportParameters configuration = (NetexprofileExportParameters) context.get(Constant.CONFIGURATION);
		mobi.chouette.model.Line neptuneLine = exportableData.getLine();

		AvailabilityCondition availabilityCondition = createAvailabilityCondition(context);
		exportableNetexData.setLineCondition(availabilityCondition);

		org.rutebanken.netex.model.Line netexLine = lineProducer.produce(context, neptuneLine);
		exportableNetexData.setLine(netexLine);

		for (mobi.chouette.model.Route neptuneRoute : neptuneLine.getRoutes()) {
			org.rutebanken.netex.model.Route netexRoute = routeProducer.produce(context, neptuneRoute);
			exportableNetexData.getRoutes().add(netexRoute);
		}

		for (mobi.chouette.model.Route route : neptuneLine.getRoutes()) {
			for (mobi.chouette.model.JourneyPattern neptuneJourneyPattern : route.getJourneyPatterns()) {
				org.rutebanken.netex.model.ServiceJourneyPattern netexJourneyPattern = journeyPatternProducer.produce(context, neptuneJourneyPattern);
				exportableNetexData.getJourneyPatterns().add(netexJourneyPattern);
			}
		}

		produceAndCollectRoutePoints(exportableData.getLine().getRoutes(), exportableNetexData);
		produceAndCollectScheduledStopPoints(exportableData.getLine().getRoutes(), exportableNetexData);
		produceAndCollectStopAssignments(exportableData.getLine().getRoutes(), exportableNetexData, configuration);


		calendarProducer.produce(context, exportableData, exportableNetexData);

		for (mobi.chouette.model.VehicleJourney vehicleJourney : exportableData.getVehicleJourneys()) {
			ServiceJourney serviceJourney = serviceJourneyProducer.produce(context, vehicleJourney, exportableData.getLine());
			exportableNetexData.getServiceJourneys().add(serviceJourney);

			for (Interchange interchange : vehicleJourney.getConsumerInterchanges()) {
				exportableNetexData.getServiceJourneyInterchanges().add(serviceJourneyInterchangeProducer.produce(context, interchange));
			}


			JourneyPattern neptuneJourneyPattern = vehicleJourney.getJourneyPattern();
			if (neptuneJourneyPattern != null) {
				if (!exportableNetexData.getJourneyPatterns().containsKey(vehicleJourney.getJourneyPattern().getObjectId())) {
					org.rutebanken.netex.model.ServiceJourneyPattern netexJourneyPattern = journeyPatternProducer.produce(context, neptuneJourneyPattern);
					exportableNetexData.getJourneyPatterns().put(netexJourneyPattern.getId(), netexJourneyPattern);
				}
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

		AvailabilityCondition availabilityCondition = createAvailabilityCondition(context);
		exportableNetexData.setCommonCondition(availabilityCondition);

		for (Company company : exportableData.getCompanies()) {
			if (!exportableNetexData.getSharedOrganisations().containsKey(company.getObjectId())) {
				Organisation_VersionStructure organisation = organisationProducer.produce(context, company);
				exportableNetexData.getSharedOrganisations().put(company.getObjectId(), organisation);
			}
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
		NetexProducerUtils.populateId(groupOfLine, groupOfLines);
		groupOfLines.setName(ConversionUtil.getMultiLingualString(groupOfLine.getName()));

		return groupOfLines;
	}

	private void produceAndCollectRoutePoints(List<mobi.chouette.model.Route> routes, ExportableNetexData exportableNetexData) {
		for (mobi.chouette.model.Route route : routes) {
			for (StopPoint stopPoint : route.getStopPoints()) {
				if (stopPoint != null) {
					if (isSet(stopPoint.getScheduledStopPoint().getContainedInStopAreaRef().getObject())) {
						String routePointIdSuffix = stopPoint.getScheduledStopPoint().objectIdSuffix();
						String routePointId = netexId(route.objectIdPrefix(), ROUTE_POINT, routePointIdSuffix);

						if (!exportableNetexData.getRoutePoints().containsKey(routePointId)) {
							RoutePoint routePoint = createRoutePoint(routePointId, stopPoint);
							exportableNetexData.getRoutePoints().put(routePointId, routePoint);
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
		String pointVersion = stopPoint.getObjectVersion() > 0 ? String.valueOf(stopPoint.getObjectVersion()) : NETEX_DEFAULT_OBJECT_VERSION;

		RoutePoint routePoint = netexFactory.createRoutePoint().withVersion(pointVersion).withId(routePointId);



		String containedInSuffix = stopPoint.getScheduledStopPoint().objectIdSuffix();
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
					if (isSet(stopPoint.getScheduledStopPoint().getContainedInStopAreaRef().getObject())) {

						String scheduledStopPointId = stopPoint.getScheduledStopPoint().getObjectId();

						if (!exportableNetexData.getScheduledStopPoints().containsKey(scheduledStopPointId)) {
							ScheduledStopPoint scheduledStopPoint = netexFactory.createScheduledStopPoint();
							NetexProducerUtils.populateId(stopPoint.getScheduledStopPoint(), scheduledStopPoint);
							scheduledStopPoint.setName(ConversionUtil.getMultiLingualString(stopPoint.getScheduledStopPoint().getName()));
							exportableNetexData.getScheduledStopPoints().put(scheduledStopPointId, scheduledStopPoint);
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

			DestinationDisplay netexDestinationDisplay = netexFactory.createDestinationDisplay();
			NetexProducerUtils.populateId(dd, netexDestinationDisplay);

			netexDestinationDisplay.setName(ConversionUtil.getMultiLingualString(dd.getName()));
			netexDestinationDisplay.setFrontText(ConversionUtil.getMultiLingualString(dd.getFrontText()));
			netexDestinationDisplay.setSideText(ConversionUtil.getMultiLingualString(dd.getSideText()));

			exportableNetexData.getSharedDestinationDisplays().put(dd.getObjectId(), netexDestinationDisplay);

			if (dd.getVias() != null && dd.getVias().size() > 0) {
				Vias_RelStructure vias = netexFactory.createVias_RelStructure();
				netexDestinationDisplay.setVias(vias);
				for (mobi.chouette.model.DestinationDisplay via : dd.getVias()) {

					// Recurse into vias, create if missing
					addDestinationDisplay(via, exportableNetexData);

					DestinationDisplayRefStructure ref = netexFactory.createDestinationDisplayRefStructure();
					NetexProducerUtils.populateReference(via, ref, true);

					Via_VersionedChildStructure e = netexFactory.createVia_VersionedChildStructure().withDestinationDisplayRef(ref);

					netexDestinationDisplay.getVias().getVia().add(e);
				}
			}

		}

	}

	private void produceAndCollectStopAssignments(List<mobi.chouette.model.Route> routes, ExportableNetexData exportableNetexData,
			NetexprofileExportParameters parameters) {
		int index = 1;
		for (mobi.chouette.model.Route route : routes) {
			for (StopPoint stopPoint : route.getStopPoints()) {

				if (stopPoint != null) {
					if (isSet(stopPoint.getScheduledStopPoint())) {
						String stopAssignmentIdSuffix = stopPoint.getScheduledStopPoint().objectIdSuffix();
						String stopAssignmentId = netexId(stopPoint.objectIdPrefix(), PASSENGER_STOP_ASSIGNMENT, stopAssignmentIdSuffix);

						if (!exportableNetexData.getStopAssignments().containsKey(stopAssignmentId)) {
							PassengerStopAssignment stopAssignment = createStopAssignment(stopPoint, stopAssignmentId, index, parameters);
							exportableNetexData.getStopAssignments().put(stopAssignmentId, stopAssignment);
						}
						index++;
					} else {
						throw new RuntimeException(
								"StopPoint with id : " + stopPoint.getObjectId() + " is not contained in a StopArea. Cannot produce StopAssignment.");
					}
				}
			}
		}
	}

	private PassengerStopAssignment createStopAssignment(StopPoint stopPoint, String stopAssignmentId, int order, NetexprofileExportParameters parameters) {
		String pointVersion = stopPoint.getObjectVersion() > 0 ? String.valueOf(stopPoint.getObjectVersion()) : NETEX_DEFAULT_OBJECT_VERSION;

		PassengerStopAssignment stopAssignment = netexFactory.createPassengerStopAssignment().withVersion(pointVersion).withId(stopAssignmentId)
				.withOrder(BigInteger.valueOf(order));

		ScheduledStopPointRefStructure scheduledStopPointRef = netexFactory.createScheduledStopPointRefStructure();
		NetexProducerUtils.populateReference(stopPoint.getScheduledStopPoint(), scheduledStopPointRef, true);
		stopAssignment.setScheduledStopPointRef(netexFactory.createScheduledStopPointRef(scheduledStopPointRef));

		if (isSet(stopPoint.getScheduledStopPoint().getContainedInStopAreaRef().getObject())) {
			mobi.chouette.model.StopArea containedInStopArea = stopPoint.getScheduledStopPoint().getContainedInStopAreaRef().getObject();
			QuayRefStructure quayRefStruct = netexFactory.createQuayRefStructure();
			NetexProducerUtils.populateReference(containedInStopArea, quayRefStruct, false);
			stopAssignment.setQuayRef(quayRefStruct);
		}

		return stopAssignment;
	}

}
