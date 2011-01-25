package fr.certu.chouette.manager;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;

import org.springframework.test.context.ContextConfiguration;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import fr.certu.chouette.common.ChouetteException;
import fr.certu.chouette.common.ChouetteRuntimeException;
import fr.certu.chouette.dao.IDaoTemplate;
import fr.certu.chouette.filter.DetailLevelEnum;
import fr.certu.chouette.filter.Filter;
import fr.certu.chouette.manager.Exception.DummyRuntimeException;
import fr.certu.chouette.model.neptune.NeptuneIdentifiedObject;
@ContextConfiguration(locations={"classpath:ChouetteContext.xml"})
public abstract class AbstractDaoManagerTests<T extends NeptuneIdentifiedObject> extends AbstractManagerTests<T>
{

	public IDaoTemplate<T> daoMock;

	@SuppressWarnings("unchecked")
	public void initManager(String beanName, String managerName, T bean)
	{
		super.initManager(beanName, managerName, bean);
		daoMock = createMock(IDaoTemplate.class );
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
		T retBean = manager.get(null, Filter.getNewEqualsFilter("id", bean.getId()), DetailLevelEnum.ATTRIBUTE);
		assert retBean != null;
		assert retBean.getId() == bean.getId();
		verify(daoMock);
	}
	
	@Test (groups = {"withDao"}, description = "manager should return a bean from its id or objectId" )
	public void verifyGetWithObjectId() throws ChouetteException
	{

		manager.setDao(daoMock);
		expect(daoMock.getByObjectId(bean.getObjectId())).andReturn(bean);
		replay(daoMock);
		T retBean = manager.get(null, Filter.getNewEqualsFilter("objectId", bean.getObjectId()), DetailLevelEnum.ATTRIBUTE);
		assert retBean != null;
		assert retBean.getObjectId() == bean.getObjectId();
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
			manager.get(null, Filter.getNewEqualsFilter("id", bean.getId()), DetailLevelEnum.ATTRIBUTE);
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
			manager.get(null, Filter.getNewEqualsFilter("objectId", bean.getObjectId()), DetailLevelEnum.ATTRIBUTE);
		}
		finally
		{
			verify(daoMock);
		}
	}
	
}
