package mobi.chouette.exchange.netex;

import java.io.File;
import java.net.URL;

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

		File[] files = Maven.resolver().loadPomFromFile("pom.xml")
				.resolve("mobi.chouette:mobi.chouette.exchange.netex:1.0.0")
				.withTransitivity().asFile();

		result = ShrinkWrap.create(WebArchive.class, "test.war")
				.addAsWebInfResource("wildfly-ds.xml").addAsLibraries(files)
				.addAsManifestResource("line_test.xml")
				.addAsManifestResource(EmptyAsset.INSTANCE, "beans.xml");

		return result;
	}

	@Test
	public void todo() throws Exception {
		Context context = new Context();
		URL url = NetexParserTest.class.getResource("/META-INF/line_test.xml");
		context.put(Constant.FILE_URL, url.toExternalForm());
		command.execute(context);
	}
}
