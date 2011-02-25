/**
 * 
 */
package fr.certu.chouette.validation.test2;

import java.util.List;

import lombok.Getter;
import fr.certu.chouette.model.neptune.Line;
import fr.certu.chouette.model.neptune.PTNetwork;
import fr.certu.chouette.plugin.report.Report;
import fr.certu.chouette.plugin.report.ReportItem;
import fr.certu.chouette.plugin.validation.IValidationPlugin;
import fr.certu.chouette.plugin.validation.ValidationClassReportItem;
import fr.certu.chouette.plugin.validation.ValidationStepDescription;
import fr.certu.chouette.validation.report.DetailReportItem;
import fr.certu.chouette.validation.report.SheetReportItem;

/**
 * @author zbouziane
 *
 */
public class Sheet1 implements IValidationPlugin<Line>
{
	@Getter private ValidationStepDescription description;

	/**
	 * 
	 */
	public Sheet1() 
	{
		// TODO Auto-generated constructor stub
	}

	public void init()
	{
		description = new ValidationStepDescription("Test 2.1",ValidationClassReportItem.CLASS.TWO.ordinal());
	}

	@Override
	public ReportItem doValidate(List<Line> lines) 
	{
		ReportItem report = new SheetReportItem("Test2_Sheet1", 1);
		report.addItem(step2_1_1(lines));
		report.addItem(step2_1_2(lines));
		return report;
	}

	private ReportItem step2_1_1(List<Line> lines) 
	{
		ReportItem reportStep = new SheetReportItem("Test2_Sheet1_Step1",1);

		boolean checked = false;
		for (Line line : lines) 
		{
			PTNetwork network = line.getPtNetwork();
			if (network == null)
			{
				checked = true;
				ReportItem failedItem = new DetailReportItem("Test2_Sheet1_fatal");
				failedItem.setStatus(Report.STATE.FATAL);
				failedItem.addMessageArgs(line.getObjectId());
				reportStep.addItem(failedItem);
				
			}
			else
			{
			List<String> lineIds = network.getLineIds();
			if (lineIds != null && !lineIds.isEmpty())
			{
				checked = true;
				if (!lineIds.contains(line.getObjectId()))
				{
					ReportItem failedItem = new DetailReportItem("Test2_Sheet1_Step1_error");
					failedItem.setStatus(Report.STATE.ERROR);
					failedItem.addMessageArgs(network.getObjectId(),line.getObjectId());
					reportStep.addItem(failedItem);
				}else {
					reportStep.setStatus(Report.STATE.OK);
				}
			}
			}
		}
		if (!checked)
		{
			reportStep.setStatus(Report.STATE.UNCHECK);
		}
		return reportStep;
	}

	private ReportItem step2_1_2(List<Line> lines) 
	{
		ReportItem reportStep = new SheetReportItem("Test2_Sheet1_Step2", 2);

		boolean checked = false;
		for (Line line : lines) 
		{
			PTNetwork network = line.getPtNetwork();
			if (network == null)
			{
				checked = true;
				ReportItem failedItem = new DetailReportItem("Test2_Sheet1_fatal");
				failedItem.setStatus(Report.STATE.FATAL);
				failedItem.addMessageArgs(line.getObjectId());
				reportStep.addItem(failedItem);
				
			}else {
				String ptNeworkId = line.getPtNetworkIdShortcut();
				if (ptNeworkId != null)
				{
					checked = true;
					if (!ptNeworkId.equals(network.getObjectId()))
					{
						ReportItem failedItem = new DetailReportItem("Test2_Sheet1_Step2_error");
						failedItem.setStatus(Report.STATE.ERROR);
						failedItem.addMessageArgs(network.getObjectId(),line.getObjectId());
						reportStep.addItem(failedItem);
					}else {
						reportStep.setStatus(Report.STATE.OK);
					}
				}
			}
		
		}
		if (!checked)
		{
			reportStep.setStatus(Report.STATE.UNCHECK);
		}
		return reportStep;
	}


}
