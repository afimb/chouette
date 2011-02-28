package fr.certu.chouette.validation.test2;

import java.util.Arrays;
import java.util.List;

import fr.certu.chouette.model.neptune.Line;
import fr.certu.chouette.plugin.report.Report;
import fr.certu.chouette.plugin.report.ReportItem;
import fr.certu.chouette.plugin.validation.IValidationPlugin;
import fr.certu.chouette.plugin.validation.ValidationClassReportItem;
import fr.certu.chouette.plugin.validation.ValidationStepDescription;
import fr.certu.chouette.validation.report.DetailReportItem;
import fr.certu.chouette.validation.report.SheetReportItem;

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
		reportItem.addAll(Arrays.asList(step_2_6(beans)));
		return reportItem;
	}

	private ReportItem[] step_2_6(List<Line> lines){
		ReportItem[] result = new ReportItem[2];
		ReportItem report1 = new SheetReportItem("Test2_Sheet6_Step1",1);
		ReportItem report2 = new SheetReportItem("Test2_Sheet6_Step2",2);

		for(Line line : lines){
			List<String> lineEnds = line.getLineEnds();
			if(lineEnds != null){
				List<String> objectIds = Line.extractObjectIds(line.getStopPointList());
				if(!objectIds.containsAll(lineEnds)){
					ReportItem detailReportItem = new DetailReportItem("Test2_Sheet6_Step1_error", Report.STATE.ERROR, "");
					report1.addItem(detailReportItem);
				}else {
					report1.setStatus(Report.STATE.OK);	
				}
				List<String> lineEndList  = Line.extractObjectIds(line.getLineEndList());
				if(!lineEnds.containsAll(lineEndList)){
					ReportItem detailReportItem = new DetailReportItem("Test2_Sheet6_Step2_error",Report.STATE.ERROR, "");
					report2.addItem(detailReportItem);
				}else {
					report2.setStatus(Report.STATE.OK);	
				}
			}
		}
		result[0] = report1;
		result[1] = report2;
		return result;
	}
}
