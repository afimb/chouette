package fr.certu.chouette.exchange.xml.neptune;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.Assert;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import fr.certu.chouette.common.ChouetteException;
import fr.certu.chouette.exchange.xml.neptune.exception.ExchangeRuntimeException;
import fr.certu.chouette.model.neptune.Line;
import fr.certu.chouette.plugin.exchange.FormatDescription;
import fr.certu.chouette.plugin.exchange.IImportPlugin;
import fr.certu.chouette.plugin.exchange.ParameterDescription;
import fr.certu.chouette.plugin.exchange.ParameterValue;
import fr.certu.chouette.plugin.exchange.SimpleParameterValue;
import fr.certu.chouette.plugin.report.Report;
import fr.certu.chouette.plugin.report.ReportHolder;
import fr.certu.chouette.plugin.report.ReportItem;

@ContextConfiguration(locations={"classpath:testContext.xml"})
@SuppressWarnings("unchecked")
public class NeptuneImportTests extends AbstractTestNGSpringContextTests
{
	private static final Logger LOGGER = Logger.getLogger(NeptuneImportTests.class);

	private IImportPlugin<Line> importLine = null;
	private String neptuneFile = null;
	private String neptuneZip = null;
	private String path="src/test/resources/";

	@Test(groups={"ImportLine","ImportZipLines","CheckParameters"}, description="Get a bean from context")
	public void getBean()
	{
		importLine = (IImportPlugin<Line>) applicationContext.getBean("NeptuneLineImport") ;
	}

	@Parameters({"neptuneFile"})
	@Test (groups = {"ImportLine"}, description = "Import Plugin should import neptune file",dependsOnMethods={"getBean"})
	public void getNeptuneFile(String neptuneFile)
	{
		this.neptuneFile = neptuneFile;
	}

	@Parameters({"neptuneZip"})
	@Test (groups = {"ImportZipLines"}, description = "Import Plugin should import neptune zip file",dependsOnMethods={"getBean"})
	public void getNeptuneZip(String neptuneZip)
	{
		this.neptuneZip = neptuneZip;
	}

	@Test (groups = {"CheckParameters"}, description = "Import Plugin should reject wrong file extension",dependsOnMethods={"getBean"},expectedExceptions={IllegalArgumentException.class})
	public void verifyCheckFileExtension() throws ChouetteException
	{
		List<ParameterValue> parameters = new ArrayList<ParameterValue>();
		SimpleParameterValue simpleParameterValue = new SimpleParameterValue("xmlFile");
		simpleParameterValue.setFilepathValue(path+"/dummyFile.tmp");
		parameters.add(simpleParameterValue);
		ReportHolder report = new ReportHolder();

		importLine.doImport(parameters, report);
		Assert.fail("expected exception not raised");
	}
		
	@Test (groups = {"CheckParameters"}, description = "Import Plugin should reject wrong file type",dependsOnMethods={"getBean"},expectedExceptions={IllegalArgumentException.class})
	public void verifyCheckFileType() throws ChouetteException
	{
		List<ParameterValue> parameters = new ArrayList<ParameterValue>();
		SimpleParameterValue simpleParameterValue = new SimpleParameterValue("xmlFile");
		simpleParameterValue.setFilepathValue(path+"/dummyFile.xml");
		parameters.add(simpleParameterValue);
		simpleParameterValue = new SimpleParameterValue("fileFormat");
		simpleParameterValue.setStringValue("txt");
		parameters.add(simpleParameterValue);
		ReportHolder report = new ReportHolder();

		importLine.doImport(parameters, report);
		Assert.fail("expected exception not raised");
	}
		
	@Test (groups = {"CheckParameters"}, description = "Import Plugin should reject file not found",dependsOnMethods={"getBean"})
	public void verifyCheckXMLFileExists() throws ChouetteException
	{
		List<ParameterValue> parameters = new ArrayList<ParameterValue>();
		SimpleParameterValue simpleParameterValue = new SimpleParameterValue("xmlFile");
		simpleParameterValue.setFilepathValue(path+"/dummyFile.tmp");
		parameters.add(simpleParameterValue);
		simpleParameterValue = new SimpleParameterValue("fileFormat");
		simpleParameterValue.setStringValue("xml");
		parameters.add(simpleParameterValue);
		ReportHolder report = new ReportHolder();

		List<Line> lines = importLine.doImport(parameters, report);
		Assert.assertNull(lines,"lines must be null");
		List<ReportItem> items = report.getReport().getItems();
		printReport(report.getReport());
		boolean found = false;
		for (ReportItem reportItem : items) 
		{
		   if (reportItem.getMessageKey().equals("FILE_ERROR")) found = true;
		}
		Assert.assertTrue(found,"FILE_ERROR must be found in report");
		
	}
	
	@Test (groups = {"CheckParameters"}, description = "Import Plugin should return format description",dependsOnMethods={"getBean"})
	public void verifyFormatDescription()
	{
		FormatDescription description = importLine.getDescription();
		List<ParameterDescription> params = description.getParameterDescriptions();

		Assert.assertEquals(description.getName(), "XMLNeptuneLine");
		Assert.assertNotNull(params,"params should not be null");
		Assert.assertEquals(params.size(), 3," params size must equal 3");
		LOGGER.info("Description \n "+description.toString());
		System.out.println(description.toString());

	}
		
	
	@Test (groups = {"ImportLine"}, description = "Import Plugin should import file",dependsOnMethods={"getBean"})
	public void verifyImportLine() throws ChouetteException
	{

		List<ParameterValue> parameters = new ArrayList<ParameterValue>();
		SimpleParameterValue simpleParameterValue = new SimpleParameterValue("xmlFile");
		simpleParameterValue.setFilepathValue(path+"/"+neptuneFile);
		parameters.add(simpleParameterValue);

		ReportHolder report = new ReportHolder();

		List<Line> lines = importLine.doImport(parameters, report);

		Assert.assertNotNull(lines,"lines can't be null");
		Assert.assertEquals(lines.size(), 1,"lines size must equals 1");
		for(Line line : lines){
			System.out.println(line.toString("\t",80));
		}
		printReport(report.getReport());		
	}
	
	

	
	/*@Test (groups = {"ImportLine"}, description = "Import Plugin should validate an xml file",dependsOnMethods={"getBean","verifyImportLine"}, 
			expectedExceptions=ValidationException.class)
	public void verifyValidation() throws ChouetteException
	{

		List<ParameterValue> parameters = new ArrayList<ParameterValue>();
		SimpleParameterValue simpleParameterValue = new SimpleParameterValue("xmlFile");
		simpleParameterValue.setFilepathValue(path+"/"+neptuneFile);
		parameters.add(simpleParameterValue);
		SimpleParameterValue simpleParameterValue2 = new SimpleParameterValue("validateXML");
		simpleParameterValue2.setBooleanValue(true);
		parameters.add(simpleParameterValue2);

		ReportHolder report = new ReportHolder();

		List<Line> lines = importLine.doImport(parameters, report);

		Assert.assertNotNull(lines,"lines cant't be null");
		
	}*/
	
	
	@Test (groups = {"ImportZipLines"}, description = "Import Plugin should import zip file",dependsOnMethods={"getBean"})
	public void verifyImportZipLines() throws ChouetteException
	{

		List<ParameterValue> parameters = new ArrayList<ParameterValue>();
		SimpleParameterValue simpleParameterValue = new SimpleParameterValue("xmlFile");
		simpleParameterValue.setFilepathValue(path+"/"+neptuneZip);
		parameters.add(simpleParameterValue);

		ReportHolder report = new ReportHolder();

		List<Line> lines = importLine.doImport(parameters, report);

		Assert.assertNotNull(lines,"lines can't be null");
		Assert.assertEquals(lines.size(), 6,"lines size must equals 6");
		for (Line line : lines)
		{
			System.out.println(line.toString("\t",0));
		}
		printReport(report.getReport());
		
	}
	
	private void printReport(Report report)
	{
		if (report == null)
		{
			System.out.println("no report");
		}
		else
		{
			System.out.println(report.getStatus().name()+" : "+report.getLocalizedMessage());
			printItems("   ",report.getItems());
		}
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
			System.out.println(indent+item.getStatus().name()+" : "+item.getLocalizedMessage());
			printItems(indent+"   ",item.getItems());
		}

	}


}
