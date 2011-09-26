package fr.certu.chouette.validation.test;

import java.util.ArrayList;
import java.util.List;

import fr.certu.chouette.model.neptune.RestrictionConstraint;
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
public class ValidationRestrictionConstraint implements IValidationPlugin<RestrictionConstraint> {

	private ValidationStepDescription validationStepDescription;

	public void init() {
		validationStepDescription = new ValidationStepDescription("Test restrictionConstraints", ValidationClassReportItem.CLASS.TWO.ordinal());
	}

	@Override
	public ValidationStepDescription getDescription() {
		return validationStepDescription;
	}

	@Override
	public List<ValidationClassReportItem> doValidate(List<RestrictionConstraint> beans, ValidationParameters parameters) {
		return validate(beans);
	}

	/**
	 * The tests 2.12 and 2.13 
	 * @param constraints
	 * @return
	 */
	private List<ValidationClassReportItem> validate(List<RestrictionConstraint> constraints) 
	{
		ValidationClassReportItem category2 = new ValidationClassReportItem(ValidationClassReportItem.CLASS.TWO);
		ReportItem sheet2_12 = new SheetReportItem("Test2_Sheet12", 12);
		ReportItem sheet2_13 = new SheetReportItem("Test2_Sheet13", 13);
		SheetReportItem report2_12 = new SheetReportItem("Test2_Sheet12_Step1", 1);
		SheetReportItem report2_13 = new SheetReportItem("Test2_Sheet13_Step1", 1);
		
		List<ValidationClassReportItem> result = new ArrayList<ValidationClassReportItem>();
		if (constraints != null) 
		{
			report2_12.updateStatus(Report.STATE.OK);
			report2_13.updateStatus(Report.STATE.OK);
			for (RestrictionConstraint restrictionConstraint : constraints) 
			{
				// Test 2.12
				List<String> realStopAreaIds = RestrictionConstraint.extractObjectIds(restrictionConstraint.getStopAreas());
				for (String areaId : restrictionConstraint.getAreaIds())
				{
					if (!realStopAreaIds.contains(areaId)) 
					{
						ReportItem detailReportItem = new DetailReportItem("Test2_Sheet12_Step1_error", Report.STATE.ERROR, restrictionConstraint.getName(), areaId);
						report2_12.addItem(detailReportItem);
					} 
				}

				//Test 2.13
				if (restrictionConstraint.getLineIdShortCut() != null) 
				{
					if (restrictionConstraint.getLine() == null) 
					{
						ReportItem detailReportItem = new DetailReportItem("Test2_Sheet13_Step1_error", Report.STATE.ERROR);
						report2_13.addItem(detailReportItem);
					} 
				}
			}
		}
		report2_12.computeDetailItemCount();
		report2_13.computeDetailItemCount();

		sheet2_12.addItem(report2_12);
		sheet2_13.addItem(report2_13);

		category2.addItem(sheet2_12);
		category2.addItem(sheet2_13);

		result.add(category2);

		return result;

	}
}
