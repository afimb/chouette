package mobi.chouette.exchange.gtfs.exporter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.ejb.Stateless;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Color;
import mobi.chouette.common.Context;
import mobi.chouette.common.chain.Command;
import mobi.chouette.common.chain.CommandFactory;
import mobi.chouette.exchange.ProgressionCommand;
import mobi.chouette.exchange.exporter.AbstractExporterCommand;
import mobi.chouette.exchange.exporter.CompressCommand;
import mobi.chouette.exchange.exporter.SaveMetadataCommand;
import mobi.chouette.exchange.gtfs.Constant;
import mobi.chouette.exchange.report.ActionError;
import mobi.chouette.exchange.report.ActionReport;
import mobi.chouette.exchange.report.ReportConstant;
import mobi.chouette.model.Line;

import com.jamonapi.Monitor;
import com.jamonapi.MonitorFactory;

@Log4j
@Stateless(name = GtfsExporterCommand.COMMAND)
public class GtfsExporterCommand extends AbstractExporterCommand implements Command, Constant, ReportConstant {

	public static final String COMMAND = "GtfsExporterCommand";

	@Override
	public boolean execute(Context context) throws Exception {
		boolean result = ERROR;
		Monitor monitor = MonitorFactory.start(COMMAND);

		InitialContext initialContext = (InitialContext) context
				.get(INITIAL_CONTEXT);

		// initialize reporting and progression
		ProgressionCommand progression = (ProgressionCommand) CommandFactory
				.create(initialContext, ProgressionCommand.class.getName());
		
		progression.initialize(context,2);

		ActionReport report = (ActionReport) context.get(REPORT);

		// read parameters
		Object configuration = context.get(CONFIGURATION);
		if (!(configuration instanceof GtfsExportParameters)) {
			// fatal wrong parameters
			log.error("invalid parameters for gtfs export "
					+ configuration.getClass().getName());
			report.setResult(STATUS_ERROR);
			report.setFailure(new ActionError(ActionError.CODE.INVALID_PARAMETERS,"invalid parameters for gtfs export "
					+ configuration.getClass().getName()));
			progression.dispose(context);
			return ERROR;
		}

		GtfsExportParameters parameters = (GtfsExportParameters) configuration;
		if (parameters.getStartDate() != null && parameters.getEndDate() != null)
		{
			if (parameters.getStartDate().after(parameters.getEndDate()))
			{
				report.setResult(STATUS_ERROR);
				report.setFailure(new ActionError(ActionError.CODE.INVALID_PARAMETERS,"end date before start date"));
				return ERROR;
				
			}
		}

		String type = parameters.getReferencesType();
		// set default type 
		if (type == null || type.isEmpty() )
		{
			// all lines
			type = "line";
			parameters.setIds(null);
		}
		type=type.toLowerCase();

		try {
			// init
			Command initExport = CommandFactory.create(initialContext, GtfsInitExportCommand.class.getName());
			initExport.execute(context);
			progression.execute(context);

			if (type.equals("stop_area"))
			{
				progression.execute(context);
				progression.start(context, 1);
				Command exportStopArea = CommandFactory.create(initialContext,
						GtfsStopAreaProducerCommand.class.getName());
				result = exportStopArea.execute(context);
				progression.execute(context);
				progression.terminate(context,1);
			}
			else
			{
				List<Long> ids = null;
				if (parameters.getIds() != null) {
					ids = new ArrayList<Long>(parameters.getIds());
				}

				Set<Line> lines = loadLines(type, ids);
				progression.execute(context);
				progression.start(context, lines.size()+1);
				Command exportLine = CommandFactory.create(initialContext,
						DaoGtfsLineProducerCommand.class.getName());

				int lineCount = 0;
				for (Line line : lines) {
					context.put(LINE_ID, line.getId());
					progression.execute(context);
					if (exportLine.execute(context) == ERROR) 
					{
						continue;
					}
					else
					{
						lineCount ++;
					}
				}

				if (lineCount > 0)
				{
					progression.execute(context);
					Command exportSharedData = CommandFactory.create(initialContext,
							GtfsSharedDataProducerCommand.class.getName());
					result = exportSharedData.execute(context);
				}

				// save metadata
				if (parameters.isAddMetadata())
				{
					progression.terminate(context,3);
					Command saveMetadata = CommandFactory.create(initialContext,
							SaveMetadataCommand.class.getName());
					saveMetadata.execute(context);
					progression.execute(context);
				}
				else
				{
					progression.terminate(context,2);
				}
			}
			Command terminateExport = CommandFactory.create(initialContext, GtfsTerminateExportCommand.class.getName());
			terminateExport.execute(context);
			progression.execute(context);
			
			// compress
			Command compress = CommandFactory.create(initialContext,
					CompressCommand.class.getName());
			compress.execute(context);
			progression.execute(context);

		} catch (Exception e) {
			report.setFailure(new ActionError(ActionError.CODE.INTERNAL_ERROR,"Fatal :" + e));
			log.error(e.getMessage(), e);
		} finally {
			progression.dispose(context);
			log.info(Color.YELLOW + monitor.stop() + Color.NORMAL);
		}

		return result;
	}

	public static class DefaultCommandFactory extends CommandFactory {

		@Override
		protected Command create(InitialContext context) throws IOException {
			Command result = null;
			try {
				String name = "java:app/mobi.chouette.exchange.gtfs/"
						+ COMMAND;
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
		CommandFactory.factories.put(GtfsExporterCommand.class.getName(),
				new DefaultCommandFactory());
	}
}
