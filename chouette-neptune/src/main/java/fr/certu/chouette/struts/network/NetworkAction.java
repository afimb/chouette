package fr.certu.chouette.struts.network;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import lombok.Getter;
import lombok.Setter;

import org.apache.commons.io.FileUtils;
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
   @Getter
   @Setter private String             gtfsTimezone;

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


   @SkipValidation
   public String exportChouette() throws Exception
   {
      try
      {

         log.debug("Export " + exportMode + " : toutes les lignes du reseau : " + idReseau);
         if ("GEOPORTAIL".equals(exportMode))
         {
            return exportGeoportail();
         }

         List<FormatDescription> formats = lineManager.getExportFormats(user);
         boolean found = false;
         for (FormatDescription formatDescription : formats)
         {
            if (formatDescription.getName().equals(exportMode))
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
         else
         {
            for (Line line : lignes)
            {
               lineManager.completeObject(user, line);
            }
         }
         String id = "reseau_" + idReseau;
         temp = File.createTempFile("export" + exportMode, ".zip");
         temp.deleteOnExit();
         nomFichier = "C_" + exportMode + "_" + id + ".zip";
         if ("GTFS".equals(exportMode))
         {
            List<ParameterValue> parameters = new ArrayList<ParameterValue>();
            SimpleParameterValue outputFile = new SimpleParameterValue("outputFile");
            outputFile.setFilepathValue(temp.getAbsolutePath());
            parameters.add(outputFile);
            SimpleParameterValue timeZone = new SimpleParameterValue("timeZone");
            timeZone.setStringValue(gtfsTimezone);
            parameters.add(timeZone);

            ReportHolder report = new ReportHolder();
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

   /**
    * @param id
    * @throws ChouetteException
    * @throws IOException
    * @throws FileNotFoundException
    */
   private String  exportGeoportail() throws ChouetteException, IOException, FileNotFoundException
   {
      List<FormatDescription> formats = lineManager.getExportFormats(user);
      boolean found = false;
      for (FormatDescription formatDescription : formats)
      {
         if (formatDescription.getName().equals(exportMode))
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
      PTNetwork network = networkManager.getById(idReseau);
      if (network.getLines() == null || network.getLines().isEmpty())
      {
         addActionMessage(getText("export.network.noline") + " " + network.getName());
         return REDIRECTLIST;
      }

      String id = "reseau_" + idReseau;
      temp = File.createTempFile("exportGEOPORTAIL",".zip");
      temp.deleteOnExit();
      nomFichier = "C_GEOPORTAIL_" + id + ".zip";
      String propertyValue = null;



      List<PTNetwork> networks = new ArrayList<PTNetwork>();
      networks.add(network);
      String reg = network.getRegistrationNumber();
      if (reg == null)
      {
         addActionError(getText("reseau.export.geoportail.noreg"));
         return REDIRECTLIST;
      }
      List<ParameterValue> parameters = new ArrayList<ParameterValue>();
      SimpleParameterValue outputFile = new SimpleParameterValue("outputFile");
      parameters.add(outputFile);
      boolean error = false;
      // logo file
      error |= addFilePathParameter(network, reg, parameters,"logoFile","logoFile");
      // aotURL 
      error |= addStringParameter(network, reg, parameters, "aotURL", "url");
      //      legalInformation (optionnel)
      addStringParameter(network, reg, parameters, "legalInformation", "legal_information");
      //      legalInformationURL (optionnel)
      addStringParameter(network, reg, parameters, "legalInformationURL", "url_legal_information");
      //      aotAddress 
      error |= addStringParameter(network, reg, parameters, "aotAddress", "address");
      //      aotEmail 
      error |= addStringParameter(network, reg, parameters, "aotEmail", "email");
      //      aotPhone 
      error |= addStringParameter(network, reg, parameters, "aotPhone", "telephone");
      //      readMe
      propertyValue = System.getProperty("export.geoportail.readme." + reg);
      if (propertyValue == null)
      {
         error = true;
         addActionError(getText("reseau.export.geoportail.noreadme") + " " + network.getName());
      }
      {
         SimpleParameterValue parameter = new SimpleParameterValue("readMe");
         parameters.add(parameter);
         try
         {
            parameter.setFilepathValue(FileUtils.readFileToString(new File(propertyValue)));
         }
         catch (IOException e)
         {
            log.error("cannot read "+propertyValue,e);
            addActionError(getText("reseau.export.geoportail.errreadme") + " " + network.getName());
            error = true;
         }
      }
      //      stopNote 
      error |= addStringParameter(network, reg, parameters, "stopNote", "usernote1");
      //      accessNote 
      error |= addStringParameter(network, reg, parameters, "accessNote", "usernote2");
      //      quayPicto
      error |= addFilePathParameter(network, reg, parameters, "quayPicto", "pictos.quai");
      //      quayPictoMinScale 
      error |= addIntegerParameter(network, reg, parameters, "quayPictoMinScale", "minscale1");
      //      quayPictoMaxScale 
      error |= addIntegerParameter(network, reg, parameters, "quayPictoMaxScale", "maxscale");
      //      boardingPositionPicto
      error |= addFilePathParameter(network, reg, parameters, "boardingPositionPicto", "pictos.pointembarquement");
      //      boardingPositionPictoMinScale 
      error |= addIntegerParameter(network, reg, parameters, "boardingPositionPictoMinScale", "minscale2");
      //      boardingPositionPictoMaxScale "export.geoportail.maxscale2."+reg
      error |= addIntegerParameter(network, reg, parameters, "boardingPositionPictoMaxScale", "maxscale2");
      //      commercialStopPointPicto
      error |= addFilePathParameter(network, reg, parameters, "commercialStopPointPicto", "pictos.zonecommerciale");
      //      commercialStopPointPictoMinScale "export.geoportail.minscale3."+reg
      error |= addIntegerParameter(network, reg, parameters, "commercialStopPointPictoMinScale", "minscale3");
      //      commercialStopPointPictoMaxScale "export.geoportail.maxscale3."+reg
      error |= addIntegerParameter(network, reg, parameters, "commercialStopPointPictoMaxScale", "maxscale3");
      //      stopPlacePicto
      error |= addFilePathParameter(network, reg, parameters, "stopPlacePicto", "pictos.poleechange");
      //      stopPlacePictoMinScale "export.geoportail.minscale4."+reg
      error |= addIntegerParameter(network, reg, parameters, "stopPlacePictoMinScale", "minscale4");
      //      stopPlacePictoMaxScale "export.geoportail.maxscale4."+reg
      error |= addIntegerParameter(network, reg, parameters, "stopPlacePictoMaxScale", "maxscale4");
      //      accessPointPicto
      error |= addFilePathParameter(network, reg, parameters, "accessPointPicto", "pictos.pointaccess");
      //      accessPointPictoMinScale "export.geoportail.minscale5."+reg
      error |= addIntegerParameter(network, reg, parameters, "accessPointPictoMinScale", "minscale5");
      //      accessPointPictoMaxScale "export.geoportail.maxscale5."+reg
      error |= addIntegerParameter(network, reg, parameters, "accessPointPictoMaxScale", "maxscale5");

      if (error) 
      {
         addActionError(getText("reseau.export.geoportail.ko"));
         return REDIRECTLIST;
      }

      ReportHolder report = new ReportHolder();
      outputFile.setFilepathValue(temp.getAbsolutePath());
      networkManager.doExport(user  , networks, exportMode, parameters, report );
      if (! report.getReport().getStatus().equals(Report.STATE.OK))
      {
         if (temp.exists() )temp.delete();
         nomFichier = "C_INVALIDE_" + exportMode + "_" + id ;
         temp = File.createTempFile(nomFichier, ".txt");
         PrintStream stream = new PrintStream(temp);
         Report.print(stream,report.getReport(),true);
         addActionError(getText("reseau.export.geoportail.ko"));
         return REDIRECTLIST;
      }
      else
      {
         addActionMessage(getText("reseau.export.geoportail.ok"));
      }
      return EXPORT;
      /*******************************************************************************************/
   }

   /**
    * @param network
    * @param reg
    * @param parameters
    */
   private boolean addFilePathParameter(PTNetwork network, String reg, List<ParameterValue> parameters,String parameterName,String propertyName)
   {
      String propertyValue;
      boolean error = false;
      {
         SimpleParameterValue parameter = new SimpleParameterValue(parameterName);
         parameters.add(parameter);
         propertyValue = System.getProperty("export.geoportail."+propertyName+"." + reg);
         if (propertyValue == null)
         {
            error = true;
            addActionError(getText("reseau.export.geoportail.no"+propertyName) + " " + network.getName());
         }
         parameter.setFilepathValue(propertyValue);
      }
      return error;
   }

   /**
    * @param network
    * @param reg
    * @param parameters
    */
   private boolean addStringParameter(PTNetwork network, String reg, List<ParameterValue> parameters,String parameterName,String propertyName)
   {
      String propertyValue;
      boolean error = false;
      {
         propertyValue = System.getProperty("export.geoportail."+propertyName+"." + reg);
         if (propertyValue == null)
         {
            error = true;
            addActionError(getText("reseau.export.geoportail.no"+propertyName) + " " + network.getName());
         }
         else
         {
            SimpleParameterValue parameter = new SimpleParameterValue(parameterName);
            parameters.add(parameter);
            parameter.setStringValue(propertyValue);
         }
      }
      return error;
   }
   /**
    * @param network
    * @param reg
    * @param parameters
    */
   private boolean addIntegerParameter(PTNetwork network, String reg, List<ParameterValue> parameters,String parameterName,String propertyName)
   {
      String propertyValue;
      boolean error = false;
      {
         SimpleParameterValue parameter = new SimpleParameterValue(parameterName);
         parameters.add(parameter);
         propertyValue = System.getProperty("export.geoportail."+propertyName+"." + reg);
         if (propertyValue == null)
         {
            error = true;
            addActionError(getText("reseau.export.geoportail.no"+propertyName) + " " + network.getName());
         }
         parameter.setIntegerValue(Long.parseLong(propertyValue));
      }
      return error;
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
