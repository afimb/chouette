package mobi.chouette.exchange.neptune;

import java.net.URL;

import javax.ejb.EJB;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Color;
import mobi.chouette.common.Context;
import mobi.chouette.common.chain.Command;
import mobi.chouette.exchange.neptune.importer.NeptuneParserCommand;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

@Log4j
@RunWith(Arquillian.class)
public class NeptuneParserTest {

	@EJB(beanName = "NeptuneParserCommand")
	private Command command;

	@Deployment
	public static JavaArchive createDeployment() {
		
		URL url = NeptuneParserTest.class.getResource("/test/C_NEPTUNE_3.xml");
		log.info(Color.SUCCESS + "[DSU] url : " + url + Color.NORMAL);
		
		return ShrinkWrap.create(JavaArchive.class)
				.addPackage("mobi.chouette.common")
				.addPackage("mobi.chouette.common.chain")
				.addPackage("mobi.chouette.importer")
				.addPackage("mobi.chouette.exporter")
				.addPackage("mobi.chouette.validation")
				.addPackage("mobi.chouette.exchange.neptune")
				.addPackage("mobi.chouette.exchange.neptune.parser")
				.addClass(NeptuneParserCommand.class)
				.add
				.addAsManifestResource(EmptyAsset.INSTANCE, "beans.xml");

	}

	@Test
	public void todo() throws Exception {
		Context context = new Context();
		URL url = this.getClass().getResource("/test/C_NEPTUNE_3.xml");
		log.info(Color.SUCCESS + "[DSU] url : " + url + Color.NORMAL);
		
		context.put(Constant.FILE, "/home/dsuru/workspace-chouette/chouette/mobi.chouette.exchange.neptune/src/test/resources/test/C_NEPTUNE_3.xml");
		command.execute(context);
	}
}
