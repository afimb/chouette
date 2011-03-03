package fr.certu.chouette.validation.test;

import java.util.ArrayList;
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
public class ValidationVehicleJourney implements IValidationPlugin<VehicleJourney>{

	private ValidationStepDescription validationStepDescription;

	public void init(){
		validationStepDescription = new ValidationStepDescription("", ValidationClassReportItem.CLASS.TWO.ordinal());
	}

	@Override
	public ValidationStepDescription getDescription() {
		return validationStepDescription;
	}
	@Override
	public List<ValidationClassReportItem> doValidate(List<VehicleJourney> beans,ValidationParameters parameters) {	
		System.out.println("VehicleJourneyValidation");
		return validate(beans);	
	}

	private List<ValidationClassReportItem> validate(List<VehicleJourney> vehicleJourneys){
		ValidationClassReportItem category2 = new ValidationClassReportItem(ValidationClassReportItem.CLASS.TWO);
		
		ReportItem sheet2_17 = new SheetReportItem("Test2_Sheet17",17);
		ReportItem sheet2_18 = new SheetReportItem("Test2_Sheet18",18);
		ReportItem sheet2_19 = new SheetReportItem("Test2_Sheet19",19);
		ReportItem sheet2_20 = new SheetReportItem("Test2_Sheet20",20);
		ReportItem sheet2_21 = new SheetReportItem("Test2_Sheet21",21);
		ReportItem sheet2_22 = new SheetReportItem("Test2_Sheet22",22);
		ReportItem sheet2_23 = new SheetReportItem("Test2_Sheet23",23);
		ReportItem sheet2_24 = new SheetReportItem("Test2_Sheet24",24);
		
		SheetReportItem report2_17_1 = new SheetReportItem("Test2_Sheet17_Step1",1);		
		SheetReportItem report2_18_1 = new SheetReportItem("Test2_Sheet18_Step1",1);
		SheetReportItem report2_18_2 = new SheetReportItem("Test2_Sheet18_Step2",2);		
		SheetReportItem report2_19 = new SheetReportItem("Test2_Sheet19_Step1",1);
		SheetReportItem report2_20 = new SheetReportItem("Test2_Sheet20_Step1",1);
		SheetReportItem report2_21 = new SheetReportItem("Test2_Sheet21_Step1",1);
		SheetReportItem report2_22 = new SheetReportItem("Test2_Sheet22_Step1",1);
		SheetReportItem report2_23 = new SheetReportItem("Test2_Sheet23_Step1",1);
		SheetReportItem report2_24 = new SheetReportItem("Test2_Sheet24_Step1",1);
		
		List<ValidationClassReportItem> result = new ArrayList<ValidationClassReportItem>();
		
		if(vehicleJourneys != null){
			for (VehicleJourney vehicleJourney : vehicleJourneys) {
				if(vehicleJourney.getRoute() != null){
					if(!vehicleJourney.getRouteId().equals(vehicleJourney.getRoute().getObjectId())){
						ReportItem detailReportItem = new DetailReportItem("Test2_Sheet17_Step1_error", Report.STATE.ERROR, vehicleJourney.getObjectId(),vehicleJourney.getRouteId());
						report2_17_1.addItem(detailReportItem);	
					}else
						report2_17_1.updateStatus(Report.STATE.OK);	
				}else{
					ReportItem detailReportItem = new DetailReportItem("Test2_Sheet17_Step1_error", Report.STATE.ERROR, vehicleJourney.getObjectId(),vehicleJourney.getRouteId());
					report2_17_1.addItem(detailReportItem);
				}
				
				String journeyPatternId = vehicleJourney.getJourneyPatternId();
				String journeyPatternObjectId = (vehicleJourney.getJourneyPattern() != null) ? 
						vehicleJourney.getJourneyPattern().getObjectId() : ""; 
				//Test 2.18.1
				if(journeyPatternId != null){
					if(!journeyPatternObjectId.equals(journeyPatternId)){
						ReportItem detailReportItem = new DetailReportItem("Test2_Sheet18_Step1_error", Report.STATE.ERROR, vehicleJourney.getObjectId());
						report2_18_1.addItem(detailReportItem);	
					}else
						report2_18_1.updateStatus(Report.STATE.OK);
				}
				List<String> stopPointObjectIds = (vehicleJourney.getJourneyPattern() != null && vehicleJourney.getJourneyPattern().getStopPoints() != null) ? 
						VehicleJourney.extractObjectIds(vehicleJourney.getJourneyPattern().getStopPoints()) : null;
			
				List<String> stopPointIds = (vehicleJourney.getJourneyPattern() != null) ? 
					vehicleJourney.getJourneyPattern().getStopPointIds() : null;
				if(stopPointObjectIds != null && stopPointIds != null){
					//Test 2.18.2 a
					if(!stopPointIds.containsAll(stopPointObjectIds)){
						ReportItem detailReportItem = new DetailReportItem("Test2_Sheet18_Step2_error_a", Report.STATE.ERROR,"");
						report2_18_2.addItem(detailReportItem);	
					}else
						report2_18_2.updateStatus(Report.STATE.OK);
					//Test 2.18.2 b
					List<VehicleJourneyAtStop> vehicleJourneyAtStopIds = vehicleJourney.getVehicleJourneyAtStops();
					for (VehicleJourneyAtStop vehicleJourneyAtStop : vehicleJourneyAtStopIds) {
						if(!stopPointObjectIds.contains(vehicleJourneyAtStop.getStopPointId())){
							ReportItem detailReportItem = new DetailReportItem("Test2_Sheet18_Step2_error_b", Report.STATE.ERROR,"");
							report2_18_2.addItem(detailReportItem);	
						}else
							report2_18_2.updateStatus(Report.STATE.OK);
					}
				}
				//Test 2.19.1
				String lineShortCutId = vehicleJourney.getLineIdShortcut();
				if(lineShortCutId != null){
					String lineObjectId = (vehicleJourney.getRoute() != null && vehicleJourney.getRoute().getLine() != null) ? 
							vehicleJourney.getRoute().getLine().getObjectId() : null;
					if(!lineShortCutId.equals(lineObjectId)){
						ReportItem detailReportItem = new DetailReportItem("Test2_Sheet19_Step1_error", Report.STATE.ERROR,vehicleJourney.getObjectId());
						report2_19.addItem(detailReportItem);	
					}else
						report2_19.updateStatus(Report.STATE.OK);	
				}
				
				//Test 2.20.1
				String companyId = vehicleJourney.getCompanyId();
				if(companyId != null){
					String companyObjectId = (vehicleJourney.getCompany() != null) ? vehicleJourney.getCompany().getObjectId() : null;
					if(!companyId.equals(companyObjectId)){
						ReportItem detailReportItem = new DetailReportItem("Test2_Sheet20_Step1_error", Report.STATE.ERROR, vehicleJourney.getObjectId());
						report2_20.addItem(detailReportItem);	
					}else
						report2_20.updateStatus(Report.STATE.OK);	
				}
				
				//Test 2.21.1
				String timeSlotId = vehicleJourney.getTimeSlotId();
				if(timeSlotId != null){
					String timeSlotObjectId = (vehicleJourney.getTimeSlot() != null) ? vehicleJourney.getTimeSlot().getObjectId():null;
					if(!timeSlotId.equals(timeSlotObjectId)){
						ReportItem detailReportItem = new DetailReportItem("Test2_Sheet21_Step1_error", Report.STATE.ERROR, vehicleJourney.getObjectId());
						report2_21.addItem(detailReportItem);	
					}else
						report2_21.updateStatus(Report.STATE.OK);	
				}
				List<VehicleJourneyAtStop> vehicleJourneyAtStops =vehicleJourney.getVehicleJourneyAtStops();
				//Test 2.22.1
				if(vehicleJourneyAtStops != null){
					for(VehicleJourneyAtStop vehicleJourneyAtStop : vehicleJourneyAtStops){
						String stopPointId = vehicleJourneyAtStop.getStopPointId();
						String stopPointObjectId = vehicleJourneyAtStop.getStopPoint().getObjectId();				
						if(!stopPointObjectId.equals(stopPointId)){
							String arrivalTime = String.valueOf(vehicleJourneyAtStop.getArrivalTime());
							ReportItem detailReportItem = new DetailReportItem("Test2_Sheet22_Step1_error", Report.STATE.ERROR,arrivalTime);
							report2_22.addItem(detailReportItem);	
						}else
							report2_22.updateStatus(Report.STATE.OK);	
						
						
						//Test 2.23.1
						String vehicleJourneyId = vehicleJourneyAtStop.getVehicleJourneyId();
						if(!vehicleJourneyId.equals(vehicleJourney.getObjectId())){
							String arrivalTime = String.valueOf(vehicleJourneyAtStop.getArrivalTime());
							ReportItem detailReportItem = new DetailReportItem("Test2_Sheet23_Step1_error", Report.STATE.ERROR,arrivalTime);
							report2_23.addItem(detailReportItem);	
						}else
							report2_23.updateStatus(Report.STATE.OK);	
					}
				}
				
				//Test 2.24.1
				String routeIdFromVJ = vehicleJourney.getRouteId();
				String routeIdFromJP = (vehicleJourney.getJourneyPattern() != null) ? vehicleJourney.getJourneyPattern().getRouteId() : null;
				if(!routeIdFromVJ.equals(routeIdFromJP)){
					ReportItem detailReportItem = new DetailReportItem("Test2_Sheet24_Step1_error", Report.STATE.ERROR,"");
					report2_24.addItem(detailReportItem);	
				}else
					report2_24.updateStatus(Report.STATE.OK);
			}
		}
		
		report2_17_1.computeDetailItemCount();
		report2_18_1.computeDetailItemCount();
		report2_18_2.computeDetailItemCount();
		report2_19.computeDetailItemCount();
		report2_20.computeDetailItemCount();
		report2_21.computeDetailItemCount();
		report2_22.computeDetailItemCount();
		report2_23.computeDetailItemCount();
		report2_24.computeDetailItemCount();
		
		sheet2_17.addItem(report2_17_1);
		sheet2_18.addItem(report2_18_1);
		sheet2_18.addItem(report2_18_2);
		sheet2_19.addItem(report2_19);
		sheet2_20.addItem(report2_20);
		sheet2_21.addItem(report2_21);
		sheet2_22.addItem(report2_22);
		sheet2_23.addItem(report2_23);
		sheet2_24.addItem(report2_24);
		
		category2.addItem(sheet2_17);
		category2.addItem(sheet2_18);
		category2.addItem(sheet2_19);
		category2.addItem(sheet2_20);
		category2.addItem(sheet2_21);
		category2.addItem(sheet2_22);
		category2.addItem(sheet2_23);
		category2.addItem(sheet2_24);
		
		result.add(category2);
		return result;
	}

}
