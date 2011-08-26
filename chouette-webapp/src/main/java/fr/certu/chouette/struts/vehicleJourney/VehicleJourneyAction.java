package fr.certu.chouette.struts.vehicleJourney;

import com.opensymphony.xwork2.ModelDriven;
import com.opensymphony.xwork2.Preparable;
import fr.certu.chouette.modele.Course;
import fr.certu.chouette.modele.Itineraire;
import fr.certu.chouette.modele.Ligne;
import fr.certu.chouette.modele.TableauMarche;
import fr.certu.chouette.service.commun.CodeDetailIncident;
import fr.certu.chouette.service.commun.CodeIncident;
import fr.certu.chouette.service.commun.ServiceException;
import fr.certu.chouette.service.database.ICourseManager;
import fr.certu.chouette.service.database.IItineraireManager;
import fr.certu.chouette.service.database.ILigneManager;
import fr.certu.chouette.service.database.ITableauMarcheManager;
import fr.certu.chouette.struts.GeneriqueAction;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts2.interceptor.validation.SkipValidation;

public class VehicleJourneyAction extends GeneriqueAction implements ModelDriven<Course>, Preparable
{

  private static final Log log = LogFactory.getLog(VehicleJourneyAction.class);
  
  private static final Set<String> PARTICULARITES_VALIDES;
  static {
    PARTICULARITES_VALIDES = new HashSet<String>();
    PARTICULARITES_VALIDES.add("TAD");
  }
  
  //	Managers
  private ICourseManager courseManager;
  private IItineraireManager itineraireManager;
  private ITableauMarcheManager tableauMarcheManager;
  private ILigneManager ligneManager;
  //	Identifiants
  private Long idCourse;
  private Long idLigne;
  private Long idItineraire;
  private Long idTableauMarche;
  private List<String> particularites = new ArrayList<String>();
  private Date seuilDateDepartCourse;
  private Long page;
  //	Liste des courses et course sélectionnée
  private String saisieTableauMarche;
  private Long saisieTableauMarcheKey;
  private List<TableauMarche> tableauxMarcheAssocieCourse;
  private List<TableauMarche> tableauxMarchePasAssocieCourse;
  private Course model = new Course();
  private String mappedRequest;

  public Long getIdCourse()
  {
    return idCourse;
  }

  public void setIdCourse(Long idCourse)
  {
    this.idCourse = idCourse;
  }

  public Long getIdItineraire()
  {
    return idItineraire;
  }

  public void setIdItineraire(Long idItineraire)
  {
    this.idItineraire = idItineraire;
  }

  public void setIdTableauMarche(Long idTableauMarche)
  {
    this.idTableauMarche = idTableauMarche;
  }

  public Long getIdTableauMarche()
  {
    return idTableauMarche;
  }

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
  @Override
  public Course getModel()
  {
    return model;
  }
  
  @Override
  public void prepare() throws Exception
  {
    log.debug("Prepare with id : " + getIdCourse());
    if (getIdCourse() == null)
    {
      model = new Course();
    } else
    {
      model = courseManager.lire(getIdCourse());
      String vehicleTypeIdentifier = model.getVehicleTypeIdentifier();
      if(vehicleTypeIdentifier != null)
    	  particularites = Arrays.asList(vehicleTypeIdentifier.split(","));
    }

    if (idCourse != null)
    {
      //	Création d'une map idTableauMarche -> TableauMarche
      List<TableauMarche> tableauxMarches = tableauMarcheManager.lireSansDateNiPeriode();
      //	Récupération des tableaux de marche associés à la course
      tableauxMarcheAssocieCourse = courseManager.getTableauxMarcheCourse(idCourse);
      // Récupération de la liste des ids de tous les tableaux de marche
      Map<Long, TableauMarche> tableauxMarcheParId = new HashMap<Long, TableauMarche>();
      for (TableauMarche tableauMarche : tableauxMarches)
      {
        tableauxMarcheParId.put(tableauMarche.getId(), tableauMarche);
      }
      //	Elimination dans la liste des tableaux de marche ceux déjà associés à la course
      for (TableauMarche tableauMarche : tableauxMarcheAssocieCourse)
      {
        if (tableauxMarcheParId.containsKey(tableauMarche.getId()))
        {
          tableauxMarcheParId.remove(tableauMarche.getId());
        }
      }
      //	Récupération des tableaux de marche non associés à la course
      tableauxMarchePasAssocieCourse = new ArrayList<TableauMarche>();
      tableauxMarchePasAssocieCourse.addAll(tableauxMarcheParId.values());
      //	Place en requête la liste des tableaux de marche pas associé à la course
      request.put("jsonTableauMarches", getJsonTableauMarches());
    }
  }

  /********************************************************
   *                           CRUD                       *
   ********************************************************/
  @SkipValidation
  public String list()
  {
    this.request.put("courses", itineraireManager.getCoursesItineraire(idItineraire));
    log.debug("List of vehicleJourney");
    if (actionMsg != null) {
        addActionMessage(actionMsg);
        actionMsg = null;
    }
    if (errorMsg != null) {
        addActionError(errorMsg);
        errorMsg = null;
    }
    return LIST;
  }

  @SkipValidation
  public String add()
  {
    setMappedRequest(SAVE);
    return EDIT;
  }
  
  private static String actionMsg = null;
  private static String errorMsg = null;
  

  public String save()
  {

    // ré-affecter l'identifiant de l'itinéraire sur la course
    model.setIdItineraire(idItineraire);

    //remplissage du champ vehicleTypeIdentifier avec les particularites
    if(particularites.size() > 0)
    	model.setVehicleTypeIdentifier(StringUtils.join(particularites,','));
    else
    	model.setVehicleTypeIdentifier(null);

    courseManager.creer(model);
    //addActionMessage(getText("course.create.ok"));
    actionMsg = getText("course.create.ok");
    setMappedRequest(SAVE);
    log.debug("Create vehicleJourney with id : " + getModel().getId());
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

    // ré-affecter l'identifiant de l'itinéraire sur la course
    model.setIdItineraire(idItineraire);

    //remplissage du champ vehicleTypeIdentifier avec les particularites
    if(particularites.size() > 0)
    	model.setVehicleTypeIdentifier(StringUtils.join(particularites,','));
    else
    	model.setVehicleTypeIdentifier(null);

    courseManager.modifier(model);
    setMappedRequest(UPDATE);
    addActionMessage(getText("course.update.ok"));
    log.debug("Update vehicleJourney with id : " + model.getId());
    return REDIRECTLIST;
  }

  public String delete()
  {
    courseManager.supprimer(model.getId());
    addActionMessage(getText("course.delete.ok"));
    log.debug("Delete vehicleJourney with id : " + getModel().getId());
    return REDIRECTLIST;
  }

  @SkipValidation
  public String cancel()
  {
    addActionMessage(getText("course.cancel.ok"));
    return REDIRECTLIST;
  }

  @Override
  @SkipValidation
  public String input() throws Exception
  {
    return REDIRECTLIST;
  }

  /********************************************************
   *                        MANAGER                       *
   ********************************************************/
  public void setCourseManager(ICourseManager courseManager)
  {
    this.courseManager = courseManager;
  }

  public void setItineraireManager(IItineraireManager itineraireManager)
  {
    this.itineraireManager = itineraireManager;
  }

  public void setTableauMarcheManager(ITableauMarcheManager tableauMarcheManager)
  {
    this.tableauMarcheManager = tableauMarcheManager;
  }

  public void setLigneManager(ILigneManager ligneManager)
  {
    this.ligneManager = ligneManager;
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
   *                      JSON                            *
   ********************************************************/
  public String getJsonTableauMarches()
  {
    StringBuffer resultat = new StringBuffer("{");
    TableauMarche dernier = null;
    List<TableauMarche> tms = tableauxMarchePasAssocieCourse;
    if (tms.size() > 0)
    {
      dernier = tms.remove(tms.size() - 1);
    }
    for (TableauMarche tm : tms)
    {
      resultat.append("\"");
      resultat.append(tm.getComment());
      resultat.append("(");
      resultat.append(tm.getObjectId());
      resultat.append(")\": ");
      resultat.append(tm.getId());
      resultat.append(",");
    }
    if (dernier != null)
    {
      resultat.append("\"");
      resultat.append(dernier.getComment());
      resultat.append("(");
      resultat.append(dernier.getObjectId());
      resultat.append(")\": ");
      resultat.append(dernier.getId());
    }
    resultat.append("}");
    // bien penser remettre élément dans la liste pour qu'elle demeure inchangée
    tableauxMarchePasAssocieCourse.add(dernier);
    return resultat.toString();
  }

  public Ligne getLigne()
  {
    return ligneManager.lire(idLigne);
  }

  public Itineraire getItineraire()
  {
    return itineraireManager.lire(idItineraire);
  }

  public List<TableauMarche> getTableauxMarche()
  {
    return tableauxMarcheAssocieCourse;
  }

  public List<TableauMarche> getTableauxMarchePasAssocieCourse()
  {
    return tableauxMarchePasAssocieCourse;
  }

  @SkipValidation
  public String creerAssociationTableauMarche()
  {
    //	Récupération des id des tableau de marche associés à la course
    List<Long> idsTableauxMarcheAssocieCourse = new ArrayList<Long>();
    for (TableauMarche tableauMarche : tableauxMarcheAssocieCourse)
    {
      idsTableauxMarcheAssocieCourse.add(tableauMarche.getId());
    }
    //	Récupération des id des tableau de marche non associés à la course
    List<Long> idsTableauxMarchePasAssocieCourse = new ArrayList<Long>();
    for (TableauMarche tableauMarche : tableauxMarchePasAssocieCourse)
    {
      idsTableauxMarchePasAssocieCourse.add(tableauMarche.getId());
    }
    //	Ajout de l'id du tableau de marche sélectionné dans la liste déroulante à la liste
    if (saisieTableauMarcheKey != null && idsTableauxMarchePasAssocieCourse.contains(saisieTableauMarcheKey))
    {
      idsTableauxMarcheAssocieCourse.add(saisieTableauMarcheKey);
      tableauMarcheManager.associerCourseTableauxMarche(idCourse, idsTableauxMarcheAssocieCourse);
      addActionMessage(getText("course.associationTableauMarche.ok"));
    } else
    {
      addActionError(getText("course.associationTableauMarche.ko"));
    }

    return REDIRECTEDIT;
  }

  @SkipValidation
  public String supprimerAssociationTableauMarche()
  {
    List<Long> idTableauxMarche = new ArrayList<Long>();
    for (TableauMarche tableauMarche : tableauxMarcheAssocieCourse)
    {
      idTableauxMarche.add(tableauMarche.getId());
    }
    idTableauxMarche.remove(idTableauMarche);
    tableauMarcheManager.associerCourseTableauxMarche(idCourse, idTableauxMarche);
    addActionMessage(getText("course.supprimerAssociationTableauMarche.ok"));

    return REDIRECTEDIT;
  }

  public void setSaisieTableauMarcheKey(Long saisieTableauMarcheKey)
  {
    log.debug("saisieTableauMarcheKey : " + saisieTableauMarcheKey);
    this.saisieTableauMarcheKey = saisieTableauMarcheKey;
  }

  public void setSaisieTableauMarche(String saisieTableauMarche)
  {
    this.saisieTableauMarche = saisieTableauMarche;
  }

  public String getModeTransportLigne()
  {
    Ligne ligne = ligneManager.lire(idLigne);
    return ligne.getTransportModeName().toString();
  }

  public void setPage(Long page)
  {
    this.page = page;
  }

  public Long getPage()
  {
    return page;
  }

  public void setSeuilDateDepartCourse(Date seuilDateDepartCourse)
  {
    this.seuilDateDepartCourse = seuilDateDepartCourse;
  }

  public Date getSeuilDateDepartCourse()
  {
    return seuilDateDepartCourse;
  }
  
  public void setParticularites(List<String> particularites) {
	//hack : remove empty strings from list
    while(particularites.contains(""))
    	particularites.remove("");
    this.particularites = particularites;
  }
  
  public List<String> getParticularites() {
	return particularites;
  }
  
  public static Set<String> getParticularitesValides() {
	return PARTICULARITES_VALIDES;
  }
  
}
