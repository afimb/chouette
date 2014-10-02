package fr.certu.chouette.validation;

import java.util.ArrayList;
import java.util.List;

import fr.certu.chouette.common.ChouetteException;
import fr.certu.chouette.model.neptune.Line;
import fr.certu.chouette.plugin.exchange.IImportPlugin;
import fr.certu.chouette.plugin.exchange.ParameterValue;
import fr.certu.chouette.plugin.exchange.SimpleParameterValue;
import fr.certu.chouette.plugin.report.ReportHolder;

public class LineLoader
{
   public static List<Line> load(IImportPlugin<Line> importLine, String fileName)
         throws ChouetteException
   {
      List<ParameterValue> parameters = new ArrayList<ParameterValue>();
      {
         SimpleParameterValue simpleParameterValue = new SimpleParameterValue(
               "inputFile");
         simpleParameterValue.setFilepathValue(fileName);
         parameters.add(simpleParameterValue);
      }
      {
         SimpleParameterValue simpleParameterValue = new SimpleParameterValue(
               "optimizeMemory");
         simpleParameterValue.setBooleanValue(false);
         parameters.add(simpleParameterValue);
      }

      ReportHolder ireport = new ReportHolder();
      ReportHolder vreport = new ReportHolder();

      List<Line> lines = importLine.doImport(parameters, ireport, vreport);

      if (lines == null || lines.isEmpty())
      {
         AbstractValidation.printReport(ireport.getReport());
         AbstractValidation.printReport(vreport.getReport());
         lines = new ArrayList<Line>();
      }
      return lines;
   }
}
