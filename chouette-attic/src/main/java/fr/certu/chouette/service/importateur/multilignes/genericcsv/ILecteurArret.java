package fr.certu.chouette.service.importateur.multilignes.genericcsv;

import java.util.List;
import java.util.Map;
import java.util.Set;
import fr.certu.chouette.modele.ArretItineraire;
import fr.certu.chouette.modele.Course;
import fr.certu.chouette.modele.Horaire;
import fr.certu.chouette.modele.Itineraire;
import fr.certu.chouette.modele.Ligne;
import fr.certu.chouette.modele.Mission;
import fr.certu.chouette.modele.PositionGeographique;

public interface ILecteurArret {

    public void reinit();
    public void init(List<Itineraire> itinerairesDeLigne, List<Mission> missionsDeLigne, List<Course> coursesDeLigne, Map<Course, List<Horaire>> horairesParCourse, Map<Course, List<String>> arretsPhysiquesParCourse, List<PositionGeographique> arretsPhysiques);
    public void lire(Ligne ligne, Set<Course> coursesAller, Set<Course> coursesRetour, List<PositionGeographique> arretsPhysiques);
    public Map<Ligne, List<ArretItineraire>> getArretsItinerairesParLigne();
    public Map<Ligne, Map<String, String>> getItineraireParArret();
}
