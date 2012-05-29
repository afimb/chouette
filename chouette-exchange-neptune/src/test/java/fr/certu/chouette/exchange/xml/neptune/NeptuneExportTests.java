package fr.certu.chouette.exchange.xml.neptune;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.Assert;
import org.testng.Reporter;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import fr.certu.chouette.common.ChouetteException;
import fr.certu.chouette.model.neptune.AccessLink;
import fr.certu.chouette.model.neptune.AccessPoint;
import fr.certu.chouette.model.neptune.ConnectionLink;
import fr.certu.chouette.model.neptune.Facility;
import fr.certu.chouette.model.neptune.JourneyPattern;
import fr.certu.chouette.model.neptune.Line;
import fr.certu.chouette.model.neptune.Route;
import fr.certu.chouette.model.neptune.StopArea;
import fr.certu.chouette.model.neptune.StopPoint;
import fr.certu.chouette.model.neptune.type.facility.AccessFacilityEnumeration;
import fr.certu.chouette.model.neptune.type.facility.FacilityFeature;
import fr.certu.chouette.plugin.exchange.IExportPlugin;
import fr.certu.chouette.plugin.exchange.IImportPlugin;
import fr.certu.chouette.plugin.exchange.ParameterValue;
import fr.certu.chouette.plugin.exchange.SimpleParameterValue;
import fr.certu.chouette.plugin.report.Report;
import fr.certu.chouette.plugin.report.ReportHolder;
import fr.certu.chouette.plugin.report.ReportItem;

@ContextConfiguration(locations={"classpath:testContext.xml"})
@SuppressWarnings("unchecked")
public class NeptuneExportTests extends AbstractTestNGSpringContextTests
{
   private static final Logger logger = Logger.getLogger(NeptuneExportTests.class);

   private IImportPlugin<Line> importLine = null;
   private IExportPlugin<Line> exportLine = null;
   private String neptuneFile = null;
   private String path="src/test/resources/";
   private String targetPath="target/test/";


   @Test(groups={"ExportLine"}, description="Get a bean from context")
   public void getBean()
   {
      importLine = (IImportPlugin<Line>) applicationContext.getBean("NeptuneLineImport") ;
      exportLine = (IExportPlugin<Line>) applicationContext.getBean("NeptuneLineExport") ;
   }

   @Parameters({"neptuneFile"})
   @Test (groups = {"ExportLine"}, description = "Export Plugin should export neptune file",dependsOnMethods={"getBean"})
   public void getNeptuneFile(String neptuneFile)
   {
      this.neptuneFile = neptuneFile;
   }


//   @Test (groups = {"CheckParameters"}, description = "Import Plugin should reject wrong file extension",dependsOnMethods={"getBean"},expectedExceptions={IllegalArgumentException.class})
//   public void verifyCheckFileExtension() throws ChouetteException
//   {
//      List<ParameterValue> parameters = new ArrayList<ParameterValue>();
//      SimpleParameterValue simpleParameterValue = new SimpleParameterValue("inputFile");
//      simpleParameterValue.setFilepathValue(path+"/dummyFile.tmp");
//      parameters.add(simpleParameterValue);
//      ReportHolder report = new ReportHolder();
//
//      importLine.doImport(parameters, report);
//      Assert.fail("expected exception not raised");
//   }

//   @Test (groups = {"CheckParameters"}, description = "Import Plugin should reject unknown parameter",dependsOnMethods={"getBean"},expectedExceptions={IllegalArgumentException.class})
//   public void verifyCheckUnknownParameter() throws ChouetteException
//   {
//      List<ParameterValue> parameters = new ArrayList<ParameterValue>();
//      SimpleParameterValue simpleParameterValue = new SimpleParameterValue("inputFile");
//      simpleParameterValue.setFilepathValue(path+"/dummyFile.xml");
//      parameters.add(simpleParameterValue);
//      simpleParameterValue = new SimpleParameterValue("dummyParameter");
//      simpleParameterValue.setStringValue("dummy value");
//      parameters.add(simpleParameterValue);
//      ReportHolder report = new ReportHolder();
//
//      importLine.doImport(parameters, report);
//      Assert.fail("expected exception not raised");
//   }
//
//   @Test (groups = {"CheckParameters"}, description = "Import Plugin should reject missing mandatory parameter",dependsOnMethods={"getBean"},expectedExceptions={IllegalArgumentException.class})
//   public void verifyCheckMandatoryParameter() throws ChouetteException
//   {
//      List<ParameterValue> parameters = new ArrayList<ParameterValue>();
//      SimpleParameterValue simpleParameterValue = new SimpleParameterValue("validate");
//      simpleParameterValue.setBooleanValue(true);
//      parameters.add(simpleParameterValue);
//      ReportHolder report = new ReportHolder();
//
//      importLine.doImport(parameters, report);
//      Assert.fail("expected exception not raised");
//   }
//
//
//   @Test (groups = {"CheckParameters"}, description = "Import Plugin should reject wrong file type",dependsOnMethods={"getBean"},expectedExceptions={IllegalArgumentException.class})
//   public void verifyCheckFileType() throws ChouetteException
//   {
//      List<ParameterValue> parameters = new ArrayList<ParameterValue>();
//      SimpleParameterValue simpleParameterValue = new SimpleParameterValue("inputFile");
//      simpleParameterValue.setFilepathValue(path+"/dummyFile.xml");
//      parameters.add(simpleParameterValue);
//      simpleParameterValue = new SimpleParameterValue("fileFormat");
//      simpleParameterValue.setStringValue("txt");
//      parameters.add(simpleParameterValue);
//      ReportHolder report = new ReportHolder();
//
//      importLine.doImport(parameters, report);
//      Assert.fail("expected exception not raised");
//   }
//
//   //@Test (groups = {"CheckParameters"}, description = "Import Plugin should reject file not found",dependsOnMethods={"getBean"})
//   public void verifyCheckinputFileExists() throws ChouetteException
//   {
//      List<ParameterValue> parameters = new ArrayList<ParameterValue>();
//      SimpleParameterValue simpleParameterValue = new SimpleParameterValue("inputFile");
//      simpleParameterValue.setFilepathValue(path+"/dummyFile.tmp");
//      parameters.add(simpleParameterValue);
//      simpleParameterValue = new SimpleParameterValue("fileFormat");
//      simpleParameterValue.setStringValue("xml");
//      parameters.add(simpleParameterValue);
//      ReportHolder report = new ReportHolder();
//
//      List<Line> lines = importLine.doImport(parameters, report);
//      Assert.assertNull(lines,"lines must be null");
//      List<ReportItem> items = report.getReport().getItems();
//      printReport(report.getReport());
//      boolean found = false;
//      for (ReportItem reportItem : items) 
//      {
//         if (reportItem.getMessageKey().equals("FILE_ERROR")) found = true;
//      }
//      Assert.assertTrue(found,"FILE_ERROR must be found in report");
//
//   }
//
//   @Test (groups = {"CheckParameters"}, description = "Import Plugin should return format description",dependsOnMethods={"getBean"})
//   public void verifyFormatDescription()
//   {
//      FormatDescription description = importLine.getDescription();
//      List<ParameterDescription> params = description.getParameterDescriptions();
//
//      Assert.assertEquals(description.getName(), "NEPTUNE");
//      Assert.assertNotNull(params,"params should not be null");
//      Assert.assertEquals(params.size(), 3," params size must equal 3");
//      logger.info("Description \n "+description.toString());
//      Reporter.log("Description \n "+description.toString());
//
//   }


   @Test (groups = {"ExportLine"}, description = "Export Plugin should export file",dependsOnMethods={"getBean","getNeptuneFile"})
   public void verifyExportLine() throws ChouetteException
   {

      List<Line> lines = null;
      // import data
      {
      List<ParameterValue> parameters = new ArrayList<ParameterValue>();
      SimpleParameterValue simpleParameterValue = new SimpleParameterValue("inputFile");
      simpleParameterValue.setFilepathValue(path+"/"+neptuneFile);
      parameters.add(simpleParameterValue);

      ReportHolder report = new ReportHolder();
      lines = importLine.doImport(parameters, report);
      }
      // export data
      {
         List<ParameterValue> parameters = new ArrayList<ParameterValue>();
         SimpleParameterValue simpleParameterValue = new SimpleParameterValue("outputFile");
         simpleParameterValue.setFilepathValue(targetPath+"/"+neptuneFile);
         parameters.add(simpleParameterValue);

         ReportHolder report = new ReportHolder();
         exportLine.doExport(lines,parameters, report);
      }
      List<ParameterValue> parameters = new ArrayList<ParameterValue>();
      SimpleParameterValue simpleParameterValue = new SimpleParameterValue("inputFile");
      simpleParameterValue.setFilepathValue(targetPath+"/"+neptuneFile);
      parameters.add(simpleParameterValue);

      ReportHolder report = new ReportHolder();
      lines = importLine.doImport(parameters, report);
      printReport(report.getReport());    

      Assert.assertNotNull(lines,"lines can't be null");
      Assert.assertEquals(lines.size(), 1,"lines size must equals 1");
      for(Line line : lines)
      {
         Set<Facility> facilities = new HashSet<Facility>();
         // comptage des objets : 
         Assert.assertNotNull(line.getPtNetwork(),"line must have a network");
         Assert.assertNotNull(line.getGroupOfLines(),"line must have groupOfLines");
         Assert.assertEquals(line.getGroupOfLines().size(),1,"line must have 1 groupOfLine");
         Assert.assertNotNull(line.getCompany(),"line must have a company");
         Assert.assertNotNull(line.getRoutes(),"line must have routes");
         Assert.assertEquals(line.getRoutes().size(),4,"line must have 4 routes");
         Set<StopArea> bps = new HashSet<StopArea>();
         Set<StopArea> comms = new HashSet<StopArea>();

         if (line.getFacilities() != null)
            facilities.addAll(line.getFacilities());
         for (Route route : line.getRoutes())
         {
            Assert.assertNotNull(route.getJourneyPatterns(),"line routes must have journeyPattens");
            for (JourneyPattern jp : route.getJourneyPatterns())
            {
               Assert.assertNotNull(jp.getStopPoints(),"line journeyPattens must have stoppoints");
               for (StopPoint point : jp.getStopPoints())
               {
                  if (point.getFacilities() != null)
                     facilities.addAll(point.getFacilities());

                  Assert.assertNotNull(point.getContainedInStopArea(),"stoppoints must have StopAreas");
                  bps.add(point.getContainedInStopArea());
                  
                  Assert.assertNotNull(point.getContainedInStopArea().getParent(),"StopAreas must have parent : "+point.getContainedInStopArea().getObjectId());
                  comms.add(point.getContainedInStopArea().getParent());
               }
            }
         }
         Assert.assertEquals(bps.size(),18,"line must have 18 boarding positions");
         Assert.assertEquals(comms.size(),9,"line must have 9 commercial stop points");

         Set<ConnectionLink> clinks = new HashSet<ConnectionLink>();
         Set<AccessLink> alinks = new HashSet<AccessLink>();

         for (StopArea bp : bps)
         {
            if (bp.getFacilities() != null)
               facilities.addAll(bp.getFacilities());
         }
         
         for (StopArea comm : comms)
         {
            if (comm.getFacilities() != null)
               facilities.addAll(comm.getFacilities());

            if (comm.getConnectionLinks() != null)
            {
               clinks.addAll(comm.getConnectionLinks());
            }
            if (comm.getAccessLinks() != null)
            {
               alinks.addAll(comm.getAccessLinks());
            }
         }
         Assert.assertEquals(clinks.size(),2,"line must have 2 connection link");
         Calendar c = Calendar.getInstance();
         for (ConnectionLink connectionLink : clinks)
         {
            if (connectionLink.getFacilities() != null)
               facilities.addAll(connectionLink.getFacilities());
            
            c.setTimeInMillis(connectionLink.getDefaultDuration().getTime());
            int minutes = c.get(Calendar.MINUTE) ; 
            int hours = c.get(Calendar.HOUR_OF_DAY) ; 
            int seconds = c.get(Calendar.SECOND) + minutes* 60 + hours * 3600; 

            Assert.assertEquals(seconds,600,"line must have links duration of 10 minutes");
            Reporter.log(connectionLink.toString("\t",1));

         }
         Assert.assertEquals(alinks.size(),1,"line must have 1 access link");
         
         Set<AccessPoint> apoints = new HashSet<AccessPoint>();

         for (AccessLink accessLink : alinks)
         {
            c.setTimeInMillis(accessLink.getDefaultDuration().getTime());
            int minutes = c.get(Calendar.MINUTE) ; 
            int hours = c.get(Calendar.HOUR_OF_DAY) ; 
            int seconds = c.get(Calendar.SECOND) + minutes* 60 + hours * 3600; 

            Assert.assertEquals(seconds,600,"line must have links duration of 10 minutes");
            Reporter.log(accessLink.toString("\t",1));
            apoints.add(accessLink.getAccessPoint());

         }
         Assert.assertEquals(apoints.size(),1,"line must have 1 access point");
         for (AccessPoint accessPoint : apoints)
         {
            c.setTimeInMillis(accessPoint.getOpenningTime().getTime());
            int minutes = c.get(Calendar.MINUTE) ; 
            int hours = c.get(Calendar.HOUR_OF_DAY) ; 
            int seconds = c.get(Calendar.SECOND) + minutes* 60 + hours * 3600; 

            Assert.assertEquals(seconds,6*3600,"line must have opening time of 6 hours");
            c.setTimeInMillis(accessPoint.getClosingTime().getTime());
             minutes = c.get(Calendar.MINUTE) ; 
             hours = c.get(Calendar.HOUR_OF_DAY) ; 
             seconds = c.get(Calendar.SECOND) + minutes* 60 + hours * 3600; 

            Assert.assertEquals(seconds,23*3600,"line must have opening time of 23 hours");

         }
         Assert.assertEquals(facilities.size(),1,"line must have 1 facility");
         for (Facility facility : facilities)
         {
            Assert.assertNotNull(facility.getFacilityFeatures(),"Facility must have features : "+facility.getObjectId());
            Assert.assertEquals(facility.getFacilityFeatures().size(),1,"Facility must have 1 feature : "+facility.getObjectId());
            for (FacilityFeature feature : facility.getFacilityFeatures())
            {
               Assert.assertNotNull(feature.getChoiceValue(),"feature must have choice");
               Assert.assertEquals(feature.getAccessFacility(), AccessFacilityEnumeration.BARRIER,"feature must be BARRIER");
            } 
         }

      }
   }



   private void printReport(Report report)
   {
      if (report == null)
      {
         Reporter.log("no report");
      }
      else
      {
         Reporter.log(report.getStatus().name()+" : "+report.getLocalizedMessage());
         printItems("   ",report.getItems());
      }
   }

   /**
    * @param indent
    * @param items
    */
   private void printItems(String indent,List<ReportItem> items) 
   {
      if (items == null) return;
      for (ReportItem item : items) 
      {
         Reporter.log(indent+item.getStatus().name()+" : "+item.getLocalizedMessage());
         printItems(indent+"   ",item.getItems());
      }

   }


}
