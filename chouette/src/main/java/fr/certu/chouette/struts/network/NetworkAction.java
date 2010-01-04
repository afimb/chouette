package fr.certu.chouette.struts.network;

import fr.certu.chouette.struts.GeneriqueAction;
import fr.certu.chouette.modele.Reseau;
import fr.certu.chouette.service.database.IReseauManager;
import com.opensymphony.xwork2.ModelDriven;
import com.opensymphony.xwork2.Preparable;
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

  /********************************************************
   *                        MANAGER                       *
   ********************************************************/
  public void setReseauManager(IReseauManager reseauManager)
  {
    this.reseauManager = reseauManager;
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
}
