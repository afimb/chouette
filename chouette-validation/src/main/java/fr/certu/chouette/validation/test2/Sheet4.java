package fr.certu.chouette.validation.test2;

import java.util.List;

import fr.certu.chouette.model.neptune.ConnectionLink;
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
public class Sheet4 implements IValidationPlugin<ConnectionLink>{

	ValidationStepDescription validationStepDescription;

	public void init(){
		validationStepDescription = new ValidationStepDescription("Test2.4", ValidationClassReportItem.CLASS.TWO.ordinal());
	}
	@Override
	public ValidationStepDescription getDescription() {

		return validationStepDescription;
	}

	@Override
	public ReportItem doValidate(List<ConnectionLink> beans) {
		ReportItem reportItem = new SheetReportItem("Test2_Sheet4",4);
		reportItem.addItem(step_2_4_1(beans));
		return reportItem;
	}

	private ReportItem step_2_4_1(List<ConnectionLink> connectionLinks){
		ReportItem report = new SheetReportItem("Test2_Sheet4_Step1",1);
		for(ConnectionLink connectionLink : connectionLinks){
			String startOfLink = connectionLink.getStartOfLinkId();
			String endOfLink = connectionLink.getEndOfLinkId();
			if(startOfLink == null || endOfLink == null){
				ReportItem detailReportItem = new DetailReportItem("Test2_Sheet4_Step1_error_a",Report.STATE.ERROR, "");
				report.addItem(detailReportItem);
			}else if(!startOfLink.equals(connectionLink.getStartOfLink().getObjectId()) || !endOfLink.equals(connectionLink.getEndOfLink().getObjectId())){
				ReportItem detailReportItem = new DetailReportItem("Test2_Sheet4_Step1_error_b",Report.STATE.ERROR,startOfLink,endOfLink);
				report.addItem(detailReportItem);
			}else {
				report.setStatus(Report.STATE.OK);
			}
		}
		return report;
	}
}
