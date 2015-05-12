package mobi.chouette.exchange.hub.exporter;

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
import mobi.chouette.exchange.LineProcessingCommands;
import mobi.chouette.exchange.LineProcessingCommandsFactory;

@Log4j
@Data
public class HubExporterProcessingCommands implements LineProcessingCommands, Constant {

	
	public static class DefaultFactory extends LineProcessingCommandsFactory {

		@Override
		protected LineProcessingCommands create() throws IOException {
			LineProcessingCommands result = new HubExporterProcessingCommands();
			return result;
		}
	}

	static {
		LineProcessingCommandsFactory.factories.put(HubExporterProcessingCommands.class.getName(),
				new DefaultFactory());
	}

	@Override
	public List<? extends Command> getPreProcessingCommands(Context context,boolean withDao) {
		InitialContext initialContext = (InitialContext) context.get(INITIAL_CONTEXT);
		List<Command> commands = new ArrayList<>();
		try {
			commands.add(CommandFactory.create(initialContext, HubInitExportCommand.class.getName()));
		} catch (Exception e) {
			log.error(e,e);
		}
		return commands;
	}

	@Override
	public List<? extends Command> getLineProcessingCommands(Context context,boolean withDao) {
		InitialContext initialContext = (InitialContext) context.get(INITIAL_CONTEXT);
		List<Command> commands = new ArrayList<>();
		try {
			commands.add(CommandFactory.create(initialContext, HubLineProducerCommand.class.getName()));
		} catch (Exception e) {
			log.error(e,e);
		}
		
		return commands;
		
	}

	@Override
	public List<? extends Command> getPostProcessingCommands(Context context,boolean withDao) {
		InitialContext initialContext = (InitialContext) context.get(INITIAL_CONTEXT);
		List<Command> commands = new ArrayList<>();
		try {
			commands.add(CommandFactory.create(initialContext,
				HubSharedDataProducerCommand.class.getName()));
			commands.add(CommandFactory.create(initialContext, HubTerminateExportCommand.class.getName()));
		} catch (Exception e) {
			log.error(e,e);
		}
		return commands;
	}

	

}
