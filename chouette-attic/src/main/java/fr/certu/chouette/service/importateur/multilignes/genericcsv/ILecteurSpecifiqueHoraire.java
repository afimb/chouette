package fr.certu.chouette.service.importateur.multilignes.genericcsv;

import java.util.List;
import fr.certu.chouette.modele.Course;

public interface ILecteurSpecifiqueHoraire {
    
    public void lire(String[] ligneCSV, List<Course> courses);
    public void reinit();
}
