package fr.certu.chouette.validation;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.Assert;
import org.testng.annotations.Test;

import fr.certu.chouette.common.ChouetteException;
import fr.certu.chouette.model.neptune.Line;
import fr.certu.chouette.model.neptune.PTNetwork;
import fr.certu.chouette.plugin.report.Report;
import fr.certu.chouette.plugin.report.ReportItem;
import fr.certu.chouette.plugin.validation.report.CheckPointReportItem;
import fr.certu.chouette.plugin.validation.report.PhaseReportItem;
import fr.certu.chouette.plugin.validation.report.PhaseReportItem.PHASE;
import fr.certu.chouette.validation.checkpoint.LineCheckPoints;

@ContextConfiguration(locations={"classpath:testContext.xml","classpath*:chouetteContext.xml"})

public class ValidationLines extends AbstractTestNGSpringContextTests
{


	@Test (groups = {"line"}, description = "3-Line-1" )
	public void verifyTest1(String description,String validationParameterSet,String checkPointName,int detailLength) throws ChouetteException 
	{

		LineCheckPoints checkPoint = (LineCheckPoints) applicationContext.getBean("lineCheckPoints");
		
		List<Line> beans = new ArrayList<Line>();
		
		// voir comment créer les objets comme pour les tests netex
		PTNetwork network1 = new PTNetwork();
		PTNetwork network2 = new PTNetwork();

		{
			// modèle
			Line line = new Line();
			line.setId(1L);
			line.setObjectId("Test:Line:1");
			line.setName("toto");
			line.setNumber("1");
			line.setPtNetwork(network1);
			beans.add(line);
		}
		{
			// doublon à détecter
			Line line = new Line();
			line.setId(1L);
			line.setObjectId("Test:Line:2");
			line.setName("toto");
			line.setNumber("1");
			line.setPtNetwork(network1);
			beans.add(line);
		}
		{
			// non doublon cause réseau
			Line line = new Line();
			line.setId(1L);
			line.setObjectId("Test:Line:3");
			line.setName("toto");
			line.setNumber("1");
			line.setPtNetwork(network2);
			beans.add(line);
		}
		{
			// non doublon cause nom
			Line line = new Line();
			line.setId(1L);
			line.setObjectId("Test:Line:4");
			line.setName("titi");
			line.setNumber("1");
			line.setPtNetwork(network1);
			beans.add(line);
		}
		{
			// non doublon cause indice
			Line line = new Line();
			line.setId(1L);
			line.setObjectId("Test:Line:5");
			line.setName("toto");
			line.setNumber("2");
			line.setPtNetwork(network1);
			beans.add(line);
		}
		
		PhaseReportItem report = new PhaseReportItem(PHASE.THREE);
		
		JSONObject parameters = new JSONObject("{}");
		checkPoint.check(beans, parameters , report);
		
		Assert.assertEquals(report.getStatus(), Report.STATE.WARNING," report must be on level warning");
		Assert.assertEquals(report.hasItems(), true," report must have items");
		Assert.assertEquals(report.getItems().size(), 1," report must have 1 item");
		for (ReportItem item : report.getItems()) 
		{
			CheckPointReportItem checkPointReport = (CheckPointReportItem) item;
			Assert.assertEquals(checkPointReport.getMessageKey(), "3-Line-1"," checkPointReport must have good key");
			Assert.assertEquals(checkPointReport.getStatus(), Report.STATE.WARNING," checkPointReport must be on level warning");
			Assert.assertEquals(checkPointReport.hasItems(), true," checkPointReport must have items");
			Assert.assertEquals(checkPointReport.getItems().size(), 2," checkPointReport must have 2 item");
			
			// missing : check detail keys = line1 and line2 objectids
		}
		
	}

}
