package mobi.chouette.exchange.converter;

import java.io.IOException;
import java.util.List;

import javax.ejb.Stateless;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Color;
import mobi.chouette.common.Constant;
import mobi.chouette.common.Context;
import mobi.chouette.common.JobData;
import mobi.chouette.common.chain.Command;
import mobi.chouette.common.chain.CommandFactory;
import mobi.chouette.exchange.CommandCancelledException;
import mobi.chouette.exchange.ProcessingCommands;
import mobi.chouette.exchange.ProcessingCommandsFactory;
import mobi.chouette.exchange.ProgressionCommand;
import mobi.chouette.exchange.gtfs.exporter.GtfsExportParameters;
import mobi.chouette.exchange.gtfs.importer.GtfsImportParameters;
import mobi.chouette.exchange.neptune.exporter.NeptuneExportParameters;
import mobi.chouette.exchange.neptune.importer.NeptuneImportParameters;
import mobi.chouette.exchange.netex.exporter.NetexExportParameters;
import mobi.chouette.exchange.netex.importer.NetexImportParameters;
import mobi.chouette.exchange.parameters.AbstractExportParameter;
import mobi.chouette.exchange.parameters.AbstractImportParameter;
import mobi.chouette.exchange.report.ActionError;
import mobi.chouette.exchange.report.ActionReport;
import mobi.chouette.exchange.report.FileInfo;
import mobi.chouette.exchange.report.IO_TYPE;
import mobi.chouette.exchange.report.LineInfo;
import mobi.chouette.exchange.report.ReportConstant;
import mobi.chouette.exchange.validation.ValidationData;
import mobi.chouette.exchange.validation.parameters.ValidationParameters;
import mobi.chouette.exchange.validation.report.ValidationReport;
import mobi.chouette.model.Line;
import mobi.chouette.model.util.Referential;

import org.apache.commons.lang.StringUtils;

import com.jamonapi.Monitor;
import com.jamonapi.MonitorFactory;

@Log4j
@Stateless(name = ConverterCommand.COMMAND)
public class ConverterCommand implements Command, Constant, ReportConstant {

	public static final String COMMAND = "ConverterCommand";

	@Override
	public boolean execute(Context context) throws Exception {
		boolean result = ERROR;
		Monitor monitor = MonitorFactory.start(COMMAND);

		InitialContext initialContext = (InitialContext) context.get(INITIAL_CONTEXT);
		ActionReport report = (ActionReport) context.get(REPORT);

		// initialize reporting and progression
		ProgressionCommand progression = (ProgressionCommand) CommandFactory.create(initialContext,
				ProgressionCommand.class.getName());
		try {

			// read parameters
			Object configuration = context.get(CONFIGURATION);
			if (!(configuration instanceof ConvertParameters)) {
				// fatal wrong parameters
				log.error("invalid parameters for conversion " + configuration.getClass().getName());
				report.setFailure(new ActionError(ActionError.CODE.INVALID_PARAMETERS,
						"invalid parameters for conversion " + configuration.getClass().getName()));
				return ERROR;
			}

			progression.initialize(context, 1);
			context.put(VALIDATION_DATA, new ValidationData());

			result = process(context, progression, false);

		} catch (CommandCancelledException e) {
			report.setFailure(new ActionError(ActionError.CODE.INTERNAL_ERROR, "Command cancelled"));
			log.error(e.getMessage());
		} catch (Exception e) {
			report.setFailure(new ActionError(ActionError.CODE.INTERNAL_ERROR, "Fatal :" + e));
			log.error(e.getMessage(), e);
		} finally {
			progression.dispose(context);
			log.info(Color.YELLOW + monitor.stop() + Color.NORMAL);
		}

		return result;
	}

	private boolean process(Context context, ProgressionCommand progression, boolean continueLineProcesingOnError)
			throws Exception {

		boolean result = ERROR;
		ConvertParameters parameters = (ConvertParameters) context.get(CONFIGURATION);
		ActionReport report = (ActionReport) context.get(REPORT);

		// initialisation
		AbstractImportParameter importConfiguration = parameters.getImportConfiguration();
		importConfiguration.setNoSave(true);
		AbstractExportParameter exportConfiguration = parameters.getExportConfiguration();
		ConverterJobData importData = loadInputParameters(context, importConfiguration);
		ConverterJobData exportData = loadOutputParameters(context, exportConfiguration);

		ProcessingCommands importProcessingCommands = ProcessingCommandsFactory
				.create(buildCommandProcessingClassName(importData));
		ProcessingCommands exportProcessingCommands = ProcessingCommandsFactory
				.create(buildCommandProcessingClassName(exportData));

		ValidationParameters validationParameters = (ValidationParameters) context.get(VALIDATION);
		Context importContext = prepareImportContext(importData, validationParameters);
		context.put(VALIDATION_REPORT, importContext.get(VALIDATION_REPORT));
		Context exportContext = prepareExportContext(exportData);

		try {
			List<? extends Command> preProcessingImportCommands = importProcessingCommands.getPreProcessingCommands(
					importContext, false);
			List<? extends Command> preProcessingExportCommands = exportProcessingCommands.getPreProcessingCommands(
					exportContext, false);
			progression
					.initialize(context, preProcessingImportCommands.size() + preProcessingExportCommands.size() + 1);
			for (Command command : preProcessingImportCommands) {
				result = command.execute(importContext);
				mergeReports(report, importContext, IO_TYPE.INPUT);
				if (!result) {
					report.setFailure(new ActionError(ActionError.CODE.NO_DATA_FOUND, "no data to import"));
					progression.execute(context);
					return ERROR;
				}
				progression.execute(context);
			}
			for (Command command : preProcessingExportCommands) {
				result = command.execute(exportContext);
				mergeReports(report, exportContext, IO_TYPE.OUTPUT);
				if (!result) {
					report.setFailure(new ActionError(ActionError.CODE.NO_DATA_FOUND, "no data selected"));
					progression.execute(context);
					return ERROR;
				}
				progression.execute(context);
			}
			progression.execute(context);
			// process lines
			List<? extends Command> lineImportProcessingCommands = importProcessingCommands.getLineProcessingCommands(
					importContext, false);
			List<? extends Command> lineExportProcessingCommands = exportProcessingCommands.getLineProcessingCommands(
					exportContext, false);
			progression.start(context, lineImportProcessingCommands.size());
			long id = 0;
			long lineCount = 0;
			for (Command importCommand : lineImportProcessingCommands) {
				boolean exportFailed = false;
				result = importCommand.execute(importContext);
				mergeReports(report, importContext, IO_TYPE.INPUT);
				if (!result) {
					log.error("fail to execute " + importCommand.getClass().getName());
					progression.execute(context);
					importContext.put(VALIDATION_REPORT, context.get(VALIDATION_REPORT));
					continue;
				}
				// execute export commands

				// - get line in import context
				Referential referential = (Referential) importContext.get(REFERENTIAL);
				if (referential.getLines().isEmpty())
				{
					progression.execute(context);
					importContext.put(VALIDATION_REPORT, context.get(VALIDATION_REPORT));
					continue;
				}
				Line line = referential.getLines().values().iterator().next();
				// some export uses Id as file name
				line.setId(++id);
				// - put line in export context
				exportContext.put(LINE, line);
				// execute commands
				for (Command exportCommand : lineExportProcessingCommands) {
					result = exportCommand.execute(exportContext);
					mergeReports(report, exportContext, IO_TYPE.OUTPUT);
					if (!result) {
						exportFailed = true;
						log.error("fail to execute " + exportCommand.getClass().getName());
						break;
					}
				}
				if (!exportFailed)
					lineCount++;
				progression.execute(context);
				importContext.put(VALIDATION_REPORT, context.get(VALIDATION_REPORT));

			}

			// post-processing

			List<? extends Command> postImportProcessingCommands = importProcessingCommands.getPostProcessingCommands(
					importContext, false);
			List<? extends Command> postExportProcessingCommands = exportProcessingCommands.getPostProcessingCommands(
					exportContext, false);
			// check if data where exported

			if (lineCount == 0) {
				progression.terminate(context, postImportProcessingCommands.size());
				// restore input filename for link 
				// jobData.setFilename(importData.getFilename());
			} else {
				progression.terminate(context,
						postImportProcessingCommands.size() + postExportProcessingCommands.size());
			}
			// input post processing
			for (Command importCommand : postImportProcessingCommands) {
				result = importCommand.execute(importContext);
				mergeReports(report, importContext, IO_TYPE.INPUT);
				if (!result) {
					log.error("fail to execute " + importCommand.getClass().getName());
				}
				progression.execute(context);
			}
			if (lineCount == 0) {
				report.setFailure(new ActionError(ActionError.CODE.NO_DATA_PROCEEDED, "no data exported"));
				progression.execute(context);
				
				return ERROR;
			} else {
				for (Command exportCommand : postExportProcessingCommands) {
					result = exportCommand.execute(exportContext);
					mergeReports(report, exportContext, IO_TYPE.OUTPUT);
					if (!result) {
						log.error("fail to execute " + exportCommand.getClass().getName());
						break;
					}
					progression.execute(context);
				}

			}
		} finally {

			// dispose commands
			for (Command importCommand : importProcessingCommands.getDisposeCommands(importContext, false)) {
				result = importCommand.execute(importContext);
				if (!result) {
					log.error("fail to execute " + importCommand.getClass().getName());
					break;
				}
			}
			importContext.remove(CACHE);

			for (Command exportCommand : exportProcessingCommands.getDisposeCommands(exportContext, false)) {
				result = exportCommand.execute(exportContext);
				if (!result) {
					log.error("fail to execute " + exportCommand.getClass().getName());
					break;
				}

			}
		}
		return result;
	}

	private void mergeReports(ActionReport report, Context localContext, IO_TYPE ioType) {
		ActionReport localReport = (ActionReport) localContext.get(REPORT);
		for (FileInfo fileInfo : localReport.getFiles())
		{
			fileInfo.setIoType(ioType);
			if (!report.getFiles().contains(fileInfo)) report.getFiles().add(fileInfo);
		}
		for (LineInfo lineInfo : localReport.getLines())
		{
			lineInfo.setIoType(ioType);
			if (!report.getLines().contains(lineInfo)) report.getLines().add(lineInfo);
		}
        if (localReport.getZip() != null)
        {
        	localReport.getZip().setIoType(ioType);
        	if (!localReport.getZip().equals(report.getZip())) report.setZip(localReport.getZip());
        }
        if (ioType.equals(IO_TYPE.OUTPUT) && report.getStats().getLineCount() == 0)
           report.setStats(localReport.getStats()); 
	}

	private Context prepareImportContext(ConverterJobData importJobData, ValidationParameters validationParameters) {
		Context context = new Context();
		context.put(REPORT, new ActionReport());
		context.put(JOB_DATA, importJobData);
		context.put(CONFIGURATION, importJobData.getConfiguration());
		if (validationParameters != null)
			context.put(VALIDATION, validationParameters);
		context.put(VALIDATION_REPORT, new ValidationReport());
		return context;
	}

	private Context prepareExportContext(ConverterJobData exportJobData) {
		Context context = new Context();
		context.put(REPORT, new ActionReport());
		context.put(JOB_DATA, exportJobData);
		context.put(CONFIGURATION, exportJobData.getConfiguration());
		return context;
	}

	private ConverterJobData loadInputParameters(Context context, AbstractImportParameter configuration) {

		ConverterJobData data = new ConverterJobData();
		JobData jobData = (JobData) context.get(JOB_DATA);
		data.setId(jobData.getId());
		data.setInputFilename(jobData.getInputFilename());
		data.setPathName(jobData.getPathName());
		data.setAction(IMPORTER);
		if (configuration instanceof NeptuneImportParameters) {
			data.setType("neptune");
		} else if (configuration instanceof NetexImportParameters) {
			data.setType("netex");
		} else if (configuration instanceof GtfsImportParameters) {
			data.setType("gtfs");
			// force import mode to lines
			GtfsImportParameters importConfiguration = (GtfsImportParameters) configuration;
			importConfiguration.setReferencesType("line");
		} else {
			log.error("invalid input options type " + configuration.getClass().getName());
			return null;
		}

		data.setConfiguration(configuration);
		return data;

	}

	private ConverterJobData loadOutputParameters(Context context, AbstractExportParameter configuration) {

		ConverterJobData data = new ConverterJobData();
		JobData jobData = (JobData) context.get(JOB_DATA);
		data.setId(jobData.getId());
		data.setPathName(jobData.getPathName());
		data.setAction(EXPORTER);
		if (configuration instanceof NeptuneExportParameters) {
			data.setType("neptune");
		} else if (configuration instanceof NetexExportParameters) {
			data.setType("netex");
		} else if (configuration instanceof GtfsExportParameters) {
			data.setType("gtfs");
		} else {
			System.err.println("invalid output options type" + configuration.getClass().getName());
			return null;
		}
		jobData.setOutputFilename("export_" + data.getType() + "_" + jobData.getId() + ".zip");
		// force export mode to lines
		AbstractExportParameter exportConfiguration = (AbstractExportParameter) configuration;
		exportConfiguration.setReferencesType("line");

		data.setConfiguration(configuration);
		return data;

	}

	private String buildCommandProcessingClassName(ConverterJobData data) {

		return "mobi.chouette.exchange." + data.getType() + "." + data.getAction() + "."
				+ StringUtils.capitalize(data.getType()) + StringUtils.capitalize(data.getAction())
				+ "ProcessingCommands";
	}

	public static class DefaultCommandFactory extends CommandFactory {

		@Override
		protected Command create(InitialContext context) throws IOException {
			Command result = null;
			try {
				String name = "java:app/mobi.chouette.exchange.converter/" + COMMAND;
				result = (Command) context.lookup(name);
			} catch (NamingException e) {
				// try another way on test context
				String name = "java:module/" + COMMAND;
				try {
					result = (Command) context.lookup(name);
				} catch (NamingException e1) {
					log.error(e);
				}
			}
			return result;
		}
	}

	static {
		CommandFactory.factories.put(ConverterCommand.class.getName(), new DefaultCommandFactory());
	}
}
