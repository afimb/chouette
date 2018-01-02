package mobi.chouette.exchange.netexprofile.importer;

import com.jamonapi.Monitor;
import com.jamonapi.MonitorFactory;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Color;
import mobi.chouette.common.Context;
import mobi.chouette.common.chain.Command;
import mobi.chouette.common.chain.CommandFactory;
import mobi.chouette.exchange.importer.AbstractDisposeImportCommand;
import mobi.chouette.exchange.importer.CleanRepositoryCommand;
import mobi.chouette.exchange.netexprofile.Constant;
import mobi.chouette.exchange.netexprofile.parser.NetexParser;
import mobi.chouette.exchange.report.ActionReporter;

import javax.naming.InitialContext;

import java.io.IOException;

@Log4j
public class NetexDisposeImportCommand extends AbstractDisposeImportCommand implements Constant {

	public static final String COMMAND = "NetexDisposeImportCommand";

	@Override
	public boolean execute(Context context) throws Exception {
		boolean result = ERROR;

		Monitor monitor = MonitorFactory.start(COMMAND);

		try {
			super.execute(context);
			NetexParser.resetContext(context);

			NetexprofileImportParameters parameters = (NetexprofileImportParameters) context.get(CONFIGURATION);
			if (parameters.isCleanOnErrors() && ActionReporter.Factory.getInstance().hasActionError(context)) {
				log.warn("Cleaning data space after import command ended with error");
				InitialContext initialContext = (InitialContext) context.get(INITIAL_CONTEXT);
				CommandFactory.create(initialContext, CleanRepositoryCommand.class.getName()).execute(context);
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
			return new NetexDisposeImportCommand();
		}
	}

	static {
		CommandFactory.factories.put(NetexDisposeImportCommand.class.getName(), new DefaultCommandFactory());
	}

}
