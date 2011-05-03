/**
 * 
 */
package fr.certu.chouette.validation.test;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import fr.certu.chouette.model.neptune.AccessPoint;
import fr.certu.chouette.model.neptune.Facility;
import fr.certu.chouette.model.neptune.GroupOfLine;
import fr.certu.chouette.model.neptune.Line;
import fr.certu.chouette.model.neptune.PTLink;
import fr.certu.chouette.model.neptune.PTNetwork;
import fr.certu.chouette.model.neptune.Route;
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
public class ValidationLine implements IValidationPlugin<Line>{
	@Getter private ValidationStepDescription description;
	public void init(){
		//TODO 
		description = new ValidationStepDescription("",ValidationClassReportItem.CLASS.TWO.ordinal());
	}

	@Override
	public List<ValidationClassReportItem> doValidate(List<Line> lines,ValidationParameters parameters) {
		return validate(lines);
	}

	private  List<ValidationClassReportItem> validate(List<Line> lines){
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
						failedItem.addMessageArgs(network.getName()+"("+network.getObjectId()+")",line.getName()+"("+line.getObjectId()+")");
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
						failedItem.addMessageArgs(network.getName()+"("+network.getObjectId()+")",line.getName()+"("+line.getObjectId()+")");
						report2_1_2.addItem(failedItem);
					}else {
						report2_1_2.updateStatus(Report.STATE.OK);
					}
				}
			}	

			//Test 2.6.1
			List<String> lineEnds = line.getLineEnds();
			List<String> stopPointIds = Line.extractObjectIds(line.getStopPointList());
			if(lineEnds != null){
				if(!stopPointIds.containsAll(lineEnds)){
					ReportItem detailReportItem = new DetailReportItem("Test2_Sheet6_Step1_error", Report.STATE.ERROR);
					report2_6_1.addItem(detailReportItem);
				}else {
					report2_6_1.updateStatus(Report.STATE.OK);	
				}
			}

			//Test 2.7
			List<String> routeIds = Line.extractObjectIds(line.getRoutes());
			if(!line.getRouteIds().containsAll(routeIds)){
				ReportItem detailReportItem = new DetailReportItem("Test2_Sheet7_Step1_error",Report.STATE.ERROR);
				report2_7_1.addItem(detailReportItem);
			}else {
				report2_7_1.updateStatus(Report.STATE.OK);
			}
			//Test 3.4.1
			Line nextLine = (i <lines.size()-1) ? lines.get(i+1) : line;
			String refCurrent = (line.getName()+""+line.getNumber()).trim();
			String refNext = (nextLine.getName()+""+nextLine.getNumber()).trim();
			if(!line.getObjectId().equals(nextLine.getObjectId())){
				if(refCurrent.equals(refNext)){
					ReportItem detailReportItem = new DetailReportItem("Test3_Sheet4_Step1_error",Report.STATE.ERROR,line.getName()+"("+line.getObjectId()+")");
					report3_4_1.addItem(detailReportItem);
				}else 
					report3_4_1.updateStatus(Report.STATE.OK);	
			}	
			ImportedItems importedItems =line.getImportedItems();
			if(importedItems != null){

				//Test 2.2.1
				for (GroupOfLine groupOfLine : importedItems.getGroupOfLines()) {
					if(groupOfLine.getObjectId().equals(line.getGroupOfLine().getObjectId())){
						if(!groupOfLine.getLineIds().contains(line.getObjectId())){
							ReportItem detailReportItem = new DetailReportItem("Test2_Sheet2_Step1_error",Report.STATE.ERROR);
							report2_2_1.addItem(detailReportItem);
						}else 
							report2_2_1.updateStatus(Report.STATE.OK);	
					}
				}	
				if(lineEnds != null){
					//Test 2.6.2
					int count = 0;
					//List<String> lineEndList  = Line.extractObjectIds(line.getLineEndList());
					List<Route> routes = line.getRoutes();
					here: for (String lineEnd : lineEnds) {
						if(routes != null){
							for (Route route : routes) {
								List<PTLink> ptLinks = route.getPtLinks();
								if(ptLinks != null){
									for (PTLink ptLink : ptLinks) {
										if(lineEnd.equals(ptLink.getStartOfLinkId()) || lineEnd.equals(ptLink.getEndOfLinkId())){
											count++;
											continue here;
										}		
									}
								}
							}
						}	
						if(count != 1){
							ReportItem detailReportItem = new DetailReportItem("Test2_Sheet6_Step2_error",Report.STATE.ERROR);
							report2_6_2.addItem(detailReportItem);
						}else 
							report2_6_2.updateStatus(Report.STATE.OK);			
					}
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
					if(stopAreaId != null){
						if(stopAreaIds.contains(stopAreaId)){
							report2_28_1.updateStatus(Report.STATE.OK);	
						}else{
							ReportItem detailReportItem = new DetailReportItem("Test2_Sheet28_Step1_error",Report.STATE.ERROR);
							report2_28_1.addItem(detailReportItem);
						}
					}

					//Test 2.28.2
					if(facility.getLineId() != null){
						if(facility.getLine() == null){
							ReportItem detailReportItem = new DetailReportItem("Test2_Sheet28_Step2_error",Report.STATE.ERROR);
							report2_28_1.addItem(detailReportItem);
						}else
							report2_28_1.updateStatus(Report.STATE.OK);
					}

					//Test 2.28.3
					List<String> connectionLinkIds = Line.extractObjectIds(importedItems.getConnectionLinks());
					String connectionLinkId = facility.getConnectionLinkId();
					if(connectionLinkId != null){
						if(connectionLinkIds.contains(connectionLinkId)){
							report2_28_1.updateStatus(Report.STATE.OK);	
						}else{
							ReportItem detailReportItem = new DetailReportItem("Test2_Sheet28_Step3_error",Report.STATE.ERROR);
							report2_28_1.addItem(detailReportItem);
						}	
					}

					//Test 2.28.4
					String stopPointId = facility.getStopPointId();
					if(stopPointId != null){
						if(stopPointIds.contains(stopPointId)){
							report2_28_1.updateStatus(Report.STATE.OK);	
						}else{
							ReportItem detailReportItem = new DetailReportItem("Test2_Sheet28_Step4_error",Report.STATE.ERROR);
							report2_28_1.addItem(detailReportItem);
						}
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

		result.add(category2);
		result.add(category3);
		return result;
	}

}
