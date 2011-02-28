package fr.certu.chouette.validation.test2;

import java.util.List;

import fr.certu.chouette.model.neptune.StopPoint;
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
public class Sheet10 implements IValidationPlugin<StopPoint>{

	private ValidationStepDescription validationStepDescription;
	
	public void init(){
		validationStepDescription = new ValidationStepDescription("Test2.10", ValidationClassReportItem.CLASS.TWO.ordinal());
	}
	
	@Override
	public ValidationStepDescription getDescription() {
		return validationStepDescription;
	}
	@Override
	public ReportItem doValidate(List<StopPoint> beans) {
		ReportItem reportItem = new SheetReportItem("Test2_Sheet10",10);
		reportItem.addItem(step_2_10_1(beans));
		return reportItem;
	}
	
	private ReportItem step_2_10_1(List<StopPoint> stopPoints){
		ReportItem reportItem = new SheetReportItem("Test2_Sheet10_Step1",1);
	
		for (StopPoint stopPoint : stopPoints) {
			String lineIdShortcut = stopPoint.getLineIdShortcut();
			if(lineIdShortcut != null){
				String lineObjectId = stopPoint.getLine().getObjectId();
				if(!lineIdShortcut.equals(lineObjectId)){
					ReportItem detailReportItem = new DetailReportItem("Test2_Sheet10_Step1_error", Report.STATE.ERROR, "");
					reportItem.addItem(detailReportItem);	
				}else {
					reportItem.setStatus(Report.STATE.OK);	
				}
			}
		}
		return reportItem;
	}

}
