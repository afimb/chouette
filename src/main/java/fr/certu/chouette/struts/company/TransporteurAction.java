package fr.certu.chouette.struts.company;

import com.opensymphony.xwork2.ModelDriven;
import com.opensymphony.xwork2.Preparable;
import fr.certu.chouette.struts.GeneriqueAction;
import fr.certu.chouette.modele.Transporteur;
import fr.certu.chouette.service.database.ITransporteurManager;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts2.interceptor.validation.SkipValidation;

public class TransporteurAction extends GeneriqueAction implements ModelDriven, Preparable
{
  private static final Log log = LogFactory.getLog(TransporteurAction.class);
  private Transporteur companyModel = new Transporteur();
  private static ITransporteurManager transporteurManager;
  private Long idTransporteur;
  private String mappedRequest;

  public Transporteur getModel()
  {
    return companyModel;
  }

  public Long getIdTransporteur()
  {
    return idTransporteur;
  }

  public void setIdTransporteur(Long idTransporteur)
  {
    this.idTransporteur = idTransporteur;
  }

  public void prepare() throws Exception
  {
    log.debug("Prepare with id : " + getIdTransporteur());
    if (getIdTransporteur() == null)
    {
      companyModel = new Transporteur();
    }
    else
    {
      companyModel = transporteurManager.lire(getIdTransporteur());
    }
  }

  @SkipValidation
  public String list()
  {
    this.request.put("transporteurs", transporteurManager.lire());
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
    transporteurManager.creer(getModel());
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
    transporteurManager.modifier(getModel());
    setMappedRequest(UPDATE);
    addActionMessage(getText("transporteur.update.ok"));
    log.debug("Update company with id : " + getModel().getId());
    return REDIRECTLIST;
  }

  public String delete()
  {
    transporteurManager.supprimer(getModel().getId());
    addActionMessage(getText("transporteur.delete.ok"));
    log.debug("Delete company with id : " + getModel().getId());
    return REDIRECTLIST;
  }

  public void setTransporteurManager(ITransporteurManager transporteurManager)
  {
    TransporteurAction.transporteurManager = transporteurManager;
  }

  @SkipValidation
  public String cancel()
  {
    addActionMessage(getText("transporteur.cancel.ok"));
    return REDIRECTLIST;
  }

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
