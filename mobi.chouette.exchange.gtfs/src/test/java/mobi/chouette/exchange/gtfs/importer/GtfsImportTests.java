package mobi.chouette.exchange.gtfs.importer;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.ejb.EJB;
import javax.inject.Inject;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.UserTransaction;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Color;
import mobi.chouette.common.Context;
import mobi.chouette.common.chain.CommandFactory;
import mobi.chouette.dao.LineDAO;
import mobi.chouette.dao.VehicleJourneyDAO;
import mobi.chouette.exchange.gtfs.Constant;
import mobi.chouette.exchange.gtfs.DummyChecker;
import mobi.chouette.exchange.gtfs.GtfsTestsUtils;
import mobi.chouette.exchange.gtfs.JobDataTest;
import mobi.chouette.exchange.report.ActionReport;
import mobi.chouette.exchange.report.ActionReporter;
import mobi.chouette.exchange.report.ActionReporter.OBJECT_STATE;
import mobi.chouette.exchange.report.ObjectReport;
import mobi.chouette.exchange.report.ReportConstant;
import mobi.chouette.exchange.validation.report.ValidationReport;
import mobi.chouette.model.JourneyPattern;
import mobi.chouette.model.Line;
import mobi.chouette.model.Route;
import mobi.chouette.model.VehicleJourney;
import mobi.chouette.persistence.hibernate.ContextHolder;

import org.apache.commons.io.FileUtils;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.testng.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.importer.ZipImporter;
import org.jboss.shrinkwrap.api.spec.EnterpriseArchive;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.testng.Assert;
import org.testng.Reporter;
import org.testng.annotations.Test;

@Log4j
public class GtfsImportTests extends Arquillian implements Constant, ReportConstant {

	@EJB
	LineDAO lineDao;

	@EJB
	VehicleJourneyDAO vjDao;

	@PersistenceContext(unitName = "referential")
	EntityManager em;

	@Inject
	UserTransaction utx;

	@Deployment
	public static EnterpriseArchive createDeployment() {

		EnterpriseArchive result;
		File[] files = Maven.resolver().loadPomFromFile("pom.xml").resolve("mobi.chouette:mobi.chouette.exchange.gtfs")
				.withTransitivity().asFile();
		List<File> jars = new ArrayList<>();
		List<JavaArchive> modules = new ArrayList<>();
		for (File file : files) {
			if (file.getName().startsWith("mobi.chouette.exchange")) {
				String name = file.getName().split("\\-")[0] + ".jar";
				JavaArchive archive = ShrinkWrap.create(ZipImporter.class, name).importFrom(file).as(JavaArchive.class);
				modules.add(archive);
			} else {
				jars.add(file);
			}
		}
		File[] filesDao = Maven.resolver().loadPomFromFile("pom.xml").resolve("mobi.chouette:mobi.chouette.dao")
				.withTransitivity().asFile();
		if (filesDao.length == 0) {
			throw new NullPointerException("no dao");
		}
		for (File file : filesDao) {
			if (file.getName().startsWith("mobi.chouette.dao")) {
				String name = file.getName().split("\\-")[0] + ".jar";

				JavaArchive archive = ShrinkWrap.create(ZipImporter.class, name).importFrom(file).as(JavaArchive.class);
				modules.add(archive);
				if (!modules.contains(archive))
					modules.add(archive);
			} else {
				if (!jars.contains(file))
					jars.add(file);
			}
		}
		final WebArchive testWar = ShrinkWrap.create(WebArchive.class, "test.war")
				.addAsWebInfResource("postgres-ds.xml").addClass(GtfsImportTests.class).addClass(GtfsTestsUtils.class)
				.addClass(DummyChecker.class).addClass(JobDataTest.class);

		result = ShrinkWrap.create(EnterpriseArchive.class, "test.ear").addAsLibraries(jars.toArray(new File[0]))
				.addAsModules(modules.toArray(new JavaArchive[0])).addAsModule(testWar)
				.addAsResource(EmptyAsset.INSTANCE, "beans.xml");
		return result;
	}

	protected static InitialContext initialContext;

	protected void init() {
		Locale.setDefault(Locale.ENGLISH);
		if (initialContext == null) {
			try {
				initialContext = new InitialContext();
			} catch (NamingException e) {
				e.printStackTrace();
			}

		}
	}

	protected Context initImportContext() {
		init();
		ContextHolder.setContext("chouette_gui"); // set tenant schema

		Context context = new Context();
		context.put(INITIAL_CONTEXT, initialContext);
		context.put(REPORT, new ActionReport());
		context.put(VALIDATION_REPORT, new ValidationReport());
		GtfsImportParameters configuration = new GtfsImportParameters();
		context.put(CONFIGURATION, configuration);
		configuration.setName("name");
		configuration.setUserName("userName");
		configuration.setNoSave(true);
		configuration.setCleanRepository(true);
		configuration.setOrganisationName("organisation");
		configuration.setReferentialName("test");
		configuration.setObjectIdPrefix("TEST");
		configuration.setReferencesType("lines");

		JobDataTest jobData = new JobDataTest();
		context.put(JOB_DATA, jobData);
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
		jobData.setType("gtfs");
		context.put("testng", "true");
		context.put(OPTIMIZED, Boolean.FALSE);
		return context;

	}

	@Test(groups = { "ImportLine" }, description = "Import Plugin should import file")
	public void verifyImportLineWithTad() throws Exception {
		Context context = initImportContext();
		GtfsImporterCommand command = (GtfsImporterCommand) CommandFactory.create(initialContext,
				GtfsImporterCommand.class.getName());
		GtfsTestsUtils.copyFile("tad.zip");
		JobDataTest jobData = (JobDataTest) context.get(JOB_DATA);
		jobData.setInputFilename("tad.zip");
		GtfsImportParameters configuration = (GtfsImportParameters) context.get(CONFIGURATION);
		configuration.setNoSave(false);
		configuration.setCleanRepository(true);
		try {
			command.execute(context);
		} catch (Exception ex) {
			log.error("test failed", ex);
			throw ex;
		}
		ActionReport report = (ActionReport) context.get(REPORT);
		Reporter.log("report :" + report.toString(), true);
		Assert.assertEquals(report.getResult(), STATUS_OK, "result");
		Assert.assertEquals(report.getFiles().size(), 9, "file reported");
		Assert.assertNotNull(report.getCollections().get(ActionReporter.OBJECT_TYPE.LINE), "line reported");
		Assert.assertEquals(report.getCollections().get(ActionReporter.OBJECT_TYPE.LINE).getObjectReports().size(), 1,
				"line reported");
		for (ObjectReport info : report.getCollections().get(ActionReporter.OBJECT_TYPE.LINE).getObjectReports()) {
			Reporter.log("report line :" + info.toString(), true);
			Assert.assertEquals(info.getStatus(), OBJECT_STATE.OK, "line status");
		}

		// line should be saved
		utx.begin();
		em.joinTransaction();
		Line line = lineDao.findByObjectId("TEST:Line:1");

		checkLine(line);

		utx.rollback();

	}
	@Test(groups = { "ImportLine" }, description = "Import Plugin should import file with shape")
	public void verifyImportLineWithTadAndShape() throws Exception {
		Context context = initImportContext();
		GtfsImporterCommand command = (GtfsImporterCommand) CommandFactory.create(initialContext,
				GtfsImporterCommand.class.getName());
		GtfsTestsUtils.copyFile("tad_shape.zip");
		JobDataTest jobData = (JobDataTest) context.get(JOB_DATA);
		jobData.setInputFilename("tad_shape.zip");
		GtfsImportParameters configuration = (GtfsImportParameters) context.get(CONFIGURATION);
		configuration.setNoSave(false);
		configuration.setCleanRepository(true);
		try {
			command.execute(context);
		} catch (Exception ex) {
			log.error("test failed", ex);
			throw ex;
		}
		ActionReport report = (ActionReport) context.get(REPORT);
		Reporter.log("report :" + report.toString(), true);
		Assert.assertEquals(report.getResult(), STATUS_OK, "result");
		Assert.assertEquals(report.getFiles().size(), 9, "file reported");
		Assert.assertNotNull(report.getCollections().get(ActionReporter.OBJECT_TYPE.LINE), "line reported");
		Assert.assertEquals(report.getCollections().get(ActionReporter.OBJECT_TYPE.LINE).getObjectReports().size(), 1,
				"line reported");
		for (ObjectReport info : report.getCollections().get(ActionReporter.OBJECT_TYPE.LINE).getObjectReports()) {
			Reporter.log("report line :" + info.toString(), true);
			Assert.assertEquals(info.getStatus(), OBJECT_STATE.OK, "line status");
		}

		// line should be saved
		utx.begin();
		em.joinTransaction();
		Line line = lineDao.findByObjectId("TEST:Line:1");

		checkLine(line);

		utx.rollback();

	}

	private void checkLine(Line line) {
		for (Route route : line.getRoutes()) {
			log.info(Color.YELLOW + "routeId = " + route.getObjectId() + " journeyPattern size = "
					+ route.getJourneyPatterns().size() +" stopPoint size = "+route.getStopPoints().size()
					+ Color.NORMAL);
			for (JourneyPattern jp : route.getJourneyPatterns()) {
				log.info(Color.CYAN + "jpId = " + jp.getObjectId() + " vehicleJourney size = "
						+ jp.getVehicleJourneys().size() +" stopPoint size = "+jp.getStopPoints().size()+" section size = "+jp.getRouteSections().size() + Color.NORMAL);
				for (VehicleJourney vj : jp.getVehicleJourneys()) {
					log.info(Color.MAGENTA + "vjId = " + vj.getObjectId() +" name "+vj.getPublishedJourneyName() + " vehicleJourney size = "
							+ vj.getVehicleJourneyAtStops().size() + Color.NORMAL);
				}
			}

		}
		Assert.assertEquals(line.getRoutes().size(), 5, "route size");
	}

}
