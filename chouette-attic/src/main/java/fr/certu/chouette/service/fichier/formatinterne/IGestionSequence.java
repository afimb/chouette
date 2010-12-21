package fr.certu.chouette.service.fichier.formatinterne;

import java.sql.Connection;

public interface IGestionSequence extends IFournisseurId {
	
	public void initialiser();
	public void actualiser();
	public void setConnexion(Connection connexion);
}
