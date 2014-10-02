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
import fr.certu.chouette.model.neptune.JourneyPattern;

/**
 * @author michel
 * 
 */
public class JourneyPatternDaoTemplateTests extends
      AbstractDaoTemplateTests<JourneyPattern>
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
      initDaoTemplate("JourneyPattern", "journeyPatternDao");
   }

   /*
    * (non-Javadoc)
    * 
    * @see
    * fr.certu.chouette.dao.hibernate.AbstractDaoTemplateTests#refreshBean()
    */
   @Override
   public void refreshBean()
   {
      bean = createJourneyPattern();
   }

   @Override
   protected List<FilterData> getSelectFilters()
   {
      List<FilterData> ret = new ArrayList<FilterData>();
      ret.add(new FilterData("JourneyPattern : route.creatorId", Filter
            .getNewEqualsFilter("route.creatorId", "TESTNG"), 1));
      return ret;
   }

}
