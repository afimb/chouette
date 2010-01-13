package fr.certu.chouette.service.fichier.formatinterne;

import fr.certu.chouette.echange.ILectureEchange;
import fr.certu.chouette.service.fichier.formatinterne.modele.IEtatDifference;
import java.sql.Connection;

public interface IAnalyseurEtatInitial {
	
	public IEtatDifference analyser(ILectureEchange lectureEchange, Connection connexion);
	public IEtatDifference analyser(ILectureEchange lectureEchange, Connection connexion, boolean incremental);
}