package mobi.chouette.exchange.validation.checkpoint;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Context;
import mobi.chouette.common.chain.Command;
import mobi.chouette.common.chain.CommandFactory;
import mobi.chouette.dao.LineDAO;
import mobi.chouette.exchange.validation.parameters.ValidationParameters;
import mobi.chouette.exchange.validation.report.CheckPointReport;
import mobi.chouette.exchange.validation.report.ValidationReport;
import mobi.chouette.exchange.validation.report.ValidationReporter;
import mobi.chouette.exchange.validator.DummyChecker;
import mobi.chouette.exchange.validator.JobDataTest;
import mobi.chouette.exchange.validator.ValidateParameters;
import mobi.chouette.exchange.validator.ValidatorCommand;
import mobi.chouette.model.Line;

import org.jboss.arquillian.container.test.api.Deployment;
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
public class ValidationNominal extends AbstractTestValidation {

	@EJB
	LineDAO lineDao;

	@Deployment
	public static EnterpriseArchive createDeployment() {

		EnterpriseArchive result;
		File[] files = Maven.resolver().loadPomFromFile("pom.xml")
				.resolve("mobi.chouette:mobi.chouette.exchange.validator").withTransitivity().asFile();
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
				.addAsWebInfResource("postgres-ds.xml").addClass(DummyChecker.class).addClass(JobDataTest.class)
				.addClass(AbstractTestValidation.class).addClass(ValidationNominal.class);

		result = ShrinkWrap.create(EnterpriseArchive.class, "test.ear").addAsLibraries(jars.toArray(new File[0]))
				.addAsModules(modules.toArray(new JavaArchive[0])).addAsModule(testWar)
				.addAsResource(EmptyAsset.INSTANCE, "beans.xml");
		return result;
	}

	@Test(groups = { "all" }, description = "3-all-ok")
	public void verifyTestOk() throws Exception {
		// 3-all-1 : no warning nor error
		init();

		ValidationParameters parameters = null;
		try {
			parameters = loadFullParameters();
		} catch (Exception e) {
			e.printStackTrace();
		}
		Assert.assertNotNull(parameters, "no parameters for test");

		importLines("model.zip", 7, 7, true);
		List<Line> beans = lineDao.findAll();
		Assert.assertFalse(beans.isEmpty(), "No data for test");

		Context context = initValidatorContext();
		context.put(VALIDATION, parameters);
		ValidateParameters configuration = (ValidateParameters) context.get(CONFIGURATION);
		configuration.setReferencesType("line");

		Command command = (Command) CommandFactory.create(initialContext, ValidatorCommand.class.getName());

		command.execute(context);
		ValidationReport report = (ValidationReport) context.get(VALIDATION_REPORT);
		Reporter.log(report.toString(), true);
		Assert.assertFalse(report.getCheckPoints().isEmpty(), "report must have items");
		boolean bStopArea1 = false;
		boolean bAccessPoint1 = false;
		boolean bLine2 = false;
		boolean bRoute6 = false;
		boolean bRoute7 = false;
		List<String> unchecked = new ArrayList<>();
		// no shape
		unchecked.add("3-Route-5");
		unchecked.add("3-JourneyPattern-2");
		unchecked.add("3-Line-3");
		// no frequencies
		unchecked.add("3-VehicleJourney-6");
		unchecked.add("3-VehicleJourney-7");
		unchecked.add("3-VehicleJourney-8");
		
		for (CheckPointReport checkPoint : report.getCheckPoints()) {
			if (checkPoint.getName().equals("3-StopArea-1"))
				bStopArea1 = true;
			if (checkPoint.getName().equals("3-AccessPoint-1"))
				bAccessPoint1 = true;
			if (checkPoint.getName().equals("3-Line-2"))
				bLine2 = true;
			if (checkPoint.getName().equals("3-Route-6"))
				bRoute6 = true;
			if (checkPoint.getName().equals("3-Route-7"))
				bRoute7 = true;
			if (unchecked.contains(checkPoint.getName())) {
				Assert.assertEquals(checkPoint.getState(), ValidationReporter.RESULT.UNCHECK, "checkPoint "
						+ checkPoint.getName() + " must not be on level " + checkPoint.getState());
			} else {
				Assert.assertEquals(checkPoint.getState(), ValidationReporter.RESULT.OK,
						"checkPoint " + checkPoint.getName() + " must not be on level " + checkPoint.getState());
			}

		}
		Assert.assertTrue(bStopArea1, "3-StopArea-1 test must not be skipped");
		Assert.assertTrue(bAccessPoint1, "3-AccessPoint-1 test must not be skipped");
		Assert.assertTrue(bLine2, "3-Line-2 test must not be skipped");
		Assert.assertTrue(bRoute6, "3-Route-6 test must not be skipped");
		Assert.assertTrue(bRoute7, "3-Route-7 test must not be skipped");

	}

	@Test(groups = { "all" }, description = "3-all-ok-from-file")
	public void verifyTestOkFromFile() throws Exception {
		// 3-all-1 : no warning nor error
		// some tests are skiped
		init();

		ValidationParameters parameters = null;
		try {
			parameters = loadFullParameters();
		} catch (Exception e) {
			e.printStackTrace();
		}
		Assert.assertNotNull(parameters, "no parameters for test");

		importLines("model.zip", 7, 7, true);
		List<Line> beans = lineDao.findAll();
		Assert.assertFalse(beans.isEmpty(), "No data for test");

		Context context = initValidatorContext();
		context.put(VALIDATION, parameters);
		ValidateParameters configuration = (ValidateParameters) context.get(CONFIGURATION);
		configuration.setReferencesType("line");

		Command command = (Command) CommandFactory.create(initialContext, ValidatorCommand.class.getName());

		context.put(SOURCE, SOURCE_FILE);
		command.execute(context);
		ValidationReport report = (ValidationReport) context.get(VALIDATION_REPORT);
		Reporter.log(report.toString(), true);
		Assert.assertFalse(report.getCheckPoints().isEmpty(), "report must have items");
		for (CheckPointReport checkPoint : report.getCheckPoints()) {
			Assert.assertNotEquals(checkPoint.getName(), "3-StopArea-1", "3-StopArea-1 test must be skipped");
			Assert.assertNotEquals(checkPoint.getName(), "3-AccessPoint-1", "3-AccessPoint-1 test must be skipped");
			Assert.assertNotEquals(checkPoint.getName(), "3-Line-2", "3-Line-2 test must be skipped");
			Assert.assertNotEquals(checkPoint.getName(), "3-Route-6", "3-Route-6 test must be skipped");
			Assert.assertNotEquals(checkPoint.getName(), "3-Route-7", "3-Route-7 test must be skipped");
		}

	}

}
