package fr.certu.chouette.service.importateur.multilignes.hastus;

import fr.certu.chouette.modele.Course;
import fr.certu.chouette.modele.Itineraire;
import fr.certu.chouette.modele.Ligne;
import fr.certu.chouette.modele.Mission;
import fr.certu.chouette.modele.PositionGeographique;
import fr.certu.chouette.modele.TableauMarche;
import java.util.List;
import java.util.Map;

public interface ILecteurCourse extends ILecteurSpecifique {
    
    public void setLigneParRegistration(Map<String, Ligne> ligneParRegistration);
    public void setZones(Map<String, PositionGeographique> zones);
    public void setItineraireParNumber(Map<String, Itineraire> itineraireParNumber);
    public void setMissionParNom(Map<String, Mission> missionParNom);
    public Map<Course, Ligne> getLigneParCourse();
    public Map<String, Course> getCourseParNumber();
    public List<TableauMarche> getTableauxMarches(Ligne ligne);
}
