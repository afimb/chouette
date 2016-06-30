package mobi.chouette.exchange.gtfs.validation;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.naming.InitialContext;
import javax.naming.NamingException;

import mobi.chouette.common.Context;
import mobi.chouette.common.chain.Command;
import mobi.chouette.common.chain.CommandFactory;
import mobi.chouette.exchange.gtfs.JobDataTest;
import mobi.chouette.exchange.gtfs.importer.GtfsImportParameters;
import mobi.chouette.exchange.gtfs.importer.GtfsInitImportCommand;
import mobi.chouette.exchange.gtfs.importer.GtfsValidationCommand;
import mobi.chouette.exchange.gtfs.importer.GtfsValidationRulesCommand;
import mobi.chouette.exchange.importer.UncompressCommand;
import mobi.chouette.exchange.report.ActionReport2;
import mobi.chouette.exchange.report.ReportConstant;
import mobi.chouette.exchange.validation.report.CheckPointErrorReport;
import mobi.chouette.exchange.validation.report.CheckPointReport;
import mobi.chouette.exchange.validation.report.ValidationReport2;
import mobi.chouette.exchange.validation.report.ValidationReporter.RESULT;
import mobi.chouette.persistence.hibernate.ContextHolder;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import org.codehaus.jettison.json.JSONException;
import org.testng.Assert;
import org.testng.Reporter;

public abstract class AbstractTests implements Constant, ReportConstant {

	protected static InitialContext initialContext;

	public final void copyFile(Context context, String fileName) throws IOException {
		File srcFile = new File(getPath(), fileName);
		File destFile = new File("target/referential/test", fileName);
		FileUtils.copyFile(srcFile, destFile);
		JobDataTest test = (JobDataTest) context.get(JOB_DATA);
		test.setInputFilename(fileName);

	}

	public abstract String getPath();
	
	protected void init() {
		Locale.setDefault(Locale.ENGLISH);
		if (initialContext == null) {
			try {
				initialContext = new InitialContext();
			} catch (NamingException e) {
				e.printStackTrace();
			}
			BasicConfigurator.configure();
		}
	}

	protected final Context initImportContext(Context context, boolean all) {
		// init();
		ContextHolder.setContext("chouette_gui"); // set tenant schema

		context.put(INITIAL_CONTEXT, initialContext);
		context.put(REPORT, new ActionReport2());
		context.put(VALIDATION_REPORT, new ValidationReport2());
		GtfsImportParameters configuration = new GtfsImportParameters();
		context.put(CONFIGURATION, configuration);
		configuration.setName("name");
		configuration.setUserName("userName");
		configuration.setNoSave(true);
		configuration.setOrganisationName("organisation");
		configuration.setReferentialName("test");
		configuration.setObjectIdPrefix("test");
		if (all)
			configuration.setReferencesType("line");
		else
			configuration.setReferencesType("stop_area");
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
		test.setAction(IMPORTER);
		test.setType("gtfs");
		context.put("testng", "true");
		context.put(OPTIMIZED, Boolean.FALSE);
		return context;

	}

	protected final CheckPointReport verifyValidation(Logger log, Context context, String testFile, String mandatoryErrorTest,
			CheckPointReport.SEVERITY severity, RESULT status, boolean all) throws Exception {
		initImportContext(context, all);
		copyFile(context, testFile + ".zip");

		Command uncompress = CommandFactory.create(initialContext, UncompressCommand.class.getName());
		uncompress.execute(context);
		Command importCommand = CommandFactory.create(initialContext, GtfsInitImportCommand.class.getName());
		importCommand.execute(context);
		Command initValidation = CommandFactory.create(initialContext, GtfsValidationRulesCommand.class.getName());
		initValidation.execute(context);
		try {
			Command validation = CommandFactory.create(initialContext, GtfsValidationCommand.class.getName());
			validation.execute(context);
		} catch (Exception ex) {
			if (mandatoryErrorTest.equals("NONE") || ex instanceof RuntimeException)
				throw ex;
			log.info("Exception returned " + ex.getClass().getName() + " :" + ex.getMessage());
		}

		return checkMandatoryTest(log, context, mandatoryErrorTest, severity, status);

	}

	protected List<CheckPointErrorReport> getDetails(Context context, CheckPointReport checkPoint)
	{
		List<CheckPointErrorReport> details = new ArrayList<>();
		ValidationReport2 valReport = (ValidationReport2) context.get(VALIDATION_REPORT);
		
		List<CheckPointErrorReport> errors = valReport.getCheckPointErrors();
		for (Integer rank : checkPoint.getCheckPointErrorsKeys())
		{
			details.add(errors.get(rank.intValue()));
		}
		return details;
		
	}
	
	/**
	 * @param log
	 * @param mandatoryTest
	 * @param importReport
	 * @param valReport
	 * @param state
	 * @return
	 */
	private CheckPointReport checkMandatoryTest(Logger log, Context context, String mandatoryTest,
			CheckPointReport.SEVERITY severity, RESULT state) {
		ValidationReport2 valReport = (ValidationReport2) context.get(VALIDATION_REPORT);
		for (CheckPointReport phase : valReport.getCheckPoints()) {
			if (!phase.getState().equals(RESULT.UNCHECK))
			{
				// log.info(phase.getName() + ":" + phase.getState());
				if (phase.getName().equals(mobi.chouette.exchange.gtfs.validation.Constant.GTFS_1_GTFS_CSV_1))
					Assert.assertFalse(phase.getState().equals(RESULT.NOK), "fatal error GTFS_1_GTFS_CSV_1 encontered");
			}
		}
		if (mandatoryTest.equals("NONE")) {
			for (CheckPointReport phase : valReport.getCheckPoints()) {
				if (phase.getSeverity().equals(CheckPointReport.SEVERITY.ERROR))
					Assert.assertFalse(phase.getState().equals(RESULT.NOK), phase.getName() + " must have status " + state);
			}
			return null;
		} else {
			CheckPointReport foundItem = null;
			for (CheckPointReport cp : valReport.getCheckPoints()) {
				if (cp.getName().equals(mandatoryTest)) {
					foundItem = cp;
					break;
				}

			}
			if (foundItem == null)
			{
				Reporter.log(valReport.toString(),true);
			}
			Assert.assertNotNull(foundItem, mandatoryTest + " must be reported");
			Assert.assertEquals(foundItem.getSeverity(), severity, mandatoryTest + " must have severity " + severity);
			Assert.assertEquals(foundItem.getState(), state, mandatoryTest + " must have status " + state);
			if (foundItem.getState().equals(RESULT.NOK)) {
				String detailKey = mandatoryTest.replaceAll("-", "_").toLowerCase();
				Assert.assertNotEquals(foundItem.getCheckPointErrorsKeys().size(), 0, "details should be present");
				List<CheckPointErrorReport> details = valReport.getCheckPointErrors();
				for (Integer rank : foundItem.getCheckPointErrorsKeys()) {
					CheckPointErrorReport detail = details.get(rank.intValue());
					Assert.assertTrue(
							detail.getKey().startsWith(detailKey),
							"details key should start with test key : expected " + detailKey + ", found : "
									+ detail.getKey());
				}
			}
			try {
				log.info("detail :"+foundItem.toJson().toString(2));
			} catch (JSONException e) {
				log.error("unable to convert to json");
			}
			return foundItem; // for extra check
		}
	}

}
