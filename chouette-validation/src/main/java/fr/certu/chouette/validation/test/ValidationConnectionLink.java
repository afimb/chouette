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
		return validate(beans);
	}
	private List<ValidationClassReportItem> validate(List<ConnectionLink> connectionLinks){
		List<ValidationClassReportItem> res = new ArrayList<ValidationClassReportItem>();
		ValidationClassReportItem category2 = new ValidationClassReportItem(ValidationClassReportItem.CLASS.TWO);
		ValidationClassReportItem category3 = new ValidationClassReportItem(ValidationClassReportItem.CLASS.THREE);
		
		ReportItem sheet4 = new SheetReportItem("Test2_Sheet4",4);
		ReportItem sheet3_8 = new SheetReportItem("Test3_Sheet8",8);
		SheetReportItem report2_4 = new SheetReportItem("Test2_Sheet4_Step1",1);
		SheetReportItem report3_8 = new SheetReportItem("Test3_Sheet8_Step1",1);
		
		for(ConnectionLink connectionLink : connectionLinks){
			String startOfLink = connectionLink.getStartOfLinkId();
			String endOfLink = connectionLink.getEndOfLinkId();
			//Test 2.4.1
			if(startOfLink == null || endOfLink == null){
				ReportItem detailReportItem = new DetailReportItem("Test2_Sheet4_Step1_error_a",Report.STATE.ERROR, "");
				report2_4.addItem(detailReportItem);
			}else if(!startOfLink.equals(connectionLink.getStartOfLink().getObjectId()) || !endOfLink.equals(connectionLink.getEndOfLink().getObjectId())){
				ReportItem detailReportItem = new DetailReportItem("Test2_Sheet4_Step1_error_b",Report.STATE.ERROR,"");
				report2_4.addItem(detailReportItem);
			}else {
				report2_4.updateStatus(Report.STATE.OK);
			}
			//Test 3.8.1 a
			
			//Test 3.8.1 b
			
			//Test 3.8.1 c
			
			//Test 3.8.1 d
			
		}
		report2_4.computeDetailItemCount();
		sheet4.addItem(report2_4);
		category2.addItem(sheet4);
		
		res.add(category2);
		return res;
	}
}
