package fr.certu.chouette.validation.test;

import java.util.ArrayList;
import java.util.List;

import fr.certu.chouette.model.neptune.Timetable;
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
public class ValidationTimetable implements IValidationPlugin<Timetable>{

	private ValidationStepDescription validationStepDescription;
	
	public void init(){
		//TODO
		validationStepDescription = new ValidationStepDescription("", ValidationClassReportItem.CLASS.TWO.ordinal());
	}
	
	@Override
	public ValidationStepDescription getDescription() {
		return validationStepDescription;
	}
	@Override
	public List<ValidationClassReportItem> doValidate(List<Timetable> beans,ValidationParameters parameters) {		
		return validate(beans);
	}
	
	private List<ValidationClassReportItem> validate(List<Timetable> timetables){
		List<ValidationClassReportItem> result = new ArrayList<ValidationClassReportItem>();
		ValidationClassReportItem category2 = new ValidationClassReportItem(ValidationClassReportItem.CLASS.TWO);
		
		ReportItem sheet5 = new SheetReportItem("Test2_Sheet5",5);
		SheetReportItem reportItem1 = new SheetReportItem("Test2_Sheet5_Step1",1);
		//SheetReportItem reportItem2 = new SheetReportItem("Test2_Sheet5_Step2",2);
		
		for (Timetable timetable: timetables) {
			// List<String> vjIds= timetable.getVehicleJourneyIds();
			// List<String> vehicleJourneyIds = Timetable.extractObjectIds(timetable.getVehicleJourneys());
                        //Test 2.5.1
                        if (timetable.getVehicleJourneys() == null || timetable.getVehicleJourneys().isEmpty()) {
                            ReportItem detailReportItem = new DetailReportItem("Test2_Sheet5_Step1_error",Report.STATE.ERROR);
                            reportItem1.addItem(detailReportItem);
                        } else {
                            reportItem1.updateStatus(Report.STATE.OK);
                        }
			/*if(vjIds != null){
				if(!vehicleJourneyIds.containsAll(vjIds)){
					ReportItem detailReportItem = new DetailReportItem("Test2_Sheet5_Step1_error",Report.STATE.ERROR);
					reportItem1.addItem(detailReportItem);
				}else {
					reportItem1.updateStatus(Report.STATE.OK);	
				}
			} else {
                            ReportItem detailReportItem = new DetailReportItem("Test2_Sheet5_Step1_error",Report.STATE.ERROR);
                            reportItem1.addItem(detailReportItem);
                        }*/
			//Test 2.5.2
			/*if(timetable.getVehicleJourneys() != null){
				if(!vjIds.containsAll(vehicleJourneyIds)){
					ReportItem detailReportItem = new DetailReportItem("Test2_Sheet5_Step2_error",Report.STATE.ERROR);
					reportItem2.addItem(detailReportItem);
				}else {
					reportItem2.updateStatus(Report.STATE.OK);	
				}
			}*/
		}
		reportItem1.computeDetailItemCount();
		//reportItem2.computeDetailItemCount();
		
		sheet5.addItem(reportItem1);
		//sheet5.addItem(reportItem2);
		category2.addItem(sheet5);
		result.add(category2);
		return result;
	}

}
