package mobi.chouette.exchange.validator;

import java.io.File;
import java.io.IOException;
import java.util.Locale;

import javax.naming.InitialContext;
import javax.naming.NamingException;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Constant;
import mobi.chouette.common.Context;
import mobi.chouette.common.chain.CommandFactory;
import mobi.chouette.exchange.neptune.importer.NeptuneImportParameters;
import mobi.chouette.exchange.neptune.importer.NeptuneImporterCommand;
import mobi.chouette.exchange.report.ActionReport;
import mobi.chouette.exchange.report.ActionReporter.OBJECT_STATE;
import mobi.chouette.exchange.report.ActionReporter.OBJECT_TYPE;
import mobi.chouette.exchange.report.ObjectReport;
import mobi.chouette.exchange.report.ReportConstant;
import mobi.chouette.exchange.validation.report.CheckPointReport;
import mobi.chouette.exchange.validation.report.ValidationReport;
import mobi.chouette.exchange.validation.report.ValidationReporter;
import mobi.chouette.persistence.hibernate.ContextHolder;

import org.apache.commons.io.FileUtils;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.testng.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.testng.Assert;
import org.testng.Reporter;
import org.testng.annotations.Test;

@Log4j
public class ValidatorTest extends Arquillian implements Constant,ReportConstant{

	@Deployment
	public static WebArchive createDeployment() {

		WebArchive result;

		File[] files = Maven.resolver().loadPomFromFile("pom.xml")
				.resolve("mobi.chouette:mobi.chouette.exchange.validator").withTransitivity().asFile();
		

		result = ShrinkWrap.create(WebArchive.class, "test.war").addAsWebInfResource("postgres-ds.xml")
				.addAsLibraries(files)
				.addClass(DummyChecker.class)
				.addClass(JobDataTest.class)
				.addAsResource(EmptyAsset.INSTANCE, "beans.xml");
		return result;

	}

	protected static InitialContext initialContext;
	protected void init() {
		Locale.setDefault(Locale.ENGLISH);
		if (initialContext == null) {
			try {
				initialContext = new InitialContext();
			} catch (NamingException e) {
				e.printStackTrace();
			}
			
			
		}
	}
	protected Context initImportContext() {
		init();
		ContextHolder.setContext("chouette_gui"); // set tenant schema

		Context context = new Context();
		context.put(INITIAL_CONTEXT, initialContext);
		context.put(REPORT, new ActionReport());
		context.put(VALIDATION_REPORT, new ValidationReport());
		NeptuneImportParameters configuration = new NeptuneImportParameters();
		configuration.setCleanRepository(true);
		configuration.setNoSave(false);
		context.put(CONFIGURATION, configuration);
		configuration.setName("name");
		configuration.setUserName("userName");
		configuration.setNoSave(true);
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
		test.setReferential( "chouette_gui");
		test.setAction( IMPORTER);
		test.setType( "neptune");
		context.put("testng", "true");
		context.put(OPTIMIZED, Boolean.FALSE);
		return context;

	}

	
	   @Test(groups = { "validator" }, description = "test line validation")
	   public void verifyValidate() throws Exception
	   {
			// save data
			//importLines("test_neptune.zip",6,6);
	   }


	
	
	
	private void importLines(String file, int fileCount, int lineCount) throws Exception
	{
		Context context = initImportContext();


		NeptuneImporterCommand command = (NeptuneImporterCommand) CommandFactory.create(initialContext,
				NeptuneImporterCommand.class.getName());
		TestsUtils.copyFile(file);
		JobDataTest test =  (JobDataTest) context.get(JOB_DATA);
		test.setInputFilename( file);
		NeptuneImportParameters configuration = (NeptuneImportParameters) context.get(CONFIGURATION);
		configuration.setNoSave(false);
		configuration.setCleanRepository(true);
		try {
			command.execute(context);
		} catch (Exception ex) {
			log.error("test failed", ex);
			throw ex;
		}
		ActionReport report = (ActionReport) context.get(REPORT);
		Reporter.log(report.toString(),true);
		ValidationReport valReport = (ValidationReport) context.get(VALIDATION_REPORT);
		for (CheckPointReport cp : valReport.getCheckPoints()) 
		{
			if (cp.getState().equals(ValidationReporter.RESULT.NOK))
			{
				Reporter.log(cp.toString(),true);
			}
		}
		Assert.assertEquals(report.getResult(), STATUS_OK, "result");
		Assert.assertEquals(report.getFiles().size(), fileCount, "file reported");
		Assert.assertTrue(report.getCollections().containsKey(OBJECT_TYPE.LINE), "line type reported");
		Assert.assertEquals(report.getCollections().get(OBJECT_TYPE.LINE).getObjectReports().size(), lineCount, "line reported");
		for (ObjectReport info : report.getCollections().get(OBJECT_TYPE.LINE).getObjectReports()) {
			Assert.assertEquals(info.getStatus(), OBJECT_STATE.OK, "line status");
		}


	}

}
