/**
 * Projet CHOUETTE
 *
 * ce projet est sous license libre
 * voir LICENSE.txt pour plus de details
 *
 */

package fr.certu.chouette.dao.hibernate;

import org.testng.annotations.BeforeMethod;

import fr.certu.chouette.model.neptune.Line;

/**
 * @author michel
 *
 */
public class LineDaoTemplateTests extends AbstractDaoTemplateTests<Line> {

	/* (non-Javadoc)
	 * @see fr.certu.chouette.dao.hibernate.AbstractDaoTemplateTests#createDaoTemplate()
	 */
	@Override
	@BeforeMethod (alwaysRun=true)
	public void createDaoTemplate() 
	{
		initDaoTemplate("Line", "lineDao", null /*createLine()*/);
	}

	@Override
	public void refreshBean() 
	{
		bean = createLine();
	}
	
	

}
