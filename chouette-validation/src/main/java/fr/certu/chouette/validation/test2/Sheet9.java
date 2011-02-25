/**
 * Chouette project
 */
package fr.certu.chouette.validation.test2;

import java.util.List;

import fr.certu.chouette.model.neptune.Route;
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
public class Sheet9 implements IValidationPlugin<Route>{

	private ValidationStepDescription validationStepDescription;
	
	public void init(){
		validationStepDescription = new ValidationStepDescription("Test2.9", ValidationClassReportItem.CLASS.TWO.ordinal());
	}
	
	@Override
	public ValidationStepDescription getDescription() {
		return validationStepDescription;
	}
	@Override
	public ReportItem doValidate(List<Route> beans) {
		ReportItem report = new SheetReportItem("Test2_Sheet9",9);
		report.addItem(step_2_9(beans));
		return report;
	}
	
	private ReportItem step_2_9(List<Route> routes){
		ReportItem reportItem = new SheetReportItem("Test2_Sheet9_Step1", 1);
		for (Route route : routes) {
			String wayBackRouteId = route.getWayBackRouteId(); 
			if(wayBackRouteId != null){
				//Test 2.9.1
				for (Route route2 : routes) {
					if(!route.getObjectId().equals(route2.getObjectId())){
						if(!wayBackRouteId.equals(route2.getObjectId())){
							ReportItem detailReportItem = new DetailReportItem("Test2_Sheet9_Step1_error", Report.STATE.ERROR, "");
							reportItem.addItem(detailReportItem);	
						}else{
							reportItem.setStatus(Report.STATE.OK);
						}
					}
				}
			}
		}
		return reportItem;
		
	}

}
