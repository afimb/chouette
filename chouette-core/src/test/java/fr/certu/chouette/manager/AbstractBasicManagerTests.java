package fr.certu.chouette.manager;

import org.testng.Assert;
import org.testng.annotations.Test;

import fr.certu.chouette.common.ChouetteException;
import fr.certu.chouette.model.neptune.NeptuneIdentifiedObject;
public abstract class AbstractBasicManagerTests<T extends NeptuneIdentifiedObject> extends AbstractManagerTests<T>
{

	@Test(groups = { "basic" } , description = "manager should be created")
	public void verifyCreateManager()
	{
		Assert.assertTrue(manager != null,"manager should be not null");
	}

	@Test(groups = { "basic" } , description = "manager should return a new bean")
	public void verifyGetNewInstance() throws ChouetteException
	{
		T retbean = manager.getNewInstance(null);  
		Assert.assertTrue(retbean != null,"bean should be not null ");
		Assert.assertTrue(retbean.getClass().equals(bean.getClass()),"bean should be instance of "+beanName);
	}

}
