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
	public ReportItem doValidate(Line line) 
	{
		ReportItem report = new SheetReportItem("Test2_Sheet1");
		report.addItem(step2_2_1(line));
		return report;
	}
	
    private ReportItem step2_2_1(Line line) 
    {
		ReportItem reportStep = new SheetReportItem("Test2_Sheet1_Step1");

		PTNetwork network = line.getPtNetwork();
		List<String> lineIds = network.getLineIds();
		if (lineIds != null && !lineIds.isEmpty())
		{
			if (!lineIds.contains(line.getObjectId()))
			{
				ReportItem failedItem = new DetailReportItem("Test2_Sheet1_Step1_error");
				failedItem.setStatus(Report.STATE.ERROR);
				failedItem.addMessageArgs(network.getObjectId(),line.getObjectId());
				reportStep.addItem(failedItem);
			}
		}
		return reportStep;
	}



}
