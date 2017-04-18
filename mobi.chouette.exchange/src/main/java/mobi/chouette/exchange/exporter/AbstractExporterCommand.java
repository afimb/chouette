package mobi.chouette.exchange.exporter;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.ejb.EJB;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Constant;
import mobi.chouette.common.Context;
import mobi.chouette.common.chain.Command;
import mobi.chouette.exchange.DaoReader;
import mobi.chouette.exchange.ProcessingCommands;
import mobi.chouette.exchange.ProgressionCommand;
import mobi.chouette.exchange.parameters.AbstractExportParameter;
import mobi.chouette.exchange.report.ActionReporter;

@Log4j
public class AbstractExporterCommand implements Constant {

	@EJB DaoReader reader;
	
	protected enum Mode {
		line, stopareas
	}

	public boolean process(Context context, ProcessingCommands commands, ProgressionCommand progression,
			boolean continueLineProcesingOnError, Mode mode) throws Exception {
		boolean result = ERROR;
		boolean disposeResult = SUCCESS;
		AbstractExportParameter parameters = (AbstractExportParameter) context.get(CONFIGURATION);
		ActionReporter reporter = ActionReporter.Factory.getInstance();

		try {
			// initialisation
			List<? extends Command> preProcessingCommands = commands.getPreProcessingCommands(context, true);
			progression.initialize(context, preProcessingCommands.size() + (mode.equals(Mode.line) ? 1 : 0));
			for (Command exportCommand : preProcessingCommands) {
                result = exportCommand.execute(context);
                if (!result) {
                    reporter.setActionError(context, ActionReporter.ERROR_CODE.NO_DATA_FOUND, "no data selected");
                    progression.execute(context);
                    return ERROR;
                }
                progression.execute(context);
            }

			if (mode.equals(Mode.line)) {
                String type = parameters.getReferencesType();
                if (type == null || type.isEmpty()) {
                    type = "line";
                    parameters.setIds(null);
                }
                type = type.toLowerCase();

                List<Long> ids = null;
                if (parameters.getIds() != null) {
                    ids = new ArrayList<>(parameters.getIds());
                }

                Set<Long> lines = reader.loadLines(type, ids);
                if (lines.isEmpty()) {
                    reporter.setActionError(context, ActionReporter.ERROR_CODE.NO_DATA_FOUND, "no data selected");
                    return ERROR;

                }
                progression.execute(context);

                // process lines
                List<? extends Command> lineProcessingCommands = commands.getLineProcessingCommands(context, true);
                progression.start(context, lines.size());
                int lineCount = 0;

                // export each line
                for (Long line : lines) {
                    context.put(LINE_ID, line);
                    boolean exportFailed = false;
                    for (Command exportCommand : lineProcessingCommands) {
                        result = exportCommand.execute(context);
                        if (!result) {
                            exportFailed = true;
                            break;
                        }
                    }
                    progression.execute(context);
                    if (!exportFailed) {
                        lineCount++;
                    } else if (!continueLineProcesingOnError) {
                        reporter.setActionError(context, ActionReporter.ERROR_CODE.INVALID_DATA, "unable to export data");
                        return ERROR;
                    }
                }

                // check if data where exported
                if (lineCount == 0) {
                    progression.terminate(context, 1);
                    reporter.setActionError(context, ActionReporter.ERROR_CODE.NO_DATA_PROCEEDED, "no data exported");
                    progression.execute(context);
                    return ERROR;
                }
            } else // stopareas
            {
				// get stop info
                List<? extends Command> stopProcessingCommands = commands.getStopAreaProcessingCommands(context, true);
                progression.start(context, stopProcessingCommands.size());
                for (Command command : stopProcessingCommands) {
                    result = command.execute(context);
                    if (!result) {
                        return ERROR;
                    }
                    progression.execute(context);
                }
            }
			// post processing

			List<? extends Command> postProcessingCommands = commands.getPostProcessingCommands(context, true);
			progression.terminate(context, postProcessingCommands.size());
			for (Command exportCommand : postProcessingCommands) {
                result = exportCommand.execute(context);
                if (!result) {
                    if (!reporter.hasActionError(context))
                        reporter.setActionError(context, ActionReporter.ERROR_CODE.NO_DATA_PROCEEDED, "no data exported");
                    return ERROR;
                }
                progression.execute(context);
            }
		} finally {

			// dispose commmands
			try {
				List<? extends Command> disposeCommands = commands.getDisposeCommands(context, true);
				for (Command command : disposeCommands) {
					disposeResult = command.execute(context);
					if (!disposeResult) {
						break;
					}
				}
			} catch (Exception e) {
				log.warn("dispose commands failed " + e.getMessage());
			}
		}
		return result;
	}

}
