package fr.certu.chouette.service.database;

import java.util.Collection;
import java.util.List;

import fr.certu.chouette.critere.Ordre;
import fr.certu.chouette.modele.ArretItineraire;
import fr.certu.chouette.modele.Horaire;
import fr.certu.chouette.modele.PositionGeographique;

public interface IArretItineraireManager
{
	void modifier( ArretItineraire arret);
	void creer( ArretItineraire arret);
	void supprimer( Long idArret);
	ArretItineraire lire( Long idArret);
	List<ArretItineraire> lire();
	
	List<Horaire> getHorairesArret(Long idArret);
	List<PositionGeographique> getArretsPhysiques( final Collection<Long> idPhysiques, Ordre ordre);
}
