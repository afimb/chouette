package mobi.chouette.exchange.importer.updater;

import javax.ejb.EJB;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.UserTransaction;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Color;
import mobi.chouette.common.Context;
import mobi.chouette.common.chain.CommandFactory;
import mobi.chouette.dao.LineDAO;
import mobi.chouette.exchange.neptune.importer.NeptuneImportParameters;
import mobi.chouette.exchange.neptune.importer.NeptuneImporterCommand;
import mobi.chouette.exchange.validation.report.CheckPointReport;
import mobi.chouette.exchange.validation.report.ValidationReport;
import mobi.chouette.exchange.validation.report.ValidationReporter;
import mobi.chouette.exchange.validator.JobDataTest;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.shrinkwrap.api.spec.EnterpriseArchive;
import org.testng.Assert;
import org.testng.annotations.BeforeGroups;
import org.testng.annotations.Test;

@Log4j
public class ValidationStopAreasUpdater extends AbstractTestValidation {
	@EJB 
	LineDAO lineDao;

	@PersistenceContext(unitName = "referential")
	EntityManager em;

	@Inject
	UserTransaction utx;

	@Deployment
	public static EnterpriseArchive createDeployment() {

		return prepareDeployment(ValidationStopAreasUpdater.class);
	}

	@BeforeGroups(groups = { "StopArea" })
	public void init() {
		super.init();
	}

	@Test(groups = { "StopArea" }, description = "2-DATABASE-StopArea-1", priority = 1)
	public void verifyTest2_1() throws Exception {
		// 2-DATABASE-StopArea-1 : check columns
		log.info(Color.BLUE + "2-DATABASE-StopArea-1" + Color.NORMAL);
	
		importLines("Ligne_OK.xml", 1, 1, true);
		
		
		utx.begin();
	    em.joinTransaction();
		Context context = initImportContext();


		NeptuneImporterCommand command = (NeptuneImporterCommand) CommandFactory.create(initialContext,
				NeptuneImporterCommand.class.getName());
		copyFile("Ligne_2_Database_Stop_Area_Parent_Test.xml");
		JobDataTest test = (JobDataTest) context.get(JOB_DATA);
		test.setInputFilename("Ligne_2_Database_Stop_Area_Parent_Test.xml");
		NeptuneImportParameters configuration = (NeptuneImportParameters) context.get(CONFIGURATION);
		configuration.setNoSave(false);
		configuration.setCleanRepository(false);
		try {
			command.execute(context);
		} catch (Exception ex) {
			log.error("test failed", ex);
			throw ex;
		}
				
		ValidationReport validationReport = (ValidationReport) context.get(VALIDATION_REPORT);
		CheckPointReport checkPointReport = validationReport.findCheckPointReportByName("2-DATABASE-StopArea-1");
		Assert.assertNotNull(checkPointReport, "report must contain a 2-DATABASE-StopArea-1 checkPoint");

		Assert.assertEquals(checkPointReport.getState(), ValidationReporter.RESULT.NOK, " checkPointReport must be nok");
		
		utx.rollback();


	}
	
	@Test(groups = { "StopArea" }, description = "2-DATABASE-StopArea-2", priority = 2)
	public void verifyTest2_2() throws Exception {
		// 2-DATABASE-StopArea-2 : check columns
		log.info(Color.BLUE + "2-DATABASE-StopArea-2" + Color.NORMAL);
	
		importLines("Ligne_OK.xml", 1, 1, true);
		
		
		utx.begin();
	    em.joinTransaction();
		Context context = initImportContext();


		NeptuneImporterCommand command = (NeptuneImporterCommand) CommandFactory.create(initialContext,
				NeptuneImporterCommand.class.getName());
		copyFile("Ligne_2_Database_Stop_Area_Type_Test.xml");
		JobDataTest test = (JobDataTest) context.get(JOB_DATA);
		test.setInputFilename("Ligne_2_Database_Stop_Area_Type_Test.xml");
		NeptuneImportParameters configuration = (NeptuneImportParameters) context.get(CONFIGURATION);
		configuration.setNoSave(false);
		configuration.setCleanRepository(false);
		try {
			command.execute(context);
		} catch (Exception ex) {
			log.error("test failed", ex);
			throw ex;
		}
				
		ValidationReport validationReport = (ValidationReport) context.get(VALIDATION_REPORT);
		CheckPointReport checkPointReport = validationReport.findCheckPointReportByName("2-DATABASE-StopArea-2");
		Assert.assertNotNull(checkPointReport, "report must contain a 2-DATABASE-StopArea-2 checkPoint");

		Assert.assertEquals(checkPointReport.getState(), ValidationReporter.RESULT.NOK, " checkPointReport must be nok");
		
		utx.rollback();


	}
	
	@Test(groups = { "StopArea" }, description = "2-DATABASE-AccessPoint-1", priority = 3)
	public void verifyTest2_AccessPoint_1() throws Exception {
		// 2-DATABASE-AccessPoint-1 : check columns
		log.info(Color.BLUE + "2-DATABASE-AccessPoint-1" + Color.NORMAL);
	
		importLines("Ligne_OK.xml", 1, 1, true);
		
		
		utx.begin();
	    em.joinTransaction();
		Context context = initImportContext();


		NeptuneImporterCommand command = (NeptuneImporterCommand) CommandFactory.create(initialContext,
				NeptuneImporterCommand.class.getName());
		copyFile("Ligne_2_Database_AccessPoint_ContainedInStopArea_Test.xml");
		JobDataTest test = (JobDataTest) context.get(JOB_DATA);
		test.setInputFilename("Ligne_2_Database_AccessPoint_ContainedInStopArea_Test.xml");
		NeptuneImportParameters configuration = (NeptuneImportParameters) context.get(CONFIGURATION);
		configuration.setNoSave(false);
		configuration.setCleanRepository(false);
		try {
			command.execute(context);
		} catch (Exception ex) {
			log.error("test failed", ex);
			throw ex;
		}
				
		ValidationReport validationReport = (ValidationReport) context.get(VALIDATION_REPORT);
		CheckPointReport checkPointReport = validationReport.findCheckPointReportByName("2-DATABASE-AccessPoint-1");
		Assert.assertNotNull(checkPointReport, "report must contain a 2-DATABASE-AccessPoint-1 checkPoint");

		Assert.assertEquals(checkPointReport.getState(), ValidationReporter.RESULT.NOK, " checkPointReport must be nok");
		
		utx.rollback();


	}
	
	@Test(groups = { "StopArea" }, description = "2-DATABASE-ConnectionLink-1-1", priority = 4)
	public void verifyTest2_ConnectionLink_1_1() throws Exception {
		// 2-DATABASE-ConnectionLink-1-1 : check columns
		log.info(Color.BLUE + "2-DATABASE-ConnectionLink-1 1" + Color.NORMAL);
	
		importLines("Ligne_OK.xml", 1, 1, true);
		
		
		utx.begin();
	    em.joinTransaction();
		Context context = initImportContext();


		NeptuneImporterCommand command = (NeptuneImporterCommand) CommandFactory.create(initialContext,
				NeptuneImporterCommand.class.getName());
		copyFile("Ligne_2_Database_ConnectionLink_1_1_Test.xml");
		JobDataTest test = (JobDataTest) context.get(JOB_DATA);
		test.setInputFilename("Ligne_2_Database_ConnectionLink_1_1_Test.xml");
		NeptuneImportParameters configuration = (NeptuneImportParameters) context.get(CONFIGURATION);
		configuration.setNoSave(false);
		configuration.setCleanRepository(false);
		try {
			command.execute(context);
		} catch (Exception ex) {
			log.error("test failed", ex);
			throw ex;
		}
				
		ValidationReport validationReport = (ValidationReport) context.get(VALIDATION_REPORT);
		CheckPointReport checkPointReport = validationReport.findCheckPointReportByName("2-DATABASE-ConnectionLink-1");
		Assert.assertNotNull(checkPointReport, "report must contain a 2-DATABASE-ConnectionLink-1 checkPoint");

		Assert.assertEquals(checkPointReport.getState(), ValidationReporter.RESULT.NOK, " checkPointReport must be nok");
		
		utx.rollback();


	}
	
	@Test(groups = { "StopArea" }, description = "2-DATABASE-ConnectionLink-1-2", priority = 5)
	public void verifyTest2_ConnectionLink_1_2() throws Exception {
		// 2-DATABASE-ConnectionLink-1-1 : check columns
		log.info(Color.BLUE + "2-DATABASE-ConnectionLink-1 2" + Color.NORMAL);
	
		importLines("Ligne_OK.xml", 1, 1, true);
		
		
		utx.begin();
	    em.joinTransaction();
		Context context = initImportContext();


		NeptuneImporterCommand command = (NeptuneImporterCommand) CommandFactory.create(initialContext,
				NeptuneImporterCommand.class.getName());
		copyFile("Ligne_2_Database_ConnectionLink_1_2_Test.xml");
		JobDataTest test = (JobDataTest) context.get(JOB_DATA);
		test.setInputFilename("Ligne_2_Database_ConnectionLink_1_2_Test.xml");
		NeptuneImportParameters configuration = (NeptuneImportParameters) context.get(CONFIGURATION);
		configuration.setNoSave(false);
		configuration.setCleanRepository(false);
		try {
			command.execute(context);
		} catch (Exception ex) {
			log.error("test failed", ex);
			throw ex;
		}
				
		ValidationReport validationReport = (ValidationReport) context.get(VALIDATION_REPORT);
		CheckPointReport checkPointReport = validationReport.findCheckPointReportByName("2-DATABASE-ConnectionLink-1");
		Assert.assertNotNull(checkPointReport, "report must contain a 2-DATABASE-ConnectionLink-1 checkPoint");

		Assert.assertEquals(checkPointReport.getState(), ValidationReporter.RESULT.NOK, " checkPointReport must be nok");
		
		utx.rollback();


	}

}
