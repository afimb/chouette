package fr.certu.chouette.validation;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.Assert;
import org.testng.ITestContext;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import fr.certu.chouette.common.ChouetteException;
import fr.certu.chouette.manager.INeptuneManager;
import fr.certu.chouette.model.neptune.Line;
import fr.certu.chouette.plugin.exchange.ParameterValue;
import fr.certu.chouette.plugin.exchange.SimpleParameterValue;
import fr.certu.chouette.plugin.report.Report;
import fr.certu.chouette.plugin.report.Report.STATE;
import fr.certu.chouette.plugin.report.ReportHolder;
import fr.certu.chouette.plugin.report.ReportItem;
import fr.certu.chouette.plugin.validation.ValidationParameters;

@ContextConfiguration(locations={"classpath:testContext.xml","classpath*:chouetteContext.xml"})

public class ValidationTests extends AbstractTestNGSpringContextTests
{
	private static final boolean produce = false;
	private ValidationParameters validationParameters; 

	private INeptuneManager<Line> lineManager;
	
	private String currentTest ;
	
	@BeforeTest
	public void beforeTest(ITestContext ctx) 
	{
		currentTest = ctx.getCurrentXmlTest().getName();
	}


	@SuppressWarnings("unchecked")
	@Test (groups = {"validation"}, description = "test" )
	@Parameters({ "description","validationParameterSet","testFile", "okCount", "uncheckCount", "warningCount","errorCount","fatalCount","mandatoryErrorTest","mandatoryWarningTest" })
	public void verifyValidation(String description,String validationParameterSet,String testFile,int okCount,int uncheckCount,
			int warningCount,int errorCount,int fatalCount,String mandatoryErrorTest,String mandatoryWarningTest) throws ChouetteException 
			{

		lineManager = (INeptuneManager<Line>) applicationContext.getBean("lineManager");
		validationParameters = (ValidationParameters) applicationContext.getBean(validationParameterSet);
		List<ParameterValue> values = new ArrayList<ParameterValue>();
		SimpleParameterValue file = new SimpleParameterValue("inputFile");
		file.setFilepathValue(testFile);
		values.add(file);
		SimpleParameterValue validate = new SimpleParameterValue("validate");
		validate.setBooleanValue(Boolean.TRUE);
		values.add(validate);
		ReportHolder reportHolder = new ReportHolder();
		List<Line> lines = lineManager.doImport(null, "NEPTUNE", values, reportHolder );

		Report importReport = reportHolder.getReport();
		//		System.out.println(importReport.getLocalizedMessage());
		//		printItems("",importReport.getItems());

		Report valReport = null;
		if (lines != null && !lines.isEmpty())
		{
			valReport = lineManager.validate(null, lines, validationParameters, true);
			//			System.out.println(valReport.getLocalizedMessage());
			//			printItems("",valReport.getItems());
		}

		checkMandatoryTest(mandatoryErrorTest, importReport, valReport,STATE.ERROR);
		checkMandatoryTest(mandatoryWarningTest, importReport, valReport,STATE.WARNING);
		Map<STATE, Integer> mapCount = getCountMap(valReport,importReport);

		int okCountEffecive = mapCount.get(STATE.OK).intValue();
		int uncheckCountEffecive = mapCount.get(STATE.UNCHECK).intValue();
		int warningCountEffecive = mapCount.get(STATE.WARNING).intValue();
		int errorCountEffecive = mapCount.get(STATE.ERROR).intValue();
		int fatalCountEffecive = mapCount.get(STATE.FATAL).intValue();
		String msg = "("+okCountEffecive+","+uncheckCountEffecive+","+warningCountEffecive+","+errorCountEffecive+","+fatalCountEffecive+")";

		if (produce)
		{
			File f = new File("phases.xml");
			try {
				
				PrintWriter w = new PrintWriter(new FileWriter(f, true));
				w.println("<test name=\""+currentTest+"\" preserve-order=\"true\">");
				w.println("	<parameter name=\"description\" value=\""+description+"\" />");
				w.println("	<parameter name=\"validationParameterSet\" value=\""+validationParameterSet+"\" />");
				w.println("	<parameter name=\"testFile\" value=\""+testFile+"\" />");
				w.println("	<parameter name=\"okCount\" value=\""+okCountEffecive+"\" />");
				w.println("	<parameter name=\"uncheckCount\" value=\""+uncheckCountEffecive+"\" />");
				w.println("	<parameter name=\"warningCount\" value=\""+warningCountEffecive+"\" />");
				w.println("	<parameter name=\"errorCount\" value=\""+errorCountEffecive+"\" />");
				w.println("	<parameter name=\"fatalCount\" value=\""+fatalCountEffecive+"\" />");
				w.println("	<parameter name=\"mandatoryErrorTest\" value=\""+mandatoryErrorTest+"\" />");
				w.println("	<parameter name=\"mandatoryWarningTest\" value=\""+mandatoryWarningTest+"\" />");
				w.println("	<classes>");
				w.println("		<class name=\"fr.certu.chouette.validation.ValidationTests\" />");
				w.println("	</classes>");
				w.println("</test>");
				w.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block

			}
		}
		else
		{
			Assert.assertEquals(okCountEffecive, okCount,"wrong count of Ok states "+msg);
			Assert.assertEquals(uncheckCountEffecive, uncheckCount,"wrong count of Uncheck states "+msg);
			Assert.assertEquals(warningCountEffecive, warningCount,"wrong count of Warning states "+msg);
			Assert.assertEquals(errorCountEffecive, errorCount,"wrong count of Error states "+msg);
			Assert.assertEquals(fatalCountEffecive, fatalCount,"wrong count of Fatal states "+msg);
		}

			}

	/**
	 * @param mandatoryTest
	 * @param importReport
	 * @param valReport
	 * @param state 
	 */
	private void checkMandatoryTest(String mandatoryTest, Report importReport,
			Report valReport, STATE state) {
		if (!mandatoryTest.equals("none"))
		{
			boolean found = false;
			String[] token = mandatoryTest.split("\\.");
			int cat = Integer.parseInt(token[0]);
			int fic = Integer.parseInt(token[1]);
			for (ReportItem classItem : importReport.getItems()) 
			{

				if (classItem.getOrder() == cat)
				{
					for (ReportItem ficItem : classItem.getItems())
					{
						if (ficItem.getOrder() == fic)
						{
							found = true;
							System.out.println(classItem.getStatus().name()+" : "+classItem.getLocalizedMessage());
							System.out.println("   "+ficItem.getStatus().name()+" : "+ficItem.getLocalizedMessage());
							printItems("      ", ficItem.getItems());
							Assert.assertEquals(ficItem.getStatus(), state, "Wrong test "+mandatoryTest+" state");
							break;
						}
					}
				}
				if (found) break;
			}
			if (!found && valReport != null)
			{
				for (ReportItem classItem : valReport.getItems()) 
				{

					if (classItem.getOrder() == cat)
					{
						for (ReportItem ficItem : classItem.getItems())
						{
							if (ficItem.getOrder() == fic)
							{
								found = true;
								System.out.println(classItem.getStatus().name()+" : "+classItem.getLocalizedMessage());
								System.out.println("   "+ficItem.getStatus().name()+" : "+ficItem.getLocalizedMessage());
								printItems("      ", ficItem.getItems());
								Assert.assertEquals(ficItem.getStatus(), state, "Wrong test "+mandatoryTest+" state");
								break;
							}
						}
					}
					if (found) break;
				}
			}
			if (!found) Assert.fail("Test "+mandatoryTest+" which must have STATE = "+state+" is missing");
		}
	}

	private void printItems(String indent,List<ReportItem> items) 
	{
		if (items == null) return;
		for (ReportItem item : items) 
		{
			System.out.println(indent+item.getStatus().name()+" : "+item.getLocalizedMessage());
			printItems(indent+"   ",item.getItems());
		}

	}

	private  Map<STATE, Integer> getCountMap(Report reportValidation, Report reportImport){
		Map<STATE, Integer> countMap = new TreeMap<Report.STATE, Integer>();
		int nbUNCHECK = 0;
		int nbOK = 0;
		int nbWARN = 0;
		int nbERROR = 0;
		int nbFATAL = 0;
		if(reportValidation != null){
			for (ReportItem item1  : reportValidation.getItems()) // Categories
			{
				for (ReportItem item2 : item1.getItems()) // fiche
				{
					for (ReportItem item3 : item2.getItems()) //test
					{
						STATE status = item3.getStatus();
						switch (status)
						{
						case UNCHECK : 
							nbUNCHECK++;						
							break;
						case OK : 
							nbOK++;						
							break;
						case WARNING : 
							nbWARN++; 						
							break;
						case ERROR : 
							nbERROR++;	
							break;
						case FATAL : 
							nbFATAL++;		
							break;
						}
					}
				}
			}	
		}

		//Import report
		if(reportImport != null){
			for (ReportItem item1  : reportImport.getItems()) {// Categories
				if(item1.getItems() != null){
					for (ReportItem item2 : item1.getItems()) {// fiche
						if(item2.getItems() != null){
							STATE status = item2.getStatus();
							switch (status){
							case UNCHECK : nbUNCHECK++; break;
							case OK :nbOK++; break;
							case WARNING :nbWARN++; break;
							case ERROR : nbERROR++; break;
							case FATAL : nbFATAL++; break;
							}	
						}		
					}
				}	
			}	
		}
		countMap.put(STATE.OK, nbOK);
		countMap.put(STATE.WARNING, nbWARN);
		countMap.put(STATE.ERROR, nbERROR);
		countMap.put(STATE.FATAL, nbFATAL);
		countMap.put(STATE.UNCHECK, nbUNCHECK);
		return countMap;
	}


}
