package fr.certu.chouette.exchange.gtfs.importer;

import java.io.FileNotFoundException;
import java.util.Iterator;

import lombok.extern.log4j.Log4j;

import fr.certu.chouette.exchange.gtfs.refactor.importer.GtfsException;
import fr.certu.chouette.exchange.gtfs.refactor.importer.GtfsImporter;
import fr.certu.chouette.exchange.gtfs.refactor.importer.Index;
import fr.certu.chouette.exchange.gtfs.refactor.model.GtfsAgency;
import fr.certu.chouette.exchange.gtfs.refactor.model.GtfsCalendar;
import fr.certu.chouette.exchange.gtfs.refactor.model.GtfsCalendarDate;
import fr.certu.chouette.exchange.gtfs.refactor.model.GtfsFrequency;
import fr.certu.chouette.exchange.gtfs.refactor.model.GtfsObject;
import fr.certu.chouette.exchange.gtfs.refactor.model.GtfsRoute;
import fr.certu.chouette.exchange.gtfs.refactor.model.GtfsStop;
import fr.certu.chouette.exchange.gtfs.refactor.model.GtfsStopTime;
import fr.certu.chouette.exchange.gtfs.refactor.model.GtfsTransfer;
import fr.certu.chouette.exchange.gtfs.refactor.model.GtfsTrip;
import fr.certu.chouette.plugin.exchange.report.ExchangeReportItem;
import fr.certu.chouette.plugin.report.Report;
import fr.certu.chouette.plugin.report.ReportItem;

@Log4j
public class GtfsChecker
{
   public boolean check(GtfsImporter importer, Report report, boolean full)
   {
      // check mandatory files and data syntax

      // agency.txt
      boolean agencyOk = true;
      if (full)
      {
         String fileName = "agency.txt";
         try
         {
            Index<GtfsAgency> index = importer.getAgencyById();
            ExchangeReportItem reportItem = new ExchangeReportItem(ExchangeReportItem.KEY.ZIP_ERROR, Report.STATE.OK, fileName);
            agencyOk = checkData(index, fileName, reportItem);
            if (!agencyOk)
            {
               report.addItem(reportItem);
            }
         }
         catch (GtfsException e)
         {
            agencyOk = manageSystemException(fileName, e, true, report);
         }
      }
      // stops.txt
      boolean stopOk = true;
      {
         String fileName = "stops.txt";
         try
         {
            Index<GtfsStop> index = importer.getStopById();
            ExchangeReportItem reportItem = new ExchangeReportItem(ExchangeReportItem.KEY.ZIP_ERROR, Report.STATE.OK, fileName);
            stopOk = checkData(index, fileName, reportItem);
            if (!stopOk)
            {
               report.addItem(reportItem);
            }
         }
         catch (GtfsException e)
         {
            stopOk = manageSystemException(fileName, e, true, report);
         }
      }
      // routes.txt
      boolean routeOk = true;
      if (full)
      {
         String fileName = "routes.txt";
         try
         {
            Index<GtfsRoute> index = importer.getRouteById();
            ExchangeReportItem reportItem = new ExchangeReportItem(ExchangeReportItem.KEY.ZIP_ERROR, Report.STATE.OK, fileName);
            routeOk = checkData(index, fileName, reportItem);
            if (!routeOk)
            {
               report.addItem(reportItem);
            }
         }
         catch (GtfsException e)
         {
            routeOk = manageSystemException(fileName, e, true, report);
         }
      }
      // trips.txt
      boolean tripOk = true;
      if (full)
      {
         String fileName = "trips.txt";
         try
         {
            Index<GtfsTrip> index = importer.getTripById();
            ExchangeReportItem reportItem = new ExchangeReportItem(ExchangeReportItem.KEY.ZIP_ERROR, Report.STATE.OK, fileName);
            tripOk = checkData(index, fileName, reportItem);
            if (!tripOk)
            {
               report.addItem(reportItem);
            }
         }
         catch (GtfsException e)
         {
            tripOk = manageSystemException(fileName, e, true, report);
         }
      }
      // stop_times.txt
      boolean stopTimeOk = true;
      if (full)
      {
         String fileName = "stop_times.txt";
         try
         {
            Index<GtfsStopTime> index = importer.getStopTimeByTrip();
            ExchangeReportItem reportItem = new ExchangeReportItem(ExchangeReportItem.KEY.ZIP_ERROR, Report.STATE.OK, fileName);
            stopTimeOk = checkData(index, fileName, reportItem);
            if (!stopTimeOk)
            {
               report.addItem(reportItem);
            }
         }
         catch (GtfsException e)
         {
            stopTimeOk = manageSystemException(fileName, e, true, report);
         }
      }
      // calendar.txt
      boolean calendarOk = true;
      if (full)
      {
         String fileName = "calendar.txt";
         try
         {
            Index<GtfsCalendar> index = importer.getCalendarByService();
            ExchangeReportItem reportItem = new ExchangeReportItem(ExchangeReportItem.KEY.ZIP_ERROR, Report.STATE.OK, fileName);
            calendarOk = checkData(index, fileName, reportItem);
            if (!calendarOk)
            {
               report.addItem(reportItem);
            }
         }
         catch (GtfsException e)
         {
            calendarOk = manageSystemException(fileName, e, false, report);
         }
      }
      // calendar_dates.txt
      boolean calendarDateOk = true;
      if (full)
      {
         String fileName = "calendar_dates.txt";
         try
         {
            Index<GtfsCalendarDate> index = importer.getCalendarDateByService();
            ExchangeReportItem reportItem = new ExchangeReportItem(ExchangeReportItem.KEY.ZIP_ERROR, Report.STATE.OK, fileName);
            calendarDateOk = checkData(index, fileName, reportItem);
            if (!calendarDateOk)
            {
               report.addItem(reportItem);
            }
         }
         catch (GtfsException e)
         {
            calendarDateOk = manageSystemException(fileName, e, false, report);
         }
      }
      // transfers.txt
      boolean transferOk = true;
      {
         String fileName = "transfers.txt";
         try
         {
            Index<GtfsTransfer> index = importer.getTransferByFromStop();
            ExchangeReportItem reportItem = new ExchangeReportItem(ExchangeReportItem.KEY.ZIP_ERROR, Report.STATE.OK, fileName);
            transferOk = checkData(index, fileName, reportItem);
            if (!transferOk)
            {
               report.addItem(reportItem);
            }
         }
         catch (GtfsException e)
         {
            transferOk = manageSystemException(fileName, e, false, report);
         }
      }
      // frequencies.txt
      boolean frequencyOk = true;
      if (full)
      {
         String fileName = "frequencies.txt";
         try
         {
            Index<GtfsFrequency> index = importer.getFrequencyByTrip();
            ExchangeReportItem reportItem = new ExchangeReportItem(ExchangeReportItem.KEY.ZIP_ERROR, Report.STATE.OK, fileName);
            frequencyOk = checkData(index, fileName, reportItem);
            if (!frequencyOk)
            {
               report.addItem(reportItem);
            }
         }
         catch (GtfsException e)
         {
            frequencyOk = manageSystemException(fileName, e, false, report);
         }
      }

      if (full)
      {
         return agencyOk && tripOk && routeOk && stopOk && stopTimeOk && transferOk && frequencyOk && (calendarOk || calendarDateOk);
      }
      else
      {
         return stopOk && transferOk;
      }
   }

   /**
    * @param fileName
    * @param e
    */
   private boolean manageSystemException(String fileName, GtfsException e, boolean mandatory, Report report)
   {

      if (e.getError().equals(GtfsException.ERROR.SYSTEM))
      {
         if (e.getCause() instanceof FileNotFoundException)
         {
            ReportItem item = new ExchangeReportItem(ExchangeReportItem.KEY.ZIP_MISSING_ENTRY, mandatory ? Report.STATE.ERROR : Report.STATE.WARNING, fileName,
                  "");
            report.addItem(item);
            report.updateStatus(item.getStatus());
            if (!mandatory)
               return true;
            log.error("problem on " + fileName);
            log.error("missing mandatory file : " + fileName);
         }
         else
         {
            ReportItem item = new ExchangeReportItem(ExchangeReportItem.KEY.ZIP_ERROR, Report.STATE.ERROR, fileName, "", e.getCause().getLocalizedMessage());
            report.addItem(item);
            report.updateStatus(Report.STATE.ERROR);
            log.error("zip import failed (cannot read " + fileName + ")" + e.getCause().getLocalizedMessage(),e);
            log.error("non gtfs file : " + fileName);
         }
      }
      else if (e.getError().equals(GtfsException.ERROR.MISSING_FIELD))
      {
         ExchangeReportItem item = new ExchangeReportItem(ExchangeReportItem.KEY.ZIP_ERROR, Report.STATE.ERROR, fileName);
         report.addItem(item);
         report.updateStatus(Report.STATE.ERROR);
         ExchangeReportItem detail = new ExchangeReportItem(ExchangeReportItem.KEY.MANDATORY_DATA, Report.STATE.ERROR, e.getId(), e.getField());
         item.addItem(detail);
         log.error("zip import failed (cannot index " + fileName + ")" + e.toString());
         log.error("missing index key in : " + fileName);

      }
      else
      {
         ReportItem item = new ExchangeReportItem(ExchangeReportItem.KEY.ZIP_ERROR, Report.STATE.ERROR, fileName, "", e.toString());
         report.addItem(item);
         report.updateStatus(Report.STATE.ERROR);
         log.error("zip import failed (cannot read " + fileName + ")" + e.toString());
         log.error("unexpected error found on " + fileName, e);
      }
      return false;
   }

   private boolean checkData(Index<? extends GtfsObject> index, String fileName, Report report)
   {
      boolean ok = true;
      int errorCount = 0;
      for (Iterator<? extends GtfsObject> iterator = index.iterator(); iterator.hasNext();)
      {
         try
         {
            iterator.next();
         }
         catch (GtfsException e)
         {
            log.error("problem on " + fileName+" : "+e.toString());
            ExchangeReportItem item = null;
            switch (e.getError())
            {
            case DUPLICATE_FIELD: // DUPLICATE_ID
               item = new ExchangeReportItem(ExchangeReportItem.KEY.DUPLICATE_ID, Report.STATE.ERROR, e.getId(), e.getField(), e.getValue());
               report.addItem(item);
               break;
            case INVALID_FORMAT: // INVALID_FORMAT
               item = new ExchangeReportItem(ExchangeReportItem.KEY.INVALID_FORMAT, Report.STATE.ERROR, e.getId(), e.getField(), e.getValue());
               report.addItem(item);
               break;
            case MISSING_FIELD: // MANDATORY_DATA
               item = new ExchangeReportItem(ExchangeReportItem.KEY.MANDATORY_DATA, Report.STATE.ERROR, e.getId(), e.getField());
               report.addItem(item);
               break;
            case MISSING_FOREIGN_KEY: // BAD_REFERENCE
               item = new ExchangeReportItem(ExchangeReportItem.KEY.BAD_REFERENCE, Report.STATE.ERROR, e.getId(), e.getField(), e.getValue());
               report.addItem(item);
               break;
            case SYSTEM: // ???
               log.error("unexpected error : " + e.toString(), e);
            }
            ok = false;
            errorCount++;

            if (errorCount > 10)
               break;
         }
      }
      return ok;

   }

}
