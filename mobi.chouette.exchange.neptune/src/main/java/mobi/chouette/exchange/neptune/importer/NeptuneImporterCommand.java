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
import mobi.chouette.common.FileUtil;
import mobi.chouette.common.JobData;
import mobi.chouette.common.chain.Chain;
import mobi.chouette.common.chain.ChainCommand;
import mobi.chouette.common.chain.Command;
import mobi.chouette.common.chain.CommandFactory;
import mobi.chouette.exchange.ProgressionCommand;
import mobi.chouette.exchange.importer.CleanRepositoryCommand;
import mobi.chouette.exchange.importer.CopyCommand;
import mobi.chouette.exchange.importer.LineRegisterCommand;
import mobi.chouette.exchange.importer.UncompressCommand;
import mobi.chouette.exchange.report.ActionReport;
import mobi.chouette.exchange.report.FileInfo;
import mobi.chouette.exchange.report.FileInfo.FILE_STATE;
import mobi.chouette.exchange.report.ReportConstant;
import mobi.chouette.exchange.validation.DaoSharedDataValidatorCommand;
import mobi.chouette.exchange.validation.ImportedLineValidatorCommand;
import mobi.chouette.exchange.validation.ValidationData;
import mobi.chouette.model.util.Referential;

import com.jamonapi.Monitor;
import com.jamonapi.MonitorFactory;

@Log4j
public class NeptuneImporterCommand implements Command, Constant, ReportConstant {

	public static final String COMMAND = "NeptuneImporterCommand";

	@Override
	public boolean execute(Context context) throws Exception {
		boolean result = ERROR;
		Monitor monitor = MonitorFactory.start(COMMAND);

		InitialContext initialContext = (InitialContext) context.get(INITIAL_CONTEXT);

		ActionReport report = (ActionReport) context.get(REPORT);
		
		JobData jobData = (JobData) context.get(JOB_DATA);

		// initialize reporting and progression
		ProgressionCommand progression = (ProgressionCommand) CommandFactory.create(initialContext,
				ProgressionCommand.class.getName());

		progression.initialize(context, 2);
		context.put(REFERENTIAL, new Referential());

		// read parameters
		Object configuration = context.get(CONFIGURATION);
		if (!(configuration instanceof NeptuneImportParameters)) {
			// fatal wrong parameters

			log.error("invalid parameters for neptune import " + configuration.getClass().getName());
			report.setFailure("invalid parameters for neptune import " + configuration.getClass().getName());
			progression.dispose(context);
			return ERROR;
		}

		NeptuneImportParameters parameters = (NeptuneImportParameters) configuration;
		int initCount = 2 + (parameters.isCleanRepository() ? 1 : 0);
		progression.initialize(context, initCount);

		boolean level3validation = context.get(VALIDATION) != null;

		if (level3validation)
			context.put(VALIDATION_DATA, new ValidationData());

		try {

			// clean repository if asked
			if (parameters.isCleanRepository()) {
				Command clean = CommandFactory.create(initialContext, CleanRepositoryCommand.class.getName());
				clean.execute(context);
				progression.execute(context);
			}

			// uncompress data
			Command uncompress = CommandFactory.create(initialContext, UncompressCommand.class.getName());
			if (!uncompress.execute(context)) {
				return ERROR;
			}

			progression.execute(context);

			Path path = Paths.get(jobData.getPath(), INPUT);
			List<Path> stream = FileUtil.listFiles(path, "*.xml", "*metadata*");

			List<Path> excluded = FileUtil.listFiles(path, "*", "*.xml");
			if (!excluded.isEmpty()) {
				for (Path exclude : excluded) {
					FileInfo file = new FileInfo();
					file.setName(exclude.getFileName().toString());
					file.setStatus(FILE_STATE.UNCHECKED);
					report.getFiles().add(file);
				}
			}

			ChainCommand master = (ChainCommand) CommandFactory.create(initialContext, ChainCommand.class.getName());
			master.setIgnored(true);

			for (Path file : stream) {
				Chain chain = (Chain) CommandFactory.create(initialContext, ChainCommand.class.getName());
				master.add(chain);

				chain.add(progression);

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

				if (!parameters.isNoSave()) {

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
			progression.execute(context);
			progression.start(context, stream.size());
			master.execute(context);

			progression.terminate(context, level3validation ? 2 : 1);
			if (level3validation) {
				// add shared data validation
				Command validate = CommandFactory.create(initialContext, DaoSharedDataValidatorCommand.class.getName());
				validate.execute(context);
				progression.execute(context);

			}
			progression.execute(context);
			if (report.getLines().size() == 0) {
				report.setResult(STATUS_ERROR);
				if (report.getFailure() == null)
					report.setFailure("no data");
			}
			result = SUCCESS;

		} catch (Exception e) {
			log.error("Fatal :", e);
			report.setResult(STATUS_ERROR);
			report.setFailure("Fatal :" + e);

		} finally {
			progression.dispose(context);
			log.info(Color.YELLOW + monitor.stop() + Color.NORMAL);
		}

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
		CommandFactory.factories.put(NeptuneImporterCommand.class.getName(), new DefaultCommandFactory());
	}
}
