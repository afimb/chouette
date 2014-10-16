/**
 * Projet CHOUETTE
 *
 * ce projet est sous license libre
 * voir LICENSE.txt pour plus de details
 *
 */
package fr.certu.chouette.gui.command;

// import java.io.BufferedReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.EntityManager;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import org.apache.log4j.Logger;
import org.json.JSONObject;

import fr.certu.chouette.common.ChouetteException;
import fr.certu.chouette.dao.IDaoTemplate;
import fr.certu.chouette.filter.Filter;
import fr.certu.chouette.manager.INeptuneManager;
import fr.certu.chouette.model.neptune.Company;
import fr.certu.chouette.model.neptune.GroupOfLine;
import fr.certu.chouette.model.neptune.NeptuneIdentifiedObject;
import fr.certu.chouette.model.neptune.PTNetwork;
import fr.certu.chouette.plugin.model.CompilanceCheckTask;
import fr.certu.chouette.plugin.model.Referential;
import fr.certu.chouette.plugin.validation.report.PhaseReportItem;

/**
 * 
 * validate command : -c validate -id validationId
 * 
 */
@NoArgsConstructor
public class ValidateCommand extends AbstractCommand
{
   private static final Logger logger = Logger.getLogger(ValidateCommand.class);

   @Getter
   @Setter
   private IDaoTemplate<Referential> referentialDao;;

   @Getter
   @Setter
   private IDaoTemplate<CompilanceCheckTask> validationDao;

   private void startProcess(EntityManager session,
         CompilanceCheckTask compilanceCheckTask)
   {
      compilanceCheckTask.setStatus("processing");
      validationDao.save(compilanceCheckTask);
      validationDao.flush();

   }

   /**
    * validate command from database : -c validation -id ZZZ
    * 
    * @param parameters
    * @return
    * @throws ChouetteException
    */
   public int executeValidate(EntityManager session,
         Map<String, List<String>> parameters) throws ChouetteException
   {
      INeptuneManager<NeptuneIdentifiedObject> manager = managers.get("line");
      Long validationId = Long.valueOf(getSimpleString(parameters, "id"));
      if (!validationDao.exists(validationId))
      {
         // error validation not found
         logger.error("compilanceCheckTask not found " + validationId);
         return 1;
      }
      CompilanceCheckTask compilanceCheckTask = validationDao.get(validationId);
      startProcess(session, compilanceCheckTask);

      // read parameters
      JSONObject validationParameters = compilanceCheckTask.getParameters();

      // read object type
      String objectType = extractObjectType(compilanceCheckTask
            .getReferencesType().toLowerCase());

      List<NeptuneIdentifiedObject> beans = new ArrayList<NeptuneIdentifiedObject>();
      String idstring = compilanceCheckTask.getReferenceIds();
      if (idstring == null || idstring.isEmpty())
      {
         beans = manager.getAll(null);
      } else
      {
         String[] ids = idstring.split(",");
         List<Long> checkIds = new ArrayList<>();
         for (String id : ids)
         {
            checkIds.add(Long.valueOf(id));
         }

         if (!objectType.startsWith("line"))
         {
            INeptuneManager<NeptuneIdentifiedObject> loadManager = managers
                  .get(objectType);
            if (loadManager == null)
            {
               logger.error("object type " + objectType + " not found "
                     + validationId);
               return 1;
            }
            Filter filter = Filter.getNewInFilter("id", checkIds);
            List<NeptuneIdentifiedObject> containerBeans = loadManager.getAll(
                  null, filter);
            Set<NeptuneIdentifiedObject> beanSet = new HashSet<NeptuneIdentifiedObject>();
            for (NeptuneIdentifiedObject container : containerBeans)
            {
               if (objectType.equals("network"))
               {
                  PTNetwork network = (PTNetwork) container;
                  beanSet.addAll(network.getLines());
               } else if (objectType.equals("company"))
               {
                  Company company = (Company) container;
                  beanSet.addAll(company.getLines());
               } else if (objectType.equals("groupofline"))
               {
                  GroupOfLine group = (GroupOfLine) container;
                  beanSet.addAll(group.getLines());
               } else
               {
                  logger.error("object type " + objectType + " not managed "
                        + validationId);
               }
            }
            beans.addAll(beanSet);
         } else
         {
            Filter filter = Filter.getNewInFilter("id", checkIds);
            beans = manager.getAll(null, filter);

         }
      }

      PhaseReportItem valReport = new PhaseReportItem(
            PhaseReportItem.PHASE.THREE);
      if (beans != null && !beans.isEmpty())
      {
         manager.validate(null, beans, validationParameters, valReport, null, true);
      }

      // save report
      if (valReport != null)
      {
         saveValidationReport(session, compilanceCheckTask, valReport);
      }

      return 0;
   }

   private String extractObjectType(String type)
   {
      // type shall begin by chouette::
      if (type.startsWith("chouette::"))
         return type.substring(10);

      return type;
   }

   /**
    * @param string
    * @return
    */
   private String getSimpleString(Map<String, List<String>> parameters,
         String key)
   {
      List<String> values = parameters.get(key);
      if (values == null)
         throw new IllegalArgumentException("parameter -" + key
               + " of String type is required");
      if (values.size() > 1)
         throw new IllegalArgumentException("parameter -" + key
               + " of String type must be unique");
      return values.get(0);
   }

   private void saveValidationReport(EntityManager session,
         CompilanceCheckTask compilanceCheckTask, PhaseReportItem vreport)
   {
      if (vreport != null && vreport.getItems() != null)
      {
         switch (vreport.getStatus())
         {
         case WARNING:
         case ERROR:
         case FATAL:
            compilanceCheckTask.setStatus("nok");
            break;
         case OK:
            compilanceCheckTask.setStatus("ok");
            break;
         case UNCHECK:
            compilanceCheckTask.setStatus("na");
            break;
         }
         compilanceCheckTask.addAllResults(vreport.toValidationResults());
         validationDao.save(compilanceCheckTask);
         validationDao.flush();
      }

   }

}
