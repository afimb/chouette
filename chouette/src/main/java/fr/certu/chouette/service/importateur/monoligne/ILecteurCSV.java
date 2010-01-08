package fr.certu.chouette.service.importateur.monoligne;

import fr.certu.chouette.echange.ILectureEchange;
import fr.certu.chouette.service.importateur.ILecteur;

public interface ILecteurCSV extends ILecteur {
	
	public ILectureEchange getLectureEchange();
}