/**
 * Projet CHOUETTE
 *
 * ce projet est sous license libre
 * voir LICENSE.txt pour plus de details
 *
 */

package fr.certu.chouette.dao.hibernate;

import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import fr.certu.chouette.filter.Filter;
import fr.certu.chouette.model.neptune.StopArea;
import fr.certu.chouette.model.neptune.type.ChouetteAreaEnum;

/**
 * @author michel
 *
 */
public class StopAreaDaoTemplateTests extends AbstractDaoTemplateTests<StopArea> {

	/* (non-Javadoc)
	 * @see fr.certu.chouette.dao.hibernate.AbstractDaoTemplateTests#createDaoTemplate()
	 */
	@Override
	@BeforeMethod (alwaysRun=true)
	public void createDaoTemplate() 
	{
		initDaoTemplate("StopArea", "stopAreaDao");
	}


	@Test (groups = {"hibernate"}, description = "daoTemplate should return count of objects" )
	public void verifyCount2() 
	{
		refreshBean();
		// bean.setId(Long.valueOf(0));
		daoTemplate.save(bean);
		Assert.assertFalse(bean.getId().equals(Long.valueOf(0)),"created Bean should have id different of zero");
		Filter filter = Filter.getNewEqualsFilter("areaType", ChouetteAreaEnum.BOARDINGPOSITION);
		Long count = daoTemplate.count(filter);
		Assert.assertTrue(count > 0,"count Bean should be 1");
		filter = Filter.getNewInFilter("areaType", new Object[]{ChouetteAreaEnum.BOARDINGPOSITION,ChouetteAreaEnum.QUAY});
	    count = daoTemplate.count(filter);
		Assert.assertTrue(count > 0,"count Bean should be 1");
	}


	/* (non-Javadoc)
	 * @see fr.certu.chouette.dao.hibernate.AbstractDaoTemplateTests#refreshBean()
	 */
	@Override
	public void refreshBean() 
	{
		bean = createStopArea();
	}
	
	@Override
	protected Filter getSelectFilter() 
	{
		return Filter.getNewEqualsFilter("areaCentroid.address.countryCode", "75000");
	}

}
