package mobi.chouette.exchange.gtfs.importer;

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
import mobi.chouette.exchange.gtfs.model.GtfsRoute;
import mobi.chouette.exchange.gtfs.model.importer.GtfsImporter;
import mobi.chouette.exchange.gtfs.model.importer.Index;

@Data
public class GtfsImporterProcessingCommands implements CommandLineProcessingCommands, Constant {


	public static class DefaultFactory extends CommandLineProcessingCommandsFactory {

		@Override
		protected CommandLineProcessingCommands create() throws IOException {
			CommandLineProcessingCommands result = new GtfsImporterProcessingCommands();
			return result;
		}
	}

	static {
		CommandLineProcessingCommandsFactory.factories.put(GtfsImporterProcessingCommands.class.getName(),
				new DefaultFactory());
	}

	@Override
	public List<? extends Command> getPreProcessingCommands(Context context) {
		InitialContext initialContext = (InitialContext) context.get(INITIAL_CONTEXT);
		List<Command> commands = new ArrayList<>();
		try {
			Command initImport = CommandFactory.create(initialContext, GtfsInitImportCommand.class.getName());
			commands.add(initImport);
			Command validation = CommandFactory.create(initialContext, GtfsValidationCommand.class.getName());
			commands.add(validation);
			} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return commands;
	}

	@Override
	public List<? extends Command> getLineProcessingCommands(Context context) {
		InitialContext initialContext = (InitialContext) context.get(INITIAL_CONTEXT);
		List<Command> commands = new ArrayList<>();
		GtfsImporter importer = (GtfsImporter) context.get(PARSER);

		Index<GtfsRoute> index = importer.getRouteById();
		try
		{
			for (GtfsRoute gtfsRoute : index) {

				GtfsRouteParserCommand parser = (GtfsRouteParserCommand) CommandFactory.create(initialContext,
						GtfsRouteParserCommand.class.getName());
				parser.setGtfsRouteId(gtfsRoute.getRouteId());

				commands.add(parser);
			}

		}
		catch (Exception e)
		{

		}

		return commands;
	}

	@Override
	public List<? extends Command> getPostProcessingCommands(Context context) {
		return new ArrayList<>();
	}



}
