package mobi.chouette.exchange.gtfs.importer;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.naming.InitialContext;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Color;
import mobi.chouette.common.Context;
import mobi.chouette.common.chain.Chain;
import mobi.chouette.common.chain.ChainCommand;
import mobi.chouette.common.chain.Command;
import mobi.chouette.common.chain.CommandFactory;
import mobi.chouette.exchange.ProgressionCommand;
import mobi.chouette.exchange.gtfs.Constant;
import mobi.chouette.exchange.gtfs.model.GtfsRoute;
import mobi.chouette.exchange.gtfs.model.importer.GtfsImporter;
import mobi.chouette.exchange.gtfs.model.importer.Index;
import mobi.chouette.exchange.importer.CopyCommand;
import mobi.chouette.exchange.importer.LineRegisterCommand;
import mobi.chouette.exchange.importer.UncompressCommand;
import mobi.chouette.exchange.validator.report.ValidationReport;
import mobi.chouette.model.util.Referential;

import com.jamonapi.Monitor;
import com.jamonapi.MonitorFactory;

@Log4j
public class GtfsImporterCommand implements Command, Constant {

	public static final String COMMAND = "GtfsImporterCommand";

	@Override
	public boolean execute(Context context) throws Exception {
		boolean result = ERROR;

		Monitor monitor = MonitorFactory.start(COMMAND);

		InitialContext initialContext = (InitialContext) context
				.get(INITIAL_CONTEXT);

		ProgressionCommand progression = (ProgressionCommand) CommandFactory
				.create(initialContext, ProgressionCommand.class.getName());
		progression.initialize(context,3);

		context.put(REFERENTIAL, new Referential());
		
		// report service
		ValidationReport validationReport = new ValidationReport();
		context.put(Constant.VALIDATION_REPORT, validationReport);

        // check params
		Object configuration = context.get(CONFIGURATION);
		if (!(configuration instanceof GtfsImportParameters)) {
			// TODO report service
			log.error(new IllegalArgumentException(configuration.toString()));
			return ERROR;
		}
		GtfsImportParameters parameters = (GtfsImportParameters) configuration;

		GtfsImporter importer = (GtfsImporter) context.get(PARSER);
		if (importer == null) {
			Path path = Paths.get(context.get(PATH).toString(), INPUT);
			importer = new GtfsImporter(path.toString());
			context.put(PARSER, importer);
		}

		try {
			// uncompress data
			Command uncompress = CommandFactory.create(initialContext,
					UncompressCommand.class.getName());
			uncompress.execute(context);
			progression.execute(context);

			// validation
			Command validation = CommandFactory.create(initialContext,
					GtfsValidationCommand.class.getName());
			validation.execute(context);
			progression.execute(context);

			ChainCommand master = (ChainCommand) CommandFactory.create(
					initialContext, ChainCommand.class.getName());
			master.setIgnored(true);

			Index<GtfsRoute> index = importer.getRouteById();
			
			for (GtfsRoute gtfsRoute : index) {
				
				Chain chain = (Chain) CommandFactory.create(initialContext,
						ChainCommand.class.getName());
				master.add(chain);

				chain.add(progression);

				// parser
				GtfsParserCommand parser = (GtfsParserCommand) CommandFactory
						.create(initialContext,
								GtfsParserCommand.class.getName());
				parser.setGtfsRouteId(gtfsRoute.getRouteId());
				
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
			progression.execute(context);
			progression.start(context, index.getLength());
			master.execute(context);
			progression.terminate(context,1);
			progression.execute(context);
			result = SUCCESS;

		} catch (Exception e) {
			// TODO report service
		} finally {
			progression.dispose(context);
			// TODO report service
		}

		log.info(Color.YELLOW + monitor.stop() + Color.NORMAL);
		return result;
	}

	public static class DefaultCommandFactory extends CommandFactory {

		@Override
		protected Command create(InitialContext context) throws IOException {
			Command result = new GtfsImporterCommand();
			return result;
		}
	}

	static {
		CommandFactory.factories.put(GtfsImporterCommand.class.getName(),
				new DefaultCommandFactory());
	}
}
