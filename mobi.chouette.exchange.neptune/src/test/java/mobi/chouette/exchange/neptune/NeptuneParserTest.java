package mobi.chouette.exchange.neptune;

import java.io.File;
import java.net.URL;

import javax.ejb.EJB;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Context;
import mobi.chouette.common.chain.Command;
import mobi.chouette.exchange.neptune.importer.Constant;
import mobi.chouette.exchange.neptune.importer.NeptuneParserCommand;

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
	private Command command;

	@Deployment
	public static WebArchive createDeployment() {

		WebArchive result;

		// TODO use POM

		File[] files = Maven.resolver().loadPomFromFile("pom.xml")
				.resolve("mobi.chouette:mobi.chouette.exchange.neptune:1.0.0")
				.withTransitivity().asFile();

		result = ShrinkWrap.create(WebArchive.class, "test.war")
				.addAsWebInfResource("wildfly-ds.xml").addAsLibraries(files)
				.addAsManifestResource("C_NEPTUNE_3.xml")
				.addAsManifestResource(EmptyAsset.INSTANCE, "beans.xml");

		// String[] artifacts = {
		// "mobi.chouette:mobi.chouette.exchange.neptune:1.0.0",
		// "mobi.chouette:mobi.chouette.common:1.0.0",
		// "mobi.chouette:mobi.chouette.model:1.0.0",
		// "mobi.chouette:mobi.chouette.importer:1.0.0",
		// "mobi.chouette:mobi.chouette.exporter:1.0.0",
		// "mobi.chouette:mobi.chouette.validation:1.0.0",
		// "mobi.chouette:mobi.chouette.exchange.neptune:1.0.0",
		// "xpp3:xpp3:1.1.3.4.O", "com.google.guava:guava:18.0" };
		// File[] dependencies = Maven.resolver().resolve(artifacts)
		// .withoutTransitivity().asFile();
		//
		// result = ShrinkWrap.create(WebArchive.class)
		// .addAsLibraries(dependencies)
		// .addAsResource("xml/C_NEPTUNE_3.xml")
		// .addAsManifestResource(EmptyAsset.INSTANCE, "beans.xml");

		return result;

	}

	@Test
	public void todo() throws Exception {
		Context context = new Context();
		URL url = NeptuneParserTest.class
				.getResource("/META-INF/C_NEPTUNE_3.xml");
		context.put(Constant.FILE, url.toExternalForm());
		command.execute(context);
	}
}
