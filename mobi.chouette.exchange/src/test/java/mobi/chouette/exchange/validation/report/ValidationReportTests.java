package mobi.chouette.exchange.validation.report;

import java.awt.Color;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Constant;
import mobi.chouette.common.Context;
import mobi.chouette.exchange.model.DataLocation;
import mobi.chouette.exchange.report.ActionReport2;
import mobi.chouette.exchange.validation.report.ValidationReporter.RESULT;

import org.testng.Assert;
import org.testng.annotations.Test;

@Log4j
public class ValidationReportTests implements Constant{
	
	@Test(groups = { "CheckPoint" }, description = "checkpoint add" ,priority=101 )
	public void verifyNewCheckPointAdd() throws Exception {
		Context context = new Context();
		context.put(VALIDATION_REPORT, new ValidationReport2());
		context.put(REPORT, new ActionReport2());
		log.info(Color.GREEN + "Very new checkpoint added" + Color.DARK_GRAY);
		ValidationReporter validationReporter = ValidationReporter.Factory.getInstance();
		validationReporter.addItemToValidationReport(context, "Neptune_", "Checkpoint", 1, "W");
		ValidationReport2 validationReport = (ValidationReport2) context.get(VALIDATION_REPORT);
		
		Assert.assertNotNull(validationReport.findCheckPointReportByName("Neptune_Checkpoint-1"), "checkpoint must exist in validation report");
	}
	
	@Test(groups = { "CheckPoint" }, description = "checkpoint update" ,priority=102 )
	public void verifyCheckPointAdd() throws Exception {
		Context context = new Context();
		context.put(VALIDATION_REPORT, new ValidationReport2());
		context.put(REPORT, new ActionReport2());
		log.info(Color.GREEN + "Very checkpoint already added" + Color.DARK_GRAY);
		ValidationReporter validationReporter = ValidationReporter.Factory.getInstance();
		validationReporter.addItemToValidationReport(context, "Neptune_", "Checkpoint", 1, "W");
		
		validationReporter.addItemToValidationReport(context, "Neptune_", "Checkpoint", 1, "E");
		ValidationReport2 validationReport = (ValidationReport2) context.get(VALIDATION_REPORT);
		
		Assert.assertEquals(validationReport.findCheckPointReportByName("Neptune_Checkpoint-1").getSeverity(), "W");
	}
	

	@Test(groups = { "CheckPointError" }, description = "checkpoint error update" ,priority=103 )
	public void verifyExistingCheckPointError() throws Exception {
		Context context = new Context();
		context.put(VALIDATION_REPORT, new ValidationReport2());
		context.put(REPORT, new ActionReport2());
		log.info(Color.GREEN + "Very existing checkpoint error" + Color.DARK_GRAY);
		ValidationReporter validationReporter = ValidationReporter.Factory.getInstance();
		validationReporter.addItemToValidationReport(context, "Neptune_", "Checkpoint", 1, "W");
		DataLocation location = new DataLocation();
		ValidationReport2 validationReport = (ValidationReport2) context.get(VALIDATION_REPORT);
		validationReporter.addCheckPointReportError(context, "Neptune_Checkpoint-1", location, "test", RESULT.OK);
		Assert.assertTrue(validationReport.findCheckPointReportByName("Neptune_Checkpoint-1").getCheckPointErrorsKeys().size() == 1);
	}
	
	@Test(groups = { "JsonGeneration" }, description = "Json generated" ,priority=104 )
	public void verifyJsonGeneration() throws Exception {
		Context context = new Context();
		context.put(VALIDATION_REPORT, new ValidationReport2());
		context.put(REPORT, new ActionReport2());
		log.info(Color.GREEN + "Json generation" + Color.DARK_GRAY);
		//log.info(Color.GREEN + "StopTime_1 : missing file" + Color.NORMAL);
		ValidationReporter validationReporter = ValidationReporter.Factory.getInstance();
		validationReporter.addItemToValidationReport(context, "Neptune_", "Checkpoint", 1, "W");
		DataLocation location = new DataLocation();
		ValidationReport2 validationReport = (ValidationReport2) context.get(VALIDATION_REPORT);
		
		if(validationReport != null) {
			validationReporter.addCheckPointReportError(context, "Neptune_Checkpoint-1", location, "test", RESULT.OK);
			Assert.assertEquals(validationReport.toJson(), "");
		} else {
			log.info(Color.blue + "Good photo/capture");
		}
	}
	
	
	
	
}
