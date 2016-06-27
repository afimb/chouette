package mobi.chouette.exchange.neptune.exporter;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import javax.naming.InitialContext;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Color;
import mobi.chouette.common.Context;
import mobi.chouette.common.FileUtil;
import mobi.chouette.common.JobData;
import mobi.chouette.common.chain.Command;
import mobi.chouette.common.chain.CommandFactory;
import mobi.chouette.exchange.ProgressionCommand;
import mobi.chouette.exchange.neptune.Constant;
import mobi.chouette.exchange.neptune.importer.NeptuneDisposeImportCommand;
import mobi.chouette.exchange.neptune.importer.NeptuneImportParameters;
import mobi.chouette.exchange.neptune.importer.NeptuneInitImportCommand;
import mobi.chouette.exchange.neptune.importer.NeptuneParserCommand;
import mobi.chouette.exchange.neptune.importer.NeptuneSAXParserCommand;
import mobi.chouette.exchange.neptune.importer.NeptuneValidationCommand;
import mobi.chouette.exchange.report.ActionReport2;

import com.jamonapi.Monitor;
import com.jamonapi.MonitorFactory;

@Log4j
public class NeptuneValidateExportCommand implements Command, Constant {

	public static final String COMMAND = "NeptuneValidateExportCommand";

	@Override
	public boolean execute(Context context) throws Exception {
		boolean result = ERROR;

		Monitor monitor = MonitorFactory.start(COMMAND);

		try {
			// create specific context
			Context validateContext = new Context();
			validateContext.putAll(context);
			// build parameter
			NeptuneImportParameters parameters = new NeptuneImportParameters();
			NeptuneExportParameters configuration = (NeptuneExportParameters) context.get(CONFIGURATION);
			parameters.setOrganisationName(configuration.getOrganisationName());
			parameters.setUserName(configuration.getUserName());
			parameters.setName(configuration.getName());
			parameters.setNoSave(true);
			parameters.setReferentialName(configuration.getReferentialName());
			validateContext.put(CONFIGURATION, parameters);
			validateContext.put(REPORT, context.get(REPORT));
			// rename output folder to input folder
			JobData jobData = (JobData) context.get(JOB_DATA);
			String pathName = jobData.getPathName();
			File output = new File(pathName, OUTPUT);
			File input = new File(pathName, INPUT);
			if (!output.renameTo(input))
				log.error("rename failed");
			output = new File(pathName, OUTPUT);
			// run gtfs validation preparation
			InitialContext initialContext = (InitialContext) context.get(INITIAL_CONTEXT);
			try {
				Path path = Paths.get(jobData.getPathName(), INPUT);
				// run NeptuneInitImportCommand
				Command c = CommandFactory.create(initialContext, NeptuneInitImportCommand.class.getName());
				c.execute(validateContext);
				// for each neptune file
				List<Path> stream = FileUtil.listFiles(path, "*.xml", "*metadata*");
				for (Path file : stream) {
					// validation schema
					String url = file.toUri().toURL().toExternalForm();
					NeptuneSAXParserCommand schema = (NeptuneSAXParserCommand) CommandFactory.create(initialContext,
							NeptuneSAXParserCommand.class.getName());
					schema.setFileURL(url);
					schema.execute(validateContext);

					// parser
					NeptuneParserCommand parser = (NeptuneParserCommand) CommandFactory.create(initialContext,
							NeptuneParserCommand.class.getName());
					parser.setFileURL(file.toUri().toURL().toExternalForm());
					parser.execute(validateContext);

					// validation
					Command validation = CommandFactory
							.create(initialContext, NeptuneValidationCommand.class.getName());
					validation.execute(validateContext);
					ProgressionCommand.mergeValidationReports(validateContext);
				}
			} catch (Exception ex) {
				log.error("problem in validation" + ex);
			} finally {
				// rename folder to output before dispose
				input.renameTo(output);
				// terminate validation
				Command c = CommandFactory.create(initialContext, NeptuneDisposeImportCommand.class.getName());
				c.execute(validateContext);
			}
			// save report in folder
			context.put(VALIDATION_REPORT, validateContext.get(VALIDATION_REPORT));
			result = SUCCESS;

		} catch (Exception e) {
			log.error(e, e);
			throw e;
		} finally {
			log.info(Color.MAGENTA + monitor.stop() + Color.NORMAL);
		}

		return result;
	}

	public static class DefaultCommandFactory extends CommandFactory {

		@Override
		protected Command create(InitialContext context) throws IOException {
			Command result = new NeptuneValidateExportCommand();
			return result;
		}
	}

	static {
		CommandFactory.factories.put(NeptuneValidateExportCommand.class.getName(), new DefaultCommandFactory());
	}

}
