package fr.certu.chouette.validation.test2;

import java.util.List;

import fr.certu.chouette.model.neptune.JourneyPattern;
import fr.certu.chouette.plugin.report.Report;
import fr.certu.chouette.plugin.report.ReportItem;
import fr.certu.chouette.plugin.validation.IValidationPlugin;
import fr.certu.chouette.plugin.validation.ValidationClassReportItem;
import fr.certu.chouette.plugin.validation.ValidationStepDescription;
import fr.certu.chouette.validation.report.DetailReportItem;
import fr.certu.chouette.validation.report.SheetReportItem;

/**
 * 
 * @author mamadou keira
 *
 */
public class Sheet16 implements IValidationPlugin<JourneyPattern>{

	private ValidationStepDescription validationStepDescription;

	public void init(){
		validationStepDescription = new ValidationStepDescription("Test2.16", ValidationClassReportItem.CLASS.TWO.ordinal());
	}

	@Override
	public ValidationStepDescription getDescription() {
		return validationStepDescription;
	}
	@Override
	public ReportItem doValidate(List<JourneyPattern> beans) {
		ReportItem report = new SheetReportItem("Test2_Sheet16",16);
		report.addItem(step_2_16(beans));
		return report;
	}

	private ReportItem step_2_16(List<JourneyPattern> journeyPatterns){

		ReportItem reportItem = new SheetReportItem("Test2_Sheet16_Step1",1);
		for (JourneyPattern journeyPattern : journeyPatterns) {
			String lineIdShortCutId = journeyPattern.getLineIdShortcut();
			if(lineIdShortCutId != null){
				if(!journeyPattern.getRoute().getLine().getObjectId().equals(lineIdShortCutId)){
					ReportItem detailReportItem = new DetailReportItem("Test2_Sheet16_Step1_error", Report.STATE.ERROR,"");
					reportItem.addItem(detailReportItem);	
				}else
					reportItem.setStatus(Report.STATE.OK);
			}
		}
		return reportItem;
	}

}
