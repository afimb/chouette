package fr.certu.chouette.validation.test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import lombok.Getter;

import org.apache.log4j.Logger;

import fr.certu.chouette.model.neptune.JourneyPattern;
import fr.certu.chouette.model.neptune.StopPoint;
import fr.certu.chouette.model.neptune.TimeSlot;
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
 * Validation plugin based on VehicleJourneys attributes and relationships
 * <li>
 * <ul>2.17 : </ul>
 * <ul>2.18 : </ul>
 * <ul>2.19 : </ul>
 * <ul>2.20 : </ul>
 * <ul>2.21 : </ul>
 * <ul>2.22 : </ul>
 * <ul>2.23 : </ul>
 * <ul>2.24 : </ul>
 * <ul>3.7 : </ul>
 * <ul>3.9 : </ul>
 * <ul>3.15 : </ul>
 * <ul>3.16 : </ul>
 * </li>
 * 
 * @author mamadou keira
 *
 */
public class ValidationVehicleJourney extends AbstractValidation implements IValidationPlugin<VehicleJourney>{

	private static final Logger logger = Logger.getLogger(ValidationVehicleJourney.class);

	private ValidationStepDescription validationStepDescription;
	private final double DIVIDER = 1000 ; 

	public void init(){
		validationStepDescription = new ValidationStepDescription("", ValidationClassReportItem.CLASS.TWO.ordinal());
	}

	@Override
	public ValidationStepDescription getDescription() {
		return validationStepDescription;
	}
	@Override
	public List<ValidationClassReportItem> doValidate(List<VehicleJourney> beans,ValidationParameters parameters) {	
		return validate(beans,parameters);	
	}

	private List<ValidationClassReportItem> validate(List<VehicleJourney> vehicleJourneys, ValidationParameters parameters){
		logger.info("start validate "+vehicleJourneys.size()+" vehicle journeys");
		ValidationClassReportItem category2 = new ValidationClassReportItem(ValidationClassReportItem.CLASS.TWO);
		ValidationClassReportItem category3 = new ValidationClassReportItem(ValidationClassReportItem.CLASS.THREE);

		ReportItem sheet5 = new SheetReportItem("Test2_Sheet5",5);
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

		SheetReportItem reportItem2 = new SheetReportItem("Test2_Sheet5_Step2",2);
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
		// SheetReportItem report3_16_2 = new SheetReportItem("Test3_Sheet16_Step2",2);
		SheetReportItem report3_16_3 = new SheetReportItem("Test3_Sheet16_Step3",3);

		List<ValidationClassReportItem> result = new ArrayList<ValidationClassReportItem>();
		Map<String, Set<VehicleJourney>> map = new TreeMap<String, Set<VehicleJourney>>();
		// Set<VehicleJourney> set = new HashSet<VehicleJourney>();

		double param3_15 = parameters.getTest3_15_MinimalTime();
		double min3_7 = parameters.getTest3_7_MinimalDistance();
		double max3_7 = parameters.getTest3_7_MaximalDistance();
		double min3_9 = parameters.getTest3_9_MinimalSpeed();
		double max3_9 = parameters.getTest3_9_MaximalSpeed();
		long param3_16_1 = parameters.getTest3_16_1_MaximalTime();
		long param3_16_3a = parameters.getTest3_16_3a_MaximalTime();
		long param3_16_3b = parameters.getTest3_16_3b_MaximalTime();

		if(vehicleJourneys != null)
		{
			for (int i=0; i<vehicleJourneys.size();i++) 
			{
				VehicleJourney vehicleJourney = vehicleJourneys.get(i);

				//Test 2.5.2
				if (vehicleJourney.getTimetables() == null || vehicleJourney.getTimetables().isEmpty()) {
					ReportItem detailReportItem = new DetailReportItem("Test2_Sheet5_Step2_error",Report.STATE.ERROR);
					reportItem2.addItem(detailReportItem);
				} else {
					reportItem2.updateStatus(Report.STATE.OK);	
				}

				String key = vehicleJourney.getRouteId();
				Set<VehicleJourney> values = map.get(key); 
				if(values != null )
				{
					values.add(vehicleJourney);
				}
				else 
				{
					values = new HashSet<VehicleJourney>();
					values.add(vehicleJourney);
					map.put(vehicleJourney.getRouteId(),values);	
				}
				if(vehicleJourney.getRoute() != null){
					if(!vehicleJourney.getRouteId().equals(vehicleJourney.getRoute().getObjectId())){
						ReportItem detailReportItem = new DetailReportItem("Test2_Sheet17_Step1_error", Report.STATE.ERROR, 
								vehicleJourney.getName()+"("+vehicleJourney.getObjectId()+")",vehicleJourney.getRouteId());
						report2_17_1.addItem(detailReportItem);	
					}else
						report2_17_1.updateStatus(Report.STATE.OK);	
				}else{
					ReportItem detailReportItem = new DetailReportItem("Test2_Sheet17_Step1_error", Report.STATE.ERROR, 
							vehicleJourney.getName()+"("+vehicleJourney.getObjectId()+")",vehicleJourney.getRouteId());
					report2_17_1.addItem(detailReportItem);
				}				
				String journeyPatternId = vehicleJourney.getJourneyPatternId();
				JourneyPattern journeyPattern = vehicleJourney.getJourneyPattern(); 
				//Test 2.18.1
				if(journeyPatternId != null){
					if(journeyPattern == null){
						ReportItem detailReportItem = new DetailReportItem("Test2_Sheet18_Step1_error", Report.STATE.ERROR, 
								vehicleJourney.getName()+"("+vehicleJourney.getObjectId()+")");
						report2_18_1.addItem(detailReportItem);	
					}else
						report2_18_1.updateStatus(Report.STATE.OK);
				}
				List<String> stopPointObjectIds = (vehicleJourney.getJourneyPattern() != null && vehicleJourney.getJourneyPattern().getStopPoints() != null) ? VehicleJourney.extractObjectIds(vehicleJourney.getJourneyPattern().getStopPoints()) : null;

				List<String> stopPointIds = (vehicleJourney.getJourneyPattern() != null) ?  vehicleJourney.getJourneyPattern().getStopPointIds() : null;
				if(stopPointObjectIds != null && stopPointIds != null)
				{
					//Test 2.18.2 a
					//					if(!stopPointIds.containsAll(stopPointObjectIds)){
					//						ReportItem detailReportItem = new DetailReportItem("Test2_Sheet18_Step2_error_a", 
					//								Report.STATE.ERROR);
					//						report2_18_2.addItem(detailReportItem);	
					//					}else
					//						report2_18_2.updateStatus(Report.STATE.OK);

					//Test 2.18.2 b
					int count = 0;
					List<VehicleJourneyAtStop> vehicleJourneyAtStops = vehicleJourney.getVehicleJourneyAtStops();
					for (VehicleJourneyAtStop vehicleJourneyAtStop : vehicleJourneyAtStops) 
					{
						if(stopPointObjectIds.contains(vehicleJourneyAtStop.getStopPointId()))
							count++;
					}


					if(count != stopPointObjectIds.size())
					{
						ReportItem detailReportItem = new DetailReportItem("Test2_Sheet18_Step2_error_b", Report.STATE.ERROR);
						report2_18_2.addItem(detailReportItem);	
					}
					else
					{
						report2_18_2.updateStatus(Report.STATE.OK);
					}
				}
				//Test 2.19.1
				String lineShortCutId = vehicleJourney.getLineIdShortcut();
				if(lineShortCutId != null){
					String lineObjectId = (vehicleJourney.getLine() != null) ? vehicleJourney.getLine().getObjectId() : null;
					if(!lineShortCutId.equals(lineObjectId))
					{
						ReportItem detailReportItem = new DetailReportItem("Test2_Sheet19_Step1_error", Report.STATE.ERROR,
								vehicleJourney.getName()+"("+vehicleJourney.getObjectId()+")");
						report2_19.addItem(detailReportItem);	
					}
					else
						report2_19.updateStatus(Report.STATE.OK);	
				}

				//Test 2.20.1
				String companyId = vehicleJourney.getCompanyId();
				if(companyId != null){
					String companyObjectId = (vehicleJourney.getCompany() != null) ? vehicleJourney.getCompany().getObjectId() : null;
					if(!companyId.equals(companyObjectId)){
						ReportItem detailReportItem = new DetailReportItem("Test2_Sheet20_Step1_error", Report.STATE.ERROR,
								vehicleJourney.getName()+"("+vehicleJourney.getObjectId()+")");
						report2_20.addItem(detailReportItem);	
					}else
						report2_20.updateStatus(Report.STATE.OK);	
				}

				//Test 2.21.1
				String timeSlotId = vehicleJourney.getTimeSlotId();
				TimeSlot timeSlot = vehicleJourney.getTimeSlot();
				if(timeSlotId != null){
					if(timeSlot == null){
						ReportItem detailReportItem = new DetailReportItem("Test2_Sheet21_Step1_error", Report.STATE.ERROR, 
								vehicleJourney.getName()+"("+vehicleJourney.getObjectId()+")");
						report2_21.addItem(detailReportItem);	
					}else
						report2_21.updateStatus(Report.STATE.OK);	
				}
				//Test 2.24.1
				String routeIdFromVJ = (vehicleJourney.getRouteId() != null) ? vehicleJourney.getRouteId() : "";
				String routeIdFromJP = (vehicleJourney.getJourneyPattern() != null) ? vehicleJourney.getJourneyPattern().getRouteId() : null;
				if(!routeIdFromVJ.equals(routeIdFromJP)){
					ReportItem detailReportItem = new DetailReportItem("Test2_Sheet24_Step1_error", Report.STATE.ERROR);
					report2_24.addItem(detailReportItem);	
				}else
					report2_24.updateStatus(Report.STATE.OK);
				//Test 3.16.2
//				if (vehicleJourney.getTimetables() != null)
//				{
//					for (Timetable timetable : vehicleJourney.getTimetables()) {
//						if(!timetable.getVehicleJourneyIds().contains(vehicleJourney.getObjectId())){
//							ReportItem detailReportItem = new DetailReportItem("Test3_Sheet16_Step2_warning", Report.STATE.WARNING,
//									vehicleJourney.getName()+"("+vehicleJourney.getObjectId()+")");
//							report3_16_2.addItem(detailReportItem);	
//						}else
//							report3_16_2.updateStatus(Report.STATE.OK);
//					}	
//				}
			}

			//
			// Map<String, Set<VehicleJourneyAtStop[]>> doubletMap = new TreeMap<String, Set<VehicleJourneyAtStop[]>>();
			Map<String, List<IntervalDuration>> segmentDurationMap = new HashMap<String, List<IntervalDuration>>();
			//for (String key : map.keySet()) 
			for (Iterator<Set<VehicleJourney>> iterator = map.values().iterator(); iterator.hasNext();) 
			{
				Set<VehicleJourney> vJSet = iterator.next() ;//map.get(key);
				iterator.remove();

				for (VehicleJourney vehicleJourney : vJSet) 
				{
					List<VehicleJourneyAtStop> vehicleJourneyAtStops =vehicleJourney.getVehicleJourneyAtStops();
					// Set<VehicleJourneyAtStop[]> stopsSet = new HashSet<VehicleJourneyAtStop[]>();
					if(vehicleJourneyAtStops != null){
						for(VehicleJourneyAtStop vehicleJourneyAtStop : vehicleJourneyAtStops){

							//Test 2.22.1
							if(vehicleJourneyAtStop.getStopPoint() == null){
								String arrivalTime = String.valueOf(vehicleJourneyAtStop.getArrivalTime());
								ReportItem detailReportItem = new DetailReportItem("Test2_Sheet22_Step1_error", Report.STATE.ERROR,arrivalTime);
								report2_22.addItem(detailReportItem);	
							}else{
								report2_22.updateStatus(Report.STATE.OK);
							}


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
							if(arrivalTime != CONSTANT){
								if(diff > param3_15){
									ReportItem detailReportItem = new DetailReportItem("Test3_Sheet15_Step1_error", Report.STATE.ERROR,
											String.valueOf(param3_15));
									report3_15.addItem(detailReportItem);	
								}else
									report3_15.updateStatus(Report.STATE.OK);	
							}
						}
						//Test 3.7 && Test 3.9
						if(vehicleJourneyAtStops.size() >1){
							for (VehicleJourneyAtStop vJAtStop : vehicleJourneyAtStops) {
								String stopPointId = vJAtStop.getStopPointId();
								StopPoint stopPoint = vJAtStop.getStopPointByObjectId(stopPointId);
								if(stopPoint != null){
									double y1 = (stopPoint.getLatitude()!=null) ? stopPoint.getLatitude().doubleValue():0;
									double x1 = (stopPoint.getLongitude()!=null) ? stopPoint.getLongitude().doubleValue():0;
									//									PrecisionModel precisionModel = new PrecisionModel(PrecisionModel.maximumPreciseValue);
									//									int SRID1 = (stopPoint.getLongLatType()!= null) ? stopPoint.getLongLatType().epsgCode() : 0;
									//									GeometryFactory factory1 = new GeometryFactory(precisionModel, SRID1);
									//									Coordinate coordinate = new Coordinate(x1, y1);
									//									Point point1 = factory1.createPoint(coordinate);

									for (VehicleJourneyAtStop vJAtStop2 : vehicleJourneyAtStops) {
										long diff = vJAtStop2.getOrder()-vJAtStop.getOrder();
										if(diff == 1){
											VehicleJourneyAtStop[] vJAtStops = new VehicleJourneyAtStop[2];
											vJAtStops[0] = vJAtStop;
											vJAtStops[1] = vJAtStop2;
											// stopsSet.add(vJAtStops);
											IntervalDuration duration = new IntervalDuration(vJAtStop, vJAtStop2);
											List<IntervalDuration> list = segmentDurationMap.get(duration.getStopPairKey());
											if (list == null)
											{
												list = new ArrayList<ValidationVehicleJourney.IntervalDuration>();
												segmentDurationMap.put(duration.getStopPairKey(),list);
											}
											list.add(duration);

											//Test 3.7.1
											String stopPointId2 = vJAtStop2.getStopPointId();
											StopPoint stopPoint2 = vJAtStop2.getStopPointByObjectId(stopPointId2);
											if(stopPoint2 != null)
											{
												double y2 = (stopPoint2.getLatitude()!=null) ? stopPoint2.getLatitude().doubleValue():0;
												double x2 = (stopPoint2.getLongitude()!=null) ? stopPoint2.getLongitude().doubleValue():0;
												//												int SRID2 = (stopPoint2.getLongLatType()!= null) ? stopPoint2.getLongLatType().epsgCode() : 0;
												//												GeometryFactory factory2 = new GeometryFactory(precisionModel, SRID2);
												//												Coordinate coordinate2 = new Coordinate(x2, y2);
												//												Point point2 = factory2.createPoint(coordinate2);
												//												DistanceOp distanceOp = new DistanceOp(point1, point2);
												//												double distance = distanceOp.distance()* 6378000 /180 * Math.PI; // TODO
												double distance = distance(x1,y1,x2,y2);

												if(distance < min3_7  || distance > max3_7){
													ReportItem detailReportItem = new DetailReportItem("Test3_Sheet7_Step1_warning", Report.STATE.WARNING,
															String.valueOf(min3_7),String.valueOf(max3_7));
													report3_7.addItem(detailReportItem);	
												}else
													report3_7.updateStatus(Report.STATE.OK);
												//Test 3.9.1

												double arrivalTime = 0;
												if (vJAtStop2.getArrivalTime() != null) 
													arrivalTime =  vJAtStop2.getArrivalTime().getTime() /DIVIDER  ;
												else if (vJAtStop2.getWaitingTime() != null)
													arrivalTime = (vJAtStop2.getDepartureTime().getTime() - vJAtStop2.getWaitingTime().getTime() ) /DIVIDER  ;
												else 
													arrivalTime = vJAtStop2.getDepartureTime().getTime() /DIVIDER;
												double departureTime = vJAtStop.getDepartureTime().getTime() /DIVIDER;

												double speed = max3_9 * 10;
												if ((arrivalTime - departureTime) > 0) 
													speed = distance / (arrivalTime - departureTime) / 1000;

												if(speed < min3_9 || speed > max3_9)
												{
//														logger.info("speed between "+stopPointId+" and "+stopPointId2+" is "+speed+", distance = "+distance);
//														logger.info("   departure Time = "+departureTime*3600+", arrivalTime = "+arrivalTime*3600);
													ReportItem detailReportItem = new DetailReportItem("Test3_Sheet9_Step1_warning", Report.STATE.WARNING,
															stopPointId,stopPointId2,String.valueOf(min3_9),String.valueOf(max3_9));
													report3_9.addItem(detailReportItem);	
												}else
													report3_9.updateStatus(Report.STATE.OK);

											}
											//Test 3.16.3 a
											double departureTime = (vJAtStop.getDepartureTime() != null) ? vJAtStop.getDepartureTime().getTime() /1000 : 0;
											double arrivalTime = (vJAtStop2.getArrivalTime() != null) ? vJAtStop2.getArrivalTime().getTime() /1000 : ((vJAtStop2.getDepartureTime() != null) ? vJAtStop2.getDepartureTime().getTime() /1000 : 1000) ;
											long diffTime = (long) (Math.abs(arrivalTime - departureTime));
											if(diffTime > param3_16_3a){
												ReportItem detailReportItem = new DetailReportItem("Test3_Sheet16_Step3_error_a", Report.STATE.ERROR,
														String.valueOf(diffTime),String.valueOf(param3_16_3a));
												report3_16_3.addItem(detailReportItem);	
											}

											//Test 3.16.3 b
											if(departureTime <= arrivalTime)
												report3_16_3.updateStatus(Report.STATE.OK);
											//Test 3.16.3 b (suite)
											else if(arrivalTime > param3_16_3b) {
												ReportItem detailReportItem = new DetailReportItem("Test3_Sheet16_Step3_error_b", Report.STATE.ERROR,
														String.valueOf(param3_16_3b));
												report3_16_3.addItem(detailReportItem);	
											}else
												report3_16_3.updateStatus(Report.STATE.OK);
											
											break;
										}
									}
								}
							}
						}
					}
					//doubletMap.put(vehicleJourney.getObjectId(), stopsSet);
				}
			}
			//Test 3.16.1 

			for (List<IntervalDuration> durations : segmentDurationMap.values()) 
			{
				if (durations.size() > 1)
				{
					for (int i = 0; i < durations.size()-1; i++)
					{
						long diffAbsolute1 = durations.get(i).getDurationInMillis();
						for (int j = i+1; j < durations.size(); j++)
						{
							long diffAbsolute2 = durations.get(j).getDurationInMillis();
							long diff = (long) (Math.abs(diffAbsolute1-diffAbsolute2)/ 1000);
							if(diff <= param3_16_1)	 
								report3_16_1.updateStatus(Report.STATE.OK);
							else {
								ReportItem detailReportItem = new DetailReportItem("Test3_Sheet16_Step1_error", Report.STATE.ERROR,
										durations.get(i).getFirstStopPointId(),durations.get(i).getNextStopPointId(), String.valueOf(diff));
								report3_16_1.addItem(detailReportItem);	
							}
						}
					}
				}
			}
			//			for (String key : doubletMap.keySet()) {
			//				Set<VehicleJourneyAtStop[]> stopsSet = doubletMap.get(key);
			//				for (VehicleJourneyAtStop[] vjAtStops : stopsSet) {
			//					for (String key2 : doubletMap.keySet()) {
			//						if(!key.equals(key2)){
			//							Set<VehicleJourneyAtStop[]> stopsSet2 = doubletMap.get(key2);
			//							for (VehicleJourneyAtStop[] vjAtStops2 : stopsSet2) {
			//								if(vjAtStops[0].getStopPointId().equals(vjAtStops2[0].getStopPointId()) && 
			//										vjAtStops[1].getStopPointId().equals(vjAtStops2[1].getStopPointId())){
			//									long diffAbsolute1 = Math.abs(vjAtStops[0].getDepartureTime().getTime() - vjAtStops[1].getArrivalTime().getTime());
			//									long diffAbsolute2 = Math.abs(vjAtStops2[0].getDepartureTime().getTime() - vjAtStops2[1].getArrivalTime().getTime());
			//									long diff = Math.abs(diffAbsolute1-diffAbsolute2)/ DIVIDER;
			//									if(diff >= min && diff<=max)	
			//										report3_16_1.updateStatus(Report.STATE.OK);
			//									else {
			//										ReportItem detailReportItem = new DetailReportItem("Test3_Sheet16_Step1_error", Report.STATE.ERROR,
			//												vjAtStops[0].getStopPointId(),vjAtStops2[1].getStopPointId(), String.valueOf(diff));
			//										report3_16_1.addItem(detailReportItem);	
			//									}
			//								}
			//							}	
			//						}
			//					}
			//				}
			//			}
		}

		reportItem2.computeDetailItemCount();
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
		// report3_16_2.computeDetailItemCount();
		report3_16_3.computeDetailItemCount();

		sheet5.addItem(reportItem2);
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
		// sheet3_16.addItem(report3_16_2);
		sheet3_16.addItem(report3_16_3);

		category2.addItem(sheet5);
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
		logger.info("vehicle journey validation terminated");

		return result;
	}

	class IntervalDuration 
	{
		@Getter String vehicleJourneyId;
		//		@Getter String routeId;
		//		@Getter String lineId;
		@Getter String firstStopPointId;
		@Getter String nextStopPointId;
		@Getter long rank;
		@Getter long durationInMillis;
		@Getter String stopPairKey ;

		public IntervalDuration(VehicleJourneyAtStop first, VehicleJourneyAtStop next)
		{
			vehicleJourneyId = first.getVehicleJourney().getObjectId();
			//			Route route = (first.getVehicleJourney().getRoute() != null)? first.getVehicleJourney().getRoute():first.getVehicleJourney().getJourneyPattern().getRoute();
			//			if (route != null)
			//			{
			//				routeId = route.getObjectId();
			//				if (route.getLine() != null)
			//					lineId = route.getLine().getObjectId();
			//			}
			rank = first.getOrder();
			durationInMillis = next.getArrivalTime().getTime() - first.getDepartureTime().getTime() ;
			if (durationInMillis < 0) durationInMillis += 86400000; // passage à minuit
			firstStopPointId = first.getStopPoint().getObjectId();
			nextStopPointId = next.getStopPoint().getObjectId();
			stopPairKey = firstStopPointId+"@"+nextStopPointId;
		}
	}


}
