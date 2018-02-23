package mobi.chouette.common.parallel;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;

import javax.naming.InitialContext;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Color;
import mobi.chouette.common.Context;
import mobi.chouette.common.Pair;
import mobi.chouette.common.chain.Command;
import mobi.chouette.common.chain.CommandFactory;

import com.jamonapi.Monitor;
import com.jamonapi.MonitorFactory;

@Log4j
public class ParallelExecutionCommand implements Command {

	public static final String COMMAND = "ParallelExecutionCommand";

	private List<Pair<Command, Function<Context, Context>>> commands = new ArrayList<>();

	@Getter
	@Setter
	private boolean ignored = false;

	public void add(Command command, Function<Context, Context> contextIntializer) {
		commands.add(Pair.of(command, contextIntializer));
	}

	@Override
	public boolean execute(Context context) throws Exception {

		if (context == null) {
			throw new IllegalArgumentException();
		}
		boolean result = SUCCESS;
		Monitor monitor = MonitorFactory.start(COMMAND);

		final AtomicInteger counter = new AtomicInteger(0);
		ThreadFactory threadFactory = r -> {
			Thread t = new Thread(r);
			t.setName("parallel-execution-command-thread-" + (counter.incrementAndGet()));
			t.setPriority(Thread.MIN_PRIORITY);
			return t;
		};
		int processors = Runtime.getRuntime().availableProcessors();
		ExecutorService executor = Executors.newFixedThreadPool(processors,threadFactory);

		try {
			List<Future<Boolean>> commandExecutionResults = new ArrayList<>();

			for (Pair<Command, Function<Context, Context>> commandWithContext : commands) {
				Command command = commandWithContext.getLeft();
				Function<Context, Context> contextInitializer = commandWithContext.getRight();
				Context commandContext = contextInitializer.apply(context);
				commandExecutionResults.add(executor.submit(() -> command.execute(commandContext)));
			}

			executor.shutdown();
			executor.awaitTermination(60, TimeUnit.MINUTES);

			for (Future<Boolean> commandResult : commandExecutionResults) {
				if (!ignored && commandResult.get() == ERROR) {
					result = ERROR;
				}
			}
		} catch (Exception e) {
			log.error("Parallel command execution failed ", e);
			result = ERROR;
		} finally {
			executor.shutdown();
			log.info(Color.MAGENTA + monitor.stop() + Color.NORMAL);
		}


		return result;
	}

	public static class DefaultCommandFactory extends CommandFactory {

		@Override
		protected Command create(InitialContext context) throws IOException {
			Command result = new ParallelExecutionCommand();
			return result;
		}
	}

	static {
		CommandFactory.factories.put(ParallelExecutionCommand.class.getName(),
				new ParallelExecutionCommand.DefaultCommandFactory());
	}
}
