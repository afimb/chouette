package fr.certu.chouette.validation.test2;

import java.util.List;

import fr.certu.chouette.model.neptune.VehicleJourney;
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
public class Sheet17 implements IValidationPlugin<VehicleJourney>{

	private ValidationStepDescription validationStepDescription;

	public void init(){
		validationStepDescription = new ValidationStepDescription("Test2.17", ValidationClassReportItem.CLASS.TWO.ordinal());
	}

	@Override
	public ValidationStepDescription getDescription() {
		return validationStepDescription;
	}
	@Override
	public ReportItem doValidate(List<VehicleJourney> beans) {
		ReportItem report = new SheetReportItem("Test2_Sheet17",17);
		report.addItem(step_2_17(beans));
		return report;
	}

	private ReportItem step_2_17(List<VehicleJourney> vehicleJourneys){

		ReportItem reportItem = new SheetReportItem("Test2_Sheet17_Step1",1);
		if(vehicleJourneys != null){
			for (VehicleJourney vehicleJourney : vehicleJourneys) {
				if(vehicleJourney.getRoute() != null){
					if(!vehicleJourney.getRouteId().equals(vehicleJourney.getRoute().getObjectId())){
						ReportItem detailReportItem = new DetailReportItem("Test2_Sheet17_Step1_error", Report.STATE.ERROR, vehicleJourney.getObjectId());
						reportItem.addItem(detailReportItem);	
					}else
						reportItem.setStatus(Report.STATE.OK);	
				}else{
					ReportItem detailReportItem = new DetailReportItem("Test2_Sheet17_Step1_error", Report.STATE.ERROR, vehicleJourney.getObjectId());
					reportItem.addItem(detailReportItem);
				}
				
			}
		}
		
		return reportItem;
	}

}
