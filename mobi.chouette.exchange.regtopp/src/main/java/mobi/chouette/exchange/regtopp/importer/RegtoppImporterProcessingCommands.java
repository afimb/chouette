package mobi.chouette.exchange.regtopp.importer;

import static mobi.chouette.common.Constant.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.naming.InitialContext;

import lombok.Data;
import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Context;
import mobi.chouette.common.chain.Chain;
import mobi.chouette.common.chain.ChainCommand;
import mobi.chouette.common.chain.Command;
import mobi.chouette.common.chain.CommandFactory;
import mobi.chouette.exchange.ProcessingCommands;
import mobi.chouette.exchange.ProcessingCommandsFactory;
import mobi.chouette.exchange.importer.CleanRepositoryCommand;
import mobi.chouette.exchange.importer.CopyCommand;
import mobi.chouette.exchange.importer.LineRegisterCommand;
import mobi.chouette.exchange.importer.StopAreaRegisterCommand;
import mobi.chouette.exchange.importer.UncompressCommand;
import mobi.chouette.exchange.regtopp.model.RegtoppTripIndexTIX;
import mobi.chouette.exchange.regtopp.model.importer.parser.RegtoppImporter;
import mobi.chouette.exchange.regtopp.model.importer.parser.index.Index;
import mobi.chouette.exchange.validation.ImportedLineValidatorCommand;
import mobi.chouette.exchange.validation.SharedDataValidatorCommand;

@Data
@Log4j
public class RegtoppImporterProcessingCommands implements ProcessingCommands {

	public static class DefaultFactory extends ProcessingCommandsFactory {

		@Override
		protected ProcessingCommands create() throws IOException {
			ProcessingCommands result = new RegtoppImporterProcessingCommands();
			return result;
		}
	}

	static {
		ProcessingCommandsFactory.factories.put(RegtoppImporterProcessingCommands.class.getName(), new DefaultFactory());
	}

	@Override
	public List<? extends Command> getPreProcessingCommands(Context context, boolean withDao) {
		InitialContext initialContext = (InitialContext) context.get(INITIAL_CONTEXT);
		RegtoppImportParameters parameters = (RegtoppImportParameters) context.get(CONFIGURATION);
		List<Command> commands = new ArrayList<>();
		try {
			if (withDao && parameters.isCleanRepository()) {
				commands.add(CommandFactory.create(initialContext, CleanRepositoryCommand.class.getName()));
			}
			// Uncompress zip file
			commands.add(CommandFactory.create(initialContext, UncompressCommand.class.getName()));
			// Does nothing but some setup, TODO remove
			commands.add(CommandFactory.create(initialContext, RegtoppValidationRulesCommand.class.getName()));
			// Does little, instantiates importer, sets up validation
			commands.add(CommandFactory.create(initialContext, RegtoppInitImportCommand.class.getName()));

			// Validate that files are present, if missing some report this
			commands.add(CommandFactory.create(initialContext, RegtoppFilePresenceValidationCommand.class.getName()));

			// Parse and validate file consistency
			commands.add(CommandFactory.create(initialContext, RegtoppFileConsistencyValidationCommand.class.getName()));
		} catch (Exception e) {
			log.error(e, e);
			throw new RuntimeException("unable to call factories", e);
		}
		return commands;
	}

	@Override
	public List<? extends Command> getLineProcessingCommands(Context context, boolean withDao) {
		InitialContext initialContext = (InitialContext) context.get(INITIAL_CONTEXT);
		RegtoppImportParameters parameters = (RegtoppImportParameters) context.get(CONFIGURATION);
		boolean level3validation = context.get(VALIDATION) != null;
		List<Command> commands = new ArrayList<>();
		RegtoppImporter importer = (RegtoppImporter) context.get(PARSER);

		try {

			Index<RegtoppTripIndexTIX> index = importer.getUniqueLinesByTripIndex();
			Iterator<String> keys = index.keys();
			while (keys.hasNext()) {
				String lineId = keys.next();

				Chain chain = (Chain) CommandFactory.create(initialContext, ChainCommand.class.getName());

				// Pull out line by line and convert to Chouette model
				RegtoppLineParserCommand parser = (RegtoppLineParserCommand) CommandFactory.create(initialContext, RegtoppLineParserCommand.class.getName());

				parser.setLineId(lineId);
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
					Command validate = CommandFactory.create(initialContext, ImportedLineValidatorCommand.class.getName());
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
		RegtoppImportParameters parameters = (RegtoppImportParameters) context.get(CONFIGURATION);

		List<Command> commands = new ArrayList<>();
		try {
			Chain chain = (Chain) CommandFactory.create(initialContext, ChainCommand.class.getName());

			RegtoppStopParserCommand parser = (RegtoppStopParserCommand) CommandFactory.create(initialContext, RegtoppStopParserCommand.class.getName());
			chain.add(parser);
			if (withDao && !parameters.isNoSave()) {

				// register
				Command register = CommandFactory.create(initialContext, StopAreaRegisterCommand.class.getName());
				chain.add(register);
			}
			commands.add(chain);

		} catch (Exception e) {
			log.error(e, e);
		}
		return commands;
	}

	@Override
	public List<? extends Command> getPostProcessingCommands(Context context, boolean withDao) {
		InitialContext initialContext = (InitialContext) context.get(INITIAL_CONTEXT);
		boolean level3validation = context.get(VALIDATION) != null;
		RegtoppImportParameters parameters = (RegtoppImportParameters) context.get(CONFIGURATION);

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
			commands.add(CommandFactory.create(initialContext, RegtoppDisposeImportCommand.class.getName()));

		} catch (Exception e) {
			log.error(e, e);
			throw new RuntimeException("unable to call factories");
		}
		return commands;
	}

}
