package fr.certu.chouette.exchange.xml.neptune.exporter;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Set;
import java.sql.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import lombok.Setter;

import org.apache.log4j.Logger;
import fr.certu.chouette.plugin.report.Report;

import chouette.schema.ChouetteArea;
import chouette.schema.ChouetteLineDescription;
import chouette.schema.ChouettePTNetwork;
import chouette.schema.ChouettePTNetworkTypeType;
import chouette.schema.ChouetteRoute;
import chouette.schema.ITL;
import fr.certu.chouette.common.ChouetteException;
import fr.certu.chouette.exchange.xml.neptune.exception.ExchangeExceptionCode;
import fr.certu.chouette.exchange.xml.neptune.exception.ExchangeRuntimeException;
import fr.certu.chouette.exchange.xml.neptune.exporter.producer.AreaCentroidProducer;
import fr.certu.chouette.exchange.xml.neptune.exporter.producer.CompanyProducer;
import fr.certu.chouette.exchange.xml.neptune.exporter.producer.ConnectionLinkProducer;
import fr.certu.chouette.exchange.xml.neptune.exporter.producer.JourneyPatternProducer;
import fr.certu.chouette.exchange.xml.neptune.exporter.producer.LineProducer;
import fr.certu.chouette.exchange.xml.neptune.exporter.producer.PTLinkProducer;
import fr.certu.chouette.exchange.xml.neptune.exporter.producer.PTNetworkProducer;
import fr.certu.chouette.exchange.xml.neptune.exporter.producer.RouteProducer;
import fr.certu.chouette.exchange.xml.neptune.exporter.producer.RoutingConstraintProducer;
import fr.certu.chouette.exchange.xml.neptune.exporter.producer.StopAreaProducer;
import fr.certu.chouette.exchange.xml.neptune.exporter.producer.StopPointProducer;
import fr.certu.chouette.exchange.xml.neptune.exporter.producer.TimetableProducer;
import fr.certu.chouette.exchange.xml.neptune.exporter.producer.VehicleJourneyProducer;
import fr.certu.chouette.exchange.xml.neptune.report.NeptuneReport;
import fr.certu.chouette.exchange.xml.neptune.report.NeptuneReportItem;
import fr.certu.chouette.model.neptune.AreaCentroid;
import fr.certu.chouette.model.neptune.Company;
import fr.certu.chouette.model.neptune.ConnectionLink;
import fr.certu.chouette.model.neptune.JourneyPattern;
import fr.certu.chouette.model.neptune.Line;
import fr.certu.chouette.model.neptune.PTLink;
import fr.certu.chouette.model.neptune.Period;
import fr.certu.chouette.model.neptune.Route;
import fr.certu.chouette.model.neptune.StopArea;
import fr.certu.chouette.model.neptune.StopPoint;
import fr.certu.chouette.model.neptune.Timetable;
import fr.certu.chouette.model.neptune.VehicleJourney;
import fr.certu.chouette.model.neptune.type.ChouetteAreaEnum;
import fr.certu.chouette.model.neptune.type.DayTypeEnum;
import fr.certu.chouette.plugin.exchange.FormatDescription;
import fr.certu.chouette.plugin.exchange.IExportPlugin;
import fr.certu.chouette.plugin.exchange.ParameterDescription;
import fr.certu.chouette.plugin.exchange.ParameterValue;
import fr.certu.chouette.plugin.exchange.SimpleParameterValue;
import fr.certu.chouette.plugin.report.Report;
import fr.certu.chouette.plugin.report.ReportHolder;

/**
 *  Export lines in Neptune XML format
 */
public class XMLNeptuneExportLinePlugin implements IExportPlugin<Line>
{

   private static final Logger       logger = Logger.getLogger(XMLNeptuneExportLinePlugin.class);

   private FormatDescription         description;
   @Setter
   private LineProducer              lineProducer;
   @Setter
   private PTNetworkProducer         networkProducer;
   @Setter
   private RouteProducer             routeProducer;
   @Setter
   private JourneyPatternProducer    journeyPatternProducer;
   @Setter
   private VehicleJourneyProducer    vehicleJourneyProducer;
   @Setter
   private StopPointProducer         stopPointProducer;
   @Setter
   private PTLinkProducer            ptLinkProducer;
   @Setter
   private CompanyProducer           companyProducer;
   @Setter
   private StopAreaProducer          stopAreaProducer;
   @Setter
   private AreaCentroidProducer      areaCentroidProducer;
   @Setter
   private ConnectionLinkProducer    connectionLinkProducer;
   @Setter
   private TimetableProducer         timetableProducer;
   @Setter
   private RoutingConstraintProducer routingConstraintProducer;

   /**
    * Export lines in Neptune XML format
    */
   public XMLNeptuneExportLinePlugin()
   {
      description = new FormatDescription(this.getClass().getName());
      description.setName("NEPTUNE");
      List<ParameterDescription> params = new ArrayList<ParameterDescription>();
      {
         ParameterDescription param = new ParameterDescription("outputFile", ParameterDescription.TYPE.FILEPATH, false,
               true);
         param.setAllowedExtensions(Arrays.asList(new String[] { "xml", "zip" }));
         params.add(param);
      }
      {
         ParameterDescription param = new ParameterDescription("startDate", ParameterDescription.TYPE.DATE, false,
               false);
         params.add(param);
      }
      {
         ParameterDescription param = new ParameterDescription("endDate", ParameterDescription.TYPE.DATE, false,
               false);
         params.add(param);
      }
      description.setParameterDescriptions(params);
   }

   /* (non-Javadoc)
    * @see fr.certu.chouette.plugin.exchange.IExchangePlugin#getDescription()
    */
   @Override
   public FormatDescription getDescription()
   {
      return description;
   }

   /* (non-Javadoc)
    * @see fr.certu.chouette.plugin.exchange.IExportPlugin#doExport(java.util.List, java.util.List, fr.certu.chouette.plugin.report.ReportHolder)
    */
   @Override
   public void doExport(List<Line> beans, List<ParameterValue> parameters, ReportHolder reportContainer)
   throws ChouetteException
   {
      NeptuneReport report = new NeptuneReport(NeptuneReport.KEY.EXPORT);
      report.setStatus(Report.STATE.OK);
      reportContainer.setReport(report);

      String fileName = null;

      if (beans == null)
      {
         throw new IllegalArgumentException("no beans to export");
      }
      Date startDate = null; 
      Date endDate = null; 

      for (ParameterValue value : parameters)
      {
         if (value instanceof SimpleParameterValue)
         {
            SimpleParameterValue svalue = (SimpleParameterValue) value;
            if (svalue.getName().equals("outputFile"))
            {
               fileName = svalue.getFilepathValue();
               if (fileName == null) 
               {
                  logger.warn("outputFile changed as FILEPATH type");
                  fileName = svalue.getFilenameValue();
               }
            }
            else if (svalue.getName().equals("startDate"))
            {
               Calendar c = svalue.getDateValue();
               if (c != null)
                  startDate = new Date(c.getTime().getTime());
            }
            else if (svalue.getName().equals("endDate"))
            {
               Calendar c = svalue.getDateValue();
               if (c != null)
                  endDate = new Date(c.getTime().getTime());
            }

         }
      }
      if (fileName == null)
      {
         throw new IllegalArgumentException("outputFile required");
      }

      String fileExtension = fileName.substring(fileName.lastIndexOf('.') + 1).toLowerCase();

      if (beans.size() > 1 && fileExtension.equals("xml"))
      {
         throw new IllegalArgumentException("cannot export multiple lines in one XML file");
      }

      NeptuneFileWriter neptuneFileWriter = new NeptuneFileWriter();
      File outputFile = new File(fileName);
      if (!outputFile.getParentFile().exists())
      {
         outputFile.getParentFile().mkdirs();
      }
      if (fileExtension.equals("xml"))
      {
         Line line = beans.get(0);
         ChouettePTNetworkTypeType rootObject = exportLine(line,startDate,endDate);
         if (rootObject != null)
         {    
            logger.info("exporting "+line.getName()+" ("+line.getObjectId()+")");
            neptuneFileWriter.write(rootObject, outputFile);
            NeptuneReportItem item = new NeptuneReportItem(NeptuneReportItem.KEY.EXPORTED_LINE,Report.STATE.OK , line.getName(), line.getObjectId());
            report.addItem(item);
         }
         else
         {
            logger.info("no vehiclejourneys for line "+line.getName()+" ("+line.getObjectId()+"): not exported");
            NeptuneReportItem item = new NeptuneReportItem(NeptuneReportItem.KEY.EMPTY_LINE,Report.STATE.ERROR , line.getName(), line.getObjectId());
            report.addItem(item);
         }
      }
      else
      {

         try
         {
            // Create the ZIP file
            ZipOutputStream out = new ZipOutputStream(new FileOutputStream(fileName));

            // Compress the files
            for (Iterator<Line> iterator = beans.iterator(); iterator.hasNext();)
            {
               Line line = iterator.next();
               iterator.remove();

               ChouettePTNetworkTypeType rootObject = exportLine(line,startDate,endDate);
               if (rootObject != null)
               {    
                  logger.info("exporting "+line.getName()+" ("+line.getObjectId()+")");

                  String name = line.getObjectId().split(":")[2];

                  ByteArrayOutputStream stream = new ByteArrayOutputStream();
                  neptuneFileWriter.write(rootObject, stream);

                  // Add ZIP entry to output stream.
                  ZipEntry entry = new ZipEntry(name + ".xml");
                  out.putNextEntry(entry);

                  out.write(stream.toByteArray());

                  // Complete the entry
                  out.closeEntry();
                  NeptuneReportItem item = new NeptuneReportItem(NeptuneReportItem.KEY.EXPORTED_LINE,Report.STATE.OK , line.getName(), line.getObjectId());
                  report.addItem(item);
               }
               else
               {
                  logger.info("no vehiclejourneys for line "+line.getName()+" ("+line.getObjectId()+"): not exported");
                  NeptuneReportItem item = new NeptuneReportItem(NeptuneReportItem.KEY.EMPTY_LINE,Report.STATE.WARNING , line.getName(), line.getObjectId());
                  report.addItem(item);
               }
               System.gc();

            }

            // Complete the ZIP file
            out.close();
         }
         catch (IOException e)
         {
            logger.error("cannot create zip file", e);
            throw new ExchangeRuntimeException(ExchangeExceptionCode.ERR_XML_WRITE, e);
         }
      }
   }

   /**
    * produce a chouetteLine from a line
    * 
    * @param line line to export
    * @param startDate optional calendar start filter
    * @param endDate optional calendar end filter
    * @return chouetteLine or null if line has no valid vehicleJourneys
    */
   private ChouettePTNetworkTypeType exportLine(Line line, Date startDate, Date endDate)
   {
      ChouettePTNetwork rootObject = new ChouettePTNetwork();

      if (line != null)
      {
         line.complete();
         if (line.getPtNetwork() != null)
         {
            rootObject.setPTNetwork(networkProducer.produce(line.getPtNetwork()));
         }

         HashSet<Company> companies = new HashSet<Company>();
         if (line.getCompany() != null)
         {
            companies.add(line.getCompany());
         }


         HashSet<JourneyPattern> journeyPatterns = new HashSet<JourneyPattern>();
         for (Route route : line.getRoutes())
         {
            if (route.getJourneyPatterns() != null)
            {
               journeyPatterns.addAll(route.getJourneyPatterns());
            }
         }

         HashSet<VehicleJourney> vehicleJourneys = new HashSet<VehicleJourney>();
         for (JourneyPattern journeyPattern : journeyPatterns)
         {
            if (journeyPattern.getVehicleJourneys() != null)
            {
               vehicleJourneys.addAll(journeyPattern.getVehicleJourneys());
            }
         }

         Set<String> validObjectIds = new HashSet<String>();
         Set<Timetable> timetables = new HashSet<Timetable>();
         Set<VehicleJourney> validVehicleJourneys = new HashSet<VehicleJourney>();
         Set<JourneyPattern> validJourneyPatterns = new HashSet<JourneyPattern>();
         Set<Route> validRoutes = new HashSet<Route>();

         for (VehicleJourney vehicleJourney : vehicleJourneys)
         {
            if (startDate == null && endDate == null)
            {
               if (vehicleJourney.getTimetables() != null)
               {
                  timetables.addAll(vehicleJourney.getTimetables());
                  validVehicleJourneys.add(vehicleJourney);
                  validJourneyPatterns.add(vehicleJourney.getJourneyPattern());
                  validRoutes.add(vehicleJourney.getRoute());
               }
            }
            else
            {
               boolean isValid = false;
               for (Timetable timetable : vehicleJourney.getTimetables())
               {
                  if (timetables.contains(timetable))
                  {
                     isValid = true;
                  }
                  else
                  {
                     Timetable validTimetable = timetable;
                     if (startDate != null) validTimetable = reduceTimetable(timetable, startDate, true);
                     if (validTimetable != null && endDate != null) validTimetable = reduceTimetable(validTimetable, endDate, false);
                     if (validTimetable != null) 
                     {
                        timetables.add(timetable);
                        isValid = true;
                     }
                  }
               }
               if (isValid)
               {
                  validVehicleJourneys.add(vehicleJourney);
                  validJourneyPatterns.add(vehicleJourney.getJourneyPattern());
                  validRoutes.add(vehicleJourney.getRoute());
               }
            }
         }

         // if line has no valid vehiclejourneys remove line ! 
         if (validVehicleJourneys.isEmpty()) return null;

         ChouetteLineDescription chouetteLineDescription = new ChouetteLineDescription();
         chouette.schema.Line castorLine = lineProducer.produce(line);
         chouetteLineDescription.setLine(castorLine);

         // insert routes, journeyPatterns and stoppoints
         HashSet<StopPoint> stopPoints = new HashSet<StopPoint>();
         for (JourneyPattern journeyPattern : validJourneyPatterns)
         {
            chouette.schema.JourneyPattern castorObj = journeyPatternProducer.produce(journeyPattern);
            validObjectIds.add(castorObj.getObjectId());
            chouetteLineDescription.addJourneyPattern(castorObj);
            if (journeyPattern.getStopPoints() != null)
            {
               stopPoints.addAll(journeyPattern.getStopPoints());
            }
         }

         HashSet<PTLink> ptLinks = new HashSet<PTLink>();
         for (Route route : validRoutes)
         {
            ChouetteRoute castorObj = routeProducer.produce(route);
            // remove unreferenced Routes
            {
               List<String> cjps = castorObj.getJourneyPatternIdAsReference();
               List<String> ids = new ArrayList<String>();
               ids.addAll(cjps);
               for (String id : ids)
               {
                  if (!validObjectIds.contains(id))
                  {
                     castorObj.removeJourneyPatternId(id);
                  }
               }
            }
            validObjectIds.add(castorObj.getObjectId());
            chouetteLineDescription.addChouetteRoute(castorObj);
            if (route.getPtLinks() != null)
            {
               ptLinks.addAll(route.getPtLinks());
            }
         }

         HashSet<String> vehicleJourneyObjectIds = new HashSet<String>();
         for (VehicleJourney vehicleJourney : validVehicleJourneys)
         {
            vehicleJourneyObjectIds.add(vehicleJourney.getObjectId());
            if (vehicleJourney.getCompany() != null)
            {
               companies.add(vehicleJourney.getCompany());
            }
            else
            {
               vehicleJourney.setCompany(line.getCompany());
            }

            chouetteLineDescription.addVehicleJourney(vehicleJourneyProducer.produce(vehicleJourney));
         }

         HashSet<StopArea> stopAreas = new HashSet<StopArea>();
         HashSet<String> stopRefs = new HashSet<String>(); // for cleaning
         // stoparea contains
         // refs
         for (StopPoint stopPoint : stopPoints)
         {
            stopRefs.add(stopPoint.getObjectId());
            chouetteLineDescription.addStopPoint(stopPointProducer.produce(stopPoint));
            stopAreas.addAll(extractStopAreaHierarchy(stopPoint.getContainedInStopArea(), line));
         }

         for (PTLink ptLink : ptLinks)
         {
            chouetteLineDescription.addPtLink(ptLinkProducer.produce(ptLink));
         }

         for (Company company : companies)
         {
            rootObject.addCompany(companyProducer.produce(company));
         }

         ChouetteArea chouetteArea = new ChouetteArea();
         HashSet<AreaCentroid> areaCentroids = new HashSet<AreaCentroid>();
         HashSet<ConnectionLink> connectionLinks = new HashSet<ConnectionLink>();
         for (StopArea stopArea : stopAreas)
         {
            stopRefs.add(stopArea.getObjectId());
         }
         for (StopArea stopArea : stopAreas)
         {
            chouette.schema.StopArea chouetteStopArea = stopAreaProducer.produce(stopArea);
            // remove external stopPoints or stopareas
            List<String> pointRefs = chouetteStopArea.getContainsAsReference();
            for (Iterator<String> iterator = pointRefs.iterator(); iterator.hasNext();)
            {
               String ref = iterator.next();
               if (!stopRefs.contains(ref))
               {
                  iterator.remove();
               }

            }
            chouetteArea.addStopArea(chouetteStopArea);
            if (stopArea.getAreaCentroid() != null)
            {
               areaCentroids.add(stopArea.getAreaCentroid());
            }
            if (stopArea.getConnectionLinks() != null)
            {
               connectionLinks.addAll(stopArea.getConnectionLinks());
            }
         }

         for (AreaCentroid areaCentroid : areaCentroids)
         {
            chouetteArea.addAreaCentroid(areaCentroidProducer.produce(areaCentroid));
         }

         rootObject.setChouetteArea(chouetteArea);

         for (ConnectionLink connectionLink : connectionLinks)
         {
            rootObject.addConnectionLink(connectionLinkProducer.produce(connectionLink));
         }

         for (Timetable timetable : timetables)
         {
            if (startDate != null || endDate != null)
            {
               SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
               String oid = timetable.getObjectId()+"_red";
               if (startDate != null) oid+="_from_"+sdf.format(startDate);
               if (endDate != null) oid+="_to_"+sdf.format(endDate);               
            }
            rootObject.addTimetable(timetableProducer.produce(timetable));
         }

         // routing Constraints 
         if (line.getRoutingConstraints() != null)
         {
            for (StopArea routingConstraint : line.getRoutingConstraints())
            {
               if (stopRefs.contains(routingConstraint.getObjectId()))
               {
                  ITL castorITL = routingConstraintProducer.produceITL(line, routingConstraint);
                  chouetteLineDescription.addITL(castorITL);
               }
               else
               {
                  // TODO ? routing constraint without stop on line
               }
            }
         }

         rootObject.setChouetteLineDescription(chouetteLineDescription);

         // cleaning a little
         rootObject.getPTNetwork().removeAllLineId();
         rootObject.getPTNetwork().addLineId(castorLine.getObjectId());

         // remove unreferenced Routes
         {
            List<String> crs = castorLine.getRouteIdAsReference();
            List<String> ids = new ArrayList<String>();
            ids.addAll(crs);
            for (String id : ids)
            {
               if (!validObjectIds.contains(id))
               {
                  castorLine.removeRouteId(id);
               }
            }
         }

         // remove unreferenced vj from timetables
         for (chouette.schema.Timetable timetable : rootObject.getTimetable())
         {
            List<String> vjs = timetable.getVehicleJourneyIdAsReference();
            List<String> ids = new ArrayList<String>();
            ids.addAll(vjs);
            for (String id : ids)
            {
               if (!vehicleJourneyObjectIds.contains(id))
               {
                  timetable.removeVehicleJourneyId(id);
               }
            }
         }



      }

      return rootObject;
   }

   /**
    * extract parent tree for physical Stop
    * 
    * @param stopArea physical stop to check
    * @param line line for routingConstraint relationship
    * @return stopareas
    */
   private List<StopArea> extractStopAreaHierarchy(StopArea stopArea, Line line)
   {
      List<StopArea> stopAreas = new ArrayList<StopArea>();
      if (stopArea != null)
      {
         if (stopArea.getAreaType().equals(ChouetteAreaEnum.ITL))
         {
            boolean validRestrictionConstraint = false;
            for (Line routingLine : stopArea.getRoutingConstraintLines())
            {
               if (line.equals(routingLine))
               {
                  validRestrictionConstraint = true;
                  break;
               }
            }
            if (!validRestrictionConstraint)
               return stopAreas;
         }
         // logger.debug("add StopArea " + stopArea.getObjectId());
         stopAreas.add(stopArea);
         if (stopArea.getParents() != null)
         {
            for (StopArea parent : stopArea.getParents())
            {
               stopAreas.addAll(extractStopAreaHierarchy(parent, line));
            }
         }
      }

      return stopAreas;
   }

   /**
    * produce a timetable reduced to a date
    * 
    * @param timetable original timetable
    * @param boundaryDate boundary date
    * @param before true to eliminate before boundary date , false otherwise
    * @return a copy reduced to date or null if reduced to nothing
    */
   private Timetable reduceTimetable(Timetable timetable, Date boundaryDate, boolean before)
   {
      Timetable reduced = new Timetable();
      reduced.setDayTypes(new ArrayList<DayTypeEnum>(timetable.getDayTypes()));
      reduced.setObjectId(timetable.getObjectId());
      reduced.setObjectVersion(timetable.getObjectVersion());
      reduced.setCreationTime(timetable.getCreationTime());
      reduced.setComment(timetable.getComment());
      reduced.setVehicleJourneyIds(timetable.getVehicleJourneyIds());
      reduced.setVehicleJourneys(timetable.getVehicleJourneys());

      List<Date> dates = new ArrayList<Date>(timetable.getCalendarDays());
      for (Iterator<Date> iterator = dates.iterator(); iterator.hasNext();)
      {
         Date date = iterator.next();
         if (date == null)
         {
            iterator.remove();
         }
         else if (checkDate(date, boundaryDate, before))
         {
            iterator.remove();
         }
      }
      List<Period> periods = new ArrayList<Period>(timetable.getPeriods());
      for (Iterator<Period> iterator = periods.iterator(); iterator.hasNext();)
      {
         Period period = iterator.next();
         if (checkPeriod(period, boundaryDate, before))
         {
            iterator.remove();
         }
         else
         {
            shortenPeriod(period, boundaryDate, before);
         }
      }
      if (dates.isEmpty() && periods.isEmpty())
      {
         return null;
      }
      reduced.setCalendarDays(dates);
      reduced.setPeriods(periods);
      return  reduced;

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
