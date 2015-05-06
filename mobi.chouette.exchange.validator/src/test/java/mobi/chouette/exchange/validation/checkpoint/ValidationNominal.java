package mobi.chouette.exchange.validation.checkpoint;

import java.io.File;
import java.util.List;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Context;
import mobi.chouette.common.chain.Command;
import mobi.chouette.common.chain.CommandFactory;
import mobi.chouette.exchange.validation.parameters.ValidationParameters;
import mobi.chouette.exchange.validation.report.CheckPoint;
import mobi.chouette.exchange.validation.report.ValidationReport;
import mobi.chouette.exchange.validator.JobDataTest;
import mobi.chouette.exchange.validator.ValidateParameters;
import mobi.chouette.exchange.validator.ValidatorCommand;
import mobi.chouette.model.Line;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.testng.Assert;
import org.testng.annotations.Test;

@Log4j
public class ValidationNominal extends AbstractTestValidation {

	@Deployment
	public static WebArchive createDeployment() {

		WebArchive result;

		File[] files = Maven.resolver().loadPomFromFile("pom.xml").importRuntimeAndTestDependencies()
				.resolve("mobi.chouette:mobi.chouette.exchange.validator:3.0.0").withTransitivity().asFile();

		result = ShrinkWrap.create(WebArchive.class, "test.war").addAsWebInfResource("postgres-ds.xml")
				.addAsLibraries(files).addClass(JobDataTest.class).addClass(AbstractTestValidation.class)
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

		importLines("model.zip", 7, 7,true);
		List<Line> beans = lineDao.findAll();
		Assert.assertFalse(beans.isEmpty(), "No data for test");

		Context context = initValidatorContext();
		context.put(VALIDATION, parameters);
		ValidateParameters configuration = (ValidateParameters) context.get(CONFIGURATION);
		configuration.setReferencesType("line");

		Command command = (Command) CommandFactory.create(initialContext, ValidatorCommand.class.getName());

		command.execute(context);
		ValidationReport report = (ValidationReport) context.get(MAIN_VALIDATION_REPORT);
		Assert.assertFalse(report.getCheckPoints().isEmpty(), "report must have items");
		for (CheckPoint checkPoint : report.getCheckPoints()) {
			if (checkPoint.getName().equals("3-Route-5")) {
				Assert.assertEquals(checkPoint.getState(), CheckPoint.RESULT.UNCHECK,
						"checkPoint " + checkPoint.getName() + " must not be on level " + checkPoint.getState());
			} else {
				Assert.assertEquals(checkPoint.getState(), CheckPoint.RESULT.OK, "checkPoint " + checkPoint.getName()
						+ " must not be on level " + checkPoint.getState());
			}

		}

	}

}
