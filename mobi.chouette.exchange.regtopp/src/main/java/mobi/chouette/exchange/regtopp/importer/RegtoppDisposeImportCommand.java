package mobi.chouette.exchange.regtopp.importer;

import static mobi.chouette.exchange.regtopp.RegtoppConstant.*;

import java.io.IOException;

import javax.naming.InitialContext;

import com.jamonapi.Monitor;
import com.jamonapi.MonitorFactory;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Color;
import mobi.chouette.common.Context;
import mobi.chouette.common.chain.Command;
import mobi.chouette.common.chain.CommandFactory;
import mobi.chouette.exchange.importer.AbstractDisposeImportCommand;
import mobi.chouette.exchange.regtopp.model.importer.parser.RegtoppImporter;
import mobi.chouette.exchange.regtopp.validation.RegtoppValidationReporter;

@Log4j
public class RegtoppDisposeImportCommand extends AbstractDisposeImportCommand {

	public static final String COMMAND = "RegtoppDisposeImportCommand";

	@Override
	public boolean execute(Context context) throws Exception {
		boolean result = ERROR;

		Monitor monitor = MonitorFactory.start(COMMAND);

		try {
			super.execute(context);
			RegtoppImporter importer = (RegtoppImporter) context.get(PARSER);
			if (importer != null) {
				importer.dispose();
			}
			RegtoppValidationReporter validationReporter = (RegtoppValidationReporter) context.get(REGTOPP_REPORTER);
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
			Command result = new RegtoppDisposeImportCommand();
			return result;
		}
	}

	static {
		CommandFactory.factories.put(RegtoppDisposeImportCommand.class.getName(), new DefaultCommandFactory());
	}

}
