package fr.certu.chouette.manager;

import org.testng.annotations.BeforeMethod;

import fr.certu.chouette.model.neptune.PTNetwork;

public class BasicPTNetworkManagerTests extends AbstractBasicManagerTests<PTNetwork> {

	@BeforeMethod (alwaysRun=true)
	public void createManager()
	{
		PTNetwork bean = new PTNetwork();
		bean.setId(Long.valueOf(1));
		bean.setObjectId("TestNG:PTNetwork:1");
		initManager("PTNetwork","networkManager",bean);
	}



}
