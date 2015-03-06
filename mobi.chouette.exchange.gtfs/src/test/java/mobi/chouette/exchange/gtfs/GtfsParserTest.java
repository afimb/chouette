package mobi.chouette.exchange.gtfs;

import java.io.File;

import javax.naming.InitialContext;
import javax.naming.NamingException;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Context;
import mobi.chouette.common.chain.CommandFactory;
import mobi.chouette.exchange.gtfs.importer.GtfsImportParameters;
import mobi.chouette.exchange.gtfs.importer.GtfsParserCommand;
import mobi.chouette.exchange.gtfs.model.importer.GtfsImporter;
import mobi.chouette.exchange.report.Report;
import mobi.chouette.exchange.validator.report.ValidationReport;
import mobi.chouette.model.util.Referential;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.testng.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.testng.annotations.Test;

@Log4j
public class GtfsParserTest  extends Arquillian implements Constant{

	private InitialContext initialContext ;
	private GtfsImportParameters configuration;

	@Deployment
	public static WebArchive createDeployment() {

		WebArchive result;

		File[] files = Maven.resolver().loadPomFromFile("pom.xml")
				.resolve("mobi.chouette:mobi.chouette.exchange.gtfs:3.0.0")
				.withTransitivity().asFile();

		result = ShrinkWrap.create(WebArchive.class, "test.war")
				.addAsWebInfResource("postgres-ds.xml").addAsLibraries(files)
				.addAsResource(EmptyAsset.INSTANCE, "beans.xml");

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
		if (configuration == null)
		{
			configuration = new GtfsImportParameters();
			configuration.setObjectIdPrefix("GtfsTest");
		}
				
	}

	@Test
	public void validData() throws Exception {
		init();
		Context context = new Context();
		context.put(INITIAL_CONTEXT, initialContext);
		Report report = new Report();
		ValidationReport validationReport = new ValidationReport();
		GtfsParserCommand command = (GtfsParserCommand) CommandFactory.create(initialContext, GtfsParserCommand.class.getName());
		GtfsImporter importer = new GtfsImporter("src/test/data/valid");
		context.put(PARSER, importer);
		context.put(REPORT, report);
		context.put(REFERENTIAL, new Referential());
		context.put(VALIDATION_REPORT, validationReport);
		context.put(CONFIGURATION, configuration);
		command.setGtfsRouteId("0001");
		
		command.execute(context);
	}
}
