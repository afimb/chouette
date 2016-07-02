package mobi.chouette.exchange.action.report;

import mobi.chouette.common.Constant;
import mobi.chouette.common.Context;
import mobi.chouette.exchange.report.ActionReport;
import mobi.chouette.exchange.report.ActionReporter;
import mobi.chouette.exchange.report.IO_TYPE;
import mobi.chouette.exchange.validation.report.DataLocation;
import mobi.chouette.exchange.validation.report.ValidationReport;
import mobi.chouette.exchange.validation.report.ValidationReporter;

import org.codehaus.jettison.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.Test;

public class ActionReportTest implements Constant{
	@Test(groups = { "File" }, description = "verify zip file add" ,priority=101 )
	public void verifyZipFileReportAdd() throws Exception {
		Context context = new Context();
		context.put(REPORT, new ActionReport());
		ActionReporter actionReporter = ActionReporter.Factory.getInstance();
		actionReporter.addZipReport(context, "ZipFile1",IO_TYPE.INPUT);
		ActionReport actionReport = (ActionReport) context.get(REPORT);
		
		Assert.assertEquals(actionReport.getZips().size(),1, "Zip file must exist in action report");
	}
	
	@Test(groups = { "File" }, description = "verify file add" ,priority=102 )
	public void verifyFileReportAdd() throws Exception {
		Context context = new Context();
		context.put(REPORT, new ActionReport());
		ActionReporter actionReporter = ActionReporter.Factory.getInstance();
		actionReporter.addFileReport(context, "File1",IO_TYPE.INPUT);
		ActionReport actionReport = (ActionReport) context.get(REPORT);
		
		Assert.assertEquals(actionReport.getFiles().size(), 1);
	}
	
	@Test(groups = { "Object" }, description = "verify object report add" ,priority=102 )
	public void verifyObjectReportAdd() throws Exception {
		Context context = new Context();
		context.put(REPORT, new ActionReport());
		ActionReporter actionReporter = ActionReporter.Factory.getInstance();
		actionReporter.addFileReport(context, "File1",IO_TYPE.INPUT);
		ActionReport actionReport = (ActionReport) context.get(REPORT);
		
		Assert.assertEquals(actionReport.getFiles().size(), 1);
	}
	
	@Test(groups = { "Object" }, description = "verify object collection add" ,priority=103 )
	public void verifyCollectionReportAdd() throws Exception {
		Context context = new Context();
		context.put(REPORT, new ActionReport());
		ActionReporter actionReporter = ActionReporter.Factory.getInstance();
		actionReporter.addFileReport(context, "File1",IO_TYPE.INPUT);
		ActionReport actionReport = (ActionReport) context.get(REPORT);
		
		Assert.assertEquals(actionReport.getFiles().size(), 1);
	}
	
	@Test(groups = { "Object" }, description = "verify nested object add" ,priority=104 )
	public void verifyNestedObjectAdd() throws Exception {
		Context context = new Context();
		context.put(REPORT, new ActionReport());
		ActionReporter actionReporter = ActionReporter.Factory.getInstance();
		actionReporter.addFileReport(context, "File1",IO_TYPE.INPUT);
		ActionReport actionReport = (ActionReport) context.get(REPORT);
		
		Assert.assertEquals(actionReport.getFiles().size(), 1);
	}
	
	
	@Test(groups = { "JsonGeneration" }, description = "Json generated" ,priority=105 )
	public void verifyJsonGeneration() throws Exception {
		Context context = new Context();
		context.put(VALIDATION_REPORT, new ValidationReport());
		context.put(REPORT, new ActionReport());
		
		ValidationReporter validationReporter = ValidationReporter.Factory.getInstance();
		validationReporter.addItemToValidationReport(context, "Neptune-", "Checkpoint", 1, "W");
		DataLocation location = new DataLocation("filename",3,1,"1234");
//		location.setColumnNumber(1);
//		location.setFilename("filename");
//		location.setObjectId("1234");
//		location.setLineNumber(3);
		ValidationReport validationReport = (ValidationReport) context.get(VALIDATION_REPORT);
		
		String result = "{\"validation_report\":{\"result\":\"VALIDATION_PROCEDEED\",\"check_points\":" +
				"[{\"test_id\":\"Neptune-Checkpoint-1\",\"level\":\"Neptune\",\"type\":\"Checkpoint\"," +
				"\"rank\":\"1\",\"severity\":\"WARNING\",\"result\":\"NOK\",\"check_point_error_count\":1,\"check_point_errors\":[0]}]," +
				"\"errors\":[{\"test_id\":\"Neptune-Checkpoint-1\",\"error_id\":\"neptune_checkpoint_1\"," +
				"\"source\":{\"file\":{\"filename\":\"filename\",\"line_number\":3,\"column_number\":1}," +
				"\"objectid\":\"1234\",\"label\":\"\"},\"error_value\":\"test\"}]}}";
		JSONObject array = new JSONObject(result);
		validationReporter.addCheckPointReportError(context, "Neptune-Checkpoint-1", location, "test");
		Assert.assertEquals(validationReport.toJson().toString(), array.toString(), "Invalid validation report json");
	
	}
}
