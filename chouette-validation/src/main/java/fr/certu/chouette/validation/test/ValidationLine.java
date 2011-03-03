/**
 * 
 */
package fr.certu.chouette.validation.test;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import fr.certu.chouette.model.neptune.Line;
import fr.certu.chouette.model.neptune.PTNetwork;
import fr.certu.chouette.plugin.report.Report;
import fr.certu.chouette.plugin.report.ReportItem;
import fr.certu.chouette.plugin.validation.IValidationPlugin;
import fr.certu.chouette.plugin.validation.ValidationClassReportItem;
import fr.certu.chouette.plugin.validation.ValidationParameters;
import fr.certu.chouette.plugin.validation.ValidationStepDescription;
import fr.certu.chouette.validation.report.DetailReportItem;
import fr.certu.chouette.validation.report.SheetReportItem;

/**
 * @author mamadou keira
 *
 */
public class ValidationLine implements IValidationPlugin<Line>
{
	@Getter private ValidationStepDescription description;

	public void init()
	{
		//TODO 
		description = new ValidationStepDescription("",ValidationClassReportItem.CLASS.TWO.ordinal());
	}

	@Override
	public List<ValidationClassReportItem> doValidate(List<Line> lines,ValidationParameters parameters) 
	{
		System.out.println("LineValidation");
		List<ValidationClassReportItem> validationClassReportItems = new ArrayList<ValidationClassReportItem>();
		ValidationClassReportItem category2 = new ValidationClassReportItem(ValidationClassReportItem.CLASS.TWO);
		category2.addAll(validate(lines));
		validationClassReportItems.add(category2);
		return validationClassReportItems;
	}

	private List<ReportItem> validate(List<Line> lines){
		ReportItem sheet1 = new SheetReportItem("Test2_Sheet1", 1);
		ReportItem sheet2 = new SheetReportItem("Test2_Sheet2",2);
		ReportItem sheet6 = new SheetReportItem("Test2_Sheet6",6);
		ReportItem sheet7 = new SheetReportItem("Test2_Sheet7",7);

		ReportItem report2_1_1 = new SheetReportItem("Test2_Sheet1_Step1",1);
		ReportItem report2_1_2 = new SheetReportItem("Test2_Sheet1_Step2", 2);

		ReportItem report2_6_1 = new SheetReportItem("Test2_Sheet6_Step1",1);
		ReportItem report2_6_2 = new SheetReportItem("Test2_Sheet6_Step2",2);
		
		ReportItem report2_7_1 = new SheetReportItem("Test2_Sheet7_Step1",1);

		List<ReportItem> result = new ArrayList<ReportItem>();
		for (Line line : lines) {
			PTNetwork network = line.getPtNetwork();
			if (network == null)
			{
				ReportItem failedItem = new DetailReportItem("Test2_Sheet1_fatal");
				failedItem.setStatus(Report.STATE.FATAL);
				failedItem.addMessageArgs(line.getObjectId());
				report2_1_1.addItem(failedItem);

			}
			else
			{
				List<String> lineIds = network.getLineIds();
				if (lineIds != null && !lineIds.isEmpty())
				{
					//Test 2.1.1
					if (!lineIds.contains(line.getObjectId()))
					{
						ReportItem failedItem = new DetailReportItem("Test2_Sheet1_Step1_error");
						failedItem.setStatus(Report.STATE.ERROR);
						failedItem.addMessageArgs(network.getObjectId(),line.getObjectId());
						report2_1_1.addItem(failedItem);
					}else {
						report2_1_1.setStatus(Report.STATE.OK);
					}
					//Test 2.1.2
					String ptNeworkId = line.getPtNetworkIdShortcut();
					if (!ptNeworkId.equals(network.getObjectId()))
					{
						ReportItem failedItem = new DetailReportItem("Test2_Sheet1_Step2_error");
						failedItem.setStatus(Report.STATE.ERROR);
						failedItem.addMessageArgs(network.getObjectId(),line.getObjectId());
						report2_1_2.addItem(failedItem);
					}else {
						report2_1_2.setStatus(Report.STATE.OK);
					}
				}
			}	
			//Test 2.6
			List<String> lineEnds = line.getLineEnds();
			if(lineEnds != null){
				List<String> objectIds = Line.extractObjectIds(line.getStopPointList());
				if(!objectIds.containsAll(lineEnds)){
					ReportItem detailReportItem = new DetailReportItem("Test2_Sheet6_Step1_error", Report.STATE.ERROR, "");
					report2_6_1.addItem(detailReportItem);
				}else {
					report2_6_1.setStatus(Report.STATE.OK);	
				}
				List<String> lineEndList  = Line.extractObjectIds(line.getLineEndList());
				if(!lineEnds.containsAll(lineEndList)){
					ReportItem detailReportItem = new DetailReportItem("Test2_Sheet6_Step2_error",Report.STATE.ERROR, "");
					report2_6_2.addItem(detailReportItem);
				}else {
					report2_6_2.setStatus(Report.STATE.OK);	
				}
			}
			
			//Test 2.7
			List<String> routeIds = Line.extractObjectIds(line.getRoutes());
			if(!routeIds.containsAll(line.getRouteIds())){
				ReportItem detailReportItem = new DetailReportItem("Test2_Sheet7_Step1_error",Report.STATE.ERROR, "");
				report2_7_1.addItem(detailReportItem);
			}else {
				report2_7_1.setStatus(Report.STATE.OK);
			}
			
			sheet1.addItem(report2_1_1);
			sheet2.addItem(report2_1_2);
			sheet6.addItem(report2_6_1);
			sheet6.addItem(report2_6_2);
			sheet7.addItem(report2_7_1);

			result.add(sheet1);
			result.add(sheet2);
			result.add(sheet6);
			result.add(sheet7);
		}
		return result;
	}

}
