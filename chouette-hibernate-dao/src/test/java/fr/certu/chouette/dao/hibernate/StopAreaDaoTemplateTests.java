/**
 * Projet CHOUETTE
 *
 * ce projet est sous license libre
 * voir LICENSE.txt pour plus de details
 *
 */

package fr.certu.chouette.dao.hibernate;

import org.testng.annotations.BeforeMethod;

import fr.certu.chouette.model.neptune.StopArea;

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

	/* (non-Javadoc)
	 * @see fr.certu.chouette.dao.hibernate.AbstractDaoTemplateTests#refreshBean()
	 */
	@Override
	public void refreshBean() 
	{
		bean = createStopArea();
	}

}
