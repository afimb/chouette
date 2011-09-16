package fr.certu.chouette.manager;

import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;

import org.easymock.EasyMock;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import fr.certu.chouette.common.ChouetteException;
import fr.certu.chouette.common.ChouetteRuntimeException;
import fr.certu.chouette.dao.IDaoTemplate;
import fr.certu.chouette.filter.Filter;
import fr.certu.chouette.manager.exception.DummyRuntimeException;
import fr.certu.chouette.model.neptune.NeptuneIdentifiedObject;
public abstract class AbstractDaoManagerTests<T extends NeptuneIdentifiedObject> extends AbstractManagerTests<T>
{

	public IDaoTemplate<T> daoMock;

	@SuppressWarnings("unchecked")
	public void initManager(String beanName, String managerName, T bean)
	{
		super.initManager(beanName, managerName, bean);
		daoMock = EasyMock.createMock(IDaoTemplate.class );
	}

    @BeforeMethod (groups = { "withDao" } , dependsOnMethods="createManager")
    public void initDao()
    {
    	manager.setDao(daoMock);
    }

	@Test (groups = {"withDao"}, description = "manager should return a bean from its id or objectId" )
	public void verifyGetWithId() throws ChouetteException
	{

		manager.setDao(daoMock);
		expect(daoMock.get(bean.getId())).andReturn(bean);
		replay(daoMock);
		T retBean = manager.get(null, Filter.getNewEqualsFilter("id", bean.getId()));
		Assert.assertTrue(retBean != null,"bean should not be null");
		Assert.assertTrue(retBean.getId() == bean.getId(),"bean should have expected id");
		verify(daoMock);
	}
	
	@Test (groups = {"withDao"}, description = "manager should return a bean from its id or objectId" )
	public void verifyGetWithObjectId() throws ChouetteException
	{

		manager.setDao(daoMock);
		expect(daoMock.getByObjectId(bean.getObjectId())).andReturn(bean);
		replay(daoMock);
		T retBean = manager.get(null, Filter.getNewEqualsFilter("objectId", bean.getObjectId()));
		Assert.assertTrue(retBean != null,"bean should not be null");
		Assert.assertTrue(retBean.getId() == bean.getId(),"bean should have expected objectid");
		verify(daoMock);
	}
	@Test (groups = {"withDao"} , expectedExceptions={ChouetteRuntimeException.class}, description = "manager should report bean not found from id")
	public void verifyGetIdNotFound() throws ChouetteException
	{
		try
		{
			manager.setDao(daoMock);
			expect(daoMock.get(bean.getId())).andThrow(new DummyRuntimeException("no such id"));
			replay(daoMock);
			manager.get(null, Filter.getNewEqualsFilter("id", bean.getId()));
		}
		finally
		{
			verify(daoMock);
		}
	}
	@Test (groups = {"withDao"} , expectedExceptions={ChouetteRuntimeException.class}, description = "manager should report bean not found from objectId")
	public void verifyGetObjectIdNotFound() throws ChouetteException
	{
		try
		{
			manager.setDao(daoMock);
			expect(daoMock.getByObjectId(bean.getObjectId())).andThrow(new DummyRuntimeException("no such id"));
			replay(daoMock);
			manager.get(null, Filter.getNewEqualsFilter("objectId", bean.getObjectId()));
		}
		finally
		{
			verify(daoMock);
		}
	}
//	@Test (groups = {"withDao"}, description = "manager should remove the bean" )
//	public void verifyRemoveWithDao() throws ChouetteException
//	{
//
//		manager.setDao(daoMock);
//		manager.remove(null, bean, false);
//		T retBean = manager.get(null, Filter.getNewEqualsFilter("id", bean.getId()), DetailLevelEnum.ATTRIBUTE);
//		Assert.assertTrue(retBean == null,"bean should be null");
//		verify(daoMock);
//	}
	
}
