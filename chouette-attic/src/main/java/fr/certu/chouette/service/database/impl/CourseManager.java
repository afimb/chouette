package fr.certu.chouette.service.database.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.log4j.Logger;

import fr.certu.chouette.dao.IModificationSpecifique;
import fr.certu.chouette.dao.ISelectionSpecifique;
import fr.certu.chouette.dao.ITemplateDao;
import fr.certu.chouette.modele.ArretItineraire;
import fr.certu.chouette.modele.Course;
import fr.certu.chouette.modele.Horaire;
import fr.certu.chouette.modele.TableauMarche;
import fr.certu.chouette.service.database.ICourseManager;
import fr.certu.chouette.service.identification.IIdentificationManager;

public class CourseManager implements ICourseManager
{

  private static final Logger logger = Logger.getLogger(CourseManager.class);
  private ITemplateDao<Course> courseDao;
  private ISelectionSpecifique selectionSpecifique;
  private IModificationSpecifique modificationSpecifique;
  private IIdentificationManager identificationManager;

  public List<Horaire> getHorairesCourse(Long idCourse)
  {
    return selectionSpecifique.getHorairesCourse(idCourse);
  }

  public List<Horaire> getHorairesCourses(Collection<Long> idCourses)
  {
    return selectionSpecifique.getHorairesCourses(idCourses);
  }

  public void associerTableauMarcheCourses(Long idTM, List<Long> idCourses)
  {
    modificationSpecifique.associerTableauMarcheCourses(idTM, idCourses);
  }

  public void associerCourseTableauxMarche(Long idCourse, List<Long> idTMs)
  {
    modificationSpecifique.associerCourseTableauxMarche(idCourse, idTMs);
  }

  public List<TableauMarche> getTableauxMarcheCourse(Long idCourse)
  {
    return selectionSpecifique.getTableauxMarcheCourse(idCourse);
  }

  public List<TableauMarche> getTableauxMarcheCourses(Collection<Long> idCourses)
  {
    return selectionSpecifique.getTableauxMarcheCourses(idCourses);
  }

  public Map<Long, List<Long>> getTimeTablesIdByRouteId(Long idItineraire)
  {
    return selectionSpecifique.getTimeTablesIdByRouteId(idItineraire);
  }

  public List<Long> getTimeTablesIdByVehicleJourneyId(Long vehicleJourneyId)
  {
    return selectionSpecifique.getTimeTablesIdByVehicleJourneyId(vehicleJourneyId);
  }

  public List<Horaire> getHorairesCourseOrdonnes(Long idCourse)
  {
    Course course = lire(idCourse);

    List<ArretItineraire> arretsItineraire = selectionSpecifique.getArretsItineraire(course.getIdItineraire());
    List<Horaire> horaires = selectionSpecifique.getHorairesCourse(idCourse);

    SortedSet<Horaire> horairesTries = new TreeSet<Horaire>(new HoraireComparator(arretsItineraire));
    horairesTries.addAll(horaires);

    return new ArrayList<Horaire>(horairesTries);
  }

  public List<Horaire> getHorairesCourseOrdonnes(Collection<Long> idCourses)
  {
    if (idCourses == null)
    {
      return new ArrayList<Horaire>();
    }

    List<Horaire> horairesTriesMultiCourses = new ArrayList<Horaire>();

    List<ArretItineraire> arretsItineraire = null;
    for (Long idCourse : idCourses)
    {
      Course course = lire(idCourse);

      if (arretsItineraire == null)
      {
        arretsItineraire = selectionSpecifique.getArretsItineraire(course.getIdItineraire());
      }

      List<Horaire> horaires = selectionSpecifique.getHorairesCourse(idCourse);
      SortedSet<Horaire> horairesTries = new TreeSet<Horaire>(new HoraireComparator(arretsItineraire));
      horairesTries.addAll(horaires);

      horairesTriesMultiCourses.addAll(horairesTries);
    }

    return horairesTriesMultiCourses;
  }

  public void modifier(Course course)
  {
    courseDao.update(course);
  }

  public void creer(Course course)
  {
    courseDao.save(course);
    String objectId = identificationManager.getIdFonctionnel("VehicleJourney", course);
    course.setObjectId(objectId);
    course.setCreationTime(new Date());
    course.setObjectVersion(1);
    courseDao.update(course);
  }

  public void supprimer(Long idCourse)
  {
    modificationSpecifique.supprimerCourse(idCourse);
  }

  public Course lire(Long idCourse)
  {
    return courseDao.get(idCourse);
  }

  public List<Course> lire()
  {
    return courseDao.getAll();
  }

  public void setCourseDao(ITemplateDao<Course> courseDao)
  {
    this.courseDao = courseDao;
  }

  public void setModificationSpecifique(
          IModificationSpecifique modificationSpecifique)
  {
    this.modificationSpecifique = modificationSpecifique;
  }

  public void setSelectionSpecifique(ISelectionSpecifique selectionSpecifique)
  {
    this.selectionSpecifique = selectionSpecifique;
  }

  public void setIdentificationManager(
          IIdentificationManager identificationManager)
  {
    this.identificationManager = identificationManager;
  }

  private class HoraireComparator implements Comparator<Horaire>
  {

    private Map<Long, Integer> positionParArretItineraireId = new Hashtable<Long, Integer>();

    public int compare(Horaire o1, Horaire o2)
    {
      return getPosition(o1) - getPosition(o2);
    }

    private int getPosition(Horaire horaire)
    {
      return positionParArretItineraireId.get(horaire.getIdArret()).intValue();
    }

    public HoraireComparator(List<ArretItineraire> arretsItineraire)
    {
      for (ArretItineraire arretItineraire : arretsItineraire)
      {
        positionParArretItineraireId.put(arretItineraire.getId(), arretItineraire.getPosition());
      }
    }
  }

  public List<Course> getCoursesFiltrees(Long idItineraire, Long idTableauMarche, Date seuilDateDepartCourses)
  {
    return this.selectionSpecifique.getCoursesFiltrees(idItineraire, idTableauMarche, seuilDateDepartCourses);
  }
}
