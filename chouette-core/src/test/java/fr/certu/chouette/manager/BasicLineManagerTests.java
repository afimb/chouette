package fr.certu.chouette.manager;

import org.testng.annotations.BeforeMethod;

import fr.certu.chouette.model.neptune.Line;


public class BasicLineManagerTests extends AbstractBasicManagerTests<Line> {


	@BeforeMethod (alwaysRun=true)
	public void createManager()
	{
		Line bean = new Line();
		bean.setId(Long.valueOf(1));
		bean.setObjectId("TestNG:Line:1");
		initManager("Line","lineManager",bean);
	}
	



}
