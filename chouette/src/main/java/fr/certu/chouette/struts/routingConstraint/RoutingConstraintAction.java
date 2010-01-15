package fr.certu.chouette.struts.routingConstraint;

import com.opensymphony.xwork2.ModelDriven;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts2.interceptor.validation.SkipValidation;

import com.opensymphony.xwork2.Preparable;

import fr.certu.chouette.modele.InterdictionTraficLocal;
import fr.certu.chouette.modele.Ligne;
import fr.certu.chouette.modele.PositionGeographique;
import fr.certu.chouette.service.database.IITLManager;
import fr.certu.chouette.service.database.ILigneManager;
import fr.certu.chouette.service.database.IPositionGeographiqueManager;
import fr.certu.chouette.struts.GeneriqueAction;

public class RoutingConstraintAction extends GeneriqueAction implements ModelDriven<InterdictionTraficLocal>, Preparable
{

  private final Log log = LogFactory.getLog(RoutingConstraintAction.class);
  private IITLManager itlManager;
  private ILigneManager ligneManager;
  private IPositionGeographiqueManager positionGeographiqueManager;
  private Long idItl;
  private Long idAreaStop;
  private List<PositionGeographique> arrets;
  private List<PositionGeographique> arretsDansITLList;
  private String saisieNomArretExistant;
  private String saisieNomArretExistantKey;
  private String mappedRequest;
  private String name;
  private InterdictionTraficLocal model = new InterdictionTraficLocal();

  public Long getIdItl()
  {
    return idItl;
  }

  public void setIdItl(Long idItl)
  {
    this.idItl = idItl;
  }

  public String getName()
  {
    return name;
  }

  public void setName(String name)
  {
    this.name = name;
  }

  public Long getIdAreaStop()
  {
    return idAreaStop;
  }

  public void setIdAreaStop(Long idAreaStop)
  {
    this.idAreaStop = idAreaStop;
  }

  public String getChaineIdLigne()
  {
    return model.getIdLigne().toString();
  }

  /********************************************************
   *                  MODEL + PREPARE                     *
   ********************************************************/
  @SkipValidation
  public void prepare() throws Exception
  {
    log.debug("Prepare with id : " + getIdItl());
    if (getIdItl() == null)
    {
      model = new InterdictionTraficLocal();
    }
    else
    {
      model = itlManager.lire(getIdItl());
    }
  }

  public InterdictionTraficLocal getModel()
  {
    return model;
  }

  /********************************************************
   *                           CRUD                       *
   ********************************************************/
  @SkipValidation
  public String cancel()
  {
    addActionMessage(getText("itl.cancel.ok"));
    return REDIRECTLIST;
  }

  @SkipValidation
  public String delete()
  {
    itlManager.supprimer(model.getId());
    addActionMessage(getText("itl.delete.ok"));
    log.debug("Delete itl with id : " + model.getId());
    return REDIRECTLIST;
  }

  @SkipValidation
  public String list()
  {
    this.request.put("itls", itlManager.lire());
    log.debug("List of itls");
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
      itlManager.creer(model);
      addActionMessage(getText("itl.create.ok"));
    }
    catch (Exception exception)
    {
      addActionError(getText("itl.create.ko"));
    }
    setMappedRequest(UPDATE);
    // Update itl id to update itl
    setIdItl(model.getId());
    log.debug("Create itl with id : " + model.getId());
    return REDIRECTEDIT;
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
      itlManager.modifier(model);
      addActionMessage(getText("itl.update.ok"));
    }
    catch (Exception exception)
    {
      addActionError(getText("itl.update.ko"));
    }
    setMappedRequest(UPDATE);
    log.debug("Update itl with id : " + model.getId());
    return REDIRECTEDIT;
  }

  @SkipValidation
  public String addStop()
  {
    if (saisieNomArretExistantKey != null && !saisieNomArretExistantKey.isEmpty())
    {
      List<Long> l = model.getArretPhysiqueIds();
      l.add(Long.valueOf(saisieNomArretExistantKey));
      model.setArretPhysiqueIds(l);
      try
      {
        itlManager.modifier(model);
        addActionMessage(getText("itl.update.ok"));
      }
      catch (Exception ex)
      {
        addActionError(getText("itl.update.ko"));
      }
    }
    return REDIRECTEDIT;
  }

  @SkipValidation
  public String removeStop()
  {
    if (idAreaStop != null)
    {
      List<Long> l = this.model.getArretPhysiqueIds();
      int idx = l.indexOf(Long.valueOf(idAreaStop));
      if (idx != -1)
      {
        l.remove(idx);
        try
        {
          itlManager.modifier(model);
          addActionMessage(getText("itl.update.ok"));
        }
        catch (Exception ex)
        {
          addActionError(getText("itl.update.ko"));
        }
      }
    }
    return REDIRECTEDIT;
  }

  /********************************************************
   *                        MANAGER                       *
   ********************************************************/
  public void setItlManager(IITLManager itlManager)
  {
    this.itlManager = itlManager;
  }

  public void setLigneManager(ILigneManager ligneManager)
  {
    this.ligneManager = ligneManager;
  }

  public void setPositionGeographiqueManager(
          IPositionGeographiqueManager positionGeographiqueManager)
  {
    this.positionGeographiqueManager = positionGeographiqueManager;
  }

  /********************************************************
   *                        JSON                          *
   ********************************************************/
  public String getJsonArrets(Long idLigne)
  {
    String resultat = "";

    List<PositionGeographique> arretsPhysiques = null;
    if (idLigne == null)
    {
      arretsPhysiques = positionGeographiqueManager.lireArretsPhysiques();
    }
    else
    {
      //arretsPhysiques = arretPhysiqueManager.getArretsPhysiques(idLigne);
      arretsPhysiques = positionGeographiqueManager.lireArretsPhysiques();
    }

    resultat += "{";
    for (PositionGeographique arretPhysique : arretsPhysiques)
    {
      if (arretsPhysiques.indexOf(arretPhysique) == arretsPhysiques.size() - 1)
      {
        resultat += "\"" + arretPhysique.getName() + "\"" + ": " + arretPhysique.getId();
      }
      else
      {
        resultat += "\"" + arretPhysique.getName() + "\"" + ": " + arretPhysique.getId() + ",";
      }
    }
    resultat += "}";
    return resultat;
  }

  /********************************************************
   *                     RESEARCH FORM                    *
   ********************************************************/
  public String getSaisieNomArretExistant()
  {
    return saisieNomArretExistant;
  }

  public void setSaisieNomArretExistant(String saisieNomArretExistant)
  {
    this.saisieNomArretExistant = saisieNomArretExistant;
  }

  public String getSaisieNomArretExistantKey()
  {
    return saisieNomArretExistantKey;
  }

  public void setSaisieNomArretExistantKey(String saisieNomArretExistantKey)
  {
    this.saisieNomArretExistantKey = saisieNomArretExistantKey;
  }

  public List<PositionGeographique> getArrets()
  {
    arrets = ligneManager.getArretsPhysiques(model.getIdLigne());
    
    if (arretsDansITLList != null)
    {
      for (int i = 0; i < arretsDansITLList.size(); i++)
      {
        if (arrets != null)
        {
          for (int j = 0; j < arrets.size(); j++)
          {
            Long l1 = arretsDansITLList.get(i).getId();
            Long l2 = arrets.get(j).getId();
            if (l1.equals(l2))
            {
              arrets.remove(j);
            }
          }
        }
        arrets.remove(arretsDansITLList.get(i));
      }
    }

    return arrets;
  }

  public List<PositionGeographique> getArretsDansITLList()
  {
    arretsDansITLList = new ArrayList();
    if (model != null)
    {
      List list_id = model.getArretPhysiqueIds();
      if (list_id != null)
      {
        for (int i = 0; i < list_id.size(); i++)
        {
          Long id = (Long) list_id.get(i);
          PositionGeographique ap = positionGeographiqueManager.lire(id);
          arretsDansITLList.add(ap);
        }
      }
    }
    return arretsDansITLList;
  }

  /********************************************************
   *                     Line                             *
   ********************************************************/
  /**
   * Retourne le nom de la ligne
   *
   * @return string
   */
  public String getLigneName()
  {
    if (model != null && model.getIdLigne() != null)
    {
      Ligne l = ligneManager.lire(model.getIdLigne());
      return l.getFullName();
    }
    else
    {
      return null;
    }
  }

  public List<Ligne> getLignes()
  {
    return ligneManager.lire();
  }

  /********************************************************
   *                   METHODE ACTION                     *
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
