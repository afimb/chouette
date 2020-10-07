package mobi.chouette.exchange.netexprofile.exporter;

import java.io.File;
import java.math.BigInteger;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.xml.bind.Marshaller;

import mobi.chouette.common.Context;
import mobi.chouette.common.JobData;
import mobi.chouette.exchange.metadata.Metadata;
import mobi.chouette.exchange.metadata.NeptuneObjectPresenter;
import mobi.chouette.exchange.netexprofile.Constant;
import mobi.chouette.exchange.netexprofile.ConversionUtil;
import mobi.chouette.exchange.netexprofile.exporter.producer.BrandingProducer;
import mobi.chouette.exchange.netexprofile.exporter.producer.CalendarProducer;
import mobi.chouette.exchange.netexprofile.exporter.producer.DatedServiceJourneyProducer;
import mobi.chouette.exchange.netexprofile.exporter.producer.JourneyPatternProducer;
import mobi.chouette.exchange.netexprofile.exporter.producer.LineProducer;
import mobi.chouette.exchange.netexprofile.exporter.producer.NetexProducer;
import mobi.chouette.exchange.netexprofile.exporter.producer.NetexProducerUtils;
import mobi.chouette.exchange.netexprofile.exporter.producer.NetworkProducer;
import mobi.chouette.exchange.netexprofile.exporter.producer.OrganisationProducer;
import mobi.chouette.exchange.netexprofile.exporter.producer.RouteProducer;
import mobi.chouette.exchange.netexprofile.exporter.producer.ServiceJourneyInterchangeProducer;
import mobi.chouette.exchange.netexprofile.exporter.producer.ServiceJourneyProducer;
import mobi.chouette.exchange.netexprofile.exporter.producer.StopPlaceProducer;
import mobi.chouette.exchange.report.ActionReporter;
import mobi.chouette.exchange.report.IO_TYPE;
import mobi.chouette.model.Branding;
import mobi.chouette.model.Company;
import mobi.chouette.model.DatedServiceJourney;
import mobi.chouette.model.GroupOfLine;
import mobi.chouette.model.Interchange;
import mobi.chouette.model.JourneyPattern;
import mobi.chouette.model.Route;
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

import static mobi.chouette.exchange.netexprofile.exporter.producer.NetexProducerUtils.*;
import static mobi.chouette.exchange.netexprofile.util.NetexObjectIdTypes.*;

public class NetexLineDataProducer extends NetexProducer implements Constant {

	private static OrganisationProducer organisationProducer = new OrganisationProducer();
	private static StopPlaceProducer stopPlaceProducer = new StopPlaceProducer();
	private static NetworkProducer networkProducer = new NetworkProducer();
	private static LineProducer lineProducer = new LineProducer();
	private static RouteProducer routeProducer = new RouteProducer();
	private static JourneyPatternProducer journeyPatternProducer = new JourneyPatternProducer();
	private static CalendarProducer calendarProducer = new CalendarProducer();
	private static ServiceJourneyProducer serviceJourneyProducer = new ServiceJourneyProducer();
	private static DatedServiceJourneyProducer datedServiceJourneyProducer = new DatedServiceJourneyProducer();
	private static ServiceJourneyInterchangeProducer serviceJourneyInterchangeProducer = new ServiceJourneyInterchangeProducer();
	private static BrandingProducer brandingProducer = new BrandingProducer();

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
		NetexprofileExportParameters configuration = (NetexprofileExportParameters) context.get(CONFIGURATION);

		mobi.chouette.model.Line neptuneLine = exportableData.getLine();

		AvailabilityCondition availabilityCondition = createAvailabilityCondition(context);
		exportableNetexData.setLineCondition(availabilityCondition);

		org.rutebanken.netex.model.Line_VersionStructure netexLine = lineProducer.produce(context, neptuneLine);
		exportableNetexData.setLine(netexLine);

		for (mobi.chouette.model.Route neptuneRoute : exportableData.getRoutes()) {
			org.rutebanken.netex.model.Route netexRoute = routeProducer.produce(context, neptuneRoute);
			exportableNetexData.getRoutes().add(netexRoute);
		}

		for (JourneyPattern neptuneJourneyPattern : exportableData.getJourneyPatterns()) {
			org.rutebanken.netex.model.JourneyPattern netexJourneyPattern = journeyPatternProducer.produce(context, neptuneJourneyPattern);
			exportableNetexData.getJourneyPatterns().add(netexJourneyPattern);
		}

		produceAndCollectRoutePoints(context, exportableData.getRoutes(), exportableNetexData);
		produceAndCollectScheduledStopPoints(exportableData.getRoutes(), exportableNetexData);
		produceAndCollectStopAssignments(exportableData.getRoutes(), exportableNetexData, configuration);

		calendarProducer.produce(context, exportableData, exportableNetexData);

		for (mobi.chouette.model.VehicleJourney vehicleJourney : exportableData.getVehicleJourneys()) {
			ServiceJourney serviceJourney = serviceJourneyProducer.produce(context, vehicleJourney, exportableData.getLine());
			exportableNetexData.getServiceJourneys().add(serviceJourney);
		}

		for (DatedServiceJourney datedServiceJourney : exportableData.getDatedServiceJourneys()) {
			 org.rutebanken.netex.model.DatedServiceJourney netexDatedServiceJourney = datedServiceJourneyProducer.produce(context, datedServiceJourney, exportableData.getLine());
			exportableNetexData.getDatedServiceJourneys().add(netexDatedServiceJourney);
		}

		for (Interchange interchange : exportableData.getInterchanges()) {
			exportableNetexData.getServiceJourneyInterchanges().add(serviceJourneyInterchangeProducer.produce(context, interchange));
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

				Branding branding = company.getBranding();
				if (branding != null && !exportableNetexData.getSharedBrandings().containsKey(branding.getObjectId())) {
					brandingProducer.addBranding(exportableNetexData, branding);
				}
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
		List<Route> activeRoutes = exportableData.getVehicleJourneys().stream().map(vj -> vj.getRoute()).distinct().collect(Collectors.toList());
		produceAndCollectDestinationDisplays(activeRoutes, exportableNetexData);
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

	private void produceAndCollectRoutePoints(Context context, List<mobi.chouette.model.Route> routes, ExportableNetexData exportableNetexData) {
		for (mobi.chouette.model.Route route : routes) {
			for (mobi.chouette.model.RoutePoint neptuneRoutePoint : route.getRoutePoints()) {
				if (neptuneRoutePoint != null) {
					if (isSet(neptuneRoutePoint.getScheduledStopPoint().getContainedInStopAreaRef().getObject())) {

						if (!exportableNetexData.getSharedRoutePoints().containsKey(neptuneRoutePoint.getObjectId())) {
							RoutePoint routePoint = createRoutePoint(context, neptuneRoutePoint);
							exportableNetexData.getSharedRoutePoints().put(routePoint.getId(), routePoint);
						}
					} else {
						throw new RuntimeException(
								"RoutePoint with id : " + neptuneRoutePoint.getObjectId() + " is not contained in a StopArea. Cannot produce RoutePoint.");
					}
				}
			}
		}
	}

	private RoutePoint createRoutePoint(Context context, mobi.chouette.model.RoutePoint neptuneRoutePoint) {
		String pointVersion = neptuneRoutePoint.getObjectVersion() > 0 ? String.valueOf(neptuneRoutePoint.getObjectVersion()) : NETEX_DEFAULT_OBJECT_VERSION;

		RoutePoint routePoint = netexFactory.createRoutePoint().withVersion(pointVersion).withId(neptuneRoutePoint.getObjectId());

		if (isSet(neptuneRoutePoint.getName())) {
			routePoint.setName(ConversionUtil.getMultiLingualString(neptuneRoutePoint.getName()));
		}
		routePoint.setBorderCrossing(neptuneRoutePoint.getBoarderCrossing());

		mobi.chouette.model.ScheduledStopPoint scheduledStopPoint = neptuneRoutePoint.getScheduledStopPoint();
		String containedInSuffix = scheduledStopPoint.objectIdSuffix();
		String stopPointIdRef = netexId(scheduledStopPoint.objectIdPrefix(), SCHEDULED_STOP_POINT, containedInSuffix);

		String pointProjectionId = createUniqueId(context, POINT_PROJECTION);

		String sspVersion = scheduledStopPoint.getObjectVersion() > 0 ? String.valueOf(scheduledStopPoint.getObjectVersion()) : NETEX_DEFAULT_OBJECT_VERSION;

		PointRefStructure pointRefStruct = netexFactory.createPointRefStructure().withRef(stopPointIdRef).withVersion(sspVersion);

		PointProjection pointProjection = netexFactory.createPointProjection().withVersion(pointVersion).withId(pointProjectionId)
				.withProjectedPointRef(pointRefStruct);

		Projections_RelStructure projections = netexFactory.createProjections_RelStructure()
				.withProjectionRefOrProjection(netexFactory.createPointProjection(pointProjection));
		routePoint.setProjections(projections);

		return routePoint;
	}

	private void produceAndCollectScheduledStopPoints(List<mobi.chouette.model.Route> routes, ExportableNetexData exportableNetexData) {
		for (mobi.chouette.model.Route route : routes) {
			for (JourneyPattern journeyPattern : route.getJourneyPatterns()) {
				for (StopPoint stopPoint : journeyPattern.getStopPoints()) {
					if (stopPoint != null) {
						collectScheduledStopPoint(stopPoint.getScheduledStopPoint(), exportableNetexData);
					}
				}
			}
			for (mobi.chouette.model.RoutePoint routePoint : route.getRoutePoints()) {
				collectScheduledStopPoint(routePoint.getScheduledStopPoint(), exportableNetexData);
			}
		}
	}

	public void collectScheduledStopPoint(mobi.chouette.model.ScheduledStopPoint chouetteScheduledStopPoint, ExportableNetexData exportableNetexData) {
		if (chouetteScheduledStopPoint != null) {
			if (isSet(chouetteScheduledStopPoint.getContainedInStopAreaRef().getObject())) {

				String scheduledStopPointId = chouetteScheduledStopPoint.getObjectId();

				if (!exportableNetexData.getSharedScheduledStopPoints().containsKey(scheduledStopPointId)) {
					ScheduledStopPoint scheduledStopPoint = netexFactory.createScheduledStopPoint();
					NetexProducerUtils.populateId(chouetteScheduledStopPoint, scheduledStopPoint);
					scheduledStopPoint.setName(ConversionUtil.getMultiLingualString(chouetteScheduledStopPoint.getName()));
					exportableNetexData.getSharedScheduledStopPoints().put(scheduledStopPointId, scheduledStopPoint);
				}
			} else {
				throw new RuntimeException(
						"ScheduledStopPoint with id : " + chouetteScheduledStopPoint.getObjectId() + " is not contained in a StopArea. Cannot produce ScheduledStopPoint.");
			}
		}
	}

	private void produceAndCollectDestinationDisplays(List<mobi.chouette.model.Route> routes, ExportableNetexData exportableNetexData) {
		for (mobi.chouette.model.Route route : routes) {
			for (JourneyPattern journeyPattern : route.getJourneyPatterns()) {
				for (StopPoint stopPoint : journeyPattern.getStopPoints()) {
					if (stopPoint != null) {
						mobi.chouette.model.DestinationDisplay dd = stopPoint.getDestinationDisplay();
						if (dd != null) {
							addDestinationDisplay(dd, exportableNetexData);
						}
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
		for (mobi.chouette.model.Route route : routes) {
			for (JourneyPattern journeyPattern : route.getJourneyPatterns()) {
				for (StopPoint stopPoint : journeyPattern.getStopPoints()) {
					if (stopPoint != null) {
						collectStopAssignment(exportableNetexData, parameters, stopPoint.getScheduledStopPoint());
					}
				}
			}
			for (mobi.chouette.model.RoutePoint routePoint: route.getRoutePoints()){
				if (routePoint.getScheduledStopPoint()!=null) {
					collectStopAssignment(exportableNetexData, parameters, routePoint.getScheduledStopPoint());
				}
			}
		}
	}

	private void collectStopAssignment(ExportableNetexData exportableNetexData, NetexprofileExportParameters parameters, mobi.chouette.model.ScheduledStopPoint scheduledStopPoint) {
		if (isSet(scheduledStopPoint)) {

			String stopAssignmentIdSuffix = scheduledStopPoint.objectIdSuffix();
			String stopAssignmentId = netexId(scheduledStopPoint.objectIdPrefix(), PASSENGER_STOP_ASSIGNMENT, stopAssignmentIdSuffix);

			if (!exportableNetexData.getSharedStopAssignments().containsKey(stopAssignmentId)) {
				PassengerStopAssignment stopAssignment = createStopAssignment(scheduledStopPoint, stopAssignmentId, exportableNetexData.getSharedStopAssignments().size() + 1, parameters);
				exportableNetexData.getSharedStopAssignments().put(stopAssignmentId, stopAssignment);
			}
		} else {
			throw new RuntimeException(
					"ScheduledStopPoint with id : " + scheduledStopPoint.getObjectId() + " is not contained in a StopArea. Cannot produce StopAssignment.");
		}
	}

	private PassengerStopAssignment createStopAssignment(mobi.chouette.model.ScheduledStopPoint scheduledStopPoint, String stopAssignmentId, int order, NetexprofileExportParameters parameters) {
		String pointVersion = scheduledStopPoint.getObjectVersion() > 0 ? String.valueOf(scheduledStopPoint.getObjectVersion()) : NETEX_DEFAULT_OBJECT_VERSION;

		PassengerStopAssignment stopAssignment = netexFactory.createPassengerStopAssignment().withVersion(pointVersion).withId(stopAssignmentId)
				.withOrder(BigInteger.valueOf(order));

		ScheduledStopPointRefStructure scheduledStopPointRef = netexFactory.createScheduledStopPointRefStructure();
		NetexProducerUtils.populateReference(scheduledStopPoint, scheduledStopPointRef, true);
		stopAssignment.setScheduledStopPointRef(netexFactory.createScheduledStopPointRef(scheduledStopPointRef));

		if (isSet(scheduledStopPoint.getContainedInStopAreaRef().getObject())) {
			mobi.chouette.model.StopArea containedInStopArea = scheduledStopPoint.getContainedInStopAreaRef().getObject();
			QuayRefStructure quayRefStruct = netexFactory.createQuayRefStructure();
			NetexProducerUtils.populateReference(containedInStopArea, quayRefStruct, parameters.isExportStops());
			stopAssignment.setQuayRef(quayRefStruct);
		}

		return stopAssignment;
	}

}
