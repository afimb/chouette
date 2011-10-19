/**
 * 
 */
package fr.certu.chouette.validation.test;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;

import org.apache.log4j.Logger;

import fr.certu.chouette.model.neptune.Facility;
import fr.certu.chouette.model.neptune.GroupOfLine;
import fr.certu.chouette.model.neptune.JourneyPattern;
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
public class ValidationLine implements IValidationPlugin<Line> {

   private static final Logger logger = Logger.getLogger(ValidationLine.class);
   @Getter
   private ValidationStepDescription description;

   public void init() {
      //TODO 
      description = new ValidationStepDescription("", ValidationClassReportItem.CLASS.TWO.ordinal());
   }

   @Override
   public List<ValidationClassReportItem> doValidate(List<Line> lines, ValidationParameters parameters) {
      return validate(lines);
   }

   private List<ValidationClassReportItem> validate(List<Line> lines) {
      logger.info("start validate "+lines.size()+" lines");
      ValidationClassReportItem category2 = new ValidationClassReportItem(ValidationClassReportItem.CLASS.TWO);
      ValidationClassReportItem category3 = new ValidationClassReportItem(ValidationClassReportItem.CLASS.THREE);

      List<ValidationClassReportItem> result = new ArrayList<ValidationClassReportItem>();

      ReportItem sheet1 = new SheetReportItem("Test2_Sheet1", 1);
      ReportItem sheet2 = new SheetReportItem("Test2_Sheet2", 2);
      ReportItem sheet6 = new SheetReportItem("Test2_Sheet6", 6);
      ReportItem sheet7 = new SheetReportItem("Test2_Sheet7", 7);
      ReportItem sheet8 = new SheetReportItem("Test2_Sheet8", 8);
      //		ReportItem sheet15 = new SheetReportItem("Test2_Sheet15", 15);
      ReportItem sheet3_4 = new SheetReportItem("Test3_Sheet4", 4);
      //		ReportItem sheet2_26 = new SheetReportItem("Test2_Sheet26", 26);
      ReportItem sheet2_27 = new SheetReportItem("Test2_Sheet27", 27);
      ReportItem sheet2_28 = new SheetReportItem("Test2_Sheet28", 28);

      SheetReportItem report2_1_1 = new SheetReportItem("Test2_Sheet1_Step1", 1);
      SheetReportItem report2_1_2 = new SheetReportItem("Test2_Sheet1_Step2", 2);
      SheetReportItem report2_2_1 = new SheetReportItem("Test2_Sheet2_Step1", 1);
      //		SheetReportItem report2_15_2 = new SheetReportItem("Test2_Sheet15_Step2", 2);
      SheetReportItem report2_6_1 = new SheetReportItem("Test2_Sheet6_Step1", 1);
      SheetReportItem report2_6_2 = new SheetReportItem("Test2_Sheet6_Step2", 2);
      SheetReportItem report2_7_1 = new SheetReportItem("Test2_Sheet7_Step1", 1);
      SheetReportItem report2_8_3 = new SheetReportItem("Test2_Sheet8_Step3", 3);
      SheetReportItem report3_4_1 = new SheetReportItem("Test3_Sheet4_Step1", 1);
      //		SheetReportItem report2_26_1 = new SheetReportItem("Test2_Sheet26_Step1", 1);
      SheetReportItem report2_27_1 = new SheetReportItem("Test2_Sheet27_Step1", 1);
      SheetReportItem report2_28_1 = new SheetReportItem("Test2_Sheet28_Step1", 1);

      for (int i = 0; i < lines.size(); i++) {
         Line line = lines.get(i);
         PTNetwork network = line.getPtNetwork();
         String ptNeworkId = line.getPtNetworkIdShortcut();
         List<String> lineEnds = line.getLineEnds();
         List<String> stopPointIds = Line.extractObjectIds(line.getStopPointList());
         List<String> routeIds = Line.extractObjectIds(line.getRoutes());
         List<Route> routes = line.getRoutes();
         ImportedItems importedItems = line.getImportedItems();
         List<GroupOfLine> groupOfLines = importedItems.getGroupOfLines();
         List<PTLink> ptLinks = importedItems.getPtLinks();
         List<StopPoint> stopPoints = importedItems.getStopPoints();
         List<JourneyPattern> journeyPatterns = importedItems.getJourneyPatterns();

         if (network == null) {
            ReportItem failedItem = new DetailReportItem("Test2_Sheet1_fatal");
            failedItem.setStatus(Report.STATE.FATAL);
            failedItem.addMessageArgs(line.getObjectId());
            report2_1_1.addItem(failedItem);
            if (ptNeworkId != null) {
               ReportItem failedItem2 = new DetailReportItem("Test2_Sheet1_Step2b_error");
               failedItem2.setStatus(Report.STATE.ERROR);
               failedItem2.addMessageArgs(ptNeworkId, line.getName() + "(" + line.getObjectId() + ")");
               report2_1_2.addItem(failedItem2);
            } else {
               report2_1_2.updateStatus(Report.STATE.UNCHECK);
            }
         } else {
            List<String> lineIds = network.getLineIds();
            if (lineIds != null && !lineIds.isEmpty()) {
               //Test 2.1.1
               if (!lineIds.contains(line.getObjectId())) {
                  ReportItem failedItem = new DetailReportItem("Test2_Sheet1_Step1_error");
                  failedItem.setStatus(Report.STATE.ERROR);
                  failedItem.addMessageArgs(network.getName() + "(" + network.getObjectId() + ")", line.getName() + "(" + line.getObjectId() + ")");
                  report2_1_1.addItem(failedItem);
               } else {
                  report2_1_1.updateStatus(Report.STATE.OK);
               }
               //Test 2.1.2
               if (ptNeworkId != null) {
                  if (!ptNeworkId.equals(network.getObjectId())) {
                     ReportItem failedItem = new DetailReportItem("Test2_Sheet1_Step2a_error");
                     failedItem.setStatus(Report.STATE.ERROR);
                     failedItem.addMessageArgs(network.getName() + "(" + network.getObjectId() + ")", line.getName() + "(" + line.getObjectId() + ")", ptNeworkId);
                     report2_1_2.addItem(failedItem);
                  } else {
                     report2_1_2.updateStatus(Report.STATE.OK);
                  }
               } else {
                  report2_1_2.updateStatus(Report.STATE.UNCHECK);
               }
            } else {
               report2_1_1.updateStatus(Report.STATE.UNCHECK);
            }                                
         }

         //Test 2.2.1
         for (GroupOfLine groupOfLine : groupOfLines) {
            List<String> lineIds = groupOfLine.getLineIds();
            if (lineIds == null || lineIds.isEmpty()) {
               report2_2_1.updateStatus(Report.STATE.UNCHECK);
            } else if (lineIds.contains(line.getObjectId())) {
               report2_2_1.updateStatus(Report.STATE.OK);
            } else {
               ReportItem detailReportItem = new DetailReportItem("Test2_Sheet2_Step1_error", Report.STATE.ERROR);
               report2_2_1.addItem(detailReportItem);
            }
         }
         //Test 2.6
         if (lineEnds == null || lineEnds.isEmpty()) {
            report2_6_1.updateStatus(Report.STATE.UNCHECK);
            report2_6_2.updateStatus(Report.STATE.UNCHECK);
         } else {
            //Test 2.6.1
            if (stopPointIds == null || stopPointIds.isEmpty()) {
               ReportItem detailReportItem = new DetailReportItem("Test2_Sheet6_Step1_error", Report.STATE.ERROR);
               report2_6_1.addItem(detailReportItem);                                
            } else {
               if (!stopPointIds.containsAll(lineEnds)) {
                  ReportItem detailReportItem = new DetailReportItem("Test2_Sheet6_Step1_error", Report.STATE.ERROR);
                  report2_6_1.addItem(detailReportItem);
               } else {
                  report2_6_1.updateStatus(Report.STATE.OK);
               }
            }

            //Test 2.6.2
            boolean isStart = false;
            boolean isEnd = false;
            boolean isOk = true;
            lineEndsRef:
               for (String lineEnd : lineEnds) {
                  if (lineEnd == null) {
                     continue;
                  }
                  lineEnd = lineEnd.trim();
                  if (lineEnd.length() == 0) {
                     continue;
                  }
                  isStart = false;
                  isEnd = false;
                  if (ptLinks == null) {
                     ptLinks = new ArrayList<PTLink>();
                  }
                  for (PTLink ptLink : ptLinks) {
                     if (ptLink == null) {
                        continue;
                     }
                     if (ptLink.getStartOfLink() == null) {
                        continue;
                     }
                     if (ptLink.getStartOfLink().getObjectId() == null) {
                        continue;
                     }
                     if (lineEnd.equals(ptLink.getStartOfLink().getObjectId().trim())) {
                        isStart = true;
                     }
                     if (ptLink.getEndOfLink() == null) {
                        continue;
                     }
                     if (ptLink.getEndOfLink().getObjectId() == null) {
                        continue;
                     }
                     if (lineEnd.equals(ptLink.getEndOfLink().getObjectId().trim())) {
                        isEnd = true;
                     }
                     if (isStart && isEnd) {
                        ReportItem detailReportItem = new DetailReportItem("Test2_Sheet6_Step2_error", Report.STATE.ERROR);
                        report2_6_2.addItem(detailReportItem);
                        isOk = false;
                        continue lineEndsRef;
                     }
                  }
                  if (!isStart && !isEnd) {
                     ReportItem detailReportItem = new DetailReportItem("Test2_Sheet6_Step2_error", Report.STATE.ERROR);
                     report2_6_2.addItem(detailReportItem);
                     isOk = false;
                  }
               }
            if (isOk) {
               report2_6_2.updateStatus(Report.STATE.OK);
            }
         }

         //Test 2.7
         //if (!line.getRouteIds().containsAll(routeIds)) {
         if (routeIds != null && routeIds.size() > 0) {
            if (!routeIds.containsAll(line.getRouteIds())) {
               ReportItem detailReportItem = new DetailReportItem("Test2_Sheet7_Step1_error", Report.STATE.ERROR);
               report2_7_1.addItem(detailReportItem);
            } else {
               report2_7_1.updateStatus(Report.STATE.OK);
            }
         }

         if (stopPoints == null) {
            report2_8_3.updateStatus(Report.STATE.UNCHECK);
         } else {
            // Test 2.8.3_a
            java.util.Set<StopPoint> journeyPatternStopPoints = new java.util.HashSet<StopPoint>();
            for (JourneyPattern journeyPattern : journeyPatterns) {
               if (journeyPattern.getStopPoints() != null)
                  journeyPatternStopPoints.addAll(journeyPattern.getStopPoints());
            }
            if (journeyPatternStopPoints.containsAll(stopPoints)) {
               report2_8_3.updateStatus(Report.STATE.OK);
            } else {
               for (StopPoint stopPoint : stopPoints) {
                  if (!journeyPatternStopPoints.contains(stopPoint)) {
                     ReportItem detailReportItem = new DetailReportItem("Test2_Sheet8_Step3_a_error", Report.STATE.ERROR, stopPoint.getName() + "(" + stopPoint.getObjectId() + ")");
                     report2_8_3.addItem(detailReportItem);
                  }
               }
            }

            // Test 2.8.3_b
            for (StopPoint stopPoint : stopPoints) {
               boolean isStart = false;
               boolean isEnd = false;
               boolean isOk = true;
               if (ptLinks == null || ptLinks.isEmpty()) {
                  ReportItem detailReportItem = new DetailReportItem("Test2_Sheet8_Step3_b_error", Report.STATE.ERROR, stopPoint.getName() + "(" + stopPoint.getObjectId() + ")");
                  report2_8_3.addItem(detailReportItem);
               } else {
                  for (PTLink ptLink : ptLinks) {
                     if (stopPoint == ptLink.getStartOfLink()) {
                        if (isStart) {
                           // stopPoint is start of more than one ptLink
                           isOk = false;
                           ReportItem detailReportItem = new DetailReportItem("Test2_Sheet8_Step3_b_error", Report.STATE.ERROR, stopPoint.getName() + "(" + stopPoint.getObjectId() + ")");
                           report2_8_3.addItem(detailReportItem);
                        }
                        isStart = true;
                     }
                     if (stopPoint == ptLink.getEndOfLink()) {
                        if (stopPoint == ptLink.getStartOfLink()) {
                           // stopPoint is start and end of the same ptLink
                           isOk = false;
                           ReportItem detailReportItem = new DetailReportItem("Test2_Sheet8_Step3_b_error", Report.STATE.ERROR, stopPoint.getName() + "(" + stopPoint.getObjectId() + ")");
                           report2_8_3.addItem(detailReportItem);
                        }
                        if (isEnd) {
                           // stopPoint is end of more than one ptLink
                           isOk = false;
                           ReportItem detailReportItem = new DetailReportItem("Test2_Sheet8_Step3_b_error", Report.STATE.ERROR, stopPoint.getName() + "(" + stopPoint.getObjectId() + ")");
                           report2_8_3.addItem(detailReportItem);
                        }
                        isEnd = true;
                     }
                  }
                  if (isOk && (isStart || isEnd)) {
                     report2_8_3.updateStatus(Report.STATE.OK);
                  } else {
                     //stopPoint doesn't appear in any ptlink
                     ReportItem detailReportItem = new DetailReportItem("Test2_Sheet8_Step3_b_error", Report.STATE.ERROR, stopPoint.getName() + "(" + stopPoint.getObjectId() + ")");
                     report2_8_3.addItem(detailReportItem);
                  }
               }
            }
         }

         /*
                        if (ptLinks == null || ptLinks.isEmpty()) {
                            ReportItem detailReportItem = new DetailReportItem("Test2_Sheet8_Step3_d_error", Report.STATE.ERROR);
                            report2_8_3.addItem(detailReportItem);
                        } else {
                            for (PTLink ptLink : ptLinks) {
                                // Test 2.8.3_d
                                if (ptLink.getStartOfLink() == null || ptLink.getEndOfLink() == null) {
                                    //every ptLink must have
                                }
                            }
                        }////////////////////////////
          */


         //Test 2.8.3_c and 2.8.3_d
         report2_8_3.updateStatus(Report.STATE.OK);
         java.util.Set<PTLink> routePtLinks = new java.util.HashSet<PTLink>();
         for (Route route : routes) 
         {
            if (route.getPtLinks() != null) 
            {
               //Test 2.8.3_c
               for (PTLink ptLink : route.getPtLinks()) 
               {
                  if (!routePtLinks.add(ptLink)) 
                  {
                     // the ptLink belongs to more than one route
                     ReportItem detailReportItem = new DetailReportItem("Test2_Sheet8_Step3_c_error", Report.STATE.ERROR, ptLink.getName() + "(" + ptLink.getObjectId() + ")");
                     report2_8_3.addItem(detailReportItem);
                  }
               }
               // test 2.8.3_d 
               List<StopPoint> pointsInRoute = route.getStopPoints();
               for (JourneyPattern journeyPattern : route.getJourneyPatterns()) 
               {
                  List<StopPoint> pointsInJourney = journeyPattern.getStopPoints();
                  int j = 0;
                  for (StopPoint point : pointsInRoute) 
                  {
                     if (point.getObjectId().equals(pointsInJourney.get(j).getObjectId()))
                     {
                        // Point on Route is found in correct order in journey
                        j++;
                        if (j == pointsInJourney.size()) break;
                     }
                  }
                  if (j < pointsInJourney.size())
                  {
                     // missing points or misordered ones
                     ReportItem detailReportItem = new DetailReportItem("Test2_Sheet8_Step3_d_error", Report.STATE.ERROR,route.getObjectId(),journeyPattern.getObjectId());
                     report2_8_3.addItem(detailReportItem);
                  }
               }
            }
            else
            {
               // test 2.8.3_d : no check possible , missing PTLinks
               ReportItem detailReportItem = new DetailReportItem("Test2_Sheet8_Step3_d_error", Report.STATE.ERROR,route.getObjectId(),"");
               report2_8_3.addItem(detailReportItem);
            }
         }
         if (!routePtLinks.containsAll(ptLinks)) {
            for (PTLink ptLink : ptLinks) {
               if (!routePtLinks.contains(ptLink)) {
                  // the ptLinks doesn't belong to any route
                  ReportItem detailReportItem = new DetailReportItem("Test2_Sheet8_Step3_c_error", Report.STATE.ERROR, ptLink.getName() + "(" + ptLink.getObjectId() + ")");
                  report2_8_3.addItem(detailReportItem);
               }
            }
         }

         //Test 3.4.1
         Line nextLine = (i < lines.size() - 1) ? lines.get(i + 1) : line;
         String refCurrent = (line.getName() + "" + line.getNumber()).trim();
         String refNext = (nextLine.getName() + "" + nextLine.getNumber()).trim();
         if (!line.getObjectId().equals(nextLine.getObjectId())) {
            if (refCurrent.equals(refNext)) {
               ReportItem detailReportItem = new DetailReportItem("Test3_Sheet4_Step1_error", Report.STATE.ERROR, line.getName() + "(" + line.getObjectId() + ")");
               report3_4_1.addItem(detailReportItem);
            } else {
               report3_4_1.updateStatus(Report.STATE.OK);
            }
         }

         if (importedItems != null) {

            //Test 2.15.2
            //				boolean propertyIsError = false;
            //				for (StopPoint stopPoint : importedItems.getStopPoints()) {
            //					boolean notFound = true;
            //					for (JourneyPattern journeyPattern : importedItems.getJourneyPatterns()) {
            //						if (journeyPattern.getStopPointIds() != null && journeyPattern.getStopPointIds().contains(stopPoint.getObjectId())) {
            //							notFound = false;
            //							propertyIsError = true;
            //							break;
            //						}
            //					}
            //					if (notFound) {
            //						ReportItem detailReportItem = new DetailReportItem("Test2_Sheet15_Step2_error", Report.STATE.ERROR);
            //						report2_15_2.addItem(detailReportItem);
            //					}
            //				}
            //				if (!propertyIsError) {
            //					report2_15_2.updateStatus(Report.STATE.OK);                    
            //				}

            List<String> stopAreaIds = Line.extractObjectIds(importedItems.getStopAreas());
            //Test 2.26.1
            //				List<AccessPoint> accessPoints = importedItems.getAccessPoints();
            //				
            //				List<String> accessLinkIds = Line.extractObjectIds(importedItems.getAccessLinks());
            //				for (AccessPoint accessPoint : accessPoints) {
            //					String containedInId = accessPoint.getContainedIn();
            //					if (stopAreaIds.contains(containedInId) || accessLinkIds.contains(containedInId)) {
            //						report2_26_1.updateStatus(Report.STATE.OK);
            //					} else {
            //						ReportItem detailReportItem = new DetailReportItem("Test2_Sheet26_Step1_error", Report.STATE.ERROR);
            //						report2_26_1.addItem(detailReportItem);
            //					}
            //				}
            //Test 2.27.1
            List<Facility> facilities = importedItems.getFacilities();
            for (Facility facility : facilities) {
               if (facility.getFacilityLocation() != null) {
                  String containedIn = facility.getFacilityLocation().getContainedIn();
                  if (containedIn != null && stopAreaIds.contains(containedIn)) {
                     report2_27_1.updateStatus(Report.STATE.OK);
                  } else {
                     ReportItem detailReportItem = new DetailReportItem("Test2_Sheet27_Step1_error", Report.STATE.ERROR);
                     report2_27_1.addItem(detailReportItem);
                  }
               }

               //Test 2.28.1
               String stopAreaId = facility.getStopAreaId();
               if (stopAreaId != null) {
                  if (stopAreaIds.contains(stopAreaId)) {
                     report2_28_1.updateStatus(Report.STATE.OK);
                  } else {
                     ReportItem detailReportItem = new DetailReportItem("Test2_Sheet28_Step1_error", Report.STATE.ERROR);
                     report2_28_1.addItem(detailReportItem);
                  }
               }

               //Test 2.28.2
               if (facility.getLineId() != null) {
                  if (facility.getLine() == null) {
                     ReportItem detailReportItem = new DetailReportItem("Test2_Sheet28_Step2_error", Report.STATE.ERROR);
                     report2_28_1.addItem(detailReportItem);
                  } else {
                     report2_28_1.updateStatus(Report.STATE.OK);
                  }
               }

               //Test 2.28.3
               List<String> connectionLinkIds = Line.extractObjectIds(importedItems.getConnectionLinks());
               String connectionLinkId = facility.getConnectionLinkId();
               if (connectionLinkId != null) {
                  if (connectionLinkIds.contains(connectionLinkId)) {
                     report2_28_1.updateStatus(Report.STATE.OK);
                  } else {
                     ReportItem detailReportItem = new DetailReportItem("Test2_Sheet28_Step3_error", Report.STATE.ERROR);
                     report2_28_1.addItem(detailReportItem);
                  }
               }

               //Test 2.28.4
               String stopPointId = facility.getStopPointId();
               if (stopPointId != null) {
                  if (stopPointIds.contains(stopPointId)) {
                     report2_28_1.updateStatus(Report.STATE.OK);
                  } else {
                     ReportItem detailReportItem = new DetailReportItem("Test2_Sheet28_Step4_error", Report.STATE.ERROR);
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
      report2_8_3.computeDetailItemCount();
      //		report2_15_2.computeDetailItemCount();
      report3_4_1.computeDetailItemCount();
      //		report2_26_1.computeDetailItemCount();
      report2_27_1.computeDetailItemCount();
      report2_28_1.computeDetailItemCount();

      sheet1.addItem(report2_1_1);
      sheet1.addItem(report2_1_2);
      sheet2.addItem(report2_2_1);
      sheet6.addItem(report2_6_1);
      sheet6.addItem(report2_6_2);
      sheet7.addItem(report2_7_1);
      sheet8.addItem(report2_8_3);
      //		sheet15.addItem(report2_15_2);
      sheet3_4.addItem(report3_4_1);
      //		sheet2_26.addItem(report2_26_1);
      sheet2_27.addItem(report2_27_1);
      sheet2_28.addItem(report2_28_1);

      category2.addItem(sheet1);
      category2.addItem(sheet2);
      category2.addItem(sheet6);
      category2.addItem(sheet7);
      category2.addItem(sheet8);
      //		category2.addItem(sheet15);
      //		category2.addItem(sheet2_26);
      category2.addItem(sheet2_27);
      category2.addItem(sheet2_28);
      category3.addItem(sheet3_4);

      result.add(category2);
      result.add(category3);
      logger.info("line validation terminated");
      return result;
   }
}
