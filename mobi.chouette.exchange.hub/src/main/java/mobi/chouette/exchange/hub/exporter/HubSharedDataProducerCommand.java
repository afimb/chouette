package mobi.chouette.exchange.hub.exporter;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.naming.InitialContext;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Color;
import mobi.chouette.common.Constant;
import mobi.chouette.common.Context;
import mobi.chouette.common.chain.Command;
import mobi.chouette.common.chain.CommandFactory;
import mobi.chouette.exchange.report.ActionReport;
import mobi.chouette.exchange.report.LineStats;

import com.jamonapi.Monitor;
import com.jamonapi.MonitorFactory;

@Log4j
public class HubSharedDataProducerCommand implements Command, Constant {
	public static final String COMMAND = "HubSharedDataProducerCommand";

	private HubFileWriter writer = new HubFileWriter();

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
			LineStats globalStats = report.getStats();
			if (globalStats == null) {
				globalStats = new LineStats();
				report.setStats(globalStats);
			}
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
		String rootDirectory = (String) context.get(PATH);
		Path dir = Paths.get(rootDirectory, OUTPUT);

	}

	private void saveCommercialStops(Context context) throws Exception {
		ExportableData collection = (ExportableData) context.get(EXPORTABLE_DATA);
		if (collection.getCommercialStopPoints().isEmpty())
			return;
		String rootDirectory = (String) context.get(PATH);
		Path dir = Paths.get(rootDirectory, OUTPUT);

	}


	private void saveConnectionLinks(Context context) throws Exception {
		ExportableData collection = (ExportableData) context.get(EXPORTABLE_DATA);
		if (collection.getConnectionLinks().isEmpty())
			return;
		String rootDirectory = (String) context.get(PATH);
		Path dir = Paths.get(rootDirectory, OUTPUT);

	}

	private void saveAccessPoints(Context context) throws Exception {
		ExportableData collection = (ExportableData) context.get(EXPORTABLE_DATA);
		if (collection.getAccessPoints().isEmpty())
			return;
		String rootDirectory = (String) context.get(PATH);
		Path dir = Paths.get(rootDirectory, OUTPUT);

	}

	private void saveAccessLinks(Context context) throws Exception {
		// TODO Auto-generated method stub

	}

	public static class DefaultCommandFactory extends CommandFactory {

		@Override
		protected Command create(InitialContext context) throws IOException {
			Command result = new HubSharedDataProducerCommand();
			return result;
		}
	}

	static {
		CommandFactory.factories.put(HubSharedDataProducerCommand.class.getName(), new DefaultCommandFactory());
	}

}
