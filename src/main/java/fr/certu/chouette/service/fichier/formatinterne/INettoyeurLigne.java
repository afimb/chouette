package fr.certu.chouette.service.fichier.formatinterne;

import java.sql.Connection;

public interface INettoyeurLigne {
	
	public void nettoyer(Long ligneId);
	public void nettoyer(Long ligneId, boolean incremental);
	public void setConnexion(Connection connexion);
	public String getDatabaseSchema();
}
