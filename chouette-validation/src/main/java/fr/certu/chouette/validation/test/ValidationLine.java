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
		List<ValidationClassReportItem> items = validate(lines); 
		return items;
	}

	private  List<ValidationClassReportItem> validate(List<Line> lines){
		ValidationClassReportItem category2 = new ValidationClassReportItem(ValidationClassReportItem.CLASS.TWO);
		ValidationClassReportItem category3 = new ValidationClassReportItem(ValidationClassReportItem.CLASS.THREE);

		List<ValidationClassReportItem> result = new ArrayList<ValidationClassReportItem>();
	
		ReportItem sheet1 = new SheetReportItem("Test2_Sheet1", 1);
		ReportItem sheet2 = new SheetReportItem("Test2_Sheet2",2);
		ReportItem sheet6 = new SheetReportItem("Test2_Sheet6",6);
		ReportItem sheet7 = new SheetReportItem("Test2_Sheet7",7);
		ReportItem sheet3_4 = new SheetReportItem("Test3_Sheet4",4);

		SheetReportItem report2_1_1 = new SheetReportItem("Test2_Sheet1_Step1",1);
		SheetReportItem report2_1_2 = new SheetReportItem("Test2_Sheet1_Step2", 2);
		SheetReportItem report2_2_1 = new SheetReportItem("Test2_Sheet2_Step1", 1);
		SheetReportItem report2_6_1 = new SheetReportItem("Test2_Sheet6_Step1",1);
		SheetReportItem report2_6_2 = new SheetReportItem("Test2_Sheet6_Step2",2);
		SheetReportItem report2_7_1 = new SheetReportItem("Test2_Sheet7_Step1",1);
		SheetReportItem report3_4_1 = new SheetReportItem("Test3_Sheet4_Step1",1);

		for (int i=0;i<lines.size();i++) {
			Line line = lines.get(i);
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
						report2_1_1.updateStatus(Report.STATE.OK);
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
						report2_1_2.updateStatus(Report.STATE.OK);
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
					report2_6_1.updateStatus(Report.STATE.OK);	
				}
				List<String> lineEndList  = Line.extractObjectIds(line.getLineEndList());
				if(!lineEnds.containsAll(lineEndList)){
					ReportItem detailReportItem = new DetailReportItem("Test2_Sheet6_Step2_error",Report.STATE.ERROR, "");
					report2_6_2.addItem(detailReportItem);
				}else 
					report2_6_2.updateStatus(Report.STATE.OK);	
			}
			
			//Test 2.7
			List<String> routeIds = Line.extractObjectIds(line.getRoutes());
			if(!routeIds.containsAll(line.getRouteIds())){
				ReportItem detailReportItem = new DetailReportItem("Test2_Sheet7_Step1_error",Report.STATE.ERROR, "");
				report2_7_1.addItem(detailReportItem);
			}else {
				report2_7_1.updateStatus(Report.STATE.OK);
			}
			//Test 3.4.1
			Line nextLine = (i <lines.size()-1) ? lines.get(i+1) : line;
			String refCurrent = line.getName()+""+line.getNumber();
			String refNext = nextLine.getName()+""+nextLine.getNumber();
			if(refCurrent.equals(refNext)){
				ReportItem detailReportItem = new DetailReportItem("Test3_Sheet4_Step1_error",Report.STATE.ERROR, line.getObjectId());
				report3_4_1.addItem(detailReportItem);
			}else 
				report3_4_1.updateStatus(Report.STATE.OK);	
		}
		report2_1_1.computeDetailItemCount();
		report2_1_2.computeDetailItemCount();
		report2_2_1.computeDetailItemCount();
		report2_6_1.computeDetailItemCount();
		report2_6_2.computeDetailItemCount();
		report2_7_1.computeDetailItemCount();
		report3_4_1.computeDetailItemCount();
		
		sheet1.addItem(report2_1_1);
		sheet1.addItem(report2_1_2);
		sheet2.addItem(report2_2_1);
		sheet6.addItem(report2_6_1);
		sheet6.addItem(report2_6_2);
		sheet7.addItem(report2_7_1);
		sheet3_4.addItem(report3_4_1);

		category2.addItem(sheet1);
		category2.addItem(sheet2);
		category2.addItem(sheet6);
		category2.addItem(sheet7);
		category3.addItem(report3_4_1);
		result.add(category2);
		result.add(category3);
		return result;
	}

}
