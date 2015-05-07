package mobi.chouette.exchange.neptune.exporter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.naming.InitialContext;

import lombok.Data;
import mobi.chouette.common.Constant;
import mobi.chouette.common.Context;
import mobi.chouette.common.chain.Command;
import mobi.chouette.common.chain.CommandFactory;
import mobi.chouette.exchange.CommandLineProcessingCommands;
import mobi.chouette.exchange.CommandLineProcessingCommandsFactory;

@Data
public class NeptuneExporterProcessingCommands implements CommandLineProcessingCommands, Constant {

	
	public static class DefaultFactory extends CommandLineProcessingCommandsFactory {

		@Override
		protected CommandLineProcessingCommands create() throws IOException {
			CommandLineProcessingCommands result = new NeptuneExporterProcessingCommands();
			return result;
		}
	}

	static {
		CommandLineProcessingCommandsFactory.factories.put(NeptuneExporterProcessingCommands.class.getName(),
				new DefaultFactory());
	}

	@Override
	public List<? extends Command> getPreProcessingCommands(Context context) {
		InitialContext initCtx = (InitialContext) context.get(INITIAL_CONTEXT);
		List<Command> commands = new ArrayList<>();
		try {
			commands.add(CommandFactory.create(initCtx, NeptuneInitExportCommand.class.getName()));
		} catch (Exception e) {
			// TODO
		}
		
		return commands;
	}

	@Override
	public List<? extends Command> getLineProcessingCommands(Context context) {
		InitialContext initCtx = (InitialContext) context.get(INITIAL_CONTEXT);
		List<Command> commands = new ArrayList<>();
		try {
			commands.add(CommandFactory.create(initCtx, NeptuneProducerCommand.class.getName()));
		} catch (Exception e) {
			// TODO
		}
		
		return commands;
		
	}

	@Override
	public List<? extends Command> getPostProcessingCommands(Context context) {
		return new ArrayList<>();
	}

	

}
