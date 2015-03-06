package mobi.chouette.exchange.netex;

import java.io.File;

import javax.naming.InitialContext;
import javax.naming.NamingException;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Context;
import mobi.chouette.common.chain.CommandFactory;
import mobi.chouette.exchange.netex.importer.NetexParserCommand;
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
public class NetexParserTest  extends Arquillian implements mobi.chouette.common.Constant{

	private InitialContext initialContext ;

	@Deployment
	public static WebArchive createDeployment() {

		WebArchive result;

		File[] files = Maven.resolver().loadPomFromFile("pom.xml")
				.resolve("mobi.chouette:mobi.chouette.exchange.netex:3.0.0")
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
	}

	@Test
	public void valid() throws Exception {
		init();
		Context context = new Context();
		context.put(INITIAL_CONTEXT, initialContext);
		NetexParserCommand command = (NetexParserCommand) CommandFactory.create(initialContext, NetexParserCommand.class.getName());
		File f = new File("src/test/data/valid/line_test.xml");
		Report report = new Report();
		ValidationReport validationReport = new ValidationReport();
		command.setFileURL("file://"+f.getAbsolutePath());
		context.put(Constant.REPORT, report);
		context.put(REFERENTIAL, new Referential());
		context.put(Constant.VALIDATION_REPORT, validationReport);
		command.execute(context);
	}
}
