package mobi.chouette.exchange.netex.importer;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import javax.naming.InitialContext;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Color;
import mobi.chouette.common.Context;
import mobi.chouette.common.FileUtils;
import mobi.chouette.common.chain.Chain;
import mobi.chouette.common.chain.ChainCommand;
import mobi.chouette.common.chain.Command;
import mobi.chouette.common.chain.CommandFactory;
import mobi.chouette.exchange.importer.CopyCommand;
import mobi.chouette.exchange.importer.LineRegisterCommand;
import mobi.chouette.exchange.importer.UncompressCommand;
import mobi.chouette.exchange.netex.Constant;
import mobi.chouette.exchange.report.Report;
import mobi.chouette.model.util.Referential;

import com.jamonapi.Monitor;
import com.jamonapi.MonitorFactory;

@Log4j
public class NetexImporterCommand implements Command, Constant {

	public static final String COMMAND = "NetextImporterCommand";

	@Override
	public boolean execute(Context context) throws Exception {
		boolean result = SUCCESS;
		Monitor monitor = MonitorFactory.start(COMMAND);

		InitialContext initialContext = (InitialContext) context
				.get(INITIAL_CONTEXT);

		context.put(REFERENTIAL, new Referential());

		context.put(REPORT, new Report());

		Object configuration = context.get(CONFIGURATION);
		if (!(configuration instanceof NetexImportParameters)) {

			return ERROR;
		}

		NetexImportParameters parameters = (NetexImportParameters) configuration;

		try {

			// uncompress data
			Command uncompress = CommandFactory.create(initialContext,
					UncompressCommand.class.getName());
			uncompress.execute(context);

			Path path = Paths.get(context.get(PATH).toString(), INPUT);
			List<Path> stream = FileUtils
					.listFiles(path, "*.xml", "*metadata*");

			ChainCommand master = (ChainCommand) CommandFactory.create(
					initialContext, ChainCommand.class.getName());
			master.setIgnored(true);

			for (Path file : stream) {

				Chain chain = (Chain) CommandFactory.create(initialContext,
						ChainCommand.class.getName());
				master.add(chain);

				// validation schema
				String url = file.toUri().toURL().toExternalForm();
				NetexSAXParserCommand schema = (NetexSAXParserCommand) CommandFactory
						.create(initialContext,
								NetexSAXParserCommand.class.getName());
				schema.setFileURL(url);
				chain.add(schema);

				// parser
				NetexParserCommand parser = (NetexParserCommand) CommandFactory
						.create(initialContext,
								NetexParserCommand.class.getName());
				parser.setFileURL(file.toUri().toURL().toExternalForm());
				chain.add(parser);

				if (!parameters.isNoSave()) {

					// register
					Command register = CommandFactory.create(initialContext,
							LineRegisterCommand.class.getName());
					chain.add(register);

					Command copy = CommandFactory.create(initialContext,
							CopyCommand.class.getName());
					chain.add(copy);
				}

			}
			master.execute(context);

		} catch (Exception e) {

			log.error(e);

		} finally {
		}

		log.info(Color.YELLOW + monitor.stop() + Color.NORMAL);
		return result;
	}

	public static class DefaultCommandFactory extends CommandFactory {

		@Override
		protected Command create(InitialContext context) throws IOException {
			Command result = new NetexImporterCommand();
			return result;
		}
	}

	static {
		CommandFactory.factories.put(NetexImporterCommand.class.getName(),
				new DefaultCommandFactory());
	}
}
