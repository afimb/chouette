package fr.certu.chouette.service.database;

import java.util.Collection;
import java.util.Date;
import java.util.List;

import fr.certu.chouette.modele.ArretItineraire;
import fr.certu.chouette.modele.Course;
import fr.certu.chouette.modele.Horaire;
import fr.certu.chouette.modele.Itineraire;
import fr.certu.chouette.modele.Mission;
import fr.certu.chouette.modele.PositionGeographique;
import fr.certu.chouette.modele.TableauMarche;
import fr.certu.chouette.service.database.impl.modele.EtatMajArretItineraire;
import java.util.Map;

public interface IItineraireManager
{
	void modifier( Itineraire itineraire);
	void creer( Itineraire itineraire);
	void supprimer( Long idItineraire);
	Itineraire lire(Long idItineraire);
	List<Itineraire> lire();
	
	List<PositionGeographique> getArretPhysiqueItineraire(Long idItineraire);
	List<ArretItineraire> getArretsItineraire(Long idItineraire);
	List<ArretItineraire> getArretsItineraires( final Collection<Long> idItineraires);
	List<Course> getCoursesItineraire(Long idItineraire);
	List<Course> getCoursesItineraireSelonHeureDepartPremiereCourse(Long idItineraire, Date seuilDateDepartCourse);
	List<Course> getCoursesItineraires( final Collection<Long> idItineraires);
	List<Horaire> getHorairesItineraire(Long idItineraire);
	List<Horaire> getHorairesItineraires(Collection<Long> idItineraires);
	List<Mission> getMissionsItineraire(Long idItineraire);
	List<Mission> getMissionsItineraires(Collection<Long> idItineraires);
	
	void creerItineraireRetour(Long idItineraire);
	/**
	 * Renvoie la liste des tableaux de marche utilisés par les courses
	 * de l'itinéraire
	 * 
	 * @param idItineraire : identifiant d'itinéraire
	 * @return : les tableaux de marche 
	 */
	List<TableauMarche> getTableauxMarcheItineraire(Long idItineraire);
	List<TableauMarche> getTableauxMarcheItineraires(Collection<Long> idItineraires);
	void modifierArretsItineraire(Long idItineraire, List<EtatMajArretItineraire> majArretsItineraire);
	void associerItineraire(Long idRoute1, Long idRoute2);
	void dissocierItineraire(Long idRoute1);

  Map<Long, String> getCommentParTMId(final Long idItineraire);
}
