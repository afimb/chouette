package fr.certu.chouette.service.fichier.formatinterne;

import fr.certu.chouette.echange.ILectureEchange;
import fr.certu.chouette.service.fichier.formatinterne.modele.IEtatDifference;
import java.sql.Connection;
import java.util.Map;

public interface IProducteurFichier {
	
	public void produire(boolean majIdentification, ILectureEchange echange, IEtatDifference etatDifference, Connection connexion);
	public void produire(boolean majIdentification, ILectureEchange echange, IEtatDifference etatDifference, Connection connexion, boolean incremental);
	public Map<String, Long> getIdParObjectId();
}