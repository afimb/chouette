package fr.certu.chouette.validation.test2;

import java.util.List;

import fr.certu.chouette.model.neptune.Line;
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
public class Sheet7 implements IValidationPlugin<Line>{

	private ValidationStepDescription description;
	public void init(){
		description = new ValidationStepDescription("Test2.7", ValidationClassReportItem.CLASS.TWO.ordinal());
	}
	@Override
	public ValidationStepDescription getDescription() {
		return description;
	}

	@Override
	public ReportItem doValidate(List<Line> beans) {
		ReportItem report = new SheetReportItem("Test2_Sheet7",7);
		report.addItem(step_2_7_1(beans));
		return report;
	}
	
	private ReportItem step_2_7_1(List<Line> lines){
		ReportItem report = new SheetReportItem("Test2_Sheet7_Step1",1);
		for (Line line : lines) {
			List<String> routeIds = Line.extractObjectIds(line.getRoutes());
			if(!routeIds.containsAll(line.getRouteIds())){
				ReportItem detailReportItem = new DetailReportItem("Test2_Sheet7_Step1_error",Report.STATE.ERROR, "");
				report.addItem(detailReportItem);
			}else {
				report.setStatus(Report.STATE.OK);
			}
		}
		
		return report;
		
	}

}
