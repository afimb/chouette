package mobi.chouette.exchange.exporter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.ejb.EJB;

import mobi.chouette.common.Constant;
import mobi.chouette.common.Context;
import mobi.chouette.common.chain.Command;
import mobi.chouette.dao.CompanyDAO;
import mobi.chouette.dao.GroupOfLineDAO;
import mobi.chouette.dao.LineDAO;
import mobi.chouette.dao.NetworkDAO;
import mobi.chouette.exchange.ProcessingCommands;
import mobi.chouette.exchange.ProgressionCommand;
import mobi.chouette.exchange.parameters.AbstractExportParameter;
import mobi.chouette.exchange.report.ActionError;
import mobi.chouette.exchange.report.ActionReport;
import mobi.chouette.model.Company;
import mobi.chouette.model.GroupOfLine;
import mobi.chouette.model.Line;
import mobi.chouette.model.Network;

public class AbstractExporterCommand implements Constant {

	@EJB
	protected LineDAO lineDAO;

	@EJB
	protected NetworkDAO ptNetworkDAO;

	@EJB
	protected CompanyDAO companyDAO;

	@EJB
	protected GroupOfLineDAO groupOfLineDAO;

	protected Set<Line> loadLines(String type, List<Long> ids) {
		Set<Line> lines = new HashSet<Line>();
		if (ids == null || ids.isEmpty()) {
			lines.addAll(lineDAO.findAll());
		} else {
			if (type.equals("line")) {
				lines.addAll(lineDAO.findAll(ids));
			} else if (type.equals("network")) {
				List<Network> list = ptNetworkDAO.findAll(ids);
				for (Network ptNetwork : list) {
					lines.addAll(ptNetwork.getLines());
				}
			} else if (type.equals("company")) {
				List<Company> list = companyDAO.findAll(ids);
				for (Company company : list) {
					lines.addAll(company.getLines());
				}
			} else if (type.equals("group_of_line")) {
				List<GroupOfLine> list = groupOfLineDAO.findAll(ids);
				for (GroupOfLine groupOfLine : list) {
					lines.addAll(groupOfLine.getLines());
				}
			}
		}
		return lines;
	}
	
	public boolean process(Context context, ProcessingCommands commands, ProgressionCommand progression, boolean continueLineProcesingOnError) throws Exception
	{
		boolean result = ERROR;
		AbstractExportParameter parameters = (AbstractExportParameter) context.get(CONFIGURATION);
		ActionReport report = (ActionReport) context.get(REPORT);

		// initialisation
		List<? extends Command> preProcessingCommands = commands.getPreProcessingCommands(context, true);
		progression.initialize(context, preProcessingCommands.size()+1);
		for (Command exportCommand : preProcessingCommands) {
			result = exportCommand.execute(context);
			if (!result) {
				report.setFailure(new ActionError(ActionError.CODE.NO_DATA_FOUND,"no data to export"));
				progression.execute(context);
				return ERROR;		
			}
			progression.execute(context);
		}

		// get lines 
		String type = parameters.getReferencesType();
		// set default type 
		if (type == null || type.isEmpty() )
		{
			// all lines
			type = "line";
			parameters.setIds(null);
		}
		type=type.toLowerCase();

		List<Long> ids = null;
		if (parameters.getIds() != null) {
			ids = new ArrayList<Long>(parameters.getIds());
		}

		Set<Line> lines = loadLines(type, ids);
		if (lines.isEmpty()) {
			report.setFailure(new ActionError(ActionError.CODE.NO_DATA_FOUND,"no data to export"));
			return ERROR;

		}
		progression.execute(context);
		
		// process lines
		List<? extends Command> lineProcessingCommands = commands.getLineProcessingCommands(context, true);
		progression.start(context, lines.size());
		int lineCount = 0;
		// export each line
		for (Line line : lines) {
			context.put(LINE_ID, line.getId());
			progression.execute(context);
			boolean exportFailed = false;
			for (Command exportCommand : lineProcessingCommands) {
				result = exportCommand.execute(context);
				if (!result) {
					exportFailed = true;
					break;
				}
			}
			if (!exportFailed) 
			{
				lineCount ++;
			}
			else if (!continueLineProcesingOnError)
			{
				report.setFailure(new ActionError(ActionError.CODE.INVALID_DATA,"unable to export data"));
				return ERROR;
			}
		}
		// post processing
		
		// check if data where exported
		if (lineCount == 0) {
			progression.terminate(context, 1);
			report.setFailure(new ActionError(ActionError.CODE.NO_DATA_PROCEEDED,"no data exported"));
			progression.execute(context);
			return ERROR;		
		}
		
		List<? extends Command> postProcessingCommands = commands.getPostProcessingCommands(context, true);
		progression.terminate(context, postProcessingCommands.size());
		for (Command exportCommand : postProcessingCommands) {
			result = exportCommand.execute(context);
			if (!result) {
				if (report.getFailure() == null)
				   report.setFailure(new ActionError(ActionError.CODE.NO_DATA_PROCEEDED,"no data exported"));
				return ERROR;
			}
			progression.execute(context);
		}
		return result;
	}

}
