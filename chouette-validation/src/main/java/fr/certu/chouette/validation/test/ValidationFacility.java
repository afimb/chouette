package fr.certu.chouette.validation.test;

import java.util.ArrayList;
import java.util.List;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.geom.PrecisionModel;

import lombok.Getter;
import fr.certu.chouette.model.neptune.Facility;
import fr.certu.chouette.model.neptune.type.FacilityLocation;
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
public class ValidationFacility implements IValidationPlugin<Facility> {
	@Getter private ValidationStepDescription description;
	public void init(){
		//TODO 
		description = new ValidationStepDescription("",ValidationClassReportItem.CLASS.THREE.ordinal());
	}

	@Override
	public List<ValidationClassReportItem> doValidate(List<Facility> facilities,ValidationParameters parameters){
		return validate(facilities,parameters); 
	}
	private List<ValidationClassReportItem> validate(List<Facility> facilities,ValidationParameters parameters) {
		ValidationClassReportItem category3 = new ValidationClassReportItem(ValidationClassReportItem.CLASS.THREE);

		List<ValidationClassReportItem> result = new ArrayList<ValidationClassReportItem>();

		ReportItem sheet3_19 = new SheetReportItem("Test3_Sheet19", 19);
		ReportItem sheet3_20 = new SheetReportItem("Test3_Sheet20", 20);
		SheetReportItem report3_19_1 = new SheetReportItem("Test3_Sheet19_Step1",1);
		SheetReportItem report3_20_1 = new SheetReportItem("Test3_Sheet20_Step1",1);
		List<Coordinate> listCoordinates = parameters.getTest3_2_Polygon();
		Coordinate first = listCoordinates.get(0);
		Coordinate last = listCoordinates.get(listCoordinates.size()-1);
		if(!first.equals(last))
			listCoordinates.add(first);
		Coordinate[] coordinates = listCoordinates.toArray(new Coordinate[0]);

		String param = parameters.getProjection_reference().trim();
		for (Facility facility : facilities) {
			int SRIDparam = (LongLatTypeEnum.fromValue(param) != null) ? LongLatTypeEnum.fromValue(param).epsgCode():0;
			FacilityLocation facilityLocation = facility.getFacilityLocation();
			if(facilityLocation != null){
				int SRID = (facilityLocation.getLongLatType() != null) ? facilityLocation.getLongLatType().epsgCode():0; 
				//Test 3.19.1 && Test 3.20.1.a
				if(SRID == SRIDparam)
					report3_19_1.updateStatus(Report.STATE.OK);
				else {
					ReportItem detailReportItem = new DetailReportItem("Test3_Sheet19_Step1_warning", Report.STATE.WARNING,facility.getName()+"("+facility.getObjectId()+")");
					report3_19_1.addItem(detailReportItem);

					ReportItem detailReportItem2 = new DetailReportItem("Test3_Sheet20_Step1_warning_a", Report.STATE.WARNING,facility.getName()+"("+facility.getObjectId()+")");
					report3_20_1.addItem(detailReportItem2);
				}

				//Test 3.20.1.b
				PrecisionModel precisionModel = new PrecisionModel(PrecisionModel.maximumPreciseValue);
				GeometryFactory factory1 = new GeometryFactory(precisionModel, SRID);
				LinearRing shell = factory1.createLinearRing(coordinates);
				LinearRing[] holes = null;
				Polygon polygon = factory1.createPolygon(shell, holes);
				double y = (facilityLocation.getLatitude()!=null) ? facilityLocation.getLatitude().doubleValue():0;
				double x = (facilityLocation.getLongitude()!=null) ? facilityLocation.getLongitude().doubleValue():0;
				Coordinate coordinate = new Coordinate(x, y);
				Point point1 = factory1.createPoint(coordinate);
				if(!polygon.contains(point1)){
					ReportItem detailReportItem = new DetailReportItem("Test3_Sheet20_Step1_error_b", Report.STATE.ERROR,facility.getName()+"("+facility.getObjectId()+")");
					report3_20_1.addItem(detailReportItem);	
				}else	
					report3_20_1.updateStatus(Report.STATE.OK);
			}
		}

		report3_19_1.computeDetailItemCount();
		report3_20_1.computeDetailItemCount();

		sheet3_19.addItem(report3_19_1);
		sheet3_20.addItem(report3_20_1);

		category3.addItem(sheet3_19);
		category3.addItem(sheet3_20);
		result.add(category3);
		return result;
	}
}
