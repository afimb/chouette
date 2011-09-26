package fr.certu.chouette.validation.test;

import java.util.ArrayList;
import java.util.List;

import fr.certu.chouette.model.neptune.AccessLink;
import fr.certu.chouette.model.neptune.AccessPoint;
import fr.certu.chouette.model.neptune.AreaCentroid;
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
public class ValidationAccessLink extends AbstractValidation implements IValidationPlugin<AccessLink>{

	private ValidationStepDescription validationStepDescription;
	private final double DIVIDER = 1000 * 3600;
	public void init(){
		//TODO
		validationStepDescription = new ValidationStepDescription("", ValidationClassReportItem.CLASS.TWO.ordinal());
	}
	@Override
	public ValidationStepDescription getDescription() {
		return validationStepDescription;
	}

	@Override
	public List<ValidationClassReportItem> doValidate(List<AccessLink> beans,
			ValidationParameters parameters) {
		return validate(beans, parameters);
	}

	private List<ValidationClassReportItem> validate(List<AccessLink> accessLinks,
			ValidationParameters parameters) {
		List<ValidationClassReportItem> res = new ArrayList<ValidationClassReportItem>();
		ValidationClassReportItem category2 = new ValidationClassReportItem(ValidationClassReportItem.CLASS.TWO);
		ValidationClassReportItem category3 = new ValidationClassReportItem(ValidationClassReportItem.CLASS.THREE);
		ReportItem sheet2_25 = new SheetReportItem("Test2_Sheet25",25);
		ReportItem sheet3_21 = new SheetReportItem("Test3_Sheet21",21);
		SheetReportItem report2_25 = new SheetReportItem("Test2_Sheet25_Step1", 1);
		SheetReportItem report3_21 = new SheetReportItem("Test3_Sheet21_Step1", 1);

		double minA = parameters.getTest3_21a_MinimalSpeed();
		double maxA = parameters.getTest3_21a_MaximalSpeed();
		double minB = parameters.getTest3_21b_MinimalSpeed();
		double maxB = parameters.getTest3_21b_MaximalSpeed();
		double minC = parameters.getTest3_21c_MinimalSpeed();
		double maxC = parameters.getTest3_21c_MaximalSpeed();
		double minD = parameters.getTest3_21d_MinimalSpeed();
		double maxD = parameters.getTest3_21d_MaximalSpeed();
		
		for (AccessLink accessLink : accessLinks) {
			//Test 2.25.1
			if(accessLink.getStartOfLinkId().equals(accessLink.getEndOfLinkId())){
				ReportItem detailReportItem = new DetailReportItem("Test2_Sheet25_Step1_error_a",Report.STATE.ERROR);
				report2_25.addItem(detailReportItem);
			}
			else if (accessLink.getStopArea() == null || accessLink.getAccessPoint() == null)
			{
				ReportItem detailReportItem = new DetailReportItem("Test2_Sheet25_Step1_error_b",Report.STATE.ERROR);
				report2_25.addItem(detailReportItem);
			}
			else
			{
				report2_25.updateStatus(Report.STATE.OK);
			}
				
			//Test 3.21
			StopArea stopArea = accessLink.getStopArea();
			AccessPoint accessPoint = accessLink.getAccessPoint();
			AreaCentroid areaCentroidStart = (stopArea != null) ? stopArea.getAreaCentroid() : null; 

//			PrecisionModel precisionModel = new PrecisionModel(PrecisionModel.maximumPreciseValue);
			double yStart = (areaCentroidStart != null && areaCentroidStart.getLatitude() != null) ? areaCentroidStart.getLatitude().doubleValue() : 0;
			double xStart = (areaCentroidStart != null && areaCentroidStart.getLongitude() != null) ? areaCentroidStart.getLongitude().doubleValue() : 0;
//			int SRIDstart = (areaCentroidStart != null && areaCentroidStart.getLongLatType()!= null) ? areaCentroidStart.getLongLatType().epsgCode() : 0;
//			GeometryFactory factoryStart = new GeometryFactory(precisionModel, SRIDstart);
//			Point pointStart = factoryStart.createPoint(new Coordinate(xStart, yStart));

			double yEnd = (accessPoint != null && accessPoint.getLatitude() != null) ? accessPoint.getLatitude().doubleValue() : 0;		
			double xEnd = (accessPoint != null && accessPoint.getLongitude() != null) ? accessPoint.getLongitude().doubleValue() : 0;				
//			int SRIDend = (accessPoint != null && accessPoint.getLongLatType()!= null) ? accessPoint.getLongLatType().epsgCode() : 0;								
//			GeometryFactory factoryEnd = new GeometryFactory(precisionModel, SRIDend);
//			Point pointEnd = factoryEnd.createPoint(new Coordinate(xEnd, yEnd));
//			DistanceOp distanceOp = new DistanceOp(pointStart, pointEnd);
//			double distance = distanceOp.distance()*CONVERTER;
			double distance = distance(xStart,yStart,xEnd,yEnd) / 1000; // in kilometers meters 
			//Test 3.21.1  a
			double timeA = (accessLink.getDefaultDuration() != null) ? accessLink.getDefaultDuration().getTime() / DIVIDER  : 0 ;
			double speedA = distance /timeA;
			if(speedA < minA || speedA > maxA){
				ReportItem detailReportItem = new DetailReportItem("Test3_Sheet21_Step1_error_a",Report.STATE.ERROR,
						String.valueOf(minA),String.valueOf(maxA),accessLink.getName()+"("+accessLink.getObjectId()+")");
				report3_21.addItem(detailReportItem);
			}
			//Test 3.21.1 b
			double timeB = (accessLink.getFrequentTravellerDuration() != null) ? accessLink.getFrequentTravellerDuration().getTime() / DIVIDER : 0;
			double speedB = distance/timeB;
			if(speedB < minB || speedB > maxB){
				ReportItem detailReportItem = new DetailReportItem("Test3_Sheet21_Step1_error_b",Report.STATE.ERROR,
						String.valueOf(minB),String.valueOf(maxB),accessLink.getName()+"("+accessLink.getObjectId()+")");
				report3_21.addItem(detailReportItem);
			}		
			//Test 3.21.1 c
			double timeC = (accessLink.getOccasionalTravellerDuration() != null) ? accessLink.getOccasionalTravellerDuration().getTime() / DIVIDER: 0;
			double speedC = distance/timeC;
			
			if(speedC < minC || speedC > maxC){
				ReportItem detailReportItem = new DetailReportItem("Test3_Sheet21_Step1_error_c",Report.STATE.ERROR,
						String.valueOf(minC),String.valueOf(maxC),accessLink.getName()+"("+accessLink.getObjectId()+")");
				report3_21.addItem(detailReportItem);
			}		
			//Test 3.21.1 d
			if(accessLink.getMobilityRestrictedTravellerDuration() == null)
				report3_21.updateStatus(Report.STATE.OK);
			else {
				double timeD = accessLink.getMobilityRestrictedTravellerDuration().getTime() / DIVIDER;
				double speedD = distance/timeD;
				if(speedD < minD || speedD > maxD){
					ReportItem detailReportItem = new DetailReportItem("Test3_Sheet21_Step1_error_d",Report.STATE.ERROR,
							String.valueOf(minD),String.valueOf(maxD),accessLink.getName()+"("+accessLink.getObjectId()+")");
					report3_21.addItem(detailReportItem);
				}else 
					report3_21.updateStatus(Report.STATE.OK);
			}
		}
		report2_25.computeDetailItemCount();
		report3_21.computeDetailItemCount();
		sheet2_25.addItem(report2_25);
		sheet3_21.addItem(report3_21);
		category3.addItem(sheet3_21);
		category2.addItem(sheet2_25);

		res.add(category2);
		res.add(category3);
		return res;
	}

}
