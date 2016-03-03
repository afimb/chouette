package mobi.chouette.exchange.regtopp.importer;

import java.io.File;
import java.io.IOException;

import javax.ejb.EJB;
import javax.inject.Inject;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.UserTransaction;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.testng.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.testng.Assert;
import org.testng.annotations.Test;

import mobi.chouette.common.Context;
import mobi.chouette.common.chain.CommandFactory;
import mobi.chouette.dao.LineDAO;
import mobi.chouette.dao.VehicleJourneyDAO;
import mobi.chouette.exchange.regtopp.JobDataTest;
import mobi.chouette.exchange.regtopp.RegtoppTestUtils;
import mobi.chouette.exchange.report.ActionReport;
import mobi.chouette.exchange.validation.report.ValidationReport;
import mobi.chouette.model.Line;
import mobi.chouette.persistence.hibernate.ContextHolder;

public class RegtopImporterCommandTest extends Arquillian implements mobi.chouette.common.Constant{

	private InitialContext initialContext ;

	
	private void init()
	{
		if (initialContext == null)
		{
			try {
				initialContext = new InitialContext();
		
			} catch (NamingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	
	@EJB
	LineDAO lineDao;

	@EJB
	VehicleJourneyDAO vjDao;


	@PersistenceContext(unitName = "referential")
	EntityManager em;

	@Inject
	UserTransaction utx;

	@Deployment
	public static WebArchive createDeployment() {

		WebArchive result;

		File[] files = Maven.resolver().loadPomFromFile("pom.xml")
				.resolve("mobi.chouette:mobi.chouette.exchange.regtopp").withTransitivity().asFile();

		result = ShrinkWrap.create(WebArchive.class, "test.war")
				.addAsWebInfResource("postgres-ds.xml")
				.addAsLibraries(files)
				.addClass(RegtoppTestUtils.class)
				.addClass(JobDataTest.class)
				.addAsResource(EmptyAsset.INSTANCE, "beans.xml");
		return result;

	}

	
	protected Context initImportContext() {
		init();
		ContextHolder.setContext("chouette_gui"); // set tenant schema

		Context context = new Context();
		context.put(INITIAL_CONTEXT, initialContext);
		context.put(REPORT, new ActionReport());
		context.put(MAIN_VALIDATION_REPORT, new ValidationReport());
		RegtoppImportParameters configuration = new RegtoppImportParameters();
		context.put(CONFIGURATION, configuration);
		configuration.setName("name");
		configuration.setUserName("userName");
		configuration.setNoSave(true);
		configuration.setCleanRepository(true);
		configuration.setOrganisationName("organisation");
		configuration.setReferentialName("test");
		JobDataTest jobData = new JobDataTest();
		context.put(JOB_DATA,jobData);
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
		jobData.setType( "regtopp");
		context.put("testng", "true");
		context.put(OPTIMIZED, Boolean.FALSE);
		return context;

	}

	

	
	@Test
	public void importRegtopAtBStopArea() throws Exception {
		// Prepare context
		Context context = initImportContext();

		File f = new File("src/test/data/atb-20160118-20160619.zip");
		File dest = new File("target/referential/test");
		FileUtils.copyFileToDirectory(f, dest);
		JobDataTest job = (JobDataTest) context.get(JOB_DATA);
		job.setFilename(f.getName());

		RegtoppImporterCommand command = (RegtoppImporterCommand) CommandFactory.create(initialContext, RegtoppImporterCommand.class.getName());
		
		

		
	
		RegtoppImportParameters parameters = (RegtoppImportParameters) context.get(CONFIGURATION);
		parameters.setObjectIdPrefix("TST");
		parameters.setReferencesType("stop_area");
		
		boolean result = command.execute(context);

		ActionReport report = (ActionReport) context.get(REPORT);
		ValidationReport validationReport = (ValidationReport) context.get(VALIDATION_REPORT);
		
		
//		Assert.assertEquals(report.getResult(), STATUS_OK, "result");
//		Assert.assertEquals(report.getFiles().size(), 1, "file reported");
//		Assert.assertEquals(report.getLines().size(), 1, "line reported");
//		Reporter.log("report line :" + report.getLines().get(0).toString(), true);
//		Assert.assertEquals(report.getLines().get(0).getStatus(), LINE_STATE.OK, "line status");
//		RegtoppTestUtils.checkLine(context);
//		
//		Referential referential = (Referential) context.get(REFERENTIAL);
//		Assert.assertNotEquals(referential.getTimetables(),0, "timetables" );
//		Assert.assertNotEquals(referential.getSharedTimetables(),0, "shared timetables" );

		// line should be saved
		utx.begin();
		em.joinTransaction();
		Line line = lineDao.findByObjectId("NINOXE:Line:15574334");
		
//		RegtoppTestUtils.checkMinimalLine(line);
		
		utx.rollback();

		if(!result) {
			System.out.println(ToStringBuilder.reflectionToString(report,ToStringStyle.MULTI_LINE_STYLE));
			System.out.println(validationReport);
			
		}
		
		Assert.assertTrue(result,"Importer command execution failed: "+report.getFailure());
	}

	@Test
	public void importRegtopAtBLines() throws Exception {
		// Prepare context
		Context context = initImportContext();

		File f = new File("src/test/data/atb-20160118-20160619.zip");
		File dest = new File("target/referential/test");
		FileUtils.copyFileToDirectory(f, dest);
		JobDataTest job = (JobDataTest) context.get(JOB_DATA);
		job.setFilename(f.getName());

		RegtoppImporterCommand command = (RegtoppImporterCommand) CommandFactory.create(initialContext, RegtoppImporterCommand.class.getName());
		
		

		
	
		RegtoppImportParameters parameters = (RegtoppImportParameters) context.get(CONFIGURATION);
		parameters.setObjectIdPrefix("TST");
		parameters.setReferencesType("line");
		
		boolean result = command.execute(context);

		ActionReport report = (ActionReport) context.get(REPORT);
		ValidationReport validationReport = (ValidationReport) context.get(VALIDATION_REPORT);
		
		
//		Assert.assertEquals(report.getResult(), STATUS_OK, "result");
//		Assert.assertEquals(report.getFiles().size(), 1, "file reported");
//		Assert.assertEquals(report.getLines().size(), 1, "line reported");
//		Reporter.log("report line :" + report.getLines().get(0).toString(), true);
//		Assert.assertEquals(report.getLines().get(0).getStatus(), LINE_STATE.OK, "line status");
//		RegtoppTestUtils.checkLine(context);
//		
//		Referential referential = (Referential) context.get(REFERENTIAL);
//		Assert.assertNotEquals(referential.getTimetables(),0, "timetables" );
//		Assert.assertNotEquals(referential.getSharedTimetables(),0, "shared timetables" );

		// line should be saved
		utx.begin();
		em.joinTransaction();
		Line line = lineDao.findByObjectId("NINOXE:Line:15574334");
		
//		RegtoppTestUtils.checkMinimalLine(line);
		
		utx.rollback();

		if(!result) {
			System.out.println(ToStringBuilder.reflectionToString(report,ToStringStyle.MULTI_LINE_STYLE));
			System.out.println(validationReport);
			
		}
		
		Assert.assertTrue(result,"Importer command execution failed: "+report.getFailure());
	}

	@Test
	public void importRegtoppKolumbusLines() throws Exception {
		// Prepare context
		Context context = initImportContext();

		File f = new File("src/test/data/kolumbus_regtopp_20160329-20160624.zip");
		File dest = new File("target/referential/test");
		FileUtils.copyFileToDirectory(f, dest);
		JobDataTest job = (JobDataTest) context.get(JOB_DATA);
		job.setFilename(f.getName());

		RegtoppImporterCommand command = (RegtoppImporterCommand) CommandFactory.create(initialContext, RegtoppImporterCommand.class.getName());
		
		

		
	
		RegtoppImportParameters parameters = (RegtoppImportParameters) context.get(CONFIGURATION);
		parameters.setObjectIdPrefix("TST");
		parameters.setReferencesType("line");
		
		boolean result = command.execute(context);

		ActionReport report = (ActionReport) context.get(REPORT);
		ValidationReport validationReport = (ValidationReport) context.get(VALIDATION_REPORT);
		
		
//		Assert.assertEquals(report.getResult(), STATUS_OK, "result");
//		Assert.assertEquals(report.getFiles().size(), 1, "file reported");
//		Assert.assertEquals(report.getLines().size(), 1, "line reported");
//		Reporter.log("report line :" + report.getLines().get(0).toString(), true);
//		Assert.assertEquals(report.getLines().get(0).getStatus(), LINE_STATE.OK, "line status");
//		RegtoppTestUtils.checkLine(context);
//		
//		Referential referential = (Referential) context.get(REFERENTIAL);
//		Assert.assertNotEquals(referential.getTimetables(),0, "timetables" );
//		Assert.assertNotEquals(referential.getSharedTimetables(),0, "shared timetables" );

		// line should be saved
		utx.begin();
		em.joinTransaction();
		Line line = lineDao.findByObjectId("NINOXE:Line:15574334");
		
//		RegtoppTestUtils.checkMinimalLine(line);
		
		utx.rollback();

		if(!result) {
			System.out.println(ToStringBuilder.reflectionToString(report,ToStringStyle.MULTI_LINE_STYLE));
			System.out.println(validationReport);
			
		}
		
		Assert.assertTrue(result,"Importer command execution failed: "+report.getFailure());
	}
}
