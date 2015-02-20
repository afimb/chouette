package mobi.chouette.exchange.neptune.importer;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import javax.naming.InitialContext;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Color;
import mobi.chouette.common.Constant;
import mobi.chouette.common.Context;
import mobi.chouette.common.FileUtils;
import mobi.chouette.common.chain.Chain;
import mobi.chouette.common.chain.ChainCommand;
import mobi.chouette.common.chain.Command;
import mobi.chouette.common.chain.CommandFactory;
import mobi.chouette.exchange.ProgressionCommand;
import mobi.chouette.exchange.importer.CopyCommand;
import mobi.chouette.exchange.importer.LineRegisterCommand;
import mobi.chouette.exchange.importer.UncompressCommand;
import mobi.chouette.exchange.report.Report;
import mobi.chouette.exchange.report.ReportCommand;
import mobi.chouette.exchange.validation.report.ValidationReport;

import com.jamonapi.Monitor;
import com.jamonapi.MonitorFactory;

@Log4j
public class NeptuneImporterCommand implements Command, Constant {

	public static final String COMMAND = "NeptuneImporterCommand";

	@Override
	public boolean execute(Context context) throws Exception {
		boolean result = SUCCESS;
		Monitor monitor = MonitorFactory.start(COMMAND);

		InitialContext initialContext = (InitialContext) context
				.get(INITIAL_CONTEXT);

		// TODO report service
		Command reportCmd = CommandFactory.create(initialContext,
				ReportCommand.class.getName());
		Report report = new Report();
		ValidationReport validationReport = new ValidationReport();
		context.put(Constant.REPORT, report);
		context.put(Constant.VALIDATION_REPORT, validationReport);
		// read parameters
		Object configuration = context.get(CONFIGURATION);
		if (!(configuration instanceof NeptuneImportParameters)) {
			// fatal wrong parameters
			log.error("invalid parameters for neptune import "
					+ configuration.getClass().getName());
			report.setFailure("invalid parameters for neptune import "
					+ configuration.getClass().getName());
			reportCmd.execute(context);
			return false;
		}

		NeptuneImportParameters parameters = (NeptuneImportParameters) configuration;

		ProgressionCommand progression = (ProgressionCommand) CommandFactory
				.create(initialContext, ProgressionCommand.class.getName());

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

				chain.add(progression);

				// validation schema
				String url = file.toUri().toURL().toExternalForm();
				NeptuneSAXParserCommand schema = (NeptuneSAXParserCommand) CommandFactory
						.create(initialContext,
								NeptuneSAXParserCommand.class.getName());
				schema.setFileURL(url);
				chain.add(schema);

				// parser
				NeptuneParserCommand parser = (NeptuneParserCommand) CommandFactory
						.create(initialContext,
								NeptuneParserCommand.class.getName());
				parser.setFileURL(file.toUri().toURL().toExternalForm());
				chain.add(parser);

				// validation
				Command validation = CommandFactory.create(initialContext,
						NeptuneValidationCommand.class.getName());
				chain.add(validation);

				if (parameters.getNoSave().equals(Boolean.FALSE)) {

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

		} finally {
			// TODO report service

			// save report
			report.setProgression(null);
			reportCmd.execute(context);

			// save validation report
			// validationReportCmd.execute(context);

		}

		log.info(Color.YELLOW + monitor.stop() + Color.NORMAL);
		return result;
	}

	public static class DefaultCommandFactory extends CommandFactory {

		@Override
		protected Command create(InitialContext context) throws IOException {
			Command result = new NeptuneImporterCommand();
			return result;
		}
	}

	static {
		CommandFactory.factories.put(NeptuneImporterCommand.class.getName(),
				new DefaultCommandFactory());
	}
}
