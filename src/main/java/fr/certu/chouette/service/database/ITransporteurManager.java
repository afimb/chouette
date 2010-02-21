package fr.certu.chouette.service.database;

import java.util.List;

import fr.certu.chouette.modele.Ligne;
import fr.certu.chouette.modele.Transporteur;

public interface ITransporteurManager {

	public abstract List<Ligne> getLignesTransporteur(Long idTransporteur);

	public abstract void modifier(Transporteur transporteur);

	public abstract void creer(Transporteur transporteur);

	public abstract Transporteur lire(Long idReseau);

	public abstract List<Transporteur> lire();

	public abstract void supprimer(Long idTransporteur);

	public Transporteur lireParObjectId(String objectId);

}