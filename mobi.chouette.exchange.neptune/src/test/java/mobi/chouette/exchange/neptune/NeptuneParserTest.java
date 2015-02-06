package mobi.chouette.exchange.neptune;

import java.io.File;
import java.net.URL;

import javax.ejb.EJB;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Constant;
import mobi.chouette.common.Context;
import mobi.chouette.common.chain.Command;
import mobi.chouette.exchange.importer.SAXParserCommand;
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
	private Command parser;

	@EJB(beanName = SAXParserCommand.COMMAND)
	private Command validation;

	@Deployment
	public static WebArchive createDeployment() {

		WebArchive result;

		File[] files = Maven.resolver().loadPomFromFile("pom.xml")
				.resolve("mobi.chouette:mobi.chouette.exchange.neptune:1.0.0")
				.withTransitivity().asFile();

		result = ShrinkWrap
				.create(WebArchive.class, "test.war")
				.addAsWebInfResource("wildfly-ds.xml")
				.addAsLibraries(files)
				.addAsManifestResource("C_NEPTUNE_3.xml")
				.addAsManifestResource("neptune.xsd")
				.addAsResource(EmptyAsset.INSTANCE, "beans.xml");

		return result;

	}

//	@Test
//	public void validation() throws Exception {
//		Context context = new Context();
//		URL schema = NeptuneParserTest.class.getResource("/META-INF/neptune.xsd");
//		context.put(Constant.SCHEMA_FILE, schema.toExternalForm());
//		URL file = NeptuneParserTest.class.getResource("/META-INF/C_NEPTUNE_3.xml");
//		context.put(Constant.FILE_URL, file.toExternalForm());
//		validation.execute(context);
//	}

	@Test
	public void parser() throws Exception {
		Context context = new Context();
		URL file = NeptuneParserTest.class.getResource("/META-INF/C_NEPTUNE_3.xml");
		context.put(Constant.FILE_URL, file.toExternalForm());
		parser.execute(context);
	}
}
