package fr.certu.chouette.service.database;

import java.util.Collection;
import java.util.List;

import fr.certu.chouette.modele.Correspondance;

public interface ICorrespondanceManager {

	void creer(Correspondance correspondance);
	void modifier(Correspondance correspondance);
	Correspondance lire(Long idCorrespondance);
	List<Correspondance> lire();
	void supprimer(Long idCorrespondance);
	/**
	 * Renvoie les correspondances d'une zone ou d'un arrêt physique
	 * 
	 * @param idGeoPosition : identifiant de zone ou d'arrêt physique
	 * @return : les correspondances
	 */
	List<Correspondance> getCorrespondancesParGeoPosition(Long idGeoPosition);
	public Correspondance lireParObjectId(String objectId);
	
	List<Correspondance> selectionParPositions(Collection<Long> positionIds);
}