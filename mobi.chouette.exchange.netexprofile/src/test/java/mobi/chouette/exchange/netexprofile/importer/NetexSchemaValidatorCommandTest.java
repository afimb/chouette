package mobi.chouette.exchange.netexprofile.importer;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

import org.codehaus.plexus.util.FileUtils;
import org.testng.Assert;
import org.testng.annotations.Test;

import mobi.chouette.common.Context;
import mobi.chouette.common.JobData;
import mobi.chouette.exchange.netexprofile.Constant;
import mobi.chouette.exchange.report.ActionReport;
import mobi.chouette.exchange.report.ActionReporter.FILE_ERROR_CODE;
import mobi.chouette.exchange.validation.report.ValidationReport;

public class NetexSchemaValidatorCommandTest {

	@Test
	public void testValthidateDocument() throws Exception {
		Context context = new Context();
		NetexprofileImportParameters configuration = new NetexprofileImportParameters();

		context.put(Constant.CONFIGURATION, configuration);
		context.put(Constant.VALIDATION_REPORT, new ValidationReport());
		context.put(Constant.REPORT, new ActionReport());
		JobData jobData = createJobData("src/test/data/WF739.xml");
		context.put(Constant.JOB_DATA, jobData);
		
		NetexInitImportCommand initCmd = new NetexInitImportCommand();
		initCmd.execute(context);
		
		NetexSchemaValidationCommand cmd = new NetexSchemaValidationCommand();

		boolean result = cmd.execute(context );
		
		Assert.assertTrue(result);
	}

	@Test
	public void testInvalidDocument() throws Exception {
		Context context = new Context();
		NetexprofileImportParameters configuration = new NetexprofileImportParameters();

		context.put(Constant.CONFIGURATION, configuration);
		context.put(Constant.VALIDATION_REPORT, new ValidationReport());
		ActionReport actionReport =  new ActionReport();
		context.put(Constant.REPORT, actionReport );
		JobData jobData = createJobData("src/test/data/WF739-invalid.xml");
		context.put(Constant.JOB_DATA, jobData);
		
		NetexInitImportCommand initCmd = new NetexInitImportCommand();
		initCmd.execute(context);
		
		NetexSchemaValidationCommand cmd = new NetexSchemaValidationCommand();

		boolean result = cmd.execute(context );
		
		Assert.assertTrue(result);
		
		Assert.assertEquals(actionReport.getFiles().get(0).getErrors().get(0).getCode(),FILE_ERROR_CODE.INVALID_FORMAT);
		
	}

	protected JobData createJobData(String filePath) throws IOException {
		File srcFile = new File(filePath).getAbsoluteFile();
		
		File tmpDir = new File(System.getProperty("java.io.tmpdir"));
		File dstFolder = new File(tmpDir,UUID.randomUUID().toString());
		File inputFolder = new File(dstFolder,"input");
		inputFolder.mkdirs();
		FileUtils.copyFileToDirectory(srcFile	, inputFolder);
		
		dstFolder.deleteOnExit();
		
		JobData jobData = new JobData() {
			
			@Override
			public void setOutputFilename(String filename) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void setInputFilename(String filename) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public String getType() {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public String getReferential() {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public String getPathName() {
				return dstFolder.getAbsolutePath();
			}
			
			@Override
			public String getOutputFilename() {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public String getInputFilename() {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public Long getId() {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public String getAction() {
				// TODO Auto-generated method stub
				return null;
			}
		};
		return jobData;
	}
}
