package fr.certu.chouette.exchange.csv;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.Assert;
import org.testng.Reporter;
import org.testng.annotations.Test;

import fr.certu.chouette.common.ChouetteException;
import fr.certu.chouette.model.neptune.Company;
import fr.certu.chouette.model.neptune.JourneyPattern;
import fr.certu.chouette.model.neptune.Line;
import fr.certu.chouette.model.neptune.PTNetwork;
import fr.certu.chouette.model.neptune.Route;
import fr.certu.chouette.model.neptune.StopArea;
import fr.certu.chouette.model.neptune.StopPoint;
import fr.certu.chouette.model.neptune.Timetable;
import fr.certu.chouette.model.neptune.VehicleJourney;
import fr.certu.chouette.model.neptune.type.DayTypeEnum;
import fr.certu.chouette.model.neptune.type.PTDirectionEnum;
import fr.certu.chouette.model.neptune.type.TransportModeNameEnum;
import fr.certu.chouette.plugin.exchange.FormatDescription;
import fr.certu.chouette.plugin.exchange.IImportPlugin;
import fr.certu.chouette.plugin.exchange.ParameterDescription;
import fr.certu.chouette.plugin.exchange.ParameterValue;
import fr.certu.chouette.plugin.exchange.SimpleParameterValue;
import fr.certu.chouette.plugin.report.Report;
import fr.certu.chouette.plugin.report.ReportHolder;
import fr.certu.chouette.plugin.report.ReportItem;

@ContextConfiguration(locations={"classpath:testContext.xml","classpath*:chouetteContext.xml"})
@SuppressWarnings("unchecked")
public class CSVImportTests extends AbstractTestNGSpringContextTests
{
	private static final Logger logger = Logger.getLogger(CSVImportTests.class);

	private IImportPlugin<Line> importLine = null;
	private String path="src/test/data/";

	@Test(groups={"ImportLine","CheckParameters"}, description="Get a bean from context")
	public void getBean()
	{
		importLine = (IImportPlugin<Line>) applicationContext.getBean("CSVLineImport") ;
	}


	@Test (groups = {"CheckParameters"}, description = "Import Plugin should reject wrong file extension",dependsOnMethods={"getBean"},expectedExceptions={IllegalArgumentException.class})
	public void verifyCheckFileExtension() throws ChouetteException
	{
		List<ParameterValue> parameters = new ArrayList<ParameterValue>();
		SimpleParameterValue simpleParameterValue = new SimpleParameterValue("inputFile");
		simpleParameterValue.setFilepathValue(path+"/dummyFile.tmp");
		parameters.add(simpleParameterValue);
		ReportHolder report = new ReportHolder();

		importLine.doImport(parameters, report,null);
		Assert.fail("expected exception not raised");
	}

	@Test (groups = {"CheckParameters"}, description = "Import Plugin should reject unknown parameter",dependsOnMethods={"getBean"},expectedExceptions={IllegalArgumentException.class})
	public void verifyCheckUnknownParameter() throws ChouetteException
	{
		List<ParameterValue> parameters = new ArrayList<ParameterValue>();
		SimpleParameterValue simpleParameterValue = new SimpleParameterValue("inputFile");
		simpleParameterValue.setFilepathValue(path+"/dummyFile.csv");
		parameters.add(simpleParameterValue);
		simpleParameterValue = new SimpleParameterValue("dummyParameter");
		simpleParameterValue.setStringValue("dummy value");
		parameters.add(simpleParameterValue);
		ReportHolder report = new ReportHolder();

		importLine.doImport(parameters, report,null);
		Assert.fail("expected exception not raised");
	}


	@Test (groups = {"CheckParameters"}, description = "Import Plugin should reject wrong file type",dependsOnMethods={"getBean"},expectedExceptions={IllegalArgumentException.class})
	public void verifyCheckFileType() throws ChouetteException
	{
		List<ParameterValue> parameters = new ArrayList<ParameterValue>();
		SimpleParameterValue simpleParameterValue = new SimpleParameterValue("inputFile");
		simpleParameterValue.setFilepathValue(path+"/dummyFile.tmp");
		parameters.add(simpleParameterValue);
		simpleParameterValue = new SimpleParameterValue("fileFormat");
		simpleParameterValue.setStringValue("xml");
		parameters.add(simpleParameterValue);
		ReportHolder report = new ReportHolder();

		importLine.doImport(parameters, report,null);
		Assert.fail("expected exception not raised");
	}

	//@Test (groups = {"CheckParameters"}, description = "Import Plugin should reject file not found",dependsOnMethods={"getBean"})
	public void verifyCheckinputFileExists() throws ChouetteException
	{
		List<ParameterValue> parameters = new ArrayList<ParameterValue>();
		SimpleParameterValue simpleParameterValue = new SimpleParameterValue("inputFile");
		simpleParameterValue.setFilepathValue(path+"/dummyFile.tmp");
		parameters.add(simpleParameterValue);
		simpleParameterValue = new SimpleParameterValue("fileFormat");
		simpleParameterValue.setStringValue("csv");
		parameters.add(simpleParameterValue);
		ReportHolder report = new ReportHolder();

		List<Line> lines = importLine.doImport(parameters, report,null);
		Assert.assertNull(lines,"lines must be null");
		List<ReportItem> items = report.getReport().getItems();
		printReport(report.getReport());
		boolean found = false;
		for (ReportItem reportItem : items) 
		{
			if (reportItem.getMessageKey().equals("FILE_ERROR")) found = true;
		}
		Assert.assertTrue(found,"FILE_ERROR must be found in report");

	}

	@Test (groups = {"CheckParameters"}, description = "Import Plugin should return format description",dependsOnMethods={"getBean"})
	public void verifyFormatDescription()
	{
		FormatDescription description = importLine.getDescription();
		List<ParameterDescription> params = description.getParameterDescriptions();

		Assert.assertEquals(description.getName(), "CSV");
		Assert.assertNotNull(params,"params should not be null");
		Assert.assertEquals(params.size(), 4," params size must equal 4");
		logger.info("Description \n "+description.toString());
		Reporter.log("Description \n "+description.toString());

	}


	@Test (groups = {"ImportLine"}, description = "Import Plugin should import file",dependsOnMethods={"getBean"})
	public void verifyImportLine() throws ChouetteException
	{

		List<ParameterValue> parameters = new ArrayList<ParameterValue>();
		{
			SimpleParameterValue simpleParameterValue = new SimpleParameterValue("inputFile");
			simpleParameterValue.setFilepathValue(path+"/test_ok.csv");
			parameters.add(simpleParameterValue);
		}
		{
			SimpleParameterValue simpleParameterValue = new SimpleParameterValue("objectIdPrefix");
			simpleParameterValue.setStringValue("CSV");
			parameters.add(simpleParameterValue);
		}

		ReportHolder report = new ReportHolder();

		List<Line> lines = importLine.doImport(parameters, report,null);

		printReport(report.getReport());    

		Assert.assertNotNull(lines,"lines can't be null");
		Assert.assertEquals(lines.size(), 1,"lines size must equals 1");
		int l = 0;
		for (Line line : lines)
		{
			l++;
			assertLine(line,l);
			// comptage des objets : 
			Set<StopArea> bps = new HashSet<StopArea>();
			Set<StopArea> comms = new HashSet<StopArea>();
			Set<Timetable> tms = new HashSet<Timetable>();
			assertNetwork(line.getPtNetwork());
			assertCompany(line.getCompany());

			int r = 0;
			for (Route route : line.getRoutes())
			{
				r++;
				assertRoute(route,l,r);
				int j=0;
				int v=0;
				int p=0;
				for (StopPoint point : route.getStopPoints())
				{
					p++;
					assertStopPoint(point,l, r, route.getWayBack(), p);
					bps.add(point.getContainedInStopArea());

					Assert.assertNotNull(point.getContainedInStopArea().getParent(),"StopAreas must have parent : "+point.getContainedInStopArea().getObjectId());
					comms.add(point.getContainedInStopArea().getParent());
				}

				for (JourneyPattern jp : route.getJourneyPatterns())
				{
					j++;
					assertJourneyPattern(jp, l, r, route.getWayBack(), j);

					for (VehicleJourney vj : jp.getVehicleJourneys())
					{
						v++;
						assertVehicleJourney(vj, l, r, route.getWayBack(), v);
						tms.addAll(vj.getTimetables());
					}
				}
			}
			Assert.assertEquals(bps.size(),8,"line must have 8 boarding positions");
			for (StopArea area : bps) 
			{
				assertBoarding(area);
			}

			Assert.assertEquals(comms.size(),4,"line must have 4 commercial stop points");
			for (StopArea area : comms) 
			{
				assertCommercial(area);
			}
			Assert.assertEquals(tms.size(),2,"line must have 2 timetables");
			for (Timetable tm : tms) 
			{
				assertTimetable(tm);
			}

			Reporter.log(line.toString("\t",1));
		}

	}


	private void assertTimetable(Timetable tm) 
	{
		Assert.assertEquals(tm.getObjectId(), "CSV:Timetable:"+tm.getVersion(),"Time Table must have correct objectId");
		Assert.assertNotNull(tm.getComment(),"Time Table must have comment");
		if (!tm.getPeriods().isEmpty())
		{
			Assert.assertEquals(tm.getPeriods().size(), 2," when tm has periods, 2 periods are expected");
			int expectedDayType = Timetable.buildDayTypeMask(Arrays.asList(new DayTypeEnum[]{DayTypeEnum.Monday,DayTypeEnum.Wednesday}));
			Assert.assertEquals(tm.getIntDayTypes(), Integer.valueOf(expectedDayType), "when tm has periods, day type expected for Monday and Wednesday");
		}
		if (!tm.getCalendarDays().isEmpty())
		{
			Assert.assertEquals(tm.getCalendarDays().size(), 8," when tm has dates, 8 dates are expected");
			Assert.assertNull(tm.getIntDayTypes(),  "when tm has dates, no day type expected");			
		}

	}

	private void assertArea(StopArea area,String prefix) 
	{
		Assert.assertNotNull(area.getName(),"StopArea must have name");
		Assert.assertNotNull(area.getCountryCode(),"StopArea must have country code");
		String oid = prefix+area.getName()+"_"+area.getCountryCode();
		Assert.assertEquals(area.getObjectId(), "CSV:StopArea:"+toIdString(oid),"StopArea must have correct objectId");
		Assert.assertTrue(area.hasCoordinates(),"StopArea must have coordinates");

	}
	
	private void assertCommercial(StopArea area) 
	{
		assertArea(area, "C_");

	}


	private void assertBoarding(StopArea area) 
	{
		assertArea(area, "BP_");

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

	/**
	 * 
	 */
	private void assertNetwork(PTNetwork network)
	{
		Assert.assertEquals(network.getObjectId(),"CSV:GroupOfLine:1234","network must have correct objectId");
		Assert.assertEquals(network.getName(),"Réseau 1","network must have correct name");
		Assert.assertEquals(network.getDescription(),"Réseau de test CSV","network must have correct comment");
		Assert.assertEquals(network.getRegistrationNumber(),"1234","network must have correct registration number");
	}

	/**
	 * 
	 */
	private void assertCompany(Company company)
	{
		Assert.assertEquals(company.getObjectId(),"CSV:Company:4321","company must have correct objectId");
		Assert.assertEquals(company.getName(),"Transporteur 1","company must have correct name");
		Assert.assertEquals(company.getShortName(),"un","company must have correct short name");
		Assert.assertEquals(company.getCode(),"75000","company must have correct zip code");
		Assert.assertEquals(company.getOrganisationalUnit(),"Transporteur de test CSV","company must have correct Description");
		Assert.assertEquals(company.getPhone(),"01.02.03.04.05","company must have correct phone number");
		Assert.assertEquals(company.getFax(),"01.03.05.07.09","company must have correct fax number");
		Assert.assertEquals(company.getEmail(),"transport@transport.fr","company must have correct email");
		Assert.assertEquals(company.getRegistrationNumber(),"4321","company must have correct registration number");
	}

	/**
	 * 
	 */
	private void assertLine(Line line, int rank)
	{
		Assert.assertEquals(line.getObjectId(),"CSV:Line:ABCD"+rank,"line must have correct objectId");
		Assert.assertEquals(line.getName(),"Ligne CSV "+rank,"line must have correct name");
		Assert.assertEquals(line.getPublishedName(),"Ligne de test CSV "+rank,"line must have correct published name");
		Assert.assertEquals(line.getComment(),"Ligne de test pour le format CSV "+rank,"line must have correct comment");
		Assert.assertEquals(line.getRegistrationNumber(),"ABCD"+rank,"line must have correct registration number");
		Assert.assertEquals(line.getNumber(),"CSV "+rank,"line must have correct number");
		Assert.assertEquals(line.getTransportModeName(), TransportModeNameEnum.Bus,"line must be on Bus mode");
		Assert.assertNotNull(line.getPtNetwork(),"line must have a network");
		Assert.assertTrue(line.getGroupOfLines().isEmpty(),"line must have no groupOfLines");
		Assert.assertNotNull(line.getCompany(),"line must have a company");
		Assert.assertFalse(line.getRoutes().isEmpty(),"line must have routes");
		Assert.assertEquals(line.getRoutes().size(),2,"line must have 2 routes");
	}

	/**
	 * 
	 */
	private void assertRoute(Route route, int line_rank, int rank)
	{
		if (rank % 2 == 1)
		{
			Assert.assertEquals(route.getObjectId(),"CSV:Route:ABCD"+line_rank+"_A","route must have correct objectId");
			Assert.assertEquals(route.getDirection(), PTDirectionEnum.A,"route must be on A direction");
			Assert.assertEquals(route.getWayBack(),"A" ,"route must be on A wayback");
			Assert.assertEquals(route.getName(),"ALLER","route must have correct name");
			Assert.assertEquals(route.getWayBackRouteId(),"CSV:Route:ABCD"+line_rank+"_R" ,"route must have correct waybackId");
		}
		else
		{
			Assert.assertEquals(route.getObjectId(),"CSV:Route:ABCD"+line_rank+"_R","route must have correct objectId");
			Assert.assertEquals(route.getDirection(), PTDirectionEnum.R,"route must be on R direction");    	   
			Assert.assertEquals(route.getWayBack(),"R" ,"route must be on R wayback");
			Assert.assertEquals(route.getName(),"RETOUR","route must have correct name");
			Assert.assertEquals(route.getWayBackRouteId(),"CSV:Route:ABCD"+line_rank+"_A" ,"route must have correct waybackId");
		}
		Assert.assertNotNull(route.getJourneyPatterns(),"line routes must have journeyPattens");
	}

	/**
	 * 
	 */
	private void assertJourneyPattern(JourneyPattern jp, int line_rank, int route_rank,String route_orientation, int rank)
	{
		Assert.assertEquals(jp.getObjectId(),"CSV:JourneyPattern:ABCD"+line_rank+"_"+route_orientation+"_"+rank,"journeyPattern must have correct objectId");
		Assert.assertNull(jp.getName(),"journeyPattern must have no name");
		Assert.assertNotNull(jp.getStopPoints(),"line journeyPattens must have stoppoints");
		Assert.assertEquals(jp.getStopPoints().size(),4,"line journeyPattens must have 4 stopPoints");
		Assert.assertNotNull(jp.getVehicleJourneys(),"line journeyPattens must have vehicleJourneys");
		Assert.assertEquals(jp.getVehicleJourneys().size(),2,"line journeyPattens must have 2 vehicleJourneys");
	}

	/**
	 * 
	 */
	private void assertVehicleJourney(VehicleJourney vj, int line_rank, int route_rank,String route_orientation, int rank)
	{
		Assert.assertEquals(vj.getObjectId(),"CSV:VehicleJourney:ABCD"+line_rank+"_"+route_orientation+"_"+rank,"vehicleJourney must have correct objectId");
		Assert.assertNotNull(vj.getVehicleJourneyAtStops(),"line vehicleJourneys must have vehicleJourneyAtStops");
		Assert.assertEquals(vj.getVehicleJourneyAtStops().size(),4,"line vehicleJourneys must have 4 vehicleJourneyAtStops");
		Assert.assertNotNull(vj.getTimetables(),"line vehicleJourneys must have timetables");
		Assert.assertEquals(vj.getTimetables().size(),1,"line vehicleJourneys must have 1 tiemtable");
	}

	/**
	 * 
	 */
	private void assertStopPoint(StopPoint stop, int line_rank, int route_rank,String route_orientation, int rank)
	{
		Assert.assertEquals(stop.getObjectId(),"CSV:StopPoint:ABCD"+line_rank+"_"+route_orientation+"_"+rank,"StopPoint must have correct objectId");
		Assert.assertNotNull(stop.getContainedInStopArea(),"StopPoint must have contained in StopArea");
	}

	protected String toIdString(String input)
	{
		String output = input.replaceAll("[^0-9A-Za-z_\\-]", "_");
		return output;
	}


}
