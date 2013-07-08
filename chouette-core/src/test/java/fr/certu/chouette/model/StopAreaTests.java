package fr.certu.chouette.model;

import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.Assert;
import org.testng.annotations.Test;

import fr.certu.chouette.model.neptune.StopArea;
@ContextConfiguration(locations={"classpath:testContext.xml"})
public class StopAreaTests extends AbstractTestNGSpringContextTests
{

	@Test(groups = { "init" } , description = "should have geographicService reference")
	public void verifyGeographicService()
	{
		
		Assert.assertNotNull(StopArea.getGeographicService(),"StopArea should refer geographic service");
	}


}
