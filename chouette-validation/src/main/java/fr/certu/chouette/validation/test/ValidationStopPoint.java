package fr.certu.chouette.validation.test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.geom.PrecisionModel;

import fr.certu.chouette.model.neptune.PTLink;
import fr.certu.chouette.model.neptune.Route;
import fr.certu.chouette.model.neptune.StopPoint;
import fr.certu.chouette.model.neptune.type.ImportedItems;
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
 */
public class ValidationStopPoint extends AbstractValidation implements IValidationPlugin<StopPoint> {

	private ValidationStepDescription validationStepDescription;

	public void init() {
		//TODO
		validationStepDescription = new ValidationStepDescription("", ValidationClassReportItem.CLASS.TWO.ordinal());
	}

	@Override
	public ValidationStepDescription getDescription() {
		return validationStepDescription;
	}

	@Override
	public List<ValidationClassReportItem> doValidate(List<StopPoint> beans, ValidationParameters parameters) {
		return validate(beans, parameters);
	}

	private List<ValidationClassReportItem> validate(List<StopPoint> stopPoints, ValidationParameters parameters) {

		ValidationClassReportItem category2 = new ValidationClassReportItem(ValidationClassReportItem.CLASS.TWO);
		ValidationClassReportItem category3 = new ValidationClassReportItem(ValidationClassReportItem.CLASS.THREE);

		ReportItem sheet10 = new SheetReportItem("Test2_Sheet10", 10);
		ReportItem sheet11 = new SheetReportItem("Test2_Sheet11", 11);
		//        ReportItem sheet14 = new SheetReportItem("Test2_Sheet14", 14);

		ReportItem sheet3_1 = new SheetReportItem("Test3_Sheet1", 1);
		ReportItem sheet3_2 = new SheetReportItem("Test3_Sheet2", 2);
		ReportItem sheet3_3 = new SheetReportItem("Test3_Sheet3", 3);
		ReportItem sheet3_5 = new SheetReportItem("Test3_Sheet5", 5);
		ReportItem sheet3_6 = new SheetReportItem("Test3_Sheet6", 6);
		ReportItem sheet3_10 = new SheetReportItem("Test3_Sheet10", 10);

		SheetReportItem report2_10_1 = new SheetReportItem("Test2_Sheet10_Step1", 1);
		SheetReportItem report2_11_1 = new SheetReportItem("Test2_Sheet11_Step1", 1);
		//        SheetReportItem report2_14_1 = new SheetReportItem("Test2_Sheet14_Step1", 1);

		SheetReportItem report3_1_1 = new SheetReportItem("Test3_Sheet1_Step1", 1);
		SheetReportItem report3_2_1 = new SheetReportItem("Test3_Sheet2_Step1", 1);
		SheetReportItem report3_3_1 = new SheetReportItem("Test3_Sheet3_Step1", 1);
		SheetReportItem report3_5_1 = new SheetReportItem("Test3_Sheet5_Step1", 1);
		SheetReportItem report3_6_1 = new SheetReportItem("Test3_Sheet6_Step1", 1);
		SheetReportItem report3_10_1 = new SheetReportItem("Test3_Sheet10_Step1", 1);
		SheetReportItem report3_10_2 = new SheetReportItem("Test3_Sheet10_Step2", 2);
		SheetReportItem report3_10_3 = new SheetReportItem("Test3_Sheet10_Step3", 3);

		List<ValidationClassReportItem> result = new ArrayList<ValidationClassReportItem>();
		float param = parameters.getTest3_1_MinimalDistance();
		float param2 = parameters.getTest3_2_MinimalDistance();
		List<Coordinate> listCoordinates = parameters.getTest3_2_Polygon();
		double distanceMin3_10 = parameters.getTest3_10_MinimalDistance();
		Coordinate first = listCoordinates.get(0);
		Coordinate last = listCoordinates.get(listCoordinates.size() - 1);
		if (!first.equals(last)) {
			listCoordinates.add(first);
		}
		Coordinate[] coordinates = listCoordinates.toArray(new Coordinate[0]);
		String pj = parameters.getProjection_reference();
		final int TEST = 99999;
		Map<String, Set<StopPoint>> stopPointsFromPTLinkMap = new HashMap<String, Set<StopPoint>>();
		int size = stopPoints.size();

		for (int i = 0; i < size; i++) {
			StopPoint stopPoint = stopPoints.get(i);

			//Test2.10.1
			String lineIdShortcut = stopPoint.getLineIdShortcut();
			if (lineIdShortcut != null) {
				if (stopPoint.getLine() == null || !stopPoint.getLine().getObjectId().equals(lineIdShortcut)) {
					ReportItem detailReportItem = new DetailReportItem("Test2_Sheet10_Step1_error", Report.STATE.ERROR);
					report2_10_1.addItem(detailReportItem);
				} else {
					report2_10_1.updateStatus(Report.STATE.OK);
				}
			}

			//Test2.11.1
			String ptNetworkIdShortcut = stopPoint.getPtNetworkIdShortcut();
			if (ptNetworkIdShortcut != null) {
				if (stopPoint.getPtNetwork() == null || !stopPoint.getPtNetwork().getObjectId().equals(ptNetworkIdShortcut)) {
					ReportItem detailReportItem = new DetailReportItem("Test2_Sheet11_Step1_error", Report.STATE.ERROR);
					report2_11_1.addItem(detailReportItem);
				} else {
					report2_11_1.updateStatus(Report.STATE.OK);
				}
			}
			//Category 3
			double y1 = (stopPoint.getLatitude() != null) ? stopPoint.getLatitude().doubleValue() : 0;
			double x1 = (stopPoint.getLongitude() != null) ? stopPoint.getLongitude().doubleValue() : 0;
			PrecisionModel precisionModel = new PrecisionModel(PrecisionModel.maximumPreciseValue);
			int SRID1 = (stopPoint.getLongLatType() != null) ? stopPoint.getLongLatType().epsgCode() : TEST;
			GeometryFactory factory1 = new GeometryFactory(precisionModel, SRID1);
			Coordinate coordinate = new Coordinate(x1, y1);
			Point point1 = factory1.createPoint(coordinate);

			for (int j = i + 1; j < size; j++) {
				StopPoint another = stopPoints.get(j);
				double y2 = (another.getLatitude() != null) ? another.getLatitude().doubleValue() : 0;
				double x2 = (another.getLongitude() != null) ? another.getLongitude().doubleValue() : 0;
//				int SRID2 = (another.getLongLatType() != null) ? another.getLongLatType().epsgCode() : TEST;
//				GeometryFactory factory2 = new GeometryFactory(precisionModel, SRID2);
//				Coordinate coordinate2 = new Coordinate(x2, y2);
//				Point point2 = factory2.createPoint(coordinate2);
//				DistanceOp distanceOp = new DistanceOp(point1, point2);
//				double distance = distanceOp.distance() * CONVERTER;
				double distance = distance(x1, y1, x2, y2); // in meters 

				//Test 3.1.1
				if (distance < param) {
					if (!stopPoint.getName().equals(another.getName())) {
						ReportItem detailReportItem = new DetailReportItem("Test3_Sheet1_Step1_warning", Report.STATE.WARNING,
								String.valueOf(param),
								stopPoint.getName() + "(" + stopPoint.getObjectId() + ")",
								another.getName() + "(" + another.getObjectId() + ")",
								String.valueOf(distance));
						report3_1_1.addItem(detailReportItem);
					} else {
						report3_1_1.updateStatus(Report.STATE.OK);
					}
				}
				//Test 3.2.1
				if (distance < param2) {
					if (!stopPoint.getContainedInStopAreaId().equals(another.getContainedInStopAreaId())) {
						ReportItem detailReportItem = new DetailReportItem("Test3_Sheet2_Step1_warning", Report.STATE.WARNING,
								String.valueOf(distance),
								String.valueOf(param2),
								stopPoint.getName() + "(" + stopPoint.getObjectId() + ")",
								another.getName() + "(" + another.getObjectId() + ")");
						report3_2_1.addItem(detailReportItem);
					} else {
						report3_2_1.updateStatus(Report.STATE.OK);
					}
				}

				//Test 3.3.1
				if (stopPoint.getName().equals(another.getName())
						&& (!stopPoint.getContainedInStopAreaId().equals(another.getContainedInStopAreaId())
								|| stopPoint.getContainedInStopAreaId() == null || another.getContainedInStopAreaId() == null)) 
				{
					if (stopPoint.getAddress() != null && another.getAddress() != null) 
					{
						if (stopPoint.getAddress().equals(another.getAddress())) 
						{
							ReportItem detailReportItem = new DetailReportItem("Test3_Sheet3_Step1_warning", Report.STATE.WARNING,
									stopPoint.getObjectId() + " : " + stopPoint.getName(), another.getObjectId() + " : " + stopPoint.getName());
							report3_3_1.addItem(detailReportItem);
						}
						else 
						{
							report3_3_1.updateStatus(Report.STATE.OK);
						}
					} 
					else 
					{
						ReportItem detailReportItem = new DetailReportItem("Test3_Sheet3_Step1_warning", Report.STATE.WARNING,
								stopPoint.getObjectId() + " : " + stopPoint.getName(), another.getObjectId() + " : " + stopPoint.getName());
						report3_3_1.addItem(detailReportItem);
					}
				}
			}
			//Test 3.5.1 & Test 3.6.1 a (redundant ???) 

			if (!pj.trim().equals(stopPoint.getLongLatType().toString()) )
			{
				ReportItem detailReportItem = new DetailReportItem("Test3_Sheet5_Step1_warning", Report.STATE.WARNING,
						stopPoint.getName() + "(" + stopPoint.getObjectId() + ")");
				report3_5_1.addItem(detailReportItem);

//				ReportItem detailReportItem6a = new DetailReportItem("Test3_Sheet6_Step1_warning_a", Report.STATE.WARNING,
//						stopPoint.getName() + "(" + stopPoint.getObjectId() + ")");
//				report3_6_1.addItem(detailReportItem6a);
			} else {
				report3_5_1.updateStatus(Report.STATE.OK);
//				report3_6_1.updateStatus(Report.STATE.OK);
			}

			//Test 3.6.1 b
			LinearRing shell = factory1.createLinearRing(coordinates);
			LinearRing[] holes = null;
			Polygon polygon = factory1.createPolygon(shell, holes);
			if (!polygon.contains(point1)) {
				ReportItem detailReportItem6b = new DetailReportItem("Test3_Sheet6_Step1_error_b", Report.STATE.ERROR,
						stopPoint.getName() + "(" + stopPoint.getObjectId() + ")");
				report3_6_1.addItem(detailReportItem6b);
			} else {
				report3_6_1.updateStatus(Report.STATE.OK);
			}

			//Test 3.10
			int count = 0;
			List<PTLink> ptLinks4Route = new ArrayList<PTLink>();
			// boolean exists = false;
			// boolean existsPTLink = false;
			Set<StopPoint> stopPointsSet = new HashSet<StopPoint>();
			ImportedItems importedItems = (stopPoint.getLine() != null) ? stopPoint.getLine().getImportedItems() : null;
			if (importedItems != null) {
				List<PTLink> ptLinks = importedItems.getPtLinks();
				for (int k = 0; k < ptLinks.size(); k++) {
					PTLink ptLink = ptLinks.get(k);
					StopPoint start = ptLink.getStartOfLink();
					StopPoint end = ptLink.getEndOfLink();
					//Test 3.10.1
					if (stopPoint.getObjectId().equals(start.getObjectId())
							|| stopPoint.getObjectId().equals(end.getObjectId())) {
						count++;
						ptLinks4Route.add(ptLink);
						// exists = true;
					}
					stopPointsSet.add(start);
					stopPointsSet.add(end);
					stopPointsFromPTLinkMap.put(ptLink.getRouteId(), stopPointsSet);

					//Test 3.10.3
					double yStart = (start != null && start.getLatitude() != null) ? start.getLatitude().doubleValue() : 0;
					double xStart = (start != null && start.getLongitude() != null) ? start.getLongitude().doubleValue() : 0;
//					int SRIDStart = (start != null && start.getLongLatType() != null) ? start.getLongLatType().epsgCode() : 0;
//					GeometryFactory factoryStart = new GeometryFactory(precisionModel, SRIDStart);
//					Point pointSart = factoryStart.createPoint(new Coordinate(xStart, yStart));

					double yEnd = (end != null && end.getLatitude() != null) ? end.getLatitude().doubleValue() : 0;
					double xEnd = (end != null && end.getLongitude() != null) ? end.getLongitude().doubleValue() : 0;
//					int SRIDEnd = (end != null && end.getLongLatType() != null) ? end.getLongLatType().epsgCode() : 0;
//					GeometryFactory factoryEnd = new GeometryFactory(precisionModel, SRIDEnd);
//					Point pointEnd = factoryEnd.createPoint(new Coordinate(xEnd, yEnd));
//
//					DistanceOp distanceOp = new DistanceOp(pointSart, pointEnd);
//					double distance = distanceOp.distance() * CONVERTER;
					
					double distance = distance(xStart, yStart, xEnd, yEnd); // in meters 
					if (distance < distanceMin3_10) {
						ReportItem detailReportItem = new DetailReportItem("Test3_Sheet10_Step3_warning", Report.STATE.WARNING,
								String.valueOf(distance), String.valueOf(distanceMin3_10));
						report3_10_3.addItem(detailReportItem);
					} else {
						report3_10_3.updateStatus(Report.STATE.OK);
					}

					List<Route> routes = importedItems.getRoutes();
					for (Route route : routes) {
						if (route.getPtLinkIds().contains(ptLink.getObjectId())) {
							// existsPTLink = true;
						}
					}
					//Test 2.14.1 b
					//                    if (!existsPTLink) {
						//                        ReportItem detailReportItem = new DetailReportItem("Test2_Sheet14_Step1b_error", Report.STATE.ERROR,
					//                                ptLink.getName() + "(" + ptLink.getObjectId() + ")");
					//                        report2_14_1.addItem(detailReportItem);
					//                    } else {
					//                        report2_14_1.updateStatus(Report.STATE.OK);
					//                    }
				}

				//Test 2.14.1 a
				//                if (!exists) {
				//                    ReportItem detailReportItem = new DetailReportItem("Test2_Sheet14_Step1a_error", Report.STATE.ERROR,
				//                            stopPoint.getName() + "(" + stopPoint.getObjectId() + ")");
				//                    report2_14_1.addItem(detailReportItem);
				//                } else {
				//                    report2_14_1.updateStatus(Report.STATE.OK);
				//                }
				//Test 3.10.1 a
				if (count == 1 || count == 2) {
					report3_10_1.updateStatus(Report.STATE.OK);
				} else {
					ReportItem detailReportItem = new DetailReportItem("Test3_Sheet10_Step1_error_a", Report.STATE.ERROR,
							stopPoint.getName() + "(" + stopPoint.getObjectId() + ")");
					report3_10_1.addItem(detailReportItem);
				}
				//test 3.10.1 b
				for (int k = 0; k < ptLinks4Route.size(); k++) {
					PTLink ptLink = ptLinks4Route.get(k);
					PTLink next = (k == ptLinks4Route.size() - 1) ? ptLinks4Route.get(k) : ptLinks4Route.get(k + 1);
					if (ptLink.getRouteId() != null) {
						if (!ptLink.getRouteId().equals(next.getRouteId())) {
							ReportItem detailReportItem = new DetailReportItem("Test3_Sheet10_Step1_error_b", Report.STATE.ERROR);
							report3_10_1.addItem(detailReportItem);
							break;
						} else {
							report3_10_1.updateStatus(Report.STATE.OK);
						}
					}
				}
			}
		}

		//Test 3.10.2
		Set<String> containedInSet = new HashSet<String>();
		for (String routeId : stopPointsFromPTLinkMap.keySet()) {
			int count = 0;
			Set<StopPoint> stopPointsSet = stopPointsFromPTLinkMap.get(routeId);
			for (StopPoint stopPoint : stopPointsSet) {
				if (!containedInSet.add(stopPoint.getContainedInStopAreaId())) {
					count++;
				}
			}
			if (count != 0) {
				ReportItem detailReportItem = new DetailReportItem("Test3_Sheet10_Step2_warning", Report.STATE.WARNING, routeId);
				report3_10_2.addItem(detailReportItem);
			} else {
				report3_10_2.updateStatus(Report.STATE.OK);
			}
		}
		report2_10_1.computeDetailItemCount();
		report2_11_1.computeDetailItemCount();
		//        report2_14_1.computeDetailItemCount();

		report3_1_1.computeDetailItemCount();
		report3_2_1.computeDetailItemCount();
		report3_3_1.computeDetailItemCount();
		report3_5_1.computeDetailItemCount();
		report3_6_1.computeDetailItemCount();
		report3_10_1.computeDetailItemCount();
		report3_10_2.computeDetailItemCount();
		report3_10_3.computeDetailItemCount();

		sheet10.addItem(report2_10_1);
		sheet11.addItem(report2_11_1);
		//        sheet14.addItem(report2_14_1);

		sheet3_1.addItem(report3_1_1);
		sheet3_2.addItem(report3_2_1);
		sheet3_3.addItem(report3_3_1);
		sheet3_5.addItem(report3_5_1);
		sheet3_6.addItem(report3_6_1);
		sheet3_10.addItem(report3_10_1);
		sheet3_10.addItem(report3_10_2);
		sheet3_10.addItem(report3_10_3);

		category2.addItem(sheet10);
		category2.addItem(sheet11);
		//        category2.addItem(sheet14);

		category3.addItem(sheet3_1);
		category3.addItem(sheet3_2);
		category3.addItem(sheet3_3);
		category3.addItem(sheet3_5);
		category3.addItem(sheet3_6);
		category3.addItem(sheet3_10);

		result.add(category2);
		result.add(category3);
		return result;
	}
}
