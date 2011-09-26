package fr.certu.chouette.struts.network;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import lombok.Getter;
import lombok.Setter;

import org.apache.log4j.Logger;
import org.apache.struts2.interceptor.validation.SkipValidation;

import com.opensymphony.xwork2.ModelDriven;
import com.opensymphony.xwork2.Preparable;

import fr.certu.chouette.common.ChouetteException;
import fr.certu.chouette.filter.Filter;
import fr.certu.chouette.manager.INeptuneManager;
import fr.certu.chouette.model.neptune.Line;
import fr.certu.chouette.model.neptune.PTNetwork;
import fr.certu.chouette.model.user.User;
import fr.certu.chouette.plugin.exchange.FormatDescription;
import fr.certu.chouette.plugin.exchange.ParameterValue;
import fr.certu.chouette.plugin.exchange.SimpleParameterValue;
import fr.certu.chouette.plugin.report.Report;
import fr.certu.chouette.plugin.report.ReportHolder;
import fr.certu.chouette.struts.GeneriqueAction;
import fr.certu.chouette.struts.exception.ServiceException;

public class NetworkAction extends GeneriqueAction implements ModelDriven<PTNetwork>, Preparable
{

   private static final long          serialVersionUID = 5577647506346473140L;
   private static final Logger        log              = Logger.getLogger(NetworkAction.class);
   @Getter
   private PTNetwork                  model            = new PTNetwork();
   @Getter
   @Setter
   private INeptuneManager<PTNetwork> networkManager;
   @Getter
   @Setter
   private INeptuneManager<Line>      lineManager;
   @Getter
   @Setter
   private Long                       idReseau;
   private String                     mappedRequest;
   @Getter
   @Setter
   private String                     exportMode;
   @Getter
   private String                     nomFichier;
   private File                       temp;
   @Getter
   @Setter
   private String                     useGtfs;
   @Getter
   @Setter
   private String                     useGeoportail;
   private User user = null;


   /********************************************************
    * MODEL + PREPARE *
    ********************************************************/

   public void prepare() throws Exception
   {
      log.debug("Prepare with id : " + getIdReseau());
      if (getIdReseau() == null)
      {
         model = new PTNetwork();
      }
      else
      {
         Filter filter = Filter.getNewEqualsFilter("id", getIdReseau());
         model = networkManager.get(null, filter);
      }
   }

   /********************************************************
    * CRUD
    * 
    * @throws ChouetteException
    *            *
    ********************************************************/
   @SuppressWarnings("unchecked")
   @SkipValidation
   public String list() throws ChouetteException
   {
      this.request.put("reseaux", networkManager.getAll(null));
      log.debug("List of networks");
      return LIST;
   }

   @SkipValidation
   public String add()
   {
      setMappedRequest(SAVE);
      return EDIT;
   }

   public String save()
   {
      try
      {
         networkManager.addNew(null, model);
      }
      catch (Exception e)
      {
         addActionMessage(getText("reseau.homonyme"));
         return INPUT;
      }
      setMappedRequest(SAVE);
      addActionMessage(getText("reseau.create.ok"));
      log.debug("Create network with id : " + model.getId());
      return REDIRECTLIST;
   }

   @SkipValidation
   public String edit()
   {
      setMappedRequest(UPDATE);
      return EDIT;
   }

   public String update()
   {
      try
      {
         networkManager.update(null, model);
      }
      catch (Exception e)
      {
         addActionMessage(getText("reseau.homonyme"));
         return INPUT;
      }
      setMappedRequest(UPDATE);
      addActionMessage(getText("reseau.update.ok"));
      log.debug("Update network with id : " + model.getId());
      return REDIRECTLIST;
   }

   public String delete() throws ChouetteException
   {
      networkManager.remove(null, model, false);
      addActionMessage(getText("reseau.delete.ok"));
      log.debug("Delete network with id : " + model.getId());
      return REDIRECTLIST;
   }

   @SkipValidation
   public String cancel()
   {
      addActionMessage(getText("reseau.cancel.ok"));
      return REDIRECTLIST;
   }

   @Override
   @SkipValidation
   public String input() throws Exception
   {
      return INPUT;
   }

//   private void write(List<ILectureEchange> lecturesEchanges, String _nomFichier, ZipOutputStream zipOutputStream,
//         int type) throws IOException
//   {
//      String extenstion = null;
//      if (type == GTFS)
//      {
//         extenstion = ".txt";
//      }
//      else if (type == GEOPORTAIL)
//      {
//         extenstion = ".csv";
//      }
//      File _temp = File.createTempFile(_nomFichier, extenstion);
//      _temp.deleteOnExit();
//      if (type == GTFS)
//      {
//         gtfsFileWriter.write(lecturesEchanges, _temp, _nomFichier);
//      }
//      else if (type == GEOPORTAIL)
//      {
//         geoportailFileWriter.write(lecturesEchanges, _temp, _nomFichier);
//      }
//      zipOutputStream.putNextEntry(new ZipEntry(_nomFichier + extenstion));
//      byte[] bytes = new byte[(int) _temp.length()];
//      FileInputStream fis = new FileInputStream(_temp);
//      fis.read(bytes);
//      zipOutputStream.write(bytes);
//      zipOutputStream.flush();
//   }

   @SkipValidation
   public String exportChouette() throws Exception
   {
      try
      {
         String exportModeStr = exportMode.toString();
         log.debug("Export " + exportModeStr + " : toutes les lignes du reseau : " + idReseau);
         List<FormatDescription> formats = lineManager.getExportFormats(user);
         boolean found = false;
         for (FormatDescription formatDescription : formats)
         {
            if (formatDescription.getName().equals(exportModeStr))
            {
               found = true;
               break;
            }
         }
         if (!found)
         {
            // unknown format; send error
            return REDIRECTLIST;
         }
         Filter filter = Filter.getNewEqualsFilter("ptNetwork.id", idReseau);
         List<Line> lignes = lineManager.getAll(null, filter);
         if ((lignes == null) || (lignes.size() == 0))
         {
            addActionMessage(getText("export.network.noline"));
            return REDIRECTLIST;
         }
         String id = "reseau_" + idReseau;
         temp = File.createTempFile("export" + exportModeStr, ".zip");
         temp.deleteOnExit();
         nomFichier = "C_" + exportModeStr + "_" + id + ".zip";
         if ("GEOPORTAIL".equals(exportModeStr))
         {
            List<ParameterValue> parameters = new ArrayList<ParameterValue>();
            SimpleParameterValue outputFile = new SimpleParameterValue("outputFile");
            parameters.add(outputFile);
            
            ReportHolder report = new ReportHolder();
            outputFile.setFilepathValue(temp.getAbsolutePath());
            lineManager.doExport(user  , lignes, exportMode, parameters, report );
            if (! report.getReport().getStatus().equals(Report.STATE.OK))
            {
               if (temp.exists() )temp.delete();
               nomFichier = "C_INVALIDE_" + exportMode + "_" + id ;
               temp = File.createTempFile(nomFichier, ".txt");
               PrintStream stream = new PrintStream(temp);
               Report.print(stream,report.getReport(),true);

            }
//            List<ILectureEchange> lecturesEchanges = new ArrayList<ILectureEchange>();
//            for (Line ligne : lignes)
//            {
//               lecturesEchanges.add(lecteurEchangeXML.lire(exportManager.getExportParIdLigne(ligne.getId())));
//            }
//            write(lecturesEchanges, "aot", zipOutputStream, GEOPORTAIL);
//            write(lecturesEchanges, "chouette_metadata", zipOutputStream, GEOPORTAIL);
//            write(lecturesEchanges, "pictos", zipOutputStream, GEOPORTAIL);
//            write(lecturesEchanges, "tc_points", zipOutputStream, GEOPORTAIL);
//            /*******************************************************************************************/
//            Set<String> regs = new HashSet<String>();
//            for (ILectureEchange lectureEchange : lecturesEchanges)
//            {
//               Reseau reseau = lectureEchange.getReseau();
//               if (reseau == null)
//               {
//                  continue;
//               }
//               String reg = reseau.getRegistrationNumber();
//               if (!regs.add(reg))
//               {
//                  continue;
//               }
//               String _tempName = System.getProperty("export.geoportail.readme." + reg);
//               File _temp = null;
//               if (_tempName != null)
//               {
//                  _temp = new File(_tempName);
//                  if (_temp.exists())
//                  {
//                     zipOutputStream.putNextEntry(new ZipEntry("Readme.txt"));
//                     byte[] bytes = new byte[(int) _temp.length()];
//                     FileInputStream fis = new FileInputStream(_temp);
//                     fis.read(bytes);
//                     zipOutputStream.write(bytes);
//                     zipOutputStream.flush();
//                  }
//               }
//               else
//               {
//                  addActionError(getText("reseau.export.geoportail.noreadme") + " " + reseau.getName());
//               }
//               _tempName = System.getProperty("export.geoportail.logoFile." + reg);
//               _temp = null;
//               if (_tempName != null)
//               {
//                  _temp = new File(_tempName);
//                  if (_temp.exists())
//                  {
//                     zipOutputStream.putNextEntry(new ZipEntry("Logos" + File.separator + _temp.getName()));
//                     byte[] bytes = new byte[(int) _temp.length()];
//                     FileInputStream fis = new FileInputStream(_temp);
//                     fis.read(bytes);
//                     zipOutputStream.write(bytes);
//                     zipOutputStream.flush();
//                  }
//               }
//               else
//               {
//                  addActionError(getText("reseau.export.geoportail.nologo") + " " + reseau.getName());
//               }
//               _tempName = System.getProperty("export.geoportail.pictos.pointaccess." + reg);
//               _temp = null;
//               if (_tempName != null)
//               {
//                  _temp = new File(_tempName);
//                  if (_temp.exists())
//                  {
//                     zipOutputStream.putNextEntry(new ZipEntry("Pictos" + File.separator + _temp.getName()));
//                     byte[] bytes = new byte[(int) _temp.length()];
//                     FileInputStream fis = new FileInputStream(_temp);
//                     fis.read(bytes);
//                     zipOutputStream.write(bytes);
//                     zipOutputStream.flush();
//                  }
//               }
//               else
//               {
//                  addActionError(getText("reseau.export.geoportail.noptaccess") + " " + reseau.getName());
//               }
//               _tempName = System.getProperty("export.geoportail.pictos.pointembarquement." + reg);
//               _temp = null;
//               if (_tempName != null)
//               {
//                  _temp = new File(_tempName);
//                  if (_temp.exists())
//                  {
//                     zipOutputStream.putNextEntry(new ZipEntry("Pictos" + File.separator + _temp.getName()));
//                     byte[] bytes = new byte[(int) _temp.length()];
//                     FileInputStream fis = new FileInputStream(_temp);
//                     fis.read(bytes);
//                     zipOutputStream.write(bytes);
//                     zipOutputStream.flush();
//                  }
//               }
//               else
//               {
//                  addActionError(getText("reseau.export.geoportail.noboarding") + " " + reseau.getName());
//               }
//               _temp = null;
//               _tempName = System.getProperty("export.geoportail.pictos.poleechange." + reg);
//               if (_tempName != null)
//               {
//                  _temp = new File(_tempName);
//                  if (_temp.exists())
//                  {
//                     zipOutputStream.putNextEntry(new ZipEntry("Pictos" + File.separator + _temp.getName()));
//                     byte[] bytes = new byte[(int) _temp.length()];
//                     FileInputStream fis = new FileInputStream(_temp);
//                     fis.read(bytes);
//                     zipOutputStream.write(bytes);
//                     zipOutputStream.flush();
//                  }
//               }
//               else
//               {
//                  addActionError(getText("reseau.export.geoportail.noplace") + " " + reseau.getName());
//               }
//               _temp = null;
//               _tempName = System.getProperty("export.geoportail.pictos.quai." + reg);
//               if (_tempName != null)
//               {
//                  _temp = new File(_tempName);
//                  if (_temp.exists())
//                  {
//                     zipOutputStream.putNextEntry(new ZipEntry("Pictos" + File.separator + _temp.getName()));
//                     byte[] bytes = new byte[(int) _temp.length()];
//                     FileInputStream fis = new FileInputStream(_temp);
//                     fis.read(bytes);
//                     zipOutputStream.write(bytes);
//                     zipOutputStream.flush();
//                  }
//               }
//               else
//               {
//                  addActionError(getText("reseau.export.geoportail.noquay") + " " + reseau.getName());
//               }
//               _temp = null;
//               _tempName = System.getProperty("export.geoportail.pictos.zonecommerciale." + reg);
//               if (_tempName != null)
//               {
//                  _temp = new File(_tempName);
//                  if (_temp.exists())
//                  {
//                     zipOutputStream.putNextEntry(new ZipEntry("Pictos" + File.separator + _temp.getName()));
//                     byte[] bytes = new byte[(int) _temp.length()];
//                     FileInputStream fis = new FileInputStream(_temp);
//                     fis.read(bytes);
//                     zipOutputStream.write(bytes);
//                     zipOutputStream.flush();
//                  }
//               }
//               else
//               {
//                  addActionError(getText("reseau.export.geoportail.nocommercial") + " " + reseau.getName());
//               }
//            }
            addActionMessage(getText("reseau.export.geoportail.ok"));
            /*******************************************************************************************/
         }
         else if ("GTFS".equals(exportModeStr))
         {
            List<ParameterValue> parameters = new ArrayList<ParameterValue>();
            SimpleParameterValue outputFile = new SimpleParameterValue("outputFile");
            parameters.add(outputFile);
            
            ReportHolder report = new ReportHolder();
            outputFile.setFilepathValue(temp.getAbsolutePath());
            lineManager.doExport(user  , lignes, exportMode, parameters, report );
            if (! report.getReport().getStatus().equals(Report.STATE.OK))
            {
               if (temp.exists() )temp.delete();
               nomFichier = "C_INVALIDE_" + exportMode + "_" + id ;
               temp = File.createTempFile(nomFichier, ".txt");
               PrintStream stream = new PrintStream(temp);
               Report.print(stream,report.getReport(),true);

            }
            
            addActionMessage(getText("reseau.export.gtfs.ok"));
         }
         else
         {
          ZipOutputStream zipOutputStream = new ZipOutputStream(new FileOutputStream(temp));
          zipOutputStream.setLevel(ZipOutputStream.DEFLATED);
            List<ParameterValue> parameters = new ArrayList<ParameterValue>();
            SimpleParameterValue outputFile = new SimpleParameterValue("outputFile");
            parameters.add(outputFile);
            for (Line ligne : lignes)
            {
               List<Line> beans = new ArrayList<Line>();
               beans.add(ligne);
               ReportHolder report = new ReportHolder();
               String _nomFichier = "C_" + exportMode + "_" + id + "_" + ligne.getId();
               File _temp = File.createTempFile(_nomFichier, ".xml");
               outputFile.setFilepathValue(_temp.getAbsolutePath());
               
               lineManager.doExport(user  , beans, exportMode, parameters, report );
               if (! report.getReport().getStatus().equals(Report.STATE.OK))
               {
                  if (_temp.exists() )_temp.delete();
                  _nomFichier = "C_INVALIDE_" + exportMode + "_" + id + "_" + ligne.getId();
                  _temp = File.createTempFile(_nomFichier, ".xml");
                  PrintStream stream = new PrintStream(_temp);
                  Report.print(stream,report.getReport(),true);

               }
               zipOutputStream.putNextEntry(new ZipEntry(_nomFichier + ".xml"));
               byte[] bytes = new byte[(int) _temp.length()];
               FileInputStream fis = new FileInputStream(_temp);
               fis.read(bytes);
               zipOutputStream.write(bytes);
               zipOutputStream.flush();
               _temp.delete();
            }
            zipOutputStream.close();
         }
      }
      catch (ServiceException exception)
      {
         log.debug("ServiceException : " + exception.getMessage());
         addActionError(getText(exception.getCode().name()));
         return REDIRECTLIST;
      }
      return EXPORT;
   }


   /********************************************************
    * METHOD ACTION *
    ********************************************************/
   // this prepares command for button on initial screen write
   public void setMappedRequest(String actionMethod)
   {
      this.mappedRequest = actionMethod;
   }

   // when invalid, the request parameter will restore command action
   public void setActionMethod(String method)
   {
      this.mappedRequest = method;
   }

   public String getActionMethod()
   {
      return mappedRequest;
   }

   public InputStream getInputStream() throws Exception
   {
      return new FileInputStream(temp.getPath());
   }

}
