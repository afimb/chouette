package fr.certu.chouette.struts.network;

import chouette.schema.ChouettePTNetworkTypeType;
import fr.certu.chouette.struts.GeneriqueAction;
import fr.certu.chouette.modele.Reseau;
import fr.certu.chouette.service.database.IReseauManager;
import com.opensymphony.xwork2.ModelDriven;
import com.opensymphony.xwork2.Preparable;
import fr.certu.chouette.modele.Ligne;
import fr.certu.chouette.service.commun.ServiceException;
import fr.certu.chouette.service.database.IExportManager;
import fr.certu.chouette.service.database.IExportManager.ExportMode;
import fr.certu.chouette.service.validation.commun.TypeInvalidite;
import fr.certu.chouette.service.validation.commun.ValidationException;
import fr.certu.chouette.service.validation.util.MainSchemaProducer;
import fr.certu.chouette.service.xml.ILecteurFichierXML;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.List;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts2.interceptor.validation.SkipValidation;

public class NetworkAction extends GeneriqueAction implements ModelDriven<Reseau>, Preparable
{

  private static final Log log = LogFactory.getLog(NetworkAction.class);
  private Reseau model = new Reseau();
  private IReseauManager reseauManager;
  private Long idReseau;
  private String mappedRequest;
  private ExportMode exportMode;
  private String nomFichier;
  private File temp;
  private IExportManager exportManager;
  private ILecteurFichierXML lecteurFichierXML;

  public Long getIdReseau()
  {
    return idReseau;
  }

  public void setIdReseau(Long idReseau)
  {
    this.idReseau = idReseau;
  }

  /********************************************************
   *                  MODEL + PREPARE                     *
   ********************************************************/
  public Reseau getModel()
  {
    return model;
  }

  public void prepare() throws Exception
  {
    log.debug("Prepare with id : " + getIdReseau());
    if (getIdReseau() == null)
    {
      model = new Reseau();
    }
    else
    {
      model = reseauManager.lire(getIdReseau());
    }
  }

  /********************************************************
   *                           CRUD                       *
   ********************************************************/
  @SkipValidation
  public String list()
  {
    this.request.put("reseaux", reseauManager.lire());
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
    reseauManager.creer(model);
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
    reseauManager.modifier(model);
    setMappedRequest(UPDATE);
    addActionMessage(getText("reseau.update.ok"));
    log.debug("Update network with id : " + model.getId());
    return REDIRECTLIST;
  }

  public String delete()
  {
    reseauManager.supprimer(model.getId());
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
      log.debug("Export Chouette : toutes les lignes du reseau : " + idReseau);
      List<Ligne> lignes = reseauManager.getLignesReseau(idReseau);
      if ((lignes == null) || (lignes.size() == 0))
      {
        addActionMessage(getText("export.network.noline"));
        return REDIRECTLIST;
      }
      String id = "reseau_" + idReseau;
      temp = File.createTempFile("exportChouette", ".zip");
      temp.deleteOnExit();
      ZipOutputStream zipOutputStream = new ZipOutputStream(new FileOutputStream(temp));
      zipOutputStream.setLevel(ZipOutputStream.DEFLATED);
      nomFichier = "C_" + exportMode + "_" + id + ".zip";
      for (Ligne ligne : lignes)
      {
        ChouettePTNetworkTypeType ligneLue = exportManager.getExportParIdLigne(ligne.getId());
        try
        {
          MainSchemaProducer mainSchemaProducer = new MainSchemaProducer();
          mainSchemaProducer.getASG(ligneLue);
        }
        catch (ValidationException e)
        {
          List<TypeInvalidite> categories = e.getCategories();
          if (categories != null)
          {
            for (TypeInvalidite category : categories)
            {
              Set<String> messages = e.getTridentIds(category);
              for (String message : messages)
              {
                log.error(message);
              }
            }
          }
          String _nomFichier = "C_INVALIDE_" + exportMode + "_" + id + "_" + ligne.getId();
          File _temp = File.createTempFile(_nomFichier, ".xml");
          _temp.deleteOnExit();
          lecteurFichierXML.ecrire(ligneLue, _temp);
          zipOutputStream.putNextEntry(new ZipEntry(_nomFichier + ".xml"));
          byte[] bytes = new byte[(int) _temp.length()];
          FileInputStream fis = new FileInputStream(_temp);
          fis.read(bytes);
          zipOutputStream.write(bytes);
          zipOutputStream.flush();
          continue;
        }
        String _nomFichier = "C_" + exportMode + "_" + id + "_" + ligne.getId();
        File _temp = File.createTempFile(_nomFichier, ".xml");
        _temp.deleteOnExit();
        lecteurFichierXML.ecrire(ligneLue, _temp);
        zipOutputStream.putNextEntry(new ZipEntry(_nomFichier + ".xml"));
        byte[] bytes = new byte[(int) _temp.length()];
        FileInputStream fis = new FileInputStream(_temp);
        fis.read(bytes);
        zipOutputStream.write(bytes);
        zipOutputStream.flush();
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
   *                        MANAGER                       *
   ********************************************************/
  public void setReseauManager(IReseauManager reseauManager)
  {
    this.reseauManager = reseauManager;
  }

  public void setExportManager(IExportManager exportManager)
  {
    this.exportManager = exportManager;
  }

  public void setLecteurFichierXML(ILecteurFichierXML lecteurFichierXML)
  {
    this.lecteurFichierXML = lecteurFichierXML;
  }

  /********************************************************
   *                   METHOD ACTION                      *
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

  /********************************************************
   *                   EXPORT MODE                        *
   ********************************************************/
  public ExportMode getExportMode()
  {
    return exportMode;
  }

  public void setExportMode(ExportMode exportMode)
  {
    this.exportMode = exportMode;
  }

  public InputStream getInputStream() throws Exception
  {
    return new FileInputStream(temp.getPath());
  }

  public String getNomFichier()
  {
    return nomFichier;
  }
}
