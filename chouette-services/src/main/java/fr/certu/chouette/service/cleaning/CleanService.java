package fr.certu.chouette.service.cleaning;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;

import lombok.Setter;

import org.apache.log4j.Logger;

import fr.certu.chouette.common.ChouetteException;
import fr.certu.chouette.filter.Filter;
import fr.certu.chouette.manager.INeptuneManager;
import fr.certu.chouette.model.neptune.ConnectionLink;
import fr.certu.chouette.model.neptune.JourneyPattern;
import fr.certu.chouette.model.neptune.Line;
import fr.certu.chouette.model.neptune.Period;
import fr.certu.chouette.model.neptune.Route;
import fr.certu.chouette.model.neptune.StopArea;
import fr.certu.chouette.model.neptune.Timetable;
import fr.certu.chouette.model.neptune.VehicleJourney;
import fr.certu.chouette.plugin.report.Report;

public class CleanService implements ICleanService
{
   private static final SimpleDateFormat   sdf     = new SimpleDateFormat("yyyy-MM-dd");
   private static final SimpleDateFormat   sdftime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
   private static final Logger             logger  = Logger.getLogger(CleanService.class);

   @Setter
   private INeptuneManager<StopArea>       stopAreaManager;
   @Setter
   private INeptuneManager<Line>           lineManager;
   @Setter
   private INeptuneManager<VehicleJourney> vehicleJourneyManager;
   @Setter
   private INeptuneManager<Route>          routeManager;
   @Setter
   private INeptuneManager<JourneyPattern> journeyPatternManager;
   @Setter
   private INeptuneManager<ConnectionLink> connectionLinkManager;
   @Setter
   private INeptuneManager<Timetable>      timetableManager;

   /* (non-Javadoc)
    * @see fr.certu.chouette.service.cleaning.ICleanService#purgeAllItems(java.sql.Date, boolean)
    */
   @Override
   public Report purgeAllItems(Date boundaryDate, boolean before)
   {
      CleanReport purgeReport = new CleanReport();
      purgeReport.setStatus(CleanReport.STATE.OK);
      purgeReport.addItem(new CleanReportItem("Header", CleanReport.STATE.OK, sdftime.format(Calendar.getInstance()
            .getTime()), sdf.format(boundaryDate)));
      logger.info("purge asked"+ (before?" before ":" after ")+sdf.format(boundaryDate) );
      try
      {
         // count vehicleJourney before purge
         long numberOfVehicleJourneyBeforePurge = vehicleJourneyManager.count(null, null);
         // count physical StopPoint before purge
         Filter physicalStopFilter = StopArea.physicalStopsFilter;
         long numberOfPhysicalStopPointBeforePurge = stopAreaManager.count(null, physicalStopFilter);
         // count commercial StopPoint before purge
         Filter commercialStopFilter = StopArea.commercialStopPointFilter;
         long numberOfCommercialStopPointBeforePurge = stopAreaManager.count(null, commercialStopFilter);
         // count ConnectionLink before purge
         long numberOfConnectionLinkBeforePurge = connectionLinkManager.count(null, null);

         // get timetables
         List<Timetable> timetables = timetableManager.getAll(null);
         List<Timetable> timetablesToUpdate = new ArrayList<Timetable>();
         List<Timetable> timetablesToDelete = new ArrayList<Timetable>();
         long numberOfDates = 0;
         long numberOfPeriodes = 0;
         long numberOfPeriodesUpdates = 0;
         for (Timetable timetable : timetables)
         {
            boolean update = false;
            List<Date> dates = timetable.getCalendarDays();
            for (Iterator<Date> iterator = dates.iterator(); iterator.hasNext();)
            {
               Date date = iterator.next();
               if (date == null)
               {
                  logger.error("null date for "+timetable.getObjectId());
                  numberOfDates++;
                  iterator.remove();
                  update = true;
               }
               else if (checkDate(date, boundaryDate, before))
               {
                  numberOfDates++;
                  iterator.remove();
                  update = true;
               }
            }
            List<Period> periods = timetable.getPeriods();
            for (Iterator<Period> iterator = periods.iterator(); iterator.hasNext();)
            {
               Period period = iterator.next();
               if (checkPeriod(period, boundaryDate, before))
               {
                  numberOfPeriodes++;
                  iterator.remove();
                  update = true;
               }
               else if (shortenPeriod(period, boundaryDate, before))
               {
                  numberOfPeriodesUpdates++;
                  update = true;
               }
            }
            if (dates.isEmpty() && periods.isEmpty())
            {
               timetablesToDelete.add(timetable);
            }
            else if (update)
            {
               timetablesToUpdate.add(timetable);
            }
         }

         // deleting empty timetables
         timetableManager.removeAll(null, timetablesToDelete, false);
         long numberOfDeletedTimetables = timetablesToDelete.size();

         // save updated timetables
         timetableManager.saveOrUpdateAll(null, timetablesToUpdate);
         long numberOfUpdatedTimetables = timetablesToUpdate.size();

         // delete vehicleJourney which aren't referenced in
         // timetableVehicleJourney table
         long numberOfVehicleJourneys = vehicleJourneyManager.purge(null);

         // delete JourneyPatterns which aren't referenced in vehicleJourney
         // table
         long numberOfJourneyPatternss = journeyPatternManager.purge(null);

         // delete routes which aren't referenced in vehicleJourney table
         long numberOfRoutes = routeManager.purge(null);

         // delete line which aren't referenced in route table
         long numberOfLines = lineManager.purge(null);

         // delete stopareas which aren't referenced by a child
         long numberOfStopArea = stopAreaManager.purge(null);

         // count vehicleJourney after purge
         long numberOfVehicleJourneyAfterPurge = vehicleJourneyManager.count(null, null);
         // count physical StopPoint after purge
         long numberOfPhysicalStopPointAfterPurge = stopAreaManager.count(null, physicalStopFilter);
         // count commercial StopPoint after purge
         long numberOfCommercialStopPointAfterPurge = stopAreaManager.count(null, commercialStopFilter);
         // count ConnectionLink after purge
         long numberOfConnectionLinkAfterPurge = connectionLinkManager.count(null, null);

         // prepare report
         purgeReport.addItem(new CleanReportItem("UpdatedTimetables", Long.toString(numberOfUpdatedTimetables)));
         purgeReport.addItem(new CleanReportItem("DeletedTimetables", Long.toString(numberOfDeletedTimetables)));
         purgeReport.addItem(new CleanReportItem("DeletedVehicleJourneys", Long.toString(numberOfVehicleJourneys)));
         purgeReport.addItem(new CleanReportItem("DeletedJourneyPatterns", Long.toString(numberOfJourneyPatternss)));
         purgeReport.addItem(new CleanReportItem("DeletedRoutes", Long.toString(numberOfRoutes)));
         purgeReport.addItem(new CleanReportItem("DeletedLines", Long.toString(numberOfLines)));
         purgeReport.addItem(new CleanReportItem("DeletedStopAreas", Long.toString(numberOfStopArea)));
         purgeReport.addItem(new CleanReportItem("VehicleJourneyCount", Long
               .toString(numberOfVehicleJourneyBeforePurge), Long.toString(numberOfVehicleJourneyAfterPurge)));
         purgeReport.addItem(new CleanReportItem("PhysicalStopCount", Long
               .toString(numberOfPhysicalStopPointBeforePurge), Long.toString(numberOfPhysicalStopPointAfterPurge)));
         purgeReport
         .addItem(new CleanReportItem("CommercialStopCount", Long
               .toString(numberOfCommercialStopPointBeforePurge), Long
               .toString(numberOfCommercialStopPointAfterPurge)));
         purgeReport.addItem(new CleanReportItem("ConnectionLinkCount", Long
               .toString(numberOfConnectionLinkBeforePurge), Long.toString(numberOfConnectionLinkAfterPurge)));

      }
      catch (ChouetteException e)
      {
         logger.error("purge failed : " + e.getMessage(), e);
         purgeReport.addItem(new CleanReportItem("Exception", CleanReport.STATE.ERROR, e.getLocalizedMessage()));
      }

      return purgeReport;
   }

   /**
    * check period if partially out of bounds and reduce it to bounds
    * 
    * @param period
    * @param boundaryDate
    * @param before
    * @return true if period has been modified
    */
   private boolean shortenPeriod(Period period, Date boundaryDate, boolean before)
   {
      boolean ret = false;
      if (before && period.getStartDate().before(boundaryDate))
      {
         ret = true;
         period.setStartDate(boundaryDate);
      }
      if (!before && period.getEndDate().after(boundaryDate))
      {
         ret = true;
         period.setEndDate(boundaryDate);
      }
      return ret;
   }

   /**
    * check if period is totally out of bounds
    * 
    * @param period
    * @param boundaryDate
    * @param before
    * @return
    */
   private boolean checkPeriod(Period period, Date boundaryDate, boolean before)
   {
      if (before)
      {
         return period.getEndDate().before(boundaryDate);
      }
      return period.getStartDate().after(boundaryDate);
   }

   /**
    * check if date is out of bounds
    * 
    * @param date
    * @param boundaryDate
    * @param before
    * @return
    */
   private boolean checkDate(Date date, Date boundaryDate, boolean before)
   {
      if (before)
      {
         return date.before(boundaryDate);
      }
      return date.after(boundaryDate);
   }

}
