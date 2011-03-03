package fr.certu.chouette.validation.test;

import java.util.ArrayList;
import java.util.List;

import fr.certu.chouette.model.neptune.JourneyPattern;
import fr.certu.chouette.model.neptune.PTLink;
import fr.certu.chouette.model.neptune.Route;
import fr.certu.chouette.model.neptune.StopPoint;
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
public class ValidationRoute implements IValidationPlugin<Route>{

	private ValidationStepDescription description;
	public void init(){
		//TODO
		description = new ValidationStepDescription("", ValidationClassReportItem.CLASS.TWO.ordinal());
	}
	@Override
	public ValidationStepDescription getDescription() {

		return description;
	}

	@Override
	public List<ValidationClassReportItem> doValidate(List<Route> beans,ValidationParameters parameters) {
		System.out.println("RouteValidation");
		List<ValidationClassReportItem> validationClassReportItems = new ArrayList<ValidationClassReportItem>();
		ValidationClassReportItem category2 = new ValidationClassReportItem(ValidationClassReportItem.CLASS.TWO);
		category2.addAll(validate(beans));
		validationClassReportItems.add(category2);
		return validationClassReportItems;
	}

	private List<ReportItem> validate(List<Route> routes){
		ReportItem sheet8 = new SheetReportItem("Test2_Sheet8",8);
		ReportItem sheet9 = new SheetReportItem("Test2_Sheet9",9);
		
		SheetReportItem report2_8_1 = new SheetReportItem("Test2_Sheet8_Step1",1);
		SheetReportItem report2_8_2 = new SheetReportItem("Test2_Sheet8_Step2",2);
		SheetReportItem report2_9_1 = new SheetReportItem("Test2_Sheet9_Step1", 1);
		
		List<ReportItem> result = new ArrayList<ReportItem>();
		for (Route route : routes) {
			List<String> journeyPatternIds = Route.extractObjectIds(route.getJourneyPatterns());
			//Test 2.8.1
			if(!route.getJourneyPatternIds().containsAll(journeyPatternIds)){
				ReportItem detailReportItem = new DetailReportItem("Test2_Sheet8_Step1_error", Report.STATE.ERROR, "");
				report2_8_1.addItem(detailReportItem);	
			}else{
				report2_8_1.updateStatus(Report.STATE.OK);
			}
			//Test 2.8.2
			if(route.getJourneyPatterns() != null){
				for(JourneyPattern journeyPattern : route.getJourneyPatterns()){
					String routeObjectId = route.getObjectId();
					if(!journeyPattern.getRouteId().equals(routeObjectId)){
						ReportItem detailReportItem = new DetailReportItem("Test2_Sheet8_Step2_error", Report.STATE.ERROR, "");
						report2_8_2.addItem(detailReportItem);	
					}else{
						report2_8_2.updateStatus(Report.STATE.OK);
					}
					//Test 2.8.3 a
					for(StopPoint stopPoint : journeyPattern.getStopPoints()){
						if(!journeyPattern.getStopPointIds().contains(stopPoint.getObjectId())){
							ReportItem detailReportItem = new DetailReportItem("Test2_Sheet8_Step3_a_error", Report.STATE.ERROR, stopPoint.getObjectId());
							report2_8_2.addItem(detailReportItem);
						}
						//Test 2.8.3 b
						for(PTLink ptLink : route.getPtLinks()){
							if(!stopPoint.getObjectId().equals(ptLink.getStartOfLinkId()) && !stopPoint.getObjectId().equals(ptLink.getEndOfLinkId())){
								ReportItem detailReportItem = new DetailReportItem("Test2_Sheet8_Step3_b_error", Report.STATE.ERROR, stopPoint.getObjectId(), ptLink.getStartOfLinkId(), ptLink.getEndOfLinkId());
								report2_8_2.addItem(detailReportItem);
							}
							//Test 2.8.3 c
							if(!route.getPtLinkIds().contains(ptLink.getObjectId())){
								ReportItem detailReportItem = new DetailReportItem("Test2_Sheet8_Step3_c_error", Report.STATE.ERROR, ptLink.getObjectId());
								report2_8_2.addItem(detailReportItem);
							}
						}
					}
					//Test 2.8.3 d
					if(!route.getObjectId().equals(journeyPattern.getRouteId())){
						ReportItem detailReportItem = new DetailReportItem("Test2_Sheet8_Step3_d_error", Report.STATE.ERROR,route.getObjectId());
						report2_8_2.addItem(detailReportItem);
					}
				}
			}
			
			//Test 2.9.1
			String wayBackRouteId = route.getWayBackRouteId(); 
			if(wayBackRouteId != null){
				//Test 2.9.1
				for (Route route2 : routes) {
					if(!route.getObjectId().equals(route2.getObjectId())){
						if(!wayBackRouteId.equals(route2.getObjectId())){
							ReportItem detailReportItem = new DetailReportItem("Test2_Sheet9_Step1_error", Report.STATE.ERROR, "");
							report2_9_1.addItem(detailReportItem);	
						}else{
							report2_9_1.updateStatus(Report.STATE.OK);
						}
					}
				}
			}
		}
		
		report2_8_1.computeDetailItemCount();
		report2_8_2.computeDetailItemCount();
		report2_9_1.computeDetailItemCount();
		
		sheet8.addItem(report2_8_1);
		sheet8.addItem(report2_8_2);
		sheet9.addItem(report2_9_1);
		
		result.add(sheet8);
		result.add(sheet9);
		return result;
	}
}
