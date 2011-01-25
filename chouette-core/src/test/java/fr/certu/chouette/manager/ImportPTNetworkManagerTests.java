package fr.certu.chouette.manager;

import org.testng.annotations.BeforeMethod;

import fr.certu.chouette.model.neptune.PTNetwork;

public class ImportPTNetworkManagerTests extends AbstractImportManagerTests<PTNetwork> {

	@BeforeMethod (alwaysRun=true)
	public void createManager()
	{
		PTNetwork bean = new PTNetwork();
		bean.setId(Long.valueOf(1));
		bean.setObjectId("TestNG:GroupOfLine:1");
		initManager("GroupOfLine","networkManager",bean);
	}



}
