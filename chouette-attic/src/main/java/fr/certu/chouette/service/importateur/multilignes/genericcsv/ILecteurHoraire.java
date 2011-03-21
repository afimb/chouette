package fr.certu.chouette.service.importateur.multilignes.genericcsv;

import java.util.List;
import java.util.Map;
import fr.certu.chouette.modele.Course;
import fr.certu.chouette.modele.Horaire;

public interface ILecteurHoraire extends ILecteurSpecifiqueHoraire {

    public Map<Course, List<Horaire>> getHoraires();
    public Map<Course, List<String>> getArretsPhysiques();
    public void init();
}
