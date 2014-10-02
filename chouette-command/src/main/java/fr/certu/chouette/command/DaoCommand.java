/**
 * Projet CHOUETTE
 *
 * ce projet est sous license libre
 * voir LICENSE.txt pour plus de details
 *
 */
package fr.certu.chouette.command;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import lombok.NoArgsConstructor;
import fr.certu.chouette.common.ChouetteException;
import fr.certu.chouette.filter.Filter;
import fr.certu.chouette.filter.FilterOrder;
import fr.certu.chouette.manager.INeptuneManager;
import fr.certu.chouette.model.neptune.NeptuneIdentifiedObject;

/**
 *
 */
/**
 * @author mamadou
 * 
 */
@NoArgsConstructor
public class DaoCommand extends AbstractCommand
{

   /**
    * @param manager
    * @param parameters
    * @return
    * @throws ChouetteException
    */
   public List<NeptuneIdentifiedObject> executeGet(
         INeptuneManager<NeptuneIdentifiedObject> manager,
         Map<String, List<String>> parameters) throws ChouetteException
   {
      Command.flushDao();
      Filter filter = null;
      if (parameters.containsKey("id"))
      {
         List<String> sids = parameters.get("id");
         List<Long> ids = new ArrayList<Long>();

         for (String id : sids)
         {
            ids.add(Long.valueOf(id));
         }
         filter = Filter.getNewInFilter("id", ids);
      } else if (parameters.containsKey("objectid"))
      {
         List<String> sids = parameters.get("objectid");
         filter = Filter.getNewInFilter("objectId", sids);
      } else if (parameters.containsKey("filter"))
      {
         List<String> filterArgs = parameters.get("filter");
         if (filterArgs.size() < 2)
         {
            throw new IllegalArgumentException("invalid syntax for filter ");
         }
         String filterKey = filterArgs.get(0);
         String filterOp = filterArgs.get(1);
         if (filterArgs.size() == 2)
         {
            if (filterOp.equalsIgnoreCase("null")
                  || filterOp.equalsIgnoreCase("isnull"))
            {
               filter = Filter.getNewIsNullFilter(filterKey);
            } else
            {
               throw new IllegalArgumentException(filterOp
                     + " : invalid syntax or not yet implemented");
            }
         } else if (filterArgs.size() == 3)
         {
            String value = filterArgs.get(2);
            if (filterOp.equalsIgnoreCase("eq") || filterOp.equals("="))
            {
               filter = Filter.getNewEqualsFilter(filterKey, value);
            } else if (filterOp.equalsIgnoreCase("like"))
            {
               filter = Filter.getNewLikeFilter(filterKey, value);
            } else
            {
               throw new IllegalArgumentException(filterOp
                     + " : invalid syntax or not yet implemented");
            }
         } else if (filterArgs.size() == 4)
         {
            throw new IllegalArgumentException(filterOp
                  + " : invalid syntax or not yet implemented");
         } else
         {
            if (filterOp.equalsIgnoreCase("in"))
            {
               List<String> values = filterArgs.subList(2, filterArgs.size());
               filter = Filter.getNewInFilter(filterKey, values);
            } else
            {
               throw new IllegalArgumentException(filterOp
                     + " : invalid syntax or not yet implemented");
            }
         }
      } else
      {
         filter = Filter.getNewEmptyFilter();
      }

      if (parameters.containsKey("orderby"))
      {
         List<String> orderFields = parameters.get("orderby");

         boolean desc = getBoolean(parameters, "desc");

         if (desc)
         {
            for (String field : orderFields)
            {
               filter.addOrder(FilterOrder.desc(field));
            }
         } else
         {
            for (String field : orderFields)
            {
               filter.addOrder(FilterOrder.asc(field));
            }
         }
      }

      String limit = getSimpleString(parameters, "limit", "10");
      if (limit.equalsIgnoreCase("none"))
      {
         filter.addLimit(Integer.parseInt(limit));
      }

      List<NeptuneIdentifiedObject> beans = manager.getAll(null, filter);

      if (verbose)
      {
         int count = 0;
         for (NeptuneIdentifiedObject bean : beans)
         {
            if (count > 10)
            {
               System.out.println(" ... ");
               break;
            }
            count++;
            System.out.println(bean.getName() + " : ObjectId = "
                  + bean.getObjectId());
         }
      }
      System.out.println("beans count = " + beans.size());
      return beans;
   }

   /**
    * @param beans
    * @param manager
    * @param parameters
    * @throws ChouetteException
    */
   public void executeSave(List<NeptuneIdentifiedObject> beans,
         INeptuneManager<NeptuneIdentifiedObject> manager,
         Map<String, List<String>> parameters) throws ChouetteException
   {

      boolean mass = getBoolean(parameters, "mass");
      boolean propagate = getBoolean(parameters, "propagate");
      boolean slow = getBoolean(parameters, "slow");
      if (mass)
      {
         // boolean propagate = getBoolean(parameters, "propagate");
         // boolean slow = getBoolean(parameters, "slow");
         manager.saveAll(null, beans, propagate, !slow);
      } else
      {
         for (NeptuneIdentifiedObject bean : beans)
         {
            List<NeptuneIdentifiedObject> oneBean = new ArrayList<NeptuneIdentifiedObject>();
            oneBean.add(bean);
            manager.saveAll(null, oneBean, propagate, !slow);
         }
      }

   }

   /**
    * @param beans
    * @param manager
    * @param parameters
    * @throws ChouetteException
    */
   public void executeDelete(List<NeptuneIdentifiedObject> beans,
         INeptuneManager<NeptuneIdentifiedObject> manager,
         Map<String, List<String>> parameters) throws ChouetteException
   {
      boolean propagate = getBoolean(parameters, "propagate");
      /*
       * for (NeptuneIdentifiedObject bean : beans) { Filter filter =
       * Filter.getNewEqualsFilter("id", bean.getId()); manager.removeAll(null,
       * filter); }
       */

      manager.removeAll(null, beans, propagate);
      beans.clear();
   }

}
