/**
 * Projet CHOUETTE
 *
 * ce projet est sous license libre
 * voir LICENSE.txt pour plus de details
 *
 */
package fr.certu.chouette.exchange.xml.neptune.importer;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

import org.trident.schema.trident.ChouetteFacilityType;
import org.trident.schema.trident.ChouettePTNetworkType;
import org.trident.schema.trident.ChouettePTNetworkType.ChouetteLineDescription;
import org.trident.schema.trident.ChouettePTNetworkType.ChouetteLineDescription.ChouetteRoute;
import org.trident.schema.trident.CompanyType;
import org.trident.schema.trident.GroupOfLineType;
import org.trident.schema.trident.ITLType;
import org.trident.schema.trident.JourneyPatternType;
import org.trident.schema.trident.PTAccessPointType;
import org.trident.schema.trident.PTLinkType;
import org.trident.schema.trident.PTNetworkType;
import org.trident.schema.trident.TimeSlotType;
import org.trident.schema.trident.TimetableType;
import org.trident.schema.trident.VehicleJourneyType;

import fr.certu.chouette.exchange.xml.neptune.importer.producer.AccessLinkProducer;
import fr.certu.chouette.exchange.xml.neptune.importer.producer.AccessPointProducer;
import fr.certu.chouette.exchange.xml.neptune.importer.producer.AreaCentroidProducer;
import fr.certu.chouette.exchange.xml.neptune.importer.producer.CompanyProducer;
import fr.certu.chouette.exchange.xml.neptune.importer.producer.ConnectionLinkProducer;
import fr.certu.chouette.exchange.xml.neptune.importer.producer.FacilityProducer;
import fr.certu.chouette.exchange.xml.neptune.importer.producer.GroupOfLineProducer;
import fr.certu.chouette.exchange.xml.neptune.importer.producer.JourneyPatternProducer;
import fr.certu.chouette.exchange.xml.neptune.importer.producer.LineProducer;
import fr.certu.chouette.exchange.xml.neptune.importer.producer.PTLinkProducer;
import fr.certu.chouette.exchange.xml.neptune.importer.producer.PTNetworkProducer;
import fr.certu.chouette.exchange.xml.neptune.importer.producer.RouteProducer;
import fr.certu.chouette.exchange.xml.neptune.importer.producer.RoutingConstraintProducer;
import fr.certu.chouette.exchange.xml.neptune.importer.producer.StopAreaProducer;
import fr.certu.chouette.exchange.xml.neptune.importer.producer.StopPointProducer;
import fr.certu.chouette.exchange.xml.neptune.importer.producer.TimeSlotProducer;
import fr.certu.chouette.exchange.xml.neptune.importer.producer.TimetableProducer;
import fr.certu.chouette.exchange.xml.neptune.importer.producer.VehicleJourneyProducer;
import fr.certu.chouette.exchange.xml.neptune.model.AreaCentroid;
import fr.certu.chouette.exchange.xml.neptune.model.NeptuneRoutingConstraint;
import fr.certu.chouette.model.neptune.AccessLink;
import fr.certu.chouette.model.neptune.AccessPoint;
import fr.certu.chouette.model.neptune.Company;
import fr.certu.chouette.model.neptune.ConnectionLink;
import fr.certu.chouette.model.neptune.Facility;
import fr.certu.chouette.model.neptune.GroupOfLine;
import fr.certu.chouette.model.neptune.JourneyPattern;
import fr.certu.chouette.model.neptune.Line;
import fr.certu.chouette.model.neptune.PTLink;
import fr.certu.chouette.model.neptune.PTNetwork;
import fr.certu.chouette.model.neptune.Route;
import fr.certu.chouette.model.neptune.StopArea;
import fr.certu.chouette.model.neptune.StopPoint;
import fr.certu.chouette.model.neptune.TimeSlot;
import fr.certu.chouette.model.neptune.Timetable;
import fr.certu.chouette.model.neptune.VehicleJourney;
import fr.certu.chouette.plugin.exchange.SharedImportedData;
import fr.certu.chouette.plugin.exchange.UnsharedImportedData;
import fr.certu.chouette.plugin.exchange.tools.DbVehicleJourneyFactory;
import fr.certu.chouette.plugin.report.ReportItem;
import fr.certu.chouette.plugin.validation.report.PhaseReportItem;

/**
 * convert each Neptune item in corresponding Chouette Neptune one
 * 
 */
public class NeptuneConverter
{
   /**
    * line producer
    */
   @Getter
   @Setter
   private LineProducer lineProducer;
   /**
    * route producer
    */
   @Getter
   @Setter
   private RouteProducer routeProducer;
   /**
    * network producer
    */
   @Getter
   @Setter
   private PTNetworkProducer networkProducer;
   /**
    * company producer
    */
   @Getter
   @Setter
   private CompanyProducer companyProducer;
   /**
    * journey pattern producer
    */
   @Getter
   @Setter
   private JourneyPatternProducer journeyPatternProducer;
   /**
    * ptLink producer
    */
   @Getter
   @Setter
   private PTLinkProducer ptLinkProducer;
   /**
    * vehicle journey and vehicle journay at stop producer
    */
   @Getter
   @Setter
   private VehicleJourneyProducer vehicleJourneyProducer;
   /**
    * stop point producer
    */
   @Getter
   @Setter
   private StopPointProducer stopPointProducer;
   /**
    * stop area producer
    */
   @Getter
   @Setter
   private StopAreaProducer stopAreaProducer;
   /**
    * area centriod producer
    */
   @Getter
   @Setter
   private AreaCentroidProducer areaCentroidProducer;
   /**
    * connection link producer
    */
   @Getter
   @Setter
   private ConnectionLinkProducer connectionLinkProducer;
   /**
    * time table producer
    */
   @Getter
   @Setter
   private TimetableProducer timetableProducer;
   /**
    * routing contraint producer
    */
   @Getter
   @Setter
   private RoutingConstraintProducer routingConstraintProducer;
   /**
    * access link producer
    */
   @Getter
   @Setter
   private AccessLinkProducer accessLinkProducer;
   /**
    * access point producer
    */
   @Getter
   @Setter
   private AccessPointProducer accessPointProducer;
   /**
    * group of line producer
    */
   @Getter
   @Setter
   private GroupOfLineProducer groupOfLineProducer;
   /**
    * facility producer
    */
   @Getter
   @Setter
   private FacilityProducer facilityProducer;
   /**
    * time slot producer
    */
   @Getter
   @Setter
   private TimeSlotProducer timeSlotProducer;

   /**
    * extract the line
    * 
    * @param rootObject
    *           root XML object
    * @param sharedData
    * @param unshareableData
    * @param validator
    * @param report
    *           error report
    * @return line produced from ChouetteLineDescription
    */
   public Line extractLine(String sourceFile, ChouettePTNetworkType rootObject,
         ReportItem importReport, PhaseReportItem validationReport,
         SharedImportedData sharedData, UnsharedImportedData unshareableData,
         Level2Validator validator)
   {
      ChouetteLineDescription lineDescription = rootObject
            .getChouetteLineDescription();
      org.trident.schema.trident.ChouettePTNetworkType.ChouetteLineDescription.Line xmlLine = lineDescription
            .getLine();

      Line line = lineProducer.produce(sourceFile, xmlLine, importReport,
            validationReport, sharedData, unshareableData);

      validator.setLine(xmlLine);
      return line;
   }

   /**
    * extract routes
    * 
    * @param rootObject
    *           root XML object
    * @param report
    *           error report
    * @return routes produced from ChouetteLineDescription.ChouetteRoute
    */
   public List<Route> extractRoutes(String sourceFile,
         ChouettePTNetworkType rootObject, ReportItem importReport,
         PhaseReportItem validationReport, SharedImportedData sharedData,
         UnsharedImportedData unshareableData, Level2Validator validator)
   {
      ChouetteLineDescription lineDescription = rootObject
            .getChouetteLineDescription();
      List<ChouetteRoute> xmlRoutes = lineDescription.getChouetteRoute();

      List<Route> routes = new ArrayList<Route>();

      for (ChouetteRoute xmlRoute : xmlRoutes)
      {
         Route route = routeProducer.produce(sourceFile, xmlRoute,
               importReport, validationReport, sharedData, unshareableData);
         routes.add(route);
      }
      validator.addRoutes(xmlRoutes);

      return routes;
   }

   /**
    * extract routing constraint relations
    * 
    * @param rootObject
    *           root XML object
    * @param report
    *           error report
    * @return NeptuneRoutingConstraint produced from ChouetteLineDescription.ITL
    */
   public List<NeptuneRoutingConstraint> extractRoutingConstraints(
         String sourceFile, ChouettePTNetworkType rootObject,
         ReportItem importReport, PhaseReportItem validationReport,
         SharedImportedData sharedData, UnsharedImportedData unshareableData,
         Level2Validator validator)
   {
      ChouetteLineDescription lineDescription = rootObject
            .getChouetteLineDescription();
      List<ITLType> itls = lineDescription.getITL();

      List<NeptuneRoutingConstraint> restrictionConstraints = routingConstraintProducer
            .produce(sourceFile, itls, importReport, validationReport,
                  sharedData, unshareableData);

      validator.addRoutingConstraints(itls);
      return restrictionConstraints;
   }

   /**
    * extract companies
    * 
    * @param rootObject
    *           root XML object
    * @param report
    *           error report
    * @return companies produced from Company
    */
   public List<Company> extractCompanies(String sourceFile,
         ChouettePTNetworkType rootObject, ReportItem importReport,
         PhaseReportItem validationReport, SharedImportedData sharedData,
         UnsharedImportedData unshareableData, Level2Validator validator)
   {
      List<CompanyType> xmlCompanies = rootObject.getCompany();

      List<Company> companies = new ArrayList<Company>();

      for (CompanyType xmlCompany : xmlCompanies)
      {
         Company company = companyProducer.produce(sourceFile, xmlCompany,
               importReport, validationReport, sharedData, unshareableData);
         companies.add(company);
      }

      validator.addCompanies(xmlCompanies);
      return companies;
   }

   /**
    * extract network
    * 
    * @param rootObject
    *           root XML object
    * @param report
    *           error report
    * @return network produced from PTNetwork
    */
   public PTNetwork extractPTNetwork(String sourceFile,
         ChouettePTNetworkType rootObject, ReportItem importReport,
         PhaseReportItem validationReport, SharedImportedData sharedData,
         UnsharedImportedData unshareableData, Level2Validator validator)
   {
      PTNetworkType xmlPTNetwork = rootObject.getPTNetwork();

      PTNetwork ptNetwork = networkProducer.produce(sourceFile, xmlPTNetwork,
            importReport, validationReport, sharedData, unshareableData);

      validator.setPtNetwork(xmlPTNetwork);
      return ptNetwork;
   }

   /**
    * extract journey patterns
    * 
    * @param rootObject
    *           root XML object
    * @param report
    *           error report
    * @return journeyPatterns produced from
    *         ChouetteLineDescription.JourneyPattern
    */
   public List<JourneyPattern> extractJourneyPatterns(String sourceFile,
         ChouettePTNetworkType rootObject, ReportItem importReport,
         PhaseReportItem validationReport, SharedImportedData sharedData,
         UnsharedImportedData unshareableData, Level2Validator validator)
   {
      ChouetteLineDescription lineDescription = rootObject
            .getChouetteLineDescription();
      List<JourneyPatternType> xmlJourneyPatterns = lineDescription
            .getJourneyPattern();

      List<JourneyPattern> journeyPatterns = new ArrayList<JourneyPattern>();

      for (JourneyPatternType xmlJourneyPattern : xmlJourneyPatterns)
      {
         JourneyPattern journeyPattern = journeyPatternProducer.produce(
               sourceFile, xmlJourneyPattern, validationReport,
               validationReport, sharedData, unshareableData);
         journeyPatterns.add(journeyPattern);
      }
      validator.addJourneyPatterns(xmlJourneyPatterns);

      return journeyPatterns;
   }

   /**
    * extract PTLinks
    * 
    * @param rootObject
    *           root XML object
    * @param report
    *           error report
    * @return PTLinks produced from ChouetteLineDescription.PtLink
    */
   public List<PTLink> extractPTLinks(String sourceFile,
         ChouettePTNetworkType rootObject, ReportItem importReport,
         PhaseReportItem validationReport, SharedImportedData sharedData,
         UnsharedImportedData unshareableData, Level2Validator validator)
   {
      ChouetteLineDescription lineDescription = rootObject
            .getChouetteLineDescription();
      List<PTLinkType> xmlPTLinks = lineDescription.getPtLink();

      List<PTLink> ptLinks = new ArrayList<PTLink>();

      for (PTLinkType xmlPTLink : xmlPTLinks)
      {
         PTLink ptLink = ptLinkProducer.produce(sourceFile, xmlPTLink,
               importReport, validationReport, sharedData, unshareableData);
         ptLinks.add(ptLink);
      }

      validator.addPTLinks(xmlPTLinks);
      return ptLinks;
   }

   /**
    * extract VehicleJourneys
    * 
    * @param rootObject
    *           root XML object
    * @param report
    *           error report
    * @return VehicleJourneys produced from
    *         ChouetteLineDescription.VehicleJourney
    */
   public List<VehicleJourney> extractVehicleJourneys(String sourceFile,
         ChouettePTNetworkType rootObject, ReportItem importReport,
         PhaseReportItem validationReport, SharedImportedData sharedData,
         UnsharedImportedData unshareableData, Level2Validator validator,
         boolean optimizeMemory)
   {
      DbVehicleJourneyFactory vjFactory = new DbVehicleJourneyFactory(
            "Neptune", optimizeMemory);
      vehicleJourneyProducer.setFactory(vjFactory);
      ChouetteLineDescription lineDescription = rootObject
            .getChouetteLineDescription();
      List<VehicleJourneyType> xmlVehicleJourneys = lineDescription
            .getVehicleJourney();

      List<VehicleJourney> vehicleJourneys = new ArrayList<VehicleJourney>();

      for (VehicleJourneyType xmlVehicleJourney : xmlVehicleJourneys)
      {
         VehicleJourney vehicleJourney = vehicleJourneyProducer.produce(
               sourceFile, xmlVehicleJourney, importReport, validationReport,
               sharedData, unshareableData);
         vehicleJourneys.add(vehicleJourney);
      }
      validator.addVehicleJourneys(xmlVehicleJourneys);
      return vehicleJourneys;
   }

   /**
    * extract StopPoints
    * 
    * @param rootObject
    *           root XML object
    * @param report
    *           error report
    * @return StopPoints produced from ChouetteLineDescription.StopPoint
    */
   public List<StopPoint> extractStopPoints(String sourceFile,
         ChouettePTNetworkType rootObject, ReportItem importReport,
         PhaseReportItem validationReport, SharedImportedData sharedData,
         UnsharedImportedData unshareableData, Level2Validator validator)
   {
      ChouetteLineDescription lineDescription = rootObject
            .getChouetteLineDescription();
      List<org.trident.schema.trident.ChouettePTNetworkType.ChouetteLineDescription.StopPoint> xmlStopPoints = lineDescription
            .getStopPoint();

      List<StopPoint> stopPoints = new ArrayList<StopPoint>();

      for (org.trident.schema.trident.ChouettePTNetworkType.ChouetteLineDescription.StopPoint xmlStopPoint : xmlStopPoints)
      {
         StopPoint stopPoint = stopPointProducer.produce(sourceFile,
               xmlStopPoint, importReport, validationReport, sharedData,
               unshareableData);
         stopPoints.add(stopPoint);
      }
      validator.addStopPoints(xmlStopPoints);

      return stopPoints;
   }

   /**
    * extract StopAreas
    * 
    * @param rootObject
    *           root XML object
    * @param report
    *           error report
    * @return StopAreas produced from ChouetteArea.StopArea
    */
   public List<StopArea> extractStopAreas(String sourceFile,
         ChouettePTNetworkType rootObject, ReportItem importReport,
         PhaseReportItem validationReport, SharedImportedData sharedData,
         UnsharedImportedData unshareableData, Level2Validator validator)
   {
      List<org.trident.schema.trident.ChouettePTNetworkType.ChouetteArea.StopArea> xmlStopAreas = rootObject
            .getChouetteArea().getStopArea();

      List<StopArea> stopAreas = new ArrayList<StopArea>();

      for (org.trident.schema.trident.ChouettePTNetworkType.ChouetteArea.StopArea xmlStopArea : xmlStopAreas)
      {
         StopArea stopArea = stopAreaProducer.produce(sourceFile, xmlStopArea,
               importReport, validationReport, sharedData, unshareableData);
         stopAreas.add(stopArea);
      }
      validator.addStopAreas(xmlStopAreas);

      return stopAreas;
   }

   /**
    * extract AreaCentroids
    * 
    * @param rootObject
    *           root XML object
    * @param report
    *           error report
    * @return AreaCentroids produced from ChouetteArea.AreaCentroid
    */
   public List<AreaCentroid> extractAreaCentroids(String sourceFile,
         ChouettePTNetworkType rootObject, ReportItem importReport,
         PhaseReportItem validationReport, SharedImportedData sharedData,
         UnsharedImportedData unshareableData, Level2Validator validator)
   {
      List<org.trident.schema.trident.ChouettePTNetworkType.ChouetteArea.AreaCentroid> xmlAreaCentroids = rootObject
            .getChouetteArea().getAreaCentroid();

      List<AreaCentroid> areaCentroids = new ArrayList<AreaCentroid>();

      for (org.trident.schema.trident.ChouettePTNetworkType.ChouetteArea.AreaCentroid xmlAreaCentroid : xmlAreaCentroids)
      {
         AreaCentroid areaCentroid = areaCentroidProducer.produce(sourceFile,
               xmlAreaCentroid, importReport, validationReport, sharedData,
               unshareableData);
         areaCentroids.add(areaCentroid);
      }

      validator.addAreaCentroids(xmlAreaCentroids);
      return areaCentroids;
   }

   /**
    * extract ConnectionLinks
    * 
    * @param rootObject
    *           root XML object
    * @param report
    *           error report
    * @return ConnectionLinks produced from ConnectionLink
    */
   public List<ConnectionLink> extractConnectionLinks(String sourceFile,
         ChouettePTNetworkType rootObject, ReportItem importReport,
         PhaseReportItem validationReport, SharedImportedData sharedData,
         UnsharedImportedData unshareableData, Level2Validator validator)
   {
      List<org.trident.schema.trident.ChouettePTNetworkType.ConnectionLink> xmlConnectionLinks = rootObject
            .getConnectionLink();

      List<ConnectionLink> connectionLinks = new ArrayList<ConnectionLink>();

      for (org.trident.schema.trident.ChouettePTNetworkType.ConnectionLink xmlConnectionLink : xmlConnectionLinks)
      {
         ConnectionLink connectionLink = connectionLinkProducer.produce(
               sourceFile, xmlConnectionLink, importReport, validationReport,
               sharedData, unshareableData);
         connectionLinks.add(connectionLink);
      }
      validator.addConnectionLinks(xmlConnectionLinks);

      return connectionLinks;
   }

   /**
    * extract Timetables
    * 
    * @param rootObject
    *           root XML object
    * @param report
    *           error report
    * @return Timetables produced from Timetable
    */
   public List<Timetable> extractTimetables(String sourceFile,
         ChouettePTNetworkType rootObject, ReportItem importReport,
         PhaseReportItem validationReport, SharedImportedData sharedData,
         UnsharedImportedData unshareableData, Level2Validator validator)
   {
      List<TimetableType> xmlTimetables = rootObject.getTimetable();

      List<Timetable> timetables = new ArrayList<Timetable>();

      for (TimetableType xmlTimetable : xmlTimetables)
      {
         Timetable timetable = timetableProducer.produce(sourceFile,
               xmlTimetable, importReport, validationReport, sharedData,
               unshareableData);
         timetables.add(timetable);
      }
      validator.addTimetables(xmlTimetables);

      return timetables;
   }

   /**
    * extract AccessLinks
    * 
    * @param rootObject
    *           root XML object
    * @param report
    *           error report
    * @return AccessLinks produced from AccessLink
    */
   public List<AccessLink> extractAccessLinks(String sourceFile,
         ChouettePTNetworkType rootObject, ReportItem importReport,
         PhaseReportItem validationReport, SharedImportedData sharedData,
         UnsharedImportedData unshareableData, Level2Validator validator)
   {
      List<org.trident.schema.trident.ChouettePTNetworkType.AccessLink> xmlAccessLinks = rootObject
            .getAccessLink();

      List<AccessLink> accessLinks = new ArrayList<AccessLink>();

      for (org.trident.schema.trident.ChouettePTNetworkType.AccessLink xmlAccessLink : xmlAccessLinks)
      {
         AccessLink accessLink = accessLinkProducer.produce(sourceFile,
               xmlAccessLink, importReport, validationReport, sharedData,
               unshareableData);
         accessLinks.add(accessLink);
      }
      validator.addAccessLinks(xmlAccessLinks);

      return accessLinks;
   }

   /**
    * extract AccessPoints
    * 
    * @param rootObject
    *           root XML object
    * @param report
    *           error report
    * @return AccessPoints produced from AccessPoint
    */
   public List<AccessPoint> extractAccessPoints(String sourceFile,
         ChouettePTNetworkType rootObject, ReportItem importReport,
         PhaseReportItem validationReport, SharedImportedData sharedData,
         UnsharedImportedData unshareableData, Level2Validator validator)
   {
      List<PTAccessPointType> xmlAccessPoints = rootObject.getAccessPoint();

      List<AccessPoint> accessPoints = new ArrayList<AccessPoint>();

      for (PTAccessPointType xmlAccessPoint : xmlAccessPoints)
      {
         AccessPoint accessPoint = accessPointProducer.produce(sourceFile,
               xmlAccessPoint, importReport, validationReport, sharedData,
               unshareableData);
         accessPoints.add(accessPoint);
      }

      validator.addAccessPoints(xmlAccessPoints);
      return accessPoints;
   }

   /**
    * extract GroupOfLines
    * 
    * @param rootObject
    *           root XML object
    * @param report
    *           error report
    * @return GroupOfLines produced from GroupOfLine
    */
   public List<GroupOfLine> extractGroupOfLines(String sourceFile,
         ChouettePTNetworkType rootObject, ReportItem importReport,
         PhaseReportItem validationReport, SharedImportedData sharedData,
         UnsharedImportedData unshareableData, Level2Validator validator)
   {
      List<GroupOfLine> groupOfLines = new ArrayList<GroupOfLine>();

      List<GroupOfLineType> xmlGroupOfLines = rootObject.getGroupOfLine();
      for (GroupOfLineType xmlGroupOfLine : xmlGroupOfLines)
      {
         GroupOfLine groupOfLine = groupOfLineProducer.produce(sourceFile,
               xmlGroupOfLine, importReport, validationReport, sharedData,
               unshareableData);
         groupOfLines.add(groupOfLine);
      }
      validator.addGroupOfLines(xmlGroupOfLines);

      return groupOfLines;
   }

   /**
    * extract Facilities
    * 
    * @param rootObject
    *           root XML object
    * @param report
    *           error report
    * @return Facilities produced from Facility
    */
   public List<Facility> extractFacilities(String sourceFile,
         ChouettePTNetworkType rootObject, ReportItem importReport,
         PhaseReportItem validationReport, SharedImportedData sharedData,
         UnsharedImportedData unshareableData, Level2Validator validator)
   {
      List<Facility> facilities = new ArrayList<Facility>();
      List<ChouetteFacilityType> xmlFacilities = rootObject.getFacility();
      for (ChouetteFacilityType xmlFacility : xmlFacilities)
      {
         Facility facility = facilityProducer.produce(sourceFile, xmlFacility,
               importReport, validationReport, sharedData, unshareableData);
         facilities.add(facility);
      }
      validator.addFacilities(xmlFacilities);
      return facilities;
   }

   /**
    * extract TimeSlots
    * 
    * @param rootObject
    *           root XML object
    * @param report
    *           error report
    * @return TimeSlots produced from TimeSlot
    */
   public List<TimeSlot> extractTimeSlots(String sourceFile,
         ChouettePTNetworkType rootObject, ReportItem importReport,
         PhaseReportItem validationReport, SharedImportedData sharedData,
         UnsharedImportedData unshareableData, Level2Validator validator)
   {
      List<TimeSlot> timeSlots = new ArrayList<TimeSlot>();
      List<TimeSlotType> xmlTimeSlots = rootObject.getTimeSlot();
      for (TimeSlotType xmlTimeSlot : xmlTimeSlots)
      {
         TimeSlot timeSlot = timeSlotProducer.produce(sourceFile, xmlTimeSlot,
               importReport, validationReport, sharedData, unshareableData);
         timeSlots.add(timeSlot);
      }
      validator.addTimeSlots(xmlTimeSlots);

      return timeSlots;
   }
}
