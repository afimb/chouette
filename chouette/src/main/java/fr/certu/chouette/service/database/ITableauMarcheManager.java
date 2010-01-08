package fr.certu.chouette.service.database;

import java.util.Date;
import java.util.List;

import fr.certu.chouette.critere.IClause;
import fr.certu.chouette.modele.Course;
import fr.certu.chouette.modele.TableauMarche;

public interface ITableauMarcheManager
{
	public abstract void modifier( TableauMarche tableauMarche);
	public abstract void creer( TableauMarche tableauMarche);
	public abstract void supprimer( Long idTableauMarche);
	public abstract TableauMarche lire( Long idTableauMarche);
	//public abstract List<TableauMarche> lire();
	
	/**
	 * Renvoie la liste des courses associées au TM
	 * @param idTableauMarche
	 * @return : les courses associées au TM
	 */
	public abstract List<Course> getCoursesTableauMarche(Long idTableauMarche);
	/**
	 * Rattache un TM à une liste de courses
	 * @param idTM : identifiant du TM
	 * @param idCourses : liste des identifiants des courses
	 */
	public abstract void associerTableauMarcheCourses(Long idTM, List<Long> idCourses);
	/**
	 * Rattache une course à une liste de TM
	 * @param idCourse : identifiant de course
	 * @param idTMs : liste d'identifiants de TM
	 */
	public abstract void associerCourseTableauxMarche(Long idCourse, List<Long> idTMs);
	
	List<TableauMarche> lireSansDateNiPeriode();
	
	List<TableauMarche> select(IClause clause);
	List<TableauMarche> lire(Date dateDebutPeriode, Date dateFinPeriode, String commentaire, Long idReseau);
}
