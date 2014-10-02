/**
 * Projet CHOUETTE
 *
 * ce projet est sous license libre
 * voir LICENSE.txt pour plus de details
 *
 */

package fr.certu.chouette.plugin.validation.report;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import fr.certu.chouette.plugin.model.CompilanceCheckResult;
import fr.certu.chouette.plugin.report.ReportItem;

/**
 * @author michel
 * 
 */
public class PhaseReportItem extends ReportItem
{
   public enum PHASE
   {
      ZERO, ONE, TWO, THREE
   };

   private final PHASE phase;

   /**
	 * 
	 */
   public PhaseReportItem(PHASE phase)
   {
      this.phase = phase;
      setOrder(phase.ordinal());
      setMessageKey(phase.name());
      updateStatus(STATE.OK);
   }

   /*
    * (non-Javadoc)
    * 
    * @see
    * fr.certu.chouette.plugin.report.Report#addItem(fr.certu.chouette.plugin
    * .report.ReportItem)
    */
   @Override
   public void addItem(ReportItem item)
   {
      if (item instanceof CheckPointReportItem)
      {
         super.addItem(item);
         updateStatus(item.getStatus());
      } else
      {
         throw new IllegalArgumentException(
               "item must be of CheckPointReportItem type");
      }
   }

   public CheckPointReportItem getItem(String key)
   {
      for (ReportItem item : getItems())
      {
         if (item.getMessageKey().equals(key))
         {
            return (CheckPointReportItem) item;
         }
      }
      return null;
   }

   public void sortItems()
   {
      List<ReportItem> items = getItems();
      Collections.sort(items);
   }

   public PHASE getPhase()
   {
      return phase;
   }

   public List<CompilanceCheckResult> toValidationResults()
   {
      List<CompilanceCheckResult> list = new ArrayList<CompilanceCheckResult>();
      for (ReportItem item : getItems())
      {
         list.add(((CheckPointReportItem) item).toValidationResult());
      }
      return list;
   }

}
