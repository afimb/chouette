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
import java.util.Hashtable;
import java.util.Map;
import org.apache.struts2.interceptor.validation.SkipValidation;

public class LigneAction extends GeneriqueAction implements ModelDriven, Preparable
{

  private LineModel ligneModel = new LineModel();
  private static ILigneManager ligneManager;
  private static ITransporteurManager transporteurManager;
  private static IReseauManager reseauManager;
  private String typeLigne;
  private String useAmivif;
  private boolean detruireAvecTMs;
  private boolean detruireAvecArrets;
  private boolean detruireAvecTransporteur;
  private boolean detruireAvecReseau;

  // MODEL & PREPARE _________________________________________________________________________
  public Object getModel()
  {
    return ligneModel;
  }

  public void prepare() throws Exception
  {
    ligneModel.setReseaux(reseauManager.lire());
    Map<Long, Reseau> reseauParId = new Hashtable<Long, Reseau>();
    for (Reseau reseau : ligneModel.getReseaux())
    {
      reseauParId.put(reseau.getId(), reseau);
    }
    ligneModel.setReseauParId(reseauParId);
    
    ligneModel.setTransporteurs(transporteurManager.lire());
    Map<Long, Transporteur> transporteurParId = new Hashtable<Long, Transporteur>();
    for (Transporteur transporteur : ligneModel.getTransporteurs())
    {
      transporteurParId.put(transporteur.getId(), transporteur);
    }
    ligneModel.setTransporteurParId(transporteurParId);

    if (ligneModel.getIdLigne() != null)
    {
      ligneModel.setLigne(ligneManager.lire(ligneModel.getIdLigne()));
    }
  }

  // LIST ____________________________________________________________________________________
  @SkipValidation
  public String list()
  {
    IClause clauseFiltre = new AndClause().add(ScalarClause.newEqualsClause("idReseau", ligneModel.getIdReseau())).add(ScalarClause.newEqualsClause("idTransporteur", ligneModel.getIdTransporteur())).add(ScalarClause.newIlikeClause("name", ligneModel.getNomLigne()));
    ligneModel.setLignes(ligneManager.select(clauseFiltre));
    return SUCCESS;
  }

  // CRUD ____________________________________________________________________________________
  public String edit()
  {
    return INPUT;
  }

  public String createAndEdit()
  {
    Ligne ligne = ligneModel.getLigne();
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
    if (ligne.getId() == null)
    {
      ligneManager.creer(ligne);
      addActionMessage(getText("ligne.create.ok"));
      return "createAndEdit";
    }
    else
    {
      return INPUT;
    }
  }

  public String update()
  {
    Ligne ligne = ligneModel.getLigne();
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
    if (ligne.getId() == null)
    {
      ligneManager.creer(ligne);
      addActionMessage(getText("ligne.create.ok"));
    }
    else
    {
      ligneManager.modifier(ligne);
      addActionMessage(getText("ligne.update.ok"));
    }
    return INPUT;
  }

  public String delete()
  {
    Ligne ligne = ligneModel.getLigne();
    if (ligne == null)
    {
      return INPUT;
    }
    Long idLigne = ligne.getId();
    if (idLigne == null)
    {
      return INPUT;
    }
    ligneManager.supprimer(idLigne, detruireAvecTMs, detruireAvecArrets, detruireAvecTransporteur, detruireAvecReseau);
    return SUCCESS;
  }

  // MANAGERS ________________________________________________________________________________
  public void setLigneManager(ILigneManager ligneManager)
  {
    this.ligneManager = ligneManager;
  }

  public void setReseauManager(IReseauManager reseauManager)
  {
    this.reseauManager = reseauManager;
  }

  public void setTransporteurManager(ITransporteurManager transporteurManager)
  {
    this.transporteurManager = transporteurManager;
  }

  // MISC ____________________________________________________________________________________
  public String cancel()
  {
    addActionMessage(getText("ligne.cancel.ok"));
    return SUCCESS;
  }

  @Override
  public String input() throws Exception
  {
    return INPUT;
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

  public void setIdLigne(Long idLigne)
  {
    ligneModel.setIdLigne(idLigne);
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
