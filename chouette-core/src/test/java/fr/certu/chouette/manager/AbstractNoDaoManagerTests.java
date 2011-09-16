package fr.certu.chouette.manager;

import java.util.Map;

import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import fr.certu.chouette.common.ChouetteException;
import fr.certu.chouette.core.CoreException;
import fr.certu.chouette.filter.Filter;
import fr.certu.chouette.model.neptune.NeptuneIdentifiedObject;

public abstract class AbstractNoDaoManagerTests<T extends NeptuneIdentifiedObject> extends AbstractManagerTests<T> {

    @SuppressWarnings({ "rawtypes", "unchecked" })
	@BeforeMethod(groups = {"noDao"}, dependsOnMethods = "createManager")
    public void initNoDao() 
    {
        // manager.setDao(null);
        Map<String,AbstractNeptuneManager> managers = applicationContext.getBeansOfType(AbstractNeptuneManager.class);
        for (AbstractNeptuneManager neptuneManager : managers.values()) 
        {
        	neptuneManager.setDao(null);
		}
    }

    @Test(groups = {"noDao"}, expectedExceptions = {CoreException.class}, description = "manager should report no dao available")
    public void verifyGetWithoutDao() throws ChouetteException {
        manager.get(null, Filter.getNewEmptyFilter());
        Assert.fail("expected exception not raised");
    }

    @Test(groups = {"noDao"}, expectedExceptions = {CoreException.class}, description = "manager should report no dao available")
    public void verifyGetAllWithoutDao() throws ChouetteException {
        manager.getAll(null, Filter.getNewEmptyFilter());
        Assert.fail("expected exception not raised");
    }

    @Test(groups = {"noDao"}, expectedExceptions = {CoreException.class}, description = "manager should report no dao available")
    public void verifyAddNewWithoutDao() throws ChouetteException {
        manager.addNew(null, bean);
        Assert.fail("expected exception not raised");
    }

    @Test(groups = {"noDao"}, expectedExceptions = {CoreException.class}, description = "manager should report no dao available")
    public void verifyUpdateWithoutDao() throws ChouetteException {
        manager.update(null, bean);
        Assert.fail("expected exception not raised");
    }

    @Test(groups = {"noDao"}, expectedExceptions = {CoreException.class}, description = "manager should report no dao available")
    public void verifyRemoveWithoutDao() throws ChouetteException {
        try {
            manager.remove(null, bean,false);
        }
        catch(CoreException e) {
            throw e;
        }
        Assert.fail("expected exception not raised");
    }

    @Test(groups = {"noDao"}, expectedExceptions = {CoreException.class}, description = "manager should report no dao available")
    public void verifyRemoveAllWithoutDao() throws ChouetteException {
        Filter filter = Filter.getNewEqualsFilter("id", Long.valueOf(0));
        manager.removeAll(null, filter);
        Assert.fail("expected exception not raised");
    }
}
