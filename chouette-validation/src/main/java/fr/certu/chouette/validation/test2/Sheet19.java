package fr.certu.chouette.validation.test2;

import java.util.List;

import fr.certu.chouette.model.neptune.VehicleJourney;
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
public class Sheet19 implements IValidationPlugin<VehicleJourney>{

	private ValidationStepDescription validationStepDescription;

	public void init(){
		validationStepDescription = new ValidationStepDescription("Test2.19", ValidationClassReportItem.CLASS.TWO.ordinal());
	}

	@Override
	public ValidationStepDescription getDescription() {
		return validationStepDescription;
	}
	@Override
	public ReportItem doValidate(List<VehicleJourney> beans,ValidationParameters parameters) {
		ReportItem report = new SheetReportItem("Test2_Sheet19",19);
		report.addItem(step_2_19(beans));
		return report;
	}

	private ReportItem step_2_19(List<VehicleJourney> vehicleJourneys){

		ReportItem reportItem = new SheetReportItem("Test2_Sheet19_Step1",1);
		if(vehicleJourneys != null){
			for (VehicleJourney vehicleJourney : vehicleJourneys) {
				String lineShortCutId = vehicleJourney.getLineIdShortcut();
				if(lineShortCutId != null){
					String lineObjectId = (vehicleJourney.getRoute() != null && vehicleJourney.getRoute().getLine() != null) ? 
							vehicleJourney.getRoute().getLine().getObjectId() : null;
					if(!lineShortCutId.equals(lineObjectId)){
						ReportItem detailReportItem = new DetailReportItem("Test2_Sheet19_Step1_error", Report.STATE.ERROR,vehicleJourney.getObjectId());
						reportItem.addItem(detailReportItem);	
					}else
						reportItem.setStatus(Report.STATE.OK);	
				}
			}
		}
		
		return reportItem;
	}

}
