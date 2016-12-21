package mobi.chouette.exchange.gtfs.validator;

import java.io.IOException;

import javax.ejb.Stateless;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Color;
import mobi.chouette.common.Context;
import mobi.chouette.common.chain.Command;
import mobi.chouette.common.chain.CommandFactory;
import mobi.chouette.exchange.gtfs.importer.GtfsImporterCommand;
import mobi.chouette.exchange.parameters.AbstractImportParameter;

import com.jamonapi.Monitor;
import com.jamonapi.MonitorFactory;

@Log4j
@Stateless(name = GtfsValidatorCommand.COMMAND)
public class GtfsValidatorCommand extends GtfsImporterCommand {

	public static final String COMMAND = "GtfsValidatorCommand";

	@Override
	public boolean execute(Context context) throws Exception {
		Monitor monitor = MonitorFactory.start(COMMAND);

		try {
			// set default parameters
			AbstractImportParameter parameters = (AbstractImportParameter) context.get(CONFIGURATION);
			parameters.setNoSave(true);
			parameters.setCleanRepository(false);

			return super.execute(context);
		} finally {
			log.info(Color.YELLOW + monitor.stop() + Color.NORMAL);
		}
	}

	public static class DefaultCommandFactory extends CommandFactory {

		@Override
		protected Command create(InitialContext context) throws IOException {
			Command result = null;
			try {
				String name = "java:app/mobi.chouette.exchange.validator/" + COMMAND;
				result = (Command) context.lookup(name);
			} catch (NamingException e) {
				// try another way on test context
				String name = "java:module/" + COMMAND;
				try {
					result = (Command) context.lookup(name);
				} catch (NamingException e1) {
					log.error(e);
				}
			}
			return result;
		}
	}

	static {
		CommandFactory.factories.put(GtfsValidatorCommand.class.getName(), new DefaultCommandFactory());
	}
}
