package fr.certu.chouette.validation.test2;

import java.util.Arrays;
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
public class Sheet8 implements IValidationPlugin<Route>{

	private ValidationStepDescription description;
	public void init(){
		description = new ValidationStepDescription("Test2.8", ValidationClassReportItem.CLASS.TWO.ordinal());
	}
	@Override
	public ValidationStepDescription getDescription() {

		return description;
	}

	@Override
	public ReportItem doValidate(List<Route> beans,ValidationParameters parameters) {
		ReportItem report = new SheetReportItem("Test2_Sheet8",8);
		report.addAll(Arrays.asList(step_2_8(beans)));
		return report;
	}

	private ReportItem[] step_2_8(List<Route> routes){
		ReportItem report1 = new SheetReportItem("Test2_Sheet8_Step1",1);
		ReportItem report2 = new SheetReportItem("Test2_Sheet8_Step2",2);
		ReportItem[] result = new SheetReportItem[2];
		for (Route route : routes) {
			List<String> journeyPatternIds = Route.extractObjectIds(route.getJourneyPatterns());
			//Test 2.8.1
			if(!route.getJourneyPatternIds().containsAll(journeyPatternIds)){
				ReportItem detailReportItem = new DetailReportItem("Test2_Sheet8_Step1_error", Report.STATE.ERROR, "");
				report1.addItem(detailReportItem);	
			}else{
				report1.setStatus(Report.STATE.OK);
			}
			//Test 2.8.2
			if(route.getJourneyPatterns() != null){
				for(JourneyPattern journeyPattern : route.getJourneyPatterns()){
					String routeObjectId = route.getObjectId();
					if(!journeyPattern.getRouteId().equals(routeObjectId)){
						ReportItem detailReportItem = new DetailReportItem("Test2_Sheet8_Step2_error", Report.STATE.ERROR, "");
						report2.addItem(detailReportItem);	
					}else{
						report2.setStatus(Report.STATE.OK);
					}
					//Test 2.8.3 a
					for(StopPoint stopPoint : journeyPattern.getStopPoints()){
						if(!journeyPattern.getStopPointIds().contains(stopPoint.getObjectId())){
							ReportItem detailReportItem = new DetailReportItem("Test2_Sheet8_Step3_a_error", Report.STATE.ERROR, "");
							report2.addItem(detailReportItem);
						}
						//Test 2.8.3 b
						for(PTLink ptLink : route.getPtLinks()){
							if(!stopPoint.getObjectId().equals(ptLink.getStartOfLinkId()) || !stopPoint.getObjectId().equals(ptLink.getEndOfLinkId())){
								ReportItem detailReportItem = new DetailReportItem("Test2_Sheet8_Step3_b_error", Report.STATE.ERROR, "");
								report2.addItem(detailReportItem);
							}
							//Test 2.8.3 c
							if(!route.getPtLinkIds().contains(ptLink.getObjectId())){
								ReportItem detailReportItem = new DetailReportItem("Test2_Sheet8_Step3_c_error", Report.STATE.ERROR, "");
								report2.addItem(detailReportItem);
							}
						}
					}
					//Test 2.8.3 d
					if(!route.getObjectId().equals(journeyPattern.getRouteId())){
						ReportItem detailReportItem = new DetailReportItem("Test2_Sheet8_Step3_d_error", Report.STATE.ERROR,"");
						report2.addItem(detailReportItem);
					}
				}
			}
			
		}
		result[0] = report1;
		result[1] = report2;
		return result;
	}
}
