package mobi.chouette.exchange.neptune.importer;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import javax.naming.InitialContext;

import lombok.Data;
import mobi.chouette.common.Constant;
import mobi.chouette.common.Context;
import mobi.chouette.common.FileUtil;
import mobi.chouette.common.JobData;
import mobi.chouette.common.chain.Chain;
import mobi.chouette.common.chain.ChainCommand;
import mobi.chouette.common.chain.Command;
import mobi.chouette.common.chain.CommandFactory;
import mobi.chouette.exchange.CommandLineProcessingCommands;
import mobi.chouette.exchange.CommandLineProcessingCommandsFactory;

@Data
public class NeptuneImporterProcessingCommands implements CommandLineProcessingCommands, Constant {

	public static class DefaultFactory extends CommandLineProcessingCommandsFactory {

		@Override
		protected CommandLineProcessingCommands create() throws IOException {
			CommandLineProcessingCommands result = new NeptuneImporterProcessingCommands();
			return result;
		}
	}

	static {
		CommandLineProcessingCommandsFactory.factories.put(NeptuneImporterProcessingCommands.class.getName(),
				new DefaultFactory());
	}

	@Override
	public List<? extends Command> getPreProcessingCommands(Context context) {
		InitialContext initialContext = (InitialContext) context.get(INITIAL_CONTEXT);
		List<Command> commands = new ArrayList<>();
		try {
			commands.add(CommandFactory.create(initialContext, NeptuneInitImportCommand.class.getName()));
		} catch (Exception e) {

		}

		return commands;
	}

	@Override
	public List<? extends Command> getLineProcessingCommands(Context context) {
		InitialContext initialContext = (InitialContext) context.get(INITIAL_CONTEXT);
		List<Command> commands = new ArrayList<>();
		JobData jobData = (JobData) context.get(JOB_DATA);
		Path path = Paths.get(jobData.getPathName(), INPUT);
		try {
			List<Path> stream = FileUtil.listFiles(path, "*.xml", "*metadata*");
			for (Path file : stream) {
				Chain chain = (Chain) CommandFactory.create(initialContext, ChainCommand.class.getName());
				commands.add(chain);
				// validation schema
				String url = file.toUri().toURL().toExternalForm();
				NeptuneSAXParserCommand schema = (NeptuneSAXParserCommand) CommandFactory.create(initialContext,
						NeptuneSAXParserCommand.class.getName());
				schema.setFileURL(url);
				chain.add(schema);

				// parser
				NeptuneParserCommand parser = (NeptuneParserCommand) CommandFactory.create(initialContext,
						NeptuneParserCommand.class.getName());
				parser.setFileURL(file.toUri().toURL().toExternalForm());
				chain.add(parser);

				// validation
				Command validation = CommandFactory.create(initialContext, NeptuneValidationCommand.class.getName());
				chain.add(validation);

			}

		} catch (Exception e) {

		}

		return commands;
	}

	@Override
	public List<? extends Command> getPostProcessingCommands(Context context) {
		return new ArrayList<>();
	}

}
