package mobi.chouette.exchange.gtfs;

import java.io.File;

import javax.naming.InitialContext;
import javax.naming.NamingException;

import mobi.chouette.common.Context;
import mobi.chouette.common.chain.CommandFactory;
import mobi.chouette.exchange.gtfs.importer.GtfsImportParameters;
import mobi.chouette.exchange.gtfs.importer.GtfsRouteParserCommand;
import mobi.chouette.exchange.gtfs.model.importer.GtfsImporter;
import mobi.chouette.exchange.report.ActionReport;
import mobi.chouette.exchange.validation.report.ValidationReport;
import mobi.chouette.model.util.Referential;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.testng.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.jboss.shrinkwrap.resolver.api.maven.ScopeType;
import org.jboss.shrinkwrap.resolver.api.maven.strategy.AcceptScopesStrategy;
import org.jboss.shrinkwrap.resolver.api.maven.strategy.CombinedStrategy;
import org.jboss.shrinkwrap.resolver.api.maven.strategy.MavenResolutionStrategy;
import org.jboss.shrinkwrap.resolver.api.maven.strategy.TransitiveStrategy;
import org.testng.annotations.Test;

public class GtfsParserTest  extends Arquillian implements Constant{

	private InitialContext initialContext ;
	private GtfsImportParameters configuration;

	@Deployment
	public static WebArchive createDeployment() {

		WebArchive result;
		
		MavenResolutionStrategy strategy = new CombinedStrategy(TransitiveStrategy.INSTANCE,new AcceptScopesStrategy(ScopeType.COMPILE,
                ScopeType.TEST));

		File[] files = Maven.resolver().loadPomFromFile("pom.xml")
				.resolve("mobi.chouette:mobi.chouette.exchange.gtfs").using(strategy)
				.asFile();

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
		
		ActionReport report = new ActionReport();
		ValidationReport validationReport = new ValidationReport();
		GtfsRouteParserCommand command = (GtfsRouteParserCommand) CommandFactory.create(initialContext, GtfsRouteParserCommand.class.getName());
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
