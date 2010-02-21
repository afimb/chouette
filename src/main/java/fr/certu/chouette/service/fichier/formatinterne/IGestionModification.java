package fr.certu.chouette.service.fichier.formatinterne;

import fr.certu.chouette.echange.ILectureEchange;
import fr.certu.chouette.service.fichier.formatinterne.modele.IEtatDifference;
import java.sql.Connection;
import java.util.Map;

public interface IGestionModification {
	
	public void modifier(final ILectureEchange echange);
	public void modifier(final ILectureEchange lectureEchange, boolean incremental);
	public void setEtatDifference(IEtatDifference etatDifference);
	public void setConnexion(final Connection connexion);
	public void setIdParObjectId(Map<String, Long> idParObjectId);
}