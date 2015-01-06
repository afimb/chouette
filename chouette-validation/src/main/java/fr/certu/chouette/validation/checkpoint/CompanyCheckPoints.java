package fr.certu.chouette.validation.checkpoint;

import java.util.List;
import java.util.Map;

import lombok.extern.log4j.Log4j;

import org.json.JSONObject;

import fr.certu.chouette.model.neptune.Company;
import fr.certu.chouette.plugin.validation.ICheckPointPlugin;
import fr.certu.chouette.plugin.validation.report.CheckPointReportItem;
import fr.certu.chouette.plugin.validation.report.PhaseReportItem;

@Log4j
public class CompanyCheckPoints extends AbstractValidation<Company> implements
ICheckPointPlugin<Company>
{

   @Override
   public void check(List<Company> beans, JSONObject parameters,
         PhaseReportItem report, Map<String,Object> context)
   {
      if (isEmpty(beans))
         return;

      boolean test4_1 = (parameters.optInt(CHECK_OBJECT+OBJECT_KEY.company.name(),0) != 0);
      if (test4_1)
      {
         initCheckPoint(report, L4_COMPANY_1, CheckPointReportItem.SEVERITY.ERROR);
         prepareCheckPoint(report, L4_COMPANY_1);
      }
      else // no other tests for this object
      {
         return;
      }


      for (int i = 0; i < beans.size(); i++)
      {
         Company bean = beans.get(i);

         // 4-Company-1 : check columns constraints
         if (test4_1)
            check4Generic1(report,bean,L4_COMPANY_1,OBJECT_KEY.company,parameters,context,log );


      }
   }


}
