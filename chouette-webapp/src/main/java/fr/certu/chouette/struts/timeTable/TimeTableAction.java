package fr.certu.chouette.struts.timeTable;

import chouette.schema.types.DayTypeType;
import com.opensymphony.xwork2.ModelDriven;
import com.opensymphony.xwork2.Preparable;
import fr.certu.chouette.struts.converter.JourTypeTMConverter;
import fr.certu.chouette.modele.Periode;
import fr.certu.chouette.modele.Reseau;
import fr.certu.chouette.modele.TableauMarche;
import fr.certu.chouette.service.database.IReseauManager;
import fr.certu.chouette.service.database.ITableauMarcheManager;
import fr.certu.chouette.struts.GeneriqueAction;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts2.interceptor.validation.SkipValidation;

public class TimeTableAction extends GeneriqueAction implements Preparable, ModelDriven<TableauMarche>
{

  private static final Log log = LogFactory.getLog(TimeTableAction.class);
  private ITableauMarcheManager tableauMarcheManager;
  private IReseauManager reseauManager;
  private Long idTableauMarche;
  private Date jour;
  private Date debut;
  private Date fin;
  private Integer idxDate;
  private Integer idxPeriod;
  private List<DayTypeType> joursTypes;
  private List<Reseau> reseaux;
  private String commentaire = null;
  private Long idReseau = null;
  private Date dateDebutPeriode = null;
  private Date dateFinPeriode = null;
  private TableauMarche tableauMarcheModel = new TableauMarche();
  private String mappedRequest;

  public Long getIdTableauMarche()
  {
    return idTableauMarche;
  }

  public void setIdTableauMarche(Long idTableauMarche)
  {
    this.idTableauMarche = idTableauMarche;
  }

  /********************************************************
   *                  MODEL + PREPARE                     *
   ********************************************************/
  public TableauMarche getModel()
  {
    return tableauMarcheModel;
  }

  public void prepare() throws Exception
  {
    log.debug("Prepare with id : " + getIdTableauMarche());
    if (getIdTableauMarche() == null)
    {
      tableauMarcheModel = new TableauMarche();
    }
    else
    {
      tableauMarcheModel = tableauMarcheManager.lire(idTableauMarche);
    }

    // Chargement des réseaux
    reseaux = reseauManager.lire();

    //	Création de la liste des types de jours
    joursTypes = JourTypeTMConverter.getProperties(tableauMarcheModel);
  }

  /********************************************************
   *                           CRUD                       *
   ********************************************************/
  @SkipValidation
  public String list()
  {
    if ("".equals(commentaire))
    {
      commentaire = null;
    }
    if ("".equals(dateDebutPeriode))
    {
      dateDebutPeriode = null;
    }
    if ("".equals(dateFinPeriode))
    {
      dateFinPeriode = null;
    }

    this.request.put("tableauxMarche", tableauMarcheManager.lire(dateDebutPeriode, dateFinPeriode, commentaire, idReseau));
    log.debug("List of tableauMarche");
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
    tableauMarcheManager.creer(tableauMarcheModel);

    setMappedRequest(UPDATE);
    String[] args = new String[1];
    args[0] = tableauMarcheModel.getObjectId();
    addActionMessage(getText("tableauMarche.create.ok", args));
    // Update timetable id to update timetable
    setIdTableauMarche(tableauMarcheModel.getId());
    log.debug("Create tableauMarche with id : " + tableauMarcheModel.getId());
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
    JourTypeTMConverter.setDayTypes(tableauMarcheModel, joursTypes);

    tableauMarcheManager.modifier(tableauMarcheModel);
    setMappedRequest(UPDATE);
    String[] args = new String[1];
    args[0] = tableauMarcheModel.getObjectId();
    addActionMessage(getText("tableauMarche.update.ok", args));
    log.debug("Update tableauMarche with id : " + tableauMarcheModel.getId());
    return REDIRECTEDIT;
  }

  public String delete()
  {
    tableauMarcheManager.supprimer(tableauMarcheModel.getId());

    ArrayList args = new ArrayList();
    args.add(tableauMarcheModel.getObjectId());
    addActionMessage(getText("tableauMarche.delete.ok", args));
    log.debug("Delete tableauMarche with id : " + tableauMarcheModel.getId());

    return REDIRECTLIST;
  }

  @SkipValidation
  public String cancel()
  {
    addActionMessage(getText("tableauMarche.cancel.ok"));
    return REDIRECTLIST;
  }

  @Override
  @SkipValidation
  public String input() throws Exception
  {
    return INPUT;
  }

  public String addPeriode()
  {
    if (debut != null && fin != null)
    {
      Periode p = new Periode();
      p.setDebut(debut);
      p.setFin(fin);
      tableauMarcheModel.ajoutPeriode(p);
      debut = null;
      fin = null;

      if (tableauMarcheModel.getId() == null)
      {
        tableauMarcheManager.creer(tableauMarcheModel);
        addActionMessage(getText("tableauMarche.addperiod.ok"));
      }
      else
      {
        tableauMarcheManager.modifier(tableauMarcheModel);
        addActionMessage(getText("tableauMarche.addperiod.ok"));
      }
    }
    return REDIRECTEDIT;
  }

  public String deletePeriod()
  {
    if (this.idxPeriod != null)
    {
      Periode p = tableauMarcheModel.getPeriodes().get(idxPeriod.intValue() - 1);
      tableauMarcheModel.retraitPeriode(p);
      idxPeriod = null;
    }
    if (tableauMarcheModel.getId() == null)
    {
      tableauMarcheManager.creer(tableauMarcheModel);
      addActionMessage(getText("tableauMarche.deleteperiod.ok"));
    }
    else
    {
      tableauMarcheManager.modifier(tableauMarcheModel);
      addActionMessage(getText("tableauMarche.deleteperiod.ok"));
    }

    return REDIRECTEDIT;
  }

  public String addDate()
  {
    if (jour != null)
    {
      tableauMarcheModel.ajoutDate(jour);
      jour = null;
    }

    if (tableauMarcheModel.getId() == null)
    {
      tableauMarcheManager.creer(tableauMarcheModel);
      addActionMessage(getText("tableauMarche.addcalendarday.ok"));
    }
    else
    {
      tableauMarcheManager.modifier(tableauMarcheModel);
      addActionMessage(getText("tableauMarche.addcalendarday.ok"));
    }

    return REDIRECTEDIT;
  }

  public String deleteDate()
  {
    if (this.idxDate != null)
    {
      Date d = tableauMarcheModel.getDates().get(idxDate.intValue() - 1);
      tableauMarcheModel.retraitDate(d);
      idxDate = null;
    }
    if (tableauMarcheModel.getId() == null)
    {
      tableauMarcheManager.creer(tableauMarcheModel);
      addActionMessage(getText("tableauMarche.deletecalendarday.ok"));
    }
    else
    {
      tableauMarcheManager.modifier(tableauMarcheModel);
      addActionMessage(getText("tableauMarche.deletecalendarday.ok"));
    }

    return REDIRECTEDIT;
  }

  /********************************************************
   *                        MANAGER                       *
   ********************************************************/
  public void setReseauManager(IReseauManager reseauManager)
  {
    this.reseauManager = reseauManager;
  }

  public void setTableauMarcheManager(ITableauMarcheManager tableauMarcheManager)
  {
    this.tableauMarcheManager = tableauMarcheManager;
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
   *                   FILTER                             *
   ********************************************************/
  public Long getIdReseau()
  {
    return idReseau;
  }

  public void setIdReseau(Long idReseau)
  {
    this.idReseau = idReseau;
  }

  public String getCommentaire()
  {
    return commentaire;
  }

  public void setCommentaire(String commentaire)
  {
    this.commentaire = commentaire;
  }

  public Date getDateDebutPeriode()
  {
    return dateDebutPeriode;
  }

  public void setDateDebutPeriode(Date dateDebutPeriode)
  {
    this.dateDebutPeriode = dateDebutPeriode;
  }

  public Date getDateFinPeriode()
  {
    return dateFinPeriode;
  }

  public void setDateFinPeriode(Date dateFinPeriode)
  {
    this.dateFinPeriode = dateFinPeriode;
  }

  /********************************************************
   *                   METHODS                            *
   ********************************************************/
  public Date getJour()
  {
    return jour;
  }

  public void setJour(Date jour)
  {
    this.jour = jour;
  }

  public Date getDebut()
  {
    return debut;
  }

  public void setDebut(Date debut)
  {
    this.debut = debut;
  }

  public Date getFin()
  {
    return fin;
  }

  public void setFin(Date fin)
  {
    this.fin = fin;
  }

  public Integer getIdxDate()
  {
    return idxDate;
  }

  public void setIdxDate(Integer idxDate)
  {
    this.idxDate = idxDate;
  }

  public Integer getIdxPeriod()
  {
    return idxPeriod;
  }

  public void setIdxPeriod(Integer idxPeriod)
  {
    this.idxPeriod = idxPeriod;
  }

  public List<DayTypeType> getJoursTypes()
  {
    return JourTypeTMConverter.getProperties(tableauMarcheModel);
  }

  public void setJoursTypes(List<DayTypeType> joursTypes)
  {
    this.joursTypes = joursTypes;
  }

  public List<Reseau> getReseaux()
  {
    return reseaux;
  }

  public void setReseaux(List<Reseau> reseaux)
  {
    this.reseaux = reseaux;
  }
}
