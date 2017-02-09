package mobi.chouette.exchange.netexprofile.importer;

import com.jamonapi.Monitor;
import com.jamonapi.MonitorFactory;
import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Color;
import mobi.chouette.common.Context;
import mobi.chouette.common.chain.Command;
import mobi.chouette.common.chain.CommandFactory;
import mobi.chouette.exchange.importer.AbstractDisposeImportCommand;
import mobi.chouette.exchange.netexprofile.Constant;
import mobi.chouette.exchange.netexprofile.parser.NetexParser;
import org.w3c.dom.Document;

import javax.naming.InitialContext;
import java.io.IOException;
import java.util.List;

@Log4j
public class NetexDisposeImportCommand extends AbstractDisposeImportCommand implements  Constant {

	public static final String COMMAND = "NetexDisposeImportCommand";

	@Override
	@SuppressWarnings("unchecked")
	public boolean execute(Context context) throws Exception {
		boolean result = ERROR;

		Monitor monitor = MonitorFactory.start(COMMAND);

		try {
			((List<Document>) context.get(Constant.NETEX_COMMON_DATA_DOMS)).clear();
			super.execute(context);
			NetexParser.resetContext(context);
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
