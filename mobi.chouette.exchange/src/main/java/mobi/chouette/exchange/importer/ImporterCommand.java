package mobi.chouette.exchange.importer;

import java.io.IOException;

import javax.ejb.Stateless;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Context;
import mobi.chouette.common.chain.Chain;
import mobi.chouette.common.chain.ChainImpl;
import mobi.chouette.common.chain.Command;
import mobi.chouette.common.chain.CommandFactory;

@Stateless(name = ImporterCommand.COMMAND)
@Log4j
public class ImporterCommand implements Command {

	public static final String COMMAND = "ImporterCommand";

	@Override
	public boolean execute(Context context) throws Exception {

		boolean result = ERROR;

		Chain chain = new ChainImpl();
		// TODO add command (unzip, parsers, validation, save , ... )

		result = chain.execute(context);
		return result;
	}

	public static class DefaultCommandFactory extends CommandFactory {

		@Override
		protected Command create(InitialContext context) throws IOException {
			Command result = null;
			try {
				result = (Command) context.lookup(JAVA_MODULE + COMMAND);
			} catch (NamingException e) {
				log.error(e);
			}
			return result;
		}
	}

	static {
		CommandFactory factory = new DefaultCommandFactory();
		CommandFactory.factories.put(ImporterCommand.class.getName(), factory);
	}
}
