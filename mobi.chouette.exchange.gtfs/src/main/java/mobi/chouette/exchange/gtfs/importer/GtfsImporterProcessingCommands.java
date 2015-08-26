package mobi.chouette.exchange.gtfs.importer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.naming.InitialContext;

import lombok.Data;
import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Constant;
import mobi.chouette.common.Context;
import mobi.chouette.common.chain.Chain;
import mobi.chouette.common.chain.ChainCommand;
import mobi.chouette.common.chain.Command;
import mobi.chouette.common.chain.CommandFactory;
import mobi.chouette.exchange.ProcessingCommands;
import mobi.chouette.exchange.ProcessingCommandsFactory;
import mobi.chouette.exchange.gtfs.model.GtfsRoute;
import mobi.chouette.exchange.gtfs.model.importer.GtfsImporter;
import mobi.chouette.exchange.gtfs.model.importer.Index;
import mobi.chouette.exchange.importer.CleanRepositoryCommand;
import mobi.chouette.exchange.importer.CopyCommand;
import mobi.chouette.exchange.importer.LineRegisterCommand;
import mobi.chouette.exchange.importer.StopAreaRegisterCommand;
import mobi.chouette.exchange.importer.UncompressCommand;
import mobi.chouette.exchange.validation.ImportedLineValidatorCommand;
import mobi.chouette.exchange.validation.SharedDataValidatorCommand;

@Data
@Log4j
public class GtfsImporterProcessingCommands implements ProcessingCommands, Constant {

	public static class DefaultFactory extends ProcessingCommandsFactory {

		@Override
		protected ProcessingCommands create() throws IOException {
			ProcessingCommands result = new GtfsImporterProcessingCommands();
			return result;
		}
	}

	static {
		ProcessingCommandsFactory.factories.put(GtfsImporterProcessingCommands.class.getName(),
				new DefaultFactory());
	}

	@Override
	public List<? extends Command> getPreProcessingCommands(Context context, boolean withDao) {
		InitialContext initialContext = (InitialContext) context.get(INITIAL_CONTEXT);
		GtfsImportParameters parameters = (GtfsImportParameters) context.get(CONFIGURATION);
		List<Command> commands = new ArrayList<>();
		try {
			if (withDao && parameters.isCleanRepository()) {
				commands.add(CommandFactory.create(initialContext, CleanRepositoryCommand.class.getName()));
			}
			commands.add(CommandFactory.create(initialContext, UncompressCommand.class.getName()));
			commands.add(CommandFactory.create(initialContext, GtfsValidationRulesCommand.class.getName()));
			commands.add(CommandFactory.create(initialContext, GtfsInitImportCommand.class.getName()));
			commands.add(CommandFactory.create(initialContext, GtfsValidationCommand.class.getName()));
		} catch (Exception e) {
			log.error(e, e);
			throw new RuntimeException("unable to call factories");
		}
		return commands;
	}

	@Override
	public List<? extends Command> getLineProcessingCommands(Context context, boolean withDao) {
		InitialContext initialContext = (InitialContext) context.get(INITIAL_CONTEXT);
		GtfsImportParameters parameters = (GtfsImportParameters) context.get(CONFIGURATION);
		boolean level3validation = context.get(VALIDATION) != null;
		List<Command> commands = new ArrayList<>();
		GtfsImporter importer = (GtfsImporter) context.get(PARSER);

		Index<GtfsRoute> index = importer.getRouteById();
		try {
			for (GtfsRoute gtfsRoute : index) {

				Chain chain = (Chain) CommandFactory.create(initialContext, ChainCommand.class.getName());

				GtfsRouteParserCommand parser = (GtfsRouteParserCommand) CommandFactory.create(initialContext,
						GtfsRouteParserCommand.class.getName());
				parser.setGtfsRouteId(gtfsRoute.getRouteId());
				chain.add(parser);
				if (withDao && !parameters.isNoSave()) {

					// register
					Command register = CommandFactory.create(initialContext, LineRegisterCommand.class.getName());
					chain.add(register);

					Command copy = CommandFactory.create(initialContext, CopyCommand.class.getName());
					chain.add(copy);
				}
				if (level3validation) {
					// add validation
					Command validate = CommandFactory.create(initialContext,
							ImportedLineValidatorCommand.class.getName());
					chain.add(validate);
				}
				commands.add(chain);
			}

		} catch (Exception e) {
			log.error(e, e);
			throw new RuntimeException("unable to call factories");
		}

		return commands;
	}

	@Override
	public List<? extends Command> getStopAreaProcessingCommands(Context context, boolean withDao) {
		InitialContext initialContext = (InitialContext) context.get(INITIAL_CONTEXT);
		GtfsImportParameters parameters = (GtfsImportParameters) context.get(CONFIGURATION);

		List<Command> commands = new ArrayList<>();
		try {
			Chain chain = (Chain) CommandFactory.create(initialContext, ChainCommand.class.getName());

			GtfsStopParserCommand parser = (GtfsStopParserCommand) CommandFactory.create(initialContext,
					GtfsStopParserCommand.class.getName());
			chain.add(parser);
			if (withDao && !parameters.isNoSave()) {

				// register
				Command register = CommandFactory.create(initialContext, StopAreaRegisterCommand.class.getName());
				chain.add(register);
			}
			commands.add(chain);

		} catch (Exception e) {
			log.error(e, e);
			throw new RuntimeException("unable to call factories");
		}
		return commands;
	}

	@Override
	public List<? extends Command> getPostProcessingCommands(Context context, boolean withDao) {
		InitialContext initialContext = (InitialContext) context.get(INITIAL_CONTEXT);
		boolean level3validation = context.get(VALIDATION) != null;
		GtfsImportParameters parameters = (GtfsImportParameters) context.get(CONFIGURATION);

		List<Command> commands = new ArrayList<>();
		try {
			if (level3validation && !(parameters.getReferencesType().equalsIgnoreCase("stop_area"))) {
				// add shared data validation
				commands.add(CommandFactory.create(initialContext, SharedDataValidatorCommand.class.getName()));
			}

		} catch (Exception e) {
			log.error(e, e);
			throw new RuntimeException("unable to call factories");
		}
		return commands;
	}
	@Override
	public List<? extends Command> getDisposeCommands(Context context, boolean withDao) {
		InitialContext initialContext = (InitialContext) context.get(INITIAL_CONTEXT);
		List<Command> commands = new ArrayList<>();
		try {
			commands.add(CommandFactory.create(initialContext, GtfsDisposeImportCommand.class.getName()));

		} catch (Exception e) {
			log.error(e, e);
			throw new RuntimeException("unable to call factories");
		}
		return commands;
	}

}
