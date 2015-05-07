package mobi.chouette.exchange.gtfs.exporter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.naming.InitialContext;

import lombok.Data;
import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Constant;
import mobi.chouette.common.Context;
import mobi.chouette.common.chain.Command;
import mobi.chouette.common.chain.CommandFactory;
import mobi.chouette.exchange.CommandLineProcessingCommands;
import mobi.chouette.exchange.CommandLineProcessingCommandsFactory;

@Log4j
@Data
public class GtfsExporterProcessingCommands implements CommandLineProcessingCommands, Constant {

	
	public static class DefaultFactory extends CommandLineProcessingCommandsFactory {

		@Override
		protected CommandLineProcessingCommands create() throws IOException {
			CommandLineProcessingCommands result = new GtfsExporterProcessingCommands();
			return result;
		}
	}

	static {
		CommandLineProcessingCommandsFactory.factories.put(GtfsExporterProcessingCommands.class.getName(),
				new DefaultFactory());
	}

	@Override
	public List<? extends Command> getPreProcessingCommands(Context context) {
		InitialContext initialContext = (InitialContext) context.get(INITIAL_CONTEXT);
		List<Command> commands = new ArrayList<>();
		try {
			commands.add(CommandFactory.create(initialContext, GtfsInitExportCommand.class.getName()));
		} catch (Exception e) {
			log.error(e,e);
		}
		return commands;
	}

	@Override
	public List<? extends Command> getLineProcessingCommands(Context context) {
		InitialContext initialContext = (InitialContext) context.get(INITIAL_CONTEXT);
		List<Command> commands = new ArrayList<>();
		try {
			commands.add(CommandFactory.create(initialContext, GtfsLineProducerCommand.class.getName()));
		} catch (Exception e) {
			log.error(e,e);
		}
		
		return commands;
		
	}

	@Override
	public List<? extends Command> getPostProcessingCommands(Context context) {
		InitialContext initialContext = (InitialContext) context.get(INITIAL_CONTEXT);
		List<Command> commands = new ArrayList<>();
		try {
			commands.add(CommandFactory.create(initialContext,
				GtfsSharedDataProducerCommand.class.getName()));
			commands.add(CommandFactory.create(initialContext, GtfsTerminateExportCommand.class.getName()));
		} catch (Exception e) {
			log.error(e,e);
		}
		return commands;
	}

	

}
