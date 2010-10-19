package fr.certu.chouette.service.importateur.multilignes.hastus;

import fr.certu.chouette.modele.ArretItineraire;
import fr.certu.chouette.modele.Course;
import fr.certu.chouette.modele.Horaire;
import fr.certu.chouette.modele.Itineraire;
import fr.certu.chouette.modele.PositionGeographique;
import java.util.List;
import java.util.Map;

public interface ILecteurHoraire extends ILecteurSpecifique {
	
	public void setCourseParNumber(Map<String, Course> courseParNom);
	public void setItineraireParNumber(Map<String, Itineraire> itineraireParNom);
	public void setArretsPhysiquesParRegistration(Map<String, PositionGeographique> arretsPhysiquesParNom);
        public Map<String, Map<ArretItineraire, Map<Course, Horaire>>> getOrdre();
}
