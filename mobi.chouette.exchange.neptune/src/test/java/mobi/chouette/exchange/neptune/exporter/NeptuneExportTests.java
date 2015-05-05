package mobi.chouette.exchange.neptune.exporter;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.Locale;

import javax.ejb.EJB;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Context;
import mobi.chouette.common.chain.Command;
import mobi.chouette.common.chain.CommandFactory;
import mobi.chouette.dao.LineDAO;
import mobi.chouette.exchange.neptune.JobDataTest;
import mobi.chouette.exchange.neptune.Constant;
import mobi.chouette.exchange.neptune.NeptuneTestsUtils;
import mobi.chouette.exchange.neptune.importer.NeptuneImportParameters;
import mobi.chouette.exchange.neptune.importer.NeptuneImporterCommand;
import mobi.chouette.exchange.report.ActionReport;
import mobi.chouette.exchange.report.LineInfo;
import mobi.chouette.exchange.report.LineInfo.LINE_STATE;
import mobi.chouette.exchange.report.ReportConstant;
import mobi.chouette.exchange.validation.report.ValidationReport;
import mobi.chouette.persistence.hibernate.ContextHolder;

import org.apache.commons.io.FileUtils;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.testng.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.testng.Assert;
import org.testng.annotations.Test;

@Log4j
public class NeptuneExportTests  extends Arquillian implements Constant, ReportConstant {

	@EJB
	LineDAO lineDao;


	@Deployment
	public static WebArchive createDeployment() {

		WebArchive result;

		File[] files = Maven.resolver().loadPomFromFile("pom.xml")
				.resolve("mobi.chouette:mobi.chouette.exchange.neptune:3.0.0").withTransitivity().asFile();
		

		result = ShrinkWrap.create(WebArchive.class, "test.war").addAsWebInfResource("postgres-ds.xml")
				.addAsLibraries(files)
				.addClass(NeptuneTestsUtils.class)
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
				// TODO Auto-generated catch block
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
		context.put(MAIN_VALIDATION_REPORT, new ValidationReport());
		NeptuneImportParameters configuration = new NeptuneImportParameters();
		context.put(CONFIGURATION, configuration);
		configuration.setName("name");
		configuration.setUserName("userName");
		configuration.setNoSave(true);
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
		test.setAction( IMPORTER);
		test.setType("neptune");
		context.put("testng", "true");
		context.put(OPTIMIZED, Boolean.FALSE);
		return context;

	}

	protected Context initExportContext() {
		init();
		ContextHolder.setContext("chouette_gui"); // set tenant schema

		Context context = new Context();
		context.put(INITIAL_CONTEXT, initialContext);
		context.put(REPORT, new ActionReport());
		context.put(MAIN_VALIDATION_REPORT, new ValidationReport());
		NeptuneExportParameters configuration = new NeptuneExportParameters();
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
		test.setReferential("chouette_gui");
		test.setAction(EXPORTER);
		test.setType("neptune");
		context.put("testng", "true");
		context.put(OPTIMIZED, Boolean.FALSE);
		return context;

	}


	@Test(groups = { "CheckParameters" }, description = "Export Plugin should reject startDate after endDate")
	public void verifyCheckDatesParameter() throws Exception
	{
		Context context = initExportContext();
		NeptuneExportParameters configuration = (NeptuneExportParameters) context.get(CONFIGURATION);
		configuration.setReferencesType("Lines");
		Calendar c = Calendar.getInstance();
		configuration.setStartDate(c.getTime());
		c.add(Calendar.DATE, -5);
		configuration.setEndDate(c.getTime());
		Command command = (Command) CommandFactory.create(initialContext,
				NeptuneExporterCommand.class.getName());

		
		try {
			command.execute(context);
		} catch (Exception ex) {
			log.error("test failed", ex);
			throw ex;
		}
		ActionReport report = (ActionReport) context.get(REPORT);
		Assert.assertEquals(report.getResult(), STATUS_ERROR, "result");
		Assert.assertTrue(report.getFailure().contains("before"), "failure");
	}


	@Test(groups = { "ExportLine" }, description = "Export Plugin should export file")
	public void verifyExportLine() throws Exception
	{
//		// save data
//		importLines("C_NEPTUNE_1.xml",1,1);
//
//		// export data
//		Context context = initExportContext();
//		NeptuneExportParameters configuration = (NeptuneExportParameters) context.get(CONFIGURATION);
//		configuration.setAddMetadata(true);
//		configuration.setReferencesType("line");
//		Command command = (Command) CommandFactory.create(initialContext,
//				NeptuneExporterCommand.class.getName());
//
//		try {
//			command.execute(context);
//		} catch (Exception ex) {
//			log.error("test failed", ex);
//			throw ex;
//		}
//		
//		ActionReport report = (ActionReport) context.get(REPORT);
//		Assert.assertEquals(report.getResult(), STATUS_OK, "result");
//		Assert.assertEquals(report.getFiles().size(), 1, "file reported");
//		Assert.assertEquals(report.getLines().size(), 1, "line reported");
//		Reporter.log("report line :" + report.getLines().get(0).toString(), true);
//		Assert.assertEquals(report.getLines().get(0).getStatus(), LINE_STATE.OK, "line status");

	}


	private void importLines(String file, int fileCount, int lineCount) throws Exception
	{
		Context context = initImportContext();


		NeptuneImporterCommand command = (NeptuneImporterCommand) CommandFactory.create(initialContext,
				NeptuneImporterCommand.class.getName());
		NeptuneTestsUtils.copyFile(file);
		JobDataTest test = (JobDataTest) context.get(JOB_DATA);
		test.setFilename( file);
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
		Assert.assertEquals(report.getResult(), STATUS_OK, "result");
		Assert.assertEquals(report.getFiles().size(), fileCount, "file reported");
		Assert.assertEquals(report.getLines().size(), lineCount, "line reported");
		for (LineInfo info : report.getLines()) {
			Assert.assertEquals(info.getStatus(), LINE_STATE.OK, "line status");
		}


	}

}