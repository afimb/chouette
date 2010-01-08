package fr.certu.chouette.service.importateur.multilignes.hastus;

import fr.certu.chouette.modele.Course;
import fr.certu.chouette.modele.Ligne;
import fr.certu.chouette.modele.TableauMarche;

import java.util.List;
import java.util.Map;

public interface ILecteurCourse extends ILecteurSpecifique {
	
	public void setLigneParRegistration(Map<String, Ligne> ligneParRegistration);
	public Map<Course, Ligne> getLigneParCourse();
	public Map<String, Course> getCourseParNom();
	//public List<TableauMarche> getTableauxMarches();
	public List<TableauMarche> getTableauxMarches(Ligne ligne);
}
