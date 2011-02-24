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
public class Sheet2 implements IValidationPlugin<Line>{

	private ValidationStepDescription validationStepDescription;
	
	public void init(){
		validationStepDescription = new ValidationStepDescription("Test2.2", ValidationClassReportItem.CLASS.TWO.ordinal());
	}
	
	@Override
	public ValidationStepDescription getDescription() {
		return validationStepDescription;
	}
	@Override
	public ReportItem doValidate(List<Line> beans) {
		ReportItem reportItem = new SheetReportItem("Test2_Sheet2",2);
		reportItem.addItem(step_2_2_1(beans));
		return reportItem;
	}
	
	private ReportItem step_2_2_1(List<Line> lines){
		ReportItem reportItem = new SheetReportItem("Test2_Sheet2_Step1",1);
		//TODO Not implemted 'cause GroupOfLine is required
		ReportItem detailItem = new DetailReportItem("Test2_Sheet2_Step1_unchecked");
		reportItem.addItem(detailItem);
		reportItem.setStatus(Report.STATE.UNCHECK);
		return reportItem;
	}

}
