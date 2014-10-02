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

import fr.certu.chouette.dao.hibernate.AbstractDaoTemplateTests.FilterData;
import fr.certu.chouette.filter.Filter;
import fr.certu.chouette.model.neptune.Line;

/**
 * @author michel
 * 
 */
public class LineDaoTemplateTests extends AbstractDaoTemplateTests<Line>
{

   /*
    * (non-Javadoc)
    * 
    * @see
    * fr.certu.chouette.dao.hibernate.AbstractDaoTemplateTests#createDaoTemplate
    * ()
    */
   @Override
   @BeforeMethod(alwaysRun = true)
   public void createDaoTemplate()
   {
      initDaoTemplate("Line", "lineDao");
   }

   @Override
   public void refreshBean()
   {
      bean = createLine();
   }

   @Override
   protected List<FilterData> getSelectFilters()
   {
      List<FilterData> ret = new ArrayList<FilterData>();
      List<String> args = new ArrayList<>();
      args.add("TestNG Network");
      ret.add(new FilterData("Line : ptNetwork.name in", Filter.getNewInFilter(
            "ptNetwork.name", args), 1));
      return ret;
   }

   /*
    * (non-Javadoc)
    * 
    * @see
    * fr.certu.chouette.dao.hibernate.AbstractDaoTemplateTests#getHqlValues()
    */
   @Override
   protected List<Object> getHqlValues()
   {
      List<Object> values = new ArrayList<Object>();
      values.add("A");
      return values;
   }

   /*
    * (non-Javadoc)
    * 
    * @see
    * fr.certu.chouette.dao.hibernate.AbstractDaoTemplateTests#getHQLFilter()
    */
   @Override
   protected String getHQLFilter()
   {
      return "select distinct b from " + beanName
            + " b left join b.routes r where r.wayBack = ?";
   }

}
