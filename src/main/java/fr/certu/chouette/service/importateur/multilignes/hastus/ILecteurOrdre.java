package fr.certu.chouette.service.importateur.multilignes.hastus;

import fr.certu.chouette.modele.ArretItineraire;
import fr.certu.chouette.modele.Course;
import fr.certu.chouette.modele.Horaire;
import fr.certu.chouette.modele.Itineraire;
import fr.certu.chouette.modele.PositionGeographique;
import java.util.Map;

public interface ILecteurOrdre extends ILecteurSpecifique {
    
    public void setOrdre(Map<String, Map<ArretItineraire, Map<Course, Horaire>>> ordre);
    public void setItineraireParNumber(Map<String, Itineraire> itineraires);
    public void setArretsPhysiquesParRegistration(Map<String, PositionGeographique> arretsPhysiques);
}
