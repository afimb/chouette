package fr.certu.chouette.validation.test2;

import java.util.List;

import fr.certu.chouette.model.neptune.StopArea;
import fr.certu.chouette.model.neptune.StopPoint;
import fr.certu.chouette.model.neptune.type.ChouetteAreaEnum;
import fr.certu.chouette.plugin.report.Report;
import fr.certu.chouette.plugin.report.ReportItem;
import fr.certu.chouette.plugin.validation.IValidationPlugin;
import fr.certu.chouette.plugin.validation.ValidationClassReportItem;
import fr.certu.chouette.plugin.validation.ValidationStepDescription;
import fr.certu.chouette.validation.report.SheetReportItem;
import fr.certu.chouette.validation.util.ValidationUtils;

/**
 * 
 * @author mamadou keira
 *
 */
public class Sheet3 implements IValidationPlugin<StopArea>{

	private ValidationStepDescription validationStepDescription;

	public void init(){
		validationStepDescription = new ValidationStepDescription("Test2.3", ValidationClassReportItem.CLASS.TWO.ordinal());
	}

	@Override
	public ValidationStepDescription getDescription() {
		return validationStepDescription;
	}

	/**
	 * The test 2.3.1
	 * @param stopAreas
	 * @return
	 */
	private ReportItem step_2_3_1(List<StopArea> stopAreas) {
		ReportItem report = new SheetReportItem("Test2_Sheet3_Step1", 1);
		for(StopArea stopArea :stopAreas){
			List<String> containedStopIds = stopArea.getContainedStopIds(); 
			if(containedStopIds != null && !containedStopIds.isEmpty()){
				ChouetteAreaEnum areaType = stopArea.getAreaType(); 
				if(areaType.equals(ChouetteAreaEnum.BOARDINGPOSITION) || 
						areaType.equals(ChouetteAreaEnum.QUAY)){
					List<StopPoint> stopPoints = stopArea.getContainedStopPoints();

					if(stopPoints != null){
						for(StopPoint stopPoint : stopPoints){
							if(!containedStopIds.contains(stopPoint.getObjectId())){
								ReportItem detailReportItem =ValidationUtils.addDetail("Test2_Sheet3_Step1_error", Report.STATE.ERROR,"");
								report.addItem(detailReportItem);
							}	
						}	
					}
				}else {
					List<StopArea> containedAreas = stopArea.getContainedStopAreas();
					if(containedAreas != null){
						for(StopArea containedArea : containedAreas){
							if(!containedStopIds.contains(containedArea.getObjectId())){
								ReportItem detailReportItem =ValidationUtils.addDetail("Test2_Sheet3_Step1_error", Report.STATE.ERROR, "");
								report.addItem(detailReportItem);
							}
						}
					}
				}
			}else {
				ReportItem detailReportItem =ValidationUtils.addDetail("Test2_Sheet3_Step1_unchecked", Report.STATE.UNCHECK, "");
				report.addItem(detailReportItem);
				report.setStatus(Report.STATE.UNCHECK);
			}
		}	
		return report;
	}

	@Override
	public ReportItem doValidate(List<StopArea> beans) {
		ReportItem reportItem = new SheetReportItem("Test2_Sheet3",3);
		reportItem.addItem(step_2_3_1(beans));
		return reportItem;
	}
}
