package mobi.chouette.exchange.gtfs;

import java.io.File;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.ejb.EJB;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Context;
import mobi.chouette.common.chain.Command;
import mobi.chouette.exchange.gtfs.model.importer.GtfsImporter;

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
public class GtfsParserTest {

	@EJB(beanName = "GtfsParserCommand")
	private Command command;

	@Deployment
	public static WebArchive createDeployment() {

		WebArchive result;

		File[] files = Maven.resolver().loadPomFromFile("pom.xml")
				.resolve("mobi.chouette:mobi.chouette.exchange.gtfs:1.0.0")
				.withTransitivity().asFile();

		result = ShrinkWrap.create(WebArchive.class, "test.war")
				.addAsWebInfResource("wildfly-ds.xml").addAsLibraries(files)
				.addAsManifestResource("agency.txt")
				.addAsManifestResource("calendar_dates.txt")
				.addAsManifestResource("calendar.txt")
				.addAsManifestResource("routes.txt")
				.addAsManifestResource("stops.txt")
				.addAsManifestResource("stop_times.txt")
				.addAsManifestResource("trips.txt")
				.addAsResource(EmptyAsset.INSTANCE, "beans.xml");

		return result;
	}

	@Test
	public void agency() throws Exception {
		Context context = new Context();
		URL url = GtfsParserTest.class.getResource("/META-INF");		
		GtfsImporter importer = new GtfsImporter(url.toExternalForm());
		context.put(Constant.PARSER, importer);
		
		command.execute(context);
	}
}
