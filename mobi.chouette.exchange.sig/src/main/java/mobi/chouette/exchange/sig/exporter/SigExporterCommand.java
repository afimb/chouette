package mobi.chouette.exchange.sig.exporter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.ejb.EJB;
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
import mobi.chouette.exchange.DaoReader;
import mobi.chouette.exchange.ProcessingCommands;
import mobi.chouette.exchange.ProcessingCommandsFactory;
import mobi.chouette.exchange.ProgressionCommand;
import mobi.chouette.exchange.exporter.CompressCommand;
import mobi.chouette.exchange.geojson.exporter.GeojsonExportParameters;
import mobi.chouette.exchange.geojson.exporter.GeojsonExporterProcessingCommands;
import mobi.chouette.exchange.kml.exporter.KmlExportParameters;
import mobi.chouette.exchange.kml.exporter.KmlExporterProcessingCommands;
import mobi.chouette.exchange.parameters.AbstractExportParameter;
import mobi.chouette.exchange.report.ActionError;
import mobi.chouette.exchange.report.ActionReport;
import mobi.chouette.exchange.report.FileInfo;
import mobi.chouette.exchange.report.LineInfo;
import mobi.chouette.exchange.report.ReportConstant;

import org.apache.commons.beanutils.BeanUtils;

import com.jamonapi.Monitor;
import com.jamonapi.MonitorFactory;

@Log4j
@Stateless(name = SigExporterCommand.COMMAND)
public class SigExporterCommand implements Command, Constant, ReportConstant {

	public static final String COMMAND = "SigExporterCommand";

	@EJB DaoReader reader;
	
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
			if (!(configuration instanceof SigExportParameters)) {
				// fatal wrong parameters
				log.error("invalid parameters for conversion " + configuration.getClass().getName());
				report.setFailure(new ActionError(ActionError.CODE.INVALID_PARAMETERS,
						"invalid parameters for conversion " + configuration.getClass().getName()));
				return ERROR;
			}

			progression.initialize(context, 1);

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

	public boolean process(Context context, ProgressionCommand progression,
			boolean continueLineProcesingOnError) throws Exception {
		boolean result = ERROR;
		SigExportParameters parameters = (SigExportParameters) context.get(CONFIGURATION);
		ActionReport report = (ActionReport) context.get(REPORT);

		JobData jobData = (JobData) context.get(JOB_DATA);
		jobData.setOutputFilename("export_" + jobData.getType() + "_" + jobData.getId() + ".zip");

		ProcessingCommands kmlCommands = ProcessingCommandsFactory.create(KmlExporterProcessingCommands.class.getName());
		ProcessingCommands geojsonCommands = ProcessingCommandsFactory.create(GeojsonExporterProcessingCommands.class.getName());
		// initialisation
		SigJobData kmlJobData = prepareParameters(context,parameters,"kml",new KmlExportParameters());
		Context kmlContext = prepareContext(context, kmlJobData);
		SigJobData geojsonJobData = prepareParameters(context,parameters,"geojson",new GeojsonExportParameters());
		Context geojsonContext = prepareContext(context, geojsonJobData);
				
		List<? extends Command> preProcessingKmlCommands = kmlCommands.getPreProcessingCommands(kmlContext, true);
		List<? extends Command> preProcessingGeojsonCommands = geojsonCommands.getPreProcessingCommands(geojsonContext, true);
		progression.initialize(context, preProcessingKmlCommands.size() +preProcessingGeojsonCommands.size() + 1);
		for (Command exportCommand : preProcessingKmlCommands) {
			result = exportCommand.execute(kmlContext);
			if (!result) {
				report.setFailure(new ActionError(ActionError.CODE.NO_DATA_FOUND, "no data selected"));
				mergeReports(report,kmlContext);
				progression.execute(context);
				return ERROR;
			}
			mergeReports(report,kmlContext);
			progression.execute(context);
		}
		for (Command exportCommand : preProcessingGeojsonCommands) {
			result = exportCommand.execute(geojsonContext);
			if (!result) {
				report.setFailure(new ActionError(ActionError.CODE.NO_DATA_FOUND, "no data selected"));
				mergeReports(report,geojsonContext);
				progression.execute(context);
				return ERROR;
			}
			mergeReports(report,geojsonContext);
			progression.execute(context);
		}

			// get lines
			String type = parameters.getReferencesType();
			// set default type
			if (type == null || type.isEmpty()) {
				// all lines
				type = "line";
				parameters.setIds(null);
			}
			type = type.toLowerCase();

			List<Long> ids = null;
			if (parameters.getIds() != null) {
				ids = new ArrayList<Long>(parameters.getIds());
			}

			Set<Long> lines = reader.loadLines(type, ids);
			if (lines.isEmpty()) {
				report.setFailure(new ActionError(ActionError.CODE.NO_DATA_FOUND, "no data selected"));
				return ERROR;

			}
			progression.execute(context);

			// process lines
			List<? extends Command> lineProcessingKmlCommands = kmlCommands.getLineProcessingCommands(kmlContext, true);
			List<? extends Command> lineProcessingGeojsonCommands = geojsonCommands.getLineProcessingCommands(kmlContext, true);
			progression.start(context, lines.size());
			int lineCount = 0;
			// export each line
			for (Long line : lines) {
				kmlContext.put(LINE_ID, line);
				geojsonContext.put(LINE_ID, line);
				boolean exportFailed = false;
				for (Command exportCommand : lineProcessingKmlCommands) {
					result = exportCommand.execute(kmlContext);
					if (!result) {
						exportFailed = true;
						break;
					}
				}
				for (Command exportCommand : lineProcessingGeojsonCommands) {
					result = exportCommand.execute(geojsonContext);
					if (!result) {
						exportFailed = true;
						break;
					}
				}
				mergeReports(report,kmlContext);
				mergeReports(report,geojsonContext);
				progression.execute(context);
				if (!exportFailed) {
					lineCount++;
				} else if (!continueLineProcesingOnError) {
					report.setFailure(new ActionError(ActionError.CODE.INVALID_DATA, "unable to export data"));
					return ERROR;
				}
			}
			// check if data where exported
			if (lineCount == 0) {
				mergeReports(report,kmlContext);
				mergeReports(report,geojsonContext);
				progression.terminate(context, 1);
				report.setFailure(new ActionError(ActionError.CODE.NO_DATA_PROCEEDED, "no data exported"));
				progression.execute(context);
				return ERROR;
			}
		// post processing

		List<? extends Command> postProcessingKmlCommands = kmlCommands.getPostProcessingCommands(kmlContext, true);
		List<? extends Command> postProcessingGeojsonCommands = geojsonCommands.getPostProcessingCommands(geojsonContext, true);
		progression.terminate(context, postProcessingKmlCommands.size()+postProcessingGeojsonCommands.size()+1);
		for (Command exportCommand : postProcessingKmlCommands) {
			result = exportCommand.execute(kmlContext);
			if (!result) {
				if (report.getFailure() == null)
					report.setFailure(new ActionError(ActionError.CODE.NO_DATA_PROCEEDED, "no data exported"));
				return ERROR;
			}
			mergeReports(report,kmlContext);
			progression.execute(context);
		}
		for (Command exportCommand : postProcessingGeojsonCommands) {
			result = exportCommand.execute(geojsonContext);
			if (!result) {
				if (report.getFailure() == null)
					report.setFailure(new ActionError(ActionError.CODE.NO_DATA_PROCEEDED, "no data exported"));
				return ERROR;
			}
			mergeReports(report,geojsonContext);
			progression.execute(context);
		}
		
		// compress data
		InitialContext initialContext = (InitialContext) context.get(INITIAL_CONTEXT);
		Command compressCommand = CommandFactory.create(initialContext, CompressCommand.class.getName());
		compressCommand.execute(context);
		progression.execute(context);
		
		return result;
	}

	private void mergeReports(ActionReport report, Context localContext) {
		ActionReport localReport = (ActionReport) localContext.get(REPORT);
		for (FileInfo fileInfo : localReport.getFiles())
		{
			if (!report.getFiles().contains(fileInfo)) report.getFiles().add(fileInfo);
		}
		for (LineInfo lineInfo : localReport.getLines())
		{
			if (!report.getLines().contains(lineInfo)) report.getLines().add(lineInfo);
		}
//        if (localReport.getZip() != null)
//        {
//        	if (!localReport.getZip().equals(report.getZip())) report.setZip(localReport.getZip());
//        }
        // todo : merge stats ?
           report.setStats(localReport.getStats()); 
	}

	private Context prepareContext(Context baseContext, SigJobData jobData) {
		Context context = new Context();
		context.put(REPORT, new ActionReport());
		context.put(JOB_DATA, jobData);
		context.put(CONFIGURATION, jobData.getConfiguration());
		context.put(INITIAL_CONTEXT, baseContext.get(INITIAL_CONTEXT));
		return context;
	}


	private SigJobData prepareParameters(Context context, SigExportParameters configuration, String format, AbstractExportParameter parameters) throws Exception {

		SigJobData data = new SigJobData();
		JobData jobData = (JobData) context.get(JOB_DATA);
		data.setId(jobData.getId());
		data.setPathName(jobData.getPathName()+"/"+OUTPUT+"/"+format);
		data.setAction(EXPORTER);
		data.setType(format);
		
		
		BeanUtils.copyProperties(parameters, configuration );

		data.setConfiguration(parameters);
		return data;

	}

	public static class DefaultCommandFactory extends CommandFactory {

		@Override
		protected Command create(InitialContext context) throws IOException {
			Command result = null;
			try {
				String name = "java:app/mobi.chouette.exchange.sig/" + COMMAND;
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
		CommandFactory.factories.put(SigExporterCommand.class.getName(), new DefaultCommandFactory());
	}
}
