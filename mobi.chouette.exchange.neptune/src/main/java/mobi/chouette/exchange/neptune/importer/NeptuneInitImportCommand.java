package mobi.chouette.exchange.neptune.importer;

import java.io.IOException;

import javax.naming.InitialContext;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Color;
import mobi.chouette.common.Context;
import mobi.chouette.common.chain.Command;
import mobi.chouette.common.chain.CommandFactory;
import mobi.chouette.exchange.neptune.Constant;
import mobi.chouette.model.util.Referential;

import com.jamonapi.Monitor;
import com.jamonapi.MonitorFactory;

@Log4j
public class NeptuneInitImportCommand implements Command, Constant {

	public static final String COMMAND = "NeptuneInitImportCommand";

	@Override
	public boolean execute(Context context) throws Exception {
		boolean result = ERROR;

		Monitor monitor = MonitorFactory.start(COMMAND);

		try {
			context.put(REFERENTIAL, new Referential());
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
			Command result = new NeptuneInitImportCommand();
			return result;
		}
	}

	static {
		CommandFactory.factories.put(NeptuneInitImportCommand.class.getName(), new DefaultCommandFactory());
	}

}
