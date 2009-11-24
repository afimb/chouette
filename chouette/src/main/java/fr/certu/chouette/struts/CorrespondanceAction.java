package fr.certu.chouette.struts;

import com.opensymphony.xwork2.ModelDriven;
import com.opensymphony.xwork2.Preparable;
import fr.certu.chouette.critere.AndClause;
import fr.certu.chouette.critere.ScalarClause;
import fr.certu.chouette.critere.VectorClause;
import fr.certu.chouette.struts.enumeration.ObjetEnumere;
import fr.certu.chouette.modele.Correspondance;
import fr.certu.chouette.modele.PositionGeographique;
import fr.certu.chouette.service.database.ICorrespondanceManager;
import fr.certu.chouette.service.database.IPositionGeographiqueManager;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts2.interceptor.validation.SkipValidation;

public class CorrespondanceAction extends GeneriqueAction implements ModelDriven<Correspondance>, Preparable
{

  private static final Log log = LogFactory.getLog(CorrespondanceAction.class);
  private static ICorrespondanceManager correspondanceManager;
  private static IPositionGeographiqueManager positionGeographiqueManager;
  private String useHastus;
  private List<Correspondance> correspondances;
  private Long idCorrespondance;
  private String zoneDepartText;
  private String zoneArriveeText;
  private String saisieZoneExistante;
  private String saisieZoneExistanteKey;
  private List<PositionGeographique> zones;
  private PositionGeographique criteria;
  private PositionGeographique start;
  private PositionGeographique end;
  private String actionSuivante;
  private Long idPositionGeographique;
  private List<PositionGeographique> positionGeographiquesResultat;
  private String durationsFormat = "mm:ss";
  private Correspondance correspondanceModel = new Correspondance();
  private String mappedRequest;

  public Long getIdCorrespondance()
  {
    return idCorrespondance;
  }

  public void setIdCorrespondance(Long idCorrespondance)
  {
    this.idCorrespondance = idCorrespondance;
  }

  public Long getIdPositionGeographique()
  {
    return idPositionGeographique;
  }

  public void setIdPositionGeographique(Long idPositionGeographique)
  {
    this.idPositionGeographique = idPositionGeographique;
  }

  /********************************************************
   *                  MODEL + PREPARE                     *
   ********************************************************/
  @Override
  public Correspondance getModel()
  {
    return correspondanceModel;
  }

  public void prepare() throws Exception
  {
    log.debug("Prepare with id : " + getIdCorrespondance());
    if (getIdCorrespondance() == null)
    {
      correspondanceModel = new Correspondance();
    }
    else
    {
      correspondanceModel = correspondanceManager.lire(getIdCorrespondance());
      if (correspondanceModel.getIdDepart() != null)
      {
        this.start = positionGeographiqueManager.lire(correspondanceModel.getIdDepart());
      }
      if (correspondanceModel.getIdArrivee() != null)
      {
        this.end = positionGeographiqueManager.lire(correspondanceModel.getIdArrivee());
      }
    }
  }

  /********************************************************
   *                           CRUD                       *
   ********************************************************/
  @SkipValidation
  public String list()
  {
    this.request.put("correspondances", correspondanceManager.lire());
    log.debug("List of connectionLinks");
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
      correspondanceManager.creer(getModel());
      addActionMessage(getText("connectionlink.create.ok"));
    }
    catch (Exception exception)
    {
      addActionError(getText("connectionlink.create.ko"));
    }
    setMappedRequest(SAVE);
    log.debug("Create connectionLink with id : " + getModel().getId());
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
      correspondanceManager.modifier(getModel());
      addActionMessage(getText("connectionlink.update.ok"));
    }
    catch (Exception ex)
    {
      addActionError(getText("connectionlink.update.ko"));
    }
    setMappedRequest(UPDATE);
    log.debug("Update connectionLink with id : " + getModel().getId());
    return REDIRECTLIST;
  }

  public String delete()
  {
    correspondanceManager.supprimer(getModel().getId());
    addActionMessage(getText("connectionlink.delete.ok"));
    log.debug("Delete connectionLink with id : " + getModel().getId());
    return REDIRECTLIST;
  }

  @SkipValidation
  public String cancel()
  {
    addActionMessage(getText("connectionlink.cancel.ok"));
    return REDIRECTLIST;
  }

  @Override
  @SkipValidation
  public String input() throws Exception
  {
    return INPUT;
  }

  @SkipValidation
  public String search()
  {
    return SEARCH;
  }

  @SkipValidation
  public String doSearch()
  {
    Collection<String> areas = new HashSet<String>();
    if (criteria.getAreaType() != null)
    {
      areas.add(criteria.getAreaType().toString());
    }
    else
    {
      List<ObjetEnumere> areaEnumerations = fr.certu.chouette.struts.enumeration.EnumerationApplication.getArretPhysiqueAreaTypeEnum();
      areaEnumerations.addAll(fr.certu.chouette.struts.enumeration.EnumerationApplication.getZoneAreaTypeEnum());
      for (ObjetEnumere enumeration : areaEnumerations)
      {
        areas.add(enumeration.getEnumeratedTypeAccess().toString());
      }
    }
    if ("".equals(criteria.getName()))
    {
      criteria.setName(null);
    }
    if ("".equals(criteria.getCountryCode()))
    {
      criteria.setCountryCode(null);
    }
    positionGeographiquesResultat = positionGeographiqueManager.select(new AndClause().add(ScalarClause.newIlikeClause("name", criteria.getName())).
            add(ScalarClause.newIlikeClause("countryCode", criteria.getCountryCode())).
            add(VectorClause.newInClause("areaType", areas)));

    request.put("positionGeographiquesResultat", positionGeographiquesResultat);

    return SEARCH;
  }

  @SkipValidation
  public String cancelSearch()
  {
    return REDIRECTEDIT;
  }


  @SkipValidation
  public String addStart()
  {
    if (idPositionGeographique != null && idCorrespondance != null)
    {
      correspondanceModel = correspondanceManager.lire(idCorrespondance);
      correspondanceModel.setIdDepart(idPositionGeographique);
      correspondanceManager.modifier(correspondanceModel);
    }
    return REDIRECTEDIT;
  }

  @SkipValidation
  public String addEnd()
  {
    if (idPositionGeographique != null && idCorrespondance != null)
    {
      correspondanceModel = correspondanceManager.lire(idCorrespondance);
      correspondanceModel.setIdArrivee(idPositionGeographique);
      correspondanceManager.modifier(correspondanceModel);
    }
    return REDIRECTEDIT;
  }

  /********************************************************
   *                        INIT                          *
   ********************************************************/
  public void setUseHastus(String useHastus)
  {
    this.useHastus = useHastus;
  }

  public String getUseHastus()
  {
    return useHastus;
  }

  /********************************************************
   *                        MANAGER                       *
   ********************************************************/
  public void setPositionGeographiqueManager(IPositionGeographiqueManager positionGeographiqueManager)
  {
    CorrespondanceAction.positionGeographiqueManager = positionGeographiqueManager;
  }

  public void setCorrespondanceManager(ICorrespondanceManager correspondanceManager)
  {
    CorrespondanceAction.correspondanceManager = correspondanceManager;
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

  /********************************************************
   *                   FILTER                             *
   ********************************************************/
  public PositionGeographique getCriteria()
  {
    return criteria;
  }

  public void setCriteria(PositionGeographique criteria)
  {
    this.criteria = criteria;
  }

  /********************************************************
   *                   TIME FORMAT                        *
   ********************************************************/
  public void setStrutsOccasionalTravellerDuration(String s)
  {
    SimpleDateFormat sdfHoraire = new SimpleDateFormat(durationsFormat);
    if (s != null && s.length() > 0)
    {
      try
      {
        Date d = sdfHoraire.parse(s);
        correspondanceModel.setOccasionalTravellerDuration(d);
      }
      catch (Exception ex)
      {
        addActionError(ex.getLocalizedMessage());
      }
    }
    else
    {
      correspondanceModel.setOccasionalTravellerDuration(null);
    }
  }

  public String getStrutsOccasionalTravellerDuration()
  {
    if (correspondanceModel != null && correspondanceModel.getOccasionalTravellerDuration() != null)
    {
      Date d = correspondanceModel.getOccasionalTravellerDuration();
      SimpleDateFormat sdfHoraire = new SimpleDateFormat(durationsFormat);
      return sdfHoraire.format(d);
    }
    else
    {
      return null;
    }
  }

  public void setStrutsMobilityRestrictedTravellerDuration(String s)
  {
    SimpleDateFormat sdfHoraire = new SimpleDateFormat(durationsFormat);
    if (s != null && s.length() > 0)
    {
      try
      {
        Date d = sdfHoraire.parse(s);
        correspondanceModel.setMobilityRestrictedTravellerDuration(d);
      }
      catch (Exception ex)
      {
        addActionError(ex.getLocalizedMessage());
      }
    }
    else
    {
      correspondanceModel.setMobilityRestrictedTravellerDuration(null);
    }
  }

  public String getStrutsMobilityRestrictedTravellerDuration()
  {
    if (correspondanceModel != null && correspondanceModel.getMobilityRestrictedTravellerDuration() != null)
    {
      Date d = correspondanceModel.getMobilityRestrictedTravellerDuration();
      SimpleDateFormat sdfHoraire = new SimpleDateFormat(durationsFormat);
      return sdfHoraire.format(d);
    }
    else
    {
      return null;
    }
  }

  public void setStrutsFrequentTravellerDuration(String s)
  {
    SimpleDateFormat sdfHoraire = new SimpleDateFormat(durationsFormat);
    if (s != null && s.length() > 0)
    {
      try
      {
        Date d = sdfHoraire.parse(s);
        correspondanceModel.setFrequentTravellerDuration(d);
      }
      catch (Exception ex)
      {
        addActionError(ex.getLocalizedMessage());
      }
    }
    else
    {
      correspondanceModel.setFrequentTravellerDuration(null);
    }
  }

  public String getStrutsFrequentTravellerDuration()
  {
    if (correspondanceModel != null && correspondanceModel.getFrequentTravellerDuration() != null)
    {
      Date d = correspondanceModel.getFrequentTravellerDuration();
      SimpleDateFormat sdfHoraire = new SimpleDateFormat(durationsFormat);
      return sdfHoraire.format(d);
    }
    else
    {
      return null;
    }
  }

  public void setStrutsDefaultDuration(String s)
  {
    SimpleDateFormat sdfHoraire = new SimpleDateFormat(durationsFormat);
    if (s != null && s.length() > 0)
    {
      try
      {
        Date d = sdfHoraire.parse(s);
        correspondanceModel.setDefaultDuration(d);
      }
      catch (Exception ex)
      {
        addActionError(ex.getLocalizedMessage());
      }
    }
    else
    {
      correspondanceModel.setDefaultDuration(null);
    }
  }

  public String getStrutsDefaultDuration()
  {
    if (correspondanceModel != null && correspondanceModel.getDefaultDuration() != null)
    {
      Date d = correspondanceModel.getDefaultDuration();
      SimpleDateFormat sdfHoraire = new SimpleDateFormat(durationsFormat);
      return sdfHoraire.format(d);
    }
    else
    {
      return null;
    }
  }

  public PositionGeographique getStart()
  {
    return start;
  }

  public void setStart(PositionGeographique start)
  {
    this.start = start;
  }

  public PositionGeographique getEnd()
  {
    return end;
  }

  public void setEnd(PositionGeographique end)
  {
    this.end = end;
  }

  public String getActionSuivante()
  {
    return actionSuivante;
  }

  public void setActionSuivante(String actionSuivante)
  {
    this.actionSuivante = actionSuivante;
  }
}
