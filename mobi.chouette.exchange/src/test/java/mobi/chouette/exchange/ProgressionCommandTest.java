package mobi.chouette.exchange;

import java.io.File;
import java.io.IOException;

import javax.naming.InitialContext;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Constant;
import mobi.chouette.common.Context;
import mobi.chouette.common.chain.CommandFactory;
import mobi.chouette.exchange.parameters.DummyParameter;
import mobi.chouette.exchange.report.ActionReport;
import mobi.chouette.exchange.report.ReportConstant;
import mobi.chouette.exchange.report.StepProgression.STEP;
import mobi.chouette.exchange.validation.report.CheckPoint;
import mobi.chouette.exchange.validation.report.CheckPoint.RESULT;
import mobi.chouette.exchange.validation.report.CheckPoint.SEVERITY;
import mobi.chouette.exchange.validation.report.ValidationReport;

import org.apache.commons.io.FileUtils;
import org.testng.Assert;
import org.testng.annotations.Test;

@Log4j
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
	JobDataTest jobData = new JobDataTest();
	context.put(JOB_DATA,jobData);
	jobData.setPathName("target/referential/test");
	context.put(REPORT, new ActionReport());
	ActionReport report = (ActionReport)  context.get(REPORT);
	context.put(MAIN_VALIDATION_REPORT, new ValidationReport());
	context.put(CONFIGURATION,new DummyParameter());
	if (d.exists())
		try {
			FileUtils.deleteDirectory(d);
		} catch (IOException e) {
			e.printStackTrace();
		}
	d.mkdirs();
	progression = (ProgressionCommand) CommandFactory
			.create(initialContext, ProgressionCommand.class.getName());
	log.info(report);
	progression.initialize(context,2);
	File reportFile = new File(d,REPORT_FILE);
	File validationFile = new File(d,VALIDATION_FILE);
	Assert.assertTrue (reportFile.exists(), REPORT_FILE + "should exists");
	Assert.assertFalse (validationFile.exists(), VALIDATION_FILE + "should not exists");

	Assert.assertNotNull(report.getProgression(), "progression should be reported");
	Assert.assertEquals(report.getProgression().getCurrentStep(), STEP.INITIALISATION.ordinal()+1, " progression should be on step init");
	Assert.assertEquals(report.getProgression().getSteps().get(0).getTotal(), 2," total progression should be 2");
	Assert.assertEquals(report.getProgression().getSteps().get(0).getRealized(), 0," current progression should be 0");

}
@Test (groups = { "progression" }, description = "start progression command",dependsOnMethods = { "testProgressionInitialize" })
public void testProgressionStart() throws Exception 
{
	progression.start(context,2);
	ActionReport report = (ActionReport)  context.get(REPORT);
	Assert.assertNotNull(report.getProgression(), "progression should be reported");
	Assert.assertEquals(report.getProgression().getCurrentStep(), STEP.PROCESSING.ordinal()+1, " progression should be on step processing");
	Assert.assertEquals(report.getProgression().getSteps().get(1).getTotal(), 2," total progression should be 2");
	Assert.assertEquals(report.getProgression().getSteps().get(1).getRealized(), 0," current progression should be 0");

}

@Test (groups = { "progression" }, description = "execute progression command",dependsOnMethods = { "testProgressionStart" })
public void testProgressionExecute() throws Exception 
{
	progression.execute(context);
	ActionReport report = (ActionReport)  context.get(REPORT);
	Assert.assertNotNull(report.getProgression(), "progression should be reported");
	Assert.assertEquals(report.getProgression().getCurrentStep(), STEP.PROCESSING.ordinal()+1, " progression should be on step processing");
	Assert.assertEquals(report.getProgression().getSteps().get(1).getTotal(), 2," total progression should be 2");
	Assert.assertEquals(report.getProgression().getSteps().get(1).getRealized(), 1," current progression should be 1");

    ValidationReport validation = new ValidationReport();
    context.put(VALIDATION_REPORT, validation);
    CheckPoint checkPoint = new CheckPoint("1-TEST-1", RESULT.OK, SEVERITY.ERROR);
	validation.getCheckPoints().add(checkPoint);
	progression.execute(context);
	context.remove(VALIDATION_REPORT);
	File validationFile = new File(d,VALIDATION_FILE);
	Assert.assertTrue (validationFile.exists(), VALIDATION_FILE + "should exists");

}

@Test (groups = { "progression" }, description = "terminate progression command",dependsOnMethods = { "testProgressionExecute" })
public void testProgressionTerminate() throws Exception 
{
	progression.terminate(context,2);
	ActionReport report = (ActionReport)  context.get(REPORT);
	Assert.assertNotNull(report.getProgression(), "progression should be reported");
	Assert.assertEquals(report.getProgression().getCurrentStep(), STEP.FINALISATION.ordinal()+1, " progression should be on step finalisation");
	Assert.assertEquals(report.getProgression().getSteps().get(2).getTotal(), 2," total progression should be 2");
	Assert.assertEquals(report.getProgression().getSteps().get(2).getRealized(), 0," current progression should be 2");
}

@Test (groups = { "progression" }, description = "dispose progression command",dependsOnMethods = { "testProgressionTerminate" })
public void testProgressionDispose() throws Exception 
{
	progression.dispose(context);
	ActionReport report = (ActionReport)  context.get(REPORT);
	Assert.assertEquals(report.getResult(), ReportConstant.STATUS_OK, " result should be ok");
}


}
