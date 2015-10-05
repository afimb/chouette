package mobi.chouette.exchange.gtfs.importer;

import java.io.IOException;

import javax.naming.InitialContext;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Color;
import mobi.chouette.common.Context;
import mobi.chouette.common.chain.Command;
import mobi.chouette.common.chain.CommandFactory;
import mobi.chouette.exchange.gtfs.Constant;
import mobi.chouette.exchange.gtfs.model.importer.GtfsImporter;
import mobi.chouette.exchange.gtfs.validation.ValidationReporter;
import mobi.chouette.exchange.importer.AbstractDisposeImportCommand;

import com.jamonapi.Monitor;
import com.jamonapi.MonitorFactory;

@Log4j
public class GtfsDisposeImportCommand extends AbstractDisposeImportCommand  implements Constant {

	public static final String COMMAND = "GtfsDisposeImportCommand";

	@Override
	public boolean execute(Context context) throws Exception {
		boolean result = ERROR;

		Monitor monitor = MonitorFactory.start(COMMAND);

		try {
			super.execute(context);
			GtfsImporter importer = (GtfsImporter) context.get(PARSER);
			if (importer != null) {
				importer.dispose();
			}
			ValidationReporter validationReporter = (ValidationReporter) context.get(GTFS_REPORTER);
			if (validationReporter != null) {
				validationReporter.dispose();
			}

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
			Command result = new GtfsDisposeImportCommand();
			return result;
		}
	}

	static {
		CommandFactory.factories.put(GtfsDisposeImportCommand.class.getName(), new DefaultCommandFactory());
	}

}
