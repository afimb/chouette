package fr.certu.chouette.struts.vehicleJourneyAtStop;

import fr.certu.chouette.modele.ArretItineraire;
import fr.certu.chouette.modele.Course;
import fr.certu.chouette.modele.Horaire;
import fr.certu.chouette.modele.Mission;
import fr.certu.chouette.modele.PositionGeographique;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class VehicleJourneyAtStopModel
{

  private static final Log log = LogFactory.getLog(VehicleJourneyAtStopModel.class);
  private Map<Long, Mission> missionParIdCourse;
  private Map<Long, List<Horaire>> horairesParIdCourse;
  private Map<Long, Integer> positionArretParIdArret;
  private Map<Long, PositionGeographique> arretPhysiqueParIdArret;
  private Map<Long, SortedSet<Integer>> tableauxMarcheParIdCourse;
  private Map<Long, String> tableauxMarche;
  private List<Date> heuresCourses;
  private List<Integer> idsHorairesInvalides;
  private List<Horaire> horairesCourses;
  private List<ArretItineraire> arretsItineraire;
  private Date tempsDecalage;
  private Integer nbreCourseDecalage;
  private Long idCourseADecaler;

  private List<Course> coursesPage;

  /********************************************************
   *                  STOPPOINTS                          *
   ********************************************************/
  public List<ArretItineraire> getArretsItineraire()
  {
    return arretsItineraire;
  }

  public void setArretsItineraire(List<ArretItineraire> arretsItineraire)
  {
    this.arretsItineraire = arretsItineraire;
  }

  /********************************************************
   *                  VEHICLE JOURNEY                     *
   ********************************************************/
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

  public List<Date> getHeuresCourses()
  {
    return heuresCourses;
  }

  public void setHeuresCourses(List<Date> heuresCourses)
  {
    this.heuresCourses = heuresCourses;
  }

  /********************************************************
   *                  TIME TABLE                          *
   ********************************************************/

  public void setTableauxMarcheParIdCourse(Map<Long, SortedSet<Integer>> tableauxMarcheParIdCourse)
  {
    this.tableauxMarcheParIdCourse = tableauxMarcheParIdCourse;
  }

  public Map<Long, SortedSet<Integer>> getTableauxMarcheParIdCourse()
  {
    return tableauxMarcheParIdCourse;
  }

  public Map<Long, String> getTableauxMarche()
  {
    return tableauxMarche;
  }

  public void setTableauxMarche(Map<Long, String> tableauxMarche)
  {
    this.tableauxMarche = tableauxMarche;
  }

  /********************************************************
   *                  HOURS                               *
   ********************************************************/
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

  /********************************************************
   *               BOARDING POSITION                      *
   ********************************************************/
  public Map<Long, Integer> getPositionArretParIdArret()
  {
    return positionArretParIdArret;
  }

  public void setPositionArretParIdArret(Map<Long, Integer> positionArretParIdArret)
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

  /********************************************************
   *               JOURNEY PATTERN                        *
   ********************************************************/
  public Map<Long, Mission> getMissionParIdCourse()
  {
    return missionParIdCourse;
  }

  public void setMissionParIdCourse(Map<Long, Mission> missionParIdCourse)
  {
    this.missionParIdCourse = missionParIdCourse;
  }

  /********************************************************
   *               PAGE                                   *
   ********************************************************/
  public List<Course> getCoursesPage()
  {
    return coursesPage;
  }

  public void setCoursesPage(List<Course> coursesPage)
  {
    this.coursesPage = coursesPage;
  }
}
