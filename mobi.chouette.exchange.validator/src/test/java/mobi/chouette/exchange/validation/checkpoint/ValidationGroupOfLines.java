package mobi.chouette.exchange.validation.checkpoint;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Color;
import mobi.chouette.common.Context;
import mobi.chouette.exchange.report.ActionReport;
import mobi.chouette.exchange.validation.ValidationData;
import mobi.chouette.exchange.validation.parameters.ValidationParameters;
import mobi.chouette.exchange.validation.report.CheckPointReport;
import mobi.chouette.exchange.validation.report.ValidationReport;
import mobi.chouette.exchange.validator.JobDataTest;
import mobi.chouette.exchange.validator.ValidateParameters;
import mobi.chouette.model.GroupOfLine;
import mobi.chouette.persistence.hibernate.ContextHolder;

import org.apache.commons.io.FileUtils;
import org.testng.Assert;
import org.testng.Reporter;
import org.testng.annotations.BeforeGroups;
import org.testng.annotations.Test;

@Log4j
public class ValidationGroupOfLines extends AbstractTestValidation {
	private GroupOfLineCheckPoints checkPoint = new GroupOfLineCheckPoints();
	private ValidationParameters fullparameters;
	private GroupOfLine bean1;
	private GroupOfLine bean2;
	private List<GroupOfLine> beansFor4 = new ArrayList<>();


	@BeforeGroups(groups = { "groupOfLine" })
	public void init() {
		// BasicConfigurator.configure();
		super.init();
		long id = 1;

		fullparameters = null;
		try {
			fullparameters = loadFullParameters();
			fullparameters.setCheckGroupOfLine(1);

			bean1 = new GroupOfLine();
			bean1.setId(id++);
			bean1.setObjectId("test1:GroupOfLine:1");
			bean1.setName("test1");
			bean2 = new GroupOfLine();
			bean2.setId(id++);
			bean2.setObjectId("test2:GroupOfLine:1");
			bean2.setName("test2");

			beansFor4.add(bean1);
			beansFor4.add(bean2);
		} catch (Exception e) {
			fullparameters = null;
			e.printStackTrace();
		}

	}

	protected Context initValidatorContext() {
		ContextHolder.setContext("chouette_gui"); // set tenant schema

		Context context = new Context();
		context.put(INITIAL_CONTEXT, initialContext);
		context.put(REPORT, new ActionReport());
		context.put(VALIDATION_REPORT, new ValidationReport());
		ValidateParameters configuration = new ValidateParameters();
		context.put(CONFIGURATION, configuration);
		configuration.setName("name");
		configuration.setUserName("userName");
		configuration.setOrganisationName("organisation");
		configuration.setReferentialName("test");
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
		test.setReferential("chouette_gui");
		test.setAction(VALIDATOR);
		context.put("testng", "true");
		context.put(OPTIMIZED, Boolean.FALSE);
		return context;

	}

	@Test(groups = { "groupOfLine" }, description = "4-GroupOfLine-1 no test",priority=1)
	public void verifyTest4_1_notest() throws Exception {
		// 4-GroupOfLine-1 : check columns
		log.info(Color.BLUE + "4-GroupOfLine-1 no test" + Color.NORMAL);
		Context context = initValidatorContext();
		Assert.assertNotNull(fullparameters, "no parameters for test");
		context.put(VALIDATION_REPORT, new ValidationReport());

		fullparameters.setCheckGroupOfLine(0);
		context.put(VALIDATION, fullparameters);
		ValidationData data = new ValidationData();
		data.getGroupOfLines().addAll(beansFor4);
		context.put(VALIDATION_DATA, data);

		checkPoint.validate(context, null);

		ValidationReport report = (ValidationReport) context.get(VALIDATION_REPORT);
		Assert.assertTrue(report.findCheckPointReportByName("4-GroupOfLine-1") == null, " report must not have item 4-GroupOfLine-1");

		fullparameters.setCheckGroupOfLine(1);
		context.put(VALIDATION_REPORT, new ValidationReport());

		checkPoint.validate(context, null);
		report = (ValidationReport) context.get(VALIDATION_REPORT);
		Assert.assertTrue(report.findCheckPointReportByName("4-GroupOfLine-1") != null,
				" report must have item 4-GroupOfLine-1");
		Assert.assertEquals(report.findCheckPointReportByName("4-GroupOfLine-1").getCheckPointErrorCount(), 0,
				" checkpoint must have no detail");

	}

	@Test(groups = { "groupOfLine" }, description = "4-GroupOfLine-1 unicity",priority=2)
	public void verifyTest4_1_unique() throws Exception {
		// 4-GroupOfLine-1 : check columns
		log.info(Color.BLUE + "4-GroupOfLine-1 unicity" + Color.NORMAL);
		Context context = initValidatorContext();
		Assert.assertNotNull(fullparameters, "no parameters for test");

		context.put(VALIDATION_REPORT, new ValidationReport());

		fullparameters.setCheckGroupOfLine(1);
		fullparameters.getGroupOfLine().getObjectId().setUnique(1);

		context.put(VALIDATION, fullparameters);
		ValidationData data = new ValidationData();
		data.getGroupOfLines().addAll(beansFor4);
		context.put(VALIDATION_DATA, data);
		checkPoint.validate(context, null);
		fullparameters.getGroupOfLine().getObjectId().setUnique(0);

		ValidationReport report = (ValidationReport) context.get(VALIDATION_REPORT);
        Reporter.log(report.toString(), true);
		Assert.assertFalse(report.getCheckPoints().isEmpty(), " report must have items");
		Assert.assertNotNull(report.findCheckPointReportByName("4-GroupOfLine-1"), " report must have 1 item on key 4-GroupOfLine-1");
		CheckPointReport checkPointReport = report.findCheckPointReportByName("4-GroupOfLine-1");
		Assert.assertEquals(checkPointReport.getCheckPointErrorCount(), 1, " checkpoint must have " + 1 + " detail");
	}


}
