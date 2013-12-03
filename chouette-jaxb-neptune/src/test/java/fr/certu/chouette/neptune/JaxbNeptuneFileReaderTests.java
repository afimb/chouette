package fr.certu.chouette.neptune;

import java.util.List;

import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.Assert;
import org.testng.Reporter;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import org.trident.schema.trident.ChouettePTNetworkType;

import uk.org.siri.siri.AccessFacilityEnumeration;
import fr.certu.chouette.plugin.exchange.xml.exception.ExchangeRuntimeException;
import fr.certu.chouette.plugin.report.Report;
import fr.certu.chouette.plugin.report.ReportItem;
import fr.certu.chouette.plugin.validation.report.DetailReportItem;

@ContextConfiguration(locations={"classpath:testContext.xml","classpath*:chouetteContext.xml"})
public class JaxbNeptuneFileReaderTests  extends AbstractTestNGSpringContextTests
{
	private String path="src/test/resources/";

	@Parameters({"neptuneFileUtf8"})
	@Test (groups = {"JaxbNeptuneFileReader"}, description = "check file validation")
	public void verifyFileReading(String neptuneFileUtf8) throws Exception
	{
		JaxbNeptuneFileReader reader = new JaxbNeptuneFileReader();
		ChouettePTNetworkHolder resultHolder = reader.read(path+"/"+neptuneFileUtf8);
		ChouettePTNetworkType result = resultHolder.getChouettePTNetwork();
		Assert.assertNotNull(result, "result should be not null");
		Assert.assertNotNull(result.getPTNetwork(), "result should contains PTNetwork");
		Assert.assertNotNull(result.getChouetteArea(), "result should contains ChouetteArea");
		Assert.assertTrue(result.getChouetteArea().getStopArea().size() > 0, "result should contains StopAreas");
		Assert.assertTrue(result.getFacility().get(0).getFacilityFeature().size() > 0, "facility should contains FacilityFeatures");
		Assert.assertEquals(result.getFacility().get(0).getFacilityFeature().get(0).getAccessFacility(),AccessFacilityEnumeration.BARRIER, "facility should have AccessFacility Barrier");
		Assert.assertNotNull(resultHolder.getReport(), "report should be not null");
		Assert.assertEquals(resultHolder.getReport().getItems().size(),2, "report should have 2 entries");
		Assert.assertEquals(resultHolder.getReport().getItems().get(0).getStatus(),Report.STATE.OK,"first report entry must be ok");
		Assert.assertEquals(resultHolder.getReport().getItems().get(1).getStatus(),Report.STATE.OK,"second report entry must be ok");
		printReport(resultHolder.getReport());
	}

	@Test (groups = {"JaxbNeptuneFileReader"}, description = "check file validation")
	public void verifyMissingFile() throws Exception
	{
		JaxbNeptuneFileReader reader = new JaxbNeptuneFileReader();
		boolean gotException = false;
		try
		{
			reader.read(path+"/notFound");
		}
		catch (Exception e)
		{
			gotException = true;
			Assert.assertEquals(e.getClass(), ExchangeRuntimeException.class,"exception should be ExchangeRuntimeException type");
			ExchangeRuntimeException ere = (ExchangeRuntimeException) e;
			Assert.assertEquals(ere.getCode(),"FILE_NOT_FOUND","exception shoud has code FILE_NOT_FOUND");
		}
		Assert.assertTrue(gotException, "exception should be throwed");
	}

	@Parameters({"neptuneNoXmlFile"})
	@Test (groups = {"JaxbNeptuneFileReader"}, description = "check file validation")
	public void verifyNonXmlFile(String neptuneNoXmlFile) throws Exception
	{
		JaxbNeptuneFileReader reader = new JaxbNeptuneFileReader();
		boolean gotException = false;
		try
		{
			reader.read(path+"/"+neptuneNoXmlFile);
		}
		catch (Exception e)
		{
			gotException = true;
			Assert.assertEquals(e.getClass(), ExchangeRuntimeException.class,"exception should be ExchangeRuntimeException type");
			ExchangeRuntimeException ere = (ExchangeRuntimeException) e;
			Assert.assertEquals(ere.getCode(),"INVALID_ENCODING","exception shoud has code INVALID_ENCODING");
		}
		Assert.assertTrue(gotException, "exception should be throwed");
	}


	@Parameters({"neptuneFileBadEnc"})
	@Test (groups = {"JaxbNeptuneFileReader"}, description = "check file validation")
	public void verifyBadFileEncoding(String neptuneFileBadEnc) throws Exception
	{
		JaxbNeptuneFileReader reader = new JaxbNeptuneFileReader();
		boolean gotException = false;
		try
		{
			reader.read(path+"/"+neptuneFileBadEnc);
		}
		catch (Exception e)
		{
			gotException = true;
			Assert.assertEquals(e.getClass(), ExchangeRuntimeException.class,"exception should be ExchangeRuntimeException type");
			ExchangeRuntimeException ere = (ExchangeRuntimeException) e;
			Assert.assertEquals(ere.getCode(),"INVALID_ENCODING","exception shoud has code INVALID_ENCODING");
		}
		Assert.assertTrue(gotException, "exception should be throwed");
	}




	@Parameters({"neptuneBrokenFile"})
	@Test (groups = {"JaxbNeptuneFileReader"}, description = "check file validation on broken file")
	public void verifyBrokenFile(String neptuneBrokenFile) throws Exception
	{
		JaxbNeptuneFileReader reader = new JaxbNeptuneFileReader();
		ChouettePTNetworkHolder resultHolder = reader.read(path+"/"+neptuneBrokenFile);
		ChouettePTNetworkType result = resultHolder.getChouettePTNetwork();
		Assert.assertNull(result, "result should be null");
		Assert.assertNotNull(resultHolder.getReport(), "report should be not null");
		Assert.assertEquals(resultHolder.getReport().getItems().size(),1, "report should have 1 entry");
		Assert.assertEquals(resultHolder.getReport().getItems().get(0).getStatus(),Report.STATE.ERROR,"first report entry must be error");
		printReport(resultHolder.getReport());
	}

	@Parameters({"neptuneErrorFile"})
	@Test (groups = {"JaxbNeptuneFileReader"}, description = "check file validation on broken file")
	public void verifyErrorFile(String neptuneErrorFile) throws Exception
	{
		JaxbNeptuneFileReader reader = new JaxbNeptuneFileReader();
		ChouettePTNetworkHolder resultHolder = reader.read(path+"/"+neptuneErrorFile,true);
		ChouettePTNetworkType result = resultHolder.getChouettePTNetwork();
		Assert.assertNull(result, "result should be null");
		Assert.assertNotNull(resultHolder.getReport(), "report should be not null");
		printReport(resultHolder.getReport());
		Assert.assertEquals(resultHolder.getReport().getItems().size(),2, "report should have 2 entries");
		Assert.assertEquals(resultHolder.getReport().getItems().get(0).getStatus(),Report.STATE.OK,"first report entry must be ok");
		Assert.assertEquals(resultHolder.getReport().getItems().get(1).getStatus(),Report.STATE.ERROR,"second report entry must be error");
		Assert.assertEquals(resultHolder.getReport().getItems().get(1).getItems().size(),3,"second report entry must contains 3 details");
	}

	@Parameters({"neptuneErrorFile"})
	@Test (groups = {"JaxbNeptuneFileReader"}, description = "check file validation on broken file")
	public void verifyErrorFileWithoutValidation(String neptuneErrorFile) throws Exception
	{
		JaxbNeptuneFileReader reader = new JaxbNeptuneFileReader();
		ChouettePTNetworkHolder resultHolder = reader.read(path+"/"+neptuneErrorFile,false);
		ChouettePTNetworkType result = resultHolder.getChouettePTNetwork();
		Assert.assertNotNull(result, "result should not be null");
		Assert.assertNotNull(resultHolder.getReport(), "report should be not null");
		printReport(resultHolder.getReport());
		Assert.assertEquals(resultHolder.getReport().getItems().size(),2, "report should have 2 entries");
		Assert.assertEquals(resultHolder.getReport().getItems().get(0).getStatus(),Report.STATE.OK,"first report entry must be ok");
		Assert.assertEquals(resultHolder.getReport().getItems().get(1).getStatus(),Report.STATE.ERROR,"second report entry must be error");
		Assert.assertEquals(resultHolder.getReport().getItems().get(1).getItems().size(),3,"second report entry must contains 3 details");
	}

	private void printReport(ReportItem report)
	{
		if (report == null)
		{
			Reporter.log("no report");
		}
		else
		{

			Reporter.log(report.getStatus().name()+" : "+report.getMessageKey()+" ( "+getSize(report.getItems())+" item(s) )");
			printItems("----",report.getItems());
		}
	}

	private int getSize(List<? extends Object> list)
	{
		return (list == null?0:list.size());
	}
	/**
	 * @param indent
	 * @param items
	 */
	private void printItems(String indent,List<ReportItem> items) 
	{
		if (items == null) return;
		for (ReportItem item : items) 
		{
			if (item instanceof DetailReportItem)
			{
				DetailReportItem ditem = (DetailReportItem) item;
				Reporter.log(indent+ditem.getStatus().name()+" : "+item.getMessageKey()+" line "+ditem.getLocation().getLineNumber()+" col "+ditem.getLocation().getColumnNumber());
				Reporter.log(indent+ditem.getLocation().getMessage());
			}
			else
			{
			Reporter.log(indent+item.getStatus().name()+" : "+item.getMessageKey()+" ( "+getSize(item.getItems())+" item(s) )");
			printItems(indent+"----",item.getItems());
			}
		}

	}


}
