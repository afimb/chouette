package fr.certu.chouette.exchange.xml.neptune;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
import fr.certu.chouette.model.neptune.type.ChouetteAreaEnum;
import fr.certu.chouette.model.neptune.type.facility.AccessFacilityEnumeration;
import fr.certu.chouette.model.neptune.type.facility.FacilityFeature;
import fr.certu.chouette.plugin.exchange.FormatDescription;
import fr.certu.chouette.plugin.exchange.IImportPlugin;
import fr.certu.chouette.plugin.exchange.ParameterDescription;
import fr.certu.chouette.plugin.exchange.ParameterValue;
import fr.certu.chouette.plugin.exchange.SimpleParameterValue;
import fr.certu.chouette.plugin.report.Report;
import fr.certu.chouette.plugin.report.ReportHolder;
import fr.certu.chouette.plugin.report.ReportItem;
import fr.certu.chouette.plugin.validation.report.CheckPointReportItem;
import fr.certu.chouette.plugin.validation.report.DetailReportItem;

@ContextConfiguration(locations={"classpath:testContext.xml","classpath*:chouetteContext.xml"})
@SuppressWarnings("unchecked")
public class NeptuneImportTests extends AbstractTestNGSpringContextTests
{

	private IImportPlugin<Line> importLine = null;
	private String path="src/test/resources/";

	@Test(groups={"ImportLine","ImportUtf8Line","ImportUtf8BomLine","ImportBadEncLine","ImportRCLine","ImportZipLines","CheckParameters"}, description="Get a bean from context")
	public void getBean()
	{
		importLine = (IImportPlugin<Line>) applicationContext.getBean("NeptuneLineImport") ;
	}


	@Test (groups = {"CheckParameters"}, description = "Import Plugin should reject wrong file extension",dependsOnMethods={"getBean"},expectedExceptions={IllegalArgumentException.class})
	public void verifyCheckFileExtension() throws ChouetteException
	{
		List<ParameterValue> parameters = new ArrayList<ParameterValue>();
		SimpleParameterValue simpleParameterValue = new SimpleParameterValue("inputFile");
		simpleParameterValue.setFilepathValue(path+"/dummyFile.tmp");
		parameters.add(simpleParameterValue);
		ReportHolder ireport = new ReportHolder();
		ReportHolder vreport = new ReportHolder();

		importLine.doImport(parameters, ireport,vreport);
		Assert.fail("expected exception not raised");
	}

	@Test (groups = {"CheckParameters"}, description = "Import Plugin should reject unknown parameter",dependsOnMethods={"getBean"},expectedExceptions={IllegalArgumentException.class})
	public void verifyCheckUnknownParameter() throws ChouetteException
	{
		List<ParameterValue> parameters = new ArrayList<ParameterValue>();
		SimpleParameterValue simpleParameterValue = new SimpleParameterValue("inputFile");
		simpleParameterValue.setFilepathValue(path+"/dummyFile.xml");
		parameters.add(simpleParameterValue);
		simpleParameterValue = new SimpleParameterValue("dummyParameter");
		simpleParameterValue.setStringValue("dummy value");
		parameters.add(simpleParameterValue);
		ReportHolder ireport = new ReportHolder();
		ReportHolder vreport = new ReportHolder();

		importLine.doImport(parameters, ireport,vreport);
		Assert.fail("expected exception not raised");
	}

	@Test (groups = {"CheckParameters"}, description = "Import Plugin should reject missing mandatory parameter",dependsOnMethods={"getBean"},expectedExceptions={IllegalArgumentException.class})
	public void verifyCheckMandatoryParameter() throws ChouetteException
	{
		List<ParameterValue> parameters = new ArrayList<ParameterValue>();
		SimpleParameterValue simpleParameterValue = new SimpleParameterValue("validate");
		simpleParameterValue.setBooleanValue(true);
		parameters.add(simpleParameterValue);
		ReportHolder ireport = new ReportHolder();
		ReportHolder vreport = new ReportHolder();

		importLine.doImport(parameters, ireport,vreport);
		Assert.fail("expected exception not raised");
	}


	@Test (groups = {"CheckParameters"}, description = "Import Plugin should reject wrong file type",dependsOnMethods={"getBean"},expectedExceptions={IllegalArgumentException.class})
	public void verifyCheckFileType() throws ChouetteException
	{
		List<ParameterValue> parameters = new ArrayList<ParameterValue>();
		SimpleParameterValue simpleParameterValue = new SimpleParameterValue("inputFile");
		simpleParameterValue.setFilepathValue(path+"/dummyFile.xml");
		parameters.add(simpleParameterValue);
		simpleParameterValue = new SimpleParameterValue("fileFormat");
		simpleParameterValue.setStringValue("txt");
		parameters.add(simpleParameterValue);
		ReportHolder ireport = new ReportHolder();
		ReportHolder vreport = new ReportHolder();

		importLine.doImport(parameters, ireport,vreport);
		Assert.fail("expected exception not raised");
	}

	@Test (groups = {"CheckParameters"}, description = "Import Plugin should reject file not found",dependsOnMethods={"getBean"})
	public void verifyCheckInputFileExists() throws ChouetteException
	{
		List<ParameterValue> parameters = new ArrayList<ParameterValue>();
		SimpleParameterValue simpleParameterValue = new SimpleParameterValue("inputFile");
		simpleParameterValue.setFilepathValue(path+"/dummyFile.tmp");
		parameters.add(simpleParameterValue);
		simpleParameterValue = new SimpleParameterValue("fileFormat");
		simpleParameterValue.setStringValue("xml");
		parameters.add(simpleParameterValue);
		ReportHolder ireport = new ReportHolder();
		ReportHolder vreport = new ReportHolder();

		List<Line> lines = importLine.doImport(parameters, ireport,vreport);
		Assert.assertNull(lines,"lines must be null");
		List<ReportItem> items = ireport.getReport().getItems();
		printReport(ireport.getReport());
		boolean found = false;
		for (ReportItem reportItem : items) 
		{
			if (reportItem.getMessageKey().equals("FILE_ERROR")) found = true;
			if (reportItem.hasItems())
			{
				for (ReportItem detail : reportItem.getItems())
				{
					if (detail.getMessageKey().equals("FILE_ERROR")) found = true;
				}
			}
		}
		Assert.assertTrue(found,"FILE_ERROR must be found in report");

	}

	@Test (groups = {"CheckParameters"}, description = "Import Plugin should return format description",dependsOnMethods={"getBean"})
	public void verifyFormatDescription()
	{
		FormatDescription description = importLine.getDescription();
		List<ParameterDescription> params = description.getParameterDescriptions();

		Assert.assertEquals(description.getName(), "NEPTUNE");
		Assert.assertNotNull(params,"params should not be null");
		Assert.assertEquals(params.size(), 6," params size must equal 6");
		Reporter.log("Description \n "+description.toString());

	}

	@Parameters({"neptuneFileUtf8"})
	@Test (groups = {"ImportLineUtf8"}, description = "Import Plugin should detect file encoding",dependsOnMethods={"getBean"})
	public void verifyCheckGoodEncoding(String neptuneFileUtf8) throws ChouetteException
	{
		List<ParameterValue> parameters = new ArrayList<ParameterValue>();
		SimpleParameterValue simpleParameterValue = new SimpleParameterValue("inputFile");
		simpleParameterValue.setFilepathValue(path+"/"+neptuneFileUtf8);
		parameters.add(simpleParameterValue);
		simpleParameterValue = new SimpleParameterValue("fileFormat");
		simpleParameterValue.setStringValue("xml");
		parameters.add(simpleParameterValue);
		ReportHolder ireport = new ReportHolder();
		ReportHolder vreport = new ReportHolder();

		List<Line> lines = importLine.doImport(parameters, ireport,vreport);
		Assert.assertNotNull(lines,"lines must not be null");
		Line line = lines.get(0);
		Assert.assertTrue(line.getName().endsWith("é"));
		printReport(ireport.getReport());
	}

	@Parameters({"neptuneFileUtf8Bom"})
	@Test (groups = {"ImportLineUtf8Bom"}, description = "Import Plugin should detect bom in file encoding",dependsOnMethods={"getBean"})
	public void verifyCheckGoodEncodingWithBom(String neptuneFileUtf8Bom) throws ChouetteException
	{
		List<ParameterValue> parameters = new ArrayList<ParameterValue>();
		SimpleParameterValue simpleParameterValue = new SimpleParameterValue("inputFile");
		simpleParameterValue.setFilepathValue(path+"/"+neptuneFileUtf8Bom);
		parameters.add(simpleParameterValue);
		simpleParameterValue = new SimpleParameterValue("fileFormat");
		simpleParameterValue.setStringValue("xml");
		parameters.add(simpleParameterValue);
		ReportHolder ireport = new ReportHolder();
		ReportHolder vreport = new ReportHolder();

		List<Line> lines = importLine.doImport(parameters, ireport,vreport);
		Assert.assertNotNull(lines,"lines must not be null");
		Line line = lines.get(0);
		Assert.assertTrue(line.getName().endsWith("é"));
		printReport(ireport.getReport());
	}

	@Parameters({"neptuneFileBadEnc"})
	@Test (groups = {"ImportLineBadEnc"}, description = "Import Plugin should detect file encoding",dependsOnMethods={"getBean"})
	public void verifyCheckBadEncoding(String neptuneFileBadEnc) throws ChouetteException
	{
		List<ParameterValue> parameters = new ArrayList<ParameterValue>();
		SimpleParameterValue simpleParameterValue = new SimpleParameterValue("inputFile");
		simpleParameterValue.setFilepathValue(path+"/"+neptuneFileBadEnc);
		parameters.add(simpleParameterValue);
		simpleParameterValue = new SimpleParameterValue("fileFormat");
		simpleParameterValue.setStringValue("xml");
		parameters.add(simpleParameterValue);
		ReportHolder ireport = new ReportHolder();
		ReportHolder vreport = new ReportHolder();

		List<Line> lines = importLine.doImport(parameters, ireport,vreport);
		Assert.assertNull(lines,"lines must be null");
		printReport(ireport.getReport());
		Assert.assertEquals(ireport.getReport().getStatus(), Report.STATE.ERROR,"report status must be ERROR");
	}

	@Parameters({"neptuneFile"})
	@Test (groups = {"ImportLine"}, description = "Import Plugin should import file",dependsOnMethods={"getBean"})
	public void verifyImportLine(String neptuneFile) throws ChouetteException
	{

		List<ParameterValue> parameters = new ArrayList<ParameterValue>();
		SimpleParameterValue simpleParameterValue = new SimpleParameterValue("inputFile");
		simpleParameterValue.setFilepathValue(path+"/"+neptuneFile);
		parameters.add(simpleParameterValue);

		ReportHolder ireport = new ReportHolder();
		ReportHolder vreport = new ReportHolder();

		List<Line> lines = importLine.doImport(parameters, ireport,vreport);

		printReport(ireport.getReport());    

		Assert.assertNotNull(lines,"lines can't be null");
		Assert.assertEquals(lines.size(), 1,"lines size must equals 1");
		for (Line line : lines)
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

				Assert.assertEquals(seconds,4200,"line must have links duration of 1 hour and 10 minutes");
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

				Assert.assertEquals(seconds,60,"line must have links duration of 1 minutes");
				Reporter.log(accessLink.toString("\t",1));
				apoints.add(accessLink.getAccessPoint());

			}
			Assert.assertEquals(apoints.size(),1,"line must have 1 access point");
			for (AccessPoint accessPoint : apoints)
			{
				c.setTimeInMillis(accessPoint.getOpeningTime().getTime());
				int minutes = c.get(Calendar.MINUTE) ; 
				int hours = c.get(Calendar.HOUR_OF_DAY) ; 
				int seconds = c.get(Calendar.SECOND) + minutes* 60 + hours * 3600; 

				Assert.assertEquals(seconds,6*3600,"line must have opening time of 6 hours");
				c.setTimeInMillis(accessPoint.getClosingTime().getTime());
				minutes = c.get(Calendar.MINUTE) ; 
				hours = c.get(Calendar.HOUR_OF_DAY) ; 
				seconds = c.get(Calendar.SECOND) + minutes* 60 + hours * 3600; 

				Assert.assertEquals(seconds,22*3600+600,"line must have closing time of 22 hours 10");

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


			Reporter.log(line.toString("\t",1));
		}

	}


	@Parameters({"neptuneRCFile"})
	@Test (groups = {"ImportRCLine"}, description = "Import Plugin should import file with ITL",dependsOnMethods={"getBean"})
	public void verifyImportRCLine(String neptuneRCFile) throws ChouetteException
	{

		List<ParameterValue> parameters = new ArrayList<ParameterValue>();
		SimpleParameterValue simpleParameterValue = new SimpleParameterValue("inputFile");
		simpleParameterValue.setFilepathValue(path+"/"+neptuneRCFile);
		parameters.add(simpleParameterValue);
		SimpleParameterValue simpleParameterValue2 = new SimpleParameterValue("validate");
		simpleParameterValue2.setBooleanValue(false); // file is incomplete in other aspect
		parameters.add(simpleParameterValue2);

		ReportHolder ireport = new ReportHolder();
		ReportHolder vreport = new ReportHolder();

		List<Line> lines = importLine.doImport(parameters, ireport,vreport);

		Assert.assertNotNull(lines,"lines can't be null");
		Assert.assertEquals(lines.size(), 1,"lines size must equals 1");
		for(Line line : lines)
		{
			Reporter.log(line.toString("\t",1));
			Assert.assertNotNull(line.getRoutingConstraints(),"line must have routing constraints");
			Assert.assertEquals(line.getRoutingConstraints().size(), 1,"line must have 1 routing constraint");
			StopArea area = line.getRoutingConstraints().get(0);
			Assert.assertEquals(area.getAreaType(), ChouetteAreaEnum.ITL,"routing constraint area must be of "+ChouetteAreaEnum.ITL+" type");
			Assert.assertNotNull(area.getRoutingConstraintAreas(), "routing constraint area must have stopArea children as routing constraints");
			Assert.assertNull(area.getContainedStopAreas(), "routing constraint area must not have stopArea children");
			Assert.assertNull(area.getParent(), "routing constraint area must not have stopArea parent");
			Assert.assertTrue(area.getRoutingConstraintAreas().size() > 0, "routing constraint area must have stopArea children as routing constraints");
		}
		printReport(ireport.getReport());    
	}


	@Parameters({"neptuneZip"})
	@Test (groups = {"ImportZipLines"}, description = "Import Plugin should import zip file",dependsOnMethods={"getBean"})
	public void verifyImportZipLines(String neptuneZip) throws ChouetteException
	{

		List<ParameterValue> parameters = new ArrayList<ParameterValue>();
		SimpleParameterValue simpleParameterValue = new SimpleParameterValue("inputFile");
		simpleParameterValue.setFilepathValue(path+"/"+neptuneZip);
		parameters.add(simpleParameterValue);

		ReportHolder ireport = new ReportHolder();
		ReportHolder vreport = new ReportHolder();

		List<Line> lines = importLine.doImport(parameters, ireport,vreport);

		Assert.assertNotNull(lines,"lines can't be null");
		Assert.assertEquals(lines.size(), 6,"lines size must equals 6");
		for (Line line : lines)
		{
			Reporter.log(line.toString("\t",0));
		}
		printReport(ireport.getReport());

	}
	@Parameters({"neptuneZip"})
	@Test (groups = {"ImportZipLines"}, description = "Import Plugin should import zip file",dependsOnMethods={"getBean"})
	public void verifyImportZipLinesOptim(String neptuneZip) throws ChouetteException
	{

		List<ParameterValue> parameters = new ArrayList<ParameterValue>();
		{
			SimpleParameterValue simpleParameterValue = new SimpleParameterValue("inputFile");
			simpleParameterValue.setFilepathValue(path+"/"+neptuneZip);
			parameters.add(simpleParameterValue);
		}
		{
			SimpleParameterValue simpleParameterValue = new SimpleParameterValue("optimizeMemory");
			simpleParameterValue.setBooleanValue(true);
			parameters.add(simpleParameterValue);
		}

		ReportHolder ireport = new ReportHolder();
		ReportHolder vreport = new ReportHolder();

		List<Line> lines = importLine.doImport(parameters, ireport,vreport);

		Assert.assertNotNull(lines,"lines can't be null");
		Assert.assertEquals(lines.size(), 6,"lines size must equals 6");
		for (Line line : lines)
		{
			Reporter.log(line.toString("\t",0));
		}
		printReport(ireport.getReport());

	}
//	@Parameters({"neptuneZipErr"})
//	@Test (groups = {"ImportZipLines"}, description = "Import Plugin should import zip file") //,dependsOnMethods={"getBean"})
//	public void verifyImportZipLinesErr(String neptuneZipErr) throws ChouetteException
//	{
//		importLine = (IImportPlugin<Line>) applicationContext.getBean("NeptuneLineImport") ;
//
//		List<ParameterValue> parameters = new ArrayList<ParameterValue>();
//		SimpleParameterValue simpleParameterValue = new SimpleParameterValue("inputFile");
//		simpleParameterValue.setFilepathValue(path+"/"+neptuneZipErr);
//		parameters.add(simpleParameterValue);
//
//		ReportHolder ireport = new ReportHolder();
//		ReportHolder vreport = new ReportHolder();
//
//		List<Line> lines = importLine.doImport(parameters, ireport,vreport);
//		printReport(vreport.getReport());
//		printReport(ireport.getReport());
//
//		Assert.assertNotNull(lines,"lines can't be null");
//		Assert.assertEquals(lines.size(), 7,"lines size must equals 7");
//
//	}

	private void printReport(Report report)
	{
		if (report == null)
		{
			Reporter.log("no report");
		}
		else
		{
			//Reporter.log(report.toJSON());

			Reporter.log(report.getStatus().name()+" : "+report.getLocalizedMessage());
			printItems("---",report.getItems());
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
			if (item instanceof CheckPointReportItem)
			{
				CheckPointReportItem citem = (CheckPointReportItem) item;
				Reporter.log(indent+citem.getStatus().name()+" : "+citem.getMessageKey());

			}
			else if (item instanceof DetailReportItem)
			{
				DetailReportItem ditem = (DetailReportItem) item;
				Reporter.log(indent+ditem.toJSON());
			}
			else
			{
				Reporter.log(indent+item.getStatus().name()+" : "+item.getLocalizedMessage());
			}
			printItems(indent+"---",item.getItems());
		}

	}


}
