package fr.certu.chouette.exchange.xml.neptune.exporter;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;

import lombok.Setter;
import lombok.extern.log4j.Log4j;

import org.trident.schema.trident.ChouettePTNetworkType;
import org.trident.schema.trident.ChouettePTNetworkType.ChouetteArea;
import org.trident.schema.trident.ChouettePTNetworkType.ChouetteLineDescription;
import org.trident.schema.trident.ChouettePTNetworkType.ChouetteLineDescription.ChouetteRoute;
import org.trident.schema.trident.GroupOfLineType;
import org.trident.schema.trident.ITLType;
import org.trident.schema.trident.JourneyPatternType;
import org.trident.schema.trident.ObjectFactory;
import org.trident.schema.trident.TimetableType;

import fr.certu.chouette.common.ChouetteException;
import fr.certu.chouette.exchange.xml.neptune.exporter.producer.AbstractJaxbNeptuneProducer;
import fr.certu.chouette.exchange.xml.neptune.exporter.producer.AccessLinkProducer;
import fr.certu.chouette.exchange.xml.neptune.exporter.producer.AccessPointProducer;
import fr.certu.chouette.exchange.xml.neptune.exporter.producer.AreaCentroidProducer;
import fr.certu.chouette.exchange.xml.neptune.exporter.producer.CompanyProducer;
import fr.certu.chouette.exchange.xml.neptune.exporter.producer.ConnectionLinkProducer;
import fr.certu.chouette.exchange.xml.neptune.exporter.producer.FacilityProducer;
import fr.certu.chouette.exchange.xml.neptune.exporter.producer.GroupOfLineProducer;
import fr.certu.chouette.exchange.xml.neptune.exporter.producer.JourneyPatternProducer;
import fr.certu.chouette.exchange.xml.neptune.exporter.producer.LineProducer;
import fr.certu.chouette.exchange.xml.neptune.exporter.producer.PTLinkProducer;
import fr.certu.chouette.exchange.xml.neptune.exporter.producer.PTNetworkProducer;
import fr.certu.chouette.exchange.xml.neptune.exporter.producer.RouteProducer;
import fr.certu.chouette.exchange.xml.neptune.exporter.producer.RoutingConstraintProducer;
import fr.certu.chouette.exchange.xml.neptune.exporter.producer.StopAreaProducer;
import fr.certu.chouette.exchange.xml.neptune.exporter.producer.StopPointProducer;
import fr.certu.chouette.exchange.xml.neptune.exporter.producer.TimeSlotProducer;
import fr.certu.chouette.exchange.xml.neptune.exporter.producer.TimetableProducer;
import fr.certu.chouette.exchange.xml.neptune.exporter.producer.VehicleJourneyProducer;
import fr.certu.chouette.export.metadata.model.Metadata;
import fr.certu.chouette.export.metadata.model.NeptuneObjectPresenter;
import fr.certu.chouette.export.metadata.writer.DublinCoreFileWriter;
import fr.certu.chouette.export.metadata.writer.TextFileWriter;
import fr.certu.chouette.model.neptune.AccessLink;
import fr.certu.chouette.model.neptune.AccessPoint;
import fr.certu.chouette.model.neptune.CalendarDay;
import fr.certu.chouette.model.neptune.Company;
import fr.certu.chouette.model.neptune.ConnectionLink;
import fr.certu.chouette.model.neptune.Facility;
import fr.certu.chouette.model.neptune.GroupOfLine;
import fr.certu.chouette.model.neptune.JourneyPattern;
import fr.certu.chouette.model.neptune.Line;
import fr.certu.chouette.model.neptune.PTLink;
import fr.certu.chouette.model.neptune.Period;
import fr.certu.chouette.model.neptune.Route;
import fr.certu.chouette.model.neptune.StopArea;
import fr.certu.chouette.model.neptune.StopPoint;
import fr.certu.chouette.model.neptune.TimeSlot;
import fr.certu.chouette.model.neptune.Timetable;
import fr.certu.chouette.model.neptune.VehicleJourney;
import fr.certu.chouette.model.neptune.type.ChouetteAreaEnum;
import fr.certu.chouette.model.neptune.type.DayTypeEnum;
import fr.certu.chouette.neptune.JaxbNeptuneFileConverter;
import fr.certu.chouette.plugin.exchange.FormatDescription;
import fr.certu.chouette.plugin.exchange.IExportPlugin;
import fr.certu.chouette.plugin.exchange.ParameterDescription;
import fr.certu.chouette.plugin.exchange.ParameterValue;
import fr.certu.chouette.plugin.exchange.SimpleParameterValue;
import fr.certu.chouette.plugin.exchange.report.ExchangeReport;
import fr.certu.chouette.plugin.exchange.report.ExchangeReportItem;
import fr.certu.chouette.plugin.exchange.xml.exception.ExchangeExceptionCode;
import fr.certu.chouette.plugin.exchange.xml.exception.ExchangeRuntimeException;
import fr.certu.chouette.plugin.report.Report;
import fr.certu.chouette.plugin.report.ReportHolder;
import fr.certu.chouette.plugin.report.ReportItem;

/**
 * Export lines in Neptune XML format
 */
@SuppressWarnings("deprecation")
@Log4j
public class XMLNeptuneExportLinePlugin implements IExportPlugin<Line>
{

   private FormatDescription description;
   @Setter
   private LineProducer lineProducer;
   @Setter
   private PTNetworkProducer networkProducer;
   @Setter
   private RouteProducer routeProducer;
   @Setter
   private JourneyPatternProducer journeyPatternProducer;
   @Setter
   private VehicleJourneyProducer vehicleJourneyProducer;
   @Setter
   private StopPointProducer stopPointProducer;
   @Setter
   private PTLinkProducer ptLinkProducer;
   @Setter
   private CompanyProducer companyProducer;
   @Setter
   private StopAreaProducer stopAreaProducer;
   @Setter
   private AreaCentroidProducer areaCentroidProducer;
   @Setter
   private ConnectionLinkProducer connectionLinkProducer;
   @Setter
   private TimetableProducer timetableProducer;
   @Setter
   private RoutingConstraintProducer routingConstraintProducer;
   @Setter
   private GroupOfLineProducer groupOfLineProducer;
   @Setter
   private AccessPointProducer accessPointProducer;
   @Setter
   private AccessLinkProducer accessLinkProducer;
   @Setter
   private FacilityProducer facilityProducer;
   @Setter
   private TimeSlotProducer timeSlotProducer;

   /**
    * list of allowed file extensions
    */
   private List<String> allowedExtensions = Arrays.asList(new String[] { "xml", "zip" });

   /**
    * Export lines in Neptune XML format
    */
   public XMLNeptuneExportLinePlugin()
   {
      description = new FormatDescription(this.getClass().getName());
      description.setName("NEPTUNE");
      List<ParameterDescription> params = new ArrayList<ParameterDescription>();
      {
         ParameterDescription param = new ParameterDescription("outputFile", ParameterDescription.TYPE.FILEPATH, false, true);
         param.setAllowedExtensions(allowedExtensions);
         params.add(param);
      }
      {
         ParameterDescription param = new ParameterDescription("startDate", ParameterDescription.TYPE.DATE, false, false);
         params.add(param);
      }
      {
         ParameterDescription param = new ParameterDescription("endDate", ParameterDescription.TYPE.DATE, false, false);
         params.add(param);
      }
      {
         ParameterDescription param = new ParameterDescription("projectionType", ParameterDescription.TYPE.STRING, false, false);
         params.add(param);
      }
      {
         ParameterDescription param = new ParameterDescription("metadata", ParameterDescription.TYPE.OBJECT, false, false);
         params.add(param);
      }
      {
         ParameterDescription param = new ParameterDescription("extensions", ParameterDescription.TYPE.BOOLEAN, false, false);
         params.add(param);
      }
      description.setParameterDescriptions(params);
   }

   /*
    * (non-Javadoc)
    * 
    * @see fr.certu.chouette.plugin.exchange.IExchangePlugin#getDescription()
    */
   @Override
   public FormatDescription getDescription()
   {
      return description;
   }

   /*
    * (non-Javadoc)
    * 
    * @see
    * fr.certu.chouette.plugin.exchange.IExportPlugin#doExport(java.util.List,
    * java.util.List, fr.certu.chouette.plugin.report.ReportHolder)
    */
   @Override
   public void doExport(List<Line> beans, List<ParameterValue> parameters, ReportHolder reportContainer) throws ChouetteException
   {
      ExchangeReport report = new ExchangeReport(ExchangeReport.KEY.EXPORT, description.getName());
      report.updateStatus(Report.STATE.OK);
      reportContainer.setReport(report);

      String fileName = null;

      String projectionType = null;

      Date startDate = null;
      Date endDate = null;

      boolean addMetadata = false;
      boolean addExtensions = false;
      Metadata metadata = new Metadata(); // if not asked, will be used as dummy

      for (ParameterValue value : parameters)
      {
         if (value instanceof SimpleParameterValue)
         {
            SimpleParameterValue svalue = (SimpleParameterValue) value;
            if (svalue.getName().equalsIgnoreCase("outputFile"))
            {
               fileName = svalue.getFilepathValue();
               if (fileName == null)
               {
                  log.warn("outputFile changed as FILEPATH type");
                  fileName = svalue.getFilenameValue();
               }
            }
            else if (svalue.getName().equalsIgnoreCase("startDate"))
            {
               Calendar c = svalue.getDateValue();
               if (c != null)
                  startDate = new Date(c.getTime().getTime());
            }
            else if (svalue.getName().equalsIgnoreCase("endDate"))
            {
               Calendar c = svalue.getDateValue();
               if (c != null)
                  endDate = new Date(c.getTime().getTime());
            }
            else if (svalue.getName().equalsIgnoreCase("projectionType"))
            {
               projectionType = svalue.getStringValue();
            }
            else if (svalue.getName().equalsIgnoreCase("metadata"))
            {
               addMetadata = true;
               metadata = (Metadata) svalue.getObjectValue();
            }
            else if (svalue.getName().equalsIgnoreCase("extensions"))
            {
               addExtensions = svalue.getBooleanValue().booleanValue();
            }
            else
            {
               throw new IllegalArgumentException("unexpected argument " + svalue.getName());
            }
         }
         else
         {
            throw new IllegalArgumentException("unexpected argument " + value.getName());
         }
      }
      if (fileName == null)
      {
         throw new IllegalArgumentException("outputFile required");
      }
      if (startDate != null && endDate != null && startDate.after(endDate))
      {
         throw new IllegalArgumentException("startDate after endDate");
      }

      String fileExtension = fileName.substring(fileName.lastIndexOf('.') + 1).toLowerCase();
      if (!allowedExtensions.contains(fileExtension))
      {
         log.error("invalid argument outputFile " + fileName + ", allowed format : " + Arrays.toString(allowedExtensions.toArray()));
         throw new IllegalArgumentException("invalid file type : " + fileExtension);
      }

      if (beans == null || beans.isEmpty())
      {
         throw new IllegalArgumentException("no beans to export");
      }

      if (beans.size() > 1 && fileExtension.equals("xml"))
      {
         throw new IllegalArgumentException("cannot export multiple lines in one XML file");
      }

      if (fileExtension.equals("xml"))
      {
         addMetadata = false; // no metadata if single file
      }

      JaxbNeptuneFileConverter neptuneFileWriter;
      File outputFile = new File(fileName);
      if (outputFile.getParentFile() != null && !outputFile.getParentFile().exists())
      {
         outputFile.getParentFile().mkdirs();
      }
      if (fileExtension.equals("xml"))
      {
         try
         {
            neptuneFileWriter = new JaxbNeptuneFileConverter();
            Line line = beans.get(0);
            ExchangeReportItem item = new ExchangeReportItem(ExchangeReportItem.KEY.EXPORTED_LINE, Report.STATE.OK, line.getName(), line.getObjectId());
            report.addItem(item);
            JAXBElement<ChouettePTNetworkType> rootObject = exportLine(line, startDate, endDate, projectionType, item,metadata,addExtensions);
            if (rootObject != null)
            {
               log.info("exporting " + line.getName() + " (" + line.getObjectId() + ")");
               neptuneFileWriter.write(rootObject, outputFile);
            }
            else
            {
               log.info("no vehiclejourneys for line " + line.getName() + " (" + line.getObjectId() + "): not exported");
               ExchangeReportItem errorItem = new ExchangeReportItem(ExchangeReportItem.KEY.EMPTY_LINE, Report.STATE.ERROR, line.getName(), line.getObjectId());
               item.addItem(errorItem);
            }
         }
         catch (Exception e1)
         {
            e1.printStackTrace();
         }
      }
      else
      {
         metadata.setDate(Calendar.getInstance());
         metadata.setFormat("application/xml");
         metadata.setTitle("Export Neptune ");
         try
         {
            metadata.setRelation(new URL("http://www.normes-donnees-tc.org/format-dechange/donnees-theoriques/neptune/"));
         }
         catch (MalformedURLException e1)
         {
            log.error("problem with http://www.normes-donnees-tc.org/format-dechange/donnees-theoriques/neptune/ url", e1);
         }
         try
         {
            neptuneFileWriter = new JaxbNeptuneFileConverter();
            // Create the ZIP file
            ZipOutputStream out = new ZipOutputStream(new FileOutputStream(fileName));

            // Compress the files
            for (Iterator<Line> iterator = beans.iterator(); iterator.hasNext();)
            {
               Line line = iterator.next();
               iterator.remove();
               ExchangeReportItem item = new ExchangeReportItem(ExchangeReportItem.KEY.EXPORTED_LINE, Report.STATE.OK, line.getName(), line.getObjectId());
               report.addItem(item);

               JAXBElement<ChouettePTNetworkType> rootObject = exportLine(line, startDate, endDate, projectionType, item, metadata,addExtensions);
               if (rootObject != null)
               {
                  log.info("exporting " + line.getName() + " (" + line.getObjectId() + ")");

                  String name = line.getObjectId().split(":")[2];

                  ByteArrayOutputStream stream = new ByteArrayOutputStream();
                  try
                  {
                     neptuneFileWriter.write(rootObject, stream);

                  }
                  catch (JAXBException e)
                  {
                     log.error("fail to save " + line.getName() + " (" + line.getObjectId() + ")",e);
                  }
                  // Add ZIP entry to output stream.
                  ZipEntry entry = new ZipEntry(name + ".xml");
                  out.putNextEntry(entry);

                  out.write(stream.toByteArray());

                  // Complete the entry
                  out.closeEntry();
                  metadata.getResources().add(metadata.new Resource(name + ".xml", 
                        NeptuneObjectPresenter.getName(line.getPtNetwork()), NeptuneObjectPresenter.getName(line)));

               }
               else
               {
                  log.info("no vehiclejourneys for line " + line.getName() + " (" + line.getObjectId() + "): not exported");
                  ExchangeReportItem errorItem = new ExchangeReportItem(ExchangeReportItem.KEY.EMPTY_LINE, Report.STATE.WARNING, line.getName(),
                        line.getObjectId());
                  item.addItem(errorItem);
               }
               System.gc();

            }

            // write metadata
            if (addMetadata)
            {
               DublinCoreFileWriter dcWriter = new DublinCoreFileWriter();
               dcWriter.writeZipEntry(metadata, out);
               TextFileWriter tWriter = new TextFileWriter();
               tWriter.writeZipEntry(metadata, out);
            }

            // Complete the ZIP file
            out.close();
         }
         catch (Exception e)
         {
            log.error("cannot create zip file", e);
            throw new ExchangeRuntimeException(ExchangeExceptionCode.ERR_XML_WRITE, e);
         }
      }
   }

   /**
    * produce a chouetteLine from a line
    * 
    * @param line
    *           line to export
    * @param startDate
    *           optional calendar start filter
    * @param endDate
    *           optional calendar end filter
    * @param report
    * @return chouetteLine or null if line has no valid vehicleJourneys
    */
   private JAXBElement<ChouettePTNetworkType> exportLine(Line line, Date startDate, Date endDate, String projectionType, ReportItem report, Metadata metadata, boolean addExtension)
   {
      ObjectFactory factory = AbstractJaxbNeptuneProducer.tridentFactory;
      ChouettePTNetworkType rootObject = factory.createChouettePTNetworkType();

      if (line != null)
      {
         log.info("convert line "+line.getName()+" "+line.getObjectId()+" to neptune model");
         line.complete();

         if (line.getPtNetwork() != null)
         {
            rootObject.setPTNetwork(networkProducer.produce(line.getPtNetwork(),addExtension));
         }

         if (line.getGroupOfLines() != null)
         {
            for (GroupOfLine group : line.getGroupOfLines())
            {
               GroupOfLineType jaxbGroup = groupOfLineProducer.produce(group,addExtension);
               jaxbGroup.getLineId().clear();
               jaxbGroup.getLineId().add(line.getObjectId());
               rootObject.getGroupOfLine().add(jaxbGroup);
            }

         }

         HashSet<Facility> facilities = new HashSet<Facility>();
         if (line.getFacilities() != null)
         {
            facilities.addAll(line.getFacilities());
         }

         HashSet<Company> companies = new HashSet<Company>();
         if (line.getCompany() != null)
         {
            companies.add(line.getCompany());
         }

         List<VehicleJourney> vehicleJourneys = line.getVehicleJourneys();

         Set<String> validObjectIds = new HashSet<String>();
         Set<Timetable> timetables = new HashSet<Timetable>();
         Set<VehicleJourney> validVehicleJourneys = new HashSet<VehicleJourney>();
         Set<JourneyPattern> validJourneyPatterns = new HashSet<JourneyPattern>();
         Set<Route> validRoutes = new HashSet<Route>();
         Set<TimeSlot> validTimeSlots = new HashSet<TimeSlot>();

         for (VehicleJourney vehicleJourney : vehicleJourneys)
         {
            if (startDate == null && endDate == null)
            {
               if (vehicleJourney.getTimetables() != null)
               {
                  if (vehicleJourney.getRoute().getStopPoints().isEmpty())
                  {
                     log.error("route "+vehicleJourney.getRoute().getObjectId()+" has no stopPoints ");
                  }
                  else
                  {
                     timetables.addAll(vehicleJourney.getTimetables());
                     validVehicleJourneys.add(vehicleJourney);
                     validJourneyPatterns.add(vehicleJourney.getJourneyPattern());
                     validRoutes.add(vehicleJourney.getRoute());
                  }
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
                     if (startDate != null)
                        validTimetable = reduceTimetable(timetable, startDate, true);
                     if (validTimetable != null && endDate != null)
                        validTimetable = reduceTimetable(validTimetable, endDate, false);
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
                  if (vehicleJourney.getTimeSlot() != null)
                  {
                     validTimeSlots.add(vehicleJourney.getTimeSlot());
                  }
               }
            }
         }

         // if line has no valid vehiclejourneys remove line !
         if (validVehicleJourneys.isEmpty())
            return null;

         ChouetteLineDescription chouetteLineDescription = new ChouetteLineDescription();
         ChouetteLineDescription.Line jaxbLine = lineProducer.produce(line,addExtension);
         chouetteLineDescription.setLine(jaxbLine);

         // insert routes, journeyPatterns and stoppoints
         HashSet<StopPoint> stopPoints = new HashSet<StopPoint>();
         for (JourneyPattern journeyPattern : validJourneyPatterns)
         {
            JourneyPatternType jaxbObj = journeyPatternProducer.produce(journeyPattern,addExtension);
            validObjectIds.add(jaxbObj.getObjectId());
            chouetteLineDescription.getJourneyPattern().add(jaxbObj);
         }

         HashSet<PTLink> ptLinks = new HashSet<PTLink>();
         for (Route route : validRoutes)
         {
            ChouetteRoute jaxbObj = routeProducer.produce(route,addExtension);

            // add all stoppoints of route
            if (route.getStopPoints() != null)
            {
               stopPoints.addAll(route.getStopPoints());
            }

            // remove unreferenced Routes
            {
               List<String> cjps = jaxbObj.getJourneyPatternId();
               List<String> ids = new ArrayList<String>();
               ids.addAll(cjps);
               for (String id : ids)
               {
                  if (!validObjectIds.contains(id))
                  {
                     cjps.remove(id);
                  }
               }
            }
            validObjectIds.add(jaxbObj.getObjectId());
            chouetteLineDescription.getChouetteRoute().add(jaxbObj);
            if (route.getPtLinks() == null || route.getPtLinks().isEmpty())
            {
               route.rebuildPTLinks();
            }
            ptLinks.addAll(route.getPtLinks());
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

            chouetteLineDescription.getVehicleJourney().add(vehicleJourneyProducer.produce(vehicleJourney,addExtension));
         }

         HashSet<StopArea> stopAreas = new HashSet<StopArea>();

         HashSet<String> stopRefs = new HashSet<String>(); // for cleaning
         // stoparea contains
         // refs
         for (StopPoint stopPoint : stopPoints)
         {
            stopRefs.add(stopPoint.getObjectId());
            chouetteLineDescription.getStopPoint().add(stopPointProducer.produce(stopPoint,addExtension));
            stopAreas.addAll(extractStopAreaHierarchy(stopPoint.getContainedInStopArea(), line));
            if (stopPoint.getFacilities() != null)
            {
               facilities.addAll(stopPoint.getFacilities());
            }
         }
         // add RoutingConstraints
         if (line.getRoutingConstraints() != null)
         {
            stopAreas.addAll(line.getRoutingConstraints());
         }

         for (PTLink ptLink : ptLinks)
         {
            chouetteLineDescription.getPtLink().add(ptLinkProducer.produce(ptLink,addExtension));
         }

         for (Company company : companies)
         {
            rootObject.getCompany().add(companyProducer.produce(company,addExtension));
         }

         ChouetteArea chouetteArea = new ChouetteArea();
         HashSet<ConnectionLink> connectionLinks = new HashSet<ConnectionLink>();
         HashSet<AccessLink> accessLinks = new HashSet<AccessLink>();
         HashSet<AccessPoint> accessPoints = new HashSet<AccessPoint>();

         for (StopArea stopArea : stopAreas)
         {
            stopRefs.add(stopArea.getObjectId());
            if (stopArea.getFacilities() != null)
            {
               facilities.addAll(stopArea.getFacilities());
            }
         }
         for (StopArea stopArea : stopAreas)
         {
            stopArea.toProjection(projectionType);
            ChouetteArea.StopArea chouetteStopArea = stopAreaProducer.produce(stopArea,addExtension);
            // remove external stopPoints or stopareas
            List<String> pointRefs = chouetteStopArea.getContains();
            if (pointRefs.isEmpty())
            {
               log.error("no children for stop area "+stopArea.getObjectId()+ " "+stopArea.getName());
               throw new NullPointerException("contains");
            }
            for (Iterator<String> iterator = pointRefs.iterator(); iterator.hasNext();)
            {
               String ref = iterator.next();
               if (!stopRefs.contains(ref))
               {
                  iterator.remove();
               }

            }
            if (pointRefs.isEmpty())
            {
               log.error("no more children for stop area "+stopArea.getObjectId()+ " "+stopArea.getName());
               throw new NullPointerException("contains");
            }

            if (stopArea.hasCoordinates())
               metadata.getSpatialCoverage().update(stopArea.getLongitude().doubleValue(), stopArea.getLatitude().doubleValue());
            if (stopArea.hasAddress() || stopArea.hasCoordinates() || stopArea.hasProjection())
            {
               ChouetteArea.AreaCentroid centroid = areaCentroidProducer.produce(stopArea,addExtension);
               chouetteArea.getAreaCentroid().add(centroid);
               chouetteStopArea.setCentroidOfArea(centroid.getObjectId());
            }
            chouetteArea.getStopArea().add(chouetteStopArea);
            if (stopArea.getConnectionLinks() != null)
            {
               connectionLinks.addAll(stopArea.getConnectionLinks());
            }
            if (stopArea.getAccessLinks() != null)
            {
               accessLinks.addAll(stopArea.getAccessLinks());
            }
         }

         rootObject.setChouetteArea(chouetteArea);

         for (ConnectionLink connectionLink : connectionLinks)
         {
            rootObject.getConnectionLink().add(connectionLinkProducer.produce(connectionLink,addExtension));
            if (connectionLink.getFacilities() != null)
            {
               facilities.addAll(connectionLink.getFacilities());
            }
         }

         for (TimeSlot timeSlot : validTimeSlots)
         {
            rootObject.getTimeSlot().add(timeSlotProducer.produce(timeSlot,addExtension));
         }

         for (Facility facility : facilities)
         {
            facility.toProjection(projectionType);
            rootObject.getFacility().add(facilityProducer.produce(facility,addExtension));
         }

         for (AccessLink accessLink : accessLinks)
         {
            rootObject.getAccessLink().add(accessLinkProducer.produce(accessLink,addExtension));
            accessPoints.add(accessLink.getAccessPoint());
         }

         for (AccessPoint accessPoint : accessPoints)
         {
            accessPoint.toProjection(projectionType);
            rootObject.getAccessPoint().add(accessPointProducer.produce(accessPoint,addExtension));
         }

         for (Timetable timetable : timetables)
         {
            rootObject.getTimetable().add(timetableProducer.produce(timetable,addExtension));
            metadata.getTemporalCoverage().update(timetable.getStartOfPeriod(), timetable.getEndOfPeriod());
         }

         // routing Constraints
         if (line.getRoutingConstraints() != null)
         {
            for (StopArea routingConstraint : line.getRoutingConstraints())
            {
               if (stopRefs.contains(routingConstraint.getObjectId()))
               {
                  ITLType jaxbITL = routingConstraintProducer.produceITL(line, routingConstraint,addExtension);
                  chouetteLineDescription.getITL().add(jaxbITL);
               }
               else
               {
                  // TODO ? routing constraint without stop on line
               }
            }
         }

         rootObject.setChouetteLineDescription(chouetteLineDescription);

         // cleaning a little
         rootObject.getPTNetwork().getLineId().clear();
         rootObject.getPTNetwork().getLineId().add(jaxbLine.getObjectId());

         // remove unreferenced Routes
         {
            List<String> crs = jaxbLine.getRouteId();
            List<String> ids = new ArrayList<String>();
            ids.addAll(crs);
            for (String id : ids)
            {
               if (!validObjectIds.contains(id))
               {
                  crs.remove(id);
               }
            }
         }

         // remove unreferenced vj from timetables
         for (TimetableType timetable : rootObject.getTimetable())
         {
            List<String> vjs = timetable.getVehicleJourneyId();
            List<String> ids = new ArrayList<String>();
            ids.addAll(vjs);
            for (String id : ids)
            {
               if (!vehicleJourneyObjectIds.contains(id))
               {
                  vjs.remove(id);
               }
            }
         }

      }

      return factory.createChouettePTNetwork(rootObject);
   }

   /**
    * extract parent tree for physical Stop
    * 
    * @param stopArea
    *           physical stop to check
    * @param line
    *           line for routingConstraint relationship
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
         stopAreas.add(stopArea);
         if (stopArea.getParent() != null)
         {
            stopAreas.addAll(extractStopAreaHierarchy(stopArea.getParent(), line));
         }
      }

      return stopAreas;
   }

   /**
    * produce a timetable reduced to a date
    * 
    * @param timetable
    *           original timetable
    * @param boundaryDate
    *           boundary date
    * @param before
    *           true to eliminate before boundary date , false otherwise
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

      List<CalendarDay> dates = new ArrayList<CalendarDay>(timetable.getCalendarDays());
      for (Iterator<CalendarDay> iterator = dates.iterator(); iterator.hasNext();)
      {
         CalendarDay date = iterator.next();
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
      reduced.computeLimitOfPeriods();
      return reduced;

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
   private boolean checkDate(CalendarDay date, Date boundaryDate, boolean before)
   {
      if (before)
      {
         return date.getDate().before(boundaryDate);
      }
      return date.getDate().after(boundaryDate);
   }

}
