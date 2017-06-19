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
public class ValidationLinesUpdater extends AbstractTestValidation {
	
	@EJB 
	LineDAO lineDao;

	@PersistenceContext(unitName = "referential")
	EntityManager em;

	@Inject
	UserTransaction utx;

	@Deployment
	public static EnterpriseArchive createDeployment() {

		return prepareDeployment(ValidationLinesUpdater.class);
	}
	
	@BeforeGroups(groups = { "Line" })
	public void init() {
		super.init();
	}
	
	@Test(groups = { "Line" }, description = "2-DATABASE-Line-1", priority = 1)
	public void verifyTest2_1() throws Exception {
		// 2-DATABASE-Line-1 : check columns
		log.info(Color.BLUE + "2-DATABASE-Line-1" + Color.NORMAL);
	
		importLines("Ligne_OK.xml", 1, 1, true);
		
		
		utx.begin();
	    em.joinTransaction();
		Context context = initImportContext();


		NeptuneImporterCommand command = (NeptuneImporterCommand) CommandFactory.create(initialContext,
				NeptuneImporterCommand.class.getName());
		copyFile("Ligne_2_Database_Line_Network_Test.xml");
		JobDataTest test = (JobDataTest) context.get(JOB_DATA);
		test.setInputFilename("Ligne_2_Database_Line_Network_Test.xml");
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
		CheckPointReport checkPointReport = validationReport.findCheckPointReportByName("2-DATABASE-Line-1");
		Assert.assertNotNull(checkPointReport, "report must contain a 2-DATABASE-Line-1 checkPoint");

		Assert.assertEquals(checkPointReport.getState(), ValidationReporter.RESULT.NOK, " checkPointReport must be nok");
		
		utx.rollback();


	}
	
	@Test(groups = { "Line" }, description = "2-DATABASE-Line-2", priority = 2)
	public void verifyTest2_2() throws Exception {
		// 2-DATABASE-Line-1 : check columns
		log.info(Color.BLUE + "2-DATABASE-Line-2" + Color.NORMAL);
		
		importLines("Ligne_OK.xml", 1, 1, true);
		
		
		utx.begin();
	    em.joinTransaction();
		Context context = initImportContext();


		NeptuneImporterCommand command = (NeptuneImporterCommand) CommandFactory.create(initialContext,
				NeptuneImporterCommand.class.getName());
		copyFile("Ligne_2_Database_Line_Company_Test.xml");
		JobDataTest test = (JobDataTest) context.get(JOB_DATA);
		test.setInputFilename("Ligne_2_Database_Line_Company_Test.xml");
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
		CheckPointReport checkPointReport = validationReport.findCheckPointReportByName("2-DATABASE-Line-2");
		Assert.assertNotNull(checkPointReport, "report must contain a 2-DATABASE-Line-2 checkPoint");

		Assert.assertEquals(checkPointReport.getState(), ValidationReporter.RESULT.NOK, " checkPointReport must be nok");
		
		utx.rollback();


	}
	
	@Test(groups = { "Line" }, description = "2-DATABASE-Route-1", priority = 3)
	public void verifyTest2_Route_1() throws Exception {
		// 2-DATABASE-Route-1 : check columns
		log.info(Color.BLUE + "2-DATABASE-Route-1" + Color.NORMAL);
		
		importLines("Ligne_OK.xml", 1, 1, true);
		
		
		utx.begin();
	    em.joinTransaction();
		Context context = initImportContext();


		NeptuneImporterCommand command = (NeptuneImporterCommand) CommandFactory.create(initialContext,
				NeptuneImporterCommand.class.getName());
		copyFile("Ligne_2_Database_Line_Route_Test.xml");
		JobDataTest test = (JobDataTest) context.get(JOB_DATA);
		test.setInputFilename("Ligne_2_Database_Line_Route_Test.xml");
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
		CheckPointReport checkPointReport = validationReport.findCheckPointReportByName("2-DATABASE-Route-1");
		Assert.assertNotNull(checkPointReport, "report must contain a 2-DATABASE-Route-1 checkPoint");

		Assert.assertEquals(checkPointReport.getState(), ValidationReporter.RESULT.NOK, " checkPointReport must be nok");
		
		utx.rollback();


	}

}
