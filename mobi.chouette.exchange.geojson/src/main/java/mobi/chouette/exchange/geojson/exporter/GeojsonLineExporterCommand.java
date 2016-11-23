package mobi.chouette.exchange.geojson.exporter;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.naming.InitialContext;

import lombok.Data;
import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Color;
import mobi.chouette.common.Constant;
import mobi.chouette.common.Context;
import mobi.chouette.common.JobData;
import mobi.chouette.common.chain.Command;
import mobi.chouette.common.chain.CommandFactory;
import mobi.chouette.exchange.ChouetteIdGenerator;
import mobi.chouette.exchange.ChouetteIdGeneratorFactory;
import mobi.chouette.exchange.geojson.Feature;
import mobi.chouette.exchange.geojson.FeatureCollection;
import mobi.chouette.exchange.geojson.JAXBSerializer;
import mobi.chouette.exchange.geojson.LineString;
import mobi.chouette.exchange.geojson.MultiLineString;
import mobi.chouette.exchange.geojson.Point;
import mobi.chouette.exchange.parameters.AbstractParameter;
import mobi.chouette.exchange.report.ActionReporter;
import mobi.chouette.exchange.report.IO_TYPE;
import mobi.chouette.exchange.report.ActionReporter.OBJECT_STATE;
import mobi.chouette.exchange.report.ActionReporter.OBJECT_TYPE;
import mobi.chouette.model.AccessLink;
import mobi.chouette.model.AccessPoint;
import mobi.chouette.model.ConnectionLink;
import mobi.chouette.model.JourneyPattern;
import mobi.chouette.model.Line;
import mobi.chouette.model.Route;
import mobi.chouette.model.RouteSection;
import mobi.chouette.model.StopArea;
import mobi.chouette.model.util.NamingUtil;

import com.jamonapi.Monitor;
import com.jamonapi.MonitorFactory;
import com.vividsolutions.jts.geom.Coordinate;

@Log4j
public class GeojsonLineExporterCommand implements Command, Constant {

	public static final String COMMAND = "GeojsonLineExporterCommand";

	@Data
	class SharedData {
		Map<String, Feature> physicalStops = new HashMap<String, Feature>();
		Map<String, Feature> commercialStops = new HashMap<String, Feature>();
		Map<String, Feature> accessPoints = new HashMap<String, Feature>();
		Map<String, Feature> connectionLinks = new HashMap<String, Feature>();
		Map<String, Feature> accessLinks = new HashMap<String, Feature>();
	}

	@Data
	class Keys {
		Set<String> stopArea = new HashSet<String>();
		Set<String> accessPoints = new HashSet<String>();
		Set<String> connectionLinks = new HashSet<String>();
	}

	@Override
	public boolean execute(Context context) throws Exception {
		boolean result = ERROR;

		Monitor monitor = MonitorFactory.start(COMMAND);
		ActionReporter reporter = ActionReporter.Factory.getInstance();
		
		ChouetteIdGenerator chouetteIdGenerator = (ChouetteIdGenerator) context.get(CHOUETTEID_GENERATOR);
		GeojsonExportParameters parameters = (GeojsonExportParameters) context.get(PARAMETERS_FILE);
		
		ChouetteIdGeneratorFactory.create(parameters.getDefaultFormat());
		

		try {
			Line line = (Line) context.get(LINE);
			if (line == null) {
				return result;
			}

			// log.info("[DSU] processing  : " + line.getChouetteId().getObjectId());

			SharedData shared = (SharedData) context.get(SHARED_DATA);
			if (shared == null) {
				shared = new SharedData();
				context.put(SHARED_DATA, shared);
			}

			// line stats
			Keys keys = new Keys();
			reporter.addObjectReport(context, chouetteIdGenerator.toSpecificFormatId(line.getChouetteId(), parameters.getDefaultCodespace(), line), OBJECT_TYPE.LINE, NamingUtil.getName(line),
					OBJECT_STATE.OK, IO_TYPE.OUTPUT);

			// create route section feature
			List<Feature> features = new ArrayList<Feature>();
			List<Route> routes = line.getRoutes();
			int routeCount = 0;
			int journeyPatternCount = 0;
			for (Route route : routes) {

				// log.info("[DSU] processing  : " + route.getChouetteId().getObjectId());

				routeCount++;

				List<JourneyPattern> journeyPatterns = route.getJourneyPatterns();
				for (JourneyPattern journeyPattern : journeyPatterns) {
					String id = chouetteIdGenerator.toSpecificFormatId(journeyPattern.getChouetteId(), parameters.getDefaultCodespace(), journeyPattern);

					// log.info("[DSU] processing  : "
					// + journeyPattern.getChouetteId().getObjectId());

					journeyPatternCount++;

					Map<String, Object> properties = new HashMap<String, Object>();

					// line
					properties.put("line_objectid", getProperty(chouetteIdGenerator.toSpecificFormatId(line.getChouetteId(), parameters.getDefaultCodespace(), line)));
					properties.put("line_name", getProperty(line.getName()));
					properties.put("line_number", getProperty(line.getNumber()));
					properties.put("line_published_name", getProperty(line.getPublishedName()));
					properties.put("company_objectid", getProperty(chouetteIdGenerator.toSpecificFormatId(line.getCompany().getChouetteId(), parameters.getDefaultCodespace(), line.getCompany())));
					properties.put("network_objectid", getProperty(chouetteIdGenerator.toSpecificFormatId(line.getNetwork().getChouetteId(), parameters.getDefaultCodespace(), line.getNetwork())));
					properties.put("transport_mode", getProperty(line.getTransportModeName()));
					properties.put("color", getProperty(line.getColor()));
					properties.put("text_color", getProperty(line.getTextColor()));

					// route
					properties.put("route_wayback_code", getProperty(route.getWayBack()));
					properties.put("route_objectid", getProperty(chouetteIdGenerator.toSpecificFormatId(route.getChouetteId(), parameters.getDefaultCodespace(), route)));
					properties.put("route_name", getProperty(route.getName()));
					properties.put("route_published_name", getProperty(route.getPublishedName()));
					properties.put("route_number", getProperty(route.getNumber()));
					properties.put("route_direction", getProperty(route.getDirection()));

					// journey pattern
					properties.put("object_version", getProperty(journeyPattern.getObjectVersion()));
					properties.put("creation_time", getProperty(journeyPattern.getCreationTime()));
					properties.put("creator_id", getProperty(journeyPattern.getCreatorId()));
					properties.put("name", getProperty(journeyPattern.getName()));
					properties.put("registration_number", getProperty(journeyPattern.getRegistrationNumber()));
					properties.put("published_name", getProperty(journeyPattern.getPublishedName()));

					List<RouteSection> routeSections = journeyPattern.getRouteSections();
					double[][][] coordinates = new double[0][0][2];

					if (routeSections != null && !routeSections.isEmpty()) {

						// add filter to remove null entries
						List<RouteSection> filteredRouteSection = new ArrayList<>();
						for (RouteSection routeSection : routeSections) {
							if (routeSection != null)
								filteredRouteSection.add(routeSection);
						}
						if (!filteredRouteSection.isEmpty()) {
							RouteSection[] array = filteredRouteSection.toArray(new RouteSection[filteredRouteSection.size()]);
							coordinates = new double[array.length][][];
							for (int i = 0; i < array.length; i++) {
								RouteSection routeSection = array[i];

								// log.info("[DSU] processing  : "
								// + routeSection.getChouetteId().getObjectId());

								StopArea departure = routeSection.getDeparture();

								if (departure != null && departure.hasCoordinates()) {
									createPhysicaStop(shared, keys, departure);
									MetaData.updateBoundingBox(context, departure.getLongitude().doubleValue(),
											departure.getLatitude().doubleValue());
								}

								StopArea arrival = routeSection.getArrival();
								if (arrival != null && arrival.hasCoordinates()) {
									createPhysicaStop(shared, keys, arrival);
									MetaData.updateBoundingBox(context, arrival.getLongitude().doubleValue(), arrival
											.getLatitude().doubleValue());
								}

								com.vividsolutions.jts.geom.LineString geometry = (routeSection.getInputGeometry() != null) ? routeSection
										.getInputGeometry() : routeSection.getProcessedGeometry();
								if (geometry != null) {
									coordinates[i] = getCoordinates(geometry);
								}
							}
						}
					}
					Feature feature = new Feature(id, new MultiLineString(coordinates), properties);
					features.add(feature);
				}
			}

			// local stats
			reporter.setStatToObjectReport(context, chouetteIdGenerator.toSpecificFormatId(line.getChouetteId(), parameters.getDefaultCodespace(), line), OBJECT_TYPE.LINE, OBJECT_TYPE.LINE, 1);
			reporter.setStatToObjectReport(context,  chouetteIdGenerator.toSpecificFormatId(line.getChouetteId(), parameters.getDefaultCodespace(), line), OBJECT_TYPE.LINE, OBJECT_TYPE.JOURNEY_PATTERN,
					journeyPatternCount);
			reporter.setStatToObjectReport(context,  chouetteIdGenerator.toSpecificFormatId(line.getChouetteId(), parameters.getDefaultCodespace(), line), OBJECT_TYPE.LINE, OBJECT_TYPE.ROUTE, routeCount);
			reporter.setStatToObjectReport(context,  chouetteIdGenerator.toSpecificFormatId(line.getChouetteId(), parameters.getDefaultCodespace(), line), OBJECT_TYPE.LINE, OBJECT_TYPE.CONNECTION_LINK,
					keys.getConnectionLinks().size());
			reporter.setStatToObjectReport(context,  chouetteIdGenerator.toSpecificFormatId(line.getChouetteId(), parameters.getDefaultCodespace(), line), OBJECT_TYPE.LINE, OBJECT_TYPE.ACCESS_POINT,
					keys.getAccessPoints().size());
			reporter.setStatToObjectReport(context,  chouetteIdGenerator.toSpecificFormatId(line.getChouetteId(), parameters.getDefaultCodespace(), line), OBJECT_TYPE.LINE, OBJECT_TYPE.STOP_AREA,
					keys.getStopArea().size());
			
			// global stats
			reporter.addObjectReport(context, "merged", OBJECT_TYPE.CONNECTION_LINK, "connection links",
					OBJECT_STATE.OK, IO_TYPE.OUTPUT);
			reporter.addObjectReport(context, "merged", OBJECT_TYPE.ACCESS_POINT, "access points",
					OBJECT_STATE.OK, IO_TYPE.OUTPUT);
			reporter.addObjectReport(context, "merged", OBJECT_TYPE.STOP_AREA, "stop areas",
					OBJECT_STATE.OK, IO_TYPE.OUTPUT);
			reporter.setStatToObjectReport(context, "merged", OBJECT_TYPE.CONNECTION_LINK, OBJECT_TYPE.CONNECTION_LINK,
					shared.connectionLinks.size());
			reporter.setStatToObjectReport(context, "merged", OBJECT_TYPE.ACCESS_POINT, OBJECT_TYPE.ACCESS_POINT,
					shared.accessPoints.size());
			reporter.setStatToObjectReport(context, "merged", OBJECT_TYPE.STOP_AREA, OBJECT_TYPE.STOP_AREA,
					shared.physicalStops.size()+ shared.commercialStops.size());

			// save feature collection
			FeatureCollection target = new FeatureCollection(features);
			JobData jobData = (JobData) context.get(JOB_DATA);
			Path path = Paths.get(jobData.getPathName(), OUTPUT);
			String filename = "line_" + line.getId() + ".json";
			File file = new File(path.toFile(), filename);
			JAXBSerializer.writeTo(target, file);
			MetaData.addTableOfContentsEntry(context, file, line);
			reporter.addFileReport(context, filename, IO_TYPE.OUTPUT);

			result = SUCCESS;
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		} finally {
			log.info(Color.MAGENTA + monitor.stop() + Color.NORMAL);
		}

		return result;
	}

	private void createPhysicaStop(SharedData shared, Keys keys, StopArea stopArea) {
		
		Map<String, Feature> filter = shared.getPhysicalStops();
		if (!filter.containsKey(stopArea.getChouetteId().getObjectId())) {
			Feature feature = createFeature(stopArea);
			filter.put(stopArea.getChouetteId().getObjectId(), feature);
		}

		keys.getStopArea().add(stopArea.getChouetteId().getObjectId());

		for (ConnectionLink connectionLink : stopArea.getConnectionStartLinks()) {
			createConnectionLink(shared, keys, connectionLink);
		}

		for (ConnectionLink connectionLink : stopArea.getConnectionEndLinks()) {
			createConnectionLink(shared, keys, connectionLink);
		}

		for (AccessPoint accessPoint : stopArea.getAccessPoints()) {
			createAccessPoint(shared, keys, accessPoint);
		}

		if (stopArea.getParent() != null) {
			createCommercialStop(shared, keys, stopArea.getParent());
		}

	}

	private void createCommercialStop(SharedData shared, Keys keys, StopArea stopArea) {

		Map<String, Feature> filter = shared.getCommercialStops();
		if (!filter.containsKey(stopArea.getChouetteId().getObjectId())) {
			Feature feature = createFeature(stopArea);
			filter.put(stopArea.getChouetteId().getObjectId(), feature);
		}

		keys.getStopArea().add(stopArea.getChouetteId().getObjectId());

		for (ConnectionLink connectionLink : stopArea.getConnectionStartLinks()) {
			createConnectionLink(shared, keys, connectionLink);
		}

		for (ConnectionLink connectionLink : stopArea.getConnectionEndLinks()) {
			createConnectionLink(shared, keys, connectionLink);
		}

		for (AccessPoint accessPoint : stopArea.getAccessPoints()) {
			createAccessPoint(shared, keys, accessPoint);
		}

		if (stopArea.getParent() != null) {
			createCommercialStop(shared, keys, stopArea.getParent());
		}

	}

	private void createAccessPoint(SharedData shared, Keys keys, AccessPoint accessPoint) {

		Map<String, Feature> filter = shared.getAccessPoints();
		if (!filter.containsKey(accessPoint.getChouetteId().getObjectId())) {
			Map<String, Object> properties = new HashMap<String, Object>();
			properties.put("object_version", getProperty(accessPoint.getObjectVersion()));
			properties.put("creation_time", getProperty(accessPoint.getCreationTime()));
			properties.put("creator_id", getProperty(accessPoint.getCreatorId()));
			properties.put("name", getProperty(accessPoint.getName()));
			properties.put("country_code", getProperty(accessPoint.getCountryCode()));
			properties.put("street_name", getProperty(accessPoint.getStreetName()));
			properties.put("access_type", getProperty(accessPoint.getType()));
			properties.put("mobility_restricted_suitability", getProperty(accessPoint.getMobilityRestrictedSuitable()));
			properties.put("stairs_availability", getProperty(accessPoint.getStairsAvailable()));
			properties.put("lift_availability", getProperty(accessPoint.getLiftAvailable()));
			properties.put("stop_area_objectid", getProperty(accessPoint.getContainedIn().getChouetteId().getObjectId()));

			double[] coordinates = new double[2];
			if (accessPoint.getLongitude() != null && accessPoint.getLatitude() != null) {
				coordinates[0] = accessPoint.getLongitude().doubleValue();
				coordinates[1] = accessPoint.getLatitude().doubleValue();
			}

			Feature feature = new Feature(accessPoint.getChouetteId().getObjectId(), new Point(coordinates), properties);
			filter.put(accessPoint.getChouetteId().getObjectId(), feature);
		}

		keys.getAccessPoints().add(accessPoint.getChouetteId().getObjectId());

		for (AccessLink accessLink : accessPoint.getAccessLinks()) {
			createAccessLink(shared, accessLink);
		}

	}

	private void createConnectionLink(SharedData shared, Keys keys, ConnectionLink connectionLink) {

		Map<String, Feature> filter = shared.getConnectionLinks();
		if (!filter.containsKey(connectionLink.getChouetteId().getObjectId())) {
			Map<String, Object> properties = new HashMap<String, Object>();
			properties.put("object_version", getProperty(connectionLink.getObjectVersion()));
			properties.put("creation_time", getProperty(connectionLink.getCreationTime()));
			properties.put("creator_id", getProperty(connectionLink.getCreatorId()));
			properties.put("name", getProperty(connectionLink.getName()));
			properties.put("link_distance", getProperty(connectionLink.getLinkDistance()));
			properties.put("link_distance", getProperty(connectionLink.getLinkType()));
			properties.put("default_duration", getProperty(connectionLink.getDefaultDuration()));
			properties.put("frequent_traveller_duration", getProperty(connectionLink.getFrequentTravellerDuration()));
			properties.put("occasional_traveller_duration",
					getProperty(connectionLink.getOccasionalTravellerDuration()));
			properties.put("mobility_restricted_traveller_duration",
					getProperty(connectionLink.getMobilityRestrictedTravellerDuration()));
			properties.put("mobility_restricted_suitability",
					getProperty(connectionLink.getMobilityRestrictedSuitable()));
			properties.put("stairs_availability", getProperty(connectionLink.getStairsAvailable()));
			properties.put("lift_availability", getProperty(connectionLink.getLiftAvailable()));

			double[][] coordinates = new double[0][2];
			if (connectionLink.getStartOfLink() != null && connectionLink.getEndOfLink() != null) {
				properties.put("departure_objectid", getProperty(connectionLink.getStartOfLink().getChouetteId().getObjectId()));
				properties.put("arrival_objectid", getProperty(connectionLink.getEndOfLink().getChouetteId().getObjectId()));
				coordinates = new double[2][2];
				coordinates[0][0] = connectionLink.getStartOfLink().getLongitude().doubleValue();
				coordinates[0][1] = connectionLink.getStartOfLink().getLatitude().doubleValue();
				coordinates[1][0] = connectionLink.getEndOfLink().getLongitude().doubleValue();
				coordinates[1][1] = connectionLink.getEndOfLink().getLatitude().doubleValue();
			}

			Feature feature = new Feature(connectionLink.getChouetteId().getObjectId(), new LineString(coordinates), properties);
			filter.put(connectionLink.getChouetteId().getObjectId(), feature);
		}

		keys.getConnectionLinks().add(connectionLink.getChouetteId().getObjectId());
	}

	private void createAccessLink(SharedData shared, AccessLink accessLink) {

		Map<String, Feature> filter = shared.getAccessLinks();
		if (!filter.containsKey(accessLink.getChouetteId().getObjectId())) {
			Map<String, Object> properties = new HashMap<String, Object>();
			properties.put("object_version", getProperty(accessLink.getObjectVersion()));
			properties.put("creation_time", getProperty(accessLink.getCreationTime()));
			properties.put("creator_id", getProperty(accessLink.getCreatorId()));
			properties.put("name", getProperty(accessLink.getName()));
			properties.put("link_distance", getProperty(accessLink.getLinkDistance()));
			properties.put("link_type", getProperty(accessLink.getLinkType()));
			properties.put("default_duration", getProperty(accessLink.getDefaultDuration()));
			properties.put("frequent_traveller_duration", getProperty(accessLink.getFrequentTravellerDuration()));
			properties.put("occasional_traveller_duration", getProperty(accessLink.getOccasionalTravellerDuration()));
			properties.put("mobility_restricted_traveller_duration",
					getProperty(accessLink.getMobilityRestrictedTravellerDuration()));
			properties.put("mobility_restricted_suitability", getProperty(accessLink.getMobilityRestrictedSuitable()));
			properties.put("stairs_availability", getProperty(accessLink.getStairsAvailable()));
			properties.put("lift_availability", getProperty(accessLink.getLiftAvailable()));
			properties.put("link_orientation", getProperty(accessLink.getLinkOrientation()));

			double[][] coordinates = new double[0][];
			if (accessLink.getAccessPoint() != null && accessLink.getStopArea() != null) {
				properties.put("access_point_objectid", getProperty(accessLink.getAccessPoint().getChouetteId().getObjectId()));
				properties.put("stop_area_objectid", getProperty(accessLink.getStopArea().getChouetteId().getObjectId()));

				coordinates = new double[2][2];
				coordinates[0][0] = accessLink.getAccessPoint().getLongitude().doubleValue();
				coordinates[0][1] = accessLink.getAccessPoint().getLatitude().doubleValue();
				coordinates[1][0] = accessLink.getStopArea().getLongitude().doubleValue();
				coordinates[1][1] = accessLink.getStopArea().getLatitude().doubleValue();
			}

			Feature feature = new Feature(accessLink.getChouetteId().getObjectId(), new LineString(coordinates), properties);
			filter.put(accessLink.getChouetteId().getObjectId(), feature);
		}
	}

	private Feature createFeature(StopArea stopArea) {
		Map<String, Object> properties = new HashMap<String, Object>();
		properties.put("object_version", getProperty(stopArea.getObjectVersion()));
		properties.put("creation_time", getProperty(stopArea.getCreationTime()));
		properties.put("creator_id", getProperty(stopArea.getCreatorId()));
		properties.put("name", getProperty(stopArea.getName()));
		properties.put("area_type", getProperty(stopArea.getAreaType()));
		properties.put("registration_number", getProperty(stopArea.getRegistrationNumber()));
		properties.put("nearest_topic_name", getProperty(stopArea.getNearestTopicName()));
		properties.put("fare_code", getProperty(stopArea.getFareCode()));
		properties.put("country_code", getProperty(stopArea.getCountryCode()));
		properties.put("street_name", getProperty(stopArea.getStreetName()));
		properties.put("mobility_restricted_suitability", getProperty(stopArea.getMobilityRestrictedSuitable()));
		properties.put("stairs_availability", getProperty(stopArea.getStairsAvailable()));
		properties.put("lift_availability", getProperty(stopArea.getLiftAvailable()));
		if (stopArea.getParent() != null)
			properties.put("parent", getProperty(stopArea.getParent().getChouetteId().getObjectId()));

		double[] coordinates = new double[2];
		coordinates[0] = stopArea.getLongitude().doubleValue();
		coordinates[1] = stopArea.getLatitude().doubleValue();
		Feature feature = new Feature(stopArea.getChouetteId().getObjectId(), new Point(coordinates), properties);
		return feature;
	}

	private double[][] getCoordinates(com.vividsolutions.jts.geom.LineString geometry) {
		Coordinate[] coordinates = geometry.getCoordinates();
		double[][] result = new double[coordinates.length][2];
		for (int i = 0; i < coordinates.length; i++) {
			result[i][0] = coordinates[i].x;
			result[i][1] = coordinates[i].y;
		}
		return result;
	}

	private static String getProperty(Object object) {
		return object == null ? "" : object.toString().trim();
	}

	public static class DefaultCommandFactory extends CommandFactory {

		@Override
		protected Command create(InitialContext context) throws IOException {
			Command result = new GeojsonLineExporterCommand();
			return result;
		}
	}

	static {
		CommandFactory.factories.put(GeojsonLineExporterCommand.class.getName(), new DefaultCommandFactory());
	}
}
