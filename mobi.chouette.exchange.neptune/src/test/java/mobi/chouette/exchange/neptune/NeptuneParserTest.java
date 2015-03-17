package mobi.chouette.exchange.neptune;


import java.io.File;
import java.io.IOException;

import javax.naming.InitialContext;
import javax.naming.NamingException;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Context;
import mobi.chouette.common.chain.CommandFactory;
import mobi.chouette.exchange.neptune.importer.NeptuneImportParameters;
import mobi.chouette.exchange.neptune.importer.NeptuneImporterCommand;
import mobi.chouette.exchange.neptune.importer.NeptuneParserCommand;
import mobi.chouette.exchange.neptune.importer.NeptuneSAXParserCommand;
import mobi.chouette.exchange.report.FileInfo;
import mobi.chouette.exchange.report.ActionReport;
import mobi.chouette.exchange.validator.report.ValidationReport;
import mobi.chouette.model.Line;
import mobi.chouette.model.api.Job;
import mobi.chouette.model.util.Referential;
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
public class NeptuneParserTest extends Arquillian implements Constant {

	private InitialContext initialContext ;

	@Deployment
	public static WebArchive createDeployment() {
		log.info("createDeployment");

		WebArchive result;

		File[] files = Maven.resolver().loadPomFromFile("pom.xml")
				.resolve("mobi.chouette:mobi.chouette.exchange.neptune:3.0.0")
				.withTransitivity().asFile();

		result = ShrinkWrap.create(WebArchive.class, "test.war")
				.addAsWebInfResource("postgres-ds.xml")
				.addAsLibraries(files)
				.addAsResource(EmptyAsset.INSTANCE, "beans.xml");
		log.info("end createDeployment");

		return result;

	}

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
			ContextHolder.setContext("chouette_gui"); // set tenant schema
		}
	}
	
	private Context initContext()
	{
		init();
		Job job = new Job();
		job.setAction("importer");
		job.setType("neptune");
		job.setPath("/tmp/test/1");
		job.setReferential("chouette_gui");
		job.setStatus(Job.STATUS.SCHEDULED);
		
		Context context = new Context();
		context.put(INITIAL_CONTEXT, initialContext);
		context.put(REPORT,new ActionReport());
		context.put(REFERENTIAL, new Referential());
		context.put(VALIDATION_REPORT, new ValidationReport());
		NeptuneImportParameters configuration = new NeptuneImportParameters();
		context.put(CONFIGURATION, configuration);
		configuration.setName("name");
		configuration.setUserName("userName");
		configuration.setNoSave(true);
		configuration.setOrganisationName("organisation");
		configuration.setReferentialName("test");
		context.put(PATH, "target/referential/test");
		File f = new File("target/referential/test");
		if (f.exists())
			try {
				FileUtils.deleteDirectory(f);
			} catch (IOException e) {
				e.printStackTrace();
			}
		f.mkdirs();
		context.put(JOB_REFERENTIAL, "chouette_gui");
		context.put(ACTION, IMPORTER);
		context.put(TYPE, "neptune");
		
		return context;

	}
	
	@Test
	public void validation() throws Exception {
		log.info("validation");
		Context context = initContext();
		NeptuneSAXParserCommand validation = (NeptuneSAXParserCommand) CommandFactory.create(initialContext, NeptuneSAXParserCommand.class.getName());
		File f = new File("src/test/data/1000252.xml");
		context.put(ARCHIVE, "src/test/data/1000252.xml");
		validation.setFileURL("file://"+f.getAbsolutePath());
		validation.execute(context);
	}
	
	@Test
	public void test() throws Exception {
		log.info("test");
		Context context = initContext();
		NeptuneParserCommand parser = (NeptuneParserCommand) CommandFactory.create(initialContext, NeptuneParserCommand.class.getName());
		File f = new File("src/test/data/1000252.xml");
		parser.setFileURL("file://"+f.getAbsolutePath());
		parser.execute(context);
		Referential referential = (Referential) context.get(Constant.REFERENTIAL);
		Line line = referential.getLines().values().iterator().next();
	}

	@Test
	public void verifiyGoodFile() throws Exception {
		log.info("verifiyGoodFile");
		Context context = initContext();
		NeptuneParserCommand parser = (NeptuneParserCommand) CommandFactory.create(initialContext, NeptuneParserCommand.class.getName());
		File f = new File("src/test/data/C_NEPTUNE_3.xml");
		parser.setFileURL("file://"+f.getAbsolutePath());
		ActionReport report = (ActionReport) context.get(REPORT);
		ValidationReport validationReport = (ValidationReport) context.get(VALIDATION_REPORT);
		parser.execute(context);
		Assert.assertNull(report.getFailure(),"no error should be reported");
		Assert.assertEquals(report.getFiles().size(),1,"report one file");
		Assert.assertEquals(report.getFiles().get(0).getStatus(),FileInfo.FILE_STATE.OK,"report one error file");

	}

	@Test
	public void verifiyWrongFile() throws Exception {
		log.info("verifiyWrongFile");
		Context context = initContext();
		context.put(INITIAL_CONTEXT, initialContext);
		NeptuneSAXParserCommand validation = (NeptuneSAXParserCommand) CommandFactory.create(initialContext, NeptuneSAXParserCommand.class.getName());
		File f = new File("src/test/data/error_file.xml");
		validation.setFileURL("file://"+f.getAbsolutePath());
		try
		{
			validation.execute(context);
		}
		catch (Exception e)
		{
			System.out.println("exception received "+e.getMessage());
		}
		ActionReport report = (ActionReport) context.get(REPORT);
		Assert.assertNull(report.getFailure(),"no error should be reported");
		Assert.assertEquals(report.getFiles().size(),1,"report one file");
		Assert.assertEquals(report.getFiles().get(0).getStatus(),FileInfo.FILE_STATE.NOK,"report one error file");
		System.out.println("error message = "+report.getFiles().get(0).getErrors().get(0));
	}

	@Test
	public void verifiyBrokenFile() throws Exception {
		log.info("verifiyBrokenFile");
		Context context = initContext();
		NeptuneParserCommand parser = (NeptuneParserCommand) CommandFactory.create(initialContext, NeptuneParserCommand.class.getName());
		File f = new File("src/test/data/broken_file.xml");
		parser.setFileURL("file://"+f.getAbsolutePath());
		try
		{
			parser.execute(context);
		}
		catch (Exception e)
		{
			System.out.println("exception received "+e.getMessage());
		}
		
		ActionReport report = (ActionReport) context.get(REPORT);
		Assert.assertNull(report.getFailure(),"no error should be reported");
		Assert.assertEquals(report.getFiles().size(),1,"report one file");
		Assert.assertEquals(report.getFiles().get(0).getStatus(),FileInfo.FILE_STATE.NOK,"report one error file");
		System.out.println("error message = "+report.getFiles().get(0).getErrors().get(0));

	}

	@Test
	public void verifiyIgnoredFile() throws Exception {
		Context context = initContext();
		File f = new File("src/test/data/metadata_chouette_dc.xml");
		NeptuneImporterCommand command =  (NeptuneImporterCommand) CommandFactory.create(initialContext, NeptuneImporterCommand.class.getName());

	}
}
