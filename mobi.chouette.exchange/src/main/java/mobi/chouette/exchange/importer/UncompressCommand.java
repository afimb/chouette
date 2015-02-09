package mobi.chouette.exchange.importer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.ejb.Stateless;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Color;
import mobi.chouette.common.FileUtils;
import mobi.chouette.common.Context;
import mobi.chouette.common.chain.Command;
import mobi.chouette.common.chain.CommandFactory;

import com.jamonapi.Monitor;
import com.jamonapi.MonitorFactory;

@Stateless(name = UncompressCommand.COMMAND)
@Log4j
public class UncompressCommand implements Command {

	public static final String COMMAND = "UncompressCommand";

	@Override
	public boolean execute(Context context) throws Exception {

		boolean result = ERROR;
		Monitor monitor = MonitorFactory.start(COMMAND);

		try {
			String path = (String) context.get(PATH);
			String file = (String) context.get(ARCHIVE);
			Path filename = Paths.get(path, file);
			Path target = Paths.get(path, INPUT);
			if (!Files.exists(target)) {
				Files.createDirectories(target);
			}
			FileUtils.uncompress(filename.toString(), target.toString());
			result = SUCCESS;
		} catch (Exception e) {
			log.error(e);
		}

		log.info(Color.MAGENTA + monitor.stop() + Color.NORMAL);
		return result;
	}

	public static class DefaultCommandFactory extends CommandFactory {

		@Override
		protected Command create(InitialContext context) throws IOException {
			Command result = null;
			try {
				String name = "java:app/mobi.chouette.exchange/"
						+ COMMAND;
				result = (Command) context.lookup(name);
			} catch (NamingException e) {
				log.error(e);
			}
			return result;
		}
	}

	static {
		CommandFactory factory = new DefaultCommandFactory();
		CommandFactory.factories
				.put(UncompressCommand.class.getName(), factory);
	}
}
