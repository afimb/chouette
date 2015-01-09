package fr.certu.chouette.validation.checkpoint;

import java.util.List;
import java.util.Map;

import lombok.extern.log4j.Log4j;

import org.json.JSONObject;

import fr.certu.chouette.model.neptune.Timetable;
import fr.certu.chouette.plugin.validation.ICheckPointPlugin;
import fr.certu.chouette.plugin.validation.report.CheckPointReportItem;
import fr.certu.chouette.plugin.validation.report.PhaseReportItem;

@Log4j
public class TimetableCheckPoints extends AbstractValidation<Timetable> implements
ICheckPointPlugin<Timetable>
{

   @Override
   public void check(List<Timetable> beans, JSONObject parameters,
         PhaseReportItem report, Map<String,Object> context)
   {
      if (isEmpty(beans))
         return;

      boolean test4_1 = (parameters.optInt(CHECK_OBJECT+OBJECT_KEY.time_table.name(),0) != 0);
      if (test4_1)
      {
         initCheckPoint(report, L4_TIME_TABLE_1, CheckPointReportItem.SEVERITY.ERROR);
         prepareCheckPoint(report, L4_TIME_TABLE_1);
      }
      else // no other tests for this object
      {
         return;
      }
      for (int i = 0; i < beans.size(); i++)
      {
         Timetable bean = beans.get(i);

         // 4-Timetable-1 : check columns constraints
         if (test4_1)
            check4Generic1(report,bean,L4_TIME_TABLE_1,OBJECT_KEY.time_table,parameters,context,log );


      }
   }


}
