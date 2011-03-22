package fr.certu.chouette.service.importateur.multilignes.genericcsv;

import java.util.List;
import java.util.Map;
import fr.certu.chouette.modele.Course;
import fr.certu.chouette.modele.Ligne;
import fr.certu.chouette.modele.Mission;

public interface ILecteurMission extends ILecteurSpecifiqueMission {

    public void lire(Map<Course, List<String>> arretsPhysiquesParCourse, Ligne ligne);
    public Map<Ligne, List<Mission>> getMissions();
    public Map<String, Mission> getMissionByCode();
}
