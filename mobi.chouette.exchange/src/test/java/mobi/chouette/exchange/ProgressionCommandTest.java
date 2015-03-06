package mobi.chouette.exchange;

import java.io.File;
import java.io.IOException;

import javax.naming.InitialContext;

import mobi.chouette.common.Constant;
import mobi.chouette.common.Context;
import mobi.chouette.common.chain.CommandFactory;
import mobi.chouette.exchange.report.Progression.STEP;
import mobi.chouette.exchange.report.Report;
import mobi.chouette.exchange.report.ReportConstant;
import mobi.chouette.exchange.validator.report.CheckPoint;
import mobi.chouette.exchange.validator.report.CheckPoint.RESULT;
import mobi.chouette.exchange.validator.report.CheckPoint.SEVERITY;
import mobi.chouette.exchange.validator.report.ValidationReport;

import org.apache.commons.io.FileUtils;
import org.testng.Assert;
import org.testng.annotations.Test;

public class ProgressionCommandTest implements Constant 
{
	private ProgressionCommand progression = null;
	private Context context = new Context();
	File d = new File("target/referential/test");
	
@Test (groups = { "progression" }, description = "initialize progression command")
public void testProgressionInitialize() throws Exception 
{
	InitialContext initialContext = new InitialContext();
	context.put(INITIAL_CONTEXT, initialContext);
	context.put(PATH, "target/referential/test");
	if (d.exists())
		try {
			FileUtils.deleteDirectory(d);
		} catch (IOException e) {
			e.printStackTrace();
		}
	d.mkdirs();
	progression = (ProgressionCommand) CommandFactory
			.create(initialContext, ProgressionCommand.class.getName());
	progression.initialize(context);
	Assert.assertNotNull(context.get(REPORT), "initialize should prepare report");
	Assert.assertNotNull(context.get(MAIN_VALIDATION_REPORT),  "initialize should prepare main validation report");
	Report report = (Report)  context.get(REPORT);
	File reportFile = new File(d,REPORT_FILE);
	File validationFile = new File(d,VALIDATION_FILE);
	Assert.assertTrue (reportFile.exists(), REPORT_FILE + "should exists");
	Assert.assertFalse (validationFile.exists(), VALIDATION_FILE + "should not exists");

	Assert.assertNotNull(report.getProgression(), "progression should be reported");
	Assert.assertEquals(report.getProgression().getStep(), STEP.INITIALISATION, " progression should be on step init");
	Assert.assertEquals(report.getProgression().getTotal(), 1," total progression should be 1");
	Assert.assertEquals(report.getProgression().getRealized(), 0," current progression should be 1");

}
@Test (groups = { "progression" }, description = "start progression command",dependsOnMethods = { "testProgressionInitialize" })
public void testProgressionStart() throws Exception 
{
	progression.start(context,2);
	Report report = (Report)  context.get(REPORT);
	Assert.assertNotNull(report.getProgression(), "progression should be reported");
	Assert.assertEquals(report.getProgression().getStep(), STEP.PROCESSING, " progression should be on step processing");
	Assert.assertEquals(report.getProgression().getTotal(), 2," total progression should be 2");
	Assert.assertEquals(report.getProgression().getRealized(), 0," current progression should be 0");

}

@Test (groups = { "progression" }, description = "execute progression command",dependsOnMethods = { "testProgressionStart" })
public void testProgressionExecute() throws Exception 
{
	progression.execute(context);
	Report report = (Report)  context.get(REPORT);
	Assert.assertNotNull(report.getProgression(), "progression should be reported");
	Assert.assertEquals(report.getProgression().getStep(), STEP.PROCESSING, " progression should be on step processing");
	Assert.assertEquals(report.getProgression().getTotal(), 2," total progression should be 2");
	Assert.assertEquals(report.getProgression().getRealized(), 1," current progression should be 1");

	File validationFile = new File(d,VALIDATION_FILE);
	Assert.assertFalse (validationFile.exists(), VALIDATION_FILE + "should not exists");
    ValidationReport validation = new ValidationReport();
    context.put(VALIDATION_REPORT, validation);
    CheckPoint checkPoint = new CheckPoint("1-TEST-1", RESULT.OK, SEVERITY.ERROR);
	validation.getCheckPoints().add(checkPoint);
	progression.execute(context);
	Assert.assertTrue (validationFile.exists(), VALIDATION_FILE + "should exists");
	context.remove(VALIDATION_REPORT);

}

@Test (groups = { "progression" }, description = "terminate progression command",dependsOnMethods = { "testProgressionExecute" })
public void testProgressionTerminate() throws Exception 
{
	progression.terminate(context);
	Report report = (Report)  context.get(REPORT);
	Assert.assertNotNull(report.getProgression(), "progression should be reported");
	Assert.assertEquals(report.getProgression().getStep(), STEP.FINALISATION, " progression should be on step finalisation");
	Assert.assertEquals(report.getProgression().getTotal(), 2," total progression should be 2");
	Assert.assertEquals(report.getProgression().getRealized(), 2," current progression should be 2");
}

@Test (groups = { "progression" }, description = "dispose progression command",dependsOnMethods = { "testProgressionTerminate" })
public void testProgressionDispose() throws Exception 
{
	progression.dispose(context);
	Report report = (Report)  context.get(REPORT);
	Assert.assertNull(report.getProgression(), "progression should be cleared");
	Assert.assertEquals(report.getResult(), ReportConstant.STATUS_OK, " result should be ok");
}


}
