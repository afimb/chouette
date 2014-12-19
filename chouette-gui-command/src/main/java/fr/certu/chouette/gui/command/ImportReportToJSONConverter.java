package fr.certu.chouette.gui.command;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.extern.log4j.Log4j;

import org.json.JSONArray;
import org.json.JSONObject;

import fr.certu.chouette.plugin.exchange.report.ExchangeReportItem;
import fr.certu.chouette.plugin.report.Report;
import fr.certu.chouette.plugin.report.ReportItem;

@Log4j
public class ImportReportToJSONConverter
{
   List<ReportItem> lineReports = new ArrayList<ReportItem>();
   List<ReportItem> saveReports = new ArrayList<ReportItem>();
   List<ReportItem> fileErrorReports = new ArrayList<ReportItem>();
   List<ReportItem> fileIgnoredReports = new ArrayList<ReportItem>();
   List<ReportItem> fileOkReports = new ArrayList<ReportItem>();
   ReportItem zipReport;
   int fileOkCount = 0;
   int fileNOKCount = 0;
   int fileIGNOREDCount = 0;
   int lineCount = 0;
   int routeCount = 0;
   int journeyPatternCount = 0;
   int vehicleJourneyCount = 0;
   int stopAreaCount = 0;
   int connectionLinkCount = 0;
   int accessPointCount = 0;
   int timeTableCount = 0;
   int companyCount = 0;
   int networkCount = 0;
   Report ireport = null;

   public ImportReportToJSONConverter(Report ireport)
   {
      this.ireport = ireport;
      for (ReportItem item : ireport.getItems())
      {
         String key = item.getMessageKey();
         // log.info(key);
         if (key.equals(GuiReportItem.KEY.NO_SAVE.name()))
            saveReports.add(item);
         if (key.equals(GuiReportItem.KEY.SAVE_OK.name()))
            saveReports.add(item);
         if (key.equals(GuiReportItem.KEY.SAVE_ERROR.name()))
            saveReports.add(item);
         if (key.equals(ExchangeReportItem.KEY.ZIP_FILE.name()))
            zipReport = item;
         if (key.equals(ExchangeReportItem.KEY.FILE.name())
               || key.equals(ExchangeReportItem.KEY.ZIP_ENTRY.name()))
         {
            fileOkCount++;
            fileOkReports.add(item);
         }
         if (key.equals(ExchangeReportItem.KEY.FILE_ERROR.name()))
         {
            fileNOKCount++;
            fileErrorReports.add(item);
         }
         if (key.equals(ExchangeReportItem.KEY.FILE_IGNORED.name()))
         {
            fileIGNOREDCount++;
            fileIgnoredReports.add(item);
         }
         if (key.equals(ExchangeReportItem.KEY.IMPORTED_LINE.name()))
         {
            lineCount++;
            lineReports.add(item);
         }
         if (key.equals(ExchangeReportItem.KEY.ROUTE_COUNT.name()))
         {
            routeCount += Integer.valueOf(item.getMessageArgs().get(0)
                  .toString());
         }
         if (key.equals(ExchangeReportItem.KEY.JOURNEY_PATTERN_COUNT.name()))
         {
            journeyPatternCount += Integer.valueOf(item.getMessageArgs().get(0)
                  .toString());
         }
         if (key.equals(ExchangeReportItem.KEY.VEHICLE_JOURNEY_COUNT.name()))
         {
            vehicleJourneyCount += Integer.valueOf(item.getMessageArgs().get(0)
                  .toString());
         }
         if (key.equals(ExchangeReportItem.KEY.STOP_AREA_COUNT.name()))
         {
            stopAreaCount += Integer.valueOf(item.getMessageArgs().get(0)
                  .toString());
         }
         if (key.equals(ExchangeReportItem.KEY.CONNECTION_LINK_COUNT.name()))
         {
            connectionLinkCount += Integer.valueOf(item.getMessageArgs().get(0)
                  .toString());
         }
         if (key.equals(ExchangeReportItem.KEY.ACCES_POINT_COUNT.name()))
         {
            accessPointCount += Integer.valueOf(item.getMessageArgs().get(0)
                  .toString());
         }
         if (key.equals(ExchangeReportItem.KEY.TIME_TABLE_COUNT.name()))
         {
            timeTableCount += Integer.valueOf(item.getMessageArgs().get(0)
                  .toString());
         }
         if (item.hasItems())
            parseItems(item, 1);
      }
   }

   public JSONObject toJSONObject()
   {
      JSONObject json = new JSONObject();
      JSONObject jsonFiles = new JSONObject();
      JSONObject jsonLines = new JSONObject();

      JSONObject jsonFileStats = new JSONObject();
      jsonFileStats.put("ok_count", fileOkCount);
      jsonFileStats.put("error_count", fileNOKCount);
      jsonFileStats.put("ignored_count", fileIGNOREDCount);

      jsonFiles.put("stats", jsonFileStats);
      JSONObject jsonFileList = new JSONObject();
      JSONArray fileErrorArray = new JSONArray();
      for (ReportItem item : fileErrorReports)
      {
         JSONArray errorList = getErrors(item.getItems());
         String name = item.getMessageArgs().get(0).toString();
         JSONObject fileInfo = new JSONObject();
         fileInfo.put("name", name);
         if (errorList != null)
         {
            fileInfo.put("errors", errorList);
         }
         fileErrorArray.put(fileInfo);
      }
      jsonFileList.put("error", fileErrorArray);
      JSONArray fileIgnoredArray = new JSONArray();
      for (ReportItem item : fileIgnoredReports)
      {
         String name = item.getMessageArgs().get(0).toString();
         JSONObject fileInfo = new JSONObject();
         fileInfo.put("name", name);
         fileIgnoredArray.put(fileInfo);
      }
      jsonFileList.put("ignored", fileIgnoredArray);
      JSONArray fileOkArray = new JSONArray();
      for (ReportItem item : fileOkReports)
      {
         String name = item.getMessageArgs().get(0).toString();
         JSONObject fileInfo = new JSONObject();
         fileInfo.put("name", name);
         fileOkArray.put(fileInfo);
      }
      jsonFileList.put("ok", fileOkArray);
      jsonFiles.put("list", jsonFileList);
      json.put("files", jsonFiles);
      Map<Object, JSONObject> jsonLineMap = new HashMap<Object, JSONObject>();
      for (ReportItem item : saveReports)
      {
         String key = item.getMessageKey();
         JSONObject lineInfo = new JSONObject();
         String name = item.getMessageArgs().get(0).toString();
         String status = null;
         JSONArray errorList = null;
         if (key.equals(GuiReportItem.KEY.NO_SAVE.name()))
            status = "not_saved";
         if (key.equals(GuiReportItem.KEY.SAVE_OK.name()))
            status = "saved";
         if (key.equals(GuiReportItem.KEY.SAVE_ERROR.name()))
         {
            status = "save_error";
            errorList = getErrors(item.getItems());
         }
         lineInfo.put("name", name);
         lineInfo.put("status", status);
         if (errorList != null)
         {
            lineInfo.put("errors", errorList);
         }
         jsonLineMap.put(name, lineInfo);
         // log.info("save info for "+name);

      }
      JSONArray lineList = new JSONArray();
      for (ReportItem item : lineReports)
      {
         String name = item.getMessageArgs().get(0).toString();
         JSONObject lineInfo = jsonLineMap.get(name);
         if (lineInfo == null)
         {
            log.error("line info without save status " + name);
            continue;
         }
         lineInfo.put("stats", getStats(item.getItems()));
         lineList.put(lineInfo);
      }

      JSONObject jsonLineStats = new JSONObject();
      jsonLineStats.put("line_count", lineCount);
      jsonLineStats.put("route_count", routeCount);
      jsonLineStats.put("journey_pattern_count", journeyPatternCount);
      jsonLineStats.put("vehicle_journey_count", vehicleJourneyCount);
      jsonLineStats.put("stop_area_count", stopAreaCount);
      jsonLineStats.put("connection_link_count", connectionLinkCount);
      jsonLineStats.put("access_point_count", accessPointCount);
      jsonLineStats.put("time_table_count", timeTableCount);
      // jsonLineStats.put("company_count",companyCount);
      // jsonLineStats.put("network_count",networkCount);

      jsonLines.put("stats", jsonLineStats);
      jsonLines.put("list", lineList);
      json.put("lines", jsonLines);
      return json;
   }

   private JSONObject getStats(List<ReportItem> items)
   {
      JSONObject stats = new JSONObject();
      if (items == null || items.isEmpty())
         return stats;
      for (ReportItem item : items)
      {
         if (!item.getMessageKey().contains("COUNT"))
            continue;
         String key = item.getMessageKey().toLowerCase();
         int value = Integer.parseInt(item.getMessageArgs().get(0).toString());
         stats.put(key, value);
      }
      return stats;
   }

   private JSONArray getErrors(List<ReportItem> items)
   {
      if (items == null || items.isEmpty())
         return null;
      JSONArray errors = new JSONArray();
      for (ReportItem item : items)
      {
         if (item.getMessageKey().contains("COUNT"))
            continue;
         JSONObject error = new JSONObject();
         error.put("key", item.getMessageKey());
         error.put("args", toJsonMap(item.getMessageArgs()));
         errors.put(error);
      }
      return errors;
   }

   private JSONObject toJsonMap(List<Object> messageArgs)
   {
      JSONObject map = new JSONObject();
      int i = 0;
      for (Object arg : messageArgs)
      {
         map.put(Integer.toString(i), arg.toString());
      }
      return map;
   }

   private void parseItems(ReportItem parent, int level)
   {
      for (ReportItem item : parent.getItems())
      {
         String key = item.getMessageKey();
         // StringBuffer indent = new StringBuffer();
         // for (int i = 0; i < level; i++) indent.append("  ");
         // log.info(indent+key);
         if (key.equals(ExchangeReportItem.KEY.ZIP_ENTRY.name()))
         {
            fileOkCount++;
            fileOkReports.add(item);
         }
         if (key.equals(ExchangeReportItem.KEY.ZIP_MISSING_ENTRY.name()) && item.getStatus().equals(Report.STATE.ERROR))
         {
            fileNOKCount++;
            fileErrorReports.add(item);
         }
         if (key.equals(ExchangeReportItem.KEY.FILE.name()))
         {
            fileOkCount++;
            fileOkReports.add(item);
         }
         if (key.equals(ExchangeReportItem.KEY.FILE_ERROR.name()))
         {
            fileNOKCount++;
            fileErrorReports.add(item);
         }
         if (key.equals(ExchangeReportItem.KEY.FILE_IGNORED.name()))
         {
            fileIGNOREDCount++;
            fileIgnoredReports.add(item);
         }
         if (key.equals(ExchangeReportItem.KEY.IMPORTED_LINE.name()))
         {
            lineCount++;
            lineReports.add(item);
         }
         if (key.equals(ExchangeReportItem.KEY.ROUTE_COUNT.name()))
         {
            routeCount += Integer.valueOf(item.getMessageArgs().get(0)
                  .toString());
         }
         if (key.equals(ExchangeReportItem.KEY.JOURNEY_PATTERN_COUNT.name()))
         {
            journeyPatternCount += Integer.valueOf(item.getMessageArgs().get(0)
                  .toString());
         }
         if (key.equals(ExchangeReportItem.KEY.VEHICLE_JOURNEY_COUNT.name()))
         {
            vehicleJourneyCount += Integer.valueOf(item.getMessageArgs().get(0)
                  .toString());
         }
         if (key.equals(ExchangeReportItem.KEY.STOP_AREA_COUNT.name()))
         {
            stopAreaCount += Integer.valueOf(item.getMessageArgs().get(0)
                  .toString());
         }
         if (key.equals(ExchangeReportItem.KEY.CONNECTION_LINK_COUNT.name()))
         {
            connectionLinkCount += Integer.valueOf(item.getMessageArgs().get(0)
                  .toString());
         }
         if (key.equals(ExchangeReportItem.KEY.ACCES_POINT_COUNT.name()))
         {
            accessPointCount += Integer.valueOf(item.getMessageArgs().get(0)
                  .toString());
         }
         if (key.equals(ExchangeReportItem.KEY.TIME_TABLE_COUNT.name()))
         {
            timeTableCount += Integer.valueOf(item.getMessageArgs().get(0)
                  .toString());
         }
         if (item.hasItems())
            parseItems(item, level + 1);
      }
   }

}
