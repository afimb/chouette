package fr.certu.chouette.struts.line;

import com.opensymphony.xwork2.ModelDriven;
import com.opensymphony.xwork2.Preparable;
import fr.certu.chouette.critere.AndClause;
import fr.certu.chouette.critere.IClause;
import fr.certu.chouette.critere.ScalarClause;
import fr.certu.chouette.struts.GeneriqueAction;
import fr.certu.chouette.modele.Ligne;
import fr.certu.chouette.modele.Reseau;
import fr.certu.chouette.modele.Transporteur;
import fr.certu.chouette.service.database.ILigneManager;
import fr.certu.chouette.service.database.IReseauManager;
import fr.certu.chouette.service.database.ITransporteurManager;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts2.interceptor.validation.SkipValidation;

public class LineAction extends GeneriqueAction implements ModelDriven, Preparable
{

  private static final Log log = LogFactory.getLog(LineAction.class);
  private Ligne lineModel = new Ligne();
  private static ILigneManager ligneManager;
  private static ITransporteurManager transporteurManager;
  private static IReseauManager reseauManager;
  private Long idLigne;
  private String mappedRequest;
  private String typeLigne;
  private String useAmivif;
  private boolean detruireAvecTMs;
  private boolean detruireAvecArrets;
  private boolean detruireAvecTransporteur;
  private boolean detruireAvecReseau;
  private List<Reseau> networks;
  private List<Transporteur> companies;
  private String networkName = "";
  private String companyName = "";

  public Long getIdLigne()
  {
    return idLigne;
  }

  public void setIdLigne(Long idLigne)
  {
    this.idLigne = idLigne;
  }

  /********************************************************
   *                  MODEL + PREPARE                     *
   ********************************************************/
  public Ligne getModel()
  {
    return lineModel;
  }

  public void prepare() throws Exception
  {
    log.debug("Prepare with id : " + getIdLigne());
    if (getIdLigne() == null)
    {
      lineModel = new Ligne();
    }
    else
    {
      lineModel = ligneManager.lire(getIdLigne());
    }

    networks = new ArrayList<Reseau>(reseauManager.lire());
    companies = new ArrayList<Transporteur>(transporteurManager.lire());
  }

  /********************************************************
   *                           CRUD                       *
   ********************************************************/
  @SkipValidation
  public String list()
  {
    IClause clauseFiltre = new AndClause().add(ScalarClause.newEqualsClause("idReseau", lineModel.getIdReseau())).add(ScalarClause.newEqualsClause("idTransporteur", lineModel.getIdTransporteur())).add(ScalarClause.newIlikeClause("name", lineModel.getName()));
    this.request.put("lignes", ligneManager.select(clauseFiltre));
    log.debug("List of lines");
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
    ligneManager.creer(getModel());
    setMappedRequest(SAVE);
    addActionMessage(getText("ligne.create.ok"));
    log.debug("Create line with id : " + getModel().getId());

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
    Ligne ligne = getModel();
    if (ligne == null)
    {
      return INPUT;
    }
    if (ligneManager.nomConnu(ligne.getName()))
    {
      addActionMessage(getText("ligne.homonyme"));
    }
    if (ligne.getIdReseau().equals(new Long(-1)))
    {
      ligne.setIdReseau(null);
    }
    if (ligne.getIdTransporteur().equals(new Long(-1)))
    {
      ligne.setIdTransporteur(null);
    }
    else
    {
      ligneManager.modifier(ligne);
      setMappedRequest(UPDATE);
      addActionMessage(getText("ligne.update.ok"));
      log.debug("Update network with id : " + getModel().getId());
    }

    return REDIRECTLIST;
  }

  public String delete()
  {
    ligneManager.supprimer(getModel().getId(), detruireAvecTMs, detruireAvecArrets, detruireAvecTransporteur, detruireAvecReseau);
    addActionMessage(getText("ligne.delete.ok"));
    log.debug("Delete line with id : " + getModel().getId());

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

  /********************************************************
   *                        MANAGER                       *
   ********************************************************/
  public void setLigneManager(ILigneManager ligneManager)
  {
    LineAction.ligneManager = ligneManager;
  }

  public void setReseauManager(IReseauManager reseauManager)
  {
    LineAction.reseauManager = reseauManager;
  }

  public void setTransporteurManager(ITransporteurManager transporteurManager)
  {
    LineAction.transporteurManager = transporteurManager;
  }

  /********************************************************
   *                   METHOD  ACTION                     *
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
   *                   OTHERS METHODS                     *
   ********************************************************/
  public String getReseau(Long networkId)
  {
    for (Reseau network : networks)
    {
      if (network.getId().equals(networkId))
      {
        networkName = network.getName();
        break;
      }
    }
    return networkName;
  }

  public void setReseaux(List<Reseau> reseaux)
  {
    this.networks = reseaux;
  }

  public List<Reseau> getReseaux()
  {
    return networks;
  }

  public String getTransporteur(Long companyId)
  {
    for (Transporteur company : companies)
    {
      if (company.getId().equals(companyId))
      {
        companyName = company.getName();
      }
    }
    return companyName;
  }

  public List<Transporteur> getTransporteurs()
  {
    return companies;
  }

  public void setTransporteurs(List<Transporteur> transporteurs)
  {
    this.companies = transporteurs;
  }

  public void setTypeLigne(String typeLigne)
  {
    this.typeLigne = typeLigne;
  }

  public String getUseAmivif()
  {
    return useAmivif;
  }

  public void setUseAmivif(String useAmivif)
  {
    this.useAmivif = useAmivif;
  }

  public void setDetruireAvecTMs(boolean detruireAvecTMs)
  {
    this.detruireAvecTMs = detruireAvecTMs;
  }

  public void setDetruireAvecArrets(boolean detruireAvecArrets)
  {
    this.detruireAvecArrets = detruireAvecArrets;
  }

  public void setDetruireAvecTransporteur(boolean detruireAvecTransporteur)
  {
    this.detruireAvecTransporteur = detruireAvecTransporteur;
  }

  public void setDetruireAvecReseau(boolean detruireAvecReseau)
  {
    this.detruireAvecReseau = detruireAvecReseau;
  }
}
