package fr.certu.chouette.struts.horairesdepassage;

import fr.certu.chouette.modele.ArretItineraire;
import fr.certu.chouette.modele.Course;
import fr.certu.chouette.modele.Horaire;
import fr.certu.chouette.modele.Mission;
import fr.certu.chouette.modele.PositionGeographique;
import fr.certu.chouette.modele.TableauMarche;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class HorairesDePassageModel
{

  private Map<Long, Mission> missionParIdCourse;
  private Map<Long, Integer> referenceTableauMarcheParIdTableauMarche;
  private Map<Long, List<Horaire>> horairesParIdCourse;
  private Map<Long, Integer> positionArretParIdArret;
  private Map<Long, PositionGeographique> arretPhysiqueParIdArret;
  private Map<Long, List<TableauMarche>> tableauxMarcheParIdCourse;
  private List<TableauMarche> tableauxMarche;
  private List<Date> heuresCourses;
  private List<Integer> idsHorairesInvalides;
  private List<Horaire> horairesCourses;
  private List<ArretItineraire> arretsItineraire;
  private Date tempsDecalage;
  private Integer nbreCourseDecalage;
  private Long idCourseADecaler;
  private Long idTableauMarche;
  private Date seuilDateDepartCourse = null;
  private List<Course> coursesPage;


  public Date getTempsDecalage()
  {
    return tempsDecalage;
  }

  public void setTempsDecalage(Date tempsDecalage)
  {
    this.tempsDecalage = tempsDecalage;
  }

  public Integer getNbreCourseDecalage()
  {
    return nbreCourseDecalage;
  }

  public void setNbreCourseDecalage(Integer nbreCourseDecalage)
  {
    this.nbreCourseDecalage = nbreCourseDecalage;
  }

  public Long getIdCourseADecaler()
  {
    return idCourseADecaler;
  }

  public void setIdCourseADecaler(Long idCourseADecaler)
  {
    this.idCourseADecaler = idCourseADecaler;
  }

  public Long getIdTableauMarche()
  {
    return idTableauMarche;
  }

  public void setIdTableauMarche(Long idTableauMarche)
  {
    this.idTableauMarche = idTableauMarche;
  }

  public Date getSeuilDateDepartCourse()
  {
    return seuilDateDepartCourse;
  }

  public void setSeuilDateDepartCourse(Date seuilDateDepartCourse)
  {
    this.seuilDateDepartCourse = seuilDateDepartCourse;
  }

  public List<ArretItineraire> getArretsItineraire()
  {
    return arretsItineraire;
  }

  public void setArretsItineraire(List<ArretItineraire> arretsItineraire)
  {
    this.arretsItineraire = arretsItineraire;
  }

  public List<Course> getCoursesPage()
  {
    return coursesPage;
  }

  public void setCoursesPage(List<Course> coursesPage)
  {
    this.coursesPage = coursesPage;
  }

  public List<Integer> getIdsHorairesInvalides()
  {
    return idsHorairesInvalides;
  }

  public void setIdsHorairesInvalides(List<Integer> idsHorairesInvalides)
  {
    this.idsHorairesInvalides = idsHorairesInvalides;
  }

  public List<Horaire> getHorairesCourses()
  {
    return horairesCourses;
  }

  public void setHorairesCourses(List<Horaire> horairesCourses)
  {
    this.horairesCourses = horairesCourses;
  }

  public Map<Long, List<Horaire>> getHorairesParIdCourse()
  {
    return horairesParIdCourse;
  }

  public void setHorairesParIdCourse(Map<Long, List<Horaire>> horairesParIdCourse)
  {
    this.horairesParIdCourse = horairesParIdCourse;
  }

  public Map<Long, Integer> getPositionArretParIdArret()
  {
    return positionArretParIdArret;
  }

  public void setPositionArretParIdArret(
          Map<Long, Integer> positionArretParIdArret)
  {
    this.positionArretParIdArret = positionArretParIdArret;
  }

  public Map<Long, PositionGeographique> getArretPhysiqueParIdArret()
  {
    return arretPhysiqueParIdArret;
  }

  public void setArretPhysiqueParIdArret(Map<Long, PositionGeographique> arretPhysiqueParIdArret)
  {
    this.arretPhysiqueParIdArret = arretPhysiqueParIdArret;
  }

  public Map<Long, List<TableauMarche>> getTableauxMarcheParIdCourse()
  {
    return tableauxMarcheParIdCourse;
  }

  public void setTableauxMarcheParIdCourse(Map<Long, List<TableauMarche>> tableauxMarcheParIdCourse)
  {
    this.tableauxMarcheParIdCourse = tableauxMarcheParIdCourse;
  }

  public Map<Long, Integer> getReferenceTableauMarcheParIdTableauMarche()
  {
    return referenceTableauMarcheParIdTableauMarche;
  }

  public void setReferenceTableauMarcheParIdTableauMarche(Map<Long, Integer> referenceTableauMarcheParIdTableauMarche)
  {
    this.referenceTableauMarcheParIdTableauMarche = referenceTableauMarcheParIdTableauMarche;
  }

  public Map<Long, Mission> getMissionParIdCourse()
  {
    return missionParIdCourse;
  }

  public void setMissionParIdCourse(Map<Long, Mission> missionParIdCourse)
  {
    this.missionParIdCourse = missionParIdCourse;
  }

  public List<TableauMarche> getTableauxMarche()
  {
    return tableauxMarche;
  }

  public void setTableauxMarche(List<TableauMarche> tableauxMarche)
  {
    this.tableauxMarche = tableauxMarche;
  }

  public List<Date> getHeuresCourses()
  {
    return heuresCourses;
  }

  public void setHeuresCourses(List<Date> heuresCourses)
  {
    this.heuresCourses = heuresCourses;
  }


}
