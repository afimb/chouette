package fr.certu.chouette.validation.test2;

import java.util.List;

import fr.certu.chouette.model.neptune.VehicleJourney;
import fr.certu.chouette.model.neptune.VehicleJourneyAtStop;
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
public class Sheet22 implements IValidationPlugin<VehicleJourney>{

	private ValidationStepDescription validationStepDescription;

	public void init(){
		validationStepDescription = new ValidationStepDescription("Test2.22", ValidationClassReportItem.CLASS.TWO.ordinal());
	}

	@Override
	public ValidationStepDescription getDescription() {
		return validationStepDescription;
	}
	@Override
	public ReportItem doValidate(List<VehicleJourney> beans) {
		ReportItem report = new SheetReportItem("Test2_Sheet22",22);
		report.addItem(step_2_22(beans));
		return report;
	}

	private ReportItem step_2_22(List<VehicleJourney> vehicleJourneys){

		ReportItem reportItem = new SheetReportItem("Test2_Sheet22_Step1",1);
		if(vehicleJourneys != null){
			for (VehicleJourney vehicleJourney : vehicleJourneys) {
				for(VehicleJourneyAtStop vehicleJourneyAtStop : vehicleJourney.getVehicleJourneyAtStops()){
					String stopPointId = vehicleJourneyAtStop.getStopPointId();
					String stopPointObjectId = vehicleJourneyAtStop.getStopPoint().getObjectId();				
					if(!stopPointObjectId.equals(stopPointId)){
						String order = String.valueOf(vehicleJourneyAtStop.getOrder());
						ReportItem detailReportItem = new DetailReportItem("Test2_Sheet22_Step1_error", Report.STATE.ERROR,order);
						reportItem.addItem(detailReportItem);	
					}else
						reportItem.setStatus(Report.STATE.OK);	
				}
			}
		}
		return reportItem;
	}
}

