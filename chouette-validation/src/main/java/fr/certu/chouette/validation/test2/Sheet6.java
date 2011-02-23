package fr.certu.chouette.validation.test2;

import java.util.List;

import fr.certu.chouette.model.neptune.Line;
import fr.certu.chouette.model.neptune.NeptuneIdentifiedObject;
import fr.certu.chouette.model.neptune.StopPoint;
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
public class Sheet6 implements IValidationPlugin<Line>{

	private ValidationStepDescription validationStepDescription;
	public void init(){
		validationStepDescription = new ValidationStepDescription("Test2.6", ValidationClassReportItem.CLASS.TWO.ordinal());
	}
	@Override
	public ValidationStepDescription getDescription() {
		return validationStepDescription;
	}

	@Override
	public ReportItem doValidate(List<Line> beans) {
		ReportItem reportItem = new SheetReportItem("Test2_Sheet6",6);
		reportItem.addItem(step_2_6_1(beans));
		reportItem.addItem(step_2_6_2(beans));
		return reportItem;
	}

	private ReportItem step_2_6_1(List<Line> lines){
		ReportItem report = new SheetReportItem("Test2_Sheet6_Step1",1);
		for(Line line : lines){
			List<String> lineEnds = line.getLineEnds();
			if(lineEnds != null){
				List<String> objectIds = NeptuneIdentifiedObject.extractObjectIds(line.getStopPointList());
				if(!objectIds.containsAll(lineEnds)){
					ReportItem detailReportItem = ValidationUtils.addDetail("Test2_Sheet6_Step1_error", Report.STATE.ERROR, "");
					report.addItem(detailReportItem);
				}
			}
		}
		return report;
	}

	private ReportItem step_2_6_2(List<Line> lines){
		ReportItem report = new SheetReportItem("Test2_Sheet6_Step2",2);
		for(Line line : lines){
			List<String> lineEnds = line.getLineEnds();
			if(lineEnds != null){
				for(StopPoint stopPoint : line.getLineEndList()){
					if(!lineEnds.contains(stopPoint.getObjectId())){
						ReportItem detailReportItem = ValidationUtils.addDetail("Test2_Sheet6_Step2_error", Report.STATE.ERROR, "");
						report.addItem(detailReportItem);
					}
				}
			}
		}
		return report;
	}

}
