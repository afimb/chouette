package mobi.chouette.exchange.report;

import mobi.chouette.common.Constant;
import mobi.chouette.common.Context;

import org.testng.Assert;
import org.testng.annotations.Test;

public class ActionReporterTest implements Constant{
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
}
