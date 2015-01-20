package mobi.chouette.exchange.netex;

import java.io.File;

import javax.ejb.EJB;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Context;
import mobi.chouette.common.chain.Command;

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
public class NetexParserTest {

	@EJB(beanName = "NetexParserCommand")
	private Command command;

	@Deployment
	public static WebArchive createDeployment() {

		WebArchive result;

		String[] artifacts = {
				"mobi.chouette:mobi.chouette.exchange.netex:1.0.0",
				"mobi.chouette:mobi.chouette.common:1.0.0",
				"mobi.chouette:mobi.chouette.model:1.0.0",
				"mobi.chouette:mobi.chouette.importer:1.0.0",
				"mobi.chouette:mobi.chouette.exporter:1.0.0",
				"mobi.chouette:mobi.chouette.validation:1.0.0",
				"mobi.chouette:mobi.chouette.exchange.neptune:1.0.0",
				"xpp3:xpp3:1.1.3.4.O", "com.google.guava:guava:18.0",
				"commons-lang:commons-lang:2.6"};
		File[] dependencies = Maven.resolver().resolve(artifacts)
				.withoutTransitivity().asFile();

		result = ShrinkWrap.create(WebArchive.class)
				.addAsLibraries(dependencies)
				.addAsResource("xml/line_test.xml")
				.addAsManifestResource(EmptyAsset.INSTANCE, "beans.xml");

		return result;

	}

	@Test
	public void todo() throws Exception {
		Context context = new Context();
		context.put(
				Constant.FILE,
				"/home/dsuru/workspace-chouette/chouette/mobi.chouette.exchange.netex/src/test/resources/xml/line_test.xml");
		command.execute(context);
	}
}
