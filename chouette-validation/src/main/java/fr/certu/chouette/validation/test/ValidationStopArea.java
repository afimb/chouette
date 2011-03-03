package fr.certu.chouette.validation.test;

import java.util.ArrayList;
import java.util.List;

import fr.certu.chouette.model.neptune.StopArea;
import fr.certu.chouette.model.neptune.type.ChouetteAreaEnum;
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
public class ValidationStopArea implements IValidationPlugin<StopArea>{

	private ValidationStepDescription validationStepDescription;

	public void init(){
		validationStepDescription = new ValidationStepDescription("Test2.3", ValidationClassReportItem.CLASS.TWO.ordinal());
	}

	@Override
	public ValidationStepDescription getDescription() {
		return validationStepDescription;
	}


	@Override
	public List<ValidationClassReportItem> doValidate(List<StopArea> beans,ValidationParameters parameters) {
		System.out.println("StopAreaValidation");
		List<ValidationClassReportItem> validationClassReportItems = new ArrayList<ValidationClassReportItem>();
		ValidationClassReportItem category2 = new ValidationClassReportItem(ValidationClassReportItem.CLASS.TWO);
		validationClassReportItems.add(category2);
		category2.addAll(validate(beans));
		return validationClassReportItems;
	}
	/**
	 * The test 2.3.1
	 * @param stopAreas
	 * @return
	 */
	private List<ReportItem> validate(List<StopArea> stopAreas) {
		ReportItem sheet3 = new SheetReportItem("Test2_Sheet3",3);
		SheetReportItem report2_3 = new SheetReportItem("Test2_Sheet3_Step1", 1);
		List<ReportItem> result = new ArrayList<ReportItem>();
		for(StopArea stopArea :stopAreas){
			List<String> containedStopIds = stopArea.getContainedStopIds(); 
			if(containedStopIds != null && !containedStopIds.isEmpty()){
				ChouetteAreaEnum areaType = stopArea.getAreaType();
				
				if(areaType.equals(ChouetteAreaEnum.BOARDINGPOSITION) || 
						areaType.equals(ChouetteAreaEnum.QUAY)){
					List<String> stopPoints = StopArea.extractObjectIds(stopArea.getContainedStopPoints()) ;

					if(stopPoints != null){
						if(!containedStopIds.containsAll(stopPoints)){
							ReportItem detailReportItem = new DetailReportItem("Test2_Sheet3_Step1_error", Report.STATE.ERROR,"");
							report2_3.addItem(detailReportItem);
						}else {
							report2_3.updateStatus(Report.STATE.OK);	
						}
					}
				}else {
					List<String> containedAreas = StopArea.extractObjectIds(stopArea.getContainedStopAreas());
					if(containedAreas != null){
						if(!containedStopIds.containsAll(containedAreas)){
							ReportItem detailReportItem =new DetailReportItem("Test2_Sheet3_Step1_error",Report.STATE.ERROR, "");
							report2_3.addItem(detailReportItem);
						}else {
							report2_3.updateStatus(Report.STATE.OK);	
						}
					}
				}
			}
		}
		report2_3.computeDetailItemCount();
		sheet3.addItem(report2_3);
		result.add(sheet3);
		return result;
	}
}
