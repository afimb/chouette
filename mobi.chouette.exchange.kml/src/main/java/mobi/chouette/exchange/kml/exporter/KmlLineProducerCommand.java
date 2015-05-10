package mobi.chouette.exchange.kml.exporter;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Date;
import java.util.HashSet;
import java.util.Set;

import javax.naming.InitialContext;
import javax.xml.datatype.DatatypeConfigurationException;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Color;
import mobi.chouette.common.Constant;
import mobi.chouette.common.Context;
import mobi.chouette.common.JobData;
import mobi.chouette.common.chain.Command;
import mobi.chouette.common.chain.CommandFactory;
import mobi.chouette.exchange.kml.exporter.KmlData.KmlItem;
import mobi.chouette.exchange.report.ActionReport;
import mobi.chouette.exchange.report.LineInfo;
import mobi.chouette.exchange.report.LineInfo.LINE_STATE;
import mobi.chouette.exchange.report.LineStats;
import mobi.chouette.model.JourneyPattern;
import mobi.chouette.model.Line;
import mobi.chouette.model.Route;
import mobi.chouette.model.StopArea;
import mobi.chouette.model.StopPoint;

import com.jamonapi.Monitor;
import com.jamonapi.MonitorFactory;

@Log4j
public class KmlLineProducerCommand implements Command, Constant {
	public static final String COMMAND = "KmlLineProducerCommand";

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
			LineInfo lineInfo = new LineInfo();
			lineInfo.setName(line.getName() + " (" + line.getNumber() + ")");
			LineStats stats = new LineStats();
			lineInfo.setStats(stats);
			stats.setAccessPointCount(collection.getAccessPoints().size());
			stats.setConnectionLinkCount(collection.getConnectionLinks().size());
			stats.setJourneyPatternCount(collection.getJourneyPatterns().size());
			stats.setRouteCount(collection.getRoutes().size());
			stats.setStopAreaCount(collection.getStopAreas().size());
			// stats.setTimeTableCount(collection.getTimetables().size());
			// stats.setVehicleJourneyCount(collection.getVehicleJourneys().size());

			if (cont) {
				context.put(EXPORTABLE_DATA, collection);

				saveLine(context, line, collection);

				lineInfo.setStatus(LINE_STATE.OK);
				// merge lineStats to global ones
				LineStats globalStats = report.getStats();
				if (globalStats == null) {
					globalStats = new LineStats();
					report.setStats(globalStats);
				}
				globalStats.setLineCount(globalStats.getLineCount() + stats.getLineCount());
				globalStats.setRouteCount(globalStats.getRouteCount() + stats.getRouteCount());
				globalStats.setVehicleJourneyCount(globalStats.getVehicleJourneyCount()
						+ stats.getVehicleJourneyCount());
				globalStats.setJourneyPatternCount(globalStats.getJourneyPatternCount()
						+ stats.getJourneyPatternCount());
				result = SUCCESS;
			} else {
				lineInfo.setStatus(LINE_STATE.ERROR);
				result = ERROR;
			}
			report.getLines().add(lineInfo);

		} catch (Exception e) {
			log.error(e.getMessage(), e);
		} finally {
			log.info(Color.MAGENTA + monitor.stop() + Color.NORMAL);
		}

		return result;
	}

	private void saveLine(Context context, Line line, ExportableData collection) throws IOException,
			DatatypeConfigurationException {
		JobData jobData = (JobData) context.get(JOB_DATA);
		String rootDirectory = jobData.getPathName();
		Path dir = Paths.get(rootDirectory, OUTPUT);
		KmlFileWriter writer = new KmlFileWriter();
		// prepare data for line
		KmlData lineData = new KmlData();
		lineData.setName("ligne : " + line.getName());
		KmlItem lineItem = lineData.addNewItem();
		lineItem.setId(line.getObjectId());
		lineItem.addAttribute("name", line.getName());
		lineItem.addExtraData("transport_mode", line.getTransportModeName());
		lineItem.addExtraData("objectid", line.getObjectId());
		lineItem.addExtraData("object_version", line.getObjectVersion());
		lineItem.addExtraData("creation_time", line.getCreationTime());
		lineItem.addExtraData("creator_id", line.getCreatorId());
		lineItem.addExtraData("name", line.getName());
		lineItem.addExtraData("number", line.getNumber());
		lineItem.addExtraData("published_name", line.getPublishedName());
		lineItem.addExtraData("registration_number", line.getRegistrationNumber());
		lineItem.addExtraData("comment", line.getComment());
		lineItem.addExtraData("mobility_restricted_suitability", line.getMobilityRestrictedSuitable());
		lineItem.addExtraData("int_user_needs", line.getIntUserNeeds());
		lineItem.addExtraData("company_objectid", line.getCompany().getObjectId());
		lineItem.addExtraData("network_objectid", line.getNetwork().getObjectId());

		Set<String> linksKey = new HashSet<>();
		for (Route route : collection.getRoutes()) {
			KmlData routeData = new KmlData();
			routeData.setName("séquence d'arrêts : " + route.getName());
			KmlItem routeItem = routeData.addNewItem();
			routeItem.setId(route.getObjectId());
			routeItem.addAttribute("direction_code", route.getDirection());
			routeItem.addExtraData("wayback_code", route.getWayBack());
			routeItem.addExtraData("objectid", route.getObjectId());
			routeItem.addExtraData("object_version", route.getObjectVersion());
			routeItem.addExtraData("creation_time", route.getCreationTime());
			routeItem.addExtraData("creator_id", route.getCreatorId());
			routeItem.addExtraData("name", route.getName());
			routeItem.addExtraData("comment", route.getComment());
			routeItem.addExtraData("published_name", route.getPublishedName());
			routeItem.addExtraData("number", route.getNumber());
			routeItem.addExtraData("direction", route.getDirection());
			routeItem.addExtraData("wayback", route.getName());
			routeItem.addExtraData("line_objectid", line.getObjectId());

			StopArea previous = null;
			for (StopPoint point : route.getStopPoints()) {
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
				if (collection.getJourneyPatterns().contains(jp))
				{
				KmlData jpData = new KmlData();
				jpData.setName("mission : " + jp.getName());
				jpData.addExtraData("objectid", jp.getObjectId());
				jpData.addExtraData("object_version", jp.getObjectVersion());
				jpData.addExtraData("creation_time", jp.getCreationTime());
				jpData.addExtraData("creator_id", jp.getCreatorId());
				jpData.addExtraData("name", jp.getName());
				jpData.addExtraData("comment", jp.getComment());
				jpData.addExtraData("registration_number", jp.getRegistrationNumber());
				jpData.addExtraData("published_name", jp.getPublishedName());
				jpData.addExtraData("route_objectid", route.getObjectId());
				for (StopPoint point : route.getStopPoints()) {
					if (point.getContainedInStopArea().hasCoordinates()) {
						KmlItem pointItem = jpData.addStopArea(point.getContainedInStopArea());
						pointItem.addExtraData("stop", Boolean.valueOf(jp.getStopPoints().contains(point)));
					}
				}
				// save jp
				String fileName = "line_" + line.getId() + "_route_" + route.getId() + "_journey_pattern_"+ jp.getId() +".xml";
				File file = new File(dir.toFile(), fileName);
				writer.writeXmlFile(jpData, file);
				}

			}

			// save route
			String fileName = "line_" + line.getId() + "_route_" + route.getId() + ".xml";
			File file = new File(dir.toFile(), fileName);
			writer.writeXmlFile(routeData, file);

		}

		String fileName = "line_" + line.getId() + ".xml";
		// TODO add metadata?
		File file = new File(dir.toFile(), fileName);
		writer.writeXmlFile(lineData, file);

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
