package mobi.chouette.exchange.netexprofile.importer;

import mobi.chouette.common.Constant;
import mobi.chouette.common.Context;
import mobi.chouette.common.chain.Command;
import mobi.chouette.common.chain.CommandFactory;
import mobi.chouette.exchange.netexprofile.DummyChecker;
import mobi.chouette.exchange.netexprofile.NetexTestUtils;
import mobi.chouette.exchange.report.ActionReport;
import mobi.chouette.exchange.report.ActionReporter;
import mobi.chouette.exchange.report.IO_TYPE;
import mobi.chouette.exchange.report.ReportConstant;
import mobi.chouette.exchange.validation.report.ValidationReport;
import mobi.chouette.model.util.Referential;
import mobi.chouette.persistence.hibernate.ContextHolder;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.testng.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.importer.ZipImporter;
import org.jboss.shrinkwrap.api.spec.EnterpriseArchive;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.testng.Assert;
import org.testng.annotations.Test;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.io.File;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class NetexLineParserCommandTest extends Arquillian implements Constant, ReportConstant {

	protected static InitialContext initialContext;

	@Deployment
	public static EnterpriseArchive createDeployment() {
		EnterpriseArchive result;

		File[] files = Maven.resolver()
				.loadPomFromFile("pom.xml")
				.resolve("mobi.chouette:mobi.chouette.exchange.netexprofile")
				.withTransitivity()
				.asFile();

		List<File> jars = new ArrayList<>();
		List<JavaArchive> modules = new ArrayList<>();

		for (File file : files) {
			if (file.getName().startsWith("mobi.chouette.exchange")) {
				String name = file.getName().split("\\-")[0] + ".jar";
				JavaArchive archive = ShrinkWrap
						.create(ZipImporter.class, name)
						.importFrom(file)
						.as(JavaArchive.class);
				modules.add(archive);
			} else {
				jars.add(file);
			}
		}

		File[] filesDao = Maven.resolver()
				.loadPomFromFile("pom.xml")
				.resolve("mobi.chouette:mobi.chouette.dao")
				.withTransitivity()
				.asFile();

		if (filesDao.length == 0) {
			throw new NullPointerException("no dao");
		}

		for (File file : filesDao) {
			if (file.getName().startsWith("mobi.chouette.dao")) {
				String name = file.getName().split("\\-")[0] + ".jar";
				JavaArchive archive = ShrinkWrap
						.create(ZipImporter.class, name)
						.importFrom(file)
						.as(JavaArchive.class);
				modules.add(archive);

				if (!modules.contains(archive)) {
					modules.add(archive);
				}
			} else {
				if (!jars.contains(file))
					jars.add(file);
			}
		}

		final WebArchive testWar = ShrinkWrap.create(WebArchive.class, "test.war").addAsWebInfResource("postgres-ds.xml")
				.addClass(NetexLineParserCommandTest.class)
				.addClass(NetexInitImportCommand.class)
				.addClass(NetexTestUtils.class)
				.addClass(DummyChecker.class);

		result = ShrinkWrap.create(EnterpriseArchive.class, "test.ear")
				.addAsLibraries(jars.toArray(new File[0]))
				.addAsModules(modules.toArray(new JavaArchive[0]))
				.addAsModule(testWar)
				.addAsResource(EmptyAsset.INSTANCE, "beans.xml");

		return result;
	}

	private Context initContext() {
		Locale.setDefault(Locale.ENGLISH);

		if (initialContext == null) {
			try {
				initialContext = new InitialContext();
			} catch (NamingException e) {
				e.printStackTrace();
			}
		}

		ContextHolder.setContext("chouette_gui");
		Context context = new Context();
		context.put(INITIAL_CONTEXT, initialContext);

		NetexprofileImportParameters configuration = new NetexprofileImportParameters();
		context.put(Constant.CONFIGURATION, configuration);
		context.put(Constant.VALIDATION_REPORT, new ValidationReport());
		context.put(Constant.REPORT, new ActionReport());

		Referential referential = new Referential();
		context.put(Constant.REFERENTIAL,referential);

		return context;
	}

	@Test
	public void testParseDocument() throws Exception {
		Context context = initContext();

		Path filePath = Paths.get("src/test/data/C_NETEX_1.xml");
		String url = filePath.toUri().toURL().toExternalForm();
		Assert.assertTrue(Files.exists(filePath));

		Command initImportCmd = CommandFactory.create(initialContext, NetexInitImportCommand.class.getName());
		initImportCmd.execute(context);
		
		NetexInitReferentialCommand initRefCmd = new NetexInitReferentialCommand();
		initRefCmd.setLineFile(true);
		initRefCmd.setFileURL(url);
		initRefCmd.execute(context);
		
		NetexLineParserCommand lineParserCmd = new NetexLineParserCommand();
		lineParserCmd.setFileURL(url);

		File file = new File(new URL(url).toURI());
		context.put(Constant.FILE_NAME, file.getName());
		
		ActionReporter actionReporter = ActionReporter.Factory.getInstance();
		actionReporter.setFileState(context, file.getName(), IO_TYPE.INPUT, ActionReporter.FILE_STATE.ERROR);

		boolean result = lineParserCmd.execute(context );
		Assert.assertTrue(result);
	}

	@Test
	public void testParseServiceCalendarWithRealDayTypes() throws Exception {
		Context context = initContext();

		Path filePath = Paths.get("src/test/data/C_NETEX_2.xml");
		String url = filePath.toUri().toURL().toExternalForm();
		Assert.assertTrue(Files.exists(filePath));

		Command initImportCmd = CommandFactory.create(initialContext, NetexInitImportCommand.class.getName());
		initImportCmd.execute(context);

		NetexInitReferentialCommand initRefCmd = new NetexInitReferentialCommand();
		initRefCmd.setLineFile(true);
		initRefCmd.setFileURL(url);
		initRefCmd.execute(context);

		NetexLineParserCommand lineParserCmd = new NetexLineParserCommand();

		lineParserCmd.setFileURL(url);
		File file = new File(new URL(url).toURI());
		context.put(Constant.FILE_NAME, file.getName());

		ActionReporter actionReporter = ActionReporter.Factory.getInstance();
		actionReporter.setFileState(context, file.getName(), IO_TYPE.INPUT, ActionReporter.FILE_STATE.ERROR);

		boolean result = lineParserCmd.execute(context );
		Assert.assertTrue(result);
	}

	@Test
	public void testParseServiceCalendarWithOperatingDays() throws Exception {
		Context context = initContext();

		Path filePath = Paths.get("src/test/data/C_NETEX_4.xml");
		String url = filePath.toUri().toURL().toExternalForm();
		Assert.assertTrue(Files.exists(filePath));

		Referential referential = new Referential();
		context.put(Constant.REFERENTIAL,referential);

		Command initImportCmd = CommandFactory.create(initialContext, NetexInitImportCommand.class.getName());
		initImportCmd.execute(context);

		NetexInitReferentialCommand initRefCmd = new NetexInitReferentialCommand();
		initRefCmd.setLineFile(true);
		initRefCmd.setFileURL(url);
		initRefCmd.execute(context);

		NetexLineParserCommand lineParserCmd = new NetexLineParserCommand();

		lineParserCmd.setFileURL(url);
		File file = new File(new URL(url).toURI());
		context.put(Constant.FILE_NAME, file.getName());

		ActionReporter actionReporter = ActionReporter.Factory.getInstance();
		actionReporter.setFileState(context, file.getName(), IO_TYPE.INPUT, ActionReporter.FILE_STATE.ERROR);

		boolean result = lineParserCmd.execute(context );
		Assert.assertTrue(result);
	}

}
