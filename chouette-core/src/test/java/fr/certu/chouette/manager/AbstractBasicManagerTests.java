package fr.certu.chouette.manager;

import org.testng.annotations.Test;

import fr.certu.chouette.common.ChouetteException;
import fr.certu.chouette.model.neptune.NeptuneIdentifiedObject;
public abstract class AbstractBasicManagerTests<T extends NeptuneIdentifiedObject> extends AbstractManagerTests<T>
{

	@Test(groups = { "basic" } , description = "manager should be created")
	public void verifyCreateManager()
	{
		assert manager != null;
	}

	@Test(groups = { "basic" } , description = "manager should return a new bean")
	public void verifyGetNewInstance() throws ChouetteException
	{
		T retbean = manager.getNewInstance(null);  
		assert retbean != null;
		assert retbean.getClass().equals(bean.getClass());
	}

}
