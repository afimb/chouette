package mobi.chouette.exchange.gtfs.validation;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Locale;

import javax.naming.InitialContext;
import javax.naming.NamingException;

import mobi.chouette.common.Context;
import mobi.chouette.common.chain.Command;
import mobi.chouette.common.chain.CommandFactory;
import mobi.chouette.exchange.gtfs.validation.Constant;
import mobi.chouette.exchange.gtfs.JobDataTest;
import mobi.chouette.exchange.gtfs.importer.GtfsImportParameters;
import mobi.chouette.exchange.gtfs.importer.GtfsInitImportCommand;
import mobi.chouette.exchange.gtfs.importer.GtfsValidationCommand;
import mobi.chouette.exchange.gtfs.importer.GtfsValidationRulesCommand;
import mobi.chouette.exchange.importer.UncompressCommand;
import mobi.chouette.exchange.report.ActionReport;
import mobi.chouette.exchange.report.ReportConstant;
import mobi.chouette.exchange.validation.report.CheckPoint;
import mobi.chouette.exchange.validation.report.CheckPoint.RESULT;
import mobi.chouette.exchange.validation.report.Detail;
import mobi.chouette.exchange.validation.report.ValidationReport;
import mobi.chouette.persistence.hibernate.ContextHolder;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import org.codehaus.jettison.json.JSONException;
import org.testng.Assert;

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

	protected final Context initImportContext(boolean all) {
		// init();
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

	protected final CheckPoint verifyValidation(Logger log, String testFile, String mandatoryErrorTest,
			CheckPoint.SEVERITY severity, RESULT status, boolean all) throws Exception {
		Context context = initImportContext(all);
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

	/**
	 * @param log
	 * @param mandatoryTest
	 * @param importReport
	 * @param valReport
	 * @param state
	 * @return
	 */
	private CheckPoint checkMandatoryTest(Logger log, Context context, String mandatoryTest,
			CheckPoint.SEVERITY severity, RESULT state) {
		ValidationReport valReport = (ValidationReport) context.get(MAIN_VALIDATION_REPORT);
		for (CheckPoint phase : valReport.getCheckPoints()) {
			if (!phase.getState().equals(RESULT.UNCHECK))
			{
				// log.info(phase.getName() + ":" + phase.getState());
				if (phase.getName().equals(mobi.chouette.exchange.gtfs.validation.Constant.GTFS_1_GTFS_CSV_1))
					Assert.assertFalse(phase.getState().equals(RESULT.NOK), "fatal error GTFS_1_GTFS_CSV_1 encontered");
			}
		}
		if (mandatoryTest.equals("NONE")) {
			for (CheckPoint phase : valReport.getCheckPoints()) {
				if (phase.getSeverity().equals(CheckPoint.SEVERITY.ERROR))
					Assert.assertFalse(phase.getState().equals(RESULT.NOK), phase.getName() + " must have status " + state);
			}
			return null;
		} else {
			CheckPoint foundItem = null;
			for (CheckPoint cp : valReport.getCheckPoints()) {
				if (cp.getName().equals(mandatoryTest)) {
					foundItem = cp;
					break;
				}

			}
			Assert.assertNotNull(foundItem, mandatoryTest + " must be reported");
			Assert.assertEquals(foundItem.getSeverity(), severity, mandatoryTest + " must have severity " + severity);
			Assert.assertEquals(foundItem.getState(), state, mandatoryTest + " must have status " + state);
			if (foundItem.getState().equals(RESULT.NOK)) {
				String detailKey = mandatoryTest.replaceAll("-", "_").toLowerCase();
				Assert.assertNotEquals(foundItem.getDetails(), 0, "details should be present");
				List<Detail> details = foundItem.getDetails();
				for (Detail detail : details) {
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
