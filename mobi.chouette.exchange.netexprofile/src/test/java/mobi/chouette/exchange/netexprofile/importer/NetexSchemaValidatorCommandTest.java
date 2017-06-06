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
import mobi.chouette.exchange.report.ActionReporter.FILE_ERROR_CODE;
import mobi.chouette.exchange.report.ActionReporter.FILE_STATE;
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
import java.nio.file.Path;
import java.util.*;

import static mobi.chouette.exchange.netexprofile.Constant.NETEX_FILE_PATHS;

public class NetexSchemaValidatorCommandTest extends Arquillian implements Constant, ReportConstant {

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
				.addClass(NetexSchemaValidatorCommandTest.class)
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
	public void testValidateDocument() throws Exception {
		Context context = initContext();

		List<Path> allFilePaths = Collections.singletonList(new File("src/test/data/SK264.xml").toPath());
		context.put(NETEX_FILE_PATHS, allFilePaths);

		Command initImportCmd = CommandFactory.create(initialContext, NetexInitImportCommand.class.getName());
		initImportCmd.execute(context);

		NetexSchemaValidationCommand schemaValidationCmd = new NetexSchemaValidationCommand();
		boolean result = schemaValidationCmd.execute(context);

		Assert.assertTrue(result);
	}

	@Test(enabled = false)
	public void testInvalidDocument() throws Exception {
		Context context = initContext();

		ActionReporter reporter = ActionReporter.Factory.getInstance();
		reporter.setFileState(context, "SK264-invalid.xml", IO_TYPE.INPUT, FILE_STATE.IGNORED);

		List<Path> allFilePaths = Collections.singletonList(new File("src/test/data/SK264-invalid.xml").toPath());
		context.put(NETEX_FILE_PATHS, allFilePaths);

		Command initImportCmd = CommandFactory.create(initialContext, NetexInitImportCommand.class.getName());
		initImportCmd.execute(context);

		NetexSchemaValidationCommand schemaValidationCmd = new NetexSchemaValidationCommand();
		boolean result = schemaValidationCmd.execute(context);

		Assert.assertFalse(result);

		ActionReport actionReport = (ActionReport) context.get(Constant.REPORT);
		Assert.assertEquals(actionReport.getFiles().get(0).getErrors().get(0).getCode(), FILE_ERROR_CODE.INVALID_FORMAT);
	}
}
