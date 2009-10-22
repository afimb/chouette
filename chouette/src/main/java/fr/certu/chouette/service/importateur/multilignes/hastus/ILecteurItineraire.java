package fr.certu.chouette.service.importateur.multilignes.hastus;

import fr.certu.chouette.modele.Itineraire;
import fr.certu.chouette.modele.Ligne;
import fr.certu.chouette.modele.PositionGeographique;

import java.util.Map;

public interface ILecteurItineraire extends ILecteurSpecifique {
	
	public void setLigneParRegistration(Map<String, Ligne> ligneParRegistration);
	public Map<Itineraire, Ligne> getLigneParItineraire();
	public Map<String, Itineraire> getItineraireParNom();
	public void setZones(Map<String, PositionGeographique> zones);
	public void completion();
}
