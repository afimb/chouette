package fr.certu.chouette.validation.test2;

import java.util.Arrays;
import java.util.List;

import fr.certu.chouette.model.neptune.VehicleJourney;
import fr.certu.chouette.model.neptune.VehicleJourneyAtStop;
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
public class Sheet18 implements IValidationPlugin<VehicleJourney>{

	private ValidationStepDescription validationStepDescription;

	public void init(){
		validationStepDescription = new ValidationStepDescription("Test2.18", ValidationClassReportItem.CLASS.TWO.ordinal());
	}

	@Override
	public ValidationStepDescription getDescription() {
		return validationStepDescription;
	}
	@Override
	public ReportItem doValidate(List<VehicleJourney> beans,ValidationParameters parameters) {
		ReportItem report = new SheetReportItem("Test2_Sheet18",18);
		report.addAll(Arrays.asList(step_2_18(beans)));
		return report;
	}

	private ReportItem[] step_2_18(List<VehicleJourney> vehicleJourneys){

		ReportItem reportItem1 = new SheetReportItem("Test2_Sheet18_Step1",1);
		ReportItem reportItem2 = new SheetReportItem("Test2_Sheet18_Step2",2);
		ReportItem[] res = new ReportItem[2];
		if(vehicleJourneys != null){
			for (VehicleJourney vehicleJourney : vehicleJourneys) {
				String journeyPatternId = vehicleJourney.getJourneyPatternId();
				String journeyPatternObjectId = (vehicleJourney.getJourneyPattern() != null) ? 
						vehicleJourney.getJourneyPattern().getObjectId() : ""; 
				//Test 2.18.1
				if(journeyPatternId != null){
					if(!journeyPatternObjectId.equals(journeyPatternId)){
						ReportItem detailReportItem = new DetailReportItem("Test2_Sheet18_Step1_error", Report.STATE.ERROR, vehicleJourney.getObjectId());
						reportItem1.addItem(detailReportItem);	
					}else
						reportItem1.setStatus(Report.STATE.OK);
				}
				List<String> stopPointObjectIds = (vehicleJourney.getJourneyPattern() != null && vehicleJourney.getJourneyPattern().getStopPoints() != null) ? 
						VehicleJourney.extractObjectIds(vehicleJourney.getJourneyPattern().getStopPoints()) : null;
			
				List<String> stopPointIds = (vehicleJourney.getJourneyPattern() != null) ? 
					vehicleJourney.getJourneyPattern().getStopPointIds() : null;
				if(stopPointObjectIds != null && stopPointIds != null){
					//Test 2.18.2 a
					if(!stopPointIds.containsAll(stopPointObjectIds)){
						ReportItem detailReportItem = new DetailReportItem("Test2_Sheet18_Step2_error_a", Report.STATE.ERROR,"");
						reportItem2.addItem(detailReportItem);	
					}else
						reportItem2.setStatus(Report.STATE.OK);
					//Test 2.18.2 b
					List<VehicleJourneyAtStop> vehicleJourneyAtStopIds = vehicleJourney.getVehicleJourneyAtStops();
					for (VehicleJourneyAtStop vehicleJourneyAtStop : vehicleJourneyAtStopIds) {
						if(!stopPointObjectIds.contains(vehicleJourneyAtStop.getStopPointId())){
							ReportItem detailReportItem = new DetailReportItem("Test2_Sheet18_Step2_error_b", Report.STATE.ERROR,"");
							reportItem2.addItem(detailReportItem);	
						}else
							reportItem2.setStatus(Report.STATE.OK);
					}
				}
			}
		}
		res[0] = reportItem1;
		res[1] = reportItem2;
		return res;
	}

}
