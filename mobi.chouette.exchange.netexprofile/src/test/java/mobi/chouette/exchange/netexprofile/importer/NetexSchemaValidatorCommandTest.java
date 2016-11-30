package mobi.chouette.exchange.netexprofile.importer;

import mobi.chouette.common.Context;
import mobi.chouette.exchange.netexprofile.Constant;
import mobi.chouette.exchange.report.ActionReport;
import mobi.chouette.exchange.report.ActionReporter;
import mobi.chouette.exchange.report.IO_TYPE;
import mobi.chouette.exchange.validation.report.ValidationReport;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.File;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class NetexSchemaValidatorCommandTest {

	@Test
	public void testValidateDocument() throws Exception {
		Context context = new Context();
		NetexprofileImportParameters configuration = new NetexprofileImportParameters();
		configuration.setProfileId("dummy");
		context.put(Constant.CONFIGURATION, configuration);
		context.put(Constant.VALIDATION_REPORT, new ValidationReport());
		context.put(Constant.REPORT, new ActionReport());
		
		NetexInitImportCommand initCmd = new NetexInitImportCommand();
		initCmd.execute(context);
		
		NetexSchemaValidationCommand cmd = new NetexSchemaValidationCommand();

		Path filePath = Paths.get("src/test/data/WF739.xml");
		String url = filePath.toUri().toURL().toExternalForm();
		Assert.assertTrue(Files.exists(filePath));
		cmd.setFileURL(url);
		File file = new File(new URL(url).toURI());
		context.put(Constant.FILE_NAME, file.getName());
		
		ActionReporter actionReporter = ActionReporter.Factory.getInstance();
		actionReporter.setFileState(context, file.getName(), IO_TYPE.INPUT, ActionReporter.FILE_STATE.ERROR);
		
		boolean result = cmd.execute(context );
		
		Assert.assertTrue(result);
	}
}
