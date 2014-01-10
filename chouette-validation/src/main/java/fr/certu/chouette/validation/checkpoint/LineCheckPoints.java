package fr.certu.chouette.validation.checkpoint;

import java.util.List;

import org.json.JSONObject;

import fr.certu.chouette.model.neptune.Line;
import fr.certu.chouette.plugin.report.Report;
import fr.certu.chouette.plugin.validation.ICheckPointPlugin;
import fr.certu.chouette.plugin.validation.report.CheckPointReportItem;
import fr.certu.chouette.plugin.validation.report.DetailReportItem;
import fr.certu.chouette.plugin.validation.report.PhaseReportItem;
import fr.certu.chouette.plugin.validation.report.ReportLocation;

public class LineCheckPoints extends AbstractValidation implements ICheckPointPlugin<Line>
{

	@Override
	public void check(List<Line> beans, JSONObject parameters,
			PhaseReportItem report) 
	{
		// init checkPoints : add here all defined check points for this kind of object
		initCheckPoint(report, LINE_1, CheckPointReportItem.SEVERITY.WARNING);
		initCheckPoint(report, LINE_2, CheckPointReportItem.SEVERITY.ERROR);
		
		// 3-Line-1 : check if two lines have same name
		// 3-Line-2 : check if line has routes
		if (beans.size() > 1)
		{
			// checkPoint is applicable
			prepareCheckPoint(report, LINE_1);
			prepareCheckPoint(report, LINE_2);
			
			// en cas d'erreur, on reporte autant de detail que de lignes en erreur
			for (int i = 0; i < beans.size() -1; i++)
			{
				Line line1 = beans.get(i);
				// 3-Line-1 : check if two lines have same name
                checkLine1(beans, report, i, line1);
        		// 3-Line-2 : check if line has routes
				checkLine2(report, line1);
			}
		}
		
	}

	/**
	 * @param report
	 * @param line1
	 */
	private void checkLine2(PhaseReportItem report, Line line1) {
		if (isEmpty(line1.getRoutes()))
		{
			// failure encountered, add line 1
			ReportLocation location = new ReportLocation(line1);
			DetailReportItem detail = new DetailReportItem(LINE_2,line1.getObjectId(), Report.STATE.ERROR, location,null);
			addValidationError(report, LINE_2, detail);
		}
	}

	/**
	 * @param beans
	 * @param report
	 * @param lineRank
	 * @param line1
	 */
	private void checkLine1(List<Line> beans, PhaseReportItem report,
			int lineRank, Line line1) {
		boolean error_1 = false; // if true, add detail for this line
		for (int j = lineRank+1; j < beans.size(); j++)
		{
			Line line2 = beans.get(j);
			if (line2.getPtNetwork().equals(line1.getPtNetwork()))
			{
				if (line1.getName().equals(line2.getName()) && 
						line1.getNumber().equals(line2.getNumber()))
				{
					// failure ! add only line2 location
					ReportLocation location = new ReportLocation(line2);
					DetailReportItem detail = new DetailReportItem(LINE_1,line2.getObjectId(), Report.STATE.WARNING, location, null);
					addValidationError(report, LINE_1, detail);
					
					error_1 = true; // to add detail for line1
				}
			}

		}
		if (error_1)
		{
			// failure encountered, add line 1
			ReportLocation location = new ReportLocation(line1);
			DetailReportItem detail = new DetailReportItem(LINE_1,line1.getObjectId(), Report.STATE.WARNING, location, null);
			addValidationError(report, LINE_1, detail);
		}
	}


}
