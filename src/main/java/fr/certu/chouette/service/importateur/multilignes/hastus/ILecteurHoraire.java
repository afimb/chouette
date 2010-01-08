package fr.certu.chouette.service.importateur.multilignes.hastus;

import fr.certu.chouette.modele.ArretItineraire;
import fr.certu.chouette.modele.Course;
import fr.certu.chouette.modele.Horaire;
import fr.certu.chouette.modele.Itineraire;
import fr.certu.chouette.modele.Mission;
import fr.certu.chouette.modele.PositionGeographique;

import java.util.List;
import java.util.Map;

public interface ILecteurHoraire extends ILecteurSpecifique {
	
	public void setCourseParNom(Map<String, Course> courseParNom);
	public void setItineraireParNom(Map<String, Itineraire> itineraireParNom);
	public void setArretsPhysiquesParNom(Map<String, PositionGeographique> arretsPhysiquesParNom);
	public Map<Itineraire, Map<String, ArretItineraire>> getArretsItineraireParItineraire();
	public Map<String, Mission> getMissionParNom();
	public Map<String, List<Horaire>> getListHorairesParRegistrationLigne();
	public List<Horaire> getListHorairesParRegistrationLigne(String registrationLigne);
	public void completion();
	public void setObjectIdParParentObjectId(Map<String, String> objectIdParParentObjectId);
}
