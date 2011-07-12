package fr.certu.chouette.struts.vehicleJourneyAtStop;

import java.sql.Date;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;

import org.apache.log4j.Logger;

import fr.certu.chouette.model.neptune.JourneyPattern;
import fr.certu.chouette.model.neptune.StopArea;
import fr.certu.chouette.model.neptune.StopPoint;
import fr.certu.chouette.model.neptune.VehicleJourney;
import fr.certu.chouette.model.neptune.VehicleJourneyAtStop;
import fr.certu.chouette.modele.ArretItineraire;

public class VehicleJourneyAtStopModel
{

  private static final Logger log = Logger.getLogger(VehicleJourneyAtStopModel.class);
  private Map<Long, JourneyPattern> missionParIdCourse;
  private Map<Long, List<VehicleJourneyAtStop>> horairesParIdCourse;
  private Map<Long, Integer> positionArretParIdArret;
  private Map<Long, StopArea> arretPhysiqueParIdArret;
  private Map<Long, SortedSet<Integer>> tableauxMarcheParIdCourse;
  private Map<Long, String> tableauxMarche;
  private List<Date> heuresCourses;
  private List<Integer> idsHorairesInvalides;
  private List<VehicleJourneyAtStop> horairesCourses;
  private List<StopPoint> arretsItineraire;
  private Date tempsDecalage;
  private Integer nbreCourseDecalage;
  private Long idCourseADecaler;

  private List<VehicleJourney> coursesPage;

  /********************************************************
   *                  STOPPOINTS                          *
   ********************************************************/
  public List<StopPoint> getArretsItineraire()
  {
    return arretsItineraire;
  }

  public void setArretsItineraire(List<StopPoint> arretsItineraire)
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

  public void setHeuresCourses(List<java.sql.Date> heuresCourses2)
  {
    this.heuresCourses = heuresCourses2;
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

  public List<VehicleJourneyAtStop> getHorairesCourses()
  {
    return horairesCourses;
  }

  public void setHorairesCourses(List<VehicleJourneyAtStop> horairesCourses)
  {
    this.horairesCourses = horairesCourses;
  }

  public Map<Long, List<VehicleJourneyAtStop>> getHorairesParIdCourse()
  {
    return horairesParIdCourse;
  }

  public void setHorairesParIdCourse(Map<Long, List<VehicleJourneyAtStop>> horairesParIdCourse)
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

  public Map<Long, StopArea> getArretPhysiqueParIdArret()
  {
    return arretPhysiqueParIdArret;
  }

  public void setArretPhysiqueParIdArret(Map<Long, StopArea> arretPhysiqueParIdArret)
  {
    this.arretPhysiqueParIdArret = arretPhysiqueParIdArret;
  }

  /********************************************************
   *               JOURNEY PATTERN                        *
   ********************************************************/
  public Map<Long, JourneyPattern> getMissionParIdCourse()
  {
    return missionParIdCourse;
  }

  public void setMissionParIdCourse(Map<Long, JourneyPattern> missionParIdCourse)
  {
    this.missionParIdCourse = missionParIdCourse;
  }

  /********************************************************
   *               PAGE                                   *
   ********************************************************/
  public List<VehicleJourney> getCoursesPage()
  {
    return coursesPage;
  }

  public void setCoursesPage(List<VehicleJourney> coursesPage)
  {
    this.coursesPage = coursesPage;
  }
}
