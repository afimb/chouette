package mobi.chouette.exchange.kml.exporter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.naming.InitialContext;

import lombok.Data;
import mobi.chouette.common.Constant;
import mobi.chouette.common.Context;
import mobi.chouette.common.chain.Command;
import mobi.chouette.common.chain.CommandFactory;
import mobi.chouette.exchange.LineProcessingCommands;
import mobi.chouette.exchange.LineProcessingCommandsFactory;

@Data
public class KmlExporterProcessingCommands implements LineProcessingCommands, Constant {

	
	public static class DefaultFactory extends LineProcessingCommandsFactory {

		@Override
		protected LineProcessingCommands create() throws IOException {
			LineProcessingCommands result = new KmlExporterProcessingCommands();
			return result;
		}
	}

	static {
		LineProcessingCommandsFactory.factories.put(KmlExporterProcessingCommands.class.getName(),
				new DefaultFactory());
	}

	@Override
	public List<? extends Command> getPreProcessingCommands(Context context,boolean withDao) {
		InitialContext initCtx = (InitialContext) context.get(INITIAL_CONTEXT);
		List<Command> commands = new ArrayList<>();
		try {
			commands.add(CommandFactory.create(initCtx, KmlInitExportCommand.class.getName()));
		} catch (Exception e) {
			// TODO
		}
		
		return commands;
	}

	@Override
	public List<? extends Command> getLineProcessingCommands(Context context,boolean withDao) {
		InitialContext initCtx = (InitialContext) context.get(INITIAL_CONTEXT);
		List<Command> commands = new ArrayList<>();
		try {
			commands.add(CommandFactory.create(initCtx, KmlLineProducerCommand.class.getName()));
		} catch (Exception e) {
			// TODO
		}
		
		return commands;
		
	}

	@Override
	public List<? extends Command> getPostProcessingCommands(Context context,boolean withDao) {
		return new ArrayList<>();
	}

	

}
