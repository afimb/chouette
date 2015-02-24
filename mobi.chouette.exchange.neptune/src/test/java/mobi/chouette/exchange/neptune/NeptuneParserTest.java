package mobi.chouette.exchange.neptune;

import static org.junit.Assert.*;

import java.io.File;
import java.net.URL;

import javax.ejb.EJB;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Constant;
import mobi.chouette.common.Context;
import mobi.chouette.common.chain.Command;
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
public class NeptuneParserTest {

	@EJB(beanName = NeptuneParserCommand.COMMAND)
	private Command parser;

	@EJB(beanName = NeptuneSAXParserCommand.COMMAND)
	private Command validation;

	@Deployment
	public static WebArchive createDeployment() {
		log.info("createDeployment");

		WebArchive result;

		File[] files = Maven.resolver().loadPomFromFile("pom.xml")
				.resolve("mobi.chouette:mobi.chouette.exchange.neptune:1.0.0")
				.withTransitivity().asFile();

		result = ShrinkWrap.create(WebArchive.class, "test.war")
				.addAsWebInfResource("wildfly-ds.xml").addAsLibraries(files)
				.addAsManifestResource("C_NEPTUNE_3.xml")
				.addAsManifestResource("broken_file.xml")
				.addAsManifestResource("error_file.xml")
				.addAsManifestResource("metadata_chouette_dc.xml")
				.addAsManifestResource("1000252.xml")
				.addAsResource(EmptyAsset.INSTANCE, "beans.xml");
		log.info("end createDeployment");

		return result;

	}

	@Test
	public void validation() throws Exception {
		log.info("validation");
		Context context = new Context();
		URL file = NeptuneParserTest.class
				.getResource("/META-INF/1000252.xml");
		Report report = new Report();
		ValidationReport validationReport = new ValidationReport();
		context.put(Constant.FILE_URL, file.toExternalForm());
		context.put(Constant.REPORT, report);
		context.put(Constant.VALIDATION_REPORT, validationReport);
		validation.execute(context);
	}
	
	@Test
	public void test() throws Exception {
		log.info("test");
		Context context = new Context();
		URL file = NeptuneParserTest.class
				.getResource("/META-INF/1000252.xml");
		Report report = new Report();
		ValidationReport validationReport = new ValidationReport();
		context.put(Constant.FILE_URL, file.toExternalForm());
		context.put(Constant.REPORT, report);
		context.put(Constant.VALIDATION_REPORT, validationReport);
		parser.execute(context);
		Referential referential = (Referential) context.get(Constant.REFERENTIAL);
		Line line = referential.getLines().values().iterator().next();
	}

	@Test
	public void verifiyGoodFile() throws Exception {
		log.info("verifiyGoodFile");
		Context context = new Context();
		URL file = NeptuneParserTest.class
				.getResource("/META-INF/C_NEPTUNE_3.xml");
		Report report = new Report();
		ValidationReport validationReport = new ValidationReport();
		context.put(Constant.FILE_URL, file.toExternalForm());
		context.put(Constant.REPORT, report);
		context.put(Constant.VALIDATION_REPORT, validationReport);
		parser.execute(context);
		assertNull("no error should be reported",report.getFailure());
		assertEquals("report one file",1,report.getFiles().getFileInfos().size());
		assertEquals("report one error file",FileInfo.FILE_STATE.OK,report.getFiles().getFileInfos().get(0).getStatus());

	}

	@Test
	public void verifiyWrongFile() throws Exception {
		log.info("verifiyWrongFile");
		Context context = new Context();
		URL file = NeptuneParserTest.class
				.getResource("/META-INF/error_file.xml");
		Report report = new Report();
		ValidationReport validationReport = new ValidationReport();
		context.put(Constant.FILE_URL, file.toExternalForm());
		context.put(Constant.REPORT, report);
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
		assertEquals("report one file",1,report.getFiles().getFileInfos().size());
		assertEquals("report one error file",FileInfo.FILE_STATE.NOK,report.getFiles().getFileInfos().get(0).getStatus());
		System.out.println("error message = "+report.getFiles().getFileInfos().get(0).getErrors().get(0));
	}

	@Test
	public void verifiyBrokenFile() throws Exception {
		log.info("verifiyBrokenFile");
		Context context = new Context();
		URL file = NeptuneParserTest.class
				.getResource("/META-INF/broken_file.xml");
		Report report = new Report();
		ValidationReport validationReport = new ValidationReport();
		context.put(Constant.FILE_URL, file.toExternalForm());
		context.put(Constant.REPORT, report);
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
		assertEquals("report one file",1,report.getFiles().getFileInfos().size());
		assertEquals("report one error file",FileInfo.FILE_STATE.NOK,report.getFiles().getFileInfos().get(0).getStatus());
		System.out.println("error message = "+report.getFiles().getFileInfos().get(0).getErrors().get(0));

	}

//	@Test
//	public void verifiyIgnoredFile() throws Exception {
//		Context context = new Context();
//		URL file = NeptuneParserTest.class
//				.getResource("/META-INF/metadata_chouette_dc.xml");
//		Report report = new Report();
//		context.put(Constant.FILE_URL, file.toExternalForm());
//		context.put(Constant.REPORT, report);
//		parser.execute(context);
//		assertNull("no error should be reported",report.getError());
//		assertEquals("report one ignored file",1,report.getFiles().getFilesDetail().getIgnored().size());
//
//	}
}
