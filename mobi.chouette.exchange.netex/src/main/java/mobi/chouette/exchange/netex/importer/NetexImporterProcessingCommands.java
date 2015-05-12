package mobi.chouette.exchange.netex.importer;

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
public class NetexImporterProcessingCommands implements CommandLineProcessingCommands, Constant {

	public static class DefaultFactory extends CommandLineProcessingCommandsFactory {

		@Override
		protected CommandLineProcessingCommands create() throws IOException {
			CommandLineProcessingCommands result = new NetexImporterProcessingCommands();
			return result;
		}
	}

	static {
		CommandLineProcessingCommandsFactory.factories.put(NetexImporterProcessingCommands.class.getName(),
				new DefaultFactory());
	}

	@Override
	public List<? extends Command> getPreProcessingCommands(Context context) {
		InitialContext initialContext = (InitialContext) context.get(INITIAL_CONTEXT);
		List<Command> commands = new ArrayList<>();
		try {
			commands.add(CommandFactory.create(initialContext, NetexInitImportCommand.class.getName()));
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
