package mobi.chouette.exchange.transfer.exporter;

import static org.testng.Assert.assertEquals;

import java.io.File;
import java.io.IOException;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.Set;

import javax.ejb.EJB;
import javax.inject.Inject;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.UserTransaction;

import org.apache.commons.io.FileUtils;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.testng.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.testng.Assert;
import org.testng.annotations.Test;

import mobi.chouette.common.Context;
import mobi.chouette.common.chain.Command;
import mobi.chouette.common.chain.CommandFactory;
import mobi.chouette.dao.LineDAO;
import mobi.chouette.dao.RouteDAO;
import mobi.chouette.dao.VehicleJourneyDAO;
import mobi.chouette.exchange.importer.CleanRepositoryCommand;
import mobi.chouette.exchange.report.ActionReport;
import mobi.chouette.exchange.transfer.JobDataTest;
import mobi.chouette.exchange.validation.ValidationData;
import mobi.chouette.exchange.validation.report.ValidationReport;
import mobi.chouette.model.Company;
import mobi.chouette.model.JourneyPattern;
import mobi.chouette.model.Line;
import mobi.chouette.model.Period;
import mobi.chouette.model.Route;
import mobi.chouette.model.StopArea;
import mobi.chouette.model.StopPoint;
import mobi.chouette.model.Timetable;
import mobi.chouette.model.VehicleJourney;
import mobi.chouette.model.VehicleJourneyAtStop;
import mobi.chouette.model.type.ChouetteAreaEnum;
import mobi.chouette.model.util.ObjectFactory;
import mobi.chouette.model.util.Referential;
import mobi.chouette.persistence.hibernate.ContextHolder;
import mobi.chouette.scheduler.Scheduler;

public class TransferExporterCommandTest extends Arquillian implements mobi.chouette.common.Constant {

	private InitialContext initialContext;

	private void init() {
		if (initialContext == null) {
			try {
				initialContext = new InitialContext();

			} catch (NamingException e) {
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
	
	@EJB
	Scheduler scheduler;

	@Deployment
	public static WebArchive createDeployment() {

		File[] files = Maven.resolver().loadPomFromFile("pom.xml")
				.resolve("mobi.chouette:mobi.chouette.exchange.transfer", "mobi.chouette:mobi.chouette.dao","mobi.chouette:mobi.chouette.service").withTransitivity()
				.asFile();

		return ShrinkWrap.create(WebArchive.class, "test.war").addAsWebInfResource("postgres-ds.xml")
				.addAsLibraries(files).addClass(DummyChecker.class).addClass(JobDataTest.class);
	}

	protected Context initImportContext() {
		init();
		ContextHolder.setContext("chouette_gui"); // set tenant schema

		Context context = new Context();
		context.put(INITIAL_CONTEXT, initialContext);
		context.put(REPORT, new ActionReport());
		context.put(VALIDATION_REPORT, new ValidationReport());
		context.put(VALIDATION_DATA, new ValidationData());
		TransferExportParameters configuration = new TransferExportParameters();
		context.put(CONFIGURATION, configuration);
		configuration.setName("name");
		configuration.setUserName("userName");
		configuration.setDestReferentialName("chouette_gui_transfer");
		// configuration.setCleanRepository(true);
		configuration.setOrganisationName("organisation");
		configuration.setReferentialName("test");
		return context;

	}

	@Test
	public void transferDataToNewTenant() throws Exception {
		// Prepare context
		Context context = initImportContext();
		JobDataTest test = new JobDataTest();
		context.put(JOB_DATA, test);
		test.setPathName("target/referential/test");
		File f = new File("target/referential/test");
		if (f.exists())
			try {
				FileUtils.deleteDirectory(f);
			} catch (IOException e) {
				e.printStackTrace();
			}
		f.mkdirs();

		Referential referential = new Referential();

		// Clean source dataspace first
		ContextHolder.setContext("chouette_gui");
		Command cleanCommand = CommandFactory.create(initialContext, CleanRepositoryCommand.class.getName());
		boolean cleanCommandResult = cleanCommand.execute(context);
		Assert.assertTrue(cleanCommandResult);

		// Clean destination dataset
		ContextHolder.setContext("chouette_gui_transfer");
		Command cleanCommandTransfer = CommandFactory.create(initialContext, CleanRepositoryCommand.class.getName());
		boolean cleanCommandResultTransfer = cleanCommandTransfer.execute(context);
		Assert.assertTrue(cleanCommandResultTransfer);

		// Import some data
		ContextHolder.setContext("chouette_gui");
		Line l = createLineStructure(referential);
		lineDao.create(l);

		// Run transfer

		Command command = CommandFactory.create(initialContext, TransferExporterCommand.class.getName());

		boolean transferExportCommandResult = command.execute(context);
		Assert.assertTrue(transferExportCommandResult);

		System.err.println("Transfer complete, verifying data");

		// line should be saved
		ContextHolder.setContext("chouette_gui_transfer");
		try {
			utx.begin();
			em.joinTransaction();

			Line line = lineDao.findByObjectId("TST:Line:1");

			Assert.assertNotNull(line, "Line not found");
			Assert.assertNotNull(line.getCompany(), "line must have a company");
			Assert.assertNotNull(line.getRoutes(), "line must have routes");
			assertEquals(line.getRoutes().size(), 1, "number of routes");
			Set<StopArea> bps = new HashSet<StopArea>();

			int numStopPoints = 0;
			int numVehicleJourneys = 0;
			int numJourneyPatterns = 0;

			for (Route route : line.getRoutes()) {
				Assert.assertNotNull(route.getName(), "No route name");

				Assert.assertNotEquals(route.getJourneyPatterns().size(), 0, "line routes must have journeyPattens");
				for (JourneyPattern jp : route.getJourneyPatterns()) {
					Assert.assertNotNull(jp.getName(), "No journeypattern name");
					Assert.assertNotEquals(jp.getStopPoints().size(), 0, "line journeyPattens must have stoppoints");
					for (StopPoint point : jp.getStopPoints()) {

						numStopPoints++;
						Assert.assertNotNull(point.getContainedInStopArea(), "stoppoints must have StopAreas");
						bps.add(point.getContainedInStopArea());

					}
					Assert.assertNotEquals(jp.getVehicleJourneys().size(), 0,
							" journeyPattern should have VehicleJourneys");
					for (VehicleJourney vj : jp.getVehicleJourneys()) {
						Assert.assertNotEquals(vj.getTimetables().size(), 0, " vehicleJourney should have timetables");
						assertEquals(vj.getVehicleJourneyAtStops().size(), jp.getStopPoints().size(),
								" vehicleJourney should have correct vehicleJourneyAtStop count");
						numVehicleJourneys++;
					}
					numJourneyPatterns++;
				}
			}

			assertEquals(numJourneyPatterns, 1, "number of journeyPatterns");
			assertEquals(numVehicleJourneys, 1, "number of vehicleJourneys");
			assertEquals(numStopPoints, 2, "number of stopPoints in journeyPattern");
			assertEquals(bps.size(), 2, "number boarding positions");

		} finally {
			utx.rollback();
		}

	}

	protected Line createLineStructure(Referential referential) {

		Company c = ObjectFactory.getCompany(referential, "TST:Company:1");
		c.setName("CompanyName");

		Line l = ObjectFactory.getLine(referential, "TST:Line:1");
		l.setName("TestLine");
		l.setCompany(c);

		Route r = ObjectFactory.getRoute(referential, "TST:Route:1");
		r.setName("RouteName");
		r.setLine(l);

		JourneyPattern jp = ObjectFactory.getJourneyPattern(referential, "TST:JourneyPattern:1");
		jp.setName("JourneyPatternName");
		jp.setRoute(r);

		StopArea stop1 = ObjectFactory.getStopArea(referential, "TST:StopPlace:1");
		stop1.setName("Stop1");
		stop1.setDetached(false);
		stop1.setAreaType(ChouetteAreaEnum.CommercialStopPoint);

		StopPoint stopPoint1 = ObjectFactory.getStopPoint(referential, "TST:StopPoint:1");
		stopPoint1.setContainedInStopArea(stop1);
		stopPoint1.setRoute(r);
		jp.addStopPoint(stopPoint1);
		jp.setDepartureStopPoint(stopPoint1);

		StopArea stop2 = ObjectFactory.getStopArea(referential, "TST:StopPlace:2");
		stop2.setName("Stop2");
		stop2.setDetached(false);
		stop2.setAreaType(ChouetteAreaEnum.CommercialStopPoint);

		StopPoint stopPoint2 = ObjectFactory.getStopPoint(referential, "TST:StopPoint:2");
		stopPoint2.setContainedInStopArea(stop2);
		stopPoint2.setRoute(r);
		jp.addStopPoint(stopPoint2);
		jp.setArrivalStopPoint(stopPoint2);

		Timetable t = ObjectFactory.getTimetable(referential, "TST:Timetable:1");
		Period p = new Period();
		p.setStartDate(java.sql.Date.valueOf(LocalDate.now().minusDays(10)));
		p.setEndDate(java.sql.Date.valueOf(LocalDate.now().plusDays(10)));
		t.addPeriod(p);

		VehicleJourney vj = ObjectFactory.getVehicleJourney(referential, "TST:ServiceJourney:1");
		vj.setJourneyPattern(jp);
		vj.setRoute(r);
		vj.setCompany(c);

		t.addVehicleJourney(vj);

		VehicleJourneyAtStop vjStop1 = ObjectFactory.getVehicleJourneyAtStop();
		vjStop1.setDepartureTime(Time.valueOf(LocalTime.now()));
		vjStop1.setStopPoint(stopPoint1);
		vjStop1.setVehicleJourney(vj);

		VehicleJourneyAtStop vjStop2 = ObjectFactory.getVehicleJourneyAtStop();
		vjStop2.setDepartureTime(Time.valueOf(LocalTime.now()));
		vjStop2.setStopPoint(stopPoint2);
		vjStop2.setVehicleJourney(vj);

		return l;
	}

}
