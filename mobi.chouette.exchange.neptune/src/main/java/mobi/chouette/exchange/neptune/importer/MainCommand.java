package mobi.chouette.exchange.neptune.importer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.ejb.Stateless;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.CompressUtils;
import mobi.chouette.common.Context;
import mobi.chouette.common.chain.Command;
import mobi.chouette.common.chain.CommandFactory;

@Stateless(name = MainCommand.COMMAND)
@Log4j
public class MainCommand implements Command, Constant {

	public static final String COMMAND = "MainCommand";

	@Override
	public boolean execute(Context context) throws Exception {
		boolean result = ERROR;

		try {
		
			result = SUCCESS;
		} catch (Exception e) {
			log.error(e);
		}

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
		CommandFactory.factories.put(MainCommand.class.getName(),
				factory);
	}
}
