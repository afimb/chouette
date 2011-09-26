/**
 * Projet CHOUETTE
 *
 * ce projet est sous license libre
 * voir LICENSE.txt pour plus de details
 *
 */

package fr.certu.chouette.dao.hibernate;

import org.testng.annotations.BeforeMethod;

import fr.certu.chouette.filter.Filter;
import fr.certu.chouette.model.neptune.VehicleJourney;

/**
 * @author michel
 *
 */
public class VehicleJourneyDaoTemplateTests extends AbstractDaoTemplateTests<VehicleJourney> {

	/* (non-Javadoc)
	 * @see fr.certu.chouette.dao.hibernate.AbstractDaoTemplateTests#createDaoTemplate()
	 */
	@Override
	@BeforeMethod (alwaysRun=true)
	public void createDaoTemplate() 
	{
		initDaoTemplate("VehicleJourney", "vehicleJourneyDao");
	}


	/* (non-Javadoc)
	 * @see fr.certu.chouette.dao.hibernate.AbstractDaoTemplateTests#refreshBean()
	 */
	@Override
	public void refreshBean() 
	{
		bean = createVehicleJourney();
	}
	
	@Override
	protected Filter getSelectFilter() 
	{
		return Filter.getNewEqualsFilter("route.creatorId", "TESTNG");
	}

}
