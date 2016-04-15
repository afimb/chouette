package mobi.chouette.exchange.regtopp.importer.parser;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.ejb.EJB;
import javax.inject.Inject;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.UserTransaction;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.testng.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.testng.Assert;
import org.testng.annotations.Test;

import mobi.chouette.common.Context;
import mobi.chouette.common.chain.CommandFactory;
import mobi.chouette.dao.LineDAO;
import mobi.chouette.dao.RouteDAO;
import mobi.chouette.dao.VehicleJourneyDAO;
import mobi.chouette.exchange.regtopp.DummyChecker;
import mobi.chouette.exchange.regtopp.JobDataTest;
import mobi.chouette.exchange.regtopp.RegtoppTestUtils;
import mobi.chouette.exchange.regtopp.importer.RegtoppImportParameters;
import mobi.chouette.exchange.regtopp.importer.RegtoppImporterCommand;
import mobi.chouette.exchange.report.ActionReport;
import mobi.chouette.exchange.validation.report.ValidationReport;
import mobi.chouette.model.Footnote;
import mobi.chouette.model.JourneyPattern;
import mobi.chouette.model.Line;
import mobi.chouette.model.Route;
import mobi.chouette.model.StopArea;
import mobi.chouette.model.StopPoint;
import mobi.chouette.model.VehicleJourney;
import mobi.chouette.persistence.hibernate.ContextHolder;

public class RegtopImporterCommandTest extends Arquillian implements mobi.chouette.common.Constant{

	private InitialContext initialContext ;

	
	private void init()
	{
		if (initialContext == null)
		{
			try {
				initialContext = new InitialContext();
		
			} catch (NamingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	
	@EJB
	LineDAO lineDao;

	@EJB
	RouteDAO routeDao;

	@EJB
	VehicleJourneyDAO vjDao;

	@PersistenceContext(unitName = "referential")
	EntityManager em;

	@Inject
	UserTransaction utx;

	@Deployment
	public static WebArchive createDeploymentOld() {

		WebArchive result;

		File[] files = Maven.resolver().loadPomFromFile("pom.xml")
				.resolve("mobi.chouette:mobi.chouette.exchange.regtopp","mobi.chouette:mobi.chouette.dao").
				withTransitivity().asFile();

		result = ShrinkWrap.create(WebArchive.class, "test.war")
				.addAsWebInfResource("postgres-ds.xml")
				.addAsLibraries(files)
				.addClass(DummyChecker.class)
				.addClass(RegtoppTestUtils.class)
				.addClass(JobDataTest.class)
				.addAsResource(EmptyAsset.INSTANCE, "beans.xml");
		return result;

	}


	
	protected Context initImportContext() {
		init();
		ContextHolder.setContext("chouette_gui"); // set tenant schema

		Context context = new Context();
		context.put(INITIAL_CONTEXT, initialContext);
		context.put(REPORT, new ActionReport());
		context.put(MAIN_VALIDATION_REPORT, new ValidationReport());
		RegtoppImportParameters configuration = new RegtoppImportParameters();
		context.put(CONFIGURATION, configuration);
		configuration.setName("name");
		configuration.setUserName("userName");
		configuration.setNoSave(true);
		configuration.setCleanRepository(true);
		configuration.setOrganisationName("organisation");
		configuration.setReferentialName("test");
		JobDataTest jobData = new JobDataTest();
		context.put(JOB_DATA,jobData);
		jobData.setPathName("target/referential/test");
		File f = new File("target/referential/test");
		if (f.exists())
			try {
				FileUtils.deleteDirectory(f);
			} catch (IOException e) {
				e.printStackTrace();
			}
		f.mkdirs();
		jobData.setReferential("chouette_gui");
		jobData.setAction(IMPORTER);
		jobData.setType( "regtopp");
		context.put("testng", "true");
		context.put(OPTIMIZED, Boolean.FALSE);
		return context;

	}
	
	@Test
	public void importRegtoppKolumbusLine2306() throws Exception {
		// Prepare context
		Context context = initImportContext();

		File f = new File("src/test/data/kolumbus/line2306.zip");
		File dest = new File("target/referential/test");
		FileUtils.copyFileToDirectory(f, dest);
		JobDataTest job = (JobDataTest) context.get(JOB_DATA);
		job.setInputFilename(f.getName());

		RegtoppImporterCommand command = (RegtoppImporterCommand) CommandFactory.create(initialContext, RegtoppImporterCommand.class.getName());
		
		RegtoppImportParameters parameters = (RegtoppImportParameters) context.get(CONFIGURATION);
		parameters.setObjectIdPrefix("TST");
		parameters.setReferencesType("line");
		parameters.setNoSave(false);

		
		boolean result = command.execute(context);

		ActionReport report = (ActionReport) context.get(REPORT);
		ValidationReport validationReport = (ValidationReport) context.get(VALIDATION_REPORT);
		

		// TODO create asserts on ActionReport and ValidationReport
		
//		Assert.assertEquals(report.getResult(), STATUS_OK, "result");
//		Assert.assertEquals(report.getFiles().size(), 1, "file reported");
//		Assert.assertEquals(report.getLines().size(), 1, "line reported");
//		Reporter.log("report line :" + report.getLines().get(0).toString(), true);
//		Assert.assertEquals(report.getLines().get(0).getStatus(), LINE_STATE.OK, "line status");
//		RegtoppTestUtils.checkLine(context);
//		
//		Referential referential = (Referential) context.get(REFERENTIAL);
//		Assert.assertNotEquals(referential.getTimetables(),0, "timetables" );
//		Assert.assertNotEquals(referential.getSharedTimetables(),0, "shared timetables" );

		// line should be saved
		utx.begin();
		em.joinTransaction();
		Line line = lineDao.findByObjectId("TST:Line:2306");
		

		Assert.assertNotNull(line,"Line not found");
		Assert.assertNotNull(line.getNetwork(), "line must have a network");
		Assert.assertNotNull(line.getCompany(), "line must have a company");
		Assert.assertNotNull(line.getRoutes(), "line must have routes");
		Assert.assertEquals(line.getRoutes().size(), 9, "number of routes");
		Set<StopArea> bps = new HashSet<StopArea>();

		int numStopPoints = 0;
		int numVehicleJourneys = 0;
		int numJourneyPatterns = 0;
		
		for (Route route : line.getRoutes()) {
			Assert.assertNotNull(route.getName(),"No route name");
			
			
			Assert.assertNotEquals(route.getJourneyPatterns().size(), 0 , "line routes must have journeyPattens");
			for (JourneyPattern jp : route.getJourneyPatterns()) {
				Assert.assertNotNull(jp.getName(),"No journeypattern name");
				Assert.assertNotEquals(jp.getStopPoints().size(), 0, "line journeyPattens must have stoppoints");
				for (StopPoint point : jp.getStopPoints()) {

					numStopPoints++;
					Assert.assertNotNull(point.getContainedInStopArea(), "stoppoints must have StopAreas");
					bps.add(point.getContainedInStopArea());

					Assert.assertNotNull(point.getForAlighting(),"no alighting info StopPoint="+point);
					Assert.assertNotNull(point.getForBoarding(),"no boarding info StopPoint="+point);

				}
				Assert.assertNotEquals(jp.getVehicleJourneys().size(), 0," journeyPattern should have VehicleJourneys");
                for (VehicleJourney vj : jp.getVehicleJourneys()) {
                	Assert.assertNotEquals(vj.getTimetables().size(), 0," vehicleJourney should have timetables");
                	Assert.assertEquals(vj.getVehicleJourneyAtStops().size(), jp.getStopPoints().size()," vehicleJourney should have correct vehicleJourneyAtStop count");
                	numVehicleJourneys++; 
                }
                numJourneyPatterns++;
			}
		}
		
		Assert.assertEquals(numJourneyPatterns, 9,"number of journeyPatterns");
		Assert.assertEquals(numVehicleJourneys, 12,"number of vehicleJourneys");
		Assert.assertEquals(numStopPoints, 411,"number of stopPoints in journeyPattern");
		Assert.assertEquals(bps.size(), 90, "number boarding positions");
	
		// Check opposite routes
		Route outbound = routeDao.findByObjectId("TST:Route:2306103");
		Route inbound = routeDao.findByObjectId("TST:Route:2306203");
		
		Assert.assertNotNull(outbound,"Outbound route not found");
		Assert.assertNotNull(inbound,"Inbound route not found");
		
		Assert.assertNotNull(outbound.getOppositeRoute(),"Oppsite route to outbound not found");
		Assert.assertNotNull(inbound.getOppositeRoute(),"Oppsite route to inbound not found");
		
		Assert.assertTrue(outbound.getOppositeRoute().equals(inbound),"Opposite route incorrect");
		
		utx.rollback();

		if(!result) {
			System.out.println(ToStringBuilder.reflectionToString(report,ToStringStyle.MULTI_LINE_STYLE));
			System.out.println(validationReport);
			
		}
		
		Assert.assertTrue(result,"Importer command execution failed: "+report.getFailure());
	}

	@Test
	public void importRegtoppKolumbusLine5560FootnotesByLine() throws Exception {
		// Prepare context
		Context context = initImportContext();

		File f = new File("src/test/data/kolumbus/line5560.zip");
		File dest = new File("target/referential/test");
		FileUtils.copyFileToDirectory(f, dest);
		JobDataTest job = (JobDataTest) context.get(JOB_DATA);
		job.setInputFilename(f.getName());

		RegtoppImporterCommand command = (RegtoppImporterCommand) CommandFactory.create(initialContext, RegtoppImporterCommand.class.getName());
		
		RegtoppImportParameters parameters = (RegtoppImportParameters) context.get(CONFIGURATION);
		parameters.setObjectIdPrefix("TST");
		parameters.setReferencesType("line");
		parameters.setNoSave(false);

		
		command.execute(context);

		utx.begin();
		em.joinTransaction();
		Line line = lineDao.findByObjectId("TST:Line:5560");

		Assert.assertNotNull(line,"Line not found");
	
		// Check footnotes
		Assert.assertNotNull(line.getFootnotes(),"No footnote lists");
		Assert.assertEquals(line.getFootnotes().size(), 3, "number of line footnotes");
		
		utx.rollback();

	}

	@Test
	public void importRegtoppAtBLine0076FootnotesByVehicleJourney() throws Exception {
		// Prepare context
		Context context = initImportContext();

		File f = new File("src/test/data/atb/line0076.zip");
		File dest = new File("target/referential/test");
		FileUtils.copyFileToDirectory(f, dest);
		JobDataTest job = (JobDataTest) context.get(JOB_DATA);
		job.setInputFilename(f.getName());

		RegtoppImporterCommand command = (RegtoppImporterCommand) CommandFactory.create(initialContext, RegtoppImporterCommand.class.getName());
		
		RegtoppImportParameters parameters = (RegtoppImportParameters) context.get(CONFIGURATION);
		parameters.setObjectIdPrefix("TST");
		parameters.setReferencesType("line");
		parameters.setNoSave(false);

		
		command.execute(context);

		// line should be saved
		utx.begin();
		em.joinTransaction();
		Line line = lineDao.findByObjectId("TST:Line:0076");

		Assert.assertNotNull(line,"Line not found");
	
		// Check footnotes
		Assert.assertNotNull(line.getFootnotes(),"No footnote lists");
		Assert.assertEquals(line.getFootnotes().size(), 1, "number of line footnotes");
		
		// Find vehicle journey
		VehicleJourney vehicleJourney = vjDao.findByObjectId("TST:VehicleJourney:00760015");
		Assert.assertNotNull(vehicleJourney, "VehicleJourney not found");
		List<Footnote> footnotes = vehicleJourney.getFootnotes();
		Assert.assertNotNull(footnotes,"footnotes list null");
		Assert.assertEquals(footnotes.size(),1,"Expected 1 footnote");
		Assert.assertEquals(footnotes.get(0).getCode(), "027");
		
		
		utx.rollback();

	}

	@Test
	public void importRegtoppAtbLineD2Nortura0096() throws Exception {
		// Prepare context
		Context context = initImportContext();

		File f = new File("src/test/data/atb/line0098.zip");
		File dest = new File("target/referential/test");
		FileUtils.copyFileToDirectory(f, dest);
		JobDataTest job = (JobDataTest) context.get(JOB_DATA);
		job.setInputFilename(f.getName());

		RegtoppImporterCommand command = (RegtoppImporterCommand) CommandFactory.create(initialContext, RegtoppImporterCommand.class.getName());
		
		RegtoppImportParameters parameters = (RegtoppImportParameters) context.get(CONFIGURATION);
		parameters.setObjectIdPrefix("TST");
		parameters.setReferencesType("line");
		parameters.setNoSave(false);

		
		boolean result = command.execute(context);

		ActionReport report = (ActionReport) context.get(REPORT);
		ValidationReport validationReport = (ValidationReport) context.get(VALIDATION_REPORT);
		

		// TODO create asserts on ActionReport and ValidationReport
		
//		Assert.assertEquals(report.getResult(), STATUS_OK, "result");
//		Assert.assertEquals(report.getFiles().size(), 1, "file reported");
//		Assert.assertEquals(report.getLines().size(), 1, "line reported");
//		Reporter.log("report line :" + report.getLines().get(0).toString(), true);
//		Assert.assertEquals(report.getLines().get(0).getStatus(), LINE_STATE.OK, "line status");
//		RegtoppTestUtils.checkLine(context);
//		
//		Referential referential = (Referential) context.get(REFERENTIAL);
//		Assert.assertNotEquals(referential.getTimetables(),0, "timetables" );
//		Assert.assertNotEquals(referential.getSharedTimetables(),0, "shared timetables" );

		// line should be saved
		utx.begin();
		em.joinTransaction();
		Line line = lineDao.findByObjectId("TST:Line:0098");
		

		Assert.assertNotNull(line,"Line not found");
		Assert.assertNotNull(line.getNetwork(), "line must have a network");
		Assert.assertNotNull(line.getCompany(), "line must have a company");
		Assert.assertNotNull(line.getRoutes(), "line must have routes");
		Assert.assertEquals(line.getRoutes().size(), 3, "number of routes");
		Set<StopArea> bps = new HashSet<StopArea>();

		int numStopPoints = 0;
		int numVehicleJourneys = 0;
		int numJourneyPatterns = 0;
		
		for (Route route : line.getRoutes()) {
			Assert.assertNotNull(route.getName(),"No route name");
			
			
			Assert.assertNotEquals(route.getJourneyPatterns().size(), 0 , "line routes must have journeyPattens");
			for (JourneyPattern jp : route.getJourneyPatterns()) {
				Assert.assertNotNull(jp.getName(),"No journeypattern name");
				Assert.assertNotEquals(jp.getStopPoints().size(), 0, "line journeyPattens must have stoppoints");
				for (StopPoint point : jp.getStopPoints()) {

					numStopPoints++;
					Assert.assertNotNull(point.getContainedInStopArea(), "stoppoints must have StopAreas");
					bps.add(point.getContainedInStopArea());

//	TODO bug in chouette this not getting persisted? Se StopPointUpdater in exchange package
//					Assert.assertNotNull(point.getForAlighting(),"no alighting info StopPoint="+point);
//					Assert.assertNotNull(point.getForBoarding(),"no boarding info StopPoint="+point);

				}
				Assert.assertNotEquals(jp.getVehicleJourneys().size(), 0," journeyPattern should have VehicleJourneys");
                for (VehicleJourney vj : jp.getVehicleJourneys()) {
                	Assert.assertNotEquals(vj.getTimetables().size(), 0," vehicleJourney should have timetables");
                	Assert.assertEquals(vj.getVehicleJourneyAtStops().size(), jp.getStopPoints().size()," vehicleJourney should have correct vehicleJourneyAtStop count");
                	numVehicleJourneys++; 
                }
				for (StopPoint point : route.getStopPoints()) {

					Assert.assertNotNull(point.getContainedInStopArea(), "stoppoints must have StopAreas");
					bps.add(point.getContainedInStopArea());

//					TODO bug in chouette this not getting persisted? Se StopPointUpdater in exchange package
//					Assert.assertNotNull(point.getForAlighting(),"no alighting info StopPoint="+point);
//					Assert.assertNotNull(point.getForBoarding(),"no boarding info StopPoint="+point);

				}
                numJourneyPatterns++;
			}
		}
		
		Assert.assertEquals(numJourneyPatterns, 3,"number of journeyPatterns");
		Assert.assertEquals(numVehicleJourneys, 3,"number of vehicleJourneys");
		Assert.assertEquals(numStopPoints, 63,"number of stopPoints in journeyPattern");
		Assert.assertEquals(bps.size(), 48, "number boarding positions");
	
		
		utx.rollback();

		if(!result) {
			System.out.println(ToStringBuilder.reflectionToString(report,ToStringStyle.MULTI_LINE_STYLE));
			System.out.println(validationReport);
			
		}
		
		Assert.assertTrue(result,"Importer command execution failed: "+report.getFailure());
	}
}
