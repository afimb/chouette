package mobi.chouette.exchange.importer;

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

@Stateless(name = UncompressCommand.COMMAND)
@Log4j
public class UncompressCommand implements Command {

	public static final String COMMAND = "UncompressCommand";

	@Override
	public boolean execute(Context context) throws Exception {

		boolean result = ERROR;

		try {
			String path = (String) context.get(PATH);
			String file = (String) context.get(ARCHIVE);
			Path filename = Paths.get(path, file);
			Path target = Paths.get(path, INPUT);
			if (!Files.exists(target)) {
				Files.createDirectories(target);
			}
			CompressUtils.uncompress(filename.toString(), target.toString());
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
		CommandFactory.factories
				.put(UncompressCommand.class.getName(), factory);
	}
}
