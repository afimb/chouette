package fr.certu.chouette.validation.test3;

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
public class Sheet1 implements IValidationPlugin<StopPoint>{

	private ValidationStepDescription validationStepDescription;
	
	public void init(){
		validationStepDescription = new ValidationStepDescription("Test3.1", ValidationClassReportItem.CLASS.THREE.ordinal());
	}
	
	@Override
	public ValidationStepDescription getDescription() {
		return validationStepDescription;
	}
	@Override
	public ReportItem doValidate(List<StopPoint> beans,ValidationParameters parameters) {
		ReportItem reportItem = new SheetReportItem("Test3_Sheet1",1);
		reportItem.addItem(step_3_1(beans,parameters));
		return reportItem;
	}
	
	private ReportItem step_3_1(List<StopPoint> stopPoints,ValidationParameters parameters){
		ReportItem reportItem = new SheetReportItem("Test3_Sheet1_Step1",1);
	
		int size = stopPoints.size();
		System.out.println("size "+size);
		for (int i=0;i<size;i++) {
			StopPoint stopPoint = stopPoints.get(i);
			double x1 = stopPoint.getLatitude().doubleValue();
			double y1 = stopPoint.getLongitude().doubleValue();
			PrecisionModel precisionModel = new PrecisionModel(PrecisionModel.maximumPreciseValue);
			int SRID1 = stopPoint.getLongLatType().epsgCode();
			GeometryFactory factory1 = new GeometryFactory(precisionModel, SRID1);
			Coordinate coordinate = new Coordinate(x1, y1);
			Point point1 = factory1.createPoint(coordinate);
			
			for(int j=i+1;j<size;j++){
				StopPoint another = stopPoints.get(j);
				double x2 = another.getLatitude().doubleValue();
				double y2 = another.getLongitude().doubleValue();
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
						ReportItem detailReportItem = new DetailReportItem("Test3_Sheet1_Step1_warning", Report.STATE.WARNING,String.valueOf(param), stopPoint.getName(), another.getName());
						reportItem.addItem(detailReportItem);	
					}else
						reportItem.setStatus(Report.STATE.OK);
				}
			}		
		}
		return reportItem;
	}
}
