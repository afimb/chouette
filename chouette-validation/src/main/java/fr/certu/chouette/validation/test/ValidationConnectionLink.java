package fr.certu.chouette.validation.test;

import java.util.ArrayList;
import java.util.List;

import fr.certu.chouette.model.neptune.ConnectionLink;
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
public class ValidationConnectionLink implements IValidationPlugin<ConnectionLink>{

	ValidationStepDescription validationStepDescription;

	public void init(){
		validationStepDescription = new ValidationStepDescription("", ValidationClassReportItem.CLASS.TWO.ordinal());
	}
	@Override
	public ValidationStepDescription getDescription() {
		return validationStepDescription;
	}

	@Override
	public List<ValidationClassReportItem> doValidate(List<ConnectionLink> beans,ValidationParameters parameters) {
		System.out.println("ConnectionLinkValidation");

		List<ValidationClassReportItem> validationClassReportItems = new ArrayList<ValidationClassReportItem>();
		ValidationClassReportItem category2 = new ValidationClassReportItem(ValidationClassReportItem.CLASS.TWO);
		validationClassReportItems.add(category2);
		category2.addAll(validate(beans));
		return validationClassReportItems;
	}
	private List<ReportItem> validate(List<ConnectionLink> connectionLinks){
		ReportItem sheet4 = new SheetReportItem("Test2_Sheet4",4);
		SheetReportItem report2_4 = new SheetReportItem("Test2_Sheet4_Step1",1);
		List<ReportItem> result = new ArrayList<ReportItem>();
		
		for(ConnectionLink connectionLink : connectionLinks){
			String startOfLink = connectionLink.getStartOfLinkId();
			String endOfLink = connectionLink.getEndOfLinkId();
			if(startOfLink == null || endOfLink == null){
				ReportItem detailReportItem = new DetailReportItem("Test2_Sheet4_Step1_error_a",Report.STATE.ERROR, "");
				report2_4.addItem(detailReportItem);
			}else if(!startOfLink.equals(connectionLink.getStartOfLink().getObjectId()) || !endOfLink.equals(connectionLink.getEndOfLink().getObjectId())){
				ReportItem detailReportItem = new DetailReportItem("Test2_Sheet4_Step1_error_b",Report.STATE.ERROR,"");
				report2_4.addItem(detailReportItem);
			}else {
				report2_4.updateStatus(Report.STATE.OK);
			}
		}
		report2_4.computeDetailItemCount();
		sheet4.addItem(report2_4);
		result.add(sheet4);
		return result;
	}
}
