package fr.certu.chouette.validation.test;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import fr.certu.chouette.model.neptune.PTLink;
import fr.certu.chouette.model.neptune.Route;
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
public class ValidationRoute implements IValidationPlugin<Route> {
	private static final Logger logger = Logger.getLogger(ValidationRoute.class);

	private ValidationStepDescription description;

	public void init() {
		//TODO
		description = new ValidationStepDescription("", ValidationClassReportItem.CLASS.TWO.ordinal());
	}

	@Override
	public ValidationStepDescription getDescription() {

		return description;
	}

	@Override
	public List<ValidationClassReportItem> doValidate(List<Route> beans, ValidationParameters parameters) {
		return validate(beans);
	}

	private List<ValidationClassReportItem> validate(List<Route> routes) {
		
    	logger.info("start validate "+routes.size()+" routes");
		List<ValidationClassReportItem> result = new ArrayList<ValidationClassReportItem>();
		ValidationClassReportItem category2 = new ValidationClassReportItem(ValidationClassReportItem.CLASS.TWO);
//		ValidationClassReportItem category3 = new ValidationClassReportItem(ValidationClassReportItem.CLASS.THREE);

		ReportItem sheet2_8 = new SheetReportItem("Test2_Sheet8", 8);
		ReportItem sheet2_9 = new SheetReportItem("Test2_Sheet9", 9);
		ReportItem sheet2_14 = new SheetReportItem("Test2_Sheet14", 14);
		// ReportItem sheet3_11 = new SheetReportItem("Test3_Sheet11", 11);

		SheetReportItem report2_8_1 = new SheetReportItem("Test2_Sheet8_Step1", 1);
		//SheetReportItem report2_8_3 = new SheetReportItem("Test2_Sheet8_Step3", 3);
		SheetReportItem report2_9_1 = new SheetReportItem("Test2_Sheet9_Step1", 1);
		SheetReportItem report2_14_2 = new SheetReportItem("Test2_Sheet14_Step2", 2);
		// SheetReportItem report3_11_1 = new SheetReportItem("Test3_Sheet11_Step1", 1);

		for (Route route : routes) {
                        if (route.getJourneyPatterns() == null) {
                            report2_8_1.updateStatus(Report.STATE.UNCHECK);
                        } else {
				List<String> journeyPatternIds = Route.extractObjectIds(route.getJourneyPatterns());
				//Test 2.8.1
                                if (journeyPatternIds == null)
                                    journeyPatternIds = new ArrayList<String>();
                                if (!journeyPatternIds.containsAll(route.getJourneyPatternIds())) {
                                    ReportItem detailReportItem = new DetailReportItem("Test2_Sheet8_Step1_error", Report.STATE.ERROR);
                                    report2_8_1.addItem(detailReportItem);
                                } else {
                                    report2_8_1.updateStatus(Report.STATE.OK);
                                }
			}

			//Test 2.9.1
			String wayBackRouteId = route.getWayBackRouteId();
			if (wayBackRouteId != null) {
				//Test 2.9.1
				int count = 0;
				for (Route route2 : routes) {
					if (!route.getObjectId().equals(route2.getObjectId())) {
						if (wayBackRouteId.equals(route2.getObjectId())) {
							count++;
						}
					}
				}
				if (count == 0) {
					ReportItem detailReportItem = new DetailReportItem("Test2_Sheet9_Step1_error", Report.STATE.ERROR);
					report2_9_1.addItem(detailReportItem);
				} else {
					report2_9_1.updateStatus(Report.STATE.OK);
				}
			}
			List<PTLink> ptLinks = route.getPtLinks();
			if (ptLinks != null)
			{
				//Test  2.14.2 , ( 3.11.1 abandonned) 
				for (int i = 0; i < ptLinks.size(); i++) 
				{
					PTLink ptLink = ptLinks.get(i);
					int count1 = 0;
					int count2 = 0;
					boolean error = false;
					for (int j = i + 1; j < ptLinks.size() && !error; j++) 
					{
						PTLink ptLink2 = ptLinks.get(j);
						if (ptLink.getEndOfLinkId().equals(ptLink2.getEndOfLinkId()) || ptLink.getStartOfLinkId().equals(ptLink2.getStartOfLinkId())) 
						{
							error = true;
						}
						if (ptLink.getStartOfLinkId().equals(ptLink2.getEndOfLinkId())) {
							count1++;
						}
						if (ptLink2.getStartOfLinkId().equals(ptLink.getEndOfLinkId())) {
							count2++;
						}
					}
					if (count1 > 1 || count2 > 1 || error) {
						ReportItem detailReportItem = new DetailReportItem("Test2_Sheet14_Step2_error", Report.STATE.ERROR,ptLink.getObjectId() );
						report2_14_2.addItem(detailReportItem);
//						ReportItem detailReportItem = new DetailReportItem("Test3_Sheet11_Step1_warning", Report.STATE.WARNING);
//						report3_11_1.addItem(detailReportItem);
					} else {
						report2_14_2.updateStatus(Report.STATE.OK);
//						report3_11_1.updateStatus(Report.STATE.OK);
					}
				}
			}

		}

		report2_8_1.computeDetailItemCount();
		//report2_8_3.computeDetailItemCount();
		report2_9_1.computeDetailItemCount();
		report2_14_2.computeDetailItemCount();
//		report3_11.computeDetailItemCount();

		sheet2_8.addItem(report2_8_1);
		//sheet8.addItem(report2_8_3);
		sheet2_9.addItem(report2_9_1);
		sheet2_14.addItem(report2_14_2);
//		sheet3_11.addItem(report3_11);

		category2.addItem(sheet2_8);
		category2.addItem(sheet2_9);
		category2.addItem(sheet2_14);
//		category3.addItem(sheet3_11);

		result.add(category2);
//		result.add(category3);
    	logger.info("route validation terminated");

		return result;
	}
}
