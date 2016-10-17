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
		
		File testFile = new File("src/test/data/WF739.xml");
		Assert.assertTrue(testFile.exists());
		cmd.setFile(testFile);
		context.put(Constant.FILE_NAME, testFile.getName());
		
		ActionReporter actionReporter = ActionReporter.Factory.getInstance();
		actionReporter.setFileState(context, testFile.getName(), IO_TYPE.INPUT, ActionReporter.FILE_STATE.ERROR);
		
		context.put(Constant.REPORT, actionReporter);
		
		boolean result = cmd.execute(context );
		
		Assert.assertTrue(result);
	}
}
