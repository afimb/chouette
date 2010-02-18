package fr.certu.chouette.service.fichier.formatinterne;

import fr.certu.chouette.service.fichier.formatinterne.modele.IEtatDifference;
import java.sql.Connection;

public interface IChargeur {
	
	public void charger(IEtatDifference etatDifference, Connection connexion);
	public void charger(IEtatDifference etatDifference, Connection connexion, boolean incremental);
}
