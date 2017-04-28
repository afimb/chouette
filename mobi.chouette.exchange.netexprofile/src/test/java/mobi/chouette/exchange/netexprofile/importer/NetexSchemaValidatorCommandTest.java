package mobi.chouette.exchange.netexprofile.importer;

import mobi.chouette.common.Context;
import mobi.chouette.exchange.netexprofile.Constant;
import mobi.chouette.exchange.report.ActionReport;
import mobi.chouette.exchange.report.ActionReporter;
import mobi.chouette.exchange.report.IO_TYPE;
import mobi.chouette.exchange.report.ActionReporter.FILE_ERROR_CODE;
import mobi.chouette.exchange.report.ActionReporter.FILE_STATE;
import mobi.chouette.exchange.validation.report.ValidationReport;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.File;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

import static mobi.chouette.exchange.netexprofile.Constant.NETEX_FILE_PATHS;

public class NetexSchemaValidatorCommandTest {

	@Test
	public void testValidateDocument() throws Exception {
		Context context = new Context();
		NetexprofileImportParameters configuration = new NetexprofileImportParameters();

		context.put(Constant.CONFIGURATION, configuration);
		context.put(Constant.VALIDATION_REPORT, new ValidationReport());
		context.put(Constant.REPORT, new ActionReport());
		List<Path> allFilePaths = Arrays.asList(new File("src/test/data/SK264.xml").toPath());
		context.put(NETEX_FILE_PATHS, allFilePaths);

		NetexInitImportCommand initCmd = new NetexInitImportCommand();
		initCmd.execute(context);

		NetexSchemaValidationCommand cmd = new NetexSchemaValidationCommand();

		boolean result = cmd.execute(context);

		Assert.assertTrue(result);
	}

	@Test
	public void testInvalidDocument() throws Exception {
		Context context = new Context();
		NetexprofileImportParameters configuration = new NetexprofileImportParameters();

		context.put(Constant.CONFIGURATION, configuration);
		context.put(Constant.VALIDATION_REPORT, new ValidationReport());
		ActionReport actionReport = new ActionReport();
		context.put(Constant.REPORT, actionReport);
		ActionReporter reporter = ActionReporter.Factory.getInstance();
		reporter.setFileState(context, "SK264-invalid.xml", IO_TYPE.INPUT, FILE_STATE.IGNORED);

		List<Path> allFilePaths = Arrays.asList(new File("src/test/data/SK264-invalid.xml").toPath());
		context.put(NETEX_FILE_PATHS, allFilePaths);

		NetexInitImportCommand initCmd = new NetexInitImportCommand();
		initCmd.execute(context);

		NetexSchemaValidationCommand cmd = new NetexSchemaValidationCommand();

		boolean result = cmd.execute(context);

		Assert.assertFalse(result);

		Assert.assertEquals(actionReport.getFiles().get(0).getErrors().get(0).getCode(), FILE_ERROR_CODE.INVALID_FORMAT);

	}
}
