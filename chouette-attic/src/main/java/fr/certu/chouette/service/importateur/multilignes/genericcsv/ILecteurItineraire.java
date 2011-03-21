package fr.certu.chouette.service.importateur.multilignes.genericcsv;

import java.util.List;
import java.util.Map;
import java.util.Set;
import fr.certu.chouette.modele.Course;
import fr.certu.chouette.modele.Itineraire;
import fr.certu.chouette.modele.Ligne;
import fr.certu.chouette.modele.Mission;

public interface ILecteurItineraire {

    public void lire(List<Course> courses, Set<Course> coursesAller, Set<Course> coursesRetour, Map<String, Mission> missions, Ligne ligne);
    public Map<Ligne, List<Itineraire>> getItineraires();
    public void reinit();
}
