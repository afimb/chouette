package fr.certu.chouette.service.importateur.multilignes.genericcsv;

import java.util.List;
import java.util.Map;
import java.util.Set;

import fr.certu.chouette.modele.Course;
import fr.certu.chouette.modele.Ligne;
import fr.certu.chouette.modele.TableauMarche;

public interface ILecteurCourse extends ILecteurSpecifiqueLigne {
    
    public Set<Course> getCoursesAller();
    public Set<Course> getCoursesRetour();
    public List<Course> getCourses();
    public List<Course> getCoursesEnCours();
    public List<Course> getCourses(Ligne ligne);
    public void setTableauxMarchesParRef(Map<String, TableauMarche> caldendriersParRef);
}
