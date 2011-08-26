package fr.certu.chouette.validation.test;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import fr.certu.chouette.model.neptune.JourneyPattern;
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
public class ValidationJourneyPattern implements IValidationPlugin<JourneyPattern> {

	private static final Logger logger = Logger.getLogger(ValidationJourneyPattern.class);
	private ValidationStepDescription validationStepDescription;

	public void init() {
		validationStepDescription = new ValidationStepDescription("", ValidationClassReportItem.CLASS.TWO.ordinal());
	}

	@Override
	public ValidationStepDescription getDescription() {
		return validationStepDescription;
	}

	@Override
	public List<ValidationClassReportItem> doValidate(List<JourneyPattern> beans, ValidationParameters parameters) {
		return validate(beans);
	}

	private List<ValidationClassReportItem> validate(List<JourneyPattern> journeyPatterns) {

		logger.info("start validate "+journeyPatterns.size()+" journey patterns");
		ValidationClassReportItem category2 = new ValidationClassReportItem(ValidationClassReportItem.CLASS.TWO);

		ReportItem sheet8 = new SheetReportItem("Test2_Sheet8", 8);
		ReportItem sheet15 = new SheetReportItem("Test2_Sheet15", 15);
		ReportItem sheet16 = new SheetReportItem("Test2_Sheet16", 16);

		SheetReportItem report2_8_2 = new SheetReportItem("Test2_Sheet8_Step2", 2);
		SheetReportItem report2_15_1 = new SheetReportItem("Test2_Sheet15_Step1", 1);
		//        SheetReportItem report2_15_2 = new SheetReportItem("Test2_Sheet15_Step2", 2);

		SheetReportItem report2_16_1 = new SheetReportItem("Test2_Sheet16_Step1", 1);

		List<ValidationClassReportItem> result = new ArrayList<ValidationClassReportItem>();
		if (journeyPatterns != null) 
		{
			for (JourneyPattern journeyPattern : journeyPatterns) 
			{
				List<String> stopPointIds = JourneyPattern.extractObjectIds(journeyPattern.getStopPoints());

				//Test 2.8.2
				if (journeyPattern.getRoute() == null) 
				{
					ReportItem detailReportItem = new DetailReportItem("Test2_Sheet8_Step2_error", Report.STATE.ERROR);
					report2_8_2.addItem(detailReportItem);
				} 
				else 
				{
					report2_8_2.updateStatus(Report.STATE.OK);
				}

				//Test 2.15.1
				if (journeyPattern.getStopPointIds() == null || !stopPointIds.containsAll(journeyPattern.getStopPointIds())) 
				{
					ReportItem detailReportItem = new DetailReportItem("Test2_Sheet15_Step1_error", Report.STATE.ERROR, journeyPattern.getName() + "(" + journeyPattern.getObjectId() + ")");
					report2_15_1.addItem(detailReportItem);
				} 
				else 
				{
					report2_15_1.updateStatus(Report.STATE.OK);
				}

				//2.16.1
				String lineIdShortCutId = journeyPattern.getLineIdShortcut();
				if (lineIdShortCutId != null) 
				{
					if (journeyPattern.getRoute() != null)
					{
						if (journeyPattern.getRoute().getLine() != null)
						{
							if (!journeyPattern.getRoute().getLine().getObjectId().equals(lineIdShortCutId)) 
							{
								ReportItem detailReportItem = new DetailReportItem("Test2_Sheet16_Step1_error", Report.STATE.ERROR);
								report2_16_1.addItem(detailReportItem);
							}
							else 
							{
								report2_16_1.updateStatus(Report.STATE.OK);
							}
						}
					}
				}
			}
		}
		report2_8_2.computeDetailItemCount();
		report2_15_1.computeDetailItemCount();
		//        report2_15_2.computeDetailItemCount();
		report2_16_1.computeDetailItemCount();

		sheet8.addItem(report2_8_2);
		sheet15.addItem(report2_15_1);
		//        sheet15.addItem(report2_15_2);
		sheet16.addItem(report2_16_1);

		category2.addItem(sheet8);
		category2.addItem(sheet15);
		category2.addItem(sheet16);
		result.add(category2);
		logger.info("journey pattern validation terminated");
		return result;
	}
}
