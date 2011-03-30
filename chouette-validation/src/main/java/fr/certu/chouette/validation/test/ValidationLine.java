/**
 * 
 */
package fr.certu.chouette.validation.test;

import java.util.ArrayList;
import java.util.List;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.geom.PrecisionModel;
import com.vividsolutions.jts.operation.distance.DistanceOp;

import lombok.Getter;
import fr.certu.chouette.model.neptune.AccessPoint;
import fr.certu.chouette.model.neptune.Facility;
import fr.certu.chouette.model.neptune.GroupOfLine;
import fr.certu.chouette.model.neptune.Line;
import fr.certu.chouette.model.neptune.PTLink;
import fr.certu.chouette.model.neptune.PTNetwork;
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
 * @author mamadou keira
 *
 */
public class ValidationLine implements IValidationPlugin<Line>
{
	@Getter private ValidationStepDescription description;
	private final double CONVERTER = 6371 /180;
	public void init()
	{
		//TODO 
		description = new ValidationStepDescription("",ValidationClassReportItem.CLASS.TWO.ordinal());
	}

	@Override
	public List<ValidationClassReportItem> doValidate(List<Line> lines,ValidationParameters parameters) 
	{
		List<ValidationClassReportItem> items = validate(lines,parameters); 
		return items;
	}

	private  List<ValidationClassReportItem> validate(List<Line> lines,ValidationParameters parameters){
		ValidationClassReportItem category2 = new ValidationClassReportItem(ValidationClassReportItem.CLASS.TWO);
		ValidationClassReportItem category3 = new ValidationClassReportItem(ValidationClassReportItem.CLASS.THREE);

		List<ValidationClassReportItem> result = new ArrayList<ValidationClassReportItem>();

		ReportItem sheet1 = new SheetReportItem("Test2_Sheet1", 1);
		ReportItem sheet2 = new SheetReportItem("Test2_Sheet2",2);
		ReportItem sheet6 = new SheetReportItem("Test2_Sheet6",6);
		ReportItem sheet7 = new SheetReportItem("Test2_Sheet7",7);
		ReportItem sheet3_4 = new SheetReportItem("Test3_Sheet4",4);
		ReportItem sheet2_26 = new SheetReportItem("Test2_Sheet26",26);
		ReportItem sheet2_27 = new SheetReportItem("Test2_Sheet27",27);
		ReportItem sheet2_28 = new SheetReportItem("Test2_Sheet28",28);

		SheetReportItem report2_1_1 = new SheetReportItem("Test2_Sheet1_Step1",1);
		SheetReportItem report2_1_2 = new SheetReportItem("Test2_Sheet1_Step2", 2);
		SheetReportItem report2_2_1 = new SheetReportItem("Test2_Sheet2_Step1", 1);
		SheetReportItem report2_6_1 = new SheetReportItem("Test2_Sheet6_Step1",1);
		SheetReportItem report2_6_2 = new SheetReportItem("Test2_Sheet6_Step2",2);
		SheetReportItem report2_7_1 = new SheetReportItem("Test2_Sheet7_Step1",1);
		SheetReportItem report3_4_1 = new SheetReportItem("Test3_Sheet4_Step1",1);
		SheetReportItem report2_26_1 = new SheetReportItem("Test2_Sheet26_Step1",1);
		SheetReportItem report2_27_1 = new SheetReportItem("Test2_Sheet27_Step1",1);
		SheetReportItem report2_28_1 = new SheetReportItem("Test2_Sheet28_Step1",1);

		/**
		 * VALIDATION STOPPOINT MIGRATED
		 */
		ReportItem sheet10 = new SheetReportItem("Test2_Sheet10",10);
		ReportItem sheet11 = new SheetReportItem("Test2_Sheet11",11);
		ReportItem sheet14 = new SheetReportItem("Test2_Sheet14",14);

		ReportItem sheet3_1 = new SheetReportItem("Test3_Sheet1",1);
		ReportItem sheet3_2 = new SheetReportItem("Test3_Sheet2",2);
		ReportItem sheet3_3 = new SheetReportItem("Test3_Sheet3",3);
		ReportItem sheet3_5 = new SheetReportItem("Test3_Sheet5",5);
		ReportItem sheet3_6 = new SheetReportItem("Test3_Sheet6",6);
		ReportItem sheet3_10 = new SheetReportItem("Test3_Sheet10",10);

		SheetReportItem report2_10_1 = new SheetReportItem("Test2_Sheet10_Step1",1);
		SheetReportItem report2_11_1 = new SheetReportItem("Test2_Sheet11_Step1",1);
		SheetReportItem report2_14_1 = new SheetReportItem("Test2_Sheet14_Step1",1);

		SheetReportItem report3_1_1 = new SheetReportItem("Test3_Sheet1_Step1",1);
		SheetReportItem report3_2_1 = new SheetReportItem("Test3_Sheet2_Step1",1);
		SheetReportItem report3_3_1 = new SheetReportItem("Test3_Sheet3_Step1",1);
		SheetReportItem report3_5_1 = new SheetReportItem("Test3_Sheet5_Step1",1);
		SheetReportItem report3_6_1 = new SheetReportItem("Test3_Sheet6_Step1",1);
		SheetReportItem report3_10_1 = new SheetReportItem("Test3_Sheet10_Step1",1);
		SheetReportItem report3_10_2 = new SheetReportItem("Test3_Sheet10_Step2",2);
		SheetReportItem report3_10_3 = new SheetReportItem("Test3_Sheet10_Step3",3);

		float param = parameters.getTest3_1_MinimalDistance();
		float param2 = parameters.getTest3_2_MinimalDistance();
		List<Coordinate> listCoordinates = parameters.getTest3_2_Polygon();
		Coordinate first = listCoordinates.get(0);
		Coordinate last = listCoordinates.get(listCoordinates.size()-1);
		if(!first.equals(last))
			listCoordinates.add(first);
		Coordinate[] coordinates = listCoordinates.toArray(new Coordinate[0]);
		double distanceMin3_10 = parameters.getTest3_10_MinimalDistance();
		
		for (int i=0;i<lines.size();i++) {
			Line line = lines.get(i);
			PTNetwork network = line.getPtNetwork();
			if (network == null)
			{
				ReportItem failedItem = new DetailReportItem("Test2_Sheet1_fatal");
				failedItem.setStatus(Report.STATE.FATAL);
				failedItem.addMessageArgs(line.getObjectId());
				report2_1_1.addItem(failedItem);

			}
			else
			{
				List<String> lineIds = network.getLineIds();
				if (lineIds != null && !lineIds.isEmpty())
				{
					//Test 2.1.1
					if (!lineIds.contains(line.getObjectId()))
					{
						ReportItem failedItem = new DetailReportItem("Test2_Sheet1_Step1_error");
						failedItem.setStatus(Report.STATE.ERROR);
						failedItem.addMessageArgs(network.getObjectId(),line.getObjectId());
						report2_1_1.addItem(failedItem);
					}else {
						report2_1_1.updateStatus(Report.STATE.OK);
					}
					//Test 2.1.2
					String ptNeworkId = line.getPtNetworkIdShortcut();
					if (!ptNeworkId.equals(network.getObjectId()))
					{
						ReportItem failedItem = new DetailReportItem("Test2_Sheet1_Step2_error");
						failedItem.setStatus(Report.STATE.ERROR);
						failedItem.addMessageArgs(network.getObjectId(),line.getObjectId());
						report2_1_2.addItem(failedItem);
					}else {
						report2_1_2.updateStatus(Report.STATE.OK);
					}
				}
			}	
			//Test 2.2.1
			ImportedItems importedItems =line.getImportedItems();
			if(importedItems != null){
				for (GroupOfLine groupOfLine : importedItems.getGroupOfLines()) {
					if(groupOfLine.getObjectId().equals(line.getGroupOfLine().getObjectId())){
						if(!groupOfLine.getLineIds().contains(line.getObjectId())){
							ReportItem detailReportItem = new DetailReportItem("Test2_Sheet2_Step1_error",Report.STATE.ERROR);
							report2_2_1.addItem(detailReportItem);
						}else 
							report2_2_1.updateStatus(Report.STATE.OK);	
					}
				}	


				//Test 2.6.1
				List<String> lineEnds = line.getLineEnds();
				List<String> stopPointIds = Line.extractObjectIds(importedItems.getStopPoints());
				if(lineEnds != null){
					if(!stopPointIds.containsAll(lineEnds)){
						ReportItem detailReportItem = new DetailReportItem("Test2_Sheet6_Step1_error", Report.STATE.ERROR);
						report2_6_1.addItem(detailReportItem);
					}else {
						report2_6_1.updateStatus(Report.STATE.OK);	
					}
					//Test 2.6.2
					List<String> lineEndList  = Line.extractObjectIds(line.getLineEndList());
					if(!lineEndList.containsAll(lineEnds)){
						ReportItem detailReportItem = new DetailReportItem("Test2_Sheet6_Step2_error",Report.STATE.ERROR);
						report2_6_2.addItem(detailReportItem);
					}else 
						report2_6_2.updateStatus(Report.STATE.OK);	
				}

				//Test 2.7
				List<String> routeIds = Line.extractObjectIds(importedItems.getRoutes());
				if(!routeIds.containsAll(line.getRouteIds())){
					ReportItem detailReportItem = new DetailReportItem("Test2_Sheet7_Step1_error",Report.STATE.ERROR);
					report2_7_1.addItem(detailReportItem);
				}else {
					report2_7_1.updateStatus(Report.STATE.OK);
				}
				//Test 3.4.1
				Line nextLine = (i <lines.size()-1) ? lines.get(i+1) : line;
				String refCurrent = line.getName()+""+line.getNumber();
				String refNext = nextLine.getName()+""+nextLine.getNumber();
				if(!line.getObjectId().equals(nextLine.getObjectId())){
					if(refCurrent.equals(refNext)){
						ReportItem detailReportItem = new DetailReportItem("Test3_Sheet4_Step1_error",Report.STATE.ERROR, line.getObjectId());
						report3_4_1.addItem(detailReportItem);
					}else 
						report3_4_1.updateStatus(Report.STATE.OK);	
				}	

				//Test 2.26.1
				List<AccessPoint> accessPoints = importedItems.getAccessPoints();
				List<String> stopAreaIds = Line.extractObjectIds(importedItems.getStopAreas());
				List<String> accessLinkIds = Line.extractObjectIds(importedItems.getAccessLinks()); 
				for (AccessPoint accessPoint : accessPoints) {
					String containedInId = accessPoint.getContainedIn();
					if(stopAreaIds.contains(containedInId) || accessLinkIds.contains(containedInId)){
						report2_26_1.updateStatus(Report.STATE.OK);	
					}else{
						ReportItem detailReportItem = new DetailReportItem("Test2_Sheet26_Step1_error",Report.STATE.ERROR);
						report2_26_1.addItem(detailReportItem);
					}
				}
				//Test 2.27.1
				List<Facility> facilities = importedItems.getFacilities();
				for (Facility facility : facilities) {
					if(facility.getFacilityLocation() != null){
						String containedIn = facility.getFacilityLocation().getContainedIn();
						if(containedIn != null && stopAreaIds.contains(containedIn)){
							report2_27_1.updateStatus(Report.STATE.OK);	
						}else{
							ReportItem detailReportItem = new DetailReportItem("Test2_Sheet27_Step1_error",Report.STATE.ERROR);
							report2_27_1.addItem(detailReportItem);
						}
					}

					//Test 2.28.1
					String stopAreaId = facility.getStopAreaId(); 
					if(stopAreaId != null && stopAreaIds.contains(stopAreaId)){
						report2_28_1.updateStatus(Report.STATE.OK);	
					}else{
						ReportItem detailReportItem = new DetailReportItem("Test2_Sheet28_Step1_error",Report.STATE.ERROR);
						report2_28_1.addItem(detailReportItem);
					}

					//Test 2.28.2
					if(facility.getLine() == null){
						ReportItem detailReportItem = new DetailReportItem("Test2_Sheet28_Step2_error",Report.STATE.ERROR);
						report2_28_1.addItem(detailReportItem);
					}else
						report2_28_1.updateStatus(Report.STATE.OK);
					//Test 2.28.3
					List<String> connectionLinkIds = Line.extractObjectIds(importedItems.getConnectionLinks());
					String connectionLinkId = facility.getConnectionLinkId();
					if(connectionLinkId != null && connectionLinkIds.contains(connectionLinkId)){
						report2_28_1.updateStatus(Report.STATE.OK);	
					}else{
						ReportItem detailReportItem = new DetailReportItem("Test2_Sheet28_Step3_error",Report.STATE.ERROR);
						report2_28_1.addItem(detailReportItem);
					}
					//Test 2.28.4
					String stopPointId = facility.getStopPointId();
					if(stopPointId != null && stopPointIds.contains(stopPointId)){
						report2_28_1.updateStatus(Report.STATE.OK);	
					}else{
						ReportItem detailReportItem = new DetailReportItem("Test2_Sheet28_Step4_error",Report.STATE.ERROR);
						report2_28_1.addItem(detailReportItem);
					}
				}
			}

			/**
			 * VALIDATION STOPPOINT MIGRATED
			 */
			List<StopPoint> stopPoints = importedItems.getStopPoints();
			int size = stopPoints.size();
			for (int ii=0;ii<size;ii++) {
				StopPoint stopPoint = stopPoints.get(ii);
				//Test2.10.1
				String lineIdShortcut = stopPoint.getLineIdShortcut();
				if(lineIdShortcut != null){
					if(stopPoint.getLine() == null)
					{
						ReportItem detailReportItem = new DetailReportItem("Test2_Sheet10_Step1_error", Report.STATE.ERROR);
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
					if(stopPoint.getPtNetwork() == null){
						ReportItem detailReportItem = new DetailReportItem("Test2_Sheet11_Step1_error", Report.STATE.ERROR);
						report2_11_1.addItem(detailReportItem);	
					}else {
						report2_11_1.updateStatus(Report.STATE.OK);		
					}
				}
				//Category 3
				double y1 = (stopPoint.getLatitude()!=null) ? stopPoint.getLatitude().doubleValue():0;
				double x1 = (stopPoint.getLongitude()!=null) ? stopPoint.getLongitude().doubleValue():0;
				PrecisionModel precisionModel = new PrecisionModel(PrecisionModel.maximumPreciseValue);
				int SRID1 = (stopPoint.getLongLatType()!= null) ? stopPoint.getLongLatType().epsgCode() : 0;
				GeometryFactory factory1 = new GeometryFactory(precisionModel, SRID1);
				Coordinate coordinate = new Coordinate(x1, y1);
				Point point1 = factory1.createPoint(coordinate);
				LinearRing shell = factory1.createLinearRing(coordinates);
				LinearRing[] holes = null;
				Polygon polygon = factory1.createPolygon(shell, holes);
				for(int j=i+1;j<size;j++){
					StopPoint another = stopPoints.get(j);
					double y2 = (another.getLatitude() != null) ? another.getLatitude().doubleValue() : 0;
					double x2 = (another.getLongitude() != null) ? another.getLongitude().doubleValue() : 0;
					int SRID2 = (another.getLongLatType() != null) ? another.getLongLatType().epsgCode() : 0;
					GeometryFactory factory2 = new GeometryFactory(precisionModel, SRID2);
					Coordinate coordinate2 = new Coordinate(x2, y2);
					Point point2 = factory2.createPoint(coordinate2);
					DistanceOp distanceOp = new DistanceOp(point1, point2);
					double distance = distanceOp.distance() * CONVERTER ;

					//Test 3.1.1
					if(distance < param){
						if(!stopPoint.getName().equals(another.getName())){
							ReportItem detailReportItem = new DetailReportItem("Test3_Sheet1_Step1_warning", Report.STATE.WARNING,String.valueOf(param), stopPoint.getObjectId(), another.getObjectId(),String.valueOf(distance));
							report3_1_1.addItem(detailReportItem);	
						}else
							report3_1_1.updateStatus(Report.STATE.OK);
					}
					//Test 3.2.1
					if(distance < param2){
						if(!stopPoint.getContainedInStopAreaId().equals(another.getContainedInStopAreaId())){
							ReportItem detailReportItem = new DetailReportItem("Test3_Sheet2_Step1_warning", Report.STATE.WARNING,String.valueOf(param2), stopPoint.getObjectId(), another.getObjectId());
							report3_2_1.addItem(detailReportItem);	
						}else
							report3_2_1.updateStatus(Report.STATE.OK);
					}

					//Test 3.3.1
					if(stopPoint.getName().equals(another.getName()) && 
							(!stopPoint.getContainedInStopAreaId().equals(another.getContainedInStopAreaId()) || 
									stopPoint.getContainedInStopAreaId() == null || another.getContainedInStopAreaId() == null)){
						if(stopPoint.getAddress() != null && another.getAddress() != null){
							if(stopPoint.getAddress().equals(another.getAddress())){
								ReportItem detailReportItem = new DetailReportItem("Test3_Sheet3_Step1_warning", Report.STATE.WARNING,stopPoint.getObjectId(), another.getObjectId());
								report3_3_1.addItem(detailReportItem);	
							}else
								report3_3_1.updateStatus(Report.STATE.OK);	
						}
					}
				}	
				//Test 3.5.1 & Test 3.6.1 a
				StopPoint nextStopPoint = (i<stopPoints.size()-1) ? stopPoints.get(i+1) : stopPoint;
				if(!stopPoint.getObjectId().equals(nextStopPoint.getObjectId())){
					final int TEST =  99999;
					int refrencePJ1 = (stopPoint.getLongLatType() != null) ? stopPoint.getLongLatType().epsgCode() : TEST;
					int refrencePJ2 = (nextStopPoint.getLongLatType() != null) ? nextStopPoint.getLongLatType().epsgCode() : TEST;
					if(refrencePJ1 != TEST && refrencePJ2 != TEST){
						if(refrencePJ1 != refrencePJ2){
							ReportItem detailReportItem = new DetailReportItem("Test3_Sheet5_Step1_warning", Report.STATE.WARNING,stopPoint.getObjectId());
							report3_5_1.addItem(detailReportItem);	

							ReportItem detailReportItem6a = new DetailReportItem("Test3_Sheet6_Step1_warning_a", Report.STATE.WARNING,stopPoint.getObjectId());
							report3_6_1.addItem(detailReportItem6a);	
						}else {
							report3_5_1.updateStatus(Report.STATE.OK);
							report3_6_1.updateStatus(Report.STATE.OK);
						}	
					}else {
						ReportItem detailReportItem = new DetailReportItem("Test3_Sheet5_Step1_warning", Report.STATE.WARNING,stopPoint.getObjectId());
						report3_5_1.addItem(detailReportItem);

						ReportItem detailReportItem6a = new DetailReportItem("Test3_Sheet6_Step1_warning_a", Report.STATE.WARNING,stopPoint.getObjectId());
						report3_6_1.addItem(detailReportItem6a);	
					}	
				}
				//Test 3.6.1 b
				if(!polygon.contains(point1)){
					ReportItem detailReportItem6b = new DetailReportItem("Test3_Sheet6_Step1_error_b", Report.STATE.ERROR,stopPoint.getObjectId());
					report3_6_1.addItem(detailReportItem6b);	
				}else	
					report3_6_1.updateStatus(Report.STATE.OK);

				//Test 3.10
				int count = 0;
				List<PTLink> ptLinks4Route = new ArrayList<PTLink>();
				boolean exists = false;
				if(importedItems != null){
					List<PTLink> ptLinks = importedItems.getPtLinks();
					for (int ptLCounter=0;ptLCounter<ptLinks.size();ptLCounter++) {
						PTLink ptLink = ptLinks.get(ptLCounter);
						PTLink next = (ptLCounter == ptLinks.size()-1) ? ptLinks.get(ptLCounter) : ptLinks.get(ptLCounter+1);
						StopPoint start = ptLink.getStartOfLink();
						StopPoint end = ptLink.getEndOfLink();
						//Test 3.10.1
						if(stopPoint.getObjectId().equals(start.getObjectId()) || 
								stopPoint.getObjectId().equals(end.getObjectId())){
							count++;
							ptLinks4Route.add(ptLink);
							exists = true;
						}
						//Test 3.10.2
						if(ptLink.getStartOfLink().getContainedInStopAreaId().equals(next.getStartOfLink().getContainedInStopAreaId())){
							ReportItem detailReportItem = new DetailReportItem("Test3_Sheet10_Step2_warning", Report.STATE.WARNING);
							report3_10_2.addItem(detailReportItem);
						}else
							report3_10_2.updateStatus(Report.STATE.OK);
						
						//Test 3.10.3
						double yStart = (start != null && start.getLatitude()!=null) ? start.getLatitude().doubleValue():0;
						double xStart = (start != null && start.getLongitude()!=null) ? start.getLongitude().doubleValue():0;
						int SRIDStart = (start != null && start.getLongLatType()!= null) ? start.getLongLatType().epsgCode() : 0;
						GeometryFactory factoryStart = new GeometryFactory(precisionModel, SRIDStart);
						Point pointSart = factoryStart.createPoint(new Coordinate(xStart,yStart));

						double yEnd = (end != null && end.getLatitude()!=null) ? end.getLatitude().doubleValue():0;
						double xEnd = (end != null && end.getLongitude()!=null) ? end.getLongitude().doubleValue():0;
						int SRIDEnd = (end != null && end.getLongLatType()!= null) ? end.getLongLatType().epsgCode() : 0;
						GeometryFactory factoryEnd = new GeometryFactory(precisionModel, SRIDEnd);
						Point pointEnd = factoryEnd.createPoint(new Coordinate(xEnd,yEnd));

						DistanceOp distanceOp = new DistanceOp(pointSart, pointEnd);
						double distance = distanceOp.distance() * 6371 /180;
						if(distance < distanceMin3_10){
							ReportItem detailReportItem = new DetailReportItem("Test3_Sheet10_Step3_warning", Report.STATE.WARNING, String.valueOf(distance),String.valueOf(distanceMin3_10));
							report3_10_3.addItem(detailReportItem);	
						}else
							report3_10_3.updateStatus(Report.STATE.OK);

						//Test 2.14. b 
						List<Route> routes = importedItems.getRoutes();
						int countPtLinkInRoute = 0;
						for (Route route : routes) {
							if(!route.getPtLinkIds().contains(ptLink.getObjectId())){
								countPtLinkInRoute++;	
							}
						}
						if(countPtLinkInRoute == 0){
							ReportItem detailReportItem = new DetailReportItem("Test2_Sheet14_Step1_error", Report.STATE.ERROR,ptLink.getObjectId());
							report2_14_1.addItem(detailReportItem);	
						}else
							report2_14_1.updateStatus(Report.STATE.OK);	
					}	
				}

				//Test 2.14.1 a
				if(importedItems != null){
					if(!exists){
						ReportItem detailReportItem = new DetailReportItem("Test2_Sheet14_Step1_warning", Report.STATE.WARNING,stopPoint.getObjectId());
						report2_14_1.addItem(detailReportItem);		
					}else
						report2_14_1.updateStatus(Report.STATE.OK);	

					//Test 3.10.1 a
					if(count == 1 || count == 2){
						report3_10_1.updateStatus(Report.STATE.OK);	
					}else{
						ReportItem detailReportItem = new DetailReportItem("Test3_Sheet10_Step1_error_a", Report.STATE.ERROR,stopPoint.getObjectId());
						report3_10_1.addItem(detailReportItem);		
					}
				}
				//test 3.10.1 b
				for (int k=0; k<ptLinks4Route.size();k++) {
					PTLink ptLink = ptLinks4Route.get(k);
					PTLink next = (k == ptLinks4Route.size()-1) ? ptLinks4Route.get(k) : ptLinks4Route.get(k+1);
					if(ptLink.getRouteId() != null){
						if(!ptLink.getRouteId().equals(next.getRouteId())){
							ReportItem detailReportItem = new DetailReportItem("Test3_Sheet10_Step1_error_b", Report.STATE.ERROR);
							report3_10_1.addItem(detailReportItem);	
							break;
						}else
							report3_10_1.updateStatus(Report.STATE.OK);			
					}
				}
			}
		}

		report2_1_1.computeDetailItemCount();
		report2_1_2.computeDetailItemCount();
		report2_2_1.computeDetailItemCount();
		report2_6_1.computeDetailItemCount();
		report2_6_2.computeDetailItemCount();
		report2_7_1.computeDetailItemCount();
		report3_4_1.computeDetailItemCount();
		report2_26_1.computeDetailItemCount();
		report2_27_1.computeDetailItemCount();
		report2_28_1.computeDetailItemCount();

		sheet1.addItem(report2_1_1);
		sheet1.addItem(report2_1_2);
		sheet2.addItem(report2_2_1);
		sheet6.addItem(report2_6_1);
		sheet6.addItem(report2_6_2);
		sheet7.addItem(report2_7_1);
		sheet3_4.addItem(report3_4_1);
		sheet2_26.addItem(report2_26_1);
		sheet2_27.addItem(report2_27_1);
		sheet2_28.addItem(report2_28_1);

		category2.addItem(sheet1);
		category2.addItem(sheet2);
		category2.addItem(sheet6);
		category2.addItem(sheet7);
		category2.addItem(sheet2_26);
		category2.addItem(sheet2_27);
		category2.addItem(sheet2_28);
		category3.addItem(sheet3_4);

		/**
		 * VALIDATION STOPPOINT
		 */
		report2_10_1.computeDetailItemCount();
		report2_11_1.computeDetailItemCount();
		report2_14_1.computeDetailItemCount();

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
		sheet14.addItem(report2_14_1);

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
		category2.addItem(sheet14);

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
