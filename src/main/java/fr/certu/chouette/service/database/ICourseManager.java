package fr.certu.chouette.service.database;

import java.util.Collection;
import java.util.Date;
import java.util.List;

import fr.certu.chouette.modele.Course;
import fr.certu.chouette.modele.Horaire;
import fr.certu.chouette.modele.TableauMarche;
import java.util.Map;

public interface ICourseManager
{

  void modifier(Course course);

  void creer(Course course);

  void supprimer(Long idCourse);

  Course lire(Long idCourse);

  List<Course> lire();

  List<Horaire> getHorairesCourse(Long idCourse);

  List<Horaire> getHorairesCourses(final Collection<Long> idCourses);

  List<TableauMarche> getTableauxMarcheCourses(final Collection<Long> idCourses);

  List<TableauMarche> getTableauxMarcheCourse(Long idCourse);

  void associerCourseTableauxMarche(Long idCourse, List<Long> idTMs);

  void associerTableauMarcheCourses(Long idTM, List<Long> idCourses);

  List<Horaire> getHorairesCourseOrdonnes(Long idCourse);

  List<Course> getCoursesFiltrees(Long idItineraire, Long idTableauMarche, Date seuilDateDepartCourses);

  Map<Long, List<Long>> getTimeTablesIdByRouteId(final Long idItineraire);

  List<Long> getTimeTablesIdByVehicleJourneyId(final Long vehicleJourneyId);
}
