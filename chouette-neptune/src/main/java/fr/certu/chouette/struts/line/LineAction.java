package fr.certu.chouette.struts.line;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.struts2.interceptor.validation.SkipValidation;

import com.opensymphony.xwork2.ModelDriven;
import com.opensymphony.xwork2.Preparable;

import fr.certu.chouette.common.ChouetteException;
import fr.certu.chouette.filter.Filter;
import fr.certu.chouette.manager.INeptuneManager;
import fr.certu.chouette.model.neptune.Company;
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

@SuppressWarnings("unchecked")
public class LineAction extends GeneriqueAction implements ModelDriven<Line>, Preparable
{
   private static final long          serialVersionUID = -7602165137555108469L;
   private static final Logger        log              = Logger.getLogger(LineAction.class);
   @Getter
   @Setter
   private Line                       lineModel        = new Line();
   @Getter
   @Setter
   private INeptuneManager<Line>      lineManager;
   @Getter
   @Setter
   private INeptuneManager<Company>   companyManager;
   @Getter
   @Setter
   private INeptuneManager<PTNetwork> networkManager;
   @Getter
   @Setter
   private Long                       idLigne;
   @Getter
   @Setter
   private Long                       idReseau;
   @Getter
   @Setter
   private Long                       idTransporteur;
   @Getter
   private String                     mappedRequest;
   @Getter
   @Setter
   private boolean                    detruireAvecTMs;
   @Getter
   @Setter
   private boolean                    detruireAvecArrets;
   @Getter
   @Setter
   private boolean                    detruireAvecTransporteur;
   @Getter
   @Setter
   private boolean                    detruireAvecReseau;
   @Getter
   @Setter
   private List<PTNetwork>            networks;
   @Getter
   @Setter
   private List<Company>              companies;
   @Getter
   @Setter
   private String                     networkName      = "";
   @Getter
   @Setter
   private String                     companyName      = "";
   @Getter
   @Setter
   private String                     exportMode;
   @Getter
   @Setter
   private File                       temp;
   @Getter
   @Setter
   private String                     nomFichier;
   // Filter
   @Getter
   @Setter
   private Long                       filterNetworkId;
   @Getter
   @Setter
   private Long                       filterCompanyId;
   @Getter
   @Setter
   private String                     filterLineName;

   private boolean                    propagate        = false;
   private User                       user             = null;

   /********************************************************
    * MODEL + PREPARE *
    ********************************************************/
   public Line getModel()
   {
      return lineModel;
   }

   public void prepare() throws Exception
   {
      // log.debug("Prepare with id : " + getIdLigne());
      if (getIdLigne() == null)
      {
         lineModel = new Line();
      }
      else
      {
         // lineModel = ligneManager.lire(getIdLigne());
         Filter filter = Filter.getNewEqualsFilter("id", getIdLigne());
         lineModel = lineManager.get(user, filter);
      }

      networks = new ArrayList<PTNetwork>(networkManager.getAll(null));
      companies = new ArrayList<Company>(companyManager.getAll(null));
   }

   /********************************************************
    * CRUD
    * 
    * @throws ChouetteException
    *            *
    ********************************************************/
   @SkipValidation
   public String list() throws ChouetteException
   {
      // log.debug("List of lines");
      Filter filter = Filter.getNewAndFilter(
            Filter.getNewEqualsFilter(Line.PTNETWORK+"."+PTNetwork.ID, filterNetworkId),
            Filter.getNewEqualsFilter(Line.COMPANY+"."+Company.ID, filterCompanyId), 
            Filter.getNewIgnoreCaseLikeFilter(Line.NAME, filterLineName));
      List<Line> lines = lineManager.getAll(user, filter);
      request.put("lignes", lines);
      return LIST;
   }

   @SkipValidation
   public String add()
   {
      setMappedRequest(SAVE);
      return EDIT;
   }

   public String save() throws ChouetteException
   {
      Line line = getModel();
      if (line == null)
      {
         return INPUT;
      }
      Filter filter = Filter.getNewOrFilter(
            Filter.getNewEqualsFilter(Line.NAME, line.getName()),
            Filter.getNewEqualsFilter(Line.REGISTRATIONNUMBER, line.getRegistrationNumber()));
      if (!lineManager.getAll(null, filter).isEmpty())
      {
         addActionMessage(getText("ligne.homonyme"));
      }
      if (idReseau != -1)
      {
         PTNetwork network = networkManager.getById(idReseau);
         line.setPtNetwork(network);
      }
      else
      {
         line.setPtNetwork(null);
      }
      if (idTransporteur != -1)
      {
         Company company = companyManager.getById(idTransporteur);
         line.setCompany(company);
      }
      else
      {
         line.setCompany(null);
      }
      lineManager.addNew(null, line);
      setMappedRequest(SAVE);
      addActionMessage(getText("ligne.create.ok"));

      return REDIRECTLIST;
   }

   @SkipValidation
   public String edit()
   {
      setMappedRequest(UPDATE);
      return EDIT;
   }

   public String update() throws ChouetteException
   {
      Line line = getModel();
      if (line == null)
      {
         return INPUT;
      }
      Filter filter = Filter.getNewOrFilter(
            Filter.getNewEqualsFilter(Line.NAME, line.getName()),
            Filter.getNewEqualsFilter(Line.REGISTRATIONNUMBER, line.getRegistrationNumber()));
      List<Line> others = lineManager.getAll(null, filter);
      for (Line otherLine : others)
      {
         if (!otherLine.getId().equals(line.getId()))
         {
            addActionMessage(getText("ligne.homonyme"));
            break;
         }
      }
      lineManager.update(null, line);
      setMappedRequest(UPDATE);
      addActionMessage(getText("ligne.update.ok"));
      // log.debug("Update line with id : " + getModel().getId());

      return REDIRECTLIST;
   }

   public String delete() throws ChouetteException
   {
      lineManager.remove(user, getModel(), propagate);

      addActionMessage(getText("ligne.delete.ok"));
      // log.debug("Delete line with id : " + getModel().getId());

      return REDIRECTLIST;
   }

   @SkipValidation
   public String cancel()
   {
      addActionMessage(getText("ligne.cancel.ok"));
      return REDIRECTLIST;
   }

   @Override
   public String input() throws Exception
   {
      return INPUT;
   }

   @SkipValidation
   public String exportChouette() throws Exception
   {
      log.debug("Export NEPTUNE");
      try
      {
         // Creation d'un fichier temporaire
         temp = File.createTempFile("exportChouette", ".xml");
         // Destruction de ce fichier temporaire à la sortie du programme
         temp.deleteOnExit();

         List<ParameterValue> parameters = new ArrayList<ParameterValue>();

         ReportHolder reportHolder = new ReportHolder();
         
         List<Line> lines = new ArrayList<Line>();
         lineManager.completeObject(user, lineModel);
         lines.add(lineModel);
         // Nom du fichier de sortie
         nomFichier = "C_" + exportMode + "_" + lineModel.getRegistrationNumber() + ".xml";
         SimpleParameterValue simpleParameterValue = new SimpleParameterValue("outputFile");
         simpleParameterValue.setFilepathValue(temp.getAbsolutePath());
         parameters.add(simpleParameterValue);

         lineManager.doExport(user, lines, exportMode, parameters, reportHolder);
         if (reportHolder.getReport() != null)
         {
            if (!reportHolder.getReport().getStatus().equals(Report.STATE.OK))
            {
               Report r = reportHolder.getReport();
               log.error(r.getLocalizedMessage());
               logItems("", r.getItems(), Level.ERROR);
               nomFichier = "C_INVALIDE_" + exportMode + "_" + lineModel.getRegistrationNumber() + ".xml";
               PrintStream stream = new PrintStream(temp);
               Report.print(stream, r, true);
            }
         }
      }
      catch (ChouetteException exception)
      {
         log.error("ChouetteException : " + exception.getMessage());
         addActionError(exception.getLocalizedMessage());
         return REDIRECTLIST;
      }
      return EXPORT;
   }

   @SkipValidation
   public String deleteChouette() throws Exception
   {
      try
      {
         List<ParameterValue> parameters = new ArrayList<ParameterValue>();

         List<FormatDescription> formats = lineManager.getDeleteExportFormats(null);
         ReportHolder reportHolder = new ReportHolder();
         String formatDescriptor = formats.get(0).getName();
         // Creation d'un fichier temporaire
         temp = File.createTempFile("exportSupprimerChouette", ".xml");
         // Destruction de ce fichier temporaire à la sortie du programme
         temp.deleteOnExit();
         // Nom du fichier de sortie
         nomFichier = "S_" + exportMode + "_" + lineModel.getRegistrationNumber() + ".xml";

         SimpleParameterValue simpleParameterValue = new SimpleParameterValue("outputFile");
         simpleParameterValue.setFilenameValue(temp.getAbsolutePath());
         parameters.add(simpleParameterValue);

         List<Line> lines = new ArrayList<Line>();
         lines.add(lineModel);
         lineManager.doExportDeleted(null, lines, formatDescriptor, parameters, reportHolder);
         if (reportHolder.getReport() != null)
         {
            Report r = reportHolder.getReport();
            log.error(r.getLocalizedMessage());
            logItems("", r.getItems(), Level.ERROR);

         }

         Filter filter = Filter.getNewEqualsFilter("id", idLigne);
         Line line = lineManager.get(null, filter);
         lineManager.remove(null, line, propagate);
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

   /********************************************************
    * OTHERS METHODS *
    ********************************************************/
   public String getReseau(Long networkId)
   {
      if (networkId != null)
      {
         for (PTNetwork network : networks)
         {
            if (network.getId().equals(networkId))
            {
               networkName = network.getName();
               break;
            }
         }
         return networkName;
      }
      else
      {
         return "";
      }
   }

   public void setReseaux(List<PTNetwork> reseaux)
   {
      this.networks = reseaux;
   }

   public List<PTNetwork> getReseaux()
   {
      return networks;
   }

   public List<Company> getTransporteurs()
   {
      return companies;
   }

   public void setTransporteurs(List<Company> transporteurs)
   {
      this.companies = transporteurs;
   }

   public String getTransporteur(Long companyId)
   {
      if (companyId != null)
      {
         for (Company company : companies)
         {
            if (company.getId().equals(companyId))
            {
               companyName = company.getName();
            }

         }
         return companyName;
      }
      else
      {
         return "";
      }
   }

}
