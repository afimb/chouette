package mobi.chouette.exchange.neptune;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.io.File;

import javax.naming.InitialContext;
import javax.naming.NamingException;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Context;
import mobi.chouette.common.chain.CommandFactory;
import mobi.chouette.exchange.neptune.importer.NeptuneParserCommand;
import mobi.chouette.exchange.neptune.importer.NeptuneSAXParserCommand;
import mobi.chouette.exchange.report.FileInfo;
import mobi.chouette.exchange.report.Report;
import mobi.chouette.exchange.validator.report.ValidationReport;
import mobi.chouette.model.Line;
import mobi.chouette.model.util.Referential;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.junit.Test;
import org.junit.runner.RunWith;

@Log4j
@RunWith(Arquillian.class)
public class NeptuneParserTest implements Constant {

	private InitialContext initialContext ;

	@Deployment
	public static WebArchive createDeployment() {
		log.info("createDeployment");

		WebArchive result;

		File[] files = Maven.resolver().loadPomFromFile("pom.xml")
				.resolve("mobi.chouette:mobi.chouette.exchange.neptune:1.0.0")
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
		}
	}
	
	@Test
	public void validation() throws Exception {
		init();
		log.info("validation");
		Context context = new Context();
		context.put(INITIAL_CONTEXT, initialContext);
		NeptuneSAXParserCommand validation = (NeptuneSAXParserCommand) CommandFactory.create(initialContext, NeptuneSAXParserCommand.class.getName());
		File f = new File("src/test/data/1000252.xml");
		Report report = new Report();
		ValidationReport validationReport = new ValidationReport();
		validation.setFileURL("file://"+f.getAbsolutePath());
		context.put(Constant.REPORT, report);
		context.put(REFERENTIAL, new Referential());
		context.put(Constant.VALIDATION_REPORT, validationReport);
		validation.execute(context);
	}
	
	@Test
	public void test() throws Exception {
		init();
		log.info("test");
		Context context = new Context();
		context.put(INITIAL_CONTEXT, initialContext);
		NeptuneParserCommand parser = (NeptuneParserCommand) CommandFactory.create(initialContext, NeptuneParserCommand.class.getName());
		File f = new File("src/test/data/1000252.xml");
		Report report = new Report();
		ValidationReport validationReport = new ValidationReport();
		parser.setFileURL("file://"+f.getAbsolutePath());
		context.put(Constant.REPORT, report);
		context.put(REFERENTIAL, new Referential());
		context.put(Constant.VALIDATION_REPORT, validationReport);
		parser.execute(context);
		Referential referential = (Referential) context.get(Constant.REFERENTIAL);
		Line line = referential.getLines().values().iterator().next();
	}

	@Test
	public void verifiyGoodFile() throws Exception {
		init();
		log.info("verifiyGoodFile");
		Context context = new Context();
		context.put(INITIAL_CONTEXT, initialContext);
		NeptuneParserCommand parser = (NeptuneParserCommand) CommandFactory.create(initialContext, NeptuneParserCommand.class.getName());
		File f = new File("src/test/data/C_NEPTUNE_3.xml");
		Report report = new Report();
		ValidationReport validationReport = new ValidationReport();
		parser.setFileURL("file://"+f.getAbsolutePath());
		context.put(Constant.REPORT, report);
		context.put(REFERENTIAL, new Referential());
		context.put(Constant.VALIDATION_REPORT, validationReport);
		parser.execute(context);
		assertNull("no error should be reported",report.getFailure());
		assertEquals("report one file",1,report.getFiles().size());
		assertEquals("report one error file",FileInfo.FILE_STATE.OK,report.getFiles().get(0).getStatus());

	}

	@Test
	public void verifiyWrongFile() throws Exception {
		init();
		log.info("verifiyWrongFile");
		Context context = new Context();
		context.put(INITIAL_CONTEXT, initialContext);
		NeptuneSAXParserCommand validation = (NeptuneSAXParserCommand) CommandFactory.create(initialContext, NeptuneSAXParserCommand.class.getName());
		File f = new File("src/test/data/error_file.xml");
		Report report = new Report();
		ValidationReport validationReport = new ValidationReport();
		validation.setFileURL("file://"+f.getAbsolutePath());
		context.put(Constant.REPORT, report);
		context.put(REFERENTIAL, new Referential());
		context.put(Constant.VALIDATION_REPORT, validationReport);
		try
		{
			validation.execute(context);
		}
		catch (Exception e)
		{
			System.out.println("exception received "+e.getMessage());
		}
		assertNull("no error should be reported",report.getFailure());
		assertEquals("report one file",1,report.getFiles().size());
		assertEquals("report one error file",FileInfo.FILE_STATE.NOK,report.getFiles().get(0).getStatus());
		System.out.println("error message = "+report.getFiles().get(0).getErrors().get(0));
	}

	@Test
	public void verifiyBrokenFile() throws Exception {
		init();
		log.info("verifiyBrokenFile");
		Context context = new Context();
		context.put(INITIAL_CONTEXT, initialContext);
		NeptuneParserCommand parser = (NeptuneParserCommand) CommandFactory.create(initialContext, NeptuneParserCommand.class.getName());
		File f = new File("src/test/data/broken_file.xml");
		Report report = new Report();
		ValidationReport validationReport = new ValidationReport();
		parser.setFileURL("file://"+f.getAbsolutePath());
		context.put(Constant.REPORT, report);
		context.put(REFERENTIAL, new Referential());
		context.put(Constant.VALIDATION_REPORT, validationReport);
		try
		{
			parser.execute(context);
		}
		catch (Exception e)
		{
			System.out.println("exception received "+e.getMessage());
		}
		
		assertNull("no error should be reported",report.getFailure());
		assertEquals("report one file",1,report.getFiles().size());
		assertEquals("report one error file",FileInfo.FILE_STATE.NOK,report.getFiles().get(0).getStatus());
		System.out.println("error message = "+report.getFiles().get(0).getErrors().get(0));

	}

//	@Test
//	public void verifiyIgnoredFile() throws Exception {
//		Context context = new Context();
//		File f = new File("src/test/data/metadata_chouette_dc.xml");
//		Report report = new Report();
//		parser.setFileURL("file://"+f.getAbsolutePath());
//		context.put(Constant.REPORT, report);
//		parser.execute(context);
//		assertNull("no error should be reported",report.getError());
//		assertEquals("report one ignored file",1,report.getFiles().getFilesDetail().getIgnored().size());
//
//	}
}
