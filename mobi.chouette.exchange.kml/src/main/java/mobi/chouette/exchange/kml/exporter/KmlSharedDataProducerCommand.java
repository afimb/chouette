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
import mobi.chouette.exchange.kml.exporter.KmlLineProducerCommand.SharedData;
import mobi.chouette.exchange.report.ActionReport;
import mobi.chouette.exchange.report.DataStats;
import mobi.chouette.exchange.report.FileInfo;
import mobi.chouette.exchange.report.FileInfo.FILE_STATE;

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

			SharedData shared = (SharedData) context.get(SHARED_DATA);
			if (shared == null) {
				return ERROR;
			}

			saveData(context);
			DataStats globalStats = report.getStats();
			globalStats.connectionLinkCount = shared.getConnectionLinks().getItems().size();
			globalStats.stopAreaCount = shared.getPhysicalStops().getItems().size();
			globalStats.stopAreaCount += shared.getCommercialStops().getItems().size();
			globalStats.stopAreaCount += shared.getStopPlaces().getItems().size();
			globalStats.accessPointCount = shared.getAccessPoints().getItems().size();
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
		SharedData collection = (SharedData) context.get(SHARED_DATA);
		if (collection.getPhysicalStops().getItems().isEmpty())
			return;
		JobData jobData = (JobData) context.get(JOB_DATA);
		String rootDirectory = jobData.getPathName();
		Path dir = Paths.get(rootDirectory, OUTPUT);
		KmlData data = collection.getPhysicalStops();
		String fileName = "physical_stop_areas.kml";
		// TODO add metadata?
		File file = new File(dir.toFile(), fileName);
		writer.writeXmlFile(data, file);
		ActionReport report = (ActionReport) context.get(REPORT);
		FileInfo fileItem = new FileInfo(fileName,FILE_STATE.OK);
		report.getFiles().add(fileItem);

	}

	private void saveCommercialStops(Context context) throws Exception {
		SharedData collection = (SharedData) context.get(SHARED_DATA);
		if (collection.getCommercialStops().getItems().isEmpty())
			return;
		JobData jobData = (JobData) context.get(JOB_DATA);
		String rootDirectory = jobData.getPathName();
		Path dir = Paths.get(rootDirectory, OUTPUT);
		KmlData data = collection.getCommercialStops();
		String fileName = "commercial_stop_areas.kml";
		// TODO add metadata?
		File file = new File(dir.toFile(), fileName);
		writer.writeXmlFile(data, file);
		ActionReport report = (ActionReport) context.get(REPORT);
		FileInfo fileItem = new FileInfo(fileName,FILE_STATE.OK);
		report.getFiles().add(fileItem);

	}

	private void saveStopPlaces(Context context) throws Exception {
		SharedData collection = (SharedData) context.get(SHARED_DATA);
		if (collection.getStopPlaces().getItems().isEmpty())
			return;
		JobData jobData = (JobData) context.get(JOB_DATA);
		String rootDirectory = jobData.getPathName();
		Path dir = Paths.get(rootDirectory, OUTPUT);
		KmlData data = collection.getStopPlaces();
		String fileName = "stop_places.kml";
		// TODO add metadata?
		File file = new File(dir.toFile(), fileName);
		writer.writeXmlFile(data, file);
		ActionReport report = (ActionReport) context.get(REPORT);
		FileInfo fileItem = new FileInfo(fileName,FILE_STATE.OK);
		report.getFiles().add(fileItem);

	}

	private void saveConnectionLinks(Context context) throws Exception {
		SharedData collection = (SharedData) context.get(SHARED_DATA);
		if (collection.getConnectionLinks().getItems().isEmpty())
			return;
		JobData jobData = (JobData) context.get(JOB_DATA);
		String rootDirectory = jobData.getPathName();
		Path dir = Paths.get(rootDirectory, OUTPUT);
		KmlData data = collection.getConnectionLinks();
		String fileName = "connection_links.kml";
		File file = new File(dir.toFile(), fileName);
		writer.writeXmlFile(data, file);
		ActionReport report = (ActionReport) context.get(REPORT);
		FileInfo fileItem = new FileInfo(fileName,FILE_STATE.OK);
		report.getFiles().add(fileItem);

	}

	private void saveAccessPoints(Context context) throws Exception {
		SharedData collection = (SharedData) context.get(SHARED_DATA);
		if (collection.getAccessPoints().getItems().isEmpty())
			return;
		JobData jobData = (JobData) context.get(JOB_DATA);
		String rootDirectory = jobData.getPathName();
		Path dir = Paths.get(rootDirectory, OUTPUT);
		KmlData data = collection.getAccessPoints();
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
