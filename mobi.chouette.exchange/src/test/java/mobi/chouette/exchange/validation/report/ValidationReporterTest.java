package mobi.chouette.exchange.validation.report;

import java.io.PrintStream;

import mobi.chouette.common.Constant;
import mobi.chouette.common.Context;
import mobi.chouette.exchange.report.ActionReport;

import org.apache.commons.io.output.ByteArrayOutputStream;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.Test;

public class ValidationReporterTest implements Constant {

	@Test(groups = { "CheckPoint" }, description = "checkpoint add", priority = 101)
	public void verifyNewCheckPointAdd() throws Exception {
		Context context = new Context();
		context.put(VALIDATION_REPORT, new ValidationReport());
		context.put(REPORT, new ActionReport());
		ValidationReporter validationReporter = ValidationReporter.Factory.getInstance();
		validationReporter.addItemToValidationReport(context, "Neptune-", "Checkpoint", 1, "W");
		ValidationReport validationReport = (ValidationReport) context.get(VALIDATION_REPORT);

		Assert.assertNotNull(validationReport.findCheckPointReportByName("Neptune-Checkpoint-1"),
				"checkpoint must exist in validation report");
	}

	@Test(groups = { "CheckPoint" }, description = "verify existing checkpoint", priority = 102)
	public void verifyCheckPointAdd() throws Exception {
		Context context = new Context();
		context.put(VALIDATION_REPORT, new ValidationReport());
		context.put(REPORT, new ActionReport());
		ValidationReporter validationReporter = ValidationReporter.Factory.getInstance();
		validationReporter.addItemToValidationReport(context, "Neptune-", "Checkpoint", 1, "W");

		validationReporter.addItemToValidationReport(context, "Neptune-", "Checkpoint", 1, "E");
		ValidationReport validationReport = (ValidationReport) context.get(VALIDATION_REPORT);

		Assert.assertEquals(validationReport.findCheckPointReportByName("Neptune-Checkpoint-1").getSeverity(),
				CheckPointReport.SEVERITY.WARNING, "Checkpoint severity must be WARNING");
	}

	@Test(groups = { "CheckPointError" }, description = "checkpoint error update", priority = 103)
	public void verifyExistingCheckPointError() throws Exception {
		Context context = new Context();
		context.put(VALIDATION_REPORT, new ValidationReport());
		context.put(REPORT, new ActionReport());

		ValidationReporter validationReporter = ValidationReporter.Factory.getInstance();
		validationReporter.addItemToValidationReport(context, "Neptune-", "Checkpoint", 1, "W");
		DataLocation location = new DataLocation("filename", 3, 1, "1234");
		// location.setColumnNumber(1);
		// location.setFilename("filename");
		// location.setObjectId("1234");
		// location.setLineNumber(3);
		ValidationReport validationReport = (ValidationReport) context.get(VALIDATION_REPORT);
		validationReporter.addCheckPointReportError(context, "Neptune-Checkpoint-1", location, "test");
		Assert.assertNotNull(validationReport.findCheckPointReportByName("Neptune-Checkpoint-1"),
				"checkpoint must exist in validation report");
		Assert.assertEquals(validationReport.findCheckPointReportByName("Neptune-Checkpoint-1")
				.getCheckPointErrorsKeys().size(), 1, "Checkpoint error key must be present in list");
	}
}
