package fr.certu.chouette.validation.test2;

import java.util.Arrays;
import java.util.List;

import fr.certu.chouette.model.neptune.JourneyPattern;
import fr.certu.chouette.plugin.report.Report;
import fr.certu.chouette.plugin.report.ReportItem;
import fr.certu.chouette.plugin.validation.IValidationPlugin;
import fr.certu.chouette.plugin.validation.ValidationClassReportItem;
import fr.certu.chouette.plugin.validation.ValidationParameters;
import fr.certu.chouette.plugin.validation.ValidationStepDescription;
import fr.certu.chouette.validation.report.DetailReportItem;
import fr.certu.chouette.validation.report.SheetReportItem;

/**
 * 
 * @author mamadou keira
 *
 */
public class Sheet15 implements IValidationPlugin<JourneyPattern>{

	private ValidationStepDescription validationStepDescription;
	
	public void init(){
		validationStepDescription = new ValidationStepDescription("Test2.15", ValidationClassReportItem.CLASS.TWO.ordinal());
	}
	
	@Override
	public ValidationStepDescription getDescription() {
		return validationStepDescription;
	}
	@Override
	public ReportItem doValidate(List<JourneyPattern> beans,ValidationParameters parameters) {
		ReportItem report = new SheetReportItem("Test2_Sheet15",15);
		report.addAll(Arrays.asList(step_2_15(beans)));
		return report;
	}
	
	private ReportItem[] step_2_15(List<JourneyPattern> journeyPatterns){
		
		ReportItem reportItem1 = new SheetReportItem("Test2_Sheet15_Step1",1);
		ReportItem reportItem2 = new SheetReportItem("Test2_Sheet15_Step2",2);
		ReportItem[] res = new ReportItem[2];
		if(journeyPatterns != null){
			for (JourneyPattern journeyPattern : journeyPatterns) {
				List<String> stopPointIds = JourneyPattern.extractObjectIds(journeyPattern.getStopPoints());
				//Test 2.15.1
				if(!stopPointIds.containsAll(journeyPattern.getStopPointIds())){
					ReportItem detailReportItem = new DetailReportItem("Test2_Sheet15_Step1_error", Report.STATE.ERROR,"");
					reportItem1.addItem(detailReportItem);	
				}else {
					reportItem1.setStatus(Report.STATE.OK);
				}
				//Test 2.15.2
				if(!journeyPattern.getStopPointIds().containsAll(stopPointIds)){
					ReportItem detailReportItem = new DetailReportItem("Test2_Sheet15_Step2_error", Report.STATE.ERROR,"");
					reportItem2.addItem(detailReportItem);	
				}else {
					reportItem2.setStatus(Report.STATE.OK);
				}
			}
		}
		
		res[0] = reportItem1;
		res[1] = reportItem2;
		return res;
	}

}
