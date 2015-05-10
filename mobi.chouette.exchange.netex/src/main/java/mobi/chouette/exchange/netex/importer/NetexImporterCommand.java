package mobi.chouette.exchange.netex.importer;

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
import mobi.chouette.common.chain.Chain;
import mobi.chouette.common.chain.ChainCommand;
import mobi.chouette.common.chain.Command;
import mobi.chouette.common.chain.CommandFactory;
import mobi.chouette.exchange.ProgressionCommand;
import mobi.chouette.exchange.importer.CleanRepositoryCommand;
import mobi.chouette.exchange.importer.CopyCommand;
import mobi.chouette.exchange.importer.LineRegisterCommand;
import mobi.chouette.exchange.importer.UncompressCommand;
import mobi.chouette.exchange.netex.Constant;
import mobi.chouette.exchange.report.ActionReport;
import mobi.chouette.exchange.report.FileInfo;
import mobi.chouette.exchange.report.FileInfo.FILE_STATE;
import mobi.chouette.exchange.validation.DaoSharedDataValidatorCommand;
import mobi.chouette.exchange.validation.ImportedLineValidatorCommand;
import mobi.chouette.exchange.validation.ValidationData;
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

		// TODO progression
		InitialContext initialContext = (InitialContext) context
				.get(INITIAL_CONTEXT);

		JobData jobData = (JobData) context.get(JOB_DATA);
		context.put(REFERENTIAL, new Referential());

		// initialize reporting and progression
		ProgressionCommand progression = (ProgressionCommand) CommandFactory
				.create(initialContext, ProgressionCommand.class.getName());

		progression.initialize(context,2);

		Object configuration = context.get(CONFIGURATION);
		if (!(configuration instanceof NetexImportParameters)) {
			// fatal wrong parameters
			ActionReport report = (ActionReport) context.get(REPORT);
			log.error("invalid parameters for netex import "
					+ configuration.getClass().getName());
			report.setFailure("invalid parameters for netex import "
					+ configuration.getClass().getName());
			progression.dispose(context);
			return false;
		}

		NetexImportParameters parameters = (NetexImportParameters) configuration;
		int initCount = 2 + (parameters.isCleanRepository()?1:0);
		progression.initialize(context,initCount);
		boolean level3validation = context.get(VALIDATION) != null;
		
		if (level3validation) context.put(VALIDATION_DATA, new ValidationData());

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
			if (!uncompress.execute(context)) {
				return ERROR;
			}
			progression.execute(context);

			// init
			Command initImport = CommandFactory.create(initialContext, NetexInitImportCommand.class.getName());
			initImport.execute(context);
			progression.execute(context);

			Path path = Paths.get(jobData.getPathName(), INPUT);
			List<Path> stream = FileUtil
					.listFiles(path, "*.xml", "*metadata*");

			List<Path> excluded = FileUtil
					.listFiles(path, "*", "*.xml");
			if (!excluded.isEmpty())
			{
				ActionReport report = (ActionReport) context.get(REPORT);
				for (Path exclude : excluded) {
					FileInfo file = new FileInfo();
					file.setName(exclude.getFileName().toString());
					file.setStatus(FILE_STATE.IGNORED);
					report.getFiles().add(file);
				}
			}

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
				if (level3validation)
				{
					// add validation
					Command validate = CommandFactory.create(initialContext,
							ImportedLineValidatorCommand.class.getName());
					chain.add(validate);
				}

			}
			progression.execute(context);
			progression.start(context, stream.size());
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
			Command result = new NetexImporterCommand();
			return result;
		}
	}

	static {
		CommandFactory.factories.put(NetexImporterCommand.class.getName(),
				new DefaultCommandFactory());
	}
}
