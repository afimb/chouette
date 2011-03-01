package fr.certu.chouette.validation.test2;

import java.util.List;

import fr.certu.chouette.model.neptune.StopPoint;
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
public class Sheet11 implements IValidationPlugin<StopPoint>{

	private ValidationStepDescription validationStepDescription;
	
	public void init(){
		validationStepDescription = new ValidationStepDescription("Test2.11", ValidationClassReportItem.CLASS.TWO.ordinal());
	}
	
	@Override
	public ValidationStepDescription getDescription() {
		return validationStepDescription;
	}
	@Override
	public ReportItem doValidate(List<StopPoint> beans,ValidationParameters parameters) {
		ReportItem reportItem = new SheetReportItem("Test2_Sheet11",11);
		reportItem.addItem(step_2_11_1(beans));
		return reportItem;
	}
	
	private ReportItem step_2_11_1(List<StopPoint> stopPoints){
		ReportItem reportItem = new SheetReportItem("Test2_Sheet11_Step1",1);
	
		for (StopPoint stopPoint : stopPoints) {
			String ptNetworkIdShortcut = stopPoint.getPtNetworkIdShortcut();
			if(ptNetworkIdShortcut != null){
				String ptNetworkObjectId = stopPoint.getPtNetwork().getObjectId();
				if(!ptNetworkIdShortcut.equals(ptNetworkObjectId)){
					ReportItem detailReportItem = new DetailReportItem("Test2_Sheet11_Step1_error", Report.STATE.ERROR,"");
					reportItem.addItem(detailReportItem);	
				}else {
					reportItem.setStatus(Report.STATE.OK);		
				}
			}
		}
		return reportItem;
	}

}
