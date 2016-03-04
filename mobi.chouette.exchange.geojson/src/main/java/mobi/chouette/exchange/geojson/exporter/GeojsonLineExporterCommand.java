package mobi.chouette.exchange.geojson.exporter;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.naming.InitialContext;

import lombok.Data;
import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Color;
import mobi.chouette.common.Constant;
import mobi.chouette.common.Context;
import mobi.chouette.common.JobData;
import mobi.chouette.common.chain.Command;
import mobi.chouette.common.chain.CommandFactory;
import mobi.chouette.exchange.geojson.Feature;
import mobi.chouette.exchange.geojson.FeatureCollection;
import mobi.chouette.exchange.geojson.JAXBSerializer;
import mobi.chouette.exchange.geojson.LineString;
import mobi.chouette.exchange.geojson.MultiLineString;
import mobi.chouette.exchange.geojson.Point;
import mobi.chouette.exchange.report.ActionReport;
import mobi.chouette.exchange.report.DataStats;
import mobi.chouette.exchange.report.FileInfo.FILE_STATE;
import mobi.chouette.exchange.report.LineInfo.LINE_STATE;
import mobi.chouette.model.AccessLink;
import mobi.chouette.model.AccessPoint;
import mobi.chouette.model.ConnectionLink;
import mobi.chouette.model.JourneyPattern;
import mobi.chouette.model.Line;
import mobi.chouette.model.Route;
import mobi.chouette.model.RouteSection;
import mobi.chouette.model.StopArea;

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

	@Override
	public boolean execute(Context context) throws Exception {
		boolean result = ERROR;

		Monitor monitor = MonitorFactory.start(COMMAND);

		try {
			Line line = (Line) context.get(LINE);
			if (line == null) {
				return result;
			}

			SharedData shared = (SharedData) context.get(SHARED_DATA);
			if (shared == null) {
				shared = new SharedData();
				context.put(SHARED_DATA, shared);
			}

			// line stats
			DataStats stats = new DataStats();
			Report.addLineInfo(context, line, stats);
			Report.addLineInfo(context, line, LINE_STATE.OK);

			// create route section feature
			List<Feature> features = new ArrayList<Feature>();
			List<Route> routes = line.getRoutes();
			for (Route route : routes) {

				stats.routeCount++;

				List<JourneyPattern> journeyPatterns = route
						.getJourneyPatterns();
				for (JourneyPattern journeyPattern : journeyPatterns) {
					String id = journeyPattern.getObjectId();

					stats.journeyPatternCount++;

					Map<String, Object> properties = new HashMap<String, Object>();

					// line
					properties.put("line_objectid", line.getObjectId());
					properties.put("line_name", line.getName());
					properties.put("line_number", line.getNumber());
					properties.put("line_published_name",
							line.getPublishedName());
					properties.put("company_objectid", line.getCompany()
							.getObjectId());
					properties.put("network_objectid", line.getNetwork()
							.getObjectId());
					properties.put("transport_mode",
							line.getTransportModeName());

					// route
					properties.put("route_wayback_code", route.getWayBack());
					properties.put("route_objectid", route.getObjectId());
					properties.put("route_name", route.getName());
					properties.put("route_published_name",
							route.getPublishedName());
					properties.put("route_number", route.getNumber());
					properties.put("route_direction", route.getDirection());

					// journey pattern
					properties.put("object_version",
							journeyPattern.getObjectVersion());
					properties.put("creation_time",
							journeyPattern.getCreationTime());
					properties.put("creator_id", journeyPattern.getCreatorId());
					properties.put("name", journeyPattern.getName());
					properties.put("registration_number",
							journeyPattern.getRegistrationNumber());
					properties.put("published_name",
							journeyPattern.getPublishedName());

					List<RouteSection> routeSections = journeyPattern
							.getRouteSections();
					double[][][] coordinates = new double[0][0][2];

					if (routeSections != null && !routeSections.isEmpty()) {

						RouteSection[] array = routeSections
								.toArray(new RouteSection[routeSections.size()]);
						coordinates = new double[array.length][][];
						for (int i = 0; i < array.length; i++) {
							RouteSection routeSection = array[i];

							StopArea departure = routeSection.getDeparture();

							if (departure != null && departure.hasCoordinates()) {
								createPhysicaStop(shared, stats, departure);
								MetaData.updateBoundingBox(context, departure
										.getLongitude().doubleValue(),
										departure.getLatitude().doubleValue());
							}

							StopArea arrival = routeSection.getArrival();
							if (arrival != null && arrival.hasCoordinates()) {
								createPhysicaStop(shared, stats, arrival);
								MetaData.updateBoundingBox(context, arrival
										.getLongitude().doubleValue(), arrival
										.getLatitude().doubleValue());
							}

							com.vividsolutions.jts.geom.LineString geometry = (routeSection
									.getInputGeometry() != null) ? routeSection
									.getInputGeometry() : routeSection
									.getProcessedGeometry();
							if (geometry != null) {
								coordinates[i] = getCoordinates(geometry);
							}
						}
					}
					Feature feature = new Feature(id, new MultiLineString(
							coordinates), properties);
					features.add(feature);
				}
			}

			// global stats
			ActionReport report = (ActionReport) context.get(REPORT);
			DataStats globalStats = report.getStats();
			globalStats.lineCount++;
			globalStats.routeCount += stats.routeCount;
			globalStats.journeyPatternCount += stats.journeyPatternCount;
			globalStats.connectionLinkCount = shared.connectionLinks.size();
			globalStats.accessPointCount = shared.accessPoints.size();
			globalStats.stopAreaCount = shared.physicalStops.size()
					+ shared.commercialStops.size();
			Report.addGlobalStats(context, globalStats);

			// save feature collection
			FeatureCollection target = new FeatureCollection(features);
			JobData jobData = (JobData) context.get(JOB_DATA);
			Path path = Paths.get(jobData.getPathName(), OUTPUT);
			String filename = "line_" + line.getId() + ".json";
			File file = new File(path.toFile(), filename);
			JAXBSerializer.writeTo(target, file);
			MetaData.addTableOfContentsEntry(context, file, line);
			Report.addFileInfo(context, filename, FILE_STATE.OK);

			result = SUCCESS;
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		} finally {
			log.info(Color.MAGENTA + monitor.stop() + Color.NORMAL);
		}

		return result;
	}

	private Feature createPhysicaStop(SharedData shared, DataStats stats,
			StopArea stopArea) {

		Map<String, Feature> filter = shared.getPhysicalStops();
		if (filter.containsKey(stopArea.getObjectId())) {
			return null;
		}

		for (ConnectionLink connectionLink : stopArea.getConnectionStartLinks()) {
			createConnectionLink(shared, stats, connectionLink);
		}

		for (ConnectionLink connectionLink : stopArea.getConnectionEndLinks()) {
			createConnectionLink(shared, stats, connectionLink);
		}

		for (AccessPoint accessPoint : stopArea.getAccessPoints()) {
			createAccessPoint(shared, stats, accessPoint);
		}

		if (stopArea.getParent() != null) {
			createCommercialStop(shared, stats, stopArea.getParent());
		}

		Feature feature = createFeature(stopArea);
		filter.put(stopArea.getObjectId(), feature);
		stats.stopAreaCount++;

		return feature;
	}

	private Feature createCommercialStop(SharedData shared, DataStats stats,
			StopArea stopArea) {

		Map<String, Feature> filter = shared.getCommercialStops();
		if (filter.containsKey(stopArea.getObjectId())) {
			return null;
		}

		for (ConnectionLink connectionLink : stopArea.getConnectionStartLinks()) {
			createConnectionLink(shared, stats, connectionLink);
		}

		for (ConnectionLink connectionLink : stopArea.getConnectionEndLinks()) {
			createConnectionLink(shared, stats, connectionLink);
		}

		for (AccessPoint accessPoint : stopArea.getAccessPoints()) {
			createAccessPoint(shared, stats, accessPoint);
		}

		if (stopArea.getParent() != null) {
			createCommercialStop(shared, stats, stopArea.getParent());
		}

		Feature feature = createFeature(stopArea);
		filter.put(stopArea.getObjectId(), feature);
		stats.stopAreaCount++;

		return feature;
	}

	private Feature createAccessPoint(SharedData shared, DataStats stats,
			AccessPoint accessPoint) {

		Map<String, Feature> filter = shared.getAccessPoints();
		if (filter.containsKey(accessPoint.getObjectId())) {
			return null;
		}

		for (AccessLink accessLink : accessPoint.getAccessLinks()) {
			createAccessLink(shared, stats, accessLink);
		}

		Map<String, Object> properties = new HashMap<String, Object>();
		properties.put("object_version", accessPoint.getObjectVersion());
		properties.put("creation_time", accessPoint.getCreationTime());
		properties.put("creator_id", accessPoint.getCreatorId());
		properties.put("name", accessPoint.getName());
		properties.put("country_code", accessPoint.getCountryCode());
		properties.put("street_name", accessPoint.getStreetName());
		properties.put("access_type", accessPoint.getType());
		properties.put("mobility_restricted_suitability",
				accessPoint.getMobilityRestrictedSuitable());
		properties.put("stairs_availability", accessPoint.getStairsAvailable());
		properties.put("lift_availability", accessPoint.getLiftAvailable());
		properties.put("stop_area_objectid", accessPoint.getContainedIn()
				.getObjectId());

		double[] coordinates = new double[0];
		if (accessPoint.getLongitude() != null
				&& accessPoint.getLatitude() != null) {
			coordinates[0] = accessPoint.getLongitude().doubleValue();
			coordinates[1] = accessPoint.getLatitude().doubleValue();
		}

		Feature feature = new Feature(accessPoint.getObjectId(), new Point(
				coordinates), properties);
		filter.put(accessPoint.getObjectId(), feature);
		stats.accessPointCount++;

		return feature;
	}

	private Feature createConnectionLink(SharedData shared, DataStats stats,
			ConnectionLink connectionLink) {

		Map<String, Feature> filter = shared.getAccessLinks();
		if (filter.containsKey(connectionLink.getObjectId())) {
			return null;
		}

		Map<String, Object> properties = new HashMap<String, Object>();
		properties.put("object_version", connectionLink.getObjectVersion());
		properties.put("creation_time", connectionLink.getCreationTime());
		properties.put("creator_id", connectionLink.getCreatorId());
		properties.put("name", connectionLink.getName());
		properties.put("link_distance", connectionLink.getLinkDistance());
		properties.put("link_type", connectionLink.getLinkType());
		properties.put("default_duration", connectionLink.getDefaultDuration());
		properties.put("frequent_traveller_duration",
				connectionLink.getFrequentTravellerDuration());
		properties.put("occasional_traveller_duration",
				connectionLink.getOccasionalTravellerDuration());
		properties.put("mobility_restricted_traveller_duration",
				connectionLink.getMobilityRestrictedTravellerDuration());
		properties.put("mobility_restricted_suitability",
				connectionLink.getMobilityRestrictedSuitable());
		properties.put("stairs_availability",
				connectionLink.getStairsAvailable());
		properties.put("lift_availability", connectionLink.getLiftAvailable());

		double[][] coordinates = new double[0][2];
		if (connectionLink.getStartOfLink() != null
				&& connectionLink.getEndOfLink() != null) {
			properties.put("departure_objectid", connectionLink
					.getStartOfLink().getObjectId());
			properties.put("arrival_objectid", connectionLink.getEndOfLink()
					.getObjectId());
			coordinates = new double[2][2];
			coordinates[0][0] = connectionLink.getStartOfLink().getLongitude()
					.doubleValue();
			coordinates[0][1] = connectionLink.getStartOfLink().getLatitude()
					.doubleValue();
			coordinates[1][0] = connectionLink.getEndOfLink().getLongitude()
					.doubleValue();
			coordinates[1][1] = connectionLink.getEndOfLink().getLatitude()
					.doubleValue();
		}

		Feature feature = new Feature(connectionLink.getObjectId(),
				new LineString(coordinates), properties);
		filter.put(connectionLink.getObjectId(), feature);
		stats.connectionLinkCount++;

		return feature;
	}

	private Feature createAccessLink(SharedData shared, DataStats stats,
			AccessLink accessLink) {

		Map<String, Feature> filter = shared.getAccessLinks();
		if (filter.containsKey(accessLink.getObjectId())) {
			return null;
		}

		Map<String, Object> properties = new HashMap<String, Object>();
		properties.put("object_version", accessLink.getObjectVersion());
		properties.put("creation_time", accessLink.getCreationTime());
		properties.put("creator_id", accessLink.getCreatorId());
		properties.put("name", accessLink.getName());
		properties.put("link_distance", accessLink.getLinkDistance());
		properties.put("link_type", accessLink.getLinkType());
		properties.put("default_duration", accessLink.getDefaultDuration());
		properties.put("frequent_traveller_duration",
				accessLink.getFrequentTravellerDuration());
		properties.put("occasional_traveller_duration",
				accessLink.getOccasionalTravellerDuration());
		properties.put("mobility_restricted_traveller_duration",
				accessLink.getMobilityRestrictedTravellerDuration());
		properties.put("mobility_restricted_suitability",
				accessLink.getMobilityRestrictedSuitable());
		properties.put("stairs_availability", accessLink.getStairsAvailable());
		properties.put("lift_availability", accessLink.getLiftAvailable());
		properties.put("link_orientation", accessLink.getLinkOrientation());

		double[][] coordinates = new double[0][];
		if (accessLink.getAccessPoint() != null
				&& accessLink.getStopArea() != null) {
			properties.put("access_point_objectid", accessLink.getAccessPoint()
					.getObjectId());
			properties.put("stop_area_objectid", accessLink.getStopArea()
					.getObjectId());

			coordinates = new double[2][2];
			coordinates[0][0] = accessLink.getAccessPoint().getLongitude()
					.doubleValue();
			coordinates[0][1] = accessLink.getAccessPoint().getLatitude()
					.doubleValue();
			coordinates[1][0] = accessLink.getStopArea().getLongitude()
					.doubleValue();
			coordinates[1][1] = accessLink.getStopArea().getLatitude()
					.doubleValue();
		}

		Feature feature = new Feature(accessLink.getObjectId(), new LineString(
				coordinates), properties);
		filter.put(accessLink.getObjectId(), feature);

		return feature;
	}

	private Feature createFeature(StopArea stopArea) {
		Map<String, Object> properties = new HashMap<String, Object>();
		properties.put("object_version", stopArea.getObjectVersion());
		properties.put("creation_time", stopArea.getCreationTime());
		properties.put("creator_id", stopArea.getCreatorId());
		properties.put("name", stopArea.getName());
		properties.put("area_type", stopArea.getAreaType());
		properties.put("registration_number", stopArea.getRegistrationNumber());
		properties.put("nearest_topic_name", stopArea.getNearestTopicName());
		properties.put("fare_code", stopArea.getFareCode());		
		properties.put("country_code", stopArea.getCountryCode());
		properties.put("street_name", stopArea.getStreetName());
		properties.put("mobility_restricted_suitability",
				stopArea.getMobilityRestrictedSuitable());
		properties.put("stairs_availability", stopArea.getStairsAvailable());
		properties.put("lift_availability", stopArea.getLiftAvailable());
		if (stopArea.getParent() != null)
			properties.put("parent", stopArea.getParent().getObjectId());

		double[] coordinates = new double[2];
		coordinates[0] = stopArea.getLongitude().doubleValue();
		coordinates[1] = stopArea.getLatitude().doubleValue();
		Feature feature = new Feature(stopArea.getObjectId(), new Point(
				coordinates), properties);
		return feature;
	}

	private double[][] getCoordinates(
			com.vividsolutions.jts.geom.LineString geometry) {
		Coordinate[] coordinates = geometry.getCoordinates();
		double[][] result = new double[coordinates.length][2];
		for (int i = 0; i < coordinates.length; i++) {
			result[i][0] = coordinates[i].x;
			result[i][1] = coordinates[i].y;
		}
		return result;
	}

	public static class DefaultCommandFactory extends CommandFactory {

		@Override
		protected Command create(InitialContext context) throws IOException {
			Command result = new GeojsonLineExporterCommand();
			return result;
		}
	}

	static {
		CommandFactory.factories.put(
				GeojsonLineExporterCommand.class.getName(),
				new DefaultCommandFactory());
	}
}
