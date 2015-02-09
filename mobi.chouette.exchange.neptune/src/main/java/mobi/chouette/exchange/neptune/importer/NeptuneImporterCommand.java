package mobi.chouette.exchange.neptune.importer;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.enterprise.concurrent.ManagedExecutorService;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import lombok.AllArgsConstructor;
import lombok.ToString;
import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Color;
import mobi.chouette.common.Constant;
import mobi.chouette.common.Context;
import mobi.chouette.common.FileUtils;
import mobi.chouette.common.chain.Chain;
import mobi.chouette.common.chain.ChainImpl;
import mobi.chouette.common.chain.Command;
import mobi.chouette.common.chain.CommandFactory;
import mobi.chouette.exchange.TransactionnalCommand;
import mobi.chouette.exchange.importer.UncompressCommand;

import com.google.common.collect.Lists;
import com.jamonapi.Monitor;
import com.jamonapi.MonitorFactory;

@Stateless(name = NeptuneImporterCommand.COMMAND)
@ToString
@Log4j
public class NeptuneImporterCommand implements Command, Constant {

	public static final String COMMAND = "NeptuneImporterCommand";

	@Resource(lookup = "java:comp/DefaultManagedExecutorService")
	ManagedExecutorService executor;

	@EJB
	TransactionnalCommand transactionnal;

	@Override
	public boolean execute(Context context) throws Exception {
		boolean result = ERROR;
		Monitor monitor = MonitorFactory.start(COMMAND);

		try {
			InitialContext initialContext = (InitialContext) context
					.get(INITIAL_CONTEXT);

			// uncompress
			Command command = CommandFactory.create(initialContext,
					UncompressCommand.class.getName());
			command.execute(context);

			Path path = Paths.get(context.get(PATH).toString(), INPUT);
			List<Path> stream = FileUtils.listFiles(path, "*.xml");

			// validation
			int n = Runtime.getRuntime().availableProcessors() / 1;
			int size = (int )Math.ceil(stream.size() / n);
			List<List<Path>> partition = Lists.partition(stream, size);
			List<Task> tasks = new ArrayList<Task>();
			for (int i = 0; i < n; i++) {
				tasks.add(new Task(initialContext, context, partition.get(i)));
			}
			List<Future<Boolean>> futures = executor.invokeAll(tasks);

			for (Path file : stream) {

				context.put(FILE_URL, file.toUri().toURL().toExternalForm());

				Chain chain = new ChainImpl();

				// parser
				Command parser = CommandFactory.create(initialContext,
						NeptuneParserCommand.class.getName());
				chain.add(parser);

				// register
				// Command register = CommandFactory.create(ctx,
				// RegisterCommand.class.getName());
				// transac.add(register);

				transactionnal.execute(context, chain);
			}

			result = SUCCESS;
		} catch (Exception e) {
			log.error(e);
		}

		log.info(Color.MAGENTA + monitor.stop() + Color.NORMAL);
		return result;
	}

	@AllArgsConstructor
	class Task implements Callable<Boolean> {

		private InitialContext initialContext;
		private Context context;
		private List<Path> files = new ArrayList<Path>();

		@Override
		public Boolean call() throws Exception {
			boolean result = SUCCESS;
			log.info("[DSU] validate : " + files.size());

			for (Path file : files) {
				context.put(FILE_URL, file.toUri().toURL().toExternalForm());
				Command validation = CommandFactory.create(this.initialContext,
						NeptuneSAXParserCommand.class.getName());
				result = validation.execute(context);
			}
			return result;
		}
	}

	public static class DefaultCommandFactory extends CommandFactory {

		@Override
		protected Command create(InitialContext context) throws IOException {
			Command result = null;
			try {
				String name = "java:app/mobi.chouette.exchange.neptune/"
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
		CommandFactory.factories.put(NeptuneImporterCommand.class.getName(),
				factory);
	}
}
