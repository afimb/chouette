package mobi.chouette.exchange.gtfs.exporter;

import java.io.IOException;

import javax.naming.InitialContext;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Color;
import mobi.chouette.common.Context;
import mobi.chouette.common.chain.Command;
import mobi.chouette.common.chain.CommandFactory;
import mobi.chouette.exchange.gtfs.Constant;
import mobi.chouette.exchange.gtfs.model.exporter.GtfsExporter;
import mobi.chouette.exchange.report.ActionReport;

import com.jamonapi.Monitor;
import com.jamonapi.MonitorFactory;

@Log4j
public class GtfsTerminateExportCommand implements Command, Constant {

	public static final String COMMAND = "GtfsTerminateExportCommand";

	@Override
	public boolean execute(Context context) throws Exception {
		boolean result = ERROR;

		Monitor monitor = MonitorFactory.start(COMMAND);

		try {
			ActionReport report = (ActionReport) context.get(REPORT);
			GtfsExporter gtfsExporter = (GtfsExporter) context.get(GTFS_EXPORTER);
			gtfsExporter.dispose(report);
			result = SUCCESS;

		} catch (Exception e) {
			log.error(e, e);
			throw e;
		} finally {
			log.info(Color.MAGENTA + monitor.stop() + Color.NORMAL);
		}

		return result;
	}

	public static class DefaultCommandFactory extends CommandFactory {

		@Override
		protected Command create(InitialContext context) throws IOException {
			Command result = new GtfsTerminateExportCommand();
			return result;
		}
	}

	static {
		CommandFactory.factories.put(GtfsTerminateExportCommand.class.getName(), new DefaultCommandFactory());
	}

}
