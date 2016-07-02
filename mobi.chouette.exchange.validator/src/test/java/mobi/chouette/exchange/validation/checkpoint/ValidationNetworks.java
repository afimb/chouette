package mobi.chouette.exchange.validation.checkpoint;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Color;
import mobi.chouette.common.Constant;
import mobi.chouette.common.Context;
import mobi.chouette.common.JSONUtil;
import mobi.chouette.exchange.report.ActionReport;
import mobi.chouette.exchange.validation.ValidationData;
import mobi.chouette.exchange.validation.parameters.ValidationParameters;
import mobi.chouette.exchange.validation.report.CheckPointErrorReport;
import mobi.chouette.exchange.validation.report.CheckPointReport;
import mobi.chouette.exchange.validation.report.ValidationReport;
import mobi.chouette.exchange.validator.JobDataTest;
import mobi.chouette.exchange.validator.ValidateParameters;
import mobi.chouette.model.Network;
import mobi.chouette.persistence.hibernate.ContextHolder;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.BasicConfigurator;
import org.testng.Assert;
import org.testng.annotations.BeforeGroups;
import org.testng.annotations.Test;

@Log4j
public class ValidationNetworks extends AbstractTestValidation implements Constant{
	private NetworkCheckPoints checkPoint = new NetworkCheckPoints();
	private ValidationParameters fullparameters;
	private Network bean1;
	private Network bean2;
	private List<Network> beansFor4 = new ArrayList<>();

	protected static final String path = "src/test/data/checkpoints";
	protected ValidationParameters loadFullParameters() throws Exception
	{
				String filename = "fullparameterset.json";
				File f = new File(path,filename);
				return (ValidationParameters) JSONUtil.fromJSON(f.toPath(), ValidationParameters.class);
	}

	@BeforeGroups(groups = { "network" })
	public void init() {
		BasicConfigurator.configure();

		super.init();
		long id = 1;

		fullparameters = null;
		try {
			fullparameters = loadFullParameters();
			fullparameters.setCheckNetwork(1);

			bean1 = new Network();
			bean1.setId(id++);
			bean1.setObjectId("test1:GroupOfLine:1");
			bean1.setName("test1");
			bean2 = new Network();
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
		test.setPathName( "target/referential/test");
		File f = new File("target/referential/test");
		if (f.exists())
			try {
				FileUtils.deleteDirectory(f);
			} catch (IOException e) {
				e.printStackTrace();
			}
		f.mkdirs();
		test.setReferential( "chouette_gui");
		test.setAction( VALIDATOR);
		context.put("testng", "true");
		context.put(OPTIMIZED, Boolean.FALSE);
		return context;

	}

	@Test(groups = { "network" }, description = "4-Network-1 no test", priority = 1)
	public void verifyTest4_1_notest() throws Exception {
		// 4-Network-1 : check columns
		log.info(Color.BLUE + "4-Network-1 no test" + Color.NORMAL);
		Context context = initValidatorContext();
		Assert.assertNotNull(fullparameters, "no parameters for test");
		context.put(VALIDATION_REPORT, new ValidationReport());

		fullparameters.setCheckNetwork(0);
		context.put(VALIDATION, fullparameters);
		ValidationData data = new ValidationData();
		data.getNetworks().addAll(beansFor4);
		context.put(VALIDATION_DATA, data);

		checkPoint.validate(context, null);

		ValidationReport report = (ValidationReport) context.get(VALIDATION_REPORT);
		Assert.assertTrue(report.findCheckPointReportByName("4-Network-1") == null,
				" report must not have item 4-Network-1");


		fullparameters.setCheckNetwork(1);
		context.put(VALIDATION_REPORT, new ValidationReport());

		checkPoint.validate(context, null);
		report = (ValidationReport) context.get(VALIDATION_REPORT);
		Assert.assertTrue(report.findCheckPointReportByName("4-Network-1") != null, " report must have item 4-ConnectionLink-1");
		Assert.assertEquals(report.findCheckPointReportByName("4-Network-1").getCheckPointErrorCount(), 0,
				" checkpoint must have no detail");


	}

	@Test(groups = { "network" }, description = "4-Network-1 unicity", priority = 2)
	public void verifyTest4_1_unique() throws Exception {
		// 4-Network-1 : check columns
		log.info(Color.BLUE + "4-Network-1 unicity" + Color.NORMAL);
		Context context = initValidatorContext();
		Assert.assertNotNull(fullparameters, "no parameters for test");

		context.put(VALIDATION_REPORT, new ValidationReport());

		fullparameters.setCheckNetwork(1);
		fullparameters.getNetwork().getObjectId().setUnique(1);

		context.put(VALIDATION, fullparameters);
		ValidationData data = new ValidationData();
		data.getNetworks().addAll(beansFor4);
		context.put(VALIDATION_DATA, data);
		checkPoint.validate(context, null);
		fullparameters.getNetwork().getObjectId().setUnique(0);

		ValidationReport report = (ValidationReport) context.get(VALIDATION_REPORT);

		Assert.assertFalse(report.getCheckPoints().isEmpty(), " report must have items");
		Assert.assertNotNull(report.findCheckPointReportByName("4-Network-1"), " report must have 1 item on key "+"4-Network-1");
		CheckPointReport checkPointReport = report.findCheckPointReportByName("4-Network-1");
		Assert.assertEquals(checkPointReport.getCheckPointErrorCount(), 1, " checkpoint must have "+1+" detail");
	}

	@Test(groups = { "network" }, description = "4-Network-1 pattern numeric", priority = 3)
	public void verifyTest4_1_pattern_numeric() throws Exception {
		// 4-Network-1 : check columns
		log.info(Color.BLUE + "4-Network-1 pattern numeric" + Color.NORMAL);
		Context context = initValidatorContext();
		Assert.assertNotNull(fullparameters, "no parameters for test");

		context.put(VALIDATION_REPORT, new ValidationReport());

		// pattern
		bean1.setRegistrationNumber("1234");
		bean2.setRegistrationNumber("az234ZDER");
		
		fullparameters.setCheckNetwork(1);
		fullparameters.getNetwork().getRegistrationNumber().setPattern(AbstractValidation.PATTERN_OPTION.num.ordinal());

		context.put(VALIDATION, fullparameters);
		ValidationData data = new ValidationData();
		data.getNetworks().addAll(beansFor4);
		context.put(VALIDATION_DATA, data);
		checkPoint.validate(context, null);

		ValidationReport report = (ValidationReport) context.get(VALIDATION_REPORT);
		checkReportForTest(report, "4-Network-1",1);
	}

	@Test(groups = { "network" }, description = "4-Network-1 pattern alphabetic", priority = 4)
	public void verifyTest4_1_pattern_alpha() throws Exception {
		// 4-Network-1 : check columns
		log.info(Color.BLUE + "4-Network-1 pattern alphabetic" + Color.NORMAL);
		Context context = initValidatorContext();
		Assert.assertNotNull(fullparameters, "no parameters for test");

		context.put(VALIDATION_REPORT, new ValidationReport());
		bean1.setRegistrationNumber("AzErTy");
		bean2.setRegistrationNumber("az234ZDER");

		fullparameters.getNetwork().getRegistrationNumber().setPattern(AbstractValidation.PATTERN_OPTION.alpha.ordinal());

		context.put(VALIDATION, fullparameters);
		ValidationData data = new ValidationData();
		data.getNetworks().addAll(beansFor4);
		context.put(VALIDATION_DATA, data);
		checkPoint.validate(context, null);

		ValidationReport report = (ValidationReport) context.get(VALIDATION_REPORT);
		checkReportForTest(report, "4-Network-1",1);
	}

	@Test(groups = { "network" }, description = "4-Network-1 pattern uppercase", priority = 5)
	public void verifyTest4_1_pattern_upper() throws Exception {
		// 4-Network-1 : check columns
		log.info(Color.BLUE + "4-Network-1 pattern uppercase" + Color.NORMAL);
		Context context = initValidatorContext();
		Assert.assertNotNull(fullparameters, "no parameters for test");

		context.put(VALIDATION_REPORT, new ValidationReport());
		bean1.setRegistrationNumber("AZERTY");
		bean2.setRegistrationNumber("az234ZDER");

		fullparameters.getNetwork().getRegistrationNumber().setPattern(AbstractValidation.PATTERN_OPTION.upper.ordinal());

		context.put(VALIDATION, fullparameters);
		ValidationData data = new ValidationData();
		data.getNetworks().addAll(beansFor4);
		context.put(VALIDATION_DATA, data);
		checkPoint.validate(context, null);

		ValidationReport report = (ValidationReport) context.get(VALIDATION_REPORT);
		checkReportForTest(report, "4-Network-1",1);
	}

	@Test(groups = { "network" }, description = "4-Network-1 pattern lowercase", priority = 6)
	public void verifyTest4_1_pattern_lower() throws Exception {
		// 4-Network-1 : check columns
		log.info(Color.BLUE + "4-Network-1 pattern lowercase" + Color.NORMAL);
		Context context = initValidatorContext();
		Assert.assertNotNull(fullparameters, "no parameters for test");

		context.put(VALIDATION_REPORT, new ValidationReport());
		bean1.setRegistrationNumber("azerty");
		bean2.setRegistrationNumber("az234ZDER");

		fullparameters.getNetwork().getRegistrationNumber().setPattern(AbstractValidation.PATTERN_OPTION.lower.ordinal());

		context.put(VALIDATION, fullparameters);
		ValidationData data = new ValidationData();
		data.getNetworks().addAll(beansFor4);
		context.put(VALIDATION_DATA, data);
		checkPoint.validate(context, null);

		ValidationReport report = (ValidationReport) context.get(VALIDATION_REPORT);
		checkReportForTest(report, "4-Network-1",1);
	}

	@Test(groups = { "network" }, description = "4-Network-1 min_size alpha", priority = 7)
	public void verifyTest4_1_min_size_alpha() throws Exception {
		// 4-Network-1 : check columns
		log.info(Color.BLUE + "4-Network-1 min_size alpha" + Color.NORMAL);
		Context context = initValidatorContext();
		Assert.assertNotNull(fullparameters, "no parameters for test");

		context.put(VALIDATION_REPORT, new ValidationReport());

		// minsize alpha
		bean1.setRegistrationNumber("1234");
		bean2.setRegistrationNumber("");
		fullparameters.getNetwork().getRegistrationNumber().setPattern(AbstractValidation.PATTERN_OPTION.free.ordinal());
		fullparameters.getNetwork().getRegistrationNumber().setMinSize("1");

		context.put(VALIDATION, fullparameters);
		ValidationData data = new ValidationData();
		data.getNetworks().addAll(beansFor4);
		context.put(VALIDATION_DATA, data);
		checkPoint.validate(context, null);
		fullparameters.getNetwork().getRegistrationNumber().setMinSize("");

		ValidationReport report = (ValidationReport) context.get(VALIDATION_REPORT);
		checkReportForTest(report, "4-Network-1",1);

	}

	@Test(groups = { "network" }, description = "4-Network-1 max_size alpha", priority = 8)
	public void verifyTest4_1_max_size_alpha() throws Exception {
		// 4-Network-1 : check columns
		log.info(Color.BLUE + "4-Network-1 max_size alpha" + Color.NORMAL);
		Context context = initValidatorContext();
		Assert.assertNotNull(fullparameters, "no parameters for test");

		context.put(VALIDATION_REPORT, new ValidationReport());
		// maxsize alpha
		bean1.setRegistrationNumber("12345");
		bean2.setRegistrationNumber("123456");
		fullparameters.getNetwork().getRegistrationNumber().setMaxSize("5");

		context.put(VALIDATION, fullparameters);
		ValidationData data = new ValidationData();
		data.getNetworks().addAll(beansFor4);
		context.put(VALIDATION_DATA, data);
		checkPoint.validate(context, null);
		fullparameters.getNetwork().getRegistrationNumber().setMaxSize("");

		ValidationReport report = (ValidationReport) context.get(VALIDATION_REPORT);
		checkReportForTest(report, "4-Network-1",1);
	}

	@Test(groups = { "network" }, description = "4-Network-1 min_size numeric", priority = 9)
	public void verifyTest4_1_min_size_numeric() throws Exception {
		// 4-Network-1 : check columns
		log.info(Color.BLUE + "4-Network-1 min_size numeric" + Color.NORMAL);
		Context context = initValidatorContext();
		Assert.assertNotNull(fullparameters, "no parameters for test");

		context.put(VALIDATION_REPORT, new ValidationReport());
		// minsize num
		bean1.setRegistrationNumber("124");
		bean2.setRegistrationNumber("123");
		fullparameters.getNetwork().getRegistrationNumber().setPattern(AbstractValidation.PATTERN_OPTION.num.ordinal());
		fullparameters.getNetwork().getRegistrationNumber().setMinSize("124");

		context.put(VALIDATION, fullparameters);
		ValidationData data = new ValidationData();
		data.getNetworks().addAll(beansFor4);
		context.put(VALIDATION_DATA, data);
		checkPoint.validate(context, null);
		fullparameters.getNetwork().getRegistrationNumber().setMinSize("");

		ValidationReport report = (ValidationReport) context.get(VALIDATION_REPORT);
		checkReportForTest(report, "4-Network-1",1);
	}

	@Test(groups = { "network" }, description = "4-Network-1 max_size numeric", priority = 10)
	public void verifyTest4_1_max_size_numeric() throws Exception {
		// 4-Network-1 : check columns
		log.info(Color.BLUE + "4-Network-1 max_size numeric" + Color.NORMAL);
		Context context = initValidatorContext();
		Assert.assertNotNull(fullparameters, "no parameters for test");

		context.put(VALIDATION_REPORT, new ValidationReport());
		// maxsize num
		bean1.setRegistrationNumber("1240");
		bean2.setRegistrationNumber("1241");

		fullparameters.getNetwork().getRegistrationNumber().setPattern(AbstractValidation.PATTERN_OPTION.num.ordinal());
		fullparameters.getNetwork().getRegistrationNumber().setMaxSize("1240");

		context.put(VALIDATION, fullparameters);
		ValidationData data = new ValidationData();
		data.getNetworks().addAll(beansFor4);
		context.put(VALIDATION_DATA, data);
		checkPoint.validate(context, null);

		ValidationReport report = (ValidationReport) context.get(VALIDATION_REPORT);
		checkReportForTest(report, "4-Network-1",1);
	}

	/**
	 * @param report
	 */
	protected List<CheckPointErrorReport> checkReportForTest(ValidationReport report, String key, int detailSize) {
		Assert.assertFalse(report.getCheckPoints().isEmpty(), " report must have items");
		Assert.assertNotNull(report.findCheckPointReportByName(key), " report must have 1 item on key "+key);
		CheckPointReport checkPointReport = report.findCheckPointReportByName(key);
		Assert.assertEquals(checkPointReport.getCheckPointErrorCount(), detailSize, " checkpoint must have "+detailSize+" detail");
		List<CheckPointErrorReport> details = super.checkReportForTest(report,key,-1);
		CheckPointErrorReport detail = details.get(0);
		Assert.assertEquals(detail.getReferenceValue(), "RegistrationNumber", "detail must refer column");
		Assert.assertEquals(detail.getValue(), bean2.getRegistrationNumber(), "detail must refer value");
		return details;
	}

}
