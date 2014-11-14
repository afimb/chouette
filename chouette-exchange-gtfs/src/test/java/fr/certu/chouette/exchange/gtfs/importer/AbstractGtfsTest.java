package fr.certu.chouette.exchange.gtfs.importer;

import java.util.List;

import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.Reporter;

import fr.certu.chouette.plugin.report.Report;
import fr.certu.chouette.plugin.report.ReportItem;

public abstract class AbstractGtfsTest extends AbstractTestNGSpringContextTests
{

   protected final void printReport(Report report)
   {
      if (report == null)
      {
         Reporter.log("no report");
      } else
      {
         Reporter.log(report.getStatus().name() + " : "
               + report.getLocalizedMessage());
         printItems("   ", report.getItems());
      }
   }

   /**
    * @param indent
    * @param items
    */
   protected final void printItems(String indent, List<ReportItem> items)
   {
      if (items == null)
         return;
      for (ReportItem item : items)
      {
         Reporter.log(indent + item.getStatus().name() + " : "
               + item.getLocalizedMessage());
         printItems(indent + "   ", item.getItems());
      }

   }

}
