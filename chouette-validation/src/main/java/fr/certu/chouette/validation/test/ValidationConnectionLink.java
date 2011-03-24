package fr.certu.chouette.validation.test;

import java.util.ArrayList;
import java.util.List;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.PrecisionModel;
import com.vividsolutions.jts.operation.distance.DistanceOp;

import fr.certu.chouette.model.neptune.AreaCentroid;
import fr.certu.chouette.model.neptune.ConnectionLink;
import fr.certu.chouette.model.neptune.StopArea;
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
public class ValidationConnectionLink implements IValidationPlugin<ConnectionLink>{

	private ValidationStepDescription validationStepDescription;
	private final long DIVIDER = 1000 * 3600;

	public void init(){
		validationStepDescription = new ValidationStepDescription("", ValidationClassReportItem.CLASS.TWO.ordinal());
	}
	@Override
	public ValidationStepDescription getDescription() {
		return validationStepDescription;
	}

	@Override
	public List<ValidationClassReportItem> doValidate(List<ConnectionLink> beans,ValidationParameters parameters) {
		System.out.println("ConnectionLinkValidation");
		return validate(beans,parameters);
	}
	private List<ValidationClassReportItem> validate(List<ConnectionLink> connectionLinks, ValidationParameters parameters){
		List<ValidationClassReportItem> res = new ArrayList<ValidationClassReportItem>();
		ValidationClassReportItem category2 = new ValidationClassReportItem(ValidationClassReportItem.CLASS.TWO);
		ValidationClassReportItem category3 = new ValidationClassReportItem(ValidationClassReportItem.CLASS.THREE);

		ReportItem sheet2_4 = new SheetReportItem("Test2_Sheet4",4);
		ReportItem sheet3_8 = new SheetReportItem("Test3_Sheet8",8);
		SheetReportItem report2_4 = new SheetReportItem("Test2_Sheet4_Step1",1);
		SheetReportItem report3_8 = new SheetReportItem("Test3_Sheet8_Step1",1);

		for(ConnectionLink connectionLink : connectionLinks){
			String startOfLinkId = connectionLink.getStartOfLinkId();
			String endOfLinkId = connectionLink.getEndOfLinkId();
			//Test 2.4.1
			if(startOfLinkId == null || endOfLinkId == null){
				ReportItem detailReportItem = new DetailReportItem("Test2_Sheet4_Step1_error_a",Report.STATE.ERROR);
				report2_4.addItem(detailReportItem);
			//}else if(!startOfLinkId.equals(connectionLink.getStartOfLink().getObjectId()) || !endOfLinkId.equals(connectionLink.getEndOfLink().getObjectId())){
			}else if(connectionLink.getStartOfLink() == null || connectionLink.getEndOfLink() == null){
				ReportItem detailReportItem = new DetailReportItem("Test2_Sheet4_Step1_error_b",Report.STATE.ERROR);
				report2_4.addItem(detailReportItem);
			}else {
				report2_4.updateStatus(Report.STATE.OK);
			}
			//Test 3.8.1

			StopArea startOfLink = connectionLink.getStartOfLink();
			StopArea endOfLink = connectionLink.getEndOfLink();
			AreaCentroid areaCentroidStart = (startOfLink != null) ? startOfLink.getAreaCentroid() : null; 
			AreaCentroid areaCentroidEnd = (endOfLink != null) ? endOfLink.getAreaCentroid() : null;

			PrecisionModel precisionModel = new PrecisionModel(PrecisionModel.maximumPreciseValue);
			double yStart = (areaCentroidStart != null && areaCentroidStart.getLatitude() != null) ? areaCentroidStart.getLatitude().doubleValue() : 0;
			double xStart = (areaCentroidStart != null && areaCentroidStart.getLongitude() != null) ? areaCentroidStart.getLongitude().doubleValue() : 0;
			int SRIDstart = (areaCentroidStart != null && areaCentroidStart.getLongLatType()!= null) ? areaCentroidStart.getLongLatType().epsgCode() : 0;
			GeometryFactory factoryStart = new GeometryFactory(precisionModel, SRIDstart);
			Point pointStart = factoryStart.createPoint(new Coordinate(xStart, yStart));

			double yEnd = (areaCentroidEnd != null && areaCentroidEnd.getLatitude() != null) ? areaCentroidEnd.getLatitude().doubleValue() : 0;		
			double xEnd = (areaCentroidEnd != null && areaCentroidEnd.getLongitude() != null) ? areaCentroidEnd.getLongitude().doubleValue() : 0;				
			int SRIDend = (areaCentroidEnd != null && areaCentroidEnd.getLongLatType()!= null) ? areaCentroidEnd.getLongLatType().epsgCode() : 0;								
			GeometryFactory factoryEnd = new GeometryFactory(precisionModel, SRIDend);
			Point pointEnd = factoryEnd.createPoint(new Coordinate(xEnd, yEnd));
			DistanceOp distanceOp = new DistanceOp(pointStart, pointEnd);
			double distance = distanceOp.distance();
			//Test 3.8.1  a
			long timeA = (connectionLink.getDefaultDuration() != null) ? connectionLink.getDefaultDuration().getTime() / DIVIDER  : 0 ;
			double speedA = distance /timeA;
			double minA = parameters.getTest3_8a_MinimalSpeed();
			double maxA = parameters.getTest3_8a_MaximalSpeed();
			if(speedA < minA && speedA > maxA){
				ReportItem detailReportItem = new DetailReportItem("Test3_Sheet8_Step1_error_a",Report.STATE.ERROR,
						String.valueOf(minA),String.valueOf(maxA),connectionLink.getObjectId());
				report3_8.addItem(detailReportItem);
			}
			//Test 3.8.1 b
			long timeB = (connectionLink.getFrequentTravellerDuration() != null) ? connectionLink.getFrequentTravellerDuration().getTime() / DIVIDER : 0;
			double speedB = distance/timeB;
			double minB = parameters.getTest3_8b_MinimalSpeed();
			double maxB = parameters.getTest3_8b_MaximalSpeed();
			if(speedB < minB && speedB > maxB){
				ReportItem detailReportItem = new DetailReportItem("Test3_Sheet8_Step1_error_b",Report.STATE.ERROR,
						String.valueOf(minB),String.valueOf(maxB),connectionLink.getObjectId());
				report3_8.addItem(detailReportItem);
			}		
			//Test 3.8.1 c
			long timeC = (connectionLink.getOccasionalTravellerDuration() != null) ? connectionLink.getOccasionalTravellerDuration().getTime() / DIVIDER: 0;
			double speedC = distance/timeC;
			double minC = parameters.getTest3_8c_MinimalSpeed();
			double maxC = parameters.getTest3_8c_MaximalSpeed();
			if(speedC < minC && speedC > maxC){
				ReportItem detailReportItem = new DetailReportItem("Test3_Sheet8_Step1_error_c",Report.STATE.ERROR,
						String.valueOf(minC),String.valueOf(maxC),connectionLink.getObjectId());
				report3_8.addItem(detailReportItem);
			}		
			//Test 3.8.1 d
			if(connectionLink.getMobilityRestrictedTravellerDuration() == null)
				report3_8.updateStatus(Report.STATE.OK);
			else {
				long timeD = connectionLink.getMobilityRestrictedTravellerDuration().getTime() / DIVIDER;
				double speedD = distance/timeD;
				double minD = parameters.getTest3_8d_MinimalSpeed();
				double maxD = parameters.getTest3_8d_MaximalSpeed();
				if(speedD < minD && speedD > maxD){
					ReportItem detailReportItem = new DetailReportItem("Test3_Sheet8_Step1_error_d",Report.STATE.ERROR,
							String.valueOf(minD),String.valueOf(maxD),connectionLink.getObjectId());
					report3_8.addItem(detailReportItem);
				}else 
					report3_8.updateStatus(Report.STATE.OK);
			}
		}
		report2_4.computeDetailItemCount();
		report3_8.computeDetailItemCount();
		
		sheet2_4.addItem(report2_4);
		sheet3_8.addItem(report3_8);
		category2.addItem(sheet2_4);
		category3.addItem(sheet3_8);
		
		res.add(category2);
		res.add(category3);
		return res;
	}
}
