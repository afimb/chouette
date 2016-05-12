package mobi.chouette.exchange.kml.exporter;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Date;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.naming.InitialContext;
import javax.xml.datatype.DatatypeConfigurationException;

import lombok.Data;
import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Color;
import mobi.chouette.common.Constant;
import mobi.chouette.common.Context;
import mobi.chouette.common.JobData;
import mobi.chouette.common.chain.Command;
import mobi.chouette.common.chain.CommandFactory;
import mobi.chouette.exchange.kml.exporter.KmlData.KmlItem;
import mobi.chouette.exchange.metadata.Metadata;
import mobi.chouette.exchange.metadata.NeptuneObjectPresenter;
import mobi.chouette.exchange.report.ActionReport;
import mobi.chouette.exchange.report.DataStats;
import mobi.chouette.exchange.report.FileInfo;
import mobi.chouette.exchange.report.FileInfo.FILE_STATE;
import mobi.chouette.exchange.report.LineError;
import mobi.chouette.exchange.report.LineInfo;
import mobi.chouette.model.AccessPoint;
import mobi.chouette.model.ConnectionLink;
import mobi.chouette.model.JourneyPattern;
import mobi.chouette.model.Line;
import mobi.chouette.model.Route;
import mobi.chouette.model.RouteSection;
import mobi.chouette.model.StopArea;
import mobi.chouette.model.StopPoint;

import com.jamonapi.Monitor;
import com.jamonapi.MonitorFactory;

@Log4j
public class KmlLineProducerCommand implements Command, Constant {
	public static final String COMMAND = "KmlLineProducerCommand";

	@Data
	class SharedData {
		KmlData physicalStops = new KmlData("Arrêts");
		KmlData commercialStops = new KmlData("Arrêts commerciaux");
		KmlData stopPlaces = new KmlData("Pôles d'échange");
		KmlData accessPoints = new KmlData("Accès");
		KmlData connectionLinks = new KmlData("Correspondances");
		KmlData accessLinks = new KmlData("Liens d'accès");
	}

	@Override
	public boolean execute(Context context) throws Exception {
		boolean result = ERROR;

		Monitor monitor = MonitorFactory.start(COMMAND);
		ActionReport report = (ActionReport) context.get(REPORT);

		try {

			Line line = (Line) context.get(LINE);

			KmlExportParameters configuration = (KmlExportParameters) context.get(CONFIGURATION);

			ExportableData collection = (ExportableData) context.get(EXPORTABLE_DATA);
			if (collection == null) {
				collection = new ExportableData();
				context.put(EXPORTABLE_DATA, collection);
			} else {
				collection.clear();
			}
			SharedData shared = (SharedData) context.get(SHARED_DATA);
			if (shared == null) {
				shared = new SharedData();
				context.put(SHARED_DATA, shared);
			}
			Date startDate = null;
			if (configuration.getStartDate() != null) {
				startDate = new Date(configuration.getStartDate().getTime());
			}

			Date endDate = null;
			if (configuration.getEndDate() != null) {
				endDate = new Date(configuration.getEndDate().getTime());
			}
			KmlDataCollector collector = new KmlDataCollector();

			boolean cont = (collector.collect(collection, line, startDate, endDate));
			LineInfo lineInfo = new LineInfo(line);
			DataStats stats = lineInfo.getStats();
			stats.setAccessPointCount(collection.getAccessPoints().size());
			stats.setConnectionLinkCount(collection.getConnectionLinks().size());
			stats.setJourneyPatternCount(collection.getJourneyPatterns().size());
			stats.setRouteCount(collection.getRoutes().size());
			stats.setStopAreaCount(collection.getStopAreas().size());
			// stats.setTimeTableCount(collection.getTimetables().size());
			// stats.setVehicleJourneyCount(collection.getVehicleJourneys().size());

			if (cont) {
				// context.put(EXPORTABLE_DATA, collection);

				saveLine(context, line, collection);

				saveSharedData(context, collection, shared);

				// merge lineStats to global ones
				DataStats globalStats = report.getStats();
				globalStats.setLineCount(globalStats.getLineCount() + stats.getLineCount());
				globalStats.setRouteCount(globalStats.getRouteCount() + stats.getRouteCount());
				globalStats.setVehicleJourneyCount(globalStats.getVehicleJourneyCount()
						+ stats.getVehicleJourneyCount());
				globalStats.setJourneyPatternCount(globalStats.getJourneyPatternCount()
						+ stats.getJourneyPatternCount());
				result = SUCCESS;
			} else {
				lineInfo.addError(new LineError(LineError.CODE.NO_DATA_ON_PERIOD, "no data to export on period"));
				result = ERROR;
			}
			report.getLines().add(lineInfo);

		} catch (Exception e) {
			log.error("fail to export line " + e.getClass().getName() + " : " + e.getMessage(), e);
		} finally {
			log.info(Color.MAGENTA + monitor.stop() + Color.NORMAL);
		}

		return result;
	}

	private void saveSharedData(Context context, ExportableData collection, SharedData shared) {
		Metadata metadata = (Metadata) context.get(METADATA);
		for (StopArea area : collection.getBoardingPositions()) {
			shared.getPhysicalStops().addStopArea(area);
			if (metadata != null && area.hasCoordinates())
				metadata.getSpatialCoverage().update(area.getLongitude().doubleValue(),
						area.getLatitude().doubleValue());
		}
		for (StopArea area : collection.getQuays()) {
			shared.getPhysicalStops().addStopArea(area);
			if (metadata != null && area.hasCoordinates())
				metadata.getSpatialCoverage().update(area.getLongitude().doubleValue(),
						area.getLatitude().doubleValue());
		}
		for (StopArea area : collection.getCommercialStops()) {
			shared.getCommercialStops().addStopArea(area);
			if (metadata != null && area.hasCoordinates())
				metadata.getSpatialCoverage().update(area.getLongitude().doubleValue(),
						area.getLatitude().doubleValue());
		}
		for (StopArea area : collection.getStopPlaces()) {
			if (metadata != null && area.hasCoordinates())
				metadata.getSpatialCoverage().update(area.getLongitude().doubleValue(),
						area.getLatitude().doubleValue());
			shared.getStopPlaces().addStopArea(area);
		}
		for (ConnectionLink link : collection.getConnectionLinks()) {
			shared.getConnectionLinks().addConnectionLink(link);
		}
		for (AccessPoint point : collection.getAccessPoints()) {
			shared.getAccessPoints().addAccessPoint(point);
		}
	}

	private boolean isEmpty(String value) {
		return value == null || value.trim().isEmpty();
	}

	private void saveLine(Context context, Line line, ExportableData collection) throws IOException,
			DatatypeConfigurationException {
		ActionReport report = (ActionReport) context.get(REPORT);
		JobData jobData = (JobData) context.get(JOB_DATA);
		String rootDirectory = jobData.getPathName();
		Path dir = Paths.get(rootDirectory, OUTPUT);
		KmlFileWriter writer = new KmlFileWriter();
		// prepare data for line
		KmlData lineData = new KmlData("ligne : " + line.getName());
		KmlItem lineItem = lineData.addNewItem(line.getObjectId());
		lineItem.addAttribute("name", line.getName());
		lineItem.addExtraData("transport_mode", line.getTransportModeName());
		lineItem.addExtraData("objectid", line.getObjectId());
		lineItem.addExtraData("object_version", line.getObjectVersion());
		lineItem.addExtraData("creation_time", line.getCreationTime());
		lineItem.addExtraData("creator_id", line.getCreatorId());
		lineItem.addExtraData("number", line.getNumber());
		lineItem.addExtraData("published_name", line.getPublishedName());
		lineItem.addExtraData("registration_number", line.getRegistrationNumber());
		lineItem.addExtraData("color", line.getColor());
		lineItem.addExtraData("text_color", line.getTextColor());
		lineItem.addExtraData("mobility_restricted_suitability", line.getMobilityRestrictedSuitable());
		lineItem.addExtraData("company_objectid", line.getCompany().getObjectId());
		lineItem.addExtraData("network_objectid", line.getNetwork().getObjectId());

		Set<String> linksKey = new HashSet<>();
		for (Route route : collection.getRoutes()) {
			KmlData routeData = new KmlData("séquence d'arrêts : " + route.getName());
			KmlItem routeItem = routeData.addNewItem(route.getObjectId());
			if (!isEmpty(route.getName()))
				routeItem.addAttribute("name", route.getName());
			routeItem.addExtraData("wayback_code", route.getWayBack());
			routeItem.addExtraData("objectid", route.getObjectId());
			routeItem.addExtraData("object_version", route.getObjectVersion());
			routeItem.addExtraData("creation_time", route.getCreationTime());
			routeItem.addExtraData("creator_id", route.getCreatorId());
			routeItem.addExtraData("published_name", route.getPublishedName());
			routeItem.addExtraData("number", route.getNumber());
			routeItem.addExtraData("direction", route.getDirection());
			routeItem.addExtraData("line_objectid", line.getObjectId());

			StopArea previous = null;
			for (StopPoint point : route.getStopPoints()) {
				if (point == null)
					continue;
				// skip non localized stops
				if (!point.getContainedInStopArea().hasCoordinates())
					continue;
				StopArea current = point.getContainedInStopArea();
				routeItem.addPoint(current);
				if (previous != null) {

					String key1 = previous.getObjectId() + "-" + current.getObjectId();
					String key2 = current.getObjectId() + "-" + previous.getObjectId();
					if (!linksKey.contains(key1)) {
						// add link
						lineItem.addLineString(previous, current);
						linksKey.add(key1);
						linksKey.add(key2);
					}
				}
				previous = current;
			}

			for (JourneyPattern jp : route.getJourneyPatterns()) {
				if (collection.getJourneyPatterns().contains(jp)) {
					KmlData jpData = new KmlData("mission : " + jp.getName());
					KmlItem jpItem = jpData.addNewItem(jp.getObjectId());
					if (!isEmpty(jp.getName()))
						jpItem.addAttribute("name", jp.getName());
					jpItem.addExtraData("object_version", jp.getObjectVersion());
					jpItem.addExtraData("creation_time", jp.getCreationTime());
					jpItem.addExtraData("creator_id", jp.getCreatorId());
					jpItem.addExtraData("registration_number", jp.getRegistrationNumber());
					jpItem.addExtraData("published_name", jp.getPublishedName());
					jpItem.addExtraData("route_objectid", route.getObjectId());
					List<RouteSection> routeSections = jp.getRouteSections();

					boolean sections = false;
					if (routeSections != null && !routeSections.isEmpty()) {
						// add filter to remove null entries
						List<RouteSection> filteredRouteSection = new ArrayList<>();
						for (RouteSection routeSection : routeSections) {
							if (routeSection != null)
								filteredRouteSection.add(routeSection);
						}
						if (!filteredRouteSection.isEmpty()) {
							for (RouteSection routeSection : filteredRouteSection) {
								com.vividsolutions.jts.geom.LineString geometry = (routeSection.getInputGeometry() != null) ? routeSection
										.getInputGeometry() : routeSection.getProcessedGeometry();
								if (geometry != null) {
									jpItem.addLineString(geometry);
									sections = true;
								}
							}
						}
					}

					for (StopPoint point : route.getStopPoints()) {
						if (point == null)
							continue;
						if (point.getContainedInStopArea().hasCoordinates()) {
							KmlItem pointItem = jpData.addStopPoint(point);
							pointItem.addExtraData("stop", Boolean.valueOf(jp.getStopPoints().contains(point)));
							if (!sections)
								jpItem.addPoint(point.getContainedInStopArea());
						}
					}
					// save jp
					String fileName = "line_" + line.getId() + "_route_" + route.getId() + "_journey_pattern_"
							+ jp.getId() + ".kml";
					File file = new File(dir.toFile(), fileName);
					writer.writeXmlFile(jpData, file);
					FileInfo fileItem = new FileInfo(fileName, FILE_STATE.OK);
					report.getFiles().add(fileItem);
				}

			}

			// save route
			String fileName = "line_" + line.getId() + "_route_" + route.getId() + ".kml";
			File file = new File(dir.toFile(), fileName);
			writer.writeXmlFile(routeData, file);
			FileInfo fileItem = new FileInfo(fileName, FILE_STATE.OK);
			report.getFiles().add(fileItem);

		}

		String fileName = "line_" + line.getId() + ".kml";
		File file = new File(dir.toFile(), fileName);
		writer.writeXmlFile(lineData, file);
		FileInfo fileItem = new FileInfo(fileName, FILE_STATE.OK);
		report.getFiles().add(fileItem);

		Metadata metadata = (Metadata) context.get(METADATA);
		if (metadata != null)
			metadata.getResources().add(
					metadata.new Resource(fileName, NeptuneObjectPresenter.getName(collection.getLine().getNetwork()),
							NeptuneObjectPresenter.getName(collection.getLine())));

	}

	public static class DefaultCommandFactory extends CommandFactory {

		@Override
		protected Command create(InitialContext context) throws IOException {
			Command result = new KmlLineProducerCommand();
			return result;
		}
	}

	static {
		CommandFactory.factories.put(KmlLineProducerCommand.class.getName(), new DefaultCommandFactory());
	}

}
