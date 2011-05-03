package fr.certu.chouette.validation.test;

import java.util.ArrayList;
import java.util.List;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.geom.PrecisionModel;

import fr.certu.chouette.model.neptune.AccessPoint;
import fr.certu.chouette.model.neptune.type.LongLatTypeEnum;
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
public class ValidationAccessPoint implements IValidationPlugin<AccessPoint>{

	private ValidationStepDescription description;
	public void init(){
		//TODO
		description = new ValidationStepDescription("", ValidationClassReportItem.CLASS.THREE.ordinal());
	}
	@Override
	public ValidationStepDescription getDescription() {
		return description;
	}


	@Override
	public List<ValidationClassReportItem> doValidate(List<AccessPoint> beans,
			ValidationParameters parameters) {		
		return validate(beans, parameters);
	}
	private List<ValidationClassReportItem> validate(List<AccessPoint> accessPoints,ValidationParameters parameters){
		ValidationClassReportItem category3 = new ValidationClassReportItem(ValidationClassReportItem.CLASS.THREE);

		List<ValidationClassReportItem> result = new ArrayList<ValidationClassReportItem>();

		ReportItem sheet3_17 = new SheetReportItem("Test3_Sheet17", 17);
		ReportItem sheet3_18 = new SheetReportItem("Test3_Sheet18", 18);
		
		SheetReportItem report3_17 = new SheetReportItem("Test3_Sheet17_Step1", 1);
		SheetReportItem report3_18 = new SheetReportItem("Test3_Sheet18_Step1", 1);
		List<Coordinate> listCoordinates = parameters.getTest3_2_Polygon();
		Coordinate first = listCoordinates.get(0);
		Coordinate last = listCoordinates.get(listCoordinates.size()-1);
		if(!first.equals(last))
			listCoordinates.add(first);
		Coordinate[] coordinates = listCoordinates.toArray(new Coordinate[0]);
		String param = parameters.getProjection_reference();
		int SRIDparam = (LongLatTypeEnum.fromValue(param) != null) ? LongLatTypeEnum.fromValue(param).epsgCode():0;
		
		for (AccessPoint accessPoint : accessPoints) {			
			int SRID = (accessPoint.getLongLatType() != null) ? accessPoint.getLongLatType().epsgCode():0; 
			//Test 3.17.1 && Test 3.18.1.a
			if(SRID == SRIDparam)
				report3_17.updateStatus(Report.STATE.OK);
			else {
				ReportItem detailReportItem = new DetailReportItem("Test3_Sheet17_Step1_warning", Report.STATE.WARNING,accessPoint.getName()+"("+accessPoint.getObjectId()+")");
				report3_17.addItem(detailReportItem);
				
				ReportItem detailReportItem2 = new DetailReportItem("Test3_Sheet18_Step1_error_a", Report.STATE.ERROR,accessPoint.getName()+"("+accessPoint.getObjectId()+")");
				report3_18.addItem(detailReportItem2);
			}
			
			//Test 3.18.1.b
			PrecisionModel precisionModel = new PrecisionModel(PrecisionModel.maximumPreciseValue);
			GeometryFactory factory1 = new GeometryFactory(precisionModel, SRID);
			LinearRing shell = factory1.createLinearRing(coordinates);
			LinearRing[] holes = null;
			Polygon polygon = factory1.createPolygon(shell, holes);
			double y = (accessPoint.getLatitude()!=null) ? accessPoint.getLatitude().doubleValue():0;
			double x = (accessPoint.getLongitude()!=null) ? accessPoint.getLongitude().doubleValue():0;
			Coordinate coordinate = new Coordinate(x, y);
			Point point1 = factory1.createPoint(coordinate);
			if(!polygon.contains(point1)){
				ReportItem detailReportItem = new DetailReportItem("Test3_Sheet18_Step1_error_b", Report.STATE.ERROR,accessPoint.getName()+"("+accessPoint.getObjectId()+")");
				report3_18.addItem(detailReportItem);	
			}else	
				report3_18.updateStatus(Report.STATE.OK);

		}


		report3_17.computeDetailItemCount();
		report3_18.computeDetailItemCount();
		
		sheet3_17.addItem(report3_17);
		sheet3_18.addItem(report3_18);
		
		category3.addItem(sheet3_17);
		category3.addItem(sheet3_18);
		result.add(category3);
		return result;
	}

}
