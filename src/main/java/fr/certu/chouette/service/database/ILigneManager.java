package fr.certu.chouette.service.database;

import fr.certu.chouette.critere.IClause;
import fr.certu.chouette.modele.Itineraire;
import fr.certu.chouette.modele.Ligne;
import java.util.Collection;
import java.util.List;

public interface ILigneManager  {
	
	void modifier( Ligne ligne);
	void creer( Ligne ligne);
	Ligne lire( Long idLigne);
	List<Ligne> lire();
	void supprimer( Long idLigne);
	List<Itineraire> getItinerairesLigne(Long idLigne);
	List<Itineraire> getLigneItinerairesExportables(Long idLigne);
	Ligne getLigneParRegistration(String registrationNumber);
	List<Ligne> filtrer( Collection<Long> idReseaux, Collection<Long> idTransporteurs);
	List<Ligne> getLignes( final Collection<Long> idLignes);
	List<fr.certu.chouette.modele.PositionGeographique> getArretsPhysiques( Long idLigne);
	boolean nomConnu( String name);
	boolean nomConnu(Long id,String name) ;
	List <Ligne> select (IClause clause);
	void supprimer(Long idLigne, boolean detruireAvecTMs, boolean detruireAvecArrets, boolean detruireAvecTransporteur, boolean detruireAvecReseau);
}
