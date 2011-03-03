package fr.certu.chouette.validation.test;

import java.util.ArrayList;
import java.util.List;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.PrecisionModel;
import com.vividsolutions.jts.operation.distance.DistanceOp;

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
public class ValidationStopPoint implements IValidationPlugin<StopPoint>{

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
	public List<ValidationClassReportItem> doValidate(List<StopPoint> beans,ValidationParameters parameters) {	
		System.out.println("StopPointValidation "+beans.size());
		return validate(beans,parameters);	
	}
	
	private List<ValidationClassReportItem> validate(List<StopPoint> stopPoints,ValidationParameters parameters){
		
		ValidationClassReportItem category2 = new ValidationClassReportItem(ValidationClassReportItem.CLASS.TWO);
		ValidationClassReportItem category3 = new ValidationClassReportItem(ValidationClassReportItem.CLASS.THREE);
		
		ReportItem sheet10 = new SheetReportItem("Test2_Sheet10",10);
		ReportItem sheet11 = new SheetReportItem("Test2_Sheet11",11);
		
		ReportItem sheet3_1 = new SheetReportItem("Test3_Sheet1",1);
		ReportItem sheet3_2 = new SheetReportItem("Test3_Sheet2",2);
		
		SheetReportItem report2_10_1 = new SheetReportItem("Test2_Sheet10_Step1",1);
		SheetReportItem report2_11_1 = new SheetReportItem("Test2_Sheet11_Step1",1);
		SheetReportItem report3_1_1 = new SheetReportItem("Test3_Sheet1_Step1",1);
		SheetReportItem report3_2_1 = new SheetReportItem("Test3_Sheet2_Step1",1);
		
		List<ValidationClassReportItem> result = new ArrayList<ValidationClassReportItem>();
		
		int size = stopPoints.size();
		for (int i=0;i<size;i++) {
			StopPoint stopPoint = stopPoints.get(i);
			
			//Test2.10.1
			String lineIdShortcut = stopPoint.getLineIdShortcut();
			if(lineIdShortcut != null){
				String lineObjectId = stopPoint.getLine().getObjectId();
				if(!lineIdShortcut.equals(lineObjectId))
				{
					ReportItem detailReportItem = new DetailReportItem("Test2_Sheet10_Step1_error", Report.STATE.ERROR, "");
					report2_10_1.addItem(detailReportItem);	
				}
				else
				{
					report2_10_1.updateStatus(Report.STATE.OK);	
				}
			}
			//Test2.11.1
			String ptNetworkIdShortcut = stopPoint.getPtNetworkIdShortcut();
			if(ptNetworkIdShortcut != null){
				String ptNetworkObjectId = stopPoint.getPtNetwork().getObjectId();
				if(!ptNetworkIdShortcut.equals(ptNetworkObjectId)){
					ReportItem detailReportItem = new DetailReportItem("Test2_Sheet11_Step1_error", Report.STATE.ERROR,"");
					report2_11_1.addItem(detailReportItem);	
				}else {
					report2_11_1.updateStatus(Report.STATE.OK);		
				}
			}
			//Category 3
			double x1 = (stopPoint.getLatitude()!=null) ? stopPoint.getLatitude().doubleValue():0;
			double y1 = (stopPoint.getLongitude()!=null) ? stopPoint.getLongitude().doubleValue():0;
			PrecisionModel precisionModel = new PrecisionModel(PrecisionModel.maximumPreciseValue);
			int SRID1 = stopPoint.getLongLatType().epsgCode();
			GeometryFactory factory1 = new GeometryFactory(precisionModel, SRID1);
			Coordinate coordinate = new Coordinate(x1, y1);
			Point point1 = factory1.createPoint(coordinate);
			
			for(int j=i+1;j<size;j++){
				StopPoint another = stopPoints.get(j);
				double x2 = (another.getLatitude() != null) ? another.getLatitude().doubleValue() : 0;
				double y2 = (another.getLongitude() != null) ? another.getLongitude().doubleValue() : 0;
				int SRID2 = another.getLongLatType().epsgCode();
				GeometryFactory factory2 = new GeometryFactory(precisionModel, SRID2);
				Coordinate coordinate2 = new Coordinate(x2, y2);
				Point point2 = factory2.createPoint(coordinate2);
				DistanceOp distanceOp = new DistanceOp(point1, point2);
				double distance = distanceOp.distance();
				
				//Test 3.1.1
				float param = parameters.getTest3_1_MinimalDistance();
				if(distance < param){
					if(!stopPoint.getName().equals(another.getName())){
						ReportItem detailReportItem = new DetailReportItem("Test3_Sheet1_Step1_warning", Report.STATE.WARNING,String.valueOf(param), stopPoint.getObjectId(), another.getObjectId());
						report3_1_1.addItem(detailReportItem);	
					}else
						report3_1_1.updateStatus(Report.STATE.OK);
				}
				//Test 3.2.1
				float param2 = parameters.getTest3_2_MinimalDistance();
				if(distance < param2){
					if(!stopPoint.getContainedInStopAreaId().equals(another.getContainedInStopAreaId())){
						ReportItem detailReportItem = new DetailReportItem("Test3_Sheet2_Step1_warning", Report.STATE.WARNING,String.valueOf(param2), stopPoint.getObjectId(), another.getObjectId());
						report3_2_1.addItem(detailReportItem);	
					}else
						report3_2_1.updateStatus(Report.STATE.OK);
				}
			}		
		}
		report2_10_1.computeDetailItemCount();
		report2_11_1.computeDetailItemCount();
		report3_1_1.computeDetailItemCount();
		report3_2_1.computeDetailItemCount();
		
		sheet10.addItem(report2_10_1);
		sheet11.addItem(report2_11_1);
		
		sheet3_1.addItem(report3_1_1);
		sheet3_2.addItem(report3_2_1);
		
		category2.addItem(sheet10);
		category2.addItem(sheet11);
		
		category3.addItem(sheet3_1);
		category3.addItem(sheet3_2);
		
		result.add(category2);
		result.add(category3);
		return result;
	}

}
