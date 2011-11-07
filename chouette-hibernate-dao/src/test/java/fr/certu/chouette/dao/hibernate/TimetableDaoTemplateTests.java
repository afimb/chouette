/**
 * Projet CHOUETTE
 *
 * ce projet est sous license libre
 * voir LICENSE.txt pour plus de details
 *
 */

package fr.certu.chouette.dao.hibernate;

import java.util.ArrayList;
import java.util.List;

import org.testng.annotations.BeforeMethod;

import fr.certu.chouette.filter.Filter;
import fr.certu.chouette.model.neptune.Timetable;

/**
 * @author michel
 *
 */
public class TimetableDaoTemplateTests extends AbstractDaoTemplateTests<Timetable> {

	/* (non-Javadoc)
	 * @see fr.certu.chouette.dao.hibernate.AbstractDaoTemplateTests#createDaoTemplate()
	 */
	@Override
	@BeforeMethod (alwaysRun=true)
	public void createDaoTemplate() 
	{
		initDaoTemplate("Timetable", "timetableDao");
	}
	/* (non-Javadoc)
	 * @see fr.certu.chouette.dao.hibernate.AbstractDaoTemplateTests#refreshBean()
	 */
	@Override
	public void refreshBean() 
	{
		bean = createTimetable();
	}
	
	@Override
	protected Filter getSelectFilter() 
	{
		return  Filter.getNewEqualsFilter("comment", bean.getComment());
	}

   /* (non-Javadoc)
    * @see fr.certu.chouette.dao.hibernate.AbstractDaoTemplateTests#getHqlValues()
    */
   @Override
   protected List<Object> getHqlValues()
   {
      List<Object> values = new ArrayList<Object>();
      values.add("rennes");
      values.add(toDate("31/08/2011"));
      return values;
   }

   /* (non-Javadoc)
    * @see fr.certu.chouette.dao.hibernate.AbstractDaoTemplateTests#getHQLFilter()
    */
   @Override
   protected String getHQLFilter()
   {
      return "select distinct b from "+beanName+" as b left join b.vehicleJourneys as v left join b.periods as p where v.journeyPattern.route.line.ptNetwork.name = ? and p.endDate > ?";
      // return "select distinct b from "+beanName+" b left join b.vehicleJourneys v where v.journeyPattern.route.line.ptNetwork.name = ?";
   }

}
