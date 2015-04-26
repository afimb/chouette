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
import mobi.chouette.exchange.importer.CleanRepositoryCommand;
import mobi.chouette.exchange.importer.CopyCommand;
import mobi.chouette.exchange.importer.LineRegisterCommand;
import mobi.chouette.exchange.importer.StopAreaRegisterCommand;
import mobi.chouette.exchange.importer.UncompressCommand;
import mobi.chouette.exchange.report.ActionReport;
import mobi.chouette.exchange.validation.DaoSharedDataValidatorCommand;
import mobi.chouette.exchange.validation.ValidationData;
import mobi.chouette.exchange.validation.report.ValidationReport;
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
			// fatal wrong parameters
			ActionReport report = (ActionReport) context.get(REPORT);
			log.error("invalid parameters for gtfs import "
					+ configuration.getClass().getName());
			report.setFailure("invalid parameters for gtfs import "
					+ configuration.getClass().getName());
			progression.dispose(context);
			return ERROR;
		}
		GtfsImportParameters parameters = (GtfsImportParameters) configuration;
		if (parameters.getReferencesType() == null || parameters.getReferencesType().isEmpty())
		{
			parameters.setReferencesType("all");
		}
		boolean all = !(parameters.getReferencesType().equalsIgnoreCase("stoparea"));
		boolean level3validation = context.get(VALIDATION) != null;
		int initCount = 2 + (parameters.isCleanRepository()?1:0);
		progression.initialize(context,initCount);
		
		if (level3validation) context.put(VALIDATION_DATA, new ValidationData());

		GtfsImporter importer = (GtfsImporter) context.get(PARSER);
		if (importer == null) {
			Path path = Paths.get(context.get(PATH).toString(), INPUT);
			importer = new GtfsImporter(path.toString());
			context.put(PARSER, importer);
		}

		try {
			// clean repository if asked
			if (parameters.isCleanRepository())
			{
				Command clean = CommandFactory.create(initialContext,
						CleanRepositoryCommand.class.getName());
				clean.execute(context);
				progression.execute(context);
			}

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
			
			if (all)
			{

			ChainCommand master = (ChainCommand) CommandFactory.create(
					initialContext, ChainCommand.class.getName());
			master.setIgnored(true);

			Index<GtfsRoute> index = importer.getRouteById();
			progression.start(context, index.getLength());
			
			for (GtfsRoute gtfsRoute : index) {
				
				Chain chain = (Chain) CommandFactory.create(initialContext,
						ChainCommand.class.getName());
				master.add(chain);

				chain.add(progression);

				// parser
				GtfsRouteParserCommand parser = (GtfsRouteParserCommand) CommandFactory
						.create(initialContext,
								GtfsRouteParserCommand.class.getName());
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
			progression.terminate(context,level3validation?2:1);
			if (level3validation)
			{
			    // add shared data validation
				Command validate = CommandFactory.create(initialContext,
						DaoSharedDataValidatorCommand.class.getName());
				validate.execute(context);
				progression.execute(context);

			}
			progression.execute(context);
			result = SUCCESS;
			}
			else
			{
				// process stoparea inport
				progression.start(context, 1);
				// parser
				GtfsStopParserCommand parser = (GtfsStopParserCommand) CommandFactory
						.create(initialContext,
								GtfsStopParserCommand.class.getName());
				parser.execute(context);
				
				if (!parameters.isNoSave()) {
					Command register = CommandFactory.create(initialContext,
							StopAreaRegisterCommand.class.getName());
					register.execute(context);
				}
				progression.execute(context);
				progression.terminate(context,1);
//				if (level3validation) when possibility to validate areas only ?
//				{
//				    // add shared data validation
//					Command validate = CommandFactory.create(initialContext,
//							DaoSharedDataValidatorCommand.class.getName());
//					validate.execute(context);
//					progression.execute(context);
//
//				}				progression.execute(context);
				result = SUCCESS;
				
				
			}

		} catch (Exception e) {
			ActionReport report = (ActionReport) context.get(REPORT);
			log.error(e);
			report.setFailure("Fatal :"+e);
		} finally {
			progression.dispose(context);
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
