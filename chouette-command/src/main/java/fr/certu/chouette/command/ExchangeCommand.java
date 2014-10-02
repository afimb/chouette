/**
 * Projet CHOUETTE
 *
 * ce projet est sous license libre
 * voir LICENSE.txt pour plus de details
 *
 */
package fr.certu.chouette.command;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.Charset;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j;

import org.apache.commons.io.FileUtils;
import org.json.JSONObject;

import fr.certu.chouette.common.ChouetteException;
import fr.certu.chouette.manager.INeptuneManager;
import fr.certu.chouette.model.neptune.NeptuneIdentifiedObject;
import fr.certu.chouette.plugin.exchange.FormatDescription;
import fr.certu.chouette.plugin.exchange.ListParameterValue;
import fr.certu.chouette.plugin.exchange.ParameterDescription;
import fr.certu.chouette.plugin.exchange.ParameterValue;
import fr.certu.chouette.plugin.exchange.SimpleParameterValue;
import fr.certu.chouette.plugin.report.Report;
import fr.certu.chouette.plugin.report.Report.STATE;
import fr.certu.chouette.plugin.report.ReportHolder;
import fr.certu.chouette.plugin.report.ReportItem;
import fr.certu.chouette.plugin.validation.report.PhaseReportItem;

/**
 *
 */
/**
 * @author mamadou
 * 
 */
@Log4j
@NoArgsConstructor
public class ExchangeCommand extends AbstractCommand
{

   /**
    * @param beans
    * @param manager
    * @param parameters
    */
   @SuppressWarnings("incomplete-switch")
   public void executeExport(List<NeptuneIdentifiedObject> beans,
         INeptuneManager<NeptuneIdentifiedObject> manager,
         Map<String, List<String>> parameters)
   {
      String format = getSimpleString(parameters, "format");
      try
      {
         List<FormatDescription> formats = manager.getExportFormats(null);
         FormatDescription description = null;

         for (FormatDescription formatDescription : formats)
         {
            if (formatDescription.getName().equalsIgnoreCase(format))
            {
               description = formatDescription;
               break;
            }
         }
         if (description == null)
         {
            throw new IllegalArgumentException("format " + format
                  + " unavailable, check command getExportFormats for list ");
         }

         List<ParameterValue> values = new ArrayList<ParameterValue>();
         for (ParameterDescription desc : description
               .getParameterDescriptions())
         {
            String name = desc.getName();
            String key = name.toLowerCase();
            List<String> vals = parameters.get(key);
            if (vals == null)
            {
               if (desc.isMandatory())
               {
                  throw new IllegalArgumentException(
                        "parameter -"
                              + name
                              + " is required, check command getExportFormats for list ");
               }
            } else
            {
               if (desc.isCollection())
               {
                  ListParameterValue val = new ListParameterValue(name);
                  switch (desc.getType())
                  {
                  case FILEPATH:
                     val.setFilepathList(vals);
                     break;
                  case STRING:
                     val.setStringList(vals);
                     break;
                  case FILENAME:
                     val.setFilenameList(vals);
                     break;
                  default:
                     throw new IllegalArgumentException("parameter -" + name
                           + " is invalid ");
                  }
                  values.add(val);
               } else
               {
                  if (vals.size() != 1)
                  {
                     throw new IllegalArgumentException(
                           "parameter -"
                                 + name
                                 + " must be unique, check command getExportFormats for list ");
                  }
                  String simpleval = vals.get(0);

                  SimpleParameterValue val = new SimpleParameterValue(name);
                  switch (desc.getType())
                  {
                  case FILEPATH:
                     val.setFilepathValue(simpleval);
                     break;
                  case STRING:
                     val.setStringValue(simpleval);
                     break;
                  case FILENAME:
                     val.setFilenameValue(simpleval);
                     break;
                  case BOOLEAN:
                     val.setBooleanValue(Boolean.parseBoolean(simpleval));
                     break;
                  case INTEGER:
                     val.setIntegerValue(Long.parseLong(simpleval));
                     break;
                  case DATE:
                     val.setDateValue(toCalendar(simpleval));
                     break;
                  }
                  values.add(val);
               }
            }
         }

         ReportHolder holder = new ReportHolder();
         manager.doExport(null, beans, format, values, holder);
         PrintStream stream = System.out;
         if (holder.getReport() != null)
         {
            Report r = holder.getReport();
            stream.println(r.getLocalizedMessage());
            printItems(stream, "", r.getItems());
         }
      } catch (ChouetteException e)
      {
         log.error(e.getMessage());

         Throwable caused = e.getCause();
         while (caused != null)
         {
            log.error("caused by " + caused.getMessage());
            caused = caused.getCause();
         }
         throw new RuntimeException("export failed, see details in log");
      }
   }

   /**
    * convert date string to calendar
    * 
    * @param simpleval
    * @return
    */
   private Calendar toCalendar(String simpleval)
   {
      SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
      try
      {
         Date d = sdf.parse(simpleval);
         Calendar c = Calendar.getInstance();
         c.setTime(d);
         return c;
      } catch (ParseException e)
      {
         log.error("invalid date format : " + simpleval
               + " dd/MM/yyyy expected");
         throw new RuntimeException("invalid date format : " + simpleval
               + " dd/MM/yyyy expected");
      }

   }

   /**
    * @param beans
    * @param manager
    * @param parameters
    */
   public void executeExportDeletion(List<NeptuneIdentifiedObject> beans,
         INeptuneManager<NeptuneIdentifiedObject> manager,
         Map<String, List<String>> parameters)
   {
      String format = getSimpleString(parameters, "format");
      try
      {
         List<FormatDescription> formats = manager.getDeleteExportFormats(null);
         FormatDescription description = null;

         for (FormatDescription formatDescription : formats)
         {
            if (formatDescription.getName().equalsIgnoreCase(format))
            {
               description = formatDescription;
               break;
            }
         }
         if (description == null)
         {
            throw new IllegalArgumentException(
                  "format "
                        + format
                        + " unavailable, check command getDeletionExportFormats for list ");
         }

         List<ParameterValue> values = new ArrayList<ParameterValue>();
         for (ParameterDescription desc : description
               .getParameterDescriptions())
         {
            String name = desc.getName();
            String key = name.toLowerCase();
            List<String> vals = parameters.get(key);
            if (vals == null)
            {
               if (desc.isMandatory())
               {
                  throw new IllegalArgumentException(
                        "parameter -"
                              + name
                              + " is required, check command getDeletionExportFormats for list ");
               }
            } else
            {
               if (desc.isCollection())
               {
                  ListParameterValue val = new ListParameterValue(name);
                  switch (desc.getType())
                  {
                  case FILEPATH:
                     val.setFilepathList(vals);
                     break;
                  case STRING:
                     val.setStringList(vals);
                     break;
                  case FILENAME:
                     val.setFilenameList(vals);
                     break;
                  default:
                     throw new IllegalArgumentException(
                           "parameter -"
                                 + name
                                 + " invalid, check command getDeletionExportFormats for list ");
                  }
                  values.add(val);
               } else
               {
                  if (vals.size() != 1)
                  {
                     throw new IllegalArgumentException(
                           "parameter -"
                                 + name
                                 + " must be unique, check command getDeletionExportFormats for list ");
                  }
                  String simpleval = vals.get(0);

                  SimpleParameterValue val = new SimpleParameterValue(name);
                  switch (desc.getType())
                  {
                  case FILEPATH:
                     val.setFilepathValue(simpleval);
                     break;
                  case STRING:
                     val.setStringValue(simpleval);
                     break;
                  case FILENAME:
                     val.setFilenameValue(simpleval);
                     break;
                  case BOOLEAN:
                     val.setBooleanValue(Boolean.parseBoolean(simpleval));
                     break;
                  case INTEGER:
                     val.setIntegerValue(Long.parseLong(simpleval));
                     break;
                  default:
                     throw new IllegalArgumentException(
                           "parameter -"
                                 + name
                                 + " invalid, check command getDeletionExportFormats for list ");
                  }
                  values.add(val);
               }
            }
         }

         ReportHolder holder = new ReportHolder();
         manager.doExportDeleted(null, beans, format, values, holder);
         if (holder.getReport() != null)
         {
            Report r = holder.getReport();
            System.out.println(r.getLocalizedMessage());
            printItems(System.out, "", r.getItems());
         }
      } catch (ChouetteException e)
      {
         log.error(e.getMessage());

         Throwable caused = e.getCause();
         while (caused != null)
         {
            log.error("caused by " + caused.getMessage());
            caused = caused.getCause();
         }
         throw new RuntimeException("export failed, see details in log");
      }
   }

   /**
    * @param manager
    * @param parameters
    * @throws ChouetteException
    */
   public void executeGetExportFormats(
         INeptuneManager<NeptuneIdentifiedObject> manager,
         Map<String, List<String>> parameters) throws ChouetteException
   {

      List<FormatDescription> formats = manager.getExportFormats(null);
      for (FormatDescription formatDescription : formats)
      {
         System.out.println(formatDescription.toString(locale));
      }

   }

   /**
    * @param manager
    * @param parameters
    * @return
    */
   @SuppressWarnings("incomplete-switch")
   public List<NeptuneIdentifiedObject> executeImport(
         INeptuneManager<NeptuneIdentifiedObject> manager,
         Map<String, List<String>> parameters)
   {
      String reportFileName = getSimpleString(parameters, "reportfile", "");
      String reportFormat = getSimpleString(parameters, "reportformat", "txt");
      boolean append = getBoolean(parameters, "append");
      String format = getSimpleString(parameters, "format");
      PrintStream stream = System.out;
      String encoding = Charset.defaultCharset().toString();
      if (!reportFileName.isEmpty())
      {
         try
         {
            if (reportFormat.equals("json"))
            {
               encoding = "UTF-8";
            }
            stream = new PrintStream(new FileOutputStream(new File(
                  reportFileName), append), true, encoding);
         } catch (IOException e)
         {
            System.err.println("cannot open file :" + reportFileName + " "
                  + e.getMessage());
            reportFileName = "";
         }
      }
      try
      {
         List<FormatDescription> formats = manager.getImportFormats(null);
         FormatDescription description = null;

         for (FormatDescription formatDescription : formats)
         {
            if (formatDescription.getName().equalsIgnoreCase(format))
            {
               description = formatDescription;
               break;
            }
         }
         if (description == null)
         {
            throw new IllegalArgumentException("format " + format
                  + " unavailable, check command getImportFormats for list ");
         }

         List<ParameterValue> values = new ArrayList<ParameterValue>();
         for (ParameterDescription desc : description
               .getParameterDescriptions())
         {
            String name = desc.getName();
            String key = name.toLowerCase();
            List<String> vals = parameters.get(key);
            if (vals == null)
            {
               if (desc.isMandatory())
               {
                  throw new IllegalArgumentException(
                        "parameter -"
                              + name
                              + " is required, check command getImportFormats for list ");
               }
            } else
            {
               if (desc.isCollection())
               {
                  ListParameterValue val = new ListParameterValue(name);
                  switch (desc.getType())
                  {
                  case FILEPATH:
                     val.setFilepathList(vals);
                     break;
                  case STRING:
                     val.setStringList(vals);
                     break;
                  case FILENAME:
                     val.setFilenameList(vals);
                     break;
                  default:
                     throw new IllegalArgumentException(
                           "parameter -"
                                 + name
                                 + " invalid, check command getImportFormats for list ");
                  }
                  values.add(val);
               } else
               {
                  if (vals.size() != 1)
                  {
                     throw new IllegalArgumentException(
                           "parameter -"
                                 + name
                                 + " must be unique, check command getImportFormats for list ");
                  }
                  String simpleval = vals.get(0);

                  SimpleParameterValue val = new SimpleParameterValue(name);
                  switch (desc.getType())
                  {
                  case FILEPATH:
                     val.setFilepathValue(simpleval);
                     break;
                  case STRING:
                     val.setStringValue(simpleval);
                     break;
                  case FILENAME:
                     val.setFilenameValue(simpleval);
                     break;
                  case BOOLEAN:
                     val.setBooleanValue(Boolean.parseBoolean(simpleval));
                     break;
                  case INTEGER:
                     val.setIntegerValue(Long.parseLong(simpleval));
                     break;
                  case DATE:
                     val.setDateValue(toCalendar(simpleval));
                     break;
                  }
                  values.add(val);
               }
            }
         }

         ReportHolder ireport = new ReportHolder();
         ReportHolder vreport = new ReportHolder();
         List<NeptuneIdentifiedObject> beans = manager.doImport(null, format,
               values, ireport, vreport);
         if (ireport.getReport() != null)
         {
            Report r = ireport.getReport();
            if (reportFormat.equals("json"))
            {
               stream.println(r.toJSON());
            } else
            {
               stream.println(r.getLocalizedMessage());
               printItems(stream, "", r.getItems());
            }

         }
         if (vreport.getReport() != null)
         {
            Report r = vreport.getReport();
            if (reportFormat.equals("json"))
            {
               stream.println(r.toJSON());
            } else
            {
               stream.println(r.getLocalizedMessage());
               printItems(stream, "", r.getItems());
            }

         }
         if (beans == null || beans.isEmpty())
         {
            System.out.println("import failed");
         }

         else
         {
            System.out.println("beans count = " + beans.size());
         }
         return beans;

      } catch (ChouetteException e)
      {
         log.error(e.getMessage());

         Throwable caused = e.getCause();
         while (caused != null)
         {
            log.error("caused by " + caused.getMessage());
            caused = caused.getCause();
         }
         throw new RuntimeException("import failed , see log for details");
      } finally
      {
         if (!reportFileName.isEmpty())
         {
            stream.close();
         }
      }

   }

   /**
    * @param beans
    * @param manager
    * @param parameters
    * @throws ChouetteException
    */
   public void executeValidate(List<NeptuneIdentifiedObject> beans,
         INeptuneManager<NeptuneIdentifiedObject> manager,
         Map<String, List<String>> parameters) throws ChouetteException
   {
      String fileName = getSimpleString(parameters, "file", "");
      boolean append = getBoolean(parameters, "append");
      JSONObject validationParameters;

      try
      {
         String json = FileUtils
               .readFileToString(new File("parameterset.json"));
         validationParameters = new JSONObject(json);
      } catch (IOException e1)
      {
         System.err.println("cannot open file :parameterset.json");
         e1.printStackTrace();
         return;
      }

      PhaseReportItem valReport = new PhaseReportItem(
            PhaseReportItem.PHASE.THREE);
      manager.validate(null, beans, validationParameters, valReport);
      PrintStream stream = System.out;
      if (!fileName.isEmpty())
      {
         try
         {
            stream = new PrintStream(new FileOutputStream(new File(fileName),
                  append));
         } catch (FileNotFoundException e)
         {
            System.err.println("cannot open file :" + fileName);
            fileName = "";
         }
      }

      stream.println(valReport.getLocalizedMessage());
      printItems(stream, "", valReport.getItems());
      int nbUNCHECK = 0;
      int nbOK = 0;
      int nbWARN = 0;
      int nbERROR = 0;
      int nbFATAL = 0;
      for (ReportItem item1 : valReport.getItems()) // Categorie
      {
         if (item1.getItems() != null)
            for (ReportItem item2 : item1.getItems()) // fiche
            {
               STATE status = item2.getStatus();
               switch (status)
               {
               case UNCHECK:
                  nbUNCHECK++;
                  break;
               case OK:
                  nbOK++;
                  break;
               case WARNING:
                  nbWARN++;
                  break;
               case ERROR:
                  nbERROR++;
                  break;
               case FATAL:
                  nbFATAL++;
                  break;
               }

            }
      }
      stream.println("Bilan : " + nbOK + " tests ok, " + nbWARN + " warnings, "
            + nbERROR + " erreurs, " + nbUNCHECK + " non effectués, " + nbFATAL
            + " fatals");
      if (!fileName.isEmpty())
      {
         stream.close();
      }
   }

   private void printItems(PrintStream stream, String indent,
         List<ReportItem> items)
   {
      if (items == null)
         return;
      for (ReportItem item : items)
      {
         stream.println(indent + item.getStatus().name() + " : "
               + item.getLocalizedMessage());
         printItems(stream, indent + "   ", item.getItems());
      }

   }

   public void executeGetImportFormats(
         INeptuneManager<NeptuneIdentifiedObject> manager,
         Map<String, List<String>> parameters) throws ChouetteException
   {

      List<FormatDescription> formats = manager.getImportFormats(null);
      for (FormatDescription formatDescription : formats)
      {
         System.out.println(formatDescription.toString(locale));
      }

   }

   public void executeGetDeletionFormats(
         INeptuneManager<NeptuneIdentifiedObject> manager,
         Map<String, List<String>> parameters) throws ChouetteException
   {

      List<FormatDescription> formats = manager.getDeleteExportFormats(null);
      for (FormatDescription formatDescription : formats)
      {
         System.out.println(formatDescription.toString(locale));
      }

   }

}
