package fr.certu.chouette.validation.test2;

import java.util.Arrays;
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
public class Sheet5 implements IValidationPlugin<Timetable>{

	private ValidationStepDescription validationStepDescription;
	
	public void init(){
		validationStepDescription = new ValidationStepDescription("Test2.5", ValidationClassReportItem.CLASS.TWO.ordinal());
	}
	
	@Override
	public ValidationStepDescription getDescription() {
		return validationStepDescription;
	}
	@Override
	public ReportItem doValidate(List<Timetable> beans,ValidationParameters parameters) {
		ReportItem report = new SheetReportItem("Test2_Sheet5",5);
		report.addAll(Arrays.asList(step_2_5(beans)));
		return report;
	}
	
	private ReportItem[] step_2_5(List<Timetable> timetables){
		ReportItem reportItem1 = new SheetReportItem("Test2_Sheet5_Step1",1);
		ReportItem reportItem2 = new SheetReportItem("Test2_Sheet5_Step2",2);
		ReportItem[] result = new SheetReportItem[2];
		
		for (Timetable timetable: timetables) {
			List<String> vjIds= timetable.getVehicleJourneyIds();
			List<String> vehicleJourneyIds = Timetable.extractObjectIds(timetable.getVehicleJourneys());
			if(vjIds != null){
				//Test 2.5.1
				if(!vehicleJourneyIds.containsAll(vjIds)){
					ReportItem detailReportItem = new DetailReportItem("Test2_Sheet5_Step1_error",Report.STATE.ERROR, "");
					reportItem1.addItem(detailReportItem);
				}else {
					reportItem1.setStatus(Report.STATE.OK);	
				}
			}
			//Test 2.5.2
			if(vehicleJourneyIds != null){
				if(!vjIds.containsAll(vehicleJourneyIds)){
					ReportItem detailReportItem = new DetailReportItem("Test2_Sheet5_Step2_error",Report.STATE.ERROR, "");
					reportItem2.addItem(detailReportItem);
				}else {
					reportItem2.setStatus(Report.STATE.OK);	
				}
			}
		}
		result[0] = reportItem1;
		result[1] = reportItem2;
		return result;
	}

}
