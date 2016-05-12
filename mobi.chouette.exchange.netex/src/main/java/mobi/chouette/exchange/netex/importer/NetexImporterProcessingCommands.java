package mobi.chouette.exchange.netex.importer;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import javax.naming.InitialContext;

import lombok.Data;
import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Constant;
import mobi.chouette.common.Context;
import mobi.chouette.common.FileUtil;
import mobi.chouette.common.JobData;
import mobi.chouette.common.chain.Chain;
import mobi.chouette.common.chain.ChainCommand;
import mobi.chouette.common.chain.Command;
import mobi.chouette.common.chain.CommandFactory;
import mobi.chouette.exchange.ProcessingCommands;
import mobi.chouette.exchange.ProcessingCommandsFactory;
import mobi.chouette.exchange.importer.CleanRepositoryCommand;
import mobi.chouette.exchange.importer.CopyCommand;
import mobi.chouette.exchange.importer.LineRegisterCommand;
import mobi.chouette.exchange.importer.UncompressCommand;
import mobi.chouette.exchange.validation.ImportedLineValidatorCommand;
import mobi.chouette.exchange.validation.SharedDataValidatorCommand;

@Data
@Log4j
public class NetexImporterProcessingCommands implements ProcessingCommands, Constant {

	public static class DefaultFactory extends ProcessingCommandsFactory {

		@Override
		protected ProcessingCommands create() throws IOException {
			ProcessingCommands result = new NetexImporterProcessingCommands();
			return result;
		}
	}

	static {
		ProcessingCommandsFactory.factories.put(NetexImporterProcessingCommands.class.getName(),
				new DefaultFactory());
	}

	@Override
	public List<? extends Command> getPreProcessingCommands(Context context,boolean withDao) {
		InitialContext initialContext = (InitialContext) context.get(INITIAL_CONTEXT);
		NetexImportParameters parameters = (NetexImportParameters) context.get(CONFIGURATION);
		List<Command> commands = new ArrayList<>();
		try {
			if (withDao && parameters.isCleanRepository()) {
				commands.add(CommandFactory.create(initialContext, CleanRepositoryCommand.class.getName()));
			}
			commands.add(CommandFactory.create(initialContext, UncompressCommand.class.getName()));
			commands.add(CommandFactory.create(initialContext, NetexInitImportCommand.class.getName()));
		} catch (Exception e) {
			log.error(e, e);
			throw new RuntimeException("unable to call factories");
		}

		return commands;
	}

	@Override
	public List<? extends Command> getLineProcessingCommands(Context context,boolean withDao) {
		InitialContext initialContext = (InitialContext) context.get(INITIAL_CONTEXT);
		NetexImportParameters parameters = (NetexImportParameters) context.get(CONFIGURATION);
		boolean level3validation = context.get(VALIDATION) != null;
		List<Command> commands = new ArrayList<>();
		JobData jobData = (JobData) context.get(JOB_DATA);
		Path path = Paths.get(jobData.getPathName(), INPUT);
		try {
			List<Path> stream = FileUtil.listFiles(path, "*.xml", "*metadata*");
			for (Path file : stream) {
				Chain chain = (Chain) CommandFactory.create(initialContext, ChainCommand.class.getName());
				commands.add(chain);
				String url = file.toUri().toURL().toExternalForm();
				// validation schema
				// NetexSAXParserCommand schema = (NetexSAXParserCommand)
				// CommandFactory.create(initialContext,
				// NetexSAXParserCommand.class.getName());
				// schema.setFileURL(url);
				// chain.add(schema);

				// parser
				NetexParserCommand parser = (NetexParserCommand) CommandFactory.create(initialContext,
						NetexParserCommand.class.getName());
				parser.setFileURL(url);
				chain.add(parser);

				// validation
				Command validation = CommandFactory.create(initialContext, NetexValidationCommand.class.getName());
				chain.add(validation);

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
			}

		} catch (Exception e) {

		}

		return commands;
	}

	@Override
	public List<? extends Command> getPostProcessingCommands(Context context,boolean withDao) {
		InitialContext initialContext = (InitialContext) context.get(INITIAL_CONTEXT);
		boolean level3validation = context.get(VALIDATION) != null;

		List<Command> commands = new ArrayList<>();
		try {
			if (level3validation) {
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
	public List<? extends Command> getStopAreaProcessingCommands(Context context, boolean withDao) {
		return new ArrayList<>();
	}
	@Override
	public List<? extends Command> getDisposeCommands(Context context, boolean withDao) {
		List<Command> commands = new ArrayList<>();
		return commands;
	}

}
