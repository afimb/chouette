package mobi.chouette.exchange.kml.exporter;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.naming.InitialContext;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Color;
import mobi.chouette.common.Constant;
import mobi.chouette.common.Context;
import mobi.chouette.common.JobData;
import mobi.chouette.common.chain.Command;
import mobi.chouette.common.chain.CommandFactory;
import mobi.chouette.exchange.metadata.Metadata;
import mobi.chouette.exchange.report.ActionReport;
import mobi.chouette.exchange.report.FileInfo;
import mobi.chouette.exchange.report.FileInfo.FILE_STATE;
import mobi.chouette.exchange.report.DataStats;
import mobi.chouette.model.AccessPoint;
import mobi.chouette.model.ConnectionLink;
import mobi.chouette.model.StopArea;

import com.jamonapi.Monitor;
import com.jamonapi.MonitorFactory;

@Log4j
public class KmlSharedDataProducerCommand implements Command, Constant {
	public static final String COMMAND = "KmlSharedDataProducerCommand";

	private KmlFileWriter writer = new KmlFileWriter();

	@Override
	public boolean execute(Context context) throws Exception {
		boolean result = ERROR;
		Monitor monitor = MonitorFactory.start(COMMAND);
		ActionReport report = (ActionReport) context.get(REPORT);
		try {

			ExportableData collection = (ExportableData) context.get(EXPORTABLE_DATA);
			if (collection == null) {
				return ERROR;
			}

			saveData(context);
			DataStats globalStats = report.getStats();
			globalStats.setConnectionLinkCount(collection.getConnectionLinks().size());
			globalStats.setStopAreaCount(collection.getStopAreas().size());
			globalStats.setAccessPointCount(collection.getAccessPoints().size());
			// globalStats.setTimeTableCount(collection.getTimetables().size());
			result = SUCCESS;
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		} finally {
			log.info(Color.MAGENTA + monitor.stop() + Color.NORMAL);
		}
		return result;
	}

	private void saveData(Context context) throws Exception {

		// save physical stops
		savePhysicalStops(context);

		// save commercial stops
		saveCommercialStops(context);

		// save stop places
		saveStopPlaces(context);

		// save connection links
		saveConnectionLinks(context);

		// save access points
		saveAccessPoints(context);

		// save access links
		saveAccessLinks(context);
	}

	private void savePhysicalStops(Context context) throws Exception {
		ExportableData collection = (ExportableData) context.get(EXPORTABLE_DATA);
		if (collection.getBoardingPositions().isEmpty() && collection.getQuays().isEmpty())
			return;
		Metadata metadata = (Metadata) context.get(METADATA); 
		JobData jobData = (JobData) context.get(JOB_DATA);
		String rootDirectory = jobData.getPathName();
		Path dir = Paths.get(rootDirectory, OUTPUT);
		KmlData data = new KmlData();
		data.setName("Arrêts");
		for (StopArea area : collection.getBoardingPositions()) {
			data.addStopArea(area);
			if (metadata != null && area.hasCoordinates())
				metadata.getSpatialCoverage().update(area.getLongitude().doubleValue(), area.getLatitude().doubleValue());
		}
		for (StopArea area : collection.getQuays()) {
			data.addStopArea(area);
			if (metadata != null && area.hasCoordinates())
				metadata.getSpatialCoverage().update(area.getLongitude().doubleValue(), area.getLatitude().doubleValue());
		}
		String fileName = "stop_areas.kml";
		// TODO add metadata?
		File file = new File(dir.toFile(), fileName);
		writer.writeXmlFile(data, file);
		ActionReport report = (ActionReport) context.get(REPORT);
		FileInfo fileItem = new FileInfo(fileName,FILE_STATE.OK);
		report.getFiles().add(fileItem);

	}

	private void saveCommercialStops(Context context) throws Exception {
		ExportableData collection = (ExportableData) context.get(EXPORTABLE_DATA);
		if (collection.getCommercialStops().isEmpty())
			return;
		Metadata metadata = (Metadata) context.get(METADATA); 
		JobData jobData = (JobData) context.get(JOB_DATA);
		String rootDirectory = jobData.getPathName();
		Path dir = Paths.get(rootDirectory, OUTPUT);
		KmlData data = new KmlData();
		data.setName("Arrêts commerciaux");
		for (StopArea area : collection.getCommercialStops()) {
			data.addStopArea(area);
			if (metadata != null && area.hasCoordinates())
				metadata.getSpatialCoverage().update(area.getLongitude().doubleValue(), area.getLatitude().doubleValue());
		}
		String fileName = "commercial_stop_areas.kml";
		// TODO add metadata?
		File file = new File(dir.toFile(), fileName);
		writer.writeXmlFile(data, file);
		ActionReport report = (ActionReport) context.get(REPORT);
		FileInfo fileItem = new FileInfo(fileName,FILE_STATE.OK);
		report.getFiles().add(fileItem);

	}

	private void saveStopPlaces(Context context) throws Exception {
		ExportableData collection = (ExportableData) context.get(EXPORTABLE_DATA);
		if (collection.getStopPlaces().isEmpty())
			return;
		Metadata metadata = (Metadata) context.get(METADATA); 
		JobData jobData = (JobData) context.get(JOB_DATA);
		String rootDirectory = jobData.getPathName();
		Path dir = Paths.get(rootDirectory, OUTPUT);
		KmlData data = new KmlData();
		data.setName("Pôles d'échange");
		for (StopArea area : collection.getStopPlaces()) {
			if (metadata != null && area.hasCoordinates())
				metadata.getSpatialCoverage().update(area.getLongitude().doubleValue(), area.getLatitude().doubleValue());
			data.addStopArea(area);
		}
		String fileName = "stop_places.kml";
		// TODO add metadata?
		File file = new File(dir.toFile(), fileName);
		writer.writeXmlFile(data, file);
		ActionReport report = (ActionReport) context.get(REPORT);
		FileInfo fileItem = new FileInfo(fileName,FILE_STATE.OK);
		report.getFiles().add(fileItem);

	}

	private void saveConnectionLinks(Context context) throws Exception {
		ExportableData collection = (ExportableData) context.get(EXPORTABLE_DATA);
		if (collection.getConnectionLinks().isEmpty())
			return;
		JobData jobData = (JobData) context.get(JOB_DATA);
		String rootDirectory = jobData.getPathName();
		Path dir = Paths.get(rootDirectory, OUTPUT);
		KmlData data = new KmlData();
		data.setName("Correspondance");
		for (ConnectionLink link : collection.getConnectionLinks()) {
			data.addConnectionLink(link);
		}
		String fileName = "connection_links.kml";
		File file = new File(dir.toFile(), fileName);
		writer.writeXmlFile(data, file);
		ActionReport report = (ActionReport) context.get(REPORT);
		FileInfo fileItem = new FileInfo(fileName,FILE_STATE.OK);
		report.getFiles().add(fileItem);

	}

	private void saveAccessPoints(Context context) throws Exception {
		ExportableData collection = (ExportableData) context.get(EXPORTABLE_DATA);
		if (collection.getAccessPoints().isEmpty())
			return;
		JobData jobData = (JobData) context.get(JOB_DATA);
		String rootDirectory = jobData.getPathName();
		Path dir = Paths.get(rootDirectory, OUTPUT);
		KmlData data = new KmlData();
		data.setName("Accès");
		for (AccessPoint point : collection.getAccessPoints()) {
			data.addAccessPoint(point);
		}
		String fileName = "access_points.kml";
		// TODO add metadata?
		File file = new File(dir.toFile(), fileName);
		writer.writeXmlFile(data, file);
		ActionReport report = (ActionReport) context.get(REPORT);
		FileInfo fileItem = new FileInfo(fileName,FILE_STATE.OK);
		report.getFiles().add(fileItem);

	}

	private void saveAccessLinks(Context context) throws Exception {

	}

	public static class DefaultCommandFactory extends CommandFactory {

		@Override
		protected Command create(InitialContext context) throws IOException {
			Command result = new KmlSharedDataProducerCommand();
			return result;
		}
	}

	static {
		CommandFactory.factories.put(KmlSharedDataProducerCommand.class.getName(), new DefaultCommandFactory());
	}

}
