package fr.certu.chouette.validation.test;

import java.util.ArrayList;
import java.util.List;

import fr.certu.chouette.model.neptune.AccessLink;
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
public class ValidationAccessLink implements IValidationPlugin<AccessLink>{

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
	public List<ValidationClassReportItem> doValidate(List<AccessLink> beans,
			ValidationParameters parameters) {
		System.err.println("AccessLinkValidation");
		return validate(beans, parameters);
	}

	private List<ValidationClassReportItem> validate(List<AccessLink> accessLinks,
			ValidationParameters parameters) {
		List<ValidationClassReportItem> res = new ArrayList<ValidationClassReportItem>();
		ValidationClassReportItem category2 = new ValidationClassReportItem(ValidationClassReportItem.CLASS.TWO);
		ReportItem sheet2_25 = new SheetReportItem("Test2_Sheet25",25);
		SheetReportItem report2_25 = new SheetReportItem("Test2_Sheet25_Step1", 1);
		
		for (AccessLink accessLink : accessLinks) {
			//Test 2.25.1
			if(accessLink.getStartOfLinkId() == null || accessLink.getEndOfLinkId() == null){
				ReportItem detailReportItem = new DetailReportItem("Test2_Sheet25_Step1_error_a",Report.STATE.ERROR);
				report2_25.addItem(detailReportItem);
			}else if(accessLink.getStopArea() != null || accessLink.getAccessPoint() != null){
				report2_25.updateStatus(Report.STATE.OK);
			}else {
				ReportItem detailReportItem = new DetailReportItem("Test2_Sheet25_Step1_error_b",Report.STATE.ERROR);
				report2_25.addItem(detailReportItem);
			}		
		}
		report2_25.computeDetailItemCount();
		sheet2_25.addItem(report2_25);
		category2.addItem(sheet2_25);
		res.add(category2);
		return res;
	}

}
