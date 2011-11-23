package fr.certu.chouette.struts.company;

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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts2.interceptor.validation.SkipValidation;

import com.opensymphony.xwork2.ModelDriven;
import com.opensymphony.xwork2.Preparable;

import fr.certu.chouette.common.ChouetteException;
import fr.certu.chouette.filter.Filter;
import fr.certu.chouette.manager.INeptuneManager;
import fr.certu.chouette.model.neptune.Company;
import fr.certu.chouette.model.neptune.Line;
import fr.certu.chouette.model.user.User;
import fr.certu.chouette.plugin.exchange.FormatDescription;
import fr.certu.chouette.plugin.exchange.ParameterValue;
import fr.certu.chouette.plugin.exchange.SimpleParameterValue;
import fr.certu.chouette.plugin.report.Report;
import fr.certu.chouette.plugin.report.ReportHolder;
import fr.certu.chouette.struts.GeneriqueAction;
import fr.certu.chouette.struts.exception.ServiceException;

public class CompanyAction extends GeneriqueAction implements ModelDriven<Company>, Preparable
{

   private static final long        serialVersionUID = -4424720354695864931L;

   private static final Log         log              = LogFactory.getLog(CompanyAction.class);
   private Company                  companyModel     = new Company();
   @Getter
   @Setter
   private INeptuneManager<Company> companyManager;
   @Getter
   @Setter
   INeptuneManager<Line>            lineManager;
   @Getter
   @Setter
   private Long                     idTransporteur;
   private String                   mappedRequest;
   @Getter
   @Setter
   private String                   exportMode;
   private File                     temp;
   @Getter private String                   nomFichier;

   private User user = null;


   /********************************************************
    * MODEL + PREPARE *
    ********************************************************/
   public Company getModel()
   {
      return companyModel;
   }

   public void prepare() throws Exception
   {
      log.debug("Prepare with id : " + getIdTransporteur());
      if (getIdTransporteur() == null)
      {
         companyModel = new Company();
      }
      else
      {
         Filter filter = Filter.getNewEqualsFilter("id", getIdTransporteur());
         companyModel = companyManager.get(null, filter);
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
      this.request.put("transporteurs", companyManager.getAll(user));
      log.debug("List of companies");
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
         companyManager.addNew(user, getModel());
      }
      catch (Exception e)
      {
         addActionMessage(getText("transporteur.homonyme"));
         return INPUT;
      }
      setMappedRequest(SAVE);
      addActionMessage(getText("transporteur.create.ok"));
      log.debug("Create company with id : " + getModel().getId());
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
         companyManager.update(user, getModel());
      }
      catch (Exception e)
      {
         addActionMessage(getText("transporteur.homonyme"));
         return INPUT;
      }
      setMappedRequest(UPDATE);
      addActionMessage(getText("transporteur.update.ok"));
      log.debug("Update company with id : " + getModel().getId());
      return REDIRECTLIST;
   }

   public String delete() throws ChouetteException
   {
      companyManager.remove(user, getModel(), false);
      addActionMessage(getText("transporteur.delete.ok"));
      log.debug("Delete company with id : " + getModel().getId());
      return REDIRECTLIST;
   }

   @SkipValidation
   public String cancel()
   {
      addActionMessage(getText("transporteur.cancel.ok"));
      return REDIRECTLIST;
   }

   @SkipValidation
   public String exportChouette() throws Exception
   {
      try
      {
         log.debug("Export Chouette : toutes les lignes du transporteur : " + idTransporteur);

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
         
         Filter filter = Filter.getNewEqualsFilter("company.id", idTransporteur);
         List<Line> lignes = lineManager.getAll(user, filter);
         if ((lignes == null) || (lignes.size() == 0))
         {
            addActionMessage(getText("export.company.noline"));
            return REDIRECTLIST;
         }
         else
         {
            for (Line line : lignes)
            {
               lineManager.completeObject(user, line);
            }
         }
         String id = "transporteur_" + idTransporteur;
         temp = File.createTempFile("exportChouette", ".zip");
         temp.deleteOnExit();
         ZipOutputStream zipOutputStream = new ZipOutputStream(new FileOutputStream(temp));
         zipOutputStream.setLevel(ZipOutputStream.DEFLATED);
         nomFichier = "C_" + exportMode + "_" + id + ".zip";
         
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
            
            lineManager.doExport(user , beans, exportMode, parameters, report );
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
            fis.close();
            _temp.delete();
         }
         zipOutputStream.close();
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
