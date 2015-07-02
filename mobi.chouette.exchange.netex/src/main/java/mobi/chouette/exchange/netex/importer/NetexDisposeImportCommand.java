package mobi.chouette.exchange.netex.importer;

import java.io.IOException;

import javax.naming.InitialContext;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Color;
import mobi.chouette.common.Context;
import mobi.chouette.common.chain.Command;
import mobi.chouette.common.chain.CommandFactory;
import mobi.chouette.exchange.importer.AbstractDisposeImportCommand;
import mobi.chouette.exchange.netex.Constant;

import com.jamonapi.Monitor;
import com.jamonapi.MonitorFactory;

@Log4j
public class NetexDisposeImportCommand extends AbstractDisposeImportCommand implements  Constant {

	public static final String COMMAND = "NetexDisposeImportCommand";

	@Override
	public boolean execute(Context context) throws Exception {
		boolean result = ERROR;

		Monitor monitor = MonitorFactory.start(COMMAND);

		try {
			super.execute(context);
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
			Command result = new NetexDisposeImportCommand();
			return result;
		}
	}

	static {
		CommandFactory.factories.put(NetexDisposeImportCommand.class.getName(), new DefaultCommandFactory());
	}

}
