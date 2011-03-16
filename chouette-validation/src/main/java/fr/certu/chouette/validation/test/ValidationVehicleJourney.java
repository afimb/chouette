package fr.certu.chouette.validation.test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.PrecisionModel;
import com.vividsolutions.jts.operation.distance.DistanceOp;

import fr.certu.chouette.model.neptune.StopPoint;
import fr.certu.chouette.model.neptune.Timetable;
import fr.certu.chouette.model.neptune.VehicleJourney;
import fr.certu.chouette.model.neptune.VehicleJourneyAtStop;
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
public class ValidationVehicleJourney implements IValidationPlugin<VehicleJourney>{

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
	public List<ValidationClassReportItem> doValidate(List<VehicleJourney> beans,ValidationParameters parameters) {	
		System.out.println("VehicleJourneyValidation "+beans.size());
		return validate(beans,parameters);	
	}

	private List<ValidationClassReportItem> validate(List<VehicleJourney> vehicleJourneys, ValidationParameters parameters){
		ValidationClassReportItem category2 = new ValidationClassReportItem(ValidationClassReportItem.CLASS.TWO);
		ValidationClassReportItem category3 = new ValidationClassReportItem(ValidationClassReportItem.CLASS.THREE);

		ReportItem sheet2_17 = new SheetReportItem("Test2_Sheet17",17);
		ReportItem sheet2_18 = new SheetReportItem("Test2_Sheet18",18);
		ReportItem sheet2_19 = new SheetReportItem("Test2_Sheet19",19);
		ReportItem sheet2_20 = new SheetReportItem("Test2_Sheet20",20);
		ReportItem sheet2_21 = new SheetReportItem("Test2_Sheet21",21);
		ReportItem sheet2_22 = new SheetReportItem("Test2_Sheet22",22);
		ReportItem sheet2_23 = new SheetReportItem("Test2_Sheet23",23);
		ReportItem sheet2_24 = new SheetReportItem("Test2_Sheet24",24);
		ReportItem sheet3_7 = new SheetReportItem("Test3_Sheet7",7);
		ReportItem sheet3_9 = new SheetReportItem("Test3_Sheet9",9);
		ReportItem sheet3_15 = new SheetReportItem("Test3_Sheet15",15);
		ReportItem sheet3_16 = new SheetReportItem("Test3_Sheet16",16);

		SheetReportItem report2_17_1 = new SheetReportItem("Test2_Sheet17_Step1",1);		
		SheetReportItem report2_18_1 = new SheetReportItem("Test2_Sheet18_Step1",1);
		SheetReportItem report2_18_2 = new SheetReportItem("Test2_Sheet18_Step2",2);		
		SheetReportItem report2_19 = new SheetReportItem("Test2_Sheet19_Step1",1);
		SheetReportItem report2_20 = new SheetReportItem("Test2_Sheet20_Step1",1);
		SheetReportItem report2_21 = new SheetReportItem("Test2_Sheet21_Step1",1);
		SheetReportItem report2_22 = new SheetReportItem("Test2_Sheet22_Step1",1);
		SheetReportItem report2_23 = new SheetReportItem("Test2_Sheet23_Step1",1);
		SheetReportItem report2_24 = new SheetReportItem("Test2_Sheet24_Step1",1);
		SheetReportItem report3_7 = new SheetReportItem("Test3_Sheet7_Step1",1);
		SheetReportItem report3_9 = new SheetReportItem("Test3_Sheet9_Step1",1);
		SheetReportItem report3_15 = new SheetReportItem("Test3_Sheet15_Step1",1);
		SheetReportItem report3_16_1 = new SheetReportItem("Test3_Sheet16_Step1",1);
		SheetReportItem report3_16_2 = new SheetReportItem("Test3_Sheet16_Step2",2);
		SheetReportItem report3_16_3 = new SheetReportItem("Test3_Sheet16_Step3",3);

		List<ValidationClassReportItem> result = new ArrayList<ValidationClassReportItem>();
		Map<String, Set<VehicleJourney>> map = new TreeMap<String, Set<VehicleJourney>>();
		Set<VehicleJourney> set = new HashSet<VehicleJourney>();
		if(vehicleJourneys != null){
			for (int i=0; i<vehicleJourneys.size();i++) {
				VehicleJourney vehicleJourney = vehicleJourneys.get(i);
				String key = vehicleJourney.getRouteId();
				Set<VehicleJourney> values = map.get(key); 
				if(values != null && !values.isEmpty()){
					values.add(vehicleJourney);
				}else {
					set.add(vehicleJourney);
					map.put(vehicleJourney.getRouteId(),set);	
				}
				if(vehicleJourney.getRoute() != null){
					if(!vehicleJourney.getRouteId().equals(vehicleJourney.getRoute().getObjectId())){
						ReportItem detailReportItem = new DetailReportItem("Test2_Sheet17_Step1_error", Report.STATE.ERROR, vehicleJourney.getObjectId(),vehicleJourney.getRouteId());
						report2_17_1.addItem(detailReportItem);	
					}else
						report2_17_1.updateStatus(Report.STATE.OK);	
				}else{
					ReportItem detailReportItem = new DetailReportItem("Test2_Sheet17_Step1_error", Report.STATE.ERROR, vehicleJourney.getObjectId(),vehicleJourney.getRouteId());
					report2_17_1.addItem(detailReportItem);
				}				
				String journeyPatternId = vehicleJourney.getJourneyPatternId();
				String journeyPatternObjectId = (vehicleJourney.getJourneyPattern() != null) ? 
						vehicleJourney.getJourneyPattern().getObjectId() : ""; 
						//Test 2.18.1
						if(journeyPatternId != null){
							if(!journeyPatternObjectId.equals(journeyPatternId)){
								ReportItem detailReportItem = new DetailReportItem("Test2_Sheet18_Step1_error", Report.STATE.ERROR, vehicleJourney.getObjectId());
								report2_18_1.addItem(detailReportItem);	
							}else
								report2_18_1.updateStatus(Report.STATE.OK);
						}
						List<String> stopPointObjectIds = (vehicleJourney.getJourneyPattern() != null && vehicleJourney.getJourneyPattern().getStopPoints() != null) ? 
								VehicleJourney.extractObjectIds(vehicleJourney.getJourneyPattern().getStopPoints()) : null;

								List<String> stopPointIds = (vehicleJourney.getJourneyPattern() != null) ? 
										vehicleJourney.getJourneyPattern().getStopPointIds() : null;
										if(stopPointObjectIds != null && stopPointIds != null){
											//Test 2.18.2 a
											if(!stopPointIds.containsAll(stopPointObjectIds)){
												ReportItem detailReportItem = new DetailReportItem("Test2_Sheet18_Step2_error_a", Report.STATE.ERROR,"");
												report2_18_2.addItem(detailReportItem);	
											}else
												report2_18_2.updateStatus(Report.STATE.OK);
											//Test 2.18.2 b
											List<VehicleJourneyAtStop> vehicleJourneyAtStopIds = vehicleJourney.getVehicleJourneyAtStops();
											for (VehicleJourneyAtStop vehicleJourneyAtStop : vehicleJourneyAtStopIds) {
												if(!stopPointObjectIds.contains(vehicleJourneyAtStop.getStopPointId())){
													ReportItem detailReportItem = new DetailReportItem("Test2_Sheet18_Step2_error_b", Report.STATE.ERROR,"");
													report2_18_2.addItem(detailReportItem);	
												}else
													report2_18_2.updateStatus(Report.STATE.OK);
											}
										}
										//Test 2.19.1
										String lineShortCutId = vehicleJourney.getLineIdShortcut();
										if(lineShortCutId != null){
											String lineObjectId = (vehicleJourney.getRoute() != null && vehicleJourney.getRoute().getLine() != null) ? 
													vehicleJourney.getRoute().getLine().getObjectId() : null;
													if(!lineShortCutId.equals(lineObjectId)){
														ReportItem detailReportItem = new DetailReportItem("Test2_Sheet19_Step1_error", Report.STATE.ERROR,vehicleJourney.getObjectId());
														report2_19.addItem(detailReportItem);	
													}else
														report2_19.updateStatus(Report.STATE.OK);	
										}

										//Test 2.20.1
										String companyId = vehicleJourney.getCompanyId();
										if(companyId != null){
											String companyObjectId = (vehicleJourney.getCompany() != null) ? vehicleJourney.getCompany().getObjectId() : null;
											if(!companyId.equals(companyObjectId)){
												ReportItem detailReportItem = new DetailReportItem("Test2_Sheet20_Step1_error", Report.STATE.ERROR, vehicleJourney.getObjectId());
												report2_20.addItem(detailReportItem);	
											}else
												report2_20.updateStatus(Report.STATE.OK);	
										}

										//Test 2.21.1
										String timeSlotId = vehicleJourney.getTimeSlotId();
										if(timeSlotId != null){
											String timeSlotObjectId = (vehicleJourney.getTimeSlot() != null) ? vehicleJourney.getTimeSlot().getObjectId():null;
											if(!timeSlotId.equals(timeSlotObjectId)){
												ReportItem detailReportItem = new DetailReportItem("Test2_Sheet21_Step1_error", Report.STATE.ERROR, vehicleJourney.getObjectId());
												report2_21.addItem(detailReportItem);	
											}else
												report2_21.updateStatus(Report.STATE.OK);	
										}
										//Test 2.24.1
										String routeIdFromVJ = (vehicleJourney.getRouteId() != null) ? vehicleJourney.getRouteId() : "";
										String routeIdFromJP = (vehicleJourney.getJourneyPattern() != null) ? vehicleJourney.getJourneyPattern().getRouteId() : null;
										if(!routeIdFromVJ.equals(routeIdFromJP)){
											ReportItem detailReportItem = new DetailReportItem("Test2_Sheet24_Step1_error", Report.STATE.ERROR,"");
											report2_24.addItem(detailReportItem);	
										}else
											report2_24.updateStatus(Report.STATE.OK);
										//Test 3.16.2
										for (Timetable timetable : vehicleJourney.getTimetables()) {
											if(!timetable.getVehicleJourneyIds().contains(vehicleJourney.getObjectId())){
												ReportItem detailReportItem = new DetailReportItem("Test3_Sheet16_Step2_warning", Report.STATE.WARNING,vehicleJourney.getObjectId());
												report3_16_2.addItem(detailReportItem);	
											}else
												report3_16_2.updateStatus(Report.STATE.OK);
										}							
			}

			//
			Map<String, Set<VehicleJourneyAtStop[]>> doubletMap = new TreeMap<String, Set<VehicleJourneyAtStop[]>>();
			for (String key : map.keySet()) {
				Set<VehicleJourney> vJSet = map.get(key);
				for (VehicleJourney vehicleJourney : vJSet) {
					List<VehicleJourneyAtStop> vehicleJourneyAtStops =vehicleJourney.getVehicleJourneyAtStops();
					Set<VehicleJourneyAtStop[]> stopsSet = new HashSet<VehicleJourneyAtStop[]>();
					//Test 2.22.1
					if(vehicleJourneyAtStops != null){
						for(VehicleJourneyAtStop vehicleJourneyAtStop : vehicleJourneyAtStops){
							String stopPointId = vehicleJourneyAtStop.getStopPointId();
							String stopPointObjectId = vehicleJourneyAtStop.getStopPoint().getObjectId();				
							if(!stopPointObjectId.equals(stopPointId)){
								String arrivalTime = String.valueOf(vehicleJourneyAtStop.getArrivalTime());
								ReportItem detailReportItem = new DetailReportItem("Test2_Sheet22_Step1_error", Report.STATE.ERROR,arrivalTime);
								report2_22.addItem(detailReportItem);	
							}else
								report2_22.updateStatus(Report.STATE.OK);	

							//Test 2.23.1
							String vehicleJourneyId = vehicleJourneyAtStop.getVehicleJourneyId();
							if(!vehicleJourneyId.equals(vehicleJourney.getObjectId())){
								String arrivalTime = String.valueOf(vehicleJourneyAtStop.getArrivalTime());
								ReportItem detailReportItem = new DetailReportItem("Test2_Sheet23_Step1_error", Report.STATE.ERROR,arrivalTime);
								report2_23.addItem(detailReportItem);	
							}else
								report2_23.updateStatus(Report.STATE.OK);	

							//Test 3.15
							final long CONSTANT = 9999;
							long arrivalTime = (vehicleJourneyAtStop.getArrivalTime() != null) ? vehicleJourneyAtStop.getArrivalTime().getTime() : CONSTANT;
							long departureTime = vehicleJourneyAtStop.getDepartureTime().getTime();
							long diff = Math.abs(arrivalTime - departureTime);
							double param3_15 = parameters.getTest3_15_MinimalTime();
							if(arrivalTime != CONSTANT){
								if(diff > param3_15){
									ReportItem detailReportItem = new DetailReportItem("Test3_Sheet15_Step1_error", Report.STATE.ERROR,String.valueOf(param3_15));
									report3_15.addItem(detailReportItem);	
								}else
									report3_15.updateStatus(Report.STATE.OK);	
							}
						}
						//Test 3.7 && Test 3.9
						if(vehicleJourneyAtStops.size() >1){
							for (VehicleJourneyAtStop vJAtStop : vehicleJourneyAtStops) {
								String stopPointId = vJAtStop.getStopPointId();
								StopPoint stopPoint = vJAtStop.getOjectByObjectId(stopPointId);
								if(stopPoint != null){
									double y1 = (stopPoint .getLatitude()!=null) ? stopPoint.getLatitude().doubleValue():0;
									double x1 = (stopPoint.getLongitude()!=null) ? stopPoint.getLongitude().doubleValue():0;
									PrecisionModel precisionModel = new PrecisionModel(PrecisionModel.maximumPreciseValue);
									int SRID1 = (stopPoint.getLongLatType()!= null) ? stopPoint.getLongLatType().epsgCode() : 0;
									GeometryFactory factory1 = new GeometryFactory(precisionModel, SRID1);
									Coordinate coordinate = new Coordinate(x1, y1);
									Point point1 = factory1.createPoint(coordinate);

									for (VehicleJourneyAtStop vJAtStop2 : vehicleJourneyAtStops) {
										long diff = vJAtStop2.getOrder()-vJAtStop.getOrder();
										if(diff == 1){
											VehicleJourneyAtStop[] vJAtStops = new VehicleJourneyAtStop[2];
											vJAtStops[0] = vJAtStop;
											vJAtStops[1] = vJAtStop2;
											stopsSet.add(vJAtStops);
											//Test 3.7.1
											String stopPointId2 = vJAtStop2.getStopPointId();
											StopPoint stopPoint2 = vJAtStop2.getOjectByObjectId(stopPointId2);
											if(stopPoint2 != null){
												double y2 = (stopPoint2 .getLatitude()!=null) ? stopPoint2.getLatitude().doubleValue():0;
												double x2 = (stopPoint2.getLongitude()!=null) ? stopPoint2.getLongitude().doubleValue():0;
												int SRID2 = (stopPoint2.getLongLatType()!= null) ? stopPoint2.getLongLatType().epsgCode() : 0;
												GeometryFactory factory2 = new GeometryFactory(precisionModel, SRID2);
												Coordinate coordinate2 = new Coordinate(x2, y2);
												Point point2 = factory2.createPoint(coordinate2);
												DistanceOp distanceOp = new DistanceOp(point1, point2);
												double distance = distanceOp.distance()* 6371 /180;
												double min3_7 = parameters.getTest3_7_MinimalDistance();
												double max3_7 = parameters.getTest3_7_MaximalDistance();
												if(distance < min3_7  && distance > max3_7){
													ReportItem detailReportItem = new DetailReportItem("Test3_Sheet7_Step1_warning", Report.STATE.WARNING,String.valueOf(min3_7),String.valueOf(max3_7));
													report3_7.addItem(detailReportItem);	
												}else
													report3_7.updateStatus(Report.STATE.OK);
												//Test 3.9.1

												long arrivalTime = (vJAtStop2.getArrivalTime() != null) ? vJAtStop2.getArrivalTime().getTime() /DIVIDER : DIVIDER ;
												long departureTime = (vJAtStop.getDepartureTime() != null) ? vJAtStop.getDepartureTime().getTime() /DIVIDER : 0;
												double speed = distance / Math.abs(departureTime - arrivalTime);
												double min3_9 = parameters.getTest3_9_MinimalSpeed();
												double max3_9 = parameters.getTest3_9_MaximalSpeed();
												if(arrivalTime != DIVIDER){
													if(speed < min3_9 && speed > max3_9){
														ReportItem detailReportItem = new DetailReportItem("Test3_Sheet9_Step1_warning", Report.STATE.WARNING,
																stopPointId,stopPointId2,String.valueOf(min3_9),String.valueOf(max3_9));
														report3_9.addItem(detailReportItem);	
													}else
														report3_9.updateStatus(Report.STATE.OK);
												}							
											}											//Test 3.16.3 a
											long departureTime = (vJAtStop.getDepartureTime() != null) ? vJAtStop.getDepartureTime().getTime() /DIVIDER : 0;
											long arrivalTime = (vJAtStop2.getArrivalTime() != null) ? vJAtStop2.getArrivalTime().getTime() /DIVIDER : DIVIDER ;
											long param3_16_3 = parameters.getTest3_16_3a_MinimalTime();
											if(Math.abs(arrivalTime - departureTime) > param3_16_3){
												ReportItem detailReportItem = new DetailReportItem("Test3_Sheet16_Step3_error_a", Report.STATE.ERROR,String.valueOf(param3_16_3));
												report3_16_3.addItem(detailReportItem);	
											}

											//Test 3.16.3 b
											if(departureTime <= arrivalTime)
												report3_16_3.updateStatus(Report.STATE.OK);
											//Test 3.16.3 b (suite)
											else if(arrivalTime > param3_16_3) {
												ReportItem detailReportItem = new DetailReportItem("Test3_Sheet16_Step3_error_b", Report.STATE.ERROR,String.valueOf(param3_16_3));
												report3_16_3.addItem(detailReportItem);	
											}else
												report3_16_3.updateStatus(Report.STATE.OK);
										}
									}
								}
							}
						}
					}
					doubletMap.put(vehicleJourney.getObjectId(), stopsSet);
				}
				//System.gc();
			}
			//Test 3.16.1
			for (String key : doubletMap.keySet()) {
				Set<VehicleJourneyAtStop[]> stopsSet = doubletMap.get(key);
				for (VehicleJourneyAtStop[] vjAtStops : stopsSet) {
					for (String key2 : doubletMap.keySet()) {
						if(!key.equals(key2)){
							Set<VehicleJourneyAtStop[]> stopsSet2 = doubletMap.get(key2);
							for (VehicleJourneyAtStop[] vjAtStops2 : stopsSet2) {
								if(vjAtStops[0].getStopPointId().equals(vjAtStops2[0].getStopPointId()) && 
										vjAtStops[1].getStopPointId().equals(vjAtStops2[1].getStopPointId())){
									long diffAbsolute1 = Math.abs(vjAtStops[0].getDepartureTime().getTime() - vjAtStops[1].getArrivalTime().getTime());
									long diffAbsolute2 = Math.abs(vjAtStops2[0].getDepartureTime().getTime() - vjAtStops2[1].getArrivalTime().getTime());
									long min = parameters.getTest3_16c_MinimalTime();
									long max = parameters.getTest3_16c_MaximalTime();
									long diff = Math.abs(diffAbsolute1-diffAbsolute2)/ DIVIDER;
									if(diff >= min && diff<=max)	
										report3_16_1.updateStatus(Report.STATE.OK);
									else {
										ReportItem detailReportItem = new DetailReportItem("Test3_Sheet16_Step1_error", Report.STATE.ERROR,vjAtStops[0].getStopPointId(),vjAtStops2[1].getStopPointId());
										report3_16_1.addItem(detailReportItem);	
									}
								}
							}	
						}
					}
				}
			}
		}

		report2_17_1.computeDetailItemCount();
		report2_18_1.computeDetailItemCount();
		report2_18_2.computeDetailItemCount();
		report2_19.computeDetailItemCount();
		report2_20.computeDetailItemCount();
		report2_21.computeDetailItemCount();
		report2_22.computeDetailItemCount();
		report2_23.computeDetailItemCount();
		report2_24.computeDetailItemCount();
		report3_7.computeDetailItemCount();
		report3_9.computeDetailItemCount();
		report3_15.computeDetailItemCount();
		report3_16_1.computeDetailItemCount();
		report3_16_2.computeDetailItemCount();
		report3_16_3.computeDetailItemCount();

		sheet2_17.addItem(report2_17_1);
		sheet2_18.addItem(report2_18_1);
		sheet2_18.addItem(report2_18_2);
		sheet2_19.addItem(report2_19);
		sheet2_20.addItem(report2_20);
		sheet2_21.addItem(report2_21);
		sheet2_22.addItem(report2_22);
		sheet2_23.addItem(report2_23);
		sheet2_24.addItem(report2_24);
		sheet3_7.addItem(report3_7);
		sheet3_9.addItem(report3_9);
		sheet3_15.addItem(report3_15);
		sheet3_16.addItem(report3_16_1);
		sheet3_16.addItem(report3_16_2);
		sheet3_16.addItem(report3_16_3);

		category2.addItem(sheet2_17);
		category2.addItem(sheet2_18);
		category2.addItem(sheet2_19);
		category2.addItem(sheet2_20);
		category2.addItem(sheet2_21);
		category2.addItem(sheet2_22);
		category2.addItem(sheet2_23);
		category2.addItem(sheet2_24);
		category3.addItem(sheet3_7);
		category3.addItem(sheet3_9);
		category3.addItem(sheet3_15);
		category3.addItem(sheet3_16);

		result.add(category2);
		result.add(category3);
		return result;
	}

}
