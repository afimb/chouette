package mobi.chouette.exchange.validation.checkpoint;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Color;
import mobi.chouette.common.Context;
import mobi.chouette.common.JSONUtil;
import mobi.chouette.core.ChouetteException;
import mobi.chouette.exchange.report.ActionReport;
import mobi.chouette.exchange.validation.ValidationData;
import mobi.chouette.exchange.validation.parameters.ValidationParameters;
import mobi.chouette.exchange.validation.report.CheckPointReport;
import mobi.chouette.exchange.validation.report.ValidationReport;
import mobi.chouette.exchange.validator.JobDataTest;
import mobi.chouette.exchange.validator.ValidateParameters;
import mobi.chouette.model.Timetable;
import mobi.chouette.persistence.hibernate.ContextHolder;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.BasicConfigurator;
import org.testng.Assert;
import org.testng.Reporter;
import org.testng.annotations.BeforeGroups;
import org.testng.annotations.Test;

@Log4j
public class ValidationTimetables extends AbstractTestValidation {
	private TimetableCheckPoints checkPoint = new TimetableCheckPoints();
	private ValidationParameters fullparameters;
	private Timetable bean1;
	private Timetable bean2;
	private List<Timetable> beansFor4 = new ArrayList<>();

	protected static final String path = "src/test/data/checkpoints";

	protected ValidationParameters loadFullParameters() throws Exception {
		String filename = "fullparameterset.json";
		File f = new File(path, filename);
		return (ValidationParameters) JSONUtil.fromJSON(f.toPath(), ValidationParameters.class);
	}

	@BeforeGroups(groups = { "timetable" })
	public void init() {
		BasicConfigurator.configure();
		super.init();
		long id = 1;

		fullparameters = null;
		try {
			fullparameters = loadFullParameters();
			fullparameters.setCheckTimetable(1);

			bean1 = new Timetable();
			bean1.setId(id++);
			bean1.setObjectId("test1:Timetable:1");
			bean1.setComment("test1");
			bean2 = new Timetable();
			bean2.setId(id++);
			bean2.setObjectId("test2:Timetable:1");
			bean2.setComment("test2");

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

	@Test(groups = { "timetable" }, description = "4-Timetable-1 no test",priority=1)
	public void verifyTest4_1_notest() throws ChouetteException {
		// 4-Timetable-1 : check columns
		log.info(Color.BLUE + "4-Timetable-1 no test" + Color.NORMAL);
		Context context = initValidatorContext();
		Assert.assertNotNull(fullparameters, "no parameters for test");
		context.put(VALIDATION_REPORT, new ValidationReport());

		fullparameters.setCheckTimetable(0);
		context.put(VALIDATION, fullparameters);
		ValidationData data = new ValidationData();
		data.getTimetables().addAll(beansFor4);
		context.put(VALIDATION_DATA, data);

		checkPoint.validate(context, null);

		ValidationReport report = (ValidationReport) context.get(VALIDATION_REPORT);
		Assert.assertTrue(report.findCheckPointReportByName("4-Timetable-1") == null, " report must not have item 4-Timetable-1");

		fullparameters.setCheckTimetable(1);
		context.put(VALIDATION_REPORT, new ValidationReport());

		checkPoint.validate(context, null);
		report = (ValidationReport) context.get(VALIDATION_REPORT);
		Assert.assertTrue(report.findCheckPointReportByName("4-Timetable-1") != null,
				" report must have item 4-Timetable-1");
		Assert.assertEquals(report.findCheckPointReportByName("4-Timetable-1").getCheckPointErrorCount(), 0,
				" checkpoint must have no detail");

	}

	@Test(groups = { "timetable" }, description = "4-Timetable-1 unicity",priority=2)
	public void verifyTest4_1_unique() throws ChouetteException {
		// 4-Timetable-1 : check columns
		log.info(Color.BLUE + "4-Timetable-1 unicity" + Color.NORMAL);
		Context context = initValidatorContext();
		Assert.assertNotNull(fullparameters, "no parameters for test");

		context.put(VALIDATION_REPORT, new ValidationReport());

		fullparameters.setCheckTimetable(1);
		fullparameters.getTimetable().getObjectId().setUnique(1);

		context.put(VALIDATION, fullparameters);
		ValidationData data = new ValidationData();
		data.getTimetables().addAll(beansFor4);
		context.put(VALIDATION_DATA, data);
		checkPoint.validate(context, null);
		fullparameters.getTimetable().getObjectId().setUnique(0);

		ValidationReport report = (ValidationReport) context.get(VALIDATION_REPORT);

        Reporter.log(report.toString(), true);
		Assert.assertFalse(report.getCheckPoints().isEmpty(), " report must have items");
		Assert.assertNotNull(report.findCheckPointReportByName("4-Timetable-1"), " report must have 1 item on key 4-Timetable-1");
		CheckPointReport checkPointReport = report.findCheckPointReportByName("4-Timetable-1");
		Assert.assertEquals(checkPointReport.getCheckPointErrorCount(), 1, " checkpoint must have " + 1 + " detail");
	}


}
